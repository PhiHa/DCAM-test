# 02 - Architecture Principles

**Page ID**: 47120416  
**Version**: 12  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47120416

---


# 02 - Architecture Principles

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Software Architecture Document / Principles

Version

Approved 1.6

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer

Approver

Hoàng Ngọc Quyền

Parent Folder

4.1 - Software Architecture

Target Audience

PM/BA, Tech Lead, Android Developers, QA

Last Updated

2026-07-08

Related Jira

None

Related Documents

DCAM Architecture Home, 01 - Architecture Overview, 03 - Android Platform & Compatibility Strategy, 04 - Application & Module Architecture, DCAM Documentation Governance, 05 - User & Device Operation Requirements, DCAM-BDMA Data Contract, DCAM Device Capability & Feature Eligibility Design, DCAM Security & Encryption Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM SQLite Database Design

## 1. Purpose

Trang này định nghĩa các nguyên tắc kiến trúc bắt buộc cho DCAM.

Mọi thiết kế chi tiết, coding guideline và ADR sau này cần tuân theo các nguyên tắc này hoặc ghi rõ lý do nếu có ngoại lệ.

## 2. Principle Summary

Principle

Status

Meaning

Offline-first Architecture

Decided

Core features và operator login phải chạy khi không có Internet.

Reliability First

Decided

Không mất dữ liệu quan trọng là ưu tiên số một.

Operator-attributed Evidence

Decided

Normal recording/capture evidence phải gắn với authenticated operator session.

Emergency Override Auditability

Decided

Emergency recording without login dùng `EMERGENCY_OVERRIDE_ADMIN` và phải auditable.

Runtime-recoverable Design

Decided

App phải recover an toàn sau crash/reboot/service/storage/DB/session issues.

Capability-based Design

Decided

Không giả định mọi BodyCamera giống nhau, kể cả login method capability.

Platform Abstraction Layer

Decided

Không gọi trực tiếp Android SDK, hardware-specific APIs hoặc cloud provider SDK từ business logic.

Cloud Provider Abstraction

Decided

Cloud services phải đi qua abstraction; Firebase chỉ là một provider có thể dùng.

BDMA-compatible by Design

Decided

Dữ liệu sinh ra và user/operator data phải phục vụ BDMA ngay từ đầu.

Modular Architecture

Decided

Module tách trách nhiệm rõ ràng.

Configuration over Hardcode

Decided

Config phải externalize, không hardcode.

Observability

Decided

Luồng quan trọng phải có log/tracing đủ để debug.

Backward Compatibility

Decided

Data Contract / schema / settings / user sync phải được versioned để bảo đảm compatibility.

Security-aware Design

Decided

Không ghi thông tin nhạy cảm vào log và bảo vệ media/config/operator data khi cần.

## 3. Offline-first Architecture

DCAM phải hoạt động với các chức năng cốt lõi ngay cả khi không có Internet.

Các chức năng sau không được phụ thuộc vào Cloud/Firebase:

Video recording.

Image capture.

Local operator login.

Local storage.

Metadata generation.

Logging.

Basic device status.

BDMA ingest readiness.

Cloud services chỉ là phần mở rộng, không phải điều kiện bắt buộc cho core capture hoặc operator login.

## 4. Reliability First

Trong các luồng chính, ưu tiên bảo toàn dữ liệu hơn UI đẹp hoặc feature phụ.

Area

Requirement

Recording

Không được mất file khi app pause/background/crash nếu có thể recover.

Operator attribution

Media/session phải giữ operator snapshot tại thời điểm recording/capture.

Emergency override

Emergency recording without login phải dùng auditable system operator.

Storage

Cần phân biệt temporary file và completed file.

Database

Runtime state và DB/file/session mismatch phải recoverable nếu có thể.

Metadata

Metadata phải tạo đủ để BDMA nhận biết trạng thái file.

Error

Lỗi phải ghi log và có trạng thái phục hồi nếu có thể.

Crash

Crash log cần hỗ trợ truy vết nguyên nhân.

## 5. Capability-based Design

DCAM không được giả định tất cả thiết bị BodyCamera có cùng phần cứng hoặc firmware.

Capability

Handling Direction

Camera

Detect camera availability và các supported mode trước khi bật tính năng camera.

Microphone

Required cho audio/video và PTT; nếu unavailable thì phải có fallback phù hợp.

GPS

Optional cho metadata/tracking; app phải xử lý được trường hợp GPS unavailable.

Storage

Check availability và free space trước khi recording.

Login methods

Password/pattern/face/QR/NFC availability phải tuân theo device capability và security policy.

Network

Required cho cloud/remote features, không phải điều kiện bắt buộc cho core capture/login.

GMS

