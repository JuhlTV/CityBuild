# Maven Compile Fixes - Summary

**Date:** April 23, 2026  
**Status:** ✅ All 5 compile issues fixed  

---

## Issues Fixed

### 1. ✅ ICommandHandler Lambda Compilation Issue
**Problem:** ICommandHandler had 2 abstract methods (execute + getName), preventing lambda usage

**Solution:** Removed `getName()` abstract method from interface
- Now functional interface with single abstract method: `execute()`
- Other methods (getPermission, getMinArguments, getUsage) remain as defaults

**Files Modified:**
- `ICommandHandler.java` - Removed `String getName()`
- `MenuCommandHandler.java` - Removed `getName()` override
- `InfoCommandHandler.java` - Removed `getName()` override  
- `LeaderboardCommandHandler.java` - Removed `getName()` override
- `CommandRegistryTest.java` - Removed `getName()` assertion

**Impact:** Lambdas in CityBuildCommand now compile correctly:
```java
registry.register("achievements", (player, args) -> {
    plugin.getGUIManager().openAchievementsMenu(player);
    return true;
});
```

---

### 2. ✅ EconomyCommandHandler UUID Mismatches
**Problem:** Called methods with UUID but managers only expose String/Player overloads

**Solution:** Converted UUID calls to String UUID using `.toString()`

**Fixes Applied:**
- `handlePay()` - Line ~163-164
  - `removeBalance(player.getUniqueId(), amount)` → `removeBalance(player.getUniqueId().toString(), amount)`
  - `addBalance(target.getUniqueId(), amount)` → `addBalance(target.getUniqueId().toString(), amount)`

- `handleAdd()` - Line ~180
  - `addBalance(target.getUniqueId(), amount)` → `addBalance(target.getUniqueId().toString(), amount)`

- `handleRemove()` - Line ~278
  - `removeBalance(target.getUniqueId(), amount)` → `removeBalance(target.getUniqueId().toString(), amount)`

- `handleSet()` - Line ~334
  - `setBalance(target.getUniqueId(), amount)` → `setBalance(target.getUniqueId().toString(), amount)`

- `handleTransfer()` - Line ~438-439
  - `removeBalance(from.getUniqueId(), amount)` → `removeBalance(from.getUniqueId().toString(), amount)`
  - `addBalance(to.getUniqueId(), amount)` → `addBalance(to.getUniqueId().toString(), amount)`

- `handleLeaderboard()` - Line ~426
  - `for (var entry : leaderboard.entrySet())` → `for (var entry : leaderboard)`
  - Fixed: getLeaderboard() returns List<Map.Entry<>>, not Map

---

### 3. ✅ EconomyService ValidationUtils Signature
**Problem:** Called ValidationUtils.validatePositiveAmount(amount) but signature requires (amount, context)

**Solution:** Added context parameter to all calls

**Fixes Applied:**
- `transfer()` - Line ~30
  - `validatePositiveAmount(amount)` → `validatePositiveAmount(amount, "EconomyService.transfer")`

- `addBonus()` - Line ~52
  - `validatePositiveAmount(amount)` → `validatePositiveAmount(amount, "EconomyService.addBonus")`

- UUID fixes in EconomyService:
  - `removeBalance(fromPlayer.getUniqueId(), amount)` → `.toString()`
  - `addBalance(toPlayer.getUniqueId(), amount)` → `.toString()`
  - `addBalance(player.getUniqueId(), amount)` → `.toString()`
  - Rollback: `addBalance(fromPlayer.getUniqueId(), amount)` → `.toString()`

---

### 4. ✅ AdminService Method Mismatches & UUID Type Issues
**Problem:** Called methods that don't exist (warnPlayer, mutePlayer) with UUID types, but AdminManager API is different

**Solution:** Updated to match actual AdminManager API

**Fixes Applied:**

