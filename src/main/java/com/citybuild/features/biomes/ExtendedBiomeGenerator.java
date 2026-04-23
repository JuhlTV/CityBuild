package com.citybuild.features.biomes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Extended Biome Generator with 12+ custom biomes and structures
 */
public class ExtendedBiomeGenerator {
    
    private final JavaPlugin plugin;
    
    public enum CustomBiome {
        MYSTICAL("Mystical Forest", Material.PURPLE_CONCRETE, Material.OAK_LOG, "Enchanted trees and purple grass"),
        LAVA_LAND("Lava Land", Material.RED_CONCRETE, Material.NETHERRACK, "Volcanic terrain with lava flows"),
        ICE_KINGDOM("Ice Kingdom", Material.LIGHT_BLUE_CONCRETE, Material.ICE, "Frozen landscape with ice spikes"),
        JUNGLE_TEMPLE("Jungle Temple", Material.GREEN_CONCRETE, Material.JUNGLE_LOG, "Dense jungle with ruins"),
        CRYSTAL_CAVE("Crystal Cave", Material.CYAN_CONCRETE, Material.AMETHYST_BLOCK, "Glowing crystal formations"),
        DESERT_OASIS("Desert Oasis", Material.YELLOW_CONCRETE, Material.SAND, "Sandy dunes with water springs"),
        DARK_FOREST("Dark Forest", Material.GRAY_CONCRETE, Material.SPRUCE_LOG, "Dark mysterious trees"),
        CORAL_REEF("Coral Reef", Material.BLUE_CONCRETE, Material.BRAIN_CORAL, "Underwater coral gardens"),
        MUSHROOM_LANDS("Mushroom Lands", Material.MAGENTA_CONCRETE, Material.BROWN_MUSHROOM_BLOCK, "Giant mushroom farms"),
        SKY_ISLANDS("Sky Islands", Material.WHITE_CONCRETE, Material.END_STONE, "Floating islands in sky"),
        NETHER_FORTRESS("Nether Fortress", Material.DARK_RED_CONCRETE, Material.CRIMSON_NETHER_BRICK, "Hell-like fortress"),
        END_DIMENSION("End Dimension", Material.PURPLE_CONCRETE, Material.END_STONE_BRICKS, "End city replica");
        
        public final String displayName;
        public final Material primaryBlock;
        public final Material secondaryBlock;
        public final String description;
        
        CustomBiome(String displayName, Material primaryBlock, Material secondaryBlock, String description) {
            this.displayName = displayName;
            this.primaryBlock = primaryBlock;
            this.secondaryBlock = secondaryBlock;
            this.description = description;
        }
    }
    
    public ExtendedBiomeGenerator(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Generate custom biome at location
     */
    public void generateBiome(Location center, int radius, CustomBiome biome) {
        World world = center.getWorld();
        if (world == null) return;
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            switch (biome) {
                case MYSTICAL:
                    generateMysticalForest(world, center, radius);
                    break;
                case LAVA_LAND:
                    generateLavaLand(world, center, radius);
                    break;
                case ICE_KINGDOM:
                    generateIceKingdom(world, center, radius);
                    break;
                case JUNGLE_TEMPLE:
                    generateJungleTemple(world, center, radius);
                    break;
                case CRYSTAL_CAVE:
                    generateCrystalCave(world, center, radius);
                    break;
                case DESERT_OASIS:
                    generateDesertOasis(world, center, radius);
                    break;
                case DARK_FOREST:
                    generateDarkForest(world, center, radius);
                    break;
                case CORAL_REEF:
                    generateCoralReef(world, center, radius);
                    break;
                case MUSHROOM_LANDS:
                    generateMushroomLands(world, center, radius);
                    break;
                case SKY_ISLANDS:
                    generateSkyIslands(world, center, radius);
                    break;
                case NETHER_FORTRESS:
                    generateNetherFortress(world, center, radius);
                    break;
                case END_DIMENSION:
                    generateEndDimension(world, center, radius);
                    break;
            }
            
            plugin.getLogger().info("✓ Generated " + biome.displayName + " at " + center.getBlockX() + "," + center.getBlockZ());
        });
    }
    
    private void generateMysticalForest(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.7) {
                    block.setType(Material.PURPLE_CONCRETE);
                } else {
                    block.setType(Material.GRASS_BLOCK);
                }
            }
        }
    }
    
    private void generateLavaLand(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.8) {
                    block.setType(Material.LAVA);
                } else {
                    block.setType(Material.NETHERRACK);
                }
            }
        }
    }
    
    private void generateIceKingdom(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.85) {
                    block.setType(Material.PACKED_ICE);
                } else {
                    block.setType(Material.SNOW_BLOCK);
                }
            }
        }
    }
    
    private void generateJungleTemple(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.75) {
                    block.setType(Material.JUNGLE_LOG);
                } else {
                    block.setType(Material.JUNGLE_LEAVES);
                }
            }
        }
    }
    
    private void generateCrystalCave(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.6) {
                    block.setType(Material.AMETHYST_BLOCK);
                } else {
                    block.setType(Material.DEEPSLATE);
                }
            }
        }
    }
    
    private void generateDesertOasis(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int dist = (int) Math.sqrt(x * x + z * z);
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (dist < 10) {
                    block.setType(Material.WATER);
                } else if (Math.random() > 0.7) {
                    block.setType(Material.SAND);
                } else {
                    block.setType(Material.YELLOW_CONCRETE);
                }
            }
        }
    }
    
    private void generateDarkForest(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.65) {
                    block.setType(Material.DARK_OAK_LOG);
                } else {
                    block.setType(Material.DARK_OAK_LEAVES);
                }
            }
        }
    }
    
    private void generateCoralReef(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 62, center.getBlockZ() + z);
                block.setType(Material.WATER);
                
                Block surface = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                double rand = Math.random();
                if (rand > 0.8) surface.setType(Material.BRAIN_CORAL);
                else if (rand > 0.6) surface.setType(Material.BUBBLE_CORAL);
                else if (rand > 0.4) surface.setType(Material.FIRE_CORAL);
                else surface.setType(Material.HORN_CORAL);
            }
        }
    }
    
    private void generateMushroomLands(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.7) {
                    block.setType(Material.BROWN_MUSHROOM_BLOCK);
                } else {
                    block.setType(Material.RED_MUSHROOM_BLOCK);
                }
            }
        }
    }
    
    private void generateSkyIslands(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int dist = (int) Math.sqrt(x * x + z * z);
                if (dist % 3 == 0) {
                    Block block = world.getBlockAt(center.getBlockX() + x, 100 + dist, center.getBlockZ() + z);
                    block.setType(Material.END_STONE);
                }
            }
        }
    }
    
    private void generateNetherFortress(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.6) {
                    block.setType(Material.CRIMSON_NETHER_BRICK);
                } else {
                    block.setType(Material.BLACKSTONE);
                }
            }
        }
    }
    
    private void generateEndDimension(World world, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, 64, center.getBlockZ() + z);
                if (Math.random() > 0.75) {
                    block.setType(Material.END_STONE_BRICKS);
                } else {
                    block.setType(Material.PURPUR_BLOCK);
                }
            }
        }
    }
}
