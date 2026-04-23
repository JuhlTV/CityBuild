package com.citybuild.core.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dependency Injection Container Tests
 * Tests singleton management and dependency resolution
 */
public class ContainerTest {
    private Container container;
    private Logger logger;
    
    @BeforeEach
    public void setUp() {
        logger = Logger.getLogger("test");
        container = new Container(logger);
    }
    
    @Test
    public void testRegisterSingleton() {
        TestService service = new TestService();
        container.registerSingleton(TestService.class, service);
        
        TestService retrieved = container.get(TestService.class);
        assertSame(service, retrieved, "Should return same instance");
    }
    
    @Test
    public void testSingletonCaching() {
        TestService service = new TestService();
        container.registerSingleton(TestService.class, service);
        
        TestService first = container.get(TestService.class);
        TestService second = container.get(TestService.class);
        
        assertSame(first, second, "Singleton should be cached");
    }
    
    @Test
    public void testRegisterBinding() {
        container.registerBinding(IService.class, TestService.class);
        
        IService service = container.get(IService.class);
        assertNotNull(service, "Should resolve binding");
        assertTrue(service instanceof TestService);
    }
    
    @Test
    public void testGetCount() {
        TestService service = new TestService();
        container.registerSingleton(TestService.class, service);
        
        assertEquals(1, container.getSingletonCount());
        assertEquals(0, container.getBindingCount());
    }
    
    @Test
    public void testClear() {
        TestService service = new TestService();
        container.registerSingleton(TestService.class, service);
        
        assertEquals(1, container.getSingletonCount());
        
        container.clear();
        
        assertEquals(0, container.getSingletonCount());
    }
    
    // Test interfaces and classes
    
    public interface IService {
    }
    
    public static class TestService implements IService {
    }
}
