# DCAM SQLite Database Design

**Page ID**: 48529463  
**Version**: 14  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48529463

---


# DCAM SQLite Database Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 1.3

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / DB Reviewer / BDMA Lead / Security Reviewer / Cloud Lead / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, BDMA Developers, QA, Support, Cloud/WebServer Team

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, DCAM-BDMA Data Contract, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 09 - System Settings Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Self Update Design, DCAM Device Capability & Feature Eligibility Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM State Machine Design, DCAM Security & Encryption Design, 07 - Logging & Diagnostics Requirements

## 1. Purpose

**DCAM SQLite Database Design** định nghĩa `dcam.db` schema direction và runtime database behavior cho settings, runtime state, media/session state, update state, capability/eligibility state, optional kiosk policy state snapshot, in-app console state, maintenance audit/session state, monitoring/tracking state, **offline user/operator management**, authentication/session state, **device identity/provisioning state**, device information mirror, app/contract metadata, remote config cache và BDMA write-back compatibility.

Current baseline follows **DCAM Factory Provisioning & Device Production SOP**:

textwide760Tài liệu này cũng định nghĩa table ownership, schema versioning, migration policy, transaction boundaries, database locking expectations, external write detection, user sync/write-back validation, provisioning recovery và database recovery behavior.

## 2. Database Boundary

`dcam.db` là runtime state boundary giữa:

textwide760Important rule:

textwide760## 3. Authoritative References

Topic

Authoritative Document

Local Usage

DB schema, ownership, transaction, lock, write-back and recovery behavior

DCAM SQLite Database Design

Tài liệu này là authoritative cho `dcam.db` runtime database behavior.

Factory provisioning, DSetup, serial injection and SD Identity File

DCAM Factory Provisioning & Device Production SOP

DSetup resolves serial from SD Identity File or barcode and injects serial into DCAM.

API/data schema and Firestore identity contract

DCAM Web Portal & Device API Contract

Định nghĩa `serial_lookup/{serial_number}`, `devices/{dcam_cloud_device_id}` và production record fields.

Device identity, device information and CSON scope

04 - Device Configuration Requirements

Tài liệu này lưu identity mirror, device information mirror và provisioning state.

Firebase/WebServer identity and provisioning architecture

06 - Cloud Services, Update & Configuration Architecture

Định nghĩa `dcam_cloud_device_id`, `serial_number`, Web Portal/Firebase provisioning and cloud metadata.

Android Device Owner / kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

Tài liệu này chỉ persist optional policy state snapshot/audit pointers nếu cần; actual policy behavior không nằm ở DB design.

In-app console / maintenance UX

DCAM In-App Operation, Device Settings & Media Console Design

Tài liệu này lưu requested/applied console settings, non-sensitive maintenance session/audit state and approved target state if needed.

Self Update

DCAM Self Update Design

Tài liệu này lưu update state/history and validation result; artifact/install behavior belongs to Self Update Design.

Remote config apply policy

09 - System Settings Requirements

Tài liệu này lưu pending/applied config state, including requested kiosk policy/update settings.

User/operator requirements, login policy and emergency override requirement

05 - User & Device Operation Requirements

Tài liệu này persist user/operator/auth/session data.

BDMA-facing data/file/import/write-back/user sync contract

DCAM-BDMA Data Contract

Data Contract định nghĩa BDMA được read/write/sync những gì, bao gồm app/data/media/encoder contract compatibility.

Android runtime startup/login/recovery

DCAM Android Operation Design

Android Operation mở DB, resolve identity, verify policy state và quản lý session lifecycle.

Credential/auth/security/kiosk/update security

DCAM Security & Encryption Design

Credentials, identity values, maintenance values, Google account and policy-sensitive values phải tuân theo security rules.

## 4. Database Ownership Rule

Android owns database schema và runtime invariants.

BDMA và Firebase/WebServer chỉ có thể ảnh hưởng tới approved data thông qua approved sync/config flows. Android validate trước khi apply runtime state.

Core rules:

textwide760## 5. Table Ownership Matrix

