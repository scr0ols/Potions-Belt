# Column Loadouts

The belt's 27 slots are 3 rows x 9 columns. The intended way to use it is as
a **loadout**: dedicate each column to one potion type, stacked one per row
(e.g. column 1 = three Regeneration potions, one per row; column 2 = three
Fire Resistance; and so on).

## Why columns, not just "grab any potion"

Drinking always replaces **only the slot that was drunk** with an empty
bottle — nothing else in the belt shifts or reorders. That means a column's
contents stay predictable over time: if column 1 is your Regeneration
column, it stays your Regeneration column, drink after drink, until you
manually restock it.

## Picking a column

1. Set up the "Column Select Modifier" keybind (see
   [Keybinds](Keybinds.md) — unbound by default).
2. While holding the belt, hold that modifier and either:
   - Press a hotbar number key (1-9) to jump straight to that column, or
   - Scroll to cycle the column up/down by one.
3. Your pick becomes a **sticky default** — it's remembered for every
   future drink, not just the next one, until you change it again or
   disconnect.

A plain right click (no modifier involved) always drinks from your current
default column first. If that column's top potion is gone, the belt falls
back to row 2, then row 3 of that same column, and only after the whole
column is empty does it fall back further to the first potion anywhere in
the belt — so a drink almost never just fails outright unless the belt
truly has nothing left to drink.

## Two belts, one default

The column default is tracked per player, not per belt item — if you're
carrying two belts, they share the same selected column. This is a known
current limitation, not a bug.
