# CityBuild Code Quality Standards & Best Practices

## 📋 Null Safety Checklist

### ✅ Required Null Checks
- [ ] All method parameters with `Player` type
- [ ] All manager getter methods
- [ ] All inventory/item operations
- [ ] All command arguments
- [ ] All data deserialization

### ✅ Safe Pattern Examples

**GOOD:**
```java
public void handlePlayer(Player player) {
    if (player == null) return;
    // rest of code
}

public void processItem(ItemStack item) {
    if (item == null || !item.hasItemMeta()) return;
    // rest of code
}
```

**BAD:**
```java
public void handlePlayer(Player player) {
    player.sendMessage("Hi"); // NPE if player is null!
}
```

---

## 🎯 Constants & Magic Numbers

### ✅ Use CityBuildConstants.java
Never hardcode values! Use constants instead:

```java
// GOOD
public static final int PLOT_SIZE = 100;
int plotArea = PLOT_SIZE * PLOT_SIZE;

// BAD
int plotArea = 100 * 100; // Magic number!
```

### ✅ Centralized Configuration
All configurable values in:
- `CityBuildConstants.java` - Static constants
- `ConfigManager.java` - YAML configuration
- Never scatter values across classes

---

## 🔒 Error Handling

### ✅ Standard Exception Handling

```java
try {
    // Operation
    result = manager.doSomething();
} catch (NullPointerException e) {
    logger.error("NPE in operation", e);
    player.sendMessage(PREFIX_ERROR + "Operation failed");
} catch (Exception e) {
    logger.error("Unexpected error", e);
}
```

### ✅ Use ExceptionHandler Utility

```java
exceptionHandler.handleCommandException("mycmd", player, e);
exceptionHandler.notifyPlayerError(player, "Something went wrong");
```

---

## 📝 Logging Standards

### ✅ Use CityBuildLogger

```java
// GOOD
logger.info("Player joined");
logger.success("Plot purchased");
logger.warn("Config missing value");
logger.error("Database error", exception);

// BAD
System.out.println("Player joined");
plugin.getLogger().info("Random message");
```

---

## 🧪 Validation Rules

### ✅ Input Validation

Always validate before processing:

```java
// Check null
if (player == null) return false;

// Check empty
if (name == null || name.trim().isEmpty()) return false;

// Check bounds
if (amount < 0 || amount > MAX_AMOUNT) return false;

// Check type
if (!(obj instanceof Player)) return;

// Use ValidationUtil
ValidationUtil.isValidPrice(price);
ValidationUtil.isValidUsername(name);
```

---

## 🔄 Async Operations

### ✅ Async Pattern

```java
// For I/O operations (JSON save, database)
asyncDataSaver.saveAsync(() -> {
    manager.saveAllData();
}, () -> {
    logger.success("Data saved");
});

// For long-running tasks
scheduler.runTaskAsynchronously(plugin, () -> {
    // Long operation
});

// For delayed operations
scheduler.runTaskLater(plugin, () -> {
    // Task
}, delayTicks);
```

### ⚠️ WARNING: Never do I/O on main thread!
```java
// BAD - Blocks server!
player.saveAllData();

// GOOD - Async
asyncDataSaver.saveAsync(() -> player.saveAllData());
```

---

## 💾 JSON/Persistence

### ✅ JSON Safety Pattern

```java
// GOOD - Use JSONSafetyWrapper
JSONSafetyWrapper json = new JSONSafetyWrapper(jsonObject);
String name = json.getString("name", "Unknown");
int count = json.getInt("count", 0);

// BAD - Direct access
String name = jsonObject.get("name").getAsString(); // NPE!
```

---

## 📊 Command Implementation

### ✅ Command Structure

```java
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // 1. Type check
    if (!(sender instanceof Player)) {
        sender.sendMessage("Only players!");
        return true;
    }
    
    Player player = (Player) sender;
    
    // 2. Argument validation
    if (args.length == 0) {
        showHelp(player);
        return true;
    }
    
    // 3. Process subcommands
    String subcommand = args[0].toLowerCase();
    try {
        switch (subcommand) {
            case "list":
                handleList(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            default:
                showHelp(player);
        }
    } catch (Exception e) {
        exceptionHandler.handleCommandException(label, player, e);
    }
    
    return true;
}
```

---

## 🎨 Code Style

### ✅ Formatting Rules

1. **Indentation**: 4 spaces (no tabs)
2. **Line length**: Max 120 characters
3. **Naming**:
   - Classes: PascalCase
   - Methods: camelCase
   - Constants: UPPER_SNAKE_CASE
   - Private fields: camelCase (with `_` prefix optional)

### ✅ Method Documentation

```java
/**
 * Brief description of what method does
 * 
 * @param player The player performing the action
 * @param amount The amount to process
 * @return true if successful, false otherwise
 * @throws IllegalArgumentException if amount is negative
 */
public boolean process(Player player, double amount) {
    // implementation
}
```

---

## 🚀 Performance Checklist

- [ ] No N+1 loops (nested iterations over large sets)
- [ ] No synchronous I/O on main thread
- [ ] Cache frequently accessed data
- [ ] Use `getOrDefault()` instead of `get()` + null check
- [ ] Stream operations: prefer `filter().forEach()` over manual loops
- [ ] Lambda expressions: keep simple, extract complex logic

---

## ✅ Pre-Commit Checklist

Before committing code:

- [ ] Run `get_errors` - 0 compilation errors
- [ ] Test null-safety (try to crash it)
- [ ] Verify logging messages are appropriate
- [ ] Check no magic numbers exist
- [ ] Verify all resources are closed (try-with-resources)
- [ ] No System.out.println() statements
- [ ] All new constants in CityBuildConstants.java
- [ ] Documentation/comments for complex logic

---

## 🔍 Common Bug Patterns to Avoid

### Pattern 1: Unchecked Casts
```java
// BAD
Map<String, Object> map = (Map) jsonObject;

// GOOD
Map<String, Object> map = new HashMap<>();
try {
    map = gson.fromJson(jsonObject, Map.class);
} catch (Exception e) {
    // handle error
}
```

### Pattern 2: Missing null checks in loops
```java
// BAD
for (Player p : Bukkit.getOnlinePlayers()) {
    p.sendMessage("Hi"); // p could be null in edge cases
}

// GOOD
for (Player p : Bukkit.getOnlinePlayers()) {
    if (p != null && p.isOnline()) {
        p.sendMessage("Hi");
    }
}
```

### Pattern 3: Resource leaks
```java
// BAD
FileReader reader = new FileReader(file);
String content = IOUtils.toString(reader);
// reader never closed!

// GOOD
try (FileReader reader = new FileReader(file)) {
    String content = IOUtils.toString(reader);
}
```

---

## 📚 Reference Classes

- `ValidationUtil.java` - Input validation
- `JSONSafetyWrapper.java` - JSON parsing
- `CityBuildConstants.java` - Configuration
- `CityBuildLogger.java` - Logging
- `ExceptionHandler.java` - Error handling
- `AsyncDataSaver.java` - Async operations

---

**Last Updated**: 2026-04-23  
**Version**: 2.0  
**Status**: Active
