# DCAM database DDL v1 design

Status: proposed design for review  
Prepared against repository state: 2026-07-09  

> Build 0.1 implementation note (2026-07-11): this proposed DDL is not the media-readiness source of
> truth for the Working Recording Slice. BDMA discovers contract files from `Media/*` and ignores
> `Temp`; safe filesystem publication is the Build 0.1 readiness boundary. Per-media/session tables
> remain deferred until their exact schema, consumer, audit purpose and bounded retention/deletion
> policy are approved. This note does not remove the proposed recovery/audit design for a later
> build.

Companion SQL: [database-ddl-v1.sql](database-ddl-v1.sql)  
Authority: Approved DCAM–BDMA Data Contract 1.6 plus the MVP codebase guide

## 1. Decision summary

The MVP should use one authoritative Room-managed SQLite database named `dcam.db` for:

- device identity and provisioning state;
- offline users, authentication references and operator sessions;
- requested/applied operational settings;
- runtime, foreground-host and feature-eligibility state;
- media recording, finalization, recovery and BDMA readiness;
- BDMA import/write-back coordination;
- structured diagnostics and the provider-specific log-delivery queue.

Media bytes, logs and device-information CSON remain separate artifacts. The database stores their
identity, lifecycle and compatibility state; it does not store large media BLOBs.

The schema uses stable text IDs, UTC epoch-millisecond timestamps, relative contract paths,
optimistic revisions, explicit state columns and versioned BDMA views. It deliberately avoids a
generic entity/value database: query-critical data remains typed and indexed.

“Future proof” here means changes can be added safely and old consumers can detect compatibility.
It does not mean predicting every future feature or putting arbitrary JSON into every table.

## 2. Baseline freeze rule

The current source calls its four-table schema Room version 1, and
`AppDatabaseMigrations` says that version has not shipped.

Before implementation:

1. Confirm whether any database version 1 build has been distributed outside disposable
   development devices.
2. If **not shipped**, replace the current Room v1 baseline and regenerate `1.json`.
3. If **shipped anywhere that must retain data**, do not rewrite v1. Implement this design as v2
   with a tested `Migration(1, 2)`.
4. Once a version ships, never edit/delete its schema JSON or migration. Add a new version.

The SQL file is a design reference. Room entities, foreign keys, indexes and `@DatabaseView`
definitions remain the runtime source. Custom views or indexes not expressible by annotations must
be installed idempotently by the migration/on-create path and covered by tests.

## 3. Database placement and files

Logical contract location:

```text
Internal DCAM Storage Root/
└── Database/
    └── dcam.db
```

The exact Android physical location remains subject to ADR-005 and real-device/ADB validation.
Requirements:

- the authoritative DB is never placed on removable external storage;
- `dcam.db`, `dcam.db-wal` and `dcam.db-shm` are treated as one live SQLite database;
- Android backup/data extraction excludes the DB unless a reviewed secure backup design says
  otherwise;
- the app never uses destructive migration fallback;
- a corrupt database is preserved for recovery/diagnostics rather than overwritten automatically.

## 4. Naming and representation conventions

### 4.1 SQL naming

- Tables, columns, indexes and views use `lower_snake_case`.
- Primary business IDs use descriptive names such as `media_session_id`, never a generic `id`.
- Singleton tables use `singleton_id = 1`.
- Index prefixes: `uq_` unique, `ix_` non-unique.
- Released BDMA views are versioned: `bdma_media_v1`, never an unversioned view whose meaning
  changes silently.

### 4.2 IDs

- Application-generated UUIDs are lowercase canonical text.
- Integer autoincrement IDs are limited to local append-only sequences where external identity is
  unnecessary, such as media events and change logs.
- `request_id` is an idempotency key. Repeated hardware/UI delivery of the same request cannot
  create a second media session.
- IDs are never reused after deletion or failure.

UUID text costs more space than a 16-byte BLOB but is easier to inspect over ADB, log safely,
exchange with BDMA and migrate across languages. If size becomes measurable, a later schema can add
a compact representation without changing external IDs.

