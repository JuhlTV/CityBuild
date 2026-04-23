package com.citybuild.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.economy.EconomyManager;

import java.util.*;

/**
 * GUIManager - Handles all inventory-based GUI menus
 * Includes proper null-safety checks and error handling
 */
@SuppressWarnings("deprecation")
public class GUIManager {
    private final CityBuildPlugin plugin;
    private final EconomyManager economyManager;
    
    private static final String MAIN_MENU_TYPE = "main_menu";
    private static final String PLOT_MENU_TYPE = "plot_menu";
    private static final String ENCHANTING_MENU_TYPE = "enchanting_menu";
    
    public GUIManager(CityBuildPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }
    
    /**
     * Opens the main menu for a player
     */
    public void openMainMenu(Player player) {
        if (player == null) return;
        
        Inventory mainMenu = Bukkit.createInventory(
            new CityBuildInventoryHolder(MAIN_MENU_TYPE), 
            27, 
            "§l§aCityBuild Menu"
        );
        
        mainMenu.setItem(10, createItem(Material.GRASS_BLOCK, "§aPlot Management", 
            "Manage your plots", "Buy, sell, and configure"));
        mainMenu.setItem(12, createItem(Material.EMERALD, "§aShop", 
            "Visit the shop", "Buy items and upgrades"));
        mainMenu.setItem(14, createItem(Material.GOLD_INGOT, "§aEconomy", 
            "Check balance", "View your balance and transactions"));
        mainMenu.setItem(16, createItem(Material.ENCHANTED_BOOK, "§aEnchanting", 
            "Enchant items", "Upgrade your gear"));
        
        player.openInventory(mainMenu);
    }
    
    /**
     * Opens the plot management menu for a player
     */
    public void openPlotMenu(Player player) {
        if (player == null) return;
        
        Inventory plotMenu = Bukkit.createInventory(
            new CityBuildInventoryHolder(PLOT_MENU_TYPE), 
            27, 
            "§l§aPlot Management"
        );
        
        plotMenu.setItem(11, createItem(Material.LIME_CONCRETE, "§aBuy Plot", 
            "Purchase a new plot", "Cost: 1000 coins"));
        plotMenu.setItem(13, createItem(Material.BARRIER, "§cSell Plot", 
            "Sell your plot", "Get 80% back"));
        plotMenu.setItem(15, createItem(Material.REDSTONE, "§6Plot Settings", 
            "Configure your plot", "Manage permissions"));
        
        plotMenu.setItem(26, createItem(Material.ARROW, "§7Back", 
            "Return to main menu"));
        
        player.openInventory(plotMenu);
    }
    
    /**
     * Opens the enchanting shop menu
     */
    public void openEnchantingMenu(Player player) {
        if (player == null) return;
        
        Inventory enchantingMenu = Bukkit.createInventory(
            new CityBuildInventoryHolder(ENCHANTING_MENU_TYPE), 
            27, 
            "§l§aEnchanting Shop"
        );
        
        enchantingMenu.setItem(10, createItem(Material.DIAMOND_SWORD, "§aSharpness IV", 
            "Make your sword stronger", "Cost: 500 coins"));
        enchantingMenu.setItem(12, createItem(Material.DIAMOND_PICKAXE, "§aEfficiency V", 
            "Faster mining", "Cost: 500 coins"));
        enchantingMenu.setItem(14, createItem(Material.DIAMOND_BOOTS, "§aProtection IV", 
            "Better armor", "Cost: 400 coins"));
        enchantingMenu.setItem(16, createItem(Material.GOLDEN_APPLE, "§aHealing", 
            "Restore health", "Cost: 300 coins"));
        
        enchantingMenu.setItem(26, createItem(Material.ARROW, "§7Back", 
            "Return to main menu"));
        
        player.openInventory(enchantingMenu);
    }
    
