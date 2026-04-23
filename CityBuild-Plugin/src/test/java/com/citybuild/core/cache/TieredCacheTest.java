package com.citybuild.core.cache;

import com.citybuild.core.metrics.MetricsCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TieredCache Unit Tests
 */
public class TieredCacheTest {
    private TieredCache<String, String> cache;
    private MetricsCollector metrics;
    
    @BeforeEach
    public void setUp() {
        metrics = new MetricsCollector(Logger.getLogger("test"));
        cache = new TieredCache<>("test_cache", 60000, Logger.getLogger("test"), metrics);
    }
    
    @Test
    public void testPutAndGet() {
        cache.put("key1", "value1");
        
        var result = cache.get("key1");
        assertTrue(result.isPresent());
        assertEquals("value1", result.get());
    }
    
    @Test
    public void testCacheMiss() {
        var result = cache.get("nonexistent");
        
        assertFalse(result.isPresent());
    }
    
    @Test
    public void testCacheSize() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        
        assertEquals(3, cache.size());
    }
    
    @Test
    public void testRemove() {
        cache.put("key1", "value1");
        assertEquals(1, cache.size());
        
        cache.remove("key1");
        assertEquals(0, cache.size());
        
        assertFalse(cache.get("key1").isPresent());
    }
    
    @Test
    public void testClear() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        
        cache.clear();
        
        assertEquals(0, cache.size());
    }
    
    @Test
    public void testCustomTtl() throws InterruptedException {
        cache.put("key1", "value1", 100); // 100ms TTL
        
        assertTrue(cache.get("key1").isPresent());
        
        Thread.sleep(150);
        
        assertFalse(cache.get("key1").isPresent());
    }
    
    @Test
    public void testGetStats() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        
        TieredCache.CacheStats stats = cache.getStats();
        assertEquals("test_cache", stats.name);
        assertEquals(2, stats.size);
        assertEquals(60000, stats.ttlMs);
    }
}
