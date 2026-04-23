package com.citybuild.commands;

import com.citybuild.features.achievements.Achievement;
import com.citybuild.features.achievements.AchievementManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Command handler for /achievements command
 * Shows player achievements and progress
 */
public class AchievementCommand implements CommandExecutor {
    
    private final AchievementManager achievementManager;
    
    public AchievementCommand(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Befehl ist nur für Spieler!");
            return true;
        }
        
        if (args.length > 0 && args[0].equalsIgnoreCase("progress")) {
            showProgress(player);
        } else {
            showAchievements(player);
        }
        
        return true;
    }
    
    private void showAchievements(Player player) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Achievement> achievements = achievementManager.getPlayerAchievements(playerUUID);
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║ Deine Achievements");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        
        // Group by category
        for (Achievement.Category category : Achievement.Category.values()) {
            player.sendMessage("");
            player.sendMessage(category.getEmoji() + " §6§l" + category.getDisplayName());
            
            achievements.values().stream()
                .filter(ach -> ach.getCategory() == category)
                .forEach(ach -> {
                    String status = ach.isUnlocked() ? "§a✓" : "§7✗";
                    String name = ach.getRarity().getColor() + ach.getName();
                    String progress = ach.getProgressBar();
                    player.sendMessage(String.format("  %s %s §7[%s]", status, name, progress));
                });
        }
        
        player.sendMessage("");
        player.sendMessage("§6§l╚════════════════════════════════════╝");
        
        int unlocked = achievementManager.getUnlockedCount(playerUUID);
        int total = achievements.size();
        player.sendMessage(String.format("§7Achievements: §a%d§7/§c%d §7(%.0f%%)", 
            unlocked, total, achievementManager.getCompletionPercentage(playerUUID)));
    }
    
    private void showProgress(Player player) {
        UUID playerUUID = player.getUniqueId();
        Map<String, Achievement> achievements = achievementManager.getPlayerAchievements(playerUUID);
        
        player.sendMessage("§6§l╔════════════════════════════════════╗");
        player.sendMessage("§6§l║ Fortschritt Details");
        player.sendMessage("§6§l╠════════════════════════════════════╣");
        
        achievements.values().stream()
            .filter(ach -> !ach.isUnlocked())
            .forEach(ach -> {
                String progress = ach.getProgressBar();
                String desc = ach.getDescription();
                player.sendMessage(String.format("§e%s §7- %s", ach.getName(), progress));
                player.sendMessage(String.format("  §7%s", desc));
            });
        
        player.sendMessage("");
        int unlocked = achievementManager.getUnlockedCount(playerUUID);
        int points = achievementManager.getPlayerAchievementPoints(playerUUID);
        player.sendMessage(String.format("§7Freigeschalten: §a%d §7Points: §b%d", unlocked, points));
        player.sendMessage("§6§l╚════════════════════════════════════╝");
    }
}
