# DCAM Android Development Standard

**Page ID**: 47120580  
**Version**: 11  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47120580

---


# DCAM Android Development Standard

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Project-specific Android Development Standard

Version

Approved 1.8

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer / Cloud Lead / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.3 - Android Development

Target Audience

Android Developers, Java Desktop Developers, Tech Lead, QA, Cloud/WebServer Team

Last Updated

2026-07-09

Related Documents

DCAM Architecture Home, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 09 - System Settings Requirements, 10 - Android Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Android Device Owner & Kiosk Policy Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM State Machine Design, DCAM-BDMA Data Contract, DCAM Android Training & Architecture Onboarding, 04 - Application & Module Architecture, DCAM Documentation Governance

## 1. Purpose

Tài liệu này định nghĩa tiêu chuẩn phát triển Android riêng cho dự án DCAM.

Mục tiêu:

Chuẩn hóa cách tổ chức code Android cho DCAM.

Tách business logic khỏi Android SDK, hardware SDK, cloud SDK, device policy APIs và vendor-specific SDK.

Giúp code dễ review, test, debug và bảo trì.

Đảm bảo implementation bám sát các runtime design mới: Android Operation, Android Device Owner & Kiosk Policy, Recording & Capture, Storage, SQLite Database, Security, Device Identity, Remote Config và User Management.

## 2. Standard Application Architecture

DCAM Android phải đi theo hướng kiến trúc sau:

textRule quan trọng:

text## 3. Runtime Implementation References

Sau khi các runtime design chính đã được mở rộng, implementation phải bám theo các runtime owner sau.

Runtime Area

Main Implementation Owner

Authoritative Design

App startup, identity restore, provisioning state, kiosk policy verification, login screen, session lifecycle, foreground service, permission lifecycle, module registry, safe mode

`AppRuntimeOrchestrator`, `DeviceIdentityManager`, `ProvisioningManager`, `KioskPolicyManager`, `OperatorSessionManager`, `RuntimeModuleRegistry`, `ForegroundServiceHost`, `RecoveryManager`

DCAM Android Operation Design

Android Device Owner / DPC policy, Lock Task, User Restrictions, Home/Launcher policy, Maintenance Mode

`DevicePolicyStateManager`, `KioskPolicyManager`, `LockTaskController`, `UserRestrictionPolicyManager`, `HomeAppPolicyManager`, `MaintenanceModeController`, `PolicyAuditLogger`

DCAM Android Device Owner & Kiosk Policy Design

Device identity and serial/config file behavior

`DeviceIdentityManager`, `DeviceConfigRepository`, `CsonConfigStore`

04 - Device Configuration Requirements

WebServer/Firebase identity and Web Portal provisioning provider boundary

`CloudDeviceIdentityService`, `ProvisioningService`, `RemoteConfigProvider`

06 - Cloud Services, Update & Configuration Architecture

Remote config cache/apply behavior

`RemoteConfigRepository`, `SettingsRepository`, `ConfigApplyCoordinator`

09 - System Settings Requirements + SQLite Database Design

User/operator management and login policy

`UserRepository`, `UserManagementUseCase`, `AuthMethodManager`, `OperatorSessionManager`

05 - User & Device Operation Requirements

Recording/capture session lifecycle, operator gate and emergency evidence flow

`RecordingController`

DCAM Recording & Capture Design

Android-side storage mechanics, temp/final move, storage recovery and BDMA readiness

`StorageService`, `StorageRootResolver`, `FinalizationManager`, `StorageRecoveryScanner`

DCAM Storage Design

SQLite schema, transaction, identity/config cache tables, user/auth/session tables, write-back, external change detection and DB recovery

`DatabaseService`, `DeviceIdentityRepository`, `RemoteConfigRepository`, `UserRepository`, `OperatorSessionRepository`, `MediaSessionRepository`, `SettingsRepository`, `BdmaWriteBackRepository`

