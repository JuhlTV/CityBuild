package com.citybuild.features.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.achievements.AchievementManager;

import java.util.*;

/**
 * Manages all quests in the game
 */
public class QuestManager {

    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    private final AchievementManager achievementManager;
    private final Map<String, Quest> allQuests = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> playerQuestProgress = new HashMap<>();
    private final Map<UUID, Set<String>> playerCompletedQuests = new HashMap<>();
    private BukkitTask questResetTask;
    private BukkitTask questExpiryTask;

    public QuestManager(JavaPlugin plugin, EconomyManager economyManager, AchievementManager achievementManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.achievementManager = achievementManager;
        initializeQuests();
        startQuestTasks();
    }

    /**
     * Initialize all available quests
     */
    private void initializeQuests() {
        // DAILY QUESTS
        addQuest(new Quest(
            "daily_mine", "Daily Miner", "Mine 500 blocks",
            Quest.QuestType.DAILY, 500, 1000, 50
        ));

        addQuest(new Quest(
            "daily_earn", "Daily Earner", "Earn $5000",
            Quest.QuestType.DAILY, 5000, 2500, 75
        ));

        addQuest(new Quest(
            "daily_farm", "Daily Farmer", "Collect 300 crops",
            Quest.QuestType.DAILY, 300, 1500, 60
        ));

        // WEEKLY QUESTS
        addQuest(new Quest(
            "weekly_trader", "Weekly Trader", "Complete 5 trades",
            Quest.QuestType.WEEKLY, 5, 5000, 150
        ));

        addQuest(new Quest(
            "weekly_builder", "Weekly Builder", "Build on 10 different plots",
            Quest.QuestType.WEEKLY, 10, 7500, 200
        ));

        addQuest(new Quest(
            "weekly_achiever", "Weekly Achiever", "Unlock 3 achievements",
            Quest.QuestType.WEEKLY, 3, 10000, 250
        ));

        // PERMANENT QUESTS
        addQuest(new Quest(
            "permanent_richest", "The Richest", "Acquire $1000000",
            Quest.QuestType.PERMANENT, 1000000, 50000, 500
        ));

        addQuest(new Quest(
            "permanent_builder", "Grand Architect", "Own 50 plots",
            Quest.QuestType.PERMANENT, 50, 75000, 750
        ));

        addQuest(new Quest(
            "permanent_legend", "Legendary Player", "Unlock 15 achievements",
            Quest.QuestType.PERMANENT, 15, 100000, 1000
        ));

        plugin.getLogger().info("✓ " + allQuests.size() + " Quests initialized");
    }

    /**
     * Add a quest to the system
     */
    public void addQuest(Quest quest) {
        allQuests.put(quest.getQuestId(), quest);
    }

    /**
     * Get all available quests
     */
    public Collection<Quest> getAllQuests() {
        return allQuests.values();
    }

    /**
     * Get quest by ID
     */
    public Quest getQuest(String questId) {
        return allQuests.get(questId);
    }

    /**
     * Update player quest progress
     */
    public void updateProgress(UUID playerUUID, String questId, int amount) {
        if (!allQuests.containsKey(questId)) return;

        Quest quest = allQuests.get(questId);
        if (quest.isExpired() && quest.getType() != Quest.QuestType.PERMANENT) {
            return; // Quest expired
        }

        Map<String, Integer> progress = playerQuestProgress.computeIfAbsent(playerUUID, k -> new HashMap<>());
        int currentProgress = progress.getOrDefault(questId, 0);
        int newProgress = Math.min(currentProgress + amount, quest.getProgressRequired());
        progress.put(questId, newProgress);

        // Check if quest completed
        if (newProgress >= quest.getProgressRequired() && !isQuestCompleted(playerUUID, questId)) {
            completeQuest(playerUUID, questId);
        }
    }

