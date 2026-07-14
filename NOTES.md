# Potion's Belt — Session notes

## Session 5 (2026-07-13): milestone 5 — column selection

**Summary: number-key column selection implemented as planned (client mixin
intercepting hotbar keys 1-9 while drinking, C2S payload, server-side
per-player pending column, row 1 -> 2 -> 3 fallback in finishUsingItem).
Build and all 11 unit tests pass. In-game verification pending — João to
test against the checklist below.**

What was added:
- `SelectColumnPayload` (record, C2S): single `int column` field, registered
  via `PayloadTypeRegistry.playC2S()` in `PotionsBelt.onInitialize()` (runs
  on both sides since the main entrypoint is common code).
- `BeltSelections`: static per-player pending column, keyed by player UUID in
  a plain `HashMap` — Fabric's networking API dispatches payload receivers on
  the server thread, so no concurrency is needed. Cleared in three places:
  `finishUsingItem` (after resolving, success or abort), `releaseUsing`
  (early release or vanilla-interrupted use — hotbar switch, dropping the
  item, etc., all route through this one vanilla hook), and
  `ServerPlayConnectionEvents.DISCONNECT`.
- `BeltInventory.firstPotionSlotInColumn(belt, column)`: row-major index of
  the first drinkable potion in the given 1-9 column, checking row 1 -> row 2
  -> row 3 (bottles skipped, same rule as `firstPotionSlot`). Returns -1 for
  an out-of-range column (defensive: the column number arrives over the
  network, a boundary worth validating even though our own client only ever
  sends 1-9) or when the column has no potion in any row.
- `PotionsBeltItem.finishUsingItem`: if a column is pending
  (`BeltSelections.get`), resolves via `firstPotionSlotInColumn`; otherwise
  unchanged first-available scan from milestone 4. If the column resolves to
  nothing, aborts with the same action-bar message + `BUNDLE_INSERT_FAIL`
  sound used for the empty-belt case, consuming nothing (removal only ever
  happens through `takePotionAt`, which is never reached in the abort path).
- `KeybindsMixin` (client, `Minecraft` mixin): injects at the `HEAD` of
  `handleKeybinds()`. If the player `isUsingItem()` and the item is a
  `PotionsBeltItem` (checked via `getUseItem()` — hand-agnostic, covers main
  hand and offhand), drains any pressed hotbar key's `consumeClick()` and
  sends `SelectColumnPayload` for it. Draining the click here means vanilla's
  own hotbar-switch loop later in the same method call sees `clickCount` at
  0 and does nothing — no cancellation of the whole method, so every other
  keybind handled by `handleKeybinds` (attack, drop, swap hands, etc.) is
  unaffected. New client mixin config `potions-belt.client.mixins.json`,
  registered in `fabric.mod.json`'s `mixins` array (client-only environment)
  — this is the first mixin since the session-2 template cleanup removed the
  example ones.
- 5 new JUnit cases on `firstPotionSlotInColumn` (row 1 direct pick, row 2
  fallback, row 3 fallback, all-empty column, out-of-range column), same
  pattern as `BeltInventoryTest` from session 4. 11/11 tests pass.

Design/API notes (verified via `javap` against the mapped 1.21.11 jars and
the fabric-api jar before writing code, same practice as sessions 3-4):
- `Options.keyHotbarSlots` and `Minecraft.player`/`options` are public
  fields — no accessor mixin needed, direct field reads from the injected
  method.
- `LivingEntity.getUseItem()` / `isUsingItem()` / `getUsedItemHand()` live on
  the common `LivingEntity` class, so they work identically client- and
  server-side; `getUseItem()` already reflects whichever hand triggered the
  use, so no explicit hand check was needed to satisfy the "main hand or
  offhand" requirement.
- `Item.releaseUsing(stack, level, entity, timeCharged)` is vanilla's single
  hook for every early-stop path (manual release, hotbar switch, item
  dropped) — confirmed there's no separate mixin needed for the "switch
  slot mid-drink" edge case, vanilla's own `stopUsingItem()` routes through
  it.
