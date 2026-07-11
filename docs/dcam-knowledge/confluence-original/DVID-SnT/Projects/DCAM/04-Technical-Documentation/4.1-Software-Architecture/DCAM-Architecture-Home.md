# DCAM Architecture Home

**Page ID**: 47185929  
**Version**: 36  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47185929

---


# DCAM Architecture Home

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Software Architecture Home

Version

Approved 1.30

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / BDMA Lead / Security Reviewer / Cloud Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.1 - Software Architecture

Target Audience

PM/BA, Tech Lead, Android Developers, AI/ML Engineer, QA, BDMA Team, Cloud/WebServer Team, Factory, Support, Stakeholders

Last Updated

2026-07-10

Related Jira

None

Related Documents

DCAM Project Home, DCAM Documentation Governance, DCAM Requirements Home, 07 - Logging & Diagnostics Requirements, DCAM Architecture Delivery Profile, DCAM Concurrency & Threading Model Design, DCAM Performance Budget & Resource Constraints, DCAM Logging & Diagnostics Design, DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Device Provisioning Web Portal Implementation Design, DCAM Security & Encryption Design, DCAM Android Operation Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM-BDMA Data Contract, DCAM QA Test Strategy & Test Matrix, DCAM Device POC & Hardware Validation Report

## 1. Purpose

Trang này là **Architecture Home** cho bộ tài liệu kiến trúc phần mềm của dự án **DCAM**.

Mục tiêu:

Điều hướng nhóm tài liệu **4.1 - Software Architecture**.

Liên kết Architecture với Requirements, Technical Design, API/Data Contract, QA và Factory SOP.

Duy trì source-of-truth ownership theo **DCAM Documentation Governance**.

Ghi nhận Delivery Profile, Concurrency Model, Performance Budget và Logging Architecture.

Ghi nhận Web Portal baseline: `Factory Worker`, `Login + Workspace`, QR displayed by DCAM là serial source duy nhất, Firebase Hosting/Auth/Functions/Firestore.

Ghi nhận identity baseline: `serial_number`, `dcam_cloud_device_id`, `serial_lookup/{serial_number}` và SD Identity File recovery cache.

Ghi nhận no-external-EMM baseline và Self Update là primary update path.

Phân biệt internal logging implementation với BDMA-facing `Logs/logs.txt` contract.

## 2. Software Architecture Reading Order

Order

Document

Purpose

1

DCAM Architecture Home

Trang điều hướng kiến trúc.

2

DCAM Project Home

Cấu trúc tài liệu và current decisions.

3

DCAM Documentation Governance

Ownership, approval dependency và maintenance rules.

4

DCAM Architecture Delivery Profile

MVP vs Target Architecture và Working Recording Slice.

5

DCAM Concurrency & Threading Model Design

Execution lanes và critical-path safety.

6

DCAM Performance Budget & Resource Constraints

Measurable performance/resource targets.

7

DCAM Requirements Home

Functional requirements.

8

07 - Logging & Diagnostics Requirements

Required logging events, local-first behavior, provider failure and sensitive logging requirements.

9

DCAM-BDMA Data Contract

Data/storage/media/BDMA contract, including stable `Logs/logs.txt`.

10

DCAM Non-functional Requirements

Quality baseline.

11

01 - Architecture Overview

High-level context.

12

02 - Architecture Principles

Core principles.

13

03 - Android Platform & Compatibility Strategy

Android/GMS/non-GMS/device strategy.

14

04 - Application & Module Architecture

Layers/modules/dependencies.

15

05 - Data, Storage & BDMA Architecture

Data/storage/BDMA overview.

16

06 - Cloud Services, Update & Configuration Architecture

Cloud, provisioning, config and update boundary.

17

07 - Logging, Diagnostics, Performance & Security

Logging provider ownership and quality baseline.

18

DCAM Logging & Diagnostics Design

Internal files/rotation, queue, `logs.txt` export, relay and Crashlytics detail.

19

08 - DCAM-BDMA Integration Boundary

DCAM/BDMA/WebServer responsibility split.

20

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Dedicated-device decision.

21

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id

Identity decision.

22

DCAM Android Device Owner & Kiosk Policy Design

Device Owner/kiosk policy.

23

DCAM Android Operation Design

Startup/runtime/recovery.

24

DCAM Device Provisioning Web Portal Design

Approved Factory Worker QR-only business flow.

25

DCAM Device Provisioning Web Portal App Design

Approved Login/Workspace app model.

26

DCAM Device Provisioning Web Portal Implementation Design

Approved Firebase implementation architecture.

27

DCAM Web Portal & Device API Contract

API/auth/schema/reason-code contract.

28

DCAM Security & Encryption Design

Provisioning, credential, logging, update and kiosk security.

29

DCAM Recording & Capture Design

Recording behavior.

30

DCAM Storage Design

Storage/finalization/recovery.

31

DCAM SQLite Database Design

DB schema and recovery.

32

DCAM Self Update Design

