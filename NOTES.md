# Potion's Belt — Session notes

## Session 8 (2026-07-15): milestone 8 design discussion + repo cleanup

**Summary: no code changes. Cleaned up two stray leftover files, then had a
full design discussion for milestone 8 (configurable settings) and captured
it in a new working doc, `MILESTONE8-SETTINGS.md`. Implementation
deliberately deferred to a future session — João wanted to think through the
design first rather than build against a half-settled plan.**

Cleanup: two accidental test textures (`ASDASD.png`, `asd3232.png`) had
ended up directly in the mod's item textures folder (untracked, not part of
any commit). Moved to `Logo/archived/` and added `Logo/archived/` to
`.gitignore` so archived source material doesn't get tracked. `Logo/` and
`Sounds/` themselves (source PNG/SVG/wav material) remain untracked but not
gitignored — out of scope for this cleanup, left as-is.

Milestone 8 design, all settled via discussion with João and written up in
`MILESTONE8-SETTINGS.md` (not yet folded into PLAN.md — that only happens
once implementation actually starts, per this project's doc convention):
- **Settings UI mechanism**: two access points — ModMenu integration (new
  dependency) plus a dedicated remappable keybind matching the existing
  `BeltKeybinds` pattern, both opening the same settings screen. Persistence
  via Gson JSON (already on the classpath).
- **Invert scroll-cycle direction**: simplest of the three, one boolean read
  where `MouseScrollMixin` currently hardcodes the direction ternary. No
  open questions.
- **"No pre-selector required" mode**: settled as a full replacement of
  `SELECT_MODIFIER` gating when enabled (not additive), covering both
  hotbar keys and scroll — effectively an opt-in revival of the original
  milestone 5/6 always-on design (the one that collided with vanilla's own
  hotbar keybindings, see session 7's "Playtest round 2" notes), now safely
  gated behind a setting instead of being the hardcoded default.
- **Combined inventory + belt tab**: the most-refined-but-still-open of the
  three. Settled: a real single `AbstractContainerMenu` combining vanilla's
  full inventory screen (crafting, armor, recipes, player grid) with the
  belt's 27 slots, not a recipe-book-style overlay swap; belt slots default
  visible with a show/hide button; trigger condition is "belt anywhere in
  inventory" (not just held), matching PLAN.md's original wording over an
  earlier "belt in hand" phrasing raised mid-discussion; and once enabled,
  this setting fully takes over both entry points (E and the dedicated
  "Open Belt Menu" keybind both open the combined screen, the standalone
  27-slot-only screen isn't used at all while the setting is on). Still
  open: which screen-building approach to use technically (extend vanilla's
  `InventoryScreen` vs. a parallel custom screen — needs a `javap`/sources
  jar check before deciding, same practice as every prior session), which
  belt's contents show if the player carries more than one (parallel to the
  existing "default column is per-belt" limitation — likely same answer:
  pick one deterministically, accept as a known limitation), exact
  show/hide button placement and persistence, and exact screen dimensions
  for the taller layout.

Next session: implementation, starting with the config/persistence
infrastructure and settings-screen scaffold (all three settings depend on
it), then invert-scroll (simplest, no remaining open questions) end-to-end,
then no-pre-selector mode, then the combined-tab setting last given it's
the most involved and still has open technical questions to resolve while
building.

**Follow-up (2026-07-15): milestone 7 (docs pass) fully implemented, plus a
new milestone 10 (localization) scoped and started.** Same session, pivoted
from milestone 8 planning to milestone 7 per João's request — he wanted
Potion's Belt to match the repo-standards level of his other mod,
`github.com/scr0ols/soundtweaks` (fetched its README/wiki structure for
reference: badges, full community-health files, GitHub Discussions with
templates, an extensive wiki — adapted, not copied). Design discussion
captured in a new working doc, `MILESTONE7-DOCS.md` (same pattern as
`MILESTONE8-SETTINGS.md`), then implemented in full within the same
session rather than deferred.

Found and fixed in passing: `potions-belt-fabric-1.21.11/.github/workflows/
build.yml` was **dead config** — GitHub Actions only reads `.github/
workflows/` from the true repo root (the `Potion's Belt/` folder), not a
subdirectory, so this workflow had never actually run. It also targeted
Java 25 (project uses 21) and assumed Gradle lived at the repo root. Fixed
by moving it to the real `.github/workflows/build.yml`, correcting the Java
version, and adding `working-directory: potions-belt-fabric-1.21.11` (plus
fixing the artifact path) so `./gradlew build` actually resolves.

Settled and built:
- **`CODE_OF_CONDUCT.md`**: Contributor Covenant v2.1 (the standard
  template, used verbatim per its own CC-BY-4.0 reuse terms), enforcement
  contact `joaosilva98.work@gmail.com`.
- **`SECURITY.md`**: same contact; notes the mod's actually-small attack
  surface (client-side only, no server component of its own, no network
  beyond the existing bounded C2S payloads already covered in PLAN.md).
- **`CONTRIBUTING.md`**: build/test commands, the `dev`-branch-only
  workflow, code style pointers (mirrors this file's own CLAUDE.md rules,
  written out for outside contributors who won't have CLAUDE.md loaded),
  conventional commits, and a PR checklist.
- **`.github/ISSUE_TEMPLATE/`**: `bug_report.md`, `feature_request.md`, and
  `new_translation.md` (the last one specifically for milestone 10's
  claim-before-you-translate process). Plus `.github/
  PULL_REQUEST_TEMPLATE.md`.
- **`.github/DISCUSSION_TEMPLATE/`**: `q-a.yml`, `ideas.yml`,
  `show-and-tell.yml` — the three categories agreed on. **Enabling
  Discussions itself is a repo setting** (Settings > Features), not a file;
  João still needs to flip that toggle on github.com himself.
- **README.md overhaul**: shields.io badges (Minecraft, Fabric, Java,
  License — core set only, styled after soundtweaks' but with this
  project's own values; no Modrinth/CurseForge/CI/Ko-fi badges yet, nothing
  to point them at), a rewritten feature list matching actual end-state
  behavior (the old one still listed GUI/drinking/column-selection as
  unchecked TODOs despite milestones 3-6 being done), requirements table,
  installation steps, links to the new community-health files and the
  wiki, and an accurate status checklist including milestones 7-10.
- **Milestone 10 (localization), started**: `pt_pt.json` added (European
  Portuguese — João's own language; `pt_br` deliberately left for a future
  community contribution rather than done now). Grepped the whole
  `src/main/java` tree for `Component.literal`/`Component.translatable`
  before translating anything — found exactly one user-facing string
  (`potions-belt.belt.no_potions`), already using `Component.translatable`
  correctly; no hardcoded-English debt from milestones 4-6's rapid
  iteration, closing that open question from `MILESTONE7-DOCS.md`.
  Contribution process (claim via the new `new_translation.md` issue
  template, then PR a new lang JSON, vanilla's own silent fallback to
  English covers incomplete translations) is documented, not yet exercised
  by an actual outside contributor.
- **`wiki/` folder (new, repo root)**: 8 pages drafted as plain markdown —
  `Home`, `Installation`, `Main-Screen-and-HUD`, `Column-Loadouts`,
  `Keybinds`, `Translations`, `FAQ-and-Troubleshooting`,
  `Building-from-Source`. **Not yet in the actual GitHub wiki** — this
  session has no GitHub write access (same limitation the Prompting Guide
  already anticipated), and the real GitHub wiki is a separate git remote
  (`<repo>.wiki.git`) this local checkout isn't set up to push to anyway.
  João needs to enable the wiki (Settings > Features) and copy each page's
  content in manually, or push `wiki/`'s content to the wiki remote
  himself.

Verification: `./gradlew build` (from `potions-belt-fabric-1.21.11/`) —
`BUILD SUCCESSFUL`, all existing unit tests still pass. This session's
changes are docs/resources/CI-config only, no gameplay code touched, so no
in-game verification was needed.

Left open for a future session:
- Actually enabling GitHub Discussions and the wiki (repo settings only
  João can flip) and copying `wiki/`'s pages in.
- Milestone 10's remaining open question from `MILESTONE7-DOCS.md`: whether
  to eventually add a build-time completeness check for lang files, versus
  continuing to rely on vanilla's silent English fallback (current lean,
  unchanged).
- Milestone 8 (configurable settings) is still fully unimplemented — this
  session's pivot was purely to milestone 7/10; `MILESTONE8-SETTINGS.md`'s
  design from earlier today stands as next in line.

## Session 7 (2026-07-14): milestone 6 — sounds

**Summary: milestone 6 fully closed out — sounds and the edge-case pass both
done, everything confirmed working in-game. Three custom sounds: `belt_open`
(menu-open), `bottle_open` (drink-start), `bottle_close` (drink-end or
abandoned). Build and all 11 tests pass throughout.**

Three sounds, converted from João's recordings via
`ffmpeg -c:a libvorbis -qscale:a 5` (Minecraft only loads Vorbis .ogg, not
.wav) and registered in `ModSounds.java` as
`SoundEvent.createVariableRangeEvent`s. Every source recording came in quiet
(peaks between -5 and -17 dB) and needed a gain boost before encoding
(`ffmpeg -af volume=<N>dB`, targeting a ~-2 dB peak) plus a full 1.0F
`playSound` volume — worth checking for on any future sound addition, since
it happened three times in a row.

Final design, after several playtest rounds narrowed it down:
- `belt_open`: plays in `PotionsBeltItem.openMenu()`, covering both entry
  points (the dedicated keybind and the E-key redirect) since they share
  that one method. Chosen over an equip/holding sound because Minecraft has
  no generic "item picked up in hand" hook to attach one to.
- `bottle_open`: plays in `use()`'s success branch, immediately before
  `startUsingItem`, so it always lands ahead of the vanilla drink sound.
- `bottle_close`: closes out *every* drink, consumed or not — the design
  settled on "every drink resolves to open+drink+close (consumed) or
  open+close (abandoned), never open with no resolution." Firing this
  correctly needed three separate hooks, since no single vanilla mechanism
  covers every way a drink can end:
  - `releaseUsing()` (`Item#releaseUsing`) — manual early release.
  - `LivingEntityMixin` (new, first mixin in this project targeting a
    vanilla class instead of client input) — `LivingEntity.
    updatingUsingItem()`'s own per-tick item-mismatch check, which calls
    `stopUsingItem()` directly, bypassing `Item#releaseUsing` entirely.
  - `ServerGamePacketListenerImplMixin` (new) — hotbar-slot switch
    specifically turned out to be a *third*, separate bypass:
    `handleSetCarriedItem` (the server's packet handler for the switch)
    calls `stopUsingItem()` directly too, before `updatingUsingItem()` ever
    gets a chance to run, so `LivingEntityMixin` alone doesn't catch it —
    this took real `javap -c` digging to find, since it directly
    contradicted an unverified claim in session 5's notes that
    `Item#releaseUsing` was "vanilla's single hook for every early-stop
    path" (it isn't, for hotbar switches specifically).
  - `finishUsingItem`'s success branch, for the consumed case, via
    `DelayedBottleClose.schedule()` (below) rather than an immediate call.

`DelayedBottleClose` (new) ended up doing three jobs, each added in response
to real in-game testing:
1. **The ~0.2s "lower the bottle" delay** on the consumed-drink close only
   (João's request, after confirming the vanilla drink sound already sits a
   full 1.6s after `bottle_open` structurally — verified via `javap` that
   `Consumable.onConsume`'s `GENERIC_DRINK` sound fires once, at completion,
   not periodically). Deliberately *not* implemented by delaying
   `startUsingItem()` itself: that would push total drink time past
   vanilla's 1.6s (conflicts with PLAN.md's "must not change game balance"
   decision) and would need real cross-thread scheduling state that's a
   genuine race condition in singleplayer (client prediction and the
   integrated server share one JVM). Delaying only the *close*, after the
   potion's already been fully applied server-side, sidesteps both problems.
2. **Per-entity dedup**, after testing showed a single clean drink could
   trigger two of the hooks above in close succession (a real race between
   the natural-completion path and something else reaching `releaseUsing`
   for the same drink — root mechanism not fully pinned down despite deep
   `javap` tracing of `ServerGamePacketListenerImpl`/`releaseUsingItem`, but
   the delay window is what made it audible). Fixed pragmatically: whichever
   hook claims a given entity's close first (within a 10-tick window) wins,
   any other trigger is silently ignored — robust regardless of which path
   races first.
3. **A hold-off gate on the *next* drink** (`hasPending`, checked in
   `use()`), after chaining several drinks by holding right click showed the
   next `bottle_open` could land on or right after the previous
   `bottle_close` — first because nothing stopped a new drink from starting
   the instant the close was scheduled, then (once gated) because the gate
   cleared the instant the close sound *started* rather than after it
   finished. Fixed by blocking new drinks until a short buffer past when the
   close actually plays, not just until it's been scheduled.

Temporary diagnostic logging (added mid-session to pin down the double-close
race via `run/logs/latest.log`) was removed once its job was done, before
committing.

## Session 6 (2026-07-14): milestone 6 — sticky column default + HUD preview

**Summary: implemented the design settled at the end of session 5 (see that
session's 2026-07-14 entries below) — column selection is now a persistent
per-player default instead of a one-shot pending value, with fallback to
first-available on a plain right click, plus a HUD icon previewing the
potion about to be drunk. Build and all 11 unit tests pass unchanged (no
test-covered logic changed — `firstPotionSlotInColumn`/`firstPotionSlot`
themselves are untouched, only their caller). In-game verification pending —
João to test.**

What changed:
- `BeltSelections`: renamed concept from "pending column, cleared per drink"
  to "sticky default column, cleared only on disconnect". `get()` now
  returns `1` (not `-1`) when the player has never picked a column, matching
  "default starts at column 1". `set()`/`clear()` unchanged in shape.
- `PotionsBeltItem.finishUsingItem()`: always resolves via the player's
  default column first (`BeltSelections.get`); if that column is empty,
  falls back to `BeltInventory.firstPotionSlot` (first potion anywhere)
  instead of aborting. Only aborts (feedback + sound) if the fallback is
  also empty, i.e. the whole belt is empty. No longer calls
  `BeltSelections.clear` — the default persists across drinks now.
- `PotionsBeltItem.releaseUsing()` **removed entirely**: its only job was
  clearing the pending column on early release/interrupt, which is exactly
  what should *not* happen anymore. Item's default no-op behavior applies.
- `onColumnSelected()` (mid-drink number-key press) is otherwise unchanged:
  still updates the default immediately (even if empty), still cuts the
  current drink short via `stopUsingItem()` if the picked column is empty —
  that specific-drink-cancel UX from the 2026-07-14 follow-up fix (see
  session 5 below) is independent of the sticky-default change and stays as
  the one case where an empty column doesn't fall back.
- `BeltInventory.getItem(belt, slot)`: new read-only accessor (no mutation),
  added so the HUD preview can read the actual `ItemStack` at a resolved
  slot without going through `takePotionAt` (which would consume it).
- `ClientBeltState` (new, client-only): static `int defaultColumn` mirroring
  the server's `BeltSelections` value for the local player. Set the instant
  `KeybindsMixin` sends a `SelectColumnPayload` (no server round trip needed
  — the client already knows what it just picked), reset to `1` on
  `ClientPlayConnectionEvents.DISCONNECT`. No new S2C payload, as planned.
- `BeltHud` (new, client-only): a Fabric HUD element rendering a preview icon
  of the potion about to be drunk, registered via
  `HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, ...)` in
  `PotionsBeltClient`. Visible whenever the belt is held in main hand or
  offhand (checked directly, not tied to `isUsingItem()`). Resolves the
  preview with the *exact same calls* `finishUsingItem` uses
  (`BeltInventory.firstPotionSlotInColumn` against `ClientBeltState`'s
  column, falling back to `firstPotionSlot`, then `getItem` to read the
  stack) so preview and actual drink can't drift apart by construction.
  Positioned like vanilla's offhand-item hotbar indicator (same sprites,
  `hud/hotbar_offhand_left`/`_right`, same 29x24 box and item-icon inset)
  but mirrored to the side matching the player's main arm — i.e. the
  opposite side from where vanilla draws the *real* offhand item — so the
  two icons never overlap even when the belt is in the offhand.

Design/API notes (verified via `javap` + sources jars before writing code,
same practice as prior sessions):
- Fabric API 0.141.3 (1.21.11) uses a newer HUD layer system than
  `HudRenderCallback`: `net.fabricmc.fabric.api.client.rendering.v1.hud.
  HudElementRegistry` (`addFirst`/`addLast`/`attachElementBefore`/
  `attachElementAfter`/`replaceElement`), with named vanilla anchors in
  `VanillaHudElements` (e.g. `HOTBAR`). `attachElementAfter(afterThis,
  newId, element)` inherits the anchor's render condition — confirmed via
  the module's sources jar that this means our element automatically
  respects the same "hide GUI" (F1) / screen-open suppression the hotbar
  itself already has, no manual guard needed.
  `HudElement.render(GuiGraphics, DeltaTracker)` is the functional method.
- Exact vanilla offhand-indicator layout, read via `javap -c` disassembly of
  `Gui.renderItemHotbar`/`renderSlot` against the mapped client jar (no
  decompiler available, so worked from bytecode operands directly): hotbar
  background is `centerX-91, guiHeight-22, 182x22`; the offhand slot sprite
  is `29x24` at `centerX-91-29` (drawn when `mainArm.getOpposite() ==
  LEFT`, i.e. default right-handed play) or `centerX+91` (opposite case),
  both at `y = guiHeight-23`; the item icon inside is inset to
  `(boxX+3, guiHeight-19)` on the left sprite and `(boxX+10, guiHeight-19)`
  on the right one (the two sprites aren't symmetric paddings — read
  directly off the bytecode rather than guessed). `BeltHud` reuses these
  exact numbers, substituting `mainArm` for `mainArm.getOpposite()` to land
  on the opposite side from the real indicator.
- `GuiGraphics.renderItem(ItemStack, int, int)` and `renderItemDecorations
  (Font, ItemStack, int, int)` are the plain (non-entity-seeded) item-icon
  draw calls, sufficient here since potions don't need the bob/seed
  animation `renderItem(LivingEntity, ...)` provides for hand-held items.
- `ClientPlayConnectionEvents.JOIN`/`DISCONNECT` (fabric-networking-api-v1)
  give clean hooks for client-side session-scoped state; used `DISCONNECT`
  only (mirrors the server's own disconnect-only clear — no need for a
  separate `JOIN` reset since a fresh client launch already starts at `1`).

Not yet done this session (left for next): sounds, and the milestone-6
edge-case testing pass. In-game verification of everything built this
session (sticky default across multiple drinks/sessions, plain-right-click
fallback behavior, HUD icon/name showing/hiding correctly in both hands and
updating live, the new keybinds, scroll cycling, and the open-menu key) is
also still open — João to test.

**Follow-up (2026-07-14): potion name label + full redesign of column/menu
input, from João's first round of in-game feedback.** Playtesting the HUD
icon surfaced two things:

1. The icon alone didn't say *which* potion it was previewing. Fix:
   `BeltHud` now also draws the potion's display name (`drawCenteredString`,
   white, centered above the icon) whenever the preview is non-empty — folds
   the still-open "tooltip contents preview" milestone-6 item into this
   feature instead of a separate hover tooltip.
2. A bigger UX problem: right click was overloaded (drink *and*, via
   sneak/mid-drink hotbar keys, the only way to touch column selection or
   open the menu), which made drinking feel forced/eager — you had to
   commit to a drink just to change your pick. Discussed with João; agreed
   direction: **fully decouple selection and menu-opening from right click**,
   using dedicated, remappable Fabric keybindings instead of hijacking
   vanilla hotbar keys or overloading sneak. Sneak was explicitly rejected
   as the modifier despite being the existing GUI-open convention — João's
   reasoning: it's a key that "directly influences gameplay" (sneaking
   protects against fall damage/ledges), so gating belt actions behind it
   creates situations where a player needs to sneak for movement safety and
   use the belt at the same time. Full remappability was the explicit
   end goal (João: doesn't know each player's keyboard/hand setup, wants
   everyone able to bind these however is convenient for them).

