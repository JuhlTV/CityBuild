package com.citybuild.features.guilds;

import java.util.*;

/**
 * Represents a player guild/team
 */
public class Guild {
    
    private String guildId;
    private String guildName;
    private String guildTag;
    private UUID leaderUUID;
    private Set<UUID> members;
    private double treasury;
    private String description;
    private int level;
    
    public Guild(String guildId, String guildName, String guildTag, UUID leader) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.guildTag = guildTag;
        this.leaderUUID = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.treasury = 0;
        this.description = "Neue Gilde";
        this.level = 1;
    }
    
    // Getters
    public String getGuildId() { return guildId; }
    public String getGuildName() { return guildName; }
    public String getGuildTag() { return guildTag; }
    public UUID getLeaderUUID() { return leaderUUID; }
    public Set<UUID> getMembers() { return new HashSet<>(members); }
    public double getTreasury() { return treasury; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }
    public int getMemberCount() { return members.size(); }
    
    // Setters
    public void setDescription(String desc) { this.description = desc; }
    public void setGuildTag(String tag) { this.guildTag = tag; }
    public void setLeader(UUID newLeader) {
        if (members.contains(newLeader)) {
            this.leaderUUID = newLeader;
        }
    }
    
    // Member Management
    public boolean addMember(UUID playerUUID) {
        if (members.size() >= 50) return false; // Max 50 members
        return members.add(playerUUID);
    }
    
    public boolean removeMember(UUID playerUUID) {
        if (playerUUID.equals(leaderUUID)) return false; // Can't remove leader
        return members.remove(playerUUID);
    }
    
    public boolean isMember(UUID playerUUID) {
        return members.contains(playerUUID);
    }
    
    public boolean isLeader(UUID playerUUID) {
        return leaderUUID.equals(playerUUID);
    }
    
    // Treasury Management
    public void addToTreasury(double amount) {
        this.treasury += amount;
    }
    
    public boolean withdrawFromTreasury(double amount) {
        if (treasury >= amount) {
            this.treasury -= amount;
            return true;
        }
        return false;
    }
    
    // Level Management
    public void levelUp() {
        this.level++;
    }
    
    public String getFormattedInfo() {
        return String.format(
            "§6§l[%s] %s §7(Level %d)\n" +
            "§7Mitglieder: §a%d/50\n" +
            "§7Kasse: §e$%.0f\n" +
            "§7Beschreibung: §7%s",
            guildTag, guildName, level, members.size(), treasury, description
        );
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (Members: %d, Treasury: $%.0f)", guildTag, guildName, members.size(), treasury);
    }
}
