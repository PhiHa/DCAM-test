# DCAM Requirements Home

**Page ID**: 47710513  
**Version**: 4  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47710513

---


# DCAM Requirements Home

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Requirements Home / Documentation Hub

Version

Approved 1.1

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

2026-07-07

Related Jira

None

Related Documents

DCAM-BDMA Data Contract, DCAM Non-functional Requirements, DCAM MVP Scope, DCAM Roadmap, DCAM Architecture Home, DCAM Documentation Governance

## 1. Purpose

Trang này là trang điều hướng cho bộ tài liệu **Functional Requirements** của dự án **DCAM**.

Mục tiêu là tách các yêu cầu chức năng thành nhiều trang nhỏ, dễ review, dễ bảo trì và dễ trace sang Jira Epic / Story / Task.

Lưu ý: **DCAM Non-functional Requirements** là tài liệu Requirements cùng cấp với trang này trong folder **03 - Requirements**, không phải page con của Requirements Home.

## 2. Requirements Structure

Cấu trúc thực tế hiện tại trong folder **03 - Requirements**:

text## 3. Functional Requirements Pages

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

Yêu cầu về media naming, important media, encrypted media, `.md5` cho `.mp4` và cleanup.

04 - Device Configuration Requirements

Approved

Yêu cầu về `dcam_config.cson` và device information.

05 - User & Device Operation Requirements

Approved

Yêu cầu về user/operator, device tracking và operational data.

06 - BDMA Integration Requirements

Approved

Yêu cầu tích hợp BDMA, ADB, import result, CSON/DB/log access và `.mp4` verification.

07 - Logging & Diagnostics Requirements

Approved

Yêu cầu về logs, diagnostics và troubleshooting.

08 - Security & Encryption Requirements

Approved

Yêu cầu về AES-256, `_enc`, permissions và sensitive data.

09 - System Settings Requirements

Approved

Yêu cầu về storage mode, feature settings, Firebase Remote Config, AutoUpdate, Play Store / Cloudflare R2 update settings và runtime configuration trong `dcam.db`.

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

DCAM-BDMA Data Contract

Hiểu baseline dữ liệu chính thức giữa DCAM Android và BDMA Desktop.

2

DCAM Requirements Home

Hiểu cấu trúc bộ Functional Requirements.

3

01 - Recording & Capture Requirements

Hiểu yêu cầu về video recording, image capture và audio capture.

4

02 - Media Storage Requirements

Hiểu yêu cầu về Internal / External / Auto storage.

5

03 - Media Management Requirements

Hiểu yêu cầu về media naming, important media, encrypted media, MP4-only MD5 và cleanup.

6

04 - Device Configuration Requirements

Hiểu yêu cầu về `dcam_config.cson` và thông tin thiết bị.

7

05 - User & Device Operation Requirements

Hiểu yêu cầu về user/operator, device tracking và operational data.

8

06 - BDMA Integration Requirements

Hiểu yêu cầu tích hợp BDMA, ADB, import, `.md5` cho `.mp4` và cleanup/write-back.

9

07 - Logging & Diagnostics Requirements

Hiểu yêu cầu về logs, diagnostics và troubleshooting.

10

08 - Security & Encryption Requirements

Hiểu yêu cầu về AES-256, `_enc`, permissions và security.

11

09 - System Settings Requirements

Hiểu system settings, feature settings, Firebase Remote Config, AutoUpdate và operational settings trong `dcam.db`.

12

10 - Android Device Operation Requirements

Hiểu cách app DCAM vận hành trên Android: full screen, Home App, auto start on boot, service, lifecycle và recovery.

13

DCAM Non-functional Requirements

Hiểu các ràng buộc chất lượng áp dụng toàn hệ thống.

## 6. Relationship with Technical Design

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

## 7. Notes

Trang này phản ánh cấu trúc Requirements thực tế tại thời điểm cập nhật.

Khi tạo thêm functional requirement page, non-functional requirement page hoặc thay đổi vị trí tài liệu trong folder **03 - Requirements**, cần cập nhật lại trang này và **DCAM Project Home**.