# Potion's Belt — Plan

## Goal

A "potion belt" item: a 27-slot (3x9) container that only accepts potions.
Holding right click with the belt in hand drinks the first available potion;
pressing a number key (1-9) while holding right click drinks from that column
instead, falling back row 1 -> row 2 -> row 3.

## Decisions (confirmed 2026-07-13)

1. **Drinkable potions only** for v1. Splash and lingering are rejected by
   the slot filter; support for them is a possible later feature with its own
   mechanics (throwing).
2. **Empty bottles return to the belt, not the player inventory — in place.**
   *(Revised 2026-07-13 after milestone 4 playtesting.)* The originally
   planned "compact forward" behavior (shift every later potion forward to
   close the gap) was tried and rejected: it moved potions across rows,
   destroying the column-based loadout players naturally build (e.g. column 1
   = Regeneration x3, one per row). The confirmed behavior instead is
   **in-place replacement**: drinking a potion turns *only that slot* into an
   empty bottle; every other slot is untouched. This also makes milestone 5's
   column selection straightforward — a column's contents don't drift over
   time. Bottles occupy one slot each; in the GUI the player collects them
   with vanilla double-click gather (up to a full stack of 64 in hand).
3. **Vanilla drink time (1.6 s / 32 ticks).** The belt is a convenience item;
   it must not change game balance.
4. **Target: Minecraft 1.21.11**, Java 21, Mojang official mappings.

## Loader decision: Fabric

Target: **Minecraft 1.21.11, Fabric Loader 0.18.4, Fabric API 0.141.3, Mojang
official mappings, Java 21.**

Both loaders can implement every feature we need (custom item, container-style
menu, intercepting number keys client-side, custom network payloads), so this
is not a capability decision. Fabric wins on practical grounds:

1. **A working Fabric template for this exact mod already exists** (see reuse
   inventory below) with coherent pinned versions.
2. **Faster dev loop** — lighter toolchain, quicker client launches, which
   matters when iterating on input handling and GUI behavior.
3. **First-class mixin culture and current docs** (docs.fabricmc.net covers
   custom payloads, screen handlers, and keybind interception — exactly the
   patterns this mod needs).
4. **"Forge" today effectively means NeoForge** for 1.21+; classic Forge is
   legacy. NeoForge's main historical advantage (its capability/item-handler
   system) is neutralized here because vanilla Data Components (1.20.5+)
   natively cover item-stored inventories.

Trade-off accepted: NeoForge has a larger share of big modpacks, but this is a
standalone learning project, not a modpack integration.

## Repository layout

Repo root = this folder (`Potion's Belt/`), pushed to
`github.com/scr0ols/Potions-Belt`.

```
PLAN.md, NOTES.md, README.md, CLAUDE.md   project docs (repo root)
potions-belt-fabric-1.21.11/              the Fabric mod project (Gradle root)
```

Build/run always from inside `potions-belt-fabric-1.21.11/`.

## Reuse inventory

### Fabric template (now at `potions-belt-fabric-1.21.11/`) — reused

Moved into this repo from `D:\scr0ols\Dev\Minecraft Projects`. Reused as-is:
Gradle setup (`build.gradle`, `gradle.properties`, `settings.gradle`,
wrapper), `fabric.mod.json` as base, GitHub Actions workflow, item texture +
model + lang + crafting recipe + mod icon, registration pattern in
`ModItems.java`. Its standalone `.git` (one local template commit, no remote)
was absorbed into this repo.

Cleanup owed in session 2 (skeleton milestone):
- Package `scr0ols.potionsbelt.modid` -> `scr0ols.potionsbelt` (and
  `maven_group` in `gradle.properties`); the `.modid` suffix is template
  noise.
- Drop `ModBlocks`/`potion_shelf` block (out of scope; also the cause of the
  March 2026 crash — block registered without `setId`) and the example
  mixins.
