# DCAM Web Portal & Device API Contract

**Page ID**: 49873154  
**Version**: 6  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/49873154

---


# DCAM Web Portal & Device API Contract

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design / API Contract

Version

Draft 0.5

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / Backend Lead / Web Portal Lead / Security Reviewer / QA Lead / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Android Developers, Web Portal Developers, Backend/WebServer Developers, QA, Factory Admin, Support, Security Reviewer

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Device Provisioning Web Portal Design, DCAM Factory Provisioning & Device Production SOP, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Operation Design, DCAM Self Update Design, 06 - Cloud Services, Update & Configuration Architecture, 09 - System Settings Requirements, DCAM Security & Encryption Design, DCAM SQLite Database Design, DCAM Device Capability & Feature Eligibility Design, DCAM QA Test Strategy & Test Matrix

## 1. Purpose

This document defines the API/data contract between:

textwide760The contract covers:

textwide760This document follows **DCAM Factory Provisioning & Device Production SOP** as the device identity baseline:

textwide760This document does not redefine Web Portal UI flow, Android runtime, Device Owner setup, Lock Task Mode, User Restrictions, BDMA ADB import, Security rules or Self Update install behavior. It defines the API/schema boundary used by those designs.

## 2. Current Baseline

textwide760Important boundary:

textwide760## 3. Actors and API Clients

Actor / Client

Calls API?

Purpose

DCAM Android App

Yes

Use injected/imported `serial_number`, fetch device record/config, check update, report status/result.

DCAM Web Portal

Yes

Factory/Admin creates/restores device record by `serial_number`, assigns device information, updates server state.

Backend/WebServer/Firebase Cloud Firestore

Server

Owns Firestore device records, serial lookup mapping, config, update manifest, factory records and audit.

Factory Admin

Through Web Portal

Assign serial/owner/manufacture date and approve production/factory actions.

DSetup

Optional / Factory

Resolves serial from SD Identity File or barcode, injects serial to DCAM, may submit/attach factory metadata if approved.

Support/Admin

Through Web Portal or support tool

View status, audit, device record, update/config results.

BDMA

No for MVP Web API

BDMA import/user sync remains ADB-based according to DCAM-BDMA Data Contract.

## 4. API Design Principles

Principle

Description

Versioned API

All APIs and payload schemas must be versioned.

Firestore Baseline

Firebase Cloud Firestore is the selected Firebase storage baseline for backend data. Realtime Database is not the current baseline.

Serial-based Provisioning

Device cloud identity is created/restored by `serial_number`.

One Lookup Path for Cloud Identity

Backend uses `serial_lookup/{serial_number}` to resolve `dcam_cloud_device_id`.

Stable Device Identity

`dcam_cloud_device_id` is the server/cloud primary device id after provisioning.

Hardware Identity

`serial_number` is the Hardware Identity and primary recovery key.

Recovery Cache Boundary

SD Identity File may cache `serial_number` on external SD card, but it is not a primary key and not Hardware Identity.

Mutable Device Information

`owner_name`, `manufacture_date`, `device_model`, `firmware_version` are device information, not primary keys.

No Android System ID Dependency

API design must not depend on raw `ANDROID_ID`, `android_id_hash`, IMEI, MAC address or other app-inaccessible hardware identifiers.

No Personal Account Dependency

API design must not depend on personal Google account or Managed Google Play.

Requested vs Applied Config

Server config values are requested values; Android validates capability/policy/runtime guard before apply.

Safe Errors

API errors must use stable `reason_code` and must not expose secrets.

Offline-first

Android must continue with last valid local identity/config where applicable after device is provisioned.

Auditable

Provisioning, device info change, config apply, update result and factory status must be auditable.

## 5. Environment and Versioning

### 5.1 Base URL Direction

Exact URLs are deployment-specific.

Environment

Base URL

Status

Development

`https://dev-api.dcam.example.com`

Placeholder

QA

`https://qa-api.dcam.example.com`

Placeholder

Production

`https://api.dcam.example.com`

Placeholder

Firebase direct storage

Firebase Cloud Firestore collection/document paths

Selected baseline

Firebase Realtime Database

Not used for current storage baseline

Not Applicable

### 5.2 API Version

textwide760Breaking changes require a new API/schema version or an explicitly compatible migration path.

## 6. Common Request Headers

Header

Required

Client

Description

`Authorization`

Conditional

Android/Web Portal

Bearer token/session/device credential if REST backend is used. Exact auth TBD.

`X-DCAM-Request-Id`

Yes

All

UUID for tracing/idempotency/debugging.

`X-DCAM-App-Version-Code`

Android

Android

Android app versionCode.