APK update path.

33

DCAM State Machine Design

Runtime state guards.

34

DCAM Device Capability & Feature Eligibility Design

Capability states.

35

DCAM Android Development Standard

Implementation standard.

36

DCAM QA Test Strategy & Test Matrix

QA/release readiness including explicit logging tests.

37

DCAM Device POC & Hardware Validation Report

Real-device/provider evidence.

38

DCAM Factory Provisioning & Device Production SOP

Factory production procedure.

## 3. Architecture Document Set

Architecture Page

Responsibility

Related Detail Documents

DCAM Architecture Delivery Profile

MVP vs Target and delivery guardrails.

MVP Scope, Development Plan, Application Architecture

DCAM Concurrency & Threading Model Design

Execution lanes and critical path safety.

Recording, Storage, SQLite, State Machine

DCAM Performance Budget & Resource Constraints

Numeric latency/resource/stability targets.

NFR, QA, Device POC

01 - Architecture Overview

System context.

Product Vision, Requirements

02 - Architecture Principles

Core principles.

Governance, NFR, Security, ADR

03 - Android Platform & Compatibility Strategy

Android/device compatibility.

Capability, Device POC, Kiosk

04 - Application & Module Architecture

Layers/modules/dependency direction.

Delivery Profile, State Machine, Android Standard

05 - Data, Storage & BDMA Architecture

Data/storage/BDMA overview.

Data Contract, Storage, SQLite

06 - Cloud Services, Update & Configuration Architecture

Cloud identity, provisioning, config and update boundaries.

Web Portal set, API Contract, Self Update

07 - Logging, Diagnostics, Performance & Security

Provider ownership and quality baseline.

Logging Requirements, Logging Design, Security, Performance Budget, QA

08 - DCAM-BDMA Integration Boundary

DCAM/BDMA/WebServer responsibility split.

Data Contract, BDMA Design

## 4. Authoritative Rule Ownership

Shared Rule / Principle

Authoritative Document

Summary

MVP delivery scope

DCAM Architecture Delivery Profile

Target Architecture không đồng nghĩa full MVP implementation.

Concurrency/threading

DCAM Concurrency & Threading Model Design

One source for execution lanes and critical path.

Performance targets

DCAM Performance Budget & Resource Constraints

Numeric targets belong here.

Logging provider ownership

07 - Logging, Diagnostics, Performance & Security

Loggly = Operational Logging; Crashlytics = Crash/Stability.

Logging requirements

07 - Logging & Diagnostics Requirements

Events, local-first, bounded queue, provider failure and sensitive logging.

Logging implementation

DCAM Logging & Diagnostics Design

Facade, schema, internal files/rotation, queue, relay, `logs.txt` export and sanitization.

BDMA logs artifact

DCAM-BDMA Data Contract

Stable sanitized `Logs/logs.txt`, UTF-8/single-record boundary and read-only BDMA access.

Logging validation

DCAM QA Test Strategy & Test Matrix

Loggly/Crashlytics/local queue/outage/non-GMS/BDMA test coverage.

Factory production and acceptance

DCAM Factory Provisioning & Device Production SOP

Official record and final acceptance.

DSetup behavior

DCAM DSetup Factory Tool Design

Stops after imported serial verification.

Device identity

Identity ADR + Requirements

Serial hardware key, cloud ID, SD recovery cache.

Web Portal business flow

DCAM Device Provisioning Web Portal Design

Factory Worker, QR-only, Login/Workspace.

Web Portal app model

DCAM Device Provisioning Web Portal App Design

Screens, panels/states and app behavior.

Web Portal implementation

DCAM Device Provisioning Web Portal Implementation Design

Firebase Hosting/Auth/Functions/Firestore.

Web Portal API

DCAM Web Portal & Device API Contract

Request/response/path/auth/schema/reason code.

Web Portal security

DCAM Security & Encryption Design

Worker authorization, QR security, camera privacy, no override.

Factory Wi-Fi credential baseline

Factory SOP + Security Design

Current approved project decision and handling restrictions.

Device Owner/kiosk

DCAM Android Device Owner & Kiosk Policy Design

Device Owner, Lock Task, restrictions and maintenance.

Self Update

DCAM Self Update Design

Primary update path.

SQLite

DCAM SQLite Database Design

Schema/transaction/recovery.

Recording

DCAM Recording & Capture Design

Operator gate and attribution.

Media/BDMA

DCAM-BDMA Data Contract

Naming, MD5, import and write-back.

## 5. Logging Architecture Boundary

DCAM Modules
    ↓
Logging Facade
    ↓
Internal active/rotated structured logs
    ├────────→ sanitized Logs/logs.txt → BDMA read-only
    ↓
Persistent upload queue
    ↓
Authenticated Backend Relay
    ↓
Loggly
Separately:

Unexpected fatal/non-fatal/ANR context
    ↓
Crashlytics Adapter
    ↓
Firebase Crashlytics
Architecture rules:

