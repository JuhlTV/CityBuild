package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.*;

public class AuctionHouseManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final List<AuctionItem> auctions;
    private int nextAuctionId = 1;

    public static class AuctionItem {
        public int auctionId;
        public String seller; // UUID
        public String itemName;
        public Material material;
        public int amount;
        public long price;
        public LocalDateTime createdAt;
        public boolean sold;

        public AuctionItem(int auctionId, String seller, String itemName, Material material, int amount, long price) {
            this.auctionId = auctionId;
            this.seller = seller;
            this.itemName = itemName;
            this.material = material;
            this.amount = amount;
            this.price = price;
            this.createdAt = LocalDateTime.now();
            this.sold = false;
        }
    }

    public AuctionHouseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/auctions.json");
        this.auctions = new ArrayList<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    /**
     * Create a new auction listing
     */
    public boolean createAuction(String sellerUuid, String itemName, Material material, int amount, long pricePerItem) {
        if (amount <= 0 || pricePerItem <= 0) {
            return false;
        }

        AuctionItem auction = new AuctionItem(nextAuctionId++, sellerUuid, itemName, material, amount, pricePerItem);
        auctions.add(auction);
        saveData();
        return true;
    }

    /**
     * Purchase an auction
     */
    public boolean purchaseAuction(int auctionId, String buyerUuid, EconomyManager economy) {
        for (AuctionItem auction : auctions) {
            if (auction.auctionId == auctionId && !auction.sold) {
                // Check if buyer has enough money
                long totalPrice = auction.price * auction.amount;
                if (economy.getBalance(buyerUuid) < totalPrice) {
                    return false; // Not enough money
                }

                // Transfer money: buyer loses money, seller gains money
                economy.removeBalance(buyerUuid, totalPrice);
                // Get seller player offline
                org.bukkit.OfflinePlayer seller = org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.fromString(auction.seller));
                if (seller != null && seller.getPlayer() != null) {
                    economy.addBalance(seller.getPlayer(), totalPrice);
                } else {
                    // Add to balance file for offline player
                    economy.addBalance(auction.seller, totalPrice);
                }

                // Mark auction as sold
                auction.sold = true;
                saveData();
                return true;
            }
        }
        return false;
    }

    /**
     * Cancel an auction (seller only)
     */
    public boolean cancelAuction(int auctionId, String sellerUuid) {
        for (AuctionItem auction : auctions) {
            if (auction.auctionId == auctionId && auction.seller.equals(sellerUuid) && !auction.sold) {
                auctions.remove(auction);
                saveData();
                return true;
            }
        }
        return false;
    }

    /**
     * Get all active (unsold) auctions
     */
    public List<AuctionItem> getActiveAuctions() {
        List<AuctionItem> active = new ArrayList<>();
        for (AuctionItem auction : auctions) {
            if (!auction.sold) {
                active.add(auction);
            }
        }
        return active;
    }

    /**
     * Get auctions by specific seller
     */
    public List<AuctionItem> getSellerAuctions(String sellerUuid) {
        List<AuctionItem> sellerAuctions = new ArrayList<>();
        for (AuctionItem auction : auctions) {
            if (auction.seller.equals(sellerUuid) && !auction.sold) {
                sellerAuctions.add(auction);
            }
        }
        return sellerAuctions;
    }

    /**
     * Get single auction by ID
     */
    public AuctionItem getAuction(int auctionId) {
        for (AuctionItem auction : auctions) {
            if (auction.auctionId == auctionId) {
                return auction;
            }
        }
        return null;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }

            JsonArray jsonArray = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonArray();

            for (var element : jsonArray) {
                JsonObject json = element.getAsJsonObject();
                AuctionItem auction = new AuctionItem(
                    json.get("auctionId").getAsInt(),
                    json.get("seller").getAsString(),
                    json.get("itemName").getAsString(),
                    Material.valueOf(json.get("material").getAsString()),
                    json.get("amount").getAsInt(),
                    json.get("price").getAsLong()
                );
                auction.sold = json.get("sold").getAsBoolean();
                auctions.add(auction);
                nextAuctionId = Math.max(nextAuctionId, auction.auctionId + 1);
            }

            plugin.getLogger().info("✓ Loaded auctions from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load auction data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();

            JsonArray jsonArray = new JsonArray();

            for (AuctionItem auction : auctions) {
                JsonObject json = new JsonObject();
                json.addProperty("auctionId", auction.auctionId);
                json.addProperty("seller", auction.seller);
                json.addProperty("itemName", auction.itemName);
                json.addProperty("material", auction.material.toString());
                json.addProperty("amount", auction.amount);
                json.addProperty("price", auction.price);
                json.addProperty("sold", auction.sold);
                jsonArray.add(json);
            }

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(jsonArray, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save auction data: " + e.getMessage());
        }
    }
}
