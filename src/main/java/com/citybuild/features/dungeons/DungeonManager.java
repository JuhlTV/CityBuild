package com.citybuild.features.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Manages all dungeons in the game
 */
public class DungeonManager {

    private final JavaPlugin plugin;
    private final Map<String, Dungeon> dungeons = new HashMap<>();
    private final Map<UUID, String> playerCurrentDungeon = new HashMap<>();
    private int dungeonCounter = 0;

    public DungeonManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeDefaultDungeons();
    }

    /**
     * Initialize default dungeons
     */
    private void initializeDefaultDungeons() {
        createDungeon("The Goblin Mines", "Battle through endless goblin hordes", 4, 15);
        createDungeon("Dragon's Lair", "Face the mighty dragon boss", 6, 25);
        createDungeon("Undead Crypt", "Survive waves of undead creatures", 5, 20);
        createDungeon("Wizard's Tower", "Master dangerous magical trials", 4, 30);

        plugin.getLogger().info("§6✓ Initialized 4 default dungeons");
    }

    /**
     * Create a new dungeon
     */
    public String createDungeon(String name, String description, int maxPlayers, int durationMinutes) {
        String dungeonId = "DUNGEON_" + (dungeonCounter++);
        Dungeon dungeon = new Dungeon(dungeonId, name, description, maxPlayers, durationMinutes);
        dungeons.put(dungeonId, dungeon);

        Bukkit.broadcastMessage("§6╔════════════════════════════════════════╗");
        Bukkit.broadcastMessage("§6║ ⚔️  NEW DUNGEON AVAILABLE");
        Bukkit.broadcastMessage("§6╚════════════════════════════════════════╝");
        Bukkit.broadcastMessage("§e" + name);
        Bukkit.broadcastMessage("§7" + description);
        Bukkit.broadcastMessage("§6Use §e/dungeon list §6to enter!");

        return dungeonId;
    }

    /**
     * Get dungeon by ID
     */
    public Dungeon getDungeon(String dungeonId) {
        return dungeons.get(dungeonId);
    }

    /**
     * Player enters dungeon
     */
    public boolean enterDungeon(UUID playerUUID, String dungeonId) {
        // Check if already in a dungeon
        if (playerCurrentDungeon.containsKey(playerUUID)) {
            return false;
        }

        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon == null) return false;

        if (dungeon.addPlayer(playerUUID)) {
            playerCurrentDungeon.put(playerUUID, dungeonId);

            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§a✓ Entered dungeon: §6" + dungeon.getDungeonName());
                player.sendMessage("§7Duration: §e" + dungeon.getDurationMinutes() + " minutes");
            }

            return true;
        }

        return false;
    }

    /**
     * Player exits dungeon
     */
    public void exitDungeon(UUID playerUUID) {
        String dungeonId = playerCurrentDungeon.get(playerUUID);
        if (dungeonId == null) return;

        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon != null) {
            dungeon.removePlayer(playerUUID);
            playerCurrentDungeon.remove(playerUUID);

            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage("§cYou left the dungeon!");
            }
        }
    }

    /**
     * Complete dungeon
     */
    public void completeDungeon(UUID playerUUID, Dungeon.Difficulty difficulty) {
        String dungeonId = playerCurrentDungeon.get(playerUUID);
        if (dungeonId == null) return;

        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon != null) {
            dungeon.completeDungeon(playerUUID, difficulty);
            exitDungeon(playerUUID);

            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendTitle(
                    "§a✓ DUNGEON COMPLETE!",
                    "§6+" + String.format("%.0f", difficulty.getReward()),
                    10, 40, 10
                );
                player.sendMessage("§a✓ Dungeon completed on " + difficulty.getDisplay() + "§a!");
                player.sendMessage("§6Reward: $" + String.format("%.0f", difficulty.getReward()));
            }
        }
    }

    /**
     * Get player's current dungeon
     */
    public Dungeon getPlayerDungeon(UUID playerUUID) {
        String dungeonId = playerCurrentDungeon.get(playerUUID);
        return dungeonId != null ? dungeons.get(dungeonId) : null;
    }

    /**
     * Get all dungeons
     */
    public Collection<Dungeon> getAllDungeons() {
        return new ArrayList<>(dungeons.values());
    }

    /**
     * Get dungeon statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_dungeons", dungeons.size());
        stats.put("active_players", playerCurrentDungeon.size());
        stats.put("total_clears", dungeons.values().stream()
            .mapToInt(Dungeon::getTotalClears)
            .sum());
        stats.put("total_rewards", dungeons.values().stream()
            .mapToDouble(Dungeon::getTotalRewardsDistributed)
            .sum());
        return stats;
    }
}
