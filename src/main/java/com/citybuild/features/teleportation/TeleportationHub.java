package com.citybuild.features.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Teleportation hub system
 */
public class TeleportationHub {

    public static class Hub {
        private final String hubId;
        private final String hubName;
        private final Location location;
        private final Set<String> destinations;
        private final double cost;
        private int uses;

        public Hub(String hubId, String hubName, Location location, double cost) {
            this.hubId = hubId;
            this.hubName = hubName;
            this.location = location;
            this.cost = cost;
            this.destinations = new HashSet<>();
            this.uses = 0;
        }

        public void addDestination(String destination) { destinations.add(destination); }
        public void removeDestination(String destination) { destinations.remove(destination); }
        public void recordUse() { this.uses++; }

        public String getHubId() { return hubId; }
        public String getHubName() { return hubName; }
        public Location getLocation() { return location; }
        public Set<String> getDestinations() { return new HashSet<>(destinations); }
        public double getCost() { return cost; }
        public int getUses() { return uses; }
    }

    private final JavaPlugin plugin;
    private final Map<String, Hub> hubs = new HashMap<>();
    private final Map<String, Location> destinations = new HashMap<>();
    private int hubCounter = 0;

    public TeleportationHub(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Create new hub
     */
    public String createHub(String name, Location location, double cost) {
        String hubId = "HUB_" + (hubCounter++);
        Hub hub = new Hub(hubId, name, location, cost);
        hubs.put(hubId, hub);

        plugin.getLogger().info("§6Teleportation hub created: " + name + " at (" +
            location.getX() + ", " + location.getY() + ", " + location.getZ() + ")");

        return hubId;
    }

    /**
     * Register destination
     */
    public void registerDestination(String name, Location location) {
        destinations.put(name, location);
        plugin.getLogger().info("§6Destination registered: " + name);
    }

    /**
     * Teleport player
     */
    public boolean teleportPlayer(Player player, String hubId, String destination) {
        Hub hub = hubs.get(hubId);
        if (hub == null) return false;

        if (!hub.getDestinations().contains(destination)) {
            player.sendMessage("§c❌ Destination not available at this hub!");
            return false;
        }

        Location dest = destinations.get(destination);
        if (dest == null) return false;

        hub.recordUse();
        player.teleport(dest);
        player.sendMessage("§a✓ Teleported to §6" + destination);

        return true;
    }

    /**
     * Get hub
     */
    public Hub getHub(String hubId) {
        return hubs.get(hubId);
    }

    /**
     * Get all hubs
     */
    public Collection<Hub> getAllHubs() {
        return new ArrayList<>(hubs.values());
    }

    /**
     * Get hub statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_hubs", hubs.size());
        stats.put("total_destinations", destinations.size());
        stats.put("total_uses", hubs.values().stream()
            .mapToInt(Hub::getUses)
            .sum());
        return stats;
    }

    /**
     * Get formatted hub info
     */
    public String getHubInfo(Hub hub) {
        StringBuilder sb = new StringBuilder();
        sb.append("§6").append(hub.getHubName()).append("\n");
        sb.append("§7Cost: §6$").append(String.format("%.0f", hub.getCost())).append("\n");
        sb.append("§7Destinations: §e").append(hub.getDestinations().size()).append("\n");
        sb.append("§7Total Uses: §e").append(hub.getUses());
        return sb.toString();
    }
}
