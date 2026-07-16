<div align="center">

# Potion's Belt

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-62B47A)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.18.4%2B-C8A87A)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-GPL--3.0%2B-blue)](LICENSE)

*A dedicated 3x9 potion container with fast, column-based drinking — no more digging through the hotbar.*

</div>

---

## What it does

Potion's Belt adds a single item: a 27-slot belt that accepts drinkable
potions and empty bottles, plus a fast way to drink from it without
opening any GUI mid-fight.

| Feature | Description |
|---|---|
| **Drink on right click** | The vanilla 1.6s drink animation and timing — no balance changes, just faster access. |
| **Column-based loadout** | 3 rows x 9 columns. Dedicate each column to one potion type; drinking only ever replaces the exact slot drunk, so a column's contents never drift. |
| **Sticky column selection** | Hold the remappable "Column Select" modifier + a hotbar key (1-9) or scroll to pick a column. Your pick is remembered as the default for every future drink, not just the current one. |
| **Row and belt-wide fallback** | If the selected column's top potion is gone, the belt falls back to row 2, then row 3, of that column; if the whole column is empty, it falls back further to the first potion anywhere in the belt, only failing outright if the belt has none left at all. |
| **HUD preview** | An icon + name next to the hotbar always shows exactly which potion is about to be drunk. |
| **Bottles return to the belt** | Drinking turns only that slot into an empty bottle, in place — nothing else shifts. |
| **Two ways to open the GUI** | A dedicated keybind (either hand), or <kbd>E</kbd> while the belt is specifically your **main-hand** item. |
| **Custom sounds** | Distinct sounds for opening the belt and for each drink starting/ending. |

> [!TIP]
> All of Potion's Belt's keybinds are **unbound by default** and fully
> remappable under **Options > Controls > Potions Belt** — see the
> [Keybinds wiki page](../../wiki/Keybinds) before your first drink.

Full behavior spec and edge cases: [PLAN.md](PLAN.md). Full session history
and the reasoning behind every design decision: [NOTES.md](NOTES.md).

---

## Requirements

| | |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.4+ |
| Fabric API | 0.141.3+1.21.11 |
| Java | 21+ |

> [!NOTE]
> The belt's consumption logic runs server-side, so multiplayer requires
> the mod on both the server and every client — see the wiki's
> [Installation](../../wiki/Installation) page.

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for the same
   version and drop it in your `mods/` folder.
3. Download Potion's Belt and drop it in `mods/` too.

See the [wiki](../../wiki) for a full usage guide, keybind setup, and
column-loadout tips.

---

## Languages

| Language | Locale | File |
|---|---|---|
| 🇬🇧 English (US) | `en_us` | [`en_us.json`](potions-belt-fabric-1.21.11/src/main/resources/assets/potions-belt/lang/en_us.json) |
| 🇵🇹 Portuguese (Portugal) | `pt_pt` | [`pt_pt.json`](potions-belt-fabric-1.21.11/src/main/resources/assets/potions-belt/lang/pt_pt.json) |

> [!NOTE]
> Want to add or fix a translation? See the wiki's
> [Translations](../../wiki/Translations) page.

---

## Loader: Fabric (decided, do not revisit)

Why Fabric over (Neo)Forge — both can implement everything this mod needs
(custom item, container menu, client key interception, custom payloads), so
the choice was practical:

- A working Fabric Gradle template with coherent pinned versions already
  existed for this project.
- Lighter toolchain and faster client launches for iterating on input/GUI
  behavior.
- Current, first-class documentation (docs.fabricmc.net) for exactly the
  patterns this mod uses: screen handlers, custom payloads, mixins.
- Classic Forge is legacy for 1.21+ (NeoForge is its successor), and
  NeoForge's item-handler advantage is irrelevant here since vanilla data
  components already cover item-stored inventories.

---

## Repository layout

```
PLAN.md / NOTES.md / CLAUDE.md    project docs and session memory
wiki/                              wiki pages, drafted locally for now
potions-belt-fabric-1.21.11/       the Fabric mod (Gradle project root)
```

---

## Build & run

Requires Java 21 (JDK). From `potions-belt-fabric-1.21.11/`:

**Linux / macOS**
```bash
./gradlew build        # produces the mod jar in build/libs/, runs unit tests
./gradlew runClient    # launches a dev Minecraft client with the mod
```

**Windows**
```bat
gradlew.bat build
gradlew.bat runClient
```

> [!TIP]
> The built jar ends up in `build/libs/` — drop that jar (not the
> `-sources.jar`) into your `mods/` folder to test a local build.

---

## Contributing

Contributions, bug reports, and translations are welcome — see
[CONTRIBUTING.md](CONTRIBUTING.md) for the workflow, and the
[Code of Conduct](CODE_OF_CONDUCT.md) for community standards. Security
issues: see [SECURITY.md](SECURITY.md).

> [!WARNING]
> Target the `dev` branch, not `main`, when opening a pull request — see
> Branches below.

---

## Branches

Development happens on `dev`; `main` holds the stable state and is updated
by merging `dev` when a milestone is ready.

---

## Status

**In active development.** Core gameplay (milestones 1-6) is complete and
in-game verified; polish, docs, and configurability are ongoing.

- [x] Planning: loader decision, architecture, edge cases
- [x] Repository setup
- [x] Skeleton builds and runs
- [x] GUI (3x9 menu, potion-only filter, persistence)
- [x] Drinking via right click
- [x] Column selection: sticky default, modifier-gated hotbar keys + scroll,
      HUD preview
- [x] Polish: sounds, column-selection feedback, edge-case pass
- [ ] Docs pass: community-health files, wiki (in progress)
- [ ] Configurable settings (scroll direction, no-pre-selector mode,
      combined inventory+belt tab)
- [x] Localization: community translation process + initial languages
- [ ] Belt skin/texture applier (idea stage, not scoped)

---

## License

<div align="center">

[GPL-3.0+](LICENSE)

</div>