Implemented:
- `PotionsBeltItem.use()` simplified: right click is now unconditionally
  "drink" — the `isShiftKeyDown()` branch that opened the menu is gone
  entirely. Sneak state no longer changes right-click behavior at all.
- `OpenBeltMenuPayload` (new, C2S, empty record via `StreamCodec.unit`):
  sent when the new "Open Belt Menu" keybind is pressed.
  `PotionsBeltItem.openMenu(player)` (new, server-side) does what `use()`'s
  sneak branch used to do, just triggered independently.
- `BeltKeybinds` (new, client-only): registers **9 independent "Select
  Column N" keybindings** plus **one "Open Belt Menu" keybinding** via
  `KeyBindingHelper.registerKeyBinding`, all under a new "Potions Belt"
  category in the vanilla Controls screen, all **unbound by default** (a
  hardcoded default risks colliding with a given player's existing
  movement/mod bindings — matches João's "let each player set it up"
  requirement more literally than picking defaults ourselves would).
  Polled once per client tick via `ClientTickEvents.END_CLIENT_TICK`
  (`consumeClick()`, same idiom the old mixin used on vanilla's hotbar
  keys) — only acts while the belt is held in either hand; queued clicks
  are drained (not deferred) while it isn't.
- `MouseScrollMixin` (new, client mixin into `MouseHandler#onScroll`):
  João also asked for scroll-wheel cycling as a second, parallel selection
  method ("1-9 hotkeys plus scroll is enough for this phase, don't want to
  overload the player with more"). While the belt is held and no screen is
  open, scrolling cycles the default column ±1 (wrapping 1-9) instead of
  switching the hotbar slot. No Fabric API event exists for world mouse
  scroll (checked every fabric-api submodule jar for a "Scroll" class —
  only screen-scoped scroll hooks exist in `fabric-screen-api-v1`), so this
  mixes into vanilla's own `MouseHandler#onScroll` and cancels it for this
  one case, read via `javap -c` disassembly (no decompiler available) to
  find the exact `ScrollWheelHandler.onMouseScroll`/`Inventory.
  setSelectedSlot` call vanilla makes at the end of that method — confirmed
  there's no earlier hook point to redirect instead of cancelling outright.
  **Known trade-off, called out to João, not yet contested**: this steals
  scroll-to-switch-hotbar-slot for as long as the belt is actively held in
  a hand; switching away by scroll requires releasing the belt first (or
  using the vanilla number keys, which stay completely free).
- `KeybindsMixin` **stripped down significantly**: the old `handleKeybinds`
  hotbar-key-interception loop is gone (vanilla hotbar 1-9 are no longer
  touched at all, by anything, ever). What's left: the HEAD injection on
  `handleKeybinds` now only clears `suppressBeltDrinkUntilRelease` on
  key-release (unchanged logic, just the surrounding loop removed), the
  `startUseItem` block (unchanged), and a new public
  `onColumnPicked(LocalPlayer, int)` entry point that both `BeltKeybinds`
  and `MouseScrollMixin` call — centralizes "mirror into `ClientBeltState`,
  check for the mid-drink-empty-column cancel case, send
  `SelectColumnPayload`" so the two input methods can't drift in behavior.
- `PotionsBeltItem.isHeldBy(Player)` (new, common, static): the
  "belt in main hand or offhand" check, previously duplicated per-mixin,
  now shared by `BeltKeybinds`, `KeybindsMixin`, and `MouseScrollMixin`
  (`BeltHud`'s own `heldBelt` stays separate since it needs the actual
  `ItemStack`, not just a boolean).

Design/API notes (verified via `javap` + sources jars before writing code):
- `KeyMapping.Category` is a record wrapping an `Identifier` as of this MC
  version (no longer a plain enum/string) — created via
  `KeyMapping.Category.register(Identifier)`; its Controls-screen label
  comes from `Component.translatable(id.toLanguageKey("key.category"))`,
  i.e. lang key `key.category.<namespace>.<path>`. Individual `KeyMapping`
  names are used directly as their own lang keys, matching vanilla's
  `key.attack`-style convention.
- `new KeyMapping(name, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.
  getValue(), category)` is the unbound-by-default constructor call —
  `InputConstants.UNKNOWN.getValue()` avoids pulling in a direct LWJGL/GLFW
  import just for the sentinel int.
  `net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.
  registerKeyBinding` is the Fabric-side registration call (confirmed via
  that module's sources jar; its own doc example still shows the pre-1.21.2
  `KeyBinding.Category.MISC` API, now stale — verified the real signature
  against the mapped 1.21.11 `KeyMapping` class directly instead of trusting
  the doc comment).
- Confirmed no generic Fabric API mouse-scroll event exists in 0.141.3 by
  grepping every `fabric-api` submodule jar for scroll-related classes —
  only `fabric-screen-api-v1` has scroll hooks, and those are Screen-scoped
  (`ScreenEvents`/similar), not usable for world/hotbar scroll.
  `net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.
  END_CLIENT_TICK` is the tick hook used for polling `BeltKeybinds`.

Build and all 11 unit tests pass (no test-covered logic changed — this is
all input/networking wiring, not `BeltInventory` slot-picking logic).
In-game verification of this whole redesign is still open.

**Bug found via `runClient` (2026-07-14): `KeybindsMixin` crashed the client
at startup.** João ran `runClient` right after the redesign above and hit an
immediate mixin-apply crash; `run/logs/latest.log` had the real error:
`InvalidMixinException: Mixin ... KeybindsMixin ... contains non-private
static method onColumnPicked(...)`. Root cause: `onColumnPicked` had been
added as a `public static` helper directly on the `@Mixin(Minecraft.class)`
class so `BeltKeybinds` and `MouseScrollMixin` could call it — but a mixin
class is dissolved into its target class at load time and Sponge Mixin only
accepts recognized injector methods (or `@Shadow`/`@Unique` members) inside
one; a plain public static method with no annotation is invalid and fails
the whole mixin apply, which crashes Minecraft before the window even opens
(mixin transformation runs at classload, ahead of any game logic). This is
exactly the kind of error the build/test suite can't catch — `./gradlew
build` compiles and unit-tests fine regardless, since it's a bytecode-apply
failure Sponge Mixin only raises when the target class is actually
transformed at runtime. Lesson: any mixin class that needs to expose logic
to *other* classes must not do it directly — move that logic to a plain
(non-mixin) class instead.

Fix: moved `onColumnPicked` (and the `suppressDrinkUntilRelease` flag it
touches) off `KeybindsMixin` and onto `ClientBeltState`, which was already
the natural home for client-side belt-selection state. `KeybindsMixin` now
contains *only* its two private `@Inject` methods and nothing else — the
minimum a mixin class should ever hold. `BeltKeybinds` and
`MouseScrollMixin` now call `ClientBeltState.onColumnPicked(...)` directly.
Rebuilt clean, all 11 tests still pass; in-game re-verification (does it
actually launch now, plus the full checklist from before) still pending.

**Playtest round 2 (2026-07-14): full checklist results from João, plus a
real design flaw in the 9-separate-column-keybinds approach.** Results
against the checklist above (A-G, items 1-20): everything passed except
6, 10, and 11 (16 untested — couldn't reconnect to check disconnect reset).
Two real fixes came out of it, plus one new feature request:

1. **Item 6 (column keys) failed for a genuine reason, not user error.**
   João bound "Select Column 1".."9" to physical keys "1".."9" (the obvious
   choice) and only column 1 worked. Root cause: those are also vanilla's
   own default hotbar-slot keybindings. `KeyMapping.click()` fires for every
   mapping bound to a key, so pressing "3" both (a) let vanilla's own
   `handleKeybinds()` switch the hotbar to slot 3, *and* (b) queued a click
   on our "Select Column 3" mapping — but our polling ran on
   `ClientTickEvents.END_CLIENT_TICK`, which fires *after* `handleKeybinds()`
   within the same tick. By the time we checked `isHeldBy(player)`, the
   hotbar had already switched away from the belt (unless the belt happened
   to already be sitting in slot 3, which is exactly why key "1" "worked" —
   the belt was in hotbar slot 1). Not a Minecraft limitation ("only one key"
   per João's guess) — a same-tick ordering collision from reusing physical
   keys vanilla already owns.

   João's own proposed fix was the right one: gate column selection behind
   a held modifier key (his examples: Shift or V) instead of giving each
   column its own binding, i.e. bring back something like milestone 5's
   "hotbar keys mean something else while X is held" shape, but with X now
   a real remappable `KeyMapping` instead of hardcoded sneak. Implemented:
   the 9 "Select Column N" keybindings are gone, replaced by one
   `BeltKeybinds.SELECT_MODIFIER` keybinding (unbound by default). While
   it's held and the belt is held, `KeybindsMixin` drains vanilla's own
   `keyHotbarSlots[0..8]` clicks (same trick milestone 5 used) and
   reinterprets them as column picks — this has to stay a `handleKeybinds`
   HEAD injection (not a tick event) specifically so the drain happens
   *before* vanilla's own hotbar-switch loop later in the same method, which
   is what actually avoids the collision this time (vanilla never sees the
   click while the modifier is down). "Open Belt Menu" is unaffected, still
   its own separate keybind.
2. **Item 10: scroll direction was backwards.** Scrolling down moved the
   column left; João wanted down = forward/right. One-line fix in
   `MouseScrollMixin`: flipped the `yOffset > 0 ? ... : ...` ternary.
3. **New request: the vanilla "Open/Close Inventory" key (E) should open
   the belt menu instead of the player inventory when the belt is the
   selected (main hand) item.** João's reasoning, paraphrased: right now
   opening the menu depends entirely on a brand-new custom keybind
   (`Open Belt Menu`) the player has to discover and bind themselves;
   reusing E is zero-config and matches an established pattern from other
   "bag"-style mods. Implemented as a third `handleKeybinds` HEAD injector
   in `KeybindsMixin`: drains `client.options.keyInventory`'s click when the
   main-hand item is the belt, sending `OpenBeltMenuPayload` instead of
   letting vanilla open the plain inventory screen. Deliberately scoped to
   main hand only (matches "the selected item," and means a belt sitting
   only in the offhand doesn't block normal inventory access via E). No
   downside to losing "plain inventory via E" while the belt is in main
   hand, since `PotionsBeltMenu` already includes the full player inventory
   grid at the bottom (same shape as a shulker box screen) — nothing is
   actually inaccessible, just presented alongside the belt's 27 slots
   instead of alone. The dedicated "Open Belt Menu" keybind stays too, as
   an alternative for players who'd rather not give up E's vanilla meaning
   at all (e.g. by leaving it bound to something else, since both roads
   lead to the same `OpenBeltMenuPayload`).

Note for next session: **`./gradlew build`/`test` cannot catch mixin-apply
errors** like the `KeybindsMixin` crash above — Sponge Mixin only validates
a mixin when its target class is actually loaded through Fabric Loader's
Knot classloader, which only happens when the game actually launches, not
during plain JUnit tests. A green build is necessary but not sufficient
proof a mixin change is safe; `runClient` (or in-game testing) is the only
real check for that class of bug.

Build and all 11 tests pass after this round's changes too. In-game
re-verification of items 6, 10, and 11, plus the new E-key behavior, is the
next thing João should test.

**Playtest round 3 (2026-07-14): modifier/E-key redesign fully retested,
one more scroll bug found, belt-lock removed, settings menu scoped for
milestone 8.** Checklist results: items 1-3, 5-13, 15 all OK. Item 4 was
functionally OK but surfaced a real gap — the select-modifier gates the
hotbar-key column picks correctly, but scroll cycling (`MouseScrollMixin`)
had no such gating and fired on scroll alone, whenever the belt was held.
Fixed: scroll cycling now also requires `BeltKeybinds.SELECT_MODIFIER.
isDown()`, matching the hotbar-key path exactly — holding the belt without
the modifier leaves scroll as plain vanilla hotbar-switching, in both hands.

Item 14 (moving/reorganizing the belt in inventory while its menu is open)
was NOK, and João found the actual cause himself: session 3's
"backpack-in-itself" lock (`PotionsBeltMenu.PlayerSlot#mayPickup` returning
false for a belt, plus a `clicked()` override blocking SWAP clicks that
would move one) — confirmed via code read, unrelated to anything changed
this session, just newly annoying now that E opens the menu so much more
casually than the old deliberate sneak+right-click did. João's call:
**keep vanilla behavior "at almost all costs"** — Minecraft already lets
you Q-drop/drag items around while their own container-adjacent GUIs are
open elsewhere, and restricting that here isn't worth the friction, as long
as it can't be turned into a duplication exploit. Reviewed for that
specifically before removing anything: moving/dropping the belt only ever
relocates the *same* `ItemStack` object the menu's `beltStack` field and
`PotionSlot`s already reference (vanilla inventory manipulation reassigns
references, it doesn't copy), so nothing new is ever created; and
`PotionSlot.mayPlace` already independently rejects the belt item from
being placed into its own 27-slot grid (it's neither a potion nor a glass
bottle), so "belt inside itself" was never actually dependent on the lock
we just removed. No dupe path found. Removed both the `mayPickup` override
and the `clicked()` SWAP block entirely — `PlayerSlot` had no other purpose
so the whole inner class is gone, player-inventory slots are now plain
vanilla `Slot`s. Deliberately did *not* add any "close the menu if the belt
leaves the inventory" logic (`stillValid` still always returns `true`) —
wasn't asked for, and the 27 slots continuing to show/edit whichever
`ItemStack` the menu opened with, even after it's dropped, is harmless
(same non-dupe reasoning: still just one object, no duplication, purely a
cosmetic "still editing a stack that's now elsewhere" state until the menu
is manually closed).

Also discussed, deliberately deferred rather than built now: a real
settings menu for the mod, prompted by two things João raised while
picking apart today's defaults — the scroll direction we just hardcoded
might not suit everyone, and there's a genuine alternate design for
opening the belt (E always opens the *normal* inventory screen, taller,
with an added "Potions Belt" tab down the left edge like vanilla's own
recipe-book/stats tabs, rather than jumping straight to the standalone
27-slot screen) that's a bigger, config-worthy behavioral choice rather
than a fixed default. Recorded as milestone 8 in PLAN.md with all three
candidate settings (scroll direction, "no pre-selector required" mode, the
combined inventory+belt tab) — not started, no config persistence or
settings screen exists yet.

Build and all 11 tests pass after this round's changes too (scroll
modifier-gating, belt-lock removal). In-game re-verification of the fixed
scroll gating and the now-unlocked belt movement is the next thing to test.

