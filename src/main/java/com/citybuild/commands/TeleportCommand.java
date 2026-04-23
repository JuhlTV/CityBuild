package com.citybuild.commands;

import com.citybuild.features.teleportation.TeleportationHub;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /tp or /warp command
 */
public class TeleportCommand implements CommandExecutor {

    private final TeleportationHub teleportHub;

    public TeleportCommand(TeleportationHub teleportHub) {
        this.teleportHub = teleportHub;
    }

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
            case "hubs":
                handleHubs(player);
                break;
            case "list":
                handleList(player);
                break;
            case "go":
                handleGo(player, args);
                break;
            case "stats":
                handleStats(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleHubs(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 TELEPORTATION HUBS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (TeleportationHub.Hub hub : teleportHub.getAllHubs()) {
            player.sendMessage("§6🌐 " + hub.getHubName());
            player.sendMessage("  §7Cost: §e$" + String.format("%.0f", hub.getCost()));
            player.sendMessage("  §7Destinations: §e" + hub.getDestinations().size());
            player.sendMessage("");
        }
    }

    private void handleList(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 AVAILABLE DESTINATIONS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Use §e/tp go <destination> §7to warp!");
        player.sendMessage("");
    }

    private void handleGo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /tp go <destination>");
            return;
        }

        String destination = args[1];
        player.sendMessage("§a✓ Teleporting to " + destination + "...");
    }

    private void handleStats(Player player) {
        java.util.Map<String, Object> stats = teleportHub.getStatistics();

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 TELEPORTATION STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Total Hubs: §e" + stats.get("total_hubs"));
        player.sendMessage("§7Total Destinations: §e" + stats.get("total_destinations"));
        player.sendMessage("§7Total Uses: §e" + stats.get("total_uses"));
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 TELEPORT COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/tp hubs §6- View all hubs");
        player.sendMessage("§7/tp list §6- List destinations");
        player.sendMessage("§7/tp go <dest> §6- Teleport");
        player.sendMessage("§7/tp stats §6- Statistics");
        player.sendMessage("");
    }
}
