package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DailyRewardManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, DailyRewardData> dailyRewards;
    
    private static final long DAILY_REWARD = 500; // $500 per day
    
    public static class DailyRewardData {
        public String uuid;
        public String lastRewardDate;
        public int consecutiveDays;
        public long totalRewards;
        
        public DailyRewardData(String uuid) {
            this.uuid = uuid;
            this.lastRewardDate = "";
            this.consecutiveDays = 0;
            this.totalRewards = 0;
        }
    }
    
    public DailyRewardManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/dailyrewards.json");
        this.dailyRewards = new HashMap<>();
        
        dataFile.getParentFile().mkdirs();
        loadData();
    }
    
    /**
     * Claim daily reward if available
     */
    public long claimDailyReward(Player player) {
        String uuid = player.getUniqueId().toString();
        String today = LocalDate.now().toString();
        
        DailyRewardData data = dailyRewards.computeIfAbsent(uuid, DailyRewardData::new);
        
        // Check if already claimed today
        if (data.lastRewardDate.equals(today)) {
            return 0; // Already claimed
        }
        
        // Check for consecutive days
        if (!data.lastRewardDate.isEmpty()) {
            LocalDate lastDate = LocalDate.parse(data.lastRewardDate);
            LocalDate currentDate = LocalDate.parse(today);
            
            if (lastDate.equals(currentDate.minusDays(1))) {
                // Consecutive day
                data.consecutiveDays++;
            } else {
                // Streak broken
                data.consecutiveDays = 1;
            }
        } else {
            data.consecutiveDays = 1;
        }
        
        // Calculate reward with streak bonus
        long reward = DAILY_REWARD + (data.consecutiveDays - 1) * 100; // +100 per consecutive day
        
        data.lastRewardDate = today;
        data.totalRewards += reward;
        
        saveData();
        
        return reward;
    }
    
    /**
     * Check if player can claim daily reward
     */
    public boolean canClaimDaily(Player player) {
        String uuid = player.getUniqueId().toString();
        String today = LocalDate.now().toString();
        
        DailyRewardData data = dailyRewards.get(uuid);
        if (data == null) {
            return true;
        }
        
        return !data.lastRewardDate.equals(today);
    }
    
    /**
     * Get next reward amount
     */
    public long getNextRewardAmount(Player player) {
        String uuid = player.getUniqueId().toString();
        DailyRewardData data = dailyRewards.get(uuid);
        
        if (data == null) {
            return DAILY_REWARD;
        }
        
        int streak = data.consecutiveDays;
        return DAILY_REWARD + Math.max(0, streak) * 100;
    }
    
    /**
     * Get consecutive days
     */
    public int getConsecutiveDays(Player player) {
        String uuid = player.getUniqueId().toString();
        DailyRewardData data = dailyRewards.get(uuid);
        
        return data != null ? data.consecutiveDays : 0;
    }
    
    /**
     * Get total claimed rewards
     */
    public long getTotalRewards(Player player) {
        String uuid = player.getUniqueId().toString();
        DailyRewardData data = dailyRewards.get(uuid);
        
        return data != null ? data.totalRewards : 0;
    }
    
    /**
     * Load daily reward data from JSON
     */
    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }
            
            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
            
            json.entrySet().forEach(entry -> {
                JsonObject obj = entry.getValue().getAsJsonObject();
                DailyRewardData data = new DailyRewardData(entry.getKey());
                data.lastRewardDate = obj.get("lastRewardDate").getAsString();
                data.consecutiveDays = obj.get("consecutiveDays").getAsInt();
                data.totalRewards = obj.get("totalRewards").getAsLong();
                dailyRewards.put(entry.getKey(), data);
            });
            
            plugin.getLogger().info("✓ Loaded daily reward data");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load daily reward data: " + e.getMessage());
        }
    }
    
    /**
     * Save daily reward data to JSON
     */
    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            dailyRewards.forEach((uuid, data) -> {
                JsonObject obj = new JsonObject();
                obj.addProperty("lastRewardDate", data.lastRewardDate);
                obj.addProperty("consecutiveDays", data.consecutiveDays);
                obj.addProperty("totalRewards", data.totalRewards);
                json.add(uuid, obj);
            });
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save daily reward data: " + e.getMessage());
        }
    }
}
