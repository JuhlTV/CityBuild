package com.citybuild;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.PlotManager;
import com.citybuild.managers.WorldManager;
import com.citybuild.managers.ShopManager;
import com.citybuild.managers.BankManager;
import com.citybuild.managers.DailyRewardManager;
import com.citybuild.managers.AuctionHouseManager;
import com.citybuild.managers.AchievementManager;
import com.citybuild.managers.ClanManager;
import com.citybuild.managers.WarpManager;
import com.citybuild.managers.QuestManager;
import com.citybuild.managers.EnchantingManager;
import com.citybuild.managers.TradingManager;
import com.citybuild.managers.PlaytimeManager;
import com.citybuild.managers.EventManager;
import com.citybuild.commands.CityBuildCommand;
import com.citybuild.listeners.PlayerListener;
import com.citybuild.listeners.MobSpawnListener;
import com.citybuild.listeners.InventoryClickListener;
import com.citybuild.listeners.PlotProtectionListener;
import com.citybuild.listeners.RewardListener;
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
    private AuctionHouseManager auctionHouseManager;
    private AchievementManager achievementManager;
    private ClanManager clanManager;
    private WarpManager warpManager;
    private QuestManager questManager;
    private EnchantingManager enchantingManager;
    private TradingManager tradingManager;
    private PlaytimeManager playtimeManager;
    private EventManager eventManager;

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
        this.auctionHouseManager = new AuctionHouseManager(this);
        this.dailyRewardManager = new DailyRewardManager(this);
        
        // Initialize expansion features (Tier 1)
        this.achievementManager = new AchievementManager(this);
        this.clanManager = new ClanManager(this);
        this.warpManager = new WarpManager(this);
        
        // Initialize advanced features (Tier 2)
        this.questManager = new QuestManager(this);
        this.enchantingManager = new EnchantingManager(this, economyManager);
        this.tradingManager = new TradingManager(this);
        
        // Initialize tier 3 features
        this.playtimeManager = new PlaytimeManager(this, economyManager);
        this.eventManager = new EventManager(this, economyManager);
        
        // Initialize GUI Manager
        this.guiManager = new GUIManager(this);
        
        // Register commands
        getCommand("citybuild").setExecutor(new CityBuildCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(worldManager.getPlotWorld()), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlotProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new RewardListener(this), this);
        
        getLogger().info("✓ CityBuild Plugin enabled v2.1.0 - MEGA EDITION");
        getLogger().info("✓ Running on Paper 1.21.1");
        getLogger().info("✓ 11 Managers Active: Economy, Plot, World, Shop, Bank, Daily, Auction, Achievement, Clan, Warp, Quest, Enchanting, Trading, Playtime, Events");
        getLogger().info("✓ All 9 Expansion Features Available!");
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
        if (auctionHouseManager != null) {
            auctionHouseManager.saveData();
        }
        if (achievementManager != null) {
            achievementManager.saveData();
        }
        if (clanManager != null) {
            clanManager.saveData();
        }
        if (warpManager != null) {
            warpManager.saveData();
        }
        if (questManager != null) {
            questManager.saveData();
        }
        if (tradingManager != null) {
            tradingManager.saveData();
        }
        if (playtimeManager != null) {
            playtimeManager.saveData();
        }
        getLogger().info("✓ CityBuild Plugin v2.1.0 disabled - All 11 managers saved");
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

    public AuctionHouseManager getAuctionHouseManager() {
        return auctionHouseManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public EnchantingManager getEnchantingManager() {
        return enchantingManager;
    }

    public TradingManager getTradingManager() {
        return tradingManager;
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