- Clean `fabric.mod.json` placeholders (description, authors, contact).
- Replace the template's CC0 `LICENSE` inside the Gradle folder with the
  repo's MIT license.

### `potions-belt-neoforge-mcreator-1.21.8` — discarded

MCreator-generated NeoForge workspace (still at the old location). The item
class is an empty stub, and MCreator owns/regenerates the source from its
workspace file, which makes hand-written development impractical. Nothing
worth carrying over.

## Architecture

```
src/main/java/scr0ols/potionsbelt/
  PotionsBelt.java          ModInitializer: registers item, menu type, payloads
  ModItems.java             item registration
  PotionsBeltItem.java      item behavior (use / finishUsing / openMenu)
  PotionsBeltMenu.java      AbstractContainerMenu, 27 potion-only slots
  BeltInventory.java        slot-picking + compaction + component read/write
  BeltSelections.java       server-side sticky default column per player
  SelectColumnPayload.java  C2S payload: player picked column N
  OpenBeltMenuPayload.java  C2S payload: player pressed the open-menu keybind
src/client/java/scr0ols/potionsbelt/
  PotionsBeltClient.java    ClientModInitializer: screen + HUD + keybind setup
  PotionsBeltScreen.java    AbstractContainerScreen (reuses shulker box
                            texture — vanilla shulker GUI is exactly 3x9)
  ClientBeltState.java      client-side mirror of the default column
  BeltHud.java              HUD icon + name previewing the potion about to be drunk
  BeltKeybinds.java         registers the remappable keybinds (open menu,
                            column-select modifier) and polls the former
  mixin/KeybindsMixin.java  handleKeybinds hooks that must run ahead of
                            vanilla's own key handling (hotbar-key draining
                            while the modifier is held, the inventory-key
                            override, the mid-drink suppress-clear) plus the
                            startUseItem block that backs the suppression
  mixin/MouseScrollMixin.java  scroll-to-cycle-column while the belt is held
```

### Data storage (27 slots)

Vanilla data component **`DataComponents.CONTAINER`** (`ItemContainerContents`)
— the same component shulker boxes use. No custom codec, automatic NBT
serialization and client sync, survives item copy/anvil. `BeltInventory`
wraps read/modify/write of this component.

### GUI

- **Two ways to open the menu**, both sending `OpenBeltMenuPayload`:
  a dedicated "Open Belt Menu" keybind (`BeltKeybinds`, unbound by default,
  remappable in Controls > Potions Belt), and the vanilla "Open/Close
  Inventory" key (E by default) when the belt is the main-hand item —
  `KeybindsMixin` drains that click and redirects it instead of letting
  vanilla open the plain inventory screen. No loss of access: `PotionsBeltMenu`
  already includes the full player inventory grid at the bottom, so nothing
  is unreachable via E while the belt is in main hand, just presented
  alongside the belt's 27 slots instead of alone. Deliberately main-hand-only
  so a belt sitting only in the offhand doesn't block plain E-inventory
  access. *(Revised 2026-07-14: replaced sneak+right-click — see the
  2026-07-14 design-decision entries in NOTES.md. Right click is now
  unconditionally "drink"; sneak state no longer changes what right click
  does at all.)*
- `PotionsBeltMenu`: 27 slots backed by a `SimpleContainer` loaded from the
  component; written back on close/change. Slots override `mayPlace` to
  accept drinkable potions and empty glass bottles (`BeltInventory.
  isAcceptable`) — nothing else can be placed in, but bottles (whether
  produced by drinking or placed manually) sit in slots and can be picked up
  / double-click gathered normally.
