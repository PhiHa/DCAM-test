# Open decisions, risks, and missing documents

## Highest-priority missing documents

| Priority | Document | Why it blocks or reduces rework |
|---|---|---|
| P1 | DCAM–BDMA Data Contract | Locks exposure, schema, statuses, versions, and ingest behavior for two products |
| P1 | Functional Requirements | Turns product direction into Jira epics/stories/tasks and testable behavior |
| P2 | Non-functional Requirements | Sets measurable stability, performance, battery, storage, GPS, offline, and security constraints |
| P2 | Storage Design | Defines root/folders, filenames, temporary/final handling, DB/files, and ADB visibility |
| P2 | Metadata Design | Defines fields, validation, state model, schema versioning, and BDMA mapping |
| P2 | Security & Encryption Design | Defines Phase 2 basic and Phase 3 advanced encryption and BDMA compatibility |
| P3 | Streaming, PTT, GPS-route designs | Required before Phase 3 implementation |
| P3 | Release Plan | Defines build/version/release/pilot process |
| P3 | ADRs | Records stable choices and their trade-offs |

## Data and BDMA decisions

- Exact Android storage root exposed through ADB.
- Final folder structure and deterministic filename convention.
- Whether BDMA reads the local `.db`, exported metadata files, or both.
- Metadata file format and complete required/optional field schema.
- Stable device ID source and user/operator mapping.
- Source-state vocabulary; `recording`, `pending`, `completed`, `corrupted`, and `recovered` are only proposed directions.
- Schema evolution and backward-compatibility rules.
- Checksum/hash generation and validation.
- Discovery, validation, duplicate import, partial import, retry/resume, and error rules.
- Whether BDMA may write a sync/import marker.
- Post-import retention, cleanup, and deletion policy.
- Import-error UX and ownership across DCAM and BDMA.

## Android and application decisions

- Final supported device matrix and real pilot hardware.
- Final `minSdk`, `targetSdk`, and `compileSdk` policy in documentation.
- CameraX versus Camera2/vendor SDK after real-device POC.
- Preview and background-recording requirements.
- Behavior when microphone is unavailable.
- Final package/module naming.
- MVVM confirmation at architecture/ADR level (the Development Standard already treats it as the project standard).
- Dependency injection approach; currently not mandatory.
- State and domain error model.
- Room versus direct SQLite/data-file strategy; documentation remains undecided.
- Gson versus Jackson.
- Service interface signatures and adapter selection per hardware model.
- Foreground Service lifecycle, process death, restart, and recording recovery.

## Cloud, config, and update decisions

- Which cloud capabilities are actually required and which provider(s) implement them.
- Firebase SDK features that work on target devices and their GMS dependency.
- REST/API fallback and BDMA-desktop cloud responsibilities.
- Remote-config schema, keys, validation, and safe rollout behavior.
- Local config format/ownership and runtime override controls.
- Self-update server/mechanism, signature validation, rollback, forced/silent update policy.
- Mandatory performance metrics and their local/cloud format.

## Security decisions

- Encryption algorithms and exact media/metadata scope.
- Key creation, secure storage, distribution, rotation, recovery, and BDMA decryption.
- API/provider authentication and authorization.
- Update signature verification.
- Sensitive metadata classification, log redaction, export, retention, and access control.
- Integrity/checksum policy.

## Advanced-feature decisions

- Streaming protocol, service/provider, session lifecycle, timeout, reconnect, and weak-network behavior.
- PTT protocol/SDK, audio format, trigger/state model, and error recovery.
- GPS route sampling, accuracy, session model, storage, and downstream display.
- How future cloud sync or remote control modifies the currently local/ADB ownership boundary.

## Principal delivery risks

| Risk | Mitigation direction |
|---|---|
| Java/Desktop team's Android learning curve | Two-week onboarding, sample app, device exercises, review |
| Camera/firmware incompatibility | POC and sustained tests on every target BodyCamera |
| File corruption on crash/power loss | Temporary/final state, explicit recovery, integrity checks |
| Data Contract changes | Define and version it early with both DCAM and BDMA teams |
| GPS instability | Optional validity state and graceful fallback |
| Storage/battery constraints | Real-device profiling, thresholds, warnings, and bounded logging |
| Encryption degrading recording | Benchmark on target hardware before committing design |
| Streaming/PTT network quality | Weak-network, timeout, reconnect, and fallback tests |
| Scope creep | Jira/change control and PM review; do not spend buffer on new scope |

## Decision discipline

- Mark unknowns as TBD; do not let prototype choices silently become contracts.
- Validate hardware-sensitive choices with a POC on real BodyCamera devices.
- Record major decisions as ADRs.
- Update the Data Contract, both boundary/data architecture pages, BDMA docs, tests, and this digest together when ownership or schema changes.
