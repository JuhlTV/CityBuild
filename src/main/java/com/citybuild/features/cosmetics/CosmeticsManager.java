package com.citybuild.features.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all player cosmetics
 */
public class CosmeticsManager {

    private final Map<UUID, PlayerCosmetics> playerCosmetics = new HashMap<>();

    /**
     * Get or create player cosmetics
     */
    public PlayerCosmetics getPlayerCosmetics(UUID playerUUID) {
        return playerCosmetics.computeIfAbsent(playerUUID, PlayerCosmetics::new);
    }

    /**
     * Set player title
     */
    public void setPlayerTitle(UUID playerUUID, String title) {
        PlayerCosmetics cosmetics = getPlayerCosmetics(playerUUID);
        cosmetics.setTitle(title);
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage("§a✓ Title set to §6" + title);
        }
    }

    /**
     * Set player name color
     */
    public void setPlayerNameColor(UUID playerUUID, String colorCode) {
        PlayerCosmetics cosmetics = getPlayerCosmetics(playerUUID);
        cosmetics.setNameColor(colorCode);
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage("§a✓ Name color changed!");
        }
    }

    /**
     * Toggle rainbow name effect
     */
    public void toggleRainbowName(UUID playerUUID) {
        PlayerCosmetics cosmetics = getPlayerCosmetics(playerUUID);
        boolean newState = !cosmetics.hasRainbowName();
        cosmetics.setRainbowName(newState);
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            String state = newState ? "§aENABLED" : "§cDISABLED";
            player.sendMessage("§6Rainbow Name: " + state);
        }
    }

    /**
     * Toggle particle effects
     */
    public void toggleParticleEffects(UUID playerUUID) {
        PlayerCosmetics cosmetics = getPlayerCosmetics(playerUUID);
        boolean newState = !cosmetics.hasParticleEffects();
        cosmetics.setParticleEffects(newState);
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            String state = newState ? "§aENABLED" : "§cDISABLED";
            player.sendMessage("§6Particle Effects: " + state);
        }
    }

    /**
     * Add prestige level
     */
    public void addPrestige(UUID playerUUID) {
        PlayerCosmetics cosmetics = getPlayerCosmetics(playerUUID);
        cosmetics.addPrestige();
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendTitle(
                "§6⭐ PRESTIGE UP! ⭐",
                "§eLv. " + cosmetics.getPrestigeLevel(),
                10, 60, 10
            );
            
            // Broadcast prestige achievement
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage("§d⭐ " + player.getName() + " §dreached prestige level §d" + 
                    cosmetics.getPrestigeLevel() + "!");
            });
        }
    }

    /**
     * Get formatted cosmetics info
     */
    public String getCosmeticsInfo(UUID playerUUID) {
        PlayerCosmetics cosmetics = getPlayerCosmetics(playerUUID);
        return cosmetics.getFormattedInfo();
    }

    /**
     * Reset all cosmetics for player
     */
    public void resetCosmetics(UUID playerUUID) {
        playerCosmetics.remove(playerUUID);
        
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage("§a✓ All cosmetics reset!");
        }
    }

    /**
     * Get all players with cosmetics
     */
    public Map<UUID, PlayerCosmetics> getAllCosmetics() {
        return new HashMap<>(playerCosmetics);
    }

    /**
     * Clear all cosmetics (reload)
     */
    public void clearAll() {
        playerCosmetics.clear();
    }
}
