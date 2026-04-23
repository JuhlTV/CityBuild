package com.citybuild.core.services;

/**
 * IService - Base interface for all service classes
 * Defines common contract for all services
 */
public interface IService {
    /**
     * Initialize the service
     * Called after dependency injection
     */
    default void initialize() {
        // Optional implementation
    }
    
    /**
     * Shutdown the service
     * Called on plugin disable
     */
    default void shutdown() {
        // Optional implementation
    }
    
    /**
     * Get service name
     * @return Human-readable service name
     */
    String getServiceName();
    
    /**
     * Check if service is ready
     * @return true if service is initialized and ready
     */
    default boolean isReady() {
        return true;
    }
}