**Playtest round 4 (2026-07-14): both fixes confirmed, session closed out.**
5/5 OK: scroll without the modifier is plain vanilla hotbar-switching in
both hands; scroll with the modifier held cycles the column correctly in
the (now corrected) direction; Q-dropping and drag-relocating the belt out
of its own open menu both work normally with contents intact on reopen; and
the belt still can't be placed into its own 27-slot grid, confirming the
lock removal didn't open a self-containment hole. Everything built this
session (sticky default column, HUD icon+name preview, the keybind/E-key
redesign replacing sneak+right-click and mixin-hijacked hotbar keys, the
select-modifier + scroll column selection, and the belt-lock removal) is
now in-game verified. Session done; milestone 6 is complete except sounds
and a broader edge-case pass, both explicitly deferred (not attempted this
session). Milestone 8 (configurable settings) is scoped in PLAN.md but not
started — next real candidate for a future session, alongside milestone 6's
remaining sounds/edge-case items.

**Environment note (2026-07-14, recurrence): Gradle daemon loopback connect
timeouts, separate from the earlier WMI `runClient` hang.** Mid-session,
`./gradlew build` started failing every attempt with `Could not connect to
the Gradle daemon` / `Connection timed out: getsockopt` on `127.0.0.1` —
distinct from the "first attempt after inactivity" daemon flake documented
in session 4 (that one succeeds on a plain retry; this one didn't across ~5
attempts, `--no-daemon`, and `--stop` + retry). The one full build that did
run (before this started) was clean: `BUILD SUCCESSFUL`, all 11 tests
passed, with every source change from this session already in place — so
the code itself is verified; this is purely local networking, matching the
"correlated with ProtonVPN churning the network stack" pattern from the
WMI note above. If this recurs, check VPN state and loopback connectivity
first before assuming a real build problem.

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
feedback per above, tooltip contents preview, edge-case pass). See the
2026-07-14 design-decision entry below for column-selection feedback: the
plan is now settled (sticky default column + HUD preview), not just a known
gap.

