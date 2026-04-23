package com.citybuild.features.farming;

import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import com.citybuild.CityBuildPlugin;

/**
 * AchievementManager - Manages achievements, badges, and leaderboards
 */
public class AchievementManager {
    private final CityBuildPlugin plugin;
    private final Map<String, Set<String>> playerAchievements; // UUID -> Achievement IDs
    private final Map<String, PlayerFarmData> playerStats; // UUID -> Stats
    
    public AchievementManager(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.playerAchievements = new HashMap<>();
        this.playerStats = new HashMap<>();
    }
    
    /**
     * Checks and awards achievements to a player
     */
    public void checkAchievements(String playerUUID, PlayerFarmData data) {
        Set<String> achievements = playerAchievements.computeIfAbsent(playerUUID, k -> new HashSet<>());
        
        // 🏆 1000 Blocks Achievement
        if (data.getBlocksMinedTotal() >= 1000 && achievements.add("blocks_1000")) {
            awardAchievement(playerUUID, "blocks_1000", "§6🏆 Block Master", "Mined 1000 blocks!", 100);
        }
        
        // 💰 $10000 Achievement
        if (data.getCoinsEarnedTotal() >= 10000 && achievements.add("coins_10000")) {
            awardAchievement(playerUUID, "coins_10000", "§6💰 Rich Farmer", "Earned $10,000!", 150);
        }
        
        // 🔥 Streak 50 Achievement
        if (data.getCurrentStreak() >= 50 && achievements.add("streak_50")) {
            awardAchievement(playerUUID, "streak_50", "§6🔥 On Fire!", "50 block streak!", 75);
        }
        
        // 📈 Level 10 Achievement
        if (data.getFarmerLevel() >= 10 && achievements.add("level_10")) {
            awardAchievement(playerUUID, "level_10", "§6📈 Experienced Farmer", "Reached Level 10!", 120);
        }
        
        // 🎨 Combo 5 Achievement
        if (data.getComboCount() >= 5 && achievements.add("combo_5")) {
            awardAchievement(playerUUID, "combo_5", "§6🎨 Combo Master", "Mined 5 different blocks in 5s!", 85);
        }
        
        // 🌙 Night Owl Achievement
        if (isNightTime() && data.getBlocksMinedTotal() % 100 == 0 && achievements.add("night_owl")) {
            awardAchievement(playerUUID, "night_owl", "§6🌙 Night Owl", "Mine 100 blocks at night!", 110);
        }
    }
    
    /**
     * Awards an achievement to a player
     */
    private void awardAchievement(String playerUUID, String id, String title, String description, long points) {
        PlayerFarmData data = playerStats.get(playerUUID);
        if (data != null) {
            data.addAchievementPoints(points);
            plugin.getLogger().info("✓ Achievement awarded to " + playerUUID + ": " + title);
        }
    }
    
    /**
     * Gets the top farmers (leaderboard)
     */
    public List<String> getTopFarmers(int limit) {
        return playerStats.values().stream()
            .sorted(Comparator.comparingDouble(PlayerFarmData::getCoinsEarnedTotal).reversed())
            .limit(limit)
            .map(data -> {
                String playerName = Bukkit.getOfflinePlayer(UUID.fromString(data.getPlayerUUID())).getName();
                return String.format("§a%s §7- Level %d, $%.2f",
                    playerName != null ? playerName : "Unknown",
                    data.getFarmerLevel(),
                    data.getCoinsEarnedTotal()
                );
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Gets top block miners
     */
    public List<String> getTopMiners(int limit) {
        return playerStats.values().stream()
            .sorted(Comparator.comparingLong(PlayerFarmData::getBlocksMinedTotal).reversed())
            .limit(limit)
            .map(data -> {
                String playerName = Bukkit.getOfflinePlayer(UUID.fromString(data.getPlayerUUID())).getName();
                return String.format("§a%s §7- %d blocks",
                    playerName != null ? playerName : "Unknown",
                    data.getBlocksMinedTotal()
                );
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Gets current leaderboard formatted for chat
     */
    public String getFormattedLeaderboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6§l=== 🏆 Top Farmers Leaderboard ===" +
            "\n");
        
        int rank = 1;
        for (String entry : getTopFarmers(5)) {
            sb.append("§e#").append(rank).append(" ").append(entry).append("\n");
            rank++;
        }
        
        return sb.toString();
    }
    
    /**
     * Registers player stats
     */
    public void registerPlayerStats(String playerUUID, PlayerFarmData data) {
        playerStats.put(playerUUID, data);
    }
    
    /**
     * Gets player stats
     */
    public PlayerFarmData getPlayerStats(String playerUUID) {
        return playerStats.get(playerUUID);
    }
    
    /**
     * Checks if it's night time
     */
    private boolean isNightTime() {
        long time = Bukkit.getWorlds().get(0).getTime();
        return time > 13000 || time < 23000; // Night is roughly 13000-23000 ticks
    }
    
    /**
     * Gets player achievement count
     */
    public int getAchievementCount(String playerUUID) {
        return playerAchievements.getOrDefault(playerUUID, new HashSet<>()).size();
    }
}
