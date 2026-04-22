package com.citybuild;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.literal;

import com.citybuild.features.plots.PlotManager;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.terrain.TerrainManager;
import com.citybuild.features.admin.AdminManager;
import com.citybuild.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CityBuildMod implements ModInitializer {
    public static final String MOD_ID = "citybuild";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static CityBuildMod instance;
    private PlotManager plotManager;
    private EconomyManager economyManager;
    private TerrainManager terrainManager;
    private AdminManager adminManager;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("╔════════════════════════════════╗");
        LOGGER.info("║     CityBuild Mod v1.0         ║");
        LOGGER.info("║   City Building System Loaded   ║");
        LOGGER.info("╚════════════════════════════════╝");

        try {
            initializeManagers();
            registerCommands();
            LOGGER.info("✓ All systems initialized successfully!");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize CityBuild: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeManagers() {
        this.plotManager = new PlotManager(this);
        this.economyManager = new EconomyManager(this);
        this.terrainManager = new TerrainManager(this);
        this.adminManager = new AdminManager(this);
        LOGGER.info("✓ Managers initialized");
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new PlotCommand(plotManager).register(dispatcher);
            new EconomyCommand(economyManager).register(dispatcher);
            new TerrainCommand(terrainManager).register(dispatcher);
            new AdminCommand(adminManager, plotManager).register(dispatcher);
            new ShopCommand(economyManager).register(dispatcher);
        });
        LOGGER.info("✓ Commands registered");
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

    public static CityBuildMod getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
