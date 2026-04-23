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
 * Handles persistence of all achievement data to JSON
 */
public class AchievementPersistence {

    private final JavaPlugin plugin;
    private final Gson gson;
    private final File achievementsFile;

    public AchievementPersistence(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        File playersFolder = new File(dataFolder, "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }
        
        this.achievementsFile = new File(playersFolder, "achievements.json");
    }

    /**
     * Save all player achievements to JSON
     */
    public void saveAllAchievements(Map<String, Map<String, Object>> allPlayerAchievements) {
        try (FileWriter writer = new FileWriter(achievementsFile)) {
            gson.toJson(allPlayerAchievements, writer);
            plugin.getLogger().info("✓ All achievements saved to JSON");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save achievements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load all player achievements from JSON
     */
    public Map<String, Map<String, Object>> loadAllAchievements() {
        if (!achievementsFile.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(achievementsFile)) {
            Type mapType = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
            Map<String, Map<String, Object>> data = gson.fromJson(reader, mapType);
            return data != null ? data : new HashMap<>();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load achievements: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Save achievements for a single player
     */
    public void savePlayerAchievements(String playerUUID, Map<String, Object> playerAchievements) {
        try {
            Map<String, Map<String, Object>> allData = loadAllAchievements();
            allData.put(playerUUID, playerAchievements);
            saveAllAchievements(allData);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save player achievements: " + e.getMessage());
        }
    }

    /**
     * Load achievements for a single player
     */
    public Map<String, Object> loadPlayerAchievements(String playerUUID) {
        Map<String, Map<String, Object>> allData = loadAllAchievements();
        return allData.getOrDefault(playerUUID, new HashMap<>());
    }

    /**
     * Check if achievements file exists
     */
    public boolean hasAchievementsData() {
        return achievementsFile.exists() && achievementsFile.length() > 0;
    }

    /**
     * Clear all achievement data
     */
    public void clearAchievementsData() {
        if (achievementsFile.exists()) {
            achievementsFile.delete();
        }
    }

    /**
     * Get achievement statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Map<String, Object>> allData = loadAllAchievements();
        
        stats.put("total_players", allData.size());
        stats.put("total_achievements_data", allData.values().stream()
            .mapToInt(Map::size)
            .sum());
        
        return stats;
    }
}
