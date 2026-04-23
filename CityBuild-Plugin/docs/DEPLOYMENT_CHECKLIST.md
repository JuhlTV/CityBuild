# CityBuild Plot System - Deployment Checklist

Use this checklist before deploying the Plot System to production.

---

## Pre-Deployment Phase

### Code Review
- [ ] All 7 commits reviewed and tested
  - `c268635` - Initial plot system files
  - `8593d70` - PlotManager integration
  - `216b3c8` - PlotProtectionListener update
  - `74a002a` - CityBuildCommand integration
  - `e541191` - Test compilation fix
  - `dbd7e11` - AdminCommandHandler integration
  - `1168509` - Adventure API imports fix

### Maven Build
- [ ] `mvn -B clean package -DskipTests` succeeds with no errors
- [ ] GitHub Actions workflow completes successfully
- [ ] JAR artifact is generated and available

### Code Quality
- [ ] All classes have proper documentation/JavaDoc
- [ ] PlotData, PlotGenerator, PlotManager, AdminPlotCommandHandler reviewed
- [ ] No hardcoded values (except constants in PlotGenerator)
- [ ] Error handling present in all methods

---

## Server Deployment

### Pre-Server Setup
- [ ] Test server environment ready (separate from production)
- [ ] Minecraft server version 1.21.1+ (Paper API compatible)
- [ ] WorldEdit available for testing terrain modifications
- [ ] Plot world created and accessible
- [ ] Backup of current server state made

### Deployment Steps
- [ ] Stop test server
- [ ] Place CityBuildPlugin JAR in `/plugins/` directory
- [ ] Start server
- [ ] Check server logs for errors:
  ```
  ✓ CityBuild Plugin loaded
  ✓ Loaded X plots from database
  ✓ Economy system initialized
  ✓ Plot system initialized
  ```
- [ ] No errors in console

---

## Basic Functionality Testing

### Player Journey - Plot Purchase
- [ ] Connect to test server as Player 1
- [ ] Run `/citybuild balance` → shows starting balance
- [ ] Run `/citybuild buy` → plot purchased successfully
- [ ] Verify in chat: "✓ Plot #1 purchased for $5000"
- [ ] Verify terrain generated at Y=-60:
  - [ ] Grass surface visible
  - [ ] Dirt underlay (Y=-63 to -59)
  - [ ] Oak fences around borders
  - [ ] 3×3 spawn platform with oak sign at center
  - [ ] Sign text readable with plot info

### Plot Information
- [ ] Run `/citybuild plot info` → shows plot dimensions (16×16 default)
- [ ] Run `/citybuild plot info` → shows owner name (Player 1)
- [ ] Sign on plot displays:
  - [ ] Plot ID (e.g., "Plot #1")
  - [ ] Owner UUID (first 8 chars + "...")
  - [ ] Size (e.g., "16x16 blocks")
  - [ ] Area (e.g., "256 m²")

### Plot Teleportation
- [ ] Teleport away from plot: `/teleport @s 0 100 100`
- [ ] Run `/citybuild tpplot` → teleported to plot spawn (center, Y=-58)
- [ ] Can see spawn platform and borders from spawn location
- [ ] Info title shows plot size and area

### Plot Selling
- [ ] Run `/citybuild sell` → plot sold successfully
- [ ] Verify in chat: "✓ Plot sold for $4000"
- [ ] Verify terrain completely removed:
  - [ ] Grass removed
  - [ ] Borders removed
  - [ ] Sign removed
  - [ ] Area is empty (default world terrain only)
- [ ] Plot no longer owned by player
- [ ] Balance increased by $4000

---

## Admin Commands Testing

### Setup
- [ ] Give yourself OP status: `/op Player1`
- [ ] Create test plot: `/citybuild buy` as Player 2

### Plot Expand
- [ ] Run: `/citybuild admin plot expand Player2 north 5`
- [ ] Verify in chat: "✓ Expanded Player2's plot 5 blocks to the north"
- [ ] Verify terrain extended northward:
  - [ ] Grass surface extended
  - [ ] New borders placed
  - [ ] Spawn platform repositioned/regenerated
- [ ] Repeat for: south, east, west directions
- [ ] Test with different block amounts: `1`, `5`, `20`

### Plot Resize
- [ ] Run: `/citybuild admin plot resize Player2 30 30`
- [ ] Verify in chat: "✓ Resized Player2's plot to 30x30"
- [ ] Verify plot is now exactly 30×30:
  - [ ] Grass surface expanded to new size
  - [ ] Borders redrawn at new boundaries
  - [ ] Spawn platform centered in new plot
- [ ] Test minimum: `/citybuild admin plot resize Player2 10 10` (should work)
- [ ] Test maximum: `/citybuild admin plot resize Player2 100 100` (should work)
- [ ] Test below minimum: `/citybuild admin plot resize Player2 5 5` (should reject)
- [ ] Test above maximum: `/citybuild admin plot resize Player2 101 101` (should reject)

### Plot Clear
- [ ] Build something on plot: place some blocks
- [ ] Run: `/citybuild admin plot clear Player2`
- [ ] Verify in chat: "✓ Cleared Player2's plot"
- [ ] Verify all player blocks removed:
  - [ ] Grass surface intact
  - [ ] Borders intact
  - [ ] Spawn platform intact
  - [ ] All custom builds removed

### Plot Delete
- [ ] Run: `/citybuild admin plot delete Player2`
- [ ] Verify in chat: "✓ Deleted Plot #X from Player2"
- [ ] Verify complete removal:
  - [ ] All terrain gone
  - [ ] All borders gone
  - [ ] All signs gone
  - [ ] Area is empty
- [ ] Player2 no longer has plot (run `/citybuild admin plot info Player2` → error)

