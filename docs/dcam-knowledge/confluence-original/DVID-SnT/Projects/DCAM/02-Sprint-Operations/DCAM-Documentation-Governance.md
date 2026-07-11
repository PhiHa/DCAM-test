# DCAM Documentation Governance

**Page ID**: 47120620  
**Version**: 13  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47120620

---


# DCAM Documentation Governance

Item

Information

Project

DCAM

Document Type

Documentation Governance

Version

Approved 1.11

Status

Approved

Owner

Hoàng Ngọc Quyền

Last Updated

2026-07-10

## 1. Authoritative Document Rule

Shared rules must be defined once in the authoritative document and referenced elsewhere.

Define once.
Reference elsewhere.
Avoid copying full rule tables into many documents.
A dependent document may summarize an authoritative rule only when the summary is required for its audience or local execution context.

## 2. Rule Ownership Matrix

Shared Topic

Authoritative Document

Documentation hierarchy, ownership and change process

DCAM Documentation Governance

Project navigation and current decision summary

DCAM Project Home

Architecture navigation and architecture-level alignment

DCAM Architecture Home

Feature eligibility states

DCAM Device Capability & Feature Eligibility Design

Runtime transition guards

DCAM State Machine Design

Android dedicated-device decision

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Device Owner / approved DPC policy

DCAM Android Device Owner & Kiosk Policy Design

Lock Task Mode

DCAM Android Device Owner & Kiosk Policy Design

User Restrictions

DCAM Android Device Owner & Kiosk Policy Design

Home/Launcher policy

DCAM Android Device Owner & Kiosk Policy Design

Maintenance Mode policy

DCAM Android Device Owner & Kiosk Policy Design

In-app console navigation and Setting hub

DCAM In-App Operation, Device Settings & Media Console Design

Maintenance Password Gate UX / controlled target flow

DCAM In-App Operation, Device Settings & Media Console Design + DCAM Security & Encryption Design

Full Android unrestricted mode policy

DCAM Android Device Owner & Kiosk Policy Design + DCAM In-App Operation, Device Settings & Media Console Design

AutoUpdate preconditions

09 - System Settings Requirements

Self Update flow

DCAM Self Update Design

Policy-safe update constraints

DCAM Android Device Owner & Kiosk Policy Design

No external EMM / No Android Management API / No Managed Google Play decision

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Device identity baseline

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id + 04 - Device Configuration Requirements

Factory production procedure and final acceptance

DCAM Factory Provisioning & Device Production SOP

DSetup tool behavior, serial recovery/injection and imported-serial verification

DCAM DSetup Factory Tool Design

SD Identity File production/recovery execution

DCAM Factory Provisioning & Device Production SOP + DCAM DSetup Factory Tool Design

Serial-based Web Portal/Firebase API/data contract

DCAM Web Portal & Device API Contract

`serial_lookup/{serial_number}` create/restore contract

DCAM Web Portal & Device API Contract

Web Portal device record and `devices/{dcam_cloud_device_id}` schema

DCAM Web Portal & Device API Contract

Web Portal provisioning business flow

DCAM Device Provisioning Web Portal Design

Web Portal user-facing account, Login/Workspace model, panels and app behavior

DCAM Device Provisioning Web Portal App Design

Web Portal frontend/backend modules and Firebase implementation

DCAM Device Provisioning Web Portal Implementation Design

Web Portal request/response/path/schema/reason code

DCAM Web Portal & Device API Contract

Web Portal authentication/authorization/security constraints

DCAM Security & Encryption Design + DCAM Web Portal & Device API Contract

Remote config API/schema contract

DCAM Web Portal & Device API Contract + 09 - System Settings Requirements

Self Update check/result API contract

DCAM Web Portal & Device API Contract + DCAM Self Update Design

Factory production record API contract

DCAM Web Portal & Device API Contract + DCAM Factory Provisioning & Device Production SOP

Factory production states, ready-to-ship checklist and quarantine rules

DCAM Factory Provisioning & Device Production SOP

Factory SOP approval gates and real-device evidence

DCAM Device POC & Hardware Validation Report + DCAM QA Test Strategy & Test Matrix

Production record security constraints

DCAM Factory Provisioning & Device Production SOP + DCAM Security & Encryption Design

Media naming, folders, MD5 and BDMA cleanup

DCAM-BDMA Data Contract

BDMA-facing logs artifact location/format/access contract

DCAM-BDMA Data Contract

Internal operational log files, rotation, queue and provider routing

DCAM Logging & Diagnostics Design

Recording and emergency media flow

DCAM Recording & Capture Design

Logging provider ownership and two-channel architecture

07 - Logging, Diagnostics, Performance & Security

Required logging behavior/events and sensitive logging requirements

07 - Logging & Diagnostics Requirements

Operational Logging implementation, event schema, local-first queue, Loggly routing and Crashlytics boundary

DCAM Logging & Diagnostics Design

Logging security constraints and forbidden sensitive fields

DCAM Security & Encryption Design + 07 - Logging & Diagnostics Requirements

Logging and diagnostics release validation

