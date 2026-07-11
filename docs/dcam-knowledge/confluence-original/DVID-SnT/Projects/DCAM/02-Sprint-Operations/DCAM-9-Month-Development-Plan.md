# DCAM 9-Month Development Plan

**Page ID**: 46759955  
**Version**: 8  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/46759955

---


# DCAM 9-Month Development Plan

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Development Plan

Version

Approved 1.4

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

02 - Sprint Operations

Target Audience

PM/BA, Product Owner, Tech Lead, Developers, QA, Stakeholders

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Project Home, DCAM Documentation Governance, DCAM Product Vision, DCAM Project Charter, DCAM Roadmap, DCAM MVP Scope, DCAM Architecture Delivery Profile

Duration

9 Months

Sprint Length

2-4 Weeks

## 1. Purpose

Tài liệu này mô tả kế hoạch triển khai thực tế của dự án **DCAM** trong 9 tháng.

Khác với **DCAM Roadmap**, tài liệu này tập trung vào cách đội phát triển sẽ thực hiện roadmap, bao gồm Android onboarding, sprint planning, buffer planning, delivery planning, integration strategy, risk management và release preparation.

Roadmap trả lời câu hỏi **What & When**.    
Development Plan trả lời câu hỏi **How**.

Kế hoạch implementation phải tuân theo **DCAM Architecture Delivery Profile** để tránh implement toàn bộ target architecture trước khi có working recording/storage/BDMA vertical slice.

## 2. Project Assumptions

Item

Value

Development Duration

9 months

Sprint Length

2-4 weeks

Estimated Total Sprints

18 sprints

Development Team

4 Developers

QA

Shared QA / assigned per phase

Product Owner

Internal

Project Manager

Hoàng Ngọc Quyền

Platform

Android BodyCamera

Desktop Integration

BDMA Desktop

Main Delivery Target

Customer Pilot / Production Candidate

Development Language

Java-first

Architecture Delivery Baseline

MVP first; target architecture grows by phase after working recording slice.

## 3. Current Team Situation

Team hiện tại có nền tảng tốt về Java, JavaFX/Desktop Application, BDMA domain knowledge, Git, Jira và Confluence.

DCAM là ứng dụng Android chạy trên BodyCamera, vì vậy team cần bổ sung năng lực Android Studio, Java on Android, Android Lifecycle, CameraX/Camera2, Android Storage, Runtime Permission, Foreground Service, GPS/Location Service, network basics và device-specific debugging.

Tài liệu onboarding chính cho phần này là **DCAM Android Training & Architecture Onboarding**.

Quy tắc implementation Android chính nằm trong **DCAM Android Development Standard**.

Quy trình owner/reviewer và bảo trì tài liệu nằm trong **DCAM Documentation Governance**.

Architecture delivery guardrail nằm trong **DCAM Architecture Delivery Profile**.

## 4. Development Strategy

Principle

Description

Foundation First

Làm chắc recording, capture, storage, metadata và logging trước khi mở rộng.

Working Recording First

Trước khi mở rộng platform architecture, team phải có APK chạy được trên BodyCamera và pass working recording/storage/BDMA vertical slice.

Integration Early

Đưa BDMA integration vào sớm để giảm rủi ro Data Contract thay đổi muộn.

Platform Before Communication Features

Remote Device Management và Advanced User Management được đưa vào Phase 2 để làm nền cho BDMA, Live Streaming và PTT.

Incremental Delivery

Mỗi phase cần có deliverable rõ ràng và có thể demo được.

Architecture by Delivery Profile

Target architecture được giữ, nhưng implementation module/component/state/rule phải áp dụng theo phase trong DCAM Architecture Delivery Profile.

Stabilize Before Pilot

Phase cuối tập trung hardening, QA, release documentation và customer pilot readiness.

Documentation Governance

Tài liệu chính cần tuân theo owner/reviewer/RACI và update rules trong DCAM Documentation Governance.

Delivery guardrail:

No new architecture layer/module may be added before Working Recording Slice is demoable,
unless it directly blocks recording, storage, BDMA ingest, device POC or release safety.
## 5. Overall Timeline

Stage

Timeline

Main Goal

Main Deliverable

Android Training

