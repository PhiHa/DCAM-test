# DCAM Android Operation Design

**Page ID**: 48562239  
**Version**: 18  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48562239

---


# DCAM Android Operation Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 1.7

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer / Cloud Lead / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, QA, Support, Cloud/WebServer Team

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 09 - System Settings Requirements, 10 - Android Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Device Provisioning Web Portal Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Self Update Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM SQLite Database Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM State Machine Design, DCAM Device Capability & Feature Eligibility Design, DCAM Security & Encryption Design

## 1. Purpose

**DCAM Android Operation Design** định nghĩa Android runtime startup, kiosk policy verification, identity restore, device information restore, provisioning state, login/session lifecycle, in-app console readiness, foreground service, boot/process survival, permission lifecycle, runtime module registry, Self Update runtime guard, recovery và safe mode behavior.

Trang này follows [**DCAM Factory Provisioning & Device Production SOP**](/wiki/spaces/DVID/pages/49545629/DCAM+Factory+Provisioning+Device+Production+SOP) làm chuẩn định danh thiết bị:

serial_number = Hardware Identity / primary recovery key
dcam_cloud_device_id = Cloud Identity / primary cloud device id
SD Identity File = recovery cache trên thẻ nhớ ngoài, không phải Hardware Identity
Không dùng ANDROID_ID
Không dùng android_id_hash
Không dùng device_lookup/{android_id_hash}
Trang này là authoritative cho Android runtime orchestration. Full Web Portal provisioning business flow, screens, QR direction, backend API direction, states và audit/error handling được định nghĩa trong **DCAM Device Provisioning Web Portal Design**.

Chi tiết Device Owner/DPC policy, Lock Task Mode, User Restrictions, Home/Launcher policy, Maintenance Mode và no-external-EMM baseline thuộc **DCAM Android Device Owner & Kiosk Policy Design**.

Chi tiết Record/Live View default, Setting hub, child modules, Maintenance Password Gate UX, Controlled Mode và optional Play Store fallback UX thuộc **DCAM In-App Operation, Device Settings & Media Console Design**.

Chi tiết APK artifact/download/validation/install thuộc **DCAM Self Update Design**.

## 2. Current Runtime Baseline

No external EMM / No Android Management API / No Managed Google Play. (per ADR - Dedicated Device / Device Owner / Lock Task Decision)
DCAM-as-DPC / local Device Owner is preferred if target firmware supports it.
Primary update path = DCAM Self Update / APK update.
Manual Google Play Store update = optional controlled maintenance fallback only if GMS/Play Store exists and approved process exists.
Device identity restore uses serial_number and dcam_cloud_device_id.
SD Identity File is a recovery cache only.
ANDROID_ID/android_id_hash/device_lookup are not used in current production baseline.
Runtime must not wait for or depend on EMM policy APIs.

## 3. Runtime Ownership

Area

Runtime Owner

Source of Truth

Startup orchestration

`AppRuntimeOrchestrator`

Tài liệu này

Device policy state detection

`DevicePolicyStateManager`

Tài liệu này + Kiosk Policy Design

Kiosk policy orchestration

`KioskPolicyManager`

Kiosk Policy Design

Lock Task runtime entry/recovery

`LockTaskController`

Tài liệu này + Kiosk Policy Design

User Restrictions apply/verify

`UserRestrictionPolicyManager`

Kiosk Policy Design

Home/Launcher policy verify

`HomeAppPolicyManager`

Kiosk Policy Design

In-app console readiness/navigation state

`ConsoleNavigationCoordinator` / `ConsoleModuleRegistry`

In-App Console Design

App operation settings runtime apply

`ConsoleSettingCoordinator` / `AppOperationSettingsManager`

In-App Console + System Settings + Recording Design

Device/system setting proxy

`DeviceSystemSettingsController`

In-App Console + Kiosk Policy Design

Maintenance Mode runtime transition

`MaintenanceModeController`

Kiosk Policy + Security + In-App Console

Maintenance access/password gate

`MaintenanceAccessController`

Security + In-App Console

Approved maintenance targets

`ApprovedMaintenanceTargetController`

In-App Console + Kiosk Policy + Security

Device identity restore

`DeviceIdentityManager`

Tài liệu này + SQLite + Cloud Architecture + Factory SOP

SD Identity File sync

`SdIdentityFileManager` / `DeviceIdentityManager`

Tài liệu này + Factory SOP

Device information restore

`DeviceIdentityManager` / `DeviceConfigRepository`