Table / Area

Android Write

BDMA Write

Firebase/WebServer Write to Device DB

Conflict Policy

`schema_version`

Yes

No

No

Android owns migration.

`device_identity`

Yes

No

Indirect via provision/fetch only

Android lưu resolved identity từ server.

`device_information` / device info fields

Yes

Approved fields only if contract allows

Indirect via provision/fetch only

Android validates and mirrors latest device information.

`device_identity_history`

Yes

No

Indirect via provision/fetch only

Append-only direction.

`provisioning_state`

Yes

No

Indirect via provision status fetch

Android runtime-owned.

`sd_identity_sync_state`

Yes

No

No

Android-owned local sync state for recovery cache.

`kiosk_policy_state`

Yes

No

Indirect requested policy only

Android-owned snapshot; actual policy belongs to Kiosk Policy Design.

`kiosk_policy_history`

Yes

No

No

Append-only safe audit/reference direction.

`console_setting_state`

Yes

No

Indirect requested settings only

Android validates and applies.

`maintenance_session_state`

Yes

No

No

Android-owned; no secret values.

`maintenance_audit_history`

Yes

No

No

Append-only safe audit.

`remote_config_cache`

Yes

No

Indirect via config fetch only

Android validate trước khi cache/apply.

`remote_config_apply_state`

Yes

No

No

Android owns applied state.

`user_profile`

Yes

Yes

No direct write

Two-way sync; conflict rule by Data Contract/User Design.

`user_auth_method`

Yes

Yes, approved fields only

No direct write

Auth refs phải versioned và security-validated.

`operator_session`

Yes

No

No

Android runtime-owned; read-only cho diagnostics.

`user_sync_state`

Yes

Yes

No direct write

Shared sync checkpoint/revision state.

`operational_setting`

Yes

Yes, allowed fields only

Indirect via remote config fetch

Chỉ là requested value; Android validate trước khi apply.

`applied_setting_state`

Yes

No

No

Android source of truth cho applied runtime value.

`feature_eligibility_state`

Yes

No

No

Android evaluate; server/BDMA read cho support.

`update_state`

Yes

No

Indirect update request/config only

Android-owned Self Update runtime state.

`update_history`

Yes

No

No

Append-only safe update audit/history.

`media_session`

Yes

Limited / normally No

No

Android source of truth cho recording/capture lifecycle và operator snapshot.

`media_import_state`

Yes

Yes

No

BDMA có thể update import status theo contract.

`external_change_log`

Yes

Yes

No direct write

Dùng để notify Android về BDMA write-back.

`diagnostic_event`

Yes

No

No

Android write; external read-only.

## 6. Core Table Groups

Group

Candidate Tables

Purpose

Ownership Direction

Schema / Migration

`schema_version`, `schema_migration_history`

DB version và migration status.

Android-owned.

Device Identity / Information / Provisioning

`device_identity`, `device_information`, `device_identity_history`, `provisioning_state`, `sd_identity_sync_state`

Cloud identity, serial mirror, owner name, manufacture date, app/contract metadata, provisioning status and SD recovery-cache sync status.

Android-owned local mirror.

Kiosk Policy Snapshot

`kiosk_policy_state`, `kiosk_policy_history`

Optional snapshot of requested/applied policy revision, lock task state, maintenance mode and last policy error.

Android-owned snapshot only; actual policy owned by Kiosk Policy Design.

In-app Console

`console_setting_state`, `console_module_visibility`, `console_navigation_state` if persisted

Requested/applied console settings, module visibility and support state if needed.

Android-owned; can be derived/runtime-only if simple.

Maintenance

`maintenance_session_state`, `maintenance_audit_history`, `maintenance_failed_attempt_state`

Non-sensitive maintenance session/audit/lockout/cooldown metadata.

Android-owned; no plaintext credential.

Remote Config

`remote_config_cache`, `remote_config_apply_state`, `remote_config_history`

Target/pending/applied config revision và apply result.

Android-owned cache/apply state.

User / Operator

