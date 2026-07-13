# Potion's Belt — project context

Minecraft mod. Adds a "potion belt" item: a chest-like container that only
accepts Potion items, with a 3x9 grid (27 slots). Held RIGHT CLICK drinks the
first available potion; RIGHT CLICK + number key (1-9) drinks the potion in
that column, falling back row 1 -> row 2 -> row 3 if that column is empty.
Loader: Fabric, Minecraft 1.21.11, Java 21. Detailed architecture: see
`PLAN.md`. The Gradle/mod project lives in `potions-belt-fabric-1.21.11/`;
docs (PLAN, NOTES, README) live at the repo root. Build/run from inside the
Gradle folder.

Full history of decisions and lessons learned: see `NOTES.md`. Read both
before starting work on a new session.

## Language

Everything in English: code, comments, identifiers, commit messages, docs,
and the conversation itself. Do not switch to Portuguese mid-session.

## Coding

- Do the simplest thing that solves the current task. No speculative
  abstractions, no feature flags, no "prep for future features."
- No error handling/validation for cases that can't happen. Validate only at
  real boundaries (user input, external APIs).
- Don't refactor or clean up code outside the scope of the current task.
- Verify claims against real tool output (build, tests) before reporting
  something as done or working.

## Documentation

- Keep README.md accurate: how to build/run, chosen loader and why, current
  status.
- Update NOTES.md at the end of every session: what changed, why, and any
  lesson worth remembering. Update existing entries instead of duplicating.
- Update PLAN.md only if the architecture actually changes.

## Git

- Branch workflow: all development happens on `dev` (commit and push there).
  `main` is the stable branch; merging `dev` into `main` is a manual decision
  by João, done when a milestone is ready — never merge to `main` on your own.
- NEVER add "Co-Authored-By: Claude" or any similar co-authorship line.
- NEVER add "Generated with Claude", "🤖 Generated with Claude Code", or any
  similar AI-authorship signature/footer to commits, PR descriptions,
  comments, or any other generated content.
- Commit messages in English, conventional commits: `feat:`, `fix:`,
  `chore:`, `docs:`, `refactor:`, `perf:`, `test:`.
- Run `git log --oneline -10` before committing to stay consistent with
  existing history.
- Small commits, one logical change each.

For full prompting patterns and session-kickoff templates, see
`Potions-Belt-Prompting-Guide.md`.
