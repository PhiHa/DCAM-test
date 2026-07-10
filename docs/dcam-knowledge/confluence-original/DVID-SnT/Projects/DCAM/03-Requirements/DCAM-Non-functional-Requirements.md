# DCAM Non-functional Requirements

**Page ID**: 48595009  
**Version**: 11  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48595009

---


# DCAM Non-functional Requirements

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Non-functional Requirements

Version

Approved 1.10

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / QA Lead / Security Reviewer / BDMA Lead / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

03 - Requirements

Target Audience

PM/BA, Tech Lead, Android Developers, QA, Stakeholders, Support, BDMA Team

Last Updated

2026-07-08

Related Documents

05 - User & Device Operation Requirements, 10 - Android Device Operation Requirements, DCAM Android Device Owner & Kiosk Policy Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM SQLite Database Design, DCAM Storage Design, DCAM Device Capability & Feature Eligibility Design, DCAM-BDMA Data Contract, DCAM Security & Encryption Design, 07 - Logging & Diagnostics Requirements, DCAM State Machine Design, DCAM QA Test Strategy & Test Matrix

## 1. Purpose

Tài liệu này ghi nhận các yêu cầu phi chức năng của DCAM.

Tài liệu này không copy lại các bảng rule dùng chung. Các rule chi tiết phải nằm ở tài liệu authoritative tương ứng.

Sau khi User Management và Android dedicated-device/kiosk policy được chốt, NFR cần bổ sung các quality constraints cho offline authentication, no-timeout session, reboot login requirement, emergency override auditability, BDMA/DCAM user sync, Device Owner / Lock Task reliability, User Restrictions, Maintenance Mode, policy recovery và security testability.

## 2. Authoritative References

Topic

Authoritative Document

Local Use

User/operator requirement, login policy, emergency override

05 - User & Device Operation Requirements

NFR yêu cầu offline auth reliability và auditability.

Android dedicated-device/kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

NFR yêu cầu kiosk reliability, policy recovery, controlled Maintenance Mode và fail-closed behavior.

Dedicated-device decision rationale

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

NFR reference long-term decision only.

App runtime orchestration, login screen, session lifecycle, safe mode

DCAM Android Operation Design

NFR yêu cầu session behavior phải ổn định khi app background hoặc process recreate, nhưng không restore sau reboot.

Recording/finalization/emergency evidence recovery

DCAM Recording & Capture Design

NFR yêu cầu operator attribution và emergency override evidence safety.

DB ownership, transaction, user sync, write-back and recovery

DCAM SQLite Database Design

NFR yêu cầu DB consistency, recoverability và safe BDMA write-back.

Storage path/temp/final/BDMA readiness/recovery

DCAM Storage Design

NFR yêu cầu storage reliability và không expose partial-file.

Feature eligibility states and runtime pruning

DCAM Device Capability & Feature Eligibility Design

NFR yêu cầu capability-aware runtime behavior.

Media/file/MD5/user sync/BDMA contract

DCAM-BDMA Data Contract

NFR yêu cầu Data Contract compliance.

Auth/security/encryption/kiosk exit security

DCAM Security & Encryption Design

NFR yêu cầu safe credential handling, kiosk security và auditability.

Sensitive logging rules

07 - Logging & Diagnostics Requirements

NFR yêu cầu diagnostics không làm lộ sensitive data.

State priority and transition guard

DCAM State Machine Design

NFR yêu cầu state-driven safe behavior.

## 3. NFR Principles

Principle

Requirement

Stability

Core operation phải ổn định trong điều kiện device/resource constraints.

Kiosk Reliability

Production kiosk mode phải recover sau boot/crash/update/process recreation và không silently leave device unrestricted.

Runtime Recoverability

App phải recover an toàn sau unexpected stop, reboot, process recreation, service interruption, policy failure, DB issue hoặc storage interruption.

Evidence Preservation

Emergency/important media candidates phải được preserve khi state chưa chắc chắn.

Offline Authentication

Operator authentication phải hoạt động without Internet/cloud dependency.

Authenticated Evidence

Normal recording/capture evidence phải có authenticated operator attribution.

Emergency Override Auditability

Emergency recording without login phải được attribute cho `EMERGENCY_OVERRIDE_ADMIN` và auditable.

No-timeout Session Consistency

Operator session không được expire do time hoặc background/foreground transition.

Reboot Security Boundary

Device reboot phải yêu cầu login lại.

Capability-aware Runtime

Runtime chỉ nên start eligible features hoặc approved degraded features.

Policy-aware Runtime

