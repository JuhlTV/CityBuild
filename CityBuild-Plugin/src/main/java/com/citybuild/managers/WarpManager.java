package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class WarpManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, Warp> warps; // Warp name -> Warp

    public static class Warp {
        public String name;
        public String creator; // UUID
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
        public String world;
        public long createdAt;

        public Warp(String name, String creator, Location loc) {
            this.name = name;
            this.creator = creator;
            this.x = loc.getX();
            this.y = loc.getY();
            this.z = loc.getZ();
            this.yaw = loc.getYaw();
            this.pitch = loc.getPitch();
            this.world = loc.getWorld().getName();
            this.createdAt = System.currentTimeMillis();
        }

        public Location toLocation(org.bukkit.World world) {
            return new Location(world, x, y, z, yaw, pitch);
        }
    }

    public WarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/warps.json");
        this.warps = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    /**
     * Create a new warp at location
     */
    public boolean createWarp(String warpName, String creatorUuid, Location location) {
        if (warps.containsKey(warpName.toLowerCase())) {
            return false;
        }

        Warp warp = new Warp(warpName.toLowerCase(), creatorUuid, location);
        warps.put(warpName.toLowerCase(), warp);
        saveData();
        return true;
    }

    /**
     * Delete a warp
     */
    public boolean deleteWarp(String warpName) {
        if (warps.remove(warpName.toLowerCase()) != null) {
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Get warp by name
     */
    public Warp getWarp(String warpName) {
        return warps.get(warpName.toLowerCase());
    }

    /**
     * Get all warps
     */
    public Collection<Warp> getAllWarps() {
        return new ArrayList<>(warps.values());
    }

    /**
     * Get warps created by specific player
     */
    public List<Warp> getPlayerWarps(String creatorUuid) {
        List<Warp> playerWarps = new ArrayList<>();
        for (Warp warp : warps.values()) {
            if (warp.creator.equals(creatorUuid)) {
                playerWarps.add(warp);
            }
        }
        return playerWarps;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();

            json.entrySet().forEach(entry -> {
                JsonObject warpJson = entry.getValue().getAsJsonObject();
                Warp warp = new Warp(
                    entry.getKey(),
                    warpJson.get("creator").getAsString(),
                    new Location(
                        null,
                        warpJson.get("x").getAsDouble(),
                        warpJson.get("y").getAsDouble(),
                        warpJson.get("z").getAsDouble(),
                        warpJson.get("yaw").getAsFloat(),
                        warpJson.get("pitch").getAsFloat()
                    )
                );
                warp.world = warpJson.get("world").getAsString();
                warps.put(entry.getKey(), warp);
            });

            plugin.getLogger().info("✓ Loaded warps from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load warp data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();

            JsonObject json = new JsonObject();
            warps.forEach((name, warp) -> {
                JsonObject warpJson = new JsonObject();
                warpJson.addProperty("creator", warp.creator);
                warpJson.addProperty("x", warp.x);
                warpJson.addProperty("y", warp.y);
                warpJson.addProperty("z", warp.z);
                warpJson.addProperty("yaw", warp.yaw);
                warpJson.addProperty("pitch", warp.pitch);
                warpJson.addProperty("world", warp.world);
                json.add(name, warpJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save warp data: " + e.getMessage());
        }
    }
}
