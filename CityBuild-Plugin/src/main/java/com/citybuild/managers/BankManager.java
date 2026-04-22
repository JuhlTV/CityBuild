package com.citybuild.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.*;

public class BankManager {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    
    // Transaction history: playerUUID -> List of transactions
    private final Map<String, List<Transaction>> transactionHistory;
    
    public static class Transaction {
        public String from;
        public String to;
        public long amount;
        public LocalDateTime timestamp;
        public String reason;
        
        public Transaction(String from, String to, long amount, String reason) {
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.reason = reason;
            this.timestamp = LocalDateTime.now();
        }
    }
    
    public BankManager(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.transactionHistory = new HashMap<>();
    }
    
    /**
     * Transfer money from one player to another
     */
    public boolean transferMoney(Player from, Player to, long amount) {
        if (amount <= 0) {
            return false;
        }
        
        if (!economyManager.canAfford(from, amount)) {
            return false;
        }
        
        economyManager.removeBalance(from, amount);
        economyManager.addBalance(to, amount);
        
        // Record transaction
        recordTransaction(from.getUniqueId().toString(), to.getUniqueId().toString(), amount, "Player Transfer");
        
        return true;
    }
    
    /**
     * Pay a player (with reason)
     */
    public boolean payPlayer(Player from, Player to, long amount, String reason) {
        if (amount <= 0) {
            return false;
        }
        
        if (!economyManager.canAfford(from, amount)) {
            return false;
        }
        
        economyManager.removeBalance(from, amount);
        economyManager.addBalance(to, amount);
        
        recordTransaction(from.getUniqueId().toString(), to.getUniqueId().toString(), amount, reason);
        
        return true;
    }
    
    /**
     * Record a transaction
     */
    private void recordTransaction(String from, String to, long amount, String reason) {
        Transaction transaction = new Transaction(from, to, amount, reason);
        
        transactionHistory.computeIfAbsent(from, k -> new ArrayList<>()).add(transaction);
        transactionHistory.computeIfAbsent(to, k -> new ArrayList<>()).add(transaction);
    }
    
    /**
     * Get transaction history for a player
     */
    public List<Transaction> getTransactionHistory(String playerUuid, int limit) {
        List<Transaction> history = transactionHistory.getOrDefault(playerUuid, new ArrayList<>());
        
        // Return last 'limit' transactions
        int startIndex = Math.max(0, history.size() - limit);
        return new ArrayList<>(history.subList(startIndex, history.size()));
    }
    
    /**
     * Get total sent amount
     */
    public long getTotalSent(String playerUuid) {
        return transactionHistory.getOrDefault(playerUuid, new ArrayList<>())
            .stream()
            .filter(t -> t.from.equals(playerUuid))
            .mapToLong(t -> t.amount)
            .sum();
    }
    
    /**
     * Get total received amount
     */
    public long getTotalReceived(String playerUuid) {
        return transactionHistory.getOrDefault(playerUuid, new ArrayList<>())
            .stream()
            .filter(t -> t.to.equals(playerUuid))
            .mapToLong(t -> t.amount)
            .sum();
    }
}
