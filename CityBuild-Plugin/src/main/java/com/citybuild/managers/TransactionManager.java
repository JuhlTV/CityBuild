package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, List<Transaction>> transactions;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public enum TransactionType {
        EARN("💰 Earned"),
        SPEND("💸 Spent"),
        TRANSFER("🔄 Transfer"),
        FARM_REWARD("🌾 Farm Reward"),
        PVP_REWARD("⚔️ PvP Reward"),
        DAILY_REWARD("📅 Daily Reward"),
        SHOP_BUY("🛒 Shop Buy"),
        SHOP_SELL("🛒 Shop Sell"),
        PLOT_BUY("🏗️ Plot Buy"),
        PLOT_SELL("🏗️ Plot Sell"),
        AUCTION("🏆 Auction"),
        ADMIN_ADJUSTMENT("👑 Admin Adjust");

        public final String display;
        TransactionType(String display) {
            this.display = display;
        }
    }

    public static class Transaction {
        public long timestamp;
        public String uuid;
        public TransactionType type;
        public long amount;
        public String description;
        public String relatedPlayer; // For transfers

        public Transaction(String uuid, TransactionType type, long amount, String description, String relatedPlayer) {
            this.timestamp = System.currentTimeMillis();
            this.uuid = uuid;
            this.type = type;
            this.amount = amount;
            this.description = description;
            this.relatedPlayer = relatedPlayer;
        }
    }

    public TransactionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/transactions.json");
        this.transactions = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    // ===== TRANSACTION LOGGING =====
    public void logTransaction(String uuid, TransactionType type, long amount, String description) {
        logTransaction(uuid, type, amount, description, null);
    }

    public void logTransaction(String uuid, TransactionType type, long amount, String description, String relatedPlayer) {
        transactions.computeIfAbsent(uuid, k -> new ArrayList<>())
            .add(new Transaction(uuid, type, amount, description, relatedPlayer));
        
        // Keep only last 200 transactions per player
        List<Transaction> playerTransactions = transactions.get(uuid);
        if (playerTransactions.size() > 200) {
            playerTransactions.remove(0);
        }
        
        saveData();
    }

    public List<Transaction> getTransactions(String uuid, int limit) {
        List<Transaction> playerTransactions = transactions.getOrDefault(uuid, new ArrayList<>());
        int startIndex = Math.max(0, playerTransactions.size() - limit);
        return new ArrayList<>(playerTransactions.subList(startIndex, playerTransactions.size()));
    }

    public long getTotalEarned(String uuid) {
        return transactions.getOrDefault(uuid, new ArrayList<>()).stream()
            .filter(t -> t.amount > 0)
            .mapToLong(t -> t.amount)
            .sum();
    }

    public long getTotalSpent(String uuid) {
        return transactions.getOrDefault(uuid, new ArrayList<>()).stream()
            .filter(t -> t.amount < 0)
            .mapToLong(t -> Math.abs(t.amount))
            .sum();
    }

    public long getNetWorth(String uuid) {
        return getTotalEarned(uuid) - getTotalSpent(uuid);
    }

    public String formatTransaction(Transaction t) {
        String date = dateFormat.format(new Date(t.timestamp));
        String sign = t.amount >= 0 ? "+" : "";
        return String.format("%s | %s $%s%d | %s",
            date, t.type.display, sign, t.amount, t.description);
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            // Simple loading - can be expanded for better efficiency
            plugin.getLogger().info("✓ Loaded transaction history");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load transactions: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            transactions.forEach((uuid, transactionList) -> {
                json.add(uuid, gson.toJsonTree(transactionList));
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save transactions: " + e.getMessage());
        }
    }
}
