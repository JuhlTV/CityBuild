package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.citybuild.model.PlotData;
import com.citybuild.utils.PlotGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlotManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, List<Integer>> playerPlots;
    private final Map<Integer, List<String>> plotMembers; // Plot ID -> List of member UUIDs
    private final Map<Integer, PlotData> plots; // PlotID -> PlotData (new system)
    private final int plotBuyPrice;
    private final int plotSellPrice;
    private int nextPlotId = 1;
    
    // Plot Spawning System
    private final int PLOT_SIZE = 16; // 16x16 blocks per plot
    private final int PLOT_SPACING = 2; // 2 blocks spacing between plots
    private World plotWorld;

    public PlotManager(JavaPlugin plugin, World plotWorld) {
        this.plugin = plugin;
        this.plotWorld = plotWorld;
        this.dataFile = new File(plugin.getDataFolder(), "data/plots.json");
        this.playerPlots = new HashMap<>();
        this.plotMembers = new HashMap<>();
        this.plots = new HashMap<>();
        this.plotBuyPrice = plugin.getConfig().getInt("economy.plot_buy_price", 5000);
        this.plotSellPrice = plugin.getConfig().getInt("economy.plot_sell_price", 4000);
        
        dataFile.getParentFile().mkdirs();
        loadData();
    }

    public void addPlot(String playerUuid) {
        playerPlots.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(nextPlotId++);
        saveData();
    }

    public void removePlot(String playerUuid) {
        List<Integer> plots = playerPlots.get(playerUuid);
        if (plots != null && !plots.isEmpty()) {
            plots.remove(0);
            if (plots.isEmpty()) {
                playerPlots.remove(playerUuid);
            }
            saveData();
        }
    }

    public int getPlotCount(String playerUuid) {
        return playerPlots.getOrDefault(playerUuid, new ArrayList<>()).size();
    }

    public boolean hasPlots(String playerUuid) {
        return getPlotCount(playerUuid) > 0;
    }

    public List<Integer> getPlayerPlots(String playerUuid) {
        return new ArrayList<>(playerPlots.getOrDefault(playerUuid, new ArrayList<>()));
    }

    public int getPlotBuyPrice() {
        return plotBuyPrice;
    }

    public int getPlotSellPrice() {
        return plotSellPrice;
    }

    public int getTotalPlots() {
        return playerPlots.values().stream().mapToInt(List::size).sum();
    }
    
    /**
     * Calculate plot spawn location based on plot ID
     * Uses grid system: 16x16 blocks per plot with 2 block spacing
     */
    public Location getPlotSpawn(int plotId) {
        int index = plotId - 1;
        int plotsPerRow = 10; // 10 plots per row
        
        int row = index / plotsPerRow;
        int col = index % plotsPerRow;
        
        int plotDistance = PLOT_SIZE + PLOT_SPACING;
        int x = col * plotDistance;
        int z = row * plotDistance;
        
        return new Location(plotWorld, x + 8, -60, z + 8); // Center of plot at Y=-60
    }
    
    /**
     * Get all plot spawn locations for a player
     */
    public List<Location> getPlayerPlotLocations(String playerUuid) {
        List<Integer> plots = getPlayerPlots(playerUuid);
        List<Location> locations = new ArrayList<>();
        
        for (Integer plotId : plots) {
            locations.add(getPlotSpawn(plotId));
        }
        
        return locations;
    }
    
    /**
     * Teleport player to first plot location
     */
    public Location getFirstPlotLocation(String playerUuid) {
        List<Integer> plots = getPlayerPlots(playerUuid);
        if (plots.isEmpty()) {
            return null;
        }
        return getPlotSpawn(plots.get(0));
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }
            
            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            
            // Load player plots
            if (json.has("players")) {
                JsonObject playersJson = json.getAsJsonObject("players");
                playersJson.entrySet().forEach(entry -> {
                    List<Integer> plotIds = new ArrayList<>();
                    JsonArray array = entry.getValue().getAsJsonArray();
                    array.forEach(el -> {
                        int id = el.getAsInt();
                        plotIds.add(id);
                        nextPlotId = Math.max(nextPlotId, id + 1);
                    });
                    playerPlots.put(entry.getKey(), plotIds);
                });
            }
            
            // Load plot members
            if (json.has("members")) {
                JsonObject membersJson = json.getAsJsonObject("members");
                membersJson.entrySet().forEach(entry -> {
                    List<String> members = new ArrayList<>();
                    JsonArray array = entry.getValue().getAsJsonArray();
                    array.forEach(el -> members.add(el.getAsString()));
                    plotMembers.put(Integer.parseInt(entry.getKey()), members);
                });
            }

            // Load PlotData objects
            if (json.has("plots")) {
                JsonObject plotsJson = json.getAsJsonObject("plots");
                plotsJson.entrySet().forEach(entry -> {
                    try {
                        int plotId = Integer.parseInt(entry.getKey());
                        JsonObject plotObj = entry.getValue().getAsJsonObject();

                        PlotData plot = new PlotData(
                                plotId,
                                plotObj.get("ownerUuid").getAsString(),
                                plotObj.get("cornerX").getAsInt(),
                                plotObj.get("cornerZ").getAsInt()
                        );

                        plot.setSizeX(plotObj.get("sizeX").getAsInt());
                        plot.setSizeZ(plotObj.get("sizeZ").getAsInt());
                        plot.setPremium(plotObj.get("isPremium").getAsBoolean());
                        if (plotObj.has("biome")) {
                            plot.setBiome(plotObj.get("biome").getAsString());
                        }

                        // Load members
                        if (plotObj.has("members")) {
                            JsonArray membersArray = plotObj.getAsJsonArray("members");
                            membersArray.forEach(el -> plot.addMember(el.getAsString()));
                        }

                        plots.put(plotId, plot);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to load plot " + entry.getKey() + ": " + e.getMessage());
                    }
                });
            }
            
            plugin.getLogger().info("✓ Loaded " + plots.size() + " plots from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load plot data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            
            // Save player plots
            JsonObject playersJson = new JsonObject();
            playerPlots.forEach((uuid, plotIds) -> {
                playersJson.add(uuid, gson.toJsonTree(plotIds));
            });
            json.add("players", playersJson);
            
            // Save plot members
            JsonObject membersJson = new JsonObject();
            plotMembers.forEach((plotId, members) -> {
                membersJson.add(String.valueOf(plotId), gson.toJsonTree(members));
            });
            json.add("members", membersJson);

            // Save PlotData objects
            JsonObject plotsJson = new JsonObject();
            plots.forEach((plotId, plot) -> {
                JsonObject plotObj = new JsonObject();
                plotObj.addProperty("plotId", plot.getPlotId());
                plotObj.addProperty("ownerUuid", plot.getOwnerUuid());
                plotObj.addProperty("sizeX", plot.getSizeX());
                plotObj.addProperty("sizeZ", plot.getSizeZ());
                plotObj.addProperty("cornerX", plot.getCornerX());
                plotObj.addProperty("cornerZ", plot.getCornerZ());
                plotObj.addProperty("isPremium", plot.isPremium());
                plotObj.addProperty("biome", plot.getBiome());
                plotObj.addProperty("createdAt", plot.getCreatedAt());

                // Save members as array
                JsonArray membersArray = new JsonArray();
                for (String member : plot.getMembers()) {
                    membersArray.add(member);
                }
                plotObj.add("members", membersArray);

                plotsJson.add(String.valueOf(plotId), plotObj);
            });
            json.add("plots", plotsJson);
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save plot data: " + e.getMessage());
        }
    }

    /**
     * Create a 24x24 frame of stairs around a plot (16x16 plot size)
     * Stairs are placed at Y=65 (same as plot spawn level)
     */
    public void createPlotFrame(String playerUuid) {
        List<Integer> playerPlotList = getPlayerPlots(playerUuid);
        if (playerPlotList.isEmpty()) {
            return;
        }
        
        int plotId = playerPlotList.get(0); // Get first plot
        Location plotCenter = getPlotSpawn(plotId);
        
        int centerX = plotCenter.getBlockX();
        int centerZ = plotCenter.getBlockZ();
        int y = 65;
        
        // Calculate plot boundaries (center is at +8, +8, so actual plot is from center-8 to center+8)
        int plotStartX = centerX - 8;
        int plotEndX = centerX + 8;
        int plotStartZ = centerZ - 8;
        int plotEndZ = centerZ + 8;
        
        // Frame goes from -1 to +1 around the plot
        int frameStartX = plotStartX - 1;
        int frameEndX = plotEndX + 1;
        int frameStartZ = plotStartZ - 1;
        int frameEndZ = plotEndZ + 1;
        
        // Clear old frame if exists
        clearPlotFrame(frameStartX, frameEndX, frameStartZ, frameEndZ, y);
        
        // Create north wall (z = frameStartZ)
        for (int x = frameStartX; x <= frameEndX; x++) {
            Block block = plotWorld.getBlockAt(x, y, frameStartZ);
            block.setType(Material.DARK_OAK_STAIRS);
            if (block.getBlockData() instanceof Directional directional) {
                directional.setFacing(BlockFace.SOUTH);
                block.setBlockData(directional);
            }
        }
        
        // Create south wall (z = frameEndZ)
        for (int x = frameStartX; x <= frameEndX; x++) {
            Block block = plotWorld.getBlockAt(x, y, frameEndZ);
            block.setType(Material.DARK_OAK_STAIRS);
            if (block.getBlockData() instanceof Directional directional) {
                directional.setFacing(BlockFace.NORTH);
                block.setBlockData(directional);
            }
        }
        
        // Create west wall (x = frameStartX)
        for (int z = frameStartZ + 1; z < frameEndZ; z++) {
            Block block = plotWorld.getBlockAt(frameStartX, y, z);
            block.setType(Material.DARK_OAK_STAIRS);
            if (block.getBlockData() instanceof Directional directional) {
                directional.setFacing(BlockFace.EAST);
                block.setBlockData(directional);
            }
        }
        
        // Create east wall (x = frameEndX)
        for (int z = frameStartZ + 1; z < frameEndZ; z++) {
            Block block = plotWorld.getBlockAt(frameEndX, y, z);
            block.setType(Material.DARK_OAK_STAIRS);
            if (block.getBlockData() instanceof Directional directional) {
                directional.setFacing(BlockFace.WEST);
                block.setBlockData(directional);
            }
        }
        
        // Place owner sign on north wall
        placeOwnerSign(playerUuid, plotStartX, y, frameStartZ);
    }

    /**
     * Place a sign on the plot frame with the owner's name
     */
    private void placeOwnerSign(String playerUuid, int x, int y, int z) {
        try {
            // Get player name from UUID (cached by Bukkit)
            org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.fromString(playerUuid));
            String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : playerUuid.substring(0, 8);
            
            Block signBlock = plotWorld.getBlockAt(x, y + 1, z);
            signBlock.setType(Material.OAK_WALL_SIGN);
            
            Sign sign = (Sign) signBlock.getState();
            sign.line(0, Component.text("=== PLOT ===" , NamedTextColor.DARK_GREEN));
            sign.line(1, Component.text("Owner:", NamedTextColor.GREEN));
            sign.line(2, Component.text(playerName, NamedTextColor.AQUA));
            sign.line(3, Component.text("ID: " + getPlayerPlots(playerUuid).get(0), NamedTextColor.GRAY));
            sign.update();
            
            // Set wall sign direction (facing outward from plot)
            if (signBlock.getBlockData() instanceof Directional directional) {
                directional.setFacing(BlockFace.SOUTH);
                signBlock.setBlockData(directional);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to place owner sign: " + e.getMessage());
        }
    }

    /**
     * Clear the old plot frame
     */
    private void clearPlotFrame(int startX, int endX, int startZ, int endZ, int y) {
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                Block block = plotWorld.getBlockAt(x, y, z);
                if (block.getType().toString().contains("STAIRS") || 
                    block.getType().toString().contains("SIGN")) {
                    block.setType(Material.AIR);
                }
                
                // Clear sign above frame
                Block signBlock = plotWorld.getBlockAt(x, y + 1, z);
                if (signBlock.getType().toString().contains("SIGN")) {
                    signBlock.setType(Material.AIR);
                }
            }
        }
    }

    // ===== PLOT PROTECTION SYSTEM =====

    /**
     * Check if a player owns a specific plot
     */
    public boolean isPlotOwner(String playerUuid, int plotId) {
        List<Integer> plots = playerPlots.getOrDefault(playerUuid, new ArrayList<>());
        return plots.contains(plotId);
    }

    /**
     * Check if a player is a member of a plot (owner or invited)
     */
    public boolean isPlotMember(String playerUuid, int plotId) {
        // Owner check
        if (isPlotOwner(playerUuid, plotId)) {
            return true;
        }
        
        // Member check
        List<String> members = plotMembers.getOrDefault(plotId, new ArrayList<>());
        return members.contains(playerUuid);
    }

    /**
     * Add a member to a plot
     */
    public void addMember(int plotId, String memberUuid) {
        plotMembers.computeIfAbsent(plotId, k -> new ArrayList<>()).add(memberUuid);
        saveData();
    }

    /**
     * Remove a member from a plot
     */
    public void removeMember(int plotId, String memberUuid) {
        List<String> members = plotMembers.get(plotId);
        if (members != null) {
            members.remove(memberUuid);
            if (members.isEmpty()) {
                plotMembers.remove(plotId);
            }
            saveData();
        }
    }

    /**
     * Get all members of a plot
     */
    public List<String> getPlotMembers(int plotId) {
        return new ArrayList<>(plotMembers.getOrDefault(plotId, new ArrayList<>()));
    }

    // ===== NEW PLOTDATA SYSTEM =====

    /**
     * Get next plot ID for new plot creation
     */
    public int getNextPlotId() {
        return nextPlotId;
    }

    /**
     * Get a specific plot by ID
     */
    public PlotData getPlot(int plotId) {
        return plots.get(plotId);
    }

    /**
     * Get first plot of a player
     */
    public PlotData getFirstPlot(String playerUuid) {
        List<Integer> playerPlotIds = playerPlots.getOrDefault(playerUuid, new ArrayList<>());
        if (playerPlotIds.isEmpty()) return null;
        return plots.get(playerPlotIds.get(0));
    }

    /**
     * Get all plots of a player
     */
    public List<PlotData> getPlayerPlotData(String playerUuid) {
        List<PlotData> result = new ArrayList<>();
        List<Integer> plotIds = playerPlots.getOrDefault(playerUuid, new ArrayList<>());
        for (Integer plotId : plotIds) {
            PlotData plot = plots.get(plotId);
            if (plot != null) {
                result.add(plot);
            }
        }
        return result;
    }

    /**
     * Save or update a plot
     */
    public void savePlot(PlotData plot) {
        plots.put(plot.getPlotId(), plot);
        saveData();
    }

    /**
     * Remove a plot by ID
     */
    public void removePlot(String playerUuid, int plotId) {
        List<Integer> playerPlotIds = playerPlots.get(playerUuid);
        if (playerPlotIds != null) {
            playerPlotIds.remove(Integer.valueOf(plotId));
            if (playerPlotIds.isEmpty()) {
                playerPlots.remove(playerUuid);
            }
        }
        plots.remove(plotId);
        plotMembers.remove(plotId);
        saveData();
    }

    /**
     * Check if player is plot owner
     */
    public boolean isPlotOwner(String playerUuid, PlotData plot) {
        return plot.isOwner(playerUuid);
    }

    /**
     * Add member to a plot
     */
    public void addPlotMember(PlotData plot, String memberUuid) {
        plot.addMember(memberUuid);
        savePlot(plot);
    }

    /**
     * Remove member from a plot
     */
    public void removePlotMember(PlotData plot, String memberUuid) {
        plot.removeMember(memberUuid);
        savePlot(plot);
    }
}
