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
        // Block ALL mobs/creatures in plot world (animals, monsters, etc)
        if (event.getLocation().getWorld() == plotWorld) {
            // Only allow spawn eggs (player manually spawning)
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
                event.setCancelled(true);
            }
        }
    }
}