DCAM SQLite Database Design

Device identity security, credential/auth security, provisioning security, kiosk policy security, emergency override auditability and encryption direction

`SecurityService`, `CredentialHasher`, `AuthMethodManager`, `MaintenanceCredentialService`

DCAM Security & Encryption Design

Feature eligibility and runtime pruning

`DeviceCapabilityManager`, `FeatureEligibilityEvaluator`

DCAM Device Capability & Feature Eligibility Design

Cross-runtime guard rules

`StateMachineCoordinator`

DCAM State Machine Design

## 4. Service Interface Rule

Mọi external/platform capability phải được truy cập thông qua Service Interface.

Recommended interfaces:

Interface

Responsibility

Runtime Design Reference

`DevicePolicyStateService` / `DevicePolicyStateManager`

Detect Device Owner / approved DPC state and policy authority.

Kiosk Policy Design + Android Operation

`KioskPolicyService` / `KioskPolicyManager`

Apply/verify production kiosk profile.

Kiosk Policy Design

`LockTaskService` / `LockTaskController`

Verify allowlist, start/stop/recover Lock Task Mode.

Kiosk Policy Design

`UserRestrictionService` / `UserRestrictionPolicyManager`

Apply/verify/remove approved User Restrictions.

Kiosk Policy Design

`HomeAppPolicyService` / `HomeAppPolicyManager`

Configure/verify preferred Home/Launcher policy.

Kiosk Policy Design

`MaintenanceModeService` / `MaintenanceModeController`

Enter/exit authorized Maintenance Mode.

Kiosk Policy Design + Security Design

`DeviceIdentityService` / `DeviceIdentityManager`

Resolve local identity, restore cloud identity by `serial_number` / `serial_lookup/{serial_number}`, manage SD Identity File sync state and provisioning state.

Android Operation + Cloud Architecture + SQLite Design

`ProvisioningService` / `ProvisioningManager`

Quản lý provisioning-required state, QR display data, polling và provisioning result.

Android Operation + Cloud Architecture

`RemoteConfigService` / `RemoteConfigProvider`

Fetch effective config theo `dcam_cloud_device_id`.

Cloud Architecture + System Settings

`RemoteConfigRepository`

Cache target/pending/applied config state trong DB.

SQLite Database Design

`ConfigApplyCoordinator`

Validate và apply/defer config theo runtime guard.

System Settings + State Machine Design

`DeviceConfigStore` / `CsonConfigStore`

Read/write `dcam_config.cson` chỉ cho device information.

Device Configuration Requirements + Data Contract

`CameraService`

Camera open/close/preview/recording adapter.

Recording & Capture Design

`RecordingController`

Owner quyết định authoritative cho recording session.

Recording & Capture Design

`OperatorSessionService` / `OperatorSessionManager`

Create/restore/invalidate/read active operator session.

Android Operation + SQLite Database Design

`AuthMethodService` / `AuthMethodManager`

Dispatch Password, pattern, face, QR và NFC auth method.

Security & Encryption Design

`UserRepository`

Truy cập user/operator profile và sync data qua DB boundary.

SQLite Database Design

`StorageService`

Quản lý storage root/temp/final/recovery mechanics.

Storage Design

`DatabaseService`

Boundary cho SQLite transaction, query và migration.

SQLite Database Design

`PermissionService`

Detect permission state và map affected-feature.

Android Operation Design

`DeviceCapabilityService`

Detect hardware/platform/performance/policy capability.

Device Capability Design

`LocationService`

GPS/location adapter.

Sensor & Location Monitoring Design

`RealtimeAnalyticsService`

Realtime analytics runtime adapter.

Realtime AI Detection Design

`UpdateService`

Thực thi Play Store / Self Update.

Self Update Design

`LogService`

Ghi local diagnostics logging.

Logging Requirements

`SecurityService`

Xử lý credential, identity, encryption/signature/key-related functions.

Security & Encryption Design

