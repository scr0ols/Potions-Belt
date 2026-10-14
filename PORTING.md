# Porting to a new Minecraft version

Minecraft 26.x is unobfuscated, and almost all of the mod lives in `common/`,
so a version bump is mostly a change to the root `gradle.properties`. Allow
an hour or two plus the in-game checklist.

## Branches

1. Work on a `port/<minecraft version>` branch off `dev` and open a PR into
   `dev`, like any other change.
2. When `dev` is merged into `main`, create the `26.N` branch for the version
   that `main` previously targeted (if it does not exist yet), then tag the
   release as `<mod version>+<minecraft version>`.

## Steps

1. **Versions** in the root `gradle.properties`:
   - `minecraft_version`, `minecraft_version_range`
   - `neo_form_version`: list at https://projects.neoforged.net/neoforged/neoform
   - `fabric_version`, `fabric_loader_version`: https://fabricmc.net/develop/
   - `neoforge_version`: https://projects.neoforged.net/neoforged/neoforge (a
     beta is fine until a stable build ships; then switch)
   - `version`, as `<mod version>+<minecraft version>`
2. **Plugins** in the root `build.gradle` (`net.fabricmc.fabric-loom`,
   `net.neoforged.moddev`) and the Gradle wrapper, if the new version needs
   newer ones. Compare with the upstream
   [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template)
   branch for that Minecraft version.
3. `JAVA_HOME` must point to the JDK the version requires (`java_version`).
4. `./gradlew build`, then fix compile errors in `common/` first, then in the
   loader modules. Typical breakage: renamed vanilla methods, input constants
   (the 26.3 SDL3 switch changed key and button codes), and loader API
   renames.
5. Boot both dedicated servers (`./gradlew :fabric:runServer`,
   `:neoforge:runServer`) and check that they reach "Done" with no mixin
   errors. Mixins only fail at launch, never in `./gradlew test`.
6. Run the in-game checklist below on **both** loaders.
7. Update the README and wiki requirement tables and the CHANGELOG.

## In-game checklist

- [ ] The game starts; no mixin or registry errors in the log; the recipe
      loads on world creation.
- [ ] Controls: both keybinds are listed under the Potion's Belt category and
      can be rebound to a letter, a number, Shift and a mouse button; Esc
      cancels binding; bindings persist after a restart.
- [ ] Holding right click with the belt drinks the first potion (vanilla
      1.6 s) and the bottle returns to the belt.
- [ ] Modifier plus a hotbar number picks a column without switching the
      hotbar slot; an empty column falls back row 1, 2, 3.
- [ ] Modifier plus scroll cycles columns in the right direction; scroll
      without the modifier still switches the hotbar; scroll over an open
      GUI does nothing.
- [ ] The belt GUI opens by keybind and by `E` with the belt in the main
      hand; only potions can be inserted.
- [ ] The HUD preview updates on column change and hides with F1; the open
      and drink sounds play.
- [ ] Switching hotbar slot mid-drink cancels the drink.
- [ ] A dedicated server boots, and a multiplayer or LAN session can open the
      GUI and drink.