Tài liệu này + Device Configuration Requirements + SQLite

Provisioning runtime state

`ProvisioningManager`

Tài liệu này

Provisioning business flow

Web Portal + Backend + Android runtime

Device Provisioning Web Portal Design

Login/session lifecycle

`OperatorSessionManager`

Tài liệu này + User Requirements + SQLite

Auth method dispatch

`AuthMethodManager`

Security Design

Self Update runtime coordination

`SelfUpdateCoordinator` / `UpdateService`

Self Update Design + System Settings

Optional Play Store fallback launch

`MaintenanceModeController` + `ApprovedMaintenanceTargetController`

In-App Console + Security

Foreground service

`ForegroundServiceHost`

Tài liệu này

Boot/process survival

`ProcessSurvivalCoordinator`

Tài liệu này + Recording/Storage/DB/Kiosk/Self Update designs

Runtime module registry

`RuntimeModuleRegistry`

Tài liệu này + Device Capability

Recovery coordination

`RecoveryManager`

Tài liệu này + Storage/DB/Recording/Kiosk/Update designs

State coordination

`StateMachineCoordinator`

State Machine Design

## 4. Startup Flow

App process starts / device boots
    ↓
Initialize logging
    ↓
Detect runtime profile and kiosk policy requirement
    ↓
Check Device Owner / DCAM DPC policy state if production profile requires it
    ↓
If required policy authority is missing:
        enter DEVICE_POLICY_REQUIRED or POLICY_DEGRADED
        block/defer normal field operation according to deployment policy
    ↓
Verify or apply User Restrictions when policy authority exists
    ↓
Verify Home/Launcher policy if required
    ↓
Verify Lock Task allowlist
    ↓
Open or create dcam.db
    ↓
Read dcam_config.cson if available
    ↓
Resolve device identity and device information by serial_number / dcam_cloud_device_id
    ↓
Sync SD Identity File if app-private serial_number is available
    ↓
Run DB/storage/recovery checks
    ↓
Load settings / remote config cache / update metadata cache
    ↓
Detect device capability including policy capability and GMS/Play Store availability
    ↓
Evaluate feature eligibility
    ↓
Prepare in-app console modules based on role/capability/policy/product phase
    ↓
Start system modules that do not require operator login
    ↓
Enter Lock Task Mode when Activity/UI lifecycle is ready and safe
    ↓
Resolve operator session
    ↓
If valid same-boot session exists:
    enter OPERATOR_AUTHENTICATED / READY flow and show Record / Live View
Else:
    show Login Screen
System modules allowed before login:

logging
Device Owner / DPC policy state detection
kiosk policy verification
Lock Task allowlist verification
DB recovery
storage recovery
capability/eligibility detection
console capability pruning
sensor monitoring if eligible and policy allows
GPS/system tracking if eligible and policy allows
BDMA/user-sync readiness
remote config identity/provisioning checks by serial_number/dcam_cloud_device_id
SD Identity File sync when app-private serial_number exists
update metadata check if safe and allowed
Normal recording/capture evidence is blocked until operator session exists, except emergency override flow. If required production kiosk policy is missing, normal field operation must be blocked/degraded instead of silently continuing unrestricted.

## 5. Main Screen / Console Runtime

Runtime default screen after successful startup/login:

Record / Live View = default main screen
Setting = in-app console hub
Back behavior:

Record / Live View
    └── Back → Setting

Setting
    └── Back → Record / Live View

Setting → Any child module
    └── Back → Setting
Runtime rules:

Rule

Description

OP-CONSOLE-001

Record / Live View is default visible screen after login/session restore.

OP-CONSOLE-002

Back must not exit DCAM while kiosk mode is active.

OP-CONSOLE-003

Back on Record opens Setting; Back on Setting returns Record.

OP-CONSOLE-004

Back from child modules returns Setting.

OP-CONSOLE-005

Child module access must respect role, capability, policy and runtime guard.

OP-CONSOLE-006

File Manager and Media Viewer must remain read-only.

OP-CONSOLE-007

Future modules must be hidden/disabled until approved design.

## 6. Controlled Maintenance Runtime

Controlled maintenance is the only approved temporary kiosk exit path.

Admin / Maintenance requested
    ↓
Validate Admin or approved Maintenance role
    ↓
Maintenance Password Gate
    ↓
Validate runtime safe state
    ↓
Enter Controlled Maintenance Mode
    ↓
