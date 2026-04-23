package com.citybuild.core.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MetricsCollector Unit Tests
 */
public class MetricsCollectorTest {
    private MetricsCollector collector;
    
    @BeforeEach
    public void setUp() {
        collector = new MetricsCollector(Logger.getLogger("test"));
    }
    
    @Test
    public void testRecordOperation() {
        collector.recordOperation("test_op", 100);
        
        MetricsCollector.OperationMetrics metrics = collector.getMetrics("test_op");
        assertNotNull(metrics);
        assertEquals(1, metrics.getExecutionCount());
        assertEquals(100, metrics.getMaxTime());
    }
    
    @Test
    public void testAverageTime() {
        collector.recordOperation("test_op", 100);
        collector.recordOperation("test_op", 200);
        collector.recordOperation("test_op", 300);
        
        MetricsCollector.OperationMetrics metrics = collector.getMetrics("test_op");
        assertEquals(3, metrics.getExecutionCount());
        assertEquals(200.0, metrics.getAverageTime());
    }
    
    @Test
    public void testCacheMetrics() {
        collector.recordCacheHit("cache1");
        collector.recordCacheHit("cache1");
        collector.recordCacheMiss("cache1");
        
        MetricsCollector.OperationMetrics metrics = collector.getMetrics("cache:cache1");
        assertEquals(2, metrics.getCacheHits());
        assertEquals(1, metrics.getCacheMisses());
        assertEquals(66.67, metrics.getHitRate(), 0.1);
    }
    
    @Test
    public void testMinMaxTime() {
        collector.recordOperation("test_op", 50);
        collector.recordOperation("test_op", 100);
        collector.recordOperation("test_op", 25);
        
        MetricsCollector.OperationMetrics metrics = collector.getMetrics("test_op");
        assertEquals(25, metrics.getMinTime());
        assertEquals(100, metrics.getMaxTime());
    }
    
    @Test
    public void testGetReport() {
        collector.recordOperation("op1", 100);
        collector.recordOperation("op2", 200);
        
        String report = collector.getReport();
        assertTrue(report.contains("op1"));
        assertTrue(report.contains("op2"));
        assertTrue(report.contains("Executions"));
    }
    
    @Test
    public void testClear() {
        collector.recordOperation("op1", 100);
        assertEquals(1, collector.getMetricCount());
        
        collector.clear();
        assertEquals(0, collector.getMetricCount());
    }
}
