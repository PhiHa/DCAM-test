# DCAM Roadmap

**Page ID**: 41615474  
**Version**: 12  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/41615474

---


# DCAM Roadmap

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Product Roadmap

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

01 - Product Management

Target Audience

PM/BA, Product Owner, Tech Lead, Developers, QA, Stakeholders

Last Updated

2026-07-04

Related Jira

None

Related Documents

DCAM Project Charter, DCAM Product Vision, DCAM MVP Scope, DCAM-BDMA Data Contract, DCAM 9-Month Development Plan, DCAM Documentation Governance

Source Reference

DCAM 9-Month Plan with Android Training, Buffer and PTT

## 1. Purpose

Tài liệu này mô tả lộ trình phát triển của dự án DCAM.

Roadmap tập trung vào các mục tiêu chính, milestone và deliverables của từng giai đoạn, không mô tả chi tiết toàn bộ công việc theo tuần.

Các kế hoạch triển khai chi tiết được quản lý trong tài liệu **DCAM 9-Month Development Plan**.

**DCAM-BDMA Data Contract** hiện đã được tạo và approved, vì vậy các phase liên quan đến BDMA integration, storage, media naming, MD5, AES-256 suffix, CSON, SQLite DB, logs và cleanup behavior phải align với tài liệu này.

## 2. Roadmap Overview

Stage

Timeline

Primary Goal

Main Deliverable

Android Training

Week 1–2

Chuẩn bị năng lực Android cho đội phát triển hiện tại

Android readiness and camera training prototype

Phase 1 – DCAM MVP Foundation

Week 3–12 / Month 1–3

Hoàn thành nền tảng DCAM: camera core, storage, contract data, logs

DCAM MVP Internal Build 0.1

Phase 2 – Platform Foundation & BDMA Integration

Week 13–22 / Month 4–5

Triển khai BDMA integration theo Data Contract và xây dựng nền tảng Device/User sớm để giảm rework

Secure Platform MVP Build 0.2

Phase 3 – Advanced Communication & Customer Pilot

Week 23–32 / Month 6–7

Triển khai live streaming, PTT, advanced encryption và GPS route dựa trên nền tảng Device/User đã có

Advanced Communication Beta Build 0.3

Phase 4 – Hardening, QA & Release

Week 33–40 / Month 8–9

Ổn định sản phẩm, kiểm thử thực tế, tối ưu hiệu năng/bảo mật và chuẩn bị pilot

Customer Pilot Release / Production Candidate

## 3. Phase Objectives

### 3.1 Android Training

Item

Description

Timeline

Week 1–2

Objective

Trang bị kiến thức Android cần thiết cho đội phát triển trước khi bắt đầu dự án.

Expected Outcome

Dev có thể build/test Android camera sample trên BodyCamera và hiểu Android lifecycle, permission, storage, GPS, CameraX/Camera2.

Main Deliverable

Android Foundation Notes, Camera Training Prototype

### 3.2 Phase 1 – DCAM MVP Foundation

Item

Description

Timeline

Week 3–12 / Month 1–3

Objective

Xây dựng các chức năng cốt lõi của ứng dụng DCAM.

Major Features

Video Recording, Image Capture, Local Storage, Data Contract Compliance, Logging, Folder Structure, Camera Framework, MVP Architecture.

Main Deliverable

DCAM MVP Internal Build 0.1

Notes

Phase này đã bao gồm 20% buffer. Storage, media naming và contract data phải align với DCAM-BDMA Data Contract.

### 3.3 Phase 2 – Platform Foundation & BDMA Integration

Item

Description

Timeline

Week 13–22 / Month 4–5

Objective

Đảm bảo DCAM tương thích với BDMA, đồng thời xây dựng sớm nền tảng quản lý Device/User để các phase sau không phải refactor lớn.

Major Features

DCAM-BDMA Data Contract implementation, BDMA Ingest, CSON/SQLite integration, MD5 verification, AES-256 naming support, post-import cleanup, File Recovery, GPS per Media File, Basic Encryption, Remote Device Management, Advanced User Management.

Main Deliverable

Secure Platform MVP Build 0.2

Notes

