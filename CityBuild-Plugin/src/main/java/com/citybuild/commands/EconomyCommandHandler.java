package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.TransactionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler für alle Economy/Geld-bezogenen Befehle
 * Verwaltet: balance, pay, add, remove, set, transfer
 */
public class EconomyCommandHandler {
    private final CityBuildPlugin plugin;
    private final EconomyManager economy;
    private final TransactionManager transactions;
    
    // Cooldowns für Pay-Befehl (verhindert Spam)
    private final Map<UUID, Long> payCooldowns = new HashMap<>();
    private static final long PAY_COOLDOWN_MS = 500; // 0.5 Sekunden

    public EconomyCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
        this.transactions = plugin.getTransactionManager();
    }

    /**
     * /citybuild economy <subcommand> [args...]
     * Subcommands:
     * - balance [player]
     * - pay <player> <amount>
     * - add <player> <amount> (Admin)
     * - remove <player> <amount> (Admin)
     * - set <player> <amount> (Admin)
     * - transfer <from> <to> <amount> (Admin)
     */
    public boolean handleEconomyCommand(Player player, String[] args) {
        if (args.length < 2) {
            showEconomyHelp(player);
            return true;
        }

        String subcommand = args[1].toLowerCase();

        switch (subcommand) {
            case "balance":
                return handleBalance(player, args);
            case "pay":
                return handlePay(player, args);
            case "add":
                return handleAdd(player, args);
            case "remove":
                return handleRemove(player, args);
            case "set":
                return handleSet(player, args);
            case "transfer":
                return handleTransfer(player, args);
            case "top":
            case "leaderboard":
                return handleLeaderboard(player);
            case "help":
            default:
                showEconomyHelp(player);
                return true;
        }
    }

    /**
     * /citybuild economy balance [player]
     * Zeigt den Balance des Spielers (oder eines anderen)
     */
    private boolean handleBalance(Player player, String[] args) {
        Player target = player;
        
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                player.sendMessage(formatError("Spieler nicht gefunden!"));
                return true;
            }
        }

        long balance = economy.getBalance(target);
        String targetName = target.equals(player) ? "Dein" : target.getName() + "s";
        
        player.sendMessage(Component.empty()
                .append(Component.text("═══════════════════════════════════", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("  💰 ", NamedTextColor.GOLD))
                .append(Component.text(targetName + " Guthaben", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("  "))
                .append(Component.text("$" + formatNumber(balance), NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("═══════════════════════════════════", NamedTextColor.BLUE))
        );

        return true;
    }

    /**
     * /citybuild economy pay <player> <amount>
     * Überweis Geld an einen anderen Spieler
     */
    private boolean handlePay(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(formatError("Use: /citybuild economy pay <player> <amount>"));
            return true;
        }

        // Cooldown-Check
        UUID playerUuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (payCooldowns.containsKey(playerUuid)) {
            long lastUse = payCooldowns.get(playerUuid);
            if (now - lastUse < PAY_COOLDOWN_MS) {
                player.sendMessage(formatError("⏳ Bitte warte einen Moment!"));
                return true;
            }
        }
        payCooldowns.put(playerUuid, now);

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(formatError("Spieler " + args[2] + " nicht online!"));
            return true;
        }

        if (player.equals(target)) {
            player.sendMessage(formatError("Du kannst dir selbst kein Geld schicken!"));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(formatError("Ungültiger Betrag: " + args[3]));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(formatError("Der Betrag muss größer als 0 sein!"));
            return true;
        }

        long playerBalance = economy.getBalance(player);
        if (playerBalance < amount) {
            player.sendMessage(formatError("❌ Du hast nicht genug Geld! (Benötigt: $" + amount + ", hast: $" + playerBalance + ")"));
            return true;
        }

        // Transfer durchführen
        economy.removeBalance(player.getUniqueId(), amount);
        economy.addBalance(target.getUniqueId(), amount);
        
        // Transaktionen loggen
        transactions.logTransaction(player.getUniqueId(), TransactionManager.TransactionType.TRANSFER, amount, 
                "Transfer zu " + target.getName(), target.getUniqueId().toString());
        transactions.logTransaction(target.getUniqueId(), TransactionManager.TransactionType.TRANSFER, amount, 
                "Transfer von " + player.getName(), player.getUniqueId().toString());

        // Bestätigung
        player.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text("$" + formatNumber(amount) + " an ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" überwies", NamedTextColor.GREEN))
        );

        target.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text("Du hast $" + formatNumber(amount) + " von ", NamedTextColor.GREEN))
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" erhalten", NamedTextColor.GREEN))
        );

        return true;
    }

    /**
     * /citybuild economy add <player> <amount>
     * Admin-Command zum Geld hinzufügen
     */
    private boolean handleAdd(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(formatError("❌ Du hast keine Berechtigung!"));
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(formatError("Use: /citybuild economy add <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(formatError("Spieler nicht gefunden!"));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(formatError("Ungültiger Betrag!"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(formatError("Der Betrag muss größer als 0 sein!"));
            return true;
        }

        economy.addBalance(target.getUniqueId(), amount);
        transactions.logTransaction(target.getUniqueId(), TransactionManager.TransactionType.ADMIN_ADJUSTMENT, amount,
                "Admin hinzugefügt von " + player.getName(), player.getUniqueId().toString());

        player.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text("$" + formatNumber(amount) + " zu ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" hinzugefügt", NamedTextColor.GREEN))
        );

        target.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text("Admin hat dir $" + formatNumber(amount) + " gegeben", NamedTextColor.GREEN))
        );

        return true;
    }

    /**
     * /citybuild economy remove <player> <amount>
     * Admin-Command zum Geld entfernen
     */
    private boolean handleRemove(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(formatError("❌ Du hast keine Berechtigung!"));
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(formatError("Use: /citybuild economy remove <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(formatError("Spieler nicht gefunden!"));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(formatError("Ungültiger Betrag!"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(formatError("Der Betrag muss größer als 0 sein!"));
            return true;
        }

        economy.removeBalance(target.getUniqueId(), amount);
        transactions.logTransaction(target.getUniqueId(), TransactionManager.TransactionType.ADMIN_ADJUSTMENT, -amount,
                "Admin entfernt von " + player.getName(), player.getUniqueId().toString());

        player.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text("$" + formatNumber(amount) + " von ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" entfernt", NamedTextColor.GREEN))
        );

        target.sendMessage(Component.empty()
                .append(Component.text("⚠ ", NamedTextColor.YELLOW))
                .append(Component.text("Admin hat dir $" + formatNumber(amount) + " entfernt", NamedTextColor.YELLOW))
        );

        return true;
    }

    /**
     * /citybuild economy set <player> <amount>
     * Admin-Command zum Geld setzen
     */
    private boolean handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(formatError("❌ Du hast keine Berechtigung!"));
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(formatError("Use: /citybuild economy set <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(formatError("Spieler nicht gefunden!"));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(formatError("Ungültiger Betrag!"));
            return true;
        }

        if (amount < 0) {
            player.sendMessage(formatError("Der Betrag kann nicht negativ sein!"));
            return true;
        }

        long oldBalance = economy.getBalance(target);
        economy.setBalance(target.getUniqueId(), amount);
        transactions.logTransaction(target.getUniqueId(), TransactionManager.TransactionType.ADMIN_ADJUSTMENT, amount - oldBalance,
                "Admin setzte Balance auf $" + amount, player.getUniqueId().toString());

        player.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text(target.getName() + "s Balance auf $", NamedTextColor.GREEN))
                .append(Component.text(formatNumber(amount), NamedTextColor.YELLOW))
                .append(Component.text(" gesetzt", NamedTextColor.GREEN))
        );

        target.sendMessage(Component.empty()
                .append(Component.text("⚠ ", NamedTextColor.YELLOW))
                .append(Component.text("Dein Balance wurde auf $" + formatNumber(amount) + " gesetzt", NamedTextColor.YELLOW))
        );

        return true;
    }

    /**
     * /citybuild economy transfer <from> <to> <amount>
     * Admin-Command zum Geld übertragen (ohne dass Spieler online sein muss)
     */
    private boolean handleTransfer(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(formatError("❌ Du hast keine Berechtigung!"));
            return true;
        }

        if (args.length < 5) {
            player.sendMessage(formatError("Use: /citybuild economy transfer <from> <to> <amount>"));
            return true;
        }

        // Spieler müssen online sein für diesen Befehl
        Player from = Bukkit.getPlayer(args[2]);
        Player to = Bukkit.getPlayer(args[3]);

        if (from == null || to == null) {
            player.sendMessage(formatError("Beide Spieler müssen online sein!"));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[4]);
        } catch (NumberFormatException e) {
            player.sendMessage(formatError("Ungültiger Betrag!"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(formatError("Der Betrag muss größer als 0 sein!"));
            return true;
        }

        if (economy.getBalance(from) < amount) {
            player.sendMessage(formatError(from.getName() + " hat nicht genug Geld!"));
            return true;
        }

        economy.removeBalance(from.getUniqueId(), amount);
        economy.addBalance(to.getUniqueId(), amount);
        transactions.logTransaction(from.getUniqueId(), TransactionManager.TransactionType.TRANSFER, -amount,
                "Admin Transfer zu " + to.getName(), player.getUniqueId().toString());
        transactions.logTransaction(to.getUniqueId(), TransactionManager.TransactionType.TRANSFER, amount,
                "Admin Transfer von " + from.getName(), player.getUniqueId().toString());

        player.sendMessage(Component.empty()
                .append(Component.text("✓ ", NamedTextColor.GREEN))
                .append(Component.text("$" + formatNumber(amount) + " von ", NamedTextColor.GREEN))
                .append(Component.text(from.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" zu ", NamedTextColor.GREEN))
                .append(Component.text(to.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" übertragen", NamedTextColor.GREEN))
        );

        return true;
    }

    /**
     * /citybuild economy top / leaderboard
     * Top 10 reichste Spieler
     */
    private boolean handleLeaderboard(Player player) {
        var leaderboard = economy.getLeaderboard(10);
        
        player.sendMessage(Component.empty()
                .append(Component.text("═══════════════════════════════════", NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("  🏆 TOP 10 REICHSTE SPIELER", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("═══════════════════════════════════", NamedTextColor.GOLD))
        );

        int position = 1;
        for (var entry : leaderboard.entrySet()) {
            String medal = position == 1 ? "🥇" : position == 2 ? "🥈" : position == 3 ? "🥉" : "  ";
            player.sendMessage(Component.empty()
                    .append(Component.text(medal + " ", NamedTextColor.YELLOW))
                    .append(Component.text(position + ". ", NamedTextColor.GRAY))
                    .append(Component.text(entry.getKey(), NamedTextColor.WHITE))
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text("$" + formatNumber(entry.getValue()), NamedTextColor.GREEN))
            );
            position++;
        }

        player.sendMessage(Component.text("═══════════════════════════════════", NamedTextColor.GOLD));

        return true;
    }

    /**
     * Zeigt die Economy-Hilfe an
     */
    private void showEconomyHelp(Player player) {
        player.sendMessage(Component.empty()
                .append(Component.text("═══════════════════════════════════════════", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("  💰 ECONOMY BEFEHLE", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("═══════════════════════════════════════════", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy balance [player]", NamedTextColor.GRAY))
                .append(Component.text(" - Zeige Guthaben", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy pay <player> <amount>", NamedTextColor.GRAY))
                .append(Component.text(" - Überweis Geld", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy top", NamedTextColor.GRAY))
                .append(Component.text(" - Top 10 reichste Spieler", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("═══════════════════════════════════════════", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("  👤 ADMIN BEFEHLE:", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy add <player> <amount>", NamedTextColor.GRAY))
                .append(Component.text(" - Geld hinzufügen", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy remove <player> <amount>", NamedTextColor.GRAY))
                .append(Component.text(" - Geld entfernen", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy set <player> <amount>", NamedTextColor.GRAY))
                .append(Component.text(" - Geld setzen", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /citybuild economy transfer <from> <to> <amount>", NamedTextColor.GRAY))
                .append(Component.text(" - Geld übertragen", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("═══════════════════════════════════════════", NamedTextColor.BLUE))
        );
    }

    /**
     * Formatiere Fehler-Nachricht
     */
    private Component formatError(String message) {
        return Component.text("❌ ", NamedTextColor.RED)
                .append(Component.text(message, NamedTextColor.RED));
    }

    /**
     * Formatiere große Zahlen mit Tausenderpunkten
     */
    private String formatNumber(long number) {
        return String.format("%,d", number).replace(",", ".");
    }
}
