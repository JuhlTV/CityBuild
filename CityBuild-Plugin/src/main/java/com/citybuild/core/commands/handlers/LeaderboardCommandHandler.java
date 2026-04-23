package com.citybuild.core.commands.handlers;

import com.citybuild.CityBuildPlugin;
import com.citybuild.core.commands.ICommandHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * LeaderboardCommandHandler - Shows top player leaderboard
 */
public class LeaderboardCommandHandler implements ICommandHandler {
    private final CityBuildPlugin plugin;
    
    public LeaderboardCommandHandler(CityBuildPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        var economy = plugin.getEconomyManager();
        var leaderboard = economy.getLeaderboard(10);
        
        player.sendMessage(Component.text("=== CityBuild Leaderboard ===", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        
        int rank = 1;
        for (var entry : leaderboard) {
            String uuid = entry.getKey();
            long balance = entry.getValue();
            
            Player p = Bukkit.getPlayer(java.util.UUID.fromString(uuid));
            String name = p != null ? p.getName() : "Unknown";
            
            String medal = switch(rank) {
                case 1 -> "🥇";
                case 2 -> "🥈";
                case 3 -> "🥉";
                default -> "  ";
            };
            
            player.sendMessage(Component.text(medal + " #" + rank + ": " + name + " - $" + balance, NamedTextColor.YELLOW));
            rank++;
        }
        
        return true;
    }
    
    @Override
    public String getName() {
        return "leaderboard";
    }
    
    @Override
    public String getUsage() {
        return "/citybuild leaderboard";
    }
}
