# 🚀 CityBuild v2.6.0 - Economy System & Command Guide

## Die modernste Minecraft CityBuild in Deutschland 🇩🇪

Vollständige Dokumentation aller Economy-Befehle, Admin-Funktionen und System-Features.

---

## 📊 SPIELER-BEFEHLE (Economy)

### 1. Balance anzeigen
```
/citybuild economy balance [player]
Alias: /cb economy balance
```
- Zeigt das Guthaben des Spielers (oder eines anderen)
- Schöne Formatierung mit Emojis und Farben
- Beispiel Output:
```
═══════════════════════════════════
  💰 Dein Guthaben
  $1.234.567,00
═══════════════════════════════════
```

### 2. Geld überweis en (Pay)
```
/citybuild economy pay <spieler> <betrag>
Alias: /cb economy pay
```
- Überweis Geld an einen anderen Spieler
- Beide Spieler erhalten Bestätigung
- Cooldown: 500ms (verhindert Spam)
- Sicherheits-Features:
  - ✅ Kann sich selbst kein Geld schicken
  - ✅ Balance-Check vor Transfer
  - ✅ Gültigkeit des Spielers prüfen
  - ✅ Positive Beträge erforderlich
- Transaktionen werden geloggt
- Beispiel:
```
/cb economy pay PlayerXYZ 5000
✓ $5.000 an PlayerXYZ überwies
```

### 3. Top 10 Leaderboard
```
/citybuild economy top
Alias: /cb economy top / leaderboard
```
- Zeigt die 10 reichsten Spieler
- Mit Medaillen: 🥇 🥈 🥉
- Beispiel Output:
```
════════════════════════════════════
  🏆 TOP 10 REICHSTE SPIELER
════════════════════════════════════
🥇 1. PlayerA - $10.000.000
🥈 2. PlayerB - $8.500.000
🥉 3. PlayerC - $7.250.000
   4. PlayerD - $6.100.000
...
```

---

## 👤 ADMIN-BEFEHLE (Economy)

### 1. Geld hinzufügen
```
/citybuild economy add <spieler> <betrag>
Alias: /cb economy add
Benötigung: OP-Status
```
- Fügt einem Spieler sofort Geld hinzu
- Beide Spieler erhalten Bestätigung
- Wird in Transaktionslog unter ADMIN_ADJUSTMENT geloggt
- Sicherheits-Features:
  - ✅ OP-Check erforderlich
  - ✅ Spieler muss online sein
  - ✅ Positive Beträge nur
- Beispiel:
```
/cb economy add PlayerXYZ 10000
✓ $10.000 zu PlayerXYZ hinzugefügt
```

### 2. Geld entfernen
```
/citybuild economy remove <spieler> <betrag>
Alias: /cb economy remove
Benötigung: OP-Status
```
- Entfernt Geld von einem Spieler
- Beispiel für Strafgebühren oder Rückerstattungen
- Wird geloggt
- Sicherheits-Features:
  - ✅ OP-Check erforderlich
  - ✅ Balance bleibt nicht-negativ
  - ✅ Validierter Betrag

### 3. Guthaben setzen
```
/citybuild economy set <spieler> <betrag>
Alias: /cb economy set
Benötigung: OP-Status
```
- Setzt das Guthaben auf einen exakten Betrag
- Nützlich für Korrekturen
- Berechnet die Differenz und loggt sie
- Beispiel:
```
/cb economy set PlayerXYZ 50000
✓ PlayerXYZs Balance auf $50.000 gesetzt
```

### 4. Geld zwischen Spielern übertragen
```
/citybuild economy transfer <von> <zu> <betrag>
Alias: /cb economy transfer
Benötigung: OP-Status
```
- Admin kann Geld ohne Spieler-Zustimmung übertragen
- Beide müssen online sein
- Beide Seiten werden geloggt (Sender und Empfänger)
- Sicherheits-Features:
  - ✅ Balance-Check vom Sender
  - ✅ Gültige Spieler-Namen
  - ✅ Positive Beträge

