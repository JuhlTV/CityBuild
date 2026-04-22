package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.gui.GUIManager;
import com.citybuild.managers.EconomyManager;
import com.citybuild.managers.ShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    private final CityBuildPlugin plugin;
    private final GUIManager guiManager;
    private final EconomyManager economy;
    private final ShopManager shop;

    public InventoryClickListener(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.guiManager = plugin.getGUIManager();
        this.economy = plugin.getEconomyManager();
        this.shop = plugin.getShopManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();
        
        // Handle clicks only in our custom GUIs
        if (title.contains("CityBuild") || title.contains("Leaderboard") || title.contains("Player Stats") || title.contains("Bank")) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            // Main Menu handling
            if (title.contains("CityBuild Hub")) {
                handleMainMenuClick(player, clicked);
            }
            // Shop handling
            else if (title.contains("CityBuild Shop")) {
                handleShopClick(player, event, clicked);
            }
            // Stats Menu
            else if (title.contains("Player Stats")) {
                handleStatsClick(player, clicked);
            }
            // Bank Menu
            else if (title.contains("Bank")) {
                handleBankClick(player, clicked);
            }
            // Leaderboard Menu
            else if (title.contains("Leaderboard")) {
                handleLeaderboardClick(player, clicked);
            }
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clicked) {
        String displayName = clicked.getItemMeta() != null ? 
            clicked.getItemMeta().getDisplayName() : "";

        if (displayName.contains("Shop")) {
            guiManager.openShopMenu(player);
        } else if (displayName.contains("Bank")) {
            guiManager.openBankMenu(player);
        } else if (displayName.contains("Daily")) {
            if (plugin.getDailyRewardManager().canClaimDaily(player)) {
                long amount = plugin.getDailyRewardManager().claimDailyReward(player);
                player.closeInventory();
                player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("✓ You claimed $" + amount + "!", NamedTextColor.GREEN)));
            } else {
                player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                    .append(Component.text("❌ Come back tomorrow for your next reward!", NamedTextColor.RED)));
            }
        } else if (displayName.contains("Leaderboard")) {
            guiManager.openLeaderboardMenu(player);
        } else if (displayName.contains("Teleport")) {
            player.closeInventory();
            plugin.getPlotManager().createPlotFrame(player.getUniqueId().toString());
            player.teleport(plugin.getPlotManager().getFirstPlotLocation(player.getUniqueId().toString()));
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("✓ Teleported to your plot!", NamedTextColor.GREEN)));
        } else if (displayName.contains("Your Plots")) {
            guiManager.openStatsMenu(player);
        }
    }

    private void handleShopClick(Player player, InventoryClickEvent event, ItemStack clicked) {
        String displayName = clicked.getItemMeta() != null ? 
            clicked.getItemMeta().getDisplayName() : "";

        // Back button
        if (displayName.contains("Back")) {
            guiManager.openMainMenu(player);
            return;
        }

        // Try to process as shop item
        Material material = clicked.getType();
        ShopManager.ShopItem shopItem = shop.getShopItem(material.toString());
        
        if (shopItem != null) {
            if (event.isLeftClick()) {
                // Buy
                long balance = economy.getBalance(player);
                if (balance >= shopItem.buyPrice) {
                    economy.removeBalance(player, shopItem.buyPrice);
                    player.getInventory().addItem(new ItemStack(material, 1));
                    player.sendMessage(Component.text("[CityBuild Shop] ", NamedTextColor.GREEN)
                        .append(Component.text("✓ Bought " + shopItem.displayName + " for $" + shopItem.buyPrice, NamedTextColor.AQUA)));
                } else {
                    player.sendMessage(Component.text("[CityBuild Shop] ", NamedTextColor.RED)
                        .append(Component.text("❌ Not enough money! Need $" + shopItem.buyPrice, NamedTextColor.YELLOW)));
                }
            } else if (event.isRightClick()) {
                // Sell - count items in inventory
                int hasAmount = 0;
                for (ItemStack stack : player.getInventory().getContents()) {
                    if (stack != null && stack.getType() == material) {
                        hasAmount += stack.getAmount();
                    }
                }

                if (hasAmount > 0) {
                    long totalPrice = (long) hasAmount * shopItem.sellPrice;
                    player.getInventory().removeItem(new ItemStack(material, hasAmount));
                    economy.addBalance(player, totalPrice);
                    player.sendMessage(Component.text("[CityBuild Shop] ", NamedTextColor.GREEN)
                        .append(Component.text("✓ Sold " + hasAmount + "x " + shopItem.displayName + " for $" + totalPrice, NamedTextColor.AQUA)));
                } else {
                    player.sendMessage(Component.text("[CityBuild Shop] ", NamedTextColor.RED)
                        .append(Component.text("❌ You don't have any " + shopItem.displayName, NamedTextColor.YELLOW)));
                }
            }
            guiManager.openShopMenu(player);
        }
    }

    private void handleStatsClick(Player player, ItemStack clicked) {
        String displayName = clicked.getItemMeta() != null ? 
            clicked.getItemMeta().getDisplayName() : "";

        if (displayName.contains("Back")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleBankClick(Player player, ItemStack clicked) {
        String displayName = clicked.getItemMeta() != null ? 
            clicked.getItemMeta().getDisplayName() : "";

        if (displayName.contains("Back")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleLeaderboardClick(Player player, ItemStack clicked) {
        String displayName = clicked.getItemMeta() != null ? 
            clicked.getItemMeta().getDisplayName() : "";

        if (displayName.contains("Back")) {
            guiManager.openMainMenu(player);
        }
    }
}
