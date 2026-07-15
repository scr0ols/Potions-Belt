# Milestone 7 — docs pass: design doc

**Status: implemented 2026-07-15** — see NOTES.md's session 8 follow-up
entry for the full build-out. This doc stays as the design record; nothing
below needs re-reading to pick up context, NOTES.md is more current.

Working document for milestone 7. Mirrors the `MILESTONE8-SETTINGS.md`
pattern: capture ideas here first, fold into PLAN.md only once settled.
Reference project for standards/style: `github.com/scr0ols/soundtweaks`
(João's other mod — has README badges, full community-health files, GitHub
Discussions with templates, and an extensive wiki; adapt, don't copy).

## Scope (expanded from the original "README + wiki" framing)

João's ask covers the full standard-OSS-repo checklist, matching
soundtweaks:
1. **Community health files**: `LICENSE` (already exists, MIT — no change
   needed), `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, `SECURITY.md`.
2. **README overhaul**: badges, modern visual structure/formatting — not
   just accurate content (current README is also stale: still shows GUI/
   drinking/column-selection as unchecked TODOs in the Status section,
   despite milestones 3-6 being done).
3. **GitHub Discussions**: enabled, with templates and contribution norms
   for bug reports/PRs.
4. **Wiki**: extensive, covering everything a player needs — usage, not
   just "what it does."
5. **New: a localization/translations milestone** (not part of the docs
   pass itself, but raised in this same discussion since it affects what
   the README/wiki need to document). Currently the mod only has
   `en_us.json`. Needs: an initial set of translated languages, and a
   documented process for the community to contribute new translations or
   suggest new languages via PR.

## Repo inventory (current state, checked 2026-07-15)

- `LICENSE`: MIT, already in place at repo root (both the doc root and
  inside the Gradle project, per session 1/2 notes).
- `.github/FUNDING.yml`: already exists, points at `ko_fi: scr0ols`.
- No `.github/ISSUE_TEMPLATE/`, no `PULL_REQUEST_TEMPLATE.md`, no
  `CODE_OF_CONDUCT.md`, `CONTRIBUTING.md`, or `SECURITY.md` yet.
- `src/main/resources/assets/potions-belt/lang/`: only `en_us.json`.
- README's Status checklist is stale (last updated at milestone 2-ish);
  needs a full rewrite against actual end-state behavior regardless of
  everything else in this doc.

## Settled (2026-07-15)

- **`CODE_OF_CONDUCT.md`**: Contributor Covenant (the standard template).
- **`SECURITY.md`**: report via `joaosilva98.work@gmail.com` (a dedicated
  address, distinct from João's personal email) — note in the file that the
  mod is client-side-only with a small attack surface (no server code, no
  network beyond vanilla-style C2S payloads already covered in PLAN.md).
- **README badges**: core set only — Minecraft version, Fabric, Java,
  License — styled like soundtweaks' (shields.io, with a Minecraft logo
  data-URI and matching color scheme), but with Potion's Belt's own values,
  not copied verbatim. No Modrinth/CurseForge/CI/Ko-fi badges yet (not
  published on those platforms, no confirmed-running CI badge, sponsor
  badge not requested) — revisit once the mod is actually published
  somewhere.
- **GitHub Discussions + templates: full setup.** Categories (Q&A, Ideas,
  Show & Tell), `.github/ISSUE_TEMPLATE/` (bug report, feature request),
  `PULL_REQUEST_TEMPLATE.md`, and a real `CONTRIBUTING.md` with
  contribution norms — matches soundtweaks. Claude prepares every file;
  João flips the Discussions toggle on github.com himself (repo setting,
  not a file).

## Localization / translations — new milestone 10

Separate milestone from the docs pass itself (real technical + process
work, not just writing), but scoped here since it was raised in the same
discussion and the wiki/README need to reference it.

**Settled (2026-07-15):**
- **Initial language set: English + Portuguese only** — `en_us.json`
  (exists) + `pt_pt.json` (European Portuguese, matching João's own
  language; `pt_br` left for a future community contribution rather than
  done up front). Not attempting soundtweaks' full 19-language spread in
  this pass — that milestone's ambition was to build the *process*, and let
  the community fill in the rest.
- **Community contribution process: PR + a translation-tracking issue
  template.** A contributor opens a "New Translation" GitHub issue first
  (claims the language, gets pointed at the key list/process), then a PR
  adding e.g. `de_de.json` (copy `en_us.json`, translate values, same keys).
  Documented in the wiki's Translations page. Reduces duplicate/incomplete
  PRs for the same language compared to PRs with no upfront coordination.

Open questions (for when this milestone actually starts):
- Full audit of every translatable string currently hardcoded vs. properly
  externalized to lang keys (e.g. action-bar feedback messages, HUD potion
  names use vanilla `Component.translatable` already via item names —
  worth double-checking nothing user-facing was written as a raw English
  string during milestones 4-6's rapid iteration).
- The `.github/ISSUE_TEMPLATE/new-translation.md` template's exact fields
  (language, contributor GitHub handle, any known partial-translation
  status).
- Whether missing keys in a non-English file should fall back silently to
  English (Minecraft's own vanilla behavior for lang files) or whether we
  want a build-time check that flags incomplete translations — leaning
  towards "just rely on vanilla fallback," but worth confirming.

## Open questions

See the two rounds of questions asked in-session — answers get folded back
into this doc's "Settled" section once given, same as
`MILESTONE8-SETTINGS.md`'s approach.

### Not Claude's call to make alone (GitHub repo settings)

Enabling **Discussions** itself is a repository *setting*
(Settings > Features > Discussions), not a file — Claude can prepare the
`.github/DISCUSSION_TEMPLATE/` files and `CONTRIBUTING.md` content, but
João needs to flip the toggle on github.com himself (no GitHub write access
in this session regardless — see the Prompting Guide's "Notes on current
state").
