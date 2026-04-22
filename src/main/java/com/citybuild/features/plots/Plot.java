package com.citybuild.features.plots;

import java.util.UUID;
import java.time.LocalDateTime;

public class Plot {
    private String plotId;
    private UUID ownerUUID;
    private double price;
    private LocalDateTime createdAt;
    private String biome;

    public Plot(String plotId, UUID ownerUUID, double price) {
        if (plotId == null || plotId.trim().isEmpty()) throw new IllegalArgumentException("Plot ID cannot be null or empty");
        if (ownerUUID == null) throw new IllegalArgumentException("Owner UUID cannot be null");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        
        this.plotId = plotId;
        this.ownerUUID = ownerUUID;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.biome = "PLAINS";
    }

    public String getPlotId() {
        return plotId;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
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

    @Override
    public String toString() {
        return String.format("§6Plot [§eID: %s§6, Owner: %s§6, Price: §a$%.2f§6, Biome: %s§6]", 
            plotId, ownerUUID, price, biome);
    }
}
