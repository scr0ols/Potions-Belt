# Main Screen and HUD

## The belt item

Craft or obtain a Potion's Belt item. It has 27 slots (3 rows x 9 columns)
and only accepts:
- Drinkable (non-splash, non-lingering) potions.
- Empty glass bottles (so you can pre-load bottles, or just so drunk
  potions have somewhere to sit).

---

## Opening the belt's GUI

Two ways, both open the same 27-slot screen (reuses the vanilla shulker
box layout):
- Press <kbd>E</kbd> (the vanilla "Open/Close Inventory" key) while the
  belt is in your **main hand**.
- Press the dedicated "Open Belt Menu" keybind (unbound by default — see
  [Keybinds](Keybinds.md) to set it up), which works from either hand.

Inside, drag potions and bottles in and out like any other container.
Anything that isn't a drinkable potion or an empty bottle is rejected,
including on shift-click.

You can pick up, drop, or drag the belt around your inventory normally
even while its own menu is open — nothing is locked.

---

## Drinking

**Right click** with the belt in either hand to drink — it uses the same
1.6-second animation as drinking a potion normally, so it doesn't change
game balance. Which potion gets drunk depends on your current column
selection; see [Column Loadouts](Column-Loadouts.md).

If the belt has no potions at all (only bottles, or completely empty),
right click does nothing but show a message and play a short sound —
no animation plays.

---

## The HUD preview

Whenever the belt is held in either hand, a small icon plus the potion's
name appears next to the hotbar (mirroring the position of vanilla's
offhand-item indicator, on the opposite side). It always shows exactly
which potion *would* be drunk right now, so you never have to guess before
committing to the 1.6-second animation.

---

## Sounds

- A leather-belt sound plays whenever the GUI opens (either entry point).
- A distinct "bottle opens" sound plays right before the drink animation
  starts.
- A "bottle closes" sound plays when a drink ends — whether it finished
  normally, was released early, or was interrupted (switching hotbar slot,
  picking an empty column mid-drink, etc.).