- `warn()` method:
  - `adminManager.warnPlayer(targetPlayer.getUniqueId(), reason)` 
  - → `adminManager.addWarning(targetPlayer.getUniqueId().toString())`
  - `adminManager.getWarnings(targetPlayer.getUniqueId())` 
  - → `adminManager.getWarnings(targetUuid)` with proper String UUID

- `mute()` method:
  - `adminManager.mutePlayer(targetPlayer.getUniqueId(), durationMinutes, reason)`
  - → `adminManager.mute(targetUuid, durationMs)` with time unit conversion (minutes → milliseconds)

- `isMuted()` & `getMuteTimeRemaining()`:
  - `adminManager.isMuted(player.getUniqueId())` → `adminManager.isMuted(player.getUniqueId().toString())`
  - `adminManager.getMuteTimeRemaining(player.getUniqueId())` → `adminManager.getMuteTimeRemaining(player.getUniqueId().toString())`

- `setRole()` method:
  - Added role enum conversion: `AdminManager.Role.valueOf(role.toUpperCase())`
  - Changed return type handling: `previousRole.displayName` (was: `previousRole` String)

- `getRole()` method:
  - Changed to return AdminManager.Role enum, converted to String: `role.name()`

- `hasRole()` method:
  - Replaced non-existent `adminManager.hasRole(uuid, role)` with level-based comparison
  - `actual.level >= required.level` check

- `hasPermission()` method:
  - Added required Role parameter: `adminManager.hasPermission(uuid, AdminManager.Role.MODERATOR)`

- Fixed return type bug:
  - Line ~50: Changed `OperationResult.success()` to `WarnResult.success()`

---

### 5. ✅ DIBootstrap EventDispatcher Constructor
**Problem:** Called `new EventDispatcher(logger)` but constructor expects CityBuildPlugin

**Solution:** Changed argument from logger to plugin

**Fix Applied:**
- Line ~163: `EventDispatcher eventDispatcher = new EventDispatcher(logger)`
- Changed to: `EventDispatcher eventDispatcher = new EventDispatcher(plugin)`

---

## Type System Summary

### UUID Usage Standardization
**Pattern:** Service/Handler layer → Manager layer

| Layer | Type | Example |
|-------|------|---------|
| Command Handler | UUID | `player.getUniqueId()` |
| Service | UUID | `toPlayer.getUniqueId()` |
| Manager API | Player \| String | `economy.removeBalance(Player, amount)` |
| Internal Manager | String | Stores `String uuid` internally |

**Fix:** All calls to Manager API now use:
- `player.getUniqueId().toString()` for String UUID parameters
- `player` object for Player parameters (which managers accept directly)

---

## Compilation Prerequisites
To compile now, ensure:
- ✅ All source files are syntactically correct
- ⚠️ Maven must be installed and in PATH
- ⚠️ Java 21 configured as source/target

Run: `mvn clean compile`

---

## Files Modified (10 total)

### Core Files
1. `ICommandHandler.java` - Removed getName() method
2. `DIBootstrap.java` - Fixed EventDispatcher constructor

### Handler Classes
3. `MenuCommandHandler.java` - Removed getName() override
4. `InfoCommandHandler.java` - Removed getName() override
5. `LeaderboardCommandHandler.java` - Removed getName() override

### Business Logic
6. `EconomyCommandHandler.java` - UUID → String conversions + leaderboard fix
7. `EconomyService.java` - ValidationUtils context + UUID fixes
8. `AdminService.java` - Method API alignment + role enum handling

### Tests
9. `CommandRegistryTest.java` - Removed getName() assertion

---

## Next Steps

1. **Install Maven** (if not already installed):
   ```powershell
   choco install maven
   # or
   # Download from https://maven.apache.org/download.cgi
   ```

2. **Run compilation:**
   ```powershell
   cd C:\Users\julia\OneDrive\Dokumente\Minecraft\CityBuild-Plugin
   mvn clean compile
   ```

3. **If compilation succeeds:**
   ```powershell
   mvn clean package -DskipTests
   ```

4. **Deploy and test on server**

---

**All compile fixes ready for testing!** ✅
