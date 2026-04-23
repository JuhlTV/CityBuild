package com.citybuild.core.services;

import com.citybuild.managers.AdminManager;
import com.citybuild.utils.ValidationUtils;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Admin Service - Business logic layer for admin operations
 * Handles role management, punishments, and moderation
 */
public class AdminService {
    private final AdminManager adminManager;
    private final Logger logger;
    
    private static final int MAX_WARNING_POINTS = 5;
    private static final int WARNING_RESET_DAYS = 30;
    
    public AdminService(AdminManager adminManager, Logger logger) {
        this.adminManager = adminManager;
        this.logger = logger;
    }
    
    /**
     * Issue a warning to a player
     * @param targetPlayer Player to warn
     * @param adminPlayer Admin issuing warning
     * @param reason Reason for warning
     * @return WarnResult
     */
    public WarnResult warn(Player targetPlayer, Player adminPlayer, String reason) {
        if (targetPlayer == null || adminPlayer == null || reason == null || reason.trim().isEmpty()) {
            return WarnResult.failure("Invalid parameters");
        }
        
        try {
            adminManager.warnPlayer(targetPlayer.getUniqueId(), reason);
            int warnings = adminManager.getWarnings(targetPlayer.getUniqueId());
            
            logger.info("Warning: " + adminPlayer.getName() + " warned " + targetPlayer.getName() + 
                       " (" + warnings + "/" + MAX_WARNING_POINTS + ") - " + reason);
            
            if (warnings >= MAX_WARNING_POINTS) {
                return WarnResult.banThreshold("⚠️ Threshold reached - " + MAX_WARNING_POINTS + 
                                              " warnings = auto-ban", warnings);
            }
            
            return WarnResult.success("Warned (" + warnings + "/" + MAX_WARNING_POINTS + ")", warnings);
        } catch (Exception e) {
            logger.severe("Warn failed: " + e.getMessage());
            return WarnResult.failure("Failed to warn: " + e.getMessage());
        }
    }
    
    /**
     * Mute a player
     * @param targetPlayer Player to mute
     * @param adminPlayer Admin issuing mute
     * @param durationMinutes Duration in minutes
     * @param reason Reason
     * @return OperationResult
     */
    public OperationResult mute(Player targetPlayer, Player adminPlayer, long durationMinutes, String reason) {
        if (targetPlayer == null || adminPlayer == null || durationMinutes <= 0) {
            return OperationResult.failure("Invalid parameters");
        }
        
        try {
            adminManager.mutePlayer(targetPlayer.getUniqueId(), durationMinutes, reason);
            logger.info("Mute: " + adminPlayer.getName() + " muted " + targetPlayer.getName() + 
                       " for " + durationMinutes + " min - " + reason);
            return OperationResult.success(targetPlayer.getName() + " muted for " + durationMinutes + " minutes");
        } catch (Exception e) {
            logger.severe("Mute failed: " + e.getMessage());
            return OperationResult.failure("Failed to mute: " + e.getMessage());
        }
    }
    
    /**
     * Check if player is muted
     * @param player Player to check
     * @return true if currently muted
     */
    public boolean isMuted(Player player) {
        if (player == null) return false;
        return adminManager.isMuted(player.getUniqueId());
    }
    
    /**
     * Get remaining mute time
     * @param player Player
     * @return Remaining minutes, or 0 if not muted
     */
    public long getMuteTimeRemaining(Player player) {
        if (player == null) return 0;
        return adminManager.getMuteTimeRemaining(player.getUniqueId());
    }
    
    /**
     * Set player role
     * @param targetPlayer Player
     * @param role New role
     * @param adminPlayer Admin making change
     * @return OperationResult
     */
    public OperationResult setRole(Player targetPlayer, String role, Player adminPlayer) {
        if (targetPlayer == null || role == null || adminPlayer == null) {
            return OperationResult.failure("Invalid parameters");
        }
        
        // Validate role
        if (!isValidRole(role)) {
            return OperationResult.failure("Invalid role: " + role);
        }
        
        try {
            String previousRole = adminManager.getRole(targetPlayer.getUniqueId());
            adminManager.setRole(targetPlayer.getUniqueId(), role);
            
            logger.info("Role change: " + adminPlayer.getName() + " set " + targetPlayer.getName() + 
                       " role to " + role + " (was: " + previousRole + ")");
            
            return OperationResult.success("Role changed: " + previousRole + " → " + role);
        } catch (Exception e) {
            logger.severe("Role change failed: " + e.getMessage());
            return OperationResult.failure("Failed to change role: " + e.getMessage());
        }
    }
    
    /**
     * Get player role
     * @param player Player
     * @return Role or "GUEST" if no role
     */
    public String getRole(Player player) {
        if (player == null) return "GUEST";
        return adminManager.getRole(player.getUniqueId());
    }
    
    /**
     * Check if player has role
     * @param player Player
     * @param role Role to check
     * @return true if player has at least this role
     */
    public boolean hasRole(Player player, String role) {
        if (player == null) return false;
        return adminManager.hasRole(player.getUniqueId(), role);
    }
    
    /**
     * Check if player has permission
     * @param player Player
     * @return true if player has admin permission
     */
    public boolean hasPermission(Player player) {
        if (player == null) return false;
        return player.isOp() || adminManager.hasPermission(player.getUniqueId());
    }
    
    /**
     * Validate role name
     */
    private boolean isValidRole(String role) {
        return role != null && (
            role.equalsIgnoreCase("OWNER") ||
            role.equalsIgnoreCase("ADMIN") ||
            role.equalsIgnoreCase("MODERATOR") ||
            role.equalsIgnoreCase("MEMBER") ||
            role.equalsIgnoreCase("GUEST")
        );
    }
    
    /**
     * Result class for warning operations
     */
    public static class WarnResult {
        private final boolean success;
        private final String message;
        private final int warnings;
        private final boolean banThreshold;
        
        private WarnResult(boolean success, String message, int warnings, boolean banThreshold) {
            this.success = success;
            this.message = message;
            this.warnings = warnings;
            this.banThreshold = banThreshold;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getWarnings() {
            return warnings;
        }
        
        public boolean isBanThreshold() {
            return banThreshold;
        }
        
        public static WarnResult success(String message, int warnings) {
            return new WarnResult(true, message, warnings, false);
        }
        
        public static WarnResult banThreshold(String message, int warnings) {
            return new WarnResult(true, message, warnings, true);
        }
        
        public static WarnResult failure(String message) {
            return new WarnResult(false, message, 0, false);
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
