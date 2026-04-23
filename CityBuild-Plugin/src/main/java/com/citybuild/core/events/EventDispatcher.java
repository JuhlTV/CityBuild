package com.citybuild.core.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import com.citybuild.CityBuildPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Event Dispatcher - Centralized event management
 * Allows plugins to hook into CityBuild events
 */
public class EventDispatcher {
    private final PluginManager pluginManager;
    private final Logger logger;
    private final CityBuildPlugin plugin;
    
    public EventDispatcher(CityBuildPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();
        this.logger = plugin.getLogger();
    }
    
    /**
     * Dispatch an event
     * @param event Event to dispatch
     */
    public void dispatch(Event event) {
        if (event == null) {
            logger.warning("Cannot dispatch null event!");
            return;
        }
        
        try {
            pluginManager.callEvent(event);
            logger.fine("Dispatched event: " + event.getClass().getSimpleName());
        } catch (Exception e) {
            logger.severe("Error dispatching event: " + event.getClass().getName());
            e.printStackTrace();
        }
    }
    
    /**
     * Register an event listener
     * @param listener Listener to register
     */
    public void registerListener(Listener listener) {
        if (listener == null) {
            logger.warning("Cannot register null listener!");
            return;
        }
        
        pluginManager.registerEvents(listener, plugin);
        logger.info("Registered listener: " + listener.getClass().getSimpleName());
    }
    
    /**
     * Call an event and wait for result
     * Used for events that need immediate feedback
     * @param event Event to call
     * @return The event (may be modified by listeners)
     */
    public <T extends Event> T call(T event) {
        dispatch(event);
        return event;
    }
}