DCAM-BDMA Data Contract đã có bản approved và là baseline chính thức cho BDMA integration. Phase này đã bao gồm 20% buffer.

### 3.4 Phase 3 – Advanced Communication & Customer Pilot Preparation

Item

Description

Timeline

Week 23–32 / Month 6–7

Objective

Triển khai các năng lực giao tiếp/thời gian thực và các capability nâng cao dựa trên nền tảng Device/User đã hoàn thiện ở Phase 2.

Major Features

Live Streaming, Push-to-Talk (PTT), Full GPS Tracking Route, Advanced Encryption, streaming/PTT state logging, reconnect/timeout handling.

Main Deliverable

Advanced Communication Beta Build 0.3

Notes

PTT và Live Streaming được triển khai ở mức beta/basic implementation. Remote Device Management và Advanced User Management không còn là scope chính của Phase 3, chỉ harden hoặc mở rộng nếu cần.

### 3.5 Phase 4 – Hardening, QA & Release

Item

Description

Timeline

Week 33–40 / Month 8–9

Objective

Ổn định sản phẩm và chuẩn bị phát hành cho khách hàng/pilot.

Major Activities

Regression Testing, Stability Testing, Streaming/PTT Testing, GPS Route Testing, Security Review, Data Contract Regression, Performance Optimization, Release Documentation.

Main Deliverable

Customer Pilot Release / Production Candidate

## 4. Feature Roadmap

Feature

Target Phase

Expected Level

Android Training

Training

Team readiness

Camera Framework Evaluation

Training / Phase 1

Prototype decision

Video Recording

Phase 1

MVP

Image Capture

Phase 1

MVP

Local Storage

Phase 1

MVP aligned with Data Contract

Data Contract Compliance

Phase 1 / Phase 2

Implementation baseline

Logging

Phase 1

MVP

DCAM-BDMA Data Contract

Created / Approved

Version 1 baseline

BDMA Ingest

Phase 2

End-to-end demo

CSON / SQLite Integration

Phase 2

Contract-based implementation

MD5 Verification

Phase 2

Verified / Unverified import handling

Post-import Cleanup

Phase 2

Cleanup policy implementation

File Recovery

Phase 2

Basic recovery/status handling

GPS per Media File

Phase 2

Basic implementation

Basic Encryption

Phase 2

`_enc` naming and basic encryption scope

Remote Device Management

Phase 2

Basic implementation

Advanced User Management

Phase 2

Basic role/profile/operator/permission foundation

Live Streaming

Phase 3

Beta feature

Push-to-Talk (PTT)

Phase 3

Beta/basic implementation

Full GPS Tracking Route

Phase 3

GPS route v1

Advanced Encryption

Phase 3

Extended encryption scope

Regression/Stability Testing

Phase 4

Full pilot validation

Security Review

Phase 4

Release readiness

Customer Pilot Release

Phase 4

Pilot / production candidate

## 5. Milestones

Milestone

Target Time

Success Condition

M0 – Android Training Completed

End of Week 2

Dev có thể build/test Android camera sample trên BodyCamera.

M1 – Camera Prototype

End of Week 6

Video recording và image capture hoạt động được trên BodyCamera.

M2 – DCAM MVP Internal 0.1

End of Week 12

File, contract data và logs được tạo ổn định.

M3 – DCAM-BDMA E2E Demo

End of Week 16

BDMA có thể ingest và hiển thị dữ liệu DCAM theo Data Contract.

M4 – Secure Platform MVP 0.2

End of Week 22

Recovery, GPS per file, basic encryption, Remote Device Management và Advanced User Management foundation đã sẵn sàng.

M5 – Advanced Communication Beta 0.3

End of Week 32

Live streaming beta, PTT beta, GPS route v1 và advanced encryption scope đã sẵn sàng.

M6 – Release Candidate

End of Week 39

Critical bugs đã được xử lý, sẵn sàng cho pilot.

M7 – Customer Pilot Release

End of Week 40

Pilot build và release/test documents đã sẵn sàng.

## 6. 9-Month Timeline by Month

Month

Focus

Primary Output

Month 1

Android training and foundation

