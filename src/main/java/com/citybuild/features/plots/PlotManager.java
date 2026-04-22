package com.citybuild.features.plots;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

    public boolean buyPlot(Player player, int price) {
        if (playerPlots.containsKey(player.getUniqueId().toString())) {
            player.sendMessage("§cYou already own a plot!");
            return false;
        }

        String plotId = "plot_" + (++plotCounter);
        Plot plot = new Plot(plotId, player.getUniqueId(), price);
        
        plots.put(plotId, plot);
        playerPlots.put(player.getUniqueId().toString(), plotId);
        
        player.sendMessage("§a✓ Plot purchased! ID: " + plotId);
        return true;
    }

    public boolean sellPlot(Player player, int sellPrice) {
        String plotId = playerPlots.get(player.getUniqueId().toString());
        if (plotId == null) {
            player.sendMessage("§cYou don't own a plot!");
            return false;
        }

        plots.remove(plotId);
        playerPlots.remove(player.getUniqueId().toString());
        
        player.sendMessage("§a✓ Plot sold for $" + sellPrice);
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
