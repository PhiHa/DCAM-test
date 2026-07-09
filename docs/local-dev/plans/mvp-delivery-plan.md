# DCAM MVP delivery plan

Status: active implementation plan  
Prepared from repository state: 2026-07-09  
Last implementation review: 2026-07-09  
Primary planning source: `DCAM_Huong_dan_dev_MVP_codebase.pdf`  
Contract baseline: Approved DCAM–BDMA Data Contract 1.6
Delivery baseline: Approved DCAM Architecture Delivery Profile 1.1

Current checkpoint: the repository uses the approved smaller `:app`/`:core` Gradle shape;
password-first login, boot-scoped operator sessions and initial Room auth tables exist; credentials
are stored as salted PBKDF2-HMAC-SHA256 hashes; 55 local tests and `assembleDebug` pass. This is a
partial Sprint 1 implementation, not an MVP acceptance claim.

## 1. Objective

Turn the current capture prototype into an MVP that can safely demonstrate this complete path on
a target BodyCamera:

```text
boot/start
  -> initialize logging, database, configuration and device identity
  -> recover interrupted work
  -> require an operator login
  -> accept one recording command through one recording authority
  -> run recording under a foreground-service lifetime
  -> write to Temp/staging
  -> stop and finalize
  -> publish to the final Media folder
  -> persist COMPLETED / BDMA_READY
  -> allow BDMA to discover and import the finalized artifact
```

The MVP is not complete when the app merely creates a playable MP4. It is complete when the app can
explain who recorded it, whether finalization completed, where its durable state is stored, and what
happens after a crash, service kill, reboot, storage failure, or BDMA read.

## 2. Planning rules

1. Reliability and preservation of evidence-like artifacts outrank secondary features and UI polish.
2. Logging, database, configuration, storage, crypto utilities and app bootstrap are always-on
   infrastructure. Product-feature toggles must not disable them.
3. A normal new feature works without a developer gate. Runtime disablement is added only when
   explicitly requested.
4. Disabled or later-phase surfaces must not execute hidden background behavior from the critical
   startup path.
5. Do not create empty future interfaces. Add a boundary only together with a current use case and a
   real or test implementation.
6. Hardware-sensitive choices require a real BodyCamera POC and an ADR. Emulator success is not
   acceptance evidence.
7. Existing class names are not proof that a PDF capability is implemented. Acceptance is based on
   behavior and failure handling.

## 3. Current-state assessment

### 3.1 Capability matrix

| PDF MVP capability | Current repository evidence | Assessment | MVP action |
|---|---|---|---|
| Project/layer skeleton | Feature-first packages, pure-Java `:core`, Android `:app`, ports/use cases, platform adapters, composition root and cross-module architecture tests | Present for current scope | Preserve the two-module shape; split another module only when an approved trigger exists |
| Runtime orchestrator | `AppComposition` constructs adapters and starts cloud-state work; no persisted runtime mode, ordered recovery, eligibility or safe mode | Missing behavior | Implement startup orchestration outside `Activity` |
| Foreground service host | `RecordingForegroundService` shows a notification, is `START_NOT_STICKY`, and does not own CameraX/recording/finalization | Prototype only | Move durable recording lifetime/authority behind service host after POC |
| Operator login/session | Password-first login UI/use cases, Room user/auth/session tables, default/developer user provisioning, boot-scoped restore/invalidation and hardware-input guard exist | Partial | Enforce the session in one recording authority, add instrumentation/rate-limit policy, and validate target-device behavior |
| Recording authority | `VideoRecordingUseCaseImpl` delegates directly to `CameraGateway`; CameraX adapter owns mutable active recording | Missing authority | Introduce one application recording coordinator/state machine |
| Camera service/adapter | CameraX photo/video works behind `CameraGateway`; target-device and screen-off behavior unverified | Prototype | Keep adapter; complete real-device POC and error mapping |
| Temp/final/BDMA_READY | `Temp` folder is created, but camera/audio currently target final media paths directly; no durable finalization state | Missing | Implement staging, finalization and explicit readiness |
| SQLite/Room v1 | Room v1 now covers Loggly, identity, remote config, operational settings, users, hashed credentials and operator sessions | Partial foundation | Add runtime, media-session, lease, service and finalization tables from the reviewed MVP subset |
| Media session repository | No media-session/finalization repository or operator snapshot | Missing | Add transactional Room repository |
| Local-first logging | Local logs, rotation, Loggly outbox and context exist | Partial | Add stable reason codes, redaction tests and recording/runtime correlation IDs |
| Recovery manager | No DB/file reconciliation or safe mode | Missing | Implement startup and boot recovery |
| BDMA-readable contract | Media folders and filenames are partly aligned; no physical-path proof, lifecycle DB, embedded compatibility metadata, MD5 workflow or E2E fixture | Partial | Complete the minimum contract path and prove it with BDMA |
| Boot/kiosk | Boot receiver, Home intent and Device Owner/Lock Task scaffolding exist | Partial | Validate policy on target hardware; do not let kiosk work block recording core |
| Device awareness | Battery, free app storage and GPS capability/status are available | Partial but useful | Keep status; add recording precondition thresholds after ADR |

