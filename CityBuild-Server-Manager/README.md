# CityBuild Server Manager 🐍

Ein **Python-basierter Server Manager** für Vanilla Minecraft, der ein komplettes City-Build System ohne Mods oder Plugins verwirklicht!

✅ **Vanilla Minecraft** - Unverändert, keine Mods  
✅ **Python Script** - Vollständig programmierbar  
✅ **RCON Integration** - Kommuniziert mit Vanilla Server  
✅ **JSON Datenbank** - Alle Spielerdaten lokal gespeichert  
✅ **Economy System** - Plots kaufen/verkaufen, Balance tracking  

---

## 🚀 Installation

### Requirements
- Python 3.8+
- Minecraft Server (Vanilla 1.20.1+)
- RCON aktiviert auf Server

### Step 1: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 2: Configure Server

**In `server.properties`:**
```properties
enable-rcon=true
rcon.port=25575
rcon.password=your_secret_password
```

### Step 3: Setup Environment

**`.env` erstellen:**
```bash
copy .env.example .env
```

**`.env` anpassen:**
```
RCON_HOST=your-server-ip
RCON_PORT=25575
RCON_PASSWORD=your_rcon_password
STARTING_BALANCE=10000
PLOT_BUY_PRICE=5000
PLOT_SELL_PRICE=4000
```

### Step 4: Run Manager

```bash
python citybuild_manager.py
```

---

## 🎮 Commands

### Im Manager
```
balance    → Check player balance
buy        → Buy a plot
sell       → Sell a plot
reset      → Reset all data
stats      → Show statistics
list       → List all players
exit       → Shutdown
```

### Im Minecraft Server
```
/scoreboard players get @s cb_balance  → View balance
/scoreboard players get @s cb_plots    → View plot count
/scoreboard players list @s            → View all scores
```

---

## 💾 Datenstruktur

### `data/citybuild.json`
```json
{
  "PlayerName": {
    "uuid": null,
    "balance": 10000,
    "plots": ["plot_0", "plot_1"],
    "joined": "2026-04-22T12:00:00",
    "last_login": "2026-04-22T12:30:00"
  }
}
```

---

## 🔧 How It Works

```
Vanilla Server (Minecraft)
    ↓ (RCON Protocol)
CityBuild Manager (Python)
    ↓ (JSON)
Database (local storage)
```

1. **Server läuft normal** - Keine Änderungen
2. **Manager im Hintergrund** - Verwaltet Daten
3. **RCON Kommandos** - Sync mit Scoreboards
4. **Persistente Daten** - JSON speichert alles

---

## 📊 Features

### Economy
- ✓ Spielerbalance tracking
- ✓ Money hinzufügen/entfernen
- ✓ Automatische Sync mit Scoreboards

### Plots
- ✓ Plots kaufen ($5000)
- ✓ Plots verkaufen ($4000)
- ✓ Unlimited plot ownership
- ✓ Plot tracking per player

### Admin
- ✓ Reset all data
- ✓ System statistics
- ✓ Player list
- ✓ Logging

---

## 🖥️ Physgun Deployment

### Option 1: Lokaler Manager + Remote Server

```
1. Manager läuft auf deinem Computer
2. Server läuft auf Physgun
3. RCON verbindung über Internet
```

**`.env`:**
```
RCON_HOST=your-physgun-ip
RCON_PORT=25575
RCON_PASSWORD=physgun_rcon_password
```

### Option 2: Manager auf Physgun Server

```
1. Physgun hat SSH/Terminal zugang
2. Python installieren
3. Manager Script uploaden
4. Mit `python citybuild_manager.py` starten
```

---

## 📝 Logging

Alle Aktionen werden geloggt:

**`logs/citybuild.log`:**
```
[2026-04-22 12:00:00] RCON connection established
[2026-04-22 12:01:00] New player initialized: Steve
[2026-04-22 12:02:00] Steve bought plot 0
[2026-04-22 12:03:00] Steve gained $4000
[2026-04-22 12:04:00] Database saved: 1 players
```

---

## 🎯 Advanced Features (TODO)

- [ ] GUI Interface (tkinter)
- [ ] Discord Bot Integration
- [ ] Web Dashboard
- [ ] Taxes & Rent System
- [ ] Trading System
- [ ] Auctions
- [ ] Leaderboards

---

## ⚙️ Configuration

### Economy Settings
```python
STARTING_BALANCE=10000      # Starting money
PLOT_BUY_PRICE=5000        # Cost to buy plot
PLOT_SELL_PRICE=4000       # Money for selling
```

### Server Settings
```python
RCON_HOST=localhost        # Server IP
RCON_PORT=25575           # RCON Port
RCON_PASSWORD=password    # RCON Password
```

---

## 🚨 Troubleshooting

### "Failed to connect RCON"
```
1. Check server.properties: enable-rcon=true
2. Check RCON_PASSWORD in .env matches server
3. Check firewall allows port 25575
4. Check RCON_HOST is correct IP
```

### "Database not found"
```
Manager will auto-create data/citybuild.json
If not, create data/ folder manually
```

### "Commands not syncing"
```
1. Check RCON connection: logs/citybuild.log
2. Restart server
3. Run /reload in Minecraft
4. Check scoreboard: /scoreboard players list @s
```

---

## 📚 Code Examples

### Custom Command
```python
def custom_command(player_name):
    manager._init_player(player_name)
    # Do something with manager.players_data[player_name]
    manager._save_data()
```

### Add to Economy
```python
manager.add_money("Steve", 1000)
manager.remove_money("Steve", 500)
balance = manager.get_balance("Steve")
```

### Broadcast Message
```python
manager.broadcast("Hello everyone!", "green")
```

---

## 📄 License

Open Source - Free to use and modify

---

**Ready to script your CityBuild server!** 🚀

https://github.com/JuhlTV/CityBuild
