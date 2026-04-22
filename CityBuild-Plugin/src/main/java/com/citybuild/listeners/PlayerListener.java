package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
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
    }
}
