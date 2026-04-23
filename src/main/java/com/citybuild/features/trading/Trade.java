package com.citybuild.features.trading;

import java.util.UUID;

/**
 * Represents a trade request between two players
 */
public class Trade {
    
    private String tradeId;
    private UUID player1UUID;
    private UUID player2UUID;
    private double player1Coins;
    private double player2Coins;
    private String player1Items;  // Serialized item list
    private String player2Items;
    private TradeStatus status;
    private long createdAt;
    private long expiresAt;
    
    public enum TradeStatus {
        PENDING,      // Awaiting player2 response
        ACCEPTED,     // Both players agreed
        COMPLETED,    // Trade executed
        CANCELLED,    // One player cancelled
        EXPIRED       // Trade timed out
    }
    
    public Trade(String tradeId, UUID player1, UUID player2) {
        this.tradeId = tradeId;
        this.player1UUID = player1;
        this.player2UUID = player2;
        this.player1Coins = 0;
        this.player2Coins = 0;
        this.player1Items = "";
        this.player2Items = "";
        this.status = TradeStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = createdAt + (5 * 60 * 1000); // 5 minute expiry
    }
    
    // Getters & Setters
    public String getTradeId() { return tradeId; }
    public UUID getPlayer1UUID() { return player1UUID; }
    public UUID getPlayer2UUID() { return player2UUID; }
    public double getPlayer1Coins() { return player1Coins; }
    public double getPlayer2Coins() { return player2Coins; }
    public String getPlayer1Items() { return player1Items; }
    public String getPlayer2Items() { return player2Items; }
    public TradeStatus getStatus() { return status; }
    
    public void setPlayer1Coins(double coins) { this.player1Coins = coins; }
    public void setPlayer2Coins(double coins) { this.player2Coins = coins; }
    public void setPlayer1Items(String items) { this.player1Items = items; }
    public void setPlayer2Items(String items) { this.player2Items = items; }
    public void setStatus(TradeStatus status) { this.status = status; }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    public boolean isReadyToComplete() {
        return status == TradeStatus.ACCEPTED && !isExpired();
    }
    
    @Override
    public String toString() {
        return String.format("Trade{id=%s, p1=%s, p2=%s, coins=%d/%d, status=%s}",
            tradeId, player1UUID, player2UUID, (int)player1Coins, (int)player2Coins, status);
    }
}
