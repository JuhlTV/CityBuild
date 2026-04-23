package com.citybuild.managers;

import com.citybuild.model.PlotData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages plot-to-player trading and market listings
 * Players can list plots for sale, other players can buy
 */
public class PlotMarket {
    private final JavaPlugin plugin;
    private final PlotManager plotManager;
    private final EconomyManager economyManager;
    
    // PlotID -> MarketListing
    private final Map<Integer, MarketListing> listings = new ConcurrentHashMap<>();
    private int nextListingId = 1;

    public PlotMarket(JavaPlugin plugin, PlotManager plotManager, EconomyManager economyManager) {
        this.plugin = plugin;
        this.plotManager = plotManager;
        this.economyManager = economyManager;
    }

    // ===== LISTING MANAGEMENT =====

    /**
     * Create a market listing for a plot
     */
    public boolean listPlot(String ownerUuid, int plotId, long price, String description) {
        PlotData plot = plotManager.getPlot(plotId);
        if (plot == null || !plot.isOwner(ownerUuid)) {
            return false;
        }

        if (listings.containsKey(plotId)) {
            return false; // Already listed
        }

        MarketListing listing = new MarketListing(
            nextListingId++,
            plotId,
            ownerUuid,
            price,
            description,
            System.currentTimeMillis()
        );

        listings.put(plotId, listing);
        plugin.getLogger().info("✓ Plot #" + plotId + " listed for $" + price + " by " + ownerUuid.substring(0, 8));
        return true;
    }

    /**
     * Remove a listing from market
     */
    public boolean delistPlot(int plotId, String ownerUuid) {
        MarketListing listing = listings.get(plotId);
        if (listing == null || !listing.ownerUuid.equals(ownerUuid)) {
            return false;
        }

        listings.remove(plotId);
        plugin.getLogger().info("✓ Plot #" + plotId + " delisted from market");
        return true;
    }

    /**
     * Purchase a listed plot
     */
    public boolean buyPlot(String buyerUuid, int plotId) {
        MarketListing listing = listings.get(plotId);
        if (listing == null) {
            return false; // Not listed
        }

        PlotData plot = plotManager.getPlot(plotId);
        if (plot == null) {
            return false;
        }

        // Check buyer has funds
        if (!economyManager.hasBalance(buyerUuid, listing.price)) {
            return false; // Insufficient funds
        }

        // Transfer money: buyer -> seller
        economyManager.withdraw(buyerUuid, listing.price);
        economyManager.deposit(listing.ownerUuid, listing.price);

        // Transfer plot ownership
        plot.setOwner(buyerUuid);
        plotManager.savePlot(plot);

        // Remove listing
        listings.remove(plotId);

        plugin.getLogger().info("✓ Plot #" + plotId + " sold from " + 
            listing.ownerUuid.substring(0, 8) + " to " + buyerUuid.substring(0, 8) + 
            " for $" + listing.price);

        return true;
    }

    /**
     * Get all active listings
     */
    public Collection<MarketListing> getAllListings() {
        return new ArrayList<>(listings.values());
    }

    /**
     * Get listings sorted by price (low to high)
     */
    public List<MarketListing> getListingsByPrice() {
        return listings.values().stream()
            .sorted(Comparator.comparingLong(l -> l.price))
            .toList();
    }

    /**
     * Get listings by owner
     */
    public List<MarketListing> getListingsByOwner(String ownerUuid) {
        return listings.values().stream()
            .filter(l -> l.ownerUuid.equals(ownerUuid))
            .toList();
    }

    /**
     * Get listing for specific plot
     */
    public MarketListing getListingForPlot(int plotId) {
        return listings.get(plotId);
    }

    /**
     * Get market statistics
     */
    public MarketStats getMarketStats() {
        if (listings.isEmpty()) {
            return new MarketStats(0, 0, 0, 0);
        }

        long totalValue = listings.values().stream()
            .mapToLong(l -> l.price)
            .sum();

        long avgPrice = totalValue / listings.size();

        long minPrice = listings.values().stream()
            .mapToLong(l -> l.price)
            .min()
            .orElse(0);

        long maxPrice = listings.values().stream()
            .mapToLong(l -> l.price)
            .max()
            .orElse(0);

        return new MarketStats(listings.size(), avgPrice, minPrice, maxPrice);
    }

    // ===== DATA CLASSES =====

    public static class MarketListing {
        public final int listingId;
        public final int plotId;
        public final String ownerUuid;
        public final long price;
        public final String description;
        public final long listedAt;

        public MarketListing(int listingId, int plotId, String ownerUuid, long price, String description, long listedAt) {
            this.listingId = listingId;
            this.plotId = plotId;
            this.ownerUuid = ownerUuid;
            this.price = price;
            this.description = description;
            this.listedAt = listedAt;
        }

        public int getDaysListed() {
            return (int) ((System.currentTimeMillis() - listedAt) / (1000 * 60 * 60 * 24));
        }
    }

    public static class MarketStats {
        public final int activeListings;
        public final long averagePrice;
        public final long minPrice;
        public final long maxPrice;

        public MarketStats(int activeListings, long averagePrice, long minPrice, long maxPrice) {
            this.activeListings = activeListings;
            this.averagePrice = averagePrice;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }
    }
}
