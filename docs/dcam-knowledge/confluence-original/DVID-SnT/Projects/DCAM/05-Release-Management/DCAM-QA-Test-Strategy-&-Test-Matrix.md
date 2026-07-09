# DCAM QA Test Strategy & Test Matrix

**Page ID**: 49545345  
**Version**: 8  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/49545345

---


# DCAM QA Test Strategy & Test Matrix

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

QA Strategy / Test Matrix

Version

Approved 1.6

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / QA Lead / BDMA Lead / Security Reviewer / Cloud Lead / Android Lead / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

05 - Release Management

Target Audience

PM/BA, Tech Lead, Android Developers, QA, BDMA Team, Cloud/WebServer Team, Security Reviewer, Factory, Support, Stakeholders

Last Updated

2026-07-09

Related Jira

Không có

Related Documents

DCAM Requirements Home, DCAM Non-functional Requirements, DCAM-BDMA Data Contract, DCAM Web Portal & Device API Contract, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM State Machine Design, DCAM Device Capability & Feature Eligibility Design, DCAM Device Provisioning Web Portal Design, 09 - System Settings Requirements, DCAM Self Update Design, DCAM Android Development Standard, DCAM Device POC & Hardware Validation Report, DCAM Factory Provisioning & Device Production SOP

## 1. Purpose

Tài liệu này định nghĩa QA strategy và test matrix cho **DCAM Android BodyCamera Application**.

Mục tiêu:

Xác định phạm vi test cho DCAM MVP.

Liên kết test coverage với Requirements, Architecture, Technical Design, API Contract, Data Contract, Device POC và Factory SOP.

Định nghĩa test level, test environment, test device matrix và exit criteria.

Chuẩn hóa cách QA kiểm tra các luồng critical như recording, storage, user login, emergency override, BDMA import, provisioning, dedicated-device/kiosk policy, in-app operation/device/media console, controlled maintenance, update, recovery, security và factory ready-to-ship flow.

Xác nhận current baseline: **không external EMM / Android Management API / Managed Google Play policy-driven update**, **Self Update / APK update là primary path**, **Play Store manual fallback là optional controlled maintenance flow**.

## 2. Scope

Area

In Scope

Android Runtime

Kiểm tra startup, boot, foreground service, session restore, safe mode, policy verification, console readiness và recovery.

Kiosk Policy

Kiểm tra DCAM-as-DPC/local Device Owner, Lock Task Mode, User Restrictions, Home/Launcher behavior, Maintenance Mode, Maintenance Password Gate và policy recovery.

No External EMM Baseline

Kiểm tra test/implementation không phụ thuộc external EMM, Android Management API hoặc Managed Google Play policy-driven update.

In-app Operation / Device / Media Console

Kiểm tra Record/Live View default screen, Setting hub, Back behavior, App Operation Settings, Device/System Settings proxy, Login Settings, Admin-only User Settings, Storage Dashboard, read-only File/Storage Manager, read-only Media Viewer và future placeholders.

Controlled Maintenance

Kiểm tra Exit Kiosk temporarily chỉ đi qua Admin/Maintenance + Maintenance Password Gate; chỉ mở approved targets; không có full unrestricted Android.

Update

Kiểm tra DCAM Self Update / APK update primary path, package validation, defer logic, policy-safe update recovery và optional manual Play Store fallback nếu được enable.

Factory Production SOP

Kiểm tra factory procedure từ raw/factory-reset BodyCamera đến quyết định READY_TO_SHIP / QUARANTINED.

User / Auth

Kiểm tra Login, no-timeout session, reboot login, Login Settings, User Settings và emergency override.

Recording / Capture

Kiểm tra normal recording, emergency recording, image/audio capture và operator attribution.

Storage

Kiểm tra internal/external storage, temp/final, `BDMA_READY`, low storage, read-only File/Media và recovery.

SQLite

Kiểm tra user/auth/session/media/config state, console settings, optional policy snapshot, update audit, migration, recovery và write-back.

