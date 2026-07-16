# Column Loadouts & Keybinds

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

---

## Keybinds

> [!TIP]
> All of Potion's Belt's keybinds are **unbound by default** and fully
> remappable, on purpose — set them up under
> **Options > Controls > Potions Belt** to whatever fits your own layout.

| Keybind | What it does |
|---|---|
| Open Belt Menu | Opens the belt's 27-slot GUI, from either hand. |
| Column Select Modifier | Hold this, then press a hotbar key (1-9) or scroll, to pick a column while the belt is held. |

---

## Picking a column

1. Set up the "Column Select Modifier" keybind above (unbound by default).
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

---

## Interaction with vanilla keys

- <kbd>E</kbd> (vanilla "Open/Close Inventory") also opens the belt's menu,
  but only when the belt is your **main-hand** item — otherwise it opens
  your normal inventory as usual.
- Vanilla hotbar keys 1-9 and scroll-to-switch-slot are **untouched** unless
  you're actively holding the Column Select Modifier while the belt is in
  hand. Without the modifier held, they behave exactly like vanilla, even
  while the belt is in your hand.
- Right click is unconditionally "drink" — it's never repurposed to open
  the menu, regardless of sneaking or any other modifier.

> [!WARNING]
> While the Column Select Modifier is held and the belt is in hand,
> scrolling cycles the column instead of switching your hotbar slot. To
> switch slots by scroll while holding the belt, either release the
> modifier first or use the vanilla number keys instead — a known,
> accepted trade-off.

---

## Two belts, one default

> [!NOTE]
> The column default is tracked per player, not per belt item — if you're
> carrying two belts, they share the same selected column. This is a known
> current limitation, not a bug.
