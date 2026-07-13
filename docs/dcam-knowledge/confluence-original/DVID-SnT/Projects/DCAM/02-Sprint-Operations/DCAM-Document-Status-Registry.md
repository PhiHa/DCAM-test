# DCAM Document Status Registry

**Page ID**: 51085647  
**Version**: 3  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/51085647

---


# DCAM Document Status Registry

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Document Status / Version Registry

Version

Approved 1.2

Status

Approved

Owner

Hoàng Ngọc Quyền

Parent Folder

02 - Sprint Operations

Target Audience

PM/BA, Tech Lead, Document Owners, QA, Reviewers, Approvers

Last Updated

2026-07-13

Related Documents

DCAM Documentation Governance, DCAM Project Home, DCAM Architecture Home, DCAM Requirements Home, DCAM Release & Build Applicability Matrix, DCAM Requirement–Design–Test Traceability Matrix

## 1. Purpose

Trang này là registry duy nhất để tổng hợp version, document status, approval scope và dependency condition của các tài liệu DCAM.

Document page metadata = trạng thái chính thức của chính tài liệu đó.
DCAM Document Status Registry = nơi tổng hợp duy nhất để review toàn bộ bộ tài liệu.
Navigation pages must not copy version/status tables.
Khi version hoặc status của một tài liệu thay đổi, document owner phải cập nhật metadata của trang và registry này trong cùng change set.

## 2. Status Taxonomy

Status

Meaning

Allowed Use

Draft

Nội dung đang soạn hoặc chưa hoàn tất review bắt buộc.

Không dùng làm production baseline.

Approved Direction

Product/architecture direction đã được chấp thuận; implementation detail hoặc evidence chưa hoàn tất.

Dùng cho planning và downstream design có điều kiện.

Approved Provisional Baseline

Baseline có thể dùng cho implementation/QA hiện tại nhưng giá trị có thể được điều chỉnh bằng evidence.

Phải ghi rõ dependency và adjustment rule.

Approved Pending Device POC

Direction/baseline đã được duyệt nhưng production validity phụ thuộc target device/model/firmware evidence.

Không được diễn giải là hardware-certified.

Approved Pending Security Review

Behavior/boundary đã được duyệt nhưng cryptographic/policy values chưa được Security Review chốt.

Không được tuyên bố production-security approved.

Approved for Build `<profile>`

Tài liệu hoặc subset đã được duyệt làm release baseline cho build cụ thể.

Chỉ có hiệu lực cho build được nêu.

Production Approved

Nội dung, dependency evidence và approval gate cần thiết cho production đã hoàn tất.

Có thể dùng làm production/release baseline.

Superseded / Archived

Không còn là nguồn hiện hành.

Chỉ dùng cho audit/history.

`Approved` không kèm qualifier chỉ được dùng khi decision complete và không còn POC/Security/Deployment dependency làm thay đổi nghĩa của baseline.

## 3. Registry Rules

Rule ID

Rule

REG-001

Không copy version/status table vào DCAM Project Home, DCAM Architecture Home hoặc DCAM Requirements Home.

REG-002

Mỗi thay đổi status phải có version message nêu rõ approval scope hoặc dependency còn lại.

REG-003

`Approved` không hợp lệ nếu bảng Approval của cùng trang còn `Pending`.

REG-004

Baseline phụ thuộc hardware phải dùng `Approved Pending Device POC` hoặc `Approved Provisional Baseline`.

REG-005

Baseline phụ thuộc algorithm/key/policy security phải dùng `Approved Pending Security Review`.

REG-006

Build release acceptance phải dùng `Approved for Build <profile>` hoặc tham chiếu Release & Build Applicability Matrix.

REG-007

Navigation pages chỉ link tới registry và authoritative pages; không pin mutable versions.

REG-008

Khi registry và page metadata khác nhau, page metadata là nguồn tức thời; registry phải được sửa trong cùng ngày và discrepancy phải được ghi nhận.

## 4. Current Document Register

Document

Version

Status

Approval Scope / Dependency

Owner

Last Reviewed

DCAM Project Home

3.33

Approved

Project navigation and current baseline summary; no copied mutable status table.

Hoàng Ngọc Quyền

2026-07-10

DCAM Project Charter

1.4

Approved

Project direction and governance; approval table completed.

Hoàng Ngọc Quyền

2026-07-10

DCAM Product Vision

2.4

Approved

Long-term product direction; build applicability belongs to Matrix.

Hoàng Ngọc Quyền

2026-07-10

DCAM Roadmap

1.5

Approved

Phase/milestone direction; build applicability belongs to Matrix.

Hoàng Ngọc Quyền

2026-07-10

DCAM MVP Scope

2.5

Approved

Working Recording Slice and Build 0.1 boundary.

Hoàng Ngọc Quyền

2026-07-10

DCAM 9-Month Development Plan

1.5

Approved

Execution plan; active scope belongs to Matrix.

Hoàng Ngọc Quyền

2026-07-10

DCAM Documentation Governance

1.14

Approved

Documentation ownership, status taxonomy, approval integrity and traceability rules.

Hoàng Ngọc Quyền

