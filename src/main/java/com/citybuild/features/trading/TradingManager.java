package com.citybuild.features.trading;

import com.citybuild.features.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * Manages all player-to-player trades
 */
public class TradingManager {
    
    private final EconomyManager economyManager;
    private final Map<String, Trade> activeTrades = new HashMap<>();
    private final Map<UUID, String> playerActiveTrade = new HashMap<>(); // player UUID -> trade ID
    
    public TradingManager(Plugin plugin, EconomyManager economyManager) {
        this.economyManager = economyManager;
        
        // Cleanup expired trades every 30 seconds
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanupExpiredTrades, 600, 600);
    }
    
    /**
     * Create a new trade request
     */
    public Trade createTrade(UUID player1, UUID player2) {
        String tradeId = "trade_" + UUID.randomUUID().toString().substring(0, 8);
        Trade trade = new Trade(tradeId, player1, player2);
        
        activeTrades.put(tradeId, trade);
        playerActiveTrade.put(player1, tradeId);
        playerActiveTrade.put(player2, tradeId);
        
        return trade;
    }
    
    /**
     * Get active trade for player
     */
    public Trade getPlayerTrade(UUID playerUUID) {
        String tradeId = playerActiveTrade.get(playerUUID);
        if (tradeId == null) return null;
        
        Trade trade = activeTrades.get(tradeId);
        if (trade != null && trade.isExpired()) {
            cancelTrade(tradeId);
            return null;
        }
        return trade;
    }
    
    /**
     * Offer coins in a trade
     */
    public boolean offerCoins(UUID playerUUID, Trade trade, double amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return false;
        
        // Check if player has enough coins
        double balance = economyManager.getBalance(player);
        if (balance < amount) {
            player.sendMessage("§cDu hast nicht genug Münzen! (Benötigt: §e$" + (int)amount + "§c, Hast: §e$" + (int)balance + "§c)");
            return false;
        }
        
        if (trade.getPlayer1UUID().equals(playerUUID)) {
            trade.setPlayer1Coins(amount);
        } else if (trade.getPlayer2UUID().equals(playerUUID)) {
            trade.setPlayer2Coins(amount);
        } else {
            return false;
        }
        
        notifyTradePartner(playerUUID, trade, player.getName() + " hat §e$" + (int)amount + " §7angeboten!");
        return true;
    }
    
    /**
     * Accept a trade
     */
    public boolean acceptTrade(UUID playerUUID, Trade trade) {
        if (!trade.getStatus().equals(Trade.TradeStatus.PENDING)) {
            return false;
        }
        
        // Mark as accepted (both must accept, so first accept = just noted)
        if (trade.getPlayer1UUID().equals(playerUUID)) {
            notifyTradePartner(playerUUID, trade, player(playerUUID) + " hat zugestimmt!");
        } else if (trade.getPlayer2UUID().equals(playerUUID)) {
            // Both have now "accepted" - complete the trade
            trade.setStatus(Trade.TradeStatus.ACCEPTED);
            completeTrade(trade);
            return true;
        }
        return false;
    }
    
    /**
     * Complete a trade and transfer coins
     */
    private void completeTrade(Trade trade) {
        trade.setStatus(Trade.TradeStatus.COMPLETED);
        
        UUID p1 = trade.getPlayer1UUID();
        UUID p2 = trade.getPlayer2UUID();
        Player player1 = Bukkit.getPlayer(p1);
        Player player2 = Bukkit.getPlayer(p2);
        
        // Exchange coins
        if (trade.getPlayer1Coins() > 0) {
            economyManager.removeBalance(player1, trade.getPlayer1Coins());
            economyManager.addBalance(player2, trade.getPlayer1Coins());
        }
        
        if (trade.getPlayer2Coins() > 0) {
            economyManager.removeBalance(player2, trade.getPlayer2Coins());
            economyManager.addBalance(player1, trade.getPlayer2Coins());
        }
        
        // Notify both players
        if (player1 != null && player1.isOnline()) {
            player1.sendMessage("§a✓ Handel abgeschlossen!");
            player1.sendMessage(String.format("§7Du erhältst: §e$%.0f", trade.getPlayer2Coins()));
        }
        if (player2 != null && player2.isOnline()) {
            player2.sendMessage("§a✓ Handel abgeschlossen!");
            player2.sendMessage(String.format("§7Du erhältst: §e$%.0f", trade.getPlayer1Coins()));
        }
        
        removeTrade(trade.getTradeId());
    }
    
    /**
     * Cancel a trade
     */
    public void cancelTrade(String tradeId) {
        Trade trade = activeTrades.get(tradeId);
        if (trade == null) return;
        
        trade.setStatus(Trade.TradeStatus.CANCELLED);
        
        Player p1 = Bukkit.getPlayer(trade.getPlayer1UUID());
        Player p2 = Bukkit.getPlayer(trade.getPlayer2UUID());
        
        if (p1 != null && p1.isOnline()) p1.sendMessage("§cHandel wurde abgebrochen.");
        if (p2 != null && p2.isOnline()) p2.sendMessage("§cHandel wurde abgebrochen.");
        
        removeTrade(tradeId);
    }
    
    /**
     * Remove trade from active list
     */
    private void removeTrade(String tradeId) {
        Trade trade = activeTrades.remove(tradeId);
        if (trade != null) {
            playerActiveTrade.remove(trade.getPlayer1UUID());
            playerActiveTrade.remove(trade.getPlayer2UUID());
        }
    }
    
    /**
     * Clean up expired trades
     */
    private void cleanupExpiredTrades() {
        activeTrades.values().stream()
            .filter(Trade::isExpired)
            .forEach(trade -> {
                cancelTrade(trade.getTradeId());
            });
    }
    
    /**
     * Notify trade partner
     * @param initiatorUUID The initiator's UUID
     * @param trade The trade object
     * @param message The message to send
     */
    private void notifyTradePartner(UUID initiatorUUID, Trade trade, String message) {
        if (initiatorUUID == null || trade == null || message == null) return;
        
        UUID partnerUUID = trade.getPlayer1UUID().equals(initiatorUUID) 
            ? trade.getPlayer2UUID() 
            : trade.getPlayer1UUID();
        
        if (partnerUUID == null) return;
        
        Player partner = Bukkit.getPlayer(partnerUUID);
        if (partner != null && partner.isOnline()) {
            partner.sendMessage("§6[Handel] " + message);
        }
    }
    
    /**
     * Get safe player name from UUID
     * @param uuid The player UUID
     * @return Player name or "Unknown" if player not found
     */
    private String player(UUID uuid) {
        if (uuid == null) return "Unknown";
        Player p = Bukkit.getPlayer(uuid);
        return p != null ? p.getName() : "Unknown";
    }
}