Temporarily stop Lock Task / relax only approved restrictions if needed
    ↓
Open only approved Android Settings screen or approved maintenance app
    ↓
Return to DCAM
    ↓
Restore User Restrictions and Lock Task policy
Runtime rules:

Rule

Description

OP-MAINT-001

Maintenance Password Gate is required before Enter Maintenance Mode / Exit Kiosk temporarily.

OP-MAINT-002

Emergency override must not satisfy Maintenance Password Gate.

OP-MAINT-003

Controlled Mode must not allow full Android unrestricted mode.

OP-MAINT-004

Approved targets must be enforced by `ApprovedMaintenanceTargetController`.

OP-MAINT-005

Maintenance is blocked during recording, emergency, finalizing, DB/storage recovery, update/install unsafe state or policy recovery.

OP-MAINT-006

On timeout/resume/reboot/recovery, runtime must attempt policy restore.

## 7. Self Update Runtime

Current baseline uses DCAM Self Update / APK update as primary path.

Update requested / scheduled
    ↓
Check System Settings AutoUpdate preconditions
    ↓
Check Android Operation mode and State Machine guard
    ↓
Check kiosk policy state is safe
    ↓
Load manifest from approved artifact provider
    ↓
Download APK
    ↓
Validate package identity, checksum, signature, version and compatibility
    ↓
Install through approved package/update path
    ↓
After restart/resume, verify app version
    ↓
Verify policy and restore Lock Task
    ↓
Verify app-private serial_number and dcam_cloud_device_id still valid
    ↓
Sync SD Identity File if needed
Optional Play Store fallback is separate and only runs through Controlled Maintenance Mode if enabled.

Runtime rules:

Rule

Description

OP-UPD-001

Runtime must not assume Managed Google Play or Android Management API exists (per ADR). Policy-driven update: not applicable.

OP-UPD-002

Self Update is deferred during recording, emergency, finalization, DB/storage recovery, policy recovery or unsafe maintenance transition.

OP-UPD-003

Invalid package/checksum/signature/version is rejected and current app remains active if possible.

OP-UPD-004

Update failure must not leave device unrestricted.

OP-UPD-005

Play Store fallback requires Controlled Maintenance Mode, approved target, GMS/Play Store capability and approved maintenance/factory account process.

OP-UPD-006

Personal Google account update path is not supported for production maintenance.

## 8. Kiosk Policy Runtime Segment

Detailed policy rules belong to **DCAM Android Device Owner & Kiosk Policy Design**. Android Operation only orchestrates startup/recovery behavior.

DevicePolicyStateManager checks policy authority
    ↓
If DCAM Device Owner / DPC state is required and present:
        KioskPolicyManager verifies policy profile
        UserRestrictionPolicyManager applies/verifies restrictions
        HomeAppPolicyManager verifies launcher policy if required
        LockTaskController verifies allowlist
        continue startup
    ↓
If policy authority is missing:
        enter DEVICE_POLICY_REQUIRED or POLICY_DEGRADED
        log safe reason code
        block normal field operation if production profile requires kiosk
    ↓
If policy authority is present but partial policy failed:
        enter POLICY_DEGRADED or POLICY_RECOVERY_REQUIRED
        continue only if degradation is approved and recording safety is preserved
Rules:

Rule

Description

OP-KIOSK-001

Android Operation must verify required device policy state before normal field operation.

OP-KIOSK-002

DCAM must not assume it is Device Owner only because the APK is installed.

OP-KIOSK-003

Lock Task Mode must not start until package allowlist is verified.

OP-KIOSK-004

Lock Task start must wait until Activity/UI lifecycle is safe.

OP-KIOSK-005

User Restrictions apply/remove must not run during recording, emergency, finalization or unsafe recovery.

OP-KIOSK-006

Maintenance Mode transitions must be routed through approved controller and security guard.

OP-KIOSK-007

Policy failures must use safe reason codes and must not expose credentials or raw identifiers.

OP-KIOSK-008

Runtime must not depend on external EMM for current baseline.

## 9. Device Identity and Provisioning Runtime

Approved identity model:

Hardware Identity = serial_number
Cloud Identity = dcam_cloud_device_id
Recovery Cache = SD Identity File
Not used = ANDROID_ID, android_id_hash, device_lookup/{android_id_hash}
Identity restore:

Open dcam.db and read dcam_config.cson
    ↓
If local dcam_cloud_device_id and serial_number exist:
    use local identity
    validate/load local device information
    sync SD Identity File if available
    ↓
