# Translations

Potion's Belt currently ships with:

| Language | File |
|---|---|
| English (US) | `en_us.json` |
| Portuguese (Portugal) | `pt_pt.json` |

Language files live at
`potions-belt-fabric-1.21.11/src/main/resources/assets/potions-belt/lang/`,
using standard Minecraft resource-pack JSON — the same format vanilla and
every other mod uses.

## Contributing a new language

1. **Claim the language first**: open an issue using the
   [new translation template](../../issues/new?template=new_translation.md),
   naming the [Minecraft locale code](https://minecraft.wiki/w/Language)
   you're translating (e.g. `de_de`, `fr_fr`). This avoids two people
   translating the same language at once.
2. Copy `en_us.json` to `<locale_code>.json` in the same `lang/` folder.
3. Translate every value (the string on the right of each `:`) — leave the
   keys (left side) untouched, they're internal identifiers, not text
   shown to players.
4. Open a pull request. See [CONTRIBUTING.md](../CONTRIBUTING.md) for the
   general PR process.

## Updating an existing translation

Same flow — claim it via the new-translation issue template (marking it as
an update, not a new language), edit the existing file, open a PR.

## What happens if a translation is incomplete

Minecraft falls back to English for any key missing from a non-English lang
file — a partial translation still works, it just shows English for the
gaps. You don't need every key translated before opening a PR, though a
complete translation is obviously preferred.
