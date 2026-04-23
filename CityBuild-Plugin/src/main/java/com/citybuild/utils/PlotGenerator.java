package com.citybuild.utils;

import com.citybuild.model.PlotData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Generates and manages plot terrain, borders, and structures
 */
public class PlotGenerator {
    private static final Material PLOT_BASE = Material.DIRT;
    private static final Material PLOT_GRASS = Material.GRASS_BLOCK;
    private static final Material BORDER_MATERIAL = Material.OAK_FENCE;
    private static final Material CORNER_MATERIAL = Material.OAK_FENCE;
    
    // Configurable height levels (initialized from config.yml)
    private static int PLOT_HEIGHT = -60;      // Default: Y=-60
    private static int BORDER_HEIGHT = -59;    // Default: Y=-59

    /**
     * Initialize terrain heights from plugin configuration
     * Called by PlotManager during setup
     */
    public static void initialize(JavaPlugin plugin) {
        PLOT_HEIGHT = plugin.getConfig().getInt("terrain.generation_height", -60);
        BORDER_HEIGHT = plugin.getConfig().getInt("terrain.border_height", -59);
        plugin.getLogger().info("✓ Plot heights configured: surface=" + PLOT_HEIGHT + ", border=" + BORDER_HEIGHT);
    }

    /**
     * Get configured plot generation height
     */
    public static int getPlotHeight() {
        return PLOT_HEIGHT;
    }

    /**
     * Get configured border height
     */
    public static int getBorderHeight() {
        return BORDER_HEIGHT;
    }

    /**
     * Generate complete plot terrain and border
     */
    public static void generatePlot(World world, PlotData plot) {
        int cornerX = plot.getCornerX();
        int cornerZ = plot.getCornerZ();
        int sizeX = plot.getSizeX();
        int sizeZ = plot.getSizeZ();

        // Generate floor (dirt underlay + grass surface)
        for (int x = cornerX; x < cornerX + sizeX; x++) {
            for (int z = cornerZ; z < cornerZ + sizeZ; z++) {
                // Generate 3-block dirt underlay + 1-block grass surface
                for (int y = PLOT_HEIGHT - 3; y <= PLOT_HEIGHT; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (y == PLOT_HEIGHT) {
                        block.setType(PLOT_GRASS);  // Surface
                    } else {
                        block.setType(PLOT_BASE);   // Underlay
                    }
                }
            }
        }

        // Generate border (fences around plot)
        generateBorder(world, plot);

        // Set spawn platform (3x3 at center with signs)
        generateSpawnPlatform(world, plot);
    }

    /**
     * Generate border fence around plot
     */
    private static void generateBorder(World world, PlotData plot) {
        int cornerX = plot.getCornerX();
        int cornerZ = plot.getCornerZ();
        int sizeX = plot.getSizeX();
        int sizeZ = plot.getSizeZ();

        // North and South borders
        for (int x = cornerX - 1; x <= cornerX + sizeX; x++) {
            // South border (z - 1)
            Block southBorder = world.getBlockAt(x, BORDER_HEIGHT, cornerZ - 1);
            southBorder.setType(BORDER_MATERIAL);

            // North border (z + sizeZ)
            Block northBorder = world.getBlockAt(x, BORDER_HEIGHT, cornerZ + sizeZ);
            northBorder.setType(BORDER_MATERIAL);
        }

        // East and West borders
        for (int z = cornerZ - 1; z <= cornerZ + sizeZ; z++) {
            // West border (x - 1)
            Block westBorder = world.getBlockAt(cornerX - 1, BORDER_HEIGHT, z);
            westBorder.setType(BORDER_MATERIAL);

            // East border (x + sizeX)
            Block eastBorder = world.getBlockAt(cornerX + sizeX, BORDER_HEIGHT, z);
            eastBorder.setType(BORDER_MATERIAL);
        }

        // Corner markers
        placeCornerMarker(world, cornerX - 1, cornerZ - 1);
        placeCornerMarker(world, cornerX + sizeX, cornerZ - 1);
        placeCornerMarker(world, cornerX - 1, cornerZ + sizeZ);
        placeCornerMarker(world, cornerX + sizeX, cornerZ + sizeZ);
    }

