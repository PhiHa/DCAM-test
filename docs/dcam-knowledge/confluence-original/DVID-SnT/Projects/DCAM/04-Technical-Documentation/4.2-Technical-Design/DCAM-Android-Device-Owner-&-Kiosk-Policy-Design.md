# DCAM Android Device Owner & Kiosk Policy Design

**Page ID**: 49840280  
**Version**: 6  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/49840280

---


# DCAM Android Device Owner & Kiosk Policy Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design / Device Owner / Kiosk Policy Design

Version

Draft 0.6

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / Security Reviewer / QA Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, QA, Security Reviewer, Support, Factory/Admin Users

Last Updated

2026-07-09

Related Jira

Không có

Related Documents

DCAM Factory Provisioning & Device Production SOP, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, 10 - Android Device Operation Requirements, DCAM Android Operation Design, DCAM In-App Operation, Device Settings & Media Console Design, 03 - Android Platform & Compatibility Strategy, 09 - System Settings Requirements, DCAM Security & Encryption Design, DCAM Self Update Design, DCAM Web Portal & Device API Contract, DCAM Device Provisioning Web Portal Design, DCAM QA Test Strategy & Test Matrix, DCAM Device POC & Hardware Validation Report

## 1. Purpose

Tài liệu này là **source of truth** cho dedicated-device / kiosk policy của DCAM trên BodyCamera.

Tài liệu này định nghĩa cách DCAM production deployment sử dụng hoặc tích hợp với:

textCurrent baseline clarification:

textTài liệu này không định nghĩa lại DCAM business identity provisioning. Business provisioning cho `dcam_cloud_device_id`, `serial_number`, `owner_name`, `manufacture_date` và SD Identity File recovery-cache boundary thuộc **DCAM Factory Provisioning & Device Production SOP**, **DCAM Device Provisioning Web Portal Design** và **DCAM Web Portal & Device API Contract**.

## 2. Scope

### 2.1 In Scope

Area

Description

Dedicated-device deployment

DCAM chạy trên controlled BodyCamera devices như một kiosk/dedicated-device application.

DCAM-as-DPC / local Device Owner model

Định nghĩa DCAM-owned Device Owner/DPC-capable policy behavior nếu target firmware cho phép.

Lock Task Mode

Định nghĩa app allowlist, start/stop behavior, recovery và runtime expectations.

User Restrictions

Định nghĩa approved restriction profile để kiosk hardening.

Home / Launcher policy

Định nghĩa khi nào DCAM nên là preferred Home/Launcher.

Admin / Maintenance Mode

Định nghĩa controlled support path để thoát restricted runtime một cách an toàn.

Maintenance Password Gate

Định nghĩa policy requirement rằng Enter Maintenance Mode / Exit Kiosk temporarily phải được bảo vệ bằng password/maintenance credential.

Controlled Mode only

Temporary kiosk exit chỉ mở approved apps/settings screens; không có full Android unrestricted mode.

Policy recovery

Định nghĩa behavior sau reboot, crash, app update, policy failure hoặc missing Device Owner state.

Audit and diagnostics

Định nghĩa safe logs/reason codes cho policy apply/remove/failure/maintenance entry.

POC and QA direction

Định nghĩa các điểm phải validate trên real BodyCamera hardware/OEM firmware.

### 2.2 Out of Scope

Area

Managed In / Direction

External EMM / Android Management API / Managed Google Play

Not applicable (per ADR).

External DPC/EMM ownership model

Not applicable for current baseline (per ADR). Future only if deployment model changes.

DSetup factory provisioning, serial recovery from SD Identity File, barcode scan and serial injection

DCAM Factory Provisioning & Device Production SOP

DCAM business provisioning, Web Portal screens, QR business flow and backend provisioning API

DCAM Device Provisioning Web Portal Design + DCAM Web Portal & Device API Contract

In-app console UI, Setting hub, Back navigation, Login Settings, User Settings, Maintenance Password Gate UX and optional Play Store manual fallback UX

DCAM In-App Operation, Device Settings & Media Console Design

Device identity and CSON scope

04 - Device Configuration Requirements

Remote config payload fields

09 - System Settings Requirements / future Remote Config design

Android startup/session/recording orchestration

DCAM Android Operation Design

Recording/capture state machine

DCAM Recording & Capture Design

Security constraints, credential storage and maintenance credential protection

DCAM Security & Encryption Design

Self update artifact/download/install detail

DCAM Self Update Design

QA master matrix

DCAM QA Test Strategy & Test Matrix

## 3. Core Decision

