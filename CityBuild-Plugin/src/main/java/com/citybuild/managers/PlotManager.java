package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class PlotManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, List<Integer>> playerPlots;
    private final int plotBuyPrice;
    private final int plotSellPrice;
    private int nextPlotId = 1;
    
    // Plot Spawning System
    private final int PLOT_SIZE = 16; // 16x16 blocks per plot
    private final int PLOT_SPACING = 2; // 2 blocks spacing between plots
    private World plotWorld;

    public PlotManager(JavaPlugin plugin, World plotWorld) {
        this.plugin = plugin;
        this.plotWorld = plotWorld;
        this.dataFile = new File(plugin.getDataFolder(), "data/plots.json");
        this.playerPlots = new HashMap<>();
        this.plotBuyPrice = plugin.getConfig().getInt("economy.plot_buy_price", 5000);
        this.plotSellPrice = plugin.getConfig().getInt("economy.plot_sell_price", 4000);
        
        dataFile.getParentFile().mkdirs();
        loadData();
    }

    public void addPlot(String playerUuid) {
        playerPlots.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(nextPlotId++);
        saveData();
    }

    public void removePlot(String playerUuid) {
        List<Integer> plots = playerPlots.get(playerUuid);
        if (plots != null && !plots.isEmpty()) {
            plots.remove(0);
            if (plots.isEmpty()) {
                playerPlots.remove(playerUuid);
            }
            saveData();
        }
    }

    public int getPlotCount(String playerUuid) {
        return playerPlots.getOrDefault(playerUuid, new ArrayList<>()).size();
    }

    public boolean hasPlots(String playerUuid) {
        return getPlotCount(playerUuid) > 0;
    }

    public List<Integer> getPlayerPlots(String playerUuid) {
        return new ArrayList<>(playerPlots.getOrDefault(playerUuid, new ArrayList<>()));
    }

    public int getPlotBuyPrice() {
        return plotBuyPrice;
    }

    public int getPlotSellPrice() {
        return plotSellPrice;
    }

    public int getTotalPlots() {
        return playerPlots.values().stream().mapToInt(List::size).sum();
    }
    
    /**
     * Calculate plot spawn location based on plot ID
     * Uses grid system: 16x16 blocks per plot with 2 block spacing
     */
    public Location getPlotSpawn(int plotId) {
        int index = plotId - 1;
        int plotsPerRow = 10; // 10 plots per row
        
        int row = index / plotsPerRow;
        int col = index % plotsPerRow;
        
        int plotDistance = PLOT_SIZE + PLOT_SPACING;
        int x = col * plotDistance;
        int z = row * plotDistance;
        
        return new Location(plotWorld, x + 8, 65, z + 8); // Center of plot at Y=65
    }
    
    /**
     * Get all plot spawn locations for a player
     */
    public List<Location> getPlayerPlotLocations(String playerUuid) {
        List<Integer> plots = getPlayerPlots(playerUuid);
        List<Location> locations = new ArrayList<>();
        
        for (Integer plotId : plots) {
            locations.add(getPlotSpawn(plotId));
        }
        
        return locations;
    }
    
    /**
     * Teleport player to first plot location
     */
    public Location getFirstPlotLocation(String playerUuid) {
        List<Integer> plots = getPlayerPlots(playerUuid);
        if (plots.isEmpty()) {
            return null;
        }
        return getPlotSpawn(plots.get(0));
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }
            
            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            
            json.entrySet().forEach(entry -> {
                List<Integer> plots = new ArrayList<>();
                JsonArray array = entry.getValue().getAsJsonArray();
                array.forEach(el -> plots.add(el.getAsInt()));
                playerPlots.put(entry.getKey(), plots);
                nextPlotId = Math.max(nextPlotId, plots.isEmpty() ? 1 : plots.stream().max(Integer::compare).orElse(0) + 1);
            });
            
            plugin.getLogger().info("✓ Loaded plots from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load plot data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            playerPlots.forEach((uuid, plots) -> {
                json.add(uuid, gson.toJsonTree(plots));
            });
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save plot data: " + e.getMessage());
        }
    }
}