### 4.3 Time

- Persistent wall-clock values use UTC Unix epoch milliseconds in `INTEGER`.
- `NULL` means unknown/not occurred. Do not use `0` as a fake timestamp.
- Flows sensitive to wall-clock changes also store `elapsed_realtime_ms` and `boot_id`.
- Filename time uses the approved contract format, but database time remains epoch milliseconds.

### 4.4 Booleans and enums

- Boolean values are `INTEGER NOT NULL CHECK (... IN (0, 1))`.
- State/reason/type values are uppercase stable `TEXT` codes.
- Critical readers must map unknown future codes to `UNKNOWN`/safe behavior, not crash.
- Most enum columns intentionally have no SQL `CHECK` list. Adding a state should not require a
  table rebuild, while state-transition policy remains in domain/application code.
- A released code is never repurposed with a different meaning.

### 4.5 JSON and BLOBs

- JSON is reserved for opaque/versioned extension payloads, diagnostics and external requests.
- Data used for filtering, joining, ownership, state or compatibility gets a real column.
- Every externally supplied JSON payload has a schema version, size limit and allowlist validator.
- Credential hashes/protected data use BLOB/Keystore references, never JSON or plaintext.
- Media bytes and thumbnails do not belong in `dcam.db`.

### 4.6 Mutable rows

Every mutable contract row has:

- `updated_at_ms`;
- integer `revision`, starting at 1;
- where relevant, `source` and upstream `server_revision`.

Updates use optimistic concurrency:

```sql
UPDATE user_profile
SET display_name = ?, updated_at_ms = ?, revision = revision + 1
WHERE user_id = ? AND revision = ?;
```

Zero changed rows means conflict/stale input, not success.

## 5. Table catalog

### 5.1 Compatibility and identity

#### `schema_version`

One row that BDMA reads before any other DB object.

Contains:

- Room/SQLite schema version;
- schema name;
- app code, package, version name/code;
- DCAM data, media and encoder contract versions;
- minimum compatible BDMA schema/version when agreed;
- compatibility status and revision.

`PRAGMA user_version`, Room schema JSON and this row must agree. Startup enters safe mode when they
conflict.

#### `device_identity`

One current device identity row.

Contains stable server identity (`dcam_cloud_device_id`), Android recovery hash, serial/device/model
mirrors, owner/manufacture/firmware/hardware display fields, identity status/source and revisions.

Ownership:

- DCAM/server owns `dcam_cloud_device_id` and `android_id_hash`.
- BDMA may change only device-information fields approved by Data Contract/ADR-008.
- No Firebase-specific field belongs in the stable contract table. Provider IDs, if required, go in
  a provider adapter table or versioned remote payload.

#### `provisioning_state`

One current state row with state/reason, environment, attempt counters and timestamps.

It is separate from `device_identity` so provisioning retries/failure do not rewrite stable identity
columns and can evolve independently.

### 5.2 User, authentication and active operator

#### `user_profile`

Offline operator profile and synchronization unit.

- `user_id` is the stable application/server identity and is not overloaded as a filename field.
- `file_user_id` is the unique contract six-character UserID used in media filenames.
- Uses soft disable/delete (`status`, `deleted_at_ms`); evidence references are not broken by
  physical profile deletion.
- Includes role, validity window, source, server revision and local revision.

#### `user_auth_method`

One row per authentication method.

- Supports password first and later pattern/QR/NFC/face references without adding columns to
  `user_profile`.
- Stores algorithm/version and protected credential blob or Keystore alias.
- Never stores plaintext password, PIN, QR secret or biometric template.
- Only one active method of a given type per user.

#### `operator_session`

Login session scoped to one boot.

- Stores user reference plus immutable operator ID, six-character filename UserID, name and role
  snapshots.
- `identity_type` distinguishes normal operator and protected emergency identity.
- Includes `boot_id`, process ID, status, start/end reason and activity time.
- A partial unique index enforces at most one `ACTIVE` session.
- Reboot expires old active sessions before normal capture is accepted.