textDCAM không được chỉ dựa vào fullscreen Activity flags, immersive mode hoặc Home/Launcher behavior để kiểm soát production kiosk.

## 4. Provisioning Boundary

Có hai lớp setup/provisioning tách biệt.

Layer

Purpose

Source of Truth

Device Owner / DPC setup

Làm cho DCAM hoặc approved local DPC component có khả năng enforce Device Owner policy trên BodyCamera nếu được hỗ trợ.

This document + Factory SOP + Device POC

DSetup factory provisioning

Install/update approved APK, set/verify Device Owner when allowed, recover/scan serial and inject serial into DCAM.

DCAM Factory Provisioning & Device Production SOP

DCAM business provisioning

Đăng ký/restore device identity và device information vào DCAM backend/WebServer/Firebase using `serial_number`.

DCAM Device Provisioning Web Portal Design + DCAM Web Portal & Device API Contract

Important rule:

textAccepted setup directions for current baseline:

Method

Usage Direction

Status

DSetup + ADB `dpm set-device-owner`

Current factory baseline for Device Owner setup after clean/factory-reset state.

Approved Factory Baseline

DCAM-as-DPC / local Device Owner provisioning

Preferred current direction nếu firmware/factory process hỗ trợ.

POC Required

QR/NFC/factory provisioning for DCAM DPC

Được phép nếu selected device firmware hỗ trợ mà không cần external EMM.

POC Required

External EMM / Android Management API / Managed Google Play

Not applicable for current baseline (per ADR).

Not Applicable

Normal APK install only

Không đủ để trở thành Device Owner.

Not Allowed as Production Assumption

## 5. DPC Ownership Model

Current production direction:

text
Model

Current Direction

Impact

DCAM-as-DPC / local Device Owner

Primary direction cho current baseline.

DCAM own policy enforcement, security review, maintenance flow và policy recovery.

External DPC / EMM owns policy

Not applicable (per ADR).

N/A

Hybrid EMM model

Chỉ là future nếu deployment model thay đổi.

Cần ADR/update tài liệu này và QA matrix.

## 6. Runtime Components

Component

Responsibility

`DevicePolicyStateManager`

Detect Device Owner/DPC state, policy authority và policy health.

`KioskPolicyManager`

Own high-level kiosk policy apply/verify/clear orchestration.

`LockTaskController`

Manage Lock Task allowlist, start/stop behavior và recovery.

`UserRestrictionPolicyManager`

Apply approved User Restrictions khi có policy authority.

`HomeAppPolicyManager`

Configure hoặc verify preferred Home/Launcher policy nếu supported và required.

`MaintenanceModeController`

Control authorized temporary exit/degrade path cho support.

`MaintenanceAccessController`

Validate Admin/Maintenance role và Maintenance Password Gate trước temporary kiosk exit.

`ApprovedMaintenanceTargetController`

Chỉ allow approved apps/settings screens trong Controlled Mode.

`PolicyAuditLogger`

Emit safe policy events, reason codes và failure diagnostics.

Boundary rule:

text## 7. Startup and Policy Verification Flow

Android Operation owns full startup orchestration. Tài liệu này owns policy segment.

textRules:

Rule

Description

KIOSK-BOOT-001

DCAM phải verify policy state khi startup và sau reboot.

KIOSK-BOOT-002

DCAM không được giả định Device Owner state chỉ vì APK đã được install.

KIOSK-BOOT-003

Lock Task Mode chỉ được enter sau khi allowlist được verify.

KIOSK-BOOT-004

Nếu required policy state bị thiếu trong production deployment, normal field operation phải vào controlled degraded/policy-required state.

KIOSK-BOOT-005

Device identity restore và operator login không được silently override missing kiosk policy.

KIOSK-BOOT-006

External EMM/Managed Google Play policy không được required để current startup success.

## 8. Lock Task Mode Policy

### 8.1 Lock Task Allowlist

Minimum allowlist direction:

Package

Direction

DCAM app package

Required.

DCAM DPC/admin receiver package/component

Required nếu tách khỏi app package; nếu không, DCAM package owns policy.

Android Settings

Không allowlisted cho normal field operation; chỉ có thể mở approved screens trong Controlled Maintenance Mode.

Google Play Store

Optional fallback chỉ khi device có GMS/Play Store và manual update được approved; không thuộc normal field allowlist.

Package installer/update helper

Chỉ dùng nếu required bởi approved Self Update path và chỉ trong safe update/maintenance window.

Remote support app

Optional; chỉ dùng nếu được Security/Support policy approve.

Rules:

Rule

Description

KIOSK-LT-001

