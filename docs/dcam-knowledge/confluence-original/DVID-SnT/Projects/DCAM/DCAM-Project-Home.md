# DCAM Project Home

**Page ID**: 41648280  
**Version**: 60  
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

Approved 3.29

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

PM/BA, Tech Lead, Android Developers, AI/ML Engineer, BDMA Team, Cloud/WebServer Team, QA, Factory, Support, Stakeholders

Last Updated

2026-07-10

Related Jira

None

Related Documents

DCAM Requirements Home, 07 - Logging & Diagnostics Requirements, DCAM Architecture Home, DCAM Architecture Delivery Profile, DCAM Documentation Governance, DCAM QA Test Strategy & Test Matrix, DCAM Factory Provisioning & Device Production SOP, DCAM DSetup Factory Tool Design, DCAM Device POC & Hardware Validation Report, DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Device Provisioning Web Portal Implementation Design, DCAM Web Portal & Device API Contract, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Device Owner & Kiosk Policy Design, DCAM Android Operation Design, DCAM Logging & Diagnostics Design, DCAM Performance Budget & Resource Constraints, DCAM Concurrency & Threading Model Design, DCAM Security & Encryption Design, DCAM Self Update Design, DCAM-BDMA Data Contract

## 1. Purpose

Trang này là landing page chính cho toàn bộ bộ tài liệu **DCAM**.

Mục tiêu:

Phản ánh hierarchy Confluence hiện tại.

Giúp team tìm nhanh source of truth theo domain.

Ghi nhận các decision hiện hành mà tài liệu chi tiết phải tuân theo.

Tránh copy rule giữa nhiều page theo **DCAM Documentation Governance**.

Phân biệt rõ Architecture, Requirements, Technical Design, Implementation Design, QA và Factory SOP.

Current cross-project baselines:

Identity:
    serial_number = Hardware Identity / primary recovery key
    dcam_cloud_device_id = Cloud Identity / primary cloud device id
    serial_lookup/{serial_number} = cloud create/restore lookup
    SD Identity File = recovery cache

Cloud:
    Firebase Cloud Firestore = current storage baseline
    Firebase Realtime Database = not used

Dedicated Device:
    No external EMM
    No Android Management API
    No Managed Google Play policy-driven update
    DCAM Self Update / approved APK update = primary update path

Web Portal:
    User-facing account type = Factory Worker only
    Screens = Login and Workspace only
    serial_number source = provisioning QR displayed by DCAM
    serial_number behavior = read-only
    no manual serial entry
    no direct serial barcode scan
    frontend = Firebase Hosting
    authentication = Firebase Authentication
    backend = Firebase Cloud Functions
    storage = Firebase Cloud Firestore

DSetup:
    exactly one ADB device at a time
    completes when DCAM confirms imported_serial_number == expected serial_number
    no official production record
    no PASS / FAIL / QUARANTINED / READY_TO_SHIP decision

Logging:
    Operational Logging → Loggly through local-first queue and authenticated Backend Relay
    Crash & Stability Monitoring → Firebase Crashlytics
    internal active/rotated logs and upload queue = implementation-private
    Logs/logs.txt = stable sanitized BDMA-facing artifact

Factory Wi-Fi:
    Current project decision keeps factory Wi-Fi SSID/password hardcoded in approved DCAM APK
    Wi-Fi password must not appear in logs, Crashlytics, QR, API payload, production records or evidence
## 2. Current Documentation Structure

