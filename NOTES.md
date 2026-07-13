# Potion's Belt — Session notes

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