### 3.2 Important current hazards

- Camera ownership is bound to `MainActivity`; the notification service does not preserve or recover
  recording after Activity/process loss.
- Media is allocated in final `Media/*` locations, so BDMA can observe a file before DCAM has proven
  that it is finalized.
- There is no persisted single-active-recording invariant. Process restart can lose in-memory
  authority.
- Login-screen navigation and hardware input are session-gated, but capture use cases still lack one
  application recording authority; no durable operator snapshot is attached to a media session.
- Passwords are no longer persisted as plaintext. The remaining auth risks are the default six-digit
  development credential, missing failed-attempt throttling/lockout policy, and unmeasured PBKDF2
  latency on target BodyCamera hardware.
- Encryption can be configured, but key management, BDMA decryption and performance approval are
  unresolved. Hiding the Security screen alone is not sufficient; MVP policy must explicitly force
  encryption off unless ADR-006 approves it.
- `AppComposition` performs cloud/no-op remote-config initialization during startup even though
  advanced remote configuration is not required for the local-recording MVP.
- Demo settings controls resemble completed features but do not persist or apply approved operational
  settings.

### 3.3 Implementation progress checkpoint

| Increment | Status | Evidence / remaining boundary |
|---|---|---|
| Approved small Gradle shape | Complete | `:app` depends one-way on independently compiled/tested `:core` |
| Password-first login UI and use cases | Implemented foundation | Login, ambiguous-password handling, provisioning and logout have JVM tests |
| Boot-scoped operator session | Implemented foundation | Room session row plus boot-ID restore/invalidation; Android/Room integration still needs instrumentation |
| Password protection at rest | Implemented | Unique salt and PBKDF2-HMAC-SHA256 with 600,000 iterations; no plaintext credential column |
| Capture session enforcement | Partial | Navigation/hardware guards exist; application recording authority and durable operator snapshot do not |
| Full MVP DDL/runtime state | Not complete | Auth tables exist; runtime/media/lease/finalization/recovery tables remain |
| Service-owned recording | Not started | Notification service still does not own CameraX or finalization |
| Staging/finalization/BDMA readiness | Not started | Capture still targets final media paths and has no `BDMA_READY` transaction |
| Recovery and BDMA E2E | Not started | No DB/filesystem reconciliation or joint import fixture |

### 3.4 Immediate execution order

1. Close the remaining Sprint 0 gates: target BodyCamera Camera/FGS/storage POC, ADR decisions,
   deterministic MVP encryption policy, and removal of no-op cloud refresh from critical startup.
2. Finish the Sprint 1 authority boundary: every touch/hardware start command must enter one
   authenticated recording coordinator and return stable rejection reason codes.
3. Implement the reviewed MVP subset of runtime/media/lease/finalization tables and Room tests.
4. Move recording lifetime to the foreground host selected by the POC.
5. Implement Temp/staging, verified publication, `BDMA_READY`, recovery and the shared BDMA fixture.

## 4. MVP product surface

### 4.1 Keep visible or implement now