**Follow-up fix (2026-07-14): drink animation no longer plays out fully on a
column that's already empty.** Found by João testing a fully-drunk column
(all 3 rows turned to bottles) — pressing right click + that column's number
still played the full 1.6 s vanilla drink animation before the "No potions in
the belt" message appeared; nothing was actually consumed (correct), but the
wait was pure friction, and the empty-belt case (no animation at all) had set
the expectation that failure should be immediate. Root cause: `use()` only
gates on `BeltInventory.hasPotion(belt)` (any potion anywhere), since the
column itself isn't known yet at that point — the number key arrives as a
payload only after `startUsingItem` already began. Column validity could
previously only be checked at `finishUsingItem`, i.e. after the full
animation.

Fix: `PotionsBeltItem.onColumnSelected(player, column)` (called from the
`SelectColumnPayload` server receiver in `PotionsBelt.java`, replacing a
direct `BeltSelections.set` call) now checks the column immediately upon
selection — if the player is mid-drink on the belt and the selected column
is already empty, it shows the same feedback (action bar message + sound,
extracted into a shared `announceNoPotions` helper used by `use()`,
`finishUsingItem`'s abort branch, and this new path) and calls
`player.stopUsingItem()` right away. `stopUsingItem()` syncs the "using item"
entity flag to the client immediately, cutting the vanilla animation short —
confirmed this is the same mechanism already relied on for the "switch
hotbar slot mid-drink" case (session 5, checklist item 7), so no new client
code was needed. `finishUsingItem`'s own abort branch stays as a fallback for
the (currently unreachable in practice, but cheap to keep) case where the
column becomes empty between selection and the animation's natural end.
Build and all 11 existing unit tests still pass unchanged (the fix only
changes *when* the abort feedback fires, not the fallback logic itself, so
no new unit test was needed — this is server networking + entity-state
behavior, verified in-game only). Verified in-game by João: animation now
cancels immediately on an empty-column pick.

That first pass had a follow-on bug: with right click held (the normal way
to drink), cancelling the animation didn't stop the click itself, so on the
very next tick `use()` saw `hasPotion(belt)` true (other columns still had
potions) and started a brand new drink — the animation cancelled and
immediately restarted, visible as a flicker. Fix, entirely client-side in
`KeybindsMixin`: picking an empty column now also sets a
`suppressBeltDrinkUntilRelease` flag; a new injection at the head of
`Minecraft.startUseItem()` cancels belt use-starts while that flag is set,
and it's cleared the moment the use key is released (or the belt is no
longer held). Net effect: an empty-column pick ends that belt use for real —
right click must be released and pressed again to start another one. Columns
that do have a potion, and plain right click with no column selected, are
unaffected. Confirmed working in-game by João (2026-07-14).

