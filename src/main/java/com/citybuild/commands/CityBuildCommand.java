package com.citybuild.commands;

import com.citybuild.features.plots.Plot;
import com.citybuild.features.plots.PlotManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /citybuild shortcut commands.
 */
public class CityBuildCommand implements CommandExecutor {

    private final PlotManager plotManager;

    public CityBuildCommand(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "buy":
                plotManager.buyPlot(player, 5000);
                return true;
            case "tpplot":
                plotManager.teleportToPlayerPlot(player);
                return true;
            case "sell":
                plotManager.sellPlot(player);
                return true;
            case "info":
                Plot plot = plotManager.getPlayerPlot(player);
                if (plot == null) {
                    player.sendMessage("§cDu besitzt kein Plot!");
                } else {
                    player.sendMessage("§6" + plot.toString());
                }
                return true;
            default:
                showHelp(player);
                return true;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== CityBuild Commands ===");
        player.sendMessage("§e/citybuild buy §7- Kauft ein Plot");
        player.sendMessage("§e/citybuild tpplot §7- Teleportiert zu deinem Plot");
        player.sendMessage("§e/citybuild sell §7- Verkauft dein Plot");
        player.sendMessage("§e/citybuild info §7- Zeigt Plot-Infos");
    }
}