| Surface/capability | MVP treatment |
|---|---|
| Operator login | Add and show before normal capture |
| Camera operation screen | Keep; show operator, recording state, storage state and controlled error |
| Video start/stop | Keep and route through the recording authority |
| Image capture | Keep and apply operator/storage/finalization rules |
| Emergency/SOS recording | Keep only after emergency identity and policy are implemented |
| Files/media browser | Keep read-only and limited to finalized `Media` folders |
| About/diagnostics | Keep non-sensitive version/device/diagnostic information |
| Battery/storage/capability status | Keep available; optional capability failure must not crash capture |
| Local logging and Loggly when configured | Always-on infrastructure; must not depend on a product gate |
| Hidden developer controls | Debug/internal builds only; not evidence of operational settings |

### 4.2 Hide until a real implementation is scheduled

Default the following product surfaces off for the MVP build. Hiding a surface must not disable
shared platform infrastructure.

| Surface | Reason/condition to re-enable |
|---|---|
| Standalone audio recording | Not in the Phase 1 video/image minimum; re-enable with approved use case and artifact lifecycle |
| Recording settings | Current controls are demo state; re-enable after DB-backed requested/applied settings exist |
| Camera settings | Re-enable after Camera POC exposes supported values and writes are validated |
| Audio settings | Re-enable with an approved audio requirement and persisted applied state |
| Storage settings | Current Internal/External/Auto mapping and fallback are incomplete; retain read-only storage status |
| GPS settings | Keep capability detection; re-enable settings with location permission, sampling and persistence design |
| Device settings | Split language/identity from unsupported demo controls before re-enabling |
| Security settings | Re-enable only after encryption/key/BDMA ADR and security review |
| Cloud/network settings | Advanced cloud/config is not required for offline MVP; logging/network infrastructure stays operational |
| Transfer | FTP/RTSP transfer is not the current ADB-based BDMA boundary |
| Video streaming/PTT/JT808 | Later phase; no MVP runtime initialization |
| Self Update UI/automatic install | Manual controlled update for initial MVP until validation/install/rollback ADR is approved |
| Face, QR and NFC authentication | Password-first MVP; add only after credential/revocation/capability design |
| Realtime AI | No recording-core dependency; add later behind a proven capability boundary |

### 4.3 Code behavior for hidden capabilities

- Do not merely hide a tile while starting its worker/provider at boot.
- Remove advanced remote-config refresh from the critical startup chain. Device identity and required
  operational state belong to runtime/data ownership, not to a cloud feature.
- Keep future package README files as documentation only; do not add placeholder interfaces.
- Retain existing feature code only when it is tested, harmless, and not on the MVP critical path.
- Release configuration must explicitly list enabled product surfaces; it must not inherit a
  developer's local toggle history.

### 4.4 Scope decisions that must not remain implicit

| Tension | Required decision |
|---|---|
| The PDF permits manual update/stub in the first MVP; Approved Android Device Operation makes DCAM Self Update the primary no-EMM path | Approve a time-bounded manual-update exception for the internal MVP or bring validated Self Update into scope |
| The PDF recommends encryption off without a security ADR; current source can encrypt media | ADR-006 must force a deterministic build/runtime policy and prevent accidental encryption from legacy config |
| Data Contract 1.6 is approved, but exact embedded metadata and physical roots remain TBD | Define the smallest contract-compatible MVP fixture with BDMA; do not invent standalone media JSON |
| Dedicated kiosk operation is approved direction, but exact Device Owner/Home/status-bar behavior remains device-dependent | Record the MVP deployment profile and maintenance fallback after real-device POC |
| Standalone audio exists in source but is absent from the first video/image MVP baseline | Hide it unless Product explicitly adds it with the same session/finalization/recovery guarantees |

## 5. Target MVP boundaries

The names below describe responsibilities. Final names may follow an ADR, but ownership must remain.

```text
UI / hardware input
  -> authentication/session use case
  -> recording command use case (single authority)
      -> operator-session repository
      -> storage precondition
      -> media-session repository
      -> foreground recording host
          -> camera recording engine
          -> staging/finalization storage
  -> persisted capture/runtime state
  -> recovery use case on startup/boot
```

