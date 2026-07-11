# DCAM Phase 2 Documentation Plan

**Page ID**: 41189378  
**Version**: 10  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/41189378

---


# DCAM Phase 2 Documentation Plan

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Documentation Plan

Version

Draft 2.4

Status

Archived

Owner

Hoàng Ngọc Quyền

Last Updated

2026-07-03

## 1. Purpose

Tài liệu này mô tả kế hoạch xây dựng và duy trì bộ tài liệu nền cho dự án **DCAM**.

Hiện tại, DCAM đã có các tài liệu Product Management baseline, Sprint Operations baseline, Android onboarding cho team Java/Desktop, bộ Software Architecture baseline dạng nhiều trang ngắn, **DCAM Android Development Standard** cho rule implementation riêng của dự án và **DCAM Documentation Governance** cho ownership/RACI/workflow tài liệu.

Trọng tâm tiếp theo là hoàn thiện **Requirements**, **DCAM-BDMA Data Contract** và các tài liệu **Technical Design** để có thể phân rã Jira backlog và triển khai development ổn định.

## 2. Current Documentation Structure

Cấu trúc tài liệu hiện tại của DCAM:

DCAM
├── DCAM Project Home
├── 01 - Product Management
│   ├── DCAM Project Charter
│   ├── DCAM Roadmap
│   ├── DCAM Product Vision
│   └── DCAM MVP Scope
│
├── 02 - Sprint Operations
│   ├── DCAM Phase 2 Documentation Plan
│   ├── DCAM 9-Month Development Plan
│   └── DCAM Documentation Governance
│
├── 03 - Requirements
│
├── 04 - Technical Documentation
│   ├── 4.1 - Software Architecture
│   │   └── DCAM Architecture Home
│   │       ├── 01 - Architecture Overview
│   │       ├── 02 - Architecture Principles
│   │       ├── 03 - Android Platform & Compatibility Strategy
│   │       ├── 04 - Application & Module Architecture
│   │       ├── 05 - Data, Storage & BDMA Architecture
│   │       ├── 06 - Cloud Services, Update & Configuration Architecture
│   │       ├── 07 - Logging, Diagnostics, Performance & Security
│   │       └── 08 - DCAM-BDMA Integration Boundary
│   │
│   ├── 4.2 - Technical Design
│   ├── 4.3 - Android Development
│   │   ├── DCAM Android Training & Architecture Onboarding
│   │   └── DCAM Android Development Standard
│   └── 4.4 - Architecture Decision Records (ADR)
│
├── 05 - Release Management
└── 06 - Incident Log
## 3. Current Documentation Status

Area

Status

Current Documents

Comment

Project Home

Created

DCAM Project Home

Trang điều hướng trung tâm đã có.

Product Management

Baseline Completed

Product Vision, Project Charter, Roadmap, MVP Scope

Nhóm tài liệu định hướng sản phẩm/dự án đã đủ baseline và đã align theo Roadmap mới.

Sprint Operations

Baseline Created

Phase 2 Documentation Plan, 9-Month Development Plan, Documentation Governance

Đã có kế hoạch tài liệu, kế hoạch triển khai 9 tháng và governance/RACI cho tài liệu.

Requirements

Empty

None

Cần ưu tiên Functional Requirements và DCAM-BDMA Data Contract.

Software Architecture

Baseline Created

DCAM Architecture Home + SAD pages 01-08

Đã có baseline kiến trúc, gồm Cloud Services abstraction và DCAM-BDMA Integration Boundary.

Android Development

Started

DCAM Android Training & Architecture Onboarding, DCAM Android Development Standard

Đã có onboarding Android và standard implementation riêng cho DCAM.

Technical Design

Empty

None

Cần tạo Storage Design, Metadata Design, Security Design và các design chi tiết khác.

Release Management

Empty

None

Cần Release Plan trước giai đoạn pilot/release.

Incident Log

Empty

None

Chưa cần ưu tiên ngay, dùng khi có pilot/incident thực tế.

## 4. Completed Baseline Documents

Document

Folder

Status

Purpose

DCAM Project Home

DCAM root

Created / Updated

Trang điều hướng trung tâm cho toàn bộ tài liệu DCAM.

DCAM Product Vision

01 - Product Management

Created / Updated

Định hướng sản phẩm, target users, product principles, relationship với BDMA và Product Evolution Roadmap.

DCAM Project Charter

01 - Product Management

Created

Project authorization, scope, stakeholder, governance, risks và approval baseline.

DCAM Roadmap

01 - Product Management

Created / Updated

Roadmap 9 tháng, phase objectives, milestones và feature roadmap.

DCAM MVP Scope

01 - Product Management