DCAM
├── DCAM Project Home
├── 01 - Product Management
│   ├── DCAM Project Charter
│   ├── DCAM Roadmap
│   ├── DCAM Product Vision
│   └── DCAM MVP Scope
├── 02 - Sprint Operations
│   ├── DCAM 9-Month Development Plan
│   └── DCAM Documentation Governance
├── 03 - Requirements
│   ├── DCAM-BDMA Data Contract
│   ├── DCAM Requirements Home
│   │   ├── 01 - Recording & Capture Requirements
│   │   ├── 02 - Media Storage Requirements
│   │   ├── 03 - Media Management Requirements
│   │   ├── 04 - Device Configuration Requirements
│   │   ├── 05 - User & Device Operation Requirements
│   │   ├── 06 - BDMA Integration Requirements
│   │   ├── 07 - Logging & Diagnostics Requirements
│   │   ├── 08 - Security & Encryption Requirements
│   │   ├── 09 - System Settings Requirements
│   │   └── 10 - Android Device Operation Requirements
│   └── DCAM Non-functional Requirements
├── 04 - Technical Documentation
│   ├── 4.1 - Software Architecture
│   │   └── DCAM Architecture Home
│   │       ├── DCAM Architecture Delivery Profile
│   │       ├── 01 - Architecture Overview
│   │       ├── 02 - Architecture Principles
│   │       ├── 03 - Android Platform & Compatibility Strategy
│   │       ├── 04 - Application & Module Architecture
│   │       ├── 05 - Data, Storage & BDMA Architecture
│   │       ├── 06 - Cloud Services, Update & Configuration Architecture
│   │       ├── 07 - Logging, Diagnostics, Performance & Security
│   │       └── 08 - DCAM-BDMA Integration Boundary
│   ├── 4.2 - Technical Design
│   │   ├── DCAM Android Operation Design
│   │   ├── DCAM State Machine Design
│   │   ├── DCAM SQLite Database Design
│   │   ├── DCAM Recording & Capture Design
│   │   ├── DCAM Storage Design
│   │   ├── DCAM BDMA Integration Technical Design
│   │   ├── DCAM Self Update Design
│   │   ├── DCAM Security & Encryption Design
│   │   ├── DCAM Sensor & Location Monitoring Design
│   │   ├── DCAM Realtime AI Detection Design
│   │   ├── DCAM Device Capability & Feature Eligibility Design
│   │   ├── DCAM Device Provisioning Web Portal Design
│   │   │   ├── DCAM Device Provisioning Web Portal App Design
│   │   │   └── DCAM Device Provisioning Web Portal Implementation Design
│   │   ├── DCAM Android Device Owner & Kiosk Policy Design
│   │   ├── DCAM In-App Operation, Device Settings & Media Console Design
│   │   ├── DCAM Web Portal & Device API Contract
│   │   ├── DCAM Concurrency & Threading Model Design
│   │   ├── DCAM Performance Budget & Resource Constraints
│   │   └── DCAM Logging & Diagnostics Design
│   ├── 4.3 - Android Development
│   │   ├── DCAM Android Training & Architecture Onboarding
│   │   ├── DCAM Android Development Standard
│   │   └── DCAM Device POC & Hardware Validation Report
│   └── 4.4 - Architecture Decision Records (ADR)
│       ├── ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision
│       └── ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id
├── 05 - Release Management
│   ├── DCAM QA Test Strategy & Test Matrix
│   └── DCAM Factory Provisioning & Device Production SOP
│       └── DCAM DSetup Factory Tool Design
└── 06 - Incident Log
Draft page trống trong ADR folder không được liệt kê trong structure chính.

## 3. Authoritative Rule Ownership Matrix

Rule / Principle

Authoritative Document

Ownership Summary

Documentation governance

DCAM Documentation Governance

Define once, reference elsewhere; approval dependencies and change triggers.

MVP vs Target Architecture

DCAM Architecture Delivery Profile

Working Recording Slice đi trước platform expansion.

Device identity baseline

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id

Serial, cloud ID, lookup và SD recovery cache.

Factory production procedure and final acceptance

DCAM Factory Provisioning & Device Production SOP

Production steps, official record, `READY_TO_SHIP` và `QUARANTINED`.

DSetup behavior

DCAM DSetup Factory Tool Design

Single-device ADB helper flow đến imported serial verification.

Web Portal business flow

DCAM Device Provisioning Web Portal Design

Factory Worker QR-based provisioning flow.

Web Portal app model

DCAM Device Provisioning Web Portal App Design

`Login`, `Workspace`, panels/states và user-facing behavior.

Web Portal implementation

DCAM Device Provisioning Web Portal Implementation Design

Firebase Hosting/Auth/Functions/frontend/backend modules.

Web Portal/Device API

DCAM Web Portal & Device API Contract

Request, response, path, schema, reason code, auth context và Firestore.

Device Owner / kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

Device Owner, Lock Task, User Restrictions và Maintenance Mode.

Android runtime

DCAM Android Operation Design

Startup, identity application, provisioning state và recovery.

Concurrency/threading

DCAM Concurrency & Threading Model Design

Execution lanes, critical path và concurrency guardrails.

Performance/resource constraints

DCAM Performance Budget & Resource Constraints

Measurable budgets and release thresholds.

Logging provider ownership

07 - Logging, Diagnostics, Performance & Security

Loggly and Crashlytics provider roles.

Logging requirements

