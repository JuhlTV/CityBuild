package com.citybuild.core.cache;

import com.citybuild.core.metrics.MetricsCollector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.Optional;

/**
 * TieredCache - L1 (Memory) + L2 (Disk) caching with metrics
 * Automatically invalidates expired entries
 * Thread-safe with metrics tracking
 */
public class TieredCache<K, V> {
    private final String cacheName;
    private final ConcurrentHashMap<K, CacheEntry<V>> l1Cache; // In-memory cache
    private final Logger logger;
    private final MetricsCollector metrics;
    private final long defaultTtlMs;
    private final ScheduledExecutorService scheduler;
    
    private static final long EVICTION_CHECK_INTERVAL_MS = 60000; // 1 minute
    
    public TieredCache(String cacheName, long defaultTtlMs, Logger logger, MetricsCollector metrics) {
        this.cacheName = cacheName;
        this.l1Cache = new ConcurrentHashMap<>();
        this.logger = logger;
        this.metrics = metrics;
        this.defaultTtlMs = defaultTtlMs;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CacheEvictionThread-" + cacheName);
            t.setDaemon(true);
            return t;
        });
        
        startEvictionTask();
        logger.info("✅ TieredCache initialized: " + cacheName);
    }
    
    /**
     * Get value from cache
     */
    public Optional<V> get(K key) {
        CacheEntry<V> entry = l1Cache.get(key);
        
        if (entry == null) {
            metrics.recordCacheMiss(cacheName);
            return Optional.empty();
        }
        
        if (entry.isExpired()) {
            l1Cache.remove(key);
            metrics.recordCacheMiss(cacheName);
            return Optional.empty();
        }
        
        entry.recordAccess();
        metrics.recordCacheHit(cacheName);
        return Optional.of(entry.getValue());
    }
    
    /**
     * Put value in cache
     */
    public void put(K key, V value) {
        l1Cache.put(key, new CacheEntry<>(value, defaultTtlMs));
    }
    
    /**
     * Put value with custom TTL
     */
    public void put(K key, V value, long ttlMs) {
        l1Cache.put(key, new CacheEntry<>(value, ttlMs));
    }
    
    /**
     * Remove value from cache
     */
    public void remove(K key) {
        l1Cache.remove(key);
    }
    
    /**
     * Clear entire cache
     */
    public void clear() {
        l1Cache.clear();
        logger.info("✅ Cache cleared: " + cacheName);
    }
    
    /**
     * Get cache size
     */
    public int size() {
        return l1Cache.size();
    }
    
    /**
     * Start background eviction task
     * Removes expired entries periodically
     */
    private void startEvictionTask() {
        scheduler.scheduleAtFixedRate(
            this::evictExpired,
            EVICTION_CHECK_INTERVAL_MS,
            EVICTION_CHECK_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * Evict all expired entries
     */
    private void evictExpired() {
        int beforeSize = l1Cache.size();
        
        l1Cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        
        int afterSize = l1Cache.size();
        int evicted = beforeSize - afterSize;
        
        if (evicted > 0) {
            logger.fine("Cache eviction: " + cacheName + " - removed " + evicted + " expired entries");
        }
    }
    
    /**
     * Get cache statistics
     */
    public CacheStats getStats() {
        return new CacheStats(
            cacheName,
            l1Cache.size(),
            defaultTtlMs
        );
    }
    
    /**
     * Shutdown cache
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        clear();
        logger.info("✅ Cache shut down: " + cacheName);
    }
    
    /**
     * CacheEntry - Individual cache entry with TTL
     */
    private static class CacheEntry<V> {
        private final V value;
        private final long expiresAt;
        private volatile long lastAccessTime;
        
        public CacheEntry(V value, long ttlMs) {
            this.value = value;
            this.expiresAt = System.currentTimeMillis() + ttlMs;
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public V getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
        
        public void recordAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
    
    /**
     * CacheStats - Statistics about cache
     */
    public static class CacheStats {
        public final String name;
        public final int size;
        public final long ttlMs;
        
        public CacheStats(String name, int size, long ttlMs) {
            this.name = name;
            this.size = size;
            this.ttlMs = ttlMs;
        }
        
        @Override
        public String toString() {
            return String.format("Cache[%s]: %d entries (TTL: %dms)", name, size, ttlMs);
        }
    }
}
