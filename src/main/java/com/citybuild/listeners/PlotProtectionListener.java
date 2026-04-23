package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.plots.Plot;
import com.citybuild.features.plots.PlotManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

/**
 * PlotProtectionListener - Protects plots from unauthorized building
 * Prevents players from breaking/placing blocks outside their own plots
 */
public class PlotProtectionListener implements Listener {
    
    private final PlotManager plotManager;
    
    public PlotProtectionListener(CityBuildPlugin plugin, PlotManager plotManager) {
        this.plotManager = plotManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        // OPs can always break blocks
        if (player.isOp()) {
            return;
        }
        
        int x = event.getBlock().getX();
        int z = event.getBlock().getZ();
        
        // Check if block is in a plot
        Plot plotAtBlock = findPlotAtBlock(x, z);
        
        if (plotAtBlock != null) {
            // Plot exists at this location
            if (!plotAtBlock.isOwned()) {
                // Unowned plot - nobody can break blocks
                event.setCancelled(true);
                player.sendMessage("§cDieser Plot gehört niemandem! Du kannst hier nicht bauen.");
                return;
            }
            
            if (!plotAtBlock.canBuild(player.getUniqueId())) {
                // Plot is owned but player is not owner/co-owner
                event.setCancelled(true);
                player.sendMessage(String.format("§cDieser Plot gehört jemandem anderem! Du darfst hier nicht bauen."));
                return;
            }
            
            // Player has permission to build
            return;
        }
        
        // Block is not in any plot - allow breaking (farming world)
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        // OPs can always place blocks
        if (player.isOp()) {
            return;
        }
        
        int x = event.getBlock().getX();
        int z = event.getBlock().getZ();
        
        // Check if block is in a plot
        Plot plotAtBlock = findPlotAtBlock(x, z);
        
        if (plotAtBlock != null) {
            // Plot exists at this location
            if (!plotAtBlock.isOwned()) {
                // Unowned plot - nobody can place blocks
                event.setCancelled(true);
                player.sendMessage("§cDieser Plot gehört niemandem! Du kannst hier nicht bauen.");
                return;
            }
            
            if (!plotAtBlock.canBuild(player.getUniqueId())) {
                // Plot is owned but player is not owner/co-owner
                event.setCancelled(true);
                player.sendMessage(String.format("§cDieser Plot gehört jemandem anderem! Du darfst hier nicht bauen."));
                return;
            }
            
            // Player has permission to build
            return;
        }
        
        // Block is not in any plot - allow placement (farming world)
    }
    
    /**
     * Find plot at given X,Z coordinates
     */
    private Plot findPlotAtBlock(int x, int z) {
        for (String plotId : plotManager.listAllPlots()) {
            Plot plot = plotManager.getPlot(plotId);
            if (plot != null && plot.isInPlot(x, 0, z)) {
                return plot;
            }
        }
        return null;
    }
}
