# 04 - Device Configuration Requirements

**Page ID**: 47710554  
**Version**: 6  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47710554

---


# 04 - Device Configuration Requirements

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Functional Requirements

Version

Approved 1.4

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Cloud Lead / Security Reviewer

Approver

Hoàng Ngọc Quyền

Parent Folder

03 - Requirements / DCAM Requirements Home

Target Audience

PM/BA, Tech Lead, Android Developers, BDMA Developers, QA, Cloud/WebServer Team

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, DCAM Requirements Home, DCAM-BDMA Data Contract, 09 - System Settings Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM SQLite Database Design, DCAM Android Operation Design, DCAM Security & Encryption Design, 08 - DCAM-BDMA Integration Boundary

## 1. Purpose

Trang này ghi nhận các yêu cầu chức năng liên quan đến **device configuration** của DCAM.

Các rule chi tiết về `dcam_config.cson`, device information, device identity, Firebase/WebServer identity và remote config boundary thuộc các tài liệu authoritative tương ứng.

Trang này chốt requirement-level decision theo **DCAM Factory Provisioning & Device Production SOP Draft 1.0**:

text## 2. Device Configuration Requirements

Requirement Area

Requirement Direction

Status

Device Config File

DCAM phải tạo và maintain `dcam_config.cson` trong Internal Storage.

Approved

Device Information

`dcam_config.cson` chỉ lưu static/semi-static device information và identity mirror cần thiết cho BDMA/support display.

Approved

Serial Number

`serial_number` là Hardware Identity / primary recovery key; lưu trong app-private storage, mirror vào `dcam_config.cson`, `dcam.db` và Firebase/WebServer.

Approved

Cloud Device ID

`dcam_cloud_device_id` là Firebase/WebServer primary cloud device id.

Approved

SD Identity File

SD Identity File trên thẻ nhớ ngoài là recovery cache chứa `serial_number`; không phải Hardware Identity.

Approved

Owner Name

`owner_name` là thông tin chủ sở hữu/đơn vị sở hữu thiết bị, lưu trong `dcam_config.cson`, mirror vào `dcam.db` và Firebase/WebServer.

Approved

Manufacture Date

`manufacture_date` là ngày sản xuất thiết bị, lưu trong `dcam_config.cson`, mirror vào `dcam.db` và Firebase/WebServer; format chuẩn là `YYYY-MM-DD`.

Approved

Serial Mutability

Serial không được tự ý thay đổi trong normal operation; thay đổi chỉ qua approved factory/rework/admin flow.

Approved

Device Information Mutability

`owner_name` và `manufacture_date` là device information; có thể được cập nhật qua approved provisioning/admin/config flow.

Approved

Device Identity Key

Firebase/WebServer primary key là `dcam_cloud_device_id`; recovery/create/restore dùng `serial_lookup/{serial_number}`.

Approved

No Android ID Dependency

DCAM không dùng `ANDROID_ID`, `android_id_hash` hoặc `device_lookup/{android_id_hash}` trong current production baseline.

Approved

Advertising ID

DCAM không dùng Advertising ID làm primary key hoặc recovery key.

Approved

CSON Update

DCAM/WebServer may update `dcam_config.cson` only for device information fields such as serial, owner name, manufacture date, device model and firmware information.

Approved Direction

Config Validation

DCAM cần xử lý trường hợp config missing, invalid hoặc unreadable bằng fallback/diagnostics an toàn.

Approved Direction

Operational Settings Boundary

Operational/runtime settings không lưu trong `dcam_config.cson`; phải lưu trong `dcam.db`.

Approved

Provisioning State

Nếu local `dcam_cloud_device_id` thiếu, DCAM/DSetup phải dùng `serial_number` để restore/create cloud identity; nếu serial thiếu thì vào serial/provisioning-required flow.

Approved

## 3. Device Identity and Device Information Rule

Field

Storage

Mutability

Purpose

`dcam_cloud_device_id`

`dcam.db`, Firebase/WebServer

Stable after provisioning

Primary device key on Firebase/WebServer.

`serial_number`

App-private storage, `dcam_config.cson`, `dcam.db` mirror, Firebase/WebServer, SD Identity File cache

Stable Hardware Identity; changed only by approved rework/admin flow

Hardware Identity and primary recovery key.

SD Identity File

External SD card, e.g. `<SD_CARD>/DCAM_FACTORY/device_identity.json`

Recreated/synced from app-private serial; may be lost if SD is formatted/replaced

Recovery cache used by DSetup after factory reset.

`owner_name`

`dcam_config.cson`, `dcam.db` mirror, Firebase/WebServer

Mutable / semi-static

Owner, customer, agency or organization name for admin/support/BDMA display.

`manufacture_date`

`dcam_config.cson`, `dcam.db` mirror, Firebase/WebServer

Semi-static; corrected only through approved admin flow

Device manufacture date in ISO format `YYYY-MM-DD`.

`serial_history`

Firebase/WebServer, optional DB mirror

Append-only direction

Tracks previous serial values if approved rework/admin flow changes serial.

`firebase_installation_id`

`dcam.db`, Firebase/WebServer metadata

May change after reinstall

Current app-install metadata only; not a device identity key.

Rules:

text## 4. First Install / Missing Local Identity Requirement

When DCAM starts and local identity is missing:

textFactory reset recovery:

text## 5. Web Portal Provisioning Requirement

Default provisioning method for new/reworked devices is **serial-number based Web Provisioning Portal / Firebase provisioning**.

textBDMA provisioning is not required for normal factory provisioning.

## 6. Source of Truth

Topic

Source of Truth

Factory provisioning, DSetup, SD Identity File recovery cache

DCAM Factory Provisioning & Device Production SOP

API/data schema and Firestore contract

DCAM Web Portal & Device API Contract

`dcam_config.cson` scope and external contract

DCAM-BDMA Data Contract

Firebase/WebServer identity and provisioning architecture

06 - Cloud Services, Update & Configuration Architecture

Runtime/operational settings and remote config apply policy

09 - System Settings Requirements

SQLite persistence, identity cache and remote config cache

DCAM SQLite Database Design

Startup restore/provisioning runtime flow

DCAM Android Operation Design

Identity/provisioning security constraints

DCAM Security & Encryption Design

## 7. Practical Conclusion

Device configuration requirement statuses are aligned with **DCAM Factory Provisioning & Device Production SOP Draft 1.0**.

text