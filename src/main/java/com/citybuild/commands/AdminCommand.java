package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.features.plots.PlotManager;
import com.citybuild.features.terrain.TerrainManager;

/**
 * AdminCommand - Admin commands for managing plots, farm resets, and game stats
 */
public class AdminCommand implements CommandExecutor {
    private final AdminManager adminManager;
    private final PlotManager plotManager;
    private final TerrainManager terrainManager;

    public AdminCommand(AdminManager adminManager, PlotManager plotManager, TerrainManager terrainManager) {
        this.adminManager = adminManager;
        this.plotManager = plotManager;
        this.terrainManager = terrainManager;
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
                player.sendMessage(plotManager.getStats());
                player.sendMessage(terrainManager.getResetStatus());
                return true;
                
            // Plot merging
            case "merge":
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /admin merge <plot1> <plot2>");
                    return false;
                }
                plotManager.mergePlots(player, args[1], args[2]);
                return true;
                
            case "unmerge":
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /admin unmerge <plot1> <plot2>");
                    return false;
                }
                plotManager.unmergePlots(player, args[1], args[2]);
                return true;
                
            // Farm world reset
            case "farmreset":
                player.sendMessage("§e⚠ Initiating farm world reset...");
                terrainManager.resetFarmWorld();
                return true;
                
            case "farmstatus":
                player.sendMessage(terrainManager.getResetStatus());
                return true;
                
            default:
                adminManager.showHelp(player);
                return false;
        }
    }
}
