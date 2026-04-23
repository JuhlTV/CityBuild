package com.citybuild.core.services;

import com.citybuild.managers.EconomyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * EconomyService Unit Tests
 * Tests transfer operations and balance validation
 */
public class EconomyServiceTest {
    private EconomyService service;
    private EconomyManager mockEconomyManager;
    private Logger logger;
    
    @BeforeEach
    public void setUp() {
        mockEconomyManager = mock(EconomyManager.class);
        logger = Logger.getLogger("test");
        service = new EconomyService(mockEconomyManager, logger);
    }
    
    @Test
    public void testTransferSuccess() {
        Player fromPlayer = mock(Player.class);
        Player toPlayer = mock(Player.class);
        
        when(mockEconomyManager.getBalance(fromPlayer)).thenReturn(1000L);
        
        EconomyService.TransferResult result = service.transfer(fromPlayer, toPlayer, 100);
        
        assertTrue(result.isSuccess(), "Transfer should succeed with sufficient balance");
        assertEquals(100L, result.getAmount());
    }
    
    @Test
    public void testTransferInsufficientFunds() {
        Player fromPlayer = mock(Player.class);
        Player toPlayer = mock(Player.class);
        
        when(mockEconomyManager.getBalance(fromPlayer)).thenReturn(50L);
        
        EconomyService.TransferResult result = service.transfer(fromPlayer, toPlayer, 100);
        
        assertFalse(result.isSuccess(), "Transfer should fail with insufficient balance");
    }
    
    @Test
    public void testTransferToSelf() {
        Player player = mock(Player.class);
        
        EconomyService.TransferResult result = service.transfer(player, player, 100);
        
        assertFalse(result.isSuccess(), "Cannot transfer to self");
    }
    
    @Test
    public void testAddBonus() {
        Player player = mock(Player.class);
        
        EconomyService.OperationResult result = service.addBonus(player, 500, "Daily reward");
        
        assertTrue(result.isSuccess(), "Bonus should succeed");
        verify(mockEconomyManager, times(1)).addBalance(player.getUniqueId(), 500);
    }
    
    @Test
    public void testCanAfford() {
        Player player = mock(Player.class);
        
        when(mockEconomyManager.canAfford(player, 500)).thenReturn(true);
        
        boolean result = service.canAfford(player, 500);
        
        assertTrue(result, "Should be able to afford");
    }
}
