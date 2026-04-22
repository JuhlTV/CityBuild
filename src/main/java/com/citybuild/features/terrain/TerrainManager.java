package com.citybuild.features.terrain;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import java.util.*;

public class TerrainManager {
    private final Plugin plugin;
    private String currentBiome;
    private double terrainHeight;

    public TerrainManager(Plugin plugin) {
        this.plugin = plugin;
        this.currentBiome = "PLAINS";
        this.terrainHeight = 64.0;
    }

    public boolean generateTerrain(Player player, String type) {
        switch (type.toLowerCase()) {
            case "flat":
                currentBiome = "PLAINS";
                terrainHeight = 64.0;
                player.sendMessage("§a✓ Flat terrain generated!");
                return true;
            case "mountain":
                currentBiome = "MOUNTAINS";
                terrainHeight = 150.0;
                player.sendMessage("§a✓ Mountain terrain generated!");
                return true;
            case "island":
                currentBiome = "OCEAN";
                terrainHeight = 70.0;
                player.sendMessage("§a✓ Island terrain generated!");
                return true;
            case "jungle":
                currentBiome = "JUNGLE";
                terrainHeight = 100.0;
                player.sendMessage("§a✓ Jungle terrain generated!");
                return true;
            default:
                player.sendMessage("§cUnknown terrain type! Use: flat, mountain, island, jungle");
                return false;
        }
    }

    public void setTerrainHeight(double height, Player player) {
        this.terrainHeight = Math.max(0, Math.min(256, height));
        player.sendMessage("§a✓ Terrain height set to " + terrainHeight);
    }

    public String getCurrentBiome() {
        return currentBiome;
    }

    public double getTerrainHeight() {
        return terrainHeight;
    }

    public String getTerrainInfo(Player player) {
        return String.format("§6Terrain Info: Biome=%s, Height=%.1f", currentBiome, terrainHeight);
    }
}
