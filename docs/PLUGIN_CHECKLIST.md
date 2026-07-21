# New or Edited Plugin Checklist

- Plugin name: `Copper Kingdom`
- Slug: `copper-kingdom`
- Repository: `carmelosantana/minecraft-copper-kingdom`
- Owner: `Carmelo Santana`
- Target version: `0.2.1` (released; no new version is proposed by this file)
- Paper version: `26.1.2 build 74`
- Java version: `25`
- Updater destination: `copper-kingdom.jar`
- External services: `none`
- Status: `active`

Maven `groupId`/`artifactId`: `org.xpfarm` / `copper-kingdom`. `plugin.yml` name: `CopperKingdom`.
Releasable JAR: `copper-kingdom-<version>.jar`. Latest tag in this clone: `v0.2.1`.

---

## READ THIS FIRST — this is a backfill, written 2026-07-21

This plugin **predates the checklist process**. It has never had a `docs/PLUGIN_CHECKLIST.md`,
and gates 1–6 and 8–12 were **never formally run or recorded** for it. This file is written after
the fact to record what is *actually known*, not to reconstruct a history that does not exist.

Accordingly:

- **Gate 7a carries real evidence**, produced by the docker-rig-consolidation effort's shared test
  rig. It is quoted verbatim below from
  `minecraft-plugin-docs/.superpowers/sdd/task-6-report.md`.
- **Gate 7b cites a real recorded run** (the 2026-07-19 ecosystem matrix) that lives in
  `minecraft-plugin-docs/CURRENT_STATE.md`. It was not re-run for this backfill.
- Everything else is either an **observed fact** (something readable in the repo or manifest today,
  with the place it was read stated) or is marked **NOT RECORDED / NOT RUN / UNKNOWN**.
- An **observed fact is not a passed gate**. A checkbox is ticked only where a gate criterion is
  genuinely satisfied by evidence quoted here. Where a gate was never run, the box is left
  unchecked and says so. Nothing on this page is inferred from "it probably happened."

---

## 1. Scope — NOT RECORDED

- [ ] Status is explicitly recorded as active, experimental, or excluded. **NOT RECORDED at the
      time.** `active` is asserted here from the plugin's presence in the updater manifest
      (`minecraft-plugin-updater/plugins.json`) and in the Active Plugin Releases table of
      `minecraft-plugin-docs/CURRENT_STATE.md`. No scoping decision was ever written down.
- [ ] Purpose, commands, events, permissions, configuration, persistence, and acceptance checks are
      defined. **NOT RECORDED — predates the checklist process.** No requirements interview was
      run and no acceptance checks were ever defined for any released version. The command and
      permission surface below is **read out of `src/main/resources/plugin.yml` today**; it is a
      description of what shipped, not a specification anyone wrote or signed off.
- [ ] Known limitations and any intentionally withheld gates are recorded. **NOT RECORDED** for
      any released version. The gaps known *as of this backfill* are listed under Known gaps.

### Commands and permissions — observed from `src/main/resources/plugin.yml`

| Command | Usage | Permission | Aliases |
| --- | --- | --- | --- |
| `/copperkingdom` | `/copperkingdom <give\|blessed\|test\|reload\|help> [args]` | `copperkingdom.use` | `ck` |

Permissions declared: `copperkingdom.use`, `copperkingdom.give`, `copperkingdom.blessed`,
`copperkingdom.test`, `copperkingdom.reload` — all `default: op`.

`description:` reads "Copper-based weapons with mythic lore mechanics including healing, grounding,
and storm interactions". Events, persistence, and configuration semantics are **not documented
anywhere in this repository** beyond `src/main/resources/config.yml` itself and were not audited
here.

### Known gaps (as of this backfill)

- No unit tests exist in the released tree: `src/test` is **absent**.
- No acceptance criteria exist against which any release could be judged.
- Runtime evidence covers **load and enable only** (gate 7a) — no command, event, permission,
  persistence, or reload behavior has ever been exercised on a stack for this plugin.

## 2. Repository — PARTIAL (observed)

- [x] Repository is `carmelosantana/minecraft-copper-kingdom` with an SSH `origin` and `main`
      branch. **Observed** via `git remote -v`:
      `origin git@github.com:carmelosantana/minecraft-copper-kingdom.git`. Local branches include
      `main`; this file is committed on `test/docker-rig-consolidation`, branched off `main`.
