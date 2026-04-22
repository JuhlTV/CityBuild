package com.citybuild.listeners;

import com.citybuild.CityBuildPlugin;
import com.citybuild.managers.EconomyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class RewardListener implements Listener {
    private final CityBuildPlugin plugin;
    private final EconomyManager economy;
    private final World farmWorld;
    private final World pvpWorld;

    // Reward rates
    private final long FARM_BLOCK_REWARD = 10; // $ per block mined
    private final long PVP_KILL_REWARD = 50; // $ per monster killed

    public RewardListener(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomyManager();
        this.farmWorld = plugin.getWorldManager().getFarmWorld();
        this.pvpWorld = plugin.getWorldManager().getPvpWorld();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Only reward in farm world
        if (player.getWorld() != farmWorld) {
            return;
        }

        // Don't reward on certain blocks
        switch (event.getBlock().getType()) {
            case BEDROCK:
            case END_PORTAL_FRAME:
            case COMMAND_BLOCK:
                return;
        }

        // Award money for mining
        economy.addBalance(player, FARM_BLOCK_REWARD);
        player.sendActionBar(Component.text("+$" + FARM_BLOCK_REWARD, NamedTextColor.GREEN));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Only reward for monsters killed by players in PvP world
        if (!(event.getEntity() instanceof Monster) || event.getEntity().getWorld() != pvpWorld) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        // Award money for killing monster
        long reward = PVP_KILL_REWARD;
        economy.addBalance(killer, reward);
        killer.sendActionBar(Component.text("+$" + reward, NamedTextColor.RED));
    }
}
