package com.citybuild.features.auctions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import com.citybuild.features.economy.EconomyManager;

import java.util.*;

/**
 * Manages the auction house system for buying/selling items
 */
public class AuctionHouseManager {

    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    private final Map<String, AuctionItem> activeAuctions = new HashMap<>();
    private final Map<UUID, Queue<String>> playerAuctions = new HashMap<>();
    private BukkitTask cleanupTask;
    private int auctionCounter = 0;

    public AuctionHouseManager(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        startCleanupTask();
    }

    /**
     * Create and list a new auction
     */
    public String createAuction(UUID sellerUUID, ItemStack itemStack, double startingPrice) {
        if (itemStack == null || itemStack.getAmount() == 0) {
            return null;
        }

        String auctionId = "AH_" + (auctionCounter++);
        AuctionItem auction = new AuctionItem(auctionId, sellerUUID, itemStack, startingPrice);
        activeAuctions.put(auctionId, auction);

        playerAuctions.computeIfAbsent(sellerUUID, k -> new LinkedList<>()).add(auctionId);

        return auctionId;
    }

    /**
     * Place a bid on an auction
     */
    public boolean placeBid(UUID bidderUUID, String auctionId, double bidAmount) {
        AuctionItem auction = activeAuctions.get(auctionId);
        if (auction == null || auction.getStatus() != AuctionItem.AuctionStatus.ACTIVE) {
            return false;
        }

        // Check if player has enough coins
        double playerBalance = economyManager.getPlayerBalance(bidderUUID);
        if (playerBalance < bidAmount) {
            return false;
        }

        if (!auction.placeBid(bidderUUID, bidAmount)) {
            return false;
        }

        // Notify seller and bidders
        Player seller = Bukkit.getPlayer(auction.getSellerUUID());
        if (seller != null) {
            seller.sendMessage("§e📢 Someone placed a bid on your auction: §6$" + 
                String.format("%.2f", bidAmount));
        }

        Player bidder = Bukkit.getPlayer(bidderUUID);
        if (bidder != null) {
            bidder.sendMessage("§e✓ Bid placed: §6$" + String.format("%.2f", bidAmount));
        }

        return true;
    }

    /**
     * Get all active auctions
     */
    public List<AuctionItem> getActiveAuctions() {
        List<AuctionItem> active = new ArrayList<>();
        for (AuctionItem item : activeAuctions.values()) {
            if (item.getStatus() == AuctionItem.AuctionStatus.ACTIVE) {
                active.add(item);
            }
        }
        return active;
    }

    /**
     * Get auction by ID
     */
    public AuctionItem getAuction(String auctionId) {
        return activeAuctions.get(auctionId);
    }

    /**
     * Get player's auctions
     */
    public List<AuctionItem> getPlayerAuctions(UUID playerUUID) {
        List<AuctionItem> playerAucs = new ArrayList<>();
        Queue<String> auctionIds = playerAuctions.get(playerUUID);
        if (auctionIds != null) {
            for (String id : auctionIds) {
                AuctionItem item = activeAuctions.get(id);
                if (item != null) {
                    playerAucs.add(item);
                }
            }
        }
        return playerAucs;
    }

    /**
     * Cancel an auction (only seller can cancel)
     */
    public boolean cancelAuction(String auctionId, UUID playerUUID) {
        AuctionItem auction = activeAuctions.get(auctionId);
        if (auction == null) return false;
        if (!auction.getSellerUUID().equals(playerUUID)) return false;
        if (auction.getStatus() != AuctionItem.AuctionStatus.ACTIVE) return false;

        auction.cancel();
        return true;
    }

    /**
     * Finish auction (called by cleanup task)
     */
    private void finishAuction(String auctionId) {
        AuctionItem auction = activeAuctions.get(auctionId);
        if (auction == null) return;

        auction.finishAuction();

        if (auction.getStatus() == AuctionItem.AuctionStatus.SOLD) {
            // Transfer coins from bidder to seller
            UUID bidder = auction.getCurrentBidderUUID();
            UUID seller = auction.getSellerUUID();
            double amount = auction.getCurrentBid();

            economyManager.removeBalance(bidder, amount);
            economyManager.addBalance(seller, amount);

            // Notify both parties
            Player sellerPlayer = Bukkit.getPlayer(seller);
            if (sellerPlayer != null) {
                sellerPlayer.sendMessage("§a✓ Your auction sold! §6+" + 
                    String.format("%.2f", amount) + " coins");
            }

            Player bidderPlayer = Bukkit.getPlayer(bidder);
            if (bidderPlayer != null) {
                bidderPlayer.sendMessage("§a✓ Auction won! Item acquired.");
            }
        }
    }

    /**
     * Start cleanup task to handle auction expiry
     */
    private void startCleanupTask() {
        cleanupTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<String> expiredAuctions = new ArrayList<>();
            
            for (String auctionId : activeAuctions.keySet()) {
                AuctionItem auction = activeAuctions.get(auctionId);
                if (auction.isExpired() && auction.getStatus() == AuctionItem.AuctionStatus.ACTIVE) {
                    finishAuction(auctionId);
                    expiredAuctions.add(auctionId);
                }
            }

            // Clean up finished auctions after 1 hour
            for (AuctionItem auction : activeAuctions.values()) {
                if (auction.getStatus() != AuctionItem.AuctionStatus.ACTIVE && 
                    System.currentTimeMillis() - auction.getEndTime() > 3600000) {
                    expiredAuctions.add(auction.getAuctionId());
                }
            }

            expiredAuctions.forEach(activeAuctions::remove);
        }, 1200, 1200); // Every 60 seconds
    }

    /**
     * Get auction house statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_auctions", activeAuctions.size());
        stats.put("active_auctions", getActiveAuctions().size());
        stats.put("sellers", playerAuctions.size());
        
        double totalValue = 0;
        for (AuctionItem item : getActiveAuctions()) {
            totalValue += item.getCurrentBid();
        }
        stats.put("total_value", totalValue);
        
        return stats;
    }

    /**
     * Stop cleanup task
     */
    public void stop() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
        }
    }
}
