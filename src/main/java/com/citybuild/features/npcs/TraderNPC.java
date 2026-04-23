package com.citybuild.features.npcs;

import java.util.*;

/**
 * Represents an NPC trader with goods and services
 */
public class TraderNPC {

    public enum TradeType {
        BLACKSMITH("⚒️ Blacksmith", "Sells tools and weapons"),
        MERCHANT("🛍️ Merchant", "Buys and sells goods"),
        ALCHEMIST("🧪 Alchemist", "Offers potions and enchantments"),
        BANKER("💰 Banker", "Handles currency exchange");

        private final String display;
        private final String description;

        TradeType(String display, String description) {
            this.display = display;
            this.description = description;
        }

        public String getDisplay() {
            return display;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String npcId;
    private final String npcName;
    private final TradeType tradeType;
    private final Map<String, Double> goods; // item -> price
    private final Map<String, Double> services; // service -> price
    private double totalTraded;
    private int totalTransactions;

    public TraderNPC(String npcId, String npcName, TradeType tradeType) {
        this.npcId = npcId;
        this.npcName = npcName;
        this.tradeType = tradeType;
        this.goods = new HashMap<>();
        this.services = new HashMap<>();
        this.totalTraded = 0;
        this.totalTransactions = 0;
    }

    /**
     * Add good to trade
     */
    public void addGood(String itemName, double price) {
        goods.put(itemName, price);
    }

    /**
     * Add service to trade
     */
    public void addService(String serviceName, double price) {
        services.put(serviceName, price);
    }

    /**
     * Get price of good
     */
    public Double getGoodPrice(String itemName) {
        return goods.get(itemName);
    }

    /**
     * Get price of service
     */
    public Double getServicePrice(String serviceName) {
        return services.get(serviceName);
    }

    /**
     * Record transaction
     */
    public void recordTransaction(double amount) {
        this.totalTraded += amount;
        this.totalTransactions++;
    }

    /**
     * Get formatted info
     */
    public String getFormattedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6").append(npcName).append(" ").append(tradeType.getDisplay()).append("\n");
        sb.append("§7").append(tradeType.getDescription()).append("\n");
        sb.append("§7Goods: §e").append(goods.size()).append("\n");
        sb.append("§7Services: §e").append(services.size()).append("\n");
        sb.append("§7Total Traded: §6$").append(String.format("%.0f", totalTraded)).append("\n");
        sb.append("§7Transactions: §e").append(totalTransactions);
        return sb.toString();
    }

    // Getters
    public String getNpcId() { return npcId; }
    public String getNpcName() { return npcName; }
    public TradeType getTradeType() { return tradeType; }
    public Map<String, Double> getGoods() { return new HashMap<>(goods); }
    public Map<String, Double> getServices() { return new HashMap<>(services); }
    public double getTotalTraded() { return totalTraded; }
    public int getTotalTransactions() { return totalTransactions; }
}
