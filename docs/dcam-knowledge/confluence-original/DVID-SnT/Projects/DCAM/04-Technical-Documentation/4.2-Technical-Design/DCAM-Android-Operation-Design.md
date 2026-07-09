# DCAM Android Operation Design

**Page ID**: 48562239  
**Version**: 18  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48562239

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

Trang này follows **DCAM Factory Provisioning & Device Production SOP** làm chuẩn định danh thiết bị:

textwide760Trang này là authoritative cho Android runtime orchestration. Full Web Portal provisioning business flow, screens, QR direction, backend API direction, states và audit/error handling được định nghĩa trong **DCAM Device Provisioning Web Portal Design**.

Chi tiết Device Owner/DPC policy, Lock Task Mode, User Restrictions, Home/Launcher policy, Maintenance Mode và no-external-EMM baseline thuộc **DCAM Android Device Owner & Kiosk Policy Design**.

Chi tiết Record/Live View default, Setting hub, child modules, Maintenance Password Gate UX, Controlled Mode và optional Play Store fallback UX thuộc **DCAM In-App Operation, Device Settings & Media Console Design**.

Chi tiết APK artifact/download/validation/install thuộc **DCAM Self Update Design**.

## 2. Current Runtime Baseline

textwide760Runtime must not wait for or depend on EMM policy APIs.

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

textwide760System modules allowed before login:

textwide760Normal recording/capture evidence is blocked until operator session exists, except emergency override flow. If required production kiosk policy is missing, normal field operation must be blocked/degraded instead of silently continuing unrestricted.

## 5. Main Screen / Console Runtime

Runtime default screen after successful startup/login:

textwide760Back behavior:

textwide760Runtime rules:

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

textwide760Runtime rules:

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

textwide760Optional Play Store fallback is separate and only runs through Controlled Maintenance Mode if enabled.

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

textwide760Rules:

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

textwide760Identity restore:

textwide760Factory reset behavior:

textwide760SD Identity File runtime sync:

textwide760Web Portal QR Flow = DCAM business provisioning. It does not make DCAM Device Owner.

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

textwide760Do not apply config during:

textwide760Kiosk policy settings are requested policy. Actual Device Owner / Lock Task / User Restrictions apply behavior belongs to **DCAM Android Device Owner & Kiosk Policy Design**.

`dcam_config.cson` is updated only for device information fields. Operational settings, console settings, kiosk settings and update settings are stored in `dcam.db`.

## 11. Login and Recording Gate

Product/runtime policy:

textwide760Normal recording/capture evidence requires active operator session and required kiosk policy state.

textwide760## 12. Runtime Modes

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

textwide760Foreground service must start/continue for critical long-running operations such as recording, emergency recording, finalization and critical recovery when required by Android/device policy.

Service must not be stopped when recording, emergency, finalization or critical recovery is active.

Boot flow:

textwide760Rules:

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

textwide760