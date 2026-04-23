package com.citybuild.core.commands;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command Registry - Central command dispatcher
 * Replaces large switch statements with O(1) Map lookup
 */
public class CommandRegistry {
    private final Map<String, ICommandHandler> commands = new HashMap<>();
    private final Logger logger;
    
    public CommandRegistry(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Register a command handler
     * @param commandName Command identifier
     * @param handler Command handler implementation
     */
    public void register(String commandName, ICommandHandler handler) {
        if (commandName == null || commandName.isEmpty()) {
            logger.warning("Cannot register command with null/empty name");
            return;
        }
        
        if (handler == null) {
            logger.warning("Cannot register null handler for command: " + commandName);
            return;
        }
        
        commands.put(commandName.toLowerCase(), handler);
        logger.info("Registered command: " + commandName);
    }
    
    /**
     * Execute a command by name
     * @param player Command executor
     * @param commandName Command to execute
     * @param args Command arguments
     * @return true if executed successfully
     */
    public boolean execute(Player player, String commandName, String[] args) {
        if (player == null || commandName == null) {
            logger.warning("Invalid command execution: player=" + player + ", cmd=" + commandName);
            return false;
        }
        
        ICommandHandler handler = commands.get(commandName.toLowerCase());
        if (handler == null) {
            return false;
        }
        
        // Check permission
        if (!handler.hasPermission(player)) {
            player.sendMessage("§c❌ Du hast keine Berechtigung!");
            return true;
        }
        
        // Check arguments
        if (args.length < handler.getMinArguments()) {
            player.sendMessage("§cUse: " + handler.getUsage());
            return true;
        }
        
        try {
            return handler.execute(player, args);
        } catch (Exception e) {
            logger.severe("Error executing command: " + commandName);
            e.printStackTrace();
            player.sendMessage("§c❌ Ein Fehler ist aufgetreten!");
            return true;
        }
    }
    
    /**
     * Get command by name
     * @param commandName Command identifier
     * @return Optional containing handler if exists
     */
    public Optional<ICommandHandler> getCommand(String commandName) {
        return Optional.ofNullable(commands.get(commandName.toLowerCase()));
    }
    
    /**
     * Check if command exists
     * @param commandName Command identifier
     * @return true if command is registered
     */
    public boolean hasCommand(String commandName) {
        return commands.containsKey(commandName.toLowerCase());
    }
    
    /**
     * Get all registered commands
     * @return Map of all commands
     */
    public Map<String, ICommandHandler> getAll() {
        return new HashMap<>(commands);
    }
    
    /**
     * Get number of registered commands
     * @return Command count
     */
    public int size() {
        return commands.size();
    }
    
    /**
     * Clear all registered commands
     */
    public void clear() {
        commands.clear();
    }
}
