# DCAM Architecture Home

**Page ID**: 47185929  
**Version**: 29  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47185929

---


# DCAM Architecture Home

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Software Architecture Home

Version

Approved 1.23

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

2026-07-09

Related Jira

None

Related Documents

DCAM Project Home, DCAM Documentation Governance, DCAM Requirements Home, DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 09 - System Settings Requirements, 10 - Android Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Device Provisioning Web Portal Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM-BDMA Data Contract, DCAM Non-functional Requirements, DCAM Device Capability & Feature Eligibility Design, DCAM State Machine Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM SQLite Database Design, DCAM Self Update Design, DCAM Security & Encryption Design, DCAM Android Development Standard, DCAM QA Test Strategy & Test Matrix

## 1. Purpose

Trang này là **Architecture Home** cho bộ tài liệu kiến trúc phần mềm của dự án **DCAM**.

Mục tiêu:

Là trang điều hướng chính cho nhóm tài liệu **4.1 - Software Architecture**.

Cho biết kiến trúc DCAM liên kết như thế nào với Requirements, Data Contract, Technical Design, QA, Factory SOP và Android Development Standard.

Tránh trùng lặp rule/principle bằng cách reference tài liệu authoritative theo **DCAM Documentation Governance**.

Ghi nhận kiến trúc User Management offline-first, BDMA/DCAM user sync, operator-authenticated recording, device identity, Web Portal business provisioning, remote config baseline, Android dedicated-device/kiosk deployment, in-app console, Self Update baseline và factory production readiness flow.

Ghi nhận identity baseline theo **ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id** và **DCAM Factory Provisioning & Device Production SOP**: `serial_number` là Hardware Identity / primary recovery key, `dcam_cloud_device_id` là Cloud Identity / primary cloud device id, SD Identity File là recovery cache, không dùng `ANDROID_ID`, `android_id_hash` hoặc `device_lookup/{android_id_hash}` trong current production baseline.

Ghi nhận baseline hiện tại: **không giả định external EMM / Android Management API / Managed Google Play**. DCAM Self Update / APK update là primary update path.

Bộ tài liệu kiến trúc được tổ chức theo dạng nhiều trang ngắn, mỗi trang phụ trách một chủ đề kiến trúc rõ ràng.

Cấu trúc cây tài liệu đầy đủ thuộc **DCAM Project Home / Current Documentation Structure**. Trang này chỉ giữ reading order và architecture ownership để tránh trùng lặp.

## 2. Software Architecture Reading Order

Order

Document

Purpose

1

DCAM Architecture Home

Trang điều hướng bộ tài liệu kiến trúc.

2

DCAM Project Home

Xem toàn bộ cấu trúc tài liệu DCAM hiện tại và important decisions.

3

DCAM Documentation Governance

Hiểu rule maintain tài liệu và source-of-truth ownership.

4

DCAM Requirements Home

Hiểu cấu trúc Requirements và các nhóm Functional Requirements.

5

04 - Device Configuration Requirements

Source of truth requirement cho serial, SD Identity File, `dcam_config.cson` scope, identity requirement và provisioning-required behavior.

6

05 - User & Device Operation Requirements

Source of truth cho user/operator requirement, login policy và emergency override.

7

09 - System Settings Requirements

Source of truth cho system settings, remote config identity/apply policy, kiosk requested-policy settings và AutoUpdate preconditions.

8

10 - Android Device Operation Requirements

Requirement-level Android operation behavior, including dedicated-device/kiosk and in-app console requirement impact.

9

DCAM-BDMA Data Contract

Source of truth cho storage, media naming, MD5, CSON, DB, logs, identity boundary, user sync và BDMA behavior.

10

DCAM Non-functional Requirements

Quality baseline cho offline auth, no-timeout session, recovery, security, capability-aware runtime, kiosk reliability và offline-first.

11

01 - Architecture Overview

High-level architecture context và scope.

12

02 - Architecture Principles

Architecture principles.

13

03 - Android Platform & Compatibility Strategy

Android compatibility, GMS/non-GMS, BodyCamera fragmentation, DCAM-as-DPC/kiosk compatibility và device capability.

14

04 - Application & Module Architecture

Layering, module direction, dependency rules, policy manager boundary, console services and provider/adapter boundaries.

15

05 - Data, Storage & BDMA Architecture

Data/storage/BDMA architecture overview.

16

06 - Cloud Services, Update & Configuration Architecture

Cloud provider abstraction, device identity, Web Portal provisioning boundary, remote config, Self Update provider và future WebServer direction.

17

07 - Logging, Diagnostics, Performance & Security

Logging, diagnostics, performance và security architecture baseline.

18

08 - DCAM-BDMA Integration Boundary

