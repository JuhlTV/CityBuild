package com.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.citybuild.features.economy.EconomyManager;

public class EconomyCommand implements CommandExecutor {
    private final EconomyManager economyManager;

    public EconomyCommand(EconomyManager economyManager) {
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
            case "balance":
                double balance = economyManager.getBalance(player);
                player.sendMessage("§6Your balance: §a$" + balance);
                return true;
            case "pay":
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /economy pay <player> <amount>");
                    return false;
                }
                Player target = player.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cPlayer not found!");
                    return false;
                }
                try {
                    double amount = Double.parseDouble(args[2]);
                    economyManager.payPlayer(player, target, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid amount!");
                }
                return true;
            case "check":
                player.sendMessage("§6Economy system running smoothly!");
                return true;
            default:
                showHelp(player);
                return false;
        }
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== Economy Commands ===");
        player.sendMessage("§e/economy balance §7- Check your balance");
        player.sendMessage("§e/economy pay <player> <amount> §7- Send money");
        player.sendMessage("§e/economy check §7- Check economy status");
    }
}
