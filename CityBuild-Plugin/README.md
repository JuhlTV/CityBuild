# CityBuild Plugin - Paper 1.21.1

Ein **vollständiges City-Building Plugin** für Paper Minecraft Server mit Economy, Plots, Shop, Bank, Daily Rewards, und mehr!

✅ **Für Paper 1.21.1**  
✅ **3 Welten** (Plot-Welt, Farm-Welt, PVP-Welt)  
✅ **Flache Map für Plots**  
✅ **Shop System mit 23+ Items**  
✅ **Economy System mit Bank & Transfers**  
✅ **Daily Rewards mit Streak-Bonus**  
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

Fertig: `target/CityBuildPlugin-2.0.0.jar`

### 2. Deploy zu Server
```
Physgun Dashboard
  → Files
  → plugins/
  → Upload: CityBuildPlugin-2.0.0.jar
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

> **⚠️ Wichtig:** Nach dem Upload wird das Plugin automatisch die 3 Welten erstellen und den Shop initialisieren!

---

## 🌍 Welten

Das Plugin erstellt automatisch **3 verschiedene Welten**:

| Welt | Name | Typ | Beschreibung |
|------|------|-----|-------------|
| **Plot-Welt** | `cityplot` | Flat | Flache Map für Plots - 16x16 pro Plot |
| **Farm-Welt** | `cityfarm` | Normal | Für Ressourcenabbau und Farmen |
| **PVP-Welt** | `citypvp` | Normal | Für PVP-Kämpfe |

---

## 🎮 Commands (v2.0.0)

### Economy Commands
```
/citybuild balance              Check your balance
/citybuild daily                Claim daily reward ($500 + streak bonus)
/citybuild pay <player> <amount> Send money to player
/citybuild bank history         View last 5 transactions
/citybuild bank stats           View transfer statistics
```

### Plot Commands
```
/citybuild buy              Buy a plot ($5000)
/citybuild sell             Sell a plot ($4000)
/citybuild info             View your info (balance + plots)
/citybuild tpplot           Teleport to your plot
```

### Shop Commands
```
/citybuild shop             Show shop menu
/citybuild shop list        View all items (23+ items!)
/citybuild shop buy <item> <amount>     Buy items
/citybuild shop sell <item> <amount>    Sell items back
```

### World Teleport Commands
```
/citybuild tpfarm           Teleport to Farm World
/citybuild tppvp            Teleport to PVP World
```

### Info Commands
```
/citybuild leaderboard      Top 10 richest players
/citybuild help             Show all commands
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

### Economy System
- ✅ Balance tracking
- ✅ Starting balance: $10,000
- ✅ Transaction history
- ✅ Player leaderboard (Top 10)
- ✅ Join welcome message

### Daily Rewards 🎁
- ✅ Base: $500 per day
- ✅ Streak bonus: +$100 per consecutive day
- ✅ Claim limit: Once per 24 hours
- ✅ Motivates regular play

### Shop System 🛒
- ✅ 23+ pre-configured items
- ✅ Buy & Sell functionality
- ✅ Building materials (Stone, Wood, Glass, etc.)
- ✅ Ores & Resources (Coal, Iron, Diamond, etc.)
- ✅ Decorations (Glowstone, Obsidian, etc.)
- ✅ Redstone components
- ✅ Furniture (Crafting Table, Furnace, etc.)

### Bank System 💰
- ✅ Player-to-Player transfers
- ✅ Transaction history (last 5)
- ✅ Statistics (Total sent/received/net)
- ✅ Real-time balance updates

### Plots 📍
- ✅ Buy plots: $5,000
- ✅ Sell plots: $4,000
- ✅ Track owned plots
- ✅ Unlimited plots per player
- ✅ Automatic grid spawning
- ✅ Personal plot teleport

### World System 🌍
- ✅ 3 Worlds (Plot, Farm, PVP)
- ✅ Auto-creation on plugin start
- ✅ Flat plot world for easy building
- ✅ Normal farm/pvp worlds
- ✅ Teleport commands

### Admin Features ⚙️
- ✅ Reset all data
- ✅ System statistics
- ✅ Data persistence (JSON)
- ✅ OP-only commands

---

## 🏗️ Architecture

```
CityBuildPlugin.java (Main)
  ├── EconomyManager (Balance, Leaderboard)
  ├── PlotManager (Plot tracking & Grid)
  ├── WorldManager (World creation)
  ├── ShopManager (Shop items & prices)
  ├── BankManager (Player transfers)
  ├── DailyRewardManager (Daily rewards)
  ├── CityBuildCommand (All commands)
  └── PlayerListener (Join events)
```

---

## 🛍️ Shop Items (23 Items)

**Building Materials:**
- Stone, Oak Wood, Dark Oak Wood, Glass, Clay, Sand, Gravel

**Ores:**
- Coal Ore, Copper Ore, Iron Ore, Gold Ore, Diamond Ore

**Decorations:**
- Glowstone, Obsidian, Smooth Stone, Grass Block

**Redstone:**
- Redstone Block, Repeater, Comparator

**Furniture:**
- Crafting Table, Furnace, Chest, Bookshelf

---

## 📝 Development

### Add New Command
Edit `CityBuildCommand.java`:

```java
case "custom":
    return handleCustom(player);
```

### Add New Shop Item
Edit `ShopManager.java` in `initializeDefaultShop()`:

```java
addShopItem("MATERIAL", buyPrice, sellPrice, "Display Name");
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
