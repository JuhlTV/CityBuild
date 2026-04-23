# CityBuild Plugin - Setup Checklist

## Project Setup Status

- [x] **Create Maven Project Structure**
  - [x] Created pom.xml with Paper API dependency
  - [x] Set Java 16 target compatibility
  - [x] Configured Maven plugins (compiler, shade)

- [x] **Create Plugin Source Files**
  - [x] Main plugin class (CityBuildPlugin.java)
  - [x] Feature managers (Plot, Economy, Terrain, Admin)
  - [x] Command handlers (PlotCommand, EconomyCommand, etc.)
  - [x] plugin.yml manifest

- [x] **Implement Core Features**
  - [x] Plot Management System
  - [x] Economy System with balance tracking
  - [x] Terrain Generation
  - [x] Admin Tools
  - [x] Shop System

- [ ] **Build & Test**
  - [ ] Compile with Maven
  - [ ] Verify no compilation errors
  - [ ] Test plugin loading on Paper server

- [ ] **Data Persistence** (Next Step)
  - [ ] Implement YAML/JSON file storage
  - [ ] Add player data saving/loading
  - [ ] Configure auto-save intervals

- [ ] **Advanced Features** (Phase 2)
  - [ ] Event listeners (player join, block break, etc.)
  - [ ] Plot boundary enforcement
  - [ ] Permission system integration
  - [ ] GUI menus for shops

## Quick Start

### 1. Build the Plugin
```bash
mvn clean package
```

### 2. Deploy to Server
```bash
# Copy JAR to your Paper server
cp target/CityBuildPlugin-1.0-SNAPSHOT.jar /path/to/server/plugins/
```

### 3. Start Server & Test
```bash
cd /path/to/server
java -Xmx1024M -jar paper.jar nogui
```

### 4. Test Commands In-Game
```
/plot buy
/economy balance
/terrain generate flat
/admin stats (OP only)
```

## Architecture

### Plugin Structure
- **CityBuildPlugin** - Main entry point, initializes all managers
- **Managers** - Handle business logic (PlotManager, EconomyManager, etc.)
- **Commands** - Execute player commands and call manager methods
- **Models** - Data classes (Plot class for plot data)

### Data Flow
```
Player Input → Command Class → Manager → Data Storage
   ↓              ↓                ↓            ↓
/command      PlotCommand    PlotManager    HashMap (will upgrade to file/DB)
```

## Configuration Files

### plugin.yml
- Defines plugin metadata
- Registers commands and permissions
- Sets plugin version and description

### pom.xml
- Maven build configuration
- Paper API dependency (version 1.20)
- Java 16 compatibility
- Maven plugins for compilation and packaging

## Next Steps

1. **Build the project** with `mvn clean package`
2. **Copy JAR to Paper server plugins folder**
3. **Implement data persistence** (YAML configuration files or database)
4. **Add event listeners** for more interactive gameplay
5. **Create GUI menus** for better user experience
6. **Add plot boundary visualization** with particle effects
7. **Integrate with Vault** for more economy options

## Testing Checklist

- [ ] Plugin loads without errors
- [ ] All commands are registered
- [ ] Plot buy/sell commands work
- [ ] Economy balance tracking works
- [ ] Terrain generation works
- [ ] Admin commands require OP status
- [ ] Data persists across restarts

---

**Project:** CityBuildPlugin v1.0-SNAPSHOT  
**Status:** Ready for compilation and testing
