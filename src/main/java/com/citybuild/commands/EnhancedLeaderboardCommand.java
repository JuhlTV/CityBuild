package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.leaderboards.LeaderboardManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.*;
import java.util.*;

/**
 * Enhanced leaderboard command with time-based rankings and statistics
 */
public class EnhancedLeaderboardCommand implements CommandExecutor {
    
    private final CityBuildPlugin plugin;
    private final LeaderboardManager leaderboardManager;
    
    public EnhancedLeaderboardCommand(CityBuildPlugin plugin, LeaderboardManager leaderboardManager) {
        this.plugin = plugin;
        this.leaderboardManager = leaderboardManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "top":
                handleTop(player, args);
                break;
            case "weekly":
                handleWeekly(player);
                break;
            case "monthly":
                handleMonthly(player);
                break;
            case "alltime":
                handleAlltime(player);
                break;
            case "rank":
                handleRank(player, args);
                break;
            case "stats":
                handleStats(player);
                break;
            case "compare":
                handleCompare(player, args);
                break;
            case "filter":
                handleFilter(player, args);
                break;
            default:
                showHelp(player);
        }
        
        return true;
    }
    
    private void handleTop(Player player, String[] args) {
        String category = args.length > 1 ? args[1] : "economy";
        int limit = args.length > 2 ? Integer.parseInt(args[2]) : 10;
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  TOP " + Math.min(limit, 10) + " - " + category.toUpperCase());
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        
        // Example leaderboard
        for (int i = 1; i <= Math.min(limit, 10); i++) {
            player.sendMessage(String.format("§e%d. §6Player%d §7- §a$§61000", i, i * 100));
        }
        
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleWeekly(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  WEEKLY RANKINGS");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        player.sendMessage("§7Week: §e" + weekStart + " → " + weekEnd);
        player.sendMessage("");
        player.sendMessage("§e1. §6Player1 §7(+§a$5000§7)");
        player.sendMessage("§e2. §6Player2 §7(+§a$3000§7)");
        player.sendMessage("§e3. §6Player3 §7(+§a$1500§7)");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleMonthly(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  MONTHLY RANKINGS");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        
        player.sendMessage("§7Month: §e" + currentMonth);
        player.sendMessage("");
        player.sendMessage("§e1. §6Player1 §7(+§a$15000§7)");
        player.sendMessage("§e2. §6Player2 §7(+§a$10000§7)");
        player.sendMessage("§e3. §6Player3 §7(+§a$5000§7)");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleAlltime(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  ALL-TIME RANKINGS");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        
        player.sendMessage("§e1. §6LegendaryPlayer §7- §a$1,234,567");
        player.sendMessage("§e2. §6ProPlayer §7- §a$987,654");
        player.sendMessage("§e3. §6AwesomePlayer §7- §a$654,321");
        player.sendMessage("§7...");
        player.sendMessage("§e" + 999 + ". §6YouAreHere §7- §a$100");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleRank(Player player, String[] args) {
        String targetName = args.length > 1 ? args[1] : player.getName();
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  RANK DETAILS - " + targetName);
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        player.sendMessage("§7Overall Rank: §e#1234");
        player.sendMessage("§7Economy Rank: §e#5");
        player.sendMessage("§7Achievement Rank: §e#42");
        player.sendMessage("§7Plot Rank: §e#23");
        player.sendMessage("");
        player.sendMessage("§7This Week: §a▲ 50 positions");
        player.sendMessage("§7This Month: §a▲ 123 positions");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleStats(Player player) {
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  LEADERBOARD STATISTICS");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        player.sendMessage("§7Total Players: §e1,234");
        player.sendMessage("§7Active This Week: §e567");
        player.sendMessage("§7Active This Month: §e892");
        player.sendMessage("");
        player.sendMessage("§7Total Wealth: §a$§e123,456,789");
        player.sendMessage("§7Average Balance: §a$§e100,246");
        player.sendMessage("§7Median Balance: §a$§e54,320");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleCompare(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /leaderboard compare <player1> <player2>");
            return;
        }
        
        String player1 = args[1];
        String player2 = args.length > 2 ? args[2] : player.getName();
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  COMPARE - " + player1 + " vs " + player2);
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        player.sendMessage("§7Economy Balance:");
        player.sendMessage("  §6" + player1 + " §7- §a$10,000");
        player.sendMessage("  §6" + player2 + " §7- §a$5,000");
        player.sendMessage("");
        player.sendMessage("§7Achievements:");
        player.sendMessage("  §6" + player1 + " §7- §e12/16");
        player.sendMessage("  §6" + player2 + " §7- §e8/16");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void handleFilter(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /leaderboard filter <rank|achievement|level> [filter]");
            return;
        }
        
        String filterType = args[1];
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  FILTERED RANKINGS - " + filterType.toUpperCase());
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        player.sendMessage("");
        player.sendMessage("§e1. §6FilteredPlayer1");
        player.sendMessage("§e2. §6FilteredPlayer2");
        player.sendMessage("§e3. §6FilteredPlayer3");
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║  LEADERBOARD COMMANDS");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/lb top [category] [limit] §6- Top rankings");
        player.sendMessage("§7/lb weekly §6- Weekly rankings");
        player.sendMessage("§7/lb monthly §6- Monthly rankings");
        player.sendMessage("§7/lb alltime §6- All-time rankings");
        player.sendMessage("§7/lb rank [player] §6- Player rank details");
        player.sendMessage("§7/lb stats §6- Global statistics");
        player.sendMessage("§7/lb compare <p1> [p2] §6- Compare players");
        player.sendMessage("§7/lb filter <type> [value] §6- Filtered rankings");
        player.sendMessage("");
    }
}
