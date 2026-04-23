package com.citybuild.features.arenas;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Mob arena combat system
 */
public class MobArena {

    public enum Difficulty {
        EASY("§a[EASY]", 1.0, 5, 5000),
        NORMAL("§e[NORMAL]", 1.5, 10, 15000),
        HARD("§c[HARD]", 2.0, 15, 40000),
        EXTREME("§5[EXTREME]", 2.5, 25, 100000);

        private final String display;
        private final double healthMultiplier;
        private final int waveCount;
        private final double reward;

        Difficulty(String display, double healthMultiplier, int waveCount, double reward) {
            this.display = display;
            this.healthMultiplier = healthMultiplier;
            this.waveCount = waveCount;
            this.reward = reward;
        }

        public String getDisplay() { return display; }
        public double getHealthMultiplier() { return healthMultiplier; }
        public int getWaveCount() { return waveCount; }
        public double getReward() { return reward; }
    }

    private final String arenaId;
    private final String arenaName;
    private final Location spawnLocation;
    private final Location exitLocation;
    private final Set<UUID> activePlayers;
    private final int maxPlayers;
    private Difficulty difficulty;
    private int currentWave;
    private boolean active;

    public MobArena(String arenaId, String arenaName, Location spawn, Location exit, int maxPlayers) {
        this.arenaId = arenaId;
        this.arenaName = arenaName;
        this.spawnLocation = spawn;
        this.exitLocation = exit;
        this.activePlayers = new HashSet<>();
        this.maxPlayers = maxPlayers;
        this.difficulty = Difficulty.NORMAL;
        this.currentWave = 0;
        this.active = false;
    }

    /**
     * Player enters arena
     */
    public boolean addPlayer(Player player) {
        if (activePlayers.size() >= maxPlayers) return false;

        activePlayers.add(player.getUniqueId());
        player.teleport(spawnLocation);
        return true;
    }

    /**
     * Player leaves arena
     */
    public void removePlayer(UUID playerUUID) {
        activePlayers.remove(playerUUID);

        if (activePlayers.isEmpty()) {
            active = false;
            currentWave = 0;
        }
    }

    /**
     * Start wave
     */
    public void startWave() {
        if (currentWave < difficulty.getWaveCount()) {
            currentWave++;
            active = true;
        } else {
            finishArena();
        }
    }

    /**
     * Finish arena
     */
    public void finishArena() {
        active = false;
        currentWave = 0;
    }

    /**
     * Get arena info
     */
    public String getArenaInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6").append(arenaName).append(" ").append(difficulty.getDisplay()).append("\n");
        sb.append("§7Wave: §e").append(currentWave).append("/").append(difficulty.getWaveCount()).append("\n");
        sb.append("§7Players: §e").append(activePlayers.size()).append("/").append(maxPlayers).append("\n");
        sb.append("§7Status: ").append(active ? "§a✓ ACTIVE" : "§c✕ IDLE").append("\n");
        sb.append("§7Reward: §6$").append(String.format("%.0f", difficulty.getReward()));
        return sb.toString();
    }

    // Getters
    public String getArenaId() { return arenaId; }
    public String getArenaName() { return arenaName; }
    public Location getSpawnLocation() { return spawnLocation; }
    public Location getExitLocation() { return exitLocation; }
    public Set<UUID> getActivePlayers() { return new HashSet<>(activePlayers); }
    public int getMaxPlayers() { return maxPlayers; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public int getCurrentWave() { return currentWave; }
    public boolean isActive() { return active; }
    public int getPlayerCount() { return activePlayers.size(); }
}

/**
 * Arena Manager
 */
class ArenasManager {

    private final JavaPlugin plugin;
    private final Map<String, MobArena> arenas = new HashMap<>();
    private int arenaCounter = 0;

    ArenasManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeDefaultArenas();
    }

    private void initializeDefaultArenas() {
        plugin.getLogger().info("§6✓ Initialized mob arenas");
    }

    /**
     * Create arena
     */
    public String createArena(String name, Location spawn, Location exit) {
        String arenaId = "ARENA_" + (arenaCounter++);
        MobArena arena = new MobArena(arenaId, name, spawn, exit, 4);
        arenas.put(arenaId, arena);

        plugin.getLogger().info("§6Arena created: " + name);
        return arenaId;
    }

    /**
     * Get arena
     */
    public MobArena getArena(String arenaId) {
        return arenas.get(arenaId);
    }

    /**
     * Get all arenas
     */
    public Collection<MobArena> getAllArenas() {
        return new ArrayList<>(arenas.values());
    }

    /**
     * Get statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_arenas", arenas.size());
        stats.put("active_arenas", arenas.values().stream().filter(MobArena::isActive).count());
        stats.put("active_players", arenas.values().stream()
            .mapToInt(MobArena::getPlayerCount)
            .sum());
        return stats;
    }
}
