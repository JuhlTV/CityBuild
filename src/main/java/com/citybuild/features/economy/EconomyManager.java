package com.citybuild.features.economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.*;

public class EconomyManager {
    private final Plugin plugin;
    private final Map<String, Double> playerBalance;
    private final double STARTING_BALANCE = 10000.0;

    public EconomyManager(Plugin plugin) {
        this.plugin = plugin;
        this.playerBalance = new HashMap<>();
        loadAllData();
    }

    public void addBalance(Player player, double amount) {
        String uuid = player.getUniqueId().toString();
        playerBalance.put(uuid, getBalance(player) + amount);
        player.sendMessage("§a+ $" + amount);
    }

    public boolean removeBalance(Player player, double amount) {
        if (getBalance(player) >= amount) {
            String uuid = player.getUniqueId().toString();
            playerBalance.put(uuid, getBalance(player) - amount);
            player.sendMessage("§c- $" + amount);
            return true;
        }
        player.sendMessage("§cInsufficient funds!");
        return false;
    }

    public double getBalance(Player player) {
        String uuid = player.getUniqueId().toString();
        return playerBalance.getOrDefault(uuid, STARTING_BALANCE);
    }

    public boolean payPlayer(Player sender, Player receiver, double amount) {
        if (removeBalance(sender, amount)) {
            addBalance(receiver, amount);
            sender.sendMessage("§a✓ Sent $" + amount + " to " + receiver.getName());
            receiver.sendMessage("§a✓ Received $" + amount + " from " + sender.getName());
            return true;
        }
        return false;
    }

    public void saveAllData() {
        plugin.getLogger().info("Saving economy data for " + playerBalance.size() + " players...");
        // Implement data persistence
    }

    public void loadAllData() {
        plugin.getLogger().info("Loading economy data...");
        // Implement data loading
    }
}