Chỉ approved packages mới được allowlisted.

KIOSK-LT-002

Normal field operation nên chỉ allow DCAM và required DPC/runtime packages.

KIOSK-LT-003

Maintenance Mode có thể tạm thời mở rộng allowlist cho support/update actions sau khi Maintenance Password Gate success.

KIOSK-LT-004

Allowlist change phải auditable.

KIOSK-LT-005

Non-allowlisted app launch attempts phải bị blocked hoặc fail closed.

KIOSK-LT-006

Controlled Mode không được trở thành unrestricted launcher/app drawer access.

### 8.2 Lock Task Entry and Exit

Scenario

Expected Behavior

Normal startup

Enter Lock Task sau policy verification và UI readiness.

Reboot

Re-verify policy và re-enter Lock Task.

Crash/process recreation

Reconcile policy và re-enter Lock Task khi safe.

Recording active

Không exit Lock Task.

Emergency active

Không exit Lock Task.

Maintenance request

Chỉ exit hoặc relax Lock Task qua authorized Maintenance Mode sau khi Maintenance Password Gate success.

Self Update install

Dùng policy-safe update path; không để device unrestricted.

Manual Play Store fallback

Chỉ allowed như approved maintenance target nếu enabled; restore kiosk sau action.

## 9. User Restrictions Profile

Approved baseline profile phải được validate trên target Android versions và BodyCamera firmware.

Restriction

Direction

`DISALLOW_FACTORY_RESET`

Apply trong production kiosk mode nếu supported.

`DISALLOW_SAFE_BOOT`

Apply trong production kiosk mode nếu supported.

`DISALLOW_ADD_USER`

Apply để ngăn user/profile changes.

`DISALLOW_REMOVE_USER`

Apply khi relevant.

`DISALLOW_MOUNT_PHYSICAL_MEDIA`

Conditional; phải align với SD Identity File và BDMA/support flow. Không được phá approved SD recovery cache nếu feature required.

`DISALLOW_INSTALL_UNKNOWN_SOURCES`

Apply để ngăn unapproved APK install paths. Phải reconcile với Self Update install mechanism.

`DISALLOW_UNINSTALL_APPS`

Apply để bảo vệ DCAM/DPC packages.

`DISALLOW_APPS_CONTROL`

Apply để ngăn user app management.

`DISALLOW_CONFIG_WIFI`

Conditional; có thể relax trong Controlled Maintenance Mode nếu field Wi-Fi changes required.

`DISALLOW_CONFIG_BLUETOOTH`

Conditional; apply nếu Bluetooth không required.

`DISALLOW_CONFIG_LOCATION`

Conditional; không được làm hỏng approved GPS/location feature setup.

`DISALLOW_USB_FILE_TRANSFER`

Conditional; phải align với BDMA ADB/media import boundary.

`DISALLOW_DEBUGGING_FEATURES`

Conditional; production nên block user/debug tampering, còn factory/POC có thể cần debugging.

`DISALLOW_SYSTEM_ERROR_DIALOGS`

Conditional; có thể dùng để tránh user escape surfaces, nhưng không được che support-critical failures nếu không có logs.

Rules:

Rule

Description

KIOSK-UR-001

Restriction profile phải explicit và versioned.

KIOSK-UR-002

Restrictions không được làm hỏng core recording, storage finalization, emergency evidence hoặc BDMA import boundary.

KIOSK-UR-003

Maintenance Mode chỉ được tạm relax selected restrictions sau khi Maintenance Password Gate success và phải có audit.

KIOSK-UR-004

Restriction apply/remove/failure phải được log bằng safe reason codes.

KIOSK-UR-005

OEM-specific unsupported restrictions phải được report như capability/policy limitation, không được silently ignore.

KIOSK-UR-006

Restrictions phải được restore sau maintenance timeout, exit, crash, app resume hoặc reboot recovery khi có thể.

KIOSK-UR-007

If SD Identity File recovery cache is required, policy must not prevent DCAM from syncing approved identity cache to SD card.

## 10. Home / Launcher Policy

DCAM có thể được configure làm preferred Home/Launcher app cho production devices.

Scenario

Direction

Production field operation

DCAM nên là preferred Home/Launcher nếu device policy support exists.

DCAM-as-DPC deployment

DCAM policy manager có thể own persistent preferred activity configuration nếu API/firmware hỗ trợ.

Maintenance Mode

Home/Launcher policy có thể vẫn active; support actions nên dùng authorized controlled flow.

Unsupported OEM behavior

Device POC phải document fallback behavior.

Rules:

Rule

Description

KIOSK-HOME-001

