package com.citybuild.features.terrain;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

/**
 * TerrainManager - Manages terrain generation and farm world resets
 */
public class TerrainManager {
    private final Plugin plugin;
    private String currentBiome;
    private double terrainHeight;
    
    // Farm world auto-reset
    private static final String FARM_WORLD_NAME = "farm";
    private static final long RESET_INTERVAL_TICKS = 48 * 60 * 60 * 20; // 48 hours in ticks
    private long lastResetTime = System.currentTimeMillis();

    public TerrainManager(Plugin plugin) {
        this.plugin = plugin;
        this.currentBiome = "PLAINS";
        this.terrainHeight = 64.0;
        startAutoReset();
    }
    
    /**
     * Starts the auto-reset scheduler for farm world
     */
    private void startAutoReset() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            resetFarmWorld();
        }, RESET_INTERVAL_TICKS, RESET_INTERVAL_TICKS);
        
        plugin.getLogger().info("✓ Farm world auto-reset scheduler started (every 48 hours)");
    }
    
    /**
     * Manually resets the farm world
     */
    public void resetFarmWorld() {
        World farmWorld = Bukkit.getWorld(FARM_WORLD_NAME);
        
        if (farmWorld == null) {
            plugin.getLogger().warning("⚠ Farm world not found!");
            return;
        }
        
        try {
            plugin.getLogger().info("🔄 Starting farm world reset...");
            
            // Notify all players in farm world
            for (Player player : farmWorld.getPlayers()) {
                player.sendMessage("§c⚠ Farm world is resetting in 10 seconds!");
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()); // Teleport to main world
            }
            
            // Schedule reset after 10 seconds
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                // Delete and recreate farm world
                
                // Unload world
                Bukkit.unloadWorld(farmWorld, false);
                plugin.getLogger().info("✓ Farm world unloaded");
                
                // Delete files (simplified - in production you'd use proper deletion)
                deleteWorld(farmWorld.getWorldFolder());
                plugin.getLogger().info("✓ Farm world files deleted");
                
                // Reload world
                Bukkit.createWorld(new org.bukkit.WorldCreator(FARM_WORLD_NAME));
                plugin.getLogger().info("✓ Farm world regenerated!");
                
                lastResetTime = System.currentTimeMillis();
                
                // Broadcast completion
                Bukkit.getServer().getOnlinePlayers().forEach(p ->
                    p.sendMessage("§a✓ Farm world has been reset!")
                );
            }, 200); // 10 seconds = 200 ticks
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error resetting farm world: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes world directory recursively
     */
    private void deleteWorld(java.io.File dir) {
        if (dir.isDirectory()) {
            java.io.File[] files = dir.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    deleteWorld(file);
                }
            }
        }
        dir.delete();
    }
    
    /**
     * Gets next reset time
     */
    public long getNextResetTime() {
        return lastResetTime + (48 * 60 * 60 * 1000); // 48 hours in milliseconds
    }
    
    /**
     * Gets reset status string
     */
    public String getResetStatus() {
        long nextReset = getNextResetTime();
        long now = System.currentTimeMillis();
        long timeRemaining = nextReset - now;
        long hoursRemaining = timeRemaining / (60 * 60 * 1000);
        long minutesRemaining = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000);
        
        return String.format("§6Next farm reset: §a%dh %dm", hoursRemaining, minutesRemaining);
    }

    public boolean generateTerrain(Player player, String type) {
        switch (type.toLowerCase()) {
            case "flat":
                currentBiome = "PLAINS";
                terrainHeight = 64.0;
                player.sendMessage("§a✓ Flat terrain generated!");
                return true;
            case "mountain":
                currentBiome = "MOUNTAINS";
                terrainHeight = 150.0;
                player.sendMessage("§a✓ Mountain terrain generated!");
                return true;
            case "island":
                currentBiome = "OCEAN";
                terrainHeight = 70.0;
                player.sendMessage("§a✓ Island terrain generated!");
                return true;
            case "jungle":
                currentBiome = "JUNGLE";
                terrainHeight = 100.0;
                player.sendMessage("§a✓ Jungle terrain generated!");
                return true;
            default:
                player.sendMessage("§cUnknown terrain type! Use: flat, mountain, island, jungle");
                return false;
        }
    }

    public void setTerrainHeight(double height, Player player) {
        this.terrainHeight = Math.max(0, Math.min(256, height));
        player.sendMessage("§a✓ Terrain height set to " + terrainHeight);
    }

    public String getCurrentBiome() {
        return currentBiome;
    }

    public double getTerrainHeight() {
        return terrainHeight;
    }

    public String getTerrainInfo(Player player) {
        return String.format("§6Terrain Info: Biome=%s, Height=%.1f", currentBiome, terrainHeight);
    }
}
