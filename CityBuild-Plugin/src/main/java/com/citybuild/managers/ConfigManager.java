package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private final Map<String, Object> config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config/settings.json");
        this.config = new HashMap<>();

        configFile.getParentFile().mkdirs();
        loadDefaults();
        loadData();
    }

    // ===== ECONOMY SETTINGS =====
    public void setStartingBalance(long amount) {
        config.put("economy.starting_balance", amount);
        saveData();
    }

    public long getStartingBalance() {
        return getLong("economy.starting_balance", 10000L);
    }

    public void setPlotBuyPrice(long price) {
        config.put("economy.plot_buy_price", price);
        saveData();
    }

    public long getPlotBuyPrice() {
        return getLong("economy.plot_buy_price", 5000L);
    }

    public void setPlotSellPrice(long price) {
        config.put("economy.plot_sell_price", price);
        saveData();
    }

    public long getPlotSellPrice() {
        return getLong("economy.plot_sell_price", 4000L);
    }

    // ===== PLOT SETTINGS =====
    public void setPlotHeight(int height) {
        config.put("plot.spawn_height", height);
        saveData();
    }

    public int getPlotHeight() {
        return getInt("plot.spawn_height", -60);
    }

    public void setPlotSize(int size) {
        config.put("plot.size", size);
        saveData();
    }

    public int getPlotSize() {
        return getInt("plot.size", 16);
    }

    // ===== DAILY REWARDS =====
    public void setDailyRewardBase(long amount) {
        config.put("daily.base_reward", amount);
        saveData();
    }

    public long getDailyRewardBase() {
        return getLong("daily.base_reward", 500L);
    }

    public void setDailyRewardBonus(long amount) {
        config.put("daily.bonus_per_streak", amount);
        saveData();
    }

    public long getDailyRewardBonus() {
        return getLong("daily.bonus_per_streak", 100L);
    }

    // ===== FARM REWARDS =====
    public void setFarmBlockReward(long amount) {
        config.put("farm.block_reward", amount);
        saveData();
    }

    public long getFarmBlockReward() {
        return getLong("farm.block_reward", 10L);
    }

    // ===== PVP REWARDS =====
    public void setPvpKillReward(long amount) {
        config.put("pvp.kill_reward", amount);
        saveData();
    }

    public long getPvpKillReward() {
        return getLong("pvp.kill_reward", 50L);
    }

    // ===== PLAYTIME REWARDS =====
    public void setPlaytimeRewardPerHour(long amount) {
        config.put("playtime.reward_per_hour", amount);
        saveData();
    }

    public long getPlaytimeRewardPerHour() {
        return getLong("playtime.reward_per_hour", 250L);
    }

    // ===== TAX SETTINGS =====
    public void setDailyPlotTax(long amount) {
        config.put("tax.daily_plot_tax", amount);
        saveData();
    }

    public long getDailyPlotTax() {
        return getLong("tax.daily_plot_tax", 500L);
    }

    public void setPremiumTaxMultiplier(double multiplier) {
        config.put("tax.premium_multiplier", multiplier);
        saveData();
    }

    public double getPremiumTaxMultiplier() {
        return getDouble("tax.premium_multiplier", 2.0);
    }

    // ===== ENCHANTING SETTINGS =====
    public void setEnchantBasicCost(long cost) {
        config.put("enchant.basic_cost", cost);
        saveData();
    }

    public long getEnchantBasicCost() {
        return getLong("enchant.basic_cost", 1000L);
    }

    public void setEnchantAdvancedCost(long cost) {
        config.put("enchant.advanced_cost", cost);
        saveData();
    }

    public long getEnchantAdvancedCost() {
        return getLong("enchant.advanced_cost", 5000L);
    }

    public void setEnchantLegendaryCost(long cost) {
        config.put("enchant.legendary_cost", cost);
        saveData();
    }

    public long getEnchantLegendaryCost() {
        return getLong("enchant.legendary_cost", 25000L);
    }

    // ===== SERVER SETTINGS =====
    public void setServerMotd(String motd) {
        config.put("server.motd", motd);
        saveData();
    }

    public String getServerMotd() {
        return getString("server.motd", "Welcome to CityBuild!");
    }

    public void setMaxPlotPerPlayer(int max) {
        config.put("server.max_plots_per_player", max);
        saveData();
    }

    public int getMaxPlotPerPlayer() {
        return getInt("server.max_plots_per_player", 5);
    }

    // ===== UTILITY METHODS =====
    public String getString(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue).toString();
    }

    public long getLong(String key, long defaultValue) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        return (int) getLong(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    public void set(String key, Object value) {
        config.put(key, value);
        saveData();
    }

    public Object get(String key) {
        return config.get(key);
    }

    public Map<String, Object> getAllSettings() {
        return new HashMap<>(config);
    }

    private void loadDefaults() {
        // Economy
        config.putIfAbsent("economy.starting_balance", 10000L);
        config.putIfAbsent("economy.plot_buy_price", 5000L);
        config.putIfAbsent("economy.plot_sell_price", 4000L);

        // Plot
        config.putIfAbsent("plot.spawn_height", -60);
        config.putIfAbsent("plot.size", 16);

        // Daily
        config.putIfAbsent("daily.base_reward", 500L);
        config.putIfAbsent("daily.bonus_per_streak", 100L);

        // Farm
        config.putIfAbsent("farm.block_reward", 10L);

        // PvP
        config.putIfAbsent("pvp.kill_reward", 50L);

        // Playtime
        config.putIfAbsent("playtime.reward_per_hour", 250L);

        // Tax
        config.putIfAbsent("tax.daily_plot_tax", 500L);
        config.putIfAbsent("tax.premium_multiplier", 2.0);

        // Enchanting
        config.putIfAbsent("enchant.basic_cost", 1000L);
        config.putIfAbsent("enchant.advanced_cost", 5000L);
        config.putIfAbsent("enchant.legendary_cost", 25000L);

        // Server
        config.putIfAbsent("server.motd", "Welcome to CityBuild!");
        config.putIfAbsent("server.max_plots_per_player", 5);
    }

    private void loadData() {
        try {
            if (!configFile.exists()) {
                saveData();
                return;
            }

            JsonObject json = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                Object value = gson.fromJson(entry.getValue(), Object.class);
                config.put(entry.getKey(), value);
            });

            plugin.getLogger().info("✓ Loaded settings from config");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load settings: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            configFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            config.forEach((key, value) -> {
                json.add(key, gson.toJsonTree(value));
            });

            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save settings: " + e.getMessage());
        }
    }
}
