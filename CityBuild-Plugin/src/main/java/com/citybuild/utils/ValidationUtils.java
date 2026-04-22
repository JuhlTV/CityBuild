package com.citybuild.utils;

import java.util.logging.Logger;

/**
 * Utility class for common validation, error handling, and safety checks
 * Used across all manager classes for consistent behavior
 */
public class ValidationUtils {
    private static final Logger logger = Logger.getLogger("CityBuild");

    /**
     * Validates UUID format
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        // UUID format: 8-4-4-4-12 hex digits
        return uuid.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    /**
     * Validates that amount is non-negative
     */
    public static boolean isValidAmount(long amount) {
        return amount >= 0;
    }

    /**
     * Validates that amount is positive (> 0)
     */
    public static boolean isPositiveAmount(long amount) {
        return amount > 0;
    }

    /**
     * Validates player UUID and logs warning if invalid
     */
    public static boolean validatePlayerUUID(String uuid, String context) {
        if (uuid == null || uuid.isEmpty()) {
            logger.warning("[" + context + "] Invalid UUID: null or empty");
            return false;
        }
        if (!isValidUUID(uuid)) {
            logger.warning("[" + context + "] Invalid UUID format: " + uuid);
            return false;
        }
        return true;
    }

    /**
     * Validates amount and logs warning if invalid
     */
    public static boolean validateAmount(long amount, String context) {
        if (amount < 0) {
            logger.warning("[" + context + "] Invalid amount: negative value " + amount);
            return false;
        }
        return true;
    }

    /**
     * Validates amount is positive
     */
    public static boolean validatePositiveAmount(long amount, String context) {
        if (amount <= 0) {
            logger.warning("[" + context + "] Invalid amount: non-positive value " + amount);
            return false;
        }
        return true;
    }

    /**
     * Safe string check
     */
    public static boolean isValidString(String str, int minLength) {
        return str != null && str.length() >= minLength && !str.trim().isEmpty();
    }

    /**
     * Clamp value between min and max
     */
    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamp value between min and max for double
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Check if file operation was successful
     */
    public static void logFileOperation(String operation, String file, boolean success) {
        if (success) {
            logger.fine("[" + operation + "] Successfully processed: " + file);
        } else {
            logger.severe("[" + operation + "] Failed to process: " + file);
        }
    }

    /**
     * Safe exception logging
     */
    public static void logException(Exception e, String context) {
        logger.severe("[" + context + "] Exception: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Ensure non-negative value
     */
    public static long ensureNonNegative(long value) {
        return Math.max(0, value);
    }

    /**
     * Ensure positive value (minimum 1)
     */
    public static long ensurePositive(long value) {
        return Math.max(1, value);
    }

    /**
     * Safe division with fallback
     */
    public static long safeDivide(long dividend, long divisor, long fallback) {
        if (divisor == 0) {
            logger.warning("Division by zero prevented, using fallback: " + fallback);
            return fallback;
        }
        return dividend / divisor;
    }
}
