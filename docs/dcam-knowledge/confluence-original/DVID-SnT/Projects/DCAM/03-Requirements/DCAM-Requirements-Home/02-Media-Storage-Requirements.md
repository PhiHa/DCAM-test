# 02 - Media Storage Requirements

**Page ID**: 47808901  
**Version**: 3  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47808901

---


# 02 - Media Storage Requirements

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Functional Requirements

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

03 - Requirements / DCAM Requirements Home

Target Audience

PM/BA, Tech Lead, Android Developers, QA, BDMA Team

Last Updated

2026-07-07

Related Jira

None

Related Documents

DCAM Requirements Home, DCAM-BDMA Data Contract, DCAM Storage Design, DCAM Recording & Capture Design, DCAM SQLite Database Design, 05 - Data, Storage & BDMA Architecture

## 1. Purpose

Trang này ghi nhận các yêu cầu chức năng liên quan đến **media storage** của DCAM.

Yêu cầu storage hiện đã có technical design chi tiết trong **DCAM Storage Design**. Trang này chỉ giữ requirement-level direction; physical path, temp/final mechanics, fallback, BDMA readiness và recovery behavior thuộc Storage Design.

## 2. Storage Requirements

Requirement Area

Requirement Direction

Status

Internal Storage

DCAM phải hỗ trợ lưu media vào Internal DCAM Media Root khi user chọn Internal hoặc khi Auto fallback được policy cho phép.

Approved Direction

External Storage

DCAM phải hỗ trợ lưu media vào External DCAM Media Root khi user chọn External và external root hợp lệ.

Approved Direction

Auto Storage

DCAM phải ưu tiên External và fallback sang Internal trước khi start recording nếu External unavailable, full, missing, not writable hoặc invalid.

Approved Direction

Fixed Internal Files

`dcam_config.cson`, `dcam.db` và `logs.txt` phải nằm trong Internal Storage.

Approved Direction

Temp Handling

DCAM phải phân biệt temporary/in-progress files với completed media files; in-progress files không được là BDMA import candidate.

Approved Direction

Final Media Readiness

Final media chỉ được xem là BDMA-ready sau khi final file, DB state và storage readiness conditions đạt yêu cầu.

Approved Direction

Storage Recovery

DCAM phải có cơ chế recovery khi temp/final file và DB state không khớp sau crash/reboot/storage failure.

Approved Direction

## 3. Source of Truth

Topic

Source of Truth

Folder/naming/MD5/BDMA cleanup contract

DCAM-BDMA Data Contract

Android-side storage mechanics

DCAM Storage Design

Recording/capture finalization flow

DCAM Recording & Capture Design

Media session DB state

DCAM SQLite Database Design

## 4. Practical Conclusion

Storage requirements are now no longer TBD at requirement level.

text