package com.citybuild.commands;

import com.citybuild.features.clans.Clan;
import com.citybuild.features.clans.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /clan command
 */
public class ClanCommand implements CommandExecutor {

    private final ClanManager clanManager;

    public ClanCommand(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "join":
                handleJoin(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "info":
                handleInfo(player);
                break;
            case "members":
                handleMembers(player);
                break;
            case "promote":
                handlePromote(player, args);
                break;
            case "demote":
                handleDemote(player, args);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "list":
                handleList(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cUsage: /clan create <name> <tag>");
            return;
        }

        String name = args[1];
        String tag = args[2];

        if (clanManager.getPlayerClan(player.getUniqueId()) != null) {
            player.sendMessage("§c❌ You're already in a clan!");
            return;
        }

        String clanId = clanManager.createClan(player.getUniqueId(), name, tag);
        if (clanId != null) {
            player.sendMessage("§a✓ Clan created successfully!");
        } else {
            player.sendMessage("§c❌ Failed to create clan! Check name (3-20 chars) and tag (2-5 chars) uniqueness.");
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /clan join <tag>");
            return;
        }

        if (clanManager.getPlayerClan(player.getUniqueId()) != null) {
            player.sendMessage("§c❌ You're already in a clan!");
            return;
        }

        if (clanManager.addMember(player.getUniqueId(), args[1])) {
            player.sendMessage("§a✓ Joined clan!");
        } else {
            player.sendMessage("§c❌ Failed to join clan!");
        }
    }

    private void handleLeave(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.sendMessage("§c❌ You're not in a clan!");
            return;
        }

        if (clan.isLeader(player.getUniqueId())) {
            player.sendMessage("§c❌ Leader cannot leave! Disband or transfer leadership.");
            return;
        }

        clanManager.removeMember(player.getUniqueId());
    }

    private void handleInfo(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.sendMessage("§c❌ You're not in a clan!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 CLAN INFORMATION");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(clan.getFormattedInfo());
        player.sendMessage("");
    }

    private void handleMembers(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.sendMessage("§c❌ You're not in a clan!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 CLAN MEMBERS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (org.bukkit.entity.Player member : Bukkit.getOnlinePlayers()) {
            if (clan.isMember(member.getUniqueId())) {
                Clan.ClanRank rank = clan.getMemberRank(member.getUniqueId());
                player.sendMessage(rank.getDisplay() + " §7" + member.getName());
            }
        }

        player.sendMessage("");
    }

    private void handlePromote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /clan promote <player>");
            return;
        }

        Clan clan = clanManager.getPlayerClan(player.getUniqueId());
        if (clan == null || !clan.isOfficer(player.getUniqueId())) {
            player.sendMessage("§c❌ You must be an officer!");
            return;
        }

        player.sendMessage("§a✓ Promotion system (admin command only)");
    }

    private void handleDemote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /clan demote <player>");
            return;
        }

        Clan clan = clanManager.getPlayerClan(player.getUniqueId());
        if (clan == null || !clan.isOfficer(player.getUniqueId())) {
            player.sendMessage("§c❌ You must be an officer!");
            return;
        }

        player.sendMessage("§a✓ Demotion system (admin command only)");
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /clan kick <player>");
            return;
        }

        Clan clan = clanManager.getPlayerClan(player.getUniqueId());
        if (clan == null || !clan.isOfficer(player.getUniqueId())) {
            player.sendMessage("§c❌ You must be an officer!");
            return;
        }

        player.sendMessage("§a✓ Kick system (admin command only)");
    }

    private void handleList(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 ALL CLANS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (Clan clan : clanManager.getAllClans()) {
            player.sendMessage("§6" + clan.getClanName() + " [" + clan.getClanTag() + "]");
            player.sendMessage("  §7Level: §e" + clan.getLevel() + " | Members: §e" +
                clan.getMemberCount() + "§7/§e" + Clan.MAX_MEMBERS);
        }

        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 CLAN COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/clan create <name> <tag> §6- Create clan");
        player.sendMessage("§7/clan join <tag> §6- Join clan");
        player.sendMessage("§7/clan leave §6- Leave clan");
        player.sendMessage("§7/clan info §6- Clan info");
        player.sendMessage("§7/clan members §6- List members");
        player.sendMessage("§7/clan list §6- List all clans");
        player.sendMessage("");
    }
}
