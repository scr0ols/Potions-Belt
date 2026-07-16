# FAQ and Troubleshooting

## The mod doesn't load / the game crashes on startup

- Double-check Fabric API is installed alongside Potion's Belt — it's a
  hard dependency (see [Installation](Installation.md)).
- Confirm your Minecraft, Fabric Loader, and Fabric API versions match the
  requirements table exactly. Mismatched versions are the most common cause
  of load failures.
- Check `.minecraft/logs/latest.log` for the actual error — Fabric Loader
  usually names the exact incompatibility.

## Pressing 1-9 just switches my hotbar slot instead of picking a column

You need to hold the **Column Select Modifier** keybind while pressing the
number key — it's unbound by default, so this does nothing until you bind
it yourself. See [Column Loadouts & Keybinds](Column-Loadouts.md).

## Scrolling switches my hotbar slot instead of cycling columns

Same cause as above — scroll-to-select also requires the modifier held.
Without it, scroll always behaves like vanilla, even while the belt is
held.

## E opens my normal inventory, not the belt

E only opens the belt's menu when the belt is in your **main hand**
specifically — not the offhand, and not just "somewhere in your
inventory." Use the dedicated "Open Belt Menu" keybind instead if you want
to open it without switching hands.

## My potions aren't in the column I expect anymore

Drinking never moves or reorders potions — only the exact slot you drank
from becomes a bottle. If a column looks different than you remember, it's
most likely that you (or someone else, in multiplayer) manually rearranged
the belt's contents, not the mod shuffling things.

## Can I put splash or lingering potions in the belt?

Not currently — only drinkable (regular) potions and empty bottles are
accepted. This may change in a future update.

## I found a bug that isn't covered here

Open a [bug report](../../issues/new?template=bug_report.md) with your
versions and steps to reproduce.