If local dcam_cloud_device_id is missing but serial_number exists:
    lookup Firebase/WebServer serial_lookup/{serial_number}
    ↓
    If found:
        fetch devices/{dcam_cloud_device_id}
        restore identity/device information/config metadata
        sync SD Identity File if available
    ↓
    If not found:
        enter PROVISIONING_REQUIRED or factory/admin provisioning flow
    ↓
If local serial_number is missing:
    wait for DSetup serial injection or approved provisioning/rework flow
Factory reset behavior:

Factory reset clears app-private identity.
DSetup runs again.
DSetup attempts to recover serial_number from SD Identity File.
If SD Identity File is valid, DSetup injects recovered serial_number.
If SD Identity File is missing/invalid, operator scans barcode and DSetup injects serial_number.
DCAM restores dcam_cloud_device_id through serial_lookup/{serial_number}.
SD Identity File runtime sync:

DCAM app-private serial_number is source of truth while app is running.
If SD Identity File is missing, DCAM recreates it.
If SD card is replaced, DCAM creates SD Identity File on the new card.
If SD Identity File differs from app-private serial_number, DCAM must not change app-private serial from SD; app-private serial wins.
Web Portal QR Flow = DCAM business provisioning. It does not make DCAM Device Owner.

Rules:

Rule

Description

OP-ID-001

Android must use `serial_number` as Hardware Identity / recovery key.

OP-ID-002

Android must use `dcam_cloud_device_id` as Cloud Identity / server primary device id after provisioning.

OP-ID-003

Android must not use `ANDROID_ID` as production identity.

OP-ID-004

Android must not compute/use `android_id_hash` for current production identity recovery.

OP-ID-005

Android must not use `device_lookup/{android_id_hash}` in current SOP baseline.

OP-ID-006

Android must not use `owner_name` or `manufacture_date` as Firebase/WebServer primary key.

OP-ID-007

Android must not use Advertising ID as primary key or recovery key.

OP-ID-008

If identity restore fails because server has no serial mapping, app enters `PROVISIONING_REQUIRED` or approved factory/admin recovery flow.

OP-ID-009

`manufacture_date` should use ISO format `YYYY-MM-DD`.

OP-ID-010

`bdma_decoder_profile_id` is not used; BDMA compatibility uses app/data/media/encoder contract metadata.

OP-ID-011

SD Identity File is recovery cache only and must not override app-private serial during normal operation.

## 10. Remote Config Runtime

Remote config identity, fetch/cache/apply flow and initial setting groups are defined in System Settings and Cloud Architecture. Exact field-level payload schema and field names remain TBD for future Remote Config/API implementation.

Identity resolved by dcam_cloud_device_id
    ↓
Startup fetch or periodic fetch or optional push wake-up
    ↓
Fetch effective config revision
    ↓
Validate schema/version/allowed fields/capability/policy authority if affected
    ↓
Store as pending_config in dcam.db
    ↓
Check runtime guard
    ↓
Apply if safe, otherwise defer
    ↓
Persist applied_config_revision and apply result
Do not apply config during:

recording active
emergency active
capture session active
post-record/finalizing active
DB recovery/migration active
storage recovery active
unsafe update/install state active
policy recovery active
maintenance transition active
Kiosk policy settings are requested policy. Actual Device Owner / Lock Task / User Restrictions apply behavior belongs to **DCAM Android Device Owner & Kiosk Policy Design**.

`dcam_config.cson` is updated only for device information fields. Operational settings, console settings, kiosk settings and update settings are stored in `dcam.db`.

## 11. Login and Recording Gate

Product/runtime policy:

Startup shows login screen when no valid same-boot session exists.
Session has no timeout.
Background/foreground does not logout.
Screen off/on does not logout.
Process kill/recreate can restore session if same boot and DB state is valid.
Device reboot invalidates previous session and requires login again.
Normal recording/capture evidence requires active operator session and required kiosk policy state.

StartRecordingRequested
    ↓
Check required kiosk policy state if production profile requires it
    ↓
Check active operator session
    ↓
If required policy is missing:
    reject or defer according to POLICY_REQUIRED behavior
    ↓
If active operator exists:
    continue Recording Precheck
    ↓
If no active operator and emergency recording:
    use EMERGENCY_OVERRIDE_ADMIN
    ↓
If no active operator and normal recording:
    reject OPERATOR_AUTH_REQUIRED
    show login
## 12. Runtime Modes

Mode