`X-DCAM-App-Version-Name`

Android

Android

Android app versionName.

`X-DCAM-Device-Id`

After provisioning

Android

`dcam_cloud_device_id`.

`X-DCAM-Contract-Version`

Yes

All

API/data contract version.

`X-DCAM-Client-Time`

Recommended

All

Client timestamp.

`X-DCAM-Factory-Batch-Id`

Factory only

Web Portal/Factory

Factory production batch id if applicable.

For Firestore direct implementation, these fields map to request/auth context, document metadata, transaction metadata or audit fields instead of HTTP headers.

## 7. Common Response Format

### 7.1 Success Response

jsonwide760### 7.2 Error Response

jsonwide760For Firestore direct implementation, a missing document at `serial_lookup/{serial_number}` means the serial has not been provisioned yet, unless the caller is explicitly running create/restore transaction.

## 8. Core Data Models

### 8.1 Device Identity

jsonwide760### 8.2 Device Information

jsonwide760Rules:

textwide760### 8.3 SD Identity File Metadata

The SD Identity File is not a cloud identity, but the backend/factory record may store safe result metadata.

jsonwide760Rules:

textwide760### 8.4 App and Contract Metadata

jsonwide760### 8.5 Device State Values

State

Meaning

`NOT_PROVISIONED`

No server mapping exists for `serial_number`.

`ACTIVE`

Device business identity exists and device may continue startup if runtime/policy guards allow.

`DISABLED`

Device is disabled by admin/server.

`REVOKED`

Device identity is revoked and must not operate normally.

`QUARANTINED`

Device failed factory/release checks and must not be shipped/used for field operation.

`ERROR`

Server-side identity/device state error.

## 9. Serial-based Business Provisioning

### 9.1 Provisioning Flow

textwide760### 9.2 QR Payload if UI Flow Uses QR

QR is optional business provisioning UI only. It is not Device Owner setup and it must not use Android system identifiers.

Minimum payload:

jsonwide760Optional payload fields:

jsonwide760Rules:

textwide760## 10. Serial Lookup API

Android/backend uses this to resolve or verify cloud device identity by serial number.

### 10.1 REST Endpoint Direction

httpwide760### 10.2 Firestore Collection Direction

textwide760Example Firestore document:

jsonwide760### 10.3 Response: Not Provisioned

jsonwide760### 10.4 Response: Provisioned

jsonwide760Rules:

textwide760## 11. Web Portal Device Creation / Restore Contract

Web Portal creates or restores device record by serial number.

### 11.1 Create or Restore Device Record

httpwide760Request:

jsonwide760Response:

jsonwide760Backend/Firestore side effects:

textwide760### 11.2 Device Record Shape

jsonwide760Rules:

textwide760## 12. Get Device Record API

After Android receives `dcam_cloud_device_id`, it may fetch full device record.

httpwide760Firestore equivalent:

textwide760Response:

jsonwide760## 13. Device Heartbeat / Status API

### 13.1 Endpoint

httpwide760### 13.2 Request

jsonwide760### 13.3 Response

jsonwide760Rules:

textwide760## 14. Remote Config API

### 14.1 Get Effective Config

httpwide760Response:

jsonwide760Rules:

textwide760### 14.2 Report Config Apply Result

httpwide760Request:

jsonwide760Apply states:

textwide760## 15. Self Update API

### 15.1 Check Update

httpwide760Request:

jsonwide760Response when no update:

jsonwide760Response when update is available:

jsonwide760Rules:

textwide760### 15.2 Report Update Result

httpwide760Request:

jsonwide760Update result states:

textwide760## 16. Factory Production Record API

Factory/Web Portal may submit production record after device passes SOP.

httpwide760Request:

jsonwide760Forbidden fields:

textwide760Factory test result values:

textwide760## 17. Audit Event Model

Audit may be implemented as Firestore audit collection or backend table.

jsonwide760Recommended audit events:

textwide760## 18. Standard Error Codes

Code

Meaning

Retryable

`INVALID_REQUEST`

Request schema invalid.

No

`UNAUTHORIZED`

Missing/invalid auth.

No

`FORBIDDEN`

Caller not allowed.

No

`SERIAL_LOOKUP_NOT_FOUND`

No mapping for `serial_number`.

No for lookup; create may be allowed in authorized factory flow.

`DEVICE_DISABLED`

Device disabled.

No

`DEVICE_REVOKED`

Device revoked.

No

`DEVICE_QUARANTINED`

Device quarantined and must not enter field operation.

No

`DUPLICATE_SERIAL_NUMBER`

Serial conflict.

No / Admin review

`SD_IDENTITY_FILE_INVALID`