Runtime chỉ nên enter normal production field operation nếu required Device Owner / Lock Task / User Restrictions policy state hợp lệ hoặc degraded state được approve.

Offline-first

Core local operation phải hoạt động without cloud dependency after device is provisioned and policy baseline is valid.

Contract Compliance

Storage, import, user sync và write-back behavior phải tuân thủ Data Contract.

Safe Diagnostics

Logs phải hỗ trợ Support/QA mà không expose sensitive data.

Testability

Capability profiles, auth/session behavior, kiosk policy behavior, fallback behavior, transaction failure, recovery và error paths phải testable.

## 4. Android Dedicated-device / Kiosk NFRs

NFR

Requirement

Source Design

Device policy detection reliability

DCAM phải detect required Device Owner / approved DPC state at startup, reboot and recovery.

Kiosk Policy + Android Operation

Lock Task reliability

DCAM phải enter/re-enter Lock Task Mode after startup/reboot/crash/update when required.

Kiosk Policy + Android Operation

User restriction reliability

Approved User Restrictions must be applied/verified or unsupported restrictions reported deterministically.

Kiosk Policy Design

Fail-closed production behavior

Missing required production kiosk policy must not silently allow unrestricted field operation.

Kiosk Policy + State Machine

Maintenance recoverability

Maintenance Mode entry/exit must restore production restrictions and be auditable.

Kiosk Policy + Security Design

Policy-safe update

Update must not leave device unrestricted and must restore Lock Task/policy state after restart.

Self Update + Kiosk Policy

BDMA compatibility under restrictions

Approved restriction profile must not silently break BDMA ADB import/user sync.

Kiosk Policy + Data Contract + Device POC

OEM limitation visibility

Unsupported OEM policy behavior must be documented in Device POC and reflected as degraded/release decision.

Device POC + Kiosk Policy

Policy auditability

Device policy apply/remove/failure, Lock Task failure and Maintenance Mode events must use safe reason codes.

Security + Logging Requirements

## 5. User Management and Authentication NFRs

NFR

Requirement

Source Design

Offline user availability

DCAM phải authenticate operator bằng local `dcam.db` data without Internet.

User Requirements + SQLite Design

BDMA/DCAM user sync

User/operator data phải sync two-way through ADB khi connected.

Data Contract + SQLite Design

Sync conflict handling

Conflicts phải được detect, log và resolve bằng default policy hoặc manual BDMA flow.

Data Contract + SQLite Design

Normal recording gate

Normal recording/capture evidence phải bị block nếu không có active operator session hoặc required production policy state missing.

Recording & Capture Design + Kiosk Policy

Emergency override

Emergency recording có thể proceed without login bằng `EMERGENCY_OVERRIDE_ADMIN`.

Recording & Capture Design

Operator attribution

Media/session phải preserve operator snapshot tại thời điểm recording.

Recording + SQLite Design

No session timeout

Active session không được expire theo time.

Android Operation Design

Background behavior

App background/foreground không được logout operator.

Android Operation Design

Reboot behavior

Device reboot phải expire previous session và show login.

Android Operation Design

Credential safety

Authentication data không được lưu/log ở unsafe raw form.

Security & Encryption Design

Auditability

Login, logout, emergency override, auth method change, user sync, conflict events và policy events phải auditable.

Security + Logging Requirements

## 6. Runtime Recovery / Failure Test Matrix Direction

Detailed flows nằm trong các runtime design documents. NFR yêu cầu QA coverage cho các failure classes dưới đây.

Failure Class

Test Direction

Source Design

Device Owner / DPC missing

Enter policy-required/degraded state; do not silently continue unrestricted.

Kiosk Policy + Android Operation

Lock Task unexpectedly inactive

Re-enter Lock Task when safe or enter policy recovery.

Kiosk Policy + Android Operation

User Restriction unsupported/fails

Log safe reason; apply supported subset or block if required.

Kiosk Policy + Device POC

Maintenance Mode restore fails

Remain in policy recovery/degraded state; do not return silently to field mode.

Kiosk Policy + Security

App process stopped

App restart và reconcile runtime state trước normal operation.

Android Operation Design

Foreground service interrupted

Restart hoặc reconcile dựa trên active operation.

Android Operation Design

App backgrounded by another app

Operator session phải remain active; app reopen trực tiếp.

Android Operation Design

Device reboot

Previous operator session bị expired; login screen được hiển thị.

Android Operation + SQLite Design

Unexpected stop while operator logged in

Chỉ restore session nếu same boot và session/auth data valid.

Android Operation + SQLite Design

Unexpected stop while recording