### 5.1 Application-owned boundaries

- Runtime initialization/recovery use case.
- Authenticate operator, read active session and invalidate session for a new boot.
- Start/stop/emergency recording use case as the sole command authority.
- Camera/recording engine port selected after POC.
- Recording service-host port for Android lifetime management.
- Media-session repository.
- Staging/finalization storage port.
- Storage-health/precondition port.
- Recovery repository/scanner boundary where filesystem/Room access is required.

### 5.2 Platform implementations

- Room repositories for session/runtime/media/finalization state.
- Android foreground-service host.
- CameraX, Camera2 or vendor recording engine selected by ADR-002.
- Internal/external filesystem or MediaStore implementation selected by ADR-005.
- Boot ID/device policy/permission adapters.

No application/domain class may import Android, CameraX, Room, filesystem, WorkManager or a cloud
provider.

## 6. Persisted MVP model

Create the DDL and ownership matrix before implementing repositories. Room is already present, but
ADR-003 must formally confirm Room or choose a replacement before the schema ships.

Detailed proposal: [Database DDL v1 design](database-ddl-v1-design.md) and
[reference SQL](database-ddl-v1.sql).

Development schema note: Room v1 has not shipped, so plaintext credential removal was folded into
the exported v1 schema. Existing development installs created from the older v1 schema must clear
application data or reinstall; there is intentionally no production migration for an unshipped
plaintext schema.

### 6.1 Required in the first usable schema

| Area/table | Minimum fields/behavior |
|---|---|
| Schema/compatibility metadata | DB schema version, app package/version, data/media/encoder contract versions |
| `device_identity` | Existing stable device identifiers plus provisioning/identity state required offline |
| `user_profile` | Stable user ID, display data, enabled/revision state |
| `user_auth_method` | Password hash/protected reference, algorithm/version, no plaintext credential |
| `operator_session` | User ID, boot ID, login/logout time, active/expired state |
| `operational_setting` | Requested values; existing table must be reviewed against contract ownership |
| `applied_setting_state` | Runtime-applied value/result/reason when MVP settings begin to apply |
| `app_runtime_state` | Boot ID, runtime mode, safe/recovery state and last clean shutdown marker |
| `foreground_service_state` | Start reason, active media-session ID, heartbeat/restart information |
| `media_session` | Stable session ID, type, operator snapshot, start/stop time, state, staging/final path |
| `media_finalization_state` | Finalization step, checksum/encryption result, failure reason, BDMA readiness |

### 6.2 Add before BDMA write-back testing

- `media_import_state`
- `external_change_log`
- approved user/auth synchronization revision fields
- feature eligibility state if runtime pruning is used

Diagnostic events may remain in bounded local logs for the first increment unless an approved query
or BDMA requirement needs a table.

### 6.3 Transaction invariants

1. At most one active video/emergency recording row.
2. Start intent and staging identity are persisted before the engine reports active recording.
3. A final path is not `BDMA_READY` until the file is closed, validated and published.
4. Operator identity is snapshotted into the media session and does not change when the user profile
   is later edited.
5. BDMA cannot update active operator/media lifecycle fields.
6. Unknown or ambiguous recovery artifacts are preserved and classified, never silently deleted.
7. Camera ID and the fixed-six-character User ID are validated before creating a contract filename.

## 7. Recording and finalization state model

Use explicit states rather than inferring truth from a file's existence:

```text
REQUESTED
  -> PRECHECKED
  -> STARTING
  -> RECORDING
  -> STOP_REQUESTED
  -> FINALIZING
  -> COMPLETED / BDMA_READY

Any active state
  -> FAILED
  -> RECOVERY_REQUIRED
  -> RECOVERED or CORRUPTED
```

Minimum reason codes include:

- `OPERATOR_AUTH_REQUIRED`
- `RECORDING_ALREADY_ACTIVE`
- `CAMERA_PERMISSION_REQUIRED`
- `MICROPHONE_PERMISSION_REQUIRED`
- `CAMERA_FAILED`
- `FOREGROUND_SERVICE_START_FAILED`
- `STORAGE_UNAVAILABLE`
- `STORAGE_LOW`
- `STORAGE_FULL`
- `STAGING_CREATE_FAILED`
- `FINALIZATION_FAILED`
- `DATABASE_BUSY`
- `DATABASE_CORRUPTED`
- `RECOVERY_REQUIRED`

