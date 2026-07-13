# Potion's Belt — Session notes

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
