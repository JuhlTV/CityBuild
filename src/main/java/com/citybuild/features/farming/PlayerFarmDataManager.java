package com.citybuild.features.farming;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.citybuild.storage.DataManager;
import java.util.*;

/**
 * PlayerFarmDataManager - Manages player farming statistics and persistence
 */
public class PlayerFarmDataManager {
    private final Plugin plugin;
    private final DataManager dataManager;
    private final Map<String, PlayerFarmData> playerData;

    public PlayerFarmDataManager(Plugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.playerData = new HashMap<>();
    }

    /**
     * Get or create player farm data
     */
    public PlayerFarmData getPlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        
        if (!playerData.containsKey(uuid)) {
            // Try to load from JSON
            Map<String, Object> data = dataManager.loadPlayerFarmData(uuid);
            if (data != null && !data.isEmpty()) {
                playerData.put(uuid, PlayerFarmData.fromMap(uuid, data));
            } else {
                // Create new player data
                playerData.put(uuid, new PlayerFarmData(uuid));
            }
        }
        
        return playerData.get(uuid);
    }

    /**
     * Save single player data
     */
    public void savePlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        PlayerFarmData data = playerData.get(uuid);
        if (data != null) {
            dataManager.savePlayerFarmData(uuid, data.toMap());
        }
    }

    /**
     * Save all player data
     */
    public void saveAllData() {
        for (Map.Entry<String, PlayerFarmData> entry : playerData.entrySet()) {
            dataManager.savePlayerFarmData(entry.getKey(), entry.getValue().toMap());
        }
        plugin.getLogger().info("✓ Saved farm data for " + playerData.size() + " players");
    }

    /**
     * Remove player data from cache (optional cleanup)
     */
    public void unloadPlayerData(String playerUUID) {
        PlayerFarmData data = playerData.get(playerUUID);
        if (data != null) {
            dataManager.savePlayerFarmData(playerUUID, data.toMap());
            playerData.remove(playerUUID);
        }
    }

    public PlayerFarmData getPlayerFarmData(UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }

        String uuid = uniqueId.toString();
        if (!playerData.containsKey(uuid)) {
            Map<String, Object> data = dataManager.loadPlayerFarmData(uuid);
            if (data != null && !data.isEmpty()) {
                playerData.put(uuid, PlayerFarmData.fromMap(uuid, data));
            } else {
                playerData.put(uuid, new PlayerFarmData(uuid));
            }
        }

        return playerData.get(uuid);
    }
}