- [ ] Existing user-owned worktree changes were identified and preserved. **NOT RECORDED as a
      gate.** Observed today: `git status --short --branch` reports a clean tree on
      `test/docker-rig-consolidation`. Also observed: a **nested, git-ignored worktree** at
      `.claude/worktrees/jolly-clarke-6b8c39/` containing its own `src/`, `docker-compose.yml`, and
      a `src/test/java/.../NearbyBlockCacheTest.java` that the tracked tree does **not** have. That
      worktree belongs to some other session and was **not inspected, evaluated, or touched** by
      this backfill.
- [ ] No `herobrinesystems` references remain in source, metadata, workflows, remotes, or
      documentation. **PARTIALLY CHECKED, not a formal audit.** A case-insensitive grep of the
      working tree run on 2026-07-21 returned **zero hits**. Scope limits: it excluded `target/`,
      `.git/`, `releases/`, and `server/`, and it therefore says **nothing about git history**.

## 3. Metadata — PARTIAL (observed)

- [x] AGPL-3.0-or-later `LICENSE` and Maven license metadata are present and consistent.
      **Observed:** `LICENSE` begins "GNU AFFERO GENERAL PUBLIC LICENSE / Version 3, 19 November
      2007"; `pom.xml` declares `<name>GNU Affero General Public License v3.0 or later</name>`.
- [x] `https://xpfarm.org` metadata and Carmelo Santana author metadata are present. **Observed:**
      `pom.xml` `<url>https://xpfarm.org</url>`; `plugin.yml` `author: Carmelo Santana` and
      `website: https://xpfarm.org`.
- [ ] `play.xpfarm.org` is recorded as the public Minecraft server hostname where server identity
      is documented. **NOT PRESENT.** A grep of the working tree on 2026-07-21 found **no**
      `play.xpfarm.org` reference anywhere in this repository.
- [x] New work uses the `org.xpfarm` Maven group. **Observed:** `<groupId>org.xpfarm</groupId>`,
      and the main class is `org.xpfarm.copperkingdom.CopperKingdom`. Group and package agree.
- [x] Repository slug, artifact, releasable JAR, updater destination, and `plugin.yml` names are
      consistent. **Observed:** slug `copper-kingdom`, artifact `copper-kingdom`, JAR
      `copper-kingdom-0.2.1.jar`, updater destination `copper-kingdom.jar`, `plugin.yml` name
      `CopperKingdom`. The manifest `asset_regex` `^copper-kingdom-[0-9].*\.jar$` matches the JAR
      name.
- [ ] No secrets committed in source, defaults, tests, logs, history, or documentation.
      **NOT AUDITED.** No secret scan was run for this backfill. The plugin declares no external
      service, so there is no credential it would need — but that is an argument, not a scan.

**Observed drift note:** `pom.xml` sets `<maven.compiler.release>25</maven.compiler.release>` but
the `maven-compiler-plugin` block also carries `<source>21</source>`. Both are present in the same
POM. Which one wins was **not investigated** here.

## 4. Compatibility — PARTIAL

- [x] Java 25/Paper 26.1.2 build 74 compile succeeds and `plugin.yml` uses `api-version: '1.21'`.
      **Real evidence.** The shared-rig migration ran `mvn clean verify` on this repo: "Maven
      `clean verify` → BUILD SUCCESS, produced `target/copper-kingdom-0.2.1.jar`"
      (`minecraft-plugin-docs/.superpowers/sdd/task-6-report.md`). `pom.xml` depends on
      `io.papermc.paper:paper-api:26.1.2.build.74-stable` (`provided`); `plugin.yml` declares
      `api-version: '1.21'`.
- [x] Hard dependencies, soft dependencies, optional APIs, and load ordering were reviewed and
      declared. **Observed:** `plugin.yml` declares no `depend`, `softdepend`, `loadbefore`, or
      `libraries`. Non-test POM dependencies are exactly `paper-api` at `provided` scope. Nothing
      is bundled at runtime.
