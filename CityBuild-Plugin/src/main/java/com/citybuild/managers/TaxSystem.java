package com.citybuild.managers;

import com.citybuild.model.PlotData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages plot taxes, daily maintenance fees, and government income
 * Players must pay taxes to keep their plots active
 */
public class TaxSystem {
    private final JavaPlugin plugin;
    private final PlotManager plotManager;
    private final EconomyManager economyManager;
    private final int taxPerPlot;
    private final int taxInterval; // Days between tax collection

    // UUID -> LastTaxPaid (timestamp)
    private final Map<String, Long> lastTaxPaid = new ConcurrentHashMap<>();
    
    // UUID -> TaxDebtAmount
    private final Map<String, Long> taxDebt = new ConcurrentHashMap<>();

    private long totalTaxCollected = 0;

    public TaxSystem(JavaPlugin plugin, PlotManager plotManager, EconomyManager economyManager) {
        this.plugin = plugin;
        this.plotManager = plotManager;
        this.economyManager = economyManager;
        this.taxPerPlot = plugin.getConfig().getInt("taxes.plot_tax_per_plot", 500);
        this.taxInterval = plugin.getConfig().getInt("taxes.tax_interval_days", 1);
    }

    // ===== TAX COLLECTION =====

    /**
     * Attempt to collect taxes from a player
     * Returns true if successful, false if insufficient funds
     */
    public boolean collectTax(String playerUuid) {
        List<PlotData> plots = plotManager.getPlayerPlotData(playerUuid);
        if (plots.isEmpty()) {
            return true; // No plots = no tax
        }

        long totalTax = (long) plots.size() * taxPerPlot;
        
        // Check if already paid recently
        if (!isTaxDue(playerUuid)) {
            return true;
        }

        // Attempt payment
        if (economyManager.hasBalance(playerUuid, totalTax)) {
            economyManager.withdraw(playerUuid, totalTax);
            lastTaxPaid.put(playerUuid, System.currentTimeMillis());
            taxDebt.remove(playerUuid);
            totalTaxCollected += totalTax;
            plugin.getLogger().fine("✓ Collected tax from " + playerUuid.substring(0, 8) + ": $" + totalTax);
            return true;
        } else {
            // Record debt
            taxDebt.put(playerUuid, totalTax);
            plugin.getLogger().warning("⚠ Tax debt for " + playerUuid.substring(0, 8) + ": $" + totalTax);
            return false;
        }
    }

    /**
     * Check if tax is due for player
     */
    public boolean isTaxDue(String playerUuid) {
        Long lastPaid = lastTaxPaid.get(playerUuid);
        if (lastPaid == null) {
            return true; // Never paid = tax due
        }

        long daysSincePaid = (System.currentTimeMillis() - lastPaid) / (1000 * 60 * 60 * 24);
        return daysSincePaid >= taxInterval;
    }

    /**
     * Get days until next tax due
     */
    public int getDaysUntilTaxDue(String playerUuid) {
        Long lastPaid = lastTaxPaid.get(playerUuid);
        if (lastPaid == null) {
            return 0; // Tax due now
        }

        long daysSincePaid = (System.currentTimeMillis() - lastPaid) / (1000 * 60 * 60 * 24);
        int daysUntilDue = (int) (taxInterval - daysSincePaid);
        return Math.max(0, daysUntilDue);
    }

    /**
     * Calculate tax owed by player
     */
    public long calculateTax(String playerUuid) {
        List<PlotData> plots = plotManager.getPlayerPlotData(playerUuid);
        return (long) plots.size() * taxPerPlot;
    }

    // ===== DEBT MANAGEMENT =====

    /**
     * Get current tax debt
     */
    public long getTaxDebt(String playerUuid) {
        return taxDebt.getOrDefault(playerUuid, 0L);
    }

    /**
     * Pay off tax debt
     */
    public boolean payTaxDebt(String playerUuid, long amount) {
        long debt = getTaxDebt(playerUuid);
        if (debt == 0) {
            return false; // No debt
        }

        if (!economyManager.hasBalance(playerUuid, amount)) {
            return false; // Insufficient funds
        }

        economyManager.withdraw(playerUuid, amount);
        long remainingDebt = debt - amount;

        if (remainingDebt <= 0) {
            taxDebt.remove(playerUuid);
        } else {
            taxDebt.put(playerUuid, remainingDebt);
        }

        plugin.getLogger().fine("✓ " + playerUuid.substring(0, 8) + " paid $" + amount + " towards tax debt");
        return true;
    }

    /**
     * Seize plots due to unpaid taxes (extreme measure)
     */
    public int seizePlotsForUnpaidTaxes(String playerUuid) {
        long debt = getTaxDebt(playerUuid);
        if (debt <= 0) {
            return 0;
        }

        List<PlotData> plots = plotManager.getPlayerPlotData(playerUuid);
        int seized = 0;

        for (PlotData plot : plots) {
            if (debt <= 0) break;

            // Seize plot and add to government treasury
            economyManager.deposit("government-treasury", taxPerPlot);
            debt -= taxPerPlot;
            seized++;

            plugin.getLogger().warning("⚠ Seized Plot #" + plot.getPlotId() + " from " + playerUuid.substring(0, 8));
        }

        if (seized > 0) {
            taxDebt.remove(playerUuid);
        }

        return seized;
    }

    // ===== STATISTICS =====

    /**
     * Get tax statistics
     */
    public TaxStats getStats() {
        List<PlotData> allPlots = getAllPlots();
        long estimatedTaxFromAllPlots = (long) allPlots.size() * taxPerPlot;

        return new TaxStats(
            totalTaxCollected,
            estimatedTaxFromAllPlots,
            (int) taxDebt.values().stream().filter(d -> d > 0).count(),
            taxDebt.values().stream().mapToLong(Long::longValue).sum()
        );
    }

    private List<PlotData> getAllPlots() {
        List<PlotData> allPlots = new ArrayList<>();
        // Would need to get all plots from PlotManager
        return allPlots;
    }

    /**
     * Get all players with tax debt
     */
    public Map<String, Long> getPlayersWithDebt() {
        return new HashMap<>(taxDebt);
    }

    // ===== DATA CLASSES =====

    public static class TaxStats {
        public final long totalCollected;
        public final long estimatedTotal;
        public final int playersInDebt;
        public final long totalDebt;

        public TaxStats(long totalCollected, long estimatedTotal, int playersInDebt, long totalDebt) {
            this.totalCollected = totalCollected;
            this.estimatedTotal = estimatedTotal;
            this.playersInDebt = playersInDebt;
            this.totalDebt = totalDebt;
        }
    }
}
