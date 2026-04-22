package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.AdminManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AdminListener implements Listener {
    private final CityBuildPlugin plugin;
    private final AdminManager adminManager;

    public AdminListener(CityBuildPlugin plugin, AdminManager adminManager) {
        this.plugin = plugin;
        this.adminManager = adminManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        // Check if player is muted
        if (adminManager.isMuted(uuid)) {
            event.setCancelled(true);
            long remainingMs = adminManager.getMuteTimeRemaining(uuid);
            long minutes = remainingMs / 1000 / 60;
            long seconds = (remainingMs / 1000) % 60;
            
            player.sendMessage(Component.text(
                "🤐 Du bist gemutet! (" + minutes + "m " + seconds + "s verbleibend)",
                NamedTextColor.RED
            ));
            return;
        }

        // Add role prefix to chat
        AdminManager.Role role = adminManager.getRole(uuid);
        if (role.level >= 1) {
            String prefix = role.emoji + " " + role.displayName + " ";
            event.setFormat(prefix + "%s: %s");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String command = event.getMessage();

        // Log admin actions
        if (command.toLowerCase().startsWith("/citybuild admin")) {
            adminManager.logAction(uuid, "Command executed: " + command);
        }

        // Prevent muted players from using dangerous commands
        if (adminManager.isMuted(uuid) && (command.toLowerCase().contains("/msg") || 
            command.toLowerCase().contains("/whisper") || 
            command.toLowerCase().contains("/tell"))) {
            event.setCancelled(true);
            player.sendMessage(Component.text("🤐 Du kannst nicht chatten während du gemutet bist!", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        AdminManager.AdminData data = adminManager.getAdminData(uuid);
        AdminManager.Role role = data.role;

        // Show role in join message
        if (role.level >= 1) {
            event.joinMessage(Component.text(
                role.emoji + " " + player.getName() + " (" + role.displayName + ") joined!",
                NamedTextColor.YELLOW
            ));
        }

        // Warn if has warnings
        if (data.warnings > 0) {
            player.sendMessage(Component.text(
                "⚠️ Du hast " + data.warnings + "/3 Verwarnungen!",
                NamedTextColor.YELLOW
            ));
        }

        // Show admin panel if is admin
        if (role.level >= 2) {
            player.sendMessage(Component.text(
                "👑 " + role.emoji + " Willkommen, " + role.displayName + "! Nutze /citybuild admin panel",
                NamedTextColor.GOLD
            ));
        }
    }
}
