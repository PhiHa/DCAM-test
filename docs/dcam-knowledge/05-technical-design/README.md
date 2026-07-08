# Technical design draft digest

Source status: Confluence folder [4.2 - Technical Design](https://ducviet.atlassian.net/wiki/spaces/DVID/folder/47120392), refreshed on **2026-07-08**.

Interpretation rule: these pages describe expected target behavior and design direction. Treat them as draft/boss-intent material, not as a concrete description of the current repository and not as final acceptance evidence. After implementation, the official Confluence pages should be corrected and completed against actual behavior.

## Source pages

| Page | Confluence status/version | Last API update (UTC) | Local interpretation |
|---|---:|---|---|
| [DCAM Android Operation Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48562239) | Draft 1.4 / v14 | 2026-07-08 08:59 | Runtime orchestration expectation |
| [DCAM Android Device Owner & Kiosk Policy Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49840280) | Draft 0.3 / v3 | 2026-07-08 08:53 | Dedicated-device/kiosk policy expectation |
| [DCAM In-App Operation, Device Settings & Media Console Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49840330) | Draft 0.8 / v8 | 2026-07-08 08:47 | In-app console/settings/media expectation |
| [DCAM Recording & Capture Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48529484) | Draft 0.9 / v9 | 2026-07-08 02:54 | Recording/capture lifecycle expectation |
| [DCAM Storage Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48496699) | Draft 0.6 / v6 | 2026-07-08 02:55 | Android storage mechanics expectation |
| [DCAM SQLite Database Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48529463) | Draft 1.1 / v11 | 2026-07-08 09:17 | Database/table ownership expectation |
| [DCAM State Machine Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48496753) | Approved 1.8 / v10 | 2026-07-08 09:16 | Cross-runtime guard model; still treat as target design until implemented |
| [DCAM BDMA Integration Technical Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48595030) | Draft 0.5 / v5 | 2026-07-08 05:54 | BDMA implementation expectation |
| [DCAM Device Capability & Feature Eligibility Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48758788) | Draft 0.5 / v5 | 2026-07-08 09:15 | Capability/eligibility state expectation |
| [DCAM Device Provisioning Web Portal Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49315858) | Draft 0.5 / v5 | 2026-07-08 09:58 | Provisioning business-flow expectation |
| [DCAM Web Portal & Device API Contract](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49873154) | Draft 0.1 / v1 | 2026-07-08 09:56 | API/schema boundary expectation |
| [DCAM Self Update Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48529439) | Draft 0.8 / v8 | 2026-07-08 09:18 | APK update expectation |
| [DCAM Security & Encryption Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48496720) | Draft 1.0 / v10 | 2026-07-08 08:57 | Security/key/update/auth expectation |
| [DCAM Sensor & Location Monitoring Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48496794) | Draft 0.6 / v6 | 2026-07-08 02:57 | Optional monitoring expectation |
| [DCAM Realtime AI Detection Design](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48595090) | Draft 0.7 / v7 | 2026-07-08 02:57 | Optional realtime analytics expectation |

## Runtime and kiosk expectations

- Android Operation owns startup orchestration, policy verification, identity restore, provisioning state, login/session lifecycle, foreground service, boot/process survival, runtime module registry, update guard, recovery, and safe mode.
- Production dedicated-device operation should not rely only on fullscreen flags, immersive mode, or Home/Launcher behavior. The design expects DCAM-as-DPC/local Device Owner behavior if target firmware and factory process support it.
- External EMM, Android Management API, and Managed Google Play policy-driven update are not current-baseline assumptions.
- If required Device Owner/DPC or Lock Task policy is missing in production, normal field operation should enter controlled degraded/policy-required state rather than continuing unrestricted.
- Controlled Maintenance Mode is the only expected temporary kiosk exit path. Enter Maintenance Mode / Exit Kiosk temporarily requires a Maintenance Password Gate, safe runtime state, approved targets, audit, and policy restore.
- Normal recording/capture evidence requires an active operator session. Emergency recording may use the protected `EMERGENCY_OVERRIDE_ADMIN` system identity.

## In-app console expectations

- Record / Live View is the default screen after startup/login. Setting is the in-app console hub.
- Back on Record / Live View opens Setting; Back on Setting returns to Record / Live View; Back inside child modules returns to Setting. Back must not exit DCAM while kiosk mode is active.
- File Manager and Media Viewer are read-only/view-only. They must not delete, edit metadata, mark important, export/share, or expose in-progress/temp files as final media.
- Expected console groups include App Operation Settings, Device/System Settings, File/Storage Manager, Media Viewer, Login Settings, User Settings, Admin/Maintenance, Emergency Settings, future connection/stream/PTT/AI groups, and Diagnostics/Support.
- Future modules should stay hidden, disabled, or unavailable until their design and capability/eligibility state allow them.

## Data and reliability expectations

- `RecordingController` is the only owner allowed to start, stop, mark important, finalize, or recover recording sessions. UI, sensor, AI, and emergency managers emit commands/events; they do not call camera/storage SDKs directly.
- Recording prechecks include operator session or emergency override, feature eligibility, camera/audio permissions, storage writable state, free-space threshold, DB availability, battery/thermal policy, update/install safety, and active finalization.
- Recording states include `IDLE`, `PRECHECKING`, `PREPARING_CAMERA`, `PREPARING_STORAGE`, `STARTING`, `RECORDING`, `STOPPING`, `FINALIZING`, `BDMA_READY`, `COMPLETED`, and failure/recovery states.
- Storage design expects temp/staging files to remain invisible to BDMA. Final media appears in approved Media folders only after finalization and DB readiness conditions pass.
- Internal/External/Auto root selection should be resolved before recording. Auto prefers External and falls back to Internal before recording when External is unavailable, full, missing, not writable, or invalid.
- Free-space threshold names exist as design placeholders: `WARNING_FREE_SPACE`, `MIN_START_FREE_SPACE`, `CRITICAL_ACTIVE_FREE_SPACE`, `RESERVED_FINALIZATION_SPACE`, and `MIN_RECOVERY_SPACE`. Exact values remain TBD.
- `BDMA_READY` means safe for BDMA scan/import, not already imported.

## Database and BDMA expectations

- `dcam.db` is expected to own schema/versioning, identity/provisioning, kiosk snapshot if needed, console settings, maintenance audit/session state, remote config cache/apply state, user/operator/auth/session/sync data, operational/applied settings, capability/eligibility, runtime/recovery state, media/session/finalization state, BDMA import/write-back state, tracking, update state/history, and diagnostics.
- `dcam_config.cson` remains device-information only. Operational settings, console settings, kiosk settings, update settings, and remote-config state belong in `dcam.db`.
- BDMA must identify app and contract compatibility through app/package/version plus `dcam_data_contract_version`, `media_contract_version`, and `encoder_contract_version`.
- `bdma_decoder_profile_id` is not used. Unsupported app/contract versions should block import or show compatibility warning without modifying source media.
- BDMA may read device information from `dcam_config.cson` and/or `dcam.db`, but `serial_number`, `owner_name`, and `manufacture_date` are display/support information, not primary identity keys.

## Cloud, provisioning, and update expectations

- `dcam_cloud_device_id` is the server/cloud primary key. `android_id_hash` is the recovery lookup key. Raw Android system identifiers must not be logged.
- Web Portal QR provisioning is DCAM business provisioning, not Android Enterprise Device Owner enrollment.
- The current Web Portal baseline has Android generate a local QR and poll lookup by `android_id_hash`; backend pre-created provisioning challenge/session is not required for the current baseline.
- Web Portal/device APIs must be versioned, use stable reason codes, and treat server config as requested values. Android validates capability, policy, and runtime guard before applying.
- DCAM Self Update / APK update is the primary update path for the current no-external-EMM baseline. Managed Google Play policy-driven update is not applicable.
- Optional manual Play Store fallback requires GMS/Play Store, approved maintenance/factory process, Controlled Maintenance Mode, Maintenance Password Gate, no personal Google account dependency, and policy restore afterward.

## Capability, security, sensors, and AI expectations

- Official feature eligibility states are `ENABLED`, `DEGRADED`, `DISABLED_BY_POLICY`, `DISABLED_BY_PERMISSION`, `UNSUPPORTED_HARDWARE`, `UNSUPPORTED_PERFORMANCE`, `TEMPORARILY_UNAVAILABLE`, `PRUNED`, and `ERROR`. `SUPPORTED` is descriptive wording, not a runtime/persisted state.
- Capability categories include camera, audio, sensors, location, storage, compute, auth method, kiosk policy, in-app console, maintenance, Self Update, Play Store fallback, and BDMA.
- Maintenance credentials must not be hardcoded, stored plaintext, synced to BDMA, exposed through media/file viewer, or logged. Protected representation, lockout/cooldown, reset, and rotation details remain security-review TBD.
- Media encryption naming follows the Data Contract suffixes, but algorithm, key storage, rotation, and BDMA decryption compatibility remain TBD.
- Sensor/location monitoring and realtime AI are optional, capability-gated modules. They may emit event candidates or metadata, but they must not directly control recording, camera, or storage. Their failure must not crash or block core recording unless a future approved policy makes them a direct dependency.

## Implementation caution

Use these pages to understand intended direction, design vocabulary, and future acceptance discussions. Do not mark a feature complete just because it appears here. Current implementation status remains under [local current repository notes](../../local-dev/current-repo/current-state.md), and source-backed gaps remain under [local evidence](../../local-dev/evidence/README.md).