    /**
     * Place corner marker (double height)
     */
    private static void placeCornerMarker(World world, int x, int z) {
        Block block1 = world.getBlockAt(x, BORDER_HEIGHT, z);
        Block block2 = world.getBlockAt(x, BORDER_HEIGHT + 1, z);
        block1.setType(CORNER_MATERIAL);
        block2.setType(CORNER_MATERIAL);
    }

    /**
     * Generate spawn platform (3x3) with information signs
     */
    private static void generateSpawnPlatform(World world, PlotData plot) {
        int centerX = plot.getCenterX() - 1; // -1 to center the 3x3
        int centerZ = plot.getCenterZ() - 1;
        int y = PLOT_HEIGHT + 1; // Y = -59

        // Create 3x3 platform
        for (int x = centerX; x < centerX + 3; x++) {
            for (int z = centerZ; z < centerZ + 3; z++) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(Material.OAK_PLANKS);
            }
        }

        // Place information sign
        Block signBlock = world.getBlockAt(centerX + 1, y + 1, centerZ + 1);
        signBlock.setType(Material.OAK_SIGN);
        
        if (signBlock.getState() instanceof Sign) {
            Sign sign = (Sign) signBlock.getState();
            sign.line(0, Component.text("Plot #" + plot.getPlotId(), NamedTextColor.AQUA, TextDecoration.BOLD));
            sign.line(1, Component.text("Owner: " + (plot.getOwnerUuid().length() > 8 ? plot.getOwnerUuid().substring(0, 8) + "..." : plot.getOwnerUuid()), NamedTextColor.GREEN));
            sign.line(2, Component.text(plot.getSizeX() + "x" + plot.getSizeZ() + " blocks", NamedTextColor.YELLOW));
            sign.line(3, Component.text("Area: " + plot.getArea() + "m²", NamedTextColor.GRAY));
            sign.update();
        }
    }

    /**
     * Expand plot in a direction
     */
    public static void expandPlot(World world, PlotData plot, String direction, int blocks) {
        switch (direction.toLowerCase()) {
            case "north":
                plot.setCornerZ(plot.getCornerZ() - blocks);
                plot.setSizeZ(plot.getSizeZ() + blocks);
                break;
            case "south":
                plot.setSizeZ(plot.getSizeZ() + blocks);
                break;
            case "east":
                plot.setSizeX(plot.getSizeX() + blocks);
                break;
            case "west":
                plot.setCornerX(plot.getCornerX() - blocks);
                plot.setSizeX(plot.getSizeX() + blocks);
                break;
            default:
                return;
        }

        // Regenerate the plot with new size
        generatePlot(world, plot);
    }

    /**
     * Clear plot terrain (restore to grass)
     */
    public static void clearPlot(World world, PlotData plot) {
        int cornerX = plot.getCornerX();
        int cornerZ = plot.getCornerZ();
        int sizeX = plot.getSizeX();
        int sizeZ = plot.getSizeZ();

        // Clear all blocks above surface (and borders)
        for (int x = cornerX; x < cornerX + sizeX; x++) {
            for (int z = cornerZ; z < cornerZ + sizeZ; z++) {
                // Clear from border height to sky
                for (int y = BORDER_HEIGHT; y <= 255; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(Material.AIR);
                }
                // Reset floor to grass
                Block grass = world.getBlockAt(x, PLOT_HEIGHT, z);
                grass.setType(PLOT_GRASS);
            }
        }
    }

    /**
     * Delete entire plot (terrain + border)
     */
    public static void deletePlot(World world, PlotData plot) {
        int cornerX = plot.getCornerX();
        int cornerZ = plot.getCornerZ();
        int sizeX = plot.getSizeX();
        int sizeZ = plot.getSizeZ();

        // Clear plot area
        clearPlot(world, plot);

        // Clear borders
        for (int x = cornerX - 1; x <= cornerX + sizeX; x++) {
            for (int z = cornerZ - 1; z <= cornerZ + sizeZ; z++) {
                Block border = world.getBlockAt(x, BORDER_HEIGHT, z);
                border.setType(Material.AIR);
                Block borderTop = world.getBlockAt(x, BORDER_HEIGHT + 1, z);
                borderTop.setType(Material.AIR);
            }
        }
    }

    /**
     * Get spawn location for plot (center on platform)
     */
    public static Location getPlotSpawn(World world, PlotData plot) {
        return new Location(world, plot.getCenterX() + 0.5, PLOT_HEIGHT + 2, plot.getCenterZ() + 0.5, 0, 0);
    }
}
