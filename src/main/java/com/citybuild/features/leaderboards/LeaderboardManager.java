package com.citybuild.features.leaderboards;

import com.citybuild.features.achievements.AchievementManager;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.plots.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages multiple leaderboards for competitive gameplay
 * Tracks: Richest Players, Top Farmers, Achievement Points, Plot Owners
 */
public class LeaderboardManager {
    
    public enum LeaderboardType {
        RICHEST("💰 Reichste Spieler"),
        FARMERS("🌾 Top Farmer"),
        ACHIEVEMENTS("🏆 Achievement Points"),
        PLOT_OWNERS("🏗️ Plot-Besitzer");
        
        private final String displayName;
        
        LeaderboardType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    private final EconomyManager economyManager;
    private final PlotManager plotManager;
    private final AchievementManager achievementManager;
    
    private final Map<LeaderboardType, Map<UUID, Double>> leaderboards = new HashMap<>();
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 60 * 1000; // 1 minute
    
    public LeaderboardManager(EconomyManager economyManager, PlotManager plotManager, 
                             AchievementManager achievementManager) {
        this.economyManager = economyManager;
        this.plotManager = plotManager;
        this.achievementManager = achievementManager;
        
        // Initialize empty leaderboards
        for (LeaderboardType type : LeaderboardType.values()) {
            leaderboards.put(type, new LinkedHashMap<>());
        }
    }
    
    /**
     * Update all leaderboards (called periodically)
     */
    public void updateLeaderboards() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate < UPDATE_INTERVAL) {
            return; // Update only once per minute
        }
        
        // Get all online players
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        
        // RICHEST LEADERBOARD
        Map<UUID, Double> richest = new LinkedHashMap<>();
        for (Player player : players) {
            double balance = economyManager.getBalance(player);
            richest.put(player.getUniqueId(), balance);
        }
        richest = richest.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
                (e1, e2) -> e1, LinkedHashMap::new));
        leaderboards.put(LeaderboardType.RICHEST, richest);
        
        // ACHIEVEMENT POINTS LEADERBOARD
        Map<UUID, Double> achievements = new LinkedHashMap<>();
        for (Player player : players) {
            int points = achievementManager.getPlayerAchievementPoints(player.getUniqueId());
            achievements.put(player.getUniqueId(), (double) points);
        }
        achievements = achievements.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        leaderboards.put(LeaderboardType.ACHIEVEMENTS, achievements);
        
        // PLOT OWNERS LEADERBOARD
        Map<UUID, Double> plotOwners = new LinkedHashMap<>();
        for (Player player : players) {
            int plotCount = plotManager.getPlayerPlotCount(player.getUniqueId());
            plotOwners.put(player.getUniqueId(), (double) plotCount);
        }
        plotOwners = plotOwners.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        leaderboards.put(LeaderboardType.PLOT_OWNERS, plotOwners);
        
        lastUpdate = now;
    }
    
    /**
     * Get formatted leaderboard as string array for GUI
     */
    public String[] getFormattedLeaderboard(LeaderboardType type) {
        updateLeaderboards();
        Map<UUID, Double> lb = leaderboards.get(type);
        List<String> lines = new ArrayList<>();
        
        lines.add("§6§l" + type.getDisplayName());
        lines.add("§7" + "─".repeat(40));
        
        int rank = 1;
        for (Map.Entry<UUID, Double> entry : lb.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            String playerName = player != null ? player.getName() : "Unknown";
            
            String medal = getMedal(rank);
            String value = formatValue(type, entry.getValue());
            String line = String.format("%s §7#%d §f%s §e%s", 
                medal, rank, playerName, value);
            
            lines.add(line);
            rank++;
        }
        
        if (rank == 1) {
            lines.add("§7Keine Spieler im Leaderboard");
        }
        
        return lines.toArray(new String[0]);
    }
    
    /**
     * Get player rank in leaderboard
     */
    public int getPlayerRank(LeaderboardType type, UUID playerUUID) {
        updateLeaderboards();
        Map<UUID, Double> lb = leaderboards.get(type);
        
        int rank = 1;
        for (UUID uuid : lb.keySet()) {
            if (uuid.equals(playerUUID)) {
                return rank;
            }
            rank++;
        }
        return -1; // Not in top 10
    }
    
    /**
     * Get player's value for specific leaderboard
     */
    public double getPlayerValue(LeaderboardType type, UUID playerUUID) {
        updateLeaderboards();
        Map<UUID, Double> lb = leaderboards.get(type);
        return lb.getOrDefault(playerUUID, 0.0);
    }
    
    /**
     * Get medal emoji for rank
     */
    private String getMedal(int rank) {
        return switch (rank) {
            case 1 -> "§6🥇";
            case 2 -> "§f🥈";
            case 3 -> "§e🥉";
            default -> String.format("§7#%d", rank);
        };
    }
    
    /**
     * Format value based on leaderboard type
     */
    private String formatValue(LeaderboardType type, double value) {
        return switch (type) {
            case RICHEST -> String.format("§6$%.0f", value);
            case ACHIEVEMENTS -> String.format("§b%d Points", (int) value);
            case PLOT_OWNERS -> String.format("§9%d Plots", (int) value);
            case FARMERS -> String.format("§a%d Blocks", (int) value);
        };
    }
    
    /**
     * Broadcast top players to all online players
     */
    public void broadcastLeaderboardUpdate(LeaderboardType type) {
        updateLeaderboards();
        String[] formatted = getFormattedLeaderboard(type);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            for (String line : formatted) {
                player.sendMessage(line);
            }
            player.sendMessage("");
        }
    }
}
