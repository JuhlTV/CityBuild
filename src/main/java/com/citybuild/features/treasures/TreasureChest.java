package com.citybuild.features.treasures;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Represents a treasure chest with hidden rewards
 */
public class TreasureChest {

    public enum Rarity {
        COMMON("§f[COMMON]", 1000, 100),
        UNCOMMON("§a[UNCOMMON]", 3000, 250),
        RARE("§b[RARE]", 8000, 500),
        EPIC("§5[EPIC]", 20000, 1000),
        LEGENDARY("§6[LEGENDARY]", 50000, 2500);

        private final String display;
        private final double reward;
        private final int achievementPoints;

        Rarity(String display, double reward, int achievementPoints) {
            this.display = display;
            this.reward = reward;
            this.achievementPoints = achievementPoints;
        }

        public String getDisplay() {
            return display;
        }

        public double getReward() {
            return reward;
        }

        public int getAchievementPoints() {
            return achievementPoints;
        }
    }

    private final String treasureId;
    private final Rarity rarity;
    private final Location location;
    private final long createdAt;
    private UUID discoveredBy;
    private boolean discovered;
    private long discoveredAt;

    public TreasureChest(String treasureId, Rarity rarity, Location location) {
        this.treasureId = treasureId;
        this.rarity = rarity;
        this.location = location;
        this.createdAt = System.currentTimeMillis();
        this.discovered = false;
        this.discoveredBy = null;
        this.discoveredAt = 0;
    }

    /**
     * Discover treasure
     */
    public void discover(UUID playerUUID) {
        this.discovered = true;
        this.discoveredBy = playerUUID;
        this.discoveredAt = System.currentTimeMillis();
    }

    /**
     * Check if treasure is still hidden
     */
    public boolean isHidden() {
        return !discovered;
    }

    /**
     * Get age in minutes
     */
    public long getAgeMinutes() {
        long age = System.currentTimeMillis() - createdAt;
        return age / (60 * 1000);
    }

    /**
     * Check if treasure has expired (7 days)
     */
    public boolean hasExpired() {
        long sevenDaysMs = 7 * 24 * 60 * 60 * 1000L;
        return System.currentTimeMillis() - createdAt > sevenDaysMs;
    }

    /**
     * Get formatted info
     */
    public String getFormattedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6Treasure Chest ").append(rarity.getDisplay()).append("\n");
        sb.append("§7Location: §e").append((int)location.getX()).append(", ")
          .append((int)location.getY()).append(", ").append((int)location.getZ()).append("\n");
        sb.append("§7Status: ").append(discovered ? "§c❌ DISCOVERED" : "§a✓ HIDDEN").append("\n");
        sb.append("§7Reward: §6$").append(String.format("%.0f", rarity.getReward())).append("\n");
        sb.append("§7Achievement Pts: §e").append(rarity.getAchievementPoints());
        return sb.toString();
    }

    // Getters
    public String getTreasureId() { return treasureId; }
    public Rarity getRarity() { return rarity; }
    public Location getLocation() { return location; }
    public UUID getDiscoveredBy() { return discoveredBy; }
    public boolean isDiscovered() { return discovered; }
    public long getCreatedAt() { return createdAt; }
    public long getDiscoveredAt() { return discoveredAt; }
}
