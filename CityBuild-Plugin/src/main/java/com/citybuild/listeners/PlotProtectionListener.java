package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.PlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlotProtectionListener implements Listener {
    private final CityBuildPlugin plugin;
    private final PlotManager plotManager;

    public PlotProtectionListener(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.plotManager = plugin.getPlotManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Location loc = block.getLocation();

        // Only protect in plot world
        if (loc.getWorld() != plugin.getWorldManager().getPlotWorld()) {
            return;
        }

        // Check if player owns/is member of plot at this location
        if (!isPlayerAllowedAtLocation(player, loc)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("❌ You cannot build on other players' plots!", NamedTextColor.RED)));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        // Only protect in plot world
        if (loc.getWorld() != plugin.getWorldManager().getPlotWorld()) {
            return;
        }

        // Check if player owns/is member of plot at this location
        if (!isPlayerAllowedAtLocation(player, loc)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("❌ You cannot break blocks on other players' plots!", NamedTextColor.RED)));
        }
    }

    /**
     * Check if player is allowed to build at a location (owns or is member of the plot)
     */
    private boolean isPlayerAllowedAtLocation(Player player, Location loc) {
        String uuid = player.getUniqueId().toString();

        // Get plot at location (calculate which plot ID based on coordinates)
        int plotId = getPlotIdAtLocation(loc);
        
        if (plotId == -1) {
            // Outside all plots - allow building
            return true;
        }

        // Check if player owns this plot
        if (plotManager.isPlotOwner(uuid, plotId)) {
            return true;
        }

        // Check if player is a member of this plot
        if (plotManager.isPlotMember(uuid, plotId)) {
            return true;
        }

        // Player doesn't own or is not member - block action
        return false;
    }

    /**
     * Calculate plot ID from location coordinates
     */
    private int getPlotIdAtLocation(Location loc) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        final int PLOT_SIZE = 16;
        final int PLOT_SPACING = 2;
        final int PLOTS_PER_ROW = 10;
        final int plotDistance = PLOT_SIZE + PLOT_SPACING;

        // Calculate which grid cell this block is in
        int gridX = x / plotDistance;
        int gridZ = z / plotDistance;

        // Check if we're in the spacing between plots (not allowed)
        int localX = x % plotDistance;
        int localZ = z % plotDistance;

        if (localX >= PLOT_SIZE || localZ >= PLOT_SIZE) {
            // In spacing area
            return -1;
        }

        // Calculate plot ID (1-indexed)
        int plotId = gridZ * PLOTS_PER_ROW + gridX + 1;
        return plotId;
    }
}