`user_profile`, `user_auth_method`, `operator_session`, `user_sync_state`, `user_change_history`

Offline user management, login methods, operator session và BDMA/DCAM sync.

Shared profile/auth/sync; session Android-owned.

Settings

`operational_setting`, `applied_setting_state`, `setting_change_history`

Requested/applied settings và audit.

Android-owned; BDMA/remote config có thể request allowed values.

Capability / Eligibility

`device_capability_profile`, `device_performance_profile`, `feature_eligibility_state`, `feature_runtime_state`

Capability detection và runtime eligibility.

Android-owned.

Runtime State

`app_runtime_state`, `foreground_service_state`, `recovery_state`

Android runtime và recovery state.

Android-owned.

Media / Recording

`media_session`, `media_event_marker`, `media_finalization_state`

Recording/capture lifecycle, operator snapshot và finalization state.

Android-owned.

BDMA

`media_import_state`, `bdma_import_history`, `external_change_log`

BDMA import/write-back coordination.

Shared theo contract.

Monitoring / Tracking

`sensor_monitoring_state`, `device_tracking`, `tracking_sync_state`

Sensor/GPS/tracking runtime và future sync.

Android-owned; field-level sharing TBD.

Update

`update_state`, `update_history`, `update_package_validation_state`

Self Update state, validation result, install result and history.

Android-owned.

Play Store Fallback Audit

`play_store_fallback_history` optional

Optional manual fallback audit only; no Google password/token.

Android-owned if fallback enabled.

Diagnostics

`diagnostic_event`, `runtime_error_event`

Support/QA diagnostics.

Android write, external read-only.

## 7. Device Identity, Information and Provisioning Tables

Approved identity rules:

textwide760### 7.1 `device_identity`

Field

Meaning

`dcam_cloud_device_id`

Stable Firebase/WebServer primary cloud device id.

`serial_number`

Hardware Identity / primary recovery key.

`serial_source`

`DSETUP_INJECTED`, `SD_IDENTITY_FILE`, `BARCODE_SCAN`, `WEB_PORTAL`, `SERVER_RESTORE`, `LOCAL_CSON`, `MANUAL_APPROVED_FALLBACK`, etc.

`owner_name`

Latest owner/customer/agency display name.

`manufacture_date`

Device manufacture date in ISO format `YYYY-MM-DD`.

`device_info_source`

Source of owner/manufacture/device info values.

`firebase_installation_id`

Current app-install instance metadata; not a device identity key.

`device_model`

Model snapshot.

`firmware_version`

Firmware snapshot.

`app_package_name`

Android package name.

`app_version_name`

App version name snapshot.

`app_version_code`

App version code snapshot.

`dcam_data_contract_version`

Overall DCAM-BDMA data contract version.

`media_contract_version`

Media contract version for folder/naming/checksum/import/cleanup.

`encoder_contract_version`

Fixed DCAM encoder contract version.

`identity_state`

`LOCAL_ONLY`, `SERIAL_REQUIRED`, `RESOLVED`, `PROVISIONING_REQUIRED`, `ACTIVE`, `DISABLED`, `REVOKED`, `ERROR`.

`last_identity_lookup_at`

Last serial lookup time.

`last_identity_restore_at`

Last restore time.

`last_seen_report_at`

Last server heartbeat/report time.

### 7.2 `sd_identity_sync_state`

Field

Meaning

`sd_available`

Whether approved SD card/external storage is available.

`sd_identity_file_path_reference`

Approved relative path reference, e.g. `DCAM_FACTORY/device_identity.json`.

`sync_state`

`NOT_REQUIRED`, `SYNCED`, `MISSING_RECREATED`, `CARD_REPLACED_RECREATED`, `FAILED`, `CONFLICT_OVERWRITTEN`, `CONFLICT_REPORTED`.

`last_sync_at`

Last successful sync timestamp.

`last_error_code`

Safe reason code if sync failed.

Rules:

textwide760### 7.3 `device_information` Optional Split

For MVP, `owner_name` and `manufacture_date` may be stored directly in `device_identity` to keep schema simple.