Boundary giữa DCAM Android, BDMA Desktop và WebServer/Firebase khi liên quan.

19

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Architecture decision cho Android dedicated-device/kiosk deployment.

20

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id

Architecture decision cho device identity baseline: `serial_number` as Hardware Identity / recovery key, `dcam_cloud_device_id` as Cloud Identity and SD Identity File as recovery cache.

21

DCAM Android Device Owner & Kiosk Policy Design

Device Owner/DPC-capable policy, Lock Task Mode, User Restrictions, Home/Launcher policy, Maintenance Mode, policy recovery and no external EMM baseline.

22

DCAM In-App Operation, Device Settings & Media Console Design

Record/Live View default, Setting hub, child modules, read-only File/Media, Login/User settings, Controlled Exit Kiosk, Maintenance Password Gate and Play Store fallback UX.

23

DCAM Android Operation Design

Startup, serial-based identity restore, provisioning state, kiosk policy verification, console module readiness, login screen, session lifecycle, runtime orchestration và recovery.

24

DCAM Device Provisioning Web Portal Design

DCAM business provisioning flow, Web Portal screens, optional QR/serial flow, backend API direction, states, audit và error handling.

25

DCAM Recording & Capture Design

RecordingController, operator gate, emergency override và media attribution.

26

DCAM SQLite Database Design

`dcam.db`, identity/provisioning/cache tables, user/auth/session tables, console setting state, update state, write-back, recovery và sync state.

27

DCAM Self Update Design

Primary no-EMM update path: APK artifact/version manifest/validation/install and kiosk policy restore.

28

DCAM Security & Encryption Design

Device identity security, provisioning security, credential/auth security, kiosk policy security, Maintenance Password Gate, update security and emergency override auditability.

29

DCAM State Machine Design

State-driven behavior, operator auth guard, kiosk policy states, maintenance/update guards and runtime coordination.

30

DCAM Device Capability & Feature Eligibility Design

Official feature eligibility states, GMS/Play Store availability capability and runtime pruning rule.

31

DCAM Android Development Standard

Implementation standard cho code Android.

32

DCAM QA Test Strategy & Test Matrix

QA/release readiness baseline.

33

DCAM Device POC & Hardware Validation Report

Real-device evidence used to close firmware/hardware dependent TBDs.

34

DCAM Factory Provisioning & Device Production SOP

Factory/admin ready-to-ship procedure for raw/factory-reset BodyCamera devices.

## 3. Architecture Document Set

Architecture Page

Responsibility

Related Authoritative / Detail Documents

01 - Architecture Overview

High-level system context và scope.

Product Vision, MVP Scope, Requirements Home, User Requirements, Cloud Architecture

02 - Architecture Principles

Core architecture principles.

Documentation Governance, NFR, Security Design, ADR records

03 - Android Platform & Compatibility Strategy

Android version, device fragmentation, GMS/non-GMS, BodyCamera behavior, DCAM-as-DPC/kiosk compatibility, Play Store availability and device capability strategy.

Device Capability Design, Android Operation Design, Kiosk Policy Design, In-App Console, Security Design, Device POC

04 - Application & Module Architecture

Layers, modules, dependency direction, auth/session/identity repositories, policy manager boundary, in-app console services and provider/adapter boundaries.

State Machine Design, Device Capability Design, Kiosk Policy Design, In-App Console Design, Android Development Standard

05 - Data, Storage & BDMA Architecture

Data/storage/BDMA architecture overview.

DCAM-BDMA Data Contract, Storage Design, SQLite Database Design

06 - Cloud Services, Update & Configuration Architecture

Cloud providers, device identity, Web Portal provisioning boundary, remote config, Self Update provider and future WebServer direction.

Device Config Requirements, System Settings Requirements, Android Operation Design, Web Portal Provisioning Design, Self Update, Security Design, ADR - DCAM Device Identity Baseline

07 - Logging, Diagnostics, Performance & Security

Architecture-level quality baseline cho logging, diagnostics, performance và security.

Logging Requirements, Security Design, Kiosk Policy Design, In-App Console, NFR

08 - DCAM-BDMA Integration Boundary

Responsibility split giữa DCAM Android, BDMA Desktop và WebServer/Firebase identity boundary.

Data Contract, BDMA Integration Requirements, BDMA Technical Design, Cloud Architecture, Kiosk Policy Design

## 4. Authoritative Rule Ownership

Architecture pages không nên copy full rule tables từ các tài liệu khác. Các trang này chỉ nên reference authoritative document và thêm local architecture notes khi cần.

Shared Rule / Principle

Authoritative Document

Architecture Usage

Factory provisioning, DSetup, serial recovery/scan/injection, SD Identity File recovery cache and ready-to-ship decision

