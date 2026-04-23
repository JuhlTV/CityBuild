# Plot Generation & Admin Management System

## Overview
This system allows dynamic plot generation when players buy plots, with admin commands for expansion and management.

## Components

### 1. **PlotData.java** - Plot Model
Represents a single plot with:
- Plot ID, Owner UUID, Size (width x height)
- Member management
- Premium status, Biome, Timestamps
- Boundary checking
- Area calculations

### 2. **PlotGenerator.java** - Terrain Generation
Handles all physical plot creation:
- `generatePlot()` - Create terrain + border + spawn platform
- `expandPlot()` - Expand in directions (north/south/east/west)
- `clearPlot()` - Remove all blocks (keep border)
- `deletePlot()` - Remove everything
- `getPlotSpawn()` - Get spawn location

### 3. **AdminPlotCommandHandler.java** - Admin Management
Admin commands for plot management:
```
/citybuild admin plot expand <player> <direction> [blocks]
/citybuild admin plot resize <player> <width> <height>
/citybuild admin plot clear <player>
/citybuild admin plot delete <player>
/citybuild admin plot info <player>
/citybuild admin plot premium <player> [on/off]
/citybuild admin plot teleport <player>
/citybuild admin plot list [player]
```

## Integration Steps

### Step 1: Update PlotManager
Add methods to PlotManager.java:

```java
// Add PlotData storage
private final Map<Integer, PlotData> plots = new HashMap<>();

// Add plot retrieval methods
public PlotData getPlot(int plotId) {
    return plots.get(plotId);
}

public PlotData getFirstPlot(String playerUuid) {
    List<Integer> playerPlotIds = playerPlots.get(playerUuid);
    if (playerPlotIds.isEmpty()) return null;
    return plots.get(playerPlotIds.get(0));
}

public void savePlot(PlotData plot) {
    plots.put(plot.getPlotId(), plot);
    saveData(); // Persists to JSON
}

public void removePlot(String playerUuid, int plotId) {
    List<Integer> playerPlotIds = playerPlots.get(playerUuid);
    playerPlotIds.remove(Integer.valueOf(plotId));
    plots.remove(plotId);
    saveData();
}
```

### Step 2: Update CityBuildCommand - Buy Handler
Modify `handleBuy()` in CityBuildCommand.java:

```java
private boolean handleBuy(Player player) {
    economy.initializePlayer(player);
    
    long balance = economy.getBalance(player);
    int price = plots.getPlotBuyPrice();

    if (balance < price) {
        player.sendMessage(Component.text("[CityBuild] ❌ Insufficient funds!", NamedTextColor.RED));
        return true;
    }

    // Create plot in database
    PlotData plot = new PlotData(
        plots.getNextPlotId(),
        player.getUniqueId().toString(),
        calculatePlotCornerX(plots.getTotalPlots()),
        calculatePlotCornerZ(plots.getTotalPlots())
    );

    // Generate terrain
    PlotGenerator.generatePlot(plugin.getWorldManager().getPlotWorld(), plot);

    // Save to managers
    plots.savePlot(plot);
    economy.removeBalance(player, price);

    player.sendMessage(Component.text("[CityBuild] ✓ Plot #" + plot.getPlotId() + " purchased!", NamedTextColor.GREEN));

    return true;
}

// Helper to calculate grid positions
private int calculatePlotCornerX(int plotIndex) {
    final int PLOT_SIZE = 16;
    final int PLOT_SPACING = 2;
    final int PLOTS_PER_ROW = 10;
    int col = plotIndex % PLOTS_PER_ROW;
    return col * (PLOT_SIZE + PLOT_SPACING);
}

private int calculatePlotCornerZ(int plotIndex) {
    final int PLOT_SIZE = 16;
    final int PLOT_SPACING = 2;
    final int PLOTS_PER_ROW = 10;
    int row = plotIndex / PLOTS_PER_ROW;
    return row * (PLOT_SIZE + PLOT_SPACING);
}
```

