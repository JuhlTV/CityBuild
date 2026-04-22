package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class AdminManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, AdminData> adminPlayers;

    public enum Role {
        OWNER("Owner", 4, "👑"),
        ADMIN("Admin", 3, "🔴"),
        MODERATOR("Moderator", 2, "🟡"),
        MEMBER("Member", 1, "🟢"),
        GUEST("Guest", 0, "⚪");

        public final String displayName;
        public final int level;
        public final String emoji;

        Role(String displayName, int level, String emoji) {
            this.displayName = displayName;
            this.level = level;
            this.emoji = emoji;
        }
    }

    public static class AdminData {
        public String uuid;
        public Role role;
        public long warnings;
        public boolean muted;
        public long muteEnd;
        public List<String> actionLog;
        public long joinedAt;

        public AdminData(String uuid) {
            this.uuid = uuid;
            this.role = Role.GUEST;
            this.warnings = 0;
            this.muted = false;
            this.muteEnd = 0;
            this.actionLog = new ArrayList<>();
            this.joinedAt = System.currentTimeMillis();
        }
    }

    public AdminManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/admin.json");
        this.adminPlayers = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    // ===== ROLE MANAGEMENT =====
    public void setRole(String uuid, Role role) {
        if (uuid == null || uuid.isEmpty() || role == null) {
            plugin.getLogger().warning("Invalid setRole call: uuid=" + uuid + ", role=" + role);
            return;
        }
        
        AdminData data = getAdminData(uuid);
        if (data != null) {
            data.role = role;
            logAction(uuid, "Role changed to: " + role.displayName);
            saveData();
        }
    }

    public Role getRole(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return Role.GUEST;
        }
        
        AdminData data = getAdminData(uuid);
        return data != null ? data.role : Role.GUEST;
    }

    public boolean hasPermission(String uuid, Role requiredRole) {
        if (uuid == null || requiredRole == null) {
            return false;
        }
        return getRole(uuid).level >= requiredRole.level;
    }

    public boolean canManage(String managerUuid, String targetUuid) {
        // Null/empty check
        if (managerUuid == null || managerUuid.isEmpty() || targetUuid == null || targetUuid.isEmpty()) {
            plugin.getLogger().warning("Invalid canManage call with null/empty uuid!");
            return false;
        }
        
        // Allow players to manage themselves (change own role)
        if (managerUuid.equals(targetUuid)) {
            return true;
        }
        
        Role managerRole = getRole(managerUuid);
        Role targetRole = getRole(targetUuid);
        
        // Manager must have strictly higher level than target
        return managerRole.level > targetRole.level;
    }

    // ===== WARNINGS SYSTEM =====
    public void addWarning(String uuid) {
        AdminData data = getAdminData(uuid);
        data.warnings++;
        logAction(uuid, "Warning added (Total: " + data.warnings + ")");
        saveData();
    }

    public long getWarnings(String uuid) {
        return getAdminData(uuid).warnings;
    }

    public void resetWarnings(String uuid) {
        AdminData data = getAdminData(uuid);
        data.warnings = 0;
        logAction(uuid, "Warnings reset");
        saveData();
    }

    public boolean shouldBeBanned(String uuid) {
        return getWarnings(uuid) >= 3;
    }

    // ===== MUTE SYSTEM =====
    public void mute(String uuid, long durationMs) {
        AdminData data = getAdminData(uuid);
        data.muted = true;
        data.muteEnd = System.currentTimeMillis() + durationMs;
        logAction(uuid, "Muted for " + (durationMs / 1000 / 60) + " minutes");
        saveData();
    }

    public void unmute(String uuid) {
        AdminData data = getAdminData(uuid);
        data.muted = false;
        data.muteEnd = 0;
        logAction(uuid, "Unmuted");
        saveData();
    }

    public boolean isMuted(String uuid) {
        AdminData data = getAdminData(uuid);
        if (!data.muted) return false;
        
        if (System.currentTimeMillis() >= data.muteEnd) {
            unmute(uuid);
            return false;
        }
        return true;
    }

    public long getMuteTimeRemaining(String uuid) {
        AdminData data = getAdminData(uuid);
        if (!data.muted) return 0;
        
        long remaining = data.muteEnd - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    // ===== ADMIN DATA =====
    public AdminData getAdminData(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            plugin.getLogger().warning("Attempted to get AdminData with null/empty UUID!");
            return new AdminData("unknown");
        }
        return adminPlayers.computeIfAbsent(uuid, AdminData::new);
    }

    public void logAction(String uuid, String action) {
        if (uuid == null || uuid.isEmpty() || action == null) {
            plugin.getLogger().warning("Invalid logAction call!");
            return;
        }
        
        AdminData data = getAdminData(uuid);
        if (data != null) {
            String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            data.actionLog.add("[" + timestamp + "] " + action);
            if (data.actionLog.size() > 100) {
            data.actionLog.remove(0); // Keep last 100 actions
        }
    }

    public List<String> getActionLog(String uuid) {
        return new ArrayList<>(getAdminData(uuid).actionLog);
    }

    public Map<String, AdminData> getAllAdminData() {
        return new HashMap<>(adminPlayers);
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                AdminData data = new AdminData(entry.getKey());
                JsonObject dJson = entry.getValue().getAsJsonObject();

                if (dJson.has("role")) {
                    data.role = Role.valueOf(dJson.get("role").getAsString());
                }
                if (dJson.has("warnings")) {
                    data.warnings = dJson.get("warnings").getAsLong();
                }
                if (dJson.has("muted")) {
                    data.muted = dJson.get("muted").getAsBoolean();
                }

                adminPlayers.put(entry.getKey(), data);
            });

            plugin.getLogger().info("✓ Loaded admin data");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load admin data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            adminPlayers.forEach((uuid, data) -> {
                JsonObject dJson = new JsonObject();
                dJson.addProperty("role", data.role.name());
                dJson.addProperty("warnings", data.warnings);
                dJson.addProperty("muted", data.muted);
                dJson.add("actionLog", gson.toJsonTree(data.actionLog));
                json.add(uuid, dJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save admin data: " + e.getMessage());
        }
    }
}
