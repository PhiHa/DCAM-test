# Open decisions, risks, and missing documents

## Highest-priority missing documents

| Priority | Document | Why it blocks or reduces rework |
|---|---|---|
| P1 | Expand initial Functional Requirements | Add workflows, exceptions, and acceptance criteria, especially for capture and BDMA integration |
| P1 | Jira backlog from Requirements + MVP + Data Contract | Turns approved direction into traceable epics/stories/tasks and tests |
| P2 | Non-functional Requirements completion/review | Sets measurable stability, performance, battery, storage, GPS, offline, and security constraints |
| P2 | Storage Design correction/completion | Defines root/folders, filenames, temporary/final handling, DB/files, and ADB visibility |
| P2 | Metadata Design | Defines fields, validation, state model, schema versioning, and BDMA mapping |
| P2 | Security & Encryption Design correction/completion | Defines Phase 2 basic and Phase 3 advanced encryption and BDMA compatibility |
| P3 | Streaming, PTT, GPS-route designs | Required before Phase 3 implementation |
| P3 | Release Plan | Defines build/version/release/pilot process |
| P3 | ADRs | Records stable choices and their trade-offs |

The DCAM-BDMA Data Contract is no longer missing: version 1.6 is Approved. The following are the remaining design/requirement gaps around that baseline and the July 8 draft Technical Design set.

## Data and BDMA decisions

- Exact Android storage root exposed through ADB.
- Exact mapping from logical roots to physical paths on each supported BodyCamera/storage API.
- Complete embedded metadata fields, encoding, validation, and media-format support; standalone media JSON is excluded by contract 1.6.
- Exact `dcam.db` schema and schema-version negotiation.
- Stable local implementation of `dcam_cloud_device_id`, `android_id_hash`, app/contract metadata, and user/operator mapping.
- Source-state vocabulary; `recording`, `pending`, `completed`, `corrupted`, and `recovered` are only proposed directions.
- Schema evolution and backward-compatibility rules.
- MP4 MD5 enablement/default, generation timing, file content format, and performance; its import and cleanup outcomes are already contracted.
- Duplicate import, partial import, retry/resume, and interruption rules beyond the contracted result categories.
- Where import/sync state lives in `dcam.db` and how DCAM/BDMA coordinate concurrent access.
- Retention policy beyond the contracted post-success cleanup permissions.
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
- Device-policy mechanism for optional Home/Launcher role and boot startup.
- Managed-device provisioning plus Lock Task/Kiosk/exit-control policy.
- Screen on/off, dim/keep-awake, WakeLock, battery-optimization, and background-limit policy.
- Restart behavior after crash, service kill, reboot, abnormal shutdown, and device reconnect.

## Cloud, config, and update decisions

- Which cloud capabilities are actually required and which provider(s) implement them.
- Firebase SDK features that work on target devices and their GMS dependency.
- REST/API fallback and BDMA-desktop cloud responsibilities.
- Remote-config schema, keys, validation, and safe rollout behavior.
- Runtime override controls and config precedence. Device information may use internal `dcam_config.cson`; identity/provisioning mirror, remote-config cache, and operational settings belong in `dcam.db`.
- Self-update server/mechanism, signature validation, rollback, forced/silent update policy.
- Mandatory performance metrics and their local/cloud format.

## Security decisions

- AES-256 implementation details and exact media/metadata scope; `_enc` and `_IMP_enc` naming are already decided.
- Key creation, secure storage, distribution, rotation, recovery, and BDMA decryption.
- API/provider authentication and authorization.
- Update signature verification.
- Sensitive metadata classification, log redaction, export, retention, and access control.
- Integrity behavior outside the contracted optional MP4 MD5 flow.

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
