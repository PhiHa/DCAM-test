# DCAM Project Home

**Page ID**: 41648280  
**Version**: 51  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/41648280

---


# DCAM Project Home

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Project Home / Chỉ mục tài liệu

Version

Approved 3.22

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / BDMA Lead / Security Reviewer / Cloud Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

DCAM

Target Audience

PM/BA, Tech Lead, Android Developers, AI/ML Engineer, BDMA Team, Cloud/WebServer Team, QA, Factory, Support, Stakeholders

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Requirements Home, DCAM Architecture Home, DCAM Documentation Governance, DCAM QA Test Strategy & Test Matrix, DCAM Factory Provisioning & Device Production SOP, DCAM DSetup Factory Tool Design, DCAM Device POC & Hardware Validation Report, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 09 - System Settings Requirements, 10 - Android Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Web Portal & Device API Contract, DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM State Machine Design, DCAM Device Capability & Feature Eligibility Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM SQLite Database Design, DCAM Storage Design, DCAM Self Update Design, DCAM Security & Encryption Design, DCAM Non-functional Requirements, DCAM-BDMA Data Contract

## 1. Purpose

Trang này là landing page chính cho toàn bộ bộ tài liệu **DCAM**.

Mục tiêu:

Giúp team tìm nhanh tài liệu theo nhóm.

Phản ánh đúng cấu trúc Confluence hiện tại.

Xác định baseline tài liệu đã có để chuẩn bị cho development, QA, release readiness, factory production, BDMA integration, user/operator management, continuous monitoring, realtime AI detection, device capability handling, remote config, Web Portal provisioning, Android dedicated-device/kiosk policy, in-app console và update strategy.

Định hướng **source of truth** để tránh copy trùng lặp rule/principle giữa nhiều tài liệu.

Ghi nhận quyết định cloud storage baseline: **Firebase Cloud Firestore là backend storage baseline; Firebase Realtime Database không dùng cho current storage baseline**.

Ghi nhận quyết định identity baseline theo **DCAM Factory Provisioning & Device Production SOP Draft 1.1**: `serial_number` là Hardware Identity / primary recovery key, `dcam_cloud_device_id` là Cloud Identity / primary cloud device id, SD Identity File là recovery cache trên thẻ nhớ ngoài, không dùng `ANDROID_ID`, `android_id_hash` hoặc `device_lookup/{android_id_hash}` trong current production baseline.

Ghi nhận quyết định Web Portal/API baseline: **DCAM business provisioning tạo/restore** `devices/{dcam_cloud_device_id}` bằng `serial_lookup/{serial_number}` trong Cloud Firestore.

Ghi nhận quyết định kiosk baseline hiện tại: **DCAM-as-Device-Owner/DPC-capable app nếu khả thi, Lock Task Mode, User Restrictions, Controlled Maintenance Mode, Maintenance Password Gate, current device baseline per ADR: No external EMM / No Android Management API / No Managed Google Play**.

Ghi nhận quyết định update baseline hiện tại: **DCAM Self Update / APK update là primary path; Google Play Store chỉ là optional manual maintenance fallback nếu thiết bị có GMS/Play Store**.

Ghi nhận factory baseline: **raw/factory-reset BodyCamera chỉ được marked READY_TO_SHIP sau khi pass SOP sản xuất, DSetup Device Owner setup, serial recovery/scan/injection, business provisioning, kiosk policy, recording/storage, BDMA nếu required và Self Update capability checks**.

## 2. Current Documentation Structure

This section reflects the current Confluence hierarchy under the DCAM root page.

textwide760Notes:

textwide760## 3. Authoritative Rule Ownership Matrix

Các rule/principle dùng chung chỉ định nghĩa đầy đủ tại tài liệu authoritative. Các tài liệu khác chỉ reference kèm summary ngắn để tránh lệch nội dung giữa nhiều page.

Rule / Principle

Authoritative Document

Reference Style in Other Docs

Documentation governance, source-of-truth rule and anti-duplication rule

DCAM Documentation Governance

Define once; reference elsewhere.

Factory provisioning, DSetup, serial recovery/scan/injection, SD Identity File recovery cache and ready-to-ship decision

DCAM Factory Provisioning & Device Production SOP

Source of truth cho thao tác factory/admin để biến BodyCamera raw/factory-reset thành thiết bị DCAM ready-to-ship.

DSetup tool behavior, single-device scan mode, ADB/Device Owner setup helper and factory tooling boundary

DCAM DSetup Factory Tool Design

Tool design supports Factory SOP; SOP owns production procedure.

