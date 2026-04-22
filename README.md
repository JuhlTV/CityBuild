# CityBuild - Minecraft Server System

Ein **komplettes City-Building-Spielsystem** für Minecraft - **Vanilla, ohne Mods oder Plugins!**

📦 Mehrere Implementierungen zur Auswahl:
- **CityBuild-Server-Manager** ← Python scripting (EMPFOHLEN!)
- **CITYBUILD-WORLD** ← Vanilla Command Blocks
- **CityBuildPlugin** ← Java Plugin (für Paper/Spigot)

---

## 🚀 Quick Start

### Empfohlen: Python Server Manager

**Warum?**
- ✅ Vollständig programmierbar
- ✅ Kein Plugin Installation nötig
- ✅ Vanilla Server völlig unverändert
- ✅ Einfach zu erweitern
- ✅ RCON Integration

```bash
cd CityBuild-Server-Manager
python run.py
```

**Setup:**
1. Edit `.env` mit deinem Server
2. Enable RCON auf Server
3. Run `python citybuild_manager.py`
4. Type "balance" to test

---

### Alternative 1: Command Blocks World

**Warum?**
- ✅ Zero programming nötig
- ✅ Funktioniert auf jedem Server
- ✅ Visuelle Commands möglich

```bash
cd CITYBUILD-WORLD
# Upload datapack/ zu server/world/datapacks/
```

---

### Alternative 2: Paper Plugin

**Warum?**
- ✅ Beste Features
- ✅ Event-driven
- ✅ Optimiert

**Braucht:**
- Paper Server statt Vanilla
- Java 16+

```bash
cd CityBuildPlugin
mvn clean package
```

---

## 📂 Folder Structure

```
CityBuild/
├── CityBuild-Server-Manager/      ← Python scripting (EMPFOHLEN)
│   ├── citybuild_manager.py       Main script
│   ├── api.py                     Extensions
│   ├── requirements.txt           Dependencies
│   └── .env.example              Configuration
│
├── CITYBUILD-WORLD/              ← Vanilla Command Blocks
│   ├── datapack/                 MCFunctions
│   ├── SETUP_COMMANDS.txt        Setup guide
│   └── README.md                 Documentation
│
└── CityBuildPlugin/              ← Java Plugin
    ├── pom.xml                   Maven config
    ├── src/                      Java source
    └── README.md                 Documentation
```

---

## 🎮 Features

Alle Implementierungen haben:

### Economy System
```
- Player balance tracking
- Money earning/spending
- Transactions
- Ledger system
```

### Plot System
```
- Buy plots ($5000)
- Sell plots ($4000)
- Track owned plots
- Multi-plot ownership
```

### Admin Tools
```
- System reset
- Statistics
- Player management
- Data export/import
```

---

## 🔧 Configuration

### Wirtschaft
```
Starting Balance:  $10,000
Plot Buy Price:    $5,000
Plot Sell Price:   $4,000
```

### Server
```
Minecraft Version:  1.20.1
Server Type:        Vanilla (or Paper/Fabric)
RCON:              Recommended for Manager
```

---

## 📊 Comparison

| Feature | Manager | World | Plugin |
|---------|---------|-------|--------|
| **Vanilla** | ✅ | ✅ | ❌ (Paper) |
| **Programming** | Python | MCFunction | Java |
| **Difficulty** | Medium | Easy | Hard |
| **Performance** | Excellent | Good | Best |
| **Extensible** | Very | Limited | Yes |
| **Setup Time** | 30 min | 10 min | 1 hour |

---

## 🚀 Deployment on Physgun

### Manager (Empfohlen)
```
1. Install Python 3.8+
2. Download CityBuild-Server-Manager
3. Edit .env with Physgun IP
4. Enable RCON on Physgun server
5. Run: python citybuild_manager.py
```

### World
```
1. Download CITYBUILD-WORLD
2. Upload datapack/ to Physgun world/datapacks/
3. Run /reload on server
4. Done!
```

### Plugin
```
1. Build: mvn clean package
2. Copy JAR to plugins/ folder
3. Restart server
4. Configure in plugin config
```

---

## 📖 Documentation

### Python Manager
- [README.md](CityBuild-Server-Manager/README.md) - Full docs
- [SETUP.md](CityBuild-Server-Manager/SETUP.md) - Installation

### Vanilla World
- [README.md](CITYBUILD-WORLD/README.md) - Overview
- [INSTALLATION.md](CITYBUILD-WORLD/INSTALLATION.md) - Setup

### Plugin
- [README.md](CityBuildPlugin/README.md) - Java Plugin docs

---

## 🎯 Getting Started

### 5-Minute Setup (Manager)
```bash
git clone https://github.com/JuhlTV/CityBuild.git
cd CityBuild/CityBuild-Server-Manager
copy .env.example .env
# Edit .env with your server
python run.py
```

### Test Commands
```
balance [player]     Check balance
buy [player]         Buy a plot
sell [player]        Sell a plot
stats                System stats
list                 All players
```

---

## 🛠️ Customization

### Add Features to Manager
Edit `citybuild_manager.py`:

```python
def custom_feature(self, player_name):
    # Your code here
    self._log("Custom feature executed")
    self._save_data()
```

### Add MCFunctions to World
Create new `.mcfunction` file:

```mcfunction
# Custom command
/tellraw @s {"text":"Hello!","color":"green"}
/scoreboard players add @s cb_balance 100
```

---

## 📞 Support

### Issues?
Check logs:
- **Manager**: `logs/citybuild.log`
- **World**: Server logs
- **Plugin**: Latest.log

### Common Problems
1. **RCON Connection Failed** → Check .env and server.properties
2. **Scoreboards Missing** → Run `/function citybuild:init`
3. **Commands Not Working** → Check server has CityBuild enabled

---

## 🤝 Contributing

All implementations are open source!

- **Bug Reports**: GitHub Issues
- **Feature Requests**: GitHub Discussions
- **Pull Requests**: Welcome!

---

## 📄 License

MIT License - Free to use and modify

---

## 🎉 Ready to Go!

Pick your implementation and follow the setup guide!

**Start with:** [CityBuild-Server-Manager](CityBuild-Server-Manager/README.md)

---

**Build your perfect CityBuild Server!** 🏗️✨

Repository: https://github.com/JuhlTV/CityBuild
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
