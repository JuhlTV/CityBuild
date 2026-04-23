package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.plots.Plot;
import com.citybuild.features.plots.PlotManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PlotProtectionListener - Protects plots from unauthorized building
 * Prevents players from breaking/placing blocks outside their own plots
 */
public class PlotProtectionListener implements Listener {
    
    private final PlotManager plotManager;
    private final Map<UUID, Long> lastWarningByPlayer = new HashMap<>();
    private static final long WARNING_COOLDOWN_MS = 1500L;
    
    public PlotProtectionListener(CityBuildPlugin plugin, PlotManager plotManager) {
        this.plotManager = plotManager;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (enforceBuildPermission(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (enforceBuildPermission(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ())) {
            event.setCancelled(true);
        }
    }
    
    /**
     * Find plot at given X,Z coordinates
     */
    private Plot findPlotAtBlock(int x, int z) {
        return plotManager.findPlotAt(x, z);
    }

    private boolean enforceBuildPermission(Player player, int x, int z) {
        if (player.isOp()) {
            return false;
        }

        Plot plotAtBlock = findPlotAtBlock(x, z);
        if (plotAtBlock == null) {
            return false;
        }

        if (!plotAtBlock.isOwned()) {
            sendRateLimitedWarning(player, "§cDieser Plot gehört niemandem! Du kannst hier nicht bauen.");
            return true;
        }

        if (!plotAtBlock.canBuild(player.getUniqueId())) {
            sendRateLimitedWarning(player, "§cDieser Plot gehört jemandem anderem! Du darfst hier nicht bauen.");
            return true;
        }

        return false;
    }

    private void sendRateLimitedWarning(Player player, String message) {
        long now = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();
        long lastSent = lastWarningByPlayer.getOrDefault(playerId, 0L);

        if (now - lastSent >= WARNING_COOLDOWN_MS) {
            player.sendMessage(message);
            lastWarningByPlayer.put(playerId, now);
        }
    }
}
