package com.citybuild;

import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.storage.DataManager;
import com.citybuild.features.plots.PlotManager;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.terrain.TerrainManager;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.gui.GUIManager;
import com.citybuild.features.farming.AchievementManager;
import com.citybuild.features.farming.PlayerFarmDataManager;
import com.citybuild.listeners.InventoryClickListener;
import com.citybuild.listeners.BlockBreakListener;
import com.citybuild.commands.*;
import java.util.Objects;

/**
 * CityBuildPlugin - Main plugin class with JSON persistence system
 * Manages all managers and listeners for the CityBuild plugin
 */
public class CityBuildPlugin extends JavaPlugin {
    private static CityBuildPlugin instance;
    
    private DataManager dataManager;
    private PlotManager plotManager;
    private EconomyManager economyManager;
    private TerrainManager terrainManager;
    private AdminManager adminManager;
    private GUIManager guiManager;
    private AchievementManager achievementManager;
    private PlayerFarmDataManager farmDataManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("╔════════════════════════════════╗");
        getLogger().info("║     CityBuild Plugin v3.4      ║");
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
        getLogger().info("💾 Saving all data...");
        
        // Save all manager data
        if (economyManager != null) {
            economyManager.saveAllData();
        }
        if (plotManager != null) {
            plotManager.saveAllData();
        }
        if (farmDataManager != null) {
            farmDataManager.saveAllData();
        }
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        
        getLogger().info("✓ CityBuild Plugin disabled! All data saved to JSON.");
    }

    private void initializeManagers() {
        // Initialize DataManager first (JSON persistence)
        this.dataManager = new DataManager(this);
        
        // Initialize other managers with DataManager
        this.plotManager = new PlotManager(this, dataManager);
        this.economyManager = new EconomyManager(this, dataManager);
        this.terrainManager = new TerrainManager(this);
        this.adminManager = new AdminManager(this);
        this.guiManager = new GUIManager(this, economyManager);
        this.achievementManager = new AchievementManager(this);
        this.farmDataManager = new PlayerFarmDataManager(this, dataManager);
        
        getLogger().info("✓ Managers initialized with JSON persistence");
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("plot"), "plot command not found in plugin.yml")
            .setExecutor(new PlotCommand(plotManager, guiManager));
        Objects.requireNonNull(getCommand("economy"), "economy command not found in plugin.yml")
            .setExecutor(new EconomyCommand(economyManager));
        Objects.requireNonNull(getCommand("terrain"), "terrain command not found in plugin.yml")
            .setExecutor(new TerrainCommand(terrainManager));
        Objects.requireNonNull(getCommand("admin"), "admin command not found in plugin.yml")
            .setExecutor(new AdminCommand(adminManager, plotManager, terrainManager));
        Objects.requireNonNull(getCommand("shop"), "shop command not found in plugin.yml")
            .setExecutor(new ShopCommand(guiManager));
        
        getLogger().info("✓ Commands registered");
    }

    private void registerListeners() {
        // Register inventory click listener for GUI interactions
        getServer().getPluginManager().registerEvents(
            new InventoryClickListener(this, guiManager), this
        );
        
        // Register block break listener for premium farming system with achievements
        getServer().getPluginManager().registerEvents(
            new BlockBreakListener(this, economyManager, achievementManager), this
        );
        
        getLogger().info("✓ Event listeners registered");
    }

    // Getters for managers
    public DataManager getDataManager() {
        return dataManager;
    }

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
    
    public GUIManager getGUIManager() {
        return guiManager;
    }
    
    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
    
    public PlayerFarmDataManager getFarmDataManager() {
        return farmDataManager;
    }
    
    public static CityBuildPlugin getInstance() {
        return instance;
    }
}
