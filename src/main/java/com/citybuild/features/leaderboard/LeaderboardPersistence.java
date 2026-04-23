package com.citybuild.features.leaderboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Persists leaderboard rankings to JSON
 */
public class LeaderboardPersistence {

    private final JavaPlugin plugin;
    private final File dataFolder;
    private final Gson gson;

    public LeaderboardPersistence(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data/leaderboards");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }

    /**
     * Save leaderboard
     */
    public void saveLeaderboard(String name, LinkedHashMap<String, Double> rankings) {
        try {
            File file = new File(dataFolder, name + ".json");
            FileWriter writer = new FileWriter(file);
            gson.toJson(rankings, writer);
            writer.close();

            plugin.getLogger().info("§6Leaderboard saved: " + name);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save leaderboard: " + e.getMessage());
        }
    }

    /**
     * Load leaderboard
     */
    public LinkedHashMap<String, Double> loadLeaderboard(String name) {
        try {
            File file = new File(dataFolder, name + ".json");
            if (!file.exists()) {
                return new LinkedHashMap<>();
            }

            FileReader reader = new FileReader(file);
            LinkedHashMap<String, Double> rankings = gson.fromJson(reader,
                com.google.gson.reflect.TypeToken.getParameterized(LinkedHashMap.class, String.class, Double.class).getType());
            reader.close();

            return rankings != null ? rankings : new LinkedHashMap<>();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load leaderboard: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    /**
     * Check if leaderboard exists
     */
    public boolean hasLeaderboard(String name) {
        File file = new File(dataFolder, name + ".json");
        return file.exists() && file.length() > 0;
    }

    /**
     * Get leaderboards list
     */
    public List<String> getLeaderboardNames() {
        List<String> names = new ArrayList<>();
        File[] files = dataFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    names.add(file.getName().replace(".json", ""));
                }
            }
        }
        return names;
    }
}
