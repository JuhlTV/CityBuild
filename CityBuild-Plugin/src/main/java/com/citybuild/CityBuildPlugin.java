package com.citybuild;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.PlotManager;
import com.citybuild.commands.CityBuildCommand;
import com.citybuild.listeners.PlayerListener;

public class CityBuildPlugin extends JavaPlugin {

    private static CityBuildPlugin instance;
    private EconomyManager economyManager;
    private PlotManager plotManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Create data folders
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Initialize managers
        this.economyManager = new EconomyManager(this);
        this.plotManager = new PlotManager(this);
        
        // Register commands
        getCommand("citybuild").setExecutor(new CityBuildCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        getLogger().info("✓ CityBuild Plugin enabled v1.0.0");
        getLogger().info("✓ Running on Paper 1.21.1");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) {
            economyManager.saveData();
        }
        if (plotManager != null) {
            plotManager.saveData();
        }
        getLogger().info("✓ CityBuild Plugin disabled");
    }

    public static CityBuildPlugin getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }
}
