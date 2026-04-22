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

public class TradingManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, TradeRequest> tradeRequests;
    private int nextTradeId = 1;

    public static class TradeRequest {
        public int tradeId;
        public String initiator; // UUID
        public String target; // UUID
        public String initiatorItem; // Item name
        public String targetItem; // Item name
        public long initiatorPrice;
        public long targetPrice;
        public long createdAt;
        public boolean accepted;

        public TradeRequest(int id, String initiator, String target, String iItem, String tItem, long iPrice, long tPrice) {
            this.tradeId = id;
            this.initiator = initiator;
            this.target = target;
            this.initiatorItem = iItem;
            this.targetItem = tItem;
            this.initiatorPrice = iPrice;
            this.targetPrice = tPrice;
            this.createdAt = System.currentTimeMillis();
            this.accepted = false;
        }
    }

    public TradingManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/trades.json");
        this.tradeRequests = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    /**
     * Create a trade request
     */
    public TradeRequest createTradeRequest(String initiator, String target, String iItem, String tItem, long iPrice, long tPrice) {
        TradeRequest request = new TradeRequest(nextTradeId++, initiator, target, iItem, tItem, iPrice, tPrice);
        tradeRequests.put(initiator + ":" + target, request);
        saveData();
        return request;
    }

    /**
     * Accept a trade
     */
    public boolean acceptTrade(String initiator, String target) {
        TradeRequest request = tradeRequests.get(initiator + ":" + target);
        if (request != null) {
            request.accepted = true;
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Cancel a trade
     */
    public boolean cancelTrade(String initiator, String target) {
        if (tradeRequests.remove(initiator + ":" + target) != null) {
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Get trade for players
     */
    public TradeRequest getTrade(String initiator, String target) {
        return tradeRequests.get(initiator + ":" + target);
    }

    /**
     * Get all pending trades for player
     */
    public List<TradeRequest> getPendingTrades(String playerUuid) {
        List<TradeRequest> pending = new ArrayList<>();
        for (TradeRequest tr : tradeRequests.values()) {
            if ((tr.target.equals(playerUuid) && !tr.accepted)) {
                pending.add(tr);
            }
        }
        return pending;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                JsonObject tradeJson = entry.getValue().getAsJsonObject();
                TradeRequest tr = new TradeRequest(
                    tradeJson.get("tradeId").getAsInt(),
                    tradeJson.get("initiator").getAsString(),
                    tradeJson.get("target").getAsString(),
                    tradeJson.get("initiatorItem").getAsString(),
                    tradeJson.get("targetItem").getAsString(),
                    tradeJson.get("initiatorPrice").getAsLong(),
                    tradeJson.get("targetPrice").getAsLong()
                );
                tr.accepted = tradeJson.has("accepted") && tradeJson.get("accepted").getAsBoolean();
                tradeRequests.put(entry.getKey(), tr);
            });

            plugin.getLogger().info("✓ Loaded trades from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load trade data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            tradeRequests.forEach((key, tr) -> {
                JsonObject tradeJson = new JsonObject();
                tradeJson.addProperty("tradeId", tr.tradeId);
                tradeJson.addProperty("initiator", tr.initiator);
                tradeJson.addProperty("target", tr.target);
                tradeJson.addProperty("initiatorItem", tr.initiatorItem);
                tradeJson.addProperty("targetItem", tr.targetItem);
                tradeJson.addProperty("initiatorPrice", tr.initiatorPrice);
                tradeJson.addProperty("targetPrice", tr.targetPrice);
                tradeJson.addProperty("accepted", tr.accepted);
                json.add(key, tradeJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save trade data: " + e.getMessage());
        }
    }
}
