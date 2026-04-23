package com.citybuild.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.citybuild.CityBuildPlugin;
import com.citybuild.features.economy.EconomyManager;
import com.citybuild.features.farming.PlayerFarmData;
import com.citybuild.features.farming.PlayerFarmDataManager;
import com.citybuild.features.farming.AchievementManager;

import java.util.*;

/**
 * BlockBreakListener - Premium Farming System with:
 * ✨ Tiered Rewards | 🛡️ Anti-Cheat | ⛏️ Enchantment Bonuses
 * 🔥 Streak System | 🌙 Night Multiplier | 🎨 Combo Rewards
 * 📈 Level System | 🏆 Achievements | 💎 Ore Multipliers
 */
@SuppressWarnings("deprecation")
public class BlockBreakListener implements Listener {
    private final CityBuildPlugin plugin;
    private final EconomyManager economyManager;
    private final AchievementManager achievementManager;
    private final PlayerFarmDataManager farmDataManager;
    
    private static final String FARM_WORLD_NAME = "farm";
    
    // Tiered reward system with ore multipliers
    private static final Map<Material, Double> BLOCK_REWARDS = new HashMap<>();
    
    // Cooldown system (prevents spam farming)
    private static final Map<String, Long> COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_MILLIS = 50;
    
    // Streak tracking (last block break time per player)
    private static final Map<String, Long> STREAK_COOLDOWNS = new HashMap<>();
    private static final long STREAK_TIMEOUT_MS = 30000; // 30 seconds without mining = streak reset
    
    static {
        // Initialize reward tiers
        // Tier 1: Common (0.5 coins) - 1.0x multiplier
        BLOCK_REWARDS.put(Material.DIRT, 0.5);
        BLOCK_REWARDS.put(Material.GRASS_BLOCK, 0.5);
        BLOCK_REWARDS.put(Material.SAND, 0.5);
        BLOCK_REWARDS.put(Material.GRAVEL, 0.5);
        BLOCK_REWARDS.put(Material.WHEAT, 0.75);
        BLOCK_REWARDS.put(Material.SUGAR_CANE, 0.75);
        BLOCK_REWARDS.put(Material.CACTUS, 0.75);
        
        // Tier 2: Standard (1.0 coins) - 1.0x multiplier
        BLOCK_REWARDS.put(Material.STONE, 1.0);
        BLOCK_REWARDS.put(Material.COBBLESTONE, 1.0);
        BLOCK_REWARDS.put(Material.OAK_LOG, 1.0);
        BLOCK_REWARDS.put(Material.BIRCH_LOG, 1.0);
        BLOCK_REWARDS.put(Material.SPRUCE_LOG, 1.0);
        BLOCK_REWARDS.put(Material.ACACIA_LOG, 1.0);
        BLOCK_REWARDS.put(Material.DARK_OAK_LOG, 1.0);
        BLOCK_REWARDS.put(Material.JUNGLE_LOG, 1.0);
        BLOCK_REWARDS.put(Material.COAL_ORE, 1.0);
        
        // Tier 3: Valuable (2.0 coins) - 1.5x multiplier
        BLOCK_REWARDS.put(Material.COPPER_ORE, 2.0);
        BLOCK_REWARDS.put(Material.IRON_ORE, 2.0);
        BLOCK_REWARDS.put(Material.LAPIS_ORE, 2.0);
        
        // Tier 4: Premium (5.0 coins) - 2.0x multiplier
        BLOCK_REWARDS.put(Material.GOLD_ORE, 5.0);
        BLOCK_REWARDS.put(Material.REDSTONE_ORE, 3.0);
        
        // Tier 5: Rare (10.0 coins) - 2.5x multiplier
        BLOCK_REWARDS.put(Material.DIAMOND_ORE, 10.0);
        BLOCK_REWARDS.put(Material.EMERALD_ORE, 10.0);
    }
    
    public BlockBreakListener(CityBuildPlugin plugin, EconomyManager economyManager, AchievementManager achievementManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.achievementManager = achievementManager;
        this.farmDataManager = plugin.getFarmDataManager();
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            // Validation checks
            if (event.isCancelled()) {
                return;
            }
            
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }
            
            // Check if player is in farm world
            if (!isFarmWorld(player.getWorld().getName())) {
                return;
            }
            
            Block block = event.getBlock();
            if (block == null) {
                return;
            }
            
            // Get base reward for this block
            double baseReward = BLOCK_REWARDS.getOrDefault(block.getType(), 0.0);
            if (baseReward <= 0) {
                return; // Not a reward block
            }
            
            // Check cooldown (anti-spam/anti-cheat)
            String playerUUID = player.getUniqueId().toString();
            long now = System.currentTimeMillis();
            
            if (COOLDOWNS.containsKey(playerUUID)) {
                long lastBreak = COOLDOWNS.get(playerUUID);
                if (now - lastBreak < COOLDOWN_MILLIS) {
                    return; // Too fast, likely cheating
                }
            }
            
            COOLDOWNS.put(playerUUID, now);
            
            // Get or create player data from manager
            PlayerFarmData data = farmDataManager.getPlayerData(player);
            achievementManager.registerPlayerStats(playerUUID, data);
            
            // Check streak status
            checkAndUpdateStreak(playerUUID, data);
            
            // Add block type for combo tracking
            data.addBlockType(block.getType());
            
            // Calculate final reward with all multipliers
            double finalReward = calculateFinalReward(player, baseReward, data, block.getType());
            
            // Award player
            economyManager.addBalance(player, finalReward);
            