Reason codes are domain/application values. SDK exception text may be logged safely but must not
become the product state contract.

### 7.1 Finalization order

1. Allocate session ID and staging destination.
2. Persist requested/start state and operator snapshot.
3. Start the foreground host and recording engine.
4. Record only to `Temp`/staging or an equivalent pending MediaStore artifact invisible to BDMA.
5. Stop and await the engine's final callback.
6. Close/flush output and validate non-empty readable media.
7. Apply encryption only if ADR-006 approves it.
8. Generate an MP4 `.md5` only if its MVP policy/content format is approved.
9. Move/rename atomically when supported; otherwise use a verified copy/fsync/publish strategy from
   the storage ADR.
10. Persist final path, completion and `BDMA_READY` in one controlled workflow.
11. Publish/scan the final media only after readiness.

If any step after recording fails, preserve staging and persist `RECOVERY_REQUIRED`.

## 8. Delivery sequence

### Sprint 0 — scope truth, POC and decisions

**Goal:** prevent the team from building deeply on unverified device assumptions.

**Current checkpoint:** incomplete. Documentation/module/auth work progressed, but the target-device
Camera/FGS/storage evidence, ADR approvals, encryption policy and startup-cloud cleanup remain open.

Work:

- Apply the MVP visibility matrix and default later-phase surfaces off.
- Ensure encryption is forced off by MVP policy unless ADR-006 explicitly enables it.
- Remove no-op/advanced cloud initialization from critical startup.
- Run CameraX/Camera2/vendor POC on every target BodyCamera candidate.
- Test recording with screen off/background, FGS start restrictions, process/service kill and
  callback ordering.
- Test Internal/External/Auto candidate roots, ADB visibility, free-space reporting,
  move/rename/copy atomicity and storage removal.
- Confirm Room choice and draft DDL v1/ownership matrix.
- Define the runtime failure matrix and reason-code vocabulary.
- Draft ADR-001 through ADR-008 from the PDF.

Required outputs:

- Reproducible POC report containing device model, firmware, Android version and commands.
- ADR draft/decision list with owners and due dates.
- Reviewed DDL v1 proposal.
- Approved visible/hidden MVP surface list.
- Failure-test matrix checked into `docs/local-dev`.

Exit gate:

- Camera, service and storage approaches are proven on at least one target BodyCamera.
- Unknowns are explicitly recorded; no prototype choice is silently called a contract.

### Sprint 1 — authenticated command authority and durable state

**Goal:** every normal recording request has a valid operator and a durable command/state owner.

**Current checkpoint:** in progress. Login, user provisioning, PBKDF2 credential storage and
boot-scoped sessions exist. The single recording authority, complete DDL/runtime state,
operator-to-media snapshot and Android/Room integration evidence remain open.

Work:

- Implement runtime startup ordering: logging -> DB/config/identity -> boot/session invalidation ->
  recovery check -> capability baseline -> login/operation screen.
- Add password-first offline user/auth/session storage and login UI.
- Generate/persist a boot ID and invalidate prior operator sessions after reboot.
- Implement DDL v1 entities, DAOs, schema export and migration tests.
- Migrate toward internal `Config/dcam_config.cson` as device-information-only; move operational
  settings and runtime/user state into `dcam.db` without silently discarding legacy values.
- Introduce the single recording command authority with prechecks for operator, active session,
  permissions, service availability and storage.
- Route touch and hardware commands through the same authority.
- Implement emergency policy as an explicit command path; do not treat missing login as an implicit
  emergency.

Acceptance:

- Normal video/photo without a session returns `OPERATOR_AUTH_REQUIRED`.
- Successful login enables normal capture and supplies a fixed operator snapshot.
- Reboot invalidates the session.
- Concurrent/double start produces one deterministic rejection and never two recordings.
- Unit tests cover command/state transitions and precondition ordering.

### Sprint 2 — service-owned recording and transactional finalization

