package com.citybuild.commands;

import com.citybuild.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /config command
 */
public class ConfigCommand implements CommandExecutor {

    private final ConfigManager configManager;

    public ConfigCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("citybuild.admin")) {
            sender.sendMessage("§c❌ You don't have permission!");
            return true;
        }

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
            case "view":
                handleView(player);
                break;
            case "reload":
                handleReload(player);
                break;
            case "summary":
                handleSummary(player);
                break;
            case "set":
                handleSet(player, args);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleView(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 CONFIGURATION");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");

        player.sendMessage("§7Economy:");
        player.sendMessage("  §6Starting Balance: $" + configManager.getDouble("economy.starting-balance", 10000));
        player.sendMessage("  §6Trading Fee: " + (configManager.getDouble("economy.trading-fee", 0.05) * 100) + "%");

        player.sendMessage("\n§7Plots:");
        player.sendMessage("  §6Buy Price: $" + configManager.getDouble("plots.buy-price", 5000));
        player.sendMessage("  §6Max Claims: " + configManager.getInt("plots.max-claim", 50));

        player.sendMessage("\n§7Dungeons:");
        player.sendMessage("  §6Enabled: " + configManager.getBoolean("dungeons.enabled", true));
        player.sendMessage("  §6Max Players: " + configManager.getInt("dungeons.max-players", 6));

        player.sendMessage("\n§7Quests:");
        player.sendMessage("  §6Enabled: " + configManager.getBoolean("quests.enabled", true));
        player.sendMessage("  §6Reward Multiplier: " + configManager.getDouble("quests.reward-multiplier", 1.0) + "x");

        player.sendMessage("");
    }

    private void handleReload(Player player) {
        configManager.reloadConfig();
        player.sendMessage("§a✓ Configuration reloaded!");
    }

    private void handleSummary(Player player) {
        player.sendMessage(configManager.getConfigSummary());
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cUsage: /config set <key> <value>");
            return;
        }

        String key = args[1];
        String value = args[2];

        try {
            // Try to parse as double
            if (value.contains(".")) {
                configManager.setValue(key, Double.parseDouble(value));
            }
            // Try to parse as int
            else if (value.matches("\\d+")) {
                configManager.setValue(key, Integer.parseInt(value));
            }
            // Parse as boolean
            else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                configManager.setValue(key, Boolean.parseBoolean(value));
            }
            // Parse as string
            else {
                configManager.setValue(key, value);
            }

            player.sendMessage("§a✓ Configuration updated: §6" + key + " = " + value);
        } catch (Exception e) {
            player.sendMessage("§c❌ Failed to set configuration: " + e.getMessage());
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 CONFIG COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/config view §6- View current config");
        player.sendMessage("§7/config summary §6- Configuration summary");
        player.sendMessage("§7/config reload §6- Reload config.yml");
        player.sendMessage("§7/config set <key> <value> §6- Set configuration");
        player.sendMessage("");
    }
}
