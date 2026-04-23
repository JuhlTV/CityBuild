package com.citybuild.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;

import com.citybuild.CityBuildPlugin;
import com.citybuild.gui.GUIManager;
import com.citybuild.gui.CityBuildInventoryHolder;

/**
 * InventoryClickListener - Handles all GUI menu clicks
 * Provides robust error handling for inventory interactions
 */
public class InventoryClickListener implements Listener {
    private final CityBuildPlugin plugin;
    private final GUIManager guiManager;
    
    public InventoryClickListener(CityBuildPlugin plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            // Validate event and inventory
            if (event.getInventory() == null) {
                return;
            }
            
            // Check if this is a CityBuild GUI
            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof CityBuildInventoryHolder)) {
                return;
            }
            
            // Prevent item movement in GUI
            event.setCancelled(true);
            
            // Validate player
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            
            // Validate clicked item
            if (clicked == null || !clicked.hasItemMeta()) {
                return;
            }
            
            // Get GUI type from holder
            CityBuildInventoryHolder cbHolder = (CityBuildInventoryHolder) holder;
            String guiType = cbHolder.getGuiType();
            
            // Delegate to GUIManager
            guiManager.handleInventoryClick(player, clicked, guiType);
            
        } catch (NullPointerException e) {
            plugin.getLogger().severe("NullPointerException in InventoryClickListener: " + e.getMessage());
            e.printStackTrace();
            
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("§cAn error occurred while handling your click.");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling inventory click: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

