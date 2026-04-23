package com.citybuild.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PlotData model
 * Tests boundary checking, member management, and data integrity
 */
@DisplayName("PlotData Model Tests")
class PlotDataTest {

    private PlotData plot;
    private static final String OWNER_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String MEMBER_UUID = "550e8400-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        // Create a 16x16 plot at origin
        plot = new PlotData(1, OWNER_UUID, 0, 0);
        plot.setSizeX(16);
        plot.setSizeZ(16);
    }

    // ===== LOCATION CHECKING TESTS =====

    @Test
    @DisplayName("Should identify location inside plot")
    void testLocationInPlot() {
        // Plot from 0-15, 0-15
        assertTrue(plot.isLocationInPlot(0, 0), "Corner (0,0) should be in plot");
        assertTrue(plot.isLocationInPlot(8, 8), "Center (8,8) should be in plot");
        assertTrue(plot.isLocationInPlot(15, 15), "Corner (15,15) should be in plot");
    }

    @Test
    @DisplayName("Should reject location outside plot")
    void testLocationOutsidePlot() {
        assertFalse(plot.isLocationInPlot(-1, 0), "X=-1 should be outside");
        assertFalse(plot.isLocationInPlot(16, 0), "X=16 should be outside");
        assertFalse(plot.isLocationInPlot(0, -1), "Z=-1 should be outside");
        assertFalse(plot.isLocationInPlot(0, 16), "Z=16 should be outside");
    }

    @Test
    @DisplayName("Should check boundaries correctly for different sizes")
    void testLocationCheckingVariousSizes() {
        // 10x10 plot
        plot.setSizeX(10);
        plot.setSizeZ(10);
        assertTrue(plot.isLocationInPlot(9, 9), "10x10 should include 9,9");
        assertFalse(plot.isLocationInPlot(10, 10), "10x10 should exclude 10,10");

        // 50x50 plot
        plot.setSizeX(50);
        plot.setSizeZ(50);
        assertTrue(plot.isLocationInPlot(49, 49), "50x50 should include 49,49");
        assertFalse(plot.isLocationInPlot(50, 50), "50x50 should exclude 50,50");
    }

    @Test
    @DisplayName("Should handle plots at negative coordinates")
    void testLocationNegativeCoordinates() {
        PlotData negPlot = new PlotData(2, OWNER_UUID, -20, -30);
        negPlot.setSizeX(16);
        negPlot.setSizeZ(16);

        // Plot from -20 to -5 (X), -30 to -15 (Z)
        assertTrue(negPlot.isLocationInPlot(-20, -30), "Should include corner");
        assertTrue(negPlot.isLocationInPlot(-10, -20), "Should include center");
        assertFalse(negPlot.isLocationInPlot(-21, -30), "Should exclude outside");
        assertFalse(negPlot.isLocationInPlot(-5, -15), "Should exclude outside");
    }

    // ===== MEMBER MANAGEMENT TESTS =====

    @Test
    @DisplayName("Owner should be member by default")
    void testOwnerIsMember() {
        assertTrue(plot.isMember(OWNER_UUID), "Owner should be member");
    }

    @Test
    @DisplayName("Non-owner should not be member initially")
    void testNonOwnerNotMember() {
        assertFalse(plot.isMember(MEMBER_UUID), "Non-owner shouldn't be member");
    }

    @Test
    @DisplayName("Adding member should make them member")
    void testAddMember() {
        plot.addMember(MEMBER_UUID);
        assertTrue(plot.isMember(MEMBER_UUID), "Added member should be member");
    }

    @Test
    @DisplayName("Removing member should make them non-member")
    void testRemoveMember() {
        plot.addMember(MEMBER_UUID);
        assertTrue(plot.isMember(MEMBER_UUID), "Setup: member added");

        plot.removeMember(MEMBER_UUID);
        assertFalse(plot.isMember(MEMBER_UUID), "Removed member should not be member");
    }

    @Test
    @DisplayName("Cannot remove owner from members")
    void testCannotRemoveOwner() {
        plot.removeMember(OWNER_UUID);
        assertTrue(plot.isMember(OWNER_UUID), "Owner should always be member");
    }

    @Test
    @DisplayName("Should track all members correctly")
    void testGetMembers() {
        String member1 = "550e8400-e29b-41d4-a716-446655440001";
        String member2 = "550e8400-e29b-41d4-a716-446655440002";

        plot.addMember(member1);
        plot.addMember(member2);

        var members = plot.getMembers();
        assertEquals(3, members.size(), "Should have owner + 2 members = 3 total");
        assertTrue(members.contains(OWNER_UUID), "Should contain owner");
        assertTrue(members.contains(member1), "Should contain member1");
        assertTrue(members.contains(member2), "Should contain member2");
    }

    @Test
    @DisplayName("Should not add duplicate members")
    void testNoDuplicateMembers() {
        plot.addMember(MEMBER_UUID);
        plot.addMember(MEMBER_UUID); // Try to add again

        int count = (int) plot.getMembers().stream()
                .filter(m -> m.equals(MEMBER_UUID))
                .count();
        assertEquals(1, count, "Should only have 1 instance of member");
    }

    // ===== PLOT DATA INTEGRITY TESTS =====

    @Test
    @DisplayName("Should correctly report plot size")
    void testGetSize() {
        assertEquals(16, plot.getSizeX(), "Should return correct X size");
        assertEquals(16, plot.getSizeZ(), "Should return correct Z size");

        plot.setSizeX(50);
        plot.setSizeZ(75);
        assertEquals(50, plot.getSizeX(), "Should update X size");
        assertEquals(75, plot.getSizeZ(), "Should update Z size");
    }

    @Test
    @DisplayName("Should correctly calculate area")
    void testGetArea() {
        assertEquals(256, plot.getArea(), "16x16 should be 256 m²");

        plot.setSizeX(20);
        plot.setSizeZ(20);
        assertEquals(400, plot.getArea(), "20x20 should be 400 m²");

        plot.setSizeX(50);
        plot.setSizeZ(30);
        assertEquals(1500, plot.getArea(), "50x30 should be 1500 m²");
    }

    @Test
    @DisplayName("Should return correct plot center")
    void testGetCenter() {
        // Plot from 0-15, corner is (0, 0), center should be around (7.5, 7.5)
        int centerX = plot.getCenterX();
        int centerZ = plot.getCenterZ();

        // Center of 16x16 starting at 0 is 8
        assertEquals(8, centerX, "Center X should be 8 for 16x16 at X=0");
        assertEquals(8, centerZ, "Center Z should be 8 for 16x16 at Z=0");

        // Test with offset plot
        PlotData offsetPlot = new PlotData(2, OWNER_UUID, 100, 200);
        offsetPlot.setSizeX(16);
        offsetPlot.setSizeZ(16);
        assertEquals(108, offsetPlot.getCenterX(), "Center X should be 100+8");
        assertEquals(208, offsetPlot.getCenterZ(), "Center Z should be 200+8");
    }

    @Test
    @DisplayName("Should maintain immutability of plot ID")
    void testPlotIdImmutable() {
        assertEquals(1, plot.getPlotId(), "Plot ID should be 1");
        // ID should not be changeable (no setter)
    }

    @Test
    @DisplayName("Should track owner UUID")
    void testOwnerUuid() {
        assertEquals(OWNER_UUID, plot.getOwnerUuid(), "Should return correct owner");
    }

    @Test
    @DisplayName("Should track corner coordinates")
    void testCornerCoordinates() {
        assertEquals(0, plot.getCornerX(), "Should return corner X");
        assertEquals(0, plot.getCornerZ(), "Should return corner Z");

        PlotData offsetPlot = new PlotData(2, OWNER_UUID, 50, 75);
        assertEquals(50, offsetPlot.getCornerX(), "Should return offset corner X");
        assertEquals(75, offsetPlot.getCornerZ(), "Should return offset corner Z");
    }

    // ===== PREMIUM STATUS TESTS =====

    @Test
    @DisplayName("Should track premium status")
    void testPremiumStatus() {
        assertFalse(plot.isPremium(), "Should be standard by default");

        plot.setPremium(true);
        assertTrue(plot.isPremium(), "Should be premium after set");

        plot.setPremium(false);
        assertFalse(plot.isPremium(), "Should be standard after unset");
    }

    // ===== BIOME TESTS =====

    @Test
    @DisplayName("Should track biome type")
    void testBiome() {
        assertNotNull(plot.getBiome(), "Should have default biome");

        plot.setBiome("FOREST");
        assertEquals("FOREST", plot.getBiome(), "Should set and return biome");
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle 0-sized plots (edge case)")
    void testZeroSizePlot() {
        plot.setSizeX(0);
        plot.setSizeZ(0);

        assertEquals(0, plot.getArea(), "0x0 should be 0 area");
        assertFalse(plot.isLocationInPlot(0, 0), "No location should be in 0x0 plot");
    }

    @Test
    @DisplayName("Should handle maximum size plots")
    void testMaxSizePlot() {
        plot.setSizeX(100);
        plot.setSizeZ(100);

        assertEquals(10000, plot.getArea(), "100x100 should be 10000 m²");
        assertTrue(plot.isLocationInPlot(99, 99), "Should include max boundary");
        assertFalse(plot.isLocationInPlot(100, 100), "Should exclude beyond max");
    }

    @Test
    @DisplayName("Should handle very large coordinate values")
    void testLargeCoordinates() {
        PlotData largePlot = new PlotData(999, OWNER_UUID, 1000000, -1000000);
        largePlot.setSizeX(16);
        largePlot.setSizeZ(16);

        assertTrue(largePlot.isLocationInPlot(1000000, -1000000), "Should work with large coords");
        assertFalse(largePlot.isLocationInPlot(1000001, -1000000), "Should respect boundaries with large coords");
    }

}
