package com.citybuild.core.async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AsyncExecutor Unit Tests
 */
public class AsyncExecutorTest {
    private AsyncExecutor executor;
    
    @BeforeEach
    public void setUp() {
        executor = new AsyncExecutor(Logger.getLogger("test"), 4);
    }
    
    @Test
    public void testExecuteAsync() throws Exception {
        var future = executor.executeAsync(() -> "Hello");
        
        String result = future.get();
        assertEquals("Hello", result);
    }
    
    @Test
    public void testExecuteAsyncRunnable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        executor.executeAsync(latch::countDown).get();
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
    
    @Test
    public void testExecuteParallel() throws Exception {
        List<String> results = new ArrayList<>();
        List<java.util.concurrent.Callable<String>> tasks = new ArrayList<>();
        
        tasks.add(() -> { Thread.sleep(100); return "task1"; });
        tasks.add(() -> { Thread.sleep(100); return "task2"; });
        tasks.add(() -> { Thread.sleep(100); return "task3"; });
        
        long startTime = System.currentTimeMillis();
        executor.executeParallel(tasks).get();
        long duration = System.currentTimeMillis() - startTime;
        
        // Should complete in roughly 100ms (parallel), not 300ms (sequential)
        assertTrue(duration < 500, "Parallel execution should be faster");
    }
    
    @Test
    public void testChainOperations() throws Exception {
        var future1 = executor.executeAsync(() -> 5);
        var future2 = executor.chain(future1, result -> 
            executor.executeAsync(() -> result * 2)
        );
        
        Integer result = future2.get();
        assertEquals(10, result);
    }
    
    @Test
    public void testGetThreadPoolSize() {
        assertEquals(4, executor.getThreadPoolSize());
    }
}
