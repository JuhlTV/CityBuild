# CityBuild Plugin - Paper 1.21.1

Ein **vollständiges City-Building Plugin** für Paper Minecraft Server mit Economy, Plots, Multi-World System, und Leaderboard!

✅ **Für Paper 1.21.1**  
✅ **3 Welten** (Plot-Welt, Farm-Welt, PVP-Welt)  
✅ **Flache Map für Plots**  
✅ **Economy System**  
✅ **Plot Management mit Grid-System**  
✅ **Leaderboard**  
✅ **World Teleportation**  
✅ **JSON Datenbank**  
✅ **Admin Tools**  

---

## 🚀 Installation

### 1. Build Plugin
```bash
mvn clean package
```

Fertig: `target/CityBuildPlugin-1.0.0.jar`

### 2. Deploy zu Server
```
Physgun Dashboard
  → Files
  → plugins/
  → Upload: CityBuildPlugin-1.0.0.jar
```

### 3. Restart Server
```
Physgun → Server → Restart
```

### 4. Verify
```
In-Game: /citybuild help
Should show all commands ✓
```

> **⚠️ Wichtig:** Nach dem Upload wird das Plugin automatisch die 3 Welten erstellen!

---

## 🌍 Welten

Das Plugin erstellt automatisch **3 verschiedene Welten**:

| Welt | Name | Typ | Beschreibung |
|------|------|-----|-------------|
| **Plot-Welt** | `cityplot` | Flat | Flache Map für Plots - 16x16 pro Plot |
| **Farm-Welt** | `cityfarm` | Normal | Für Ressourcenabbau und Farmen |
| **PVP-Welt** | `citypvp` | Normal | Für PVP-Kämpfe |

---

## 🎮 Commands

### Player Commands
```
/citybuild buy              Buy a plot ($5000)
/citybuild sell             Sell a plot ($4000)
/citybuild balance          Check your balance
/citybuild info             View your info (balance + plots)
/citybuild leaderboard      Top 10 richest players
```

### World Teleport Commands
```
/citybuild tpplot           Teleport to your plot
/citybuild tpfarm           Teleport to Farm World
/citybuild tppvp            Teleport to PVP World
```

### General Commands
```
/citybuild help             Show all commands
```

### Admin Commands (OP Only)
```
/citybuild admin reset      Reset all data
/citybuild admin stats      Show system statistics
```

---

## � Plot System

### Grid-basiertes Plot Spawning
Jeder Spieler bekommt einen **individuellen Plot** auf der flachen Map `cityplot`:

- **Größe:** 16x16 Blöcke pro Plot
- **Abstand:** 2 Blöcke Spacing zwischen Plots
- **Layout:** 10 Plots pro Reihe (Grid)
- **Höhe:** Y=65 (bereit zum Bauen)

```
Grid Example (Top View):
[Plot 1] [Plot 2] [Plot 3] ...
[Plot 11] [Plot 12] [Plot 13] ...
[Plot 21] [Plot 22] [Plot 23] ...
```

### Plot Locations
```
Plot ID 1: X=8, Z=8 (Center)
Plot ID 2: X=26, Z=8
Plot ID 3: X=44, Z=8
Plot ID 11: X=8, Z=26
...
```

---

## 💾 Data Storage

### `plugins/CityBuild/data/players.json`
```json
{
  "uuid": {
    "uuid": "player-uuid",
    "balance": 10000,
    "lastTransaction": 1234567890,
    "plots": 1
  }
}
```

### `plugins/CityBuild/data/plots.json`
```json
{
  "uuid": [1, 2, 3]
}
```

---

## ⚙️ Configuration

Edit `plugins/CityBuild/config.yml`:

```yaml
economy:
  starting_balance: 10000
  plot_buy_price: 5000
  plot_sell_price: 4000

worlds:
  plot_world: "cityplot"
  farm_world: "cityfarm"
  pvp_world: "citypvp"

plots:
  size: 16
  spacing: 2
  plots_per_row: 10
```

---

## 📊 Features

### Economy
- ✅ Balance tracking
- ✅ Starting balance: $10,000
- ✅ Transaction logging
- ✅ Player leaderboard
- ✅ Join welcome message

### Plots
- ✅ Buy plots: $5,000
- ✅ Sell plots: $4,000
- ✅ Track owned plots
- ✅ Unlimited plots per player
- ✅ Automatic grid spawning

### World System
- ✅ 3 Worlds (Plot, Farm, PVP)
- ✅ Auto-creation on plugin start
- ✅ Flat plot world
- ✅ Normal farm/pvp worlds
- ✅ Teleport commands

### Leaderboard
- ✅ Top 10 richest players
- ✅ Real-time updates
- ✅ Beautiful formatting

### Admin
- ✅ Reset all data
- ✅ System statistics
- ✅ Data persistence
- ✅ OP-only commands

---

## 🔧 Architecture

```
CityBuildPlugin.java (Main)
  ├── EconomyManager (Balance, Leaderboard)
  ├── PlotManager (Plot tracking)
  ├── CityBuildCommand (Command executor)
  └── PlayerListener (Events)
```

---

## 📝 Development

### Add New Command
Edit `CityBuildCommand.java`:

```java
case "custom":
    return handleCustom(player);
```

### Add New Feature
1. Create new Manager class
2. Initialize in CityBuildPlugin.onEnable()
3. Add commands in CityBuildCommand
4. Add listener if needed

---

## 🚨 Troubleshooting

### Plugin not loading?
```
Check: /plugins (should show CityBuild)
Logs: /logs/latest.log
```

### Commands not working?
```
/reload
/citybuild help
Check for errors in console
```

### Data not saving?
```
Check folder: plugins/CityBuild/data/
Check permissions on server
```

---

## 📈 Future Features

- [ ] Taxes system
- [ ] Rent system
- [ ] Player trading
- [ ] Auctions
- [ ] Web dashboard
- [ ] Discord integration
- [ ] Land claiming
- [ ] NPC shops

---

**Ready to deploy!** 🚀

https://github.com/JuhlTV/CityBuild
