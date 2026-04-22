package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;

public class PlaytimeManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, PlaytimeData> playerPlaytime;
    private final EconomyManager economy;

    public static class PlaytimeData {
        public String uuid;
        public long minutesOnline; // Total minutes
        public LocalDate lastRewarded;
        public long totalEarned;

        public PlaytimeData(String uuid) {
            this.uuid = uuid;
            this.minutesOnline = 0;
            this.lastRewarded = LocalDate.now();
            this.totalEarned = 0;
        }
    }

    public PlaytimeManager(JavaPlugin plugin, EconomyManager economy) {
        this.plugin = plugin;
        this.economy = economy;
        this.dataFile = new File(plugin.getDataFolder(), "data/playtime.json");
        this.playerPlaytime = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
        startPlaytimeTracker();
    }

    /**
     * Start tracking playtime every minute
     */
    private void startPlaytimeTracker() {
        org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                addPlaytime(player.getUniqueId().toString(), 1);
            }
        }, 20L * 60, 20L * 60); // Every minute
    }

    /**
     * Add playtime minutes
     */
    public void addPlaytime(String playerUuid, long minutes) {
        PlaytimeData data = playerPlaytime.computeIfAbsent(playerUuid, PlaytimeData::new);
        data.minutesOnline += minutes;

        // Check for hourly rewards
        if (data.minutesOnline % 60 == 0) {
            long hours = data.minutesOnline / 60;
            if (hours % 1 == 0 && !data.lastRewarded.equals(LocalDate.now())) {
                long reward = 250 * hours; // $250 per hour
                economy.addBalance(playerUuid, reward);
                data.totalEarned += reward;
                data.lastRewarded = LocalDate.now();
                saveData();
            }
        }
    }

    /**
     * Get playtime in minutes
     */
    public long getPlaytime(String playerUuid) {
        return playerPlaytime.getOrDefault(playerUuid, new PlaytimeData(playerUuid)).minutesOnline;
    }

    /**
     * Get playtime in hours
     */
    public long getPlaytimeHours(String playerUuid) {
        return getPlaytime(playerUuid) / 60;
    }

    /**
     * Get total earned from playtime
     */
    public long getTotalEarned(String playerUuid) {
        return playerPlaytime.getOrDefault(playerUuid, new PlaytimeData(playerUuid)).totalEarned;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                PlaytimeData data = new PlaytimeData(entry.getKey());
                JsonObject dJson = entry.getValue().getAsJsonObject();

                if (dJson.has("minutesOnline")) {
                    data.minutesOnline = dJson.get("minutesOnline").getAsLong();
                }
                if (dJson.has("totalEarned")) {
                    data.totalEarned = dJson.get("totalEarned").getAsLong();
                }

                playerPlaytime.put(entry.getKey(), data);
            });

            plugin.getLogger().info("✓ Loaded playtime data");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load playtime data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            playerPlaytime.forEach((uuid, data) -> {
                JsonObject dJson = new JsonObject();
                dJson.addProperty("minutesOnline", data.minutesOnline);
                dJson.addProperty("totalEarned", data.totalEarned);
                json.add(uuid, dJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save playtime data: " + e.getMessage());
        }
    }
}
