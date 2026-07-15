# DCAM Requirement–Design–Test Traceability Matrix

**Page ID**: 51085669  
**Version**: 6  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/51085669

---


# DCAM Requirement–Design–Test Traceability Matrix

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Requirement–Design–Test Traceability Matrix

Version

Approved Provisional Baseline 1.5

Status

Approved Provisional Baseline

Approval Scope

DEC-01–DEC-07 Build 0.1 backlog-readiness traceability, gồm Important Media Conditional — Not Activated; chưa phải release acceptance khi Jira/evidence còn thiếu.

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / QA Lead / BDMA Lead

Approver

Hoàng Ngọc Quyền

Parent Page

DCAM Requirements Home

Target Audience

PM/BA, Tech Lead, Developers, QA, BDMA Team, Security Reviewer

Last Updated

2026-07-14

Related Jira

Not linked

Dependencies / Blockers

Jira implementation linkage; authoritative GitHub mapping; PR/build/test evidence; Technical Review cho Camera/path/DB/CSON; Device POC execution.

Related Documents

DCAM Release & Build Applicability Matrix, DCAM Requirements Home, DCAM Architecture Home, DCAM QA Test Strategy & Test Matrix, DCAM Documentation Governance, DCAM Document Status Registry, Decision Brief – DCAM MVP Internal Build 0.1 – Working Recording Slice

## 1. Purpose

Trang này là source of truth cho traceability giữa:

Requirement ID
    → Build Applicability
    → Architecture / Technical Design section
    → QA Test ID
    → Jira implementation item
    → Verification evidence
Tài liệu này không copy toàn bộ requirement hoặc test procedure. Mỗi dòng chỉ lưu ID, link logic, ownership và trạng thái coverage.

## 2. Traceability Rules

Rule ID

Rule

TRACE-001

Mỗi requirement được activate cho một build phải map tới ít nhất một Design/Contract section và một QA Test ID.

TRACE-002

`P0` không tự động có nghĩa là release blocker; Build Applicability quyết định requirement/test có bắt buộc hay không.

TRACE-003

Jira implementation item phải ghi Build Profile, Requirement Inputs, Design Inputs và QA Group.

TRACE-004

Không đánh dấu `Covered` khi mới có document link nhưng chưa có QA ID hoặc evidence.

TRACE-005

Khi requirement behavior thay đổi, cập nhật Requirements trước, sau đó Design, QA và traceability row.

TRACE-006

Khi chỉ thay đổi build applicability, cập nhật Release & Build Applicability Matrix và cột Build; không sửa requirement behavior.

TRACE-007

Security/POC-dependent row phải ghi rõ blocker và không được diễn giải là Production Approved.

## 3. Coverage Status

Status

Meaning

Covered

Có requirement, design/contract, QA ID và evidence hoặc execution owner rõ ràng.

Partially Covered

Có một phần mapping nhưng còn thiếu QA ID, Jira hoặc evidence.

Planned

Mapping đã xác định nhưng feature chưa active cho current build.

Blocked

Chờ Device POC, Security Review, Product decision hoặc deployment decision.

Gap

Requirement active nhưng chưa có design/test mapping đủ.

## 4. Build 0.1 – Working Recording Slice Traceability

Requirement / Baseline ID

Build 0.1

Design / Contract Mapping

QA Mapping

Jira

Evidence

Coverage

REC-VID-001

Required

DCAM Recording & Capture Design §2; DCAM-BDMA Data Contract §7

QA-REC-001

Not linked

Working Recording Slice video result

Partially Covered

REC-VID-002

Required

DCAM-BDMA Data Contract §7/§16.4; DCAM Recording & Capture Design §5

QA-MEDIA-NAME-001/002, QA-BDMA-001

Not linked

Filename token validation và final media import evidence not attached

Partially Covered

REC-VID-003

Conditional — Not Activated

DCAM-BDMA Data Contract §6–§9; Recording Design §6; Release & Build Applicability Matrix §10

QA-MEDIA-IMP-001

Not linked

Not required while activation condition is false

Planned

REC-VID-004

Conditional when approved media-encryption profile is enabled

