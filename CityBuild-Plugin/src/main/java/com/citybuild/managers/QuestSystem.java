package com.citybuild.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages quests, missions, and daily challenges
 * Players can complete quests to earn money, items, and achievements
 */
public class QuestSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;

    // QuestID -> Quest
    private final Map<Integer, Quest> quests = new ConcurrentHashMap<>();
    // UUID -> List<QuestProgress>
    private final Map<String, List<QuestProgress>> playerProgress = new ConcurrentHashMap<>();

    private int nextQuestId = 1;
    private long totalQuestRewardsIssued = 0;

    public QuestSystem(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    // ===== QUEST CREATION =====

    /**
     * Create a new quest
     */
    public Quest createQuest(String questName, String objective, long reward, long durationDays,
                            String questType, int targetCount) {
        Quest quest = new Quest(
            nextQuestId++,
            questName,
            objective,
            reward,
            System.currentTimeMillis(),
            durationDays,
            questType,
            targetCount
        );

        quests.put(quest.questId, quest);

        plugin.getLogger().info("✓ Quest created: \"" + questName + "\" (" + questType + 
            ") - Reward: $" + reward);
        return quest;
    }

    /**
     * Register player for quest
     */
    public QuestProgress acceptQuest(String playerUuid, int questId) {
        Quest quest = quests.get(questId);
        if (quest == null || quest.isExpired()) {
            return null;
        }

        // Check if already accepted
        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests != null && playerQuests.stream()
            .anyMatch(qp -> qp.questId == questId && !qp.isCompleted())) {
            return null; // Already accepted
        }

        QuestProgress progress = new QuestProgress(questId, playerUuid, System.currentTimeMillis());
        playerProgress.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(progress);

        plugin.getLogger().fine("✓ Quest accepted: " + playerUuid.substring(0, 8) + 
            " -> " + quest.questName);
        return progress;
    }

    /**
     * Update quest progress (called when player completes actions)
     */
    public void incrementProgress(String playerUuid, int questId, int amount) {
        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests == null) {
            return;
        }

        for (QuestProgress progress : playerQuests) {
            if (progress.questId == questId && !progress.isCompleted()) {
                progress.progress += amount;
                break;
            }
        }
    }

    /**
     * Check if quest is complete and award reward
     */
    public boolean completeQuest(String playerUuid, int questId) {
        Quest quest = quests.get(questId);
        if (quest == null) {
            return false;
        }

        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests == null) {
            return false;
        }

        QuestProgress progress = playerQuests.stream()
            .filter(qp -> qp.questId == questId && !qp.isCompleted())
            .findFirst()
            .orElse(null);

        if (progress == null || progress.progress < quest.targetCount) {
            return false; // Not ready to complete
        }

        // Award reward
        economyManager.deposit(playerUuid, quest.reward);
        progress.completedAt = System.currentTimeMillis();
        progress.isCompleted = true;
        totalQuestRewardsIssued += quest.reward;

        plugin.getLogger().info("✓ Quest completed: " + playerUuid.substring(0, 8) + 
            " -> " + quest.questName + " (Reward: $" + quest.reward + ")");
        return true;
    }

    // ===== QUERIES =====

    /**
     * Get available quests (not expired)
     */
    public List<Quest> getAvailableQuests() {
        return quests.values().stream()
            .filter(q -> !q.isExpired())
            .sorted((a, b) -> Long.compare(b.reward, a.reward))
            .toList();
    }

    /**
     * Get quest by ID
     */
    public Quest getQuest(int questId) {
        return quests.get(questId);
    }

    /**
     * Get active quests for player
     */
    public List<QuestProgress> getActiveQuests(String playerUuid) {
        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests == null) {
            return new ArrayList<>();
        }

        return playerQuests.stream()
            .filter(qp -> !qp.isCompleted())
            .toList();
    }

    /**
     * Get completed quests for player
     */
    public List<QuestProgress> getCompletedQuests(String playerUuid) {
        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests == null) {
            return new ArrayList<>();
        }

        return playerQuests.stream()
            .filter(QuestProgress::isCompleted)
            .toList();
    }

    /**
     * Get quest progress for player
     */
    public int getQuestProgress(String playerUuid, int questId) {
        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests == null) {
            return 0;
        }

        return playerQuests.stream()
            .filter(qp -> qp.questId == questId)
            .findFirst()
            .map(qp -> Math.toIntExact(qp.progress))
            .orElse(0);
    }

    /**
     * Get player's total quest earnings
     */
    public long getPlayerQuestEarnings(String playerUuid) {
        List<QuestProgress> playerQuests = playerProgress.get(playerUuid);
        if (playerQuests == null) {
            return 0;
        }

        return playerQuests.stream()
            .filter(QuestProgress::isCompleted)
            .mapToLong(qp -> quests.get(qp.questId).reward)
            .sum();
    }

    // ===== SPECIAL QUEST GENERATORS =====

    /**
     * Create daily build challenge
     */
    public Quest createBuildChallenge(String objectiveDescription, long reward) {
        return createQuest(
            "Daily Build Challenge",
            objectiveDescription,
            reward,
            1, // 1 day
            "BUILD",
            100 // 100 blocks
        );
    }

    /**
     * Create trading challenge
     */
    public Quest createTradeChallenge(long reward) {
        return createQuest(
            "Weekly Trade Challenge",
            "Complete 10 player-to-player trades",
            reward,
            7, // 7 days
            "TRADE",
            10
        );
    }

    /**
     * Create wealth challenge
     */
    public Quest createWealthChallenge(long targetWealth, long reward) {
        return createQuest(
            "Accumulate Wealth",
            "Reach $" + targetWealth + " net worth",
            reward,
            30, // 30 days
            "WEALTH",
            (int) (targetWealth / 100) // Count in hundreds
        );
    }

    /**
     * Create business challenge
     */
    public Quest createBusinessChallenge(long reward) {
        return createQuest(
            "Grow Your Business",
            "Generate $50,000 in company profit",
            reward,
            14, // 14 days
            "BUSINESS",
            50 // 50,000 / 1000
        );
    }

    // ===== MAINTENANCE =====

    /**
     * Archive expired quests
     */
    public int archiveExpiredQuests() {
        List<Integer> toRemove = new ArrayList<>();
        for (Quest quest : quests.values()) {
            if (quest.isExpired()) {
                toRemove.add(quest.questId);
            }
        }

        for (Integer questId : toRemove) {
            quests.remove(questId);
        }

        return toRemove.size();
    }

    // ===== STATISTICS =====

    /**
     * Get quest statistics
     */
    public QuestStats getStats() {
        List<Quest> available = getAvailableQuests();
        long totalCompleted = playerProgress.values().stream()
            .flatMap(List::stream)
            .filter(QuestProgress::isCompleted)
            .count();

        return new QuestStats(
            available.size(),
            quests.size(),
            playerProgress.size(),
            totalCompleted,
            totalQuestRewardsIssued
        );
    }

    /**
     * Get leaderboard (most quests completed)
     */
    public List<PlayerQuestStats> getLeaderboard(int limit) {
        return playerProgress.entrySet().stream()
            .map(entry -> new PlayerQuestStats(
                entry.getKey(),
                entry.getValue().stream().filter(QuestProgress::isCompleted).count(),
                getPlayerQuestEarnings(entry.getKey())
            ))
            .sorted((a, b) -> Long.compare(b.questsCompleted, a.questsCompleted))
            .limit(limit)
            .toList();
    }

    // ===== DATA CLASSES =====

    public static class Quest {
        public final int questId;
        public final String questName;
        public final String objective;
        public final long reward;
        public final long createdAt;
        public final long durationDays;
        public final String questType; // BUILD, TRADE, WEALTH, BUSINESS, etc.
        public final int targetCount;

        public Quest(int questId, String questName, String objective, long reward, long createdAt,
                    long durationDays, String questType, int targetCount) {
            this.questId = questId;
            this.questName = questName;
            this.objective = objective;
            this.reward = reward;
            this.createdAt = createdAt;
            this.durationDays = durationDays;
            this.questType = questType;
            this.targetCount = targetCount;
        }

        public boolean isExpired() {
            long expiresAt = createdAt + (durationDays * 24 * 60 * 60 * 1000);
            return System.currentTimeMillis() >= expiresAt;
        }

        public long getDaysRemaining() {
            long expiresAt = createdAt + (durationDays * 24 * 60 * 60 * 1000);
            long remaining = (expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            return Math.max(0, remaining);
        }
    }

    public static class QuestProgress {
        public final int questId;
        public final String playerUuid;
        public final long acceptedAt;

        public long progress = 0;
        public long completedAt = 0;
        public boolean isCompleted = false;

        public QuestProgress(int questId, String playerUuid, long acceptedAt) {
            this.questId = questId;
            this.playerUuid = playerUuid;
            this.acceptedAt = acceptedAt;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public int getProgressPercent(Quest quest) {
            if (quest == null) {
                return 0;
            }

            int percent = (int) ((progress * 100) / quest.targetCount);
            return Math.min(100, percent);
        }
    }

    public static class QuestStats {
        public final int availableQuests;
        public final int totalQuestsCreated;
        public final int playersWithQuests;
        public final long totalQuestsCompleted;
        public final long totalRewardsIssued;

        public QuestStats(int available, int total, int players, long completed, long rewards) {
            this.availableQuests = available;
            this.totalQuestsCreated = total;
            this.playersWithQuests = players;
            this.totalQuestsCompleted = completed;
            this.totalRewardsIssued = rewards;
        }
    }

    public static class PlayerQuestStats {
        public final String playerUuid;
        public final long questsCompleted;
        public final long totalEarnings;

        public PlayerQuestStats(String uuid, long completed, long earnings) {
            this.playerUuid = uuid;
            this.questsCompleted = completed;
            this.totalEarnings = earnings;
        }
    }
}
