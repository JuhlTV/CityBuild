package com.citybuild.gui;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.PlotManager;
import com.citybuild.managers.ShopManager;
import com.citybuild.managers.BankManager;
import com.citybuild.managers.DailyRewardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    private final CityBuildPlugin plugin;
    private final EconomyManager economy;
    private final PlotManager plots;
    private final ShopManager shop;
    private final BankManager bank;
    private final DailyRewardManager dailyRewards;

    public GUIManager(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
        this.plots = plugin.getPlotManager();
        this.shop = plugin.getShopManager();
        this.bank = plugin.getBankManager();
        this.dailyRewards = plugin.getDailyRewardManager();
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("CityBuild Hub", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));

        // Balance info
        long balance = economy.getBalance(player);
        ItemStack balanceItem = createItem(Material.GOLD_BLOCK, "💰 " + "Your Balance", 
            NamedTextColor.GOLD, Component.text("$" + balance, NamedTextColor.YELLOW));
        inv.setItem(4, balanceItem);

        // Plot Info
        ItemStack plotItem = createItem(Material.GRASS_BLOCK, "🏗️ " + "Your Plots",
            NamedTextColor.GREEN, Component.text("Plots: " + plots.getPlotCount(player.getUniqueId().toString()), NamedTextColor.AQUA));
        inv.setItem(2, plotItem);

        // Shop
        ItemStack shopItem = createItem(Material.EMERALD, "🛍️ " + "Shop",
            NamedTextColor.GREEN, Component.text("Click to buy/sell items", NamedTextColor.GRAY));
        inv.setItem(11, shopItem);

        // Bank
        ItemStack bankItem = createItem(Material.IRON_NUGGET, "💳 " + "Bank",
            NamedTextColor.LIGHT_PURPLE, Component.text("Send money to players", NamedTextColor.GRAY));
        inv.setItem(12, bankItem);

        // Daily Reward
        ItemStack dailyItem = createItem(Material.LIME_CONCRETE, "📅 " + "Daily Reward",
            NamedTextColor.GREEN, Component.text("Claim your daily coins!", NamedTextColor.GRAY));
        inv.setItem(13, dailyItem);

        // Leaderboard
        ItemStack leaderboardItem = createItem(Material.DIAMOND, "🏆 " + "Leaderboard",
            NamedTextColor.AQUA, Component.text("Top 10 richest players", NamedTextColor.GRAY));
        inv.setItem(14, leaderboardItem);

        // Teleport to Plot
        ItemStack tpPlotItem = createItem(Material.NETHER_STAR, "📍 " + "Teleport to Plot",
            NamedTextColor.LIGHT_PURPLE, Component.text("Go to your plot now", NamedTextColor.GRAY));
        inv.setItem(15, tpPlotItem);

        // Help/Info
        ItemStack helpItem = createItem(Material.BOOK, "📖 " + "Help",
            NamedTextColor.BLUE, Component.text("View all commands", NamedTextColor.GRAY));
        inv.setItem(26, helpItem);

        player.openInventory(inv);
    }

    public void openShopMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, Component.text("CityBuild Shop", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));

        int slot = 0;
        for (ShopManager.ShopItem shopItem : shop.getAllShopItems().values()) {
            if (slot >= 44) break; // Leave last slot for back button

            ItemStack item = new ItemStack(Material.valueOf(shopItem.material));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(shopItem.displayName, NamedTextColor.YELLOW));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Buy: $" + shopItem.buyPrice, NamedTextColor.GREEN));
                lore.add(Component.text("Sell: $" + shopItem.sellPrice, NamedTextColor.RED));
                lore.add(Component.text(" "));
                lore.add(Component.text("Left-click to buy", NamedTextColor.AQUA));
                lore.add(Component.text("Right-click to sell", NamedTextColor.AQUA));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "← Back", NamedTextColor.RED, null);
        inv.setItem(44, backItem);

        player.openInventory(inv);
    }

    public void openStatsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Player Stats", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        long balance = economy.getBalance(player);
        int plotCount = plots.getPlotCount(uuid);

        // Balance
        ItemStack balanceItem = createItem(Material.GOLD_BLOCK, "💰 Balance", NamedTextColor.GOLD, 
            Component.text("$" + balance, NamedTextColor.YELLOW));
        inv.setItem(11, balanceItem);

        // Plots
        ItemStack plotItem = createItem(Material.GRASS_BLOCK, "🏗️ Plots Owned", NamedTextColor.GREEN,
            Component.text(plotCount + " plots", NamedTextColor.AQUA));
        inv.setItem(13, plotItem);

        // Daily Rewards
        long totalDaily = dailyRewards.getTotalRewards(player);
        ItemStack dailyItem = createItem(Material.LIME_CONCRETE, "📅 Total Rewards Claimed", NamedTextColor.GREEN,
            Component.text("$" + totalDaily, NamedTextColor.AQUA));
        inv.setItem(15, dailyItem);

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "← Back", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    public void openBankMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Bank - Transfer Money", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));

        ItemStack infoItem = createItem(Material.PAPER, "💳 Bank Transfer", NamedTextColor.LIGHT_PURPLE,
            Component.text("Use /citybuild pay <player> <amount>", NamedTextColor.GRAY),
            Component.text("to transfer money", NamedTextColor.GRAY));
        inv.setItem(13, infoItem);

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "← Back", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    public void openLeaderboardMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Leaderboard - Top 10", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));

        List<java.util.Map.Entry<String, Long>> top10 = economy.getLeaderboard(10);
        int slot = 1;

        for (int i = 0; i < top10.size() && slot < 26; i++) {
            java.util.Map.Entry<String, Long> entry = top10.get(i);
            String playerName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(entry.getKey())).getName();
            if (playerName == null) playerName = "Unknown";

            Material medalMaterial = switch (i) {
                case 0 -> Material.GOLD_BLOCK;
                case 1 -> Material.IRON_BLOCK;
                case 2 -> Material.COPPER_BLOCK;
                default -> Material.GRAY_CONCRETE;
            };

            String medal = switch (i) {
                case 0 -> "🥇 ";
                case 1 -> "🥈 ";
                case 2 -> "🥉 ";
                default -> (i + 1) + ". ";
            };

            ItemStack item = createItem(medalMaterial, medal + playerName, NamedTextColor.YELLOW,
                Component.text("$" + entry.getValue(), NamedTextColor.GOLD));
            inv.setItem(slot, item);
            slot += 2;
        }

        // Back button
        ItemStack backItem = createItem(Material.ARROW, "← Back", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    /**
     * Helper method to create an ItemStack with a name and lore
     */
    private ItemStack createItem(Material material, String name, NamedTextColor color, Component... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, color, TextDecoration.BOLD));
            if (lore.length > 0 && lore[0] != null) {
                List<Component> loreList = new ArrayList<>();
                for (Component comp : lore) {
                    if (comp != null) {
                        loreList.add(comp);
                    }
                }
                meta.lore(loreList);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
