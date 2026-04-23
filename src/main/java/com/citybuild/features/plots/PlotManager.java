package com.citybuild.features.plots;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.citybuild.storage.DataManager;
import com.citybuild.storage.DataManager.BlockData;
import com.citybuild.storage.DataManager.PlotData;
import java.util.*;

/**
 * PlotManager - Manages plot creation, buying, selling, and merging with JSON persistence
 * Supports 100 pre-generated plots
 */
public class PlotManager {
    private final Plugin plugin;
    private final DataManager dataManager;
    private final Map<String, Plot> plots;
    private final Map<String, String> playerPlots; // player UUID -> plot ID
    private final Map<String, Set<String>> mergedPlots; // main plot ID -> merged plot IDs
    private final Queue<String> availablePlots; // Queue of unowned plots
    private final Map<String, List<BlockData>> plotBlocks; // plot ID -> list of blocks built
    
    private static final int TOTAL_PLOTS = 100;
    private static final double PLOT_BASE_PRICE = 1000.0;
    
    // Grid system: 10x10 plots, each 100x100 blocks
    private static final int GRID_SIZE = 10;
    private static final int PLOT_SIZE = 100;
    private static final int PLOT_Y = -60;

    public PlotManager(Plugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.plots = new HashMap<>();
        this.playerPlots = new HashMap<>();
        this.mergedPlots = new HashMap<>();
        this.availablePlots = new LinkedList<>();
        this.plotBlocks = new HashMap<>();
        loadAllData();
    }
    
    /**
     * Initializes 100 pre-generated plots in a 10x10 grid at Y = -60
     * Each plot is 100x100 blocks
     */
    private void initializeDefaultPlots() {
        if (plots.size() >= TOTAL_PLOTS) {
            return; // Plots already initialized
        }
        
        plugin.getLogger().info("🔧 Initializing 100 plots in 10x10 grid at Y = " + PLOT_Y + "...");
        
        int plotIndex = 0;
        for (int gridX = 0; gridX < GRID_SIZE; gridX++) {
            for (int gridZ = 0; gridZ < GRID_SIZE; gridZ++) {
                plotIndex++;
                String plotId = "plot_" + plotIndex;
                
                // Calculate coordinates (each plot is 100x100 blocks)
                int x1 = gridX * PLOT_SIZE;
                int x2 = x1 + PLOT_SIZE - 1;
                int z1 = gridZ * PLOT_SIZE;
                int z2 = z1 + PLOT_SIZE - 1;
                
                // Create unowned plot
                Plot plot = new Plot(plotId, null, PLOT_BASE_PRICE, x1, z1, x2, z2);
                plots.put(plotId, plot);
                plotBlocks.put(plotId, new ArrayList<>()); // Initialize empty block list
                availablePlots.offer(plotId);
            }
        }
        
        plugin.getLogger().info("✓ Created 100 plots (10x10 grid, 100x100 blocks per plot)");
        plugin.getLogger().info("✓ All plots at Y = " + PLOT_Y);
    }

    /**
     * Player buys a plot - assigns first available plot
     */
    public boolean buyPlot(Player player, double maxPrice) {
        String playerUUID = player.getUniqueId().toString();
        
        // Check if player already owns a plot
        if (playerPlots.containsKey(playerUUID)) {
            player.sendMessage("§cYou already own a plot! Sell it first with /plot sell");
            return false;
        }
        
        // Get next available plot
        String plotId = availablePlots.poll();
        if (plotId == null) {
            player.sendMessage("§c❌ All plots are owned! No plots available.");
            return false;
        }
        
        Plot plot = plots.get(plotId);
        if (plot == null || plot.getOwnerUUID() != null) {
            player.sendMessage("§c❌ Plot is not available!");
            return false;
        }
        
        // Update existing plot with new owner
        Plot updatedPlot = new Plot(plotId, player.getUniqueId(), PLOT_BASE_PRICE, 
                                    plot.getX1(), plot.getZ1(), plot.getX2(), plot.getZ2());
        plots.put(plotId, updatedPlot);
        playerPlots.put(playerUUID, plotId);
        
        // Save to JSON
        savePlot(plotId);
        
        player.sendMessage("§a✅ Plot purchased! ID: §e" + plotId);
        player.sendMessage(String.format("§6Location: §e[%d, %d, %d] to [%d, %d, %d]", 
            plot.getX1(), plot.getY(), plot.getZ1(), plot.getX2(), plot.getY(), plot.getZ2()));
        plugin.getLogger().info(player.getName() + " bought " + plotId);
        return true;
    }