`FileIntegrityService`

Checksum/hash/validation theo Data Contract.

Storage Design / Data Contract

Not allowed:

text DevicePolicyManager
ViewModel -> DevicePolicyManager
UI -> startLockTask/stopLockTask directly
UI -> setLockTaskPackages/addUserRestriction/clearUserRestriction directly
Activity -> VendorCameraSdk
ViewModel -> SQLiteDatabase
ViewModel -> cloud/webserver SDK
ViewModel -> credential table / auth storage
UseCase -> Android file path manipulation
UseCase -> original Android system identifier logging
UseCase -> auth values in logs
Sensor Runtime -> Camera SDK
Realtime AI Runtime -> StorageService final media write
BDMA helper -> active operator_session lifecycle fields
BDMA helper -> active media_session lifecycle fields without approved contract
RecordingController bypassing OperatorSessionProvider for normal recording
Remote config code writing operational settings or kiosk settings into dcam_config.cson]]>Allowed direction:

text## 5. Device Owner / Kiosk Implementation Rule

Implementation phải tuân thủ **DCAM Android Device Owner & Kiosk Policy Design**.

Rule

Description

DEV-KIOSK-001

DevicePolicyManager APIs must be isolated behind policy services/managers.

DEV-KIOSK-002

DCAM must detect Device Owner / approved DPC state before normal production field operation.

DEV-KIOSK-003

DCAM must not assume Device Owner state because APK is installed.

DEV-KIOSK-004

Lock Task must not start until package allowlist is verified.

DEV-KIOSK-005

Lock Task entry must be lifecycle-safe and recoverable after reboot/crash/update.

DEV-KIOSK-006

User Restrictions apply/remove must go through `UserRestrictionPolicyManager`, not UI or random use cases.

DEV-KIOSK-007

Maintenance Mode must go through `MaintenanceModeController` and security/audit guard.

DEV-KIOSK-008

Policy failure must return safe domain result/reason code; do not silently continue unrestricted in production profile.

DEV-KIOSK-009

Policy logs must not include maintenance credential, enrollment secret, raw Android identifier or full sensitive config payload.

DEV-KIOSK-010

Update flow must verify policy-safe state and restore Lock Task after restart if required.

## 6. Device Identity / Provisioning Implementation Rule

Implementation phải tuân thủ approved identity và provisioning design.

Rule

Description

DEV-ID-001

Server-side primary device id là `dcam_cloud_device_id`.

DEV-ID-002

`serial_number` là Hardware Identity / primary recovery key.

DEV-ID-003

`serial_lookup/{serial_number}` là approved create/restore lookup path cho cloud identity.

DEV-ID-004

Advertising ID không được dùng làm DCAM identity key.

DEV-ID-005

Original Android system identifier không được ghi vào logs.

DEV-ID-006

Identity resolution phải được implement trong `DeviceIdentityManager`, không nằm trong Activity/ViewModel.

DEV-ID-007

Khi local DB/CSON bị mất nhưng còn app-private serial, app phải restore `dcam_cloud_device_id` bằng `serial_lookup/{serial_number}` trước provisioning.

DEV-ID-008

Nếu server lookup không tìm thấy, app enters `PROVISIONING_REQUIRED`.

DEV-ID-009

Web Portal QR Flow, nếu dùng, là DCAM business provisioning path, không phải Device Owner setup.

DEV-ID-010

Android Enterprise / Device Owner provisioning is separate from Web Portal business provisioning.

DEV-ID-011

`dcam_config.cson` chỉ được update cho device information fields như serial.

DEV-ID-012

`dcam_cloud_device_id`, `serial_number`, SD Identity File sync state và provisioning state phải được persist qua approved DB repository/transaction.

DEV-ID-013

`ANDROID_ID`, `android_id_hash` và `device_lookup/{android_id_hash}` không được dùng trong current production identity/recovery baseline.

## 7. Remote Config Implementation Rule