2026-07-10

DCAM Release & Build Applicability Matrix

1.0

Approved

Source of truth for build applicability.

Hoàng Ngọc Quyền

2026-07-10

DCAM Document Status Registry

1.2

Approved

Single cross-document version/status summary; Build 0.1 backlog-readiness changeset synchronized.

Hoàng Ngọc Quyền

2026-07-13

DCAM Requirements Home

1.3

Approved

Requirements navigation; no copied mutable status table.

Hoàng Ngọc Quyền

2026-07-10

DCAM Requirement–Design–Test Traceability Matrix

1.1

Approved Provisional Baseline

Image QA mappings and Build 0.1 ID groups synchronized; Jira/PR/build/test evidence remains missing.

Hoàng Ngọc Quyền

2026-07-13

01 - Recording & Capture Requirements

1.3

Approved

Encryption requirements depend on approved Security Profile; no algorithm claim.

Hoàng Ngọc Quyền

2026-07-10

02 - Media Storage Requirements

1.2

Approved

Stable IDs assigned; Build 0.1 core artifact/finalization/recovery requirements active; storage mode and physical paths remain decision/POC dependent.

Hoàng Ngọc Quyền

2026-07-13

06 - BDMA Integration Requirements

1.3

Approved

Stable IDs assigned; Build 0.1 covers ADB/sample import and logs access; full DB write-back deferred.

Hoàng Ngọc Quyền

2026-07-13

07 - Logging & Diagnostics Requirements

1.4

Approved

Stable Build 0.1 IDs assigned without behavior change; provider-specific activation remains conditional.

Hoàng Ngọc Quyền

2026-07-13

DCAM Storage Design

0.8

Approved

Logical-to-physical ownership clarified; actual paths/scoped storage/ADB visibility require Device POC.

Hoàng Ngọc Quyền

2026-07-13

DCAM SQLite Database Design

1.5

Approved Provisional Baseline

Build 0.1 semantic subset approved; physical schema, Room/raw SQLite, exact recovery and retry details require Technical Review.

Hoàng Ngọc Quyền

2026-07-13

DCAM BDMA Integration Technical Design

0.6

Draft

Build 0.1 ADB/import approval-scope candidate added; Tech Lead/BDMA Lead/QA Lead review required.

Hoàng Ngọc Quyền

2026-07-13

DCAM Concurrency & Threading Model Design

0.3

Draft

Build 0.1 execution-lane candidate extracted; required Technical Review remains open.

Hoàng Ngọc Quyền

2026-07-13

DCAM-BDMA Data Contract

1.8

Approved

Current interoperability contract; physical paths remain device-specific.

Hoàng Ngọc Quyền

2026-07-10

DCAM Non-functional Requirements

1.12

Approved

Quality direction; numeric values and hardware behavior are detailed in Performance Budget and Device POC.

Hoàng Ngọc Quyền

2026-07-10

DCAM Architecture Home

1.32

Approved

Architecture navigation/current baseline; no copied mutable status table.

Hoàng Ngọc Quyền

2026-07-10

DCAM Architecture Delivery Profile

1.1

Approved

MVP implementation guardrail; Matrix controls activation.

Hoàng Ngọc Quyền

2026-07-10

DCAM Recording & Capture Design

1.1

Approved Provisional Baseline

Build 0.1 runtime direction; SDK/device behavior remains Device POC dependent.

Hoàng Ngọc Quyền

2026-07-10

DCAM Security & Encryption Design

1.5

Approved Pending Security Review

Exact algorithms, key management, QR/update cryptography and policy values remain open.

Hoàng Ngọc Quyền

2026-07-10

DCAM Performance Budget & Resource Constraints

0.3

Approved Pending Device POC

Numeric targets are provisional until target hardware validation.

Hoàng Ngọc Quyền

2026-07-10

DCAM QA Test Strategy & Test Matrix

2.0

Approved

Dedicated image, DB, CSON and BDMA test IDs defined; DB/CSON exact physical assertions require Technical Review.

Hoàng Ngọc Quyền

2026-07-13

DCAM Device POC & Hardware Validation Report

0.5

Draft

Target model/firmware evidence not completed.

Hoàng Ngọc Quyền

2026-07-10

## 5. Change Workflow

Update authoritative document metadata
    ↓
Complete required review/approval
    ↓
Update this registry
    ↓
Validate dependent navigation links
    ↓
Update Traceability Matrix when requirement/design/test mapping changes
Project Home, Architecture Home và Requirements Home không cần cập nhật chỉ vì version của một trang thay đổi, trừ khi navigation, ownership, status summary hoặc reading order thực sự thay đổi.

## 6. Migration Note

Qualified statuses are applied immediately to documents with explicit open Device POC or Security Review dependencies when those pages are revised.

Other pages retain their current page metadata until their next material review. Their approval scope in this registry must be read together with the Applicability Matrix and authoritative dependency documents.

## 7. Practical Conclusion

One document owns its own metadata.
One registry summarizes the document set.
Navigation pages do not copy mutable version/status values.
Qualified approval statuses prevent Draft, POC-dependent and Security-dependent content from being mistaken for production approval.