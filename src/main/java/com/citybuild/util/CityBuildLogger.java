package com.citybuild.util;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

/**
 * Centralized logger utility for consistent logging across the plugin
 * Provides helper methods for different log levels with formatting
 */
public class CityBuildLogger {
    
    private final Logger logger;
    private final String prefix = "§b[CityBuild]§r ";
    
    public CityBuildLogger(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }
    
    /**
     * Log info message with formatting
     */
    public void info(String message) {
        logger.info(prefix + message);
    }
    
    /**
     * Log success message
     */
    public void success(String message) {
        logger.info(prefix + "§a✓ " + message);
    }
    
    /**
     * Log warning message
     */
    public void warn(String message) {
        logger.warning(prefix + "§e⚠ " + message);
    }
    
    /**
     * Log error message
     */
    public void error(String message) {
        logger.severe(prefix + "§c✗ " + message);
    }
    
    /**
     * Log error with exception
     */
    public void error(String message, Throwable throwable) {
        logger.severe(prefix + "§c✗ " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * Log debug message (only if enabled)
     */
    public void debug(String message) {
        logger.fine(prefix + "§8[DEBUG] " + message);
    }
    
    /**
     * Log configuration loading
     */
    public void logConfigLoad(String configName, boolean success) {
        if (success) {
            success("Loaded " + configName);
        } else {
            error("Failed to load " + configName);
        }
    }
    
    /**
     * Log manager initialization
     */
    public void logManagerInit(String managerName) {
        info("§aInitializing §e" + managerName + "§a...");
    }
    
    /**
     * Log manager initialization complete
     */
    public void logManagerInitComplete(String managerName) {
        success(managerName + " initialized");
    }
    
    /**
     * Get underlying logger
     */
    public Logger getLogger() {
        return logger;
    }
}