**Goal:** record, stop and publish one durable BDMA-ready artifact without Activity ownership.

Work:

- Implement the foreground recording host selected by POC/ADR.
- Move active recording ownership out of `MainActivity`.
- Persist foreground host and media-session state.
- Implement `Temp`/staging output for video and the equivalent safe path for image.
- Implement finalization workflow and final-path publication.
- Persist app/data/media/encoder compatibility metadata in the approved locations and supported
  media formats; do not create standalone per-media JSON.
- Add storage thresholds and controlled start/active-recording behavior.
- Add correlated local reason-code logging.
- Keep Files read-only and ensure it never lists `Temp`.

Acceptance:

- Leaving/recreating the Activity does not create a duplicate recording or lose authority.
- A normal stop produces exactly one final file and a `BDMA_READY` media-session row.
- BDMA-visible folders never expose active partial files.
- FGS start failure rejects recording before evidence state becomes active.
- Storage-full-before-start is controlled and tested.

### Sprint 3 — recovery, emergency and BDMA E2E

**Goal:** preserve/classify interrupted artifacts and prove the passive-producer contract.

Work:

- Run recovery before accepting new recording commands.
- Reconcile DB active/finalizing rows with staging and final files.
- Resume safe finalization or classify as recovered/corrupted/recovery-required.
- Preserve the original DB when corruption is detected and enter safe mode.
- Complete emergency recording with `EMERGENCY_OVERRIDE_ADMIN` and policy tests.
- Validate ADB path and BDMA discovery/import using contract-version fixtures.
- Implement minimum DB lock/busy behavior for concurrent BDMA reads and approved writes.
- Add import/write-back tracking only for fields approved for MVP.
- Decide and implement or explicitly omit MP4 MD5 according to ADR; BDMA must classify no-MD5 MP4
  as Unverified.

Acceptance:

- Process kill during recording/finalizing does not cause silent deletion or duplicate start.
- Reboot expires login, runs recovery and blocks new recording until runtime is safe.
- BDMA imports finalized media and ignores `Temp`.
- BDMA read while DCAM runs does not corrupt DB or interrupt recording.
- Emergency-without-login uses only the protected emergency identity.

### Sprint 4+ — hardening and MVP release candidate

**Goal:** turn the vertical slice into a repeatable internal MVP build.

Work:

- Execute the full real-device failure matrix.
- Run long-duration recording, repeated start/stop, low storage, power and thermal tests.
- Profile CPU, memory, battery, storage growth and log/outbox bounds.
- Verify permission denial/revocation for each affected feature without app crash.
- Test missing Device Owner/Home role and maintenance fallback.
- Validate clean install, upgrade/migration and rollback/reinstall procedure.
- Freeze app/data/media/encoder contract versions for the build.
- Produce installation guide, test report, known issues and BDMA E2E evidence.

Release gate:

- No open critical/high issue in authentication, recording, finalization, recovery, DB integrity or
  BDMA import.
- All mandatory target-device scenarios pass or have an approved documented limitation.

## 9. Mandatory POC/ADR register

| ADR | Decision | Must be settled by |
|---|---|---|
| ADR-001 | Java/XML/ViewBinding/MVVM and current package architecture confirmation | Sprint 0 |
| ADR-002 | CameraX vs Camera2 vs vendor SDK per target device | Before Sprint 2 |
| ADR-003 | Room vs raw SQLite and BDMA concurrency protocol | Before DDL v1 ships |
| ADR-004 | Foreground-service ownership, restart and target-SDK behavior | Before Sprint 2 |
| ADR-005 | Physical Internal/External/Auto roots and finalization mechanics | Before Sprint 2 |
| ADR-006 | MVP encryption off/on, key source and BDMA compatibility | Sprint 0; default off |
| ADR-007 | Password-first auth, emergency policy and later auth methods | Before Sprint 1 acceptance |
| ADR-008 | BDMA MVP write-back tables/fields, locking and cleanup | Before Sprint 3 |

Additional decision records are required for MP4 MD5 content/default and release/update policy if
they are not included in ADR-005/008.

## 10. Runtime failure test matrix

