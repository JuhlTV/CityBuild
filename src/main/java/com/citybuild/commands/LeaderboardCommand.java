package com.citybuild.commands;

import com.citybuild.features.leaderboards.LeaderboardManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /lb (leaderboard) command
 * Displays various leaderboards: richest, farmers, achievements, plot owners
 */
public class LeaderboardCommand implements CommandExecutor {
    
    private final LeaderboardManager leaderboardManager;
    
    public LeaderboardCommand(LeaderboardManager leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl ist nur für Spieler!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "richest":
            case "money":
            case "reich":
                return showRichest(player);
            case "achievements":
            case "ach":
            case "erfolg":
                return showAchievements(player);
            case "plots":
            case "plot":
            case "bauen":
                return showPlotOwners(player);
            case "farmers":
            case "farm":
            case "farmen":
                return showFarmers(player);
            default:
                return showHelp(player);
        }
    }
    
    private boolean showRichest(Player player) {
        String[] lb = leaderboardManager.getFormattedLeaderboard(LeaderboardManager.LeaderboardType.RICHEST);
        player.sendMessage("");
        for (String line : lb) {
            player.sendMessage(line);
        }
        player.sendMessage("");
        
        int rank = leaderboardManager.getPlayerRank(LeaderboardManager.LeaderboardType.RICHEST, 
                                                    player.getUniqueId());
        double value = leaderboardManager.getPlayerValue(LeaderboardManager.LeaderboardType.RICHEST, 
                                                         player.getUniqueId());
        
        if (rank > 0) {
            player.sendMessage(String.format("§7Dein Rank: §e#%d §7mit §6$%.0f", rank, value));
        } else {
            player.sendMessage(String.format("§7Dein Balance: §6$%.0f §7(nicht in Top 10)", value));
        }
        player.sendMessage("");
        return true;
    }
    
    private boolean showAchievements(Player player) {
        String[] lb = leaderboardManager.getFormattedLeaderboard(LeaderboardManager.LeaderboardType.ACHIEVEMENTS);
        player.sendMessage("");
        for (String line : lb) {
            player.sendMessage(line);
        }
        player.sendMessage("");
        
        int rank = leaderboardManager.getPlayerRank(LeaderboardManager.LeaderboardType.ACHIEVEMENTS, 
                                                    player.getUniqueId());
        double value = leaderboardManager.getPlayerValue(LeaderboardManager.LeaderboardType.ACHIEVEMENTS, 
                                                         player.getUniqueId());
        
        if (rank > 0) {
            player.sendMessage(String.format("§7Dein Rank: §e#%d §7mit §b%d Points", rank, (int) value));
        } else {
            player.sendMessage(String.format("§7Deine Points: §b%d §7(nicht in Top 10)", (int) value));
        }
        player.sendMessage("");
        return true;
    }
    
    private boolean showPlotOwners(Player player) {
        String[] lb = leaderboardManager.getFormattedLeaderboard(LeaderboardManager.LeaderboardType.PLOT_OWNERS);
        player.sendMessage("");
        for (String line : lb) {
            player.sendMessage(line);
        }
        player.sendMessage("");
        
        int rank = leaderboardManager.getPlayerRank(LeaderboardManager.LeaderboardType.PLOT_OWNERS, 
                                                    player.getUniqueId());
        double value = leaderboardManager.getPlayerValue(LeaderboardManager.LeaderboardType.PLOT_OWNERS, 
                                                         player.getUniqueId());
        
        if (rank > 0) {
            player.sendMessage(String.format("§7Dein Rank: §e#%d §7mit §9%d Plots", rank, (int) value));
        } else {
            player.sendMessage(String.format("§7Deine Plots: §9%d §7(nicht in Top 10)", (int) value));
        }
        player.sendMessage("");
        return true;
    }
    
    private boolean showFarmers(Player player) {
        String[] lb = leaderboardManager.getFormattedLeaderboard(LeaderboardManager.LeaderboardType.FARMERS);
        player.sendMessage("");
        for (String line : lb) {
            player.sendMessage(line);
        }
        player.sendMessage("");
        player.sendMessage("§7Farmers leaderboard basierend auf abgebauten Blöcken");
        player.sendMessage("");
        return true;
    }
    
    private boolean showHelp(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║ Leaderboard Befehle");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("§e/lb richest   §7- Reichste Spieler");
        player.sendMessage("§e/lb ach       §7- Achievement Rankings");
        player.sendMessage("§e/lb plots     §7- Plot-Besitzer");
        player.sendMessage("§e/lb farmers   §7- Top Farmer");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
        return true;
    }
}
