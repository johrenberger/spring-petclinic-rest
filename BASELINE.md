# Baseline

This is the **re-baseline** of the upstream
[spring-petclinic/spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest)
project, forked into
[johrenberger/spring-petclinic-rest](https://github.com/johrenberger/spring-petclinic-rest)
on 2026-06-13.

## What is "re-baselined"?

The fork is **operationally independent** from the upstream project. The
following changes were made on the re-baseline commit:

| Change | What | Why |
|---|---|---|
| `pom.xml` description | Updated from "REST version of the Spring Petclinic sample application" → "REST version of the Spring Petclinic sample application, operated independently" | Acknowledges fork status |
| `.github/workflows/*.yml` → `.github/workflows-disabled/*.yml.disabled` | All 4 upstream CI workflows disabled | Prevents CI from running on the original project's behalf (which would burn SonarCloud / Newman quota) |
| `.github/workflows/maven-ci.yml` | New minimal CI workflow that runs `mvn verify` on push/PR | Gives this fork its own working CI |
| `readme.md` | Added "Re-baseline" section at the top | Documents that this is an independently-operated fork |
| License | Retained as `LICENSE.txt` from upstream | Apache 2.0; attribution preserved |

## What is preserved

- All upstream source code (`src/**`)
- All upstream tests (in `src/test/**`)
- All upstream documentation (`readme.md`, `terms.txt`)
- Apache 2.0 license (file unchanged)
- SonarQube / Newman / Docker config files (moved to `workflows-disabled/`)

## Why a re-baseline?

This fork is being used as the **Tier 3 exercise target** for the
`backend-implementation` skill promotion in
[johrenberger/aiWorkflows](https://github.com/johrenberger/aiWorkflows).
Re-baselining it ensures:

1. **No accidental coupling** — the fork's CI won't suddenly start
   reporting to the upstream project's SonarCloud org or
   deploying images under the upstream's Docker Hub account.
2. **Clean PR surface** — PRs into this fork are reviewed
   independently; no need to "tell" the upstream project.
3. **Clear ownership** — the README and CI artifacts clearly
   say "this is operated independently."

## Versioning policy

- The fork will keep the upstream version number (`4.0.2`) until
  any breaking change is made.
- Once any change is made, the version bumps to `4.1.0-fork.1`,
  `4.1.0-fork.2`, etc.
- Periodically (manually), we can `git fetch` from the upstream
  to pull in fixes, but no automatic syncing is configured.

## See also

- `upstream-tracking.md` (if/when present) — list of upstream
  commits that have been merged in
- `petclinic-ermodel.png` — entity-relationship diagram (unchanged)
- `docker-compose.yml` — local dev setup (unchanged, but
  references the upstream Docker image)
