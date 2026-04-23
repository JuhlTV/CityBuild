package com.citybuild.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Constants and configuration values for the CityBuild plugin
 * Eliminates magic numbers and centralizes configuration
 */
public class CityBuildConstants {
    
    // ========== PLOT SYSTEM ==========
    public static final int PLOT_SIZE = 100;
    public static final int PLOT_GRID_SIZE = 10;
    public static final int TOTAL_PLOTS = PLOT_GRID_SIZE * PLOT_GRID_SIZE;
    public static final int PLOT_Y = -60;
    public static final double PLOT_BASE_PRICE = 1000.0;
    public static final double PLOT_REFUND_PERCENTAGE = 0.8;
    
    // ========== ECONOMY SYSTEM ==========
    public static final double STARTING_BALANCE = 1000.0;
    public static final double TRANSACTION_COOLDOWN_MS = 100;
    
    // ========== TRADING SYSTEM ==========
    public static final long TRADE_EXPIRY_MINUTES = 5;
    public static final long TRADE_CLEANUP_INTERVAL_TICKS = 600; // 30 seconds
    
    // ========== AUCTION SYSTEM ==========
    public static final long AUCTION_EXPIRY_HOURS = 24;
    public static final long AUCTION_CLEANUP_INTERVAL_TICKS = 1200; // 60 seconds
    
    // ========== GUI SYSTEM ==========
    public static final int GUI_SIZE_27 = 27;
    public static final int GUI_SIZE_45 = 45;
    public static final int GUI_SIZE_54 = 54;
    
    // ========== INVENTORY SLOTS ==========
    public static final int SLOT_BACK = 26;
    public static final int SLOT_PREVIOUS = 45;
    public static final int SLOT_NEXT = 53;
    public static final int SLOT_CONFIRM = 31;
    public static final int SLOT_CANCEL = 33;
    
    // ========== QUEST SYSTEM ==========
    public static final long DAILY_QUEST_RESET_HOURS = 24;
    public static final long WEEKLY_QUEST_RESET_HOURS = 168;
    
    // ========== ACHIEVEMENT SYSTEM ==========
    public static final int MAX_ACHIEVEMENTS = 16;
    public static final int ACHIEVEMENT_POINTS_MULTIPLIER = 100;
    
    // ========== LEADERBOARD SYSTEM ==========
    public static final int LEADERBOARD_TOP_LIMIT = 10;
    public static final int LEADERBOARD_MAX_DISPLAY = 50;
    
    // ========== BIOME SYSTEM ==========
    public static final int BIOME_MIN_RADIUS = 10;
    public static final int BIOME_MAX_RADIUS = 100;
    
    // ========== AUTO-SAVE INTERVALS ==========
    public static final long AUTO_SAVE_INTERVAL_TICKS = 6000; // 5 minutes
    
    // ========== VALIDATION RULES ==========
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 16;
    public static final double MIN_PRICE = 0.01;
    public static final double MAX_PRICE = 999999999.0;
    
    // ========== COLOR CODES ==========
    public static final String COLOR_SUCCESS = "§a";
    public static final String COLOR_ERROR = "§c";
    public static final String COLOR_WARNING = "§e";
    public static final String COLOR_INFO = "§b";
    public static final String COLOR_HEADER = "§6";
    public static final String COLOR_SECONDARY = "§7";
    
    // ========== MESSAGE PREFIXES ==========
    public static final String PREFIX_SUCCESS = COLOR_SUCCESS + "✓ ";
    public static final String PREFIX_ERROR = COLOR_ERROR + "✗ ";
    public static final String PREFIX_INFO = COLOR_INFO + "ℹ ";
    
    /**
     * Get list of all slot positions for grid layout
     */
    public static List<Integer> getGridSlots(int gridRows, int gridCols) {
        List<Integer> slots = new ArrayList<>();
        int startSlot = 10;
        
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                slots.add(startSlot + (row * 9) + col);
            }
        }
        
        return slots;
    }
    
    private CityBuildConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