If the device information set grows, it may be split into a dedicated table with `dcam_cloud_device_id`, `serial_number`, `owner_name`, `manufacture_date`, `device_model`, `firmware_version`, `updated_at` and `source`.

## 8. Kiosk Policy Snapshot Tables

Detailed Device Owner / Lock Task / User Restrictions behavior thuộc **DCAM Android Device Owner & Kiosk Policy Design**. DB chỉ lưu optional snapshot/audit fields nếu implementation cần recovery/support.

### 8.1 `kiosk_policy_state` Optional Table

Field

Meaning

`requested_policy_revision`

Requested kiosk policy revision from remote/admin config if applicable.

`applied_policy_revision`

Last policy revision successfully verified/applied locally.

`device_policy_state`

`DEVICE_POLICY_UNKNOWN`, `DEVICE_POLICY_REQUIRED`, `DEVICE_POLICY_APPLIED`, `POLICY_DEGRADED`, `POLICY_RECOVERY_REQUIRED`.

`lock_task_state`

`LOCK_TASK_READY`, `LOCK_TASK_ACTIVE`, `LOCK_TASK_FAILED`, `NOT_REQUIRED`, etc.

`user_restriction_profile`

Applied/reported restriction profile name/version.

`maintenance_mode_state`

`NONE`, `REQUESTED`, `ACTIVE`, `EXITING`, `FAILED`.

`last_policy_check_at`

Last policy verification timestamp.

`last_policy_error_code`

Safe policy reason code.

`last_policy_restore_at`

Last restore attempt timestamp after reboot/update/maintenance.

Rules:

textwide760## 9. In-app Console and Maintenance State

### 9.1 `console_setting_state`

Field

Meaning

`setting_key`

Console setting key, e.g. video resolution, pre-record, device/system proxy, update option.

`requested_value`

Requested value.

`applied_value`

Actual applied value if applied.

`apply_state`

`PENDING`, `APPLIED`, `DEFERRED`, `REJECTED`, `FAILED`.

`defer_reason`

Runtime guard reason.

`updated_by_user_id`

Actor if local setting change.

`updated_at`

Timestamp.

### 9.2 `maintenance_session_state`

Field

Meaning

`maintenance_session_id`

Maintenance session id.

`requested_by_user_id`

Admin/Maintenance actor.

`state`

`REQUESTED`, `AUTH_REQUIRED`, `AUTH_FAILED`, `ACTIVE`, `EXITING`, `EXPIRED`, `RESTORE_FAILED`, `COMPLETED`.

`started_at`

Session start timestamp.

`expires_at`

Timeout timestamp if configured.

`last_restore_attempt_at`

Last policy restore attempt.

`last_result_code`

Safe result/reason code.

### 9.3 `maintenance_failed_attempt_state`

Field

Meaning

`actor_user_id`

Actor attempting maintenance access.

`failed_count`

Non-sensitive failed attempt count.

`cooldown_until`

Cooldown/lockout time if policy applies.

`last_failed_at`

Last failed attempt timestamp.

Rules:

textwide760## 10. Self Update and Optional Play Store Fallback State

### 10.1 `update_state`

Field

Meaning

`update_id`

Current/last update operation id.

`update_source`

`SELF_UPDATE`, `LOCAL_FACTORY_APK`, `PLAY_STORE_FALLBACK`.

`current_version_code`

Current app version code snapshot.

`target_version_code`

Target version code if known.

`state`

`NONE`, `CHECKING`, `AVAILABLE`, `DEFERRED`, `DOWNLOADING`, `VALIDATING`, `INSTALLING`, `FAILED`, `INSTALLED`, `VERIFIED`.

`defer_reason`

Recording/emergency/finalizing/policy recovery/etc.

`validation_result`

Package validation result.

`install_result`

Install result if available.

`last_checked_at`

Last update check.

`last_attempt_at`

Last install attempt.

`last_success_at`

Last successful update.

### 10.2 `update_history`

Append-only update audit/history.

Field

Meaning

`update_event_id`

Unique event id.

`event_type`

