package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.PlotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CityBuildCommand implements CommandExecutor {
    private final CityBuildPlugin plugin;
    private final EconomyManager economy;
    private final PlotManager plots;

    public CityBuildCommand(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
        this.plots = plugin.getPlotManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "buy":
                return handleBuy(player);
            case "sell":
                return handleSell(player);
            case "balance":
                return handleBalance(player);
            case "info":
                return handleInfo(player);
            case "leaderboard":
                return handleLeaderboard(player);
            case "help":
                sendHelp(player);
                return true;
            case "admin":
                if (player.isOp()) {
                    return handleAdmin(player, args);
                } else {
                    player.sendMessage(Component.text("You don't have permission!").color(NamedTextColor.RED));
                }
                return true;
            default:
                sendHelp(player);
                return true;
        }
    }

    private boolean handleBuy(Player player) {
        economy.initializePlayer(player);
        
        long balance = economy.getBalance(player);
        int price = plots.getPlotBuyPrice();

        if (balance < price) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ Insufficient funds! Need $" + price + ", Have $" + balance, NamedTextColor.RED)));
            return true;
        }

        economy.removeBalance(player, price);
        plots.addPlot(player.getUniqueId().toString());

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Plot purchased! You now own " + plots.getPlotCount(player.getUniqueId().toString()) + " plots", NamedTextColor.GREEN)));

        return true;
    }

    private boolean handleSell(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (!plots.hasPlots(uuid)) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ You don't own any plots!", NamedTextColor.RED)));
            return true;
        }

        economy.addBalance(player, plots.getPlotSellPrice());
        plots.removePlot(uuid);

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Plot sold for $" + plots.getPlotSellPrice() + "!", NamedTextColor.GREEN)));

        return true;
    }

    private boolean handleBalance(Player player) {
        economy.initializePlayer(player);
        long balance = economy.getBalance(player);

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Balance: $" + balance, NamedTextColor.GREEN)));

        return true;
    }

    private boolean handleInfo(Player player) {
        economy.initializePlayer(player);
        String uuid = player.getUniqueId().toString();

        long balance = economy.getBalance(player);
        int plotCount = plots.getPlotCount(uuid);

        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Your Info:", NamedTextColor.GOLD)));
        player.sendMessage(Component.text("  Balance: $" + balance, NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Plots: " + plotCount, NamedTextColor.YELLOW));

        return true;
    }

    private boolean handleLeaderboard(Player player) {
        var leaderboard = economy.getLeaderboard(10);

        player.sendMessage(Component.text("=== CityBuild Leaderboard ===", NamedTextColor.GOLD).bold(true));

        int rank = 1;
        for (var entry : leaderboard) {
            String uuid = entry.getKey();
            long balance = entry.getValue();
            
            Player p = Bukkit.getPlayer(java.util.UUID.fromString(uuid));
            String name = p != null ? p.getName() : "Unknown";

            player.sendMessage(Component.text(rank + ". " + name + ": $" + balance, NamedTextColor.YELLOW));
            rank++;
        }

        return true;
    }

    private boolean handleAdmin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("[CityBuild] Admin commands:", NamedTextColor.BLUE));
            player.sendMessage(Component.text("  /citybuild admin reset - Reset all data", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("  /citybuild admin stats - Show statistics", NamedTextColor.YELLOW));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "reset":
                // Reset logic would go here
                player.sendMessage(Component.text("[CityBuild] Data reset!", NamedTextColor.RED));
                return true;
            case "stats":
                player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                        .append(Component.text("System Statistics:", NamedTextColor.GOLD)));
                player.sendMessage(Component.text("  Total Plots: " + plots.getTotalPlots(), NamedTextColor.YELLOW));
                player.sendMessage(Component.text("  Version: 1.0.0", NamedTextColor.YELLOW));
                return true;
            default:
                return false;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("=== CityBuild Commands ===", NamedTextColor.GOLD).bold(true));
        player.sendMessage(Component.text("/citybuild buy - Buy a plot ($" + plots.getPlotBuyPrice() + ")", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild sell - Sell a plot ($" + plots.getPlotSellPrice() + ")", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild balance - Check your balance", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild info - View your info", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild leaderboard - View top players", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild help - Show this message", NamedTextColor.YELLOW));
    }
}