The emergency identity `EMERGENCY_OVERRIDE_ADMIN` is not six characters. A media session therefore
stores both the full operator identity and a separately approved six-character filename UserID
mapping.

### 5.3 Settings and capability

#### `operational_setting`

Requested settings keyed by `(scope_type, scope_id, setting_key)`.

- `scope_id` is a non-null empty string for a global/device scope to avoid SQLite NULL uniqueness
  surprises.
- `value_type` defines safe decoding (`BOOLEAN`, `INTEGER`, `DECIMAL`, `STRING`, `JSON_V1`, etc.).
- `source` identifies DEFAULT, LOCAL, BDMA or REMOTE.
- Operational settings do not belong in `dcam_config.cson`.

#### `applied_setting_state`

Separates requested from actually applied runtime state.

Examples:

- storage mode requested `EXTERNAL`, applied `INTERNAL` due to unavailable media;
- resolution requested but unsupported by current CameraX/vendor capabilities;
- new setting accepted for the next recording but not the active recording.

The row references the exact requested revision it attempted to apply.

#### `feature_eligibility_state`

Persisted result of capability/policy evaluation:

- requested enabled;
- capability available;
- effective enabled;
- status/reason and capability fingerprint.

This table is for operational eligibility, not hidden developer UI preferences. Shared
infrastructure is never disabled through it.

### 5.4 Runtime and foreground ownership

#### `app_runtime_state`

One current runtime row:

- boot/process identity;
- mode such as `INITIALIZING`, `RECOVERY`, `LOGIN_REQUIRED`, `OPERATIONAL`, `SAFE_MODE`;
- current operator/media references;
- recovery and last-clean-shutdown markers;
- heartbeat and last reason code.

It is not a historical event log. Important transitions also emit diagnostics/media events.

#### `foreground_service_state`

One row per long-lived host, keyed by a stable service key such as `RECORDING_HOST`.

Tracks state, start reason, active media session, owner boot/process, heartbeat, restart count and
last error. It lets recovery distinguish a cleanly stopped service from a killed/stale process.

#### `recording_lease`

Singleton database mutex for recording authority.

- Contains the active media-session ID and owner/generation/heartbeat.
- Starting recording claims the empty lease in the same transaction that inserts the session.
- Ordinary code never steals a stale lease. Only recovery can classify the old session and advance
  the lease generation.
- This is stronger and more portable than relying only on an in-memory flag or a complex partial
  expression index.

The lease coordinates DCAM processes; it is not a replacement for serializing camera SDK calls.

### 5.5 Media lifecycle

#### `media_session`

Authoritative current record for video, important/emergency video, image and future approved audio.

It contains:

- UUID session and idempotent request IDs;
- media/capture/importance/state codes;
- operator session reference and immutable operator snapshots;
- separate approved six-character `file_user_id_snapshot`;
- camera/device, boot and process snapshots;
- logical storage root plus staging/final **relative** paths;
- final filename/MIME type;
- requested/start/stop/complete times and duration/size;
- encryption result flag;
- error/recovery disposition and revision.

Absolute paths are deliberately excluded. A physical root can change across devices or storage
APIs while the logical root and contract-relative path remain stable.

#### `media_finalization_state`

One-to-one durable finalization workflow:

- current stage;
- staging/target paths and observed source attributes;
- validation, encryption, checksum and publish status;
- checksum algorithm/hex/sidecar path when applicable;
- `bdma_ready`;
- retry count, timestamps and safe error detail.

`media_session.state = COMPLETED` and `bdma_ready = 1` are both required for the BDMA view.

#### `media_session_event`

Append-only ordered lifecycle history for one session.

Records sequence, event type, from/to state, reason, boot/process and redacted details. It answers
“how did this file reach this state?” without trying to reconstruct history from mutable columns.

Events are inserted in the same DB transaction as the state update they describe.

### 5.6 BDMA import and external writes

