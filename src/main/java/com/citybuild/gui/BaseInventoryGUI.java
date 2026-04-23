package com.citybuild.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Base class for all inventory-based GUIs
 */
public abstract class BaseInventoryGUI implements InventoryHolder {

    protected Inventory inventory;
    protected Player player;
    protected String title;
    protected int size;

    public BaseInventoryGUI(Player player, String title, int size) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    /**
     * Build the GUI layout
     */
    public abstract void buildGUI();

    /**
     * Handle item clicks in the GUI
     */
    public abstract void handleClick(int slot, ItemStack item);

    /**
     * Open the GUI for the player
     */
    public void open() {
        buildGUI();
        player.openInventory(inventory);
    }

    /**
     * Set item at slot with custom lore
     */
    protected void setItem(int slot, ItemStack item, String... lore) {
        if (item == null || item.getAmount() == 0) return;

        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            java.util.List<String> loreList = new java.util.ArrayList<>();
            for (String line : lore) {
                loreList.add(line);
            }
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }

        inventory.setItem(slot, item);
    }

    /**
     * Create a filler item (no-op button)
     */
    protected ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§8");
            filler.setItemMeta(meta);
        }
        return filler;
    }

    /**
     * Create a custom display item
     */
    protected ItemStack createCustomItem(org.bukkit.Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            java.util.List<String> loreList = new java.util.ArrayList<>();
            for (String line : lore) {
                loreList.add(line);
            }
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Fill borders with filler items
     */
    protected void fillBorders() {
        ItemStack filler = createFillerItem();
        // Top row
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler.clone());
        }
        // Bottom row
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, filler.clone());
        }
        // Left and right columns
        for (int i = 9; i < size - 9; i += 9) {
            inventory.setItem(i, filler.clone());
            if (i + 8 < size) {
                inventory.setItem(i + 8, filler.clone());
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
