package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.PlotManager;
import com.citybuild.model.PlotData;
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
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        // Check all player plots to see if location is within one
        java.util.List<PlotData> playerPlots = plotManager.getPlayerPlotData(uuid);
        for (PlotData plot : playerPlots) {
            if (plot.isLocationInPlot(x, z)) {
                return true; // Player owns this plot
            }
        }

        // Check if player is member of any plot at this location
        java.util.List<PlotData> allPlots = getAllPlotsAtLocation(x, z);
        for (PlotData plot : allPlots) {
            if (plot.isMember(uuid)) {
                return true; // Player is member of this plot
            }
        }

        // Player doesn't own or is not member - block action
        return false;
    }

    /**
     * Get all plots that could contain a location
     */
    private java.util.List<PlotData> getAllPlotsAtLocation(int x, int z) {
        java.util.List<PlotData> result = new java.util.ArrayList<>();
        
        // Grid calculation (same as in PlotData.isLocationInPlot)
        final int PLOT_SIZE = 16;
        final int PLOT_SPACING = 2;
        final int PLOTS_PER_ROW = 10;
        final int plotDistance = PLOT_SIZE + PLOT_SPACING;

        // Calculate grid cell
        int gridX = x / plotDistance;
        int gridZ = z / plotDistance;

        // Check surrounding grid cells (in case of boundary)
        for (int gx = gridX - 1; gx <= gridX + 1; gx++) {
            for (int gz = gridZ - 1; gz <= gridZ + 1; gz++) {
                if (gx < 0 || gz < 0) continue;
                
                int plotId = gz * PLOTS_PER_ROW + gx + 1;
                PlotData plot = plotManager.getPlot(plotId);
                if (plot != null && plot.isLocationInBoundary(x, z, 1)) {
                    result.add(plot);
                }
            }
        }

        return result;
    }
}
