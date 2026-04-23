package com.citybuild.features.cosmetics;

import java.util.UUID;

/**
 * Represents cosmetic options for a player
 */
public class PlayerCosmetics {

    private final UUID playerUUID;
    private String title;
    private boolean hasRainbowName;
    private boolean hasParticleEffects;
    private String nameColor;
    private int prestigeLevel;

    public enum Title {
        NONE("§7"),
        MAYOR("§6[MAYOR]"),
        MILLIONAIRE("§e[MILLIONAIRE]"),
        LEGENDARY("§d[LEGENDARY]"),
        BUILDING_MASTER("§c[BUILDING MASTER]"),
        FARMING_GURU("§2[FARMING GURU]"),
        GUILD_LEADER("§b[GUILD LEADER]"),
        TYCOON("§6[TYCOON]"),
        DIAMOND_PLAYER("§9[DIAMOND]");

        private final String display;

        Title(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    public enum NameColor {
        GOLD("§6"),
        RED("§c"),
        BLUE("§9"),
        GREEN("§a"),
        PURPLE("§d"),
        CYAN("§b"),
        YELLOW("§e"),
        WHITE("§f");

        private final String code;

        NameColor(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public PlayerCosmetics(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.title = Title.NONE.getDisplay();
        this.hasRainbowName = false;
        this.hasParticleEffects = false;
        this.nameColor = NameColor.WHITE.getCode();
        this.prestigeLevel = 0;
    }

    /**
     * Get formatted player display name with cosmetics
     */
    public String getFormattedName(String playerName) {
        StringBuilder sb = new StringBuilder();
        
        // Add title if not NONE
        if (!title.equals(Title.NONE.getDisplay())) {
            sb.append(title).append(" ");
        }
        
        // Add name with color
        sb.append(nameColor).append(playerName);
        
        // Add prestige indicator
        if (prestigeLevel > 0) {
            sb.append(" §c⭐§r").append(prestigeLevel);
        }
        
        return sb.toString();
    }

    /**
     * Get all cosmetics info
     */
    public String getFormattedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("§6Title: §e").append(title).append("\n");
        info.append("§6Name Color: ").append(nameColor).append(playerUUID).append("\n");
        info.append("§6Rainbow Name: ").append(hasRainbowName ? "§a✓" : "§c✕").append("\n");
        info.append("§6Particle Effects: ").append(hasParticleEffects ? "§a✓" : "§c✕").append("\n");
        info.append("§6Prestige Level: §e").append(prestigeLevel);
        return info.toString();
    }

    // Getters and Setters
    public UUID getPlayerUUID() { return playerUUID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean hasRainbowName() { return hasRainbowName; }
    public void setRainbowName(boolean value) { this.hasRainbowName = value; }
    public boolean hasParticleEffects() { return hasParticleEffects; }
    public void setParticleEffects(boolean value) { this.hasParticleEffects = value; }
    public String getNameColor() { return nameColor; }
    public void setNameColor(String color) { this.nameColor = color; }
    public int getPrestigeLevel() { return prestigeLevel; }
    public void setPrestigeLevel(int level) { this.prestigeLevel = level; }
    public void addPrestige() { this.prestigeLevel++; }
}
