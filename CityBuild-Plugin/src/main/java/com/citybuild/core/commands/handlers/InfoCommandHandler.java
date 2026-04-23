package com.citybuild.core.commands.handlers;

import com.citybuild.CityBuildPlugin;
import com.citybuild.core.commands.ICommandHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * InfoCommandHandler - Shows player info (balance, plots, etc)
 */
public class InfoCommandHandler implements ICommandHandler {
    private final CityBuildPlugin plugin;
    
    public InfoCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        var economy = plugin.getEconomyManager();
        var plots = plugin.getPlotManager();
        
        economy.initializePlayer(player);
        String uuid = player.getUniqueId().toString();
        
        long balance = economy.getBalance(player);
        int plotCount = plots.getPlotCount(uuid);
        
        player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Your Info:", NamedTextColor.GOLD)));
        player.sendMessage(Component.text("  Balance: $" + balance, NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Plots: " + plotCount, NamedTextColor.YELLOW));
        
        return true;
    }
    
    @Override
    public String getName() {
        return "info";
    }
    
    @Override
    public String getUsage() {
        return "/citybuild info";
    }
}