- The belt itself can be picked up, dropped (Q), and dragged around the
  player inventory normally while its own menu is open — no special
  locking. *(Revised 2026-07-14: session 3's original "backpack-in-itself"
  lock — a `PlayerSlot.mayPickup` override plus a `clicked()` SWAP-click
  block — was removed after playtesting made it clear it was just annoying
  (E now opens the menu casually and often, unlike the old deliberate
  sneak+right-click), not protecting against anything real: moving/dropping
  the belt only ever relocates the same `ItemStack` reference the menu's
  `PotionSlot`s and `beltStack` field already point to, so nothing gets
  duplicated. `PotionSlot.mayPlace` independently already rejects the belt
  item itself from being placed into its own 27-slot grid, unrelated to and
  unaffected by this change, so "belt inside itself" stays impossible for
  its own reason.)*

### Drinking (held right click)

Maps onto the vanilla item-use system — no timers of our own:
- `use()`: belt contains a potion anywhere -> `startUsingItem` (drink
  animation, vanilla 32 ticks = 1.6 s, same as a normal potion). Right click
  always means "drink" now, unconditionally — no sneak branch.
- `finishUsingItem()` (server): resolve target slot — the player's default
  column (`BeltSelections`, sticky, starts at 1) via
  `BeltInventory.firstPotionSlotInColumn`; if that column is empty, falls
  back to `BeltInventory.firstPotionSlot` (first potion anywhere, row 1 left
  to right then rows 2 and 3, bottles skipped) — apply its `PotionContents`
  effects, then replace *only that slot* with an empty bottle (in place, no
  shifting of any other slot). In creative, the belt isn't written to at all
  (matches vanilla: creative drinking doesn't consume the item).
- Releasing early cancels naturally (vanilla use mechanics); nothing is
  consumed because removal happens only at finish.

### Column selection (sticky default + modifier-gated hotbar keys + HUD preview)

*(Revised twice on 2026-07-14: milestone 5's approach — hijacking vanilla
hotbar keys 1-9 while mid-drink — was first replaced with 9 fully
independent "Select Column N" keybindings, decoupled from right click
entirely. That broke in practice: binding those to the bare 1-9 keys
collided with vanilla's own hotbar-slot keybindings sharing the same
physical keys (see NOTES.md's "Playtest round 2" entry) — pressing "3" both
switched the hotbar to slot 3 *and* queued a column-3 click, and if slot 3
wasn't the belt, the belt was no longer the held item by the time the click
was processed. Replaced with a single modifier keybind that reinterprets
vanilla's own hotbar keys while held, avoiding the collision entirely since
the modifier is a physically distinct key.)*

- `BeltKeybinds.SELECT_MODIFIER` (unbound by default, remappable in
  Controls > Potions Belt) is a held-key modifier, not a click. While it's
  down and the belt is held, `KeybindsMixin`'s `handleKeybinds` HEAD
  injection drains vanilla's own `keyHotbarSlots[0..8]` clicks (same
  drain-before-vanilla-sees-it trick milestone 5 used) and reinterprets each
  as a column pick — this has to happen inside `handleKeybinds` itself,
  ahead of vanilla's own hotbar-switch loop later in that same method, or
  the hotbar switches anyway regardless of what we do afterward.
- **Scroll wheel** (`MouseScrollMixin`) is a second, parallel way to change
  the column: while the belt is held and no screen is open, scrolling cycles
  the default column ±1 (wrapping 1-9; scroll down = forward) instead of
  switching the hotbar slot. No Fabric API event covers world mouse scroll,
  so this is a mixin into vanilla's `MouseHandler#onScroll`, cancelled only
  for this one case.
- Both paths funnel through `ClientBeltState#onColumnPicked`, which mirrors
  the pick into the client's own `defaultColumn` field (for `BeltHud`) and
  sends `SelectColumnPayload`. It's plain code, not mixin code — a `@Mixin`
  class can only contain recognized injector methods, nothing else callable
  from outside it (learned the hard way, see NOTES.md's "Bug found via
  runClient" entry).
- Server stores the column in `BeltSelections` as the player's **default**
  column — sticky across drinks, not just the one in progress. It updates on
  every pick (even if that column turns out empty) and is only cleared on
  disconnect, never after a drink.
- A plain right click always tries the default column first via
  `finishUsingItem`; if it's empty, falls back to first-available anywhere
  instead of failing. A pick that lands on an empty column while the player
  is already mid-drink still cancels that specific drink immediately (via
  `onColumnSelected` + `stopUsingItem`, with `KeybindsMixin`'s
  `startUseItem` block preventing the still-held right click from instantly
  restarting a new one) — independent of the sticky default itself.
- `BeltHud` (client-only, no new S2C payload for the preview itself) renders
  a HUD icon plus the potion's name, always visible — placed like vanilla's
  offhand-item hotbar indicator, mirrored to the opposite side — previewing
  the potion that would be drunk, visible whenever the belt is held in
  either hand. Uses the same `BeltInventory.firstPotionSlotInColumn` /
  `firstPotionSlot` calls as `finishUsingItem`, against `ClientBeltState`'s
  locally mirrored column, so the preview can't drift from the real drink.

## Edge cases

- **Empty belt (or only bottles left)**: `use()` fails — no drink animation,
  error sound + action bar message. Bottles never count as drinkable.
- **Default column empty (or holds a bottle) in all 3 rows**: `finishUsingItem`
  falls back to first-available anywhere; only aborts (with feedback) if the
  whole belt is empty. An explicit number-key pick mid-drink on an empty
  column is the one exception — that specific drink is cancelled immediately
  instead of falling back (potion is only removed server-side at
  `finishUsingItem`, so nothing is ever lost either way).
- **Vanilla hotbar keys 1-9 and scroll-to-switch-slot when not holding the
  belt**: fully untouched — the belt's own column keybinds are independent
  bindings, never the real hotbar keys. Scroll *is* repurposed while the
  belt is actively held in a hand (cycles column instead of switching
  slots); switching hotbar slots by scroll while holding the belt requires
  releasing/using the vanilla number keys instead — a known, accepted
  trade-off from the 2026-07-14 redesign.
- **Belt inside another belt**: impossible; slots accept only potions and
  empty bottles.
- **Shift-clicking wrong items into the GUI**: rejected by `quickMoveStack`
  respecting the same filter.
- **Belt full of potions when one is drunk**: never overflows — the bottle
  takes exactly the slot freed by the drunk potion.
- **Moving/dropping the belt while its GUI is open**: allowed, matches
  vanilla (see the 2026-07-14 revision in the GUI section above). The menu
  itself doesn't auto-close if the belt leaves the inventory entirely (e.g.
  Q-dropped) — `stillValid` doesn't check for this, deliberately not built
  since it wasn't asked for; the 27 slots just keep showing/editing whatever
  `ItemStack` the menu was opened with until manually closed. Death still
  closes any open screen via vanilla's own generic death handling, unrelated
  to this menu specifically.
- **Switching hotbar slot / dropping belt mid-drink**: vanilla stops item use
  automatically; the sticky default column is *not* cleared (it persists for
  the next drink, same as any other early release).
- **Stacking identical potions**: potions have max stack size 1 in vanilla —
  one potion per slot, no custom stacking. Empty bottles also occupy one
  slot each inside the belt; stacking happens only in the player's hand via
  double-click gather.
- **Splash / lingering potions**: rejected by the filter (v1 decision).
- **Multiplayer**: all consumption logic runs server-side; the only custom
  packets are column selection and the open-menu request; component data
  syncs automatically.
- **Default column is per-player, not per-belt-item**: two belts in the same
  player's inventory share one default column (known v1 limitation, accepted
  2026-07-14).