DCAM Factory Provisioning & Device Production SOP

Architecture references SOP for production execution.

Device identity baseline

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id + DCAM Factory Provisioning & Device Production SOP + 04 - Device Configuration Requirements

`serial_number` = Hardware Identity / recovery key; `dcam_cloud_device_id` = Cloud Identity; SD Identity File = recovery cache; no Android ID/hash.

Device config file scope and serial identity

04 - Device Configuration Requirements

Architecture chỉ reference requirement.

Firebase/WebServer identity and provider boundary

06 - Cloud Services, Update & Configuration Architecture

Architecture reference provider/provisioning boundary.

Web Portal provisioning business flow, screens, QR, API direction, states, audit and error handling

DCAM Device Provisioning Web Portal Design

Architecture reference tài liệu này như DCAM business provisioning source of truth.

Device Owner/DPC-capable policy, Lock Task Mode, User Restrictions, Home/Launcher policy and Maintenance Mode

DCAM Android Device Owner & Kiosk Policy Design

Architecture reference tài liệu này như dedicated-device/kiosk policy source of truth.

In-App Console UX and navigation

DCAM In-App Operation, Device Settings & Media Console Design

Architecture reference Record/Live View default, Setting hub, Back behavior and child modules.

Controlled temporary kiosk exit and Maintenance Password Gate

DCAM In-App Operation, Device Settings & Media Console Design + Security Design + Kiosk Policy Design

Architecture reference only; does not duplicate security details.

No external EMM / Android Management API / Managed Google Play baseline

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Architecture must reference ADR and must not assume policy-driven update.

DCAM Self Update / APK update

DCAM Self Update Design

Architecture reference primary update path for current device baseline.

Optional manual Play Store fallback

DCAM In-App Operation, Device Settings & Media Console Design + Self Update Design

Architecture states fallback only if device has GMS/Play Store and approved maintenance/factory account.

Remote config identity, cache and apply policy

09 - System Settings Requirements

Architecture reference setting/apply ownership.

Identity/provisioning/cache DB tables

DCAM SQLite Database Design

Architecture reference DB ownership và table groups.

Startup identity restore, kiosk policy verification, console readiness and provisioning state

DCAM Android Operation Design

Architecture reference Android runtime behavior.

Device identity/provisioning/kiosk policy/security/update security

DCAM Security & Encryption Design

Architecture reference security constraints.

User/operator management, login policy and emergency override requirement

05 - User & Device Operation Requirements

Architecture chỉ reference requirement.

BDMA/DCAM user sync, DB write-back and Data Contract boundary

DCAM-BDMA Data Contract

Architecture reference contract và sync boundary.

User/auth/session DB schema and transaction behavior

DCAM SQLite Database Design

Architecture reference DB ownership và table groups.

Recording operator gate and emergency override attribution

DCAM Recording & Capture Design

Architecture chỉ summarize guard.

Feature eligibility states and runtime pruning

DCAM Device Capability & Feature Eligibility Design

Architecture pages reference official state set.

State machine guards and runtime registration

DCAM State Machine Design

Architecture pages reference transition/guard behavior.

Media naming, folders, `_IMP`, `_enc`, MD5, BDMA cleanup

DCAM-BDMA Data Contract

Data/storage architecture reference contract.

## 5. Relationship with Requirements, Technical Design and Production SOP

text
Area

Architecture Role

Detail / Source of Truth

Functional behavior

Architecture phải align với Requirements.

DCAM Requirements Home and Functional Requirements 01–10

Device identity/provisioning

Architecture phải align với ADR Identity Baseline, Factory SOP, Cloud/Config architecture và Device Configuration requirements.

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id + DCAM Factory Provisioning & Device Production SOP + 04 - Device Configuration Requirements + 06 - Cloud Services, Update & Configuration Architecture

Web Portal provisioning flow

Architecture reference detailed DCAM business provisioning flow.

DCAM Device Provisioning Web Portal Design

Kiosk policy

Architecture reference Device Owner/DPC, Lock Task, User Restrictions, Home/Launcher and Maintenance Mode policy.

DCAM Android Device Owner & Kiosk Policy Design + ADR

In-app console

Architecture reference console UX and controlled maintenance behavior.

DCAM In-App Operation, Device Settings & Media Console Design

Update

Architecture must use current no-EMM update baseline.

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision + DCAM Self Update Design + System Settings + Security Design

Factory production

Architecture references factory execution flow, ready-to-ship checklist and quarantine behavior.

DCAM Factory Provisioning & Device Production SOP

Remote config

Architecture cung cấp provider boundary; System Settings định nghĩa apply rules; Kiosk Policy Design định nghĩa policy apply boundary.