    /**
     * Creates a GUI item with proper null-safety
     * @param material The material type
     * @param name The display name
     * @param loreLines The lore lines (can be null or empty)
     * @return The created ItemStack
     */
    public ItemStack createItem(Material material, String name, String... loreLines) {
        if (material == null) {
            plugin.getLogger().warning("Attempted to create item with null material!");
            material = Material.AIR;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            plugin.getLogger().warning("Could not get ItemMeta for material: " + material);
            return item;
        }
        
        // Set display name
        if (name != null && !name.isEmpty()) {
            meta.setDisplayName(name);
        }
        
        // Set lore with null-safety check
        if (loreLines != null && loreLines.length > 0) {
            List<String> lore = new ArrayList<>();
            for (String line : loreLines) {
                if (line != null && !line.isEmpty()) {
                    lore.add("§7" + line);
                }
            }
            
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Handles inventory click events - delegates to the appropriate handler
     */
    public void handleInventoryClick(Player player, ItemStack clicked, String guiType) {
        if (player == null || guiType == null) {
            return;
        }
        
        try {
            if (guiType.equals(MAIN_MENU_TYPE)) {
                handleMainMenuClick(player, clicked);
            } else if (guiType.equals(PLOT_MENU_TYPE)) {
                handlePlotMenuClick(player, clicked);
            } else if (guiType.equals(ENCHANTING_MENU_TYPE)) {
                handleEnchantingMenuClick(player, clicked);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling inventory click: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage("§cAn error occurred while processing your action.");
        }
    }
    
    private void handleMainMenuClick(Player player, ItemStack clicked) {
        if (clicked == null || clicked.getItemMeta() == null) return;
        
        String name = clicked.getItemMeta().getDisplayName();
        
        if (name.contains("Plot")) {
            openPlotMenu(player);
        } else if (name.contains("Shop")) {
            player.sendMessage("§aOpening shop...");
        } else if (name.contains("Economy")) {
            double balance = economyManager.getBalance(player);
            player.sendMessage("§6Your balance: §a" + String.format("%.2f", balance) + " §6coins");
        } else if (name.contains("Enchanting")) {
            openEnchantingMenu(player);
        }
    }
    
    private void handlePlotMenuClick(Player player, ItemStack clicked) {
        if (clicked == null || clicked.getItemMeta() == null) return;
        
        String name = clicked.getItemMeta().getDisplayName();
        
        if (name.contains("Back")) {
            openMainMenu(player);
        } else if (name.contains("Buy")) {
            player.sendMessage("§aYou bought a plot!");
        } else if (name.contains("Sell")) {
            player.sendMessage("§aPlot sold!");
        } else if (name.contains("Settings")) {
            player.sendMessage("§eOpening plot settings...");
        }
    }
    
    private void handleEnchantingMenuClick(Player player, ItemStack clicked) {
        if (clicked == null || clicked.getItemMeta() == null) return;
        
        String name = clicked.getItemMeta().getDisplayName();
        
        if (name.contains("Back")) {
            openMainMenu(player);
        } else if (name.contains("Sharpness")) {
            processEnchantmentPurchase(player, "Sharpness IV", 500);
        } else if (name.contains("Efficiency")) {
            processEnchantmentPurchase(player, "Efficiency V", 500);
        } else if (name.contains("Protection")) {
            processEnchantmentPurchase(player, "Protection IV", 400);
        } else if (name.contains("Healing")) {
            processEnchantmentPurchase(player, "Healing", 300);
        }
    }
    
    private void processEnchantmentPurchase(Player player, String enchantment, double cost) {
        double balance = economyManager.getBalance(player);
        
        if (balance >= cost) {
            economyManager.removeBalance(player, cost);
            player.sendMessage("§a✓ Purchased: §e" + enchantment);
            player.sendMessage("§6Cost: §a" + String.format("%.2f", cost) + " §6coins");
        } else {
            player.sendMessage("§c✗ Insufficient funds!");
            player.sendMessage("§eYou need §a" + String.format("%.2f", (cost - balance)) + "§e more coins");
        }
    }
}
