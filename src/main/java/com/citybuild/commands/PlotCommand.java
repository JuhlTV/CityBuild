package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.plots.PlotManager;
import com.citybuild.gui.GUIManager;

public class PlotCommand implements CommandExecutor {
    private final PlotManager plotManager;
    private final GUIManager guiManager;

    public PlotCommand(PlotManager plotManager, GUIManager guiManager) {
        this.plotManager = plotManager;
        this.guiManager = guiManager;
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
                plotManager.sellPlot(player);
                return true;
            case "info":
                var plot = plotManager.getPlayerPlot(player);
                if (plot != null) {
                    player.sendMessage("§6" + plot.toString());
                    var plots = plotManager.getPlayerPlots(player);
                    if (plots.size() > 1) {
                        player.sendMessage("§6Merged plots: " + (plots.size() - 1) + " additional");
                    }
                } else {
                    player.sendMessage("§cYou don't own a plot!");
                }
                return true;
            case "list":
                player.sendMessage(plotManager.getStats());
                var allPlots = plotManager.listAllPlots();
                player.sendMessage("§e" + Math.min(5, allPlots.size()) + " of " + allPlots.size() + " plots:");
                allPlots.stream().limit(5).forEach(p -> {
                    var p_obj = plotManager.getPlot(p);
                    player.sendMessage("  " + p_obj.toString());
                });
                return true;
            case "menu":
            case "gui":
                guiManager.openPlotMenu(player);
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
        player.sendMessage("§e/plot menu §7- Open plot GUI menu");
    }
}
