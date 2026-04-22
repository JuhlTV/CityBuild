package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class AchievementManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, Set<String>> playerAchievements; // UUID -> Set of achievement IDs

    public enum Achievement {
        FIRST_PLOT("first_plot", "First Plot Owner", "Buy your first plot", "🏗️"),
        MILLIONAIRE("millionaire", "Millionaire", "Reach $1,000,000", "💰"),
        MASTER_BUILDER("master_builder", "Master Builder", "Place 1000 blocks", "🔨"),
        MONSTER_SLAYER("monster_slayer", "Monster Slayer", "Kill 500 monsters", "⚔️"),
        FARM_MASTER("farm_master", "Farm Master", "Mine 5000 blocks", "🌾"),
        TRADER("trader", "Trader", "Buy/Sell 100 items", "🛍️"),
        LUCKY_STREAK("lucky_streak", "Lucky Streak", "Get daily reward 30 days", "🎲"),
        SOCIAL_BUTTERFLY("social_butterfly", "Social Butterfly", "Add 10 plot members", "👥"),
        RICH_QUICK("rich_quick", "Get Rich Quick", "Earn $50,000 from farming", "💸"),
        GLOBAL_TRAVELER("global_traveler", "Global Traveler", "Visit all 3 worlds", "🌍");

        public final String id;
        public final String displayName;
        public final String description;
        public final String emoji;

        Achievement(String id, String displayName, String description, String emoji) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.emoji = emoji;
        }
    }

    public AchievementManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/achievements.json");
        this.playerAchievements = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    /**
     * Unlock an achievement for a player
     */
    public boolean unlockAchievement(String playerUuid, Achievement achievement) {
        Set<String> achievements = playerAchievements.computeIfAbsent(playerUuid, k -> new HashSet<>());

        if (achievements.contains(achievement.id)) {
            return false; // Already unlocked
        }

        achievements.add(achievement.id);
        saveData();
        return true; // Newly unlocked
    }

    /**
     * Check if player has achievement
     */
    public boolean hasAchievement(String playerUuid, Achievement achievement) {
        Set<String> achievements = playerAchievements.getOrDefault(playerUuid, new HashSet<>());
        return achievements.contains(achievement.id);
    }

    /**
     * Get all achievements for a player
     */
    public Set<String> getPlayerAchievements(String playerUuid) {
        return new HashSet<>(playerAchievements.getOrDefault(playerUuid, new HashSet<>()));
    }

    /**
     * Get achievement count for a player
     */
    public int getAchievementCount(String playerUuid) {
        return playerAchievements.getOrDefault(playerUuid, new HashSet<>()).size();
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();

            json.entrySet().forEach(entry -> {
                Set<String> achievements = new HashSet<>();
                JsonArray array = entry.getValue().getAsJsonArray();
                array.forEach(el -> achievements.add(el.getAsString()));
                playerAchievements.put(entry.getKey(), achievements);
            });

            plugin.getLogger().info("✓ Loaded achievements from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load achievement data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();

            JsonObject json = new JsonObject();
            playerAchievements.forEach((uuid, achievements) -> {
                json.add(uuid, gson.toJsonTree(new ArrayList<>(achievements)));
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save achievement data: " + e.getMessage());
        }
    }
}
