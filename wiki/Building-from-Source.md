# Building from Source

## Requirements

- JDK 21.
- Git.

## Steps

```
git clone https://github.com/scr0ols/Potions-Belt.git
cd Potions-Belt/potions-belt-fabric-1.21.11
./gradlew build        # produces the mod jar in build/libs/, runs unit tests
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

To launch a development client with the mod already loaded:

```
./gradlew runClient
```

## Repository layout

The Gradle project (everything you actually build) lives in
`potions-belt-fabric-1.21.11/`, one level below the repository root — the
root only holds project documentation (`README.md`, `PLAN.md`, `NOTES.md`,
this `wiki/` folder). Always run Gradle commands from inside
`potions-belt-fabric-1.21.11/`, not the repo root.

## Running tests only

```
./gradlew test
```

Note: unit tests cover pure logic (slot-picking, fallback rules) but
**cannot** catch mixin-apply errors, which only surface when the game
actually launches (`./gradlew runClient`). A green `./gradlew build` is
necessary but not sufficient proof that a change involving mixins is safe.

## Contributing changes back

See [CONTRIBUTING.md](../CONTRIBUTING.md) in the repo root for the branch
workflow, commit conventions, and pull request process.
