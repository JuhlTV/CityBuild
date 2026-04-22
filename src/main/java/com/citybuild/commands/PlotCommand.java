package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.plots.PlotManager;

public class PlotCommand implements CommandExecutor {
    private final PlotManager plotManager;

    public PlotCommand(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "buy":
                plotManager.buyPlot(player, 5000);
                return true;
            case "sell":
                plotManager.sellPlot(player, 4000);
                return true;
            case "info":
                var plot = plotManager.getPlayerPlot(player);
                if (plot != null) {
                    player.sendMessage("§6" + plot.toString());
                } else {
                    player.sendMessage("§cYou don't own a plot!");
                }
                return true;
            case "list":
                var plots = plotManager.listAllPlots();
                player.sendMessage("§6Total plots: " + plots.size());
                plots.forEach(p -> player.sendMessage("§e- " + p));
                return true;
            default:
                showHelp(player);
                return false;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== Plot Commands ===");
        player.sendMessage("§e/plot buy §7- Buy a new plot");
        player.sendMessage("§e/plot sell §7- Sell your plot");
        player.sendMessage("§e/plot info §7- Show your plot info");
        player.sendMessage("§e/plot list §7- List all plots");
    }
}
