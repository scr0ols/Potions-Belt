# Security Policy

## Scope

Potion's Belt is a **client-side gameplay mod** for Fabric/Minecraft. It has
a small attack surface by nature:

- No server component of its own — it runs inside the vanilla
  Minecraft/Fabric server-client model.
- No network communication beyond Minecraft's own custom-payload system
  (`SelectColumnPayload`, `OpenBeltMenuPayload`), which carries no
  user-supplied data beyond a bounded column number.
- No file I/O, external services, or credentials.

Most reports will likely be regular bugs (crashes, dupe glitches, unintended
behavior) rather than security vulnerabilities — for those, please use the
[bug report issue template](.github/ISSUE_TEMPLATE/bug_report.md) instead.

## Supported Versions

Only the latest release, built against the Minecraft/Fabric version
documented in [README.md](README.md), is supported. Older versions do not
receive security fixes.

## Reporting a Vulnerability

If you believe you've found an actual security issue (e.g. a way to crash or
exploit the server through the mod's network payloads, or corrupt world/save
data beyond normal item mechanics), please report it privately instead of
opening a public issue:

**joaosilva98.work@gmail.com**

Include:
- A description of the issue and its potential impact.
- Steps to reproduce (Minecraft version, Fabric Loader/API versions, mod
  version).
- Any relevant logs or crash reports.

You should expect an initial response within a few days. Once the issue is
confirmed, a fix will be prioritized and a new release published; you'll be
credited in the release notes unless you prefer to stay anonymous.
