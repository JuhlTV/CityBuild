package com.citybuild.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ClanManager {
    private final JavaPlugin plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile;
    private final Map<String, Clan> clans; // Clan name -> Clan
    private final Map<String, String> playerClans; // Player UUID -> Clan name

    public static class Clan {
        public String name;
        public String founder; // UUID
        public long balance;
        public int level;
        public List<String> members; // List of UUIDs
        public long createdAt;

        public Clan(String name, String founder) {
            this.name = name;
            this.founder = founder;
            this.balance = 0;
            this.level = 1;
            this.members = new ArrayList<>();
            this.members.add(founder);
            this.createdAt = System.currentTimeMillis();
        }
    }

    public ClanManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/clans.json");
        this.clans = new HashMap<>();
        this.playerClans = new HashMap<>();

        dataFile.getParentFile().mkdirs();
        loadData();
    }

    /**
     * Create a new clan
     */
    public boolean createClan(String clanName, String founderUuid) {
        if (clans.containsKey(clanName) || playerClans.containsValue(founderUuid)) {
            return false;
        }

        Clan clan = new Clan(clanName, founderUuid);
        clans.put(clanName, clan);
        playerClans.put(founderUuid, clanName);
        saveData();
        return true;
    }

    /**
     * Add member to clan
     */
    public boolean addMember(String clanName, String memberUuid) {
        if (!clans.containsKey(clanName) || playerClans.containsKey(memberUuid)) {
            return false;
        }

        Clan clan = clans.get(clanName);
        clan.members.add(memberUuid);
        playerClans.put(memberUuid, clanName);
        saveData();
        return true;
    }

    /**
     * Remove member from clan
     */
    public boolean removeMember(String clanName, String memberUuid) {
        if (!clans.containsKey(clanName)) {
            return false;
        }

        Clan clan = clans.get(clanName);
        if (clan.members.remove(memberUuid)) {
            playerClans.remove(memberUuid);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Get player's clan
     */
    public Clan getPlayerClan(String playerUuid) {
        String clanName = playerClans.get(playerUuid);
        return clanName != null ? clans.get(clanName) : null;
    }

    /**
     * Get clan by name
     */
    public Clan getClan(String clanName) {
        return clans.get(clanName);
    }

    /**
     * Add money to clan balance
     */
    public void addBalance(String clanName, long amount) {
        if (clans.containsKey(clanName)) {
            clans.get(clanName).balance += amount;
            saveData();
        }
    }

    /**
     * Remove money from clan balance
     */
    public boolean removeBalance(String clanName, long amount) {
        Clan clan = clans.get(clanName);
        if (clan != null && clan.balance >= amount) {
            clan.balance -= amount;
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Get all clans
     */
    public Collection<Clan> getAllClans() {
        return new ArrayList<>(clans.values());
    }

    /**
     * Delete a clan
     */
    public boolean deleteClan(String clanName) {
        Clan clan = clans.remove(clanName);
        if (clan != null) {
            for (String member : clan.members) {
                playerClans.remove(member);
            }
            saveData();
            return true;
        }
        return false;
    }

    private void loadData() {
        try {
            if (!dataFile.exists()) {
                return;
            }

            JsonObject json = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();

            json.entrySet().forEach(entry -> {
                JsonObject clanJson = entry.getValue().getAsJsonObject();
                Clan clan = new Clan(entry.getKey(), clanJson.get("founder").getAsString());
                clan.balance = clanJson.get("balance").getAsLong();
                clan.level = clanJson.get("level").getAsInt();

                JsonArray membersArray = clanJson.getAsJsonArray("members");
                clan.members.clear();
                membersArray.forEach(el -> clan.members.add(el.getAsString()));

                clans.put(entry.getKey(), clan);

                // Rebuild player clans map
                for (String member : clan.members) {
                    playerClans.put(member, entry.getKey());
                }
            });

            plugin.getLogger().info("✓ Loaded clans from database");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load clan data: " + e.getMessage());
        }
    }

    public void saveData() {
        try {
            dataFile.getParentFile().mkdirs();

            JsonObject json = new JsonObject();
            clans.forEach((name, clan) -> {
                JsonObject clanJson = new JsonObject();
                clanJson.addProperty("founder", clan.founder);
                clanJson.addProperty("balance", clan.balance);
                clanJson.addProperty("level", clan.level);
                clanJson.add("members", gson.toJsonTree(clan.members));
                json.add(name, clanJson);
            });

            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(json, writer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save clan data: " + e.getMessage());
        }
    }
}
