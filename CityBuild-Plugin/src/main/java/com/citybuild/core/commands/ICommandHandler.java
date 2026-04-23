package com.citybuild.core.commands;

import org.bukkit.entity.Player;

/**
 * Command Handler Interface - All commands must implement this
 * Standardized command processing
 */
public interface ICommandHandler {
    
    /**
     * Execute the command
     * @param player Command executor
     * @param args Command arguments
     * @return true if command was successful, false otherwise
     */
    boolean execute(Player player, String[] args);
    
    /**
     * Get permission required for this command (optional)
     * @return Permission string, null if no permission required
     */
    default String getPermission() {
        return null;
    }
    
    /**
     * Get minimal arguments required
     * @return Minimum number of arguments
     */
    default int getMinArguments() {
        return 0;
    }
    
    /**
     * Get usage description
     * @return Usage help text
     */
    default String getUsage() {
        return "";
    }
    
    /**
     * Check if player has permission
     * @param player Player to check
     * @return true if player can execute
     */
    default boolean hasPermission(Player player) {
        String perm = getPermission();
        if (perm == null) return true;
        return player.hasPermission(perm) || player.isOp();
    }
}
