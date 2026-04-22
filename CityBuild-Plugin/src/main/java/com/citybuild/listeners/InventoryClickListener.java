package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.gui.GUIManager;
import com.citybuild.managers.*;
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
    private final AchievementManager achievements;
    private final ClanManager clans;
    private final WarpManager warps;
    private final QuestManager quests;
    private final EnchantingManager enchanting;
    private final TradingManager trading;

    public InventoryClickListener(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.guiManager = plugin.getGUIManager();
        this.economy = plugin.getEconomyManager();
        this.shop = plugin.getShopManager();
        this.achievements = plugin.getAchievementManager();
        this.clans = plugin.getClanManager();
        this.warps = plugin.getWarpManager();
        this.quests = plugin.getQuestManager();
        this.enchanting = plugin.getEnchantingManager();
        this.trading = plugin.getTradingManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();
        
        // Handle clicks only in our custom GUIs
        if (isCityBuildGUI(title)) {
            event.setCancelled(true);
            
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            // Route to appropriate handler
            if (title.contains("CityBuild Hub")) {
                handleMainMenuClick(player, clicked);
            } else if (title.contains("🛍️ SHOP")) {
                handleShopClick(player, event, clicked);
            } else if (title.contains("🏅 ACHIEVEMENTS")) {
                handleAchievementsClick(player, clicked);
            } else if (title.contains("👥 CLANS")) {
                handleClansClick(player, clicked);
            } else if (title.contains("🚩 WARPS")) {
                handleWarpsClick(player, clicked);
            } else if (title.contains("📋 DAILY QUESTS")) {
                handleQuestsClick(player, clicked);
            } else if (title.contains("✨ ENCHANTING")) {
                handleEnchantingClick(player, clicked);
            } else if (title.contains("🤝 TRADING")) {
                handleTradingClick(player, clicked);
            } else if (title.contains("📊 PLAYER STATS")) {
                handleStatsClick(player, clicked);
            } else if (title.contains("💳 BANK")) {
                handleBankClick(player, clicked);
            } else if (title.contains("🏆 TOP 10")) {
                handleLeaderboardClick(player, clicked);
            }
        }
    }

    private boolean isCityBuildGUI(String title) {
        return title.contains("CityBuild") || title.contains("SHOP") || title.contains("ACHIEVEMENTS") ||
               title.contains("CLANS") || title.contains("WARPS") || title.contains("QUESTS") ||
               title.contains("ENCHANTING") || title.contains("TRADING") || title.contains("STATS") ||
               title.contains("BANK") || title.contains("TOP 10");
    }

    private void handleMainMenuClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BALANCE")) {
            guiManager.openStatsMenu(player);
        } else if (displayName.contains("YOUR PLOTS")) {
            guiManager.openStatsMenu(player);
        } else if (displayName.contains("ACHIEVEMENTS")) {
            guiManager.openAchievementsMenu(player);
        } else if (displayName.contains("PLAYTIME")) {
            guiManager.openStatsMenu(player);
        } else if (displayName.contains("SHOP")) {
            guiManager.openShopMenu(player);
        } else if (displayName.contains("BANK")) {
            guiManager.openBankMenu(player);
        } else if (displayName.contains("DAILY REWARD")) {
            handleDailyRewardClick(player);
        } else if (displayName.contains("LEADERBOARD")) {
            guiManager.openLeaderboardMenu(player);
        } else if (displayName.contains("CLANS")) {
            guiManager.openClansMenu(player);
        } else if (displayName.contains("WARPS")) {
            guiManager.openWarpsMenu(player);
        } else if (displayName.contains("QUESTS")) {
            guiManager.openQuestsMenu(player);
        } else if (displayName.contains("ENCHANTING")) {
            guiManager.openEnchantingMenu(player);
        } else if (displayName.contains("TRADING")) {
            guiManager.openTradingMenu(player);
        } else if (displayName.contains("AUCTION HOUSE")) {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.BLUE)
                .append(Component.text("Use /auction create to list items", NamedTextColor.AQUA)));
        } else if (displayName.contains("TELEPORT")) {
            guiManager.openMainMenu(player);
            player.closeInventory();
            plugin.getPlotManager().createPlotFrame(player.getUniqueId().toString());
            player.teleport(plugin.getPlotManager().getFirstPlotLocation(player.getUniqueId().toString()));
        } else if (displayName.contains("HELP")) {
            player.closeInventory();
            sendHelpMessage(player);
        }
    }

    private void handleDailyRewardClick(Player player) {
        DailyRewardManager dailyRewards = plugin.getDailyRewardManager();
        if (dailyRewards.canClaimDaily(player)) {
            long amount = dailyRewards.claimDailyReward(player);
            int streak = dailyRewards.getConsecutiveDays(player);
            player.closeInventory();
            player.sendMessage(Component.text("[CityBuild Daily] ", NamedTextColor.GREEN)
                .append(Component.text("✓ You claimed $" + amount + "! ", NamedTextColor.AQUA))
                .append(Component.text("(Streak: " + streak + " days)", NamedTextColor.YELLOW)));
        } else {
            player.sendMessage(Component.text("[CityBuild] ", NamedTextColor.RED)
                .append(Component.text("❌ Come back tomorrow for your next reward!", NamedTextColor.YELLOW)));
        }
    }

    private void handleShopClick(Player player, InventoryClickEvent event, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
            return;
        }

        Material material = clicked.getType();
        ShopManager.ShopItem shopItem = shop.getShopItem(material.toString());
        
        if (shopItem != null) {
            if (event.isLeftClick()) {
                // BUY
                long balance = economy.getBalance(player);
                if (balance >= shopItem.buyPrice) {
                    economy.removeBalance(player, shopItem.buyPrice);
                    player.getInventory().addItem(new ItemStack(material, 1));
                    player.sendMessage(Component.text("[Shop] ", NamedTextColor.GREEN)
                        .append(Component.text("✓ Bought " + shopItem.displayName + " for $" + shopItem.buyPrice, NamedTextColor.AQUA)));
                    
                    // Check quest progress
                    quests.addProgress(player.getUniqueId().toString(), QuestManager.Quest.TRADE_ITEMS, 1);
                } else {
                    player.sendMessage(Component.text("[Shop] ", NamedTextColor.RED)
                        .append(Component.text("❌ Insufficient funds! Need $" + shopItem.buyPrice, NamedTextColor.YELLOW)));
                }
            } else if (event.isRightClick()) {
                // SELL
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
                    player.sendMessage(Component.text("[Shop] ", NamedTextColor.GREEN)
                        .append(Component.text("✓ Sold " + hasAmount + "x " + shopItem.displayName + " for $" + totalPrice, NamedTextColor.AQUA)));
                    
                    // Check quest progress
                    quests.addProgress(player.getUniqueId().toString(), QuestManager.Quest.TRADE_ITEMS, 1);
                } else {
                    player.sendMessage(Component.text("[Shop] ", NamedTextColor.RED)
                        .append(Component.text("❌ You don't have any " + shopItem.displayName, NamedTextColor.YELLOW)));
                }
            }
            guiManager.openShopMenu(player);
        }
    }

    private void handleAchievementsClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleClansClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        } else if (displayName.contains("LEAVE CLAN")) {
            String uuid = player.getUniqueId().toString();
            ClanManager.Clan clan = clans.getPlayerClan(uuid);
            if (clan != null) {
                clans.removeMember(clan.name, uuid);
                player.sendMessage(Component.text("[Clans] ", NamedTextColor.RED)
                    .append(Component.text("✓ You left the clan!", NamedTextColor.YELLOW)));
                guiManager.openClansMenu(player);
            }
        }
    }

    private void handleWarpsClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleQuestsClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleEnchantingClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        } else if (displayName.contains("BASIC")) {
            // Enchant item in hand with Basic tier
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand.getType() != Material.AIR) {
                if (enchanting.enchantItem(player.getUniqueId().toString(), inHand, EnchantingManager.EnchantTier.BASIC)) {
                    player.sendMessage(Component.text("[Enchanting] ", NamedTextColor.YELLOW)
                        .append(Component.text("✓ Item enchanted! Cost: $1,000", NamedTextColor.GREEN)));
                    guiManager.openEnchantingMenu(player);
                } else {
                    player.sendMessage(Component.text("[Enchanting] ", NamedTextColor.RED)
                        .append(Component.text("❌ Not enough money!", NamedTextColor.YELLOW)));
                }
            }
        } else if (displayName.contains("ADVANCED")) {
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand.getType() != Material.AIR) {
                if (enchanting.enchantItem(player.getUniqueId().toString(), inHand, EnchantingManager.EnchantTier.ADVANCED)) {
                    player.sendMessage(Component.text("[Enchanting] ", NamedTextColor.YELLOW)
                        .append(Component.text("✓ Item enchanted! Cost: $5,000", NamedTextColor.GREEN)));
                    guiManager.openEnchantingMenu(player);
                } else {
                    player.sendMessage(Component.text("[Enchanting] ", NamedTextColor.RED)
                        .append(Component.text("❌ Not enough money!", NamedTextColor.YELLOW)));
                }
            }
        } else if (displayName.contains("LEGENDARY")) {
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand.getType() != Material.AIR) {
                if (enchanting.enchantItem(player.getUniqueId().toString(), inHand, EnchantingManager.EnchantTier.LEGENDARY)) {
                    player.sendMessage(Component.text("[Enchanting] ", NamedTextColor.YELLOW)
                        .append(Component.text("✓ Item enchanted! Cost: $25,000", NamedTextColor.GREEN)));
                    guiManager.openEnchantingMenu(player);
                } else {
                    player.sendMessage(Component.text("[Enchanting] ", NamedTextColor.RED)
                        .append(Component.text("❌ Not enough money!", NamedTextColor.YELLOW)));
                }
            }
        }
    }

    private void handleTradingClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleStatsClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleBankClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleLeaderboardClick(Player player, ItemStack clicked) {
        if (clicked.getItemMeta() == null) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (displayName.contains("BACK")) {
            guiManager.openMainMenu(player);
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("━━━━━━━━━━ CityBuild Commands ━━━━━━━━━━", NamedTextColor.DARK_GREEN));
        player.sendMessage(Component.text("/citybuild menu", NamedTextColor.AQUA).append(Component.text(" - Open main menu", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild balance", NamedTextColor.AQUA).append(Component.text(" - Check balance", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild shop", NamedTextColor.AQUA).append(Component.text(" - Open shop", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild leaderboard", NamedTextColor.AQUA).append(Component.text(" - View leaderboard", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild pay <player> <amount>", NamedTextColor.AQUA).append(Component.text(" - Send money", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild daily", NamedTextColor.AQUA).append(Component.text(" - Claim daily reward", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild clan create <name>", NamedTextColor.AQUA).append(Component.text(" - Create clan", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/citybuild setwarp <name>", NamedTextColor.AQUA).append(Component.text(" - Create warp", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GREEN));
    }
}
