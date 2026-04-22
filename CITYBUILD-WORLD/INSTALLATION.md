# CityBuild - Vanilla Command Block World

## 🎮 Installation auf Physgun Server

### Step 1: World herunterladen/erstellen

```
Diese World enthält ein komplettes CityBuild-System mit Command Blocks.
```

### Step 2: Zu Physgun hochladen

**Über File Manager:**
```
Physgun Dashboard → Files → File Manager
  → Navigate to: /home/container/
  → Upload: world.zip oder world-Folder
  → Extract if ZIP
```

**Nach Upload muss die Ordnerstruktur so aussehen:**
```
/home/container/world/
  ├── level.dat
  ├── region/
  ├── datapacks/
  ├── data/
  └── CityBuild/  ← Dieser Ordner mit Command Blocks!
      ├── lobby.nbt
      ├── admin_panel.nbt
      └── plots/
```

### Step 3: Server neustarten

```
Physgun Dashboard → Server → Restart
```

### Step 4: In-Game Setup (als OP)

**Ingame befehle ausführen:**
```
/reload
/function citybuild:init
```

### Step 5: Testen

```
/function citybuild:help
```

---

## 🏗️ World-Struktur

### Main Lobby (-100, 64, 0)
```
Hier spawnen die Spieler
Mit Schildern zu verschiedenen Commands
```

### Admin Panel (-100, 64, -50)
```
Command Blocks für:
  - System Reset
  - Stats anzeigen
  - Player Management
```

### Plot System
```
/function citybuild:plot/buy    - Plot kaufen ($5000)
/function citybuild:plot/sell   - Plot verkaufen ($4000)
/function citybuild:plot/info   - Plot Info
/function citybuild:plot/list   - Alle Plots
```

---

## 🔧 Command Blocks Locations

Alle Command Blocks sind bei diesen Koordinaten:

### Plot kaufen
```
X: -100, Y: 70, Z: -20
Command: /function citybuild:plot/buy
Type: Repeat, Unconditional, Always Active
```

### Economy System
```
X: -100, Y: 70, Z: -30
Command: /function citybuild:economy/balance
Type: Repeat, Unconditional, Always Active
```

### Admin Panel  
```
X: -100, Y: 70, Z: -40
Command: /function citybuild:admin/stats
Type: Repeat, Unconditional, Always Active
```

---

## ⚙️ Scoreboards (Automatisch erstellt)

Die Scoreboards werden beim Start automatisch erstellt:

```
/scoreboard objectives add cb_balance dummy "CityBuild Balance"
/scoreboard objectives add cb_plots dummy "Player Plots"
/scoreboard objectives add cb_admin dummy "Admin Stats"
```

---

## 📊 Datenspeicherung

Alle Spielerdaten werden in Minecraft Scoreboards gespeichert:
- `cb_balance` = Spieler's Geld
- `cb_plots` = Anzahl Plots
- `cb_admin` = Admin Flags

Diese Daten persistieren automatisch!

---

## 🚨 Troubleshooting

### Commands funktionieren nicht?
- ✓ `/reload` ausführen
- ✓ `/function citybuild:init` ausführen
- ✓ Creative Mode aktivieren
- ✓ OP Status haben

### Command Blocks werden nicht ausgeführt?
- ✓ Redstone Power Check (rote Leitungen aktiv?)
- ✓ Block-Tick-Rate: `/gamerule randomTickSpeed 1`
- ✓ Command Block aktivieren: `/gamerule commandBlockOutput true`

### Plots/Balance reset?
- ✓ `/scoreboard players list @s` - Check Scores
- ✓ Data automatisch saved alle 5 Minuten
- ✓ Nach Server Restart sollte alles da sein

---

## 🎯 Quick Commands (In-Game)

```
/function citybuild:help              → Hilfe anzeigen
/function citybuild:plot/buy         → Plot kaufen
/function citybuild:plot/sell        → Plot verkaufen  
/function citybuild:economy/balance  → Balance check
/function citybuild:admin/reset      → Alles zurücksetzen
```

---

## 💾 World Backup

Vor jedem Test die World backuppen:

```
Physgun Files → /home/container/
  → Rechtsklick auf "world" → Download als ZIP
  → Speicher lokal
```

---

**World ist jetzt 100% vanilla-compatible und ready to go!** 🚀
