package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ShopManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, ShopItem> shopItems;
    
    public static class ShopItem {
        public String material;
        public int buyPrice;
        public int sellPrice;
        public String displayName;
        
        public ShopItem(String material, int buyPrice, int sellPrice, String displayName) {
            this.material = material;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.displayName = displayName;
        }
    }
    
    public ShopManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/shop.json");
        this.shopItems = new HashMap<>();
        
        dataFile.getParentFile().mkdirs();
        loadData();
        initializeDefaultShop();
    }
    
    /**
     * Initialize default shop items
     */
    private void initializeDefaultShop() {
        if (shopItems.isEmpty()) {
            // Building materials
            addShopItem("STONE", 50, 25, "Stone");
            addShopItem("OAK_WOOD", 80, 40, "Oak Wood");
            addShopItem("DARK_OAK_WOOD", 100, 50, "Dark Oak Wood");
            addShopItem("GLASS", 100, 50, "Glass");
            addShopItem("CLAY", 60, 30, "Clay");
            addShopItem("SAND", 40, 20, "Sand");
            addShopItem("GRAVEL", 30, 15, "Gravel");
            
            // Ores & Resources
            addShopItem("COAL_ORE", 200, 100, "Coal Ore");
            addShopItem("COPPER_ORE", 300, 150, "Copper Ore");
            addShopItem("IRON_ORE", 400, 200, "Iron Ore");
            addShopItem("GOLD_ORE", 600, 300, "Gold Ore");
            addShopItem("DIAMOND_ORE", 2000, 1000, "Diamond Ore");
            
            // Decorations
            addShopItem("GLOWSTONE", 150, 75, "Glowstone");
            addShopItem("OBSIDIAN", 500, 250, "Obsidian");
            addShopItem("SMOOTH_STONE", 80, 40, "Smooth Stone");
            addShopItem("GRASS_BLOCK", 100, 50, "Grass Block");
            
            // Redstone & Tech
            addShopItem("REDSTONE_BLOCK", 400, 200, "Redstone Block");
            addShopItem("REPEATER", 300, 150, "Repeater");
            addShopItem("COMPARATOR", 400, 200, "Comparator");
            
            // Tools & Items
            addShopItem("CRAFTING_TABLE", 500, 250, "Crafting Table");
            addShopItem("FURNACE", 600, 300, "Furnace");
            addShopItem("CHEST", 300, 150, "Chest");
            addShopItem("BOOKSHELF", 200, 100, "Bookshelf");
            
            plugin.getLogger().info("✓ Initialized default shop with 23 items");
            saveData();
        }
    }
    
    /**
     * Add an item to shop
     */
    public void addShopItem(String material, int buyPrice, int sellPrice, String displayName) {
        shopItems.put(material.toUpperCase(), new ShopItem(material.toUpperCase(), buyPrice, sellPrice, displayName));
    }
    
    /**
     * Get shop item by material
     */
    public ShopItem getShopItem(String material) {
        return shopItems.get(material.toUpperCase());
    }
    
    /**
     * Get all shop items
     */
    public Map<String, ShopItem> getAllShopItems() {
        return new HashMap<>(shopItems);
    }
    
    /**
     * Get material from string
     */
    public Material getMaterial(String materialStr) {
        try {
            return Material.valueOf(materialStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Check if item exists in shop
     */
    public boolean isItemInShop(String material) {
        return shopItems.containsKey(material.toUpperCase());
    }
    
    /**
     * Get shop item count
     */
    public int getShopItemCount() {
        return shopItems.size();
    }
    
    /**
     * Load shop data from JSON
     */
    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }
            
            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            
            json.entrySet().forEach(entry -> {
                JsonObject item = entry.getValue().getAsJsonObject();
                ShopItem shopItem = new ShopItem(
                    entry.getKey(),
                    item.get("buyPrice").getAsInt(),
                    item.get("sellPrice").getAsInt(),
                    item.get("displayName").getAsString()
                );
                shopItems.put(entry.getKey(), shopItem);
            });
            
            plugin.getLogger().info("✓ Loaded shop data from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load shop data: " + e.getMessage());
        }
    }
    
    /**
     * Save shop data to JSON
     */
    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            shopItems.forEach((material, item) -> {
                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("buyPrice", item.buyPrice);
                itemObj.addProperty("sellPrice", item.sellPrice);
                itemObj.addProperty("displayName", item.displayName);
                json.add(material, itemObj);
            });
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save shop data: " + e.getMessage());
        }
    }
}
