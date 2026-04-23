package com.citybuild.commands;

import com.citybuild.features.dungeons.Dungeon;
import com.citybuild.features.dungeons.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /dungeon command
 */
public class DungeonCommand implements CommandExecutor {

    private final DungeonManager dungeonManager;

    public DungeonCommand(DungeonManager dungeonManager) {
        this.dungeonManager = dungeonManager;
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
            case "list":
                handleList(player);
                break;
            case "enter":
                handleEnter(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "info":
                handleInfo(player);
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
        player.sendMessage("§e║§6 AVAILABLE DUNGEONS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (Dungeon dungeon : dungeonManager.getAllDungeons()) {
            player.sendMessage("§6⚔ " + dungeon.getDungeonName());
            player.sendMessage("  §7" + dungeon.getDescription());
            player.sendMessage("  §7Players: §e" + dungeon.getActivePlayers() + "§7/§e" + dungeon.getMaxPlayers() +
                " §7| Duration: §e" + dungeon.getDurationMinutes() + "m");
            player.sendMessage("");
        }

        player.sendMessage("§7Use §e/dungeon enter <name> §7to start!");
        player.sendMessage("");
    }

    private void handleEnter(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /dungeon enter <name>");
            return;
        }

        String dungeonName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        boolean found = false;
        for (Dungeon dungeon : dungeonManager.getAllDungeons()) {
            if (dungeon.getDungeonName().equalsIgnoreCase(dungeonName)) {
                if (dungeonManager.enterDungeon(player.getUniqueId(), dungeon.getDungeonId())) {
                    player.sendMessage("");
                    player.sendMessage("§a╔════════════════════════════════════════╗");
                    player.sendMessage("§a║ ⚔️  DUNGEON STARTED");
                    player.sendMessage("§a╚════════════════════════════════════════╝");
                    player.sendMessage("");
                    player.sendMessage(dungeon.getFormattedInfo(Dungeon.Difficulty.NORMAL));
                    player.sendMessage("");
                    player.sendMessage("§7Good luck! Use §e/dungeon leave §7to exit.");
                    player.sendMessage("");
                } else {
                    player.sendMessage("§c❌ Dungeon is full or you're already in one!");
                }
                found = true;
                break;
            }
        }

        if (!found) {
            player.sendMessage("§c❌ Dungeon not found!");
        }
    }

    private void handleLeave(Player player) {
        Dungeon dungeon = dungeonManager.getPlayerDungeon(player.getUniqueId());
        if (dungeon == null) {
            player.sendMessage("§c❌ You're not in a dungeon!");
            return;
        }

        dungeonManager.exitDungeon(player.getUniqueId());
    }

    private void handleInfo(Player player) {
        Dungeon dungeon = dungeonManager.getPlayerDungeon(player.getUniqueId());
        if (dungeon == null) {
            player.sendMessage("§c❌ You're not in a dungeon!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 DUNGEON INFO");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(dungeon.getFormattedInfo(Dungeon.Difficulty.NORMAL));
        player.sendMessage("");
    }

    private void handleStats(Player player) {
        java.util.Map<String, Object> stats = dungeonManager.getStatistics();

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 DUNGEON STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Total Dungeons: §e" + stats.get("total_dungeons"));
        player.sendMessage("§7Active Players: §e" + stats.get("active_players"));
        player.sendMessage("§7Total Clears: §e" + stats.get("total_clears"));
        player.sendMessage("§7Rewards Distributed: §6$" + String.format("%.0f", stats.get("total_rewards")));
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 DUNGEON COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/dungeon list §6- View all dungeons");
        player.sendMessage("§7/dungeon enter <name> §6- Enter dungeon");
        player.sendMessage("§7/dungeon leave §6- Exit dungeon");
        player.sendMessage("§7/dungeon info §6- Dungeon info");
        player.sendMessage("§7/dungeon stats §6- Statistics");
        player.sendMessage("");
    }
}
