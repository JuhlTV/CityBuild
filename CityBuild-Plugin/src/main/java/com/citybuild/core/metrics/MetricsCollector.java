package com.citybuild.core.metrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * MetricsCollector - Tracks performance metrics
 * Records execution times, cache hits/misses, database queries
 * Thread-safe with atomic counters
 */
public class MetricsCollector {
    private final ConcurrentHashMap<String, OperationMetrics> metrics = new ConcurrentHashMap<>();
    private final Logger logger;
    
    public MetricsCollector(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Record operation execution time
     * @param operationName Name of operation
     * @param executionTimeMs Execution time in milliseconds
     */
    public void recordOperation(String operationName, long executionTimeMs) {
        metrics.computeIfAbsent(operationName, k -> new OperationMetrics(operationName))
            .recordExecution(executionTimeMs);
    }
    
    /**
     * Record cache hit
     */
    public void recordCacheHit(String cacheName) {
        metrics.computeIfAbsent("cache:" + cacheName, k -> new OperationMetrics(cacheName))
            .recordCacheHit();
    }
    
    /**
     * Record cache miss
     */
    public void recordCacheMiss(String cacheName) {
        metrics.computeIfAbsent("cache:" + cacheName, k -> new OperationMetrics(cacheName))
            .recordCacheMiss();
    }
    
    /**
     * Record database query
     */
    public void recordDatabaseQuery(String queryType, long executionTimeMs) {
        metrics.computeIfAbsent("db:" + queryType, k -> new OperationMetrics(queryType))
            .recordExecution(executionTimeMs);
    }
    
    /**
     * Get metrics for operation
     */
    public OperationMetrics getMetrics(String operationName) {
        return metrics.get(operationName);
    }
    
    /**
     * Get all metrics as formatted report
     */
    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Performance Metrics Report ===\n");
        
        metrics.forEach((name, metric) -> {
            sb.append(String.format("\n%s\n", name));
            sb.append(String.format("  Executions: %d\n", metric.getExecutionCount()));
            sb.append(String.format("  Avg Time: %.2f ms\n", metric.getAverageTime()));
            sb.append(String.format("  Min Time: %d ms\n", metric.getMinTime()));
            sb.append(String.format("  Max Time: %d ms\n", metric.getMaxTime()));
            
            if (metric.hasCacheMetrics()) {
                sb.append(String.format("  Cache Hits: %d\n", metric.getCacheHits()));
                sb.append(String.format("  Cache Misses: %d\n", metric.getCacheMisses()));
                sb.append(String.format("  Hit Rate: %.2f%%\n", metric.getHitRate()));
            }
        });
        
        sb.append("\n===================================\n");
        return sb.toString();
    }
    
    /**
     * Clear all metrics
     */
    public void clear() {
        metrics.clear();
        logger.info("✅ Metrics cleared");
    }
    
    /**
     * Reset metrics for specific operation
     */
    public void reset(String operationName) {
        metrics.remove(operationName);
    }
    
    /**
     * Get metric count
     */
    public int getMetricCount() {
        return metrics.size();
    }
    
    /**
     * OperationMetrics - Tracks metrics for a single operation
     */
    public static class OperationMetrics {
        private final String name;
        private final AtomicLong executionCount = new AtomicLong(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private volatile long minTime = Long.MAX_VALUE;
        private volatile long maxTime = 0;
        
        // Cache metrics
        private final AtomicLong cacheHits = new AtomicLong(0);
        private final AtomicLong cacheMisses = new AtomicLong(0);
        
        public OperationMetrics(String name) {
            this.name = name;
        }
        
        public void recordExecution(long executionTimeMs) {
            executionCount.incrementAndGet();
            totalTime.addAndGet(executionTimeMs);
            minTime = Math.min(minTime, executionTimeMs);
            maxTime = Math.max(maxTime, executionTimeMs);
        }
        
        public void recordCacheHit() {
            cacheHits.incrementAndGet();
        }
        
        public void recordCacheMiss() {
            cacheMisses.incrementAndGet();
        }
        
        // Getters
        
        public String getName() { return name; }
        public long getExecutionCount() { return executionCount.get(); }
        public double getAverageTime() {
            long count = executionCount.get();
            return count > 0 ? (double) totalTime.get() / count : 0.0;
        }
        public long getMinTime() { return minTime == Long.MAX_VALUE ? 0 : minTime; }
        public long getMaxTime() { return maxTime; }
        public long getCacheHits() { return cacheHits.get(); }
        public long getCacheMisses() { return cacheMisses.get(); }
        
        public boolean hasCacheMetrics() {
            return cacheHits.get() > 0 || cacheMisses.get() > 0;
        }
        
        public double getHitRate() {
            long total = cacheHits.get() + cacheMisses.get();
            return total > 0 ? (100.0 * cacheHits.get() / total) : 0.0;
        }
    }
}
