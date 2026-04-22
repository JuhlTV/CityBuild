package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;

public class PlotTaxManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<Integer, PlotTaxData> plotTaxes;
    
    private final long DAILY_TAX = 500; // $500 per day per plot
    private final long PREMIUM_MULTIPLIER = 2; // 2x tax for premium plots

    public static class PlotTaxData {
        public int plotId;
        public String ownerUuid;
        public long lastPaid;
        public boolean isPremium;
        public int daysOverdue;

        public PlotTaxData(int plotId, String ownerUuid) {
            this.plotId = plotId;
            this.ownerUuid = ownerUuid;
            this.lastPaid = System.currentTimeMillis();
            this.isPremium = false;
            this.daysOverdue = 0;
        }
    }

    public PlotTaxManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/plot_taxes.json");
        this.plotTaxes = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    // ===== TAX OPERATIONS =====
    public void createPlotTax(int plotId, String ownerUuid) {
        plotTaxes.put(plotId, new PlotTaxData(plotId, ownerUuid));
        saveData();
    }

    public void upgradeToPremium(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax != null) {
            tax.isPremium = true;
            saveData();
        }
    }

    public void downgradeFromPremium(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax != null) {
            tax.isPremium = false;
            saveData();
        }
    }

    public long getDailyTax(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax == null) return DAILY_TAX;
        
        long baseTax = DAILY_TAX;
        return tax.isPremium ? baseTax * PREMIUM_MULTIPLIER : baseTax;
    }

    public long getTotalOwedTax(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax == null) return 0;
        
        long daysPassed = (System.currentTimeMillis() - tax.lastPaid) / (1000 * 60 * 60 * 24);
        return daysPassed * getDailyTax(plotId);
    }

    public boolean payTax(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax == null) return false;
        
        tax.lastPaid = System.currentTimeMillis();
        tax.daysOverdue = 0;
        saveData();
        return true;
    }

    public boolean isOverdue(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax == null) return false;
        
        long daysPassed = (System.currentTimeMillis() - tax.lastPaid) / (1000 * 60 * 60 * 24);
        return daysPassed > 1; // Overdue after 1 day
    }

    public int getDaysOverdue(int plotId) {
        PlotTaxData tax = plotTaxes.get(plotId);
        if (tax == null) return 0;
        
        long daysPassed = (System.currentTimeMillis() - tax.lastPaid) / (1000 * 60 * 60 * 24);
        return Math.max(0, (int)daysPassed - 1);
    }

    public PlotTaxData getPlotTax(int plotId) {
        return plotTaxes.get(plotId);
    }

    public Map<Integer, PlotTaxData> getAllPlotTaxes() {
        return new HashMap<>(plotTaxes);
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                int plotId = Integer.parseInt(entry.getKey());
                JsonObject tJson = entry.getValue().getAsJsonObject();
                PlotTaxData tax = new PlotTaxData(plotId, tJson.get("ownerUuid").getAsString());
                tax.lastPaid = tJson.get("lastPaid").getAsLong();
                tax.isPremium = tJson.get("isPremium").getAsBoolean();
                tax.daysOverdue = tJson.get("daysOverdue").getAsInt();
                plotTaxes.put(plotId, tax);
            });

            plugin.getLogger().info("✓ Loaded plot taxes");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load plot taxes: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            plotTaxes.forEach((plotId, tax) -> {
                JsonObject tJson = new JsonObject();
                tJson.addProperty("plotId", tax.plotId);
                tJson.addProperty("ownerUuid", tax.ownerUuid);
                tJson.addProperty("lastPaid", tax.lastPaid);
                tJson.addProperty("isPremium", tax.isPremium);
                tJson.addProperty("daysOverdue", tax.daysOverdue);
                json.add(String.valueOf(plotId), tJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save plot taxes: " + e.getMessage());
        }
    }
}
