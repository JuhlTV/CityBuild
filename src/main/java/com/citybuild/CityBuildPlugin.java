package com.citybuild;

import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.features.plots.PlotManager;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.terrain.TerrainManager;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.commands.*;

public class CityBuildPlugin extends JavaPlugin {
    
    private PlotManager plotManager;
    private EconomyManager economyManager;
    private TerrainManager terrainManager;
    private AdminManager adminManager;

    @Override
    public void onEnable() {
        getLogger().info("╔════════════════════════════════╗");
        getLogger().info("║     CityBuild Plugin v1.0      ║");
        getLogger().info("║   City Building System Loaded   ║");
        getLogger().info("╚════════════════════════════════╝");

        // Initialize all managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register event listeners
        registerListeners();
        
        getLogger().info("✓ All systems initialized successfully!");
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
        this.getCommand("plot").setExecutor(new PlotCommand(plotManager));
        this.getCommand("economy").setExecutor(new EconomyCommand(economyManager));
        this.getCommand("terrain").setExecutor(new TerrainCommand(terrainManager));
        this.getCommand("admin").setExecutor(new AdminCommand(adminManager, plotManager));
        this.getCommand("shop").setExecutor(new ShopCommand(economyManager));
        
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
}
