package com.citybuild.features.quests;

/**
 * Represents a quest that players can complete for rewards
 */
public class Quest {

    public enum QuestType {
        DAILY("📅 Daily", 24),
        WEEKLY("📅 Weekly", 168),
        PERMANENT("♾️ Permanent", 0);

        private final String display;
        private final int durationHours;

        QuestType(String display, int durationHours) {
            this.display = display;
            this.durationHours = durationHours;
        }

        public String getDisplay() {
            return display;
        }

        public int getDurationHours() {
            return durationHours;
        }
    }

    public enum QuestRewardType {
        COINS("💰"),
        ACHIEVEMENT_POINTS("🏆"),
        EXP("⭐"),
        ITEMS("📦");

        private final String symbol;

        QuestRewardType(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final String questId;
    private final String name;
    private final String description;
    private final QuestType type;
    private final int progressRequired;
    private final double coinReward;
    private final int achievementPointReward;
    private final long createdAt;

    public Quest(String questId, String name, String description, QuestType type,
                 int progressRequired, double coinReward, int achievementPointReward) {
        this.questId = questId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.progressRequired = progressRequired;
        this.coinReward = coinReward;
        this.achievementPointReward = achievementPointReward;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Get formatted quest display
     */
    public String getFormattedDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6").append(name).append(" ").append(type.getDisplay()).append("\n");
        sb.append("§7").append(description).append("\n");
        sb.append("§7Progress: §e0/").append(progressRequired).append("\n");
        sb.append("§7Rewards: ");
        sb.append("§6$").append(String.format("%.0f", coinReward));
        sb.append(" §7| §e").append(achievementPointReward).append("pts");
        return sb.toString();
    }

    /**
     * Get time remaining for quest (if limited)
     */
    public long getTimeRemainingHours() {
        if (type.getDurationHours() == 0) return 0; // Permanent quest
        long elapsedMs = System.currentTimeMillis() - createdAt;
        long remainingMs = (type.getDurationHours() * 60 * 60 * 1000L) - elapsedMs;
        return Math.max(0, remainingMs / (60 * 60 * 1000));
    }

    /**
     * Check if quest has expired
     */
    public boolean isExpired() {
        if (type.getDurationHours() == 0) return false; // Permanent never expires
        return getTimeRemainingHours() <= 0;
    }

    // Getters
    public String getQuestId() { return questId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public QuestType getType() { return type; }
    public int getProgressRequired() { return progressRequired; }
    public double getCoinReward() { return coinReward; }
    public int getAchievementPointReward() { return achievementPointReward; }
    public long getCreatedAt() { return createdAt; }
}
