# Milestone 8 — configurable settings: design doc

Working document for milestone 8. Not architecture yet — PLAN.md only gets
updated once ideas here are settled. Purpose: think through the three
candidate settings and the settings-UI mechanism before writing any code.

## Settled so far

- **Persistence**: simple JSON config via Gson (already on the classpath,
  no new dependency).
- **Settings UI, two access points**:
  1. **ModMenu integration** — settings show up in the standard Mod Menu
     config screen. Adds ModMenu as a dependency.
  2. **A dedicated remappable keybind** to open the settings screen
     directly, following the same pattern as the existing keybinds
     (`BeltKeybinds.SELECT_MODIFIER`, "Open Belt Menu") — unbound by
     default, listed under the "Potions Belt" Controls category.
- Three settings in scope for this milestone (all raised 2026-07-14 during
  milestone 6 playtesting, see NOTES.md):
  1. Invert scroll-cycle direction.
  2. "No pre-selector required" mode.
  3. Combined inventory + belt tab.

## 1. Invert scroll-cycle direction

Toggle for `MouseScrollMixin`'s "scroll down = column advances forward"
default (fixed 2026-07-14 after playtesting — see NOTES.md). Simplest of
the three: one boolean, read at the point `MouseScrollMixin` currently
hardcodes the ternary.

Open questions:
- *(none yet — flag anything that comes up)*

## 2. "No pre-selector required" mode

Opt-in alternative to `BeltKeybinds.SELECT_MODIFIER` gating: when enabled,
holding the belt alone (no modifier) is enough for hotbar keys 1-9 and/or
scroll to mean column-select. This was the *original* always-on design from
milestone 5/6, reverted after it collided with vanilla's own hotbar
keybindings sharing keys 1-9 (see NOTES.md's "Playtest round 2"). Off by
default; the modifier-gated behavior stays the default for everyone else.

**Settled (2026-07-15):**
- **Fully replaces the modifier gating** when enabled — `SELECT_MODIFIER`
  becomes irrelevant while this setting is on; hotbar keys 1-9 and scroll
  both mean column-select unconditionally whenever the belt is held, no
  modifier involved.
- **Covers both** hotbar keys and scroll — this is effectively reverting to
  the original milestone 5/6 always-on design in full, gated behind an
  opt-in setting instead of being the hardcoded default (which is what
  caused the vanilla-hotbar-key collision fixed in "Playtest round 2").

Open questions:
- Any UI/HUD cue needed so a player who turned this on knows hotbar
  1-9 no longer switches slots while the belt is held? (The original
  always-on design shipped without one; worth deciding whether that was a
  gap or a non-issue.)

## 3. Combined inventory + belt tab

Alternative to "E opens the standalone belt menu when the belt is in main
hand." When enabled and the player has a belt anywhere in inventory (not
just held), E would open the normal (taller) player inventory screen with
an added "Potions Belt" tab down the left edge — same UI pattern vanilla
uses for the recipe book / stats tabs — instead of jumping straight to the
standalone 27-slot screen.

Flagged in PLAN.md as meaningfully more work than the other two: needs a
taller custom inventory screen (extending or replacing vanilla's own
`InventoryScreen` layout) and a tab component.

**Settled (2026-07-15):**
- **Real combined menu**: one `AbstractContainerMenu` holding the vanilla
  3x9 player inventory + 4x4 crafting + armor/offhand + recipe suggestions
  *and* the 27 belt slots at once — not an overlay swap like the recipe
  book.
- **Belt slots default visible**, with a show/hide button to collapse them
  (default: shown).
- **This setting fully takes over both entry points once enabled**: E and
  the dedicated "Open Belt Menu" keybind both open the combined screen;
  the plain standalone 27-slot-only `PotionsBeltScreen` is not used at all
  while the setting is on. (When the setting is off, current behavior is
  unchanged — standalone screen via both entry points, as today.)

- **Trigger condition: belt anywhere in inventory** (not just held) — as
  long as a belt is somewhere in the player's inventory, E opens the
  combined screen instead of plain vanilla inventory (matches PLAN.md's
  original wording; supersedes the "belt in hand" phrasing used earlier in
  this session's discussion).

Open questions:
- Extend `InventoryScreen` or build a parallel custom screen that mimics
  it? (Need to check what vanilla actually allows here via `javap`/sources
  jar before committing to an approach.)
- **Multiple belts in inventory**: which one's 27 slots does the combined
  screen show if the player has more than one? (Same open question as the
  existing "default column is per-player, not per-belt-item" limitation in
  PLAN.md — likely needs the same answer: pick one deterministically, e.g.
  first found, and accept it as a known limitation.)
- Show/hide button placement, and whether its state (shown/hidden)
  persists across screen opens or always resets to shown.
- Recipe book: vanilla's `InventoryScreen` already has a recipe-book toggle
  tab on the same edge — where does the belt's show/hide button sit
  relative to it?
- Screen height/layout: how much taller does the window need to be to fit
  a 3x9 grid on top of the existing crafting/armor/inventory layout?