BDMA Integration

Kiểm tra ADB discovery, media import, `.mp4` MD5 verification, user sync và cleanup.

Provisioning

Kiểm tra Web Portal QR Flow, `serial_lookup/{serial_number}`, identity restore, provisioning state, `dcam_cloud_device_id` assignment và serial assignment/rebind.

Remote Config

Kiểm tra fetch/cache/apply/defer/reject behavior, bao gồm kiosk requested-policy, console setting và update setting defer/reject.

Security

Kiểm tra sensitive logging, credential handling, identity protection, Maintenance Password Gate, Google account/manual update constraints, admin console audit và encryption direction.

Device POC Feedback

Kiểm tra POC outcomes đã được phản ánh trong test plan, release decision và Factory SOP.

## 3. Out of Scope

Area

Reason

Final production security certification

Cần Security Review riêng.

Full BDMA UI test

Thuộc BDMA QA scope; tài liệu này chỉ cover DCAM-facing integration.

Cloud provider production SLA

Thuộc Cloud/WebServer operation.

Final hardware certification

Thuộc Device POC / Hardware Validation Report.

External EMM / Android Management API / Managed Google Play policy-driven update

Không áp dụng cho current device baseline.

Future Live Stream / PTT / AI Mode full behavior

Future requirements/designs chưa approved.

## 4. Authoritative References

Test Area

Source of Truth

Functional Requirements

DCAM Requirements Home

Data Contract

DCAM-BDMA Data Contract

API Contract

DCAM Web Portal & Device API Contract

Android Runtime

DCAM Android Operation Design

Kiosk Policy

DCAM Android Device Owner & Kiosk Policy Design

In-app Console

DCAM In-App Operation, Device Settings & Media Console Design

Dedicated-device Decision

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Recording

DCAM Recording & Capture Design

Storage

DCAM Storage Design

SQLite

DCAM SQLite Database Design

Security

DCAM Security & Encryption Design

State Guards

DCAM State Machine Design

Feature Eligibility

DCAM Device Capability & Feature Eligibility Design

Provisioning

DCAM Device Provisioning Web Portal Design

Remote Config / AutoUpdate

09 - System Settings Requirements

Self Update

DCAM Self Update Design

Development Standard

DCAM Android Development Standard

NFR

DCAM Non-functional Requirements

Device POC Evidence

DCAM Device POC & Hardware Validation Report

Factory Acceptance / Ready-to-Ship

DCAM Factory Provisioning & Device Production SOP

## 5. Test Strategy

Test Level

Purpose

Owner

Unit Test

Kiểm tra domain logic, validators, state transitions, config validation, console setting validation, maintenance gate validation, update validation, policy validation và eligibility rules.

Android Dev

Integration Test

Kiểm tra module boundary: DB, StorageService, RecordingController, OperatorSessionManager, KioskPolicyManager, ConsoleSettingCoordinator, SelfUpdateManager.

Android Dev / QA

Device Test

Kiểm tra trên BodyCamera thật: camera, storage, foreground service, reboot, screen off, Lock Task, User Restrictions, in-app console, Self Update và ADB.

QA / Android Dev

Contract Test

Kiểm tra Data Contract, API Contract, file naming, MD5, DB schema, media status và BDMA read/write behavior.

QA / BDMA Team / Backend Team

Kiosk Policy Test

Kiểm tra DCAM-as-DPC/Device Owner feasibility, Lock Task, Home/Launcher, Maintenance Mode và restriction behavior.

QA / Android Dev / Security Reviewer

In-app Console Test

Kiểm tra các settings/media/storage/admin actions được phép khi Android Settings/File Manager bị hạn chế.

QA / Android Dev / Security Reviewer

Update Test

Kiểm tra Self Update/APK primary path, optional Play Store fallback nếu được enable, và không giả định Managed Google Play.

QA / Android Dev / Security Reviewer

Factory SOP Test

