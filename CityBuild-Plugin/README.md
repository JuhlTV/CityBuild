# CityBuild Plugin - Paper 1.21.1

Ein **vollständiges City-Building Plugin** für Paper Minecraft Server mit Economy, Plots, und Leaderboard!

✅ **Für Paper 1.21.1**  
✅ **JSON Datenbank**  
✅ **Economy System**  
✅ **Plot Management**  
✅ **Leaderboard**  
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

---

## 🎮 Commands

### Player Commands
```
/citybuild buy           Buy a plot ($5000)
/citybuild sell          Sell a plot ($4000)
/citybuild balance       Check your balance
/citybuild info          View your info (balance + plots)
/citybuild leaderboard   Top 10 richest players
/citybuild help          Show all commands
```

### Admin Commands (OP Only)
```
/citybuild admin reset   Reset all data
/citybuild admin stats   Show system statistics
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
    "plots": 0
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
```

---

## 📊 Features

### Economy
- ✅ Balance tracking
- ✅ Starting balance: $10,000
- ✅ Transaction logging
- ✅ Player leaderboard

### Plots
- ✅ Buy plots: $5,000
- ✅ Sell plots: $4,000
- ✅ Track owned plots
- ✅ Unlimited plots per player

### Leaderboard
- ✅ Top 10 richest players
- ✅ Real-time updates
- ✅ Beautiful formatting

### Admin
- ✅ Reset all data
- ✅ System statistics
- ✅ Data persistence

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
