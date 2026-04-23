# CityBuild Plugin - Minecraft Paper Server

Complete City Building System with advanced features for Plot Management, Economy System, Terrain Generation, and Admin Tools.

## Features

### 🏗️ Plot Management System
- Buy and sell plots
- View plot information
- Track all plots in the city
- Biome customization per plot

### 💰 Economy System  
- Player balance tracking
- Money transfers between players
- Starting balance: $10,000
- Built-in payment system

### 🌍 Terrain Generation
- Flat terrain (Plains)
- Mountain terrain  
- Island generation
- Jungle biome support
- Adjustable terrain height

### 🔧 Admin Tools
- Plugin reload functionality
- City reset commands
- Statistics tracking
- Admin-only commands with OP verification

### 🏪 Shop System
- Create and manage shops
- Item trading (extensible)
- Shop information display

## Installation

1. **Build the Plugin:**
   ```bash
   mvn clean package
   ```

2. **Deploy:**
   - Copy `target/CityBuildPlugin-1.0-SNAPSHOT.jar` to your Paper server's `plugins/` folder

3. **Start Server:**
   ```bash
   java -Xmx1024M -Xms1024M -jar paper.jar nogui
   ```

## Commands

### Player Commands

**Plot System:**
```
/plot buy          - Purchase a new plot ($5,000)
/plot sell         - Sell your plot ($4,000)
/plot info         - View your plot information
/plot list         - List all plots in the city
```

**Economy:**
```
/economy balance           - Check your current balance
/economy pay <player> <amount> - Send money to another player
/economy check             - Check economy system status
```

**Terrain:**
```
/terrain generate <type>   - Generate terrain (flat, mountain, island, jungle)
/terrain settings          - View current terrain settings
```

**Shop:**
```
/shop create       - Create a new shop
/shop remove       - Remove your shop
/shop info         - View shop information
```

### Admin Commands

```
/admin reload      - Reload the plugin
/admin reset       - Reset the entire city
/admin stats       - View system statistics
```

## Permissions

```yaml
citybuild.plot.buy       - Allow buying plots (default: true)
citybuild.plot.sell      - Allow selling plots (default: true)
citybuild.economy.pay    - Allow money transfers (default: true)
citybuild.admin          - Admin permissions (default: OP only)
```

## Configuration

### Server Requirements
- **Java:** 16 or higher
- **Paper:** 1.20+
- **RAM:** 1GB minimum (2GB recommended)

### Default Settings
- Starting Balance: $10,000
- Plot Buy Price: $5,000
- Plot Sell Price: $4,000
- Default Biome: PLAINS
- Default Height: 64 blocks

## File Structure

```
CityBuildPlugin/
├── src/main/java/com/citybuild/
│   ├── CityBuildPlugin.java          (Main plugin class)
│   ├── commands/
│   │   ├── PlotCommand.java
│   │   ├── EconomyCommand.java
│   │   ├── TerrainCommand.java
│   │   ├── AdminCommand.java
│   │   └── ShopCommand.java
│   └── features/
│       ├── plots/
│       │   ├── PlotManager.java
│       │   └── Plot.java
│       ├── economy/
│       │   └── EconomyManager.java
│       ├── terrain/
│       │   └── TerrainManager.java
│       └── admin/
│           └── AdminManager.java
├── src/main/resources/
│   └── plugin.yml
├── pom.xml
└── README.md
```

## Development

### Building from Source
```bash
# Clone or extract the project
cd CityBuildPlugin

# Compile and package
mvn clean package

# Run tests
mvn test
```

### Extension Points

The plugin is designed to be easily extended. Common areas for customization:

1. **Plot Management** - Add plot claiming, resizing, or permissions
2. **Economy** - Integrate with external economy plugins
3. **Terrain** - Add custom world generation algorithms
4. **Shops** - Implement full shop NPC systems
5. **Events** - Add more game events and mechanics

## Future Enhancements

- [ ] Database support (MySQL/MongoDB)
- [ ] Advanced plot grid system with coordinates
- [ ] NPC shopkeepers
- [ ] Custom item trading
- [ ] Plot protection system
- [ ] Build restrictions per biome
- [ ] Economy taxes and fees
- [ ] Player statistics tracking
- [ ] Web dashboard
- [ ] Discord integration

## Support & Issues

For issues or feature requests, please create an issue in the project repository.

## License

This project is open source and available under the MIT License.

---

**Version:** 1.0-SNAPSHOT  
**Last Updated:** 2026-04-22

---

**Tools:**
- [install-tools.bat](install-tools.bat)
