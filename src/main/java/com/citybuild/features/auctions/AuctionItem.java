package com.citybuild.features.auctions;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

/**
 * Represents an item listed for auction in the auction house
 */
public class AuctionItem {
    private final String auctionId;
    private final UUID sellerUUID;
    private final ItemStack itemStack;
    private final double startingPrice;
    private double currentBid;
    private UUID currentBidderUUID;
    private final long createdAt;
    private long endTime;
    private AuctionStatus status;

    public enum AuctionStatus {
        ACTIVE("🟢 ACTIVE"),
        SOLD("💰 SOLD"),
        EXPIRED("⏰ EXPIRED"),
        CANCELLED("❌ CANCELLED");

        private final String display;

        AuctionStatus(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    /**
     * Create a new auction item (duration: 24 hours)
     */
    public AuctionItem(String auctionId, UUID sellerUUID, ItemStack itemStack, double startingPrice) {
        this.auctionId = auctionId;
        this.sellerUUID = sellerUUID;
        this.itemStack = itemStack;
        this.startingPrice = startingPrice;
        this.currentBid = startingPrice;
        this.currentBidderUUID = null;
        this.createdAt = System.currentTimeMillis();
        this.endTime = createdAt + (24 * 60 * 60 * 1000); // 24 hours
        this.status = AuctionStatus.ACTIVE;
    }

    /**
     * Place a bid on this auction
     */
    public boolean placeBid(UUID bidderUUID, double bidAmount) {
        if (status != AuctionStatus.ACTIVE) {
            return false;
        }
        
        if (bidAmount <= currentBid) {
            return false;
        }
        
        if (bidderUUID.equals(sellerUUID)) {
            return false;
        }

        currentBid = bidAmount;
        currentBidderUUID = bidderUUID;
        return true;
    }

    /**
     * Check if auction has expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }

    /**
     * Get time remaining in minutes
     */
    public long getTimeRemainingMinutes() {
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining / (60 * 1000));
    }

    /**
     * Finish the auction (called by manager when time expires)
     */
    public void finishAuction() {
        if (isExpired()) {
            if (currentBidderUUID != null) {
                status = AuctionStatus.SOLD;
            } else {
                status = AuctionStatus.EXPIRED;
            }
        }
    }

    /**
     * Cancel the auction
     */
    public void cancel() {
        status = AuctionStatus.CANCELLED;
    }

    /**
     * Get formatted info for display
     */
    public String getFormattedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6ID: §e").append(auctionId).append("\n");
        sb.append("§6Item: §e").append(itemStack.getType().name()).append(" x").append(itemStack.getAmount()).append("\n");
        sb.append("§6Status: §e").append(status.getDisplay()).append("\n");
        sb.append("§6Starting Price: §6$").append(String.format("%.2f", startingPrice)).append("\n");
        sb.append("§6Current Bid: §6$").append(String.format("%.2f", currentBid)).append("\n");
        
        if (currentBidderUUID != null) {
            sb.append("§6Leading Bidder: §e[").append(currentBidderUUID.toString().substring(0, 8)).append("]\n");
        }
        
        sb.append("§6Time Remaining: §e").append(getTimeRemainingMinutes()).append(" minutes");
        return sb.toString();
    }

    // Getters
    public String getAuctionId() { return auctionId; }
    public UUID getSellerUUID() { return sellerUUID; }
    public ItemStack getItemStack() { return itemStack; }
    public double getStartingPrice() { return startingPrice; }
    public double getCurrentBid() { return currentBid; }
    public UUID getCurrentBidderUUID() { return currentBidderUUID; }
    public long getCreatedAt() { return createdAt; }
    public long getEndTime() { return endTime; }
    public AuctionStatus getStatus() { return status; }

    // Setters
    public void setStatus(AuctionStatus status) { this.status = status; }
}
