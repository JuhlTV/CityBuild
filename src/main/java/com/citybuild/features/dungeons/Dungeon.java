package com.citybuild.features.dungeons;

import java.util.*;

/**
 * Represents a dungeon with multiple difficulty levels
 */
public class Dungeon {

    public enum Difficulty {
        EASY("§a[EASY]", 1.0, 5000, 100),
        NORMAL("§e[NORMAL]", 1.5, 15000, 300),
        HARD("§c[HARD]", 2.0, 40000, 750),
        LEGENDARY("§5[LEGENDARY]", 2.5, 100000, 2000);

        private final String display;
        private final double multiplier;
        private final double reward;
        private final int achievementPoints;

        Difficulty(String display, double multiplier, double reward, int achievementPoints) {
            this.display = display;
            this.multiplier = multiplier;
            this.reward = reward;
            this.achievementPoints = achievementPoints;
        }

        public String getDisplay() {
            return display;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public double getReward() {
            return reward;
        }

        public int getAchievementPoints() {
            return achievementPoints;
        }
    }

    private final String dungeonId;
    private final String dungeonName;
    private final String description;
    private final Map<UUID, DungeonProgress> playerProgress;
    private final int maxPlayers;
    private final int durationMinutes;
    private int totalClears;
    private double totalRewardsDistributed;

    public Dungeon(String dungeonId, String dungeonName, String description, int maxPlayers, int durationMinutes) {
        this.dungeonId = dungeonId;
        this.dungeonName = dungeonName;
        this.description = description;
        this.playerProgress = new HashMap<>();
        this.maxPlayers = maxPlayers;
        this.durationMinutes = durationMinutes;
        this.totalClears = 0;
        this.totalRewardsDistributed = 0;
    }

    /**
     * Add player to dungeon
     */
    public boolean addPlayer(UUID playerUUID) {
        if (playerProgress.size() >= maxPlayers) return false;
        if (playerProgress.containsKey(playerUUID)) return false;

        playerProgress.put(playerUUID, new DungeonProgress(playerUUID));
        return true;
    }

    /**
     * Remove player from dungeon
     */
    public void removePlayer(UUID playerUUID) {
        playerProgress.remove(playerUUID);
    }

    /**
     * Update player progress
     */
    public void updateProgress(UUID playerUUID, double bossHealth, int enemiesDefeated) {
        DungeonProgress progress = playerProgress.get(playerUUID);
        if (progress != null) {
            progress.setBossHealth(bossHealth);
            progress.setEnemiesDefeated(enemiesDefeated);
        }
    }

    /**
     * Complete dungeon for player
     */
    public void completeDungeon(UUID playerUUID, Difficulty difficulty) {
        DungeonProgress progress = playerProgress.get(playerUUID);
        if (progress != null) {
            progress.setCompleted(true);
            progress.setDifficulty(difficulty);
            totalClears++;
            totalRewardsDistributed += difficulty.getReward();
        }
    }

    /**
     * Get player progress
     */
    public DungeonProgress getPlayerProgress(UUID playerUUID) {
        return playerProgress.get(playerUUID);
    }

    /**
     * Get active players
     */
    public int getActivePlayers() {
        return playerProgress.size();
    }

    /**
     * Get formatted info
     */
    public String getFormattedInfo(Difficulty difficulty) {
        StringBuilder sb = new StringBuilder();
        sb.append("§6").append(dungeonName).append(" ").append(difficulty.getDisplay()).append("\n");
        sb.append("§7").append(description).append("\n");
        sb.append("§7Duration: §e").append(durationMinutes).append(" minutes\n");
        sb.append("§7Reward: §6$").append(String.format("%.0f", difficulty.getReward())).append("\n");
        sb.append("§7Achievement Pts: §e").append(difficulty.getAchievementPoints());
        return sb.toString();
    }

    // Getters
    public String getDungeonId() { return dungeonId; }
    public String getDungeonName() { return dungeonName; }
    public String getDescription() { return description; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getTotalClears() { return totalClears; }
    public double getTotalRewardsDistributed() { return totalRewardsDistributed; }

    /**
     * Inner class for tracking player progress
     */
    public static class DungeonProgress {
        private final UUID playerUUID;
        private boolean completed;
        private Difficulty difficulty;
        private double bossHealth;
        private int enemiesDefeated;
        private long startTime;

        public DungeonProgress(UUID playerUUID) {
            this.playerUUID = playerUUID;
            this.completed = false;
            this.difficulty = null;
            this.bossHealth = 100;
            this.enemiesDefeated = 0;
            this.startTime = System.currentTimeMillis();
        }

        public UUID getPlayerUUID() { return playerUUID; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
        public double getBossHealth() { return bossHealth; }
        public void setBossHealth(double health) { this.bossHealth = health; }
        public int getEnemiesDefeated() { return enemiesDefeated; }
        public void setEnemiesDefeated(int count) { this.enemiesDefeated = count; }
        public long getStartTime() { return startTime; }
        public long getElapsedSeconds() { return (System.currentTimeMillis() - startTime) / 1000; }
    }
}
