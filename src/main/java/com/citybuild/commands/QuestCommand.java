package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.quests.Quest;
import com.citybuild.features.quests.QuestManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command handler for /quest command
 * Manages quest display and tracking
 */
public class QuestCommand implements CommandExecutor {

    private final CityBuildPlugin plugin;
    private final QuestManager questManager;

    public QuestCommand(CityBuildPlugin plugin, QuestManager questManager) {
        this.plugin = plugin;
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showActiveQuests(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "active":
                showActiveQuests(player);
                break;
            case "completed":
                showCompletedQuests(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "all":
                showAllQuests(player);
                break;
            case "stats":
                showStats(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void showActiveQuests(Player player) {
        List<Quest> activeQuests = questManager.getPlayerActiveQuests(player.getUniqueId());

        if (activeQuests.isEmpty()) {
            player.sendMessage("§c❌ No active quests!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 ACTIVE QUESTS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (Quest quest : activeQuests) {
            int progress = questManager.getQuestProgress(player.getUniqueId(), quest.getQuestId());
            double percentage = (double) progress / quest.getProgressRequired() * 100;

            player.sendMessage("§6" + quest.getName() + " " + quest.getType().getDisplay());
            player.sendMessage("  §7" + quest.getDescription());
            player.sendMessage("  §7Progress: §e" + progress + "§7/§e" + quest.getProgressRequired() +
                " (§e" + String.format("%.1f", percentage) + "%§7)");
            player.sendMessage("");
        }
    }

    private void showCompletedQuests(Player player) {
        List<Quest> completedQuests = questManager.getPlayerCompletedQuests(player.getUniqueId());

        if (completedQuests.isEmpty()) {
            player.sendMessage("§c❌ No completed quests!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§a COMPLETED QUESTS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (Quest quest : completedQuests) {
            player.sendMessage("§a✓ §6" + quest.getName());
            player.sendMessage("  §7Reward: §6$" + String.format("%.0f", quest.getCoinReward()) +
                " + §e" + quest.getAchievementPointReward() + "pts");
            player.sendMessage("");
        }
    }

    private void showAllQuests(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 ALL AVAILABLE QUESTS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        for (Quest quest : questManager.getAllQuests()) {
            String status = questManager.isQuestCompleted(player.getUniqueId(), quest.getQuestId())
                ? "§a✓ DONE" : "§6⏳ ACTIVE";

            player.sendMessage(status + " §6" + quest.getName() + " " + quest.getType().getDisplay());
            player.sendMessage("  §7" + quest.getDescription());
            player.sendMessage("");
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /quest info <quest_id>");
            return;
        }

        Quest quest = questManager.getQuest(args[1]);
        if (quest == null) {
            player.sendMessage("§c❌ Quest not found!");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 QUEST INFORMATION");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(quest.getFormattedDisplay());
        
        if (!quest.isExpired()) {
            if (quest.getType() != Quest.QuestType.PERMANENT) {
                player.sendMessage("§7Time Remaining: §e" + quest.getTimeRemainingHours() + " hours");
            }
        } else {
            player.sendMessage("§cThis quest has expired!");
        }
        player.sendMessage("");
    }

    private void showStats(Player player) {
        var stats = questManager.getStatistics();
        int completed = questManager.getPlayerCompletedQuests(player.getUniqueId()).size();
        int active = questManager.getPlayerActiveQuests(player.getUniqueId()).size();

        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 QUEST STATISTICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7Total Quests: §e" + stats.get("total_quests"));
        player.sendMessage("§7  Daily: §e" + stats.get("daily_quests") +
            " | Weekly: §e" + stats.get("weekly_quests") +
            " | Permanent: §e" + stats.get("permanent_quests"));
        player.sendMessage("");
        player.sendMessage("§7Your Progress:");
        player.sendMessage("§7  Active: §e" + active);
        player.sendMessage("§7  Completed: §a" + completed);
        player.sendMessage("");
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 QUEST COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/quest §6- Show active quests");
        player.sendMessage("§7/quest active §6- Show active quests");
        player.sendMessage("§7/quest completed §6- Show completed quests");
        player.sendMessage("§7/quest all §6- Show all available quests");
        player.sendMessage("§7/quest info <id> §6- Quest details");
        player.sendMessage("§7/quest stats §6- Your quest statistics");
        player.sendMessage("");
    }
}
