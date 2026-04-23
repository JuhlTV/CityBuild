package com.citybuild.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Centralized message system with colors and formatting
 */
public class MessageManager {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static class Message {
        public static final String SUCCESS = "§a✓ ";
        public static final String ERROR = "§c❌ ";
        public static final String WARNING = "§e⚠ ";
        public static final String INFO = "§b ℹ ";
        public static final String PREFIX = "§6[CityBuild] ";
        public static final String SEPARATOR = "§e════════════════════════════════════════";
    }

    /**
     * Send success message
     */
    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(Message.SUCCESS + "§a" + message);
    }

    /**
     * Send error message
     */
    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(Message.ERROR + "§c" + message);
    }

    /**
     * Send warning message
     */
    public static void sendWarning(CommandSender sender, String message) {
        sender.sendMessage(Message.WARNING + "§e" + message);
    }

    /**
     * Send info message
     */
    public static void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(Message.INFO + "§b" + message);
    }

    /**
     * Send title message (player only)
     */
    public static void sendTitle(Player player, String title, String subtitle) {
        player.showTitle(Title.title(
            legacy("§6" + title),
            legacy("§e" + subtitle),
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
        ));
    }

    /**
     * Send action bar message (player only)
     */
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }

    /**
     * Broadcast message
     */
    public static void broadcast(String message) {
        Bukkit.broadcast(legacy(Message.PREFIX + "§e" + message));
    }

    /**
     * Send money message
     */
    public static void sendMoney(CommandSender sender, double amount) {
        sender.sendMessage(Message.SUCCESS + "§6$" + String.format("%.2f", amount));
    }

    /**
     * Send header
     */
    public static void sendHeader(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage(Message.SEPARATOR);
        sender.sendMessage("§6║ §e" + title);
        sender.sendMessage(Message.SEPARATOR);
        sender.sendMessage("");
    }

    /**
     * Send footer
     */
    public static void sendFooter(CommandSender sender) {
        sender.sendMessage("");
    }

    /**
     * Format percentage
     */
    public static String formatPercent(double percent) {
        if (percent >= 100) return "§a100%";
        if (percent >= 75) return "§a" + String.format("%.1f%%", percent);
        if (percent >= 50) return "§e" + String.format("%.1f%%", percent);
        if (percent >= 25) return "§6" + String.format("%.1f%%", percent);
        return "§c" + String.format("%.1f%%", percent);
    }

    /**
     * Format currency
     */
    public static String formatCurrency(double amount) {
        if (amount >= 1000000) return "§6$" + String.format("%.1fM", amount / 1000000);
        if (amount >= 1000) return "§6$" + String.format("%.1fK", amount / 1000);
        return "§6$" + String.format("%.2f", amount);
    }

    /**
     * Format player name with rank
     */
    public static String formatPlayerName(String name, String rank) {
        return rank + " §7" + name;
    }

    private static Component legacy(String text) {
        return LEGACY.deserialize(text);
    }
}
