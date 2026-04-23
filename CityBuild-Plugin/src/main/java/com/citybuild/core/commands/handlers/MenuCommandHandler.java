package com.citybuild.core.commands.handlers;

import com.citybuild.CityBuildPlugin;
import com.citybuild.core.commands.ICommandHandler;
import org.bukkit.entity.Player;

/**
 * MenuCommandHandler - Handles menu opening
 */
public class MenuCommandHandler implements ICommandHandler {
    private final CityBuildPlugin plugin;
    
    public MenuCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        plugin.getGUIManager().openMainMenu(player);
        return true;
    }
    
    @Override
    public String getName() {
        return "menu";
    }
    
    @Override
    public String getUsage() {
        return "/citybuild menu";
    }
}