`CHECK_STARTED`, `MANIFEST_LOADED`, `APK_DOWNLOADED`, `APK_VALIDATED`, `INSTALL_STARTED`, `INSTALL_FAILED`, `VERSION_VERIFIED`, `POLICY_RESTORED`, `PLAY_FALLBACK_STARTED`, `PLAY_FALLBACK_COMPLETED`.

`source`

`SELF_UPDATE`, `LOCAL_FACTORY_APK`, `PLAY_STORE_FALLBACK`.

`target_package`

DCAM or approved app package.

`old_version_code`

Old version if known.

`new_version_code`

New version if known.

`result_code`

Safe result/reason code.

`created_at`

Timestamp.

Rules:

textwide760## 11. Remote Config Tables

Remote config rule:

textwide760Candidate tables:

Table

Purpose

`remote_config_cache`

Lưu fetched config payload/metadata trước runtime apply.

`remote_config_apply_state`

Lưu pending/applied config state.

`remote_config_history`

Append-only fetch/apply/reject/defer history.

## 12. User / Operator Tables

User/operator tables vẫn được định nghĩa theo User Requirements và Security Design:

Table

Purpose

`user_profile`

Offline user/operator profile data.

`user_auth_method`

Approved credential/auth method references.

`operator_session`

Current và historical operator sessions.

`user_sync_state`

BDMA/DCAM user sync checkpoint và revision.

`user_change_history`

User/auth change audit history.

Required system operator:

textwide760Session rule:

textwide760## 13. Runtime State, Settings and Feature Eligibility

`feature_eligibility_state.eligibility_state` phải dùng official state set từ **DCAM Device Capability & Feature Eligibility Design**.

Allowed values:

textwide760`SUPPORTED` không được dùng làm persisted `feature_eligibility_state` value.

## 14. Media Session Schema Direction

`media_session` lưu recording/capture session state. Recording business lifecycle được định nghĩa bởi **DCAM Recording & Capture Design**.

Required operator attribution fields include:

textwide760Important distinction:

textwide760## 15. External Write Detection and Apply Policy

BDMA write-back, remote config changes, requested kiosk policy changes, console settings and update settings phải detectable và được apply an toàn.

Change types include:

textwide760Apply examples:

Change

During Recording Behavior

Rule

Remote config affecting runtime

Defer tới safe window.

Preserve evidence/runtime stability.

Kiosk policy affecting Lock Task/User Restrictions

Defer during recording/finalizing/emergency/recovery; apply through KioskPolicyManager only.

Preserve evidence and avoid unsafe policy transition.

Console setting affecting recording

Defer/reject during active recording/finalization/emergency.

In-App Console + Recording guard.

Self Update requested

Defer during recording/emergency/finalization/recovery/policy unsafe state.

Self Update guard.

Disable active user

Không interrupt current recording; block new recording sau safe window.

Preserve evidence.

Change storage/encryption/recording settings

Defer tới safe window.

Tránh corrupt session.

## 16. DB Recovery Behavior

Scenario

Expected Behavior

DB missing after reinstall/delete but app-private serial exists

Recreate DB and attempt server identity restore by `serial_lookup/{serial_number}`.

CSON missing after reinstall/delete

Restore serial/device information từ app-private storage/server if identity lookup succeeds.

SD Identity File missing while app-private serial exists

Recreate SD Identity File from app-private serial.

SD card replaced while app-private serial exists

Create SD Identity File on new SD card.

SD Identity File conflicts with app-private serial

App-private serial wins; overwrite SD file or raise warning according to policy.

Factory reset clears app-private data

DSetup must recover serial from SD Identity File or barcode scan and inject serial again.

Serial lookup not found

Enter `PROVISIONING_REQUIRED` or approved factory/admin provisioning flow.

Server unavailable but local identity exists

Continue với last valid local identity/config cache.

Server unavailable and no local identity

Stay trong provisioning/identity recovery state cho đến khi có network hoặc admin action.

Policy snapshot missing

Re-check actual Device Owner/Lock Task/User Restrictions state; recreate snapshot if needed.