Device identity baseline

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id + DCAM Factory Provisioning & Device Production SOP + 04 - Device Configuration Requirements

`serial_number` = Hardware Identity / recovery key; `dcam_cloud_device_id` = Cloud Identity; SD Identity File = recovery cache; không dùng Android ID/hash.

Device config file scope and identity requirement

04 - Device Configuration Requirements

Reference rule về device identity; không định nghĩa lại chi tiết storage/write.

Firebase Cloud Firestore storage baseline and cloud architecture boundary

06 - Cloud Services, Update & Configuration Architecture

Reference architecture decision; API/path/schema belongs to API Contract.

Web Portal provisioning API/data contract, `serial_lookup/{serial_number}`, `devices/{dcam_cloud_device_id}`, config/update/status/factory record API and Firestore collection/document direction

DCAM Web Portal & Device API Contract

Source of truth cho Android/Web Portal/Backend/Cloud Firestore request/schema/error boundary.

Web Portal provisioning business flow, screens, admin actions and UI behavior

DCAM Device Provisioning Web Portal Design

Source of truth cho Web Portal provisioning user/admin flow. API schema belongs to API Contract.

Web Portal app implementation design, screen/component structure and app-side execution detail for provisioning UI

DCAM Device Provisioning Web Portal App Design

Child design under Web Portal Provisioning Design; parent page owns business flow, API Contract owns schema.

DCAM business provisioning vs Android Device Owner setup

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision + DCAM Factory Provisioning & Device Production SOP + DCAM Device Provisioning Web Portal Design + DCAM Web Portal & Device API Contract + DCAM Android Device Owner & Kiosk Policy Design

Web Portal QR không tự biến app thành Device Owner.

No external EMM / No Android Management API / No Managed Google Play decision

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Current device baseline: No external EMM / No Android Management API / No Managed Google Play. Other docs reference this ADR instead of repeating the full decision block.

Device Owner/DPC policy, Lock Task Mode, User Restrictions, Home/Launcher policy, Maintenance Mode and policy recovery

DCAM Android Device Owner & Kiosk Policy Design

Source of truth cho dedicated-device/kiosk policy. Local docs only describe runtime/update/UI impact.

In-app console navigation, Setting hub, Record/Live View default screen, Login Settings, User Settings, File/Media read-only, Controlled Maintenance Mode UX

DCAM In-App Operation, Device Settings & Media Console Design

Source of truth cho app console/UX behavior.

Controlled Exit Kiosk temporarily, Maintenance Password Gate, approved target list, no full Android unrestricted mode

DCAM In-App Operation, Device Settings & Media Console Design + Kiosk Policy Design + Security Design

Reference full rule; related docs only state local impact.

DCAM Self Update / APK update as primary update path

DCAM Self Update Design

Source of truth cho artifact/download/validation/install flow. API check/result schema belongs to API Contract.

Optional manual Google Play Store fallback under Controlled Maintenance Mode

DCAM In-App Operation, Device Settings & Media Console Design + DCAM Self Update Design

Only if device has GMS/Play Store and approved maintenance/factory Google account.

Remote config validation, cache and apply policy

09 - System Settings Requirements

Reference setting/apply ownership; API schema belongs to API Contract.

Android startup identity restore and provisioning-required runtime state

DCAM Android Operation Design

Reference startup/provisioning orchestration.

Device identity/provisioning/kiosk/security constraints

DCAM Security & Encryption Design

Reference security constraints; không copy lại security rules.

User/operator management requirement, login policy, emergency override

05 - User & Device Operation Requirements

Reference full requirement; không định nghĩa lại trong architecture/dev docs.

Recording operator gate and emergency override attribution

DCAM Recording & Capture Design

Reference RecordingController behavior và media attribution.

User/auth/session DB schema, operator session persistence

DCAM SQLite Database Design

Reference tables và transaction boundaries only.

Feature eligibility states, device capability, runtime pruning

DCAM Device Capability & Feature Eligibility Design

Reference kèm summary ngắn only.

Cross-runtime guard rules and state ownership boundary

DCAM State Machine Design

Reference guard/coordination only.

Media naming, folders, MD5, BDMA cleanup baseline

DCAM-BDMA Data Contract

Reference only; storage/BDMA docs chỉ thêm implementation notes.

Sensitive logging rules

07 - Logging & Diagnostics Requirements + DCAM Security & Encryption Design

Reference; Security docs định nghĩa security rationale.

QA test strategy, master test matrix, regression set and exit criteria

DCAM QA Test Strategy & Test Matrix

Reference QA coverage và release readiness baseline.