Created / Updated

MVP scope, in-scope, out-of-scope, acceptance criteria và exit criteria.

DCAM 9-Month Development Plan

02 - Sprint Operations

Created / Updated

Kế hoạch triển khai roadmap thành phase, sprint, buffer, milestone và release readiness.

DCAM Phase 2 Documentation Plan

02 - Sprint Operations

Created / Updated

Kế hoạch hoàn thiện bộ tài liệu nền DCAM.

DCAM Documentation Governance

02 - Sprint Operations

Created

Quy định owner, reviewer, RACI, workflow, lifecycle, quality checklist và update rules cho tài liệu DCAM.

DCAM Android Training & Architecture Onboarding

4.3 - Android Development

Created

Onboarding Android cho team Java/Desktop trước khi vào implementation.

DCAM Android Development Standard

4.3 - Android Development

Created

Standard riêng cho DCAM Android: Service Interface, Platform Adapter, HandlerThread, ExecutorService, LiveData.

DCAM Architecture Home

4.1 - Software Architecture

Created

Trang điều hướng bộ Software Architecture Document dạng nhiều trang ngắn.

08 - DCAM-BDMA Integration Boundary

4.1 - Software Architecture

Created

Chốt ranh giới trách nhiệm DCAM Android và BDMA Desktop.

## 5. Updated Documentation Priority

Thứ tự ưu tiên mới sau khi Product Management baseline, Development Plan, Documentation Governance, Android Onboarding, Android Development Standard và Software Architecture baseline đã hoàn thành:

Priority

Document

Target Folder

Reason

P1

DCAM-BDMA Data Contract

03 - Requirements

Khóa chuẩn dữ liệu, file/database exposure, metadata schema, status và BDMA ingest behavior.

P1

DCAM Functional Requirements

03 - Requirements

Làm cơ sở phân rã Epic / Story / Task trên Jira.

P2

DCAM Non-functional Requirements

03 - Requirements

Làm rõ stability, performance, battery, storage, GPS, security và offline behavior.

P2

DCAM Storage Design

4.2 - Technical Design

Chi tiết hóa folder structure, file naming, local database/file layout và ADB-readable exposure.

P2

DCAM Metadata Design

4.2 - Technical Design

Chi tiết hóa metadata model, status, schema version và compatibility với BDMA.

P2

DCAM Security & Encryption Design

4.2 - Technical Design

Làm rõ basic encryption ở Phase 2 và advanced encryption ở Phase 3.

P3

DCAM Live Streaming Design

4.2 - Technical Design

Thiết kế cho Phase 3 – Advanced Communication.

P3

DCAM Push-to-Talk Design

4.2 - Technical Design

Thiết kế cho PTT beta/basic implementation.

P3

DCAM Release Plan

05 - Release Management

Chuẩn hóa build, versioning, release, release notes và pilot release.

P3

ADR Records

4.4 - Architecture Decision Records (ADR)

Ghi nhận các quyết định kỹ thuật quan trọng.

## 6. Recommended Writing Order

Order

Document

Why This Order

1

DCAM-BDMA Data Contract

Cần ngay sau Integration Boundary vì ảnh hưởng cả DCAM Android và BDMA Desktop.

2

DCAM Functional Requirements

Cần để xác định chính xác app phải làm gì và chia Jira backlog.

3

DCAM Non-functional Requirements

Cần để ràng buộc performance, stability, battery, storage, GPS và security.

4

DCAM Storage Design

Chi tiết hóa phần local storage, file exposure và ADB-readable structure.

5

DCAM Metadata Design

Chi tiết hóa metadata fields, status, schema version và mapping với BDMA.

6

DCAM Security & Encryption Design

Làm rõ encryption scope cho Phase 2 và Phase 3.

7

DCAM Release Plan

Cần trước khi có build nội bộ và customer pilot.

8

Live Streaming / PTT Designs

Viết trước khi bước vào Phase 3 implementation.

9

ADR Records

Tạo khi có quyết định kỹ thuật quan trọng.

## 7. Phase Alignment

Phase

Documentation Focus

Notes

Documentation Governance

DCAM Documentation Governance

Chuẩn hóa owner, reviewer, RACI, workflow và update rules cho toàn bộ tài liệu DCAM.

Android Training

DCAM Android Training & Architecture Onboarding, DCAM Android Development Standard

Phục vụ onboarding team từ Desktop/JavaFX sang Android bằng Java và chuẩn hóa rule coding/architecture riêng cho DCAM.

Phase 1 – MVP Foundation

Functional Requirements, DCAM-BDMA Data Contract, Storage/Metadata initial design

Tập trung recording, capture, storage, metadata, logging và BDMA ingest readiness.