**Environment note (2026-07-14): `runClient` startup hang was a wedged
Windows WMI service, not IPv6.** Symptom: the client loads every mod fine,
prints "287 Datafixer optimizations took … ms", then hangs forever — no
window, no crash, no further log lines. Cause: Minecraft gathers system info
at startup via OSHI (`oshi-core` 6.9.0 + JNA), which on Windows queries WMI;
when the WMI subsystem is wedged, the first call (`getComputerSystem`) blocks
the main thread indefinitely, right after Datafixer bootstrap and before the
window is created. Reproduced in isolation with a standalone OSHI call against
the same jar — hung >30 s at exactly that point. Fix: restart WMI from an
elevated shell (`net stop winmgmt` then `net start winmgmt`), then relaunch;
verify with `Get-CimInstance Win32_ComputerSystem` returning instantly.
Intermittent (several runs today succeeded, several hung) and correlated with
ProtonVPN churning the network stack. NOT caused by IPv6 and NOT fixed by
`-Djava.net.preferIPv4Stack=true`: Mojang auth runs asynchronously on a later
worker thread (its 401 is harmless and the game proceeds past it), so it
cannot block startup here. That earlier IPv4 experiment in `build.gradle` was
reverted.

**Design decision (2026-07-14): sticky default column + HUD preview, for the
column-selection feedback gap in milestone 6.** Discussed with João before
implementing (not yet built). Two parts:

1. Column selection becomes persistent instead of per-drink. The column
   picked via right click + number key becomes the player's default for
   future drinks too, not just the one in progress — updated even if that
   column turns out empty. Default starts at column 1. Plain right click (no
   hotkey) tries the default column first; if empty, falls back to the
   existing "first available potion anywhere" behavior instead of failing —
   this was the key open question (rejected the stricter alternative where
   an empty default would fully block plain right click). Default is
   per-player, not per-belt-item (two belts in inventory share one default —
   accepted as a known v1 limitation), and now only clears on disconnect
   instead of after every drink.
2. A HUD icon previews the potion about to be drunk, visible whenever the
   belt is held in either hand (not just mid-animation). It must mirror the
   real drink logic exactly (default column's potion, else first-available
   fallback, else empty indicator) using the same
   `BeltInventory.firstPotionSlotInColumn` / `firstPotionSlot` calls the
   drink itself uses, so preview and actual behavior can't drift apart.
   Client-side only, no new S2C payload needed — the client already knows
   its own selection the instant it sends `SelectColumnPayload`, so it can
   mirror the default column locally instead of waiting on the server.

Implementation (persistent-selection semantics, HUD rendering, exact
placement): done in session 6, see that entry above.

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
