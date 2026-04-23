package com.citybuild.features.guilds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Manages all guilds and guild memberships
 */
public class GuildManager {
    private final Map<String, Guild> guilds = new HashMap<>();
    private final Map<UUID, String> playerGuildMap = new HashMap<>(); // player UUID -> guild ID
    
    public GuildManager(Plugin plugin) {
        // Guild manager initialized
    }
    
    /**
     * Create a new guild
     */
    public Guild createGuild(UUID leaderUUID, String guildName, String guildTag) {
        Player leader = Bukkit.getPlayer(leaderUUID);
        if (leader == null) return null;
        
        // Validate inputs
        if (guildName.length() < 3 || guildName.length() > 20) {
            leader.sendMessage("§cGildennamen müssen 3-20 Zeichen lang sein!");
            return null;
        }
        
        if (guildTag.length() < 2 || guildTag.length() > 4) {
            leader.sendMessage("§cGilden-Tags müssen 2-4 Zeichen lang sein!");
            return null;
        }
        
        // Check if tag is unique
        if (guilds.values().stream().anyMatch(g -> g.getGuildTag().equalsIgnoreCase(guildTag))) {
            leader.sendMessage("§cDieses Tag existiert bereits!");
            return null;
        }
        
        String guildId = "guild_" + UUID.randomUUID().toString().substring(0, 8);
        Guild guild = new Guild(guildId, guildName, guildTag, leaderUUID);
        
        guilds.put(guildId, guild);
        playerGuildMap.put(leaderUUID, guildId);
        
        for (Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(String.format("§6Neue Gilde gegründet: §e[%s] %s", guildTag, guildName));
        }
        leader.sendMessage("§a✓ Gilde erfolgreich gegründet!");
        
        return guild;
    }
    
    /**
     * Get guild by ID
     */
    public Guild getGuild(String guildId) {
        return guilds.get(guildId);
    }
    
    /**
     * Get player's guild
     */
    public Guild getPlayerGuild(UUID playerUUID) {
        String guildId = playerGuildMap.get(playerUUID);
        return guildId != null ? guilds.get(guildId) : null;
    }
    
    /**
     * Add member to guild
     */
    public boolean addMember(UUID playerUUID, String guildTag) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return false;
        
        // Find guild by tag
        Guild guild = guilds.values().stream()
            .filter(g -> g.getGuildTag().equalsIgnoreCase(guildTag))
            .findFirst()
            .orElse(null);
        
        if (guild == null) {
            player.sendMessage("§cGilde mit Tag §e" + guildTag + " §cnicht gefunden!");
            return false;
        }
        
        if (getPlayerGuild(playerUUID) != null) {
            player.sendMessage("§cDu bist bereits Mitglied einer Gilde!");
            return false;
        }
        
        if (guild.addMember(playerUUID)) {
            playerGuildMap.put(playerUUID, guild.getGuildId());
            player.sendMessage(String.format("§a✓ Du bist der Gilde §e[%s] %s§a beigetreten!", 
                guild.getGuildTag(), guild.getGuildName()));
            
            // Notify guild members
            notifyGuildMembers(guild, player.getName() + " ist der Gilde beigetreten!");
            return true;
        }
        
        player.sendMessage("§cDie Gilde ist voll!");
        return false;
    }
    
    /**
     * Remove member from guild
     */
    public boolean removeMember(UUID playerUUID) {
        Guild guild = getPlayerGuild(playerUUID);
        if (guild == null) return false;
        
        if (!guild.removeMember(playerUUID)) return false;
        
        playerGuildMap.remove(playerUUID);
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            player.sendMessage("§cDu wurdest aus der Gilde entfernt!");
        }
        
        notifyGuildMembers(guild, player != null ? player.getName() : "Ein Spieler" + " hat die Gilde verlassen!");
        return true;
    }
    
    /**
     * Disband a guild
     */
    public void disbandGuild(String guildId) {
        Guild guild = guilds.remove(guildId);
        if (guild == null) return;
        
        // Remove all members from guild map
        guild.getMembers().forEach(playerGuildMap::remove);
        
        for (Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(String.format("§cGilde aufgelöst: §e[%s] %s", 
                guild.getGuildTag(), guild.getGuildName()));
        }
    }
    
    /**
     * Add coins to guild treasury
     */
    public void depositToGuild(UUID playerUUID, double amount) {
        Guild guild = getPlayerGuild(playerUUID);
        if (guild == null) return;
        
        guild.addToTreasury(amount);
        notifyGuildMembers(guild, Bukkit.getPlayer(playerUUID).getName() + " hat §e$" + (int)amount + " §7eingezahlt!");
    }
    
    /**
     * Get all guilds (for leaderboards)
     */
    public Collection<Guild> getAllGuilds() {
        return new ArrayList<>(guilds.values());
    }
    
    /**
     * Notify all guild members
     */
    private void notifyGuildMembers(Guild guild, String message) {
        for (UUID memberUUID : guild.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage(String.format("§6[Gilde §e%s§6] §7%s", guild.getGuildTag(), message));
            }
        }
    }
}
