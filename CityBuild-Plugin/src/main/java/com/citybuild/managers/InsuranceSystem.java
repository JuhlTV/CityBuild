package com.citybuild.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages insurance policies for plots, businesses, and stocks
 * Players can purchase insurance and file claims for losses
 */
public class InsuranceSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;

    // UUID -> List<InsurancePolicy>
    private final Map<String, List<InsurancePolicy>> playerPolicies = new ConcurrentHashMap<>();
    // UUID -> List<Claim>
    private final Map<String, List<Claim>> playerClaims = new ConcurrentHashMap<>();

    private final double plotMonthlyPremium;      // 500 = 5% of typical plot price
    private final double businessMonthlyPremium;  // 1000
    private final double stockMonthlyPremium;     // 2%
    private final double claimPayoutRatio;        // 0.80 = 80% of covered amount

    private long totalPremiumsCollected = 0;
    private long totalPayoutsIssued = 0;

    public InsuranceSystem(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.plotMonthlyPremium = plugin.getConfig().getDouble("insurance.plot_premium", 500);
        this.businessMonthlyPremium = plugin.getConfig().getDouble("insurance.business_premium", 1000);
        this.stockMonthlyPremium = plugin.getConfig().getDouble("insurance.stock_premium", 0.02);
        this.claimPayoutRatio = plugin.getConfig().getDouble("insurance.claim_payout_ratio", 0.80);
    }

    // ===== POLICY MANAGEMENT =====

    /**
     * Purchase plot insurance (monthly subscription)
     */
    public InsurancePolicy purchasePlotInsurance(String playerUuid, int plotId) {
        // Check if already insured
        if (hasActivePlotInsurance(playerUuid, plotId)) {
            return null;
        }

        if (!economyManager.hasBalance(playerUuid, (long) plotMonthlyPremium)) {
            return null;
        }

        economyManager.withdraw(playerUuid, (long) plotMonthlyPremium);

        InsurancePolicy policy = new InsurancePolicy(
            UUID.randomUUID().toString(),
            playerUuid,
            "PLOT",
            plotId,
            (long) plotMonthlyPremium,
            System.currentTimeMillis()
        );

        playerPolicies.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(policy);
        totalPremiumsCollected += (long) plotMonthlyPremium;

        plugin.getLogger().info("✓ Plot insurance purchased for " + playerUuid.substring(0, 8) + 
            " (Plot #" + plotId + ") - Premium: $" + (long) plotMonthlyPremium);
        return policy;
    }

    /**
     * Purchase business insurance
     */
    public InsurancePolicy purchaseBusinessInsurance(String playerUuid, int businessId) {
        if (hasActiveBusinessInsurance(playerUuid, businessId)) {
            return null;
        }

        if (!economyManager.hasBalance(playerUuid, (long) businessMonthlyPremium)) {
            return null;
        }

        economyManager.withdraw(playerUuid, (long) businessMonthlyPremium);

        InsurancePolicy policy = new InsurancePolicy(
            UUID.randomUUID().toString(),
            playerUuid,
            "BUSINESS",
            businessId,
            (long) businessMonthlyPremium,
            System.currentTimeMillis()
        );

        playerPolicies.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(policy);
        totalPremiumsCollected += (long) businessMonthlyPremium;

        plugin.getLogger().info("✓ Business insurance purchased for " + playerUuid.substring(0, 8) + 
            " (Business #" + businessId + ") - Premium: $" + (long) businessMonthlyPremium);
        return policy;
    }

    /**
     * Purchase stock insurance
     */
    public InsurancePolicy purchaseStockInsurance(String playerUuid, int stockId, long stockValue) {
        if (hasActiveStockInsurance(playerUuid, stockId)) {
            return null;
        }

        long premium = (long) (stockValue * stockMonthlyPremium);
        if (!economyManager.hasBalance(playerUuid, premium)) {
            return null;
        }

        economyManager.withdraw(playerUuid, premium);

        InsurancePolicy policy = new InsurancePolicy(
            UUID.randomUUID().toString(),
            playerUuid,
            "STOCK",
            stockId,
            premium,
            System.currentTimeMillis()
        );

        playerPolicies.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(policy);
        totalPremiumsCollected += premium;

        plugin.getLogger().fine("✓ Stock insurance purchased for " + playerUuid.substring(0, 8) + 
            " (Stock #" + stockId + ") - Premium: $" + premium);
        return policy;
    }

    /**
     * Cancel insurance policy (can be done anytime)
     */
    public boolean cancelPolicy(String playerUuid, String policyId) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return false;
        }

        return policies.removeIf(p -> p.policyId.equals(policyId));
    }

    /**
     * Renew insurance policy (pay next month's premium)
     */
    public boolean renewPolicy(String playerUuid, String policyId) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return false;
        }

        InsurancePolicy policy = policies.stream()
            .filter(p -> p.policyId.equals(policyId))
            .findFirst()
            .orElse(null);

        if (policy == null || !policy.isExpired()) {
            return false;
        }

        if (!economyManager.hasBalance(playerUuid, policy.monthlyPremium)) {
            return false;
        }

        economyManager.withdraw(playerUuid, policy.monthlyPremium);
        policy.expiresAt = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
        totalPremiumsCollected += policy.monthlyPremium;

        plugin.getLogger().fine("✓ Insurance renewed for " + playerUuid.substring(0, 8));
        return true;
    }

    // ===== CLAIMS =====

    /**
     * File insurance claim
     */
    public Claim fileClaim(String playerUuid, String policyId, long claimAmount, String reason) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return null;
        }

        InsurancePolicy policy = policies.stream()
            .filter(p -> p.policyId.equals(policyId) && !p.isExpired())
            .findFirst()
            .orElse(null);

        if (policy == null) {
            return null; // No active policy
        }

        // Check if claim is within policy coverage
        long maxCoverage = policy.monthlyPremium * 10; // 10x monthly premium
        if (claimAmount > maxCoverage) {
            return null; // Claim exceeds coverage
        }

        // Check if already claimed this month
        long oneMonthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        List<Claim> claims = playerClaims.getOrDefault(playerUuid, new ArrayList<>());
        boolean recentClaim = claims.stream()
            .anyMatch(c -> c.policyId.equals(policyId) && c.filedAt > oneMonthAgo);

        if (recentClaim) {
            return null; // Already claimed this month
        }

        long payoutAmount = (long) (claimAmount * claimPayoutRatio);

        Claim claim = new Claim(
            UUID.randomUUID().toString(),
            playerUuid,
            policyId,
            claimAmount,
            payoutAmount,
            reason,
            System.currentTimeMillis()
        );

        playerClaims.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(claim);
        economyManager.deposit(playerUuid, payoutAmount);
        totalPayoutsIssued += payoutAmount;

        plugin.getLogger().info("✓ Insurance claim approved: " + playerUuid.substring(0, 8) + 
            " received $" + payoutAmount + " for " + reason);
        return claim;
    }

    /**
     * Get all claims for player
     */
    public List<Claim> getPlayerClaims(String playerUuid) {
        return new ArrayList<>(playerClaims.getOrDefault(playerUuid, new ArrayList<>()));
    }

    // ===== QUERIES =====

    /**
     * Check if player has active plot insurance
     */
    public boolean hasActivePlotInsurance(String playerUuid, int plotId) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return false;
        }

        return policies.stream()
            .anyMatch(p -> p.type.equals("PLOT") && p.coveredId == plotId && !p.isExpired());
    }

    /**
     * Check if player has active business insurance
     */
    public boolean hasActiveBusinessInsurance(String playerUuid, int businessId) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return false;
        }

        return policies.stream()
            .anyMatch(p -> p.type.equals("BUSINESS") && p.coveredId == businessId && !p.isExpired());
    }

    /**
     * Check if player has active stock insurance
     */
    public boolean hasActiveStockInsurance(String playerUuid, int stockId) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return false;
        }

        return policies.stream()
            .anyMatch(p -> p.type.equals("STOCK") && p.coveredId == stockId && !p.isExpired());
    }

    /**
     * Get all active policies for player
     */
    public List<InsurancePolicy> getPlayerPolicies(String playerUuid) {
        List<InsurancePolicy> policies = playerPolicies.get(playerUuid);
        if (policies == null) {
            return new ArrayList<>();
        }

        return policies.stream()
            .filter(p -> !p.isExpired())
            .toList();
    }

    // ===== STATISTICS =====

    /**
     * Get insurance statistics
     */
    public InsuranceStats getStats() {
        long activePolicies = playerPolicies.values().stream()
            .flatMap(List::stream)
            .filter(p -> !p.isExpired())
            .count();

        long totalClaims = playerClaims.values().stream()
            .flatMap(List::stream)
            .count();

        return new InsuranceStats(
            playerPolicies.size(),
            activePolicies,
            totalClaims,
            totalPremiumsCollected,
            totalPayoutsIssued
        );
    }

    // ===== DATA CLASSES =====

    public static class InsurancePolicy {
        public final String policyId;
        public final String playerUuid;
        public final String type; // PLOT, BUSINESS, STOCK
        public final int coveredId;
        public final long monthlyPremium;
        public final long purchasedAt;
        
        public long expiresAt;

        public InsurancePolicy(String policyId, String playerUuid, String type, int coveredId,
                              long monthlyPremium, long purchasedAt) {
            this.policyId = policyId;
            this.playerUuid = playerUuid;
            this.type = type;
            this.coveredId = coveredId;
            this.monthlyPremium = monthlyPremium;
            this.purchasedAt = purchasedAt;
            this.expiresAt = purchasedAt + (30L * 24 * 60 * 60 * 1000); // 30 days
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }

        public long getDaysRemaining() {
            long remaining = (expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            return Math.max(0, remaining);
        }
    }

    public static class Claim {
        public final String claimId;
        public final String playerUuid;
        public final String policyId;
        public final long claimAmount;
        public final long payoutAmount;
        public final String reason;
        public final long filedAt;

        public Claim(String claimId, String playerUuid, String policyId, long claimAmount,
                    long payoutAmount, String reason, long filedAt) {
            this.claimId = claimId;
            this.playerUuid = playerUuid;
            this.policyId = policyId;
            this.claimAmount = claimAmount;
            this.payoutAmount = payoutAmount;
            this.reason = reason;
            this.filedAt = filedAt;
        }
    }

    public static class InsuranceStats {
        public final int policyHolders;
        public final long activePolicies;
        public final long totalClaims;
        public final long premiumsCollected;
        public final long payoutsIssued;

        public InsuranceStats(int holders, long active, long claims, long premiums, long payouts) {
            this.policyHolders = holders;
            this.activePolicies = active;
            this.totalClaims = claims;
            this.premiumsCollected = premiums;
            this.payoutsIssued = payouts;
        }
    }
}
