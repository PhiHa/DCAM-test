# 04 - Application & Module Architecture

**Page ID**: 47218698  
**Version**: 14  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47218698

---


# 04 - Application & Module Architecture

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Software Architecture Document / Application Architecture

Version

Approved 1.9

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.1 - Software Architecture

Target Audience

PM/BA, Tech Lead, Android Developers, AI/ML Engineer, QA

Last Updated

2026-07-08

Related Jira

None

Related Documents

DCAM Architecture Home, 05 - User & Device Operation Requirements, 03 - Android Platform & Compatibility Strategy, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Self Update Design, 05 - Data, Storage & BDMA Architecture, 06 - Cloud Services, Update & Configuration Architecture, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM State Machine Design, DCAM Device Capability & Feature Eligibility Design, DCAM Sensor & Location Monitoring Design, DCAM Realtime AI Detection Design, DCAM Android Development Standard

## 1. Purpose

Trang này mô tả kiến trúc ứng dụng và module direction của DCAM.

DCAM dùng kiến trúc layered/module-based để các capability như recording, emergency, user/operator authentication, dedicated-device/kiosk policy, in-app console, controlled maintenance, Self Update, monitoring, realtime analytics, storage và BDMA có thể bật/tắt/degrade theo capability từng thiết bị BodyCamera.

Current baseline:

textTrang này là architecture-level map; runtime details thuộc các technical design authoritative tương ứng.

## 2. Recommended Architecture

textCore rule:

text## 3. Module Direction

Module

Purpose

Status

`ui`

Screens và UI state, bao gồm Record/Live View, Setting hub, login, policy-required/degraded, user management and maintenance screens.

Draft Implementation

`domain`

Use cases, domain models và interfaces.

Draft Implementation

`android-operation`

Runtime orchestrator, policy verification, login screen, session lifecycle, foreground service, permission lifecycle, module registry, Self Update runtime và recovery.

Draft Design Defined

`android-kiosk-policy`

DCAM-as-DPC/local Device Owner state, Lock Task, User Restrictions, Home/Launcher policy, Controlled Maintenance Mode and policy recovery.

Draft Design Defined

`in-app-console`

Record/Live View default navigation, Setting hub, child module registry, back behavior and role/capability/policy-based console visibility.

Draft Design Defined

`app-operation-settings`

Video resolution, FPS/quality, bitrate/profile, audio, pre/post-record and file split settings with safe apply guards.

Draft Design Defined

`device-system-settings`

Controlled proxy for approved USB/Wi-Fi/GPS/device/system settings.

Draft Design Defined

`file-media-console`

Read-only Storage Dashboard, File Manager and Media Viewer for finalized media visibility.

Draft Design Defined

`maintenance-access`

Maintenance Password Gate, Admin/Maintenance authorization, approved target enforcement and session timeout.

Draft Design Defined

`user-management`

Quản lý user/operator profile, Admin-only user settings and local admin flows.

Requirement Approved / Draft Design Defined

`auth-session`

Operator login methods, Login Settings, session restore/invalidate và emergency override identity access.

Draft Design Defined

`state-machine`

Cross-runtime guard và coordination rules, bao gồm policy guard, operator auth guard, maintenance guard and update guard.

Approved Direction

`device-capability`

Detect hardware/platform/performance/auth-method/policy/update capability và tạo Device Capability Profile.

Draft Design Defined

`feature-eligibility`

Evaluate feature runtime eligibility và prune unsupported flows.

Draft Design Defined

`camera`

Camera access abstraction; lựa chọn cụ thể CameraX/Camera2/vendor vẫn cần POC.

Interface Direction Defined / Adapter TBD

`recording`

RecordingController, policy guard, operator gate, recording/capture session và emergency evidence flow.

Draft Design Defined

`capture`

Image/audio capture workflow; evidence capture dùng operator gate.

Draft Design Defined

`emergency`

Emergency Event Manager, event routing và emergency override path.

Approved Direction

`sensor-monitoring`

Accelerometer/gravity monitoring và event candidate creation; có thể chạy before login nếu eligible and policy allows.

Draft Design Defined

`location-tracking`

