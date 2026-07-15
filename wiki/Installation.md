# Installation

## Requirements

| | |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | 0.18.4+ |
| Fabric API | 0.141.3+1.21.11 |
| Java | 21+ |

## Steps

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
   The Fabric installer sets up a matching launcher profile for you.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.11
   and place the jar in your `.minecraft/mods/` folder. Potion's Belt
   depends on it and won't load without it.
3. Download Potion's Belt and place its jar in the same `mods/` folder.
4. Launch the Fabric profile. If everything loaded correctly, you'll find
   the belt item in the creative inventory (or craft it — see
   [Main Screen and HUD](Main-Screen-and-HUD.md) for the recipe).

## Multiplayer

Potion's Belt needs to be installed on both the server and every client
that wants to use it — the drinking/consumption logic runs server-side.
Clients without the mod installed will not be able to join a server that
has it (standard Fabric mod behavior, no special compatibility mode).

## Troubleshooting

See [FAQ and Troubleshooting](FAQ-and-Troubleshooting.md) if the mod
doesn't load or doesn't work as expected.
