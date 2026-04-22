package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class EconomyManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, PlayerData> playerData;
    private final int startingBalance;
    
    public static class PlayerData {
        public String uuid;
        public long balance;
        public long lastTransaction;
        public int plots;
        
        public PlayerData(String uuid) {
            this.uuid = uuid;
            this.balance = 10000;
            this.lastTransaction = System.currentTimeMillis();
            this.plots = 0;
        }
    }

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/players.json");
        this.playerData = new HashMap<>();
        this.startingBalance = plugin.getConfig().getInt("economy.starting_balance", 10000);
        
        // Create data directory
        dataFile.getParentFile().mkdirs();
        
        loadData();
    }

    public void initializePlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (!playerData.containsKey(uuid)) {
            PlayerData data = new PlayerData(uuid);
            data.balance = startingBalance;
            playerData.put(uuid, data);
            saveData();
        }
    }

    public long getBalance(Player player) {
        initializePlayer(player);
        return playerData.get(player.getUniqueId().toString()).balance;
    }

    public void setBalance(Player player, long amount) {
        initializePlayer(player);
        PlayerData data = playerData.get(player.getUniqueId().toString());
        data.balance = amount;
        data.lastTransaction = System.currentTimeMillis();
        saveData();
    }

    public void addBalance(Player player, long amount) {
        long current = getBalance(player);
        setBalance(player, current + amount);
    }

    public void addBalance(String playerUuid, long amount) {
        long current = getBalance(playerUuid);
        setBalance(playerUuid, current + amount);
    }

    public void removeBalance(Player player, long amount) {
        long current = getBalance(player);
        if (current >= amount) {
            setBalance(player, current - amount);
        }
    }

    public void removeBalance(String playerUuid, long amount) {
        long current = getBalance(playerUuid);
        if (current >= amount) {
            setBalance(playerUuid, current - amount);
        }
    }

    public long getBalance(String playerUuid) {
        if (!playerData.containsKey(playerUuid)) {
            playerData.put(playerUuid, new PlayerData(playerUuid, 10000, System.currentTimeMillis(), 0));
        }
        return playerData.get(playerUuid).balance;
    }

    public void setBalance(String playerUuid, long amount) {
        if (!playerData.containsKey(playerUuid)) {
            playerData.put(playerUuid, new PlayerData(playerUuid, 10000, System.currentTimeMillis(), 0));
        }
        PlayerData data = playerData.get(playerUuid);
        data.balance = amount;
        data.lastTransaction = System.currentTimeMillis();
        saveData();
    }

    public boolean canAfford(Player player, long amount) {
        return getBalance(player) >= amount;
    }

    public List<Map.Entry<String, Long>> getLeaderboard(int limit) {
        return playerData.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().balance, a.getValue().balance))
                .limit(limit)
                .map(e -> (Map.Entry<String, Long>) new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().balance))
                .toList();
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }
            
            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            
            json.entrySet().forEach(entry -> {
                JsonObject playerObj = entry.getValue().getAsJsonObject();
                PlayerData data = gson.fromJson(playerObj, PlayerData.class);
                playerData.put(entry.getKey(), data);
            });
            
            plugin.getLogger().info("✓ Loaded " + playerData.size() + " players from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load player data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            playerData.forEach((uuid, data) -> {
                json.add(uuid, gson.toJsonTree(data));
            });
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save player data: " + e.getMessage());
        }
    }
}
