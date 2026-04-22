package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final CityBuildPlugin plugin;

    public PlayerListener(CityBuildPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getEconomyManager().initializePlayer(event.getPlayer());
        
        // Welcome message with world info
        event.getPlayer().sendMessage(Component.text("Welcome to CityBuild! 🏗️", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        event.getPlayer().sendMessage(Component.text("Starting balance: $10,000", NamedTextColor.YELLOW));
        event.getPlayer().sendMessage(Component.text("Use ", NamedTextColor.GRAY).append(Component.text("/citybuild help", NamedTextColor.AQUA)).append(Component.text(" for commands", NamedTextColor.GRAY)));
    }
}
