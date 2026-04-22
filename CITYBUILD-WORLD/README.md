# CityBuild - Vanilla Command Block World 🎮

Eine komplette **City Building Server** für **reines Vanilla Minecraft 1.20.1** - **OHNE Mods, OHNE Plugins!**

✅ **100% Vanilla compatible**  
✅ **Funktioniert auf jedem Server**  
✅ **Command Blocks + Scoreboards**  
✅ **Plots kaufen/verkaufen**  
✅ **Economy System**  
✅ **Admin Tools**  

---

## 📥 Quick Start (Physgun Server)

### 1. Download
```
GitHub: https://github.com/JuhlTV/CityBuild
Folder: CITYBUILD-WORLD/
```

### 2. Upload zu Physgun
```
Files → /home/container/world/datapacks/
Upload: datapack/ Folder
```

### 3. In-Game Setup
```
/reload
/function citybuild:init
```

### 4. Commands testen
```
/function citybuild:help
/function citybuild:plot/buy
/function citybuild:economy/balance
```

---

## 📂 Folder Struktur

```
CITYBUILD-WORLD/
├── INSTALLATION.md              ← Setup Anleitung
├── COMMAND-BLOCKS-GUIDE.md      ← Detaillierte Guide
├── SETUP_COMMANDS.txt           ← Alle Setup Commands
├── datapack/                    ← MCFunction Dateien
│   ├── pack.mcmeta
│   └── data/citybuild/functions/
│       ├── init.mcfunction
│       ├── load.mcfunction
│       ├── help.mcfunction
│       ├── plot/
│       ├── economy/
│       └── admin/
```

---

## 🎮 Kommandos (In-Game)

### Plot System
```
/function citybuild:plot/buy      - Plot kaufen ($5000)
/function citybuild:plot/sell     - Plot verkaufen ($4000)
/function citybuild:plot/info     - Deine Plots anzeigen
```

### Economy
```
/function citybuild:economy/balance    - Balance checken
```

### Admin
```
/function citybuild:admin/stats        - System Status
/function citybuild:admin/reset        - Alles zurücksetzen
```

---

## 🏗️ How It Works

**MCFunctions + Scoreboards = vollständiges System!**

```
Player Kommando
    ↓
MCFunction wird ausgeführt
    ↓
Scoreboard wird aktualisiert
    ↓
Feedback ans Spiel
    ↓
Daten persistent gespeichert
```

**Kein Plugin nötig!** Alles wird automatisch vom Vanilla Server gespeichert.

---

## ⚙️ Setup Details

### Scoreboards (automatisch erstellt)
```
cb_balance    = Spieler's Geld (Start: $10000)
cb_plots      = Anzahl Plots (Start: 0)
cb_admin      = Admin Flags (System)
```

### MCFunctions
- **init.mcfunction** - Erstellt Scoreboards & initialisiert
- **load.mcfunction** - Nachricht beim Laden
- **help.mcfunction** - Zeigt alle Commands
- **plot/buy.mcfunction** - Plot kaufen ($5000)
- **plot/sell.mcfunction** - Plot verkaufen ($4000)
- **plot/info.mcfunction** - Deine Plots
- **economy/balance.mcfunction** - Balance
- **admin/stats.mcfunction** - System Stats
- **admin/reset.mcfunction** - Alles zurücksetzen

---

## 💾 Daten Speicherung

Alle Spielerdaten werden in **Minecraft Scoreboards** gespeichert:

✅ Persistent über Server Restarts  
✅ Automatisch gespeichert (alle 5 Minuten)  
✅ Keine externe Datenbank nötig  
✅ Vollständig in Vanilla eingebaut  

---

## 🚀 Deployment auf Physgun

### Step 1: Download
```
Klone CityBuild Repository
oder download als ZIP
```

### Step 2: Upload zu Physgun
```
Physgun Dashboard
  → Files
  → /home/container/world/datapacks/
  → Upload CITYBUILD-WORLD/datapack/ Folder
```

### Step 3: Server Restart
```
Physgun Dashboard
  → Server
  → Restart
```

### Step 4: Initialisierung
```
Connect zu Server als OP
/reload
/function citybuild:init
/function citybuild:help
```

---

## 🛠️ Troubleshooting

### MCFunctions funktionieren nicht?
```
/reload
/function citybuild:init
/gamerule commandBlockOutput true
```

### Command Blocks werden nicht ausgeführt?
```
/gamerule randomTickSpeed 1
/gamerule commandBlockRedstoneOutput true
```

### Scoreboards existieren nicht?
```
/scoreboard players list @s
(Sollte cb_balance, cb_plots, cb_admin zeigen)
```

### Balance ist 0?
```
/function citybuild:admin/reset
(Setzt alle auf $10000 zurück)
```

---

## 📊 Statistik

```
MCFunction Files:       8
Scoreboards:            3
Starting Money:         $10,000
Plot Cost:              $5,000
Plot Sell Value:        $4,000
Command Block Blocks:   ∞ (Skalierbar)
```

---

## ✅ Test Checklist

- [ ] World lädt fehlerfrei
- [ ] Scoreboards erstellt (`/scoreboard players list @s`)
- [ ] Commands funktionieren (`/function citybuild:help`)
- [ ] Plot Buy funktioniert ($5000 weg)
- [ ] Plot Sell funktioniert ($4000 dazu)
- [ ] Balance wird angezeigt
- [ ] Admin Reset funktioniert
- [ ] Daten persistieren nach Server Restart

---

## 🎯 Nächste Schritte

Nach dem Setup kannst du:

1. **Command Blocks bauen** - Mit Buttons für schnelle Commands
2. **Redstone Schaltungen** - Für Automatisierung
3. **Arenen bauen** - Mini-Games für Plotverkauf
4. **Shop NPC** - Mit Command Blöcken simulieren
5. **Plot Grenzen** - Mit Barriers markieren

---

## 💡 Pro-Tipps

### Schnelle Commands
Baue einen Command Block mit:
```
/function citybuild:plot/buy
```
Schalte ihn mit einem Button ein - **Instant Plot Kauf!**

### Admin Panel
Baue einen isolierten Admin-Bereich mit Command Blöcken für:
- `/function citybuild:admin/stats`
- `/function citybuild:admin/reset`
- Spieler Verwaltung

### Redstone Automation
Nutze Repeater + Command Blocks für Automation:
```
Clock → Command Block → Function
(Wiederholt alle 4 Ticks)
```

---

## 📖 Dokumentation

- **INSTALLATION.md** - Setup Guide für Physgun
- **COMMAND-BLOCKS-GUIDE.md** - Detaillierte Command Block Anleitung
- **SETUP_COMMANDS.txt** - Alle Setup Commands zum Copy-Paste

---

## ⚖️ Lizenz & Info

**Vanilla Minecraft 1.20.1 - Compatible**

- Keine Mods nötig ✓
- Keine Plugins nötig ✓
- Keine externe Dependencies ✓
- 100% Open Source ✓

---

**Ready to deploy! 🚀**

Für Fragen oder Issues: https://github.com/JuhlTV/CityBuild/issues
