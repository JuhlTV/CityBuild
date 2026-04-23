package com.citybuild.features.ranking;

/**
 * Enum representing player ranks based on achievement points
 * Provides tier progression system: BRONZE → SILVER → GOLD → PLATINUM → DIAMOND
 */
public enum Rank {
    BRONZE("⚪ BRONZE", 0, 100, "§7"),
    SILVER("🟡 SILVER", 100, 300, "§f"),
    GOLD("🟠 GOLD", 300, 750, "§6"),
    PLATINUM("💜 PLATINUM", 750, 1500, "§d"),
    DIAMOND("💎 DIAMOND", 1500, Integer.MAX_VALUE, "§b");

    private final String display;
    private final int minPoints;
    private final int maxPoints;
    private final String color;

    Rank(String display, int minPoints, int maxPoints, String color) {
        this.display = display;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.color = color;
    }

    /**
     * Get the rank for a given achievement point total
     * @param points Total achievement points
     * @return The appropriate rank for the point value
     */
    public static Rank getRankForPoints(int points) {
        if (points >= DIAMOND.minPoints) return DIAMOND;
        if (points >= PLATINUM.minPoints) return PLATINUM;
        if (points >= GOLD.minPoints) return GOLD;
        if (points >= SILVER.minPoints) return SILVER;
        return BRONZE;
    }

    /**
     * Get points required to reach next rank
     * @return Points needed for next rank (0 if already at max)
     */
    public int getPointsToNextRank(int currentPoints) {
        if (this == DIAMOND) return 0;
        
        Rank nextRank = switch (this) {
            case BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD -> PLATINUM;
            case PLATINUM -> DIAMOND;
            default -> null;
        };
        
        if (nextRank == null) return 0;
        return Math.max(0, nextRank.minPoints - currentPoints);
    }

    /**
     * Get progress percentage to next rank (0-100)
     */
    public int getProgressToNextRank(int currentPoints) {
        if (this == DIAMOND) return 100;
        
        int nextRankMinPoints = switch (this) {
            case BRONZE -> SILVER.minPoints;
            case SILVER -> GOLD.minPoints;
            case GOLD -> PLATINUM.minPoints;
            case PLATINUM -> DIAMOND.minPoints;
            default -> maxPoints;
        };
        
        int pointsInRange = currentPoints - minPoints;
        int rangeSize = nextRankMinPoints - minPoints;
        
        return (rangeSize == 0) ? 100 : Math.min(100, (pointsInRange * 100) / rangeSize);
    }

    /**
     * Get formatted display string with color
     */
    public String getFormattedDisplay() {
        return color + display;
    }

    /**
     * Get next rank (or DIAMOND if at max)
     */
    public Rank getNextRank() {
        return switch (this) {
            case BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD -> PLATINUM;
            case PLATINUM, DIAMOND -> DIAMOND;
        };
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return display;
    }
}
