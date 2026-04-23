package com.citybuild.core.dao;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * IDao - Data Access Object Interface
 * Abstraction for multi-database support
 * Supports sync and async operations
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface IDao<T, ID> {
    
    // Synchronous Operations
    
    /**
     * Save entity to database
     */
    void save(T entity) throws Exception;
    
    /**
     * Update existing entity
     */
    void update(T entity) throws Exception;
    
    /**
     * Delete entity by ID
     */
    void delete(ID id) throws Exception;
    
    /**
     * Find entity by ID
     */
    Optional<T> findById(ID id) throws Exception;
    
    /**
     * Find all entities
     */
    List<T> findAll() throws Exception;
    
    /**
     * Count all entities
     */
    long count() throws Exception;
    
    // Asynchronous Operations
    
    /**
     * Async save - returns CompletableFuture
     */
    CompletableFuture<Void> saveAsync(T entity);
    
    /**
     * Async update
     */
    CompletableFuture<Void> updateAsync(T entity);
    
    /**
     * Async delete
     */
    CompletableFuture<Void> deleteAsync(ID id);
    
    /**
     * Async find by ID
     */
    CompletableFuture<Optional<T>> findByIdAsync(ID id);
    
    /**
     * Async find all
     */
    CompletableFuture<List<T>> findAllAsync();
    
    // Batch Operations
    
    /**
     * Save multiple entities in batch
     */
    CompletableFuture<Void> saveBatch(List<T> entities);
    
    /**
     * Delete multiple entities in batch
     */
    CompletableFuture<Void> deleteBatch(List<ID> ids);
    
    /**
     * Sync/flush any pending operations
     */
    CompletableFuture<Void> sync();
}