GPS/location tracking và offline buffer direction; có thể chạy before login nếu eligible and policy allows.

Draft Design Defined

`realtime-ai-detection`

Frame/sample analysis và event candidate creation.

Draft Design Defined

`ai-model`

AI model provider và model validation nếu tách khỏi APK.

Future / TBD

`storage`

Android-side path/temp/final/BDMA readiness/recovery mechanics.

Draft Design Defined

`database`

SQLite `dcam.db`, schema ownership, user/auth/session tables, console/update state, transaction/write-back/recovery boundary, optional policy state snapshot nếu required.

Draft Design Defined

`bdma-integration`

Hỗ trợ BDMA readiness/import state/write-back/user sync.

Draft Design Defined

`security-encryption`

Credential/auth security, kiosk exit security, emergency override auditability, encryption, update/model validation direction.

Draft Design Defined

`self-update`

Primary no-EMM update path: manifest, APK download, validation, install, result verify and kiosk policy restore.

Draft Design Defined

`play-store-fallback`

Optional controlled maintenance fallback only if GMS/Play Store exists and approved process allows it.

Conditional / POC Required

`cloud-config`

Remote config provider, validation và apply flow, including kiosk requested-policy settings.

Approved Direction

`webserver-integration`

Future Live Streaming, PTT, SOS, JT808 và server analytics adapters.

Future / TBD

`logging`

Recording, auth, emergency, policy, capability, monitoring, DB, storage, maintenance, update và diagnostics logs.

Approved Direction

`device`

Device status, battery, thermal, device info and device policy state summary.

Draft Design Defined

`common`

Shared constants và utilities.

Draft Implementation

## 4. Capability, Policy and Update-aware Dependency Direction

textRules:

Rule

Description

DEP-001

UI phụ thuộc vào ViewModel, không phụ thuộc trực tiếp Repository hoặc Android hardware/policy APIs.

DEP-002

ViewModel gọi use cases, không gọi trực tiếp Camera/GPS/AI/Storage/SQLite/auth hardware/DevicePolicyManager/PackageInstaller.

DEP-003

Use Case phụ thuộc vào interfaces và domain services.

DEP-004

State Machine nhận events và quyết định cross-runtime guard decisions.

DEP-005

Sensor Monitoring và Realtime Analytics chỉ tạo events; không điều khiển recording trực tiếp.

DEP-006

Device Capability / Feature Eligibility phải chạy trước khi optional runtime modules được initialize.

DEP-007

Unsupported modules hoặc login methods phải được pruned/disabled khỏi runtime, không chỉ hidden trong UI.

DEP-008

Remote config không được override missing hardware, unsafe performance capability hoặc missing required policy authority.

DEP-009

Recording behavior phải đi qua RecordingController.

DEP-010

Storage behavior phải đi qua StorageService / Storage Design mechanics.

DEP-011

DB writes phải đi qua repository/DatabaseService transaction boundary.

DEP-012

Active operator session phải được truy cập qua OperatorSessionManager/approved repository.

DEP-013

Normal recording/capture evidence không được bypass operator-auth guard.

DEP-014

Emergency override phải dùng system operator `EMERGENCY_OVERRIDE_ADMIN`, không dùng real Admin user.

DEP-015

DevicePolicyManager, Lock Task and User Restrictions APIs must be isolated behind kiosk policy services/managers.

DEP-016

Maintenance Mode must be controlled by approved controller and security guard.

DEP-017

Maintenance Password Gate must be enforced before controlled kiosk exit.

DEP-018

Self Update must go through `SelfUpdateCoordinator` and must not assume Managed Google Play/EMM.

DEP-019

Manual Play Store fallback must go through Controlled Maintenance and approved target controller.

## 5. Platform Adapter Examples

Platform Service

Interface Example

Implementation Example

Status

Device Policy State

`DevicePolicyStateService`

Android `DevicePolicyManager` / local DCAM DPC state adapter.

Draft Design Defined

Kiosk Policy

`KioskPolicyService`

Policy orchestrator for Device Owner/DPC, Lock Task, restrictions and maintenance.

Draft Design Defined

Lock Task

`LockTaskService`

Android Lock Task API adapter behind `LockTaskController`.