SD Identity File is malformed or failed validation.

No / fall back to barcode scan

`SERIAL_SOURCE_REQUIRED`

No valid SD identity or barcode serial was provided.

No

`CONFIG_NOT_FOUND`

No config for device/profile.

Yes

`UNSUPPORTED_CONTRACT_VERSION`

Client contract not supported.

No

`UPDATE_NOT_AVAILABLE`

No update.

No

`INVALID_APP_VERSION`

App version not accepted.

No

`ARTIFACT_NOT_FOUND`

APK artifact missing.

Yes

`MANAGED_GOOGLE_PLAY_NOT_APPLICABLE`

Managed Google Play not supported in current baseline.

No

`RATE_LIMITED`

Too many requests.

Yes

`SERVER_ERROR`

Unexpected backend error.

Yes

## 19. Security Requirements

Rule

Description

API-SEC-001

HTTPS is required for REST implementation.

API-SEC-002

Admin APIs require Web Portal admin authentication.

API-SEC-003

Device APIs after provisioning require device-bound auth token, server-approved credential or Firestore security rules. Exact method TBD.

API-SEC-004

QR payload must not contain long-lived secret.

API-SEC-005

Raw Android ID must not be sent, stored or logged.

API-SEC-006

`android_id_hash` must not be used as production identity or recovery lookup in the current SOP baseline.

API-SEC-007

`serial_number` is the approved Hardware Identity / recovery key.

API-SEC-008

SD Identity File is a recovery cache only; validate format/signature/checksum according to approved policy.

API-SEC-009

Maintenance password must never be sent through these APIs unless a future approved secure management contract exists.

API-SEC-010

Google account password/token must never be sent.

API-SEC-011

APK signing private key must never be sent or stored in API payloads.

API-SEC-012

All provisioning/device-info/factory/update/config actions must be auditable with safe reason codes.

## 20. Retry and Offline Behavior

API Group

Offline / Retry Behavior

Serial lookup before provisioning

Android stays in `PROVISIONING_REQUIRED` or DSetup/factory flow waits for network according to SOP.

Device record fetch

Retry later; if local identity/config exists, continue according to Android Operation rules.

Config fetch

Use last valid applied config.

Config apply result

Queue/report later if supported.

Heartbeat

Queue/drop according to policy; must not block core recording.

Update check

Retry later; update is non-critical compared to recording/emergency.

Update result

Queue/report later if supported.

Factory production record

Must sync before `READY_TO_SHIP` if factory policy requires backend record.

## 21. API Versioning and Compatibility

Rules:

textwide760Example unsupported response:

jsonwide760## 22. Firestore Collection Direction

Firebase Cloud Firestore is the selected storage baseline. Realtime Database is not used for current backend storage.

Logical API

Firestore Collection / Document Direction

Serial lookup

`serial_lookup/{serial_number}`

Device record

`devices/{dcam_cloud_device_id}`

Effective config

`device_config/{dcam_cloud_device_id}/effective` or equivalent config document/subcollection

Config apply result

`device_config_apply_results/{dcam_cloud_device_id}_{config_revision}` or equivalent result document

Heartbeat/status

`device_status/{dcam_cloud_device_id}`

Update manifest/check

`device_updates/{dcam_cloud_device_id}` or channel/version manifest collection

Update result

`device_update_results/{dcam_cloud_device_id}_{update_id}` or equivalent result document

Factory production record

`factory_production_records/{dcam_cloud_device_id}`

Audit events

`audit_events/{event_id}` or scoped audit subcollection

Exact Firestore collection/document naming is implementation TBD, but the logical schema and ownership rules in this document remain the contract baseline.

Firestore modeling rules:

textwide760## 23. Open Questions / TBD

Item

Owner / Source

REST backend vs direct Firestore SDK/API usage for MVP.

Backend/Tech Lead

Exact device API auth method after provisioning.

Security + Backend + Android

Exact Web Portal admin role/permission model.

Product + Security + Backend

Exact Firestore collection/document names.

Backend/Cloud Lead

Exact Firestore security rules or backend service authorization boundary.

Security + Backend

Exact QR optional nonce/signature/expiration decision.

Security + Web Portal Design

Duplicate `serial_number` handling policy.

Product + Backend

Exact SD Identity File signature/checksum validation contract if backend/factory stores validation result.

Android + Security + Factory

Exact update manifest schema and artifact repository URL.

Self Update + Release Plan

Exact retry queue policy on Android.

Android Operation + SQLite Design

Exact audit retention policy.

Security + Backend

Exact factory production record storage/reporting process.

Factory SOP + Backend

## 24. Practical Conclusion

textwide760