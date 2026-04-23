package com.citybuild.core.async;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * AsyncExecutor - Manages async task execution with thread pool
 * Handles CompletableFuture-based async operations
 */
public class AsyncExecutor {
    private final ExecutorService executor;
    private final Logger logger;
    private final int threadPoolSize;
    
    public AsyncExecutor(Logger logger, int threadPoolSize) {
        this.logger = logger;
        this.threadPoolSize = threadPoolSize;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        
        logger.info("✅ AsyncExecutor initialized with " + threadPoolSize + " threads");
    }
    
    /**
     * Execute operation asynchronously
     * @param operation Operation to execute
     * @return CompletableFuture that completes when operation finishes
     */
    public <T> CompletableFuture<T> executeAsync(Callable<T> operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return operation.call();
            } catch (Exception e) {
                logger.severe("Async operation failed: " + e.getMessage());
                throw new CompletionException(e);
            }
        }, executor);
    }
    
    /**
     * Execute operation asynchronously (void return)
     */
    public CompletableFuture<Void> executeAsync(Runnable operation) {
        return CompletableFuture.runAsync(operation, executor);
    }
    
    /**
     * Execute multiple operations in parallel
     */
    public <T> CompletableFuture<Void> executeParallel(java.util.List<Callable<T>> operations) {
        java.util.List<CompletableFuture<T>> futures = operations.stream()
            .map(this::executeAsync)
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    /**
     * Execute with timeout
     */
    public <T> CompletableFuture<T> executeWithTimeout(
        Callable<T> operation,
        long timeout,
        TimeUnit unit
    ) {
        CompletableFuture<T> future = executeAsync(operation);
        
        return future.orTimeout(timeout, unit)
            .exceptionally(ex -> {
                logger.warning("Async operation timed out after " + timeout + " " + unit);
                throw new CompletionException(ex);
            });
    }
    
    /**
     * Chain multiple operations
     */
    public <T, U> CompletableFuture<U> chain(
        CompletableFuture<T> first,
        java.util.function.Function<T, CompletableFuture<U>> second
    ) {
        return first.thenCompose(second);
    }
    
    /**
     * Get executor thread pool size
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    /**
     * Shutdown executor gracefully
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warning("AsyncExecutor shutdown timeout - forcing termination");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("✅ AsyncExecutor shut down");
    }
}
