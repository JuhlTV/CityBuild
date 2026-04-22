package com.citybuild;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.PlotManager;
import com.citybuild.managers.WorldManager;
import com.citybuild.managers.ShopManager;
import com.citybuild.managers.BankManager;
import com.citybuild.managers.DailyRewardManager;
import com.citybuild.commands.CityBuildCommand;
import com.citybuild.listeners.PlayerListener;
import com.citybuild.listeners.MobSpawnListener;
import com.citybuild.listeners.InventoryClickListener;
import com.citybuild.gui.GUIManager;

public class CityBuildPlugin extends JavaPlugin {

    private static CityBuildPlugin instance;
    private EconomyManager economyManager;
    private PlotManager plotManager;
    private WorldManager worldManager;
    private ShopManager shopManager;
    private BankManager bankManager;
    private DailyRewardManager dailyRewardManager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Create data folders
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Initialize World Manager first (creates worlds)
        this.worldManager = new WorldManager(this);
        
        // Initialize core managers
        this.economyManager = new EconomyManager(this);
        this.plotManager = new PlotManager(this, worldManager.getPlotWorld());
        
        // Initialize advanced features
        this.shopManager = new ShopManager(this);
        this.bankManager = new BankManager(this, economyManager);
        this.dailyRewardManager = new DailyRewardManager(this);
        Initialize GUI Manager
        this.guiManager = new GUIManager(this);
        
        // Register commands
        getCommand("citybuild").setExecutor(new CityBuildCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(worldManager.getPlotWorld()), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this
        getServer().getPluginManager().registerEvents(new MobSpawnListener(worldManager.getPlotWorld()), this);
        
        getLogger().info("✓ CityBuild Plugin enabled v2.0.0");
        getLogger().info("✓ Running on Paper 1.21.1");
        getLogger().info("✓ Worlds created: cityplot, cityfarm, citypvp");
        getLogger().info("✓ Advanced Features: Shop, Bank, Daily Rewards");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) {
            economyManager.saveData();
        }
        if (plotManager != null) {
            plotManager.saveData();
        }
        if (shopManager != null) {
            shopManager.saveData();
        }
        if (dailyRewardManager != null) {
            dailyRewardManager.saveData();
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

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public DailyRewardManager getDailyRewardManager() {
        return dailyRewardManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }
}
