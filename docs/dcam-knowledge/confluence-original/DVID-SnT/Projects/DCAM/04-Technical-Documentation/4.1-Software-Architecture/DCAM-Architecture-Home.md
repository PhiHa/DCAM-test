# DCAM Architecture Home

**Page ID**: 47185929  
**Version**: 39  
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

Approved 1.33

Status

Approved

Approval Scope

Architecture/Technical Design navigation, reading order và current-summary only; không sở hữu mutable version/status table.

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / BDMA Lead / Security Reviewer / Cloud Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.1 - Software Architecture

Target Audience

PM/BA, Tech Lead, Developers, QA, BDMA Team, Cloud/WebServer Team, Factory, Support

Last Updated

2026-07-14

Related Jira

None

Related Documents

[DCAM Document Status Registry](/wiki/spaces/DVID/pages/51085647/DCAM+Document+Status+Registry), [DCAM Requirement–Design–Test Traceability Matrix](/wiki/spaces/DVID/pages/51085669/DCAM+Requirement+Design+Test+Traceability+Matrix), DCAM Project Home, DCAM Documentation Governance, DCAM Release & Build Applicability Matrix, DCAM Requirements Home, DCAM Architecture Delivery Profile, DCAM Performance Budget & Resource Constraints, DCAM Logging & Diagnostics Design, DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Web Portal & Device API Contract, DCAM Android Device Owner & Kiosk Policy Design, DCAM Security & Encryption Design, DCAM Android Operation Design, DCAM Storage Design, DCAM Recording & Capture Design

## 1. Purpose

Architecture navigation and alignment page for DCAM. Detailed rule ownership follows **DCAM Documentation Governance**.

Trang này không duy trì version/status table của từng Architecture hoặc Technical Design. Trạng thái tập trung được quản lý tại **DCAM Document Status Registry**.

## 2. Recommended Reading Order

Order

Document

Purpose

1

DCAM Release & Build Applicability Matrix

Active build scope and applicable requirements/tests.

2

DCAM Architecture Delivery Profile

MVP vs Target Architecture.

3

DCAM Requirement–Design–Test Traceability Matrix

Requirement-to-design-to-test coverage and gaps.

4

DCAM Performance Budget & Resource Constraints

Numeric targets and hardware-dependent budgets.

5

DCAM Concurrency & Threading Model Design

Execution lanes and critical path.

6

DCAM Requirements Home / NFR

Functional and quality requirements.

7

Architecture 01–08

System boundaries and decisions.

8

ADRs

Long-term decisions.

9

Technical Designs

Runtime implementation behavior.

10

API/Data Contracts

Interoperability and cloud/data boundaries.

11

QA / Device POC / Factory SOP

Verification and production execution.

12

DCAM Document Status Registry

Current version/status/approval scope summary.

## 3. Current Architecture Baselines

Active build = DCAM MVP Internal Build 0.1
Active gate = Working Recording Slice

Identity:
serial_number = Hardware Identity
dcam_cloud_device_id = Cloud Identity
serial_lookup/{serial_number} = create/restore path

Web:
Firebase Hosting + Authentication + Cloud Functions + Firestore
Factory Worker / Login + Workspace / QR-only read-only serial

Logging:
Operational Logging → Loggly
Crash & Stability → Crashlytics
BDMA diagnostics → Logs/logs.txt

Dedicated device:
No external EMM / Android Management API / Managed Google Play
Preferred model = DCAM-as-DPC / local Device Owner
Primary update = DCAM Self Update / approved APK update
## 4. Status and TBD Classification

The authoritative taxonomy belongs to **DCAM Documentation Governance** and **DCAM Document Status Registry**.

Classification

Meaning

Approved Direction

Architecture/product direction is accepted; exact implementation/evidence may remain open.

Approved Provisional Baseline

Current implementation/QA baseline may be adjusted using approved evidence.

Approved Pending Device POC

Target-device/OEM evidence is required before production interpretation.

Approved Pending Security Review

Algorithm/key/policy details remain open.

Approved for Build `<profile>`

Baseline is approved only for the named build.

Production Approved

Required production evidence and approval gates are complete.

Future / Deferred

Feature is not active in the current build/phase.

Do not label a whole area `TBD` when an authoritative document already defines its direction or baseline.

Do not interpret `Approved Direction`, `Approved Provisional Baseline`, `Approved Pending Device POC` or `Approved Pending Security Review` as `Production Approved`.

## 5. Resolved Stale-TBD Areas

Area

Current Resolution

Cloud/Web providers

Firebase Hosting/Auth/Functions/Firestore approved.

Performance telemetry

`[PERF]` Operational Logging → Loggly; crash/ANR → Crashlytics.

QR minimum schema

Defined in API Contract.

Logical provisioning endpoint

`POST /v1/factory/provisioning/devices`.

Firestore logical paths

`workers`, `serial_lookup`, `devices`, `audit_events`.

Frontend write boundary

Backend-only production writes; no direct frontend write.

Maintenance entry model

Authorized actor + Maintenance Password Gate + Controlled Mode.

DPC ownership direction

DCAM-as-DPC / local Device Owner.

Factory Device Owner setup

DSetup + ADB `dpm set-device-owner` baseline.

Storage thresholds

Performance Budget formulas and provisional 500 MB margin/headroom.

Recording finalization budget

Critical finalization `≤ 5s`.

Recording/recovery QA coverage

Defined in QA Matrix; build applicability still controls release blocking.

Required-policy missing behavior

Required/degraded state and block normal production operation by default.

## 6. Genuine Remaining TBD Groups

Group

Examples

Required Status Interpretation

Device POC

Target model/firmware, Camera SDK, physical paths, Lock Task/OEM behavior, silent install.

Approved Pending Device POC / Blocked

Security Review

Encryption algorithms, key management, QR cryptographic policy, exact maintenance policy values.

Approved Pending Security Review / Blocked

Backend/Deployment

Firebase Security Rules, physical Cloud Function mapping, retention and environment config.

Approved Direction or Deployment Pending

Product/Factory

Worker account model, owner master data, duplicate/rebind support process.

Approved Direction / Product decision pending

Implementation

Room/raw SQLite, exact table schemas, retry/backoff values, internal file paths.

Approved Provisional Baseline / implementation TBD

Future Features

Live Streaming, PTT, advanced AI, full GPS route and related protocols.

Future / Deferred

## 7. Document Status and Traceability

Need

Source of Truth

Current Architecture/Design version and qualified status

[DCAM Document Status Registry](/wiki/spaces/DVID/pages/51085647/DCAM+Document+Status+Registry)

Requirement → Build → Design → QA → Jira → Evidence mapping

[DCAM Requirement–Design–Test Traceability Matrix](/wiki/spaces/DVID/pages/51085669/DCAM+Requirement+Design+Test+Traceability+Matrix)

Build-level applicability

DCAM Release & Build Applicability Matrix

Architecture ownership and status rules

DCAM Documentation Governance

Do not copy mutable versions into Architecture Home.
Update Architecture Home only when technical ownership, reading order, architecture baseline or navigation changes.
## 8. Practical Conclusion

Architecture Home owns architecture navigation and current architecture baseline.
Applicability Matrix determines which target architecture capabilities are mandatory now.
Document Status Registry owns the cross-document version/status summary.
Traceability Matrix exposes requirement-to-design-to-test coverage and gaps.
Approved direction is not the same as production validation.