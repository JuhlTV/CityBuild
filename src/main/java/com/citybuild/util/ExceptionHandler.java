package com.citybuild.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Global exception handler for consistent error handling and player feedback
 */
public class ExceptionHandler {

    private final CityBuildLogger logger;
    
    public ExceptionHandler(JavaPlugin plugin, CityBuildLogger logger) {
        this.logger = logger;
    }
    
    /**
     * Handle command execution exceptions
     */
    public void handleCommandException(String commandName, Player player, Exception e) {
        logger.error("Error executing command /" + commandName, e);
        
        if (player != null) {
            player.sendMessage(CityBuildConstants.COLOR_ERROR + "An error occurred while executing the command.");
            player.sendMessage(CityBuildConstants.COLOR_SECONDARY + "§oError: " + e.getMessage());
        }
    }
    
    /**
     * Handle manager operation exceptions
     */
    public void handleManagerException(String managerName, String operation, Exception e) {
        logger.error(managerName + " error during " + operation, e);
    }
    
    /**
     * Handle event listener exceptions
     */
    public void handleEventException(String eventName, Exception e) {
        logger.error("Error in event listener: " + eventName, e);
    }
    
    /**
     * Handle data persistence exceptions
     */
    public void handlePersistenceException(String dataType, String operation, Exception e) {
        logger.error("Data persistence error (" + dataType + ") during " + operation, e);
    }
    
    /**
     * Handle API exceptions
     */
    public void handleAPIException(String apiName, String method, Exception e) {
        logger.error("API error in " + apiName + "." + method + "()", e);
    }
    
    /**
     * Safe notify player with error
     */
    public void notifyPlayerError(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(CityBuildConstants.PREFIX_ERROR + message);
        }
    }
    
    /**
     * Safe notify player with success
     */
    public void notifyPlayerSuccess(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(CityBuildConstants.PREFIX_SUCCESS + message);
        }
    }
    
    /**
     * Safe notify player with info
     */
    public void notifyPlayerInfo(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(CityBuildConstants.PREFIX_INFO + message);
        }
    }
    
    /**
     * Wrap exception in try-catch with logging
     */
    public void executeWithErrorHandling(String operation, Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            logger.error("Error during " + operation, e);
        }
    }
    
    /**
     * Execute task and return result safely
     */
    public <T> T executeWithErrorHandling(String operation, SafeOperation<T> task, T defaultValue) {
        try {
            return task.execute();
        } catch (Exception e) {
            logger.error("Error during " + operation, e);
            return defaultValue;
        }
    }
    
    /**
     * Safe operation interface for try-catch wrapping
     */
    public interface SafeOperation<T> {
        T execute() throws Exception;
    }
}