    /**
     * Complete a quest and give rewards
     */
    private void completeQuest(UUID playerUUID, String questId) {
        if (isQuestCompleted(playerUUID, questId)) return;

        Quest quest = allQuests.get(questId);
        Player player = Bukkit.getPlayer(playerUUID);

        // Mark as completed
        playerCompletedQuests.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(questId);

        // Give rewards
        economyManager.addBalance(playerUUID, quest.getCoinReward());
        achievementManager.updateProgress(playerUUID, "weekly_achiever", 1);

        if (player != null && player.isOnline()) {
            player.sendMessage("");
            player.sendMessage("§a§l╔════════════════════════════════════╗");
            player.sendMessage("§a§l║§6 QUEST COMPLETED!§a§l");
            player.sendMessage("§a§l╠════════════════════════════════════╣");
            player.sendMessage("§e" + quest.getName());
            player.sendMessage("§7Reward: §6$" + String.format("%.0f", quest.getCoinReward()));
            player.sendMessage("§7+ §e" + quest.getAchievementPointReward() + " achievement points");
            player.sendMessage("§a§l╚════════════════════════════════════╝");
            player.sendMessage("");
        }
    }

    /**
     * Check if player completed quest
     */
    public boolean isQuestCompleted(UUID playerUUID, String questId) {
        return playerCompletedQuests.getOrDefault(playerUUID, new HashSet<>()).contains(questId);
    }

    /**
     * Get player's progress on a quest
     */
    public int getQuestProgress(UUID playerUUID, String questId) {
        return playerQuestProgress.getOrDefault(playerUUID, new HashMap<>()).getOrDefault(questId, 0);
    }

    /**
     * Get player's active quests
     */
    public List<Quest> getPlayerActiveQuests(UUID playerUUID) {
        List<Quest> active = new ArrayList<>();
        for (Quest quest : allQuests.values()) {
            if (!isQuestCompleted(playerUUID, quest.getQuestId()) && !quest.isExpired()) {
                active.add(quest);
            }
        }
        return active;
    }

    /**
     * Get player's completed quests
     */
    public List<Quest> getPlayerCompletedQuests(UUID playerUUID) {
        List<Quest> completed = new ArrayList<>();
        for (String questId : playerCompletedQuests.getOrDefault(playerUUID, new HashSet<>())) {
            Quest quest = allQuests.get(questId);
            if (quest != null) {
                completed.add(quest);
            }
        }
        return completed;
    }

    /**
     * Start quest management tasks
     */
    private void startQuestTasks() {
        // Reset daily quests every 24 hours
        questResetTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            playerCompletedQuests.forEach((uuid, completedQuests) -> {
                completedQuests.removeIf(questId -> {
                    Quest quest = allQuests.get(questId);
                    return quest != null && quest.getType() == Quest.QuestType.DAILY && quest.isExpired();
                });
            });
            plugin.getLogger().info("✓ Daily quests reset");
        }, 86400 * 20, 86400 * 20); // Every 24 hours

        // Weekly quest reset
        questExpiryTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            playerCompletedQuests.forEach((uuid, completedQuests) -> {
                completedQuests.removeIf(questId -> {
                    Quest quest = allQuests.get(questId);
                    return quest != null && quest.getType() == Quest.QuestType.WEEKLY && quest.isExpired();
                });
            });
            plugin.getLogger().info("✓ Weekly quests reset");
        }, 604800 * 20, 604800 * 20); // Every 7 days
    }

    /**
     * Stop all tasks
     */
    public void stop() {
        if (questResetTask != null) questResetTask.cancel();
        if (questExpiryTask != null) questExpiryTask.cancel();
    }

    /**
     * Get quest statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_quests", allQuests.size());
        stats.put("daily_quests", allQuests.values().stream()
            .filter(q -> q.getType() == Quest.QuestType.DAILY).count());
        stats.put("weekly_quests", allQuests.values().stream()
            .filter(q -> q.getType() == Quest.QuestType.WEEKLY).count());
        stats.put("permanent_quests", allQuests.values().stream()
            .filter(q -> q.getType() == Quest.QuestType.PERMANENT).count());
        return stats;
    }
}