## Milestones (sessions)

1. ~~Planning + repo init, README, .gitignore.~~ (done 2026-07-13)
2. ~~Project skeleton builds and runs: belt item registered, texture, recipe;
   template cleanup (package rename, drop blocks/mixin examples, metadata).~~
   (done 2026-07-13)
3. ~~GUI: menu + screen + potion-only filter + component persistence.~~
   (done 2026-07-13)
4. ~~Drinking: held right click, first-available logic, in-place bottle
   replacement.~~ (done 2026-07-13)
5. ~~Column selection: key interception, payload, fallback logic.~~
   (done 2026-07-13)
6. Polish. In progress:
   - ~~Column-selection feedback, part 1: drink animation cancels
     immediately (not after the full 1.6s) when the selected column is
     empty.~~ (done 2026-07-14)
   - ~~Column-selection feedback, part 2: selected column is now a
     persistent per-player default (not just for the current drink) with
     fallback to first-available when the default is empty, plus a HUD icon
     previewing the potion about to be drunk whenever the belt is held.~~
     (implemented 2026-07-14)
   - ~~Tooltip contents preview~~: folded into the HUD icon — the potion's
     name is now always shown next to it, not a separate hover tooltip.
     (implemented 2026-07-14)
   - ~~Column-selection feedback, part 3: replaced right-click-driven
     selection (sneak+right-click for the menu, hijacked hotbar keys for
     column picks) with fully independent, remappable keybinds — see the
     2026-07-14 design-decision entry in NOTES.md.~~ (implemented and fully
     in-game verified 2026-07-14, across four playtest rounds — see
     NOTES.md; two bugs found along the way and fixed: scroll wasn't gated
     behind the select-modifier like the hotbar keys are, and scroll
     direction was backwards)
   - ~~"Backpack-in-itself" belt-lock removed~~: the belt can now be picked
     up/dropped/dragged normally even while its own menu is open, matching
     vanilla item behavior. (done and in-game verified 2026-07-14, see
     NOTES.md)
   - Still open: sounds, edge-case testing pass.
