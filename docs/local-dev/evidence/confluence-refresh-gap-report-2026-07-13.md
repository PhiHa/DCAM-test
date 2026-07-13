# Confluence refresh gap report - 2026-07-13

**Source refresh:** `node download-confluence.js` refreshed `docs/dcam-knowledge/confluence-original` on 2026-07-13. Confluence remains source of truth; this file records local repo gaps only.

## Refreshed Confluence baseline

- Build 0.1 target is a Working Recording Slice: local video/image capture, staging/finalization, minimum DB/CSON/log fixtures, BDMA import through ADB, and real BodyCamera evidence.
- Build 0.1 must stay offline-first. Cloud provisioning, Remote Config, Self Update, fleet/web portal, AI, streaming, PTT, JT808, full auth expansion, and production encryption/key management are deferred.
- Capture path must use one serialized authority, keep critical work off MainThread, and avoid blocking `BDMA_READY` on checksum/cloud/provider work.
- Storage contract must keep active/partial media in Temp/staging and expose only finalized media/config/database/log artifacts to BDMA.
- Current Gradle baseline is `compileSdk 36`, `minSdk 26`, `targetSdk 36`, Java 17, CameraX `1.6.1`, Room `2.8.4`.

## Local code gaps

| Gap | Evidence | Impact | Next action |
|---|---|---|---|
| Real BodyCamera POC absent | No recorded hardware report under `docs/local-dev/evidence` | CameraX, FGS, storage, ADB, screen-off, kill/reboot assumptions unproved | Run `docs/local-dev/sprint-0-poc-test-plan.md`; record model, Android version, firmware, logs |
| Build 0.1 environment matrix not frozen | `app/build.gradle` uses API 36/26/36 and Java 17, but selected device OS/SDK floor is unconfirmed | Release target can drift from supported BodyCamera | Add ADR for Android version, min/target SDK, Java, Gradle, device firmware |
| Startup cloud behavior needs release-path verification | Sprint 0 notes identify `AppComposition` startup cloud init; refreshed Confluence keeps Build 0.1 offline-first | Disabled cloud can affect startup/readiness | Remove or hard-gate cloud/remote-config startup before acceptance |
| Encryption policy not forced OFF | Refreshed baseline defers production encryption/key management | Legacy config can break BDMA import | Add deterministic Build 0.1 encryption-off gate and test |
| Contract fixture incomplete | BDMA-ready `dcam.db`, `dcam_config.cson`, `Logs/logs.txt`, and final media are not proven together | BDMA import cannot be accepted from app build alone | Freeze smallest fixture with BDMA; automate sample export check |
| Staging/finalization behavior unproved | Storage/Recording design requires Temp/staging isolation and finalized-only output | Partial media could be imported | Add staging-to-final real-device test |
| Performance/stability gates not measured | Refreshed NFR budgets cover startup, finalization, memory, I/O, long recording | Unit tests alone cannot prove MVP readiness | Record measured POC results |
| Device Owner/kiosk is not Build 0.1 blocker | Refreshed docs defer full policy unless POC requires it | Scope creep risk | Keep POC-only evidence; defer full policy work |

## Plan changes

1. Finish Sprint 0 with BodyCamera evidence first.
2. Freeze Build 0.1 environment matrix after POC.
3. Cut release path to offline-first; force encryption off.
4. Prove one end-to-end fixture: 30-second video, image, DB/CSON/log output, BDMA ADB import.
5. Expand backlog only after working slice passes.