#### `media_import_state`

One row per consumer import attempt.

- Supports repeated attempts without overwriting history.
- Records verification outcome, source revision, cleanup eligibility and source-deletion result.
- MP4 without MD5 can be `UNVERIFIED` without pretending checksum success.
- Media/session rows are retained when source media is deleted after successful import.

#### `external_change_request`

Preferred safe write boundary for BDMA.

BDMA inserts a versioned, idempotent request naming an approved target and expected revision. DCAM
validates/executes it and writes result status. This avoids permitting arbitrary direct mutation of
active runtime tables.

Only operations and fields approved by ADR-008 are accepted. Unknown targets/payload fields fail
closed.

#### `external_change_log`

Append-only outcome audit for accepted/rejected external changes, with before/after revisions and a
payload hash. It contains redacted diagnostics, not credentials or entire sensitive payloads.

If BDMA must directly update approved tables rather than use requests, ADR-008 must define:

- exact writable columns;
- optimistic revision predicates;
- maintenance/active-recording restrictions;
- lock timeout and retry behavior;
- corruption and interruption recovery;
- equivalent change-log entries.

Pull-edit-push replacement of a live `dcam.db` is never permitted.

### 5.7 Optional provider/cache and diagnostics

#### `remote_config_cache`

One dormant/cache row for a future approved remote-config provider. It has source, payload schema,
signature, validation/application timestamps and redacted error. It must not be initialized on the
critical local-recording startup path when remote config is not in scope.

#### `diagnostic_event`

Bounded structured local diagnostics with stable reason codes and boot/operator/media correlation.
No secrets, raw credentials or unrestricted exception payloads.

This table is not the source of truth for media state. It supplements the media event table and
plain local log.

#### `pending_logs`

Provider-specific outbound delivery queue. It is infrastructure, not part of the BDMA/application
domain contract. It may be pruned by bounded retry/retention rules without deleting media/session
state or the local primary log.

## 6. State vocabularies

State values require a shared code catalog in source and documentation. Initial values:

| Area | Values |
|---|---|
| Runtime | `INITIALIZING`, `RECOVERY`, `LOGIN_REQUIRED`, `OPERATIONAL`, `SAFE_MODE`, `SHUTTING_DOWN` |
| Provisioning | `PROVISIONING_REQUIRED`, `IN_PROGRESS`, `ACTIVE`, `FAILED`, `SUSPENDED` |
| Operator session | `ACTIVE`, `LOGGED_OUT`, `EXPIRED_REBOOT`, `REVOKED`, `FAILED` |
| Media session | `REQUESTED`, `PRECHECKED`, `STARTING`, `RECORDING`, `STOP_REQUESTED`, `FINALIZING`, `COMPLETED`, `FAILED`, `RECOVERY_REQUIRED`, `RECOVERED`, `CORRUPTED` |
| Finalization | `NOT_STARTED`, `WAITING_FOR_ENGINE`, `VALIDATING`, `ENCRYPTING`, `CHECKSUMMING`, `PUBLISHING`, `COMPLETE`, `FAILED` |
| Import | `DISCOVERED`, `IMPORTING`, `IMPORTED`, `FAILED`, `CLEANUP_PENDING`, `SOURCE_DELETED` |
| External request | `PENDING`, `PROCESSING`, `APPLIED`, `REJECTED`, `CONFLICT`, `FAILED` |

Rules:

- Unknown states make BDMA stop unsafe writes and make DCAM enter safe/recovery behavior where
  evidence could be affected.
- `FAILED` does not imply safe deletion.
- `COMPLETED` does not imply imported.
- `IMPORTED` does not imply source deleted.
- `RECOVERED` must record what was recovered and whether it is BDMA-ready.

## 7. Transaction workflows

SQLite cannot transact camera/filesystem work together with DB rows. Use a persisted saga: commit
intent/state, perform one external step, then commit its result.

### 7.1 Login

One transaction:

