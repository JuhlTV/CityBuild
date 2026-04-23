package com.citybuild.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Utility for particle effects
 */
public class ParticleEffects {

    public enum EffectType {
        SUCCESS(Particle.HAPPY_VILLAGER, 20),
        COINS(Particle.VILLAGER_HAPPY, 15),
        ACHIEVEMENT(Particle.COMPOSTER, 25),
        TREASURE(Particle.ENCHANT, 30),
        LEVEL_UP(Particle.EXPLOSION, 10),
        MAGIC(Particle.SPELL_MOB, 20);

        private final Particle particle;
        private final int count;

        EffectType(Particle particle, int count) {
            this.particle = particle;
            this.count = count;
        }

        public Particle getParticle() { return particle; }
        public int getCount() { return count; }
    }

    /**
     * Show particle effect at location
     */
    public static void show(Location location, EffectType type) {
        if (location.getWorld() == null) return;

        World world = location.getWorld();
        world.spawnParticle(
            type.getParticle(),
            location.getX(),
            location.getY() + 1,
            location.getZ(),
            type.getCount(),
            0.5, 0.5, 0.5,
            0.1
        );
    }

    /**
     * Show particles around player
     */
    public static void showAroundPlayer(Player player, EffectType type) {
        show(player.getLocation(), type);
    }

    /**
     * Animated effect (repeating)
     */
    public static void animateEffect(Location location, EffectType type, int duration, int interval) {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(
            org.bukkit.Bukkit.getPluginManager().getPlugins()[0],
            () -> show(location, type),
            0L,
            interval
        );
    }

    /**
     * Trail effect for movement
     */
    public static void trailEffect(Player player, EffectType type, int duration) {
        long start = System.currentTimeMillis();
        org.bukkit.Bukkit.getScheduler().runTaskTimer(
            org.bukkit.Bukkit.getPluginManager().getPlugins()[0],
            () -> {
                if (System.currentTimeMillis() - start > duration * 1000) {
                    return;
                }
                show(player.getLocation(), type);
            },
            0L,
            5L
        );
    }
}
