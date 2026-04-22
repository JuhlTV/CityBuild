package com.citybuild.managers;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    
    private static class CacheEntry<T> {
        private final T value;
        private final long createdAt;
        private final long ttlMs; // Time to live in milliseconds

        public CacheEntry(T value, long ttlMs) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
            this.ttlMs = ttlMs;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createdAt > ttlMs;
        }
    }

    private final Map<String, CacheEntry<?>> cache = new HashMap<>();
    
    // ===== CACHE OPERATIONS =====
    public <T> void put(String key, T value) {
        put(key, value, 5 * 60 * 1000); // Default 5 minutes
    }

    public <T> void put(String key, T value, long ttlMs) {
        cache.put(key, new CacheEntry<>(value, ttlMs));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        CacheEntry<?> entry = cache.get(key);
        
        if (entry == null) return null;
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }

        return (T) entry.value;
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void invalidatePrefix(String prefix) {
        cache.keySet().removeIf(key -> key.startsWith(prefix));
    }

    public void clearAll() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    // ===== UTILITY CACHE KEYS =====
    public static class CacheKeys {
        public static String playerBalance(String uuid) {
            return "balance:" + uuid;
        }

        public static String playerPlots(String uuid) {
            return "plots:" + uuid;
        }

        public static String plotData(int plotId) {
            return "plot:" + plotId;
        }

        public static String leaderboard() {
            return "leaderboard:top10";
        }

        public static String playerRole(String uuid) {
            return "role:" + uuid;
        }

        public static String clanData(String clanName) {
            return "clan:" + clanName;
        }
    }
}
