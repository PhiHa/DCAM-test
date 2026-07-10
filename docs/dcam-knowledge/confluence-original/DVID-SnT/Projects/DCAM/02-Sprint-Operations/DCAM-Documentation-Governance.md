# DCAM Documentation Governance

**Page ID**: 47120620  
**Version**: 11  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47120620

---


# DCAM Documentation Governance

Item

Information

Project

DCAM

Document Type

Documentation Governance

Version

Approved 1.9

Status

Approved

Owner

Hoàng Ngọc Quyền

Last Updated

2026-07-09

## Authoritative Document Rule

Shared rules must be defined once in the authoritative document and referenced elsewhere.

text## Rule Ownership Matrix

Shared Topic

Authoritative Document

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

No external EMM / No Managed Google Play detailed policy behavior

DCAM Android Device Owner & Kiosk Policy Design

Optional Play Store fallback boundary

DCAM In-App Operation, Device Settings & Media Console Design + DCAM Self Update Design + DCAM Security & Encryption Design

Device identity baseline

ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id + DCAM Factory Provisioning & Device Production SOP + 04 - Device Configuration Requirements

DSetup factory provisioning, serial recovery/scan/injection and SD Identity File recovery cache

DCAM Factory Provisioning & Device Production SOP

Serial-based Web Portal/Firebase API/data contract

DCAM Web Portal & Device API Contract

`serial_lookup/{serial_number}` create/restore contract

DCAM Web Portal & Device API Contract

Web Portal device record creation and `devices/{dcam_cloud_device_id}` schema

DCAM Web Portal & Device API Contract

Remote config API/schema contract

DCAM Web Portal & Device API Contract + 09 - System Settings Requirements

Self Update check/result API contract

DCAM Web Portal & Device API Contract + DCAM Self Update Design

Factory production record API contract

DCAM Web Portal & Device API Contract + DCAM Factory Provisioning & Device Production SOP

Web Portal provisioning UI/business flow

DCAM Device Provisioning Web Portal Design

Factory production states, ready-to-ship checklist and quarantine rules

DCAM Factory Provisioning & Device Production SOP

Factory SOP approval gates and real-device evidence

DCAM Device POC & Hardware Validation Report + DCAM QA Test Strategy & Test Matrix

Production record security constraints

DCAM Factory Provisioning & Device Production SOP + DCAM Security & Encryption Design

Media naming, folders, MD5, BDMA cleanup

DCAM-BDMA Data Contract

Recording and emergency media flow

DCAM Recording & Capture Design

Sensitive logging

07 - Logging & Diagnostics Requirements

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

## Reference Format

text for the full rule.
This document only applies that rule to .]]>## Change Rule

Update the authoritative document first. Dependent documents should update only their short reference or local application notes.

For Android dedicated-device/kiosk changes:

textFor Web Portal / Device API contract changes:

textFor device identity / recovery changes:

textFor Self Update / release artifact changes:

textFor factory production / device shipment changes:

text## API Contract Governance Rule

API Contract must not redefine product UI flow, Android runtime behavior or security policy.

textAPI Contract must be updated when any of the following changes:

Trigger

Required Governance Action

QR payload fields change

Update API Contract and Web Portal Provisioning Design.

`serial_lookup/{serial_number}` behavior changes

Update API Contract, Android Operation, Factory SOP and Security if affected.

SD Identity File production/recovery behavior changes

Update Factory SOP first, then Android Operation, SQLite and Security if affected.

Device record schema changes

Update API Contract, SQLite Design and Factory SOP if production record is affected.

Remote config schema changes

Update System Settings and API Contract.

Update manifest/check/result schema changes

Update Self Update Design and API Contract.

Factory production record fields change

Update Factory SOP, API Contract and Security review if sensitive fields are affected.

Auth/security model changes

Update Security Design first, then API Contract.

## Factory SOP Governance Rule

Factory SOP must not redefine technical design rules.

textFactory SOP must be updated when any of the following changes:

Trigger

Required Governance Action

Device Owner setup method changes

Update Kiosk Policy Design / Device POC first, then Factory SOP.

Required Lock Task/User Restrictions profile changes

Update Kiosk Policy Design and QA, then Factory SOP.

DCAM provisioning business flow changes

Update Web Portal Provisioning Design and API Contract if API/schema changes, then Factory SOP.

Device identity baseline changes

Update ADR - DCAM Device Identity Baseline first, then Factory SOP, Device Configuration Requirements, API Contract, Android Operation, SQLite and Security as needed.

SD Identity File recovery cache behavior changes

Update Factory SOP first, then Android Operation, SQLite, Security and QA as needed.

APK signing/checksum/versioning process changes

Update Self Update/Security/Release/API docs, then Factory SOP.

Factory acceptance checklist changes

Update QA Test Strategy and Factory SOP together.

Production record fields change

Update Factory SOP, API Contract and Security review if sensitive fields are affected.

Play Store fallback decision changes

Update In-App Console, Self Update, Security, POC/QA, then Factory SOP.

## No-EMM Baseline Reference Rule

The "No external EMM / No Android Management API / No Managed Google Play" decision is defined once in:

textAll DCAM documents must reference this ADR instead of repeating the full decision block.

Allowed reference format in other documents:

textException: Documents that serve as SOP for factory operators or serve as primary source for a specific audience, such as Factory SOP and the Kiosk Policy Purpose section, may include one explicit baseline block with a `(per ADR)` suffix.

Not allowed:

text## Current Identity Governance Baseline

Current identity baseline is defined by:

textCurrent baseline summary:

text## Practical Conclusion

text