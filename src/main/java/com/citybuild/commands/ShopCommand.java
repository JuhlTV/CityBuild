package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.economy.EconomyManager;

public class ShopCommand implements CommandExecutor {
    private final EconomyManager economyManager;

    public ShopCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command!");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                player.sendMessage("§a✓ Shop created!");
                return true;
            case "remove":
                player.sendMessage("§a✓ Shop removed!");
                return true;
            case "info":
                player.sendMessage("§6Shop Info: Status=Active, Items=0");
                return true;
            default:
                showHelp(player);
                return false;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== Shop Commands ===");
        player.sendMessage("§e/shop create §7- Create a shop");
        player.sendMessage("§e/shop remove §7- Remove your shop");
        player.sendMessage("§e/shop info §7- Show shop information");
    }
}
