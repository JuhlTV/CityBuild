package com.citybuild.core.monitor;

import com.citybuild.core.metrics.MetricsCollector;
import com.citybuild.core.cache.TieredCache;
import com.citybuild.core.async.AsyncExecutor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * PerformanceMonitor - Integrates metrics, caching, and async execution
 * Provides unified performance tracking and optimization
 */
public class PerformanceMonitor {
    private final MetricsCollector metrics;
    private final AsyncExecutor asyncExecutor;
    private final Logger logger;
    
    public PerformanceMonitor(Logger logger) {
        this.logger = logger;
        this.metrics = new MetricsCollector(logger);
        this.asyncExecutor = new AsyncExecutor(logger, 8); // 8 threads
        
        logger.info("✅ PerformanceMonitor initialized");
    }
    
    /**
     * Monitor synchronous operation execution time
     */
    public <T> T monitorSync(String operationName, Operation<T> operation) throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            T result = operation.execute();
            long executionTime = System.currentTimeMillis() - startTime;
            metrics.recordOperation(operationName, executionTime);
            
            if (executionTime > 100) {
                logger.warning("⚠️ Slow operation: " + operationName + " took " + executionTime + "ms");
            }
            
            return result;
        } catch (Exception e) {
            logger.severe("❌ Operation failed: " + operationName);
            throw e;
        }
    }
    
    /**
     * Monitor asynchronous operation execution time
     */
    public <T> CompletableFuture<T> monitorAsync(String operationName, AsyncOperation<T> operation) {
        long startTime = System.currentTimeMillis();
        
        return asyncExecutor.executeAsync(() -> {
            try {
                T result = operation.execute();
                long executionTime = System.currentTimeMillis() - startTime;
                metrics.recordOperation(operationName, executionTime);
                
                if (executionTime > 100) {
                    logger.warning("⚠️ Slow async operation: " + operationName + " took " + executionTime + "ms");
                }
                
                return result;
            } catch (Exception e) {
                logger.severe("❌ Async operation failed: " + operationName);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Monitor database query execution time
     */
    public <T> T monitorDatabaseQuery(String queryType, Operation<T> operation) throws Exception {
        long startTime = System.currentTimeMillis();
        
        try {
            T result = operation.execute();
            long executionTime = System.currentTimeMillis() - startTime;
            metrics.recordDatabaseQuery(queryType, executionTime);
            
            if (executionTime > 500) {
                logger.warning("⚠️ Slow database query: " + queryType + " took " + executionTime + "ms");
            }
            
            return result;
        } catch (Exception e) {
            logger.severe("❌ Database query failed: " + queryType);
            throw e;
        }
    }
    
    /**
     * Monitor async database query
     */
    public <T> CompletableFuture<T> monitorDatabaseQueryAsync(String queryType, AsyncOperation<T> operation) {
        long startTime = System.currentTimeMillis();
        
        return asyncExecutor.executeAsync(() -> {
            try {
                T result = operation.execute();
                long executionTime = System.currentTimeMillis() - startTime;
                metrics.recordDatabaseQuery(queryType, executionTime);
                
                if (executionTime > 500) {
                    logger.warning("⚠️ Slow async database query: " + queryType + " took " + executionTime + "ms");
                }
                
                return result;
            } catch (Exception e) {
                logger.severe("❌ Async database query failed: " + queryType);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Create monitored cache
     */
    public <K, V> TieredCache<K, V> createCache(String cacheName, long ttlMs) {
        return new TieredCache<>(cacheName, ttlMs, logger, metrics);
    }
    
    /**
     * Get metrics collector
     */
    public MetricsCollector getMetrics() {
        return metrics;
    }
    
    /**
     * Get async executor
     */
    public AsyncExecutor getAsyncExecutor() {
        return asyncExecutor;
    }
    
    /**
     * Get performance report
     */
    public String getReport() {
        return metrics.getReport();
    }
    
    /**
     * Shutdown monitor
     */
    public void shutdown() {
        asyncExecutor.shutdown();
        logger.info("✅ PerformanceMonitor shut down");
    }
    
    /**
     * Synchronous operation interface
     */
    public interface Operation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Asynchronous operation interface
     */
    public interface AsyncOperation<T> {
        T execute() throws Exception;
    }
}
