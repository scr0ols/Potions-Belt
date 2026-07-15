# Contributing to Potion's Belt

Thanks for considering contributing! This is a small personal project, but
outside contributions (bug reports, fixes, translations, ideas) are welcome.

Please also read the [Code of Conduct](CODE_OF_CONDUCT.md) — participation
in this project means agreeing to follow it.

## Ways to contribute

- **Report a bug**: open an issue using the
  [bug report template](.github/ISSUE_TEMPLATE/bug_report.md).
- **Suggest a feature**: open an issue using the
  [feature request template](.github/ISSUE_TEMPLATE/feature_request.md), or
  start a looser conversation in
  [Discussions > Ideas](../../discussions/categories/ideas) first if it's
  still an early-stage idea.
- **Ask a question**: use
  [Discussions > Q&A](../../discussions/categories/q-a) rather than an
  issue.
- **Translate the mod**: see the wiki's Translations page, and open an issue
  with the [new translation template](.github/ISSUE_TEMPLATE/new_translation.md)
  to claim a language before starting.
- **Fix a bug or implement a feature**: see the workflow below.

## Development setup

Requires Java 21 (JDK). From `potions-belt-fabric-1.21.11/`:

```
./gradlew build        # build + run unit tests
./gradlew test          # unit tests only
./gradlew runClient     # launch a dev Minecraft client with the mod loaded
```

## Branch workflow

- All development happens on `dev`. Branch your work off `dev`, and target
  `dev` when opening a pull request — not `main`.
- `main` is the stable branch, updated only by merging `dev` at project
  milestones. Don't open PRs against it directly.

## Code style

- Everything in English: code, comments, identifiers, commit messages, docs.
- Do the simplest thing that solves the problem — no speculative
  abstractions, no unneeded error handling/validation for cases that can't
  happen (validate only at real boundaries: user input, network payloads).
- Don't refactor or clean up code outside the scope of your change.
- Match the existing project structure (see `PLAN.md`'s Architecture
  section) rather than introducing a new pattern for something that already
  has one.

## Commit messages

Conventional commits, e.g. `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`,
`perf:`, `test:`. Small commits, one logical change each.

## Before opening a pull request

- Run `./gradlew build` — a green build (including unit tests) is required.
- If your change touches gameplay behavior, test it in-game via
  `./gradlew runClient` — the unit test suite intentionally doesn't cover
  everything (e.g. mixin-apply errors only surface at actual game launch,
  never during `./gradlew test`).
- Fill out the pull request template — it's applied automatically.

## Questions

If anything here is unclear, open a
[Q&A discussion](../../discussions/categories/q-a) rather than guessing.