Meaning

`BOOTING`

App/device startup flow đang chạy.

`DEVICE_POLICY_REQUIRED`

Production profile requires Device Owner/DPC policy but policy state is missing/invalid.

`DEVICE_POLICY_APPLIED`

Required kiosk policy baseline has been verified/applied.

`LOCK_TASK_ACTIVE`

Lock Task Mode active.

`POLICY_DEGRADED`

Device policy partially applied or OEM unsupported behavior exists.

`POLICY_RECOVERY_REQUIRED`

Runtime must reconcile policy state before normal operation.

`MAINTENANCE_AUTH_REQUIRED`

Maintenance Password Gate required before maintenance transition.

`MAINTENANCE_MODE`

Authorized controlled maintenance mode active.

`UNPROVISIONED`

Local identity missing and server mapping not found.

`SERIAL_REQUIRED`

Local serial is missing; DSetup/provisioning is required.

`PROVISIONING_REQUIRED`

Serial-based Web Portal/Firebase business provisioning is required.

`LOGIN_REQUIRED`

Device active but no valid operator session.

`OPERATOR_AUTHENTICATED`

Valid operator session exists.

`READY`

Runtime ready for normal commands.

`RECORDING_ACTIVE`

Recording active.

`EMERGENCY_ACTIVE`

Emergency flow active.

`DEGRADED_OPERATION`

Optional feature degraded/disabled.

`SAFE_MODE`

Runtime restricted due to DB/storage/security/recovery/policy issue.

`UPDATING`

Self Update flow running when allowed.

`FATAL_ERROR`

Unrecoverable runtime error.

## 13. Foreground Service, Boot and Process Survival Direction

Core boundary rule:

ForegroundServiceHost keeps critical runtime alive.
RecordingController owns recording decisions.
StorageService owns file finalization.
DatabaseService owns persisted runtime state.
KioskPolicyManager owns policy orchestration.
SelfUpdateCoordinator owns update orchestration.
StateMachineCoordinator owns global guards.
Foreground service must start/continue for critical long-running operations such as recording, emergency recording, finalization and critical recovery when required by Android/device policy.

Service must not be stopped when recording, emergency, finalization or critical recovery is active.

Boot flow:

Device boot completed / app auto-start policy triggered
    ↓
BootReceiver forwards startup to AppRuntimeOrchestrator
    ↓
Initialize logging
    ↓
Verify required kiosk policy state
    ↓
Open DB and resolve identity by serial_number/dcam_cloud_device_id
    ↓
Sync SD Identity File if app-private serial_number exists
    ↓
Expire previous operator session because boot changed
    ↓
Run DB/storage/recording/policy/update recovery checks
    ↓
Run capability and eligibility evaluation
    ↓
Prepare console modules
    ↓
Start eligible system modules if policy allows
    ↓
Enter or restore Lock Task when UI lifecycle is ready
    ↓
Show login screen unless provisioning/fatal recovery is active
Rules:

Rule

Description

OP-FGS-BOOT-001

Device reboot invalidates previous operator session and requires login again.

OP-FGS-BOOT-002

DCAM must not assume recording automatically continues across reboot.

OP-FGS-BOOT-003

If DB indicates recording was active before reboot, RecoveryManager reconciles DB/files and marks recovery result.

OP-FGS-BOOT-004

System modules may start before login only when eligible, policy allows and recovery state is safe.

OP-FGS-BOOT-005

Boot startup must not run update/install before recovery and guard checks complete.

OP-FGS-BOOT-006

Boot startup must verify kiosk policy state if production profile requires it.

OP-FGS-BOOT-007

Lock Task recovery must run after UI lifecycle safe point.

OP-FGS-BOOT-008

If an update was interrupted, runtime must verify version/policy and enter update recovery or policy recovery.

## 14. Recovery Behavior

Scenario

Runtime Behavior

Device Owner / DPC state missing in production profile

Enter `DEVICE_POLICY_REQUIRED` or `POLICY_DEGRADED`; block normal field operation if required.

Lock Task unexpectedly inactive

Enter `LOCK_TASK_FAILED` / `POLICY_RECOVERY_REQUIRED`; re-enter Lock Task when UI lifecycle safe.

User restriction apply failed

Log safe reason code; enter `POLICY_DEGRADED` or block if restriction is required.

Maintenance Mode exit incomplete

Attempt policy restore; if restore fails, remain in policy recovery/degraded state.

Controlled target escaped/unavailable