Kiểm tra raw/factory-reset BodyCamera có thể được chuẩn bị theo SOP và được đánh dấu đúng READY_TO_SHIP hoặc QUARANTINED.

QA / Factory / Android Dev

Recovery Test

Kiểm tra crash, reboot, service kill, DB lock/corruption, policy failure, console setting failure và interrupted finalization/update.

QA

Security Test

Kiểm tra sensitive logging, credential handling, identity protection, Maintenance Password Gate, Google account/manual update constraints và invalid config/package.

Security Reviewer / QA

Regression Test

Chạy trước mỗi release candidate.

QA

Acceptance Test

Kiểm tra MVP flow end-to-end, bao gồm factory-ready-to-ship path nếu release có shipment thiết bị.

PM / QA / Factory / Stakeholders

## 6. Test Environment Matrix

Environment

Purpose

Required

Android Emulator

Chỉ dùng cho early UI/domain test.

Optional

Android Phone

Dùng cho development smoke test.

Optional

BodyCamera Device - Model A

Thiết bị target chính để test.

Required

BodyCamera Device - Model B

Test compatibility.

Recommended

Non-GMS Device

Kiểm tra core app không phụ thuộc cứng vào GMS/Play Store.

Required nếu target deployment bao gồm non-GMS

GMS / Play Store Device

Kiểm tra optional manual Play Store fallback chỉ khi thiết bị hỗ trợ.

Conditional

DCAM-as-DPC / Device Owner Test Device

Kiểm tra local Device Owner/DPC/kiosk behavior without external EMM.

Required cho production kiosk profile

Factory-reset Device

Kiểm tra SOP từ raw/clean device đến ready-to-ship state.

Required trước pilot/production shipment

Windows PC with BDMA

Kiểm tra ADB import/user sync.

Required

Web Portal Test Backend

Kiểm tra provisioning và remote config.

Required

Artifact Provider Test Backend

Kiểm tra Self Update manifest/APK download.

Required

Offline Environment

Kiểm tra offline-first, local login, recording và BDMA readiness.

Required

Maintenance / Factory Test Environment

Kiểm tra Maintenance Mode, gate, policy relaxation và restore.

Required cho kiosk profile

## 7. Master Test Matrix

Test ID

Area

Scenario

Priority

Source Document

Status

QA-BOOT-001

Android Operation

Lần launch app đầu tiên mở DB, resolve identity và đi tới login/provisioning state.

P0

DCAM Android Operation Design

Draft

QA-KIOSK-001

Kiosk Policy

Production device boot với DCAM-as-DPC / Device Owner state bắt buộc và verify policy.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-002

Kiosk Policy

Khi Device Owner state bắt buộc bị thiếu, app vào `DEVICE_POLICY_REQUIRED` hoặc approved degraded state.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-003

Lock Task

DCAM package được allowlisted và enter Lock Task Mode sau startup.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-004

Lock Task

Home/Recents/Back behavior không cho user thoát khỏi approved kiosk experience.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-005

User Restrictions

Factory reset, app uninstall/app control và safe boot restrictions được apply hoặc unsupported reason được log.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-006

Maintenance Mode

Maintenance Mode được authorized có thể enter/exit, có audit và production restrictions được restore.

P0

Kiosk Policy + Security + In-App Console

Draft

QA-KIOSK-007

Policy Recovery

Khi app crash/process kill trong lúc Lock Task required, app recover và re-enter Lock Task hoặc policy recovery.

P0

Android Operation + Kiosk Policy

Draft

QA-KIOSK-008

No External EMM

Device/profile không phụ thuộc external EMM, Android Management API hoặc Managed Google Play để pass release baseline.

P0

Kiosk Policy + Self Update

Draft

QA-CONSOLE-001

In-app Console

Kiosk user không thể thoát DCAM nhưng vẫn truy cập được các in-app console functions được phép.

P0

In-App Console

Draft

QA-CONSOLE-002