1. Read active user/auth method and verify lock/status.
2. Expire any stale active session for another boot.
3. Insert `operator_session`.
4. Set `app_runtime_state.active_operator_session_id` and mode `OPERATIONAL`.
5. Write a structured diagnostic event.

Password verification occurs outside a long DB write transaction. Updating failed-attempt counters
uses an optimistic update.

### 7.2 Start recording

Prechecks read operator, permission/capability and storage status. Then one short transaction:

1. Confirm the active operator session/revision.
2. Confirm `recording_lease.media_session_id IS NULL`.
3. Insert `media_session` as `STARTING`.
4. Insert `media_finalization_state` as `NOT_STARTED`.
5. Insert first `media_session_event`.
6. Claim `recording_lease` with the session/boot/process and increment generation.
7. Update runtime/foreground desired state.

Commit before starting the Android service/camera engine. Callback transactions advance to
`RECORDING` or `FAILED` and release the lease only after safe cleanup/classification.

The unique `request_id` makes duplicate key/UI delivery idempotent.

### 7.3 Stop and finalize

1. Transactionally change `RECORDING -> STOP_REQUESTED` and append event.
2. Ask the recording engine to stop outside the transaction.
3. Final callback transaction changes to `FINALIZING`.
4. Each durable external step updates `media_finalization_state`.
5. After validation/publish, one transaction:
   - sets final relative path/name/size/times;
   - sets media state `COMPLETED`;
   - sets finalization `COMPLETE` and `bdma_ready = 1`;
   - appends completion event;
   - clears foreground active session and recording lease;
   - clears runtime active media reference.

Failure preserves staging, records reason, and moves to `RECOVERY_REQUIRED` when outcome is
ambiguous.

### 7.4 Recovery

Recovery runs before new recording:

1. Read runtime state, recording lease, active media states and finalization rows.
2. Compare logical DB paths with staging/final files.
3. Claim recovery using the current boot/process and lease generation.
4. Resume only idempotent known steps.
5. Otherwise preserve files and classify `RECOVERY_REQUIRED`/`CORRUPTED`.
6. Release the lease only after the old session has a durable non-active disposition.

Recovery never deletes an unknown file merely because no current row references it.

### 7.5 BDMA cleanup

1. BDMA/import workflow records verified/imported result.
2. DCAM validates cleanup eligibility against current media revision and contract result.
3. Mark `CLEANUP_PENDING`.
4. Delete allowed media and matching MP4 MD5 only.
5. Mark `SOURCE_DELETED` with timestamp.

Never delete `Temp`, config, DB, logs, failed/unverified-without-confirmation media, or unknown
artifacts.

## 8. Concurrency and WAL policy

Recommended starting policy, subject to target-device benchmark:

- foreign keys enabled;
- WAL journal mode;
- `synchronous = FULL` for maximum durability if performance passes POC, otherwise an explicitly
  approved `NORMAL` trade-off;
- bounded busy timeout;
- short DB transactions with no SDK, filesystem, network, encryption or checksum work inside;
- one Room instance per process and no main-thread queries;
- checkpoint/retention monitoring so WAL growth is bounded.

### 8.1 BDMA reads

Copying only `dcam.db` while WAL is active can produce a stale/incomplete snapshot. Approved options,
in preference order:

1. DCAM creates/exposes a consistent read snapshot after a checkpoint.
2. ADB-side tooling opens the live database read-only through SQLite under an approved access path.
3. A controlled maintenance/read window pauses writers, checkpoints, then permits pulling the DB.
4. Pull DB/WAL/SHM as a coordinated set only if proven safe by the integration design.

ADR-008 must choose and test one. “ADB pull `dcam.db` whenever” is not acceptable.

### 8.2 BDMA writes

- Preferred: insert `external_change_request`; DCAM applies it.
- Direct writes, if contractually required, are limited to approved fields and use revision checks.
- BDMA never writes active runtime/operator/media/finalization rows.
- Active capture transactions do not wait indefinitely for BDMA.
- A busy DB causes bounded defer/retry, never destructive recreation.