BodyCamera hardware validation, device POC results and real-device decision evidence

DCAM Device POC & Hardware Validation Report

Reference actual validation results để chốt Technical Design TBD và factory SOP decisions.

## 4. Cross-document Duplication Rule

textwide760Cách reference được chấp nhận:

textwide760 for the full rule.
This document only applies that rule to .]]>## 5. Technical Design Baseline

Document

Purpose

Current Version / Status

Notes

04 - Device Configuration Requirements

Xác định scope của device config file, serial, SD Identity File và identity requirement.

Approved 1.4

`serial_number` là Hardware Identity/recovery key; identity dùng `dcam_cloud_device_id`; không dùng Android ID/hash.

09 - System Settings Requirements

System settings, app operation settings, remote config, kiosk requested-policy settings, AutoUpdate preconditions and no-EMM update boundary.

Approved 1.15

Remote config fetch by `dcam_cloud_device_id`; identity recovery uses `serial_lookup/{serial_number}`.

06 - Cloud Services, Update & Configuration Architecture

Cloud Firestore storage baseline, WebServer identity, Web Portal provisioning boundary, remote config, update provider and future WebServer direction.

Approved 3.1

Firebase Cloud Firestore is selected; `serial_lookup/{serial_number}` is create/restore path. Remote config groups are defined; exact payload schema remains TBD.

DCAM Web Portal & Device API Contract

API/data contract between Android, Web Portal and Backend/Cloud Firestore for serial lookup, device record, config, update, heartbeat and factory record.

Draft 0.5

Source of truth for API/path/schema/error code and Firestore collection/document direction. Uses `serial_lookup/{serial_number}`.

DCAM Device Provisioning Web Portal Design

Provisioning business flow, Web Portal screens, optional QR/serial flow, admin actions, states, audit and error handling.

Draft 0.8

Source of truth cho DCAM business provisioning UI/flow; not Android Device Owner setup; API schema belongs to API Contract.

DCAM Device Provisioning Web Portal App Design

Web Portal app implementation design for provisioning UI, app/page structure and app-side behavior.

Draft

Child page under Web Portal Provisioning Design; supports UI implementation while parent owns business flow.

DCAM Android Device Owner & Kiosk Policy Design

Device Owner/DPC-capable policy, Lock Task Mode, User Restrictions, Home/Launcher, Controlled Maintenance Mode, Maintenance Password Gate and policy recovery.

Draft 0.6

Current device baseline references ADR for No external EMM / No Android Management API / No Managed Google Play.

DCAM In-App Operation, Device Settings & Media Console Design

Record/Live View default screen, Setting hub, Back behavior, App Operation Settings, Device/System proxy, read-only File/Media, Login Settings, User Settings, Controlled Exit Kiosk, Play Store fallback.

Draft 1.0

Source of truth for in-app console and controlled maintenance UX.

DCAM Android Operation Design

Android startup, serial-based identity restore, provisioning state, login/session lifecycle, foreground service, kiosk policy verification, console module readiness and recovery.

Draft 1.7

Runtime baseline aligned with SOP Draft 1.1 and ADR references.

DCAM State Machine Design

Cross-runtime coordination, global guard rules, operator auth guard, kiosk policy states and maintenance/update states.

Approved

Includes maintenance auth, controlled update states and no-EMM baseline.

DCAM SQLite Database Design

`dcam.db` schema direction, identity/provisioning/cache, user/auth/session, console settings, optional policy snapshot, audit, update state and recovery.

Draft 1.3

Includes `serial_number`, `dcam_cloud_device_id`, optional SD identity sync state and non-sensitive audit boundaries.

DCAM Recording & Capture Design

Recording/capture state machine, operator gate, emergency override, finalization and recovery.

Draft

Recording remains primary feature and must block unsafe settings/update/maintenance transitions.

DCAM Storage Design

Android-side storage mechanics, path validation, temp/final movement, BDMA readiness and storage recovery.

Draft

File/Media viewer is read-only and must use finalized media state.

DCAM Self Update Design

Primary update path for current no-EMM baseline: APK artifact/version manifest/validation/install and policy restore.

Draft 0.9

Play Store is optional manual fallback only if GMS/approved maintenance account exists.

DCAM Security & Encryption Design

Identity security, provisioning security, auth/security, kiosk security, Maintenance Password Gate, update/package validation and sensitive logging.

Draft 1.1

Must not allow personal Google account or plaintext maintenance/update secrets.

DCAM Device Capability & Feature Eligibility Design

Authoritative source cho eligibility states and runtime pruning.

Draft 0.5