Navigation

App mở `Record / Live View` mặc định; Back chuyển sang Setting và Back ở Setting quay lại Record.

P0

In-App Console

Draft

QA-CONSOLE-003

Navigation

Back từ mọi child module quay lại Setting và không bao giờ exit DCAM.

P0

In-App Console

Draft

QA-CONSOLE-004

App Operation Settings

Admin đổi video resolution khi app idle; setting được validate, persist và apply cho lần recording kế tiếp.

P0

In-App Console + System Settings + Recording Design

Draft

QA-CONSOLE-005

App Operation Settings

Admin đổi video resolution/pre-record khi đang recording; thay đổi bị defer/reject và recording hiện tại không bị gián đoạn.

P0

In-App Console + State Machine

Draft

QA-CONSOLE-006

Device/System Settings

USB/Wi-Fi/GPS control yêu cầu Admin/Maintenance authorization và không expose unrestricted Android Settings.

P0

In-App Console + Kiosk Policy Design

Draft

QA-CONSOLE-007

File/Media Manager

File Manager và Media Viewer là read-only: không delete, edit, mark important, export hoặc share.

P0

In-App Console + Security + Storage

Draft

QA-CONSOLE-008

Future Features

Server/Live/PTT/AI settings bị hidden/disabled cho đến khi có future design được approved.

P1

In-App Console

Draft

QA-CONSOLE-009

Emergency Settings

Emergency Settings placeholder không làm thay đổi emergency behavior cho đến khi được approved.

P1

In-App Console + Recording Design

Draft

QA-CONSOLE-010

Maintenance Password

Enter Maintenance Mode yêu cầu Maintenance Password Gate.

P0

In-App Console + Security

Draft

QA-CONSOLE-011

Controlled Mode

Controlled Maintenance chỉ mở approved apps/settings screens; không có full Android unrestricted mode.

P0

In-App Console + Kiosk Policy

Draft

QA-CONSOLE-012

Unapproved Target

Thử mở unapproved app/settings area bị block hoặc fail closed.

P0

In-App Console + Kiosk Policy

Draft

QA-CONSOLE-013

User Settings

Operator không thể mở User Settings; Admin có thể add/edit/delete-disable với audit và historical attribution được preserve.

P0

In-App Console + SQLite + Security

Draft

QA-PROV-001

Provisioning

New device chưa có local identity và không restore được bằng `serial_lookup/{serial_number}` sẽ vào `PROVISIONING_REQUIRED`.

P0

Device Provisioning Web Portal Design + API Contract

Draft

QA-PROV-002

API Contract

Android/Web Portal dùng `serial_number` để restore qua `serial_lookup/{serial_number}`; không dùng `ANDROID_ID`, `android_id_hash` hoặc `device_lookup/{android_id_hash}`.

P0

DCAM Web Portal & Device API Contract

Draft

QA-PROV-003

API Contract

Web Portal/factory provisioning tạo hoặc restore `devices/{dcam_cloud_device_id}` + `serial_lookup/{serial_number}` mapping.

P0

DCAM Web Portal & Device API Contract

Draft

QA-AUTH-001

User/Auth

Normal recording không có active operator session bị reject với `OPERATOR_AUTH_REQUIRED`.

P0

User & Device Operation Requirements

Draft

QA-AUTH-002

User/Auth

Background/foreground không logout active operator session.

P0

Android Operation Design

Draft

QA-AUTH-003

User/Auth

Device reboot làm expire previous session và yêu cầu login lại.

P0

Android Operation Design

Draft

QA-REC-001

Recording

Authenticated operator có thể start và stop normal recording.

P0

Recording & Capture Design

Draft

QA-REC-002

Recording

Emergency recording không login dùng `EMERGENCY_OVERRIDE_ADMIN`.

P0

Recording & Capture Design

Draft

QA-STO-001

Storage

In-progress file không được expose như final media.

P0

Storage Design

Draft

QA-STO-002