### Step 3: Hook Admin Commands
In CityBuildCommand.java, add to the admin command switch:

```java
case "admin":
    return handleAdmin(player, args);

private boolean handleAdmin(Player player, String[] args) {
    if (!player.isOp()) {
        player.sendMessage(formatError("❌ No permission!"));
        return true;
    }
    
    if (args.length < 2) {
        showAdminHelp(player);
        return true;
    }

    String adminSubcommand = args[1].toLowerCase();

    switch (adminSubcommand) {
        case "plot":
            AdminPlotCommandHandler plotAdmin = new AdminPlotCommandHandler(plugin);
            return plotAdmin.handleAdminPlotCommand(player, args);
        // ... other admin commands
        default:
            showAdminHelp(player);
            return true;
    }
}
```

### Step 4: Update JSON Persistence
Modify PlotManager's `loadData()` and `saveData()` to handle PlotData objects:

```java
public void saveData() {
    // ... existing code ...
    
    // Save plot data
    JsonObject plotsJson = new JsonObject();
    plots.forEach((id, plot) -> {
        JsonObject plotObj = new JsonObject();
        plotObj.addProperty("plotId", plot.getPlotId());
        plotObj.addProperty("ownerUuid", plot.getOwnerUuid());
        plotObj.addProperty("sizeX", plot.getSizeX());
        plotObj.addProperty("sizeZ", plot.getSizeZ());
        plotObj.addProperty("cornerX", plot.getCornerX());
        plotObj.addProperty("cornerZ", plot.getCornerZ());
        plotObj.addProperty("isPremium", plot.isPremium());
        plotObj.addProperty("biome", plot.getBiome());
        plotObj.addProperty("createdAt", plot.getCreatedAt());
        
        // Save members as array
        JsonArray membersArray = new JsonArray();
        for (String member : plot.getMembers()) {
            membersArray.add(member);
        }
        plotObj.add("members", membersArray);
        
        plotsJson.add(String.valueOf(id), plotObj);
    });
    json.add("plots", plotsJson);
}
```

## Feature Breakdown

### Plot Generation Features
✅ Automatically generates dirt floor + grass surface  
✅ Creates fence borders with corner markers  
✅ Places spawn platform with info signs  
✅ 16x16 default size (customizable)  

### Admin Features
✅ **Expand** - Add 5 blocks in any direction  
✅ **Resize** - Set exact dimensions (10x10 to 100x100)  
✅ **Clear** - Remove player's constructions (keep border)  
✅ **Delete** - Remove entire plot  
✅ **Premium** - Toggle premium status (for tax system)  
✅ **Info** - View detailed plot info  
✅ **Teleport** - Jump to any player's plot  
✅ **List** - View all plots  

## Example Workflow

1. **Player buys plot**
   - Command: `/citybuild buy`
   - System creates PlotData with ID, position, owner
   - PlotGenerator creates terrain/border
   - Player pays 5000$

2. **Admin expands plot**
   - Command: `/citybuild admin plot expand PlayerName north 5`
   - Plot expands 5 blocks north
   - Terrain regenerates with new size

3. **Player teleports to plot**
   - Command: `/citybuild tpplot`
   - Spawns on platform in plot center
   - Can build freely (protected by PlotProtectionListener)

4. **Admin clears for reset**
   - Command: `/citybuild admin plot clear PlayerName`
   - Removes all blocks but keeps border
   - Player can rebuild fresh

## Configuration (config.yml)
```yaml
economy:
  plot_buy_price: 5000
  plot_sell_price: 4000

plot:
  spawn_height: 65
  default_size: 16  # Width and height
  max_size: 100
  min_size: 10

tax:
  daily_plot_tax: 500
  premium_multiplier: 2.0
```

## Next Steps
1. Replace old PlotManager methods with new PlotData-based ones
2. Add database migration script if needed
3. Test plot generation and terrain
4. Add command aliases for convenience
5. Consider plot theme selection (biome/building style)
