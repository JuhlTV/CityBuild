package com.citybuild.features.achievements;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Manages all achievements and player progress
 * Tracks achievement unlocks, progress, and rewards
 * NOTE: Achievements are stored in-memory only (no persistence yet)
 */
public class AchievementManager {
    
    private final Plugin plugin;
    private final Map<String, Achievement> allAchievements = new HashMap<>();
    private final Map<UUID, Map<String, Achievement>> playerAchievements = new HashMap<>();
    
    public AchievementManager(Plugin plugin) {
        this.plugin = plugin;
        initializeAchievements();
    }
    
    /**
     * Initialize all available achievements in the game
     */
    private void initializeAchievements() {
        // MINING ACHIEVEMENTS
        addAchievement(new Achievement(
            "first_block", "Erstes Erz", "Grabe dein erstes Erz ab",
            Achievement.Category.MINING, Achievement.Rarity.COMMON, 100, 1
        ));
        
        addAchievement(new Achievement(
            "stone_collector", "Steinsammler", "Grabe 1000 Steine ab",
            Achievement.Category.MINING, Achievement.Rarity.UNCOMMON, 500, 1000
        ));
        
        addAchievement(new Achievement(
            "ore_master", "Erz-Meister", "Grabe 5000 Erze ab",
            Achievement.Category.MINING, Achievement.Rarity.RARE, 2000, 5000
        ));
        
        addAchievement(new Achievement(
            "deep_miner", "Tiefenbauer", "Grabe 50000 Blöcke ab",
            Achievement.Category.MINING, Achievement.Rarity.EPIC, 5000, 50000
        ));
        
        // ECONOMY ACHIEVEMENTS
        addAchievement(new Achievement(
            "first_coins", "Erstes Geld", "Verdiene 1000 Münzen",
            Achievement.Category.ECONOMY, Achievement.Rarity.COMMON, 200, 1000
        ));
        
        addAchievement(new Achievement(
            "rich", "Reich", "Besitze 50000 Münzen",
            Achievement.Category.ECONOMY, Achievement.Rarity.UNCOMMON, 1000, 50000
        ));
        
        addAchievement(new Achievement(
            "millionaire", "Millionär", "Besitze 1000000 Münzen",
            Achievement.Category.ECONOMY, Achievement.Rarity.EPIC, 10000, 1000000
        ));
        
        addAchievement(new Achievement(
            "billionaire", "Milliardär", "Besitze 1000000000 Münzen",
            Achievement.Category.ECONOMY, Achievement.Rarity.LEGENDARY, 50000, 1000000000
        ));
        
        // BUILDING ACHIEVEMENTS
        addAchievement(new Achievement(
            "first_plot", "Plot-Besitzer", "Kaufe deinen ersten Plot",
            Achievement.Category.BUILDING, Achievement.Rarity.COMMON, 300, 1
        ));
        
        addAchievement(new Achievement(
            "plot_master", "Plot-Meister", "Besitze 10 Plots",
            Achievement.Category.BUILDING, Achievement.Rarity.UNCOMMON, 2000, 10
        ));
        
        addAchievement(new Achievement(
            "plot_kingdom", "Plot-König", "Besitze 50 Plots",
            Achievement.Category.BUILDING, Achievement.Rarity.RARE, 5000, 50
        ));
        
        addAchievement(new Achievement(
            "plot_emperor", "Plot-Kaiser", "Besitze 100 Plots",
            Achievement.Category.BUILDING, Achievement.Rarity.LEGENDARY, 25000, 100
        ));
        
        // SPECIAL ACHIEVEMENTS
        addAchievement(new Achievement(
            "first_day", "Willkommen!", "Spiele deinen ersten Tag",
            Achievement.Category.SPECIAL, Achievement.Rarity.COMMON, 50, 1
        ));
        
        addAchievement(new Achievement(
            "week_warrior", "Wochen-Krieger", "Spiele 7 Tage insgesamt",
            Achievement.Category.SPECIAL, Achievement.Rarity.UNCOMMON, 1000, 7
        ));
        
        addAchievement(new Achievement(
            "month_master", "Monats-Meister", "Spiele 30 Tage insgesamt",
            Achievement.Category.SPECIAL, Achievement.Rarity.RARE, 5000, 30
        ));
        
        plugin.getLogger().info("✓ " + allAchievements.size() + " Achievements initialized");
    }
    
