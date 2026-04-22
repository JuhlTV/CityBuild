package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuditManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final List<AuditEntry> auditLog;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public enum AuditCategory {
        ADMIN_ACTION("👑 Admin Action"),
        ECONOMY("💰 Economy"),
        PLOT("🏗️ Plot"),
        PLAYER("👤 Player"),
        SYSTEM("⚙️ System"),
        SECURITY("🔒 Security"),
        PUNISHMENT("⚠️ Punishment");

        public final String display;
        AuditCategory(String display) {
            this.display = display;
        }
    }

    public static class AuditEntry {
        public long timestamp;
        public AuditCategory category;
        public String action;
        public String actor; // Who performed the action
        public String target; // Who/what was affected
        public String details;
        public boolean success;

        public AuditEntry(AuditCategory category, String action, String actor, String target, String details, boolean success) {
            this.timestamp = System.currentTimeMillis();
            this.category = category;
            this.action = action;
            this.actor = actor;
            this.target = target;
            this.details = details;
            this.success = success;
        }
    }

    public AuditManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/audit_log.json");
        this.auditLog = new ArrayList<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    // ===== AUDIT LOGGING =====
    public void log(AuditCategory category, String action, String actor, String target, String details) {
        log(category, action, actor, target, details, true);
    }

    public void log(AuditCategory category, String action, String actor, String target, String details, boolean success) {
        auditLog.add(new AuditEntry(category, action, actor, target, details, success));
        
        // Keep only last 1000 entries
        if (auditLog.size() > 1000) {
            auditLog.remove(0);
        }
        
        saveData();
    }

    public List<AuditEntry> getRecentEntries(int limit) {
        int startIndex = Math.max(0, auditLog.size() - limit);
        return new ArrayList<>(auditLog.subList(startIndex, auditLog.size()));
    }

    public List<AuditEntry> getEntriesByActor(String actor) {
        List<AuditEntry> results = new ArrayList<>();
        for (AuditEntry entry : auditLog) {
            if (entry.actor.equals(actor)) {
                results.add(entry);
            }
        }
        return results;
    }

    public List<AuditEntry> getEntriesByCategory(AuditCategory category) {
        List<AuditEntry> results = new ArrayList<>();
        for (AuditEntry entry : auditLog) {
            if (entry.category == category) {
                results.add(entry);
            }
        }
        return results;
    }

    public List<AuditEntry> getEntriesByTarget(String target) {
        List<AuditEntry> results = new ArrayList<>();
        for (AuditEntry entry : auditLog) {
            if (entry.target.equals(target)) {
                results.add(entry);
            }
        }
        return results;
    }

    public String formatEntry(AuditEntry entry) {
        String date = dateFormat.format(new Date(entry.timestamp));
        String status = entry.success ? "✓" : "✗";
        return String.format("[%s] %s %s | %s → %s | %s | %s",
            date, status, entry.category.display, entry.actor, entry.target, entry.action, entry.details);
    }

    public List<String> getFormattedLog(int limit) {
        List<String> formatted = new ArrayList<>();
        for (AuditEntry entry : getRecentEntries(limit)) {
            formatted.add(formatEntry(entry));
        }
        return formatted;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            if (json.has("auditLog")) {
                // Parse entries if needed - for now just confirm load
            }
            plugin.getLogger().info("✓ Loaded audit log");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load audit log: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();
            json.add("auditLog", gson.toJsonTree(auditLog));

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save audit log: " + e.getMessage());
        }
    }
}