DCAM QA Test Strategy & Test Matrix

Measurable performance targets and release thresholds

DCAM Performance Budget & Resource Constraints

Security and encryption implementation

DCAM Security & Encryption Design

Kiosk exit and Maintenance Mode security constraints

DCAM Security & Encryption Design

Remote config and DB apply

09 - System Settings Requirements

Kiosk requested-policy settings

09 - System Settings Requirements

Actual kiosk policy apply behavior

DCAM Android Device Owner & Kiosk Policy Design

## 3. Reference Format

See <Authoritative Document> for the full rule.
This document only applies that rule to <local context>.
When multiple documents share a boundary, state which part each document owns instead of using a generic combined reference.

## 4. Change Rule

Update the authoritative document first. Dependent documents update only their reference, local application notes, implementation details or verification coverage.

General sequence:

Decision / Requirement
    ↓
Architecture / ADR
    ↓
Technical Design / Contract
    ↓
Implementation Design
    ↓
QA / Device POC
    ↓
Factory SOP / Release execution
    ↓
Project Home / Architecture Home navigation sync
## 5. Web Portal Governance Rule

The Web Portal document set has four distinct ownership layers:

DCAM Device Provisioning Web Portal Design
    → business flow

DCAM Device Provisioning Web Portal App Design
    → Factory Worker account, Login/Workspace model,
      panels/states and user-facing behavior

DCAM Device Provisioning Web Portal Implementation Design
    → frontend/backend modules, Firebase Hosting/Auth/Functions/Firestore implementation

DCAM Web Portal & Device API Contract
    → request, response, path, schema, reason code and Firestore contract
Security ownership:

DCAM Security & Encryption Design
    → authentication/authorization constraints,
      QR/serial/security boundaries and sensitive-data rules
Approved dependency order:

Business Flow approved
    ↓
App Design approved
    ↓
API/Security contract aligned
    ↓
Implementation Design approved/reapproved
    ↓
QA coverage updated
Web Portal documents must not redefine:

Device Owner setup
DSetup serial injection
Factory PASS / FAIL / QUARANTINED / READY_TO_SHIP
Android runtime identity persistence
Current user-facing baseline:

Factory Worker only
Login and Workspace only
QR displayed by DCAM is the Web Portal serial source
serial_number is read-only
no manual serial entry
no direct serial barcode scan
## 6. Device Identity / Recovery Governance Rule

ADR - DCAM Device Identity Baseline
    → owns architecture decision

04 - Device Configuration Requirements
    → owns requirement-level identity rules

DCAM DSetup Factory Tool Design
    → owns factory tool resolution/injection/verification behavior

DCAM Factory Provisioning & Device Production SOP
    → owns physical production execution and acceptance

DCAM Web Portal & Device API Contract
    → owns serial_lookup/devices schema

DCAM Android Operation Design
    → owns Android runtime restore/apply behavior

DCAM SQLite Database Design
    → owns local persistence/cache

DCAM Security & Encryption Design
    → owns identifier/security/logging constraints
Current baseline:

serial_number = Hardware Identity / primary recovery key
dcam_cloud_device_id = Cloud Identity / primary cloud device id
SD Identity File = recovery cache
serial_lookup/{serial_number} = cloud create/restore lookup
Do not use ANDROID_ID, android_id_hash or device_lookup/{android_id_hash}
## 7. Logging / Diagnostics Governance Rule

07 - Logging, Diagnostics, Performance & Security
    → owns two-channel architecture and provider ownership

07 - Logging & Diagnostics Requirements
    → owns required behavior, events, local-first requirements,
      provider failure expectations and sensitive logging requirements

DCAM Logging & Diagnostics Design
    → owns logging facade, event schema, categories/levels,
      internal active/rotated files, local queue, upload,
      Backend Relay → Loggly and Crashlytics classification

DCAM-BDMA Data Contract
    → owns stable BDMA-facing Logs/logs.txt artifact,
      encoding/record boundary and read-only access

DCAM Security & Encryption Design
    → owns forbidden fields, provider credentials and security constraints

DCAM QA Test Strategy & Test Matrix
    → owns test coverage and release validation

DCAM Device POC & Hardware Validation Report
    → owns real-device/non-GMS/provider compatibility evidence
Mandatory separation:

Internal rotated logs ≠ BDMA contract artifact
Local upload queue ≠ logs.txt
Loggly delivery state ≠ BDMA artifact
Crashlytics report/cache ≠ logs.txt
Logging change triggers:

Trigger

Required Governance Action

Provider ownership changes

Update architecture page first, then Logging Design, Requirements, QA and navigation pages.

Required event coverage changes

Update Logging Requirements first, then Logging Design and QA.

Internal event schema/category/level changes

Update Logging Design; update QA if acceptance changes.

Local rotation/queue changes without BDMA impact

Update Logging Design only, plus QA as needed.

`logs.txt` location/encoding/record format/access changes

Update Data Contract, Logging Design, BDMA implementation and QA together.

Backend Relay API/schema/auth changes

Update Logging Design, API Contract, Security Design, backend implementation and QA.

