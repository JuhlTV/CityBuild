package com.citybuild.features.npcs;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Manages all NPC traders in the game
 */
public class NPCManager {

    private final JavaPlugin plugin;
    private final Map<String, TraderNPC> npcs = new HashMap<>();
    private int npcCounter = 0;

    public NPCManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeDefaultNPCs();
    }

    /**
     * Initialize default NPCs
     */
    private void initializeDefaultNPCs() {
        // Blacksmith
        createNPC("Torgan", TraderNPC.TradeType.BLACKSMITH);
        TraderNPC blacksmith = npcs.values().stream()
            .filter(n -> n.getNpcName().equals("Torgan"))
            .findFirst()
            .orElse(null);
        if (blacksmith != null) {
            blacksmith.addGood("Iron Sword", 5000);
            blacksmith.addGood("Diamond Pickaxe", 15000);
            blacksmith.addGood("Golden Armor", 8000);
            blacksmith.addService("Repair", 1000);
            blacksmith.addService("Enchant", 2500);
        }

        // Merchant
        createNPC("Barius", TraderNPC.TradeType.MERCHANT);
        TraderNPC merchant = npcs.values().stream()
            .filter(n -> n.getNpcName().equals("Barius"))
            .findFirst()
            .orElse(null);
        if (merchant != null) {
            merchant.addGood("Apple", 50);
            merchant.addGood("Bread", 100);
            merchant.addGood("Emerald", 2000);
            merchant.addService("Appraisal", 500);
        }

        // Alchemist
        createNPC("Mystara", TraderNPC.TradeType.ALCHEMIST);
        TraderNPC alchemist = npcs.values().stream()
            .filter(n -> n.getNpcName().equals("Mystara"))
            .findFirst()
            .orElse(null);
        if (alchemist != null) {
            alchemist.addGood("Healing Potion", 800);
            alchemist.addGood("Strength Potion", 1200);
            alchemist.addGood("Invisibility Potion", 2000);
            alchemist.addService("Custom Brew", 3000);
        }

        // Banker
        createNPC("Goldwick", TraderNPC.TradeType.BANKER);
        TraderNPC banker = npcs.values().stream()
            .filter(n -> n.getNpcName().equals("Goldwick"))
            .findFirst()
            .orElse(null);
        if (banker != null) {
            banker.addService("Deposit", 0);
            banker.addService("Withdraw", 0);
            banker.addService("Loan", 5000);
        }

        plugin.getLogger().info("§6✓ Initialized 4 default NPCs");
    }

    /**
     * Create a new NPC
     */
    public String createNPC(String npcName, TraderNPC.TradeType type) {
        String npcId = "NPC_" + (npcCounter++);
        TraderNPC npc = new TraderNPC(npcId, npcName, type);
        npcs.put(npcId, npc);

        plugin.getLogger().info("§6NPC created: " + npcName + " (" + type.getDisplay() + ")");

        return npcId;
    }

    /**
     * Get NPC by ID
     */
    public TraderNPC getNPC(String npcId) {
        return npcs.get(npcId);
    }

    /**
     * Find NPC by name
     */
    public TraderNPC getNPCByName(String name) {
        return npcs.values().stream()
            .filter(n -> n.getNpcName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get all NPCs
     */
    public Collection<TraderNPC> getAllNPCs() {
        return new ArrayList<>(npcs.values());
    }

    /**
     * Get NPCs by type
     */
    public Collection<TraderNPC> getNPCsByType(TraderNPC.TradeType type) {
        return npcs.values().stream()
            .filter(n -> n.getTradeType() == type)
            .toList();
    }

    /**
     * Trade with NPC
     */
    public boolean tradeWithNPC(String npcId, String item, double amount) {
        TraderNPC npc = npcs.get(npcId);
        if (npc == null) return false;

        Double goodPrice = npc.getGoodPrice(item);
        Double servicePrice = npc.getServicePrice(item);

        if (goodPrice != null || servicePrice != null) {
            double price = goodPrice != null ? goodPrice : servicePrice;
            npc.recordTransaction(price);
            return true;
        }

        return false;
    }

    /**
     * Get NPC statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_npcs", npcs.size());
        stats.put("total_goods", npcs.values().stream()
            .mapToInt(n -> n.getGoods().size())
            .sum());
        stats.put("total_services", npcs.values().stream()
            .mapToInt(n -> n.getServices().size())
            .sum());
        stats.put("total_traded", npcs.values().stream()
            .mapToDouble(TraderNPC::getTotalTraded)
            .sum());
        return stats;
    }
}
