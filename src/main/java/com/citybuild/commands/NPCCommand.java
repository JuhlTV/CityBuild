package com.citybuild.commands;

import com.citybuild.features.npcs.NPCManager;
import com.citybuild.features.npcs.TraderNPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /npc command
 */
public class NPCCommand implements CommandExecutor {

    private final NPCManager npcManager;

    public NPCCommand(NPCManager npcManager) {
        this.npcManager = npcManager;
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
            case "info":
                handleInfo(player, args);
                break;
            case "find":
                handleFind(player, args);
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
        player.sendMessage("§e║§6 AVAILABLE TRADERS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (TraderNPC.TradeType type : TraderNPC.TradeType.values()) {
            player.sendMessage("§6" + type.getDisplay());
            for (TraderNPC npc : npcManager.getNPCsByType(type)) {
                player.sendMessage("  §7- " + npc.getNpcName() + " (§e" + npc.getGoods().size() +
                    "§7 goods, §e" + npc.getServices().size() + "§7 services)");
            }
            player.sendMessage("");
        }

        player.sendMessage("§7Use §e/npc info <name> §7for details!");
        player.sendMessage("");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /npc info <name>");
            return;
        }

        String npcName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        TraderNPC npc = npcManager.getNPCByName(npcName);

        if (npc == null) {
            player.sendMessage("§c❌ NPC not found!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 NPC INFO");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(npc.getFormattedInfo());
        player.sendMessage("");

        if (!npc.getGoods().isEmpty()) {
            player.sendMessage("§6GOODS:");
            for (String item : npc.getGoods().keySet()) {
                player.sendMessage("  §7- " + item + " §e$" + String.format("%.0f", npc.getGoodPrice(item)));
            }
            player.sendMessage("");
        }

        if (!npc.getServices().isEmpty()) {
            player.sendMessage("§6SERVICES:");
            for (String service : npc.getServices().keySet()) {
                player.sendMessage("  §7- " + service + " §e$" + String.format("%.0f", npc.getServicePrice(service)));
            }
            player.sendMessage("");
        }
    }

    private void handleFind(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /npc find <name>");
            return;
        }

        String npcName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        TraderNPC npc = npcManager.getNPCByName(npcName);

        if (npc == null) {
            player.sendMessage("§c❌ NPC not found!");
            return;
        }

        player.sendMessage("§a✓ Found §6" + npc.getNpcName() + "§a!");
        player.sendMessage("§7Use §e/npc info " + npc.getNpcName() + " §7for trading options.");
    }

    private void handleStats(Player player) {
        java.util.Map<String, Object> stats = npcManager.getStatistics();

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 NPC STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Total NPCs: §e" + stats.get("total_npcs"));
        player.sendMessage("§7Total Goods: §e" + stats.get("total_goods"));
        player.sendMessage("§7Total Services: §e" + stats.get("total_services"));
        player.sendMessage("§7Total Value Traded: §6$" + String.format("%.0f", stats.get("total_traded")));
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 NPC COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/npc list §6- List all traders");
        player.sendMessage("§7/npc info <name> §6- Trader information");
        player.sendMessage("§7/npc find <name> §6- Locate trader");
        player.sendMessage("§7/npc stats §6- Statistics");
        player.sendMessage("");
    }
}