    /**
     * Player sells their plot
     */
    public boolean sellPlot(Player player) {
        String playerUUID = player.getUniqueId().toString();
        String plotId = playerPlots.get(playerUUID);
        
        if (plotId == null) {
            player.sendMessage("§cYou don't own a plot!");
            return false;
        }
        
        // Get ownership for refund calculation
        Plot plot = plots.get(plotId);
        double sellPrice = plot.getPrice() * 0.8; // 80% refund
        
        // Reset plot to unowned but keep coordinates
        Plot resetPlot = new Plot(plotId, null, PLOT_BASE_PRICE, 
                                  plot.getX1(), plot.getZ1(), plot.getX2(), plot.getZ2());
        plots.put(plotId, resetPlot);
        playerPlots.remove(playerUUID);
        plotBlocks.put(plotId, new ArrayList<>()); // Clear blocks
        availablePlots.offer(plotId); // Add back to available queue
        
        // Check if this plot was merged into another
        removePlotFromMerge(plotId);
        
        // Save to JSON
        savePlot(plotId);
        
        player.sendMessage("§a✅ Plot sold! Refund: §e$" + String.format("%.2f", sellPrice));
        plugin.getLogger().info(player.getName() + " sold " + plotId);
        return true;
    }
    
    /**
     * Add a block to plot construction data
     */
    public void addBlockToPlot(String plotId, int x, int y, int z, String material) {
        if (!plots.containsKey(plotId)) {
            plugin.getLogger().warning("Plot " + plotId + " not found!");
            return;
        }
        
        List<BlockData> blocks = plotBlocks.computeIfAbsent(plotId, k -> new ArrayList<>());
        blocks.add(new BlockData(x, y, z, material, ""));
        
        // Save immediately
        savePlot(plotId);
    }
    
    /**
     * Remove a block from plot construction data
     */
    public void removeBlockFromPlot(String plotId, int x, int y, int z) {
        List<BlockData> blocks = plotBlocks.get(plotId);
        if (blocks != null) {
            blocks.removeIf(b -> b.x == x && b.y == y && b.z == z);
            savePlot(plotId);
        }
    }
    
    /**
     * Get all blocks on a plot
     */
    public List<BlockData> getPlotBlocks(String plotId) {
        return plotBlocks.getOrDefault(plotId, new ArrayList<>());
    }
    
    /**
     * Admin merges two plots together (plots become one)
     */
    public boolean mergePlots(Player admin, String mainPlotId, String secondaryPlotId) {
        if (!admin.isOp()) {
            admin.sendMessage("§c❌ Only admins can merge plots!");
            return false;
        }
        
        Plot mainPlot = plots.get(mainPlotId);
        Plot secondaryPlot = plots.get(secondaryPlotId);
        
        if (mainPlot == null || secondaryPlot == null) {
            admin.sendMessage("§c❌ One or both plots don't exist!");
            return false;
        }
        
        if (mainPlot.getOwnerUUID() == null || !mainPlot.getOwnerUUID().equals(secondaryPlot.getOwnerUUID())) {
            admin.sendMessage("§c❌ Both plots must have the same owner!");
            return false;
        }
        
        // Register merge
        Set<String> merged = mergedPlots.computeIfAbsent(mainPlotId, k -> new HashSet<>());
        merged.add(secondaryPlotId);
        
        // Merge blocks
        List<BlockData> mainBlocks = plotBlocks.computeIfAbsent(mainPlotId, k -> new ArrayList<>());
        List<BlockData> secondaryBlocks = plotBlocks.getOrDefault(secondaryPlotId, new ArrayList<>());
        mainBlocks.addAll(secondaryBlocks);
        
        // Save both plots
        savePlot(mainPlotId);
        savePlot(secondaryPlotId);
        
        admin.sendMessage("§a✅ Plots merged!");
        admin.sendMessage("§e" + mainPlotId + " §7+ §e" + secondaryPlotId);
        
        plugin.getLogger().info("Admin " + admin.getName() + " merged " + mainPlotId + " + " + secondaryPlotId);
        return true;
    }
    
