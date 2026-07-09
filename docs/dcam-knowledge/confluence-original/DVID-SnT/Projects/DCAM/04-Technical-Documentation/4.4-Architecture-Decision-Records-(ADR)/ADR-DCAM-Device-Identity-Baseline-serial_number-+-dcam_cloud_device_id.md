# ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id

**Page ID**: 50692110  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/50692110

---


# ADR - DCAM Device Identity Baseline: `serial_number` + `dcam_cloud_device_id`

Item

Information

Project

DCAM Android BodyCamera Application

Document Type

Architecture Decision Record

Version

Approved 1.0

Status

Accepted

Decision Date

2026-07-09

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / Backend Lead / Web Portal Lead / Security Reviewer / Factory Lead / QA Lead / BDMA Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.4 - Architecture Decision Records

Target Audience

PM/BA, Tech Lead, Android Developers, Backend/Web Portal Developers, QA, Factory, Support, Security Reviewer, BDMA Team

Related Documents

DCAM Project Home, DCAM Architecture Home, DCAM Documentation Governance, 04 - Device Configuration Requirements, DCAM Android Operation Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM Web Portal & Device API Contract, DCAM Device Provisioning Web Portal Design, DCAM Factory Provisioning & Device Production SOP, DCAM QA Test Strategy & Test Matrix

## 1. Status

`Proposed`.

ADR này trở thành `Accepted` sau khi các tài liệu liên quan được cập nhật và review để thống nhất một identity baseline duy nhất cho current production baseline.

Các tài liệu cần kiểm tra sau ADR này:

textwide760## 2. Context

DCAM là Android BodyCamera Application chạy trong dedicated-device / kiosk deployment. Thiết bị cần một identity model ổn định để dùng xuyên suốt các tình huống production và support.

Identity model phải hoạt động được trong các trường hợp sau:

textwide760Các draft cũ từng đề cập tới Android-derived identity, bao gồm:

textwide760Cách tiếp cận này không phù hợp với current production baseline vì `ANDROID_ID` và `android_id_hash` không phải Hardware Identity của BodyCamera. Chúng phụ thuộc vào Android runtime / app / user / firmware behavior và có thể gây duplicate cloud device sau factory reset, reinstall hoặc rework.

Trong current DCAM baseline, Factory SOP đã có nguồn Hardware Identity rõ ràng là `serial_number`. Vì vậy identity baseline cần thống nhất quanh `serial_number` và `dcam_cloud_device_id`.

## 3. Decision

DCAM current production baseline sử dụng identity model sau:

textwide760DCAM current production baseline không sử dụng:

textwide760## 4. Identity Model

Identity

Field

Meaning

Source / Owner

Stability

Hardware Identity

`serial_number`

Định danh vật lý của BodyCamera và primary recovery key.

Factory / DSetup / DCAM runtime

Ổn định theo vòng đời production nếu được recover đúng sau factory reset hoặc rework.

Cloud Identity

`dcam_cloud_device_id`

Primary cloud/backend device id.

Backend / Firebase / WebServer

Ổn định sau khi device được provisioned hoặc restored.

Recovery Cache

SD Identity File

File trên external SD card dùng để cache `serial_number` cho factory reset recovery.

DCAM runtime + DSetup

Có ích cho recovery nhưng không phải authoritative identity.

Device Information

`owner_name`, `manufacture_date`, `device_model`, `firmware_version`

Thông tin hiển thị, support, factory và admin.

Web Portal / Factory / Backend / DCAM mirror

Mutable hoặc semi-static, không phải identity key.

## 5. Identity Rules

### 5.1 `serial_number`

`serial_number` là Hardware Identity và primary recovery key của BodyCamera.

Rules:

textwide760### 5.2 `dcam_cloud_device_id`

`dcam_cloud_device_id` là Cloud Identity và primary cloud/backend device id.

Rules:

textwide760### 5.3 `serial_lookup/{serial_number}`

`serial_lookup/{serial_number}` là production lookup path duy nhất để create/restore Cloud Identity trong current baseline.

Rules:

textwide760### 5.4 SD Identity File

SD Identity File là recovery cache, không phải Hardware Identity.

Recommended path:

textwide760/DCAM_FACTORY/device_identity.json]]>Recommended payload:

jsonwide760Rules:

textwide760## 6. Explicit Rejections

Mechanism

Decision

Reason

`ANDROID_ID`

Rejected

Không phải Hardware Identity của BodyCamera; phụ thuộc Android runtime / app / user / OEM behavior.

`android_id_hash`

Rejected

Hashing không giải quyết được vấn đề lifecycle và vẫn phụ thuộc Android system identifier.

`device_lookup/{android_id_hash}`

Rejected

Có thể tạo duplicate device sau factory reset hoặc rework và không align với Factory SOP.

Advertising ID

Rejected

Không phù hợp làm device identity hoặc recovery key.

IMEI / Wi-Fi MAC / Bluetooth MAC

Not baseline

