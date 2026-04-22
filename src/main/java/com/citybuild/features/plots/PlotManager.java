package com.citybuild.features.plots;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.*;

public class PlotManager {
    private final Plugin plugin;
    private final Map<String, Plot> plots;
    private final Map<String, String> playerPlots; // player UUID -> plot ID
    private int plotCounter = 0;

    public PlotManager(Plugin plugin) {
        this.plugin = plugin;
        this.plots = new HashMap<>();
        this.playerPlots = new HashMap<>();
        loadAllData();
    }

    public boolean buyPlot(Player player, double price) {
        String uuid = player.getUniqueId().toString();
        if (playerPlots.containsKey(uuid)) {
            player.sendMessage("§cYou already own a plot!");
            return false;
        }

        String plotId = "plot_" + (++plotCounter);
        Plot plot = new Plot(plotId, player.getUniqueId(), price);
        
        plots.put(plotId, plot);
        playerPlots.put(uuid, plotId);
        
        player.sendMessage("§a✓ Plot purchased! ID: " + plotId);
        plugin.getLogger().info(player.getName() + " bought plot " + plotId);
        return true;
    }

    public boolean sellPlot(Player player, double sellPrice) {
        String uuid = player.getUniqueId().toString();
        String plotId = playerPlots.get(uuid);
        if (plotId == null) {
            player.sendMessage("§cYou don't own a plot!");
            return false;
        }

        plots.remove(plotId);
        playerPlots.remove(uuid);
        
        player.sendMessage("§a✓ Plot sold for $" + String.format("%.2f", sellPrice));
        plugin.getLogger().info(player.getName() + " sold plot " + plotId);
        return true;
    }

    public Plot getPlayerPlot(Player player) {
        String plotId = playerPlots.get(player.getUniqueId().toString());
        return plotId != null ? plots.get(plotId) : null;
    }

    public Plot getPlot(String plotId) {
        return plots.get(plotId);
    }

    public int getTotalPlots() {
        return plots.size();
    }

    public List<String> listAllPlots() {
        return new ArrayList<>(plots.keySet());
    }

    public void saveAllData() {
        plugin.getLogger().info("Saving " + plots.size() + " plots...");
        // Implement data persistence (YAML, Database, etc.)
    }

    public void loadAllData() {
        // Implement data loading
        plugin.getLogger().info("Loading plots...");
    }
}
