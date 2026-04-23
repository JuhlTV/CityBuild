# JSON Persistence System - CityBuild Plugin

## Übersicht

Das Plugin speichert alle Spielerdaten, Plot-Informationen und Farming-Statistiken in JSON-Format im `data/` Verzeichnis.

## Verzeichnisstruktur

```
plugins/CityBuild/
├── data/
│   ├── players/
│   │   ├── [UUID]_economy.json          # Geldguthaben
│   │   ├── [UUID]_farmdata.json         # Farming-Statistiken
│   │   └── ...
│   └── plots/
│       ├── plot_1.json                  # Plot-Daten mit gebauten Blöcken
│       ├── plot_2.json
│       └── ...
```

## Dateiformate

### Player Economy Data (`[UUID]_economy.json`)
```json
{
  "uuid": "player-uuid-here",
  "balance": 5000.50,
  "lastSaved": 1682534400000
}
```

### Player Farm Data (`[UUID]_farmdata.json`)
```json
{
  "uuid": "player-uuid-here",
  "blocksMinedTotal": 250,
  "coinsEarnedTotal": 1250.75,
  "currentStreak": 15,
  "farmerLevel": 3,
  "totalAchievementPoints": 350,
  "lastBlockBreakTime": 1682534400000
}
```

### Plot Data (`plot_N.json`)
```json
{
  "plotId": "plot_1",
  "ownerUUID": "player-uuid-here",
  "price": 1000.0,
  "createdAt": 1682534400000,
  "constructedBlocks": [
    {
      "x": 100,
      "y": 64,
      "z": 200,
      "material": "OAK_LOG",
      "data": ""
    },
    {
      "x": 101,
      "y": 65,
      "z": 200,
      "material": "OAK_LEAVES",
      "data": ""
    }
  ]
}
```

## Speicherungsmechanismen

### Automatisches Speichern
- **Economy**: Beim Hinzufügen/Entfernen von Geld
- **Farm Data**: Nach jedem Block-Break Event
- **Plot Data**: Bei Plot-Kauf/Verkauf oder Block-Platzierung
- **Auto-Save Scheduler**: Alle 5 Minuten (Fallback-Mechanismus)

### Manuelles Speichern
- **Plugin Shutdown**: Speichert alle Daten beim Beenden
- **Admin-Befehl**: Zukünftig `/admin save` zum manuellen Speichern

## Integration

### DataManager (Zentrale Verwaltung)
```java
DataManager dataManager = plugin.getDataManager();

// Geld speichern
dataManager.savePlayerEconomy(playerUUID, balance);

// Farm-Daten speichern
dataManager.savePlayerFarmData(playerUUID, farmDataMap);

// Plot speichern
dataManager.savePlot(plotId, ownerUUID, price, blockList);
```

### PlayerFarmDataManager
```java
PlayerFarmDataManager farmMgr = plugin.getFarmDataManager();

// Spielerdaten abrufen
PlayerFarmData data = farmMgr.getPlayerData(player);

// Einzelnen Spieler speichern
farmMgr.savePlayerData(player);

// Alle Spieler speichern
farmMgr.saveAllData();
```

### PlotManager
```java
PlotManager plotMgr = plugin.getPlotManager();

// Block zum Plot hinzufügen (wird automatisch gespeichert)
plotMgr.addBlockToPlot(plotId, x, y, z, material);

// Block vom Plot entfernen (wird automatisch gespeichert)
plotMgr.removeBlockFromPlot(plotId, x, y, z);

// Alle gebauten Blöcke abrufen
List<BlockData> blocks = plotMgr.getPlotBlocks(plotId);
```

## Wichtige Hinweise

1. **Gson-Format**: Alle JSON-Dateien verwenden Pretty-Printing für bessere Lesbarkeit
2. **Fehlerbehandlung**: Alle Speicheroperationen haben Exception-Handling mit Logging
3. **Null-Safety**: Spielerdaten werden on-demand geladen (lazy loading)
4. **UTF-8 Encoding**: Alle Dateien verwenden UTF-8 Encoding
5. **Automatische Ordner-Erstellung**: DataManager erstellt fehlende Verzeichnisse automatisch

## Daten-Persistierung

### Beim Server-Start
1. DataManager initialisiert `data/` Verzeichnis
2. Plots werden aus JSON geladen
3. Spielerdaten werden bei Bedarf geladen (wenn Spieler online geht)

### Während des Spielbetriebs
- Geldtransaktionen: Sofort gespeichert
- Block-Breaks: Nach jedem Block im Farm-World
- Plot-Operationen: Bei Kauf/Verkauf/Merge
- Achievements: Bei Erreichen gespeichert

### Beim Server-Stop
- **onDisable()** speichert alle verbleibenden Daten:
  1. EconomyManager.saveAllData()
  2. PlotManager.saveAllData()
  3. PlayerFarmDataManager.saveAllData()
  4. DataManager.saveAllData()

## Datenbank-Migration (Zukunft)

Das System ist designed, um leicht zu einer echten Datenbank migriert zu werden:
- Ersetze DataManager Implementierung mit SQL/MongoDB
- Rest des Codes bleibt unverändert (Interface-basiert)

## Backup und Recovery

Für Backups:
```bash
# Linux/Mac
cp -r plugins/CityBuild/data plugins/CityBuild/data_backup_$(date +%Y%m%d_%H%M%S)

# Windows
copy plugins\CityBuild\data plugins\CityBuild\data_backup_%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
```

---

**Version**: 1.0  
**Letztes Update**: 2024  
**Status**: Produktionsreife
