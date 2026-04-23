package com.citybuild.gui;

import com.citybuild.features.leaderboards.LeaderboardManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for displaying leaderboards
 */
public class LeaderboardGUI extends BaseInventoryGUI {

    private final LeaderboardManager leaderboardManager;
    private LeaderboardManager.LeaderboardType currentType;

    public LeaderboardGUI(Player player, LeaderboardManager leaderboardManager) {
        super(player, "§6📊 LEADERBOARDS", 54);
        this.leaderboardManager = leaderboardManager;
        this.currentType = LeaderboardManager.LeaderboardType.RICHEST;
    }

    @Override
    public void buildGUI() {
        inventory.clear();
        fillBorders();

        // Title
        ItemStack title = createCustomItem(Material.GOLDEN_BLOCK, "§6📊 LEADERBOARDS", "§7Click to change type");
        inventory.setItem(4, title);

        // Leaderboard type buttons (row 1)
        inventory.setItem(10, createCustomItem(Material.YELLOW_CONCRETE, "§6💰 RICHEST", "§eTop wealthiest players"));
        inventory.setItem(12, createCustomItem(Material.LIME_CONCRETE, "§a🌾 FARMERS", "§eTop farming income"));
        inventory.setItem(14, createCustomItem(Material.EMERALD_BLOCK, "§a🏗️ PLOT OWNERS", "§eTop plot owners"));
        inventory.setItem(16, createCustomItem(Material.PURPLE_BLOCK, "§d🏆 ACHIEVEMENTS", "§eTop achievements"));

        // Display current leaderboard (rows 2-4)
        String leaderboard = leaderboardManager.getFormattedLeaderboard(currentType);
        String[] lines = leaderboard.split("\n");

        int slot = 19;
        for (int i = 0; i < Math.min(lines.length, 25); i++) {
            if (slot % 9 != 0 && slot % 9 != 8 && slot < 45) {
                ItemStack item = createCustomItem(Material.PAPER, lines[i]);
                inventory.setItem(slot, item);
                slot++;
            }
        }

        // Back button
        ItemStack back = createCustomItem(Material.BARRIER, "§c✕ Close");
        inventory.setItem(49, back);
    }

    @Override
    public void handleClick(int slot, ItemStack item) {
        if (slot == 10) {
            currentType = LeaderboardManager.LeaderboardType.RICHEST;
            buildGUI();
        } else if (slot == 12) {
            currentType = LeaderboardManager.LeaderboardType.FARMERS;
            buildGUI();
        } else if (slot == 14) {
            currentType = LeaderboardManager.LeaderboardType.PLOT_OWNERS;
            buildGUI();
        } else if (slot == 16) {
            currentType = LeaderboardManager.LeaderboardType.ACHIEVEMENTS;
            buildGUI();
        } else if (slot == 49) {
            player.closeInventory();
        }
    }
}
