package com.citybuild.core.services;

import com.citybuild.managers.EconomyManager;
import com.citybuild.utils.ValidationUtils;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Economy Service - Business logic layer
 * Separates business logic from data access
 * Can be used by multiple components
 */
public class EconomyService {
    private final EconomyManager economyManager;
    private final Logger logger;
    
    public EconomyService(EconomyManager economyManager, Logger logger) {
        this.economyManager = economyManager;
        this.logger = logger;
    }
    
    /**
     * Transfer money between two players
     * @param fromPlayer Player sending money
     * @param toPlayer Player receiving money
     * @param amount Amount to transfer
     * @return TransferResult with success/error info
     */
    public TransferResult transfer(Player fromPlayer, Player toPlayer, long amount) {
        // Validation
        if (fromPlayer == null || toPlayer == null) {
            return TransferResult.failure("Invalid players provided");
        }
        
        if (fromPlayer.equals(toPlayer)) {
            return TransferResult.failure("Cannot transfer to yourself");
        }
        
        if (!ValidationUtils.validatePositiveAmount(amount, "EconomyService.transfer")) {
            return TransferResult.failure("Amount must be positive");
        }
        
        // Check balance
        long fromBalance = economyManager.getBalance(fromPlayer);
        if (fromBalance < amount) {
            return TransferResult.failure("Insufficient balance. Have: $" + fromBalance + ", Need: $" + amount);
        }
        
        // Execute transfer
        try {
            economyManager.removeBalance(fromPlayer.getUniqueId().toString(), amount);
            economyManager.addBalance(toPlayer.getUniqueId().toString(), amount);
            
            logger.info("Transfer: " + fromPlayer.getName() + " → " + toPlayer.getName() + " : $" + amount);
            return TransferResult.success("Transferred $" + amount, amount);
        } catch (Exception e) {
            logger.severe("Transfer failed: " + e.getMessage());
            economyManager.addBalance(fromPlayer.getUniqueId().toString(), amount);  // Rollback
            return TransferResult.failure("Transfer failed: " + e.getMessage());
        }
    }
    
    /**
     * Add bonus money to player
     * @param player Player to reward
     * @param amount Amount to add
     * @param reason Reason for bonus
     * @return OperationResult
     */
    public OperationResult addBonus(Player player, long amount, String reason) {
        if (player == null || !ValidationUtils.validatePositiveAmount(amount, "EconomyService.addBonus")) {
            return OperationResult.failure("Invalid parameters");
        }
        
        try {
            economyManager.addBalance(player.getUniqueId().toString(), amount);
            logger.info("Bonus: " + player.getName() + " received $" + amount + " (" + reason + ")");
            return OperationResult.success("Added $" + amount);
        } catch (Exception e) {
            logger.severe("Bonus failed: " + e.getMessage());
            return OperationResult.failure("Bonus failed");
        }
    }
    
    /**
     * Get player balance
     * @param player Player
     * @return Balance or -1 if error
     */
    public long getBalance(Player player) {
        if (player == null) return -1;
        return economyManager.getBalance(player);
    }
    
    /**
     * Check if player can afford an amount
     * @param player Player
     * @param amount Amount needed
     * @return true if player has balance >= amount
     */
    public boolean canAfford(Player player, long amount) {
        if (player == null) return false;
        return economyManager.canAfford(player, amount);
    }
    
    /**
     * Result class for transfer operations
     */
    public static class TransferResult {
        private final boolean success;
        private final String message;
        private final long amount;
        
        private TransferResult(boolean success, String message, long amount) {
            this.success = success;
            this.message = message;
            this.amount = amount;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getAmount() {
            return amount;
        }
        
        public static TransferResult success(String message, long amount) {
            return new TransferResult(true, message, amount);
        }
        
        public static TransferResult failure(String message) {
            return new TransferResult(false, message, 0);
        }
    }
    
    /**
     * Result class for general operations
     */
    public static class OperationResult {
        private final boolean success;
        private final String message;
        
        private OperationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public static OperationResult success(String message) {
            return new OperationResult(true, message);
        }
        
        public static OperationResult failure(String message) {
            return new OperationResult(false, message);
        }
    }
}
