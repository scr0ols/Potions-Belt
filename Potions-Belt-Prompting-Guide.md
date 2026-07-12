# Prompting guide for Claude Fable 5, Potion's Belt project

Source: official Anthropic documentation on prompting Claude Fable 5 (platform.claude.com/docs, "Prompting Claude Fable 5" section), adapted to this case: building a Minecraft mod across several sessions (1 to 10).

## Why this differs from prompting older models

Fable 5 is trained for long, ambiguous work. Instead of giving it a detailed step-by-step checklist, it performs better when you give it the goal, the reason behind it, and let it decide the "how". Overly prescriptive instructions (a habit carried over from older models) tend to degrade output rather than improve it.

This matters especially here because the project spans multiple sessions: you need a system that makes Fable 5 remember what has already been decided, instead of re-asking everything each session.

## CLAUDE.md already covers the standing rules

There's a `CLAUDE.md` at the repo root, which Claude Code (or any harness that supports it) loads automatically at the start of every session, no need to paste it. It already contains: the mod's one-paragraph summary, the language rule (English everywhere), coding style (no speculative abstractions, no unneeded error handling), documentation upkeep (README/NOTES.md/PLAN.md), and the Git rules (no co-authored-by, no "Generated with Claude" signatures, conventional commits). The prompts below assume CLAUDE.md is already loaded, so they don't repeat any of that, they only add what's specific to that session.

## The 5 pieces you'll use

1. `CLAUDE.md`, standing rules, loaded automatically every session (see above).
2. A **project prompt**, pasted once, at the start of the first session. Contains the mod's functional spec and the planning-phase instruction.
3. A **planning phase**, inside that same first session, before any code: the agent produces a `PLAN.md` and you approve it before it proceeds.
4. A **memory file** inside the repository itself (`NOTES.md`), which Fable 5 keeps updated session after session.
5. A **session kickoff prompt**, short, referencing `NOTES.md` and `PLAN.md` and stating what to do in that specific session.

## 1. Project prompt (first session)

```
I'm building a Minecraft mod called "Potion's Belt", in the repository
https://github.com/scr0ols/Potions-Belt. This project will be built across
several sessions (between 1 and 10), each focused on part of the work. I need
you to act as a persistent technical collaborator on this project, not an
assistant that starts from zero each conversation. Standing rules for this
project are in CLAUDE.md, read it first if you haven't already.

MOD CONTEXT (the reasoning behind each technical decision):
I want to give the player an alternative to carrying loose potions in the
hotbar/inventory: a "potion belt" item that works as a small dedicated
container, with a GUI similar to a chest's, but that only accepts items of
type Potion (including splash and lingering, to be decided).

Functional spec:
- Grid of 3 rows by 9 columns (27 slots), only accepts potions.
- With the belt equipped in the hotbar, holding RIGHT CLICK for X ms drinks
  the first available potion (first row, left to right).
- RIGHT CLICK followed by a number key 1 to 9 drinks the potion in column N
  of the first row. If that column is empty in row 1, fall back to column N
  in row 2, then row 3, before failing.
- This is an initial spec, not a rigid contract: if you hit a usability
  problem or a Minecraft API limitation that makes it unworkable as
  described, say so and propose the closest alternative to the original goal.

Loader and version: still open between Fabric and Forge. During this
session's planning phase, compare both for this specific use case (custom
item with a container-style GUI, capturing scroll/number-key input, side
effects on item consumption) and recommend one, with a short justification.
Document the decision and the reasoning in the README, so we don't revisit
this in later sessions.

EXISTING CODE:
I previously attempted to start this project. There are folders with code at
D:\scr0ols\Dev\Minecraft Projects. Before starting from scratch, inspect those
folders: if there's anything reusable (project setup, base item, loader
config), reuse it; if it's outdated or a dead end, tell me why and start
clean. Don't assume you must reuse everything, only what makes sense.

PLANNING PHASE (before writing any code):
Before touching code, produce a PLAN.md file with: the mod's goal in 1-2
sentences, the loader decision and why, an inventory of what you reused (or
didn't) from the existing code, the proposed architecture (which
classes/modules, how the item stores the data for the 27 potion slots, how
held-right-click vs. right-click-plus-number-key is detected), a list of edge
cases to handle (empty belt, potion not found in any row, belt inside another
belt, stacking identical potions, etc.), and open questions for me. Show me
that plan and wait for my confirmation before moving to implementation. In
later sessions, don't repeat this full phase, only update PLAN.md if the
architecture changes.

BEHAVIOR I WANT FROM YOU:
- When you have enough information to act, act. Don't re-litigate decisions
  already made or list options you won't pursue in your final messages.
- Before reporting progress, verify each claim against an actual tool result
  (a build that runs, a test that passes, a file that exists). If something
  is unverified, say so explicitly. If something failed, say it failed.
- Pause and ask me only when it's something only I can decide (e.g. final
  name of an item, a design trade-off, a destructive action like deleting
  code). For everything else, decide and move forward.
- At the end of each session, summarize in plain language what was done and
  what's left, without jargon or shorthand I haven't seen before.

MEMORY SYSTEM:
Maintain a `NOTES.md` file at the repo root with decisions made, the
reasoning behind them, and problems already solved (e.g. "Fabric API X
doesn't support Y, worked around it with Z"). One lesson per section, with a
one-line summary at the top of each. Update the existing file instead of
duplicating information. At the start of every new session, read NOTES.md
and PLAN.md before assuming what's already been done.

First task this session: do the planning phase above. Only after I confirm
PLAN.md should you initialize the repository (Fabric or Forge, per your
recommendation), create README.md (mod context, chosen loader and why, how
to build/run, current development status), and the .gitignore appropriate to
the chosen toolchain.
```

## 2. Kickoff prompt for later sessions (2nd onward)

Short, since CLAUDE.md, NOTES.md, and PLAN.md already carry the standing context:

```
Continuing the "Potion's Belt" mod (repo scr0ols/Potions-Belt). Read NOTES.md
and PLAN.md before starting to pick up context from previous sessions.

Today's session: [describe the concrete task here, e.g. "implement the
belt's GUI" or "implement the held-right-click plus number-key logic"].

When done, update NOTES.md with what was decided and what's left. If
anything changes the architecture described in PLAN.md, update it too.
```

## 3. Fine-tuning worth keeping on hand

Verification in long sessions (useful from the 3rd/4th session onward, once the code has several interacting pieces):
```
Establish a way to check your own work every [X], for example after each new
feature: run the build, run existing tests, and check against the PLAN.md
spec before marking the task done.
```

Effort level: by default Fable 5 uses "high", which is recommended for most of this project's work. It's only worth lowering to "medium" for routine tasks (e.g. writing the .gitignore) or raising to "xhigh" if you hit something with many edge cases (e.g. the belt's row fallback logic).

Longer autonomous sessions, if you decide to let Fable 5 work without watching in real time:
```
You're operating autonomously this session. For reversible actions that
follow naturally from the original request, proceed without asking for
confirmation. Before finishing, check that the last thing you wrote isn't a
promise of work still to be done ("I'll do X"); if it is, do it first.
```

## Notes on current state

- I don't have the GitHub connector linked in this session, so I can't read or write directly to the scr0ols/Potions-Belt repo. The prompt above already includes creating README.md and .gitignore as a task within the session, for the agent (with repo access, via Claude Code or similar) to handle.
- I also don't have access to D:\scr0ols\Dev\Minecraft Projects. The prompt already instructs the agent to inspect the old code on its own, once it's running locally with disk access.