Storage

Finalized media chỉ trở thành `BDMA_READY` sau khi file và DB state hợp lệ.

P0

Storage Design

Draft

QA-BDMA-001

BDMA

BDMA import finalized `.mp4` và verify `.md5` nếu có.

P0

Data Contract

Draft

QA-BDMA-002

BDMA

Thiếu `.md5` cho `.mp4` được import dạng Unverified/warning, không hard fail.

P0

Data Contract

Draft

QA-BDMA-003

BDMA

Production/factory restrictions không làm hỏng approved BDMA ADB import/user sync path một cách silently.

P0

Kiosk Policy + Data Contract

Draft

QA-DB-001

SQLite

BDMA write-back bị reject nếu schema version không supported.

P0

SQLite Database Design

Draft

QA-RCFG-001

Remote Config

Invalid config bị reject và last valid applied config vẫn active.

P0

System Settings Requirements

Draft

QA-RCFG-002

Remote Config

Kiosk requested-policy config bị defer trong lúc recording/finalizing/policy recovery.

P0

System Settings + Kiosk Policy

Draft

QA-RCFG-003

Remote Config

Console/app operation setting bị defer trong unsafe runtime state.

P0

System Settings + In-App Console

Draft

QA-UPD-001

Self Update

Self Update là primary update path trên no-external-EMM baseline.

P0

Self Update Design

Draft

QA-UPD-002

Self Update

Update bị defer trong lúc recording.

P0

Self Update Design

Draft

QA-UPD-003

Self Update

APK package/checksum/signature không hợp lệ bị reject.

P0

Self Update + Security

Draft

QA-UPD-004

Self Update

Update verify device policy state và restore Lock Task sau update/restart.

P0

Self Update + Kiosk Policy

Draft

QA-UPD-005

Managed Google Play

Managed Google Play / policy-driven update request được đánh dấu not applicable trên current baseline.

P0

Self Update + System Settings

Draft

QA-UPD-006

Play Store Fallback

Manual Play Store update fallback chỉ chạy qua Controlled Maintenance Mode nếu được enable và available.

P1

In-App Console + Self Update

Draft

QA-UPD-007

Play Store Fallback

Personal Google account không được chấp nhận cho production maintenance update.

P1

Security + In-App Console

Draft

QA-FACTORY-001

Factory SOP

Raw/factory-reset device có thể được xử lý theo Factory SOP và đạt expected production state.

P0

Factory SOP

Draft

QA-FACTORY-002

Factory SOP

Approved APK version/checksum/signing metadata được verify trước khi install.

P0

Factory SOP + Self Update + Security

Draft

QA-FACTORY-003

Factory SOP

Device Owner/Lock Task/User Restrictions được verify trước khi device ready to ship.

P0

Factory SOP + Kiosk Policy

Draft

QA-FACTORY-004

Factory SOP

Web Portal business provisioning tạo/restore `dcam_cloud_device_id` bằng `serial_lookup/{serial_number}` và device information.

P0

Factory SOP + Web Portal Provisioning + API Contract

Draft

QA-FACTORY-005

Factory SOP

Factory recording/storage/media read-only check pass.

P0

Factory SOP + Recording + Storage + In-App Console

Draft

QA-FACTORY-006

Factory SOP

Device có critical failure được mark `QUARANTINED`, không phải `READY_TO_SHIP`.

P0

Factory SOP

Draft

QA-FACTORY-007

Factory SOP

Production record chỉ chứa safe metadata và exclude raw Android ID, `ANDROID_ID`, `android_id_hash`, maintenance secret values và Google credential values.

P0

Factory SOP + Security

Draft

QA-SEC-001

Security

Raw Android system identifier, `ANDROID_ID` và `android_id_hash` không bị log hoặc dùng làm production lookup.

P0

Security Design

Draft

QA-SEC-002

Security

Maintenance secret values/kiosk exit credential không bị log hoặc hardcode.

P0

Security Design