DCAM Security & Encryption Design §7; DCAM-BDMA Data Contract §7

QA gap: encrypted naming + decryptability test ID required

Not linked

Security Review pending

Blocked

REC-VID-005 / REC-VID-007 / REC-VID-008

Required

Data Contract §16.2; Recording Design §12; Storage Design §11

QA-BDMA-001/002/003/007, QA-PERF-003

Not linked

MP4/MD5 timestamps, failure/import result not attached

Partially Covered

REC-VID-006

Required for active core flow

Recording Design §5; Concurrency & Threading Model; Logging Design

QA-LOG-004, QA-UPD-002 when update becomes active

Not linked

Provider-outage/core-flow result

Partially Covered

REC-IMG-001

Required

Recording Design §7; Data Contract §7

QA-IMG-001

Not linked

Working Recording Slice image result not attached

Partially Covered

REC-IMG-002

Required

Data Contract §7/§16.4

QA-IMG-002, QA-MEDIA-NAME-001/002

Not linked

Filename token validation và BDMA sample import evidence not attached

Partially Covered

REC-IMG-004

Required

Data Contract §8

QA-IMG-002, QA-BDMA-004

Not linked

Image no-MD5 import evidence not attached

Partially Covered

REC-IMG-005

Required

Recording Design §7–8

QA-IMG-004

Not linked

Failure/recovery evidence not attached

Partially Covered

REC-STATE-001

Required subset

Recording Design §2; Architecture Delivery Profile §6

QA-REC-001, QA-STO-001, QA-STO-002

Not linked

State transition logs

Partially Covered

REC-STATE-005

Required

Recording Design §8; Storage Design recovery section

QA-PERF-008 plus recovery test group

Not linked

Crash/storage recovery result

Partially Covered

PERF-REC-001

Required

Performance Budget §4.1

QA-PERF-001

Not linked

`[PERF] recording_start_latency_ms`

Partially Covered

PERF-REC-002

Required

Performance Budget §4.1

QA-PERF-002

Not linked

`[PERF] recording_stop_latency_ms`

Partially Covered

PERF-REC-003

Required

Performance Budget §4.1–4.2; Recording Design §5

QA-PERF-003

Not linked

`critical_finalization_latency_ms` + `BDMA_READY`

Partially Covered

PERF-REC-004

Required

Performance Budget §4.1

QA-IMG-003

Not linked

Image capture timing not attached

Partially Covered

PERF-REC-006

Required

Performance Budget §4.1; Recording Design §3

QA-PERF-007 partially covers precheck constraints; dedicated timing assertion required

Not linked

Precheck timing

Partially Covered

PERF-ANR-001

Required

Performance Budget §4.7; Android Development Standard

QA-PERF-004

Not linked

StrictMode / timing evidence

Partially Covered

PERF-ANR-002/003

Required

Performance Budget §4.7; Concurrency Design

QA-PERF-005

Not linked

Queue/callback timing

Partially Covered

PERF-IO-004 / PERF-ANR-007

Required

Performance Budget §4.4/4.7; SQLite Design

QA-PERF-006

Not linked

DB timing/retry logs

Partially Covered

PERF-STOR-001/002/005

Required

Performance Budget §4.5; Storage Design

QA-PERF-007, QA-PERF-008

Not linked

Near-full/storage-full result

Partially Covered

PERF-STAB-001/003/004/005

Required

Performance Budget §4.8

QA-PERF-009, QA-PERF-010

Not linked

Long-running test report

Partially Covered

DATA-CONTRACT-MVP

Required

DCAM-BDMA Data Contract §16

QA-BDMA-001/002/003/004/005/006/007

Not linked

BDMA sample import and error evidence not attached

Partially Covered

STO-LOC-001 / STO-LOC-002 / STO-MODE-001 / STO-PRE-001 / STO-FAIL-001/002

Internal Required; External/Auto Not Applicable

Media Storage Requirements §2/§4; Storage Design §11; Data Contract §16.1

QA-STO-003/004, QA-PERF-007/008

Not linked

Internal path and failure evidence Pending Device POC

Blocked

STO-ART-001 / STO-TEMP-001 / STO-FINAL-001 / STO-REC-001

