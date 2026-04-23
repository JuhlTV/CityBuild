package com.citybuild.features.clans;

import java.util.*;

/**
 * Represents a clan - a large player organization
 */
public class Clan {

    private final String clanId;
    private final String clanName;
    private final String clanTag;
    private final UUID leaderUUID;
    private final Set<UUID> members;
    private final Map<UUID, ClanRank> memberRanks;
    private double treasury;
    private int level;
    private String description;
    private long createdAt;

    public enum ClanRank {
        LEADER("§c[LEADER]", 4),
        OFFICER("§6[OFFICER]", 3),
        MEMBER("§7[MEMBER]", 1),
        RECRUIT("§8[RECRUIT]", 0);

        private final String display;
        private final int priority;

        ClanRank(String display, int priority) {
            this.display = display;
            this.priority = priority;
        }

        public String getDisplay() {
            return display;
        }

        public int getPriority() {
            return priority;
        }
    }

    public static final int MAX_MEMBERS = 100;
    public static final int MAX_TAG_LENGTH = 5;

    public Clan(String clanId, String clanName, String clanTag, UUID leaderUUID) {
        this.clanId = clanId;
        this.clanName = clanName;
        this.clanTag = clanTag;
        this.leaderUUID = leaderUUID;
        this.members = new HashSet<>();
        this.memberRanks = new HashMap<>();
        this.treasury = 0;
        this.level = 1;
        this.description = "New Clan";
        this.createdAt = System.currentTimeMillis();

        // Add leader
        members.add(leaderUUID);
        memberRanks.put(leaderUUID, ClanRank.LEADER);
    }

    /**
     * Add member to clan
     */
    public boolean addMember(UUID playerUUID) {
        if (members.size() >= MAX_MEMBERS) return false;
        if (members.contains(playerUUID)) return false;

        members.add(playerUUID);
        memberRanks.put(playerUUID, ClanRank.RECRUIT);
        return true;
    }

    /**
     * Remove member from clan
     */
    public boolean removeMember(UUID playerUUID) {
        if (playerUUID.equals(leaderUUID)) return false; // Can't remove leader

        members.remove(playerUUID);
        memberRanks.remove(playerUUID);
        return true;
    }

    /**
     * Set member rank
     */
    public void setMemberRank(UUID playerUUID, ClanRank rank) {
        if (members.contains(playerUUID)) {
            memberRanks.put(playerUUID, rank);
        }
    }

    /**
     * Get member rank
     */
    public ClanRank getMemberRank(UUID playerUUID) {
        return memberRanks.getOrDefault(playerUUID, ClanRank.RECRUIT);
    }

    /**
     * Check if player is member
     */
    public boolean isMember(UUID playerUUID) {
        return members.contains(playerUUID);
    }

    /**
     * Check if player is officer or higher
     */
    public boolean isOfficer(UUID playerUUID) {
        ClanRank rank = memberRanks.getOrDefault(playerUUID, ClanRank.RECRUIT);
        return rank.getPriority() >= ClanRank.OFFICER.getPriority();
    }

    /**
     * Check if player is leader
     */
    public boolean isLeader(UUID playerUUID) {
        return playerUUID.equals(leaderUUID);
    }

    /**
     * Add coins to treasury
     */
    public void addToTreasury(double amount) {
        this.treasury += amount;
    }

    /**
     * Withdraw from treasury
     */
    public boolean withdrawFromTreasury(double amount) {
        if (treasury >= amount) {
            this.treasury -= amount;
            return true;
        }
        return false;
    }

    /**
     * Level up clan
     */
    public void levelUp() {
        this.level++;
    }

    /**
     * Get formatted info
     */
    public String getFormattedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6Clan: §e").append(clanName).append(" [").append(clanTag).append("]\n");
        sb.append("§7Level: §e").append(level).append("\n");
        sb.append("§7Members: §e").append(members.size()).append("/").append(MAX_MEMBERS).append("\n");
        sb.append("§7Treasury: §6$").append(String.format("%.2f", treasury)).append("\n");
        sb.append("§7Description: §e").append(description);
        return sb.toString();
    }

    // Getters
    public String getClanId() { return clanId; }
    public String getClanName() { return clanName; }
    public String getClanTag() { return clanTag; }
    public UUID getLeaderUUID() { return leaderUUID; }
    public Set<UUID> getMembers() { return new HashSet<>(members); }
    public int getMemberCount() { return members.size(); }
    public double getTreasury() { return treasury; }
    public int getLevel() { return level; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getCreatedAt() { return createdAt; }
}
