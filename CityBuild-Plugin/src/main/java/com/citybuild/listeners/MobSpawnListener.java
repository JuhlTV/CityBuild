package com.citybuild.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {
    private final World plotWorld;

    public MobSpawnListener(World plotWorld) {
        this.plotWorld = plotWorld;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        // Cancel all mob spawns in the plot world (except players)
        if (event.getLocation().getWorld() == plotWorld) {
            // Allow only certain spawn reasons (like player spawning eggs)
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM &&
                event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG &&
                event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.COMMAND) {
                event.setCancelled(true);
            }
        }
    }
}
