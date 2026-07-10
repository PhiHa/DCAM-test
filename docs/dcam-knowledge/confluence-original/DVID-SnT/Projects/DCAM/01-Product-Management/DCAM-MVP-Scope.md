# DCAM MVP Scope

**Page ID**: 42532866  
**Version**: 8  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/42532866

---


# DCAM MVP Scope

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

MVP Scope

Version

Approved 2.3

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

DCAM Product Vision, DCAM Project Charter, DCAM Roadmap, DCAM 9-Month Development Plan, DCAM-BDMA Data Contract, DCAM Documentation Governance

Related Roadmap

DCAM Roadmap

## 1. Executive Summary

MVP tập trung xây dựng ứng dụng Android trên BodyCamera có khả năng ghi video, chụp ảnh, lưu media cục bộ và expose dữ liệu để **BDMA Desktop** ingest và xử lý theo **DCAM-BDMA Data Contract**.

Mục tiêu của MVP không phải là hoàn thiện toàn bộ năng lực dài hạn của DCAM, mà là xác nhận luồng cốt lõi:

textTrong MVP, các rule về storage root, folder structure, media naming, MD5, AES-256 suffix, CSON, SQLite DB, logs và BDMA cleanup behavior phải tuân theo **DCAM-BDMA Data Contract**.

## 2. MVP Objectives

Objective

Description

Validate Video Recording

DCAM ghi video ổn định trên BodyCamera.

Validate Image Capture

DCAM chụp ảnh ổn định trên BodyCamera.

Validate Local Storage

Media được lưu đúng cấu trúc để BDMA có thể đọc theo Data Contract.

Validate Contract Data

DCAM tạo và expose dữ liệu theo Data Contract: media file, optional `.md5`, `dcam_config.cson`, `dcam.db` và `logs.txt`.

Validate BDMA Compatibility

BDMA ingest dữ liệu DCAM thành công theo Data Contract.

Validate Operational Stability

DCAM ổn định khi GPS unavailable hoặc storage gần đầy.

## 3. MVP Success Criteria

KPI

Target

Video Recording Success Rate

= 99%

Image Capture Success Rate

= 99%

BDMA Import Success Rate

100%

Data Contract Compliance

100% for MVP-supported fields/files

File Corruption

0 Critical Case

Application Crash

Không có Critical Crash trong luồng recording/capture chính.

## 4. Functional Scope

Module

Scope

Video Recording

Start/Stop recording, recording state.

Image Capture

Capture image and save image.

Local Storage

Internal/External storage behavior, Media folders, IMP folder, Temp folder and fallback behavior according to Data Contract.

Media Naming

File naming theo format `DCAM_XXXXXX_ZZZZZZ_YYYYMMDD_HHMMSS.<ext>`, `_IMP`, `_enc`, `_IMP_enc`.

Media Metadata

Metadata embedded trực tiếp trong media file nếu format hỗ trợ; không dùng media metadata JSON riêng trong contract hiện tại.

Device Config

`dcam_config.cson` lưu device information only.

SQLite DB

`dcam.db` lưu User data, Device tracking và Operational data/configuration.

Logs

`logs.txt` phục vụ diagnostics; BDMA read-only.

Device Status

Battery, storage và GPS availability.

BDMA Compatibility

ADB-based reading, MD5 verification if available, import result handling and post-import cleanup policy.

## 5. Out of Scope

Các chức năng sau không thuộc phạm vi MVP đầu tiên. Một số chức năng đã được ghi nhận trong Roadmap cho các phase sau.

Feature

Target Phase / Direction

Remote Device Management

Phase 2 - Platform Foundation & BDMA Integration; foundation/basic implementation, không thuộc MVP đầu tiên.

Advanced User Management

Phase 2 - Platform Foundation & BDMA Integration; foundation/basic implementation, không thuộc MVP đầu tiên.

Live Streaming

Phase 3 - Advanced Communication.

Push-to-Talk (PTT)

Phase 3 - Advanced Communication.

Advanced Encryption

Phase 3 - Advanced Communication; basic encryption có thể bắt đầu ở Phase 2 theo Roadmap/Development Plan.

Full GPS Tracking Route

Phase 3 - Advanced Communication.

Cloud Upload

Future.

OTA Update

Future.

AI Analytics

Future.

## 6. MVP User Journey

### 6.1 Record Video Flow

text### 6.2 Capture Image Flow

text### 6.3 BDMA Ingest Flow

text## 7. MVP Deliverables

Deliverable

Description

APK/AAB

Android MVP build chạy trên BodyCamera.

Source Code

DCAM MVP source code.

Data Contract Compliance

Implementation tuân theo DCAM-BDMA Data Contract.

Folder Structure Specification

Mô tả cấu trúc lưu trữ media theo Data Contract.

SQLite DB Specification

Mô tả DB scope cho User data, Device tracking và Operational data.

Installation Guide

Hướng dẫn cài đặt MVP trên BodyCamera.

Test Report

Báo cáo kiểm thử MVP.

Release Notes

Ghi chú phát hành cho MVP build.

## 8. Acceptance Criteria

ID

Criteria

AC-001

DCAM chạy được trên BodyCamera.

AC-002

Người dùng có thể ghi video.

AC-003

Người dùng có thể chụp ảnh.

AC-004

Media được lưu đúng cấu trúc theo DCAM-BDMA Data Contract.

AC-005

Media file naming tuân theo Data Contract.

AC-006

Nếu `.md5` được tạo, BDMA verify được file media tương ứng.

AC-007

Nếu thiếu `.md5`, BDMA vẫn import và đánh dấu Unverified/warning.

AC-008

GPS được ghi nhận nếu thiết bị có dữ liệu hợp lệ.

AC-009

Logging hoạt động cho các luồng chính và ghi vào `logs.txt`.

AC-010

BDMA ingest được dữ liệu DCAM qua ADB.

AC-011

BDMA xử lý cleanup đúng theo policy trong Data Contract.

AC-012

Ứng dụng không crash trong tình huống storage gần đầy hoặc GPS unavailable.

## 9. Exit Criteria

MVP được xem là hoàn thành khi các điều kiện sau đạt yêu cầu:

Condition

Required

Recording Pass

Yes

Capture Pass

Yes

Data Contract Pass

Yes

BDMA Pass

Yes

No Critical Bug

Yes

PM & QA Approval

Yes

## 10. Related Documents

Document

Purpose

DCAM Product Vision

Vision

DCAM Project Charter

Project baseline

DCAM Roadmap

Roadmap

DCAM 9-Month Development Plan

Execution plan

DCAM-BDMA Data Contract

Official data contract between DCAM Android and BDMA Desktop

DCAM Android Training & Architecture Onboarding

Android onboarding for Java/Desktop team

DCAM Functional Requirements

Requirements

DCAM Non-functional Requirements

Quality attributes

DCAM System Architecture & Technical Notes

System architecture and technical notes