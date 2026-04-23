package com.citybuild.utils;

import com.citybuild.model.PlotData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

/**
 * Generates and manages plot terrain, borders, and structures
 */
public class PlotGenerator {
    private static final Material PLOT_BASE = Material.DIRT;
    private static final Material PLOT_GRASS = Material.GRASS_BLOCK;
    private static final Material BORDER_MATERIAL = Material.OAK_FENCE;
    private static final Material CORNER_MATERIAL = Material.OAK_FENCE;
    private static final int PLOT_HEIGHT = -60;
    private static final int BORDER_HEIGHT = -59;

    /**
     * Generate complete plot terrain and border
     */
    public static void generatePlot(World world, PlotData plot) {
        int cornerX = plot.getCornerX();
        int cornerZ = plot.getCornerZ();
        int sizeX = plot.getSizeX();
        int sizeZ = plot.getSizeZ();

        // Generate floor (dirt + grass on top)
        for (int x = cornerX; x < cornerX + sizeX; x++) {
            for (int z = cornerZ; z < cornerZ + sizeZ; z++) {
                // Clear from Y=-63 to Y=-60
                for (int y = -63; y <= -60; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (y < -60) {
                        block.setType(PLOT_BASE);
                    } else {
                        block.setType(PLOT_GRASS);
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
            sign.line(0, Component.text("Plot #" + plot.getPlotId(), NamedTextColor.AQUA, net.kyori.adventure.text.format.TextDecoration.BOLD));
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

        // Clear all blocks inside plot
        for (int x = cornerX; x < cornerX + sizeX; x++) {
            for (int z = cornerZ; z < cornerZ + sizeZ; z++) {
                for (int y = -59; y <= 255; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(Material.AIR);
                }
                // Reset floor
                Block grass = world.getBlockAt(x, -60, z);
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
