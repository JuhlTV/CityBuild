package com.citybuild.commands;

import com.citybuild.features.arenas.MobArena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /arena command
 */
public class ArenaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "list":
                handleList(player);
                break;
            case "enter":
                handleEnter(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "stats":
                handleStats(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleList(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 MOB ARENAS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§a✓ Combat arenas available!");
        player.sendMessage("§7Difficulties: EASY, NORMAL, HARD, EXTREME");
        player.sendMessage("");
    }

    private void handleEnter(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /arena enter <difficulty>");
            return;
        }

        player.sendMessage("§a✓ Entering arena...");
    }

    private void handleLeave(Player player) {
        player.sendMessage("§cYou left the arena!");
    }

    private void handleStats(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 ARENA STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Arenas available for epic battles!");
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 ARENA COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/arena list §6- View all arenas");
        player.sendMessage("§7/arena enter <difficulty> §6- Enter arena");
        player.sendMessage("§7/arena leave §6- Leave arena");
        player.sendMessage("§7/arena stats §6- Statistics");
        player.sendMessage("");
    }
}
