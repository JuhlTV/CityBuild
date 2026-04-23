package com.citybuild.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.*;

/**
 * Async data saving utility to prevent blocking main thread
 * Uses BukkitScheduler for async JSON operations
 */
public class AsyncDataSaver {
    
    private final JavaPlugin plugin;
    private final BukkitScheduler scheduler;
    private final ExecutorService threadPool;
    private final BlockingQueue<SaveTask> taskQueue;
    
    public AsyncDataSaver(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
        // Use a dedicated thread pool with 2 threads for I/O operations
        this.threadPool = Executors.newFixedThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable, "CityBuild-AsyncSaver");
            thread.setDaemon(true);
            return thread;
        });
        this.taskQueue = new LinkedBlockingQueue<>();
    }
    
    /**
     * Schedule async save task
     * Callback runs on main thread after save completes
     */
    public void saveAsync(Runnable saveTask, Runnable onComplete) {
        threadPool.execute(() -> {
            try {
                saveTask.run();
                
                // Run callback on main thread
                if (onComplete != null) {
                    scheduler.runTask(plugin, onComplete);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Async save failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Schedule async save without callback
     */
    public void saveAsync(Runnable saveTask) {
        saveAsync(saveTask, null);
    }
    
    /**
     * Schedule delayed async save (useful for batch operations)
     */
    public void saveAsyncDelayed(long delayTicks, Runnable saveTask) {
        scheduler.runTaskLaterAsynchronously(plugin, saveTask, delayTicks);
    }
    
    /**
     * Schedule repeating async save (for periodic saves)
     */
    public void saveAsyncRepeating(long delayTicks, long periodTicks, Runnable saveTask) {
        scheduler.runTaskTimerAsynchronously(plugin, saveTask, delayTicks, periodTicks);
    }
    
    /**
     * Perform save synchronously (for emergency saves)
     * WARNING: Blocks main thread!
     */
    public void saveSync(Runnable saveTask) {
        try {
            saveTask.run();
        } catch (Exception e) {
            plugin.getLogger().severe("Sync save failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shutdown thread pool (call on plugin disable)
     */
    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                plugin.getLogger().warning("AsyncDataSaver thread pool did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Batch multiple saves together
     */
    public void saveBatch(Runnable... tasks) {
        saveAsync(() -> {
            for (Runnable task : tasks) {
                try {
                    task.run();
                } catch (Exception e) {
                    plugin.getLogger().warning("Batch save task failed: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Internal task wrapper
     */
    private static class SaveTask {
        Runnable task;
        long timestamp;
        
        SaveTask(Runnable task) {
            this.task = task;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