Required

Media Storage Requirements §2; Storage Design §§4–7; Data Contract

QA-STO-001/002, QA-DB-002/003, QA-CSON-001/002, QA-BDMA-005

Not linked

DB/CSON/storage recovery evidence not attached

Partially Covered

BDMA-INT-001 / BDMA-INT-002 / BDMA-INT-003 / BDMA-INT-004

Required

BDMA Integration Requirements §3; BDMA Integration Technical Design §7; Data Contract

QA-BDMA-001/003/004/005/006

Not linked

BDMA import/error evidence not attached

Partially Covered

BDMA-INT-005

Deferred for Build 0.1 full write-back scope

SQLite Design; Data Contract

QA-DB-001

Not linked

Full write-back excluded from Build 0.1 approval candidate

Planned

BDMA-INT-006

Required

BDMA Integration Requirements §3; Data Contract logs contract

QA-LOG-009

Not linked

logs.txt access evidence not attached

Partially Covered

LOG-LOCAL-001…007 / LOG-EVT-APP-001 / LOG-EVT-REC-001 / LOG-EVT-STO-001 / LOG-EVT-PERF-001 / LOG-EVT-BDMA-001 / LOG-QUAL-001…008 / LOG-SEC-001 / LOG-DIAG-REC-001 / LOG-DIAG-STO-001 / LOG-DIAG-BDMA-001

Required subset / Conditional where provider-specific

Logging & Diagnostics Requirements §12; Logging Design

QA-LOG-001/002/004/007/008/009/010

Not linked

Local/logs.txt/sanitization evidence not attached

Partially Covered

DB-SCHEMA-001 / DB-MEDIA-001 / DB-FINAL-001 / DB-BDMA-001 / DB-REC-001 / DB-OWN-001

Required semantic subset

SQLite Database Design §18; Storage Design; Data Contract

QA-DB-002/003, QA-STO-002, QA-PERF-006

Not linked

Technical Review and DB execution evidence pending

Partially Covered

WRS-001…009

Required gate

Architecture Delivery Profile §8; MVP Scope §4–7

Build 0.1 Working Recording Slice checklist

Not linked

Runnable APK + sample artifacts + BDMA import

Partially Covered

## 5. Deferred / Later-build Traceability Groups

Group

Build 0.1

Target Build

Primary Requirement / Design

QA Group

Current Status

Cloud identity / Web Portal provisioning

Deferred

0.2

Device Configuration Requirements; Web Portal Design/App/Implementation; API Contract

QA-PROV-*

Planned

Full User/Auth

Deferred

0.2

User & Device Operation Requirements; Android Operation Design

QA-AUTH-*

Planned

Device Owner / Kiosk / Maintenance

Deferred; POC only if explicitly activated

0.2+

Android Device Operation Requirements; Kiosk Policy Design; ADR

QA-KIOSK-*, QA-CONSOLE-*

Blocked by Device POC

Remote Config

Deferred

0.2

System Settings Requirements; Cloud Architecture

QA-RCFG-*

Planned

Self Update

Deferred

0.2

System Settings Requirements; Self Update Design

QA-UPD-*

Planned / Device POC dependent

Factory READY_TO_SHIP

Not Applicable

Pilot

Factory SOP; Security Design; Kiosk Design

QA-FACTORY-*

Planned

Encryption implementation

Deferred / Conditional

0.2+

Security & Encryption Requirements/Design; Data Contract

QA-SEC-* plus encryption test IDs to add

Blocked by Security Review

Live Streaming / PTT / Full GPS Route

Not Applicable

0.3

Future requirements/designs

Future QA groups

Planned

## 6. Immediate Coverage Backlog

Priority

Gap

Required Action

Owner

P0

Image QA IDs đã được map nhưng chưa có Jira/evidence execution.

Link Jira implementation/test tasks và attach result cho QA-IMG-001…004, QA-BDMA-004.

PM / QA Lead / Android Lead

P0

Working Recording Slice has no single evidence record/Jira link.

Create Jira Epic/Task set and link runnable APK, artifacts and BDMA import report.

PM / Tech Lead

P0

