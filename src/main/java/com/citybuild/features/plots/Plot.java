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
        return String.format("Plot [ID: %s, Owner: %s, Price: $%.2f, Biome: %s]", 
            plotId, ownerUUID, price, biome);
    }
}
