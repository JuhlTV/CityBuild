package com.citybuild.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Utility for sound effects
 */
public class SoundEffects {

    public enum SoundType {
        SUCCESS(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f),
        ERROR(Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f),
        COINS(Sound.ENTITY_ARMOR_STAND_PLACE, 1.0f, 1.5f),
        LEVEL_UP(Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f),
        ACHIEVEMENT(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f),
        QUEST_COMPLETE(Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f),
        TREASURE_FOUND(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f),
        DUNGEON_START(Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f),
        DUNGEON_COMPLETE(Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 1.0f),
        CLICK(Sound.UI_BUTTON_CLICK, 0.7f, 1.0f);

        private final Sound sound;
        private final float volume;
        private final float pitch;

        SoundType(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        public Sound getSound() { return sound; }
        public float getVolume() { return volume; }
        public float getPitch() { return pitch; }
    }

    /**
     * Play sound for player
     */
    public static void play(Player player, SoundType type) {
        player.playSound(player.getLocation(), type.getSound(), type.getVolume(), type.getPitch());
    }

    /**
     * Play sound at location
     */
    public static void playAt(org.bukkit.Location location, SoundType type) {
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, type.getSound(), type.getVolume(), type.getPitch());
        }
    }

    /**
     * Play for all nearby players
     */
    public static void playNearby(org.bukkit.Location location, SoundType type, double radius) {
        if (location.getWorld() == null) return;

        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distance(location) <= radius) {
                play(player, type);
            }
        }
    }
}