- Fabric API C2S payload types are registered via
  `PayloadTypeRegistry.playC2S()`; `CustomPacketPayload.Type` is constructed
  directly with our own `Identifier` (its `createType(String)` helper
  defaults to the `minecraft` namespace, not useful here).
- `ServerPlayConnectionEvents.DISCONNECT` gives `(ServerGamePacketListenerImpl
  handler, MinecraftServer server)`; `handler.getPlayer()` is the clean
  accessor for the cleanup.

In-game verification (João, all 8 checklist cases): all OK, including the
subtle ones — releasing right click after selecting a column consumes
nothing and clears the selection (case 6), and switching hotbar slot mid-drink
stops the vanilla use entirely so the pending column can't leak into a later
drink (case 7). Key interception via the mixin worked reliably; the
sneak+scroll fallback from PLAN.md was not needed.

One UX gap found during testing, deferred to milestone 6: pressing a number
key gives no feedback at all — no HUD indicator, no action-bar message
naming the selected potion/column. Functionally correct (the right slot gets
consumed) but not discoverable from the player's perspective. Milestone 6's
"action bar feedback" item should specifically cover this: some sort of
message/indicator on column selection, not just on the abort/empty cases
already handled.

Confirmed by playtesting as intentional, not a bug: sneak + right click
opens the GUI and can't drink at the same time — this is exactly the
mutual-exclusivity documented in PLAN.md's GUI section (sneak reserved for
the menu, plain right click reserved for drinking).

Milestone 5 done. Next session: milestone 6, polish (sounds, column-selection
feedback per above, tooltip contents preview, edge-case pass).

## Session 4 (2026-07-13): milestone 4 — drinking

**Summary: drinking is implemented and fully verified in-game — held right
click drinks the first available potion (vanilla 1.6 s animation), replaces
only that slot with a bottle, and every other slot is untouched. Unit tested
(6 JUnit cases on `BeltInventory`). One design revision came out of
playtesting: forward-compaction (the original plan) was replaced with
in-place replacement after it broke João's column-based loadout.**

What was added:
- `PotionsBeltItem.use()`: plain right click checks `BeltInventory.hasPotion`
  and calls `startUsingItem` (vanilla drink animation/timing comes from a new
  `Consumables.DEFAULT_DRINK` component on the belt item, set in
  `ModItems.java` — no custom timers). Empty/bottles-only belt: action bar
  message + `SoundEvents.BUNDLE_INSERT_FAIL` (soft "leather" thud, picked
  over the original harsh `DISPENSER_FAIL`), gated by `player.getCooldowns()`
  so holding right click doesn't spam the sound (same pattern as vanilla
  Ender Pearl).
- `PotionsBeltItem.finishUsingItem()` (server-only): finds the first potion
  slot via `BeltInventory.firstPotionSlot`, applies its effects through the
  potion's own `Consumable.onConsume` (same code path as drinking a normal
  potion — effects, stats, advancement trigger, sounds all vanilla), then
  `BeltInventory.takePotionAt(belt, slot, keepPotion)` replaces *only that
  slot* with an empty bottle. In creative (`keepPotion` true), the belt isn't
  written to at all — matches vanilla, which doesn't consume the item.
- `BeltInventory.isAcceptable()`: belt slots now also accept empty glass
  bottles (not just drinkable potions), so a player can manually load bottles
  too, not only receive them from a drink. Used by `PotionSlot.mayPlace` in
  `PotionsBeltMenu`.
- 6 JUnit tests in `src/test/java/.../BeltInventoryTest.java` (gappy belt,
  full belt, bottles-only, empty belt, creative leaves the belt untouched,
  `hasPotion`), run via `net.fabricmc:fabric-loader-junit` on the classpath
  so real Minecraft classes (bootstrapped via
  `SharedConstants.tryDetectVersion()` + `Bootstrap.bootStrap()`) are usable
  in plain JUnit — added `test { useJUnitPlatform() }` to `build.gradle`.

