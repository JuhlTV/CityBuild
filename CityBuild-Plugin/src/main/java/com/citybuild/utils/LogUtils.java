package com.citybuild.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Utility class for consistent logging across the CityBuild plugin
 * Provides formatted log messages with context and severity levels
 */
public class LogUtils {
    private static final Logger logger = Logger.getLogger("CityBuild");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String PREFIX = "§b[CityBuild]§r ";

    // Log levels
    public enum LogLevel {
        DEBUG("§7DEBUG"),
        INFO("§aINFO"),
        WARNING("§eWARN"),
        ERROR("§cERROR"),
        CRITICAL("§4CRITICAL");

        public final String color;

        LogLevel(String color) {
            this.color = color;
        }
    }

    /**
     * Log debug message (development/detailed info)
     */
    public static void debug(String context, String message) {
        if (isDebugEnabled()) {
            String formatted = String.format("[%s] [DEBUG] <%s> %s", getTime(), context, message);
            logger.fine(formatted);
        }
    }

    /**
     * Log info message
     */
    public static void info(String context, String message) {
        String formatted = String.format("[%s] [INFO] <%s> %s", getTime(), context, message);
        logger.info(formatted);
    }

    /**
     * Log warning message
     */
    public static void warning(String context, String message) {
        String formatted = String.format("[%s] [WARN] <%s> %s", getTime(), context, message);
        logger.warning(formatted);
    }

    /**
     * Log error message
     */
    public static void error(String context, String message) {
        String formatted = String.format("[%s] [ERROR] <%s> %s", getTime(), context, message);
        logger.severe(formatted);
    }

    /**
     * Log error with exception
     */
    public static void error(String context, String message, Throwable throwable) {
        String formatted = String.format("[%s] [ERROR] <%s> %s - %s", getTime(), context, message, throwable.getMessage());
        logger.severe(formatted);
        if (isDebugEnabled()) {
            throwable.printStackTrace();
        }
    }

    /**
     * Log critical error (system-affecting)
     */
    public static void critical(String context, String message) {
        String formatted = String.format("[%s] [CRITICAL] <%s> %s", getTime(), context, message);
        logger.severe("§4" + formatted);
    }

    /**
     * Log player action
     */
    public static void logPlayerAction(String playerName, String action, String details) {
        String formatted = String.format("[%s] [ACTION] Player: %s | Action: %s | Details: %s", 
            getTime(), playerName, action, details);
        logger.info(formatted);
    }

    /**
     * Log economy transaction
     */
    public static void logTransaction(String playerName, String type, long amount) {
        String formatted = String.format("[%s] [ECONOMY] Player: %s | Type: %s | Amount: $%d", 
            getTime(), playerName, type, amount);
        logger.info(formatted);
    }

    /**
     * Log admin action
     */
    public static void logAdminAction(String adminName, String action, String target, String reason) {
        String formatted = String.format("[%s] [ADMIN] Admin: %s | Action: %s | Target: %s | Reason: %s", 
            getTime(), adminName, action, target, reason);
        logger.warning(formatted);
    }

    /**
     * Log system event
     */
    public static void logSystemEvent(String event, String details) {
        String formatted = String.format("[%s] [SYSTEM] Event: %s | Details: %s", 
            getTime(), event, details);
        logger.info(formatted);
    }

    /**
     * Log performance metric
     */
    public static void logPerformance(String operation, long durationMs) {
        if (durationMs > 100) { // Log slow operations
            String formatted = String.format("[%s] [PERF] Operation: %s | Duration: %dms", 
                getTime(), operation, durationMs);
            logger.warning(formatted);
        } else if (isDebugEnabled()) {
            String formatted = String.format("[%s] [PERF] Operation: %s | Duration: %dms", 
                getTime(), operation, durationMs);
            logger.fine(formatted);
        }
    }

    /**
     * Get current time in HH:mm:ss format
     */
    private static String getTime() {
        return LocalDateTime.now().format(formatter);
    }

    /**
     * Check if debug logging is enabled
     */
    private static boolean isDebugEnabled() {
        // Can be configured via system property: -Dcitybuild.debug=true
        return System.getProperty("citybuild.debug", "false").equals("true");
    }

    /**
     * Log a separator for readability
     */
    public static void logSeparator() {
        logger.info("════════════════════════════════════════");
    }

    /**
     * Log startup message
     */
    public static void logStartup(String feature) {
        logger.info(PREFIX + "§aStarting: " + feature);
    }

    /**
     * Log shutdown message
     */
    public static void logShutdown(String feature) {
        logger.info(PREFIX + "§eStopping: " + feature);
    }

    /**
     * Log initialization complete
     */
    public static void logInitComplete(String feature, int count) {
        logger.info(PREFIX + "§aInitialized " + feature + " with " + count + " entries");
    }
}
