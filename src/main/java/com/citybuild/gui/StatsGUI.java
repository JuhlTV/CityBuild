package com.citybuild.gui;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.farming.PlayerFarmData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for displaying player statistics and achievements
 */
public class StatsGUI extends BaseInventoryGUI {

    private final CityBuildPlugin plugin;
    private final Player targetPlayer;

    public StatsGUI(Player player, Player targetPlayer, CityBuildPlugin plugin) {
        super(player, "§6📈 PLAYER STATS - " + targetPlayer.getName(), 54);
        this.plugin = plugin;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void buildGUI() {
        inventory.clear();
        fillBorders();

        // Player head display
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta skullMeta = (org.bukkit.inventory.meta.SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(targetPlayer);
            skullMeta.setDisplayName("§6" + targetPlayer.getName());
            head.setItemMeta(skullMeta);
        }
        inventory.setItem(4, head);

        // Economy stats
        double balance = plugin.getEconomyManager().getPlayerBalance(targetPlayer.getUniqueId());
        ItemStack economyItem = createCustomItem(Material.GOLD_INGOT,
            "§6💰 BALANCE",
            "§e$" + String.format("%.2f", balance));
        inventory.setItem(10, economyItem);

        // Plot stats
        int plotCount = plugin.getPlotManager().getPlayerPlotCount(targetPlayer.getUniqueId());
        ItemStack plotItem = createCustomItem(Material.GRASS_BLOCK,
            "§6🏗️ PLOTS OWNED",
            "§e" + plotCount + " plots");
        inventory.setItem(12, plotItem);

        // Achievement stats
        int achievements = plugin.getAchievementManager().getUnlockedCount(targetPlayer.getUniqueId());
        int achievementPoints = plugin.getAchievementManager().getPlayerAchievementPoints(targetPlayer.getUniqueId());
        ItemStack achievementItem = createCustomItem(Material.NETHER_STAR,
            "§6🏆 ACHIEVEMENTS",
            "§e" + achievements + "/16 unlocked",
            "§e" + achievementPoints + " points");
        inventory.setItem(14, achievementItem);

        // Rank stats
        var rank = plugin.getRankingManager().getPlayerRank(targetPlayer.getUniqueId(), achievementPoints);
        ItemStack rankItem = createCustomItem(Material.DIAMOND,
            rank.getFormattedDisplay(),
            "§e" + rank.getMinPoints() + " - " + rank.getMaxPoints() + " points");
        inventory.setItem(16, rankItem);

        // Guild stats
        var guild = plugin.getGuildManager().getPlayerGuild(targetPlayer.getUniqueId());
        String guildName = guild != null ? guild.getGuildName() + " [" + guild.getGuildTag() + "]" : "§cNo Guild";
        ItemStack guildItem = createCustomItem(Material.BARRIER,
            "§6👥 GUILD",
            "§e" + guildName);
        inventory.setItem(28, guildItem);

        // Farming stats (if exists)
        PlayerFarmData farmData = plugin.getFarmDataManager().getPlayerFarmData(targetPlayer.getUniqueId());
        if (farmData != null) {
            double farmIncome = farmData.getCoinsEarnedTotal();
            ItemStack farmItem = createCustomItem(Material.WHEAT,
                "§6🌾 FARMING INCOME",
                "§e$" + String.format("%.2f", farmIncome));
            inventory.setItem(30, farmItem);
        }

        // Guild treasury (if in guild)
        if (guild != null) {
            double treasury = guild.getTreasury();
            ItemStack treasuryItem = createCustomItem(Material.CHEST,
                "§6💎 GUILD TREASURY",
                "§e$" + String.format("%.2f", treasury));
            inventory.setItem(32, treasuryItem);
        }

        // Trading stats
        int tradingHistory = 0; // Placeholder - would need trade history tracking
        ItemStack tradingItem = createCustomItem(Material.COMPARATOR,
            "§6💱 TRADES",
            "§eCompleted: " + tradingHistory);
        inventory.setItem(34, tradingItem);

        // Close button
        ItemStack close = createCustomItem(Material.BARRIER, "§c✕ Close");
        inventory.setItem(49, close);
    }

    @Override
    public void handleClick(int slot, ItemStack item) {
        if (slot == 49) {
            player.closeInventory();
        }
    }
}