At minimum, automate application logic where possible and execute device-dependent rows on real
hardware:

| Scenario | Expected MVP result |
|---|---|
| Start without operator | Reject `OPERATOR_AUTH_REQUIRED`; no media row/file |
| Emergency without operator | Allow only when policy is enabled; snapshot emergency identity |
| Double start/concurrent key | One active session; deterministic rejection for the other |
| Activity recreated during recording | Recording authority remains singular |
| Process/service killed while recording | Persisted state found on restart; recovery required; no duplicate |
| Kill while finalizing | Resume/reconcile or classify; preserve artifact |
| Reboot while recording | Session expires; recovery runs; login required for new normal capture |
| Storage full before start | Controlled rejection |
| Storage full during recording | Safe stop/failure classification; preserve staging where possible |
| External storage removed | Safe stop/failure; Auto fallback applies only to a new session |
| DB busy/locked by BDMA | Bounded retry/defer; no corruption; recording policy remains deterministic |
| DB corrupt | Preserve original DB; safe mode/recovery; no destructive auto-reset |
| BDMA reads while recording | Only finalized media visible; `Temp` ignored |
| BDMA approved user disable during recording | Current evidence continues; future normal start blocked after safe point |
| Camera SDK error | `CAMERA_FAILED`, resources released, diagnostic correlation retained |
| FGS cannot start | Reject/defer operation; no false active recording state |
| Permission revoked | Only affected command blocked; app remains usable |
| Encryption configured while MVP policy is off | Output remains unencrypted and policy decision is logged safely |

## 11. Test strategy

### Local JVM tests

- Recording authority transitions and invalid transitions.
- Operator/emergency/storage/permission preconditions.
- Filename and contract-version validation.
- Finalization decision logic and failure classification.
- Recovery reconciliation matrix with fake DB/files.
- Developer-gate behavior only for explicitly disableable surfaces.
- Architecture dependency rules and always-on infrastructure rule.

### Android instrumentation/integration tests

- Room fresh schema and every shipped migration.
- Transaction and unique-active-session constraints.
- Staging-to-final file operations on supported storage modes.
- Boot/session invalidation.
- Service start/stop/bind behavior allowed by instrumentation.

### Real BodyCamera tests

- Camera/FGS/screen-off/process kill/reboot.
- Hardware key debouncing and simultaneous commands.
- Physical storage, ADB visibility, removal/full behavior.
- Device Owner/Home/Lock Task policy.
- Long-running stability, power, thermal and performance.
- BDMA import, DB concurrency and cleanup fixtures.

Unit-test and `assembleDebug` success are necessary but not sufficient for MVP acceptance.

## 12. Definition of Done

The MVP codebase is done only when:

- startup orders always-on infrastructure, recovery and login deterministically;
- normal capture requires an authenticated operator;
- emergency capture uses an explicit protected identity and policy;
- one application authority owns start/stop/finalize/recover decisions;
- active recording lifetime is not owned by `MainActivity`;
- recording uses staging and only finalized artifacts enter `Media`;
- media session, operator snapshot, final path and `BDMA_READY` are transactionally persisted;
- crash/reboot recovery preserves uncertain artifacts and blocks unsafe duplicate recording;
- BDMA can discover/import the final artifact through the approved ADB contract;
- logs contain stable reason codes and no secrets;
- later-phase/demo surfaces are hidden and do not run hidden startup behavior;
- the mandatory failure matrix passes on target BodyCamera hardware;
- ADRs, schema export, contract versions, test report and known limitations accompany the build.

## 13. Explicit non-goals for the first MVP

- Realtime AI detection.
- Face, QR or NFC authentication.
- Live streaming, PTT or JT808.
- Advanced remote configuration payload/rollout.
- Cloud media upload/sync.
- Advanced BDMA conflict resolution.
- Production-grade fleet management.
- Automatic self-update before package validation/rollback policy is approved.
- Encryption without approved key management, performance and BDMA decryption evidence.
- Full settings console backed by demo/local-only state.

These items can be planned after the recording/storage/DB/recovery vertical slice passes its release
gate. They must not consume the contingency reserved for target-device failures.
