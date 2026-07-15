# Confluence refresh gap report - 2026-07-15

**Source refresh:** `node download-confluence.js` refreshed `docs/dcam-knowledge/confluence-original` on 2026-07-15. Downloader fetched 134 pages, saved 131 pages, and excluded three credential-bearing pages. Confluence remains source of truth; this file records local repo implications only.

## Changed authoritative baseline

- Build 0.1 remains Working Recording Slice and offline-first vertical slice.
- Release & Build Applicability Matrix owns build-specific applicability; generic requirement/design text cannot widen Build 0.1 scope.
- Decision Brief owns DEC-01-DEC-07: NCC-036V / Android 12 / API 31 / firmware `877AOOAKN1_RK2_V009`, internal storage only, MP4 MD5 before `BDMA_READY`, fixed `B01OPR` placeholder operator, basic battery/storage/GPS status, one reference-device evidence set, and approved filename-token mapping.
- Important Media remains Conditional - Not Activated unless enabled through approved change control.
- Requirement-Design-Test Traceability Matrix supports backlog readiness, not release acceptance while Jira, PR, QA, and evidence references remain incomplete.
- Document Status Registry reports `62/62` named retrievable DCAM pages covered as of 2026-07-14. Two ADR pages exist; one dangling Confluence entry needs admin cleanup.

## Local documentation corrections

| Stale claim | Correct baseline |
|---|---|
| MP4 checksum is optional or runs after `BDMA_READY` | Every Build 0.1 MP4 requires MD5; finalization occurs first and `BDMA_READY` follows checksum success |
| Checksum failure leaves media import-ready | Preserve MP4, record Checksum Pending/Failed, and block BDMA import for that item |
| Checksum must not block any readiness | It must not block new recording, but it blocks affected item's `BDMA_READY` |
| July 9/13 digest is current | Current mirror refresh is 2026-07-15; governance/status cut-off is 2026-07-14 |

## Remaining repo gaps

| Gap | Impact | Next action |
|---|---|---|
| Reference-device evidence does not map business and Android model names | Reviewers may incorrectly treat `NCC-036V` and ADB model `BWC` as different devices | Record business model `NCC-036V`, ADB `ro.product.model = BWC`, Android 12/API 31, firmware, device ID, and physical-device logs |
| DEC-07 requires `DEVICE_TOKEN` to be a validated `serial_number` snapshot of `6-10` `[A-Z0-9]` characters, while Device POC observed `KF5OF2126040802193` (`17` characters); truncation, underscore padding, alias/hash, and replacement are prohibited | Full serial violates length; any transformed value violates snapshot/no-replacement guardrails. This is a requirement contradiction, not a source bug | Device POC must reconfirm the physical serial. Mark filename qualification blocked pending authoritative rule correction; do not invent or silently truncate a token |
| MP4 MD5 readiness path lacks accepted end-to-end evidence | BDMA import gate remains unproved | Verify final MP4, digest, state transition, mismatch failure, and ADB import |
| Internal-storage physical mapping still depends on device/design review | Logical contract may diverge from device path | Freeze mapping after device POC and update design/ADR evidence |
| Traceability links remain incomplete | Backlog readiness does not equal release acceptance | Add Jira, PR, QA Test ID, and evidence links for applicable Build 0.1 rows |
| Encryption-off and deferred-provider isolation need release-path proof | Optional platform behavior can disturb WRS | Verify deterministic encryption OFF and no cloud/provider startup dependency |

## Planning rule

Use this order: applicability matrix, Decision Brief, requirement/design page, traceability row, local implementation evidence. When sources conflict, Build 0.1-specific authority wins; record unresolved implementation choices instead of inventing them.