Sensitive-field policy changes

Update Security Design and Logging Requirements first, then Design/QA.

Crashlytics classification changes

Update Logging Design and QA; architecture only if provider ownership changes.

## 8. API Contract Governance Rule

API Contract must not redefine product UI flow, Android runtime behavior or security policy.

API Contract defines request/response/path/schema/error-code boundaries.
Web Portal Business Flow defines what Factory Worker does.
Web Portal App Design defines user-facing screens/panels/behavior.
Implementation Design defines modules and selected implementation stack.
Android Operation Design defines Android runtime consumption.
Security Design defines what is allowed or forbidden.
API Contract must be updated when:

Trigger

Required Governance Action

QR payload fields change

Update API Contract, Business Flow, App/Implementation Design and Security if affected.

`serial_lookup/{serial_number}` behavior changes

Update API Contract, Android Operation, Factory SOP and Security if affected.

Device record schema changes

Update API Contract, SQLite Design and Factory SOP if production record is affected.

Remote config schema changes

Update System Settings and API Contract.

Update manifest/check/result changes

Update Self Update Design and API Contract.

Factory production record fields change

Update Factory SOP, API Contract and Security review.

Operational Log Backend Relay API/schema/auth changes

Update Logging Design, API Contract, Security Design and QA together.

Authentication/security model changes

Update Security Design first, then API Contract and Implementation Design.

## 9. Factory SOP Governance Rule

Factory SOP applies approved technical baselines to physical device production. It must not redefine implementation rules.

If SOP needs a new policy, security, provisioning, update, logging or BDMA rule,
update the authoritative design/requirement first,
then update SOP with execution steps.
Factory SOP must be updated when:

Trigger

Required Governance Action

Device Owner setup method changes

Update Kiosk Policy Design / DSetup Design / Device POC first.

Required Lock Task/User Restrictions profile changes

Update Kiosk Policy Design and QA first.

Web Portal business flow changes

Update Business Flow, App Design, API/Security and Implementation Design first.

Device identity baseline changes

Update Identity ADR first, then dependent documents.

SD Identity File recovery behavior changes

Update DSetup Design and Factory SOP, then runtime/security/QA as needed.

APK validation process changes

Update Self Update/Security/Release/API docs first.

Factory acceptance checklist changes

Update QA and Factory SOP together.

Production record fields change

Update Factory SOP, API Contract and Security review.

Play Store fallback decision changes

Update Console, Self Update, Security and POC/QA first.

DSetup completion alone does not mean production acceptance.

## 10. Self Update / Release Artifact Governance Rule

DCAM Self Update Design owns artifact/download/validation/install flow.
DCAM Web Portal & Device API Contract owns update check/result API schema.
09 - System Settings Requirements owns AutoUpdate preconditions.
DCAM Security & Encryption Design owns package/signature/checksum/security constraints.
DCAM QA Test Strategy & Test Matrix owns validation coverage.
DCAM Factory Provisioning & Device Production SOP owns factory verification steps.
## 11. No-EMM Baseline Reference Rule

The decision is defined once in:

```
ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision
```

Allowed reference:

```
Current device baseline: No external EMM / No Android Management API / No Managed Google Play. (See ADR.)
```

Do not repeat the complete decision block multiple times in one document.

## 12. Approval Dependency Rule

A downstream document must not be approved against an upstream Draft unless the exception is explicitly documented.

Examples:

Implementation Design depends on approved Business Flow and App Design.
Factory SOP depends on approved/accepted technical behavior for the step it executes.
QA release criteria depend on approved requirements/contracts or an explicit test-baseline exception.
When an approved dependency changes materially, downstream approved documents must be reviewed and either:

reapproved with a new version
marked Draft until alignment is completed
or documented as unaffected with review evidence
## 13. Navigation Synchronization Rule

After authoritative versions/status/ownership change, update:

DCAM Project Home
DCAM Architecture Home if architecture/technical ownership is affected
Navigation pages summarize; they do not become source of truth for the detailed rule.

## 14. Practical Conclusion

Documentation Governance prevents rule drift.
Requirements own what DCAM must do.
Architecture/ADR own major decisions and boundaries.
Technical Design owns how DCAM works.
App Design owns user-facing application behavior.
Implementation Design owns selected modules and implementation stack.
API Contract owns request/data boundaries.
Data Contract owns BDMA-visible artifacts and interoperability.
QA owns how behavior is verified.
Device POC owns real-device evidence.
Factory SOP owns how a physical BodyCamera becomes ready to ship.
Project Home and Architecture Home own navigation only.
Current logging ownership:

Loggly = Operational Logging provider.
Firebase Crashlytics = Crash & Stability Monitoring provider.
Logging Design owns internal files/queue/routing.
Data Contract owns Logs/logs.txt for BDMA.
QA owns end-to-end verification.
Current Web Portal ownership:

Business Flow → Web Portal Design
Login/Workspace app behavior → App Design
Firebase/frontend/backend modules → Implementation Design
API/schema → API Contract
Security constraints → Security Design