# DCAM-BDMA Data Contract

Source status: **Approved 1.2**, Confluence page version 3, updated 2026-07-06. This page is a local implementation-oriented digest; the [Confluence Data Contract](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47743153) remains authoritative.

## System boundary

DCAM is the Android-side data producer. BDMA is the desktop-side consumer, importer, and manager. BDMA discovers data over ADB and must scan both the external and internal DCAM media roots, external first. Physical Android paths remain device-specific and require validation on real BodyCamera hardware.

## Logical storage layout

```text
Internal DCAM Storage Root
├── Media/{Video,Image,Audio,IMP}
├── Config/dcam_config.cson
├── Database/dcam.db
├── Logs/logs.txt
└── Temp

External DCAM Storage Root
├── Media/{Video,Image,Audio,IMP}
└── Temp
```

- `dcam_config.cson`, `dcam.db`, and `logs.txt` always live under the internal root.
- Media storage mode is Internal, External, or Auto. Auto prefers external storage and falls back to internal when external storage is missing, full, invalid, unavailable, or not writable.
- Folder names start with an uppercase letter. BDMA imports only from the four `Media` subfolders and ignores `Temp` unless a future contract defines recovery/import behavior.

## Media formats and naming

Supported source formats are `.mp4` video, `.jpg` image, and `.mp3`, `.aac`, or `.wav` audio.

```text
DCAM_<CameraID>_<UserID>_<YYYYMMDD>_<HHMMSS>[_IMP][_enc].<ext>
```

- `CameraID` is 6–10 characters; `UserID` is exactly 6 characters.
- Important media uses `_IMP` and is stored in `Media/IMP` regardless of media type.
- AES-256 encrypted media uses `_enc`. Important encrypted media must use suffix order `_IMP_enc`.
- Metadata is embedded in the media file when the format supports it. A separate per-media JSON file is not part of contract 1.2.
- Encryption algorithm detail, keys, rotation, and BDMA decryption remain for the Security & Encryption Design.

## MP4 checksum behavior

MD5 applies **only** to `.mp4` video, including important and encrypted variants. When enabled, DCAM writes a same-basename `.md5` file beside the video. It does not create MD5 sidecars for image or audio.

| Case | Import result | Source cleanup |
|---|---|---|
| MP4 + matching MD5, verification passes | Imported / Verified | Auto-delete allowed when cleanup is enabled |
| MP4 + MD5, verification fails | Failed / Checksum Mismatch | Do not delete |
| MP4 without MD5 | Imported / Unverified | Per-file user confirmation required before delete |
| Image/audio imported successfully | Imported | Auto-delete allowed when cleanup is enabled |
| Unreadable, unsupported, or unsupported encrypted media | Failed | Do not delete |

Deleting a source MP4 also permits deletion of its matching `.md5`. Cleanup must never delete config, database, logs, failed media, or `Temp` files.

## Config, database, and logs

| Artifact | Purpose | DCAM | BDMA |
|---|---|---|---|
| `Config/dcam_config.cson` | Static/semi-static device identity only | Read/write | Read/write/update device information only |
| `Database/dcam.db` | User data, device tracking, operational data/configuration | Read/write/update | Read/write/update the complete database, subject to schema/locking safety |
| `Logs/logs.txt` | Operational and diagnostic logs | Read/write | Read-only; never modify, truncate, or delete |

`dcam_config.cson` must not contain media metadata, user/history data, storage mode, feature flags, MD5/encryption settings, cleanup policy, import/sync state, or logs. Those operational settings belong in `dcam.db`.

BDMA must not alter source media bytes, embedded metadata, MD5 content, or `Temp`. It may delete source media only after a successful import under the cleanup matrix above.

## Versioning and remaining design work

- Contract version: 1.2.
- Media naming baseline: 1.0; a breaking naming change requires a contract update, BDMA compatibility review, and an ADR when architecturally significant.
- `dcam.db` needs an agreed schema-version mechanism; the contract gives `db_schema_version = 1.0` only as an example.
- Exact physical roots, SQLite schema, embedded metadata fields/encoding, encryption/key design, concurrency protocol for BDMA writes, duplicate/retry semantics, and detailed recovery behavior still need designs, requirements, fixtures, and joint DCAM-BDMA tests.
