package com.citybuild.managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player auctions for items
 * Players can auction items, others can bid, highest bidder wins
 */
public class AuctionSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;

    // AuctionID -> Auction
    private final Map<Integer, Auction> auctions = new ConcurrentHashMap<>();
    // UUID -> List<AuctionIDs> created
    private final Map<String, List<Integer>> playerAuctions = new ConcurrentHashMap<>();

    private int nextAuctionId = 1;
    private long totalAuctionVolume = 0;

    public AuctionSystem(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    // ===== AUCTION CREATION & MANAGEMENT =====

    /**
     * Create a new auction
     */
    public Auction createAuction(String sellerUuid, String itemName, long startingBid, 
                                 long durationSeconds, String description) {
        if (itemName.length() < 1 || itemName.length() > 64) {
            return null;
        }

        Auction auction = new Auction(
            nextAuctionId++,
            sellerUuid,
            itemName,
            startingBid,
            System.currentTimeMillis(),
            durationSeconds * 1000,
            description
        );

        auctions.put(auction.id, auction);
        playerAuctions.computeIfAbsent(sellerUuid, k -> new ArrayList<>()).add(auction.id);

        plugin.getLogger().info("✓ Auction created: \"" + itemName + "\" (ID: " + auction.id + 
            ") starting bid: $" + startingBid);
        return auction;
    }

    /**
     * Place bid on auction
     */
    public boolean placeBid(String bidderUuid, int auctionId, long bidAmount) {
        Auction auction = auctions.get(auctionId);
        if (auction == null || auction.isExpired()) {
            return false;
        }

        if (bidAmount <= auction.currentBid) {
            return false; // Bid must be higher
        }

        if (!economyManager.hasBalance(bidderUuid, bidAmount)) {
            return false; // Insufficient funds
        }

        // Refund previous bidder if exists
        if (auction.highestBidderUuid != null) {
            economyManager.deposit(auction.highestBidderUuid, auction.currentBid);
        }

        // Hold new bid
        economyManager.withdraw(bidderUuid, bidAmount);

        auction.currentBid = bidAmount;
        auction.highestBidderUuid = bidderUuid;
        auction.lastBidTime = System.currentTimeMillis();

        plugin.getLogger().fine("✓ Bid placed: $" + bidAmount + " by " + bidderUuid.substring(0, 8) + 
            " on auction #" + auctionId);
        return true;
    }

    /**
     * End auction (award to highest bidder or return to seller)
     */
    public boolean endAuction(int auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction == null || !auction.isExpired()) {
            return false;
        }

        if (auction.highestBidderUuid != null) {
            // Award to highest bidder
            economyManager.deposit(auction.sellerUuid, auction.currentBid);
            totalAuctionVolume += auction.currentBid;

            plugin.getLogger().info("✓ Auction #" + auctionId + " (" + auction.itemName + 
                ") won by " + auction.highestBidderUuid.substring(0, 8) + 
                " for $" + auction.currentBid);
        } else {
            // No bids, return to seller
            plugin.getLogger().info("⚠ Auction #" + auctionId + " (" + auction.itemName + 
                ") ended with no bids");
        }

        auctions.remove(auctionId);
        return true;
    }

    /**
     * Cancel auction (refund bids)
     */
    public boolean cancelAuction(int auctionId, String ownerUuid) {
        Auction auction = auctions.get(auctionId);
        if (auction == null || !auction.sellerUuid.equals(ownerUuid)) {
            return false;
        }

        // Refund any existing bid
        if (auction.highestBidderUuid != null) {
            economyManager.deposit(auction.highestBidderUuid, auction.currentBid);
        }

        auctions.remove(auctionId);
        plugin.getLogger().info("✓ Auction #" + auctionId + " cancelled");
        return true;
    }

    // ===== QUERIES =====

    /**
     * Get active auctions (not expired)
     */
    public List<Auction> getActiveAuctions() {
        return auctions.values().stream()
            .filter(a -> !a.isExpired())
            .sorted((a, b) -> Long.compare(b.createdAt, a.createdAt))
            .toList();
    }

    /**
     * Get auction by ID
     */
    public Auction getAuction(int auctionId) {
        return auctions.get(auctionId);
    }

    /**
     * Get auctions created by player
     */
    public List<Auction> getAuctionsByCreator(String creatorUuid) {
        return playerAuctions.getOrDefault(creatorUuid, new ArrayList<>()).stream()
            .map(auctions::get)
            .filter(Objects::nonNull)
            .toList();
    }

    // ===== STATISTICS =====

    /**
     * Get auction statistics
     */
    public AuctionStats getStats() {
        List<Auction> active = getActiveAuctions();
        long totalBids = auctions.values().stream()
            .filter(a -> a.highestBidderUuid != null)
            .count();

        return new AuctionStats(
            active.size(),
            auctions.size(),
            totalBids,
            totalAuctionVolume
        );
    }

    /**
     * Get top auctions by price
     */
    public List<Auction> getTopAuctions(int limit) {
        return getActiveAuctions().stream()
            .sorted((a, b) -> Long.compare(b.currentBid, a.currentBid))
            .limit(limit)
            .toList();
    }

    /**
     * Clean up expired auctions
     */
    public int cleanupExpiredAuctions() {
        List<Integer> toRemove = new ArrayList<>();
        for (Auction auction : auctions.values()) {
            if (auction.isExpired()) {
                endAuction(auction.id);
                toRemove.add(auction.id);
            }
        }
        return toRemove.size();
    }

    // ===== DATA CLASSES =====

    public static class Auction {
        public final int id;
        public final String sellerUuid;
        public final String itemName;
        public final long startingBid;
        public final long createdAt;
        public final long durationMs;
        public final String description;

        public long currentBid;
        public String highestBidderUuid = null;
        public long lastBidTime;

        public Auction(int id, String sellerUuid, String itemName, long startingBid, 
                      long createdAt, long durationMs, String description) {
            this.id = id;
            this.sellerUuid = sellerUuid;
            this.itemName = itemName;
            this.startingBid = startingBid;
            this.currentBid = startingBid;
            this.createdAt = createdAt;
            this.durationMs = durationMs;
            this.description = description;
            this.lastBidTime = createdAt;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= createdAt + durationMs;
        }

        public long getTimeRemainingSeconds() {
            long remaining = (createdAt + durationMs) - System.currentTimeMillis();
            return Math.max(0, remaining / 1000);
        }

        public int getBidCount() {
            return highestBidderUuid != null ? 1 : 0; // Simplified
        }
    }

    public static class AuctionStats {
        public final int activeAuctions;
        public final int totalAuctions;
        public final long totalBids;
        public final long totalVolume;

        public AuctionStats(int active, int total, long bids, long volume) {
            this.activeAuctions = active;
            this.totalAuctions = total;
            this.totalBids = bids;
            this.totalVolume = volume;
        }
    }
}