## 9. BDMA compatibility surface

Versioned views provide a smaller stable read contract:

- `bdma_schema_info_v1`
- `bdma_media_v1`
- `bdma_user_v1`

Benefits:

- internal tables can gain columns without changing BDMA queries;
- only finalized/ready media appears;
- sensitive auth/runtime fields are excluded;
- breaking changes create `*_v2` rather than mutating released semantics.

BDMA must read `bdma_schema_info_v1` first and reject unsupported schema/data/media/encoder
versions before attempting writes.

Views are not a security boundary by themselves. Filesystem/ADB permissions and the approved access
protocol remain required.

## 10. Ownership and write permissions

| Data | DCAM | BDMA |
|---|---|---|
| Schema/contract metadata | Write/migrate | Read; stop on incompatibility |
| Server identity primary mapping | Write/apply server result | Read only |
| Device information mirror | Read/write | Approved request/write fields only |
| User profiles | Local/auth/session use; apply validated sync | Read; approved revisioned changes |
| Auth credential material | Write/use | No direct read/write unless a future security contract explicitly allows |
| Active operator session | Read/write | Read only when approved; never mutate |
| Operational settings | Request/apply | Approved revisioned requests/fields |
| Runtime/foreground/recording lease | Read/write | No write |
| Media/finalization/events | Read/write | Read finalized view; no lifecycle write |
| Import state | Reconcile/cleanup | Record through approved import protocol |
| External request/log | Validate/apply/audit | Insert request/read result |
| Diagnostics/local logs | Write/prune by policy | Read only |
| Pending remote-log delivery | Internal infrastructure | No access |

## 11. Room implementation structure

Split the current broad `CloudStateDao` by ownership:

```text
platform/database/
├── AppDatabase
├── entities/
├── dao/
│   ├── CompatibilityDao
│   ├── DeviceIdentityDao
│   ├── UserAuthDao
│   ├── OperatorSessionDao
│   ├── OperationalSettingsDao
│   ├── RuntimeStateDao
│   ├── MediaSessionDao
│   ├── MediaImportDao
│   ├── ExternalChangeDao
│   ├── DiagnosticEventDao
│   └── PendingLogDao
└── migrations/
```

Application features do not depend on Room DAOs/entities. They depend on repositories/ports; Room
implementations map between domain values and database entities.

Implementation requirements:

- `@Transaction` for each invariant-changing workflow;
- explicit `@ForeignKey` and `@Index` definitions matching the SQL;
- snake-case `@ColumnInfo` names;
- no `OnConflictStrategy.REPLACE` for evidence/runtime rows because SQLite REPLACE is delete+insert;
- use insert/optimistic update/upsert intentionally;
- type converters must preserve unknown state codes safely;
- export and commit every Room schema JSON;
- migration tests start from every shipped version;
- database callbacks seed singleton rows idempotently.

## 12. Migration from the current four-table prototype

If the current v1 is confirmed disposable/unshipped:

- replace the baseline and regenerate Room `1.json`;
- update entities/DAOs together;
- reset only local development DBs explicitly.

If migration is required:

| Current table | Target |
|---|---|
| `device_identity` | Copy/massage into new `device_identity`; move provisioning status to `provisioning_state` |
| `remote_config_cache` | Copy into expanded cache; preserve payload/revision/status/timestamps |
| `operational_settings` | Copy to `operational_setting` with `scope_type='DEVICE'`, empty scope ID and inferred value type/source |
| `pending_logs` | Copy to snake-case delivery schema preserving event ID, status and retry counters |

Migration procedure:

1. Create new tables with temporary names where shape changes.
2. Validate required legacy values and supply explicit defaults.
3. Copy data; never silently truncate malformed records.
4. Rename/drop only after row-count/key validation.
5. Create indexes/views and singleton seeds.
6. Update compatibility metadata.
7. Let Room validate against the exported target schema.
8. Test representative valid, null, malformed and retry/dead legacy rows.

No migration may use destructive fallback.