Remote config identity/fetch/cache/apply baseline và initial setting groups đã được approved. Exact field-level payload schema và field names vẫn TBD cho implementation/API design.

Rule

Description

DEV-CONFIG-001

Remote config chỉ được fetch sau khi device identity đã được resolve.

DEV-CONFIG-002

Fetch dùng `dcam_cloud_device_id`, không dùng serial.

DEV-CONFIG-003

Fetched config phải được cache trong `dcam.db` dưới dạng pending/effective config state.

DEV-CONFIG-004

Config phải pass schema/version/allowed-field/capability/policy validation trước khi apply.

DEV-CONFIG-005

Config apply phải đi qua `ConfigApplyCoordinator`; không apply ad-hoc từ UI/network callback.

DEV-CONFIG-006

Config apply phải deferred trong lúc recording, emergency, finalization, DB/storage recovery, policy recovery hoặc unsafe update state.

DEV-CONFIG-007

Invalid config không được replace last valid applied config.

DEV-CONFIG-008

Operational settings và kiosk settings phải lưu trong `dcam.db`, không lưu trong `dcam_config.cson`.

DEV-CONFIG-009

Serial/device-information update chỉ được update `dcam_config.cson` qua `CsonConfigStore`.

DEV-CONFIG-010

Config fetch/apply/reject/defer result phải được log bằng safe reason codes.

DEV-CONFIG-011

Kiosk remote config is requested policy; actual apply belongs to policy managers.

## 8. User/Auth Implementation Rule

Implementation phải tuân thủ offline-first user management design.

Rule

Description

DEV-AUTH-001

Startup login screen và session lifecycle phải được implement qua Android Operation components, không dùng ad-hoc Activity state.

DEV-AUTH-002

Active operator session phải được read qua `OperatorSessionManager` hoặc approved repository/service.

DEV-AUTH-003

Session không có time-based timeout; background/foreground không được logout.

DEV-AUTH-004

Device reboot phải invalidate previous active session và yêu cầu login lại.

DEV-AUTH-005

Normal recording/capture evidence phải yêu cầu active operator session.

DEV-AUTH-006

Emergency recording without login phải dùng system operator `EMERGENCY_OVERRIDE_ADMIN`.

DEV-AUTH-007

Emergency override không được cấp interactive Admin UI access hoặc Maintenance Mode access.

DEV-AUTH-008

User/auth changes từ BDMA sync phải được validate và apply theo DB/Data Contract rules.

DEV-AUTH-009

User management UI phải dùng UseCase/Repository; không mutate SQLite table trực tiếp từ UI.

DEV-AUTH-010

Auth method capability phải được check trước khi enable face, QR hoặc NFC login methods.

DEV-AUTH-011

Operator login must not override missing required Device Owner / kiosk policy in production profile.

## 9. Recording Implementation Rule

Implementation phải tuân thủ **RecordingController authority**.

textRules:

Rule

Description

DEV-REC-001

Chỉ `RecordingController` được start/stop/finalize/recover recording session.

DEV-REC-002

UI, Sensor và Realtime AI modules chỉ emit commands/events.

DEV-REC-003

Camera SDK calls phải đi qua `CameraService`/adapter.

DEV-REC-004

Final media write/finalization phải đi qua `StorageService`.

DEV-REC-005

DB state changes phải đi qua repository/`DatabaseService` transaction boundary.

DEV-REC-006

Normal recording phải fail fast với `OPERATOR_AUTH_REQUIRED` nếu không có active operator session.

DEV-REC-007

Emergency recording phải persist emergency override attribution nếu chưa có operator logged in.

DEV-REC-008

Media session phải persist operator snapshot tại recording/capture start.

DEV-REC-009

Production policy-required failure must block/defer normal recording before camera start.

## 10. Storage and Database Implementation Rule

Storage và DB code phải tuân thủ runtime design boundaries tương ứng.

Area

Standard

