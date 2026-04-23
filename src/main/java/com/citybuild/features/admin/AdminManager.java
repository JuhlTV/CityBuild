package com.citybuild.features.admin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.time.LocalDateTime;

public class AdminManager {
    private final Plugin plugin;
    private LocalDateTime lastReload;
    private int totalResets;

    public AdminManager(Plugin plugin) {
        this.plugin = plugin;
        this.lastReload = LocalDateTime.now();
        this.totalResets = 0;
    }

    public void reloadPlugin(Player admin) {
        plugin.getLogger().info("Plugin reload initiated by " + admin.getName());
        admin.sendMessage("§a✓ Plugin reloaded!");
        this.lastReload = LocalDateTime.now();
    }

    public void resetCity(Player admin) {
        if (admin.isOp()) {
            plugin.getLogger().warning("City reset by " + admin.getName());
            admin.sendMessage("§c⚠ City has been reset!");
            totalResets++;
        } else {
            admin.sendMessage("§cYou don't have permission!");
        }
    }

    public String getStats() {
        return String.format("§6Stats: Resets=%d, LastReload=%s", totalResets, lastReload);
    }

    public void showHelp(Player admin) {
        admin.sendMessage("§6=== CityBuild Admin Commands ===");
        admin.sendMessage("§e/admin reload §7- Reload plugin");
        admin.sendMessage("§e/admin reset §7- Reset city");
        admin.sendMessage("§e/admin stats §7- Show statistics");
    }
}
