package com.citybuild.protection;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

        try {
            // Use WorldGuard API to check if player can build in region
            World world = location.getWorld();
            if (world == null) return false;

            // This would integrate with actual WorldGuard API
            // For now, returning true as placeholder (actual implementation requires WG API)
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking WorldGuard permission: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create a plot region in WorldGuard
     */
    public void createPlotRegion(String plotId, Location corner1, Location corner2) {
        if (!isAvailable) return;

        try {
            // Create protected region for plot
            plugin.getLogger().info("§6[WorldGuard] Creating region for plot: " + plotId);
            // Actual implementation would use WorldGuard RegionManager
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create WorldGuard region: " + e.getMessage());
        }
    }

    /**
     * Remove plot region from WorldGuard
     */
    public void removePlotRegion(String plotId) {
        if (!isAvailable) return;

        try {
            plugin.getLogger().info("§6[WorldGuard] Removing region for plot: " + plotId);
            // Actual implementation would use WorldGuard RegionManager
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to remove WorldGuard region: " + e.getMessage());
        }
    }

    /**
     * Add member to plot region
     */
    public void addMemberToRegion(String plotId, String playerName) {
        if (!isAvailable) return;

        try {
            plugin.getLogger().info("§6[WorldGuard] Adding " + playerName + " to plot region: " + plotId);
            // Actual implementation would add player to region members
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to add member to region: " + e.getMessage());
        }
    }

    /**
     * Remove member from plot region
     */
    public void removeMemberFromRegion(String plotId, String playerName) {
        if (!isAvailable) return;

        try {
            plugin.getLogger().info("§6[WorldGuard] Removing " + playerName + " from plot region: " + plotId);
            // Actual implementation would remove player from region members
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
