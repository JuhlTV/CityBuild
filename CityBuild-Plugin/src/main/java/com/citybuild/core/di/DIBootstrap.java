package com.citybuild.core.di;

import com.citybuild.CityBuildPlugin;
import com.citybuild.core.commands.CommandRegistry;
import com.citybuild.core.events.EventDispatcher;
import com.citybuild.core.services.*;
import com.citybuild.managers.*;
import java.util.logging.Logger;

/**
 * DIBootstrap - Initializes the dependency injection container
 * Registers all managers, services, and core components
 * Called once during plugin startup
 */
public class DIBootstrap {
    private final CityBuildPlugin plugin;
    private final Container container;
    private final Logger logger;
    
    public DIBootstrap(CityBuildPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.container = new Container(logger);
    }
    
    /**
     * Bootstrap the entire DI container
     * Called from CityBuildPlugin.onEnable()
     */
    public Container bootstrap() {
        logger.info("🔧 Bootstrapping Dependency Injection...");
        
        // Register core components first
        registerCoreComponents();
        
        // Register managers (all 22 of them)
        registerManagers();
        
        // Register services
        registerServices();
        
        // Register command and event systems
        registerCommandAndEventSystems();
        
        logger.info("✅ DI Bootstrap complete! (" + container.getSingletonCount() + 
                   " singletons, " + container.getBindingCount() + " bindings)");
        
        return container;
    }
    
    /**
     * Register core framework components
     */
    private void registerCoreComponents() {
        container.registerSingleton(Logger.class, logger);
        container.registerSingleton(CityBuildPlugin.class, plugin);
    }
    
    /**
     * Register all manager classes
     * These are the core data and business logic managers
     */
    private void registerManagers() {
        // Core managers - from existing plugin structure
        // Note: These assume managers are already initialized
        // In a real scenario, you'd get them from plugin instances
        
        container.registerSingleton(EconomyManager.class, plugin.getEconomyManager());
        container.registerSingleton(PlotManager.class, plugin.getPlotManager());
        container.registerSingleton(WorldManager.class, plugin.getWorldManager());
        container.registerSingleton(AdminManager.class, plugin.getAdminManager());
        container.registerSingleton(BanManager.class, plugin.getBanManager());
        container.registerSingleton(TransactionManager.class, plugin.getTransactionManager());
        container.registerSingleton(PlotTaxManager.class, plugin.getPlotTaxManager());
        container.registerSingleton(AuditManager.class, plugin.getAuditManager());
        container.registerSingleton(CacheManager.class, plugin.getCacheManager());
        container.registerSingleton(ConfigManager.class, plugin.getConfigManager());
        
        // Additional managers
        container.registerSingleton(ShopManager.class, plugin.getShopManager());
        container.registerSingleton(BankManager.class, plugin.getBankManager());
        container.registerSingleton(DailyRewardManager.class, plugin.getDailyRewardManager());
        container.registerSingleton(AuctionHouseManager.class, plugin.getAuctionHouseManager());
        container.registerSingleton(AchievementManager.class, plugin.getAchievementManager());
        container.registerSingleton(ClanManager.class, plugin.getClanManager());
        container.registerSingleton(WarpManager.class, plugin.getWarpManager());
        container.registerSingleton(QuestManager.class, plugin.getQuestManager());
        container.registerSingleton(EnchantingManager.class, plugin.getEnchantingManager());
        container.registerSingleton(TradingManager.class, plugin.getTradingManager());
        container.registerSingleton(PlaytimeManager.class, plugin.getPlaytimeManager());
        container.registerSingleton(EventManager.class, plugin.getEventManager());
        
        logger.info("✅ Registered 22 managers");
    }
    
    /**
     * Register service layer classes
     * These provide business logic abstraction
     */
    private void registerServices() {
        // Create and register services
        EconomyService economyService = new EconomyService(
            container.get(EconomyManager.class),
            logger
        );
        container.registerSingleton(EconomyService.class, economyService);
        
        AdminService adminService = new AdminService(
            container.get(AdminManager.class),
            logger
        );
        container.registerSingleton(AdminService.class, adminService);
        
        PlayerService playerService = new PlayerService(
            container.get(EconomyManager.class),
            container.get(PlotManager.class),
            container.get(AdminManager.class),
            container.get(PlaytimeManager.class),
            container.get(AchievementManager.class),
            logger
        );
        container.registerSingleton(PlayerService.class, playerService);
        
        logger.info("✅ Registered 3 services (Economy, Admin, Player)");
    }
    
    /**
     * Register command and event systems
     */
    private void registerCommandAndEventSystems() {
        // Register command registry
        CommandRegistry commandRegistry = new CommandRegistry(logger);
        container.registerSingleton(CommandRegistry.class, commandRegistry);
        
        // Register event dispatcher
        EventDispatcher eventDispatcher = new EventDispatcher(logger);
        container.registerSingleton(EventDispatcher.class, eventDispatcher);
        
        logger.info("✅ Registered command and event systems");
    }
    
    /**
     * Get the bootstrap container
     */
    public Container getContainer() {
        return container;
    }
}
