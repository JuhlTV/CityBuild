package com.citybuild.managers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnchantingManager {
    private final JavaPlugin plugin;
    private final EconomyManager economy;
    private final Random random = new Random();

    public enum EnchantTier {
        BASIC("Basic", 1000, new String[]{"PROTECTION", "SHARPNESS", "EFFICIENCY"}),
        ADVANCED("Advanced", 5000, new String[]{"BLAST_PROTECTION", "FIRE_ASPECT", "UNBREAKING"}),
        LEGENDARY("Legendary", 25000, new String[]{"THORNS", "KNOCKBACK", "LOOTING"});

        public final String name;
        public final long cost;
        public final String[] enchantments;

        EnchantTier(String name, long cost, String[] enchantments) {
            this.name = name;
            this.cost = cost;
            this.enchantments = enchantments;
        }
    }

    public EnchantingManager(JavaPlugin plugin, EconomyManager economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    /**
     * Enchant an item with random enchantment from tier
     */
    public boolean enchantItem(String playerUuid, ItemStack item, EnchantTier tier) {
        // Check balance
        if (!economy.canAfford(playerUuid, tier.cost)) {
            return false;
        }

        // Deduct cost
        economy.removeBalance(playerUuid, tier.cost);

        // Apply random enchantment
        String enchantName = tier.enchantments[random.nextInt(tier.enchantments.length)];
        try {
            Enchantment enchantment = Enchantment.getByName(enchantName);
            if (enchantment != null) {
                int level = tier == EnchantTier.BASIC ? 1 : tier == EnchantTier.ADVANCED ? 2 : 3;
                item.addEnchantment(enchantment, level);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to apply enchantment: " + e.getMessage());
        }

        return true;
    }

    /**
     * Get cost for a tier
     */
    public long getCost(EnchantTier tier) {
        return tier.cost;
    }

    /**
     * Get random enchantment from tier
     */
    public String getRandomEnchantment(EnchantTier tier) {
        return tier.enchantments[random.nextInt(tier.enchantments.length)];
    }
}