    /**
     * Register an achievement
     */
    public void addAchievement(Achievement achievement) {
        allAchievements.put(achievement.getId(), achievement);
    }
    
    /**
     * Get all available achievements
     */
    public Map<String, Achievement> getAllAchievements() {
        return new HashMap<>(allAchievements);
    }
    
    /**
     * Get player's achievement data
     */
    public Map<String, Achievement> getPlayerAchievements(UUID playerUUID) {
        return playerAchievements.computeIfAbsent(playerUUID, k -> {
            Map<String, Achievement> playerAchs = new HashMap<>();
            for (Achievement ach : allAchievements.values()) {
                // Deep copy for each player
                Achievement playerAch = new Achievement(
                    ach.getId(), ach.getName(), ach.getDescription(),
                    ach.getCategory(), ach.getRarity(), ach.getRewardCoins(),
                    ach.getProgressRequired()
                );
                playerAch.setHidden(ach.isHidden());
                playerAchs.put(ach.getId(), playerAch);
            }
            return playerAchs;
        });
    }
    
    /**
     * Update player achievement progress
     */
    public void updateProgress(UUID playerUUID, String achievementId, int progressAmount) {
        Map<String, Achievement> playerAchs = getPlayerAchievements(playerUUID);
        Achievement ach = playerAchs.get(achievementId);
        
        if (ach == null || ach.isUnlocked()) return;
        
        int oldProgress = ach.getCurrentProgress();
        ach.addProgress(progressAmount);
        
        // Check if just unlocked
        if (ach.isCompleted() && oldProgress < ach.getProgressRequired()) {
            unlockAchievement(playerUUID, achievementId);
        }
    }
    
    /**
     * Unlock achievement for player
     */
    public void unlockAchievement(UUID playerUUID, String achievementId) {
        Map<String, Achievement> playerAchs = getPlayerAchievements(playerUUID);
        Achievement ach = playerAchs.get(achievementId);
        
        if (ach == null || ach.isUnlocked()) return;
        
        ach.setUnlocked(true);
        
        // Notify player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.sendMessage("");
            player.sendMessage("§6§l╔═══════════════════════════════════╗");
            player.sendMessage("§6§l║ 🏆 §6Achievement Unlocked!§6§l");
            player.sendMessage("§6§l╠═══════════════════════════════════╣");
            player.sendMessage("§e" + ach.getName() + " §7- " + ach.getDescription());
            player.sendMessage("§7+" + ach.getRarity().getPoints() + " Points");
            player.sendMessage("§6§l╚═══════════════════════════════════╝");
            player.sendMessage("");
        }
    }
    
    /**
     * Get player's total achievement points
     */
    public int getPlayerAchievementPoints(UUID playerUUID) {
        Map<String, Achievement> playerAchs = getPlayerAchievements(playerUUID);
        return playerAchs.values().stream()
            .filter(Achievement::isUnlocked)
            .mapToInt(ach -> ach.getRarity().getPoints())
            .sum();
    }
    
    /**
     * Get number of unlocked achievements
     */
    public int getUnlockedCount(UUID playerUUID) {
        Map<String, Achievement> playerAchs = getPlayerAchievements(playerUUID);
        return (int) playerAchs.values().stream()
            .filter(Achievement::isUnlocked)
            .count();
    }
    
    /**
     * Get percentage of achievements completed
     */
    public double getCompletionPercentage(UUID playerUUID) {
        Map<String, Achievement> playerAchs = getPlayerAchievements(playerUUID);
        int total = playerAchs.size();
        int unlocked = getUnlockedCount(playerUUID);
        return total > 0 ? (double) unlocked / total * 100 : 0;
    }
    
    /**
     * Save single player data
     */
    public void savePlayerData(UUID playerUUID) {
        // Achievements are currently in-memory only
    }
    
    /**
     * Save all player data on server shutdown
     */
    public void saveAllData() {
        // Achievements are currently in-memory only
        plugin.getLogger().info("✓ Achievements saved to memory");
    }
}