09 - System Settings Requirements + DCAM Android Device Owner & Kiosk Policy Design

User/operator behavior

Architecture phải align với offline user management và recording login gate.

05 - User & Device Operation Requirements

Quality constraints

Architecture phải satisfy NFR.

DCAM Non-functional Requirements

Data and BDMA contract

Architecture không được redefine contract rules.

DCAM-BDMA Data Contract

Runtime behavior

Architecture cung cấp boundaries; detailed transitions nằm trong technical design.

DCAM State Machine Design + DCAM Android Operation Design

Device-specific feature gating

Architecture reference official capability/eligibility states.

DCAM Device Capability & Feature Eligibility Design

Android implementation

Architecture định hướng; implementation tuân theo coding standard.

DCAM Android Development Standard

## 6. Current Architecture Status

Area

Status

Notes

Requirements

Active / expanded baseline

Functional Requirements 01–10 và NFR đã có.

Device Identity

ADR approved / requirement approved / Draft technical baseline

ADR identity baseline đã approved: `serial_number` = Hardware Identity / recovery key, `dcam_cloud_device_id` = Cloud Identity, SD Identity File = recovery cache.

Web Portal Provisioning

Draft technical/business flow baseline

DCAM business provisioning, không phải Device Owner setup.

Kiosk Policy

Draft technical baseline / ADR approved direction

Device Owner/DPC-capable policy, Lock Task Mode, User Restrictions, Home/Launcher policy, Maintenance Password Gate and controlled Maintenance Mode.

External EMM / Managed Google Play

Not baseline / ADR approved

Current device baseline per ADR does not have external EMM / Android Management API / Managed Google Play policy-driven update.

In-App Console

Draft technical baseline

Record/Live View default screen, Setting hub, child modules, read-only media/file, Login/User settings and Controlled Mode.

Update

Draft technical baseline

Primary path is DCAM Self Update / APK update. Play Store is optional manual fallback only if GMS/Play Store exists.

Remote Config

Approved apply/cache baseline / Exact payload schema TBD

Identity, fetch/cache/apply architecture and setting groups đã được defined; exact payload schema/field names thuộc System Settings / future Remote Config design.

User Management

Approved requirement / Draft technical baseline

Offline user management, BDMA sync, login policy và emergency override đã được defined.

Data Contract

Approved baseline

Source of truth cho DCAM-BDMA data/file/import/user sync/identity boundary behavior.

Software Architecture

Approved baseline

Architecture pages 01–08 đã có dưới Architecture Home.

Technical Design

Draft / expanding

Major technical design pages đã có dưới 4.2.

Device Capability

Draft baseline

Official state set và runtime pruning rule đã được defined.

State Machine

Approved baseline

Runtime guard, operator auth guard, kiosk policy states và state-machine registration behavior đã được defined.

Security

Draft baseline

Device identity/provisioning security, credential/auth security, kiosk policy security, Maintenance Password Gate, update/package security và encryption direction đã được defined.

Android Development Standard

Active

Implementation standard nằm dưới 4.3.

Factory SOP

Draft baseline

Defines repeatable raw-device to ready-to-ship procedure and quarantine rules.

ADR

Active

Dedicated-device/kiosk decision và device identity baseline decision đã được ghi nhận bằng ADR.

## 7. Maintenance Rules

Rule

Description

Follow Documentation Governance

Ownership, review, lifecycle và change control tuân theo DCAM Documentation Governance.

Define Once, Reference Elsewhere

Shared rules thuộc authoritative documents. Các trang khác chỉ reference bằng short summaries.

Keep Architecture Pages Short

Architecture pages nên tập trung vào architecture boundary và direction.

Use Technical Design for Detail

Detailed runtime/database/storage/security/auth/identity/provisioning/kiosk/console/update behavior thuộc 4.2 Technical Design.

Use SOP for Production Execution

Factory thao tác từng thiết bị, ready-to-ship/quarantine decision và production record thuộc Factory SOP.

Align with Requirements

Architecture changes phải được check với Requirements và NFR.

Align with Data Contract

Không redefine naming, MD5, BDMA cleanup, user sync, identity boundary hoặc storage contract ngoài Data Contract.

Create ADR for Major Decisions

Long-term architectural choices nên được ghi nhận bằng ADR.

## 8. Practical Conclusion

**DCAM Architecture Home** là trang navigation và alignment cho software architecture.

Current architecture-to-production baseline là:

textArchitecture documents giải thích boundaries và relationships. Technical shared rules nằm trong authoritative design docs. Factory SOP biến technical baseline thành quy trình chuẩn bị thiết bị thật để giao.

Current identity baseline is defined by **ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id**:

text