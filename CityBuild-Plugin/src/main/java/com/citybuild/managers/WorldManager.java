package com.citybuild.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerator;
import com.citybuild.CityBuildPlugin;

public class WorldManager {
    
    private CityBuildPlugin plugin;
    private World plotWorld;
    private World farmWorld;
    private World pvpWorld;
    
    private static final String PLOT_WORLD_NAME = "cityplot";
    private static final String FARM_WORLD_NAME = "cityfarm";
    private static final String PVP_WORLD_NAME = "citypvp";
    
    public WorldManager(CityBuildPlugin plugin) {
        this.plugin = plugin;
        initializeWorlds();
    }
    
    private void initializeWorlds() {
        plugin.getLogger().info("Creating worlds...");
        
        // Create Plot World (Flat)
        this.plotWorld = createFlatWorld(PLOT_WORLD_NAME);
        
        // Create Farm World (Normal)
        this.farmWorld = createNormalWorld(FARM_WORLD_NAME);
        
        // Create PVP World (Normal)
        this.pvpWorld = createNormalWorld(PVP_WORLD_NAME);
        
        plugin.getLogger().info("All worlds initialized!");
    }
    
    private World createFlatWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            plugin.getLogger().info("World " + name + " already exists!");
            return world;
        }
        
        WorldCreator creator = new WorldCreator(name);
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        
        plugin.getLogger().info("Creating flat world: " + name);
        return creator.createWorld();
    }
    
    private World createNormalWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            plugin.getLogger().info("World " + name + " already exists!");
            return world;
        }
        
        WorldCreator creator = new WorldCreator(name);
        creator.type(WorldType.NORMAL);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(true);
        
        plugin.getLogger().info("Creating normal world: " + name);
        return creator.createWorld();
    }
    
    public World getPlotWorld() {
        return plotWorld;
    }
    
    public World getFarmWorld() {
        return farmWorld;
    }
    
    public World getPvpWorld() {
        return pvpWorld;
    }
    
    public World getWorldByName(String name) {
        switch(name.toLowerCase()) {
            case "plot":
                return plotWorld;
            case "farm":
                return farmWorld;
            case "pvp":
                return pvpWorld;
            default:
                return null;
        }
    }
}