Design revision — compaction vs. in-place replacement (confirmed by
playtesting): the original plan compacted every remaining potion forward in
row-major order after a drink (`BeltInventory.takeFirstPotion`, since
removed). João's actual usage was a 9-column loadout — 9 potion types, 3 of
each stacked one per row per column (e.g. column 1 = Regeneration x3) — and
compacting forward dragged potions from other columns/rows into column 1's
place, which read as "randomizing" the belt and looked like a real bug even
though the algorithm was doing what was written (verified via javap that
`ItemContainerContents.fromItems`/`copyInto` preserve slot index and order —
the bug was the compaction design itself, not those primitives). Replaced
with `BeltInventory.firstPotionSlot()` + `takePotionAt(belt, slot,
keepPotion)`: only the drunk slot changes, nothing shifts. This also
sidestepped a second bug the old code had: it always removed the potion from
the belt's data regardless of game mode, only conditionally skipping the
bottle for creative — so a creative-mode drink could permanently lose a
potion with no bottle returned. In-place replacement fixes this naturally,
since creative now skips the write entirely. PLAN.md's "Decisions" section
updated to record this as resolved (was previously flagged as an open risk
to check during playtesting).

In-game verification (João, two rounds):
1. Column-loadout drink (survival): only the drunk slot becomes a bottle,
   every other column/row untouched — OK.
2. Same in creative: belt completely unchanged, effect still applies — OK.
3. Cancel mid-drink (release early): nothing consumed — OK.
4. Bottles-only belt (now placeable thanks to `isAcceptable`): no animation,
   feedback shown — OK.
5. Empty-belt sound: soft, no longer spams while right click is held — OK,
   keeping as-is.
6. Sneak+right click still opens the GUI — OK.

Lessons / notes:
- **In-game testing handoff**: driving the dev client via the SendInput
  PowerShell helper (see session 3) burned a lot of Claude-side resources for
  marginal benefit. From this session on, in-game verification is done by
  asking João to test and report back, not by Claude driving input.
- Verified via `javap` before writing code (as in session 3): `Item` use/
  finishUsingItem signatures, `Consumable`/`Consumables` presets,
  `ItemContainerContents.fromItems`/`copyInto` internals (index-preserving,
  trims only trailing empty slots — ruled this out as the reordering bug's
  cause), `ItemCooldowns` API, `SoundEvents.BUNDLE_INSERT_FAIL` as a
  same-type drop-in for `DISPENSER_FAIL`.
