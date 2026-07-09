# DCAM Device Capability & Feature Eligibility Design

**Page ID**: 48758788  
**Version**: 5  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48758788

---


# DCAM Device Capability & Feature Eligibility Design

Item

Information

Project

DCAM

Document Type

Technical Design

Version

Draft 0.5

Status

Draft

Owner

Hoàng Ngọc Quyền

Last Updated

2026-07-08

Related Documents

DCAM Android Operation Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Self Update Design, DCAM State Machine Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Sensor & Location Monitoring Design, DCAM Realtime AI Detection Design, DCAM Device POC & Hardware Validation Report

## 1. Purpose

Trang này định nghĩa single canonical feature eligibility state set cho DCAM và runtime pruning principle được Android runtime modules sử dụng.

Detailed app operating modes, recording states, storage states, DB states, kiosk policy states và update states được định nghĩa trong các technical design documents riêng. Trang này chỉ owns feature eligibility state names, meaning và capability categories that feed eligibility decisions.

Current baseline:

text## 2. Official Feature Eligibility States

Đây là official state set được dùng trên toàn bộ DCAM documents.

State

Meaning

`ENABLED`

Feature có thể run.

`DEGRADED`

Feature có thể run ở approved reduced mode.

`DISABLED_BY_POLICY`

Feature bị tắt bởi setting hoặc policy.

`DISABLED_BY_PERMISSION`

Required permission đang missing.

`UNSUPPORTED_HARDWARE`

Required hardware capability đang missing.

`UNSUPPORTED_PERFORMANCE`

Device performance thấp hơn minimum.

`TEMPORARILY_UNAVAILABLE`

Temporary device/runtime condition ngăn safe execution.

`PRUNED`

Runtime path bị exclude khỏi initialization.

`ERROR`

Detection, evaluation hoặc runtime error.

`SUPPORTED` không phải official Feature Eligibility State. Nó chỉ có thể dùng như descriptive capability wording, không dùng làm persisted/runtime state.

## 3. Runtime Rule

textRuntime modules có trạng thái `UNSUPPORTED_*`, `DISABLED_*`, `TEMPORARILY_UNAVAILABLE`, `PRUNED` hoặc `ERROR` không được start normal runtime path.

## 4. Capability Categories

Capability Category

Examples

Notes

Camera capability

Camera available, supported resolution/FPS, preview frame analysis support.

Required for recording/capture/QR/AI camera-based features.

Audio capability

Microphone available and audio permission support.

Required for audio recording/PTT future.

Sensor capability

Accelerometer, gravity, gyroscope, motion/fall detection inputs.

Optional; pruned if unsupported.

Location capability

GPS/location provider and background location support.

Optional; policy-controlled.

Storage capability

Internal/external availability, writable state, free space, write performance.

Core recording/storage readiness.

Compute capability

CPU/memory/GPU/NPU/performance class.

AI/encryption/live streaming eligibility.

Auth method capability

Password, PIN/pattern, QR, NFC, face auth availability.

Exact methods may still be Product/Security TBD.

Kiosk policy capability

Device Owner/DPC authority, Lock Task permitted, restriction support, Home/Launcher behavior.

Current baseline does not use external EMM.

In-app console capability

Which settings can be controlled, read-only, hidden or maintenance-only.

Drives Setting hub module visibility.

Maintenance capability

Maintenance Password Gate availability, approved target enforcement, restore policy support.

Full Android unrestricted mode is not supported.

Self Update capability

Manifest access, APK download, package validation, install path and policy restore capability.

Primary update path for current baseline.

Play Store fallback capability

GMS available, Play Store available, controlled fallback process approved.

Optional only; not required for core DCAM.

BDMA capability

ADB/user sync/import path availability under approved restrictions.

Must be validated by POC.

## 5. Current Baseline Decisions That Are Not TBD

Area

Decision

External EMM

Not available / not assumed for current baseline.

Android Management API

Not available / not assumed for current baseline.

Managed Google Play policy-driven update

Not applicable for current baseline.

Update primary path

DCAM Self Update / APK update.

Play Store

Optional manual fallback only if GMS/Play Store exists and approved process allows it.

Kiosk exit

Controlled Maintenance Mode only; full Android unrestricted mode is not supported.

Maintenance authentication

Maintenance Password Gate is required.

File/Media console

File Manager and Media Viewer are read-only/view-only.

## 6. Downstream Runtime Design Alignment

Runtime Area

How It Uses This Page

Detailed Runtime Owner

Android runtime startup

Dùng eligibility result để initialize/prune modules trong `RuntimeModuleRegistry`.

DCAM Android Operation Design

Kiosk policy

Detect policy authority/restriction/Lock Task support before normal field operation.

DCAM Android Device Owner & Kiosk Policy Design

In-app console

Hide/disable/read-only console modules based on capability, role and policy.

DCAM In-App Operation, Device Settings & Media Console Design

Self Update

Enable update only when update capability and runtime guard allow.

DCAM Self Update Design

Play Store fallback

Hide/disable if GMS/Play Store unavailable or fallback not approved.

In-App Console + Self Update + Security Design

Recording/capture

Consume camera/audio/storage eligibility trước session start; detailed recording states nằm ở tài liệu riêng.

DCAM Recording & Capture Design

Storage

Consume storage capability trước root/path selection; detailed file states nằm ở tài liệu riêng.

DCAM Storage Design

SQLite DB

Persist `feature_eligibility_state` values bằng official state set này.

DCAM SQLite Database Design

Sensor/location

Chỉ start nếu feature eligibility allows; chỉ emit events.

DCAM Sensor & Location Monitoring Design

Realtime AI / analytics

Chỉ start nếu feature eligibility allows; chỉ emit events.

DCAM Realtime AI Detection Design

State machine

Reference state set này cho runtime registration decisions.

DCAM State Machine Design

## 7. Database Direction

`feature_eligibility_state.eligibility_state` chỉ được dùng official state set dưới đây.

textRecommended feature/capability keys include but are not limited to:

text## 8. Conclusion

text