package com.citybuild.features.farming;

/**
 * PlayerFarmData - Tracks individual player farming statistics
 */
public class PlayerFarmData {
    private final String playerUUID;
    private long blocksMinedTotal;
    private double coinsEarnedTotal;
    private int currentStreak; // Consecutive blocks without stopping
    private long lastBlockBreakTime;
    private int farmerLevel;
    private long totalAchievementPoints;
    
    // Combo tracking (different block types)
    private java.util.Set<org.bukkit.Material> blockTypesMinedRecently;
    private static final int COMBO_WINDOW_MS = 5000; // 5 second window
    
    public PlayerFarmData(String playerUUID) {
        this.playerUUID = playerUUID;
        this.blocksMinedTotal = 0;
        this.coinsEarnedTotal = 0.0;
        this.currentStreak = 0;
        this.lastBlockBreakTime = 0;
        this.farmerLevel = 1;
        this.totalAchievementPoints = 0;
        this.blockTypesMinedRecently = new java.util.HashSet<>();
    }
    
    // ===== Getters & Setters =====
    
    public String getPlayerUUID() {
        return playerUUID;
    }
    
    public long getBlocksMinedTotal() {
        return blocksMinedTotal;
    }
    
    public void addBlocksMined(long count) {
        this.blocksMinedTotal += count;
        updateFarmerLevel();
    }
    
    public double getCoinsEarnedTotal() {
        return coinsEarnedTotal;
    }
    
    public void addCoinsEarned(double amount) {
        this.coinsEarnedTotal += amount;
        updateFarmerLevel();
    }
    
    public int getCurrentStreak() {
        return currentStreak;
    }
    
    public void addToStreak() {
        this.currentStreak++;
        this.lastBlockBreakTime = System.currentTimeMillis();
    }
    
    public void resetStreak() {
        this.currentStreak = 0;
    }
    
    public boolean isStreakActive() {
        long timeSinceLastBlock = System.currentTimeMillis() - lastBlockBreakTime;
        return timeSinceLastBlock < COMBO_WINDOW_MS; // Active if block broken within 5 seconds
    }
    
    public int getFarmerLevel() {
        return farmerLevel;
    }
    
    public long getTotalAchievementPoints() {
        return totalAchievementPoints;
    }
    
    public void addAchievementPoints(long points) {
        this.totalAchievementPoints += points;
    }
    
    public java.util.Set<org.bukkit.Material> getBlockTypesMinedRecently() {
        return blockTypesMinedRecently;
    }
    
    public void addBlockType(org.bukkit.Material material) {
        this.blockTypesMinedRecently.add(material);
    }
    
    public void clearRecentBlockTypes() {
        this.blockTypesMinedRecently.clear();
    }
    
    public int getComboCount() {
        return blockTypesMinedRecently.size();
    }
    
    /**
     * Updates farmer level based on blocks mined (every 100 blocks = 1 level)
     */
    private void updateFarmerLevel() {
        int newLevel = (int) (blocksMinedTotal / 100) + 1;
        if (newLevel > farmerLevel) {
            this.farmerLevel = newLevel;
        }
    }
    
    /**
     * Gets detailed stats string
     */
    public String getDetailedStats() {
        return String.format(
            "§6=== Farmer Stats ===" +
            "\n§eLevel: §a%d" +
            "\n§eBlocks Mined: §a%d" +
            "\n§eCoins Earned: §a$%.2f" +
            "\n§eCurrent Streak: §a%d" +
            "\n§eAchievement Points: §a%d",
            farmerLevel, blocksMinedTotal, coinsEarnedTotal, currentStreak, totalAchievementPoints
        );
    }
    
    /**
     * Convert to Map for JSON serialization
     */
    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("blocksMinedTotal", blocksMinedTotal);
        map.put("coinsEarnedTotal", coinsEarnedTotal);
        map.put("currentStreak", currentStreak);
        map.put("farmerLevel", farmerLevel);
        map.put("totalAchievementPoints", totalAchievementPoints);
        map.put("lastBlockBreakTime", lastBlockBreakTime);
        return map;
    }
    
    /**
     * Create from Map loaded from JSON
     */
    public static PlayerFarmData fromMap(String playerUUID, java.util.Map<String, Object> map) {
        PlayerFarmData data = new PlayerFarmData(playerUUID);
        
        if (map.containsKey("blocksMinedTotal")) {
            data.blocksMinedTotal = ((Number) map.get("blocksMinedTotal")).longValue();
        }
        if (map.containsKey("coinsEarnedTotal")) {
            data.coinsEarnedTotal = ((Number) map.get("coinsEarnedTotal")).doubleValue();
        }
        if (map.containsKey("currentStreak")) {
            data.currentStreak = ((Number) map.get("currentStreak")).intValue();
        }
        if (map.containsKey("farmerLevel")) {
            data.farmerLevel = ((Number) map.get("farmerLevel")).intValue();
        }
        if (map.containsKey("totalAchievementPoints")) {
            data.totalAchievementPoints = ((Number) map.get("totalAchievementPoints")).longValue();
        }
        if (map.containsKey("lastBlockBreakTime")) {
            data.lastBlockBreakTime = ((Number) map.get("lastBlockBreakTime")).longValue();
        }
        
        data.updateFarmerLevel();
        return data;
    }
}
