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
2. **Empty bottles return to the belt, not the player inventory.** After a
   potion is drunk, the remaining potions shift forward to close the gap
   (row-major order preserved) and the empty bottle is placed right after the
   last potion. Bottles occupy one slot each; in the GUI the player collects
   them with vanilla double-click gather (up to a full stack of 64 in hand).
   *Consequence to be aware of:* because potions compact forward on every
   drink, a number key selects "the Nth potion currently in row 1", not a
   fixed loadout position. If fixed columns per potion type turn out to be
   the better UX, the alternative is in-place replacement (bottle stays in
   the drunk potion's slot, no shifting) — flag it during playtesting.
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
  PotionsBelt.java          ModInitializer: registers item, menu type, payload
  ModItems.java             item registration
  PotionsBeltItem.java      item behavior (use / finishUsing)
  PotionsBeltMenu.java      AbstractContainerMenu, 27 potion-only slots
  BeltInventory.java        slot-picking + compaction + component read/write
  BeltSelections.java       server-side "pending column" per player
  SelectColumnPayload.java  C2S payload: player picked column N while drinking
src/client/java/scr0ols/potionsbelt/
  PotionsBeltClient.java    ClientModInitializer: screen registration
  PotionsBeltScreen.java    AbstractContainerScreen (reuses shulker box
                            texture — vanilla shulker GUI is exactly 3x9)
  mixin/KeybindsMixin.java  intercepts hotbar keys 1-9 while drinking
```

### Data storage (27 slots)

Vanilla data component **`DataComponents.CONTAINER`** (`ItemContainerContents`)
— the same component shulker boxes use. No custom codec, automatic NBT
serialization and client sync, survives item copy/anvil. `BeltInventory`
wraps read/modify/write of this component.

### GUI

- **Sneak + right click** opens the menu (plain right click is reserved for
  drinking).
- `PotionsBeltMenu`: 27 slots backed by a `SimpleContainer` loaded from the
  component; written back on close/change. Slots override `mayPlace` to
  accept only drinkable potions — the player cannot place bottles (or
  anything else) in, but bottles produced by drinking sit in slots and can be
  picked up / double-click gathered normally.
- The hotbar slot holding the belt itself is locked while the menu is open
  (standard "backpack-in-itself" protection).

### Drinking (held right click)

Maps onto the vanilla item-use system — no timers of our own:
- `use()`: not sneaking + belt contains a potion -> `startUsingItem` (drink
  animation, vanilla 32 ticks = 1.6 s, same as a normal potion).
- `finishUsingItem()` (server): resolve target slot — pending column from
  `BeltSelections` if set, else first potion scanning row 1 left to right,
  then rows 2 and 3 (bottles are skipped) — remove the potion, apply its
  `PotionContents` effects, then rewrite the belt: remaining potions
  compacted forward in row-major order, existing bottles after them, plus
  the new empty bottle (skipped in creative, matching vanilla).
- Releasing early cancels naturally (vanilla use mechanics); nothing is
  consumed because removal happens only at finish.

### Number-key column selection

- Client mixin on `Minecraft#handleKeybinds`: while the player is actively
  using (drinking from) the belt, hotbar keys 1-9 are consumed before vanilla
  processes them (so the hotbar slot does NOT switch) and a
  `SelectColumnPayload(n)` is sent.
- Server stores the column in `BeltSelections` (per-player map, cleared when
  the drink finishes, is interrupted, or the player disconnects).
- Column fallback (row 1 -> 2 -> 3) happens server-side in `BeltInventory`.
- Risk noted: if consuming hotbar keys mid-use proves unreliable, fallback
  design is sneak+scroll to pre-select a column (shown on the HUD). Only if
  the primary approach fails in testing.

## Edge cases

- **Empty belt (or only bottles left)**: `use()` fails — no drink animation,
  error sound + action bar message. Bottles never count as drinkable.
- **Chosen column empty (or holds a bottle) in all 3 rows**: drink aborts at
  finish with feedback (potion is only removed server-side at
  `finishUsingItem`, so nothing is lost).
- **Number keys when not drinking**: untouched — vanilla hotbar switching.
- **Belt inside another belt**: impossible; slots accept only potions.
- **Shift-clicking wrong items into the GUI**: rejected by `quickMoveStack`
  respecting the same filter.
- **Belt full of potions when one is drunk**: never overflows — the bottle
  takes exactly the slot freed by the drunk potion.
- **Moving/dropping the belt while its GUI is open**: belt slot locked while
  menu open; menu closes if the belt disappears (e.g. dropped via death).
- **Switching hotbar slot / dropping belt mid-drink**: vanilla stops item use
  automatically; pending column selection is cleared.
- **Stacking identical potions**: potions have max stack size 1 in vanilla —
  one potion per slot, no custom stacking. Empty bottles also occupy one
  slot each inside the belt; stacking happens only in the player's hand via
  double-click gather.
- **Splash / lingering potions**: rejected by the filter (v1 decision).
- **Multiplayer**: all consumption logic runs server-side; the only custom
  packet is the column selection; component data syncs automatically.

## Milestones (sessions)

1. ~~Planning + repo init, README, .gitignore.~~ (done 2026-07-13)
2. Project skeleton builds and runs: belt item registered, texture, recipe;
   template cleanup (package rename, drop blocks/mixin examples, metadata).
3. GUI: menu + screen + potion-only filter + component persistence.
4. Drinking: held right click, first-available logic, bottle-to-belt
   compaction.
5. Column selection: key interception, payload, fallback logic.
6. Polish: sounds, action bar feedback, tooltip (contents preview), edge-case
   testing pass.
