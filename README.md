# Potion's Belt

A Minecraft mod that adds a **potion belt**: a wearable-in-hotbar container
item with a chest-like 3x9 GUI (27 slots) that only accepts drinkable
potions.

- **Hold right click** with the belt in hand to drink the first available
  potion (row 1, left to right).
- **Hold right click + press 1-9** to drink from that column instead,
  falling back row 1 -> row 2 -> row 3 if the column has no potion.
- After drinking, the **empty bottle goes back into the belt**; collect
  bottles from the GUI with a double-click.
- **Sneak + right click** opens the belt's GUI.

## Loader: Fabric (decided, do not revisit)

Fabric on **Minecraft 1.21.11**, Fabric Loader 0.18.4, Fabric API
0.141.3+1.21.11, Mojang official mappings, **Java 21**.

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

Full reasoning and architecture: [PLAN.md](PLAN.md). Session history and
lessons learned: [NOTES.md](NOTES.md).

## Repository layout

```
PLAN.md / NOTES.md / CLAUDE.md    project docs and session memory
potions-belt-fabric-1.21.11/      the Fabric mod (Gradle project root)
```

## Build & run

Requires Java 21 (JDK). From `potions-belt-fabric-1.21.11/`:

```
./gradlew build        # produces the mod jar in build/libs/
./gradlew runClient    # launches a dev Minecraft client with the mod
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Status

**In development — not yet playable.**

- [x] Planning: loader decision, architecture, edge cases ([PLAN.md](PLAN.md))
- [x] Repository setup
- [ ] Skeleton builds and runs (belt item registered, texture, recipe)
- [ ] GUI (3x9 menu, potion-only filter, persistence)
- [ ] Drinking via held right click
- [ ] Column selection via number keys
- [ ] Polish (sounds, feedback, tooltip)
