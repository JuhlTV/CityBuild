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
import java.util.logging.Logger;

public class EconomyManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, PlayerData> playerData;
    private final Logger logger;
    private long startingBalance;
    
    public static class PlayerData {
        public String uuid;
        public long balance;
        public long lastTransaction;
        public int plots;
        
        public PlayerData(String uuid) {
            this(uuid, 10000L, System.currentTimeMillis(), 0);
        }

        public PlayerData(String uuid, long balance, long lastTransaction, int plots) {
            this.uuid = uuid;
            this.balance = Math.max(0, balance); // Ensure non-negative
            this.lastTransaction = lastTransaction;
            this.plots = Math.max(0, plots);
        }
    }

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataFile = new File(plugin.getDataFolder(), "data/players.json");
        this.playerData = new HashMap<>();
        this.startingBalance = plugin.getConfig().getLong("economy.starting_balance", 10000L);
        
        // Create data directory
        if (!dataFile.getParentFile().exists()) {
            if (!dataFile.getParentFile().mkdirs()) {
                logger.warning("Failed to create data directory!");
            }
        }
        
        loadData();
        logger.info("EconomyManager initialized with " + playerData.size() + " players");
    }

    public void initializePlayer(Player player) {
        if (player == null) {
            logger.warning("Attempted to initialize null player!");
            return;
        }
        
        String uuid = player.getUniqueId().toString();
        if (!playerData.containsKey(uuid)) {
            PlayerData data = new PlayerData(uuid);
            data.balance = startingBalance;
            playerData.put(uuid, data);
            saveData();
            logger.fine("Initialized new player: " + player.getName());
        }
    }

    public long getBalance(Player player) {
        if (player == null) {
            logger.warning("Attempted to get balance for null player!");
            return 0;
        }
        
        initializePlayer(player);
        PlayerData data = playerData.get(player.getUniqueId().toString());
        return data != null ? data.balance : startingBalance;
    }

    public void setBalance(Player player, long amount) {
        if (player == null) {
            logger.warning("Attempted to set balance for null player!");
            return;
        }
        
        if (amount < 0) {
            logger.warning("Attempted to set negative balance for " + player.getName() + "!");
            return;
        }
        
        initializePlayer(player);
        PlayerData data = playerData.get(player.getUniqueId().toString());
        if (data != null) {
            data.balance = amount;
            data.lastTransaction = System.currentTimeMillis();
            saveData();
        }
    }

    public void addBalance(Player player, long amount) {
        if (player == null || amount < 0) {
            logger.warning("Invalid add balance call: player=" + player + ", amount=" + amount);
            return;
        }
        
        long current = getBalance(player);
        setBalance(player, current + amount);
    }

    public void addBalance(String playerUuid, long amount) {
        if (playerUuid == null || playerUuid.isEmpty() || amount < 0) {
            logger.warning("Invalid add balance call: uuid=" + playerUuid + ", amount=" + amount);
            return;
        }
        
        long current = getBalance(playerUuid);
        setBalance(playerUuid, current + amount);
    }

    public void removeBalance(Player player, long amount) {
        if (player == null || amount < 0) {
            logger.warning("Invalid remove balance call: player=" + player + ", amount=" + amount);
            return;
        }
        
        long current = getBalance(player);
        if (current >= amount) {
            setBalance(player, current - amount);
        } else {
            logger.warning(player.getName() + " tried to spend $" + amount + " but only has $" + current);
        }
    }

    public void removeBalance(String playerUuid, long amount) {
        if (playerUuid == null || playerUuid.isEmpty() || amount < 0) {
            logger.warning("Invalid remove balance call: uuid=" + playerUuid + ", amount=" + amount);
            return;
        }
        
        long current = getBalance(playerUuid);
        if (current >= amount) {
            setBalance(playerUuid, current - amount);
        }
    }

    public long getBalance(String playerUuid) {
        if (playerUuid == null || playerUuid.isEmpty()) {
            logger.warning("Attempted to get balance with invalid UUID!");
            return 0;
        }
        
        if (!playerData.containsKey(playerUuid)) {
            playerData.put(playerUuid, new PlayerData(playerUuid, startingBalance, System.currentTimeMillis(), 0));
            saveData();
        }
        
        PlayerData data = playerData.get(playerUuid);
        return data != null ? data.balance : startingBalance;
    }

    public void setBalance(String playerUuid, long amount) {
        if (playerUuid == null || playerUuid.isEmpty() || amount < 0) {
            logger.warning("Invalid set balance call: uuid=" + playerUuid + ", amount=" + amount);
            return;
        }
        
        if (!playerData.containsKey(playerUuid)) {
            playerData.put(playerUuid, new PlayerData(playerUuid, startingBalance, System.currentTimeMillis(), 0));
        }
        
        PlayerData data = playerData.get(playerUuid);
        if (data != null) {
            data.balance = amount;
            data.lastTransaction = System.currentTimeMillis();
            saveData();
        }
    }

    public boolean canAfford(Player player, long amount) {
        if (player == null || amount < 0) {
            return false;
        }
        return getBalance(player) >= amount;
    }

    public boolean canAfford(String playerUuid, long amount) {
        return getBalance(playerUuid) >= amount;
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
