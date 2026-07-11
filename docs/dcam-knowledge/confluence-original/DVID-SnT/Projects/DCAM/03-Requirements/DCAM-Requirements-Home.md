# DCAM Requirements Home

**Page ID**: 47710513  
**Version**: 5  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47710513

---


# DCAM Requirements Home

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Requirements Home / Documentation Hub

Version

Approved 1.2

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

03 - Requirements

Target Audience

PM/BA, Tech Lead, Android Developers, BDMA Developers, QA

Last Updated

2026-07-10

Related Jira

None

Related Documents

DCAM Release & Build Applicability Matrix, DCAM-BDMA Data Contract, DCAM Non-functional Requirements, DCAM MVP Scope, DCAM Roadmap, DCAM Architecture Home, DCAM Documentation Governance

## 1. Purpose

Trang này là trang điều hướng cho bộ tài liệu **Functional Requirements** của dự án **DCAM**.

Mục tiêu là tách các yêu cầu chức năng thành nhiều trang nhỏ, dễ review, dễ bảo trì và dễ trace sang Jira Epic / Story / Task.

Lưu ý: **DCAM Non-functional Requirements** là tài liệu Requirements cùng cấp với trang này trong folder **03 - Requirements**, không phải page con của Requirements Home.

Build applicability rule:

Requirements pages define product behavior and target direction.
DCAM Release & Build Applicability Matrix defines which requirements are mandatory for each build profile.
A requirement marked P0/Approved is not automatically a release blocker for every build.
## 2. Requirements Structure

Cấu trúc thực tế hiện tại trong folder **03 - Requirements**:

03 - Requirements
├── DCAM-BDMA Data Contract
├── DCAM Requirements Home
│   ├── 01 - Recording & Capture Requirements
│   ├── 02 - Media Storage Requirements
│   ├── 03 - Media Management Requirements
│   ├── 04 - Device Configuration Requirements
│   ├── 05 - User & Device Operation Requirements
│   ├── 06 - BDMA Integration Requirements
│   ├── 07 - Logging & Diagnostics Requirements
│   ├── 08 - Security & Encryption Requirements
│   ├── 09 - System Settings Requirements
│   └── 10 - Android Device Operation Requirements
└── DCAM Non-functional Requirements
## 3. Functional Requirements Pages

Document

Status

Purpose

01 - Recording & Capture Requirements

Approved

Yêu cầu về video recording, image capture và audio capture.

02 - Media Storage Requirements

Approved

Yêu cầu về Internal / External / Auto storage, storage behavior và Temp handling.

03 - Media Management Requirements

Approved

Yêu cầu về media naming, important media, encrypted-media naming, `.md5` cho `.mp4` và cleanup.

04 - Device Configuration Requirements

Approved

Yêu cầu về `dcam_config.cson`, device information và device identity baseline.

05 - User & Device Operation Requirements

Approved

Yêu cầu về user/operator, device tracking và operational data.

06 - BDMA Integration Requirements

Approved

Yêu cầu tích hợp BDMA, ADB, import result, CSON/DB/log access và `.mp4` verification.

07 - Logging & Diagnostics Requirements

Approved

Yêu cầu về local-first Operational Logging, diagnostics, Loggly/Crashlytics boundary và troubleshooting.

08 - Security & Encryption Requirements

Approved

Yêu cầu về authentication, credential safety, media protection, permissions và sensitive data; exact algorithm/key implementation thuộc Security Design.

09 - System Settings Requirements

Approved

Yêu cầu về local settings, remote config, requested/applied values, kiosk/update preconditions và runtime configuration trong `dcam.db`; exact provider/payload thuộc design/contract.

10 - Android Device Operation Requirements

Approved

Yêu cầu về full screen, Home App / Launcher App, auto start on boot, lifecycle, service, permission, power và recovery behavior.

## 4. Requirements Documents at 03 - Requirements Level

Document

Status

Purpose

DCAM-BDMA Data Contract

Approved

Chuẩn trao đổi dữ liệu giữa DCAM Android và BDMA Desktop; là baseline cho storage, naming, MD5, CSON, DB, logs và BDMA import behavior.

DCAM Requirements Home

Approved

Trang điều hướng cho bộ Functional Requirements.

DCAM Non-functional Requirements

Approved

