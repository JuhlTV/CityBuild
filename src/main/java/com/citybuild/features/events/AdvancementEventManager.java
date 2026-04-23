package com.citybuild.features.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages all advancement events in the game
 */
public class AdvancementEventManager {

    private final JavaPlugin plugin;
    private final Map<String, AdvancementEvent> activeEvents = new HashMap<>();
    private final Map<String, AdvancementEvent> finishedEvents = new HashMap<>();
    private int eventCounter = 0;
    private BukkitTask eventCheckTask;

    public AdvancementEventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeDefaultEvents();
        startEventCheckTask();
    }

    /**
     * Initialize default events
     */
    private void initializeDefaultEvents() {
        // Create default seasonal events
        createEvent("Mining Challenge", "Mine the most blocks this season!",
                AdvancementEvent.EventType.SEASONAL, 50000, 30000, 15000);

        createEvent("Trading Tournament", "Complete the most trades!",
                AdvancementEvent.EventType.MONTHLY, 20000, 12000, 8000);

        createEvent("Builder's Week", "Build on the most plots!",
                AdvancementEvent.EventType.WEEKLY, 5000, 3000, 1500);

        createEvent("Farmer's Harvest", "Collect the most crops!",
                AdvancementEvent.EventType.SEASONAL, 40000, 25000, 12000);

        plugin.getLogger().info("§6✓ Initialized 4 default advancement events");
    }

    /**
     * Create a new event
     */
    public String createEvent(String eventName, String description, AdvancementEvent.EventType type,
                             double reward1, double reward2, double reward3) {
        String eventId = "EVENT_" + (eventCounter++);
        AdvancementEvent event = new AdvancementEvent(eventId, eventName, description, type, reward1, reward2, reward3);
        activeEvents.put(eventId, event);

        Bukkit.broadcastMessage("§6╔════════════════════════════════════════╗");
        Bukkit.broadcastMessage("§6║ 🎪 NEW ADVANCEMENT EVENT");
        Bukkit.broadcastMessage("§6╚════════════════════════════════════════╝");
        Bukkit.broadcastMessage("§e" + eventName + " §7(" + type.getDisplay() + ")");
        Bukkit.broadcastMessage("§7" + description);
        Bukkit.broadcastMessage("§6Use §e/event view §6to join!");

        return eventId;
    }

    /**
     * Update player score in event
     */
    public void updateEventScore(String eventId, java.util.UUID playerUUID, double score) {
        AdvancementEvent event = activeEvents.get(eventId);
        if (event != null && event.isActive()) {
            event.updateScore(playerUUID, score);
        }
    }

    /**
     * Get all active events
     */
    public Collection<AdvancementEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents.values());
    }

    /**
     * Get specific event
     */
    public AdvancementEvent getEvent(String eventId) {
        AdvancementEvent event = activeEvents.get(eventId);
        if (event == null) {
            event = finishedEvents.get(eventId);
        }
        return event;
    }

    /**
     * Finish an event and distribute rewards
     */
    private void finishEvent(String eventId) {
        AdvancementEvent event = activeEvents.remove(eventId);
        if (event != null) {
            event.finishEvent();
            finishedEvents.put(eventId, event);

            // Get top 3
            List<Map.Entry<java.util.UUID, Double>> topPlayers = event.getParticipantScores().entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(3)
                    .toList();

            // Distribute rewards
            Bukkit.broadcastMessage("§6╔════════════════════════════════════════╗");
            Bukkit.broadcastMessage("§6║ EVENT FINISHED: " + event.getEventName());
            Bukkit.broadcastMessage("§6╚════════════════════════════════════════╝");

            for (int i = 0; i < topPlayers.size(); i++) {
                Map.Entry<java.util.UUID, Double> entry = topPlayers.get(i);
                Player player = Bukkit.getPlayer(entry.getKey());
                double reward = switch (i) {
                    case 0 -> event.getFirstPlaceReward();
                    case 1 -> event.getSecondPlaceReward();
                    case 2 -> event.getThirdPlaceReward();
                    default -> 0;
                };

                String place = switch (i) {
                    case 0 -> "🥇 1st Place";
                    case 1 -> "🥈 2nd Place";
                    case 2 -> "🥉 3rd Place";
                    default -> "";
                };

                Bukkit.broadcastMessage(place + " §7[§e" + (i + 1) + "§7]: §6$" + String.format("%.0f", reward));

                if (player != null) {
                    player.sendMessage("§a✓ You won §6$" + String.format("%.0f", reward) + " §ain the event!");
                }
            }

            plugin.getLogger().info("Event finished: " + event.getEventName());
        }
    }

    /**
     * Start event cleanup task
     */
    private void startEventCheckTask() {
        eventCheckTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<String> expiredEvents = new ArrayList<>();

            for (AdvancementEvent event : activeEvents.values()) {
                if (event.hasExpired()) {
                    expiredEvents.add(event.getEventId());
                }
            }

            for (String eventId : expiredEvents) {
                finishEvent(eventId);
            }
        }, 1200L, 1200L); // Check every 60 seconds
    }

    /**
     * Get event statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("active_events", activeEvents.size());
        stats.put("finished_events", finishedEvents.size());
        stats.put("total_participants", activeEvents.values().stream()
                .mapToInt(e -> e.getParticipantScores().size())
                .sum());
        return stats;
    }

    /**
     * Stop all tasks
     */
    public void shutdown() {
        if (eventCheckTask != null) {
            eventCheckTask.cancel();
        }
    }
}
