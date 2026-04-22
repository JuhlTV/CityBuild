package com.citybuild.gui;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GUIManager {
    private final CityBuildPlugin plugin;
    private final EconomyManager economy;
    private final PlotManager plots;
    private final ShopManager shop;
    private final BankManager bank;
    private final DailyRewardManager dailyRewards;
    private final AchievementManager achievements;
    private final ClanManager clans;
    private final WarpManager warps;
    private final QuestManager quests;
    private final EnchantingManager enchanting;
    private final TradingManager trading;
    private final PlaytimeManager playtime;

    public GUIManager(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
        this.plots = plugin.getPlotManager();
        this.shop = plugin.getShopManager();
        this.bank = plugin.getBankManager();
        this.dailyRewards = plugin.getDailyRewardManager();
        this.achievements = plugin.getAchievementManager();
        this.clans = plugin.getClanManager();
        this.warps = plugin.getWarpManager();
        this.quests = plugin.getQuestManager();
        this.enchanting = plugin.getEnchantingManager();
        this.trading = plugin.getTradingManager();
        this.playtime = plugin.getPlaytimeManager();
    }


    // ==================== MAIN MENU ====================
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("━━ CityBuild Hub ━━", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        long balance = economy.getBalance(player);
        int plotCount = plots.getPlotCount(uuid);
        int achievementCount = achievements.getAchievementCount(uuid);

        // ===== ROW 1: CORE STATS =====
        // Balance
        inv.setItem(2, createItem(Material.GOLD_BLOCK, "💰 BALANCE", NamedTextColor.GOLD,
            Component.text("$" + balance, NamedTextColor.YELLOW),
            Component.text("Click for stats", NamedTextColor.GRAY)));

        // Plots
        inv.setItem(4, createItem(Material.GRASS_BLOCK, "🏗️ YOUR PLOTS", NamedTextColor.GREEN,
            Component.text("Owned: " + plotCount, NamedTextColor.AQUA),
            Component.text("Click for info", NamedTextColor.GRAY)));

        // Achievements
        inv.setItem(6, createItem(Material.DIAMOND, "🏅 ACHIEVEMENTS", NamedTextColor.AQUA,
            Component.text("Unlocked: " + achievementCount + "/10", NamedTextColor.YELLOW),
            Component.text("Click to view", NamedTextColor.GRAY)));

        // Playtime
        long hours = playtime.getPlaytimeHours(uuid);
        inv.setItem(8, createItem(Material.CLOCK, "⏱️ PLAYTIME", NamedTextColor.LIGHT_PURPLE,
            Component.text("Hours: " + hours, NamedTextColor.YELLOW),
            Component.text("Earn $250/hour", NamedTextColor.GRAY)));

        // ===== ROW 2: ECONOMY FEATURES =====
        // Shop
        inv.setItem(11, createItem(Material.EMERALD, "🛍️ SHOP", NamedTextColor.GREEN,
            Component.text("Buy/Sell Items", NamedTextColor.AQUA),
            Component.text("23 items available", NamedTextColor.GRAY)));

        // Bank
        inv.setItem(13, createItem(Material.IRON_NUGGET, "💳 BANK", NamedTextColor.LIGHT_PURPLE,
            Component.text("Transfer Money", NamedTextColor.AQUA),
            Component.text("Send $ to players", NamedTextColor.GRAY)));

        // Daily Reward
        inv.setItem(15, createItem(Material.LIME_CONCRETE, "📅 DAILY REWARD", NamedTextColor.GREEN,
            Component.text("Base: $500/day", NamedTextColor.YELLOW),
            Component.text("Streak bonus!", NamedTextColor.GRAY)));

        // Leaderboard
        inv.setItem(17, createItem(Material.GOLD_INGOT, "🏆 LEADERBOARD", NamedTextColor.GOLD,
            Component.text("Top 10 Richest", NamedTextColor.YELLOW),
            Component.text("See rankings", NamedTextColor.GRAY)));

        // ===== ROW 3: EXPANSION FEATURES =====
        // Clans
        ClanManager.Clan playerClan = clans.getPlayerClan(uuid);
        inv.setItem(20, createItem(Material.REDSTONE_BLOCK, "👥 CLANS", NamedTextColor.RED,
            Component.text(playerClan == null ? "No Clan" : playerClan.name, NamedTextColor.YELLOW),
            Component.text("Create or join", NamedTextColor.GRAY)));

        // Warps
        List<WarpManager.Warp> playerWarps = warps.getPlayerWarps(uuid);
        inv.setItem(22, createItem(Material.PURPLE_CONCRETE, "🚩 WARPS", NamedTextColor.LIGHT_PURPLE,
            Component.text("Created: " + playerWarps.size(), NamedTextColor.YELLOW),
            Component.text("Set & go to warps", NamedTextColor.GRAY)));

        // Quests
        QuestManager.PlayerQuests pq = quests.getPlayerQuests(uuid);
        inv.setItem(24, createItem(Material.BOOK, "📋 QUESTS", NamedTextColor.BLUE,
            Component.text("Daily: " + pq.completedToday, NamedTextColor.YELLOW),
            Component.text("Earn rewards!", NamedTextColor.GRAY)));

        // Enchanting
        inv.setItem(26, createItem(Material.ENCHANTING_TABLE, "✨ ENCHANTING", NamedTextColor.YELLOW,
            Component.text("Upgrade gear", NamedTextColor.AQUA),
            Component.text("3 tier levels", NamedTextColor.GRAY)));

        // Trading
        List<TradingManager.TradeRequest> trades = trading.getPendingTrades(uuid);
        inv.setItem(28, createItem(Material.COMPARATOR, "🤝 TRADING", NamedTextColor.DARK_AQUA,
            Component.text("Pending: " + trades.size(), NamedTextColor.YELLOW),
            Component.text("Player trades", NamedTextColor.GRAY)));

        // Auction House (from v2.0.0)
        inv.setItem(30, createItem(Material.CHEST, "💎 AUCTION HOUSE", NamedTextColor.LIGHT_PURPLE,
            Component.text("Sell/Buy Items", NamedTextColor.AQUA),
            Component.text("Player marketplace", NamedTextColor.GRAY)));

        // ===== ROW 4: NAVIGATION =====
        // Teleport
        inv.setItem(39, createItem(Material.NETHER_STAR, "📍 TELEPORT", NamedTextColor.LIGHT_PURPLE,
            Component.text("Go to plot/world", NamedTextColor.AQUA),
            Component.text("3 second cooldown", NamedTextColor.GRAY)));

        // Menu Button (info)
        inv.setItem(41, createItem(Material.BOOK, "📖 HELP", NamedTextColor.BLUE,
            Component.text("View all commands", NamedTextColor.AQUA),
            Component.text("/citybuild help", NamedTextColor.GRAY)));

        // Settings (placeholder)
        inv.setItem(43, createItem(Material.REDSTONE, "⚙️ SETTINGS", NamedTextColor.GRAY,
            Component.text("Coming soon", NamedTextColor.DARK_GRAY)));

        player.openInventory(inv);
    }

    // ==================== ACHIEVEMENTS MENU ====================
    public void openAchievementsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, Component.text("🏅 ACHIEVEMENTS", NamedTextColor.AQUA, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        int slot = 1;

        for (AchievementManager.Achievement ach : AchievementManager.Achievement.values()) {
            boolean unlocked = achievements.hasAchievement(uuid, ach);
            Material material = unlocked ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE;
            NamedTextColor color = unlocked ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY;
            String status = unlocked ? "✓ UNLOCKED" : "✗ LOCKED";

            inv.setItem(slot, createItem(material, ach.emoji + " " + ach.displayName, color,
                Component.text(ach.description, NamedTextColor.YELLOW),
                Component.text(status, color)));
            slot++;
            if (slot == 9 || slot == 18 || slot == 27 || slot == 36) slot += 2;
        }

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(44, backItem);

        player.openInventory(inv);
    }

    // ==================== CLANS MENU ====================
    public void openClansMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Component.text("👥 CLANS", NamedTextColor.RED, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        ClanManager.Clan playerClan = clans.getPlayerClan(uuid);

        if (playerClan != null) {
            // Show clan info
            inv.setItem(4, createItem(Material.REDSTONE_BLOCK, "Clan: " + playerClan.name, NamedTextColor.GOLD,
                Component.text("Founder: " + Bukkit.getOfflinePlayer(java.util.UUID.fromString(playerClan.founder)).getName(), NamedTextColor.YELLOW),
                Component.text("Members: " + playerClan.members.size(), NamedTextColor.AQUA),
                Component.text("Balance: $" + playerClan.balance, NamedTextColor.GREEN),
                Component.text("Level: " + playerClan.level, NamedTextColor.YELLOW)));

            // Leave clan button
            inv.setItem(11, createItem(Material.BARRIER, "LEAVE CLAN", NamedTextColor.RED,
                Component.text("Click to leave", NamedTextColor.GRAY)));

            // Members list
            int slot = 13;
            for (String memberUuid : playerClan.members) {
                String name = Bukkit.getOfflinePlayer(java.util.UUID.fromString(memberUuid)).getName();
                if (name == null) name = "Unknown";
                inv.setItem(slot, createItem(Material.PLAYER_HEAD, name, NamedTextColor.AQUA, null));
                slot++;
                if (slot == 18) slot = 22;
            }
        } else {
            // Show clan browser
            inv.setItem(11, createItem(Material.LIME_CONCRETE, "CREATE CLAN", NamedTextColor.GREEN,
                Component.text("Use /citybuild clan create", NamedTextColor.GRAY)));

            inv.setItem(15, createItem(Material.YELLOW_CONCRETE, "BROWSE CLANS", NamedTextColor.YELLOW,
                Component.text("See all clans", NamedTextColor.GRAY)));
        }

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(35, backItem);

        player.openInventory(inv);
    }

    // ==================== WARPS MENU ====================
    public void openWarpsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Component.text("🚩 WARPS", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        List<WarpManager.Warp> playerWarps = warps.getPlayerWarps(uuid);

        int slot = 1;
        for (WarpManager.Warp warp : playerWarps) {
            inv.setItem(slot, createItem(Material.PURPLE_CONCRETE, warp.name, NamedTextColor.LIGHT_PURPLE,
                Component.text("World: " + warp.world, NamedTextColor.AQUA),
                Component.text("X:" + (int) warp.x + " Y:" + (int) warp.y + " Z:" + (int) warp.z, NamedTextColor.GRAY)));
            slot++;
            if (slot == 9 || slot == 18 || slot == 27) slot += 2;
        }

        // Create warp button
        inv.setItem(33, createItem(Material.LIME_CONCRETE, "CREATE WARP", NamedTextColor.GREEN,
            Component.text("Use /citybuild setwarp <name>", NamedTextColor.GRAY)));

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(35, backItem);

        player.openInventory(inv);
    }

    // ==================== QUESTS MENU ====================
    public void openQuestsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Component.text("📋 DAILY QUESTS", NamedTextColor.BLUE, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();

        int slot = 1;
        for (QuestManager.Quest quest : QuestManager.Quest.values()) {
            boolean completed = quests.isQuestCompleted(uuid, quest);
            int progress = quests.getProgress(uuid, quest);
            Material mat = completed ? Material.GREEN_CONCRETE : Material.ORANGE_CONCRETE;

            inv.setItem(slot, createItem(mat, quest.displayName, NamedTextColor.YELLOW,
                Component.text("Progress: " + progress + "/" + quest.requirement, NamedTextColor.AQUA),
                Component.text("Reward: $" + quest.reward, NamedTextColor.GREEN),
                Component.text(completed ? "✓ COMPLETED" : "IN PROGRESS", completed ? NamedTextColor.GREEN : NamedTextColor.YELLOW)));
            slot++;
            if (slot == 9 || slot == 18 || slot == 27) slot += 2;
        }

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(35, backItem);

        player.openInventory(inv);
    }

    // ==================== ENCHANTING MENU ====================
    public void openEnchantingMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("✨ ENCHANTING TABLE", NamedTextColor.YELLOW, TextDecoration.BOLD));

        inv.setItem(2, createItem(Material.LIME_CONCRETE, "BASIC ENCHANTS", NamedTextColor.GREEN,
            Component.text("Cost: $1,000", NamedTextColor.YELLOW),
            Component.text("Level I enchants", NamedTextColor.AQUA)));

        inv.setItem(4, createItem(Material.YELLOW_CONCRETE, "ADVANCED ENCHANTS", NamedTextColor.GOLD,
            Component.text("Cost: $5,000", NamedTextColor.YELLOW),
            Component.text("Level II enchants", NamedTextColor.AQUA)));

        inv.setItem(6, createItem(Material.RED_CONCRETE, "LEGENDARY ENCHANTS", NamedTextColor.RED,
            Component.text("Cost: $25,000", NamedTextColor.YELLOW),
            Component.text("Level III enchants", NamedTextColor.AQUA)));

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    // ==================== TRADING MENU ====================
    public void openTradingMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Component.text("🤝 TRADING", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        List<TradingManager.TradeRequest> trades = trading.getPendingTrades(uuid);

        int slot = 1;
        for (TradingManager.TradeRequest trade : trades) {
            String initiatorName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(trade.initiator)).getName();
            if (initiatorName == null) initiatorName = "Unknown";

            inv.setItem(slot, createItem(Material.COMPARATOR, initiatorName + "'s Trade", NamedTextColor.YELLOW,
                Component.text("Offers: " + trade.initiatorItem, NamedTextColor.AQUA),
                Component.text("Wants: " + trade.targetItem, NamedTextColor.AQUA),
                Component.text("Left-click to accept", NamedTextColor.GREEN)));
            slot++;
            if (slot == 9 || slot == 18 || slot == 27) slot += 2;
        }

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(35, backItem);

        player.openInventory(inv);
    }

    // ==================== SHOP MENU ====================

    // ==================== SHOP MENU ====================
    public void openShopMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, Component.text("🛍️ SHOP", NamedTextColor.GREEN, TextDecoration.BOLD));

        int slot = 0;
        for (ShopManager.ShopItem shopItem : shop.getAllShopItems().values()) {
            if (slot >= 44) break;

            ItemStack item = new ItemStack(Material.valueOf(shopItem.material));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(shopItem.displayName, NamedTextColor.YELLOW, TextDecoration.BOLD));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Buy: $" + shopItem.buyPrice, NamedTextColor.GREEN));
                lore.add(Component.text("Sell: $" + shopItem.sellPrice, NamedTextColor.RED));
                lore.add(Component.text(" "));
                lore.add(Component.text("← Left-click to BUY", NamedTextColor.AQUA));
                lore.add(Component.text("→ Right-click to SELL", NamedTextColor.AQUA));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
        }

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(44, backItem);

        player.openInventory(inv);
    }

    // ==================== LEADERBOARD MENU ====================
    public void openLeaderboardMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("🏆 TOP 10 RICHEST", NamedTextColor.GOLD, TextDecoration.BOLD));

        List<Map.Entry<String, Long>> top10 = economy.getLeaderboard(10);
        int slot = 1;

        for (int i = 0; i < top10.size() && slot < 26; i++) {
            Map.Entry<String, Long> entry = top10.get(i);
            String playerName = Bukkit.getOfflinePlayer(java.util.UUID.fromString(entry.getKey())).getName();
            if (playerName == null) playerName = "Unknown";

            Material mat = switch (i) {
                case 0 -> Material.GOLD_BLOCK;
                case 1 -> Material.IRON_BLOCK;
                case 2 -> Material.COPPER_BLOCK;
                default -> Material.GRAY_CONCRETE;
            };

            String medal = switch (i) {
                case 0 -> "🥇";
                case 1 -> "🥈";
                case 2 -> "🥉";
                default -> "#" + (i + 1);
            };

            inv.setItem(slot, createItem(mat, medal + " " + playerName, NamedTextColor.GOLD,
                Component.text("Balance: $" + entry.getValue(), NamedTextColor.YELLOW)));
            slot += 2;
        }

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    // ==================== STATS MENU ====================
    public void openStatsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("📊 PLAYER STATS", NamedTextColor.BLUE, TextDecoration.BOLD));

        String uuid = player.getUniqueId().toString();
        long balance = economy.getBalance(player);
        int plotCount = plots.getPlotCount(uuid);
        long totalDaily = dailyRewards.getTotalRewards(player);
        int achievementCount = achievements.getAchievementCount(uuid);
        long playtimeHours = playtime.getPlaytimeHours(uuid);

        inv.setItem(2, createItem(Material.GOLD_BLOCK, "💰 BALANCE", NamedTextColor.GOLD,
            Component.text("$" + balance, NamedTextColor.YELLOW)));

        inv.setItem(4, createItem(Material.GRASS_BLOCK, "🏗️ PLOTS", NamedTextColor.GREEN,
            Component.text(plotCount + " plots owned", NamedTextColor.AQUA)));

        inv.setItem(6, createItem(Material.DIAMOND, "🏅 ACHIEVEMENTS", NamedTextColor.AQUA,
            Component.text(achievementCount + "/10 unlocked", NamedTextColor.YELLOW)));

        inv.setItem(11, createItem(Material.LIME_CONCRETE, "📅 DAILY REWARDS", NamedTextColor.GREEN,
            Component.text("Total: $" + totalDaily, NamedTextColor.AQUA)));

        inv.setItem(13, createItem(Material.CLOCK, "⏱️ PLAYTIME", NamedTextColor.LIGHT_PURPLE,
            Component.text("Hours: " + playtimeHours, NamedTextColor.YELLOW)));

        inv.setItem(15, createItem(Material.BOOK, "📖 LEVEL", NamedTextColor.BLUE,
            Component.text("Prestige Level: 1", NamedTextColor.YELLOW),
            Component.text("Coming soon!", NamedTextColor.GRAY)));

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    // ==================== BANK MENU ====================
    public void openBankMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("💳 BANK", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));

        inv.setItem(11, createItem(Material.PAPER, "📝 TRANSFER MONEY", NamedTextColor.LIGHT_PURPLE,
            Component.text("Use /citybuild pay <player> <amount>", NamedTextColor.YELLOW),
            Component.text("to transfer money to other players", NamedTextColor.GRAY)));

        inv.setItem(13, createItem(Material.EMERALD, "💰 RECEIVING", NamedTextColor.GREEN,
            Component.text("Any player can send", NamedTextColor.GRAY),
            Component.text("you money", NamedTextColor.GRAY)));

        ItemStack backItem = createItem(Material.ARROW, "← BACK", NamedTextColor.RED, null);
        inv.setItem(26, backItem);

        player.openInventory(inv);
    }

    // ==================== HELPER METHOD ====================
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
                if (!loreList.isEmpty()) {
                    meta.lore(loreList);
                }
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