---

## 📚 HELP-SYSTEM (Überblick)

### Hauptmenü anzeigen
```
/citybuild help
/cb help
```
Zeigt 6 Kategorien:
- 💰 ECONOMY - Balance, Pay, Transfers
- 📍 PLOTS - Kauf, Verkauf, Management
- 🌍 WARPS - Teleportation, Waypoints
- 🏪 SHOP - Kaufen, Verkaufen, Items
- ⚔️ PvP WORLDS - Farming, Rewards
- 🔑 ADMIN - Verwaltung, Moderation

### Kategorie-Hilfe
```
/cb help economy     # Economy Befehle
/cb help plot        # Plot Management
/cb help warp        # Warp System
/cb help shop        # Shop & Trading
/cb help admin       # Admin Befehle
/cb help all         # Alles anzeigen
```

---

## 🔐 SICHERHEITS-FEATURES

### Input-Validierung
- ✅ Alle Beträge müssen positive Integer sein
- ✅ Spieler-Namen werden validiert
- ✅ UUIDs werden überprüft
- ✅ Balance wird nicht-negativ gehalten

### Anti-Spam
- ✅ 500ms Cooldown auf `/economy pay`
- ✅ Verhindert Spam-Transfers
- ✅ Benutzerfreundliche Cooldown-Meldung

### Permission-System
- ✅ Admin-Commands erfordern OP-Status
- ✅ Spieler können nur ihre eigenen Überweisungen machen
- ✅ Keine negativen Beträge möglich

### Transaktions-Logging
- ✅ Alle Transfers werden geloggt
- ✅ ADMIN_ADJUSTMENT für Admin-Actions
- ✅ Sender- und Empfänger-Log
- ✅ Vollständiges Audit Trail

---

## 💰 WIRTSCHAFTS-SYSTEM

### Geldquellen (Spieler)
1. **Daily Reward** - $500 + Streak-Bonus
2. **Farm-Welt** - $10 pro Block
3. **PvP-Welt** - $50 pro Kill
4. **Playtime** - $250 pro Stunde
5. **Quests** - Variabel je Quest
6. **Auktion** - Spieler-zu-Spieler

### Geldausgaben
1. **Plot-Kauf** - $50.000 (einmalig)
2. **Plot-Steuer** - $500/Tag (Premium: $1.000)
3. **Shop** - 23 verschiedene Items
4. **Enchanting** - Tier 1-3 Enchantments
5. **Trading** - Spieler-zu-Spieler

### Transaktions-Typen
```
EARN                  # Grundverdienst
SPEND                 # Ausgaben
TRANSFER              # Spieler-zu-Spieler
FARM_REWARD           # Farm-Bonus
PVP_REWARD            # Kill-Bonus
DAILY_REWARD          # Täglich Bonus
SHOP_BUY              # Shop-Kauf
SHOP_SELL             # Item-Verkauf
PLOT_BUY              # Plot-Kauf
PLOT_SELL             # Plot-Verkauf
AUCTION               # Auktions-Transfer
ADMIN_ADJUSTMENT      # Admin-Action
```

---

## 🎮 BEISPIEL-SZENARIEN

### Szenario 1: Spieler A überweist Geld an Spieler B
```
Spieler A: /cb economy pay PlayerB 5000
✓ $5.000 an PlayerB überwies

Spieler B: (erhält Meldung)
✓ Du hast $5.000 von PlayerA erhalten
```
- Wird als TRANSFER geloggt
- Cooldown wird aktiviert
- Balance wird überprüft

### Szenario 2: Admin gibt Spieler Bonus
```
Admin: /cb economy add PlayerC 10000
✓ $10.000 zu PlayerC hinzugefügt

Spieler C: (erhält Meldung)
✓ Admin hat dir $10.000 gegeben
```
- Wird als ADMIN_ADJUSTMENT geloggt
- Sofortige Gutschrift
- Audit Trail wird erstellt