Phase 2 – Platform Foundation & BDMA Integration

Data Contract refinement, Device/User Foundation Notes, Security Design

Remote Device Management và Advanced User Management đã được đưa lên Phase 2.

Phase 3 – Advanced Communication

Live Streaming Design, PTT Design, GPS Route Design, Advanced Encryption Design

Tập trung streaming, PTT, GPS route và advanced encryption.

Phase 4 – Hardening & Customer Pilot

Release Plan, Release Notes, Installation Guide, Test Report, Known Issues

Chuẩn bị pilot/release.

## 8. Naming Updates from Old Plan

Old Name / Old Idea

Updated Document / Direction

DCAM Outsourcing Project Brief

Replaced by DCAM Project Charter

DCAM Phase 2 Roadmap

Replaced by DCAM Roadmap

DCAM 9-Month Plan with Android Training, Buffer

Standardized as DCAM 9-Month Development Plan

DCAM Android Architecture

Replaced by Software Architecture document set under DCAM Architecture Home.

DCAM System Architecture & Technical Notes

Replaced by DCAM Architecture Home + SAD child pages under 4.1 - Software Architecture.

DCAM Android Training / Android Foundation Notes

Standardized as DCAM Android Training & Architecture Onboarding under 4.3 - Android Development.

Android Coding Standard idea

Standardized as DCAM Android Development Standard under 4.3 - Android Development.

Documentation ownership / workflow idea

Standardized as DCAM Documentation Governance under 02 - Sprint Operations.

DCAM Media Storage & Metadata Design

Split into DCAM Storage Design and DCAM Metadata Design under 4.2 - Technical Design.

DCAM Acceptance Criteria & Test Plan

Should be handled through MVP Scope, Functional Requirements and later Test Checklist/Test Report.

06 - QA & Testing

Not used in current structure; QA documents should be placed under Release Management or linked from relevant requirement documents unless a QA folder is later created.

## 9. Documentation Principles

Principle

Description

Single Source of Truth

Mỗi loại thông tin nên có một tài liệu chính chịu trách nhiệm.

Follow Documentation Governance

Owner, reviewer, RACI, lifecycle và update rules phải tuân theo DCAM Documentation Governance.

Product vs Requirement vs Technical Separation

Product docs định hướng; Requirement docs mô tả app cần làm gì; Technical docs mô tả cách thiết kế/triển khai.

Architecture vs Technical Design Separation

Software Architecture mô tả hướng kiến trúc và boundary; Technical Design mô tả chi tiết implementation.

Training vs Development Standard Separation

Onboarding dùng để học Android; Development Standard dùng làm rule implementation khi code DCAM.

Training vs Architecture Separation

Android onboarding chỉ dùng cho training; kiến trúc chính thức nằm trong 4.1 - Software Architecture.

Data Contract Early

DCAM-BDMA Data Contract cần được làm sớm vì ảnh hưởng cả DCAM Android và BDMA Desktop.

MVP and Planned Scope Must Be Separated

Tránh nhầm giữa scope bắt buộc của MVP và scope phase sau.

Version and Status Required

Tài liệu chính cần có owner, version, status và ngày cập nhật.

Jira Linkage

Requirement hoặc change quan trọng nên được liên kết với Jira Epic/Story/Task.

Keep Project Home Updated

Khi tạo tài liệu mới, cần cập nhật DCAM Project Home.

## 10. Immediate Next Steps

Step

Action

Target Folder

1

Create DCAM-BDMA Data Contract

03 - Requirements

2

Create DCAM Functional Requirements

03 - Requirements

3

Create DCAM Non-functional Requirements

03 - Requirements

4

Create DCAM Storage Design and DCAM Metadata Design

4.2 - Technical Design

5

Create ADRs for stable architecture decisions such as Service Interface / Platform Adapter / Hardware SDK threading

4.4 - Architecture Decision Records (ADR)

6

Update DCAM Project Home after each new document

DCAM Project Home

## 11. Practical Conclusion

Tài liệu Product Management baseline, Sprint Operations baseline, Documentation Governance, Android onboarding, Android Development Standard và Software Architecture baseline của DCAM đã tương đối đầy đủ.

Từ thời điểm này, trọng tâm tài liệu nên chuyển sang:

```
DCAM-BDMA Data Contract → Functional Requirements → Non-functional Requirements → Storage/Metadata Design → Release Plan
```

Mục tiêu là biến định hướng sản phẩm, architecture baseline, documentation governance và development standard thành backlog rõ ràng, hợp đồng dữ liệu rõ ràng và thiết kế kỹ thuật đủ chắc để team Android có thể triển khai ổn định.