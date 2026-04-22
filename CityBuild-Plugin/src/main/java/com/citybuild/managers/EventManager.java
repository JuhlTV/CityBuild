package com.citybuild.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Random;

public class EventManager {
    private final JavaPlugin plugin;
    private final EconomyManager economy;
    private final Random random = new Random();

    public enum Event {
        LUCKY_JACKPOT("Lucky Jackpot!", 1000, "🍀"),
        DOUBLE_REWARDS("Double Rewards!", 2.0, "💰"),
        STORM_BONUS("Storm Warning!", 1.5, "⛈️");

        public final String name;
        public final Object value;
        public final String emoji;

        Event(String name, Object value, String emoji) {
            this.name = name;
            this.value = value;
            this.emoji = emoji;
        }
    }

    public EventManager(JavaPlugin plugin, EconomyManager economy) {
        this.plugin = plugin;
        this.economy = economy;
        startRandomEvents();
    }

    /**
     * Start random events every 30 minutes
     */
    private void startRandomEvents() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::triggerRandomEvent,
            20L * 60 * 30, // 30 minutes
            20L * 60 * 30);
    }

    /**
     * Trigger a random event
     */
    private void triggerRandomEvent() {
        Event event = Event.values()[random.nextInt(Event.values().length)];
        broadcastEvent(event);

        if (event == Event.LUCKY_JACKPOT) {
            // Give random player $1000
            Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
            if (players.length > 0) {
                Player lucky = players[random.nextInt(players.length)];
                economy.addBalance(lucky, 1000);
                lucky.sendMessage(Component.text("🎉 YOU WON THE LUCKY JACKPOT! +$1000!", NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD));
            }
        }
    }

    /**
     * Broadcast event to all players
     */
    private void broadcastEvent(Event event) {
        Component message = Component.text(event.emoji + " " + event.name + " " + event.emoji, NamedTextColor.YELLOW)
            .decorate(TextDecoration.BOLD);
        Bukkit.broadcast(message);
    }
}