            // Update stats
            data.addBlocksMined(1);
            data.addCoinsEarned(finalReward);
            
            // Check achievements
            achievementManager.checkAchievements(playerUUID, data);
            
            // Send dynamic action bar feedback
            sendDynamicFeedback(player, finalReward, data);
            
            // Auto-save player data
            farmDataManager.savePlayerData(player);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error in BlockBreakListener: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks and updates player's mining streak
     */
    private void checkAndUpdateStreak(String playerUUID, PlayerFarmData data) {
        long now = System.currentTimeMillis();
        
        if (STREAK_COOLDOWNS.containsKey(playerUUID)) {
            long lastStreak = STREAK_COOLDOWNS.get(playerUUID);
            if (now - lastStreak > STREAK_TIMEOUT_MS) {
                data.resetStreak(); // Timeout - reset streak
            }
        }
        
        data.addToStreak();
        STREAK_COOLDOWNS.put(playerUUID, now);
    }
    
    /**
     * Calculates final reward with all multipliers:
     * - Enchantment bonus (+10% per enchantment)
     * - Streak bonus (+5% per 10 blocks)
     * - Night multiplier (1.5x at night)
     * - Combo bonus (+20% for 5+ different blocks in 5s)
     * - Ore multiplier (varies by block tier)
     */
    private double calculateFinalReward(Player player, double baseReward, PlayerFarmData data, Material blockType) {
        double reward = baseReward;
        
        // 1️⃣ Enchantment Bonus: +10% per enchantment
        reward = applyEnchantmentBonus(player, reward);
        
        // 2️⃣ Streak Bonus: +5% per 10 blocks
        reward = applyStreakBonus(reward, data);
        
        // 3️⃣ Night Multiplier: 1.5x at night
        reward = applyNightMultiplier(reward);
        
        // 4️⃣ Combo Bonus: +20% for 5+ different blocks in 5s
        reward = applyComboBonus(reward, data);
        
        // 5️⃣ Ore Multiplier: Higher for rare ores
        reward = applyOreMultiplier(reward, blockType);
        
        // 6️⃣ Level Bonus: +2% per level
        reward = applyLevelBonus(reward, data);
        
        return reward;
    }
    
    private double applyEnchantmentBonus(Player player, double reward) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        
        if (tool != null && tool.hasItemMeta() && tool.getItemMeta() != null) {
            int enchantmentCount = tool.getItemMeta().getEnchants().size();
            if (enchantmentCount > 0) {
                double bonus = reward * (0.10 * enchantmentCount);
                return reward + bonus;
            }
        }
        
        return reward;
    }
    
    private double applyStreakBonus(double reward, PlayerFarmData data) {
        if (data.getCurrentStreak() >= 10) {
            int streakTenths = data.getCurrentStreak() / 10;
            double bonusMultiplier = 1.0 + (0.05 * streakTenths);
            return reward * bonusMultiplier;
        }
        return reward;
    }
    
    private double applyNightMultiplier(double reward) {
        if (isNightTime()) {
            return reward * 1.5; // 1.5x multiplier at night
        }
        return reward;
    }
    
    private double applyComboBonus(double reward, PlayerFarmData data) {
        if (data.getComboCount() >= 5) {
            return reward * 1.2; // +20% bonus
        }
        return reward;
    }
    
    private double applyOreMultiplier(double reward, Material blockType) {
        // Higher multiplier for rarer ores
        if (blockType == Material.DIAMOND_ORE || blockType == Material.EMERALD_ORE) {
            return reward * 2.5;
        } else if (blockType == Material.GOLD_ORE) {
            return reward * 2.0;
        } else if (blockType == Material.COPPER_ORE || blockType == Material.IRON_ORE) {
            return reward * 1.5;
        }
        return reward;
    }
    
    private double applyLevelBonus(double reward, PlayerFarmData data) {
        int level = data.getFarmerLevel();
        if (level > 1) {
            double bonusMultiplier = 1.0 + (0.02 * level);
            return reward * bonusMultiplier;
        }
        return reward;
    }
    
    /**
     * Sends dynamic action bar feedback with multiplier info
     */
    private void sendDynamicFeedback(Player player, double reward, PlayerFarmData data) {
        String multipliers = "";
        
        if (data.getCurrentStreak() >= 10) {
            multipliers += "§4🔥" + (data.getCurrentStreak() / 10) + "x§7 ";
        }
        if (isNightTime()) {
            multipliers += "§51.5x§7 ";
        }
        if (data.getComboCount() >= 5) {
            multipliers += "§6🎨§7 ";
        }
        
        String message = "§a+$" + String.format("%.2f", reward);
        if (!multipliers.isEmpty()) {
            message += " §7[" + multipliers + "§7]";
        }
        message += " §8Lvl" + data.getFarmerLevel();
        
        player.sendActionBar(message);
    }
    
    /**
     * Checks if it's night time (13000-23000 ticks)
     */
    private boolean isNightTime() {
        if (plugin.getServer().getWorlds().isEmpty()) return false;
        long time = plugin.getServer().getWorlds().get(0).getTime();
        return time >= 13000 && time <= 23000;
    }
    
    /**
     * Checks if the world is a farm world
     */
    private boolean isFarmWorld(String worldName) {
        if (worldName == null) return false;
        String lower = worldName.toLowerCase();
        return lower.contains(FARM_WORLD_NAME) || lower.contains("farm_");
    }
    
    /**
     * Gets block reward value
     */
    public double getBlockReward(Material material) {
        return BLOCK_REWARDS.getOrDefault(material, 0.0);
    }
}