Storage path handling

ViewModel/UseCase không được build physical paths trực tiếp. Dùng `StorageService` / `MediaPathBuilder`.

Temp/final handling

Chỉ Storage layer xử lý temp/staging/final file movement.

CSON handling

Chỉ approved config store được update `dcam_config.cson`, và chỉ cho device information.

BDMA_READY

Chỉ set sau khi Storage + DB readiness conditions pass.

DB write

Dùng repositories và transaction helpers; tránh raw SQL rải rác trong app.

Identity/provisioning DB write

Dùng `DeviceIdentityRepository` / `ProvisioningRepository` và transaction helpers.

Kiosk policy state DB write

Nếu persisted, dùng approved policy repository/table boundary; không write ad-hoc từ UI.

Remote config DB write

Dùng `RemoteConfigRepository` và setting repositories.

User/auth/session DB write

Dùng `UserRepository`, `AuthMethodRepository`, `OperatorSessionRepository` và transaction helpers.

BDMA write-back

Phải validate schema/version/revision và table ownership.

External change detection

User/auth/settings/import writes từ BDMA phải đi qua external change detection/reload/apply logic.

Recovery

RecoveryManager / StorageRecoveryScanner / DB recovery code phải preserve evidence-like files khi chưa chắc chắn.

## 11. Credential, Identity, Policy and Logging Standard

Area

Standard

Credential storage

Chỉ store approved protected representation/reference theo Security Design.

Maintenance credential storage

Chỉ store approved protected representation; không hardcode/plaintext.

Identity storage

Store `dcam_cloud_device_id` và `serial_number` chỉ qua approved repository.

Android system identifier

Không dùng làm production identity/recovery lookup và không log original value.

Advertising ID

Không dùng làm DCAM identity key.

Auth logs

Chỉ log safe reason codes.

Policy logs

Chỉ log safe policy event/reason codes; không log secrets/credentials.

Config logs

Log revision/result/reason code; không dump sensitive config content.

Sensitive values

Không log credentials, tokens, biometric samples, encryption keys, original Android system identifier, maintenance credential, enrollment secret hoặc sensitive media data.

Emergency override log

Log việc emergency override được dùng, kèm safe session/media identifiers.

User sync log

Log sync result/conflict reason, không log sensitive auth values.

Crash/debug logs

Không dump DB rows chứa auth/identity/policy-sensitive data.

## 12. Threading Standard

Task Type

Standard Threading

Device policy check/apply

Policy manager serialized executor or main-thread-safe Android API boundary as required; never block UI with long policy work.

Lock Task enter/exit

Lifecycle-safe UI/main thread coordination through `LockTaskController`, not ad-hoc UI calls.

User Restrictions apply/remove

Policy manager serialized execution; defer if runtime guard unsafe.

Camera open/close/start/stop

Dedicated HandlerThread hoặc SDK-required serialized executor.

Vendor SDK command

Adapter-owned serialized thread trừ khi SDK quy định khác.

File read/write/move/checksum

ExecutorService hoặc dedicated IO executor.

CSON read/write

IO executor; dùng safe temp/write/replace pattern khi cần.

SQLite operations

DB executor / Room executor / controlled SQLite executor.

Identity lookup / remote config fetch

Network executor / provider-managed async mechanism.

Provisioning polling

Background worker; không được block UI.

Remote config validation/apply

Background executor + main-thread UI result only.

User sync through ADB / DB write-back apply

Background executor; không được block UI hoặc recording/finalization.

Auth method validation

Không được block UI lâu hơn mức acceptable UX; heavy work nên chạy off main thread.

Recovery scan

Background executor; không được block UI thread.

Network/update download

Network executor / library-managed executor.

UI update

Main thread qua ViewModel/LiveData.

## 13. Error Handling Standard

Errors phải được map theo từng layer.

textCommon categories:

Category

Example

Device Policy Error