## 13. Security and privacy

- Exclude DB, WAL and SHM from Android cloud/auto backup pending security approval.
- Store password hashes using the approved slow KDF and per-credential parameters/salt; use Android
  Keystore references where appropriate.
- Never persist plaintext passwords, encryption keys, tokens or raw biometric templates.
- Redact diagnostic/error fields and impose size limits.
- Do not put credentials in remote-config or external-change JSON.
- Database-at-rest encryption is not assumed. If later required, choose an implementation by ADR,
  benchmark it on target hardware and provide migration/BDMA access design.
- Logs and provider outbox may reference IDs/reason codes, not sensitive payload contents.

## 14. Retention and deletion

Retention values remain product/security decisions, but ownership rules are fixed:

- users are soft-disabled/deleted while referenced history exists;
- operator and media snapshots remain valid after profile changes;
- media lifecycle/import audit survives allowed source-file cleanup;
- pending delivery logs and low-priority diagnostics are bounded/prunable;
- dead delivery events follow the existing archive/quarantine policy;
- external change logs are retained long enough to diagnose BDMA conflicts;
- unknown, failed, recovery-required and checksum-mismatch artifacts are not auto-deleted;
- deletion happens through a use case with audit/state updates, never arbitrary DAO cascade from UI.

## 15. Index rationale

Indexes in the companion SQL cover:

- one active operator and one recording lease;
- request idempotency;
- recovery scans by media/finalization state and update time;
- final-path uniqueness;
- operator/media history;
- BDMA-ready completed media;
- pending imports/external requests/log delivery;
- diagnostics by time, reason and media session.

Do not add “index every column.” Review query plans using realistic row counts. Every index increases
recording-time write cost and migration complexity.

## 16. Verification plan

### Pure/unit tests

- state/reason code mapping and unknown-code fallback;
- operator/file UserID validation;
- optimistic revision conflicts;
- idempotent request handling;
- recovery/finalization decision matrix;
- BDMA cleanup eligibility.

### Room migration/instrumentation tests

- fresh schema exactly matches exported JSON;
- every shipped migration path;
- foreign-key behavior;
- active-session and request uniqueness;
- recording lease claim/release/recovery;
- transaction rollback on injected failure;
- views expose only approved columns and ready media;
- singleton rows and schema-version agreement.

### Concurrency/device tests

- recording writes while BDMA performs approved reads;
- busy/locked database behavior;
- process kill between every saga commit and external step;
- WAL checkpoint/snapshot correctness;
- DB corruption preservation and safe mode;
- storage full while WAL grows;
- long-duration row/index growth and query latency.

## 17. Open decisions before implementation freeze

1. Has existing Room v1 shipped, or can it be replaced?
2. Exact internal physical DB path and ADB access mechanism.
3. Room/WAL/synchronous setting after target-hardware benchmark.
4. BDMA consistent-read snapshot protocol.
5. External request inbox versus approved direct writes.
6. Exact writable BDMA tables/fields and revision/conflict behavior.
7. Password KDF/Keystore policy and emergency six-character filename identity mapping.
8. Exact embedded media metadata and contract-version values.
9. MP4 MD5 default/content/performance.
10. Retention windows for sessions, lifecycle events, diagnostics and external-change audit.
11. Whether diagnostic events need BDMA visibility in MVP.
12. Database corruption recovery/export procedure.

Resolve these through ADR-003, ADR-005, ADR-006, ADR-007 and ADR-008 before declaring the schema
frozen.

## 18. Design validation performed

The companion SQL was executed from an empty in-memory SQLite 3.46 database during this design
review:

- 59 statements executed successfully;
- 21 application tables, 33 explicit indexes and 3 views were created (plus SQLite internal
  objects);
- `PRAGMA foreign_key_check` returned zero rows.

This proves SQL syntax and declared foreign-key consistency on that SQLite version. It does not
replace Room entity/schema validation, Android target-version compatibility, migration tests, WAL
concurrency tests or real BodyCamera/BDMA validation.