Baseline NFR cho performance, recording stability, storage reliability, offline operation, Android compatibility, security, logging, BDMA import reliability, update reliability, maintainability và testability.

## 5. Reading Order

Order

Document

Purpose

1

DCAM Release & Build Applicability Matrix

Xác định requirement/test group nào đang bắt buộc cho build hiện tại.

2

DCAM-BDMA Data Contract

Hiểu baseline dữ liệu chính thức giữa DCAM Android và BDMA Desktop.

3

DCAM Requirements Home

Hiểu cấu trúc bộ Functional Requirements.

4

01 - Recording & Capture Requirements

Hiểu yêu cầu về video recording, image capture và audio capture.

5

02 - Media Storage Requirements

Hiểu yêu cầu về Internal / External / Auto storage.

6

03 - Media Management Requirements

Hiểu yêu cầu về media naming, important media, encrypted-media naming, MP4-only MD5 và cleanup.

7

04 - Device Configuration Requirements

Hiểu yêu cầu về `dcam_config.cson` và thông tin/identity thiết bị.

8

05 - User & Device Operation Requirements

Hiểu yêu cầu về user/operator, device tracking và operational data.

9

06 - BDMA Integration Requirements

Hiểu yêu cầu tích hợp BDMA, ADB, import, `.md5` cho `.mp4` và cleanup/write-back.

10

07 - Logging & Diagnostics Requirements

Hiểu yêu cầu về logs, diagnostics và troubleshooting.

11

08 - Security & Encryption Requirements

Hiểu yêu cầu về authentication, protection, permissions và security.

12

09 - System Settings Requirements

Hiểu system settings, remote config, update và operational settings trong `dcam.db`.

13

10 - Android Device Operation Requirements

Hiểu cách app DCAM vận hành trên Android: full screen, Home App, auto start on boot, service, lifecycle và recovery.

14

DCAM Non-functional Requirements

Hiểu các ràng buộc chất lượng áp dụng toàn hệ thống.

## 6. Build Applicability

Current active build baseline được định nghĩa trong **DCAM Release & Build Applicability Matrix**.

Current Active Build = DCAM MVP Internal Build 0.1
Current Gate = Working Recording Slice
Đối với Build `0.1`:

Recording, capture, storage, minimal DB/CSON/logs và BDMA sample import là required.

Web Portal, cloud identity, full auth, full kiosk, Remote Config, Self Update, AI, Live Streaming và PTT là deferred/not applicable theo Matrix.

Requirements vẫn giữ target behavior, nhưng dev/QA chỉ áp dụng subset được Matrix activate.

## 7. Relationship with Technical Design

Các tài liệu Requirements là nguồn đầu vào cho các tài liệu Technical Design trong folder **4.2 - Technical Design**.

Technical Design

Main Requirement Inputs

DCAM Self Update Design

09 - System Settings Requirements, 10 - Android Device Operation Requirements, DCAM Non-functional Requirements, 06 - Cloud Services, Update & Configuration Architecture.

DCAM SQLite Database Design

04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 09 - System Settings Requirements, DCAM-BDMA Data Contract.

DCAM Storage Design

02 - Media Storage Requirements, 03 - Media Management Requirements, DCAM-BDMA Data Contract, DCAM Non-functional Requirements.

DCAM Recording & Capture Design

01 - Recording & Capture Requirements, 02 - Media Storage Requirements, 03 - Media Management Requirements, 09 - System Settings Requirements, 10 - Android Device Operation Requirements.

DCAM Logging & Diagnostics Design

07 - Logging & Diagnostics Requirements, DCAM-BDMA Data Contract, DCAM Security & Encryption Design.

Implementation scope and release blocking are determined by **DCAM Release & Build Applicability Matrix**, not by the existence of a target Technical Design alone.

## 8. Notes

Trang này phản ánh cấu trúc Requirements thực tế tại thời điểm cập nhật.

Khi tạo thêm functional requirement page, non-functional requirement page hoặc thay đổi vị trí tài liệu trong folder **03 - Requirements**, cần cập nhật lại trang này và **DCAM Project Home**.

Khi thay đổi phase/build applicability mà không thay đổi requirement behavior, cập nhật **DCAM Release & Build Applicability Matrix** thay vì copy phase rules vào từng Requirements page.