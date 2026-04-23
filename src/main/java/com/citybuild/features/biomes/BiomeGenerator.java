package com.citybuild.features.biomes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Custom biome generator
 */
public class BiomeGenerator {

    public enum CustomBiome {
        MYSTICAL("Mystical Forest", Material.PURPLE_CONCRETE, Material.OAK_LOG),
        LAVA_LAND("Lava Land", Material.RED_CONCRETE, Material.NETHERRACK),
        ICE_KINGDOM("Ice Kingdom", Material.BLUE_CONCRETE, Material.ICE),
        JUNGLE_TEMPLE("Jungle Temple", Material.GREEN_CONCRETE, Material.JUNGLE_LOG),
        CRYSTAL_CAVE("Crystal Cave", Material.LIGHT_BLUE_CONCRETE, Material.AMETHYST_BLOCK);

        private final String display;
        private final Material base;
        private final Material accent;

        CustomBiome(String display, Material base, Material accent) {
            this.display = display;
            this.base = base;
            this.accent = accent;
        }

        public String getDisplay() { return display; }
        public Material getBase() { return base; }
        public Material getAccent() { return accent; }
    }

    private final JavaPlugin plugin;
    private final Random random;

    public BiomeGenerator(JavaPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    /**
     * Generate custom biome
     */
    public void generateBiome(Location center, int radius, CustomBiome biome) {
        World world = center.getWorld();
        if (world == null) return;

        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                int distance = Math.max(Math.abs(x - centerX), Math.abs(z - centerZ));

                if (distance <= radius) {
                    int y = world.getHighestBlockYAt(x, z);

                    // Base layer
                    world.getBlockAt(x, y, z).setType(biome.getBase());

                    // Random accent blocks
                    if (random.nextDouble() < 0.15) {
                        world.getBlockAt(x, y + 1, z).setType(biome.getAccent());
                    }

                    // Underground structure
                    if (y > 64) {
                        world.getBlockAt(x, y - 1, z).setType(Material.DIRT);
                        world.getBlockAt(x, y - 2, z).setType(Material.STONE);
                    }
                }
            }
        }

        plugin.getLogger().info("§6✓ Biome generated: " + biome.getDisplay() + " at " + centerX + ", " + centerZ);
    }

    /**
     * Create biome structure
     */
    public void createStructure(Location center, CustomBiome biome, int width, int height) {
        World world = center.getWorld();
        if (world == null) return;

        int startX = center.getBlockX();
        int startY = center.getBlockY();
        int startZ = center.getBlockZ();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < width; z++) {
                    Block block = world.getBlockAt(startX + x, startY + y, startZ + z);

                    // Border
                    if (x == 0 || x == width - 1 || z == 0 || z == width - 1) {
                        block.setType(biome.getAccent());
                    }
                    // Fill
                    else if (y == 0 || y == height - 1) {
                        block.setType(biome.getBase());
                    }
                    // Interior
                    else {
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        plugin.getLogger().info("§6✓ Structure created: " + width + "x" + height);
    }

    /**
     * Get all biomes
     */
    public Collection<CustomBiome> getAllBiomes() {
        return Arrays.asList(CustomBiome.values());
    }

    /**
     * Get biome info
     */
    public String getBiomeInfo(CustomBiome biome) {
        return "§6" + biome.getDisplay() + "\n" +
               "§7Base: §e" + biome.getBase() + "\n" +
               "§7Accent: §e" + biome.getAccent();
    }
}
