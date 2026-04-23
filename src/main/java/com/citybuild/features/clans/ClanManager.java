package com.citybuild.features.clans;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Manages all clans in the game
 */
public class ClanManager {

    private final JavaPlugin plugin;
    private final Map<String, Clan> clans = new HashMap<>();
    private final Map<UUID, String> playerClanMap = new HashMap<>();
    private int clanCounter = 0;

    public ClanManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Create a new clan
     */
    public String createClan(UUID leaderUUID, String clanName, String clanTag) {
        // Validate name
        if (clanName.length() < 3 || clanName.length() > 20) {
            return null;
        }

        // Validate tag
        if (clanTag.length() < 2 || clanTag.length() > 5) {
            return null;
        }

        // Check tag uniqueness
        for (Clan clan : clans.values()) {
            if (clan.getClanTag().equalsIgnoreCase(clanTag)) {
                return null;
            }
        }

        // Create clan
        String clanId = "CLAN_" + (clanCounter++);
        Clan clan = new Clan(clanId, clanName, clanTag, leaderUUID);
        clans.put(clanId, clan);
        playerClanMap.put(leaderUUID, clanId);

        // Notify
        Player leader = Bukkit.getPlayer(leaderUUID);
        if (leader != null) {
            leader.sendMessage("§a✓ Clan created! §6" + clanName + " [" + clanTag + "]");
        }

        return clanId;
    }

    /**
     * Get clan by ID
     */
    public Clan getClan(String clanId) {
        return clans.get(clanId);
    }

    /**
     * Get player's clan
     */
    public Clan getPlayerClan(UUID playerUUID) {
        String clanId = playerClanMap.get(playerUUID);
        return clanId != null ? clans.get(clanId) : null;
    }

    /**
     * Add member to clan
     */
    public boolean addMember(UUID playerUUID, String clanTag) {
        // Find clan by tag
        Clan clan = clans.values().stream()
            .filter(c -> c.getClanTag().equalsIgnoreCase(clanTag))
            .findFirst()
            .orElse(null);

        if (clan == null) {
            return false;
        }

        // Check if already in a clan
        if (playerClanMap.containsKey(playerUUID)) {
            return false;
        }

        if (clan.addMember(playerUUID)) {
            playerClanMap.put(playerUUID, clan.getClanId());

            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§a✓ Joined clan: §6" + clan.getClanName());
            }

            return true;
        }

        return false;
    }

    /**
     * Remove member from clan
     */
    public boolean removeMember(UUID playerUUID) {
        String clanId = playerClanMap.get(playerUUID);
        if (clanId == null) return false;

        Clan clan = clans.get(clanId);
        if (clan != null && clan.removeMember(playerUUID)) {
            playerClanMap.remove(playerUUID);

            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§cYou left the clan!");
            }

            // Disband if no members
            if (clan.getMemberCount() == 0) {
                disbandClan(clanId);
            }

            return true;
        }

        return false;
    }

    /**
     * Disband a clan
     */
    public void disbandClan(String clanId) {
        Clan clan = clans.remove(clanId);
        if (clan != null) {
            // Remove all members from map
            for (UUID member : clan.getMembers()) {
                playerClanMap.remove(member);
            }

            plugin.getLogger().info("§6Clan disbanded: " + clan.getClanName());
        }
    }

    /**
     * Deposit coins to clan treasury
     */
    public void depositToTreasury(UUID playerUUID, double amount) {
        Clan clan = getPlayerClan(playerUUID);
        if (clan != null) {
            clan.addToTreasury(amount);
        }
    }

    /**
     * Get all clans
     */
    public Collection<Clan> getAllClans() {
        return new ArrayList<>(clans.values());
    }

    /**
     * Get clan statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_clans", clans.size());
        stats.put("total_members", clans.values().stream()
            .mapToInt(Clan::getMemberCount)
            .sum());
        stats.put("total_treasury", clans.values().stream()
            .mapToDouble(Clan::getTreasury)
            .sum());
        return stats;
    }
}