Week 1-2

Chuẩn bị năng lực Android cho team Java/Desktop

Android training prototype

Phase 1 - MVP Foundation

Month 1-3

Hoàn thiện recording, capture, storage, metadata, logs; pass working recording slice

DCAM MVP Internal Build 0.1

Phase 2 - Platform Foundation & BDMA Integration

Month 4-5

Hoàn thiện BDMA compatibility, Device/User foundation, reliability và basic security

Secure Platform MVP Build 0.2

Phase 3 - Advanced Communication

Month 6-7

Triển khai Live Streaming, PTT, GPS Route và Advanced Encryption

Advanced Communication Beta Build 0.3

Phase 4 - Hardening & Customer Pilot

Month 8-9

Ổn định hệ thống và chuẩn bị pilot

Customer Pilot Release / Production Candidate

## 6. Android Training Plan

Item

Description

Duration

Week 1-2

Objective

Đảm bảo team có đủ năng lực cơ bản để phát triển Android bằng Java trên BodyCamera.

Output

Android training prototype và development readiness.

Main Reference

DCAM Android Training & Architecture Onboarding

### 6.1 Training Topics

Topic

Expected Outcome

Android Studio / Gradle

Dev build và debug được Android project.

Java on Android

Dev hiểu khác biệt giữa Java Desktop và Android Java.

Activity / Fragment / Lifecycle

Dev hiểu vòng đời app và UI state.

Runtime Permission

Dev xử lý được camera, microphone, location, storage permission.

CameraX / Camera2

Dev tạo được sample record/capture.

Storage

Dev lưu được file vào đúng folder test.

GPS / Location

Dev lấy được location sample nếu thiết bị hỗ trợ.

Foreground Service

Dev hiểu cách chạy tác vụ dài như recording.

Android Debugging Basics

Dev đọc được Logcat và debug được app trên thiết bị thật.

Architecture Delivery Profile

Dev hiểu MVP module set, deferred modules và working recording slice gate.

### 6.2 Exit Criteria

Criteria

Required

Build Android project successfully

Yes

Run app on BodyCamera

Yes

Capture image sample

Yes

Record video sample

Yes

Debug logs from device

Yes

Understand MVP vs Target Architecture split

Yes

## 7. Phase 1 - MVP Foundation

Item

Description

Timeline

Month 1-3

Target Build

DCAM MVP Internal Build 0.1

Buffer

20%

Architecture Profile

MVP Implementation Architecture only; no full target architecture expansion before Working Recording Slice.

### 7.1 Objectives

Phase 1 tập trung xây dựng các chức năng cốt lõi của DCAM:

Video Recording.

Image Capture.

Local Storage.

Metadata Generation.

Logging.

Basic Device Status.

Working Recording Slice.

### 7.2 Technical Focus

Area

Focus

Camera Module

Start/stop recording, capture image, recording state.

Storage Module

Folder structure, file naming, local media storage, temp/final handling.

Metadata Module

Generate metadata for video/image where supported by MVP contract.

Logging Module

App log, recording log, capture log, error log.

Device Status

Battery, storage, GPS availability.

BDMA Export

Ensure BDMA can detect/import sample media through ADB.

MVP Architecture

Small module set, small runtime component set, no unnecessary Gradle/module split.

Working Recording Slice

App can record 30s video, finalize file, write minimal DB/config/log output and BDMA can import sample.

### 7.3 Deliverables

Deliverable

Description

DCAM MVP Internal Build 0.1

Internal build with recording/capture/storage/metadata/logging.

Working Recording Slice Demo

Demo runnable APK on BodyCamera: record/capture/save/finalize/BDMA import.

MVP Demo

Demo basic capture/recording workflow.

MVP Test Checklist

Basic test checklist for core flows.

Known Issues List

Known issues from BodyCamera testing.

### 7.4 Phase 1 Architecture Guardrails

Phase 1 must follow the limits from **DCAM Architecture Delivery Profile**:

≤ 5 Gradle modules
≤ 8 logical modules
≤ 10 runtime components
≤ 7 runtime states
≤ 8 P0 dependency rules
1 working recording/storage/BDMA vertical slice before platform expansion
Phase 1 deferred modules:

AI Detection
Sensor Monitoring advanced
Remote Config apply
Self Update
Play Store fallback
Full In-App Console advanced
Full Kiosk Policy stack
Advanced User Management
Full Feature Eligibility engine
Full Runtime Module Registry
Full State Machine Coordinator
## 8. Phase 2 - Platform Foundation & BDMA Integration

Item

Description

Timeline

Month 4-5

Target Build

Secure Platform MVP Build 0.2

Buffer

20%

### 8.1 Objectives

Phase 2 tập trung đảm bảo DCAM tương thích với BDMA, đồng thời xây dựng sớm nền tảng Device/User để tránh refactor lớn ở các phase sau.

Phase 2 chỉ bắt đầu mở rộng platform architecture sau khi Phase 1 đã có working recording/storage/BDMA slice demoable.

### 8.2 Main Scope

Area

Description

DCAM-BDMA Data Contract

Chuẩn hóa folder structure, metadata fields, status và schema version.

BDMA Ingest

BDMA đọc được media và metadata từ DCAM.

Metadata Mapping

Mapping đúng file, device, timestamp, GPS, status.

File Recovery

Xử lý file pending/corrupted/incomplete ở mức cơ bản.

GPS per Media File

Ghi GPS cho từng media file nếu thiết bị có dữ liệu hợp lệ.

Remote Device Management

Basic status/config read-write foundation.

Device Information

Device ID, model, firmware/app version, storage, battery, network status.

Advanced User Management

Basic user/profile/operator/role/permission foundation.

Basic Encryption

Basic encryption scope for media/metadata based on agreed design.

Kiosk / Device Owner Baseline

Begin Device Owner/DPC baseline only after device POC confirms feasibility.

Remote Config Foundation

Fetch/cache foundation may start; apply policy must follow guard rules.

Self Update Foundation

Basic APK update path may start after recording/storage stability is protected.

### 8.3 Deliverables

Deliverable

Description

Secure Platform MVP Build 0.2

Build with BDMA integration, Device/User foundation and basic security.

DCAM-BDMA E2E Demo

BDMA ingest and display DCAM data end-to-end.

Data Contract Version 1

Initial agreed contract between DCAM and BDMA.

Device/User Foundation Demo

Demo device status/config and user/operator mapping foundation.

Integration Test Report

Test result for BDMA ingest and metadata mapping.

## 9. Phase 3 - Advanced Communication

Item

Description

Timeline

Month 6-7

Target Build

Advanced Communication Beta Build 0.3

### 9.1 Objectives

Phase 3 tập trung vào các năng lực giao tiếp/thời gian thực và capability nâng cao dựa trên nền tảng Device/User đã hoàn thiện ở Phase 2.

### 9.2 Scope

Area

Description

Live Streaming

Streaming video ở mức beta/basic implementation.

Push-to-Talk (PTT)

Trigger, audio capture/transmission, state logging, basic error handling.

Full GPS Tracking Route

Route recording by session.

Advanced Encryption

Extended encryption scope beyond basic encryption.

Reconnect / Timeout Handling

Basic handling for weak network, disconnect, timeout.

Streaming/PTT Logs

Log state, error, reconnect, timeout and operational status.

Sensor / AI Optional Expansion

Only after recording/storage/platform foundation is stable and approved.

## 10. Phase 4 - Hardening & Customer Pilot

Item

Description

Timeline

Month 8-9

Target Build

Customer Pilot Release / Production Candidate

### 10.1 Main Activities

Activity

Description

Regression Testing

Test lại toàn bộ core flows.

Stability Testing

Test app stability trên BodyCamera trong thời gian dài.

Performance Testing

Kiểm tra CPU, memory, battery, storage.

Streaming/PTT Testing

Test network yếu, reconnect, timeout, interruption.

GPS Route Testing

Test GPS availability, route recording and fallback.

Security Review

Review encryption/security scope.

Architecture Debt Review

Check module/component growth against Architecture Delivery Profile.

Bug Fixing

Fix critical and high-priority bugs.

Release Documentation

Release notes, install guide, test report, known issues.

### 10.2 Deliverables

Deliverable

Description

Release Candidate

Build sẵn sàng cho customer pilot.

Customer Pilot Release

