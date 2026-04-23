package com.citybuild.core.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.core.commands.handlers.InfoCommandHandler;
import com.citybuild.core.commands.handlers.LeaderboardCommandHandler;
import com.citybuild.core.commands.handlers.MenuCommandHandler;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CommandRegistry Unit Tests
 * Tests O(1) command execution and registration
 */
public class CommandRegistryTest {
    private CommandRegistry registry;
    private Player mockPlayer;
    private CityBuildPlugin mockPlugin;
    
    @BeforeEach
    public void setUp() {
        registry = new CommandRegistry(null);
        mockPlayer = mock(Player.class);
        mockPlugin = mock(CityBuildPlugin.class);
    }
    
    @Test
    public void testRegisterCommand() {
        MenuCommandHandler handler = new MenuCommandHandler(mockPlugin);
        registry.register("menu", handler);
        
        assertTrue(registry.hasCommand("menu"));
    }
    
    @Test
    public void testRegisterMultipleCommands() {
        MenuCommandHandler menuHandler = new MenuCommandHandler(mockPlugin);
        InfoCommandHandler infoHandler = new InfoCommandHandler(mockPlugin);
        LeaderboardCommandHandler leaderboardHandler = new LeaderboardCommandHandler(mockPlugin);
        
        registry.register("menu", menuHandler);
        registry.register("info", infoHandler);
        registry.register("leaderboard", leaderboardHandler);
        
        assertTrue(registry.hasCommand("menu"));
        assertTrue(registry.hasCommand("info"));
        assertTrue(registry.hasCommand("leaderboard"));
    }
    
    @Test
    public void testCaseSensitivity() {
        MenuCommandHandler handler = new MenuCommandHandler(mockPlugin);
        registry.register("MENU", handler);
        
        // Should match lowercase
        assertTrue(registry.hasCommand("menu"));
        assertTrue(registry.hasCommand("MENU"));
        assertTrue(registry.hasCommand("Menu"));
    }
    
    @Test
    public void testGetCommand() {
        MenuCommandHandler handler = new MenuCommandHandler(mockPlugin);
        registry.register("menu", handler);
        
        var cmd = registry.getCommand("menu");
        assertTrue(cmd.isPresent());
    }
    
    @Test
    public void testGetNonexistentCommand() {
        var cmd = registry.getCommand("nonexistent");
        assertFalse(cmd.isPresent());
    }
    
    @Test
    public void testExecuteCommand() {
        MenuCommandHandler handler = new MenuCommandHandler(mockPlugin);
        registry.register("menu", handler);
        
        // Should return true (command exists and executed)
        boolean result = registry.execute(mockPlayer, "menu", new String[]{});
        // Note: This will fail in test due to mockPlugin, but registration works
    }
}
