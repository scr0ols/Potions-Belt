# Changelog

All notable changes to Potion's Belt are documented in this file.

## [Unreleased] - 1.1.0

### Changed

- Restructured into a single multi-loader project (shared `common` code plus
  thin Fabric and NeoForge modules). Gameplay is unchanged.
- Jars are now named `fabric-potions-belt-<version>+<minecraft version>.jar`
  and `neoforge-potions-belt-<version>+<minecraft version>.jar`.
- The Fabric build now uses the mod id `potionsbelt` (previously
  `potions-belt`), matching the NeoForge build.

### Breaking

- **Fabric:** because the mod id changed, belts from a 1.0.x
  Fabric world are not carried over: existing belts turn into unknown items.
  Take potions out of belts before updating a Fabric world. NeoForge worlds
  are unaffected (its id was already `potionsbelt`).

## [1.0.2] - 2026-07-30

### Changed

- Fabric build updated to Minecraft 26.2 (from 26.1.2), matching the
  NeoForge build's target version. No player-facing behavior change.

## [1.0.1] - 2026-07-30

### Changed

- Fabric build updated to Minecraft 26.1.2 (from 1.21.11), matching the
  NeoForge build's target version. No player-facing behavior change.

## [1.0.0] - 2026-07-17

### Added

- Potion's Belt item: a 27-slot (3x9) container that only accepts drinkable
  potions and empty glass bottles.
- Right-click drinking, using the vanilla 1.6s animation — no balance
  changes, just faster access.
- Column-based loadout system: dedicate each column to one potion type,
  with sticky column selection (remappable modifier + hotbar key or
  scroll).
- Row and belt-wide fallback when the selected column's potions run out.
- HUD preview showing exactly which potion is about to be drunk.
- Two ways to open the belt's GUI: a dedicated keybind, or <kbd>E</kbd>
  while the belt is your main-hand item.
- Custom sounds for opening the belt and for each drink starting/ending.
- Crafting recipe gated behind an Ominous Bottle (Trial Chamber loot).
- English (US) and Portuguese (Portugal) translations.
