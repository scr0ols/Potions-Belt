# Installation

## Requirements

Two builds, same gameplay: pick the loader you already use.

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

---

## Installing

**Fabric**

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.2.
   The Fabric installer sets up a matching launcher profile for you.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 26.2
   and place the jar in your `.minecraft/mods/` folder. Potion's Belt
   depends on it and won't load without it.
3. Download the `fabric-potions-belt` jar and place it in the same `mods/`
   folder.
4. Launch the Fabric profile.

**NeoForge**

1. Install [NeoForge](https://neoforged.net/) for Minecraft 26.2.
2. Download the `neoforge-potions-belt` jar and place it in `.minecraft/mods/`.
   No separate API mod is needed.
3. Launch the NeoForge profile.

If everything loaded correctly, you'll find the belt item in the creative
inventory (or craft it, see [Main Screen and HUD](Main-Screen-and-HUD.md) for
the recipe).

---

## Verifying the installation

- The belt item shows up in the Tools & Utilities creative tab, and its
  crafting recipe works (see [Main Screen and HUD](Main-Screen-and-HUD.md)).
- Holding the belt shows the HUD preview next to the hotbar once it
  contains at least one potion.
- `Options > Controls > Potions Belt` lists the "Open Belt Menu" and
  "Column Select Modifier" keybinds — both unbound by default, see
  [Column Loadouts & Keybinds](Column-Loadouts.md).

---

## Multiplayer

> [!NOTE]
> Potion's Belt needs to be installed on both the server and every client
> that wants to use it — the drinking/consumption logic runs server-side.
> Clients without the mod installed will not be able to join a server that
> has it (standard Fabric mod behavior, no special compatibility mode).

---

## Uninstalling

Remove the mod jar from your `mods/` folder. Potion's Belt doesn't write
any config files yet, so there's nothing else to clean up.

---

## Troubleshooting

See [FAQ and Troubleshooting](FAQ-and-Troubleshooting.md) if the mod
doesn't load or doesn't work as expected.
