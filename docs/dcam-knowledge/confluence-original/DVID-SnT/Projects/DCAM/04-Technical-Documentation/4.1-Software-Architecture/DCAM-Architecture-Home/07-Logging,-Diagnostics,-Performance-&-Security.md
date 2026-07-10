# 07 - Logging, Diagnostics, Performance & Security

**Page ID**: 47185971  
**Version**: 9  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47185971

---


# 07 - Logging, Diagnostics, Performance & Security

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Software Architecture Document / Operational Quality Architecture

Version

Approved 1.6

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / QA Lead / Security Reviewer

Approver

Hoàng Ngọc Quyền

Parent Folder

4.1 - Software Architecture

Target Audience

PM/BA, Tech Lead, Android Developers, QA, BDMA Team, Support

Last Updated

2026-07-08

Related Jira

None

Related Documents

DCAM Architecture Home, 02 - Architecture Principles, 05 - Data, Storage & BDMA Architecture, 06 - Cloud Services, Update & Configuration Architecture, 08 - DCAM-BDMA Integration Boundary, DCAM Documentation Governance, 05 - User & Device Operation Requirements, 07 - Logging & Diagnostics Requirements, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM-BDMA Data Contract

## 1. Purpose

Trang này mô tả baseline kiến trúc cho logging, diagnostics, performance, reliability và security của DCAM.

Nội dung ở trang này là high-level. Các runtime/recovery/security/auth details phải reference tài liệu authoritative, không copy full rule tables.

## 2. Authoritative References

Topic

Source of Truth

Architecture Usage

Logging requirements and sensitive logging rules

07 - Logging & Diagnostics Requirements

Architecture reference logging policy và category direction.

User/operator login policy and emergency override

05 - User & Device Operation Requirements

Architecture reference auth/session audit và reliability constraints.

Android startup, login/session lifecycle, foreground service, safe mode and runtime recovery

DCAM Android Operation Design

Architecture reference runtime recovery behavior.

Recording/finalization/operator attribution recovery

DCAM Recording & Capture Design

Architecture reference recording evidence preservation behavior.

Storage thresholds, temp/final, BDMA readiness and storage recovery

DCAM Storage Design

Architecture reference storage reliability behavior.

DB transaction/user sync/write-back/recovery

DCAM SQLite Database Design

Architecture reference DB consistency và recovery boundary.

BDMA/DCAM data contract and user sync

DCAM-BDMA Data Contract

Architecture reference ADB sync/write-back boundary.

Credential/auth/encryption implementation

DCAM Security & Encryption Design

Architecture reference Security Design cho implementation details.

APK/update validation

DCAM Self Update Design

Architecture reference update validation direction.

## 3. Logging Strategy

DCAM cần logging đủ để debug trên thiết bị thực tế và hỗ trợ BDMA/support team.

Log Type

Purpose

Storage Direction

Application Log

Ghi nhận app events và state chung.

Local `logs.txt` / diagnostics storage theo Data Contract và Logging Requirements.

Auth / Session Log

Ghi nhận login success/failure, logout, session restore, reboot expiration và emergency override.

Local log file / diagnostic event, chỉ dùng safe reason codes.

User Sync Log

Ghi nhận BDMA/DCAM user sync result, conflict và rejected write-back reason.

Local log file / diagnostic event.

Recording Log

Ghi nhận start/stop/result/error/operator resolution của recording.

Local log file / diagnostics event.

Capture Log

Ghi nhận capture trigger/result/error.

Local log file / diagnostics event.

Camera Log

Ghi nhận camera state và camera errors.

Local log file / diagnostics event.

GPS Log

Ghi nhận GPS availability/accuracy/error.

Local log file / diagnostics event.

Storage Log

Ghi nhận storage free space, write/read errors.

Local log file / diagnostics event.

Update Log

Ghi nhận update method/result/error.

Local log file / diagnostics event.

DB / Write-back Log

Ghi nhận DB open/migration/transaction/external write/user sync events.

Local log file / diagnostics event.

Crash Log

Ghi nhận crash/fatal/non-fatal error.