Requirement rows have `Related Jira = None`.

Link active Build 0.1 Jira items using TRACE-003 fields.

PM / Developers

P1

Encryption QA coverage is incomplete.

Add test IDs only after approved Security Profile defines algorithm/key/decryption boundary.

Security Reviewer / QA

P1

Remaining Functional Requirements 02–10 are mapped only at document level.

Expand this matrix by requirement ID when each feature becomes active.

BA / Document Owners

## 7. Update Workflow

Requirement created/changed
    ↓
Assign stable Requirement ID
    ↓
Resolve Build Applicability
    ↓
Link Design / Contract section
    ↓
Create or link QA Test ID
    ↓
Link Jira implementation item
    ↓
Attach verification evidence
    ↓
Mark Covered
## 8. Practical Conclusion

Document links alone are not complete traceability.
An active requirement is covered only when Requirement → Build → Design → QA → Jira → Evidence is visible.
Build 0.1 đã có image QA mapping, dedicated DB/CSON/BDMA test IDs và QA-MEDIA-IMP-001 cho Important Media `Conditional — Not Activated`. Các gap đang active còn lại thuộc Jira linkage, execution evidence và Technical Review/Device POC decisions.
## 9. DEC-01–DEC-07 Controlled Traceability

Requirement / Decision ID

Build

Design / Contract

QA Test ID

Jira

Evidence

Coverage Status

DEC-01 / WRS-AC-001

DCAM MVP Internal Build 0.1

Architecture Delivery Profile §13; Recording Design §12; Platform Strategy §11; Device POC §16

QA-WRS-DEV-001, POC-WRS-001/002

Not linked

Physical device/configuration and Camera API capability evidence Pending Device POC

Blocked

DEC-02 / STO-LOC-001 / STO-PRE-001 / STO-FAIL-001/002

DCAM MVP Internal Build 0.1

Storage Design §11; Data Contract §16.1

QA-STO-003/004, POC-WRS-003/004

Not linked

Internal path, pre-check and safe-stop evidence not attached

Blocked

DEC-03 / REC-VID-005/007/008 / BDMA-B01-002/003/004

DCAM MVP Internal Build 0.1

Recording Design §12; Storage Design §11; Data Contract §16.2; BDMA Design §13

QA-PERF-003, QA-BDMA-001/002/003/007, POC-WRS-005

Not linked

Finalize/MD5/readiness/import evidence not attached

Partially Covered

DEC-04 / UDO-B01-001…004 / CFG-B01-001/002 / LOG-B01-OP-001

DCAM MVP Internal Build 0.1

State Machine §16; Android Operation §12; SQLite §18; Data Contract §16.3; Logging Design §16

QA-WRS-OP-001, POC-WRS-006

Not linked

SQLite/CSON/log artifact evidence và schema Technical Review pending

Partially Covered

DEC-05 / LOG-B01-STATUS-001

DCAM MVP Internal Build 0.1

Delivery Profile §13; Android Operation §12; Device POC §16

QA-WRS-STATUS-001, POC-WRS-007

Not linked

Battery/storage/GPS status evidence not attached

Partially Covered

DEC-06 / WRS-AC-006

DCAM MVP Internal Build 0.1

MVP Scope §10; Delivery Profile §13; Device POC §16

QA-WRS-DEV-001, POC-WRS-001/008

Not linked

End-to-end WRS evidence from identifiable physical device not attached

Blocked

DEC-07 / REC-VID-002 / REC-IMG-002 / WRS-AC-007

DCAM MVP Internal Build 0.1

Data Contract §7/§16.4; Data Architecture §8; BDMA Design §13

QA-MEDIA-NAME-001/002, POC-WRS-009

Not linked

Actual serial_number, generated filename, parser/import và contract-version evidence not attached; timezone/collision Technical Review open

Partially Covered

### Coverage Interpretation

Approved decisions và documentation mappings đã có.

Jira linkage, authoritative GitHub repository mapping và execution evidence chưa có.

Pending Device POC rows không được chuyển sang Covered/Passed trước khi evidence được gắn.

Build 0.1 result không đại diện Production, fleet, multi-model hoặc multi-firmware readiness.