package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;

public class QuestManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, PlayerQuests> playerQuests;

    public enum Quest {
        CRAFT_WORKBENCH("craft_workbench", "Craft 5 Crafting Tables", 500, "craft", "crafting_table", 5),
        SMELT_IRON("smelt_iron", "Smelt 20 Iron Ore", 400, "smelt", "iron_ore", 20),
        BUILD_BLOCKS("build_blocks", "Place 100 Blocks", 300, "place", "any", 100),
        EARN_MONEY("earn_money", "Earn $5,000", 600, "earn", "money", 5000),
        KILL_MOBS("kill_mobs", "Kill 50 Mobs", 400, "kill", "mobs", 50),
        TRADE_ITEMS("trade_items", "Buy/Sell 20 Items", 350, "trade", "items", 20);

        public final String id;
        public final String displayName;
        public final long reward;
        public final String type;
        public final String target;
        public final int requirement;

        Quest(String id, String displayName, long reward, String type, String target, int requirement) {
            this.id = id;
            this.displayName = displayName;
            this.reward = reward;
            this.type = type;
            this.target = target;
            this.requirement = requirement;
        }
    }

    public static class PlayerQuests {
        public String uuid;
        public Map<String, Integer> questProgress; // Quest ID -> progress
        public Map<String, LocalDate> questDates; // Quest ID -> date completed
        public int completedToday;

        public PlayerQuests(String uuid) {
            this.uuid = uuid;
            this.questProgress = new HashMap<>();
            this.questDates = new HashMap<>();
            this.completedToday = 0;
        }
    }

    public QuestManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/quests.json");
        this.playerQuests = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    /**
     * Get or create player quests
     */
    public PlayerQuests getPlayerQuests(String playerUuid) {
        return playerQuests.computeIfAbsent(playerUuid, PlayerQuests::new);
    }

    /**
     * Add progress to a quest
     */
    public void addProgress(String playerUuid, Quest quest, int amount) {
        PlayerQuests pq = getPlayerQuests(playerUuid);
        int current = pq.questProgress.getOrDefault(quest.id, 0);
        pq.questProgress.put(quest.id, current + amount);
        saveData();
    }

    /**
     * Get quest progress
     */
    public int getProgress(String playerUuid, Quest quest) {
        return getPlayerQuests(playerUuid).questProgress.getOrDefault(quest.id, 0);
    }

    /**
     * Check if quest is completed
     */
    public boolean isQuestCompleted(String playerUuid, Quest quest) {
        LocalDate today = LocalDate.now();
        LocalDate questDate = getPlayerQuests(playerUuid).questDates.get(quest.id);
        return questDate != null && questDate.equals(today);
    }

    /**
     * Complete a quest (mark as done today)
     */
    public boolean completeQuest(String playerUuid, Quest quest) {
        if (isQuestCompleted(playerUuid, quest)) {
            return false; // Already completed today
        }

        PlayerQuests pq = getPlayerQuests(playerUuid);
        pq.questDates.put(quest.id, LocalDate.now());
        pq.completedToday++;
        pq.questProgress.put(quest.id, 0); // Reset progress
        saveData();
        return true;
    }

    /**
     * Get completed today count
     */
    public int getCompletedToday(String playerUuid) {
        return getPlayerQuests(playerUuid).completedToday;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) return;

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            json.entrySet().forEach(entry -> {
                PlayerQuests pq = new PlayerQuests(entry.getKey());
                JsonObject pqJson = entry.getValue().getAsJsonObject();

                // Load progress
                if (pqJson.has("progress")) {
                    pqJson.getAsJsonObject("progress").entrySet().forEach(e ->
                        pq.questProgress.put(e.getKey(), e.getValue().getAsInt())
                    );
                }

                playerQuests.put(entry.getKey(), pq);
            });

            plugin.getLogger().info("✓ Loaded quests from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load quest data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            JsonObject json = new JsonObject();

            playerQuests.forEach((uuid, pq) -> {
                JsonObject pqJson = new JsonObject();
                pqJson.add("progress", gson.toJsonTree(pq.questProgress));
                json.add(uuid, pqJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save quest data: " + e.getMessage());
        }
    }
}