Includes GMS/Play Store availability and update capability categories.

DCAM Android Development Standard

Implementation standard for Android code, module boundary, policy managers and service interfaces.

Approved

Source of truth cho coding style, module boundary, policy manager boundary and PR checklist.

DCAM Device POC & Hardware Validation Report

Evidence baseline for BodyCamera behavior and real device decisions.

Draft

Must validate Device Owner feasibility, no-EMM update path, GMS/Play Store fallback and controlled maintenance.

DCAM QA Test Strategy & Test Matrix

QA baseline for release readiness.

Approved 1.6

Must include no-EMM/self-update/controlled-maintenance/API/provisioning/factory SOP-related acceptance checks.

DCAM Factory Provisioning & Device Production SOP

Factory/admin procedure to prepare a BodyCamera for shipment.

Draft 1.1

Source of truth for DSetup, Device Owner setup, serial recovery/scan/injection, SD Identity File, ready-to-ship and quarantine decisions.

DCAM DSetup Factory Tool Design

Factory tooling design for DSetup single-device scan mode, ADB/device-owner helper flow, serial recovery/scan/injection support and production tool behavior.

Draft

Child page under Factory SOP; supports SOP execution, but SOP remains procedure source of truth.

DCAM-BDMA Data Contract

DCAM/BDMA/WebServer data contract boundary.

Approved

No change unless maintenance/update/factory flow affects BDMA import/user sync contract.

## 6. Android Development / Device Validation Baseline

Document

Purpose

Current Version / Status

Notes

DCAM Android Training & Architecture Onboarding

Developer onboarding cho Android newcomers và team chuyển từ Java/Desktop sang Android.

Approved

Should reference In-App Console/Kiosk/Self Update/API baseline for developers assigned to these areas.

DCAM Android Development Standard

Project-specific Android implementation standard.

Approved

Source of truth cho coding style, module boundary, service interface, policy manager boundary and PR checklist.

DCAM Device POC & Hardware Validation Report

BodyCamera thật để chốt hardware/platform/storage/camera/service/sensor/update/kiosk policy decisions.

Draft

Must validate no external EMM baseline, DCAM-as-DPC feasibility, self-update install path, optional Play Store fallback and factory SOP assumptions.

## 7. QA / Release Management Baseline

Document

Purpose

Current Version / Status

Notes

DCAM QA Test Strategy & Test Matrix

QA strategy, test level, environment matrix, device test matrix, master test matrix, regression set and exit criteria.

Approved 1.6

Must include controlled maintenance, maintenance password, read-only file/media, no unrestricted Android, no-EMM self-update, API/provisioning, factory acceptance and optional Play Store fallback coverage.

DCAM Factory Provisioning & Device Production SOP

Factory/admin production flow from raw/factory-reset device to ready-to-ship device.

Draft 1.1

Must be used for pilot/production device preparation after Device POC and release package approval.

DCAM DSetup Factory Tool Design

DSetup tool design supporting Factory SOP execution.

Draft

Defines factory tool behavior; does not replace SOP acceptance gates.

## 8. Important Current Decisions

Decision Area

Current Decision

Offline-first

Core recording, local storage, local emergency clip, local user authentication and BDMA readiness must work without cloud/WebServer after device has valid provisioned local state.

Firebase Storage Baseline

Firebase Cloud Firestore is the selected backend storage baseline. Firebase Realtime Database is not used for current storage baseline.

Device Identity Baseline

`serial_number` is Hardware Identity / primary recovery key. `dcam_cloud_device_id` is Cloud Identity / primary cloud device id. SD Identity File is recovery cache on external SD card.

Web Portal API Baseline

DCAM business provisioning uses `serial_lookup/{serial_number}` to create/restore `devices/{dcam_cloud_device_id}`. Do not use `ANDROID_ID`, `android_id_hash` or `device_lookup/{android_id_hash}` in the current production baseline.

Web Portal App Design

Web Portal provisioning business flow is owned by the parent Web Portal Provisioning Design; app/page implementation details are captured in DCAM Device Provisioning Web Portal App Design; API/schema remains in API Contract.

Kiosk Deployment Baseline

DCAM production deployment is a dedicated-device/kiosk deployment. DCAM should use DCAM-as-Device-Owner/DPC-capable policy if feasible on selected BodyCamera firmware. Current device baseline per ADR: No external EMM / No Android Management API / No Managed Google Play.

Factory Device Owner Setup

Factory SOP current baseline uses DSetup: factory reset / clean state &rarr; DSetup install approved DCAM APK &rarr; ADB/factory `dpm set-device-owner` &rarr; verify Device Owner &rarr; inject serial. QR is not used for Device Owner setup.