Return to DCAM or block; do not leave unrestricted Android active.

Managed Google Play path requested

Mark not applicable; use Self Update or approved manual fallback.

Self Update interrupted/failed

Verify app version, preserve current state where possible and restore kiosk or enter policy recovery.

Play Store fallback unavailable

Hide/disable fallback or reject with safe reason.

`dcam.db` missing but app-private serial/source exists

Recreate DB and attempt identity restore by `serial_lookup/{serial_number}`.

`dcam_config.cson` missing

Restore device information from server if identity can resolve.

SD Identity File missing while app-private serial exists

Recreate SD Identity File from app-private serial.

SD card replaced while app-private serial exists

Create SD Identity File on new SD card.

SD Identity File conflicts with app-private serial

App-private serial wins; overwrite file or raise warning according to policy.

Local serial missing after factory reset

DSetup must recover serial from SD Identity File or barcode scan and inject serial again.

Serial mapping not found

Enter `PROVISIONING_REQUIRED` or factory/admin provisioning flow.

Server unavailable

Continue with last valid local identity/config if available; otherwise provisioning/identity restore waits.

DB corruption

Preserve corrupted DB if possible, enter safe recovery behavior.

Storage corruption/mismatch

Preserve evidence-like files and run storage recovery.

Active session after reboot

Expire and require login.

Process killed without reboot

Restore session if `device_boot_id` matches and DB/auth data valid.

Foreground service killed while recording/finalizing

Use persisted recording/service state to recover safely; do not start duplicate recording.

Foreground service cannot start

Reject/defer affected long-running operation, log reason and keep app in controlled state.

## 15. Open Questions / TBD

Item

Status

Exact foreground service type mapping by Android target SDK

TBD / Android POC

Exact foreground notification text and action buttons

TBD

BootReceiver permission/device policy requirements

TBD

Battery optimization exemption policy for BodyCamera deployment

TBD

Vendor firmware behavior when screen off/background

TBD / Device POC

Service restart strategy and retry limit values

TBD

OperationWatchdog heartbeat interval

TBD

Whether provisioning polling continues in foreground service

TBD

Whether GPS/sensor monitoring always requires service before login

TBD

Exact behavior when service cannot start but recording is requested

TBD

Runtime test matrix for service kill/process death/screen off/reboot

TBD

Exact Lock Task re-entry lifecycle point

TBD / Kiosk POC

Exact behavior when Device Owner state is missing in production build

TBD / Product + Security Review

Exact Self Update install mechanism on target BodyCamera firmware

TBD / Device POC

Exact Play Store fallback availability and account handling

TBD / Security + Product + Device POC

Exact SD Identity File sync schedule and validation behavior

TBD / Android + Security + QA

Exact behavior when SD card is absent/read-only/corrupt

TBD / Android + QA + Support

## 16. Practical Conclusion

Android Operation owns runtime startup orchestration.
Current device baseline per ADR: No external EMM / No Android Management API / No Managed Google Play.
DCAM-as-DPC / local Device Owner is preferred if target firmware supports it.
Required kiosk policy state is verified before normal field operation when production profile requires it.
Lock Task Mode is entered/recovered only after allowlist and UI lifecycle are safe.
User Restrictions and Home/Launcher policy are verified/applied through Kiosk Policy Design ownership.
Record / Live View is the default screen after login/session restore.
Setting is the in-app console hub.
Controlled Maintenance Mode requires Maintenance Password Gate and approved target enforcement.
Full Android unrestricted mode is not supported.
Primary update path is DCAM Self Update / APK update.
Manual Play Store update is optional controlled fallback only if device capability and approved process exist.
Device identity is restored through serial_number and dcam_cloud_device_id.
SD Identity File is recovery cache only; app-private serial_number is source of truth while DCAM is running.
Factory reset recovery requires DSetup to recover serial from SD Identity File or barcode scan and inject serial again.
Do not use ANDROID_ID, android_id_hash or device_lookup/{android_id_hash} in the current production baseline.
Web Portal QR Flow is DCAM business provisioning, not Device Owner setup.
Remote config identity/fetch/cache/apply baseline is defined; exact field-level payload schema remains TBD.
Login session has no timeout but reboot requires login again.
Normal recording requires active operator session and required kiosk policy state.
Emergency recording may use EMERGENCY_OVERRIDE_ADMIN.
System modules may run before login when safe and eligible.
Boot/process survival uses persisted DB/file/policy/update state, not UI memory state.