Policy snapshot conflicts with Android policy state

Actual Android policy state wins; update snapshot and log safe reason code.

Maintenance session state incomplete after crash/reboot

Attempt policy restore; mark session expired/restore result with safe reason.

Update state incomplete after crash/reboot

Verify current app version and policy state; mark update verified/failed/recovery required.

DB corrupted

Preserve corrupted DB, create recovery DB nếu allowed, enter safe recovery behavior.

Active operator session exists after reboot

Mark `EXPIRED_BY_REBOOT`; yêu cầu login lại.

User table missing/corrupted

Block normal recording, cho phép system modules và emergency override nếu policy cho phép.

Emergency override system operator missing

Recreate từ default/migration nếu safe; nếu không thì log fatal auth config issue.

Media file exists but DB record missing

Reconcile bằng cách scan final folders và insert recovered `media_session` nếu có thể.

Invalid remote config data

Reject và giữ last valid applied config.

Recovery rule:

textwide760## 17. Security / Backup / Logging

Area

Direction

Raw ANDROID_ID

Không được store/log raw.

`android_id_hash`

Không dùng/store như production recovery lookup trong current baseline.

Advertising ID

Không được dùng làm identity key.

`serial_number`

Approved Hardware Identity / recovery key; logs must follow safe production logging policy.

SD Identity File

Recovery cache only; do not treat as Hardware Identity.

Credential storage

Password, pattern, QR, NFC credentials chỉ được store dưới dạng hash/reference.

Maintenance credential

Không được store plaintext trong DB; only approved protected representation outside generic runtime tables.

Google account password/token

Không được store/log trong DB.

Update package secrets

Không store private signing key/secret.

DB backup before write-back

Recommended trước BDMA write-back hoặc remote config migration.

Audit

Identity restore, provisioning, SD Identity File sync, config apply, kiosk policy events, maintenance, update and user/auth changes nên được recorded.

Evidence safety

DB recovery không được delete source media/evidence khi chưa có approved rule.

Required example logs use safe identifiers/reason codes only:

textwide760
[CONFIG] Remote config deferred: 
[POLICY] Policy snapshot updated
[MAINTENANCE] Maintenance session started
[MAINTENANCE] Policy restore result: 
[UPDATE] Self update deferred: 
[UPDATE] Self update verified
[UPDATE] Play Store fallback used
[DB] External write detected: 
[AUTH] Session expired by reboot]]>## 18. Open Questions / TBD

Item

Status

Final SQLite implementation approach: Room vs raw SQLite wrapper

TBD

Final journal mode: WAL vs rollback journal on target BodyCamera

TBD / POC

Exact `schema_version` format

TBD

Whether optional snapshot tables are physical tables or runtime-only

TBD / Implementation

Exact SD Identity File sync table/schema need

TBD / Android + QA

Remote config payload fields

Initial setting groups and apply/cache baseline defined in System Settings; exact field-level payload schema TBD

Config profile schema

TBD / Remote Config implementation

Kiosk requested-policy payload fields

Initial requested-policy groups defined in System Settings/Kiosk Policy Design; exact field-level schema TBD

Provisioning API exact contract

API contract baseline defined in DCAM Web Portal & Device API Contract; exact implementation/auth details TBD

Provisioning QR signature/expiration format

TBD

Owner name validation length/charset

TBD

Manufacture date source and correction policy

TBD

External change detection polling/observer mechanism

TBD

Transaction retry policy

TBD

Exact BDMA user sync allowed fields

TBD in Data Contract

User conflict resolution UI on BDMA

TBD

Face authentication implementation model

TBD by device capability/security review

QR/NFC credential formats

TBD

DB encryption requirement

TBD

`media_session` exact schema

TBD

Self Update physical table schema details

TBD / Implementation

Maintenance failed-attempt lockout physical schema details

TBD / Security + Implementation

Migration test matrix with BDMA/WebServer versions

TBD

## 19. Practical Conclusion

`dcam.db` là runtime database boundary, không chỉ là schema file.

textwide760