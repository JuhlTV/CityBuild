package com.citybuild.commands;

import com.citybuild.features.events.AdvancementEvent;
import com.citybuild.features.events.AdvancementEventManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /event command
 */
public class EventCommand implements CommandExecutor {

    private final AdvancementEventManager eventManager;

    public EventCommand(AdvancementEventManager eventManager) {
        this.eventManager = eventManager;
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
            case "active":
            case "view":
                handleView(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "stats":
                handleStats(player);
                break;
            case "leaderboard":
            case "lb":
                handleLeaderboard(player, args);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleView(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 ACTIVE ADVANCEMENT EVENTS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        java.util.Collection<AdvancementEvent> events = eventManager.getActiveEvents();
        if (events.isEmpty()) {
            player.sendMessage("§c❌ No active events at the moment!");
        } else {
            for (AdvancementEvent event : events) {
                player.sendMessage(event.getFormattedDisplay());
                player.sendMessage("");
            }
        }

        player.sendMessage("§7Use §e/event info <event_name> §7for details");
        player.sendMessage("");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /event info <event_name>");
            return;
        }

        String eventName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        boolean found = false;
        for (AdvancementEvent event : eventManager.getActiveEvents()) {
            if (event.getEventName().equalsIgnoreCase(eventName)) {
                player.sendMessage("");
                player.sendMessage("§e╔════════════════════════════════════════╗");
                player.sendMessage("§e║§6 EVENT DETAILS");
                player.sendMessage("§e╚════════════════════════════════════════╝");
                player.sendMessage("");
                player.sendMessage(event.getFormattedDisplay());
                player.sendMessage("");
                player.sendMessage("§7You are now participating in this event!");
                player.sendMessage("");
                found = true;
                break;
            }
        }

        if (!found) {
            player.sendMessage("§c❌ Event not found!");
        }
    }

    private void handleStats(Player player) {
        java.util.Map<String, Object> stats = eventManager.getStatistics();

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 EVENT STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Active Events: §e" + stats.get("active_events"));
        player.sendMessage("§7Finished Events: §e" + stats.get("finished_events"));
        player.sendMessage("§7Total Participants: §e" + stats.get("total_participants"));
        player.sendMessage("");
    }

    private void handleLeaderboard(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /event leaderboard <event_name>");
            return;
        }

        String eventName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        boolean found = false;
        for (AdvancementEvent event : eventManager.getActiveEvents()) {
            if (event.getEventName().equalsIgnoreCase(eventName)) {
                player.sendMessage("");
                player.sendMessage("§e╔════════════════════════════════════════╗");
                player.sendMessage("§e║§6 " + event.getEventName().toUpperCase());
                player.sendMessage("§e╚════════════════════════════════════════╝");
                player.sendMessage("");

                java.util.List<java.util.Map.Entry<java.util.UUID, Double>> sorted = event.getParticipantScores()
                        .entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(10)
                        .toList();

                if (sorted.isEmpty()) {
                    player.sendMessage("§c❌ No participants yet!");
                } else {
                    for (int i = 0; i < sorted.size(); i++) {
                        java.util.Map.Entry<java.util.UUID, Double> entry = sorted.get(i);
                        org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(entry.getKey());
                        String name = p != null ? p.getName() : "Unknown";
                        player.sendMessage(String.format("§6%d. §e%-16s §7| §6%.0f pts", i + 1, name, entry.getValue()));
                    }
                }

                player.sendMessage("");
                found = true;
                break;
            }
        }

        if (!found) {
            player.sendMessage("§c❌ Event not found!");
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 EVENT COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/event active §6- View active events");
        player.sendMessage("§7/event view §6- View active events");
        player.sendMessage("§7/event info <name> §6- Event details");
        player.sendMessage("§7/event stats §6- Event statistics");
        player.sendMessage("§7/event leaderboard <name> §6- Event leaderboard");
        player.sendMessage("");
    }
}
