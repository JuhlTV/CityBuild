package com.citybuild.features.ranking;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player ranking system and tier progression
 * Tracks rank progress based on achievement points
 */
public class RankingManager {

    private final Map<UUID, Rank> playerRanks = new HashMap<>();

    /**
     * Get player's current rank based on achievement points
     * @param uuid Player UUID
     * @param achievementPoints Total achievement points
     * @return Current rank
     */
    public Rank getPlayerRank(UUID uuid, int achievementPoints) {
        Rank currentRank = Rank.getRankForPoints(achievementPoints);
        playerRanks.put(uuid, currentRank);
        return currentRank;
    }

    /**
     * Get cached rank (without recalculating)
     */
    public Rank getCachedRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, Rank.BRONZE);
    }

    /**
     * Update rank cache for a player
     */
    public void updatePlayerRank(UUID uuid, int achievementPoints) {
        Rank newRank = Rank.getRankForPoints(achievementPoints);
        Rank oldRank = playerRanks.getOrDefault(uuid, Rank.BRONZE);
        playerRanks.put(uuid, newRank);

        // Notify if rank up
        if (newRank.getMinPoints() > oldRank.getMinPoints()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.showTitle(Title.title(
                    LegacyComponentSerializer.legacySection().deserialize("§6⭐ RANK UP! ⭐"),
                    LegacyComponentSerializer.legacySection().deserialize(newRank.getFormattedDisplay()),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                ));
                player.playSound(
                    player.getLocation(),
                    org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    1.0f, 1.0f
                );
            }

            // Broadcast to all players
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage("§e" + (player != null ? player.getName() : "Player") + 
                    " §7hat den Rang §6" + newRank.getFormattedDisplay() + "§7 erreicht!");
            });
        }
    }

    /**
     * Get points needed to reach next rank
     */
    public int getPointsToNextRank(UUID uuid, int currentPoints) {
        Rank currentRank = Rank.getRankForPoints(currentPoints);
        return currentRank.getPointsToNextRank(currentPoints);
    }

    /**
     * Get progress percentage to next rank (0-100)
     */
    public int getProgressPercentage(UUID uuid, int currentPoints) {
        Rank currentRank = Rank.getRankForPoints(currentPoints);
        return currentRank.getProgressToNextRank(currentPoints);
    }

    /**
     * Get formatted progress bar for rank progression
     */
    public String getProgressBar(UUID uuid, int currentPoints) {
        int progress = getProgressPercentage(uuid, currentPoints);
        
        int filled = progress / 10;
        StringBuilder bar = new StringBuilder("§6");
        
        for (int i = 0; i < 10; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        bar.append(" §7").append(progress).append("%");
        return bar.toString();
    }

    /**
     * Get all rank info formatted for display
     */
    public String getRankInfo(UUID uuid, int achievementPoints) {
        Rank rank = getPlayerRank(uuid, achievementPoints);
        int pointsToNext = getPointsToNextRank(uuid, achievementPoints);
        String progressBar = getProgressBar(uuid, achievementPoints);
        
        StringBuilder info = new StringBuilder();
        info.append(rank.getFormattedDisplay()).append("\n");
        info.append("§7Points: §6").append(achievementPoints).append("§7/").append(rank.getMaxPoints()).append("\n");
        
        if (rank != Rank.DIAMOND) {
            info.append("§7To Next: §6").append(pointsToNext).append(" points\n");
            info.append("§7Progress: ").append(progressBar);
        } else {
            info.append("§b⭐ Maximum Rank Erreicht! ⭐");
        }
        
        return info.toString();
    }

    /**
     * Clear all cached ranks (reload)
     */
    public void clearCache() {
        playerRanks.clear();
    }
}