Draft Design Defined

User Restrictions

`UserRestrictionService`

Android UserManager/DevicePolicyManager restriction adapter.

Draft Design Defined

Home/Launcher Policy

`HomeAppPolicyService`

Preferred Home/Launcher policy adapter if firmware supports it.

Draft Design Defined

Console Navigation

`ConsoleNavigationService`

Record/Live View + Setting hub + child module navigation coordinator.

Draft Design Defined

Console Settings

`ConsoleSettingService`

App operation/device settings validation and safe apply coordinator.

Draft Design Defined

Maintenance Access

`MaintenanceAccessService`

Admin role + Maintenance Password Gate + timeout/lockout state.

Draft Design Defined

Approved Maintenance Target

`ApprovedMaintenanceTargetService`

Approved package/settings target launch control.

Draft Design Defined

Device Capability

`DeviceCapabilityService`

Android hardware/platform/GMS/Play Store/policy detector.

Draft Design Defined

Feature Eligibility

`FeatureEligibilityService`

Policy + settings + capability evaluator.

Draft Design Defined

Operator Session

`OperatorSessionService`

DB-backed session manager.

Draft Design Defined

Auth Methods

`AuthMethodService`

Password / pattern / face / QR / NFC dispatch.

Direction Defined / Method Details TBD

User Management

`UserRepository`

SQLite-backed user/operator profile repository.

Draft Design Defined

Camera

`CameraService`

CameraX / Camera2 / vendor SDK adapter.

Adapter TBD after POC

Motion Sensor

`MotionSensorService`

Android SensorManager.

Interface Direction Defined

Location

`LocationService`

Android Location Provider.

Interface Direction Defined

Realtime Analytics

`RealtimeAnalyticsService`

On-device runtime/model adapter.

Runtime Direction Defined / Implementation TBD

AI Model

`AIModelProvider`

Bundled hoặc future remote model provider.

Future / TBD

Storage

`StorageService`

Android file storage implementation.

Draft Design Defined

Database

`DatabaseService`

Room / SQLite wrapper.

SQLite Decided / Wrapper TBD

Remote Config

`RemoteConfigProvider`

Firebase / REST / Local.

Provider Direction Defined

Self Update

`SelfUpdateService`

Manifest/APK provider, validator and install coordinator.

Draft Design Defined

Play Store Fallback

`PlayStoreFallbackService`

Optional controlled target launcher if GMS/Play Store available.

Conditional / POC Required

BDMA

`BDMAReadinessService`, `BdmaUserSyncService`

Local DB/file readiness và user sync implementation.

Draft Design Defined

Logging

`LogService`

Local file log service.

Approved Direction

## 6. Core Flow Direction

### 6.1 Capability and Policy-aware Startup and Login

text### 6.2 Emergency Event Flow

text### 6.3 Kiosk and Maintenance Flow

text### 6.4 Self Update Flow

text## 7. Source-of-truth Alignment

Runtime/Architecture Area

Source of Truth

User/operator requirements

05 - User & Device Operation Requirements

Android dedicated-device/kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

In-app console and controlled maintenance UX

DCAM In-App Operation, Device Settings & Media Console Design

Self Update / APK update

DCAM Self Update Design

Runtime startup/module registry/session lifecycle

DCAM Android Operation Design

Recording/capture session behavior and operator attribution

DCAM Recording & Capture Design

Storage mechanics and BDMA readiness

DCAM Storage Design

SQLite transaction/user auth/session/write-back/recovery

DCAM SQLite Database Design

Credential/auth/kiosk/update security

DCAM Security & Encryption Design

Feature eligibility states

DCAM Device Capability & Feature Eligibility Design

Cross-runtime guards

DCAM State Machine Design

BDMA/user sync contract

DCAM-BDMA Data Contract

Implementation standard

DCAM Android Development Standard

## 8. Practical Conclusion

DCAM architecture phải support heterogeneous BodyCamera hardware, dedicated-device/kiosk deployment, in-app console operation, Self Update primary path và offline operator authentication.

textTBD statuses trên trang này hiện đại diện cho implementation choices thực tế, Device POC outcomes hoặc future features, không phải các architecture decisions đã có source of truth.