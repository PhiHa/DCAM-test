# DCAM current repository notes

Use this folder for repository-local implementation status, codebase guides, and notes that describe what this checkout currently does.

These files are local development references. They are not Confluence source material and should be compared with the Confluence-derived baseline in `docs/dcam-knowledge` before making product or architecture decisions.

## Files

- [current-state.md](current-state.md) records what this checkout currently implements and where it differs from the target.
- [settings-grid-map.md](settings-grid-map.md) records the current feature-gated settings grid and local implementation map. The broader target direction now lives in the Confluence-derived [Technical Design digest](../../dcam-knowledge/05-technical-design/README.md).
- `DCAM_Huong_dan_dev_MVP_codebase.pdf` is a local codebase guide/reference.

## Local implementation snapshot

Reviewed: **2026-07-08** (Asia/Saigon). Draft/demo settings and placeholder feature pages are intentionally excluded from completed status.

| Area | Code-backed status |
|---|---|
| Android app shell | Java-first Android app with ViewBinding shell, `AppComposition` wiring, capture use cases, CameraX preview/video/photo, MediaRecorder audio, and foreground notification for active video/SOS recording. |
| Feature gates | Project-phase feature gates persist locally and drive menu visibility, disabled-screen fallback, hardware capture-key behavior, remote diagnostics upload enablement, and the hidden Developer settings screen. |
| Local media | Media output uses contract-shaped folders for `Video`, `IMP`, `Image`, and `Audio` with DCAM filenames including optional `_IMP` and `_enc` markers. File browsing is read-only and rooted inside managed media folders. |
| Settings and preferences | Language selection persists locally. Media-encryption preference is gated by the Security/Encryption feature gate and is read by photo, video/SOS, and audio save flows. |
| Encryption | Local AES-256-CTR media transforms exist for saved media when encryption is enabled. Final key-management policy and BDMA compatibility evidence remain open. |
| Diagnostics | Local logging, log rotation, Room-backed Loggly outbox, and runtime Loggly upload enablement are present. Provider-neutral diagnostics design remains future work. |

Open items not claimed as done: approved Internal/External/Auto physical storage-root behavior, operational settings persistence, device-information-only `Config/dcam_config.cson`, app/contract metadata, DB-backed settings, media metadata schema, persisted media lifecycle, recovery, MP4 MD5 sidecars, BDMA E2E import proof, final security/key design, BDMA decryption validation, BodyCamera hardware validation, and later streaming/PTT/cloud/update/transfer/GPS/Device-User flows.
