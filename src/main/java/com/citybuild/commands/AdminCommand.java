package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.features.plots.PlotManager;

public class AdminCommand implements CommandExecutor {
    private final AdminManager adminManager;
    private final PlotManager plotManager;

    public AdminCommand(AdminManager adminManager, PlotManager plotManager) {
        this.adminManager = adminManager;
        this.plotManager = plotManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return false;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage("§cYou don't have permission!");
            return false;
        }

        if (args.length == 0) {
            adminManager.showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                adminManager.reloadPlugin(player);
                return true;
            case "reset":
                adminManager.resetCity(player);
                return true;
            case "stats":
                player.sendMessage(adminManager.getStats());
                player.sendMessage("§6Total plots: " + plotManager.getTotalPlots());
                return true;
            default:
                adminManager.showHelp(player);
                return false;
        }
    }
}