- [ ] Geyser/Floodgate/ViaVersion review covers Bedrock-safe input, UI, inventory, identity, and
      protocol behavior. **NEVER PERFORMED — NOT RECORDED.** No Bedrock-safety review of this
      plugin's input handling, inventory/UI surface, or identity handling exists. The gate 7a boot
      shows CopperKingdom *coexisting* with Geyser, Floodgate, and ViaVersion on one server; that
      is not a review and proves nothing about Bedrock behavior.

## 5. External services — NOT APPLICABLE

- [x] External integrations are disabled by default or require explicit configuration and have
      bounded timeouts. **Not applicable** — no external integration. `pom.xml` has no HTTP client
      dependency; `plugin.yml` declares no soft dependency.
- [x] Ollama/Umami-style external endpoints are optional and failure-tolerant. **Not applicable.**
- [x] Endpoint failure cannot fail server/plugin startup, and diagnostics redact secrets.
      **Not applicable.**

## 6. Tests and build — PARTIAL

- [ ] Unit tests cover separable logic, configuration, serialization, permissions, and failure
      paths. **NONE EXIST.** `src/test` is absent from the tracked tree; `mvn verify` therefore
      runs zero tests. (A test file exists inside the unrelated ignored worktree noted in gate 2;
      it is not part of this repository's tracked source and does not count.)
- [x] `mvn --batch-mode --no-transfer-progress clean verify` succeeds. **Real evidence**, quoted
      from `task-6-report.md`: "Maven `clean verify` → BUILD SUCCESS, produced
      `target/copper-kingdom-0.2.1.jar`". Note this is a build success with **no test coverage
      behind it**.
- [ ] The releasable JAR and embedded `plugin.yml` were inspected; `original-*` JARs are excluded.
      **NOT RECORDED.** No one has ever unzipped and inspected this plugin's built JAR or its
      embedded `plugin.yml`, and no bytecode-version check exists. What *is* observed: `pom.xml`
      configures `maven-shade-plugin`, and `.github/workflows/build.yml` filters
      `! -name 'original-*'` on the checksum, artifact-upload, and release-upload steps — so an
      `original-*` JAR cannot reach a release, though one may exist on disk after a local build.

## 7. Matrix

### 7a — single-plugin runtime verification — PARTIAL (real evidence, narrow scope)

Evidence source: **this effort's shared test rig** (`minecraft-plugin-docs/bin/xpfarm-test-stack`),
a disposable fresh-volume Legendary stack, recorded verbatim in
`minecraft-plugin-docs/.superpowers/sdd/task-6-report.md`. That report does not state its own date;
the sibling `task-4-report.md` dates the same consolidation effort **2026-07-20**.

- [x] Paper, Geyser, Floodgate, and ViaVersion start successfully together, with the plugin loaded
      **and enabled**. **VERIFIED.** Three independent observations, quoted exactly:

      Paper's own completion line:

      ```
      minecraft-1  | >....[K[17:00:59 INFO]: Done (19.232s)! For help, type "help"
      ```

      A **real Minecraft protocol handshake** against the Java port — not a bare TCP connect:

      ```
      verifying the Java port actually serves Minecraft...
      MOTD: "A Minecraft Server"
      VERSION: Paper 26.1.2 | protocol 775
      PLAYERS: 0 / 20
      ```

      RCON `plugins`, captured raw with `cat -v` so the `§` colour bytes are visible as `M-BM-'`:

      ```
      AUTH OK
      $ plugins
      M-BM-'xM-BM-'3M-BM-'4M-BM-'9M-BM-'fM-BM-'dM-BM-'aM-bM-^DM-9 M-BM-'fServer Plugins (4):
      M-BM-'xM-BM-'eM-BM-'dM-BM-'8M-BM-'1M-BM-'0M-BM-'6Bukkit Plugins:
       M-BM-'8- M-BM-'aCopperKingdomM-BM-'r, M-BM-'afloodgateM-BM-'r, M-BM-'aGeyser-SpigotM-BM-'r, M-BM-'aViaVersion
      ```

      `CopperKingdom` is prefixed `M-BM-'a` = `§a` = **green = enabled**, not merely listed. The
      header count `(4)` matches the four names listed (CopperKingdom, floodgate, Geyser-Spigot,
      ViaVersion), so no line was dropped from the capture. The report's own conclusion: "Despite
      ~12 months broken by the compose typo, CopperKingdom 0.2.1 loads and enables cleanly on a
      real stack — its onEnable() does not throw."

      Companion component versions were **not** recorded per-plugin in this report beyond what the
      `plugins` line shows; no Geyser/Floodgate/ViaVersion version strings are available for this
      specific run.

- [ ] Java and Bedrock smoke tests cover joins plus commands, events, permissions, persistence, and
      reloads. **NOT DONE — neither side.** No client ever joined. No `/copperkingdom` subcommand
      was ever dispatched, on this run or any other. Load-and-enable is the entire behavioral
      claim; nothing about what the plugin *does* has been observed.
- [ ] Public deployment smoke tests verify `play.xpfarm.org` reaches the intended entry points.
      Belongs to gate 11; **NOT DONE**.
- [x] Ollama and Umami unavailable-endpoint tests. **Not applicable** — no external integrations.

**Why this gate previously had no evidence at all.** This repository's former
`docker-compose.yml` bind-mounted `./target/copper-kingdom-0.1.1 .jar` — with a **space before
`.jar`**. Docker silently creates an empty *directory* at a missing bind-mount source, so the
plugin was never delivered and the stack still came up looking healthy. Per
`minecraft-plugin-docs/CURRENT_STATE.md` that typo dates to **2025-07-18** and "survived roughly
twelve months". Any local "it works" impression formed from that compose file between then and the
2026-07-20 rig migration was worthless. The compose file has since been deleted in favour of
`scripts/test-stack.sh`, which mounts the newest `target/*.jar` and **asserts** the plugin is
enabled.

### 7b — ten-plugin ecosystem matrix — PASSED, but recorded elsewhere and not re-run here

Not run by this backfill. `minecraft-plugin-docs/CURRENT_STATE.md` records an
**Ecosystem Matrix Run (2026-07-19) — PASSED 11/11** on a fresh-volume Legendary stack, installing
every plugin through the one-shot updater from published release assets. Its row for this plugin:

| Plugin | Installed | Enabled in log | Result |
|---|---|---|---|
| Copper Kingdom | 0.2.1 | CopperKingdom | PASS |

That run reported `Done (18.076s)`, zero SEVERE or exception lines, and that each installed JAR's
SHA-256 matched its published `SHA256SUMS.txt` digest. It is cited, not reproduced; the primary
record is `CURRENT_STATE.md`. That document also states the matrix involved **no client join**, so
it too proves coexistence, not correct in-game behavior.

## 8. CI/CD — PARTIAL (observed)

- [x] Identical standard plugin Actions workflow is installed. **Observed:**
      `.github/workflows/build.yml` is **byte-identical** to the workflow in `death-depot`,
      `ollama`, `umami`, and `curse` — md5 `df37a4e433a45b4cc999e14bb5997184` on all five, checked
      2026-07-21. It triggers on `push` to `main`, `push` of `v*` tags, `pull_request` to `main`,
      and `workflow_dispatch`; builds with `temurin` Java `25`; runs
      `mvn --batch-mode --no-transfer-progress clean verify`; writes bare-filename `SHA256SUMS.txt`;
      and uploads release assets only for `refs/tags/v`.
- [ ] Successful main Actions run is recorded before tagging. **NOT RECORDED per release in this
      repository.** `minecraft-plugin-docs/CURRENT_STATE.md` states that "The tag and `main`-branch
      GitHub Actions runs observed on `2026-07-19` were successful for all ten repositories", which
      covers this repo at `v0.2.1` — but that is an ecosystem-wide observation of *outcome*, not a
      record that a green `main` run *preceded* each tag. The ordering was never recorded here.
- [x] Workflow permissions contain no broader access than the documented contract. **Observed:**
      the workflow declares exactly `permissions: contents: write` at the top level, with no
      job-level escalation, and the only token used is `GH_TOKEN: ${{ github.token }}` for
      `gh release`. This is the same block as every sibling repo (identical file).

## 9. Release — `v0.2.1` published; asset verification NOT RE-DONE here

- [x] Semantic version matches the POM, plugin metadata, and `v<version>` tag. **Observed:**
      `pom.xml` `<version>0.2.1</version>`; the newest tag in this clone is `v0.2.1`;
      `plugin.yml` uses `version: '${project.version}'`, so it cannot drift from the POM.
      Tags present: `v0.1.1`, `v0.2.0`, `v0.2.1`.
- [x] Successful tag Actions run and GitHub release are recorded. **Cited, not re-verified.**
      `CURRENT_STATE.md` lists Copper Kingdom at release `v0.2.1` and records successful tag and
      `main` runs observed on 2026-07-19. GitHub was not queried for this backfill.
- [ ] Release contains exactly one updater-matching JAR plus `SHA256SUMS.txt` and no `original-*`
      JAR. **NOT VERIFIED here.** The published assets were not downloaded or listed. Indirect
      support only: the workflow's `! -name 'original-*'` filters, and the matrix run's report that
      each installed JAR's SHA-256 matched its published digest.
- [ ] Downloaded release assets pass `sha256sum --check SHA256SUMS.txt`. **NOT RUN here.** See the
      matrix-run checksum note above for the nearest existing evidence.

## 10. Updater — enrolled (observed); behaviors NOT RUN

- [x] Updater manifest covers repository, destination, anchored asset regex, legacy globs, enabled
      state, and optional pin. **Observed** in `minecraft-plugin-updater/plugins.json`:

      ```json
      {"name": "Copper Kingdom", "repo": "carmelosantana/minecraft-copper-kingdom", "destination": "copper-kingdom.jar", "asset_regex": "^copper-kingdom-[0-9].*\\.jar$", "legacy_globs": ["copper-kingdom-[0-9]*.jar"]}
      ```

      The regex is anchored at both ends. `enabled` is **absent, which means true**. There is **no
      version pin**. No manifest change is proposed by this backfill.
- [ ] Fresh install, upgrade, no-op, legacy archival, endpoint failure, and checksum failure
      behaviors pass. **NOT RUN for this plugin.** The 2026-07-19 matrix exercised *fresh install*
      of this entry as a side effect; the other five behaviors were never tested per-plugin.
- [ ] Updater dry-run uses a disposable directory and never a production plugin directory.
      **NOT RUN.**
- [ ] Failure retains the installed JAR and default fail-open behavior permits Minecraft startup.
      **NOT RUN for this plugin.** Fail-open is a documented property of the updater
      (`CURRENT_STATE.md`), not something tested against this entry.

## 11. Deployment — NOT RECORDED

- [ ] Dokploy redeployment notes identify the full recreation used to rerun the one-shot updater.
      **NOT RECORDED.**
- [ ] Updater completion, Minecraft startup, destination JAR, and stack/plugin logs were inspected.
      **NOT RECORDED.**
- [ ] No production plugin hot reload was used. **UNKNOWN** — no deployment record exists for this
      plugin at any version.

No deployment was performed by this backfill, and this workstation has no Dokploy access, so no
production log could be inspected even in principle. Whether `v0.2.1` is live on `play.xpfarm.org`
is **not established by anything in this repository**.

**Rollback:** untested. The prior tags are `v0.2.0` and `v0.1.1`; reverting the updater to a pin on
either is the mechanical path, but no rollback has ever been rehearsed for this plugin.

## 12. Handoff — PARTIAL

- [ ] Current-state documentation refreshed with release, CI, updater, deployment, and local
      pending state. **NOT DONE by this backfill** — `minecraft-plugin-docs/CURRENT_STATE.md` was
      deliberately left untouched. It already flags this repo as one of four carrying no gate 7a
      checklist record; that flag is now stale in this repo's favour and should be updated by
      whoever owns that document.
- [x] Known limitations, skipped checks, migration notes, rollback guidance, and follow-up owner
      are recorded. This file is that record. Owner: Carmelo Santana.
- [x] Evidence distinguishes source commit, published tag/release, updater state, and deployed
      state without exposing secrets. Source: `test/docker-rig-consolidation`, **local and
      unpushed**. Published: `v0.2.1`. Updater: enrolled, unpinned, enabled. Deployed: **unknown**.

**Follow-ups, in priority order:**

1. Exercise the plugin's actual behavior on a stack — `/copperkingdom` subcommands, permissions,
   config reload. Gate 7a currently proves only that `onEnable()` does not throw.
2. Add unit tests; the repository has none.
3. Perform the Geyser/Floodgate/ViaVersion Bedrock-safety review (gate 4), which has never been
   done.
4. Inspect the published `v0.2.1` release assets directly (gate 9) and run a secrets scan (gate 3).
5. Establish and record the deployed state (gate 11).
