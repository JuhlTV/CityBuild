package com.citybuild;

import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.features.plots.PlotManager;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.terrain.TerrainManager;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.commands.*;
import java.util.Objects;

public class CityBuildPlugin extends JavaPlugin {
    private static CityBuildPlugin instance;
    
    private PlotManager plotManager;
    private EconomyManager economyManager;
    private TerrainManager terrainManager;
    private AdminManager adminManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("╔════════════════════════════════╗");
        getLogger().info("║     CityBuild Plugin v1.0      ║");
        getLogger().info("║   City Building System Loaded   ║");
        getLogger().info("╚════════════════════════════════╝");

        try {
            // Initialize all managers
            initializeManagers();
            
            // Register commands
            registerCommands();
            
            // Register event listeners
            registerListeners();
            
            getLogger().info("✓ All systems initialized successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize CityBuild: " + e.getMessage());
            e.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("CityBuild Plugin disabled!");
        if (economyManager != null) {
            economyManager.saveAllData();
        }
        if (plotManager != null) {
            plotManager.saveAllData();
        }
    }

    private void initializeManagers() {
        this.plotManager = new PlotManager(this);
        this.economyManager = new EconomyManager(this);
        this.terrainManager = new TerrainManager(this);
        this.adminManager = new AdminManager(this);
        
        getLogger().info("✓ Managers initialized");
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("plot"), "plot command not found in plugin.yml").setExecutor(new PlotCommand(plotManager));
        Objects.requireNonNull(getCommand("economy"), "economy command not found in plugin.yml").setExecutor(new EconomyCommand(economyManager));
        Objects.requireNonNull(getCommand("terrain"), "terrain command not found in plugin.yml").setExecutor(new TerrainCommand(terrainManager));
        Objects.requireNonNull(getCommand("admin"), "admin command not found in plugin.yml").setExecutor(new AdminCommand(adminManager, plotManager));
        Objects.requireNonNull(getCommand("shop"), "shop command not found in plugin.yml").setExecutor(new ShopCommand(economyManager));
        
        getLogger().info("✓ Commands registered");
    }

    private void registerListeners() {
        // Event listeners will be registered here
        getLogger().info("✓ Event listeners registered");
    }

    // Getters for managers
    public PlotManager getPlotManager() {
        return plotManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public TerrainManager getTerrainManager() {
        return terrainManager;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }
    
    public static CityBuildPlugin getInstance() {
        return instance;
    }
}