Crashlytics does not replace Operational Logging.
Internal rotated logs are not BDMA contract artifacts.
Local upload queue is not logs.txt.
Loggly delivery state is not a BDMA artifact.
Crashlytics report/cache is not logs.txt.
Provider failure must not block recording, emergency, finalization or core offline operation.
## 6. Architecture-to-Implementation Relationship

Requirements / Data Contract / NFR
        ↓
Architecture Delivery Profile
        ↓
Concurrency Model + Performance Budget
        ↓
Software Architecture 01–08
        ↓
ADR + Technical Design 4.2
        ↓
Web/Android Implementation Design + Development Standard
        ↓
QA + Device POC
        ↓
Factory SOP

Area

Architecture Role

Detail Source

Delivery

Separate MVP and Target scope.

Delivery Profile

Runtime safety

Define execution boundaries and budgets.

Concurrency + Performance Budget

Logging

Define channels/providers and artifact boundary.

07 Architecture + Requirements + Logging Design + Data Contract

Web Portal

Define cloud/business boundary and reference document set.

Business Flow + App + Implementation + API + Security

Runtime behavior

Define boundaries; detailed states in Technical Design.

State Machine + Android Operation

Hardware/provider validation

Validate assumptions on real device.

Device POC

Production

Apply approved design through procedure.

Factory SOP

## 7. Current Architecture Status

Area

Status

Notes

Architecture Delivery Profile

Approved

MVP vs Target and Working Recording Slice.

Concurrency & Threading

Draft

Runtime execution baseline.

Performance Budget

Draft

Numeric targets require Device POC validation.

Logging Architecture

Approved

Loggly and Crashlytics ownership approved.

Logging Requirements

Approved 1.3

Local-first, bounded queue, provider failure and sensitive logging requirements.

Logging Design

Draft 0.2

Internal files/rotation, `logs.txt` export, relay and Crashlytics implementation.

Logging QA

Approved 1.8

Explicit provider, queue, outage, non-GMS, sanitization and BDMA tests.

Requirements

Active

Functional Requirements 01–10 and NFR available.

Device Identity

ADR Approved

Serial + cloud ID + SD recovery cache.

Web Portal Provisioning

Business/App/Implementation Approved; API/Security Draft

Approved Factory Worker QR-only two-screen flow and Firebase implementation.

Kiosk Policy

Draft / ADR Approved Direction

Device Owner, Lock Task and Maintenance Mode.

External EMM / Managed Google Play

Not Baseline

No external EMM/AMA/Managed GP.

Data Contract

Approved 1.8

Includes stable `Logs/logs.txt` BDMA contract.

Software Architecture

Approved

Architecture pages 01–08.

Technical Design

Draft / Expanding

Major designs available under 4.2.

State Machine

Approved

Runtime guards and coordination.

Factory SOP

Draft

Production and quarantine procedure.

## 8. Maintenance Rules

Rule

Description

Follow Documentation Governance

Ownership/review/lifecycle follow Governance.

Define Once, Reference Elsewhere

Shared rules live in authoritative docs.

Keep Architecture Focused

Architecture describes boundary and direction.

Separate Target vs MVP

Use Delivery Profile for implementation scope.

Use Concurrency and Performance Sources

No conflicting thread/budget rules.

Use Logging Sources Separately

Architecture owns providers; Requirements own must-have behavior; Design owns implementation; Data Contract owns `logs.txt`; QA owns verification.

Use Web Portal Document Set

Business, app, implementation, API and security ownership remain separate.

Use SOP for Production

Ready-to-ship/quarantine and official records belong Factory SOP.

Align with Requirements and Data Contract

Do not redefine contract behavior.

Sync Navigation after Changes

Update Project Home and Architecture Home after authoritative version/status changes.

Create ADR for Major Decisions

Long-term choices require ADR when appropriate.

## 9. Practical Conclusion

DCAM Architecture Home is the architecture navigation and alignment page.
Working recording/storage/BDMA slice goes first for MVP.
Concurrency and Performance Budget protect critical runtime paths.
Operational Logging uses Loggly through local-first queue and authenticated Backend Relay.
Crash & Stability Monitoring uses Firebase Crashlytics.
Internal active/rotated logs and upload queue are implementation-private.
Logs/logs.txt is the stable sanitized BDMA-facing artifact.
QA explicitly verifies providers, queue, outage, sanitization, non-GMS and BDMA access.
Web Portal uses Factory Worker only, Login and Workspace only, and QR-only read-only serial.
Firebase Hosting, Authentication, Cloud Functions and Firestore form the approved Web implementation baseline.
DSetup stops at imported serial verification.
Factory/QA owns official production record and READY_TO_SHIP / QUARANTINED.
Factory Wi-Fi credential handling follows the current Factory SOP and Security Design decision.
serial_number is Hardware Identity; dcam_cloud_device_id is Cloud Identity; SD Identity File is recovery cache.
Do not use ANDROID_ID, android_id_hash or device_lookup/{android_id_hash}.