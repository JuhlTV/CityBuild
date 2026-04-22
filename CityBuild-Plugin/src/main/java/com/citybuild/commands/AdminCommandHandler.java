package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.AdminManager;
import com.citybuild.gui.GUIManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.UUID;

public class AdminCommandHandler {
    private final CityBuildPlugin plugin;
    private final AdminManager adminManager;

    public AdminCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.adminManager = plugin.getAdminManager();
    }

    public boolean handleAdminCommand(Player player, String[] args) {
        String uuid = player.getUniqueId().toString();

        // Check if admin (level 2+)
        if (!adminManager.hasPermission(uuid, AdminManager.Role.MODERATOR)) {
            player.sendMessage(Component.text("❌ Du hast keine Admin-Rechte!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            showAdminHelp(player);
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "panel":
                openAdminPanel(player);
                break;
            case "role":
                handleRoleCommand(player, args);
                break;
            case "warn":
                handleWarnCommand(player, args);
                break;
            case "warnings":
                handleWarningsCommand(player, args);
                break;
            case "mute":
                handleMuteCommand(player, args);
                break;
            case "unmute":
                handleUnmuteCommand(player, args);
                break;
            case "kick":
                handleKickCommand(player, args);
                break;
            case "logs":
                handleLogsCommand(player, args);
                break;
            case "admin-list":
                showAdminList(player);
                break;
            case "help":
                showAdminHelp(player);
                break;
            default:
                player.sendMessage(Component.text("Unknown admin command!", NamedTextColor.RED));
        }

        return true;
    }

    private void handleRoleCommand(Player player, String[] args) {
        String playerUuid = player.getUniqueId().toString();

        if (args.length < 4) {
            player.sendMessage(Component.text("Usage: /citybuild admin role <player> <role>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        String targetUuid = target.getUniqueId().toString();

        // Check if can manage (OPs can always manage, or you can manage yourself)
        if (!player.isOp() && !adminManager.canManage(playerUuid, targetUuid)) {
            player.sendMessage(Component.text("❌ Du kannst diese Person nicht verwalten!", NamedTextColor.RED));
            return;
        }

        try {
            AdminManager.Role role = AdminManager.Role.valueOf(args[3].toUpperCase());
            adminManager.setRole(targetUuid, role);
            player.sendMessage(Component.text(
                "✓ " + target.getName() + " ist jetzt " + role.emoji + " " + role.displayName,
                NamedTextColor.GREEN
            ));
            target.sendMessage(Component.text(
                "👑 Du bist jetzt " + role.emoji + " " + role.displayName + "!",
                NamedTextColor.GOLD
            ));
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("❌ Ungültige Rolle!", NamedTextColor.RED));
        }
    }

    private void handleWarnCommand(Player player, String[] args) {
        String playerUuid = player.getUniqueId().toString();

        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /citybuild admin warn <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        String targetUuid = target.getUniqueId().toString();

        if (!player.isOp() && !adminManager.canManage(playerUuid, targetUuid)) {
            player.sendMessage(Component.text("❌ Du kannst diese Person nicht verwalten!", NamedTextColor.RED));
            return;
        }

        adminManager.addWarning(targetUuid);
        long warnings = adminManager.getWarnings(targetUuid);

        player.sendMessage(Component.text(
            "⚠️ " + target.getName() + " wurde verwarnt! (" + warnings + "/3)",
            NamedTextColor.YELLOW
        ));
        target.sendMessage(Component.text(
            "⚠️ Du wurdest verwarnt! (" + warnings + "/3)",
            NamedTextColor.YELLOW
        ));

        if (adminManager.shouldBeBanned(targetUuid)) {
            Bukkit.broadcast(Component.text(
                "🚫 " + target.getName() + " wurde automatisch entfernt (3 Verwarnungen)!",
                NamedTextColor.RED
            ));
            target.kickPlayer("Du hast zu viele Verwarnungen erhalten!");
            adminManager.resetWarnings(targetUuid);
        }
    }

    private void handleWarningsCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /citybuild admin warnings <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        long warnings = adminManager.getWarnings(target.getUniqueId().toString());
        player.sendMessage(Component.text(
            target.getName() + " hat " + warnings + "/3 Verwarnungen",
            NamedTextColor.YELLOW
        ));
    }

    private void handleMuteCommand(Player player, String[] args) {
        String playerUuid = player.getUniqueId().toString();

        if (args.length < 4) {
            player.sendMessage(Component.text("Usage: /citybuild admin mute <player> <minutes>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        String targetUuid = target.getUniqueId().toString();

        if (!player.isOp() && !adminManager.canManage(playerUuid, targetUuid)) {
            player.sendMessage(Component.text("❌ Du kannst diese Person nicht verwalten!", NamedTextColor.RED));
            return;
        }

        try {
            int minutes = Integer.parseInt(args[3]);
            adminManager.mute(targetUuid, (long) minutes * 60 * 1000);
            player.sendMessage(Component.text(
                "🤐 " + target.getName() + " ist für " + minutes + " Minuten gemutet!",
                NamedTextColor.GREEN
            ));
            target.sendMessage(Component.text(
                "🤐 Du wurdest für " + minutes + " Minuten gemutet!",
                NamedTextColor.RED
            ));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("❌ Ungültige Minute!", NamedTextColor.RED));
        }
    }

    private void handleUnmuteCommand(Player player, String[] args) {
        String playerUuid = player.getUniqueId().toString();

        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /citybuild admin unmute <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        String targetUuid = target.getUniqueId().toString();

        if (!player.isOp() && !adminManager.canManage(playerUuid, targetUuid)) {
            player.sendMessage(Component.text("❌ Du kannst diese Person nicht verwalten!", NamedTextColor.RED));
            return;
        }

        adminManager.unmute(targetUuid);
        player.sendMessage(Component.text(
            "✓ " + target.getName() + " ist nicht mehr gemutet!",
            NamedTextColor.GREEN
        ));
        target.sendMessage(Component.text(
            "✓ Du darfst wieder chatten!",
            NamedTextColor.GREEN
        ));
    }

    private void handleKickCommand(Player player, String[] args) {
        String playerUuid = player.getUniqueId().toString();

        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /citybuild admin kick <player> [reason]", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        String targetUuid = target.getUniqueId().toString();

        if (!player.isOp() && !adminManager.canManage(playerUuid, targetUuid)) {
            player.sendMessage(Component.text("❌ Du kannst diese Person nicht verwalten!", NamedTextColor.RED));
            return;
        }

        String reason = args.length > 3 ? args[3] : "Kicked by Admin";
        adminManager.logAction(playerUuid, "Kicked " + target.getName() + " (" + reason + ")");
        target.kickPlayer("🚫 Du wurdest gekickt! Grund: " + reason);
        player.sendMessage(Component.text("✓ " + target.getName() + " wurde gekickt!", NamedTextColor.GREEN));
    }

    private void handleLogsCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /citybuild admin logs <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            player.sendMessage(Component.text("❌ Spieler nicht gefunden!", NamedTextColor.RED));
            return;
        }

        var logs = adminManager.getActionLog(target.getUniqueId().toString());
        player.sendMessage(Component.text(
            "=== Admin Logs für " + target.getName() + " ===",
            NamedTextColor.GOLD
        ).decorate(TextDecoration.BOLD));

        if (logs.isEmpty()) {
            player.sendMessage(Component.text("Keine Logs vorhanden", NamedTextColor.GRAY));
            return;
        }

        logs.stream().skip(Math.max(0, logs.size() - 10)).forEach(log ->
            player.sendMessage(Component.text(log, NamedTextColor.YELLOW))
        );
    }

    private void showAdminList(Player player) {
        player.sendMessage(Component.text("=== Admin & Moderators ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

        var allData = adminManager.getAllAdminData();
        allData.values().stream()
            .filter(data -> data.role.level >= 2)
            .forEach(data -> {
                String name = Bukkit.getOfflinePlayer(UUID.fromString(data.uuid)).getName();
                player.sendMessage(Component.text(
                    data.role.emoji + " " + name + " - " + data.role.displayName,
                    NamedTextColor.YELLOW
                ));
            });
    }

    private void openAdminPanel(Player player) {
        String playerUuid = player.getUniqueId().toString();

        Inventory admin = Bukkit.createInventory(null, 45, "🔴 ADMIN PANEL");

        // Role management
        admin.setItem(0, GUIManager.createItem(Material.PLAYER_HEAD, "👑 Role Management", NamedTextColor.GOLD));
        admin.setItem(1, GUIManager.createItem(Material.BOOK, "📋 Admin List", NamedTextColor.YELLOW));
        admin.setItem(2, GUIManager.createItem(Material.REDSTONE, "⚠️ Warn Player", NamedTextColor.YELLOW));

        // Punishment
        admin.setItem(9, GUIManager.createItem(Material.SLIME_BALL, "🤐 Mute Player", NamedTextColor.RED));
        admin.setItem(10, GUIManager.createItem(Material.HONEY_BLOCK, "💊 Unmute Player", NamedTextColor.GREEN));
        admin.setItem(11, GUIManager.createItem(Material.BARRIER, "🚫 Kick Player", NamedTextColor.RED));

        // Logs
        admin.setItem(18, GUIManager.createItem(Material.LECTERN, "📖 View Logs", NamedTextColor.AQUA));
        admin.setItem(19, GUIManager.createItem(Material.OAK_SIGN, "✍️ My Actions", NamedTextColor.YELLOW));

        // Server stats
        admin.setItem(36, GUIManager.createItem(Material.GRASS_BLOCK, "👥 Online Players: " + Bukkit.getOnlinePlayers().size(), NamedTextColor.GREEN));
        admin.setItem(44, GUIManager.createItem(Material.REDSTONE_BLOCK, "← Back", NamedTextColor.RED));

        player.openInventory(admin);
    }

    private void showAdminHelp(Player player) {
        player.sendMessage(Component.text("=== Admin Commands (v2.2.0) ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        player.sendMessage(Component.text("/citybuild admin panel - Open Admin Panel", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin role <player> <role> - Change Role", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin warn <player> - Warn Player (3 = kick)", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin warnings <player> - Check Warnings", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin mute <player> <minutes> - Mute Player", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin unmute <player> - Unmute Player", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin kick <player> [reason] - Kick Player", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin logs <player> - View Player Logs", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/citybuild admin admin-list - Show All Admins", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Roles: OWNER(👑) | ADMIN(🔴) | MODERATOR(🟡) | MEMBER(🟢)", NamedTextColor.AQUA));
    }
}