- Gradle daemon connection is flaky in this environment ("Could not connect
  to the Gradle daemon" on first attempt after a period of inactivity) —
  simply retrying the same `./gradlew` command succeeds; not a real build
  problem.

Milestone 4 done. Next session: milestone 5, column selection (number keys
1-9 while drinking pick a column, row 1 -> 2 -> 3 fallback within it). The
in-place replacement design from this session means column contents no
longer drift over time, which should make milestone 5 more predictable than
originally planned.

## Session 3 (2026-07-13): milestone 3 — belt GUI

**Summary: the belt GUI works end-to-end and was verified in-game: sneak +
right click opens a 3x9 "Potions Belt" screen (shulker texture placeholder),
only drinkable potions go in (splash rejected on both drag and shift-click),
contents persist in `DataComponents.CONTAINER` across close/reopen, and the
belt itself is locked in place while its menu is open (click pickup and
number-key swap both blocked).**

What was added:
- `PotionsBeltMenu`: `AbstractContainerMenu` with 27 `PotionSlot`s
  (`mayPlace` = drinkable potion only) + player inventory, shulker slot
  layout. `quickMoveStack` mirrors `ShulkerBoxMenu` — the potion filter is
  honored because vanilla `moveItemStackTo` checks `mayPlace`.
- `BeltInventory`: static load/save between the belt `ItemStack`'s
  `DataComponents.CONTAINER` component and a `SimpleContainer(27)`.
  Save copies each stack to avoid aliasing live slot stacks into the
  (hash-cached) component. `isDrinkablePotion` = `stack.is(Items.POTION)`.
- `PotionsBeltScreen` (client): `AbstractContainerScreen` on the vanilla
  shulker box texture (same 3x9). Like `ShulkerBoxScreen`, needs
  `++imageHeight` (that texture is 167px, not 166).
- Wiring: `MenuType` registered in `PotionsBelt` (vanilla `MenuType`
  constructor + `FeatureFlags.VANILLA_SET`, no extended data needed —
  client menu starts with a dummy container, contents arrive via vanilla
  menu sync); screen registered via `MenuScreens.register` in
  `PotionsBeltClient`; `PotionsBeltItem.use()` opens the menu on sneak via
  `SimpleMenuProvider` (plain right click still the milestone 4 stub).

Belt-lock design (the "backpack in itself" edge case):
- Player inventory slots use a `PlayerSlot` whose `mayPickup` returns false
  whenever the slot holds a belt — covers click pickup, shift-click, and
  drop (Q), wherever the belt sits, on both sides, with no index tracking.
- That is NOT enough for `ClickType.SWAP` (number keys / offhand F): vanilla
  reads the hotbar/offhand stack directly, bypassing slot checks. So
  `clicked()` is overridden to swallow SWAP clicks whose swap target is a
  belt. Verified in-game: pressing 1 over a grid slot does nothing.

Persistence:
- A `SimpleContainer` listener writes the component back on every content
  change, plus a final save in `removed()`. Survived multiple close/reopen
  cycles in-game, including the menu being closed by the pause menu.

Lessons / notes:
- **Verify mappings with `javap` before writing code**: the remapped
  Minecraft jars live in `.gradle/loom-cache/minecraftMaven/...`. Checking
  signatures there (blit overloads, `Inventory.getSelectedSlot`,
  `MenuType` ctor, `MenuScreens.register` visibility) avoided compile
  roulette; the only miss was `Level.isClientSide` now being private (use
  `isClientSide()`).
- 1.21.11 `GuiGraphics.blit` takes a `RenderPipeline` first arg
  (`RenderPipelines.GUI_TEXTURED`).
- In-game verification was done by driving the dev client with a Win32
  SendInput PowerShell helper (screenshot → click → screenshot); the
  computer-use tooling can't target a Gradle-launched `java.exe` window.
  João was at the machine and verified sneak+right-click himself.
- The vanilla container tooltip on the belt item (hover in any inventory)
  doubles as a free contents preview — worth remembering for milestone 6.

Left for next session (milestone 4): drinking — held right click,
first-available selection, bottle-back-to-belt compaction in
`BeltInventory`.

## Session 2 (2026-07-13): milestone 2 — skeleton builds and runs

**Summary: template cleanup done (package rename, blocks/mixins dropped,
metadata cleaned); `./gradlew build` succeeds and `runClient` loads the mod
with zero errors; belt item registers, renders, and its recipe parses in a
real world load. Git workflow defined: develop on `dev`, merge to `main`
manually at milestones.**

What changed:
- Package renamed `scr0ols.potionsbelt.modid` -> `scr0ols.potionsbelt`
  (sources moved with `git mv`; `maven_group` in `gradle.properties` and
  `fabric.mod.json` entrypoints updated). No `modid` references remain.
- Dropped `ModBlocks`/`potion_shelf` (block assets, blockstate, loot table,
  lang entry) and both example mixins. The mixin config JSONs were removed
  entirely — no mixins exist yet; a client mixin config returns in
  milestone 5 with `KeybindsMixin`.
- `fabric.mod.json` cleaned: real description, author `Scr0ols`, sources
  link, license MIT. Template CC0 `LICENSE` in the Gradle folder replaced
  with the repo's MIT license.
- `PotionsBeltItem.use()` reduced to a stub with milestone TODOs; logger
  messages switched to English.
- Portuguese strings/comments in template code translated to English.

Two real bugs found and fixed via actual runClient output:
- **Recipe format**: the template recipe used pre-1.21.2 ingredient objects
  (`{"item": "minecraft:string"}`). Since 1.21.2 recipe keys map directly to
  item id strings (verified against vanilla `lead.json` inside the 1.21.11
  jar). Symptom: `Couldn't parse data file ... No key fabric:type` at world
  load; recipe count 1470 before the fix, 1471 after, error gone.
- **Missing client item definition**: since 1.21.4 items need
  `assets/<ns>/items/<name>.json` (client item definition) in addition to
  `models/item/`. The template predates this; added
  `items/potions_belt.json` pointing at the existing model.

Lessons / notes:
- Recipes only parse when a world loads, not at the title screen — errors
  like the recipe format one are invisible unless you actually enter a
  world and read the log.
- Harmless dev warning at launch: `Class path entries reference missing
  files: build/resources/client` — appears because `src/client/resources`
  is now empty (the client mixin config was its only file). Goes away in
  milestone 5.
- An old dev world in `run/saves` contains the removed `potion_shelf`
  block; Minecraft logs a recoverable "Unknown registry key" and replaces
  it with the default. Benign, self-heals per chunk.

Git workflow decided by João: all development lands on `dev` (push there);
`main` is stable and updated by merging `dev` manually when a milestone is
ready. Recorded in CLAUDE.md and README.md.

Left for next session (milestone 3): GUI — menu type, screen, potion-only
slot filter, `DataComponents.CONTAINER` persistence.

## Session 1 (2026-07-13): planning, loader decision, repo init

**Summary: chose Fabric 1.21.11; plan confirmed by João (drinkable-only, bottles return to belt, vanilla drink time); repo initialized at project root and pushed to GitHub.**

Decisions confirmed by João (details in PLAN.md "Decisions"):
- v1 accepts only drinkable potions; splash/lingering later, own nuances.
- Empty bottle returns to the belt: potions compact forward, bottle placed
  after the last potion; bottles collected via vanilla double-click gather.
  Watch during playtesting: compaction means number keys select the Nth
  potion in the current queue, not a fixed loadout column.
- Vanilla 1.6 s drink time — the belt must not change game balance.
- Target 1.21.11 confirmed.

Repo setup:
- Repo root = the `Potion's Belt` folder (docs at root, Gradle project in
  `potions-belt-fabric-1.21.11/`), remote `github.com/scr0ols/Potions-Belt`.
- João moved the old Fabric template into the repo as
  `potions-belt-fabric-1.21.11/`. Its standalone `.git` (1 local commit, no
  remote) was removed; a backup bundle went to the session scratchpad.
- Remote `dev` branch appeared on GitHub during the session (created by
  João); work landed on `main`, no branch convention defined yet.

- Inspected the two previous attempts in `D:\scr0ols\Dev\Minecraft Projects`:
  - `potions-belt-fabric-1.21.11`: standard fabric-example-mod template,
    Mojang mappings, pinned versions (Loader 0.18.4, API 0.141.3+1.21.11,
    Loom 1.15, Java 21). Gradle setup and assets reusable. Its one crash
    (2026-03-19) was `NullPointerException: Block id not set` from the
    `potion_shelf` block in `ModBlocks` — block registered without `setId`
    on `BlockBehaviour.Properties`. Blocks are out of scope, so the whole
    class is dropped rather than fixed.
  - `potions-belt-neoforge-mcreator-1.21.8`: MCreator-generated workspace,
    empty item stub. Discarded — MCreator regenerates sources from its own
    workspace file, incompatible with hand-written development.
- Loader decision (Fabric) and full reasoning recorded in PLAN.md; to be
  copied into README.md at repo init so it isn't revisited.
- GitHub repo `scr0ols/Potions-Belt` exists with a single initial commit
  containing only a LICENSE (MIT). Local project is not yet a git repo; plan
  is to `git init` here and merge that remote history at repo init.
- Template leftover to fix when importing code: package is
  `scr0ols.potionsbelt.modid` — the `.modid` suffix is template noise, new
  package is `scr0ols.potionsbelt`.
- Key design choice: belt inventory stored in vanilla
  `DataComponents.CONTAINER` (`ItemContainerContents`, same as shulker
  boxes) — no custom codec, free NBT serialization and sync. Drinking rides
  the vanilla item-use system (`startUsingItem` / `finishUsingItem`), no
  custom timers.
