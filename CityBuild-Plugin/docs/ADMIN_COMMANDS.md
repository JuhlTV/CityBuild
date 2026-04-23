# CityBuild Admin Commands

Admin commands for plot management and expansion. All commands require OP status.

---

## Plot Management Commands

### `/citybuild admin plot expand`
Expand a player's plot in a cardinal direction.

**Usage:** `/citybuild admin plot expand <player> <direction> [blocks]`

**Parameters:**
- `<player>` - Target player name
- `<direction>` - One of: `north`, `south`, `east`, `west`
- `[blocks]` - Number of blocks to expand (default: 5)

**Example:** `/citybuild admin plot expand Steve north 10`

**Effects:**
- Extends plot terrain in specified direction
- Regenerates borders and spawn platform
- Updates plot boundaries in database

---

### `/citybuild admin plot resize`
Resize a player's plot to exact dimensions.

**Usage:** `/citybuild admin plot resize <player> <width> <height>`

**Parameters:**
- `<player>` - Target player name
- `<width>` - New width in blocks (min: 10, max: 100)
- `<height>` - New height in blocks (min: 10, max: 100)

**Example:** `/citybuild admin plot resize Steve 50 50`

**Effects:**
- Sets plot to exact dimensions
- Regenerates terrain and borders
- Replaces spawn platform
- Persists changes immediately

---

### `/citybuild admin plot clear`
Clear all player-built blocks from a plot (keep borders and spawn platform).

**Usage:** `/citybuild admin plot clear <player>`

**Parameters:**
- `<player>` - Target player name

**Example:** `/citybuild admin plot clear Steve`

**Effects:**
- Removes all blocks above Y=-60 within plot
- Keeps grass surface and borders intact
- Spawn platform regenerated

---

### `/citybuild admin plot delete`
Delete a player's plot completely (terrain + borders + ownership).

**Usage:** `/citybuild admin plot delete <player>`

**Parameters:**
- `<player>` - Target player name

**Example:** `/citybuild admin plot delete Steve`

**Effects:**
- Removes all terrain and borders from world
- Deletes plot from database
- Clears player ownership
- Clears all plot members
- ⚠️ **Irreversible** - use with caution!

---

### `/citybuild admin plot info`
Display detailed information about a player's plot.

**Usage:** `/citybuild admin plot info <player>`

**Parameters:**
- `<player>` - Target player name

**Example:** `/citybuild admin plot info Steve`

**Output:**
```
========== Plot Information ==========
Plot #1 | Owner: Steve
Size: 16 x 16 blocks | Area: 256 m²
Location: X=0, Z=0
Premium: No
Members: 2 (Steve, Alex)
Created: 2026-04-23 14:30:45
```

---

## Premium & Access Management

### `/citybuild admin plot premium`
Toggle premium status for a player's plot.

**Usage:** `/citybuild admin plot premium <player> [on/off]`

**Parameters:**
- `<player>` - Target player name
- `[on/off]` - Toggle to `on` or `off` (optional: defaults to toggle current status)

**Example:** `/citybuild admin plot premium Steve on`

**Effects:**
- Premium plots may have special benefits (configurable)
- Status persists in database
- Displayed in plot info

---

## Navigation & Teleportation

### `/citybuild admin plot teleport`
Teleport to a player's plot spawn location.

**Usage:** `/citybuild admin plot teleport <player>`

**Parameters:**
- `<player>` - Target player name

**Example:** `/citybuild admin plot teleport Steve`

**Effects:**
- Admin is teleported to plot spawn (Y=-58)
- Shows in chat: "✓ Teleported to Steve's plot"

---

## Listing & Information

### `/citybuild admin plot list`
List all plots or plots owned by a specific player.

**Usage:** `/citybuild admin plot list [player]`

**Parameters:**
- `[player]` - Optional player name (lists all plots if omitted)

**Example:**
```
/citybuild admin plot list         # Lists ALL plots
/citybuild admin plot list Steve   # Lists Steve's plots only
```

**Output:**
```
=== Plots (3 total) ===
Plot #1: Steve (16x16, Premium)
Plot #2: Alex (20x20, Standard)
Plot #3: Sam (16x16, Standard)
```

---

## Help & Reference

### `/citybuild admin plot help`
Display help for plot admin commands.

**Usage:** `/citybuild admin plot help`

---

## Quick Reference Table

| Command | Syntax | Permission |
|---------|--------|-----------|
| expand | `/cb admin plot expand <player> <dir> [blocks]` | OP |
| resize | `/cb admin plot resize <player> <width> <height>` | OP |
| clear | `/cb admin plot clear <player>` | OP |
| delete | `/cb admin plot delete <player>` | OP |
| info | `/cb admin plot info <player>` | OP |
| premium | `/cb admin plot premium <player> [on/off]` | OP |
| teleport | `/cb admin plot teleport <player>` | OP |
| list | `/cb admin plot list [player]` | OP |

---

## Notes

- **Y-Level**: All plots are generated at Y=-60 (surface) with terrain from Y=-63 to -60
- **Default Size**: New plots are 16×16 blocks
- **Minimum Size**: 10×10 blocks
- **Maximum Size**: 100×100 blocks
- **Spacing**: 2 blocks between plots in grid
- **Grid Layout**: 10 plots per row

---

## Troubleshooting

**Plot not found:**
- Verify player exists and has a plot
- Use `/citybuild admin plot list <player>` to check

**Terrain generation failed:**
- Check world has write permissions
- Verify Y=-60 area is accessible (no protected regions)
- Check server logs for errors

**Member management:**
- Use `/citybuild member add <player>` and `/citybuild member remove <player>` as plot owner
- Admins can view members with `/citybuild admin plot info <player>`
