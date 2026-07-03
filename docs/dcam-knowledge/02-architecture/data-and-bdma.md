# Data, storage, and DCAM–BDMA boundary

## Core boundary decision

**DCAM is a passive data producer. BDMA is an active data consumer and manager.**

- DCAM creates original media, metadata, local records/database, device/user context, states, and logs.
- DCAM keeps source data locally and exposes an agreed ADB-readable location or mechanism.
- BDMA detects the connected device and initiates discovery, reading, synchronization, import, validation, indexing, display, backup, export, and reporting.
- DCAM does not push/upload/sync source data to BDMA in the current scope.
- The current transfer/access boundary is ADB—not REST, WebSocket, FTP, MQTT, cloud upload, or a DCAM-to-BDMA socket.

## Ownership by stage

| Stage | Owner/source of truth |
|---|---|
| Creation and finalization on BodyCamera | DCAM |
| Exposed source files/records before import | DCAM |
| ADB connection, discovery, read, and sync | BDMA |
| Import staging, validation, and retry | BDMA |
| Managed desktop copy, index, display, backup/export | BDMA |

Normal sync should treat Android source data as read-only. Deleting source files, writing import markers, or changing config requires an explicit future policy, design, and ADR.

## Data categories

| Data | Current direction |
|---|---|
| Video | `.mp4` source media |
| Image | `.jpg` source media |
| Audio/PTT | Audio file if applicable; final format TBD |
| Metadata | Local DB record, standalone file, or both; format TBD |
| Local DB | May hold media/status/device/user context; BDMA access TBD |
| Logs | Local file/DB; may be copied for support/import diagnostics |
| Device/User context | Created or captured by DCAM, then mapped by BDMA |
| Config | Ownership and BDMA visibility/write policy TBD |

Do not treat these possible extensions/formats as a final contract except where the Data Contract later confirms them.

## Source media lifecycle direction

Proposed DCAM states:

```text
recording → pending → completed
               ├────→ corrupted
               └────→ recovered (TBD)
```

Proposed BDMA import states are separate: `discovered`, `imported`, `validated`, `indexed`, and `import_failed`.

Source state and import state must not be collapsed. DCAM owns readiness/integrity before import; BDMA owns desktop import progress afterward.

## Metadata direction

DCAM must create enough versioned metadata for BDMA to map media without guessing. The baseline fields are `file_id`, `file_type`, `device_id`, `timestamp`, optional valid GPS, source `status`, and `schema_version`; user/operator arrives with the Phase 2 foundation, and checksum remains TBD.

The missing Data Contract must specify:

- Storage root, folders, filenames, and discovery rules.
- Whether the DB, metadata files, or both are authoritative/exposed.
- Exact fields, types, required/optional rules, status values, and schema versions.
- Media-to-metadata association.
- ADB read expectations.
- Validation and error behavior.
- Duplicate, partial, interrupted, and repeated import behavior.
- Integrity/checksum behavior.
- Cleanup, retention, and any permitted BDMA writes.

## Error ownership

| Scenario | Primary owner |
|---|---|
| Record/capture failure | DCAM |
| Missing or wrong source metadata | DCAM; BDMA reports validation error |
| Incomplete/corrupt source before import | DCAM classifies where detectable; BDMA must reject/report |
| BodyCamera storage full | DCAM |
| GPS unavailable | DCAM continues core work and marks unavailable |
| ADB detection/disconnect/read transport issue | BDMA |
| Desktop import/validation/index/display issue | BDMA |
| Data Contract/schema mismatch | Shared and versioned |

## Boundary for future features

- Remote management adds a command/config path but does not automatically transfer media ownership.
- Streaming adds a real-time channel while recorded-media ownership remains local/ADB-based.
- PTT adds real-time audio and possibly stored artifacts that need a contract.
- Cloud sync may add a new consumer and must explicitly revisit ownership.
- Any future write/delete responsibility across the boundary requires an ADR.
