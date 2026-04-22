package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ConfigCommandHandler {
    private final CityBuildPlugin plugin;
    private final ConfigManager configManager;

    public ConfigCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public boolean handleConfigCommand(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(Component.text("❌ Du brauchst OP-Rechte!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            showConfigHelp(player);
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "get":
                return handleGetConfig(player, args);
            case "set":
                return handleSetConfig(player, args);
            case "list":
                return handleListConfig(player);
            case "reload":
                return handleReloadConfig(player);
            case "help":
                showConfigHelp(player);
                return true;
            default:
                player.sendMessage(Component.text("❌ Unbekannter Config-Command!", NamedTextColor.RED));
                return true;
        }
    }

    private boolean handleGetConfig(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /citybuild config get <key>", NamedTextColor.RED));
            return true;
        }

        String key = args[2];
        Object value = configManager.get(key);

        if (value == null) {
            player.sendMessage(Component.text("❌ Einstellung nicht gefunden: " + key, NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text(
            "📋 " + key + " = " + value,
            NamedTextColor.GREEN
        ));
        return true;
    }

    private boolean handleSetConfig(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Component.text("Usage: /citybuild config set <key> <value>", NamedTextColor.RED));
            return true;
        }

        String key = args[2];
        String valueStr = args[3];

        try {
            Object value;
            if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                value = Boolean.parseBoolean(valueStr);
            } else if (valueStr.matches("\\d+")) {
                value = Long.parseLong(valueStr);
            } else if (valueStr.matches("\\d+\\.\\d+")) {
                value = Double.parseDouble(valueStr);
            } else {
                value = valueStr;
            }

            configManager.set(key, value);
            player.sendMessage(Component.text(
                "✓ Einstellung aktualisiert: " + key + " = " + value,
                NamedTextColor.GREEN
            ));

            // Notify admins
            plugin.getServer().getOnlinePlayers().stream()
                .filter(Player::isOp)
                .forEach(admin -> admin.sendMessage(Component.text(
                    "⚙️ " + player.getName() + " änderte Config: " + key,
                    NamedTextColor.YELLOW
                )));
        } catch (Exception e) {
            player.sendMessage(Component.text(
                "❌ Fehler beim Setzen: " + e.getMessage(),
                NamedTextColor.RED
            ));
        }
        return true;
    }

    private boolean handleListConfig(Player player) {
        player.sendMessage(Component.text("=== Aktive Einstellungen ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        
        configManager.getAllSettings().forEach((key, value) -> {
            player.sendMessage(Component.text(
                "• " + key + " = " + value,
                NamedTextColor.YELLOW
            ));
        });
        return true;
    }

    private boolean handleReloadConfig(Player player) {
        try {
            configManager.saveData();
            player.sendMessage(Component.text("✓ Config reloaded!", NamedTextColor.GREEN));
            
            // Notify all ops
            plugin.getServer().getOnlinePlayers().stream()
                .filter(Player::isOp)
                .forEach(admin -> admin.sendMessage(Component.text(
                    "⚙️ Config wurde neu geladen",
                    NamedTextColor.GREEN
                )));
        } catch (Exception e) {
            player.sendMessage(Component.text(
                "❌ Fehler beim Reload: " + e.getMessage(),
                NamedTextColor.RED
            ));
        }
        return true;
    }

    private void showConfigHelp(Player player) {
        player.sendMessage(Component.text("=== Config Manager Commands ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("/citybuild config list - Alle Einstellungen anzeigen", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild config get <key> - Spezifische Einstellung", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild config set <key> <value> - Einstellung ändern", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild config reload - Config neuladen", NamedTextColor.YELLOW));
        
        player.sendMessage(Component.text("--- Beispiele ---", NamedTextColor.AQUA));
        player.sendMessage(Component.text("/citybuild config set economy.starting_balance 15000", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/citybuild config set farm.block_reward 25", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/citybuild config set tax.daily_plot_tax 750", NamedTextColor.GRAY));
    }
}
