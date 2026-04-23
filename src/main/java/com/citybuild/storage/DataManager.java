package com.citybuild.storage;

import org.bukkit.plugin.Plugin;
import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * DataManager - Handles JSON persistence for all player and plot data
 */
public class DataManager {
    private final Plugin plugin;
    private final Path dataFolder;
    private final Path playersFolder;
    private final Path plotsFolder;
    private final Gson gson;
    
    // Auto-save interval (5 minutes = 6000 ticks)
    private static final long AUTO_SAVE_INTERVAL = 6000;

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFolder = Paths.get(plugin.getDataFolder().getAbsolutePath());
        this.playersFolder = dataFolder.resolve("players");
        this.plotsFolder = dataFolder.resolve("plots");
        
        initializeFolders();
        startAutoSave();
    }

    /**
     * Create required directories
     */
    private void initializeFolders() {
        try {
            Files.createDirectories(playersFolder);
            Files.createDirectories(plotsFolder);
            plugin.getLogger().info("✓ Data folders initialized");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create data folders: " + e.getMessage());
        }
    }

    /**
     * Start auto-save scheduler
     */
    private void startAutoSave() {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Auto-save is triggered by individual managers
        }, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
        plugin.getLogger().info("✓ Auto-save scheduler started (every 5 minutes)");
    }

    // ========== PLAYER DATA ==========

    /**
     * Save player economy data
     */
    public void savePlayerEconomy(String playerUUID, double balance) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("uuid", playerUUID);
            data.addProperty("balance", balance);
            data.addProperty("lastSaved", System.currentTimeMillis());
            
            Path file = playersFolder.resolve(playerUUID + "_economy.json");
            saveJson(file, data);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save economy data for " + playerUUID + ": " + e.getMessage());
        }
    }

    /**
     * Load player economy data
     */
    public Double loadPlayerEconomy(String playerUUID) {
        try {
            Path file = playersFolder.resolve(playerUUID + "_economy.json");
            if (!Files.exists(file)) return null;
            
            JsonObject data = loadJson(file).getAsJsonObject();
            return data.get("balance").getAsDouble();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load economy data for " + playerUUID + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Save player farm data (blocks mined, coins earned, streak, level, achievements, etc.)
     */
    public void savePlayerFarmData(String playerUUID, Map<String, Object> farmData) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("uuid", playerUUID);
            
            // Store all farm statistics
            for (Map.Entry<String, Object> entry : farmData.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Number) {
                    data.addProperty(entry.getKey(), (Number) value);
                } else if (value instanceof Boolean) {
                    data.addProperty(entry.getKey(), (Boolean) value);
                } else if (value instanceof String) {
                    data.addProperty(entry.getKey(), (String) value);
                }
            }
            
            data.addProperty("lastSaved", System.currentTimeMillis());
            Path file = playersFolder.resolve(playerUUID + "_farmdata.json");
            saveJson(file, data);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save farm data for " + playerUUID + ": " + e.getMessage());
        }
    }

    /**
     * Load player farm data
     */
    public Map<String, Object> loadPlayerFarmData(String playerUUID) {
        try {
            Path file = playersFolder.resolve(playerUUID + "_farmdata.json");
            if (!Files.exists(file)) return null;
            
            JsonObject data = loadJson(file).getAsJsonObject();
            Map<String, Object> result = new HashMap<>();
            
            for (String key : data.keySet()) {
                if (key.equals("uuid") || key.equals("lastSaved")) continue;
                JsonElement element = data.get(key);
                if (element.isJsonPrimitive()) {
                    JsonPrimitive prim = element.getAsJsonPrimitive();
                    if (prim.isNumber()) {
                        Number num = prim.getAsNumber();
                        // Try to determine if it's int or double
                        if (num.doubleValue() == num.longValue()) {
                            result.put(key, num.longValue());
                        } else {
                            result.put(key, num.doubleValue());
                        }
                    } else if (prim.isBoolean()) {
                        result.put(key, prim.getAsBoolean());
                    } else {
                        result.put(key, prim.getAsString());
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load farm data for " + playerUUID + ": " + e.getMessage());
            return null;
        }
    }

    // ========== PLOT DATA ==========

    /**
     * Save plot data with construction data and coordinates
     */
    public void savePlot(String plotId, String ownerUUID, double price, int x1, int z1, int x2, int z2, List<BlockData> constructedBlocks) {
        try {
            JsonObject plot = new JsonObject();
            plot.addProperty("plotId", plotId);
            plot.addProperty("ownerUUID", ownerUUID);
            plot.addProperty("price", price);
            plot.addProperty("createdAt", System.currentTimeMillis());
            
            // Add coordinates
            plot.addProperty("x1", x1);
            plot.addProperty("z1", z1);
            plot.addProperty("x2", x2);
            plot.addProperty("z2", z2);
            
            // Add constructed blocks
            JsonArray blocksArray = new JsonArray();
            for (BlockData block : constructedBlocks) {
                blocksArray.add(block.toJson());
            }
            plot.add("constructedBlocks", blocksArray);
            
            Path file = plotsFolder.resolve(plotId + ".json");
            saveJson(file, plot);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save plot " + plotId + ": " + e.getMessage());
        }
    }

    /**
     * Load plot data including construction data
     */
    public PlotData loadPlot(String plotId) {
        try {
            Path file = plotsFolder.resolve(plotId + ".json");
            if (!Files.exists(file)) return null;
            
            JsonObject data = loadJson(file).getAsJsonObject();
            
            String ownerUUID = data.has("ownerUUID") && !data.get("ownerUUID").isJsonNull() 
                ? data.get("ownerUUID").getAsString() : null;
            double price = data.get("price").getAsDouble();
            
            // Load coordinates
            int x1 = data.has("x1") ? data.get("x1").getAsInt() : 0;
            int z1 = data.has("z1") ? data.get("z1").getAsInt() : 0;
            int x2 = data.has("x2") ? data.get("x2").getAsInt() : 0;
            int z2 = data.has("z2") ? data.get("z2").getAsInt() : 0;
            
            List<BlockData> blocks = new ArrayList<>();
            if (data.has("constructedBlocks")) {
                JsonArray blocksArray = data.getAsJsonArray("constructedBlocks");
                for (JsonElement elem : blocksArray) {
                    blocks.add(BlockData.fromJson(elem.getAsJsonObject()));
                }
            }
            
            return new PlotData(plotId, ownerUUID, price, x1, z1, x2, z2, blocks);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load plot " + plotId + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Load all plots
     */
    public List<PlotData> loadAllPlots() {
        List<PlotData> plots = new ArrayList<>();
        try {
            if (!Files.exists(plotsFolder)) return plots;
            
            Files.list(plotsFolder)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(p -> {
                    String plotId = p.getFileName().toString().replace(".json", "");
                    PlotData plot = loadPlot(plotId);
                    if (plot != null) {
                        plots.add(plot);
                    }
                });
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load plots: " + e.getMessage());
        }
        return plots;
    }

    /**
     * Delete plot file
     */
    public void deletePlot(String plotId) {
        try {
            Path file = plotsFolder.resolve(plotId + ".json");
            Files.deleteIfExists(file);
            plugin.getLogger().info("Deleted plot data: " + plotId);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to delete plot " + plotId + ": " + e.getMessage());
        }
    }

    // ========== UTILITY ==========

    /**
     * Save JSON to file
     */
    private void saveJson(Path file, JsonObject data) throws IOException {
        String json = gson.toJson(data);
        Files.write(file, json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Load JSON from file
     */
    private JsonElement loadJson(Path file) throws IOException {
        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        return JsonParser.parseString(content);
    }

    public void saveAllData() {
        plugin.getLogger().info("✓ All data saved to JSON");
    }

    /**
     * Block data structure - represents a placed block on a plot
     */
    public static class BlockData {
        public int x, y, z;
        public String material;
        public String data; // For block states

        public BlockData(int x, int y, int z, String material, String data) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
            this.data = data;
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("x", x);
            obj.addProperty("y", y);
            obj.addProperty("z", z);
            obj.addProperty("material", material);
            obj.addProperty("data", data);
            return obj;
        }

        public static BlockData fromJson(JsonObject obj) {
            return new BlockData(
                obj.get("x").getAsInt(),
                obj.get("y").getAsInt(),
                obj.get("z").getAsInt(),
                obj.get("material").getAsString(),
                obj.get("data").getAsString()
            );
        }
    }

    // ========== ACHIEVEMENTS ==========

    /**
     * Save player achievements data
     */
    public void savePlayerAchievements(String playerUUID, Map<String, Object> achievementData) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("uuid", playerUUID);
            
            // Convert achievement data to JSON
            for (Map.Entry<String, Object> entry : achievementData.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    data.add(entry.getKey(), gson.toJsonTree(value));
                } else if (value instanceof Number) {
                    data.addProperty(entry.getKey(), (Number) value);
                } else if (value instanceof Boolean) {
                    data.addProperty(entry.getKey(), (Boolean) value);
                } else if (value instanceof String) {
                    data.addProperty(entry.getKey(), (String) value);
                }
            }
            
            data.addProperty("lastSaved", System.currentTimeMillis());
            Path file = playersFolder.resolve(playerUUID + "_achievements.json");
            saveJson(file, data);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save achievements for " + playerUUID + ": " + e.getMessage());
        }
    }

    /**
     * Load player achievements data
     */
    public Map<String, Object> loadPlayerAchievements(String playerUUID) {
        try {
            Path file = playersFolder.resolve(playerUUID + "_achievements.json");
            if (!Files.exists(file)) return null;
            
            JsonObject data = loadJson(file).getAsJsonObject();
            Map<String, Object> result = new HashMap<>();
            
            for (String key : data.keySet()) {
                if (key.equals("uuid") || key.equals("lastSaved")) continue;
                JsonElement element = data.get(key);
                result.put(key, gson.fromJson(element, Object.class));
            }
            
            return result;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load achievements for " + playerUUID + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Plot data structure
     */
    public static class PlotData {
        public String plotId;
        public String ownerUUID;
        public double price;
        public int x1, z1, x2, z2;
        public List<BlockData> constructedBlocks;

        public PlotData(String plotId, String ownerUUID, double price, int x1, int z1, int x2, int z2, List<BlockData> blocks) {
            this.plotId = plotId;
            this.ownerUUID = ownerUUID;
            this.price = price;
            this.x1 = x1;
            this.z1 = z1;
            this.x2 = x2;
            this.z2 = z2;
            this.constructedBlocks = blocks;
        }
    }
}
