package com.citybuild.commands;

import com.citybuild.features.treasures.TreasureChest;
import com.citybuild.features.treasures.TreasureManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /treasure command
 */
public class TreasureCommand implements CommandExecutor {

    private final TreasureManager treasureManager;

    public TreasureCommand(TreasureManager treasureManager) {
        this.treasureManager = treasureManager;
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
            case "search":
                handleSearch(player);
                break;
            case "nearest":
                handleNearest(player);
                break;
            case "stats":
                handleStats(player);
                break;
            case "discoveries":
                handleDiscoveries(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleSearch(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 HIDDEN TREASURES");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        java.util.Collection<TreasureChest> treasures = treasureManager.getHiddenTreasures();
        if (treasures.isEmpty()) {
            player.sendMessage("§c❌ No treasures currently hidden!");
        } else {
            player.sendMessage("§7Found §e" + treasures.size() + "§7 hidden treasures:");
            player.sendMessage("§7Explore the world to find them!");
        }

        player.sendMessage("");
    }

    private void handleNearest(Player player) {
        com.citybuild.features.treasures.TreasureChest nearest = treasureManager.findNearestTreasure(
            player.getLocation(), 1000);

        if (nearest == null) {
            player.sendMessage("§c❌ No treasures nearby!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 NEAREST TREASURE");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(nearest.getFormattedInfo());
        player.sendMessage("");
    }

    private void handleStats(Player player) {
        java.util.Map<String, Object> stats = treasureManager.getStatistics();

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 TREASURE STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Total Treasures: §e" + stats.get("total_treasures"));
        player.sendMessage("§7Hidden: §e" + stats.get("hidden_treasures"));
        player.sendMessage("§7Discovered: §e" + stats.get("discovered_treasures"));
        player.sendMessage("§7Total Value: §6$" + String.format("%.0f", stats.get("total_value")));
        player.sendMessage("");
    }

    private void handleDiscoveries(Player player) {
        int discoveries = treasureManager.getPlayerDiscoveries(player.getUniqueId());

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 YOUR DISCOVERIES");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7You have discovered §e" + discoveries + " §7treasures!");
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 TREASURE COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/treasure search §6- List hidden treasures");
        player.sendMessage("§7/treasure nearest §6- Find nearest treasure");
        player.sendMessage("§7/treasure stats §6- Treasure statistics");
        player.sendMessage("§7/treasure discoveries §6- Your discoveries");
        player.sendMessage("");
    }
}
