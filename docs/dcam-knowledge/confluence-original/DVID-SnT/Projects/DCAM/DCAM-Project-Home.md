# DCAM Project Home

**Page ID**: 41648280  
**Version**: 64  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/41648280

---


# DCAM Project Home

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Project Home / Chỉ mục tài liệu

Version

Approved 3.33

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / BDMA Lead / Security Reviewer / Cloud Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

DCAM

Target Audience

PM/BA, Tech Lead, Developers, BDMA Team, Cloud/WebServer Team, QA, Factory, Support, Stakeholders

Last Updated

2026-07-10

Related Documents

[DCAM Document Status Registry](/wiki/spaces/DVID/pages/51085647/DCAM+Document+Status+Registry), [DCAM Requirement–Design–Test Traceability Matrix](/wiki/spaces/DVID/pages/51085669/DCAM+Requirement+Design+Test+Traceability+Matrix), DCAM Release & Build Applicability Matrix, DCAM Documentation Governance, DCAM Requirements Home, DCAM Architecture Home, DCAM QA Test Strategy & Test Matrix, DCAM Factory Provisioning & Device Production SOP, DCAM-BDMA Data Contract

## 1. Purpose

Trang điều hướng chính cho bộ tài liệu DCAM và current cross-document decisions.

Trang này không duy trì danh sách version/status của từng tài liệu. Trạng thái tập trung được quản lý tại **DCAM Document Status Registry**.

## 2. Current Delivery Baseline

Active Build = DCAM MVP Internal Build 0.1
Active Gate = Working Recording Slice
Required = recording, capture, storage, minimal DB/CSON/logs, BDMA sample import
Deferred = Web provisioning, full auth, full kiosk, Remote Config, Self Update, AI, Live/PTT
Source of truth: **DCAM Release & Build Applicability Matrix**.

## 3. Current Project Baselines

Identity:
serial_number = Hardware Identity / recovery key
dcam_cloud_device_id = Cloud Identity
serial_lookup/{serial_number} = create/restore mapping
SD Identity File = recovery cache

Web Portal:
Factory Worker only
Login and Workspace only
QR displayed by DCAM is serial source
serial_number is read-only
Firebase Hosting/Auth/Functions/Firestore

DSetup:
exactly one ADB device
completion = imported serial matches expected serial
Device Owner factory baseline = ADB dpm set-device-owner when required
no production acceptance decision

Logging:
Operational Logging → Loggly through local-first queue and Backend Relay
Crash & Stability → Firebase Crashlytics
BDMA diagnostics → Logs/logs.txt

Dedicated Device:
No external EMM / Android Management API / Managed Google Play
Preferred model = DCAM-as-DPC / local Device Owner
Maintenance = authorized actor + Maintenance Password Gate + Controlled Mode
Primary update = DCAM Self Update / approved APK update

Factory Wi-Fi:
Current project decision remains hardcoded in approved APK
Value must not appear in logs, QR, API, records or evidence
## 4. Documentation Structure

DCAM
├── DCAM Project Home
├── 01 - Product Management
│   ├── DCAM Project Charter
│   ├── DCAM Product Vision
│   ├── DCAM Roadmap
│   └── DCAM MVP Scope
├── 02 - Sprint Operations
│   ├── DCAM 9-Month Development Plan
│   ├── DCAM Documentation Governance
│   ├── DCAM Release & Build Applicability Matrix
│   └── DCAM Document Status Registry
├── 03 - Requirements
│   ├── DCAM Requirements Home
│   │   ├── Functional Requirements 01–10
│   │   └── DCAM Requirement–Design–Test Traceability Matrix
│   ├── DCAM Non-functional Requirements
│   └── DCAM-BDMA Data Contract
├── 04 - Technical Documentation
│   ├── 4.1 - Software Architecture / DCAM Architecture Home
│   ├── 4.2 - Technical Design
│   ├── 4.3 - Android Development
│   └── 4.4 - Architecture Decision Records
├── 05 - Release Management
│   ├── DCAM QA Test Strategy & Test Matrix
│   └── DCAM Factory Provisioning & Device Production SOP
└── 06 - Incident Log
## 5. Status and TBD Governance

Document status taxonomy, approval integrity và version synchronization được quản lý bởi:

DCAM Documentation Governance
    → rule and taxonomy ownership

DCAM Document Status Registry
    → single cross-document version/status summary
Navigation pages không được copy mutable version/status table.

A page must not mark an entire area `TBD` when another authoritative page already defines the baseline.

Summary classifications:

Classification

Meaning

Approved Direction

Direction complete; exact implementation/evidence may remain open.

Approved Provisional Baseline

Usable baseline that may be adjusted by evidence.

Approved Pending Device POC

Target-device evidence remains required.

Approved Pending Security Review

Cryptographic/policy details remain required.

Approved for Build `<profile>`

Approved only for the named build profile.

Production Approved

Production evidence and approval gates completed.

## 6. Resolved Stale-TBD Summary

Area

Resolution

Web stack

Firebase Hosting/Auth/Functions/Firestore.

Provisioning endpoint

`POST /v1/factory/provisioning/devices`.

QR minimum payload

Defined in API Contract.

Firestore paths

`workers`, `serial_lookup`, `devices`, `audit_events`.

Frontend write model

Backend-only production writes.

Performance telemetry

Operational events → Loggly; crash/ANR → Crashlytics.

Maintenance entry

Authorized role + Maintenance Password Gate + Controlled Mode.

Device Owner direction

DCAM-as-DPC / local Device Owner.

Factory setup

DSetup + ADB `dpm set-device-owner` baseline.

Storage threshold baseline

30-minute estimate + 500 MB margin; finalization headroom ≥500 MB provisional.

Critical finalization

`≤ 5s`.

Missing required policy

Required/degraded state; normal production operation blocked by default.

Recording/recovery QA

Defined in QA Matrix.

## 7. Document Status and Traceability

Need

Source of Truth

Current document version/status/approval scope

[DCAM Document Status Registry](/wiki/spaces/DVID/pages/51085647/DCAM+Document+Status+Registry)

Requirement → Build → Design → QA → Jira → Evidence mapping

[DCAM Requirement–Design–Test Traceability Matrix](/wiki/spaces/DVID/pages/51085669/DCAM+Requirement+Design+Test+Traceability+Matrix)

Build-level Required/Conditional/Deferred/N/A

DCAM Release & Build Applicability Matrix

Documentation ownership and status rules

DCAM Documentation Governance

Do not copy mutable versions into this page.
Update this page only when navigation, ownership, reading order or current project baseline changes.
## 8. Genuine Remaining Decision Groups

Device POC:
target hardware/firmware, Camera SDK, physical paths, OEM kiosk behavior, silent install

Security:
encryption/key management, QR cryptographic policy, exact maintenance values

Backend/Deployment:
Firebase Security Rules, physical function mapping, retention/environment settings

Product/Factory:
worker account model, owner master data, duplicate/rebind process

Implementation:
Room/raw SQLite, exact schemas, retry/backoff, internal file paths

Future:
Live Streaming, PTT, advanced AI, full GPS route
## 9. Practical Conclusion

Project Home owns navigation and current project summary.
Applicability Matrix owns what is mandatory now.
Document Status Registry owns cross-document version/status summary.
Traceability Matrix owns Requirement → Build → Design → QA → Jira → Evidence mapping.
TBD remains only where Device POC, Security/Product decision, deployment choice or future feature genuinely requires it.