Chỉ required cho một số SDK-based cloud features; không phải hard blocker cho tất cả cloud services.

Battery / Thermal

Dùng cho operational warning, degraded mode và temporary unavailability.

Official feature eligibility states được định nghĩa bởi **DCAM Device Capability & Feature Eligibility Design**.

## 6. Platform Abstraction Layer

Business logic không được gọi trực tiếp Android SDK, Firebase SDK hoặc provider SDK khác.

Business logic phải đi qua interface/service layer.

Use Case / Repository
        ↓
Platform Interface
        ↓
Android / Device-specific / Cloud Provider Implementation
Implementation detail được quản lý bởi **DCAM Android Development Standard**.

## 7. Cloud Provider Abstraction

Cloud services phải được thiết kế theo hướng provider-agnostic.

Firebase có thể là provider ban đầu, nhưng không được trở thành dependency trực tiếp của business logic.

Application / Use Case
        ↓
Cloud Service Interface
        ↓
Provider Implementation
        ├── Firebase Provider
        ├── REST API Provider
        ├── Desktop-side Provider
        └── Local Provider

Area

Direction

Remote Config

Dùng RemoteConfigProvider; implementation có thể là Firebase, REST hoặc local.

Crash Reporting

Dùng CrashReportProvider; implementation có thể là cloud provider, custom REST hoặc local logs.

Device Cloud State

Dùng Cloud/Device provider interface.

Performance Metrics

Dùng metrics abstraction; implementation provider vẫn TBD.

GMS/non-GMS

GMS là device capability, không phải global cloud blocker.

## 8. BDMA-compatible by Design

Mọi media, metadata, status, user/operator data và folder structure phải được thiết kế để BDMA ingest, sync và mapping được.

Area

Direction

File naming

Tuân theo DCAM-BDMA Data Contract.

Metadata

Tuân theo DCAM-BDMA Data Contract và related technical design.

Device ID

Tuân theo DCAM-BDMA Data Contract.

User/Operator

Được stored/managed through `dcam.db` theo SQLite Database Design và Data Contract.

User sync

BDMA/DCAM user sync phải versioned, auditable và conflict-aware.

Operator attribution

Media/session giữ operator snapshot hoặc emergency override identity.

Status

Media/session/import/status tuân theo SQLite Database Design và Data Contract.

Schema version

Required cho compatibility.

## 9. Configuration over Hardcode

Configurable values không nên hardcode nếu chúng có thể thay đổi theo device/customer/environment.

Priority:

Runtime Override
    ↓
Cloud Remote Config if available
    ↓
Local Config / dcam.db
    ↓
Default Config
Operational settings được lưu trong `dcam.db`; `dcam_config.cson` chỉ dành cho device information.

## 10. Observability

DCAM phải đủ observable để hỗ trợ field support và debugging.

Area

Minimum Observability

Login/session

login success/failure, session restore, reboot expiration, emergency override.

User sync

sync started/completed/conflict/rejected write-back.

Recording

start/stop/result/error/recovery/operator resolution.

Capture

trigger/result/error.

Storage

root/free space/finalization/BDMA readiness/error.

Database

open/migration/transaction/external write/recovery.

GPS

available/unavailable/accuracy nếu có.

Permission

granted/denied/revoked.

Cloud Services

enabled/disabled/provider/fallback.

Update

method/version/result/error/deferred reason.

## 11. Remaining TBD Items

Item

Status

CameraX vs Camera2/vendor SDK final decision

TBD sau BodyCamera POC

Cloud provider list and fallback strategy

TBD theo deployment/provider decision

Performance metrics provider

TBD

Streaming protocol

TBD / future design

PTT protocol

TBD / future design

Exact login method implementation details

TBD trong Security Design / device POC

Encryption algorithm and key management

TBD trong Security Design / ADR

Items previously marked as TBD but now resolved:

Item

Current Source of Truth

Storage folder structure

DCAM-BDMA Data Contract + DCAM Storage Design

Local database choice

SQLite / DCAM SQLite Database Design

User/Operator runtime data direction

DCAM SQLite Database Design

Operator login/session policy

05 - User & Device Operation Requirements + Android Operation Design

Emergency override identity

05 - User & Device Operation Requirements + Recording & Capture Design

Feature eligibility state set

DCAM Device Capability & Feature Eligibility Design

Recording/storage/db recovery direction

Android Operation, Recording, Storage and SQLite designs

## 12. Practical Conclusion

Các nguyên tắc này là baseline cho mọi trang architecture phía sau. Khi một thiết kế chi tiết đi ngược lại các nguyên tắc này, cần tạo ADR để ghi rõ lý do và trade-off.

Các quyết định còn TBD trên trang này là open decisions thật sự cần POC, thiết bị thật, security review, provider decision hoặc ADR.