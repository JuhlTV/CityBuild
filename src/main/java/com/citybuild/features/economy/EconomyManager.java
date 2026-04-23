package com.citybuild.features.economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.citybuild.storage.DataManager;
import java.util.*;

/**
 * EconomyManager - Manages player balances with JSON persistence
 */
public class EconomyManager {
    private final Plugin plugin;
    private final DataManager dataManager;
    private final Map<String, Double> playerBalance;
    private final double STARTING_BALANCE = 10000.0;

    public EconomyManager(Plugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.playerBalance = new HashMap<>();
        loadAllData();
    }

    public void addBalance(Player player, double amount) {
        if (player == null) return;
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        
        String uuid = player.getUniqueId().toString();
        double currentBalance = getBalance(player);
        
        // Prevent overflow: cap at max double value / 2 for safety
        double maxBalance = Double.MAX_VALUE / 2;
        if (currentBalance > maxBalance - amount) {
            player.sendMessage("§cError: Balance overflow prevented!");
            return;
        }
        
        double newBalance = currentBalance + amount;
        playerBalance.put(uuid, newBalance);
        player.sendMessage("§a+ $" + String.format("%.2f", amount));
        
        // Save immediately
        dataManager.savePlayerEconomy(uuid, newBalance);
    }

    public boolean removeBalance(Player player, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        if (getBalance(player) >= amount) {
            String uuid = player.getUniqueId().toString();
            double newBalance = getBalance(player) - amount;
            playerBalance.put(uuid, newBalance);
            player.sendMessage("§c- $" + String.format("%.2f", amount));
            
            // Save immediately
            dataManager.savePlayerEconomy(uuid, newBalance);
            return true;
        }
        player.sendMessage("§cInsufficient funds!");
        return false;
    }

    public double getBalance(Player player) {
        String uuid = player.getUniqueId().toString();
        return playerBalance.getOrDefault(uuid, STARTING_BALANCE);
    }

    /**
     * Get balance by UUID (for non-player operations like auctions)
     */
    public double getBalance(UUID playerUUID) {
        if (playerUUID == null) return 0;
        String uuid = playerUUID.toString();
        return playerBalance.getOrDefault(uuid, STARTING_BALANCE);
    }

    /**
     * Add balance by UUID (for non-player operations like auctions)
     */
    public void addBalance(UUID playerUUID, double amount) {
        if (playerUUID == null || amount < 0) return;
        
        String uuid = playerUUID.toString();
        double currentBalance = getBalance(playerUUID);
        
        // Prevent overflow: cap at max double value / 2 for safety
        double maxBalance = Double.MAX_VALUE / 2;
        if (currentBalance > maxBalance - amount) {
            return;
        }
        
        double newBalance = currentBalance + amount;
        playerBalance.put(uuid, newBalance);
        
        // Save immediately
        dataManager.savePlayerEconomy(uuid, newBalance);
    }

    /**
     * Remove balance by UUID (for non-player operations like auctions)
     */
    public boolean removeBalance(UUID playerUUID, double amount) {
        if (playerUUID == null || amount < 0) return false;
        
        String uuid = playerUUID.toString();
        double currentBalance = getBalance(playerUUID);
        
        if (currentBalance >= amount) {
            double newBalance = currentBalance - amount;
            playerBalance.put(uuid, newBalance);
            
            // Save immediately
            dataManager.savePlayerEconomy(uuid, newBalance);
            return true;
        }
        return false;
    }

    public boolean payPlayer(Player sender, Player receiver, double amount) {
        if (amount <= 0) {
            sender.sendMessage("§cAmount must be positive!");
            return false;
        }
        if (removeBalance(sender, amount)) {
            addBalance(receiver, amount);
            String formatted = String.format("%.2f", amount);
            sender.sendMessage("§a✓ Sent $" + formatted + " to " + receiver.getName());
            receiver.sendMessage("§a✓ Received $" + formatted + " from " + sender.getName());
            plugin.getLogger().info(sender.getName() + " sent $" + formatted + " to " + receiver.getName());
            return true;
        }
        return false;
    }

    public void saveAllData() {
        for (Map.Entry<String, Double> entry : playerBalance.entrySet()) {
            dataManager.savePlayerEconomy(entry.getKey(), entry.getValue());
        }
        plugin.getLogger().info("✓ Saved economy data for " + playerBalance.size() + " players");
    }

    public void loadAllData() {
        plugin.getLogger().info("Loading economy data...");
        // Data loaded on-demand when players join
    }

    public double getPlayerBalance(UUID uniqueId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPlayerBalance'");
    }
}