Device Owner missing, Lock Task not permitted, restriction unsupported, Maintenance Mode denied.

Identity Error

Local identity missing, lookup failed, server mapping not found.

Provisioning Error

QR expired, provisioning pending timeout, server provisioning rejected.

Remote Config Error

Fetch failed, schema invalid, apply deferred, apply rejected.

Auth Error

Login failed, operator session missing, user disabled, auth method unavailable.

Permission Error

Camera/location/notification/NFC permission missing.

Capability Error

Device cannot run feature, login method or policy path.

Recording Error

Start/stop/finalize failed, operator auth required, policy required.

Storage Error

Root unavailable, full disk, final move failed.

Database Error

DB locked/corrupted/migration failed.

User Sync Error

Conflict, unsupported schema, invalid write-back data.

Update Error

Preconditions not met, invalid APK, policy unsafe.

Runtime Error

Service killed, process death, safe mode.

## 14. Pull Request Review Checklist

Check

Required

UI/ViewModel does not call SDK/file system/SQLite/identity/cloud/credential storage/DevicePolicyManager directly.

Yes

Runtime code uses authoritative controller/service owner.

Yes

Device policy APIs go through `KioskPolicyManager` / policy services.

Yes

Lock Task entry/exit goes through `LockTaskController`.

Yes

User Restrictions go through `UserRestrictionPolicyManager`.

Yes

Maintenance Mode goes through `MaintenanceModeController` and security guard.

Yes

Device identity resolution goes through `DeviceIdentityManager`.

Yes

Provisioning flow goes through `ProvisioningManager` / approved service.

Yes

Code uses `dcam_cloud_device_id` as server device id, not serial.

Yes

Code uses `serial_number` only as Hardware Identity / recovery key, not cloud primary key.

Yes

Code does not use `ANDROID_ID`, `android_id_hash` or `device_lookup/{android_id_hash}` for current production identity/recovery baseline.

Yes

Code does not use Advertising ID as DCAM identity key.

Yes

Original Android system identifier is not logged.

Yes

Remote config fetch/apply goes through approved provider/repository/coordinator.

Yes

Remote config apply checks runtime guard.

Yes

Kiosk requested-policy config does not directly mutate Android policy outside policy manager boundary.

Yes

Operational settings are not written into `dcam_config.cson`.

Yes

Login/session behavior goes through `OperatorSessionManager`.

Yes

Device reboot invalidates previous active session.

Yes

Background/foreground does not logout operator.

Yes

Recording behavior goes through `RecordingController`.

Yes

Normal recording checks active operator session and required production policy state.

Yes

Emergency override uses `EMERGENCY_OVERRIDE_ADMIN`, not real Admin user.

Yes

Media session stores operator snapshot.

Yes

Storage behavior goes through `StorageService`.

Yes

DB writes go through repository/transaction boundary.

Yes

BDMA user sync/write-back validates schema/version/revision and table ownership.

Yes

Optional modules use Feature Eligibility before runtime start.

Yes

Sensor/AI modules do not control recording directly.

Yes

Hardware SDK calls are isolated in adapters and serialized when needed.

Yes

Long-running work does not block UI thread.

Yes

Raw SDK/DB/IO/auth/config/policy errors are mapped before reaching UI.

Yes

Logs exist for important success/error/recovery/identity/provisioning/config/auth/sync/policy paths.

Yes

Sensitive data is not logged.

Yes

Code links to Jira issue where applicable.

Recommended

## 15. Practical Conclusion

Rule quan trọng nhất khi phát triển DCAM Android là:

textRuntime implementation phải tuân thủ các source-of-truth documents đã được mở rộng:

textCách tổ chức này giúp DCAM ổn định, dễ test và dễ maintain trên nhiều BodyCamera hardware models khác nhau, đồng thời hỗ trợ device identity recovery, Web Portal provisioning, Android dedicated-device/kiosk policy, remote config baseline, offline user management và operator-authenticated recording.