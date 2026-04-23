package com.citybuild;

import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.storage.DataManager;
import com.citybuild.features.plots.PlotManager;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.terrain.TerrainManager;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.features.achievements.AchievementManager;
import com.citybuild.features.leaderboards.LeaderboardManager;
import com.citybuild.features.trading.TradingManager;
import com.citybuild.features.guilds.GuildManager;
import com.citybuild.features.ranking.RankingManager;
import com.citybuild.features.auctions.AuctionHouseManager;
import com.citybuild.features.cosmetics.CosmeticsManager;
import com.citybuild.storage.GuildPersistence;
import com.citybuild.storage.TradePersistence;
import com.citybuild.storage.AuctionPersistence;
import com.citybuild.storage.AchievementPersistence;
import com.citybuild.features.farming.PlayerFarmDataManager;
import com.citybuild.gui.GUIManager;
import com.citybuild.listeners.InventoryClickListener;
import com.citybuild.listeners.PlotProtectionListener;
import com.citybuild.commands.PlotCommand;
import com.citybuild.commands.EconomyCommand;
import com.citybuild.commands.TerrainCommand;
import com.citybuild.commands.AdminCommand;
import com.citybuild.commands.LeaderboardCommand;
import com.citybuild.commands.AchievementCommand;
import com.citybuild.commands.GuildCommand;
import com.citybuild.commands.TradeCommand;
import com.citybuild.commands.RankCommand;
import com.citybuild.commands.AuctionCommand;
import com.citybuild.commands.CosmeticsCommand;
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
    private LeaderboardManager leaderboardManager;
    private TradingManager tradingManager;
    private GuildManager guildManager;
    private RankingManager rankingManager;
    private AuctionHouseManager auctionHouseManager;
    private CosmeticsManager cosmeticsManager;
    private GuildPersistence guildPersistence;
    private TradePersistence tradePersistence;
    private AuctionPersistence auctionPersistence;
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
        if (achievementManager != null) {
            achievementManager.saveAllData();
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
        this.leaderboardManager = new LeaderboardManager(economyManager, plotManager, achievementManager);
        this.tradingManager = new TradingManager(this, economyManager);
        this.guildManager = new GuildManager(this);
        this.rankingManager = new RankingManager();
        this.auctionHouseManager = new AuctionHouseManager(this, economyManager);
        this.cosmeticsManager = new CosmeticsManager();
        this.guildPersistence = new GuildPersistence(this);
        this.tradePersistence = new TradePersistence(this);
        this.auctionPersistence = new AuctionPersistence(this);
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
        Objects.requireNonNull(getCommand("lb"), "lb command not found in plugin.yml")
            .setExecutor(new LeaderboardCommand(leaderboardManager));
        Objects.requireNonNull(getCommand("ach"), "ach command not found in plugin.yml")
            .setExecutor(new AchievementCommand(achievementManager));
        Objects.requireNonNull(getCommand("guild"), "guild command not found in plugin.yml")
            .setExecutor(new GuildCommand(guildManager));
        Objects.requireNonNull(getCommand("trade"), "trade command not found in plugin.yml")
            .setExecutor(new TradeCommand(tradingManager));
        Objects.requireNonNull(getCommand("rank"), "rank command not found in plugin.yml")
            .setExecutor(new RankCommand(this));
        Objects.requireNonNull(getCommand("auction"), "auction command not found in plugin.yml")
            .setExecutor(new AuctionCommand(this, auctionHouseManager));
        Objects.requireNonNull(getCommand("cosmetics"), "cosmetics command not found in plugin.yml")
            .setExecutor(new CosmeticsCommand(this, cosmeticsManager));
        
        getLogger().info("✓ Commands registered");
    }

    private void registerListeners() {
        // Register inventory click listener for GUI interactions
        getServer().getPluginManager().registerEvents(
            new InventoryClickListener(this, guiManager), this
        );
        
        // Register plot protection listener for building restrictions
        getServer().getPluginManager().registerEvents(
            new PlotProtectionListener(this, plotManager), this
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
    
    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }
    
    public TradingManager getTradingManager() {
        return tradingManager;
    }
    
    public GuildManager getGuildManager() {
        return guildManager;
    }
    
    public RankingManager getRankingManager() {
        return rankingManager;
    }
    
    public AuctionHouseManager getAuctionHouseManager() {
        return auctionHouseManager;
    }
    
    public CosmeticsManager getCosmeticsManager() {
        return cosmeticsManager;
    }
    
    public GuildPersistence getGuildPersistence() {
        return guildPersistence;
    }
    
    public TradePersistence getTradePersistence() {
        return tradePersistence;
    }
    
    public AuctionPersistence getAuctionPersistence() {
        return auctionPersistence;
    }
    
    public PlayerFarmDataManager getFarmDataManager() {
        return farmDataManager;
    }
    
    public static CityBuildPlugin getInstance() {
        return instance;
    }
}