Pilot build cho khách hàng chọn lọc.

Release Notes

Ghi chú phát hành.

Installation Guide

Hướng dẫn cài đặt trên BodyCamera.

Test Report

Báo cáo kiểm thử.

Known Issues

Danh sách lỗi đã biết và workaround.

## 11. Sprint Allocation

Sprint Range

Timeline

Main Focus

Sprint 1

Week 1-2

Android Training, environment setup and Architecture Delivery Profile onboarding.

Sprint 2

Week 3-4

Camera prototype, basic record/capture proof of concept.

Sprint 3

Week 5-6

Working Recording Slice: record 30s, capture image, save/finalize file on BodyCamera.

Sprint 4

Week 7-8

Minimal DB/config/log output and BDMA import sample media.

Sprint 5-6

Week 9-12

MVP Foundation hardening: recording, capture, storage, metadata, logging and MVP test checklist.

Sprint 7-10

Week 13-20

BDMA integration, Data Contract, Device/User foundation, basic encryption.

Sprint 11

Week 21-22

Phase 2 stabilization, integration testing and Secure Platform MVP 0.2.

Sprint 12-14

Week 23-28

Live Streaming, PTT, GPS route, advanced encryption implementation.

Sprint 15-16

Week 29-32

Advanced communication hardening and beta validation.

Sprint 17

Week 33-36

Regression, performance, stability, security review.

Sprint 18

Week 37-40

Release candidate, customer pilot preparation and documentation.

## 12. Team Responsibilities

Role

Responsibility

Product Owner

Manage product direction, scope priority and acceptance direction.

Project Manager / BA

Plan execution, track progress, manage requirements, coordinate releases and documentation.

Tech Lead

Own architecture, technical decisions, code review direction, implementation quality and Architecture Delivery Profile enforcement.

Android Developers

Implement DCAM Android features, create/update technical design and fix bugs.

BDMA Team

Support Data Contract, ingest, metadata mapping and end-to-end testing.

QA

Build test checklist, execute regression/integration/stability tests and report bugs.

## 13. Buffer Strategy

Phase

Buffer

Purpose

Android Training

Included in Week 1-2

Reduce Android onboarding risk.

Phase 1

20%

Android learning curve, camera compatibility, BodyCamera hardware issues and working recording slice risk.

Phase 2

20%

BDMA integration, Data Contract change, Device/User model adjustment and encryption performance.

Phase 3

Standard management buffer

Network, streaming/PTT and GPS route risks.

Phase 4

Stabilization buffer

Bug fixing, release readiness and customer pilot preparation.

Buffer không dùng để thêm scope mới ngoài phạm vi đã được duyệt.

## 14. Risk Management

Risk

Impact

Mitigation

Android learning curve

Delay in early phase

Android training, onboarding checklist, prototype and code review.

Camera compatibility

Recording/capture instability

Test early on BodyCamera hardware.

BodyCamera hardware limitation

Performance, storage, battery or permission issues

Device-specific testing and fallback design.

Analysis-paralysis / over-architecture

Low development velocity, delayed recording demo

Enforce Architecture Delivery Profile; working recording slice before platform expansion.

Too many Gradle modules too early

Slow build, complex dependency management, team confusion

Keep MVP module count small; split package into module only with approved trigger.

BDMA Data Contract changes

Rework for DCAM and BDMA

Define Data Contract early and version it.

Device/User model changes

Rework in Phase 2/3

Build foundation early and review with BDMA.

Encryption performance

App slow or recording instability

Benchmark on device before finalizing design.

GPS instability

Missing/incorrect GPS data

Support unavailable/invalid GPS state and fallback.

Streaming/PTT network issue

Poor beta quality

Test weak network, reconnect and timeout.

Scope creep

Delay release

Use Jira, PM review and change control.

## 15. Major Milestones

Milestone

Target Time

Success Condition

M0 - Android Training Complete

End of Week 2

Team can build, run, debug and create camera sample on BodyCamera.

M1 - Camera Prototype

End of Week 6

Video recording and image capture work on BodyCamera.

M1.5 - Working Recording Slice

End of Week 8

App can record 30s, capture image, finalize media, write minimal DB/config/log output and BDMA can detect/import sample media.

