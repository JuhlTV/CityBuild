package com.citybuild.core.services;

import com.citybuild.managers.*;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.logging.Logger;

/**
 * Player Service - Business logic for player-related operations
 * Aggregates player information from multiple managers
 */
public class PlayerService {
    private final EconomyManager economyManager;
    private final PlotManager plotManager;
    private final AdminManager adminManager;
    private final PlaytimeManager playtimeManager;
    private final AchievementManager achievementManager;
    private final Logger logger;
    
    public PlayerService(EconomyManager economyManager, PlotManager plotManager, 
                         AdminManager adminManager, PlaytimeManager playtimeManager,
                         AchievementManager achievementManager, Logger logger) {
        this.economyManager = economyManager;
        this.plotManager = plotManager;
        this.adminManager = adminManager;
        this.playtimeManager = playtimeManager;
        this.achievementManager = achievementManager;
        this.logger = logger;
    }
    
    /**
     * Get comprehensive player profile
     * @param player Player
     * @return PlayerProfile with all player info
     */
    public PlayerProfile getProfile(Player player) {
        if (player == null) return null;
        
        UUID uuid = player.getUniqueId();
        String uuidStr = uuid.toString();
        return new PlayerProfile(
            uuid,
            player.getName(),
            economyManager.getBalance(player),
            adminManager.getRole(uuidStr),
            playtimeManager.getPlaytime(uuidStr),
            plotManager.getPlotsOwned(uuidStr).size(),
            adminManager.getWarnings(uuidStr),
            achievementManager.getUnlockedCount(uuidStr)
        );
    }
    
    /**
     * Get top players by balance
     * @param limit Number of players to return
     * @return List of PlayerProfile sorted by balance
     */
    public List<PlayerProfile> getTopPlayers(int limit) {
        try {
            List<PlayerProfile> profiles = new ArrayList<>();
            // This would need access to all player data
            // For now, returns empty - will be enhanced in Phase 2
            return profiles.stream().limit(Math.max(1, limit)).toList();
        } catch (Exception e) {
            logger.warning("Failed to get top players: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Check player online status and connection
     * @param player Player
     * @return true if player is valid and connected
     */
    public boolean isPlayerValid(Player player) {
        return player != null && player.isOnline();
    }
    
    /**
     * Get player display name with role indicator
     * @param player Player
     * @return Formatted name with role
     */
    public String getDisplayName(Player player) {
        if (player == null) return "Unknown";
        
        String role = adminManager.getRole(player.getUniqueId().toString());
        String rolePrefix = switch(role) {
            case "OWNER" -> "👑 ";
            case "ADMIN" -> "⚙️ ";
            case "MODERATOR" -> "👮 ";
            case "MEMBER" -> "✅ ";
            default -> "";
        };
        
        return rolePrefix + player.getName();
    }
    
    /**
     * Player profile data class
     */
    public static class PlayerProfile {
        private final UUID uuid;
        private final String name;
        private final long balance;
        private final String role;
        private final long playtimeMinutes;
        private final int plotsOwned;
        private final int warnings;
        private final int achievementsUnlocked;
        
        public PlayerProfile(UUID uuid, String name, long balance, String role, 
                           long playtimeMinutes, int plotsOwned, int warnings, 
                           int achievementsUnlocked) {
            this.uuid = uuid;
            this.name = name;
            this.balance = balance;
            this.role = role;
            this.playtimeMinutes = playtimeMinutes;
            this.plotsOwned = plotsOwned;
            this.warnings = warnings;
            this.achievementsUnlocked = achievementsUnlocked;
        }
        
        public UUID getUuid() { return uuid; }
        public String getName() { return name; }
        public long getBalance() { return balance; }
        public String getRole() { return role; }
        public long getPlaytimeMinutes() { return playtimeMinutes; }
        public int getPlotsOwned() { return plotsOwned; }
        public int getWarnings() { return warnings; }
        public int getAchievementsUnlocked() { return achievementsUnlocked; }
        
        /**
         * Get playtime in human-readable format
         */
        public String getPlaytimeFormatted() {
            long hours = playtimeMinutes / 60;
            long minutes = playtimeMinutes % 60;
            if (hours > 0) {
                return hours + "h " + minutes + "m";
            }
            return minutes + "m";
        }
        
        /**
         * Get summary string
         */
        @Override
        public String toString() {
            return String.format("%s | Balance: $%d | Role: %s | Playtime: %s | Plots: %d",
                name, balance, role, getPlaytimeFormatted(), plotsOwned);
        }
    }
}