7. Docs pass: review and update README.md (current build/run steps, feature
   list, screenshots/icon if relevant) against actual end-state behavior, and
   set up a wiki for the mod (usage guide, column-loadout explanation,
   FAQ/troubleshooting).
8. Configurable mod settings (candidate, not started — discussed 2026-07-14
   during milestone 6 playtesting, deliberately deferred rather than folded
   into that session). Needs its own persisted config (simple JSON via Gson
   is already on the classpath, no new dependency needed) and some way to
   open a settings screen (a keybind, and/or a button inside
   `PotionsBeltScreen`; ModMenu integration is a nice-to-have, not a
   requirement, since it'd be an extra dependency players would need too).
   Candidate settings, in the order they came up:
   - **Invert scroll-cycle direction**: the "scroll down = column advances
     forward" default (fixed 2026-07-14 after playtesting) won't necessarily
     suit everyone; expose it as a toggle instead of a hardcoded constant.
   - **"No pre-selector required" mode**: an opt-in alternative to
     `BeltKeybinds.SELECT_MODIFIER` gating — when enabled, holding the belt
     alone (no modifier) would be enough for hotbar keys 1-9 and/or scroll
     to mean column-select, with the explicit accepted trade-off that scroll
     stops switching the hotbar slot for as long as the belt is held (this
     was the *original* always-on design, reverted after the vanilla-hotbar
     collision found in playtest round 2 — see NOTES.md). Off by default;
     the modifier-gated behavior stays the safe default for everyone else.
   - **Combined inventory + belt tab**: an alternative to "E opens the
     standalone belt menu when the belt is in main hand." When enabled and
     the player has a belt anywhere in their inventory (not just held), E
     would open the normal (taller) player inventory screen with an added
     "Potions Belt" tab down the left edge — the same UI pattern vanilla
     already uses there for the recipe book / stats / etc. tabs — instead of
     jumping straight to the standalone 27-slot screen. Needs a taller
     custom inventory screen (extending or replacing vanilla's own
     `InventoryScreen` layout) and a tab component; meaningfully more work
     than the other two toggles, likely its own sub-task within this
     milestone rather than a quick addition.