07 - Logging & Diagnostics Requirements

Required events, local-first behavior, provider failure and sensitive logging requirements.

Operational Logging implementation

DCAM Logging & Diagnostics Design

Internal files/rotation, event schema, local queue, Loggly relay, Crashlytics boundary và sanitization.

BDMA logs artifact

DCAM-BDMA Data Contract

Stable `Logs/logs.txt`, record boundary and read-only BDMA access.

Logging validation

DCAM QA Test Strategy & Test Matrix

Local queue, relay, providers, outage, sanitization, non-GMS and BDMA tests.

Security

DCAM Security & Encryption Design

Credentials, worker authorization, QR security, factory Wi-Fi exception and sensitive data.

Media/BDMA contract

DCAM-BDMA Data Contract

Naming, files, MD5, import/write-back and interoperability.

QA/release readiness

DCAM QA Test Strategy & Test Matrix

Master test matrix, regression and exit criteria.

Real-device evidence

DCAM Device POC & Hardware Validation Report

Hardware/firmware/provider validation evidence.

## 4. Web Portal Baseline

### 4.1 Document Ownership

DCAM Device Provisioning Web Portal Design
    → business flow

DCAM Device Provisioning Web Portal App Design
    → user-facing app and Workspace behavior

DCAM Device Provisioning Web Portal Implementation Design
    → frontend/backend implementation

DCAM Web Portal & Device API Contract
    → API and data contract
### 4.2 Approved Direction

Factory Worker opens Web Portal
    ↓
Login through Firebase Authentication
    ↓
Workspace
    ↓
Scan QR displayed by DCAM
    ↓
Review read-only serial_number and device context
    ↓
Enter owner_name and manufacture_date
    ↓
Inline review and submit inside Workspace
    ↓
Cloud Functions/backend validates worker and creates/restores dcam_cloud_device_id
    ↓
Workspace displays Created / Restored / Error / Support Required
Forbidden:

manual serial_number entry
direct serial barcode scan
multiple user-facing roles
separate Confirmation screen
separate Result screen
worker override of duplicate/rebind/conflict
PASS / FAIL / QUARANTINED / READY_TO_SHIP decision
direct frontend write to provisioning collections
## 5. Factory and DSetup Baseline

Factory SOP owns complete production flow and acceptance.
DSetup is only a helper tool inside that flow.
DSetup completion:

```
imported_serial_number == expected serial_number
```

Out of DSetup scope:

Official production record
Factory acceptance
Recording/storage/BDMA/update acceptance checks
PASS / FAIL / QUARANTINED
READY_TO_SHIP
## 6. Logging and Diagnostics Baseline

### 6.1 Channel Ownership

Operational Logging
    → primary operational observability channel
    → centralized provider = Loggly

Crash & Stability Monitoring
    → fatal crash, ANR, unexpected non-fatal and stability
    → provider = Firebase Crashlytics
### 6.2 Artifact Boundary

Internal active/rotated structured logs
        ↓
Persistent upload queue
        ↓
Authenticated Backend Relay
        ↓
Loggly
Separately:

Internal logs
        ↓ sanitized export
Logs/logs.txt
        ↓ read-only ADB access
BDMA diagnostics
Rules:

Internal rotated logs are not BDMA contract artifacts.
Local upload queue is not logs.txt.
Loggly delivery state is not a BDMA artifact.
Crashlytics report/cache is not logs.txt.
Provider failure must not block core DCAM operation.
## 7. Current Document Versions / Status

Document

Current Version / Status

Notes

DCAM Documentation Governance

Approved 1.11

Registers App/Implementation ownership, approval dependencies and logging artifact boundaries.

07 - Logging & Diagnostics Requirements

Approved 1.3

Two channels, local-first, bounded queue, provider failure and sensitive logging requirements.

DCAM-BDMA Data Contract

Approved 1.8

Stable sanitized `Logs/logs.txt` artifact; internal rotated logs/queue excluded.

DCAM QA Test Strategy & Test Matrix

Approved 1.8

Explicit Loggly/Crashlytics/local queue/provider failure/non-GMS/BDMA tests.

DCAM Logging & Diagnostics Design

Draft 0.2

Internal structured files/rotation, BDMA export, relay and Crashlytics implementation.

DCAM Architecture Delivery Profile

Approved 1.1

MVP subset and Working Recording Slice guardrail.

DCAM Web Portal & Device API Contract

Draft 0.6

Factory Worker auth context and API/data ownership.

