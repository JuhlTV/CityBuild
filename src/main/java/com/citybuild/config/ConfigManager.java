package com.citybuild.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages plugin configuration
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;
    private Map<String, Object> cachedValues;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cachedValues = new HashMap<>();
        loadConfig();
    }

    /**
     * Load configuration
     */
    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        cacheValues();
        plugin.getLogger().info("§a✓ Configuration loaded");
    }

    /**
     * Create default configuration
     */
    private void createDefaultConfig() {
        try {
            plugin.getDataFolder().mkdirs();
            configFile.createNewFile();

            config = new YamlConfiguration();

            // Economy Settings
            config.set("economy.starting-balance", 10000.0);
            config.set("economy.farming-multiplier", 2.0);
            config.set("economy.trading-fee", 0.05);
            config.set("economy.auction-fee", 0.10);

            // Plot Settings
            config.set("plots.buy-price", 5000.0);
            config.set("plots.sell-multiplier", 0.8);
            config.set("plots.co-owner-limit", 5);
            config.set("plots.max-claim", 50);

            // Quest Settings
            config.set("quests.enabled", true);
            config.set("quests.daily-reset-hour", 0);
            config.set("quests.weekly-reset-day", 1);
            config.set("quests.reward-multiplier", 1.0);

            // Dungeon Settings
            config.set("dungeons.enabled", true);
            config.set("dungeons.easy-reward", 5000.0);
            config.set("dungeons.normal-reward", 15000.0);
            config.set("dungeons.hard-reward", 40000.0);
            config.set("dungeons.legendary-reward", 100000.0);
            config.set("dungeons.max-players", 6);

            // Treasure Settings
            config.set("treasures.enabled", true);
            config.set("treasures.spawn-interval-hours", 24);
            config.set("treasures.treasure-lifetime-days", 7);
            config.set("treasures.initial-spawn-count", 10);

            // Clan Settings
            config.set("clans.enabled", true);
            config.set("clans.max-members", 100);
            config.set("clans.creation-cost", 50000.0);
            config.set("clans.level-multiplier", 1.5);

            // Guild Settings
            config.set("guilds.enabled", true);
            config.set("guilds.max-members", 50);
            config.set("guilds.treasury-enabled", true);

            // Event Settings
            config.set("events.enabled", true);
            config.set("events.seasonal-duration-days", 30);
            config.set("events.reward-multiplier", 1.0);

            // NPC Settings
            config.set("npcs.enabled", true);
            config.set("npcs.trading-enabled", true);
            config.set("npcs.repair-cost-multiplier", 1.0);

            // Auction House Settings
            config.set("auction.enabled", true);
            config.set("auction.duration-hours", 24);
            config.set("auction.min-bid-increment", 100.0);

            // WorldGuard Settings
            config.set("worldguard.enabled", false);
            config.set("worldguard.auto-create-regions", true);

            // Feature Flags
            config.set("features.achievements", true);
            config.set("features.ranking", true);
            config.set("features.cosmetics", true);
            config.set("features.guis", true);
            config.set("features.persistence", true);

            // Server Settings
            config.set("server.name", "CityBuild");
            config.set("server.version", "1.0");
            config.set("server.language", "en");
            config.set("server.auto-save-interval", 300);

            config.save(configFile);
            plugin.getLogger().info("§6✓ Default configuration created");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create config: " + e.getMessage());
        }
    }

    /**
     * Cache all values for faster access
     */
    private void cacheValues() {
        cachedValues.clear();
        config.getKeys(true).forEach(key -> {
            Object value = config.get(key);
            if (value != null) {
                cachedValues.put(key, value);
            }
        });
    }

    /**
     * Get double value
     */
    public double getDouble(String path, double def) {
        Object cached = cachedValues.get(path);
        if (cached instanceof Number) {
            return ((Number) cached).doubleValue();
        }
        return config.getDouble(path, def);
    }

    /**
     * Get int value
     */
    public int getInt(String path, int def) {
        Object cached = cachedValues.get(path);
        if (cached instanceof Number) {
            return ((Number) cached).intValue();
        }
        return config.getInt(path, def);
    }

    /**
     * Get boolean value
     */
    public boolean getBoolean(String path, boolean def) {
        Object cached = cachedValues.get(path);
        if (cached instanceof Boolean) {
            return (boolean) cached;
        }
        return config.getBoolean(path, def);
    }

    /**
     * Get string value
     */
    public String getString(String path, String def) {
        Object cached = cachedValues.get(path);
        if (cached instanceof String) {
            return (String) cached;
        }
        return config.getString(path, def);
    }

    /**
     * Set value
     */
    public void setValue(String path, Object value) {
        config.set(path, value);
        cachedValues.put(path, value);
        saveConfig();
    }

    /**
     * Save configuration
     */
    public void saveConfig() {
        try {
            config.save(configFile);
            plugin.getLogger().info("§a✓ Configuration saved");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save config: " + e.getMessage());
        }
    }

    /**
     * Reload configuration
     */
    public void reloadConfig() {
        loadConfig();
        plugin.getLogger().info("§a✓ Configuration reloaded");
    }

    /**
     * Get configuration summary
     */
    public String getConfigSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6╔════════════════════════════════════════╗\n");
        sb.append("§6║ CONFIGURATION SUMMARY\n");
        sb.append("§6╚════════════════════════════════════════╝\n\n");

        sb.append("§7Economy:\n");
        sb.append("  §6Starting Balance: $").append(getDouble("economy.starting-balance", 10000)).append("\n");
        sb.append("  §6Farming Multiplier: ").append(getDouble("economy.farming-multiplier", 2.0)).append("x\n");

        sb.append("\n§7Dungeons:\n");
        sb.append("  §6Enabled: ").append(getBoolean("dungeons.enabled", true)).append("\n");
        sb.append("  §6Max Players: ").append(getInt("dungeons.max-players", 6)).append("\n");

        sb.append("\n§7Quests:\n");
        sb.append("  §6Enabled: ").append(getBoolean("quests.enabled", true)).append("\n");
        sb.append("  §6Reward Multiplier: ").append(getDouble("quests.reward-multiplier", 1.0)).append("x\n");

        sb.append("\n§7Features:\n");
        if (getBoolean("features.achievements", true)) sb.append("  §a✓ Achievements\n");
        if (getBoolean("features.ranking", true)) sb.append("  §a✓ Ranking\n");
        if (getBoolean("features.cosmetics", true)) sb.append("  §a✓ Cosmetics\n");
        if (getBoolean("features.guis", true)) sb.append("  §a✓ GUIs\n");

        return sb.toString();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
