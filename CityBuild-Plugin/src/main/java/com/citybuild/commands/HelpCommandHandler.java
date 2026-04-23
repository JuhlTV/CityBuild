package com.citybuild.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

/**
 * Handler für Hilfe-Befehle und Befehlsübersicht
 * Organisiert alle Befehle nach Kategorien
 */
public class HelpCommandHandler {

    /**
     * /citybuild help [category]
     * Kategorien: economy, plot, warp, shop, admin, all
     */
    public static boolean handleHelpCommand(Player player, String[] args) {
        if (args.length < 2) {
            showMainHelp(player);
            return true;
        }

        String category = args[1].toLowerCase();

        switch (category) {
            case "economy":
                showEconomyHelp(player);
                return true;
            case "plot":
                showPlotHelp(player);
                return true;
            case "warp":
                showWarpHelp(player);
                return true;
            case "shop":
                showShopHelp(player);
                return true;
            case "admin":
                showAdminHelp(player);
                return true;
            case "all":
                showAllHelp(player);
                return true;
            default:
                showMainHelp(player);
                return true;
        }
    }

    private static void showMainHelp(Player player) {
        player.sendMessage(Component.empty()
                .append(Component.text("╔═══════════════════════════════════════╗", NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("║  ", NamedTextColor.AQUA))
                .append(Component.text("🎮 CITYBUILD - BEFEHLSÜBERSICHT", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("  ║", NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.text("╚═══════════════════════════════════════╝", NamedTextColor.AQUA))
        );

        player.sendMessage(Component.empty()
                .append(Component.text("📚 Verfügbare Kategorien:", NamedTextColor.GOLD))
        );

        showHelpCategory(player, "💰 ECONOMY", "economy", "Balance, Pay, Transfers");
        showHelpCategory(player, "📍 PLOTS", "plot", "Kauf, Verkauf, Management");
        showHelpCategory(player, "🌍 WARPS", "warp", "Teleportation, Waypoints");
        showHelpCategory(player, "🏪 SHOP", "shop", "Kaufen, Verkaufen, Items");
        showHelpCategory(player, "⚔️ PvP WORLDS", "pvp", "Farming, PvP, Rewards");
        showHelpCategory(player, "🔑 ADMIN", "admin", "Verwaltung, Moderation");

        player.sendMessage(Component.empty()
                .append(Component.text("➤ ", NamedTextColor.AQUA))
                .append(Component.text("/citybuild help <category>", NamedTextColor.GRAY))
                .append(Component.text(" - Detaillierte Hilfe", NamedTextColor.GREEN))
        );

        player.sendMessage(Component.empty()
                .append(Component.text("➤ ", NamedTextColor.AQUA))
                .append(Component.text("/citybuild menu", NamedTextColor.GRAY))
                .append(Component.text(" - Hauptmenü öffnen", NamedTextColor.GREEN))
        );

        player.sendMessage(Component.empty()
                .append(Component.text("╚═══════════════════════════════════════╝", NamedTextColor.AQUA))
        );
    }

    private static void showEconomyHelp(Player player) {
        player.sendMessage(Component.empty()
                .append(Component.text("╔════════════════════════════════════════╗", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("║  ", NamedTextColor.GREEN))
                .append(Component.text("💰 ECONOMY BEFEHLE", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("  ║", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.GREEN))
        );

        showCommand(player, "/cb economy balance [player]", "Zeige dein/sein Guthaben");
        showCommand(player, "/cb economy pay <player> <betrag>", "Überweis Geld an Spieler");
        showCommand(player, "/cb economy top", "Top 10 reichste Spieler");

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("👤 Admin-Befehle:", NamedTextColor.YELLOW, TextDecoration.BOLD));

        showCommand(player, "/cb economy add <player> <betrag>", "Geld hinzufügen");
        showCommand(player, "/cb economy remove <player> <betrag>", "Geld entfernen");
        showCommand(player, "/cb economy set <player> <betrag>", "Guthaben setzen");
        showCommand(player, "/cb economy transfer <from> <to> <betrag>", "Zwischen Spielern übertragen");

        player.sendMessage(Component.empty()
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.GREEN))
        );
    }

    private static void showPlotHelp(Player player) {
        player.sendMessage(Component.empty()
                .append(Component.text("╔════════════════════════════════════════╗", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("║  ", NamedTextColor.BLUE))
                .append(Component.text("📍 PLOT BEFEHLE", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("  ║", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.BLUE))
        );

        showCommand(player, "/cb menu", "Plot-Menü öffnen");
        showCommand(player, "/cb tpplot", "Zur Plot-Welt teleportieren");
        showCommand(player, "/cb addmember <player>", "Spieler zu deinem Plot hinzufügen");
        showCommand(player, "/cb removemember <player>", "Spieler von deinem Plot entfernen");

        player.sendMessage(Component.empty()
                .append(Component.text("📊 Info:", NamedTextColor.GRAY))
        );

        player.sendMessage(Component.text("• Ein Plot kostet $50.000", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Tägliche Steuer: $500 (Premium: $1.000)", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Max. 10 Member pro Plot", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• 16x16 Blöcke pro Plot", NamedTextColor.GRAY));

        player.sendMessage(Component.empty()
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.BLUE))
        );
    }

    private static void showWarpHelp(Player player) {
        player.sendMessage(Component.empty()
                .append(Component.text("╔════════════════════════════════════════╗", NamedTextColor.DARK_AQUA))
                .append(Component.newline())
                .append(Component.text("║  ", NamedTextColor.DARK_AQUA))
                .append(Component.text("🌍 WARP BEFEHLE", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("  ║", NamedTextColor.DARK_AQUA))
                .append(Component.newline())
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.DARK_AQUA))
        );

        showCommand(player, "/cb warps", "Alle Warps anzeigen");
        showCommand(player, "/cb warp <name>", "Zu Warp teleportieren");
        showCommand(player, "/cb setwarp <name>", "Neuen Warp erstellen");
        showCommand(player, "/cb delwarp <name>", "Warp löschen (nur deine)");

        player.sendMessage(Component.empty()
                .append(Component.text("📊 Info:", NamedTextColor.GRAY))
        );

        player.sendMessage(Component.text("• Cooldown: 3 Sekunden zwischen Teleports", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Invincibility: 5 Sekunden nach TP", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Du brauchst kein Geld zum TP", NamedTextColor.GRAY));

        player.sendMessage(Component.empty()
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.DARK_AQUA))
        );
    }

    private static void showShopHelp(Player player) {
        player.sendMessage(Component.empty()
                .append(Component.text("╔════════════════════════════════════════╗", NamedTextColor.LIGHT_PURPLE))
                .append(Component.newline())
                .append(Component.text("║  ", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("🏪 SHOP BEFEHLE", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("  ║", NamedTextColor.LIGHT_PURPLE))
                .append(Component.newline())
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.LIGHT_PURPLE))
        );

        showCommand(player, "/cb shop", "Shop-Menü öffnen");
        showCommand(player, "/cb buy", "Aktuelle Items kaufen");
        showCommand(player, "/cb sell", "In der Hand gehaltenes Item verkaufen");

        player.sendMessage(Component.empty()
                .append(Component.text("📊 Info:", NamedTextColor.GRAY))
        );

        player.sendMessage(Component.text("• 23 verschiedene Items kaufbar", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Automatische Rückerstattung nach Rückkauf", NamedTextColor.GRAY));
        player.sendMessage(Component.text("• Preise sind fair und basieren auf Wert", NamedTextColor.GRAY));

        player.sendMessage(Component.empty()
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.LIGHT_PURPLE))
        );
    }

    private static void showAdminHelp(Player player) {
        if (!player.isOp()) {
            player.sendMessage(Component.text("❌ Du hast keine Berechtigung!", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.empty()
                .append(Component.text("╔════════════════════════════════════════╗", NamedTextColor.DARK_RED))
                .append(Component.newline())
                .append(Component.text("║  ", NamedTextColor.DARK_RED))
                .append(Component.text("⚔️ ADMIN BEFEHLE", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text("  ║", NamedTextColor.DARK_RED))
                .append(Component.newline())
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.DARK_RED))
        );

        player.sendMessage(Component.text("📋 Verwaltung:", NamedTextColor.YELLOW, TextDecoration.BOLD));
        showCommand(player, "/cb admin role <player> <role>", "Rang ändern");
        showCommand(player, "/cb admin warn <player> [reason]", "Verwarnung geben");
        showCommand(player, "/cb admin mute <player> <minutes>", "Spieler stummschalten");
        showCommand(player, "/cb admin unmute <player>", "Stummschaltung aufheben");
        showCommand(player, "/cb admin kick <player> [reason]", "Spieler kicken");

        player.sendMessage(Component.empty()
                .append(Component.text("💰 Geldverwaltung:", NamedTextColor.YELLOW, TextDecoration.BOLD))
        );
        showCommand(player, "/cb economy add <player> <amount>", "Geld hinzufügen");
        showCommand(player, "/cb economy remove <player> <amount>", "Geld entfernen");
        showCommand(player, "/cb economy set <player> <amount>", "Guthaben setzen");

        player.sendMessage(Component.empty()
                .append(Component.text("⚙️ System:", NamedTextColor.YELLOW, TextDecoration.BOLD))
        );
        showCommand(player, "/cb config list", "Konfiguration anzeigen");
        showCommand(player, "/cb config set <key> <value>", "Wert ändern");

        player.sendMessage(Component.empty()
                .append(Component.text("╚════════════════════════════════════════╝", NamedTextColor.DARK_RED))
        );
    }

    private static void showAllHelp(Player player) {
        showMainHelp(player);
        player.sendMessage(Component.empty());
        player.sendMessage(Component.empty());
        showEconomyHelp(player);
        player.sendMessage(Component.empty());
        player.sendMessage(Component.empty());
        showPlotHelp(player);
        player.sendMessage(Component.empty());
        player.sendMessage(Component.empty());
        showWarpHelp(player);
    }

    private static void showHelpCategory(Player player, String icon, String command, String description) {
        player.sendMessage(Component.empty()
                .append(Component.text(icon, NamedTextColor.YELLOW))
                .append(Component.text(" ", NamedTextColor.WHITE))
                .append(Component.text("/citybuild help " + command, NamedTextColor.GRAY))
                .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                .append(Component.text(description, NamedTextColor.GREEN))
        );
    }

    private static void showCommand(Player player, String command, String description) {
        player.sendMessage(Component.empty()
                .append(Component.text("  ➤ ", NamedTextColor.AQUA))
                .append(Component.text(command, NamedTextColor.GRAY))
                .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                .append(Component.text(description, NamedTextColor.GREEN))
        );
    }
}