Draft

QA-SEC-003

Security

Google account secret values không bị log trong optional Play Store fallback.

P1

Security Design

Draft

QA-CAP-001

Device Capability

Unsupported GPS feature bị pruned và không làm app crash.

P1

Device Capability Design

Draft

QA-CAP-002

Device Capability

GMS/Play Store availability được detect và Play Store fallback hidden/disabled nếu unavailable.

P1

Device Capability + In-App Console

Draft

QA-AI-001

Realtime AI

AI detection chỉ emit event và không điều khiển recording trực tiếp.

P1

Realtime AI Design

Draft

## 8. Factory Acceptance Test Direction

Factory SOP acceptance là bắt buộc trước pilot/production shipment.

textRules:

Rule

Description

QA-FACTORY-RULE-001

Factory acceptance không được pass nếu APK identity/checksum/signing/version chưa được approved.

QA-FACTORY-RULE-002

Factory acceptance không được pass nếu required kiosk policy không thể verify.

QA-FACTORY-RULE-003

Factory acceptance không được pass nếu tồn tại unrestricted Android escape.

QA-FACTORY-RULE-004

Factory acceptance không được pass nếu business provisioning fail hoặc thiếu `dcam_cloud_device_id`.

QA-FACTORY-RULE-005

Factory acceptance không được pass nếu recording/finalization fail.

QA-FACTORY-RULE-006

Factory acceptance không được pass nếu required BDMA readiness test fail.

QA-FACTORY-RULE-007

Factory acceptance không được pass nếu Maintenance Password Gate fails open.

QA-FACTORY-RULE-008

Factory acceptance phải mark failed devices là `QUARANTINED`.

## 9. Regression Test Set

Regression Group

Must Run Before Release

Startup / Login

Có

DCAM-as-DPC / Kiosk Policy

Có

Lock Task Recovery

Có

Maintenance Password Gate

Có

Controlled Maintenance / No unrestricted Android

Có

In-app Console Navigation

Có

In-app Console Access

Có

App Operation Settings Guard

Có

File/Storage Manager / Media Viewer Read-only

Có

Normal Recording

Có

Emergency Recording

Có

Storage Finalization

Có

BDMA Import

Có

SQLite Migration

Có

Web Portal / Device API Contract

Có

Serial lookup / identity restore

Có

Reboot Recovery

Có

Sensitive Logging

Có

Remote Config Apply/Reject

Có

Self Update / APK Validation

Có

Self Update Defer / Policy-safe Update

Có

No Managed Google Play Assumption

Có

Factory SOP Acceptance

Có trước pilot/production shipment

Optional Play Store Fallback

Conditional

Device Capability Pruning

Có

## 10. Exit Criteria

Criteria

Required

All P0 test cases passed

Có

No open P0 bug

Có

P1 bugs reviewed and accepted

Có

Recording/capture regression passed on target BodyCamera

Có

Kiosk policy regression passed on target BodyCamera

Có

In-app console P0 tests passed under kiosk mode

Có

Controlled Maintenance / Maintenance Password Gate passed

Có

No unrestricted Android escape passed

Có

Web Portal / Device API Contract P0 tests passed

Có

`serial_lookup/{serial_number}` identity restore and provisioning baseline passed

Có

Self Update primary path passed

Có

Managed Google Play / policy-driven update marked not applicable for current baseline

Có

Factory SOP acceptance passed for shipment build

Có trước pilot/production shipment

Production record format reviewed for safe metadata only

Có trước pilot/production shipment

Optional Play Store fallback either passed if enabled or documented as unavailable

Conditional

BDMA import smoke test passed under approved restriction profile

Có

Reboot/session/policy recovery test passed

Có

Sensitive logging check passed

Có

Release checklist completed

Có

## 11. Practical Conclusion

Tài liệu này là **QA source of truth** cho DCAM release readiness.

textBản baseline hiện tại tập trung vào **P0 MVP test coverage**:

text