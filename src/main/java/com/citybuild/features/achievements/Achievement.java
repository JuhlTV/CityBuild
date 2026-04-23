package com.citybuild.features.achievements;

/**
 * Represents a single Achievement that players can unlock
 * Achievements track player progress and can grant rewards
 */
public class Achievement {
    
    // Achievement Categories
    public enum Category {
        MINING("Bergbau", "⛏️"),
        FARMING("Landwirtschaft", "🌾"),
        ECONOMY("Wirtschaft", "💰"),
        BUILDING("Bauen", "🏗️"),
        SOCIAL("Sozial", "👥"),
        SPECIAL("Spezial", "⭐");
        
        private final String displayName;
        private final String emoji;
        
        Category(String displayName, String emoji) {
            this.displayName = displayName;
            this.emoji = emoji;
        }
        
        public String getDisplayName() { return displayName; }
        public String getEmoji() { return emoji; }
    }
    
    // Achievement Rarity
    public enum Rarity {
        COMMON("Häufig", "§f", 10),           // White
        UNCOMMON("Ungewöhnlich", "§a", 25),   // Green
        RARE("Selten", "§9", 50),             // Blue
        EPIC("Episch", "§d", 100),            // Magenta
        LEGENDARY("Legendär", "§6", 250);     // Gold
        
        private final String name;
        private final String color;
        private final int points;
        
        Rarity(String name, String color, int points) {
            this.name = name;
            this.color = color;
            this.points = points;
        }
        
        public String getName() { return name; }
        public String getColor() { return color; }
        public int getPoints() { return points; }
    }
    
    private String id;
    private String name;
    private String description;
    private Category category;
    private Rarity rarity;
    private int rewardCoins;
    private String rewardTitle;
    private int progressRequired;
    private boolean hidden;
    
    // Conditional fields
    private transient boolean unlocked;
    private transient int currentProgress;
    
    // JSON-friendly constructor
    public Achievement() {}
    
    public Achievement(String id, String name, String description, Category category, 
                      Rarity rarity, int rewardCoins, int progressRequired) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.rarity = rarity;
        this.rewardCoins = rewardCoins;
        this.progressRequired = progressRequired;
        this.hidden = false;
        this.unlocked = false;
        this.currentProgress = 0;
    }
    
    // ===== Getters =====
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Rarity getRarity() { return rarity; }
    public int getRewardCoins() { return rewardCoins; }
    public String getRewardTitle() { return rewardTitle; }
    public int getProgressRequired() { return progressRequired; }
    public boolean isHidden() { return hidden; }
    public boolean isUnlocked() { return unlocked; }
    public int getCurrentProgress() { return currentProgress; }
    
    // ===== Setters =====
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public void setCurrentProgress(int progress) { this.currentProgress = Math.min(progress, progressRequired); }
    public void addProgress(int amount) { 
        this.currentProgress = Math.min(currentProgress + amount, progressRequired);
    }
    public void setRewardTitle(String title) { this.rewardTitle = title; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    
    // ===== Logic Methods =====
    public boolean isCompleted() {
        return currentProgress >= progressRequired;
    }
    
    public double getProgressPercentage() {
        return (double) currentProgress / progressRequired * 100;
    }
    
    public String getProgressBar() {
        int filled = (int) (getProgressPercentage() / 10);
        int empty = 10 - filled;
        return "§a" + "█".repeat(filled) + "§7" + "█".repeat(empty) + " §7" + 
               currentProgress + "/" + progressRequired;
    }
    
    public String getFormattedDisplay() {
        String status = isUnlocked() ? "§a✓" : "§7✗";
        String color = getRarity().getColor();
        return String.format("%s %s%s %s §7- %s", 
            status,
            color,
            getCategory().getEmoji(),
            getName(),
            getDescription()
        );
    }
    
    @Override
    public String toString() {
        return String.format("Achievement{id=%s, name=%s, unlocked=%s, progress=%d/%d}", 
            id, name, unlocked, currentProgress, progressRequired);
    }
}