Preserve temp/final candidate và recover/mark failed an toàn.

Recording & Capture Design

Interruption during finalization

Resume finalization nếu safe hoặc mark recovery required.

Recording & Capture Design + Storage Design

Emergency recording without login

Recording start với `EMERGENCY_OVERRIDE_ADMIN` và auditable trong DB/BDMA.

Recording + Data Contract

Normal recording without login

Recording bị reject với `OPERATOR_AUTH_REQUIRED`.

Recording & Capture Design

User disabled by BDMA sync while recording

Current recording không bị interrupt; new recording bị block sau safe window.

SQLite + Android Operation

Auth method changed while session active

Apply tại safe window; chỉ require re-login nếu policy invalidates session.

Security + Android Operation

User sync conflict

Conflict được log; áp dụng default BDMA-wins rule hoặc manual flow.

Data Contract + SQLite Design

External storage unavailable

Stop/fail affected session an toàn; không corrupt media.

Storage Design

Final file exists but DB missing

Reconcile DB/file state mà không delete evidence.

SQLite Database Design + Storage Design

DB unavailable or inconsistent

Enter safe mode hoặc retry/defer mà không block active recording indefinitely.

SQLite Database Design

BDMA write-back during active runtime

Detect external write và apply/defer/reject an toàn.

SQLite Database Design

Optional runtime failure

Sensor/location/realtime analytics failure không được crash recording.

Android Operation + Device Capability Design

Update during critical operation

Update phải deferred khi bị block bởi System Settings / State Machine / Kiosk guard.

Self Update + System Settings + State Machine + Kiosk Policy

## 7. Security and Privacy NFRs

Area

Requirement

Authentication data

Phải được protect theo DCAM Security & Encryption Design.

Maintenance credential

Kiosk exit / Maintenance credential must be protected, auditable and never logged/plaintext/hardcoded.

Credential sync

Auth data do BDMA provision phải được validate trước khi use.

Session audit

Login success/failure, logout, revoke, reboot expiration và emergency override phải được log bằng safe reason codes.

Policy audit

Device policy, Lock Task, User Restrictions, Maintenance Mode and policy recovery events must be logged by safe reason codes.

Emergency audit

BDMA phải phân biệt được real operator recording và emergency override recording.

Sensitive logging

Auth/security/policy logs không được chứa sensitive auth values, maintenance credentials, enrollment secrets hoặc sensitive biometric data.

Data retention

User/auth/history/policy audit retention policy vẫn TBD nhưng phải support audit.

DB write-back safety

External user/auth changes không được làm hỏng active evidence flow.

Encryption

Media/DB/encryption decisions vẫn là security-design/ADR decisions.

## 8. Performance and Reliability NFRs

Area

Requirement

Login speed

Login phải responsive trên target BodyCamera; exact target TBD sau device profiling.

Policy verification speed

Kiosk policy verification should not delay startup beyond accepted target; exact target TBD after Device POC.

Startup readiness

System modules có thể start before login; login screen nên xuất hiện sau safe startup/recovery/policy checks.

User sync performance

User sync through ADB không được block recording/finalization hoặc critical recovery.

DB transaction reliability

User/auth/session/media transactions phải đủ atomic để tránh inconsistent operator attribution.

Background behavior

No-timeout session không được depend vào Activity instance còn sống; state phải restorable từ runtime/DB.

Reboot detection

Reboot/session boundary phải đủ reliable để ngăn stale session restore sau reboot.

Lock Task recovery

Lock Task should recover after boot/crash/update when production profile requires it.

Emergency latency

Emergency override không được bị delay bởi normal login screen nếu emergency trigger đang active.

## 9. Documentation Rule

text## 10. Remaining TBD Items

Item

Status

Exact login performance targets

TBD sau real device profiling

Exact policy verification / Lock Task recovery targets

TBD sau Device POC

Exact user sync performance targets

TBD sau ADB sync POC

Exact lockout policy values

TBD / Security Review

User/auth/history/policy audit retention duration

TBD

Face authentication implementation

TBD / Device Capability + Security Review

QR credential format

TBD

NFC tag format

TBD

DB encryption requirement

TBD

Media encryption key management

TBD

User sync conflict UI on BDMA

TBD

Final DPC ownership model

TBD / Device POC + ADR follow-up

Final User Restrictions profile

TBD / Device POC

Maintenance Mode credential/entry method

TBD / Security Review

## 11. Practical Conclusion

Non-functional Requirements giữ vai trò quality baseline.

textNext QA deliverable nên bao gồm runtime/auth/recovery/policy/failure test matrix dựa trên:

text