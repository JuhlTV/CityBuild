package com.citybuild.model;

import org.bukkit.Location;
import java.util.*;

/**
 * Represents a single player plot with dimensions, owner, members, and metadata
 */
public class PlotData {
    private final int plotId;
    private final String ownerUuid;
    private int sizeX = 16;  // Width (default 16x16)
    private int sizeZ = 16;  // Depth
    private int cornerX;     // Corner coordinates for calculations
    private int cornerZ;
    private final Set<String> members = new HashSet<>();
    private final long createdAt;
    private long lastModified;
    private boolean isPremium = false;
    private String biome = "plains";
    
    public PlotData(int plotId, String ownerUuid, int cornerX, int cornerZ) {
        this.plotId = plotId;
        this.ownerUuid = ownerUuid;
        this.cornerX = cornerX;
        this.cornerZ = cornerZ;
        this.createdAt = System.currentTimeMillis();
        this.lastModified = this.createdAt;
    }

    // ===== GETTERS =====
    public int getPlotId() { return plotId; }
    public String getOwnerUuid() { return ownerUuid; }
    public int getSizeX() { return sizeX; }
    public int getSizeZ() { return sizeZ; }
    public int getCornerX() { return cornerX; }
    public int getCornerZ() { return cornerZ; }
    public int getCenterX() { return cornerX + sizeX / 2; }
    public int getCenterZ() { return cornerZ + sizeZ / 2; }
    public Set<String> getMembers() { return new HashSet<>(members); }
    public long getCreatedAt() { return createdAt; }
    public long getLastModified() { return lastModified; }
    public boolean isPremium() { return isPremium; }
    public String getBiome() { return biome; }

    // ===== SETTERS =====
    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
        this.lastModified = System.currentTimeMillis();
    }

    public void setSizeZ(int sizeZ) {
        this.sizeZ = sizeZ;
        this.lastModified = System.currentTimeMillis();
    }

    public void setCornerX(int cornerX) {
        this.cornerX = cornerX;
        this.lastModified = System.currentTimeMillis();
    }

    public void setCornerZ(int cornerZ) {
        this.cornerZ = cornerZ;
        this.lastModified = System.currentTimeMillis();
    }

    public void setPremium(boolean premium) {
        this.isPremium = premium;
        this.lastModified = System.currentTimeMillis();
    }

    public void setBiome(String biome) {
        this.biome = biome;
        this.lastModified = System.currentTimeMillis();
    }

    // ===== MEMBER MANAGEMENT =====
    public void addMember(String uuid) {
        members.add(uuid);
        this.lastModified = System.currentTimeMillis();
    }

    public void removeMember(String uuid) {
        members.remove(uuid);
        this.lastModified = System.currentTimeMillis();
    }

    public boolean isMember(String uuid) {
        return members.contains(uuid);
    }

    public boolean isOwner(String uuid) {
        return ownerUuid.equals(uuid);
    }

    public boolean isOwnerOrMember(String uuid) {
        return isOwner(uuid) || isMember(uuid);
    }

    public int getMemberCount() {
        return members.size();
    }

    // ===== BOUNDARY CHECKS =====
    public boolean isLocationInPlot(int x, int z) {
        return x >= cornerX && x < cornerX + sizeX &&
               z >= cornerZ && z < cornerZ + sizeZ;
    }

    public boolean isLocationInBoundary(int x, int z, int margin) {
        return x >= cornerX - margin && x <= cornerX + sizeX + margin &&
               z >= cornerZ - margin && z <= cornerZ + sizeZ + margin;
    }

    // ===== SIZE CALCULATIONS =====
    public long getArea() {
        return (long) sizeX * sizeZ;
    }

    public int getPerimeter() {
        return 2 * (sizeX + sizeZ);
    }

    @Override
    public String toString() {
        return String.format("Plot #%d | Owner: %s | Size: %dx%d | Area: %d | Premium: %s",
                plotId, ownerUuid, sizeX, sizeZ, getArea(), isPremium);
    }
}