App-level access có thể bị hạn chế, privacy-sensitive và không ổn định trên mọi Android/OEM build.

`owner_name` as identity

Rejected

Đây là mutable device information.

`manufacture_date` as identity

Rejected

Đây là semi-static metadata, không phải unique identity.

SD Identity File as primary identity

Rejected

File này có thể bị xóa, copy, tráo SD card hoặc conflict; chỉ được xem là recovery cache.

## 7. Rationale

Quyết định này được chọn vì các lý do sau:

`serial_number` là Hardware Identity gần nhất với thiết bị vật lý BodyCamera.

`dcam_cloud_device_id` giúp Backend/Firebase có một primary cloud device id ổn định, không phụ thuộc trực tiếp vào Android runtime identifier.

`serial_lookup/{serial_number}` giúp factory reset hoặc rework restore lại đúng cloud identity cũ.

DSetup có thể recover `serial_number` từ SD Identity File hoặc barcode scan mà không cần phụ thuộc `ANDROID_ID`.

Factory, QA, Support, Backend và BDMA có cùng một baseline để trace physical device &harr; cloud device.

Rủi ro duplicate cloud device sau factory reset được giảm đáng kể.

## 8. Consequences

### 8.1 Positive Consequences

textwide760### 8.2 Negative / Trade-off Consequences

textwide760### 8.3 Security Consequences

textwide760## 9. Implementation Direction

### 9.1 Android Runtime

Android must:

textwide760### 9.2 DSetup / Factory SOP

DSetup must:

textwide760### 9.3 Backend / Firebase / Web Portal

Backend must:

textwide760### 9.4 SQLite / Local DB

`dcam.db` should support local persistence of:

textwide760`dcam.db` must not store:

textwide760### 9.5 Security

Security Design must align with this statement:

textwide760## 10. Migration / Documentation Cleanup

The following old wording must be removed from current-baseline documents:

textwide760Replace with:

textwide760
Document

Required Change

DCAM Security & Encryption Design

Sửa identity/security/recovery model từ `android_id_hash` sang `serial_number`.

DCAM QA Test Strategy & Test Matrix

Sửa các test `device_lookup/{android_id_hash}` sang `serial_lookup/{serial_number}`.

DCAM Architecture Overview

Kiểm tra lại identity và update baseline để không còn nội dung cũ.

DCAM Device Provisioning Web Portal Design

Đảm bảo QR nếu dùng chỉ là business provisioning và dùng `serial_number`.

DCAM Web Portal & Device API Contract

Giữ làm source of truth cho API/data contract của `serial_lookup`.

DCAM Factory Provisioning & Device Production SOP

Giữ làm source of truth cho DSetup, barcode scan, SD Identity File và ready-to-ship flow.

## 11. QA Acceptance Criteria

QA must verify:

textwide760Suggested QA cases:

Test ID

Scenario

Expected Result

QA-ID-001

New device provisioned by barcode serial.

Backend creates or restores `dcam_cloud_device_id` through `serial_lookup/{serial_number}`.

QA-ID-002

Factory reset with valid SD Identity File.

DSetup recovers same `serial_number`; Backend restores same `dcam_cloud_device_id`.

QA-ID-003

Factory reset without valid SD Identity File.

DSetup requires barcode scan; Backend restores by scanned `serial_number`.

QA-ID-004

SD Identity File conflicts with app-private serial.

App-private serial wins; conflict is logged/audited safely.

QA-ID-005

Duplicate serial provisioning attempt.

Backend blocks duplicate active device or requires admin conflict resolution.

QA-ID-006

Android ID/hash inspection.

No `ANDROID_ID`, `android_id_hash` or `device_lookup` is used in current production flow.

QA-ID-007

Disabled/revoked/quarantined device.

Android does not enter normal field operation.

## 12. Alternatives Considered

Alternative

Decision

Reason

Use `ANDROID_ID`

Rejected

Không phải physical BodyCamera identity và có lifecycle không phù hợp với factory reset / rework.

Use `android_id_hash`

Rejected

Che raw identifier nhưng không giải quyết được vấn đề stability và vẫn lệch Factory SOP.

Use `device_lookup/{android_id_hash}`

Rejected

Có thể tạo duplicate device và làm Web Portal/API/QA lệch identity baseline.

Use IMEI / MAC / hardware radio identifiers

Not selected

Có thể không accessible, privacy-sensitive và phụ thuộc Android/OEM build.

Use only `serial_number` as cloud primary key

Rejected

Backend vẫn cần `dcam_cloud_device_id` làm Cloud Identity ổn định để hỗ trợ migration, audit, rebind và internal reference.

## 13. Final Decision Summary

DCAM current production identity baseline là:

textwide760Current production baseline explicitly does not use:

textwide760ADR này thống nhất Android runtime, DSetup, Factory SOP, Web Portal, Backend/Firebase, SQLite, Security, QA và BDMA-facing behavior quanh một identity model duy nhất.