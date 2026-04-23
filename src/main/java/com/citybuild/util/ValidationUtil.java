package com.citybuild.util;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Input validation utility for preventing bugs and exploits
 */
public class ValidationUtil {
    
    private static final Pattern VALID_PLOT_ID = Pattern.compile("^plot_\\d+$");
    private static final Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    
    /**
     * Safely parse UUID with fallback
     */
    public static UUID parseUUID(String uuidString, UUID fallback) {
        if (uuidString == null || uuidString.isEmpty()) {
            return fallback;
        }
        
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
    
    /**
     * Safely parse double with fallback
     */
    public static double parseDouble(Object value, double fallback) {
        if (value == null) {
            return fallback;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (NumberFormatException e) {
            return fallback;
        }
        
        return fallback;
    }
    
    /**
     * Safely parse integer with fallback
     */
    public static int parseInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
        } catch (NumberFormatException e) {
            return fallback;
        }
        
        return fallback;
    }
    
    /**
     * Validate plot ID format
     */
    public static boolean isValidPlotId(String plotId) {
        return plotId != null && VALID_PLOT_ID.matcher(plotId).matches();
    }
    
    /**
     * Validate coordinate bounds
     */
    public static boolean isValidCoordinate(int x, int y, int z, int minY, int maxY) {
        return y >= minY && y <= maxY;
    }
    
    /**
     * Validate price is positive
     */
    public static boolean isValidPrice(double price) {
        return price > 0 && !Double.isInfinite(price) && !Double.isNaN(price);
    }
    
    /**
     * Validate username format
     */
    public static boolean isValidUsername(String username) {
        return username != null && VALID_USERNAME.matcher(username).matches();
    }
    
    /**
     * Safely cast to Integer
     */
    public static int safeIntegerCast(Object obj, int fallback) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            long val = (Long) obj;
            if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                return fallback;
            }
            return (int) val;
        }
        return fallback;
    }
    
    /**
     * Safely cast to Long
     */
    public static long safeLongCast(Object obj, long fallback) {
        if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        return fallback;
    }
    
    /**
     * Safely cast to Boolean
     */
    public static boolean safeBooleanCast(Object obj, boolean fallback) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof String) {
            String str = (String) obj;
            return "true".equalsIgnoreCase(str) || "1".equals(str);
        }
        return fallback;
    }
    
    /**
     * Validate string is not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Clamp value between min and max
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamp double between min and max
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