### Szenario 3: Admin korrigiert Balance
```
Admin: /cb economy set PlayerD 25000
✓ PlayerDs Balance auf $25.000 gesetzt
```
- Alte Balance: $15.000
- Neue Balance: $25.000
- Differenz (+$10.000) wird geloggt

### Szenario 4: Top 10 ansehen
```
/cb economy top
🥇 1. RichPlayer - $50.000.000
🥈 2. WealthyDude - $42.500.000
🥉 3. MoneyMaker - $38.750.000
...
```

---

## 🔍 FEHLERBEHANDLUNG

### Häufige Fehler und Lösungen

**Error: "Spieler nicht gefunden!"**
- Spieler ist nicht online
- Spalter-Name falsch geschrieben
- Warte bis Spieler online kommt

**Error: "Du hast nicht genug Geld!"**
- Balance ist zu niedrig
- Verdiene mehr Geld durch Farmen
- Leihe dir von anderen Spielern

**Error: "Du kannst dir selbst kein Geld schicken!"**
- Zirkular-Transfers sind nicht erlaubt
- Sende das Geld an einen anderen Spieler

**Error: "Der Betrag muss größer als 0 sein!"**
- Negativer oder Zero-Betrag eingegeben
- Nur positive Beträge erlaubt

**Error: "⏳ Bitte warte einen Moment!"**
- Cooldown ist noch aktiv
- Warte 500ms bis zur nächsten Überweisung

---

## ⚙️ TECHNISCHE DETAILS

### Performance
- ✅ EconomyCommandHandler - Zentrale Verwaltung
- ✅ Transaktions-Caching via CacheManager
- ✅ Optimierte Balance-Abfragen
- ✅ Async Logging für I/O-Operationen

### Persistierung
- Speicherort: `data/players.json`
- Format: GSON JSON
- Automatische Backups bei Überschreib
- Atomic File Operations (Temp-Datei Pattern)

### Integration
- ✅ EconomyManager - Zentrale Geldverwaltung
- ✅ TransactionManager - Transaktions-Logging
- ✅ ConfigManager - Konfigurierbare Werte
- ✅ AuditManager - Sicherheits-Tracking
- ✅ CacheManager - Performance-Optimierung

---

## 📈 ZUKUNFTS-FEATURES (Geplant)

- [ ] Banking-GUI für grafische Verwaltung
- [ ] 2FA für große Transfers
- [ ] Zinsgebühren auf eingezahlte Gelder
- [ ] Spieler-Spardose (Piggy Bank)
- [ ] Geldjahr-Statistiken
- [ ] Automatische Steuereinnreibung
- [ ] Economy-Inflations-Control
- [ ] Reichtums-Bestrafungs-System
- [ ] Kriminellen-Economy (Schwarzmarkt)
- [ ] Real-Time Balance-Leaderboard im Menü

---

## 🌟 WHY v2.6.0 IS THE BEST

✅ **Vollständig** - Economy-System mit allen Funktionen
✅ **Sicher** - Input-Validierung, Anti-Spam, Permission-System
✅ **Intuitiv** - Einfache Befehle mit klaren Fehlermeldungen
✅ **Schnell** - Performance-optimiert mit Caching
✅ **Nachverfolgbar** - Audit Trail für jede Transaktion
✅ **Admin-freundlich** - Umfangreiche Admin-Befehle
✅ **Spieler-freundlich** - Schöne UI mit Emoji & Farben
✅ **Dokumentiert** - Vollständige Dokumentation
✅ **Erweiterbar** - Einfach neue Features hinzufügbar
✅ **Deutsch** - Vollständig auf Deutsch

---

**Status**: ✅ v2.6.0 - Produktionsreif
**Release**: April 23, 2026
**Developer**: Julian
**Server**: Paper 1.21.1 / Physgun

**"Das modernste Minecraft CityBuild Economy System in Deutschland!"** 🚀🇩🇪
