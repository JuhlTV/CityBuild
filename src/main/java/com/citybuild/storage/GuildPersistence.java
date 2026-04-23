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
 * Handles persistence of all guild data to JSON
 */
public class GuildPersistence {

    private final JavaPlugin plugin;
    private final Gson gson;
    private final File guildsFile;

    public GuildPersistence(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        this.guildsFile = new File(dataFolder, "guilds.json");
    }

    /**
     * Save all guilds to JSON
     */
    public void saveGuilds(Map<String, Object> guildsData) {
        try (FileWriter writer = new FileWriter(guildsFile)) {
            gson.toJson(guildsData, writer);
            plugin.getLogger().info("✓ Guilds saved to JSON");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save guilds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load all guilds from JSON
     */
    public Map<String, Object> loadGuilds() {
        if (!guildsFile.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(guildsFile)) {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = gson.fromJson(reader, mapType);
            return data != null ? data : new HashMap<>();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load guilds: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Check if guilds file exists
     */
    public boolean hasGuildsData() {
        return guildsFile.exists() && guildsFile.length() > 0;
    }

    /**
     * Clear all guild data
     */
    public void clearGuildsData() {
        if (guildsFile.exists()) {
            guildsFile.delete();
        }
    }
}
