package com.citybuild.commands;

import com.citybuild.features.guilds.Guild;
import com.citybuild.features.guilds.GuildManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /guild command
 */
public class GuildCommand implements CommandExecutor {
    
    private final GuildManager guildManager;
    
    public GuildCommand(GuildManager guildManager) {
        this.guildManager = guildManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        return switch (subcommand) {
            case "create" -> createGuild(player, args);
            case "join" -> joinGuild(player, args);
            case "leave" -> leaveGuild(player);
            case "info" -> showGuildInfo(player);
            case "members" -> showMembers(player);
            default -> {
                showHelp(player);
                yield true;
            }
        };
    }
    
    private boolean createGuild(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cNutzung: /guild create <Name> <Tag>");
            return true;
        }
        
        Guild existing = guildManager.getPlayerGuild(player.getUniqueId());
        if (existing != null) {
            player.sendMessage("§cDu bist bereits Mitglied einer Gilde!");
            return true;
        }
        
        String guildName = args[1];
        String guildTag = args[2];
        
        Guild guild = guildManager.createGuild(player.getUniqueId(), guildName, guildTag);
        return guild != null;
    }
    
    private boolean joinGuild(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cNutzung: /guild join <Tag>");
            return true;
        }
        
        return guildManager.addMember(player.getUniqueId(), args[1]);
    }
    
    private boolean leaveGuild(Player player) {
        Guild guild = guildManager.getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("§cDu bist nicht Mitglied einer Gilde!");
            return true;
        }
        
        if (guildManager.removeMember(player.getUniqueId())) {
            player.sendMessage("§a✓ Du hast die Gilde verlassen.");
            return true;
        }
        return false;
    }
    
    private boolean showGuildInfo(Player player) {
        Guild guild = guildManager.getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("§cDu bist nicht Mitglied einer Gilde!");
            return true;
        }
        
        player.sendMessage("");
        player.sendMessage(guild.getFormattedInfo());
        player.sendMessage("");
        return true;
    }
    
    private boolean showMembers(Player player) {
        Guild guild = guildManager.getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("§cDu bist nicht Mitglied einer Gilde!");
            return true;
        }
        
        player.sendMessage(String.format("§6Mitglieder von [%s]:", guild.getGuildTag()));
        guild.getMembers().forEach(uuid -> {
            org.bukkit.entity.Player member = org.bukkit.Bukkit.getPlayer(uuid);
            String status = guild.isLeader(uuid) ? " §c(Leader)" : "";
            player.sendMessage("  §7- " + (member != null ? member.getName() : uuid.toString()) + status);
        });
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║ Gilde Befehle");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("§e/guild create <Name> <Tag>   §7- Gilde gründen");
        player.sendMessage("§e/guild join <Tag>            §7- Gilde beitreten");
        player.sendMessage("§e/guild leave                 §7- Gilde verlassen");
        player.sendMessage("§e/guild info                  §7- Gildinfo anzeigen");
        player.sendMessage("§e/guild members               §7- Mitglieder anzeigen");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
}