Local fallback + optional cloud provider.

## 4. Logging Rules

Rule

Description

No Sensitive Data

Không log credential, token, secret, raw private metadata hoặc sensitive media content.

Include Timestamp

Log phải có timestamp.

Include Context

Log quan trọng nên có device id/session/file id nếu phù hợp.

Auth Reason Codes

Auth/security logs dùng reason code an toàn, không log raw credential.

Emergency Override Audit

Log rõ khi emergency override được dùng, nhưng không gán vào real Admin user.

User Sync Audit

Sync conflict/reject/apply result phải có log để phục vụ Support/BDMA.

Local First

Local log phải có cho non-GMS/offline devices.

Cloud Reporting Optional

Cloud crash/diagnostics provider chỉ dùng nếu khả dụng; không bắt buộc.

Log Rotation

Log retention/rotation là bắt buộc; exact size/time values vẫn Implementation TBD.

## 5. Diagnostics Direction

Diagnostics cần hỗ trợ:

Debug trong development.

Điều tra issue trong pilot.

Customer support.

Troubleshooting cho BDMA ingest.

Troubleshooting cho user/operator sync.

Điều tra compatibility của hardware/auth-method.

Điều tra runtime recovery.

Recommended diagnostics package:

Item

Status

App version

Required

Device model

Required

Android version

Required

GMS availability

Required

Cloud provider mode

Recommended

Storage status

Required

Battery status

Required

GPS availability

Recommended

Last recording result

Recommended

Last operator resolution state

Recommended

Last emergency override usage summary

Recommended

Last user sync result

Recommended

Last capture result

Recommended

Last recovery result

Recommended

Last DB migration/write-back result

Recommended

Crash summary

Recommended

Final export format vẫn TBD.

## 6. Performance Principles

Principle

Description

Do Not Block UI Thread

File IO, camera operation, DB transaction, auth validation và network không được block UI thread.

Recording Has Priority

Logging, cloud, update, AI, user sync và background tasks không được làm giảm recording stability.

Async IO

File write/read, checksum, DB, user sync và recovery scan nên chạy trong controlled background executors.

Controlled Logging

Logging phải được giới hạn bởi retention/rotation policy.

Measure on Real Device

Performance phải được test trên BodyCamera hardware thật, không chỉ emulator/phone.

Offline Safe

App vẫn usable khi network/cloud operations timeout hoặc fail.

Offline Auth Ready

Login phải hoạt động without Internet bằng local DB-backed data.

Capability-aware Runtime

Optional workloads và auth methods phải được pruned/degraded nếu device capability không đủ.

## 7. Performance Metrics Direction

Metric target values vẫn TBD cho đến khi hoàn tất profiling trên BodyCamera thật, nhưng các metric categories đã được approved.

Metric

Purpose

Status

App startup time

Đánh giá UX và readiness.

Metric Approved / Target TBD

Login latency

Đánh giá operator readiness.

Metric Approved / Target TBD

Session restore latency

Đánh giá background/process recovery UX.

Metric Approved / Target TBD

User sync duration

Đánh giá BDMA sync supportability.

Metric Approved / Target TBD

Recording start latency

Đánh giá field operation readiness.

Metric Approved / Target TBD

Capture latency

Đánh giá image capture responsiveness.

Metric Approved / Target TBD

Storage write latency

Đánh giá media reliability.

Metric Approved / Target TBD

Finalization latency

Đánh giá BDMA readiness và evidence flow.

Metric Approved / Target TBD

DB transaction latency

Đánh giá runtime reliability.

Metric Approved / Target TBD

Memory usage

Đánh giá stability.

Metric Approved / Target TBD

Battery impact

Đánh giá operational duration.

Metric Approved / Target TBD

Crash rate

Đánh giá stability.

Metric Approved / Target TBD

ANR count

Đánh giá responsiveness.

Metric Approved / Target TBD

## 8. Reliability Direction

Scenario

Expected Direction

Source of Truth

Storage nearly full

Warn/block/stop/finalize theo threshold policy.

DCAM Storage Design

GPS unavailable

Tiếp tục recording/capture; mark GPS/location unavailable.