### Plot Info
- [ ] Create test plot: `/citybuild buy` as Player 3
- [ ] Run: `/citybuild admin plot info Player3`
- [ ] Verify output shows:
  - [ ] Plot ID
  - [ ] Owner name (Player3)
  - [ ] Dimensions
  - [ ] Location
  - [ ] Premium status
  - [ ] Member list
  - [ ] Creation date

### Plot Premium Toggle
- [ ] Run: `/citybuild admin plot premium Player3 on`
- [ ] Verify: "✓ Set Player3's plot to: ✓ Premium"
- [ ] Run: `/citybuild admin plot info Player3` → Premium: Yes
- [ ] Run: `/citybuild admin plot premium Player3 off`
- [ ] Verify: "✓ Set Player3's plot to: Standard"
- [ ] Run: `/citybuild admin plot info Player3` → Premium: No

### Plot Teleport (Admin)
- [ ] Teleport away: `/teleport @s 0 100 100`
- [ ] Run: `/citybuild admin plot teleport Player3`
- [ ] Verify teleported to Player3's plot spawn
- [ ] See spawn platform and borders

### Plot List
- [ ] Create 3 plots (with 3 different players)
- [ ] Run: `/citybuild admin plot list` → shows all 3 plots
- [ ] Run: `/citybuild admin plot list Player3` → shows only Player3's plots
- [ ] Format shows: Plot ID, Owner, Size, Premium status

---

## Plot Protection Testing

### Basic Protection
- [ ] Player A has Plot #1
- [ ] Connect as Player B
- [ ] Try to place block on Player A's plot
- [ ] Verify: Block placement prevented with message "❌ This plot belongs to..."
- [ ] Try to break block on Player A's plot
- [ ] Verify: Block break prevented

### Owner Building
- [ ] Player A (owner) places block on their own plot
- [ ] Verify: Block placed successfully
- [ ] Player A breaks block
- [ ] Verify: Block broken successfully

### Member Building (if implemented)
- [ ] Player A adds Player B as member: `/citybuild member add Player B`
- [ ] Player B tries to build on Player A's plot
- [ ] Verify: Building allowed for member

---

## Persistence Testing

### Save/Load Cycle
- [ ] Create plot as Player A: `/citybuild buy`
- [ ] Create another plot as Player B: `/citybuild buy`
- [ ] Verify plots.json exists in `/plugins/CityBuild/data/`
- [ ] Stop server
- [ ] Restart server
- [ ] Connect as Player A
- [ ] Run: `/citybuild plot info` → shows same plot dimensions and location
- [ ] Verify terrain still at Y=-60
- [ ] Connect as Player B
- [ ] Run: `/citybuild plot info` → shows correct plot
- [ ] Both plots properly restored

### Graceful Degradation
- [ ] Corrupt plots.json (remove a plot entry)
- [ ] Restart server
- [ ] Verify server starts without crashing
- [ ] Check logs for warning about failed plot load
- [ ] Remaining plots load correctly

---

## Edge Cases & Stress Testing

### Multiple Rapid Transactions
- [ ] Buy, sell, buy, sell in quick succession (10x)
- [ ] Verify no terrain glitches
- [ ] Verify plot IDs increment correctly
- [ ] Verify money transfers correctly

### Boundary Conditions
- [ ] Create plot at maximum grid position
- [ ] Try to expand plot northward near world boundary
- [ ] Verify handling of edge case (error message or graceful handling)

### Special Characters
- [ ] Create player with special characters in name (if possible)
- [ ] Verify sign displays player name correctly
- [ ] Run `/citybuild admin plot info` with special character player

### Concurrent Access
- [ ] 2+ players buy plots simultaneously
- [ ] Verify no race conditions
- [ ] Verify both plots created correctly with different IDs
- [ ] Verify terrain generation doesn't overlap

---

## Performance Testing

### Server Performance
- [ ] Create 50+ plots on test server
- [ ] Measure server TPS with WorldEdit `//perf` or similar
- [ ] Verify no significant lag introduced
- [ ] Check memory usage: plots.json size reasonable

### Terrain Generation Speed
- [ ] Time plot generation: `/citybuild buy`
- [ ] Should complete instantly (< 1 second)
- [ ] No server freeze/tick lag

---

## Documentation Verification

- [ ] ADMIN_COMMANDS.md is complete and accurate
- [ ] All command examples work as documented
- [ ] Help text in-game matches documentation
- [ ] Error messages are clear and helpful

---

## Logging & Debugging

- [ ] Server logs show plot operations:
  - [ ] "✓ Loaded X plots from database" on startup
  - [ ] "✓ Saved plot #X for [UUID]" when plot saved
  - [ ] Errors logged clearly if something fails
- [ ] Logs don't contain sensitive info (UUIDs shortened where needed)

---

## Final Pre-Production Sign-Off

### Code & Testing
- [ ] All tests passed (local + GitHub Actions)
- [ ] No console errors or warnings
- [ ] All admin commands functional
- [ ] Plot protection working

### Performance
- [ ] Server TPS stable (> 19.0 TPS)
- [ ] No memory leaks detected
- [ ] Terrain generation performant

### Documentation
- [ ] ADMIN_COMMANDS.md published to wiki/docs
- [ ] Deployment checklist completed
- [ ] Rollback plan ready (previous JAR saved)

### Go/No-Go Decision
- [ ] **GO**: All items checked, ready for production
- [ ] **NO-GO**: Issues found, document and fix before deploying

---

## Rollback Procedure (if needed)

1. Stop server
2. Restore previous CityBuildPlugin JAR from backup
3. Keep plots.json (data persists)
4. Restart server
5. Verify previous functionality restored

---

**Deployment Date:** _______________
**Deployed By:** _______________
**Sign-Off:** _______________