DCAM Device Provisioning Web Portal Design

Approved 1.0

Factory Worker, QR-only, Login/Workspace business flow.

DCAM Device Provisioning Web Portal App Design

Approved 1.1

Two-screen app and Workspace behavior.

DCAM Device Provisioning Web Portal Implementation Design

Approved 1.1

Firebase implementation direction.

DCAM DSetup Factory Tool Design

Draft 1.0

Stops at imported serial verification.

DCAM Factory Provisioning & Device Production SOP

Draft 1.2

Factory/QA owns final acceptance.

DCAM Security & Encryption Design

Draft 1.2

Worker/QR security and factory Wi-Fi exception.

DCAM Performance Budget & Resource Constraints

Draft

Numeric budget and POC validation.

DCAM Concurrency & Threading Model Design

Draft

Runtime concurrency source of truth.

DCAM Device POC & Hardware Validation Report

Draft

Real-device evidence.

## 8. QA / Release Management Baseline

Document

Purpose

Current Version / Status

DCAM QA Test Strategy & Test Matrix

QA strategy, logging/performance coverage, regression and exit criteria.

Approved 1.8

DCAM Factory Provisioning & Device Production SOP

Physical production procedure and acceptance.

Draft 1.2

DCAM DSetup Factory Tool Design

Factory helper tool behavior.

Draft 1.0

Release rules:

DSetup completion alone does not make a device production-ready.
Web Portal Created/Restored result alone does not make a device production-ready.
Logging provider outage is diagnostics degradation, not automatically a recording/production failure when local diagnostics remain valid.
READY_TO_SHIP requires Factory SOP and QA/release acceptance.
## 9. Important Current Decisions

Decision Area

Current Decision

Offline-first

Core recording, local storage, local auth and BDMA readiness work without cloud after provisioning.

Identity

Serial = Hardware Identity; cloud ID = Cloud Identity; SD file = recovery cache.

Cloud Storage

Firestore current baseline; Realtime Database not used.

Web Portal

Factory Worker; Login/Workspace; QR-only serial; read-only serial.

Web Portal Stack

Firebase Hosting + Authentication + Cloud Functions + Firestore.

DSetup

Stops after imported serial verification; no acceptance decision.

Factory Acceptance

Factory/QA owns production record and `READY_TO_SHIP / QUARANTINED`.

Factory Wi-Fi

SSID/password hardcoded in approved APK; password excluded from logs/API/records/evidence.

Dedicated Device

No external EMM, Android Management API or Managed Google Play baseline.

Update

Self Update / approved APK update is primary.

Operational Logging

Local-first and delivered to Loggly through authenticated Backend Relay.

Crash Monitoring

Crashlytics handles crash/ANR/unexpected non-fatal context.

BDMA Logs

BDMA reads stable sanitized `Logs/logs.txt` only; no internal archive/queue dependency.

Performance

Critical paths follow Performance Budget and Concurrency Model.

BDMA Media/User Sync

Remains ADB-initiated.

## 10. Current Alignment Notes

Group 1 alignment completed:
    Factory SOP / DSetup / Project Home / Web Portal Business Flow

Group 2 alignment completed:
    Web Portal Business Flow / App Design / Implementation Design / Security / API Contract

Group 3 alignment completed:
    Logging Requirements / QA / Data Contract / Logging Design / Documentation Governance
Current logging documents now agree that:

Loggly owns centralized Operational Logging.
Crashlytics owns Crash & Stability Monitoring.
Operational Logging is local-first and bounded.
Logs/logs.txt is the BDMA-facing artifact.
Internal rotated logs and upload queue are implementation-private.
QA owns explicit end-to-end provider and artifact verification.
## 11. Practical Conclusion

DCAM Project Home is the navigation and alignment source for the document set.
Factory SOP owns production procedure and final acceptance.
DSetup stops at imported serial_number verification.
Web Portal uses Factory Worker, Login/Workspace and QR-only serial source.
Backend creates/restores dcam_cloud_device_id through serial_lookup/{serial_number}.
Factory Wi-Fi credentials remain hardcoded in the approved APK by current project decision, but must never be exposed.
Operational Logging uses Loggly through local-first queue and Backend Relay.
Crash & Stability Monitoring uses Firebase Crashlytics.
Logs/logs.txt is the stable sanitized BDMA-facing diagnostics artifact.
Technical details remain owned by their authoritative documents according to DCAM Documentation Governance.