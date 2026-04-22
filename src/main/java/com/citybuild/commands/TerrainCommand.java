package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.terrain.TerrainManager;

public class TerrainCommand implements CommandExecutor {
    private final TerrainManager terrainManager;

    public TerrainCommand(TerrainManager terrainManager) {
        this.terrainManager = terrainManager;
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
            case "generate":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /terrain generate <type>");
                    return false;
                }
                terrainManager.generateTerrain(player, args[1]);
                return true;
            case "settings":
                player.sendMessage(terrainManager.getTerrainInfo(player));
                return true;
            default:
                showHelp(player);
                return false;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== Terrain Commands ===");
        player.sendMessage("§e/terrain generate <type> §7- Generate terrain (flat, mountain, island, jungle)");
        player.sendMessage("§e/terrain settings §7- Show terrain settings");
    }
}
