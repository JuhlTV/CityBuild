# CityBuild Command Block Setup Guide

## 📋 Komplette Anleitung zum Erstellen der World

### Voraussetzungen
- Minecraft Java 1.20.1
- Creative Mode
- OP Status
- Command Blocks aktiviert

---

## Phase 1: World Vorbereitung

### Step 1: Neue World erstellen

```
Minecraft Launcher
  → Create New World
  → Name: "CityBuild"
  → Game Mode: Creative
  → Version: 1.20.1
  → Enable Cheats: ON
  → Create
```

### Step 2: Grundlagen einrichten

**Im Spiel ausführen (als OP):**

```mcfunction
# Gamerules setzen
/gamerule commandBlockOutput true
/gamerule commandBlockRedstoneOutput true
/gamerule sendCommandFeedback true
/gamerule showDeathMessages true

# Difficulty
/difficulty peaceful

# World Spawn
/setworldspawn 0 64 0
```

### Step 3: Scoreboards erstellen

```mcfunction
# Economy
/scoreboard objectives add cb_balance dummy "CityBuild Balance"
/scoreboard objectives add cb_plots dummy "Player Plots"  
/scoreboard objectives add cb_admin dummy "Admin Stats"

# Stats
/scoreboard objectives add cb_version dummy "Version"

# Set default balance
/scoreboard players set @a cb_balance 10000
/scoreboard players set version cb_version 1
```

---

## Phase 2: Lobby Setup (0, 64, 0)

Gehe zu Koordinaten: **X: 0, Y: 64, Z: 0**

### Step 1: Bodenplatte bauen

```mcfunction
# Große Plattform für Lobby
/fill 0 63 0 50 63 50 oak_planks
/fill 0 64 0 50 64 50 air
```

### Step 2: Wegweiser aufstellen (Schilder mit Commands)

Baue ein Schild bei **(10, 65, 10)** mit Text:

```
[Plots]
/function
citybuild:help
Click me!
```

---

## Phase 3: Command Block Positionen

### Plot Commands
Baue Command Blocks bei diesen Positionen:

#### Plot Buy (10, 66, 10)
```
Type: Repeat
Mode: Unconditional
Redstone: Always Active
Command: scoreboard players remove @s cb_balance 5000
Command 2: scoreboard players add @s cb_plots 1
Command 3: tellraw @s {"text":"Plot #[ID] purchased!","color":"green"}
```

#### Plot Sell (10, 66, 15)
```
Type: Repeat
Conditional: if Score @s cb_plots >= 1
Command: scoreboard players add @s cb_balance 4000
Command 2: scoreboard players remove @s cb_plots 1
```

#### Economy Balance (10, 66, 20)
```
Type: Repeat
Command: tellraw @s {"text":"Balance: $[SCORE]","color":"gold"}
```

---

## Phase 4: Redstone Schaltungen

### Automated System mit Redstone

```
Clock Circuit (optional):
  [Repeater] → [Repeater] → [Comparator] → [Command Block]
  (Schleife für Automation)
```

---

## Phase 5: MCFunctions einbinden

Die Command Blocks können stattdessen auch **Functions** aufrufen:

```mcfunction
# In Command Block:
/function citybuild:plot/buy
/function citybuild:economy/balance
/function citybuild:admin/stats
```

**Das braucht aber noch die Functions zu erstellen!**

---

## 🎯 Vereinfachte Quick-Installation

**Wenn du möchtest, erstelle ich einfach:**

### Option A: Pre-built World
- Complete World mit allen Command Blocks
- Download als ZIP
- Hochladen zu Physgun
- Fertig!

### Option B: Functions + Manual Blocks
- Alle MCFunctions bereitellen
- Du platzierst Command Blocks manuell
- Commands kopieren und einfügen

### Option C: Full Script
- Ein setup.txt mit allen Commands
- Kopieren → Paste → Fertig

**Welche Option magst du?** 🚀

---

## 📍 Fertige Koordinaten-Liste

```
Lobby:                  (0, 64, 0)
Plot Buy Block:         (10, 66, 10)  
Plot Sell Block:        (10, 66, 15)
Economy Block:          (10, 66, 20)
Admin Panel:            (-50, 66, 0)
Redstone Clock:         (-100, 65, 0)
```

---

## ✅ Test-Checklist

Nach Setup, überprüfe:

- [ ] World lädt fehlerfrei
- [ ] Scoreboards existieren (`/scoreboard players list @s`)
- [ ] Command Blocks aktiviert (`/gamerule commandBlockOutput`)
- [ ] Plot Buy funktioniert ($5000 abgezogen)
- [ ] Plot Sell funktioniert ($4000 addiert)
- [ ] Balance angezeigt wird
- [ ] Admin Commands funktionieren
- [ ] Daten persistieren nach Server Restart

---

**Sollen ich die World fertig bauen oder möchtest du es manuell machen?** 🎮
