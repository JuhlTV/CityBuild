package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.ranking.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /rank command
 * Displays player ranking information and progression
 */
public class RankCommand implements CommandExecutor {

    private final CityBuildPlugin plugin;

    public RankCommand(CityBuildPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Show own rank
            showPlayerRank(player, player);
            return true;
        }

        if (args.length == 1) {
            // Show other player's rank
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§c❌ Player not found!");
                return true;
            }

            showPlayerRank(player, target);
            return true;
        }

        showHelp(player);
        return true;
    }

    private void showPlayerRank(Player viewer, Player target) {
        int achievementPoints = plugin.getAchievementManager().getPlayerAchievementPoints(target.getUniqueId());
        String rankInfo = plugin.getRankingManager().getRankInfo(target.getUniqueId(), achievementPoints);

        viewer.sendMessage("");
        viewer.sendMessage("§e╔════════════════════════════════════════╗");
        viewer.sendMessage("§e║§6 RANK INFORMATION - " + target.getName());
        viewer.sendMessage("§e╚════════════════════════════════════════╝");
        viewer.sendMessage("");
        viewer.sendMessage("§7" + rankInfo);
        viewer.sendMessage("");
        viewer.sendMessage("§7Achievement Points: §6" + achievementPoints);
        viewer.sendMessage("§7Achievements Unlocked: §6" + plugin.getAchievementManager().getUnlockedCount(target.getUniqueId()) + "§7/16");
        viewer.sendMessage("");

        // Show rank progression
        showRankProgression(viewer);
        viewer.sendMessage("");
    }

    private void showRankProgression(Player player) {
        player.sendMessage("§e═══════════════════════════════════════");
        player.sendMessage("§6📊 RANKING PROGRESSION");
        player.sendMessage("§e═══════════════════════════════════════");
        player.sendMessage("");

        Rank[] ranks = {Rank.BRONZE, Rank.SILVER, Rank.GOLD, Rank.PLATINUM, Rank.DIAMOND};
        for (Rank rank : ranks) {
            player.sendMessage(rank.getFormattedDisplay() + "§7: " + rank.getMinPoints() + " - " + 
                (rank.getMaxPoints() == Integer.MAX_VALUE ? "∞" : rank.getMaxPoints()) + " points");
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 RANK COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/rank §6- Show your rank");
        player.sendMessage("§7/rank <Player> §6- Show player's rank");
        player.sendMessage("");
    }
}