Home/Launcher policy không thay thế Lock Task Mode.

KIOSK-HOME-002

Home/Launcher policy phải được verify sau boot/update nếu required.

KIOSK-HOME-003

Failure khi configure Home policy phải enter controlled policy-degraded state trong production profile.

## 11. Admin / Maintenance Mode

Maintenance Mode là controlled mode; không phải full Android unrestricted mode.

Allowed purposes:

textMaintenance entry is protected:

textEntry methods direction:

Method

Direction

Maintenance password

Required baseline cho Enter Maintenance Mode / Exit Kiosk temporarily.

Admin PIN/password

Có thể identify Admin user, nhưng normal admin login alone không đủ để exit kiosk.

Physical key sequence

Chỉ là optional trigger; không được bypass Maintenance Password Gate.

ADB/developer path

Chỉ dùng cho test/factory, không phải normal production support assumption.

Server/portal command

Future only; không được assume EMM.

Rules:

Rule

Description

KIOSK-MAINT-001

Maintenance Mode phải explicitly authorized.

KIOSK-MAINT-002

Maintenance Mode entry/exit phải auditable.

KIOSK-MAINT-003

Maintenance Mode không được interrupt active recording, emergency hoặc finalization trừ khi policy định nghĩa safe stop rõ ràng.

KIOSK-MAINT-004

Maintenance Mode phải restore production restrictions trước khi quay lại field operation.

KIOSK-MAINT-005

Maintenance credential không được hardcoded hoặc logged.

KIOSK-MAINT-006

Enter Maintenance Mode / Exit Kiosk temporarily yêu cầu Maintenance Password Gate.

KIOSK-MAINT-007

Emergency override không được satisfy Maintenance Password Gate.

KIOSK-MAINT-008

Failed maintenance password attempts không được change Lock Task/User Restrictions state.

KIOSK-MAINT-009

Maintenance session phải timeout hoặc restore kiosk policy khi session ends, app resumes, reboots hoặc recovers.

KIOSK-MAINT-010

Full Android unrestricted mode is not supported.

KIOSK-MAINT-011

Controlled Mode chỉ được mở approved apps/settings screens.

## 12. Remote Config / Policy Settings Boundary

System Settings owns setting requirements. Tài liệu này owns kiosk policy apply behavior.

Remote/admin config có thể request:

textApply rule:

textPolicy changes phải defer khi unsafe:

text## 13. Update Boundary

Current update baseline (per ADR):

textRules:

Rule

Description

KIOSK-UPD-001

Update phải preserve package identity required bởi Device Owner/DPC policy.

KIOSK-UPD-002

Update không được remove DCAM khỏi Lock Task allowlist trước safe transition.

KIOSK-UPD-003

Update không được để device unrestricted nếu install fails.

KIOSK-UPD-004

Device policy state phải safe trước install.

KIOSK-UPD-005

Sau update/restart, DCAM phải verify policy và re-enter Lock Task.

KIOSK-UPD-006

Update/maintenance flows không được bypass Maintenance Password Gate nếu temporary kiosk exit required.

KIOSK-UPD-007

Self Update install flow details vẫn nằm trong DCAM Self Update Design.

KIOSK-UPD-008

Play Store fallback không được dùng để unapproved app browsing/installing.

## 14. Security and Audit

Required policy events:

text
[POLICY] User restriction applied: 
[POLICY] User restriction unsupported: 
[POLICY] Maintenance mode requested
[POLICY] Maintenance gate success
[POLICY] Maintenance gate failed: 
[POLICY] Maintenance gate locked/cooldown: 
[POLICY] Maintenance mode entered
[POLICY] Maintenance mode exited
[POLICY] Maintenance target blocked: 
[POLICY] Maintenance policy restore failed: 
[POLICY] Policy restored after reboot/update]]>Forbidden log content:

textSecurity constraints cho credentials, identifiers và sensitive logging vẫn thuộc **DCAM Security & Encryption Design**.

## 15. Policy Runtime States

Các states này có thể được Android Operation và State Machine designs consume.

State

Meaning

`DEVICE_POLICY_UNKNOWN`

Policy state chưa được check.

`DEVICE_POLICY_REQUIRED`

Production profile yêu cầu Device Owner/DPC policy nhưng state bị thiếu hoặc invalid.

`DEVICE_POLICY_APPLIED`

Required Device Owner/DPC policy và baseline restrictions đã được verify.

`LOCK_TASK_READY`

DCAM package đã allowlisted và có thể enter Lock Task.

`LOCK_TASK_ACTIVE`

