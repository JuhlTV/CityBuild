package com.citybuild.core.di;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Container - Dependency Injection container
 * Manages object lifecycle and dependency resolution
 * Thread-safe singleton management
 */
public class Container {
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Class<?>, Class<?>> bindings = new ConcurrentHashMap<>();
    private final Logger logger;
    
    public Container(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Register a singleton instance
     * @param interfaceClass Interface or class to bind
     * @param instance Instance to use
     */
    public <T> void registerSingleton(Class<T> interfaceClass, T instance) {
        if (interfaceClass == null || instance == null) {
            throw new IllegalArgumentException("Class and instance cannot be null");
        }
        
        singletons.put(interfaceClass, instance);
        logger.info("Registered singleton: " + interfaceClass.getSimpleName());
    }
    
    /**
     * Register a binding (interface -> implementation class)
     * @param interfaceClass Interface to bind
     * @param implementationClass Implementation class
     */
    public <I, T extends I> void registerBinding(Class<I> interfaceClass, Class<T> implementationClass) {
        if (interfaceClass == null || implementationClass == null) {
            throw new IllegalArgumentException("Classes cannot be null");
        }
        
        bindings.put(interfaceClass, implementationClass);
        logger.info("Registered binding: " + interfaceClass.getSimpleName() + 
                   " → " + implementationClass.getSimpleName());
    }
    
    /**
     * Get or create an instance
     * Singletons are cached, others are created fresh
     * @param type Type to get
     * @return Instance
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        
        // Check if singleton exists
        if (singletons.containsKey(type)) {
            return (T) singletons.get(type);
        }
        
        // Check if binding exists
        if (bindings.containsKey(type)) {
            Class<? extends T> implClass = (Class<? extends T>) bindings.get(type);
            return create(implClass);
        }
        
        // Try to create directly (might be concrete class)
        try {
            return create(type);
        } catch (Exception e) {
            logger.severe("Failed to create instance of " + type.getSimpleName() + ": " + e.getMessage());
            throw new RuntimeException("Cannot resolve dependency: " + type.getName(), e);
        }
    }
    
    /**
     * Create a new instance of a class
     * Attempts to use constructor with most parameters
     */
    @SuppressWarnings("unchecked")
    private <T> T create(Class<T> type) throws Exception {
        // Try to find constructor with parameters
        var constructors = type.getDeclaredConstructors();
        
        if (constructors.length == 0) {
            throw new Exception("No constructors found");
        }
        
        // Use constructor with most parameters
        var targetConstructor = constructors[0];
        for (var constructor : constructors) {
            if (constructor.getParameterCount() > targetConstructor.getParameterCount()) {
                targetConstructor = constructor;
            }
        }
        
        // Get constructor parameters and resolve them
        Class<?>[] paramTypes = targetConstructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = get(paramTypes[i]);
        }
        
        targetConstructor.setAccessible(true);
        return (T) targetConstructor.newInstance(params);
    }
    
    /**
     * Clear all cached singletons
     * Useful for testing or shutdown
     */
    public void clear() {
        singletons.clear();
        bindings.clear();
        logger.info("Container cleared");
    }
    
    /**
     * Get number of registered singletons
     */
    public int getSingletonCount() {
        return singletons.size();
    }
    
    /**
     * Get number of registered bindings
     */
    public int getBindingCount() {
        return bindings.size();
    }
}
