package com.citybuild.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Handles persistence of all trade data to JSON
 */
public class TradePersistence {

    private final JavaPlugin plugin;
    private final Gson gson;
    private final File tradesFile;

    public TradePersistence(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        this.tradesFile = new File(dataFolder, "trades.json");
    }

    /**
     * Save all trades to JSON
     */
    public void saveTrades(Map<String, Object> tradesData) {
        try (FileWriter writer = new FileWriter(tradesFile)) {
            gson.toJson(tradesData, writer);
            plugin.getLogger().info("✓ Trades saved to JSON");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save trades: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load all trades from JSON
     */
    public Map<String, Object> loadTrades() {
        if (!tradesFile.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(tradesFile)) {
            Map<String, Object> data = gson.fromJson(reader, Map.class);
            return data != null ? data : new HashMap<>();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load trades: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Check if trades file exists
     */
    public boolean hasTradesData() {
        return tradesFile.exists() && tradesFile.length() > 0;
    }

    /**
     * Clear all trade data
     */
    public void clearTradesData() {
        if (tradesFile.exists()) {
            tradesFile.delete();
        }
    }
}
