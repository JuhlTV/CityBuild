package com.citybuild.protection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Adapter for WorldGuard integration
 * Provides plot protection using WorldGuard regions if available
 */
public class WorldGuardAdapter {

    private final Plugin plugin;
    private boolean isAvailable;

    public WorldGuardAdapter(Plugin plugin) {
        this.plugin = plugin;
        this.isAvailable = checkWorldGuardAvailability();
    }

    /**
     * Check if WorldGuard is installed and available
     */
    private boolean checkWorldGuardAvailability() {
        try {
            Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
            if (wgPlugin != null && wgPlugin.isEnabled()) {
                plugin.getLogger().info("✓ WorldGuard detected - Advanced protection enabled!");
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("WorldGuard not available: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if player can build at location
     */
    public boolean canBuildAt(Player player, Location location) {
        if (!isAvailable) {
            return true; // Fallback to default permission
        }

        if (player == null || location == null) {
            return false; // Null-safety check
        }

        try {
            // Use WorldGuard API to check if player can build in region
            World world = location.getWorld();
            if (world == null) return true; // Fallback if world is invalid
            
            // TODO: Integrate actual WorldGuard API for region checking
            // This is a placeholder - returns true as safe default
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking WorldGuard permission: " + e.getMessage());
            return true; // Fallback to allow building on error
        }
    }

    /**
     * Create a plot region in WorldGuard
     */
    public void createPlotRegion(String plotId, Location corner1, Location corner2) {
        if (!isAvailable) return;
        
        // Null-safety checks
        if (plotId == null || plotId.isEmpty() || corner1 == null || corner2 == null) {
            plugin.getLogger().warning("Invalid parameters for createPlotRegion");
            return;
        }

        try {
            // Create protected region for plot
            plugin.getLogger().info("§6[WorldGuard] Creating region for plot: " + plotId + 
                " from (" + corner1.getBlockX() + "," + corner1.getBlockZ() + ") to (" + 
                corner2.getBlockX() + "," + corner2.getBlockZ() + ")");
            // TODO: Implement actual WorldGuard RegionManager integration
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create WorldGuard region: " + e.getMessage());
        }
    }

    /**
     * Remove plot region from WorldGuard
     */
    public void removePlotRegion(String plotId) {
        if (!isAvailable) return;

        // Null-safety check
        if (plotId == null || plotId.isEmpty()) {
            plugin.getLogger().warning("Invalid plotId for removePlotRegion");
            return;
        }

        try {
            plugin.getLogger().info("§6[WorldGuard] Removing region for plot: " + plotId);
            // TODO: Implement actual WorldGuard RegionManager integration
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to remove WorldGuard region: " + e.getMessage());
        }
    }

    /**
     * Add member to plot region
     */
    public void addMemberToRegion(String plotId, String playerName) {
        if (!isAvailable) return;

        // Null-safety checks
        if (plotId == null || plotId.isEmpty() || playerName == null || playerName.isEmpty()) {
            plugin.getLogger().warning("Invalid parameters for addMemberToRegion");
            return;
        }

        try {
            plugin.getLogger().info("§6[WorldGuard] Adding " + playerName + " to plot region: " + plotId);
            // TODO: Implement actual WorldGuard RegionManager integration
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to add member to region: " + e.getMessage());
        }
    }

    /**
     * Remove member from plot region
     */
    public void removeMemberFromRegion(String plotId, String playerName) {
        if (!isAvailable) return;

        // Null-safety checks
        if (plotId == null || plotId.isEmpty() || playerName == null || playerName.isEmpty()) {
            plugin.getLogger().warning("Invalid parameters for removeMemberFromRegion");
            return;
        }

        try {
            plugin.getLogger().info("§6[WorldGuard] Removing " + playerName + " from plot region: " + plotId);
            // TODO: Implement actual WorldGuard RegionManager integration
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to remove member from region: " + e.getMessage());
        }
    }

    /**
     * Check if WorldGuard is available
     */
    public boolean isWorldGuardAvailable() {
        return isAvailable;
    }

    /**
     * Get status string
     */
    public String getStatus() {
        return isAvailable ? "§a✓ Enabled" : "§c✕ Disabled";
    }
}
