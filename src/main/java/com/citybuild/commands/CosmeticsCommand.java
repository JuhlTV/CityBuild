package com.citybuild.commands;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.cosmetics.CosmeticsManager;
import com.citybuild.features.cosmetics.PlayerCosmetics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /cosmetics command
 * Manages player cosmetic options
 */
public class CosmeticsCommand implements CommandExecutor {

    private final CosmeticsManager cosmeticsManager;

    public CosmeticsCommand(CityBuildPlugin plugin, CosmeticsManager cosmeticsManager) {
        this.cosmeticsManager = cosmeticsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "title":
                handleTitle(player, args);
                break;
            case "color":
                handleColor(player, args);
                break;
            case "rainbow":
                handleRainbow(player);
                break;
            case "particles":
            case "effects":
                handleParticles(player);
                break;
            case "prestige":
                handlePrestige(player);
                break;
            case "info":
            case "view":
                handleInfo(player);
                break;
            case "reset":
                handleReset(player);
                break;
            default:
                showHelp(player);
        }

        return true;
    }

    private void handleTitle(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /cosmetics title <title>");
            player.sendMessage("§7Available: MAYOR, MILLIONAIRE, LEGENDARY, BUILDING_MASTER, FARMING_GURU, GUILD_LEADER, TYCOON, DIAMOND_PLAYER");
            return;
        }

        String titleName = args[1].toUpperCase();
        try {
            PlayerCosmetics.Title title = PlayerCosmetics.Title.valueOf(titleName);
            cosmeticsManager.setPlayerTitle(player.getUniqueId(), title.getDisplay());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c❌ Invalid title! Available: MAYOR, MILLIONAIRE, LEGENDARY, BUILDING_MASTER, FARMING_GURU, GUILD_LEADER, TYCOON, DIAMOND_PLAYER");
        }
    }

    private void handleColor(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /cosmetics color <color>");
            player.sendMessage("§7Available: GOLD, RED, BLUE, GREEN, PURPLE, CYAN, YELLOW, WHITE");
            return;
        }

        String colorName = args[1].toUpperCase();
        try {
            PlayerCosmetics.NameColor color = PlayerCosmetics.NameColor.valueOf(colorName);
            cosmeticsManager.setPlayerNameColor(player.getUniqueId(), color.getCode());
            player.sendMessage("§a✓ Name color updated!");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§c❌ Invalid color! Available: GOLD, RED, BLUE, GREEN, PURPLE, CYAN, YELLOW, WHITE");
        }
    }

    private void handleRainbow(Player player) {
        cosmeticsManager.toggleRainbowName(player.getUniqueId());
    }

    private void handleParticles(Player player) {
        cosmeticsManager.toggleParticleEffects(player.getUniqueId());
    }

    private void handlePrestige(Player player) {
        // Only OPs can give prestige
        if (!player.isOp()) {
            player.sendMessage("§c❌ Only OPs can grant prestige!");
            return;
        }

        cosmeticsManager.addPrestige(player.getUniqueId());
    }

    private void handleInfo(Player player) {
        String info = cosmeticsManager.getCosmeticsInfo(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 YOUR COSMETICS");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(info);
        player.sendMessage("");
    }

    private void handleReset(Player player) {
        cosmeticsManager.resetCosmetics(player.getUniqueId());
    }

    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage("§e╔════════════════════════════════════════╗");
        player.sendMessage("§e║§6 COSMETICS COMMAND HELP");
        player.sendMessage("§e╚════════════════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage("§7/cosmetics title <title> §6- Set your title");
        player.sendMessage("§7/cosmetics color <color> §6- Change name color");
        player.sendMessage("§7/cosmetics rainbow §6- Toggle rainbow name");
        player.sendMessage("§7/cosmetics particles §6- Toggle particle effects");
        player.sendMessage("§7/cosmetics info §6- View your cosmetics");
        player.sendMessage("§7/cosmetics prestige §6- Add prestige (OP only)");
        player.sendMessage("§7/cosmetics reset §6- Reset all cosmetics");
        player.sendMessage("");
    }
}