DSetup Factory Tool

DSetup is the factory tool supporting single-device scan mode, SD Identity File recovery, barcode scan fallback, approved APK install/update, Device Owner setup helper, serial injection and production result capture.

Lock Task / Restrictions

Normal field operation uses Lock Task Mode and approved User Restrictions. Back/Home/Recents must not expose Android launcher/system UI.

In-App Console

`Record / Live View` is the default main screen. `Setting` is the console hub. Back on Record opens Setting; Back on Setting returns Record; Back inside child modules returns Setting.

File/Media Manager

File Manager and Media Viewer are read-only/view-only. They must not delete, edit, mark important, export or share media files.

Login/User Settings

Login Settings supports approved current-user auth settings; User Settings is Admin-only and must preserve historical attribution.

Controlled Maintenance

Admin / Maintenance is the only approved path to Enter Maintenance Mode / Exit Kiosk temporarily. Maintenance Password Gate is required. Full Android unrestricted mode is not supported.

Approved Maintenance Targets

Controlled Mode may open only approved Android settings screens or approved maintenance/support apps. Unrestricted launcher/app drawer/settings browsing is not supported.

App Update Baseline

Current device baseline per ADR has no external EMM / Android Management API / Managed Google Play. Primary update path is DCAM Self Update / APK update.

Google Play Store Fallback

Manual Google Play Store update is optional fallback only if target BodyCamera has GMS/Play Store and approved maintenance/factory Google account. Personal Google account usage is not supported.

Factory Production Baseline

A device is not ready to ship just because DCAM APK is installed. It must pass Factory SOP checks including approved APK, DSetup Device Owner verification, serial recovery/scan/injection, SD Identity File result if required, business provisioning, kiosk policy verification, maintenance protection, recording/storage check, BDMA readiness if required, Self Update capability and production record.

Android Enterprise vs DCAM Business Provisioning

Android Device Owner setup is separate from DCAM Web Portal/Firebase business provisioning. DCAM business provisioning creates/restores `dcam_cloud_device_id`, `serial_number`, `owner_name`, `manufacture_date` and initial metadata.

Device Cloud Identity

Cloud Firestore/WebServer primary device key is `dcam_cloud_device_id`.

Identity Recovery

`serial_number` is the recovery key. Cloud restore uses `serial_lookup/{serial_number}`. SD Identity File may cache serial on external SD card for DSetup recovery after factory reset.

Serial Number

`serial_number` is Hardware Identity, stored locally, mirrored to `dcam_config.cson`/`dcam.db`, synced with Cloud Firestore/WebServer and cached in SD Identity File if feature enabled.

Advertising ID

Advertising ID is not used as DCAM identity key.

Remote Config

Remote config fetch/cache/apply baseline and initial setting groups are defined; exact field-level payload schema remains TBD. Fetch by `dcam_cloud_device_id`, cache in `dcam.db`, validate/apply only when runtime guard allows. Kiosk policy config is requested policy. API schema belongs to API Contract.

CSON

`dcam_config.cson` only stores device information. Operational settings are stored in `dcam.db`.

User Management

DCAM and BDMA both manage user/operator data; sync is two-way through ADB.

Operator Login

Normal recording/capture evidence requires active operator session.

Emergency Override

Emergency recording can run without login using system operator `EMERGENCY_OVERRIDE_ADMIN`; it is not a real Admin and cannot enter Maintenance.

Login Session

Session has no timeout; background/foreground does not logout; device reboot requires login again.

System Modules Before Login

Tracking, sensor, GPS, logging, recovery, identity/provisioning checks, kiosk policy verification, console capability pruning and capability detection can run without operator login when safe.

AutoUpdate Preconditions

System Settings owns full AutoUpdate preconditions. Self Update owns artifact/install flow. Kiosk Policy owns policy-safe update constraints. In-App Console owns manual Play Store fallback UX. API Contract owns update check/result schema.

Device POC

Device POC is required to validate real BodyCamera behavior before release and before Factory SOP is approved for production.

QA / Release Readiness

QA Test Strategy & Test Matrix is release readiness baseline. Factory SOP acceptance checks contribute to ready-to-ship decision.

BDMA Boundary

Media import/user sync is ADB-based and BDMA-initiated; WebServer identity/provisioning does not replace BDMA ingest/user-sync boundary.

## 9. Practical Conclusion

DCAM documentation should be maintained as a set of architecture documents with clear source-of-truth ownership.

textwide760Runtime + production baseline hiện tại là:

textwide760