    /**
     * Admin unmerges plots
     */
    public boolean unmergePlots(Player admin, String mainPlotId, String secondaryPlotId) {
        if (!admin.isOp()) {
            admin.sendMessage("§c❌ Only admins can unmerge plots!");
            return false;
        }
        
        Set<String> merged = mergedPlots.get(mainPlotId);
        if (merged == null || !merged.contains(secondaryPlotId)) {
            admin.sendMessage("§c❌ These plots are not merged!");
            return false;
        }
        
        merged.remove(secondaryPlotId);
        if (merged.isEmpty()) {
            mergedPlots.remove(mainPlotId);
        }
        
        // Save both plots
        savePlot(mainPlotId);
        savePlot(secondaryPlotId);
        
        admin.sendMessage("§a✅ Plots unmerged!");
        plugin.getLogger().info("Admin " + admin.getName() + " unmerged " + mainPlotId + " + " + secondaryPlotId);
        return true;
    }
    
    /**
     * Removes a plot from any merge group it belongs to
     */
    private void removePlotFromMerge(String plotId) {
        mergedPlots.values().forEach(set -> set.remove(plotId));
        mergedPlots.remove(plotId);
    }

    /**
     * Gets player's plot
     */
    public Plot getPlayerPlot(Player player) {
        String plotId = playerPlots.get(player.getUniqueId().toString());
        return plotId != null ? plots.get(plotId) : null;
    }
    
    /**
     * Gets player's plot including merged plots
     */
    public Set<String> getPlayerPlots(Player player) {
        String mainPlotId = playerPlots.get(player.getUniqueId().toString());
        if (mainPlotId == null) {
            return new HashSet<>();
        }
        
        Set<String> result = new HashSet<>();
        result.add(mainPlotId);
        result.addAll(mergedPlots.getOrDefault(mainPlotId, new HashSet<>()));
        return result;
    }

    /**
     * Gets a specific plot
     */
    public Plot getPlot(String plotId) {
        return plots.get(plotId);
    }

    /**
     * Gets total plots (owned + available)
     */
    public int getTotalPlots() {
        return TOTAL_PLOTS;
    }
    
    /**
     * Gets available plots count
     */
    public int getAvailablePlots() {
        return availablePlots.size();
    }

    /**
     * Lists all plots
     */
    public List<String> listAllPlots() {
        return new ArrayList<>(plots.keySet());
    }
    
    /**
     * Gets plot statistics
     */
    public String getStats() {
        int owned = TOTAL_PLOTS - availablePlots.size();
        return String.format("§6Plots: §a%d§6/§a%d owned §7(§e%d available§7)", 
            owned, TOTAL_PLOTS, availablePlots.size());
    }

    /**
     * Save single plot to JSON
     */
    private void savePlot(String plotId) {
        Plot plot = plots.get(plotId);
        if (plot == null) return;
        
        List<BlockData> blocks = plotBlocks.getOrDefault(plotId, new ArrayList<>());
        dataManager.savePlot(plotId, plot.getOwnerUUID() != null ? plot.getOwnerUUID().toString() : null, 
                            plot.getPrice(), plot.getX1(), plot.getZ1(), plot.getX2(), plot.getZ2(), blocks);
    }

    public void saveAllData() {
        for (String plotId : plots.keySet()) {
            savePlot(plotId);
        }
        plugin.getLogger().info("✓ Saved " + plots.size() + " plots to JSON");
    }

    public void loadAllData() {
        // Try to load from JSON first
        List<PlotData> savedPlots = dataManager.loadAllPlots();
        
        if (!savedPlots.isEmpty()) {
            plugin.getLogger().info("✓ Loaded " + savedPlots.size() + " plots from JSON");
            for (PlotData data : savedPlots) {
                Plot plot = new Plot(data.plotId, 
                    data.ownerUUID != null ? java.util.UUID.fromString(data.ownerUUID) : null,
                    data.price, data.x1, data.z1, data.x2, data.z2);
                plots.put(data.plotId, plot);
                plotBlocks.put(data.plotId, data.constructedBlocks);
                
                // Update player plots and available queue
                if (data.ownerUUID != null) {
                    playerPlots.put(data.ownerUUID, data.plotId);
                } else {
                    availablePlots.offer(data.plotId);
                }
            }
        } else {
            // Initialize default plots if no saved data
            initializeDefaultPlots();
        }
    }
}
