package com.citybuild.features.plots;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

/**
 * Plot - Represents a single plot with fixed coordinates at Y = -60
 * Supports co-ownership for collaborative building
 */
public class Plot {
    private String plotId;
    private UUID ownerUUID;
    private double price;
    private LocalDateTime createdAt;
    private String biome;
    private Set<UUID> coOwners;  // Co-owners for collaborative building
    
    // Coordinates (all plots at Y = -60)
    private int x1, z1, x2, z2;
    private static final int PLOT_Y = -60;

    public Plot(String plotId, UUID ownerUUID, double price, int x1, int z1, int x2, int z2) {
        if (plotId == null || plotId.trim().isEmpty()) throw new IllegalArgumentException("Plot ID cannot be null or empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        
        this.plotId = plotId;
        this.ownerUUID = ownerUUID; // Can be null for unowned plots
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.biome = "PLAINS";
        this.coOwners = new HashSet<>();
        
        // Set coordinates
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public String getPlotId() {
        return plotId;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    public boolean isOwned() {
        return ownerUUID != null;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getBiome() {
        return biome;
    }

    public void setBiome(String biome) {
        this.biome = biome;
    }
    
    // Coordinate getters
    public int getX1() {
        return x1;
    }
    
    public int getZ1() {
        return z1;
    }
    
    public int getX2() {
        return x2;
    }
    
    public int getZ2() {
        return z2;
    }
    
    public int getY() {
        return PLOT_Y;
    }
    
    public boolean isInPlot(int x, int y, int z) {
        return x >= x1 && x <= x2 && z >= z1 && z <= z2;
    }
    
    // Co-Owner Methods
    public boolean addCoOwner(UUID playerUUID) {
        if (ownerUUID == null) return false; // Cannot add co-owners to unowned plots
        if (playerUUID.equals(ownerUUID)) return false; // Owner cannot be co-owner
        return coOwners.add(playerUUID);
    }
    
    public boolean removeCoOwner(UUID playerUUID) {
        return coOwners.remove(playerUUID);
    }
    
    public boolean isCoOwner(UUID playerUUID) {
        return coOwners.contains(playerUUID);
    }
    
    public boolean canBuild(UUID playerUUID) {
        return playerUUID.equals(ownerUUID) || isCoOwner(playerUUID);
    }
    
    public Set<UUID> getCoOwners() {
        return new HashSet<>(coOwners);
    }
    
    public int getCoOwnerCount() {
        return coOwners.size();
    }

    @Override
    public String toString() {
        if (ownerUUID == null) {
            return String.format("§6Plot [§eID: %s§6, Status: §aAvailable§6, Price: §a$%.2f§6, Coords: (%d,%d-%d,%d)§6]", 
                plotId, price, x1, z1, x2, z2);
        }
        String coOwnerInfo = coOwners.isEmpty() ? "" : String.format(" §6+ §a%d Co-Owners", coOwners.size());
        return String.format("§6Plot [§eID: %s§6, Owner: %s§6, Price: §a$%.2f§6, Coords: (%d,%d-%d,%d)%s§6]", 
            plotId, ownerUUID, price, x1, z1, x2, z2, coOwnerInfo);
    }
}