M2 - DCAM MVP Internal 0.1

End of Week 12

Recording/capture/storage/metadata/logging work reliably.

M3 - DCAM-BDMA E2E Demo

End of Week 16

BDMA can ingest and display DCAM data.

M4 - Secure Platform MVP 0.2

End of Week 22

BDMA integration, basic encryption, Remote Device Management and Advanced User Management foundation are available.

M5 - Advanced Communication Beta 0.3

End of Week 32

Live Streaming beta, PTT beta, GPS route v1 and advanced encryption are available.

M6 - Release Candidate

End of Week 39

Critical bugs fixed and pilot documentation prepared.

M7 - Customer Pilot Release

End of Week 40

Pilot build and release/test documents are available.

## 16. Success Criteria

Criteria

Required

Android onboarding completed

Yes

Working Recording Slice completed before optional platform expansion

Yes

DCAM MVP Foundation completed

Yes

BDMA integration works end-to-end

Yes

Remote Device Management foundation completed

Yes

Advanced User Management foundation completed

Yes

Basic encryption implemented and tested

Yes

Live Streaming reaches beta/basic level

Yes

PTT reaches beta/basic level

Yes

GPS route v1 works in supported conditions

Yes

No critical blocker remains before pilot

Yes

Release documentation is available

Yes

Customer Pilot build is available

Yes

## 17. Documentation Deliverables by Phase

Phase

Required Documents

Current Baseline

DCAM Project Home, DCAM Documentation Governance, DCAM Architecture Home, DCAM Architecture Delivery Profile, DCAM Android Development Standard.

Android Training

DCAM Android Training & Architecture Onboarding, Camera Training Prototype Notes.

Phase 1

DCAM Functional Requirements, MVP Test Checklist, Working Recording Slice Notes, Known Issues, MVP Release Notes.

Phase 2

DCAM-BDMA Data Contract, Device/User Foundation Notes, Integration Test Report.

Phase 3

Live Streaming & PTT Design, GPS Route Design, Advanced Feature Test Checklist.

Phase 4

Release Notes, Installation Guide, Test Report, Known Issues, Customer Pilot Notes.

## 18. Related Documents

Document

Purpose

DCAM Project Home

Main documentation hub and current documentation status.

DCAM Documentation Governance

Owner, reviewer, RACI, lifecycle and update rules for DCAM documents.

DCAM Architecture Delivery Profile

Defines MVP vs target architecture delivery guardrails and working recording slice gate.

DCAM Product Vision

Product direction and long-term positioning.

DCAM Project Charter

Project authorization, scope, governance and approval baseline.

DCAM Roadmap

Product roadmap, phase objectives, milestones and feature roadmap.

DCAM MVP Scope

MVP scope, acceptance criteria and exit criteria.

DCAM Android Training & Architecture Onboarding

Android onboarding for Java/Desktop team.

DCAM Android Development Standard

Project-specific Android implementation standard.

DCAM Architecture Home

Official Software Architecture Home and SAD document set.

DCAM Functional Requirements

Detailed functional requirements.

DCAM Non-functional Requirements

Reliability, performance, security and operational requirements.

DCAM-BDMA Data Contract

Data contract between DCAM Android and BDMA Desktop.

DCAM Release Plan

Build, versioning, release and pilot process.

## 19. Practical Conclusion

Mục tiêu thực tế của kế hoạch 9 tháng là:

Android readiness + Working Recording Slice + DCAM stable MVP + BDMA ingest
+ Device/User platform foundation + advanced communication beta + customer pilot release.
Development Plan này không thay thế Roadmap. Tài liệu này dùng để quản lý cách triển khai roadmap thành sprint, deliverable, milestone, buffer và release readiness.

Tài liệu này cũng không thay thế DCAM Project Home hoặc DCAM Documentation Governance. Project Home phản ánh trạng thái tài liệu hiện tại; Documentation Governance quy định owner, reviewer, lifecycle và update rules.

Architecture delivery baseline hiện tại là:

Target Architecture remains valid.
MVP Implementation Architecture is intentionally smaller.
Working recording/storage/BDMA slice must come first.
Additional managers/coordinators/modules become mandatory only when their feature enters scope.