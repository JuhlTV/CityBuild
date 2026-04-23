package com.citybuild.features.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a seasonal advancement event
 */
public class AdvancementEvent {

    public enum EventType {
        SEASONAL("🎪 Seasonal", 30),      // 30 days
        MONTHLY("📅 Monthly", 7),         // 7 days
        WEEKLY("📆 Weekly", 1),           // 1 day
        SPECIAL("⭐ Special Event", 3);   // 3 days

        private final String display;
        private final int durationDays;

        EventType(String display, int durationDays) {
            this.display = display;
            this.durationDays = durationDays;
        }

        public String getDisplay() {
            return display;
        }

        public int getDurationDays() {
            return durationDays;
        }
    }

    private final String eventId;
    private final String eventName;
    private final String description;
    private final EventType type;
    private final Map<UUID, Double> participantScores;
    private final double firstPlaceReward;
    private final double secondPlaceReward;
    private final double thirdPlaceReward;
    private final long startTime;
    private final long duration;
    private boolean isActive;

    public AdvancementEvent(String eventId, String eventName, String description, EventType type,
                           double firstPlaceReward, double secondPlaceReward, double thirdPlaceReward) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.type = type;
        this.participantScores = new HashMap<>();
        this.firstPlaceReward = firstPlaceReward;
        this.secondPlaceReward = secondPlaceReward;
        this.thirdPlaceReward = thirdPlaceReward;
        this.startTime = System.currentTimeMillis();
        this.duration = type.getDurationDays() * 24 * 60 * 60 * 1000L;
        this.isActive = true;
    }

    /**
     * Add or update participant score
     */
    public void updateScore(UUID playerUUID, double score) {
        participantScores.merge(playerUUID, score, Double::sum);
    }

    /**
     * Get time remaining in hours
     */
    public long getTimeRemainingHours() {
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = duration - elapsed;
        return Math.max(0, remaining / (60 * 60 * 1000));
    }

    /**
     * Check if event has expired
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }

    /**
     * Finish the event
     */
    public void finishEvent() {
        this.isActive = false;
    }

    /**
     * Get formatted display
     */
    public String getFormattedDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6").append(eventName).append(" ").append(type.getDisplay()).append("\n");
        sb.append("§7").append(description).append("\n");
        sb.append("§7Status: ").append(isActive ? "§a✓ ACTIVE" : "§c✕ FINISHED").append("\n");
        sb.append("§7Time Remaining: §e").append(getTimeRemainingHours()).append(" hours\n");
        sb.append("§7Participants: §e").append(participantScores.size()).append("\n");
        sb.append("§7Rewards: §6$").append(String.format("%.0f", firstPlaceReward));
        sb.append(" §7| §6$").append(String.format("%.0f", secondPlaceReward));
        sb.append(" §7| §6$").append(String.format("%.0f", thirdPlaceReward));
        return sb.toString();
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getDescription() { return description; }
    public EventType getType() { return type; }
    public Map<UUID, Double> getParticipantScores() { return new HashMap<>(participantScores); }
    public double getFirstPlaceReward() { return firstPlaceReward; }
    public double getSecondPlaceReward() { return secondPlaceReward; }
    public double getThirdPlaceReward() { return thirdPlaceReward; }
    public boolean isActive() { return isActive; }
    public long getStartTime() { return startTime; }
}
