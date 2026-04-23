package com.citybuild.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages plot rentals for passive income
 * Property owners can lease plots to tenants with automatic rent collection
 */
public class RentalSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    private final PlotManager plotManager;

    // PlotID -> ActiveLease
    private final Map<Integer, Lease> activeLeasesById = new ConcurrentHashMap<>();
    // UUID -> List<Lease> (as landlord)
    private final Map<String, List<Lease>> landlordLeases = new ConcurrentHashMap<>();
    // UUID -> List<Lease> (as tenant)
    private final Map<String, List<Lease>> tenantLeases = new ConcurrentHashMap<>();

    private long totalRentCollected = 0;
    private long totalLeasesCancelled = 0;

    public RentalSystem(JavaPlugin plugin, EconomyManager economyManager, PlotManager plotManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.plotManager = plotManager;
    }

    // ===== LEASE MANAGEMENT =====

    /**
     * Create a new lease (landlord lists plot for rent)
     */
    public Lease createLease(String landlordUuid, int plotId, long dailyRent, long durationDays) {
        // Verify landlord owns the plot
        com.citybuild.model.PlotData plot = plotManager.getPlot(plotId);
        if (plot == null || !plot.getOwnerUuid().equals(landlordUuid)) {
            return null;
        }

        // Check plot not already leased
        if (activeLeasesById.containsKey(plotId)) {
            return null;
        }

        Lease lease = new Lease(
            plotId,
            landlordUuid,
            dailyRent,
            durationDays,
            System.currentTimeMillis()
        );

        activeLeasesById.put(plotId, lease);
        landlordLeases.computeIfAbsent(landlordUuid, k -> new ArrayList<>()).add(lease);

        plugin.getLogger().info("✓ Lease listed: Plot #" + plotId + " by " + landlordUuid.substring(0, 8) + 
            " for $" + dailyRent + "/day (" + durationDays + " days max)");
        return lease;
    }

    /**
     * Accept lease as tenant (pay first month upfront)
     */
    public boolean acceptLease(String tenantUuid, int plotId) {
        Lease lease = activeLeasesById.get(plotId);
        if (lease == null || lease.tenantUuid != null) {
            return false; // Already leased
        }

        long totalRent = lease.dailyRent * lease.durationDays;
        if (!economyManager.hasBalance(tenantUuid, totalRent)) {
            return false; // Insufficient funds
        }

        // Charge tenant
        economyManager.withdraw(tenantUuid, totalRent);
        // Pay landlord
        economyManager.deposit(lease.landlordUuid, totalRent);

        lease.tenantUuid = tenantUuid;
        lease.acceptedAt = System.currentTimeMillis();
        tenantLeases.computeIfAbsent(tenantUuid, k -> new ArrayList<>()).add(lease);

        plugin.getLogger().info("✓ Lease accepted: " + tenantUuid.substring(0, 8) + 
            " rented plot #" + plotId + " for $" + totalRent);
        return true;
    }

    /**
     * Terminate lease (can be called by landlord or tenant)
     */
    public boolean terminateLease(String playerUuid, int plotId) {
        Lease lease = activeLeasesById.get(plotId);
        if (lease == null) {
            return false;
        }

        // Must be landlord or tenant
        if (!lease.landlordUuid.equals(playerUuid) && !playerUuid.equals(lease.tenantUuid)) {
            return false;
        }

        // Calculate remaining days and refund
        if (lease.tenantUuid != null) {
            long elapsedDays = (System.currentTimeMillis() - lease.acceptedAt) / (1000 * 60 * 60 * 24);
            long remainingDays = Math.max(0, lease.durationDays - elapsedDays);
            long refundAmount = lease.dailyRent * remainingDays;

            if (refundAmount > 0) {
                economyManager.deposit(lease.tenantUuid, refundAmount);
            }

            tenantLeases.getOrDefault(lease.tenantUuid, new ArrayList<>()).remove(lease);
        }

        activeLeasesById.remove(plotId);
        landlordLeases.getOrDefault(lease.landlordUuid, new ArrayList<>()).remove(lease);
        totalLeasesCancelled++;

        plugin.getLogger().info("✓ Lease terminated: Plot #" + plotId);
        return true;
    }

    /**
     * Collect rent from all active leases
     */
    public int collectAllRent() {
        int collected = 0;
        List<Integer> toRemove = new ArrayList<>();

        for (Lease lease : activeLeasesById.values()) {
            if (lease.tenantUuid == null || lease.isExpired()) {
                toRemove.add(lease.plotId);
                continue;
            }

            // Collect daily rent
            if (!economyManager.hasBalance(lease.tenantUuid, lease.dailyRent)) {
                // Tenant cannot pay, auto-terminate
                terminateLease(lease.landlordUuid, lease.plotId);
                toRemove.add(lease.plotId);
                continue;
            }

            economyManager.withdraw(lease.tenantUuid, lease.dailyRent);
            economyManager.deposit(lease.landlordUuid, lease.dailyRent);
            lease.totalPaid += lease.dailyRent;
            totalRentCollected += lease.dailyRent;
            collected++;
        }

        // Remove expired leases
        for (Integer plotId : toRemove) {
            Lease expired = activeLeasesById.remove(plotId);
            if (expired != null) {
                landlordLeases.getOrDefault(expired.landlordUuid, new ArrayList<>()).remove(expired);
                if (expired.tenantUuid != null) {
                    tenantLeases.getOrDefault(expired.tenantUuid, new ArrayList<>()).remove(expired);
                }
            }
        }

        if (collected > 0) {
            plugin.getLogger().info("✓ Collected rent from " + collected + " leases ($" + 
                (collected * 1000) + " est.)"); // Rough estimate
        }
        return collected;
    }

    // ===== QUERIES =====

    /**
     * Get lease for plot
     */
    public Lease getLease(int plotId) {
        return activeLeasesById.get(plotId);
    }

    /**
     * Get all active leases (not expired)
     */
    public List<Lease> getActiveLeases() {
        return activeLeasesById.values().stream()
            .filter(l -> !l.isExpired())
            .sorted((a, b) -> Long.compare(b.dailyRent, a.dailyRent))
            .toList();
    }

    /**
     * Get available leases for rent
     */
    public List<Lease> getAvailableLeases() {
        return getActiveLeases().stream()
            .filter(l -> l.tenantUuid == null)
            .toList();
    }

    /**
     * Get leases owned by landlord
     */
    public List<Lease> getLandlordLeases(String landlordUuid) {
        return new ArrayList<>(landlordLeases.getOrDefault(landlordUuid, new ArrayList<>()));
    }

    /**
     * Get leases rented by tenant
     */
    public List<Lease> getTenantLeases(String tenantUuid) {
        return new ArrayList<>(tenantLeases.getOrDefault(tenantUuid, new ArrayList<>()));
    }

    /**
     * Get total rental income for landlord
     */
    public long getLandlordIncome(String landlordUuid) {
        return getLandlordLeases(landlordUuid).stream()
            .mapToLong(l -> l.totalPaid)
            .sum();
    }

    // ===== STATISTICS =====

    /**
     * Get rental statistics
     */
    public RentalStats getStats() {
        List<Lease> active = getActiveLeases();
        List<Lease> available = getAvailableLeases();

        long totalRentCapacity = active.stream()
            .mapToLong(l -> l.dailyRent * l.durationDays)
            .sum();

        return new RentalStats(
            active.size(),
            available.size(),
            active.stream().filter(l -> l.tenantUuid != null).count(),
            totalRentCollected,
            totalLeasesCancelled,
            totalRentCapacity
        );
    }

    /**
     * Get most popular leases
     */
    public List<Lease> getPopularLeases(int limit) {
        return getAvailableLeases().stream()
            .sorted((a, b) -> Long.compare(b.dailyRent, a.dailyRent))
            .limit(limit)
            .toList();
    }

    // ===== DATA CLASSES =====

    public static class Lease {
        public final int plotId;
        public final String landlordUuid;
        public final long dailyRent;
        public final long durationDays;
        public final long createdAt;

        public String tenantUuid = null;
        public long acceptedAt = 0;
        public long totalPaid = 0;

        public Lease(int plotId, String landlordUuid, long dailyRent, long durationDays, long createdAt) {
            this.plotId = plotId;
            this.landlordUuid = landlordUuid;
            this.dailyRent = dailyRent;
            this.durationDays = durationDays;
            this.createdAt = createdAt;
        }

        public boolean isOccupied() {
            return tenantUuid != null;
        }

        public boolean isExpired() {
            if (tenantUuid == null) {
                return false; // Not expired if no tenant
            }

            long elapsedDays = (System.currentTimeMillis() - acceptedAt) / (1000 * 60 * 60 * 24);
            return elapsedDays >= durationDays;
        }

        public long getDaysRemaining() {
            if (tenantUuid == null || acceptedAt == 0) {
                return durationDays;
            }

            long elapsedDays = (System.currentTimeMillis() - acceptedAt) / (1000 * 60 * 60 * 24);
            return Math.max(0, durationDays - elapsedDays);
        }

        public long getTotalValue() {
            return dailyRent * durationDays;
        }
    }

    public static class RentalStats {
        public final long activeLeases;
        public final long availableLeases;
        public final long occupiedLeases;
        public final long totalRentCollected;
        public final long leasesTerminated;
        public final long potentialRentCapacity;

        public RentalStats(long active, long available, long occupied, long collected, long terminated, long capacity) {
            this.activeLeases = active;
            this.availableLeases = available;
            this.occupiedLeases = occupied;
            this.totalRentCollected = collected;
            this.leasesTerminated = terminated;
            this.potentialRentCapacity = capacity;
        }
    }
}
