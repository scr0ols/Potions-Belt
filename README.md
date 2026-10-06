<div align="center">

# Potion's Belt

[![Minecraft](https://img.shields.io/badge/Minecraft-26.2%2B-62B47A?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEWcy2yXxmeTwmOSwWKQv2CNvF2KuVp/v1V+vlSDslOBsFF2tkx1tUt0tEpzs0lxsUdwsEa5hVxvr0VtrUNsrEJrq0FqqkBpqT9oqD5npz2Hh4dmpjxkpDpiojhhoTdgoDZfnzVXly2WbEpQkCZsbGx0WER5VTpZPSnN78OwAAAAnElEQVR42jWNCw7CMAxDw/8/CBuMbTBGGPb9b4hbQRtZT3Gfaodd0XXnoVrvrk3dv0vbV8vthrfHMJ0XfdVcbEG71zzxsJpN+HrSOJJHkgNLgoQFwhkQc0QQBkYISEADN1e2Ak+jyhKHlGDOsJS6FCNZhkg2oZMlLbIfdJfUwrKoZpRCjSnzBj8pKfgg1U6ZYYlAheP/ratuocjvvsNMH5BFYTKgAAAAAElFTkSuQmCC&logoColor=white)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.19.3%2B-C8A87A)](https://fabricmc.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-26.2-D7842D)](https://neoforged.net/)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-GPL--3.0%2B-blue)](LICENSE)

*A dedicated 3x9 potion container with fast, column-based drinking â no more digging through the hotbar.*

</div>

---

## What it does

Potion's Belt adds a single item: a 27-slot belt that accepts drinkable
potions and empty bottles, plus a fast way to drink from it without
opening any GUI mid-fight.

| Feature | Description |
|---|---|
| **Drink on right click** | The vanilla 1.6s drink animation and timing â no balance changes, just faster access. |
| **Column-based loadout** | 3 rows x 9 columns. Dedicate each column to one potion type; drinking only ever replaces the exact slot drunk, so a column's contents never drift. |
| **Sticky column selection** | Hold the remappable "Column Select" modifier + a hotbar key (1-9) or scroll to pick a column. Your pick is remembered as the default for every future drink, not just the current one. |
| **Row and belt-wide fallback** | If the selected column's top potion is gone, the belt falls back to row 2, then row 3, of that column; if the whole column is empty, it falls back further to the first potion anywhere in the belt, only failing outright if the belt has none left at all. |
| **HUD preview** | An icon + name next to the hotbar always shows exactly which potion is about to be drunk. |
| **Bottles return to the belt** | Drinking turns only that slot into an empty bottle, in place â nothing else shifts. |
| **Two ways to open the GUI** | A dedicated keybind (either hand), or <kbd>E</kbd> while the belt is specifically your **main-hand** item. |
| **Custom sounds** | Distinct sounds for opening the belt and for each drink starting/ending. |

> [!TIP]
> All of Potion's Belt's keybinds are **unbound by default** and fully
> remappable under **Options > Controls > Potions Belt** â see the
> [Column Loadouts & Keybinds wiki page](../../wiki/Column-Loadouts) before
> your first drink.

---

## Requirements

Two builds from one codebase, same gameplay on both: pick whichever loader
you already use.

**Fabric**

| | |
|---|---|
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.156.0+26.2 |
| Java | 25+ |

**NeoForge**

| | |
|---|---|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.21-beta+ |
| Java | 25+ |

> [!NOTE]
> The belt's consumption logic runs server-side, so multiplayer requires
> the mod on both the server and every client â see the wiki's
> [Installation](../../wiki/Installation) page.

---

## Installation

**Fabric**

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.2.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for the same
   version and drop it in your `mods/` folder.
3. Download Potion's Belt and drop it in `mods/` too.

**NeoForge**

1. Install [NeoForge](https://neoforged.net/) for Minecraft 26.2.
2. Download Potion's Belt and drop it in `mods/`. No separate API mod
   needed â NeoForge is the mod loader itself.

See the [wiki](../../wiki) for a full usage guide, keybind setup, and
column-loadout tips (loader-independent â the gameplay is identical).

---

## Languages

| Language | Locale | File |
|---|---|---|
| ð¬ð§ English (US) | `en_us` | [`en_us.json`](common/src/main/resources/assets/potionsbelt/lang/en_us.json) |
| ðµð¹ Portuguese (Portugal) | `pt_pt` | [`pt_pt.json`](common/src/main/resources/assets/potionsbelt/lang/pt_pt.json) |

> [!NOTE]
> Want to add or fix a translation? See the wiki's
> [Translations](../../wiki/Translations) page.

---

## Repository layout

```
common/      code and resources shared by both loaders (vanilla only)
fabric/      Fabric glue: entrypoints, registration, networking, keybinds
neoforge/    NeoForge glue: the same, on NeoForge's APIs
build-logic/ shared Gradle convention plugins
wiki/        wiki pages, drafted locally for now
```

One Gradle build at the root, one shared codebase: almost everything lives in
`common/`, the loader modules only hold what cannot be written against
vanilla alone. Jars are named `<loader>-potions-belt-<mod version>+<minecraft
version>.jar`.

---

## Build & run

Requires Java 25 (JDK). From the repository root:

```bash
./gradlew build                # builds both loaders, runs unit tests
./gradlew :fabric:runClient     # dev client with the Fabric build
./gradlew :neoforge:runClient   # dev client with the NeoForge build
```

(Windows: use `gradlew.bat` instead of `./gradlew`.)

> [!TIP]
> The built jars end up in `fabric/build/libs/` and `neoforge/build/libs/` —
> drop the one for your loader (not the `-sources.jar`) into your `mods/`
> folder to test a local build.

---

## Contributing

Contributions, bug reports, and translations are welcome â see
[CONTRIBUTING.md](CONTRIBUTING.md) for the workflow, and the
[Code of Conduct](CODE_OF_CONDUCT.md) for community standards. Security
issues: see [SECURITY.md](SECURITY.md).

> [!WARNING]
> Target the `dev` branch, not `main`, when opening a pull request â see
> Branches below.

---

## Branches

- `dev`: integration branch. Every change lands here through a pull request
  from its own branch (`feat/...`, `fix/...`, `refactor/...`, `docs/...`).
- `main`: stable state, always targeting the newest supported Minecraft
  version. Updated by merging `dev` when a milestone is ready.
- `26.2`, `26.3`, ...: one branch per Minecraft version, holding the code
  state released for that version. Created from `main` at release; older ones
  become maintenance lines that only receive fixes.

---

## Status

**In active development.** Core gameplay is complete and in-game verified
on both loaders; polish, docs, and configurability are ongoing.

- [x] Planning: loader decision, architecture, edge cases
- [x] Repository setup
- [x] Skeleton builds and runs
- [x] GUI (3x9 menu, potion-only filter, persistence)
- [x] Drinking via right click
- [x] Column selection: sticky default, modifier-gated hotbar keys + scroll,
      HUD preview
- [x] Polish: sounds, column-selection feedback, edge-case pass
- [x] NeoForge port: full gameplay parity with Fabric, in-game verified
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
