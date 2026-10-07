# Building from Source

## Requirements

- JDK 25 (Minecraft 26.x runs on Java 25).
- Git.

---

## Steps

```
git clone https://github.com/scr0ols/Potions-Belt.git
cd Potions-Belt
```

**Linux / macOS**
```bash
./gradlew build                # builds both loaders and runs unit tests
./gradlew :fabric:runClient    # launches a dev client with the Fabric build
./gradlew :neoforge:runClient  # launches a dev client with the NeoForge build
```

**Windows**
```bat
gradlew.bat build
gradlew.bat :fabric:runClient
gradlew.bat :neoforge:runClient
```

The mod jars end up in `fabric/build/libs/` and `neoforge/build/libs/`, named
like `fabric-potions-belt-1.1.0+26.3.jar` (loader, mod version, Minecraft
version). Ignore the `-sources.jar` files.

---

## Repository layout

One Gradle build at the repository root:

```
common/      code and resources shared by both loaders (vanilla only)
fabric/      Fabric glue: entrypoints, registration, networking, keybinds
neoforge/    NeoForge glue: the same, on NeoForge's APIs
build-logic/ shared Gradle convention plugins
wiki/        wiki pages, drafted locally
```

Almost all of the mod lives in `common/`. A loader module only holds what
cannot be written against vanilla alone.

---

## Running tests only

```
./gradlew test
```

> [!WARNING]
> Unit tests cover pure logic (slot-picking, fallback rules) but **cannot**
> catch mixin-apply errors, which only surface when the game actually
> launches (`./gradlew :fabric:runClient` or `:neoforge:runClient`). A green
> `./gradlew build` is necessary but not sufficient proof that a change
> involving mixins is safe.

---

## Contributing changes back

See [CONTRIBUTING.md](../CONTRIBUTING.md) in the repo root for the branch
workflow, commit conventions, and pull request process.
