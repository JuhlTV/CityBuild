package com.citybuild.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Handles persistence of all auction data to JSON
 */
public class AuctionPersistence {

    private final JavaPlugin plugin;
    private final Gson gson;
    private final File auctionsFile;

    public AuctionPersistence(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        this.auctionsFile = new File(dataFolder, "auctions.json");
    }

    /**
     * Save all auctions to JSON
     */
    public void saveAuctions(Map<String, Object> auctionsData) {
        try (FileWriter writer = new FileWriter(auctionsFile)) {
            gson.toJson(auctionsData, writer);
            plugin.getLogger().info("✓ Auctions saved to JSON");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save auctions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load all auctions from JSON
     */
    public Map<String, Object> loadAuctions() {
        if (!auctionsFile.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(auctionsFile)) {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = gson.fromJson(reader, mapType);
            return data != null ? data : new HashMap<>();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load auctions: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Check if auctions file exists
     */
    public boolean hasAuctionsData() {
        return auctionsFile.exists() && auctionsFile.length() > 0;
    }

    /**
     * Clear all auction data
     */
    public void clearAuctionsData() {
        if (auctionsFile.exists()) {
            auctionsFile.delete();
        }
    }
}