Training completed, architecture draft, camera prototype.

Month 2

Core MVP

Recording, image capture and storage.

Month 3

Contract data, logging and MVP internal build

Data Contract compliance, logs, MVP Build 0.1.

Month 4

BDMA compatibility and platform foundation

Data Contract implementation, BDMA ingest, E2E demo, Device/User foundation design.

Month 5

Reliability, security, device and user platform

Recovery, GPS per file, basic encryption, Remote Device Management, Advanced User Management.

Month 6

Advanced communication start

Live streaming beta, PTT design/basic implementation, GPS route design.

Month 7

Advanced communication implementation

PTT beta, streaming hardening, GPS route v1, advanced encryption.

Month 8

QA and hardening

Regression, stability, Data Contract regression, streaming/PTT and GPS route testing.

Month 9

Release candidate and pilot

Release Candidate, Customer Pilot Release.

## 7. Roadmap Success Indicators

Indicator

Description

DCAM MVP readiness

DCAM có thể record video, capture image, save files, expose contract data và write logs trên BodyCamera.

BDMA compatibility

BDMA có thể ingest và hiển thị dữ liệu DCAM end-to-end theo Data Contract.

Data Contract readiness

Storage, media naming, MD5, AES-256 suffix, CSON, SQLite DB, logs và cleanup behavior khớp với Data Contract đã approved.

Platform foundation readiness

Remote Device Management và Advanced User Management foundation đã sẵn sàng trước khi advanced communication features phụ thuộc vào chúng.

Security readiness

Basic encryption và extended encryption sau này được implement theo scope đã thống nhất.

Advanced communication readiness

Live Streaming, PTT và GPS Route đạt mức beta/basic implementation.

Pilot readiness

Release candidate đủ ổn định để pilot với khách hàng được chọn.

Documentation readiness

Release notes, install guide, test checklist và test reports đã sẵn sàng.

## 8. Roadmap Boundaries

Area

Boundary

Data Contract

Đã có approved baseline; implementation và testing phải align với baseline này.

Platform foundation

Remote Device Management và Advanced User Management được đưa vào Phase 2 như foundation capabilities, không phải full fleet management hoặc enterprise IAM.

Advanced features

Được triển khai ở mức beta/basic implementation trong Phase 3, chưa phải full production-hardened platform.

Live Streaming

Dự kiến hoạt động ở mức beta và phải được test trong các tình huống weak network/reconnect/timeout.

PTT

Dự kiến bao gồm trigger, audio capture, transmission, state logging và error handling ở mức beta/basic.

Remote Device Management

Chỉ bao gồm basic status/config read-write và device health; advanced fleet management không nằm trong scope.

Advanced User Management

Chỉ bao gồm basic role/profile/operator/permission foundation; enterprise-grade IAM không nằm trong scope.

GPS Route

Ghi nhận route theo session; visualization và analytics chi tiết có thể mở rộng sau.

Final Release

Customer Pilot / Production Candidate, chưa phải full commercial production cho toàn bộ advanced capabilities.

## 9. Related Documents

Document

Purpose

DCAM Project Charter

Project authorization, scope, governance và approval baseline.

DCAM 9-Month Development Plan

Kế hoạch execution chi tiết với Android training, sprint strategy, buffer và delivery plan.

DCAM Product Vision

Product direction và long-term positioning.

DCAM MVP Scope

MVP scope và planned scope.

DCAM-BDMA Data Contract

Data contract chính thức giữa DCAM Android và BDMA Desktop.

DCAM Functional Requirements

Functional requirements chi tiết.

DCAM Non-functional Requirements

Reliability, performance, security và operational requirements.

DCAM Android Architecture

Technical architecture cho Android BodyCamera application.

DCAM Release Plan

Build, versioning, release và pilot process.

## 10. Practical Conclusion

Mục tiêu thực tế của roadmap 9 tháng là:

textRoadmap này không nên được hiểu là cam kết full production cho toàn bộ advanced capabilities. Mục tiêu phù hợp hơn là có bản **Customer Pilot / Production Candidate** đủ tốt để triển khai thử nghiệm với khách hàng chọn lọc và thu thập feedback thực tế.