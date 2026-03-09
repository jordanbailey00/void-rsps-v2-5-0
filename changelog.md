# changelog.md

## 2026-03-09

- Added the Jad telegraph mini-rework source-of-truth document in:
  - `docs/jad_telegraph_plan.md`
- Recorded the resolved parity assumptions for Jad telegraph implementation:
  - canonical onset tick is the current headed animation start tick
  - dialog/audio is not the timing anchor
  - prayer-check timing must remain unchanged
- Mirrored the first shared-code Jad telegraph changes from `fight-caves-RL` into the headed RSPS code path:
  - added `game/src/main/kotlin/content/area/karamja/tzhaar_city/JadTelegraph.kt`
  - updated `game/src/main/kotlin/content/entity/npc/combat/Attack.kt`
  - updated `game/src/main/kotlin/content/area/karamja/tzhaar_city/TzTokJad.kt`
- This step formalizes the authoritative Jad telegraph state in the headed code path without exposing any oracle field or changing combat timing.

## 2026-03-08

- Replaced the root RSPS placeholder docs with minimum viable source-of-truth documentation:
  - `RSPSspec.md`
  - `RSPSplan.md`
- Fixed the module role explicitly as:
  - headed/oracle reference
  - manual validation/debug path
  - parity dispute reference only
- Documented that `RSPS` remains outside the RL training hot path and is not the owner of headless or RL wrapper semantics.
- Recorded the current release-workflow stance as intentionally inherited for this repo during the current docs-alignment pass.

## 2026-03-07

- Added canonical RSPS root planning/spec placeholders:
  - `RSPSspec.md`
  - `RSPSplan.md`
- Added this root changelog file so RSPS decisions and completed work can be tracked with the same convention used by the other modules.
- No RSPS runtime or gameplay code changes were made in this step.
