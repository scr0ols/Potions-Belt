# Installation

## Requirements

| | |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.4+ |
| Fabric API | 0.141.3+1.21.11 |
| Java | 21+ |

---

## Installing

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
   The Fabric installer sets up a matching launcher profile for you.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.11
   and place the jar in your `.minecraft/mods/` folder. Potion's Belt
   depends on it and won't load without it.
3. Download Potion's Belt and place its jar in the same `mods/` folder.
4. Launch the Fabric profile. If everything loaded correctly, you'll find
   the belt item in the creative inventory (or craft it — see
   [Main Screen and HUD](Main-Screen-and-HUD.md) for the recipe).

---

## Verifying the installation

- The belt item shows up in the Tools & Utilities creative tab, and its
  crafting recipe works (see [Main Screen and HUD](Main-Screen-and-HUD.md)).
- Holding the belt shows the HUD preview next to the hotbar once it
  contains at least one potion.
- `Options > Controls > Potions Belt` lists the "Open Belt Menu" and
  "Column Select Modifier" keybinds — both unbound by default, see
  [Keybinds](Keybinds.md).

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