Sensor & Location Monitoring Design

Network unavailable

Tiếp tục local operation; defer cloud/update; user auth vẫn offline.

Cloud Services Architecture + Security Design

Cloud provider unavailable

Disable/defer cloud features một cách an toàn.

Cloud Services Architecture

App backgrounded

Tiếp tục approved tasks và không logout operator.

DCAM Android Operation Design

Device reboot

Expire previous operator session và yêu cầu login.

Android Operation + SQLite Design

User sync conflict

Log conflict và apply agreed policy hoặc manual BDMA handling.

Data Contract + SQLite Design

User disabled while recording

Không interrupt current evidence; block new recording sau safe window.

SQLite + Recording Design

Emergency recording without login

Dùng `EMERGENCY_OVERRIDE_ADMIN` và preserve audit trail.

Recording + Security Design

Device power issue

Degrade optional modules và preserve recording/evidence.

Android Operation + Device Capability Design

App crash during recording

Preserve temp/final candidate và recover/mark failed an toàn.

Recording & Capture + Storage + DB Design

DB locked/corrupted

Retry/defer hoặc enter safe mode theo DB recovery behavior.

DCAM SQLite Database Design

## 9. Security Baseline

Area

Direction

Status

Secret Management

Không hardcode secrets.

Approved

Credential Handling

Auth data phải được protect theo Security Design.

Approved Direction

Token Handling

Không log token/password/session secret.

Approved

Session Policy

No-timeout session và reboot-login-required behavior là approved product decisions.

Approved

Emergency Override

`EMERGENCY_OVERRIDE_ADMIN` là auditable system identity, không phải real Admin user.

Approved

Media Protection

Apply encryption/security behavior theo Security & Encryption Design.

Approved Direction

Metadata Protection

Sensitive fields phải được protect nếu requirement/security policy yêu cầu.

Approved Direction

Permission

Chỉ request needed permissions và handle denial an toàn.

Approved

Update Security

APK/package validation là bắt buộc trước khi install.

Approved Direction

Cloud Security

Provider access control/API key policy phụ thuộc provider cụ thể.

TBD

Logs

Tránh sensitive information trong logs.

Approved

## 10. Encryption Direction

Encryption implementation details thuộc **DCAM Security & Encryption Design**.

Area

Direction

Status

Encryption scope

Protect media/database/metadata theo approved security policy.

Draft Direction

Encryption algorithm

Chốt trong Security Design/ADR.

TBD

Key storage

Chốt trong Security Design/ADR.

TBD

Key rotation

Chốt trong Security Design/ADR.

TBD

BDMA compatibility

Phối hợp với Data Contract và Security Design.

Direction Approved / Detail TBD

## 11. Cloud Diagnostics Direction

Cloud diagnostics có thể được implement qua nhiều provider khác nhau.

Provider Direction

Notes

Firebase / Crashlytics provider

Có thể là implementation nếu tương thích với target devices.

Custom REST provider

Có thể hoạt động không cần GMS nếu thiết bị có Internet và API access.

BDMA Desktop import provider

BDMA có thể import local logs qua ADB và upload/report sau.

Local-only mode

Fallback bắt buộc cho môi trường offline hoặc restricted.

Cloud diagnostics không được block recording/capture/login-critical operation.

## 12. Remaining TBD Items

Item

Status

Log retention/rotation exact values

TBD

Crash report export format

TBD

Diagnostics export package format

TBD

Cloud diagnostics provider

TBD

Performance target values

TBD after real device profiling

Auth/login performance target values

TBD after real device profiling

User sync performance target values

TBD after ADB sync POC

Encryption algorithm

TBD in Security Design / ADR

Key management

TBD in Security Design / ADR

Cloud security rules / API access control

TBD by provider decision

## 13. Practical Conclusion

DCAM phải có đủ observability, reliability và security để vận hành ngoài hiện trường.

Architecture-level TBD statuses đã được giảm ở những nơi technical design đã tồn tại. Các TBD còn lại là open decisions thật sự, cần POC, provider selection, security review hoặc real-device profiling.

text