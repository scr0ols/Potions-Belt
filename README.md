# Potion's Belt

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-62B47A)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.18.4%2B-C8A87A)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

A Minecraft mod that adds a **potion belt**: a wearable-in-hand container
item with a chest-like 3x9 GUI (27 slots) that only accepts drinkable
potions, built for players who want fast, deliberate access to a whole
loadout of potions without digging through the hotbar or inventory.

## What it does

- **Right click** with the belt in hand to drink a potion — the vanilla
  1.6s drink animation and timing, no balance changes.
- **Column selection**: hold the remappable "Column Select" modifier while
  pressing a hotbar key (1-9) or scrolling to pick which of the belt's 9
  columns to drink from. The choice becomes your **sticky default** — it
  stays selected across drinks until you change it, not just for the one
  in progress.
- **Row fallback**: if the chosen column's top potion is gone, the belt
  automatically falls back to row 2, then row 3, before giving up.
- **HUD preview**: a small icon and name, next to the hotbar, always shows
  which potion is about to be drunk — so you never have to guess.
- **Empty bottles return to the belt**, in place — drinking a potion turns
  only that slot into a bottle; every other slot is untouched, so your
  column-based loadout never drifts.
- **Open the belt's GUI** with the dedicated "Open Belt Menu" keybind, or
  just press <kbd>E</kbd> while the belt is your held item.
- **Custom sounds** for opening the belt and for each drink starting/ending.

Full behavior spec and edge cases: [PLAN.md](PLAN.md). Full session history
and the reasoning behind every design decision: [NOTES.md](NOTES.md).

## Requirements

| | |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.4+ |
| Fabric API | 0.141.3+1.21.11 |
| Java | 21+ |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for the same
   version and drop it in your `mods/` folder.
3. Download Potion's Belt and drop it in `mods/` too.

See the [wiki](../../wiki) for a full usage guide, keybind setup, and
column-loadout tips.

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

## Repository layout

```
PLAN.md / NOTES.md / CLAUDE.md    project docs and session memory
wiki/                              wiki pages, drafted locally for now
potions-belt-fabric-1.21.11/       the Fabric mod (Gradle project root)
```

## Build & run

Requires Java 21 (JDK). From `potions-belt-fabric-1.21.11/`:

```
./gradlew build        # produces the mod jar in build/libs/, runs unit tests
./gradlew runClient    # launches a dev Minecraft client with the mod
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Contributing

Contributions, bug reports, and translations are welcome — see
[CONTRIBUTING.md](CONTRIBUTING.md) for the workflow, and the
[Code of Conduct](CODE_OF_CONDUCT.md) for community standards. Security
issues: see [SECURITY.md](SECURITY.md).

## Branches

Development happens on `dev`; `main` holds the stable state and is updated
by merging `dev` when a milestone is ready.

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
- [ ] Localization: community translation process + initial languages
- [ ] Belt skin/texture applier (idea stage, not scoped)

## License

[MIT](LICENSE)
