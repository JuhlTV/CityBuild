package com.citybuild.features.treasures;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;

/**
 * Manages treasure hunting system
 */
public class TreasureManager {

    private final JavaPlugin plugin;
    private final Map<String, TreasureChest> treasures = new HashMap<>();
    private final Map<String, Integer> playerDiscoveries = new HashMap<>();
    private int treasureCounter = 0;
    private BukkitTask expireTask;

    public TreasureManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startExpireTask();
    }

    /**
     * Spawn a new treasure
     */
    public String spawnTreasure(Location location, TreasureChest.Rarity rarity) {
        String treasureId = "TREASURE_" + (treasureCounter++);
        TreasureChest chest = new TreasureChest(treasureId, rarity, location);
        treasures.put(treasureId, chest);

        plugin.getLogger().info("§6Treasure spawned at: " + location.getX() + ", " +
            location.getY() + ", " + location.getZ());

        return treasureId;
    }

    /**
     * Discover treasure
     */
    public boolean discoverTreasure(UUID playerUUID, String treasureId) {
        TreasureChest chest = treasures.get(treasureId);
        if (chest == null) return false;
        if (!chest.isHidden()) return false;

        chest.discover(playerUUID);
        playerDiscoveries.merge(playerUUID.toString(), 1, Integer::sum);

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.showTitle(Title.title(
                legacy("§6✨ TREASURE FOUND! ✨"),
                legacy("§e+" + String.format("%.0f", chest.getRarity().getReward())),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
            ));
        }

        broadcastLegacy("§6✨ " + (player != null ? player.getName() : "Unknown") +
            " found a " + chest.getRarity().getDisplay() + " §6treasure!");

        return true;
    }

    /**
     * Get treasure by ID
     */
    public TreasureChest getTreasure(String treasureId) {
        return treasures.get(treasureId);
    }

    /**
     * Find nearest treasure to location
     */
    public TreasureChest findNearestTreasure(Location location, double radius) {
        TreasureChest nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (TreasureChest chest : treasures.values()) {
            if (!chest.isHidden()) continue;
            if (chest.hasExpired()) continue;

            double dist = location.distance(chest.getLocation());
            if (dist <= radius && dist < nearestDist) {
                nearest = chest;
                nearestDist = dist;
            }
        }

        return nearest;
    }

    /**
     * Get all hidden treasures
     */
    public Collection<TreasureChest> getHiddenTreasures() {
        return treasures.values().stream()
            .filter(TreasureChest::isHidden)
            .filter(t -> !t.hasExpired())
            .toList();
    }

    /**
     * Get player discoveries
     */
    public int getPlayerDiscoveries(UUID playerUUID) {
        return playerDiscoveries.getOrDefault(playerUUID.toString(), 0);
    }

    /**
     * Start expire task
     */
    private void startExpireTask() {
        expireTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<String> expiredTreasures = new ArrayList<>();

            for (TreasureChest chest : treasures.values()) {
                if (chest.hasExpired()) {
                    expiredTreasures.add(chest.getTreasureId());
                }
            }

            for (String id : expiredTreasures) {
                treasures.remove(id);
                plugin.getLogger().info("§6Treasure expired: " + id);
            }
        }, 1200L, 1200L); // Check every 60 seconds
    }

    /**
     * Spawn random treasures around world
     */
    public void spawnRandomTreasures(int count) {
        World world = Bukkit.getWorlds().get(0);
        if (world == null) return;

        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(1000) - 500;
            int z = random.nextInt(1000) - 500;
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x, y + 1, z);
            TreasureChest.Rarity rarity = TreasureChest.Rarity.values()[random.nextInt(TreasureChest.Rarity.values().length)];

            spawnTreasure(loc, rarity);
        }

        broadcastLegacy("§6✨ " + count + " treasures have been hidden around the world!");
    }

    /**
     * Get statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_treasures", treasures.size());
        stats.put("hidden_treasures", getHiddenTreasures().size());
        stats.put("discovered_treasures", treasures.size() - getHiddenTreasures().size());
        stats.put("total_value", treasures.values().stream()
            .mapToDouble(t -> t.getRarity().getReward())
            .sum());
        return stats;
    }

    /**
     * Shutdown
     */
    public void shutdown() {
        if (expireTask != null) {
            expireTask.cancel();
        }
    }

    private Component legacy(String message) {
        return LegacyComponentSerializer.legacySection().deserialize(message);
    }

    private void broadcastLegacy(String message) {
        Bukkit.broadcast(legacy(message));
    }
}