Lock Task Mode đang active.

`LOCK_TASK_FAILED`

Lock Task không thể start hoặc bị exited ngoài dự kiến.

`MAINTENANCE_AUTH_REQUIRED`

Maintenance Password Gate required trước temporary kiosk exit.

`MAINTENANCE_AUTH_FAILED`

Maintenance credential validation failed hoặc đang locked/cooling down.

`MAINTENANCE_MODE`

Authorized controlled maintenance/support mode đang active.

`POLICY_DEGRADED`

Device policy chỉ partial applied hoặc có OEM unsupported behavior.

`POLICY_RECOVERY_REQUIRED`

Policy state phải reconcile trước normal field operation.

## 16. POC / Hardware Validation Requirements

Device POC phải validate tối thiểu:

Test Area

Expected Evidence

DCAM-as-DPC / Device Owner setup

Confirm selected setup method hoạt động trên target BodyCamera firmware, không cần external EMM.

DSetup factory setup

Confirm factory-reset/clean device can be provisioned by DSetup and Device Owner can be verified.

Lock Task start/recovery

Confirm DCAM enter và restore kiosk mode sau boot/crash/update.

Home/Recents/Back behavior

Confirm user không thể rời DCAM ngoài dự kiến.

User Restrictions

Confirm factory reset, safe boot, app uninstall, app control và external media restrictions.

SD Identity File boundary

Confirm restriction profile does not break approved DCAM SD Identity File sync/recovery if feature is enabled.

Controlled Maintenance Mode

Confirm chỉ approved targets có thể mở; không có full Android unrestricted mode.

Maintenance Password Gate

Confirm kiosk exit bị block nếu không có valid maintenance credential và được log an toàn.

Self Update

Confirm APK update path preserve policy và return to kiosk.

Optional Play Store fallback

Chỉ confirm nếu device có GMS/Play Store và approved process tồn tại.

BDMA/ADB boundary

Confirm restrictions không làm hỏng approved BDMA import/user sync process.

OEM fallback behavior

Document unsupported APIs, firmware quirks và fallback policy.

## 17. QA Acceptance Checklist

Test Case

Expected Result

Production device boots

DCAM verify Device Owner/DPC state và enter Lock Task.

Device Owner missing

DCAM enter controlled policy-required/degraded state.

Home button pressed

User vẫn ở approved kiosk experience.

Recents button pressed

Behavior tuân theo lock task features/profile.

Unapproved app launch attempted

Launch bị blocked hoặc fail closed.

Factory reset attempted from Settings

Bị blocked nếu restriction enabled.

Safe boot attempted

Bị blocked nếu restriction enabled và supported.

App crash

DCAM recover và re-enter kiosk hoặc policy recovery.

Self Update completed

DCAM verify policy và re-enter Lock Task.

Maintenance requested without password

Access bị denied; Lock Task/restrictions vẫn active; failure được audit.

Maintenance requested with valid password

Maintenance Mode chỉ enter khi runtime guard safe; có audit; chỉ approved targets mở được.

Repeated failed maintenance password attempts

Rate limit/cooldown/lockout được apply.

Maintenance entered/exited

Có audit và production restrictions được restore.

Unrestricted Android attempt

Unrestricted launcher/app drawer/settings access không available.

Managed Google Play assumption

Marked not applicable cho current baseline.

Restriction unsupported by OEM

Logged và reflected như policy/capability limitation.

## 18. Open Questions / TBD

Item

Status

Exact DCAM-as-DPC / Device Owner setup process for factory production

TBD / Device POC

Exact Lock Task feature flags

TBD

Exact package allowlist

TBD

Exact User Restrictions profile by Android version/OEM

TBD / Device POC

Maintenance Mode entry method and credential policy

TBD / Security Review

Maintenance credential length/complexity/rotation/reset policy

TBD / Security + Product

Failed maintenance attempt lockout/cooldown policy

TBD / Security + Product

Maintenance session timeout and auto-restore policy

TBD / Security + Product

Exact approved Android Settings targets

TBD / Product + Security + Device POC

Exact approved maintenance/support app list

TBD / Product + Security + Device POC

Whether Play Store exists on selected BodyCamera firmware

TBD / Device POC

Whether manual Play Store fallback is allowed

TBD / Security + Product

Silent install mechanism under DCAM-as-DPC policy

TBD / Self Update POC

Interaction with BDMA ADB import under production restrictions

TBD / Device POC

Interaction with SD Identity File sync under external media restrictions

TBD / Device POC + Security

Support process for lost policy / broken kiosk state

TBD

## 19. Practical Conclusion

text