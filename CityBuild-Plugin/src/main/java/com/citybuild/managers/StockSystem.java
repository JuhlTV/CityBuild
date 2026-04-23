package com.citybuild.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages stock shares and player investments
 * Players can buy/sell shares of projects, receive dividends
 */
public class StockSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;

    // StockID -> Stock
    private final Map<Integer, Stock> stocks = new ConcurrentHashMap<>();
    // UUID -> Map<StockID, Shares>
    private final Map<String, Map<Integer, Long>> playerShares = new ConcurrentHashMap<>();
    
    private int nextStockId = 1;

    public StockSystem(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    // ===== STOCK CREATION =====

    /**
     * Create a new stock offering
     */
    public Stock createStock(String issuerUuid, String projectName, long totalShares, long pricePerShare) {
        Stock stock = new Stock(
            nextStockId++,
            projectName,
            issuerUuid,
            totalShares,
            pricePerShare,
            System.currentTimeMillis()
        );

        stocks.put(stock.id, stock);
        
        // Issuer owns all shares initially
        playerShares.computeIfAbsent(issuerUuid, k -> new ConcurrentHashMap<>())
            .put(stock.id, totalShares);

        plugin.getLogger().info("✓ Stock IPO: \"" + projectName + "\" (" + totalShares + " shares @ $" + pricePerShare + "/share)");
        return stock;
    }

    /**
     * Buy shares of a stock
     */
    public boolean buyShares(String buyerUuid, int stockId, long sharesToBuy) {
        Stock stock = stocks.get(stockId);
        if (stock == null) {
            return false;
        }

        long totalCost = sharesToBuy * stock.pricePerShare;

        // Check buyer has funds
        if (!economyManager.hasBalance(buyerUuid, totalCost)) {
            return false;
        }

        // Check seller has shares
        Map<Integer, Long> sellerShares = playerShares.computeIfAbsent(stock.issuerUuid, k -> new ConcurrentHashMap<>());
        Long availableShares = sellerShares.getOrDefault(stockId, 0L);

        if (availableShares < sharesToBuy) {
            return false; // Not enough shares available
        }

        // Transfer money: buyer -> seller
        economyManager.withdraw(buyerUuid, totalCost);
        economyManager.deposit(stock.issuerUuid, totalCost);

        // Transfer shares
        sellerShares.put(stockId, availableShares - sharesToBuy);
        Map<Integer, Long> buyerShares = playerShares.computeIfAbsent(buyerUuid, k -> new ConcurrentHashMap<>());
        buyerShares.put(stockId, buyerShares.getOrDefault(stockId, 0L) + sharesToBuy);

        stock.totalTraded += sharesToBuy;
        plugin.getLogger().fine("✓ " + buyerUuid.substring(0, 8) + " bought " + sharesToBuy + " shares of " + stock.projectName);
        
        return true;
    }

    /**
     * Sell shares of a stock
     */
    public boolean sellShares(String sellerUuid, int stockId, long sharesToSell) {
        Stock stock = stocks.get(stockId);
        if (stock == null) {
            return false;
        }

        Map<Integer, Long> sellerShares = playerShares.get(sellerUuid);
        if (sellerShares == null) {
            return false; // Seller has no shares
        }

        Long ownedShares = sellerShares.getOrDefault(stockId, 0L);
        if (ownedShares < sharesToSell) {
            return false; // Not enough shares
        }

        long totalRevenue = sharesToSell * stock.pricePerShare;

        // Transfer shares to issuer (back to pool)
        sellerShares.put(stockId, ownedShares - sharesToSell);
        Map<Integer, Long> issuerShares = playerShares.computeIfAbsent(stock.issuerUuid, k -> new ConcurrentHashMap<>());
        issuerShares.put(stockId, issuerShares.getOrDefault(stockId, 0L) + sharesToSell);

        // Transfer money
        economyManager.withdraw(stock.issuerUuid, totalRevenue);
        economyManager.deposit(sellerUuid, totalRevenue);

        stock.totalTraded += sharesToSell;
        plugin.getLogger().fine("✓ " + sellerUuid.substring(0, 8) + " sold " + sharesToSell + " shares of " + stock.projectName);
        
        return true;
    }

    /**
     * Pay dividend to all shareholders
     */
    public void payDividend(int stockId, long totalDividend) {
        Stock stock = stocks.get(stockId);
        if (stock == null || totalDividend <= 0) {
            return;
        }

        long dividendPerShare = totalDividend / stock.totalShares;
        int recipientCount = 0;

        // Pay each shareholder
        for (Map.Entry<String, Map<Integer, Long>> player : playerShares.entrySet()) {
            Long shares = player.getValue().getOrDefault(stockId, 0L);
            if (shares > 0) {
                long dividend = shares * dividendPerShare;
                economyManager.deposit(player.getKey(), dividend);
                recipientCount++;
            }
        }

        stock.totalDividendsPaid += totalDividend;
        plugin.getLogger().info("✓ Paid dividend on " + stock.projectName + ": $" + totalDividend + 
            " to " + recipientCount + " shareholders");
    }

    // ===== PORTFOLIO QUERIES =====

    /**
     * Get player's shares in a stock
     */
    public long getShares(String playerUuid, int stockId) {
        return playerShares.getOrDefault(playerUuid, new ConcurrentHashMap<>())
            .getOrDefault(stockId, 0L);
    }

    /**
     * Get player's portfolio value
     */
    public long getPortfolioValue(String playerUuid) {
        return playerShares.getOrDefault(playerUuid, new ConcurrentHashMap<>()).entrySet().stream()
            .mapToLong(e -> {
                Stock stock = stocks.get(e.getKey());
                return stock != null ? e.getValue() * stock.pricePerShare : 0;
            })
            .sum();
    }

    /**
     * Get stocks ordered by value
     */
    public List<Stock> getStocksByValue() {
        return stocks.values().stream()
            .sorted((a, b) -> Long.compare(b.totalValue(), a.totalValue()))
            .toList();
    }

    // ===== STATISTICS =====

    /**
     * Get stock market statistics
     */
    public StockMarketStats getMarketStats() {
        long totalMarketCap = stocks.values().stream()
            .mapToLong(Stock::totalValue)
            .sum();

        long totalTraded = stocks.values().stream()
            .mapToLong(s -> s.totalTraded)
            .sum();

        long totalDividends = stocks.values().stream()
            .mapToLong(s -> s.totalDividendsPaid)
            .sum();

        return new StockMarketStats(
            stocks.size(),
            totalMarketCap,
            totalTraded,
            totalDividends
        );
    }

    // ===== DATA CLASSES =====

    public static class Stock {
        public final int id;
        public final String projectName;
        public final String issuerUuid;
        public final long totalShares;
        public long pricePerShare;
        public final long createdAt;

        public long totalTraded = 0;
        public long totalDividendsPaid = 0;

        public Stock(int id, String projectName, String issuerUuid, long totalShares, 
                    long pricePerShare, long createdAt) {
            this.id = id;
            this.projectName = projectName;
            this.issuerUuid = issuerUuid;
            this.totalShares = totalShares;
            this.pricePerShare = pricePerShare;
            this.createdAt = createdAt;
        }

        public long totalValue() {
            return totalShares * pricePerShare;
        }

        public void setPrice(long newPrice) {
            this.pricePerShare = newPrice;
        }
    }

    public static class StockMarketStats {
        public final int totalStocks;
        public final long totalMarketCap;
        public final long totalSharesTraded;
        public final long totalDividendsPaid;

        public StockMarketStats(int totalStocks, long totalMarketCap, long totalTraded, long totalDividends) {
            this.totalStocks = totalStocks;
            this.totalMarketCap = totalMarketCap;
            this.totalSharesTraded = totalTraded;
            this.totalDividendsPaid = totalDividends;
        }
    }
}
