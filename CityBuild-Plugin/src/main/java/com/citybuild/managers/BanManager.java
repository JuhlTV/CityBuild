package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class BanManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, BanData> bannedPlayers;

    public static class BanData {
        public String uuid;
        public String playerName;
        public long bannedAt;
        public long unbanAt; // -1 = permanent
        public String reason;
        public String bannedBy;
        public boolean active;

        public BanData(String uuid, String playerName, long unbanAt, String reason, String bannedBy) {
            this.uuid = uuid;
            this.playerName = playerName;
            this.bannedAt = System.currentTimeMillis();
            this.unbanAt = unbanAt;
            this.reason = reason;
            this.bannedBy = bannedBy;
            this.active = true;
        }
    }

    public BanManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/bans.json");
        this.bannedPlayers = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    // ===== BAN OPERATIONS =====
    public void banPlayer(String uuid, String playerName, long durationMs, String reason, String bannedBy) {
        long unbanAt = durationMs == -1 ? -1 : System.currentTimeMillis() + durationMs;
        BanData ban = new BanData(uuid, playerName, unbanAt, reason, bannedBy);
        bannedPlayers.put(uuid, ban);
        saveData();
    }

    public void permanentBan(String uuid, String playerName, String reason, String bannedBy) {
        banPlayer(uuid, playerName, -1, reason, bannedBy);
    }

    public void unbanPlayer(String uuid) {
        BanData ban = bannedPlayers.get(uuid);
        if (ban != null) {
            ban.active = false;
            saveData();
        }
    }

    public boolean isBanned(String uuid) {
        BanData ban = bannedPlayers.get(uuid);
        if (ban == null || !ban.active) return false;

        // Check if temporary ban expired
        if (ban.unbanAt != -1 && System.currentTimeMillis() >= ban.unbanAt) {
            unbanPlayer(uuid);
            return false;
        }

        return true;
    }

    public BanData getBanData(String uuid) {
        return bannedPlayers.get(uuid);
    }

    public String getBanReason(String uuid) {
        BanData ban = bannedPlayers.get(uuid);
        return ban != null ? ban.reason : "No reason provided";
    }

    public long getBanTimeRemaining(String uuid) {
        BanData ban = bannedPlayers.get(uuid);
        if (ban == null || ban.unbanAt == -1) return -1;
        
        long remaining = ban.unbanAt - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public List<BanData> getAllBans() {
        return new ArrayList<>(bannedPlayers.values());
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                JsonObject bJson = entry.getValue().getAsJsonObject();
                BanData ban = new BanData(
                    entry.getKey(),
                    bJson.get("playerName").getAsString(),
                    bJson.get("unbanAt").getAsLong(),
                    bJson.get("reason").getAsString(),
                    bJson.get("bannedBy").getAsString()
                );
                ban.bannedAt = bJson.get("bannedAt").getAsLong();
                ban.active = bJson.get("active").getAsBoolean();
                bannedPlayers.put(entry.getKey(), ban);
            });

            plugin.getLogger().info("✓ Loaded " + bannedPlayers.size() + " bans");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load bans: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            bannedPlayers.forEach((uuid, ban) -> {
                JsonObject bJson = new JsonObject();
                bJson.addProperty("playerName", ban.playerName);
                bJson.addProperty("bannedAt", ban.bannedAt);
                bJson.addProperty("unbanAt", ban.unbanAt);
                bJson.addProperty("reason", ban.reason);
                bJson.addProperty("bannedBy", ban.bannedBy);
                bJson.addProperty("active", ban.active);
                json.add(uuid, bJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save bans: " + e.getMessage());
        }
    }
}
