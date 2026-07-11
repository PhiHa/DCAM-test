# DCAM Web Portal & Device API Contract

**Page ID**: 49873154  
**Version**: 7  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49873154

---


# DCAM Web Portal & Device API Contract

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design / API Contract

Version

Draft 0.6

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

Android Developers, Web Portal Developers, Backend/WebServer Developers, QA, Factory Worker, Factory Lead, Support, Security Reviewer

Last Updated

2026-07-10

Related Jira

None

Related Documents

DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Device Provisioning Web Portal Implementation Design, DCAM Factory Provisioning & Device Production SOP, DCAM DSetup Factory Tool Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM Android Operation Design, DCAM Self Update Design, 06 - Cloud Services, Update & Configuration Architecture, 09 - System Settings Requirements, DCAM Security & Encryption Design, DCAM SQLite Database Design, DCAM Device Capability & Feature Eligibility Design, DCAM QA Test Strategy & Test Matrix, DCAM Documentation Governance

## 1. Purpose

Tài liệu này định nghĩa API/data contract giữa:

DCAM Android App
DCAM Device Provisioning Web Portal
Firebase Authentication
Firebase Cloud Functions / Backend
Firebase Cloud Firestore
Factory / QA production-record workflow
Support tools nếu applicable
Contract cover:

Factory Worker authentication context
QR-based business provisioning
serial_lookup based cloud identity create/restore
server device record
remote config fetch/apply-result
Self Update check/result
heartbeat/status reporting
factory production record
common errors, security and versioning rules
Identity baseline:

serial_number = Hardware Identity / primary recovery key
dcam_cloud_device_id = Cloud Identity / primary cloud device id
SD Identity File = recovery cache on external SD card
No ANDROID_ID
No android_id_hash
No device_lookup/{android_id_hash}
Web Portal baseline:

User-facing account type = Factory Worker only.
Screens = Login and Workspace only.
serial_number source in Web Portal = provisioning QR displayed by DCAM only.
serial_number is read-only.
No manual serial input.
No direct serial barcode scan in Web Portal.
Tài liệu này không redefine UI flow, Android runtime, Device Owner setup, DSetup implementation, kiosk policy, BDMA ADB import, Security rules hoặc Self Update install behavior.

## 2. Current Baseline

Backend storage = Firebase Cloud Firestore.
Firebase Realtime Database is not used.
Web Portal deployment = Firebase Hosting.
Web Portal authentication = Firebase Authentication.
Provisioning backend = Firebase Cloud Functions.
Frontend does not directly write production provisioning collections.
Factory Worker scans provisioning QR displayed by DCAM.
Web Portal obtains serial_number only from QR.
Backend create/restore path = serial_lookup/{serial_number}.
DSetup supplies serial_number to DCAM and stops after imported serial verification.
DSetup does not call Web Portal provisioning API in the current baseline.
DSetup does not submit the official production record or decide READY_TO_SHIP / QUARANTINED.
No external EMM / Android Management API / Managed Google Play policy-driven update.
Primary update path = DCAM Self Update / APK update.
BDMA media import/user sync remains ADB-based for MVP.
Factory Wi-Fi password is hardcoded in approved DCAM APK by project decision and must never be transmitted by this API contract.
Important boundary:

Web Portal provisioning creates/restores DCAM business identity.
It does not make DCAM Device Owner.
It does not perform factory acceptance.
Factory Worker cannot override duplicate/rebind/restricted device states.
Factory/QA workflow owns production acceptance and official production record submission.
## 3. Actors and API Clients

Actor / Client

Calls API?

Purpose

DCAM Android App

Yes

Resolve device identity, fetch device/config, check update và report status/result.

DCAM Web Portal Frontend

Yes

Submit authenticated QR-derived provisioning request và display result.

Factory Worker

Through Web Portal

Login, scan QR, enter owner/date, submit provisioning; không override conflict hoặc submit production acceptance.

Firebase Authentication

Authentication service

Xác thực Factory Worker và cấp identity token.

Firebase Cloud Functions / Backend

Server

Verify token/worker profile, validate request, create/restore identity, enforce policy và audit.

Firebase Cloud Firestore

Server storage

Lưu worker profile, device, serial lookup, config, status, update, audit và factory records.

DSetup

No for current Web API baseline

Local factory tool; inject/verify serial trong DCAM. Không gọi provisioning API và không submit official production result.

Factory / QA Workflow

Conditional

Submit official production record sau khi factory acceptance hoàn tất.

Support/Admin Tool

Conditional / separate authorization

Xử lý conflict/rebind/restricted state ngoài Factory Worker App.

BDMA

No for MVP Web API

Media import/user sync remains ADB-based.

## 4. API Design Principles

Principle

Description

Versioned API

Tất cả API/payload schemas phải versioned.

Cloud Functions Authority

Web frontend gọi authenticated Cloud Functions/backend; không direct-write production provisioning collections.

Factory Worker Authorization

Backend verify Firebase token, worker profile, active status và allowed action.

QR-only Web Serial Source

Web provisioning `serial_number` chỉ đến từ QR displayed by DCAM.

Read-only Serial

Frontend không cho edit/replace serial; backend reject unexpected replacement behavior.

Serial-based Provisioning

Cloud identity create/restore bằng `serial_number`.

One Lookup Path

`serial_lookup/{serial_number}` resolve `dcam_cloud_device_id`.

Stable Device Identity

`dcam_cloud_device_id` là cloud primary device id.

Recovery Cache Boundary

SD Identity File chỉ cache serial; không phải cloud identity.

Mutable Device Information

`owner_name`, `manufacture_date`, model/firmware là information, không phải primary key.

No Android System ID Dependency

Không dùng `ANDROID_ID`, `android_id_hash`, IMEI hoặc MAC làm identity.

Requested vs Applied Config

Server trả requested config; Android validate/apply locally.

Safe Errors

Error dùng stable `reason_code`, không expose secret/internal stack.

Offline-first

Android dùng last valid local state khi applicable.

Auditable

Provisioning, info change, config/update/factory status phải auditable.

Factory Acceptance Separation

Web provisioning result không đồng nghĩa production PASS/READY_TO_SHIP.

## 5. Environment and Versioning

### 5.1 Deployment Direction

Component

Direction

Status

Web Hosting

Firebase Hosting

Approved

Web Authentication

Firebase Authentication

Approved

Backend

Firebase Cloud Functions

Approved

Storage

Firebase Cloud Firestore

Approved

Realtime Database

Not used

Not Applicable

Logical REST paths trong tài liệu có thể được map sang HTTPS Cloud Functions hoặc callable functions, nhưng request/response/auth/error semantics phải giữ nguyên.

### 5.2 API Version

```
/v1
```

Breaking change yêu cầu API/schema version mới hoặc compatible migration path.

## 6. Authentication and Common Request Context

### 6.1 Web Portal Authentication

Factory Worker logs in through Firebase Authentication.
Frontend obtains Firebase identity token.
Frontend sends token to Cloud Functions/backend.
Backend verifies token and loads worker profile.
Backend checks account active and Factory Worker authorization.
Backend derives worker identity from verified context, not request body.
### 6.2 Common Headers / Context

Header / Context

Required

Client

Description

`Authorization`

Yes

Web Portal

Firebase Bearer ID token hoặc equivalent verified auth context.

`Authorization`

Conditional

Android

Device-bound credential/token; exact method TBD.

`X-DCAM-Request-Id`

Yes

All

UUID cho tracing/idempotency/reconciliation.

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

Conditional

Factory/QA workflow

Factory batch id khi submit production record.

Web Portal request body không được chứa trusted role, worker id hoặc authorization decision do frontend tự khai báo.

## 7. Common Response Format

### 7.1 Success

{
  "success": true,
  "request_id": "bcece95a-5f5f-4b24-82a8-4c43bb5a1b8d",
  "server_time": "2026-07-10T08:00:00Z",
  "data": {}
}
### 7.2 Error

{
  "success": false,
  "request_id": "bcece95a-5f5f-4b24-82a8-4c43bb5a1b8d",
  "server_time": "2026-07-10T08:00:00Z",
  "error": {
    "code": "DUPLICATE_SERIAL_CONFLICT",
    "message": "This device requires support review.",
    "retryable": false,
    "support_required": true
  }
}
Message phải safe và phù hợp Factory Worker. Raw Firebase/internal stack trace không được trả về frontend.

## 8. Core Data Models

### 8.1 Device Identity

{
  "dcam_cloud_device_id": "dcam_dev_01HXZ...",
  "serial_number": "BC-2026-00001",
  "identity_state": "ACTIVE"
}
### 8.2 Device Information

{
  "serial_number": "BC-2026-00001",
  "owner_name": "Agency A",
  "manufacture_date": "2026-07-10",
  "device_model": "BC-MODEL-A",
  "firmware_version": "FW-2026.07"
}
Rules:

serial_number is Hardware Identity / recovery key.
dcam_cloud_device_id is cloud primary key.
owner_name is required business information for approved Web implementation.
manufacture_date is required and uses YYYY-MM-DD.
### 8.3 App and Contract Metadata

{
  "app_package_name": "com.example.dcam",
  "app_version_name": "1.0.0",
  "app_version_code": 100,
  "dcam_data_contract_version": "1.0",
  "media_contract_version": "1.0",
  "encoder_contract_version": "1.0"
}
### 8.4 Device State Values

State

Meaning

`NOT_PROVISIONED`

Chưa có mapping.

`ACTIVE`

Business identity active; runtime vẫn phải pass local policy guards.

`DISABLED`

Disabled by server policy.

`REVOKED`

Identity revoked.

`QUARANTINED`

Factory/release state blocks field operation.

`ERROR`

Server-side identity/device error.

## 9. Provisioning QR Contract

QR là **required Web Portal serial source** trong approved baseline.

Minimum payload:

{
  "payload_type": "DCAM_DEVICE_PROVISIONING",
  "payload_version": "1.0",
  "serial_number": "BC-2026-00001",
  "app_package_name": "com.example.dcam",
  "app_version_code": 100,
  "app_version_name": "1.0.0",
  "device_model": "BC-MODEL-A",
  "firmware_version": "FW-2026.07"
}
Optional fields:

{
  "provisioning_nonce": "local-random-uuid",
  "generated_at": "2026-07-10T08:00:00Z",
  "expires_at": "2026-07-10T08:05:00Z",
  "signature": "optional-approved-signature",
  "serial_source": "SD_IDENTITY_FILE|BARCODE_SCAN|MANUAL_APPROVED_FALLBACK|DSETUP_INJECTED",
  "dcam_data_contract_version": "1.0",
  "media_contract_version": "1.0",
  "encoder_contract_version": "1.0"
}
Rules:

QR is displayed by DCAM and scanned inside Workspace.
Web Portal does not accept serial from manual input or device-label barcode scan.
serial_number remains read-only after parsing.
QR must not contain ANDROID_ID, android_id_hash, maintenance password, factory Wi-Fi password, Firebase token, Google credential, cloud token or signing secret.
Camera stream/image must not be stored or uploaded.
Exact signature/expiration/replay policy remains TBD.
## 10. Serial Lookup API

### 10.1 Logical Endpoint

```
GET /v1/serial-lookup/{serial_number}
```

### 10.2 Firestore Direction

```
serial_lookup/{serial_number}
```

Example document:

{
  "dcam_cloud_device_id": "dcam_dev_01HXZ...",
  "device_state": "ACTIVE",
  "created_at": "2026-07-10T08:00:00Z",
  "created_by_worker": "worker_001"
}
Not found:

{
  "success": true,
  "data": {
    "found": false,
    "device_state": "NOT_PROVISIONED"
  }
}
Found:

{
  "success": true,
  "data": {
    "found": true,
    "dcam_cloud_device_id": "dcam_dev_01HXZ...",
    "device_state": "ACTIVE",
    "serial_number": "BC-2026-00001",
    "owner_name": "Agency A",
    "manufacture_date": "2026-07-10",
    "initial_config_revision": "cfg_001"
  }
}
## 11. Web Portal Create / Restore Contract

### 11.1 Logical Endpoint

```
POST /v1/factory/provisioning/devices
```

Cloud Functions implementation có thể map endpoint này sang HTTPS/callable function, nhưng semantics không đổi.

Request:

{
  "serial_number": "BC-2026-00001",
  "owner_name": "Agency A",
  "manufacture_date": "2026-07-10",
  "device_model": "BC-MODEL-A",
  "firmware_version": "FW-2026.07",
  "app_package_name": "com.example.dcam",
  "app_version_name": "1.0.0",
  "app_version_code": 100,
  "qr_payload_version": "1.0",
  "provisioning_nonce": "optional-local-random-uuid",
  "serial_source": "DSETUP_INJECTED",
  "source": "WEB_PORTAL_QR_FLOW"
}
Rules:

serial_number must come from parsed DCAM provisioning QR.
Frontend must not send worker role or trusted worker id in the body.
Backend derives worker identity from verified authentication context.
owner_name and manufacture_date are required for the approved implementation baseline.
manufacture_date uses YYYY-MM-DD and must not shift through timestamp conversion.
Response:

{
  "success": true,
  "data": {
    "dcam_cloud_device_id": "dcam_dev_01HXZ...",
    "serial_number": "BC-2026-00001",
    "owner_name": "Agency A",
    "manufacture_date": "2026-07-10",
    "device_state": "ACTIVE",
    "created_or_restored": "RESTORED",
    "support_required": false
  }
}
Backend side effects:

Create or update devices/{dcam_cloud_device_id}
Create or verify serial_lookup/{serial_number}
Append audit event with authenticated worker identity
### 11.2 Device Record Shape

{
  "dcam_cloud_device_id": "dcam_dev_01HXZ...",
  "serial_number": "BC-2026-00001",
  "owner_name": "Agency A",
  "manufacture_date": "2026-07-10",
  "device_model": "BC-MODEL-A",
  "firmware_version": "FW-2026.07",
  "device_state": "ACTIVE",
  "created_by_worker": "worker_001",
  "last_provisioned_by_worker": "worker_001",
  "created_at": "2026-07-10T08:00:00Z",
  "updated_at": "2026-07-10T08:00:00Z"
}
Rules:

Duplicate serial must not silently create unrelated active device.
If mapping exists and restore is allowed, restore existing dcam_cloud_device_id.
Factory Worker cannot approve rebind or conflict override.
Conflict returns support_required and is handled by separate support/admin process.
Web Portal provisioning success does not mark READY_TO_SHIP.
## 12. Get Device Record API

```
GET /v1/devices/{dcam_cloud_device_id}
```

Response:

{
  "success": true,
  "data": {
    "dcam_cloud_device_id": "dcam_dev_01HXZ...",
    "serial_number": "BC-2026-00001",
    "owner_name": "Agency A",
    "manufacture_date": "2026-07-10",
    "device_model": "BC-MODEL-A",
    "firmware_version": "FW-2026.07",
    "device_state": "ACTIVE",
    "config_revision": "cfg_001",
    "app_contract": {
      "dcam_data_contract_version": "1.0",
      "media_contract_version": "1.0",
      "encoder_contract_version": "1.0"
    }
  }
}
## 13. Device Heartbeat / Status API

```
POST /v1/devices/{dcam_cloud_device_id}/heartbeat
```

Request direction:

{
  "app_version_code": 100,
  "app_version_name": "1.0.0",
  "firmware_version": "FW-2026.07",
  "device_model": "BC-MODEL-A",
  "battery_percent": 86,
  "network_state": "ONLINE",
  "policy_state": {
    "device_policy_state": "DEVICE_POLICY_APPLIED",
    "lock_task_state": "LOCK_TASK_ACTIVE"
  },
  "storage_state": {
    "free_bytes": 123456789,
    "used_bytes": 987654321
  },
  "recording_state": "IDLE",
  "update_state": "NONE"
}
Response:

{
  "success": true,
  "data": {
    "server_device_state": "ACTIVE",
    "target_config_revision": "cfg_002",
    "update_available": false
  }
}
Heartbeat không force direct runtime mutation và không chứa raw Android identifiers hoặc factory Wi-Fi credential.

## 14. Remote Config API

### 14.1 Get Effective Config

```
GET /v1/devices/{dcam_cloud_device_id}/config/effective
```

Response direction:

{
  "success": true,
  "data": {
    "config_revision": "cfg_002",
    "config_schema_version": "1.0",
    "settings": {
      "recording.video_resolution": "1080p",
      "recording.pre_record_seconds": 30,
      "kiosk.enabled": true,
      "update.self_update_enabled": true,
      "update.manual_play_store_fallback_enabled": false,
      "factory.sd_identity_sync_enabled": true
    }
  }
}
Rules:

Config values are requested values only.
Android validates capability/policy/runtime guard.
Maintenance credential and factory Wi-Fi password must not be delivered through Remote Config.
### 14.2 Report Apply Result

```
POST /v1/devices/{dcam_cloud_device_id}/config/apply-result
```

{
  "config_revision": "cfg_002",
  "apply_state": "DEFERRED",
  "reason_code": "DEFERRED_RECORDING_ACTIVE",
  "applied_at": null
}
States:

PENDING
APPLIED
DEFERRED
REJECTED
FAILED
## 15. Self Update API

### 15.1 Check Update

```
POST /v1/devices/{dcam_cloud_device_id}/updates/check
```

Request:

{
  "current_version_code": 100,
  "current_version_name": "1.0.0",
  "device_model": "BC-MODEL-A",
  "firmware_version": "FW-2026.07",
  "android_version": "Android 12",
  "policy_state": "LOCK_TASK_ACTIVE",
  "channel": "production"
}
Available response:

{
  "success": true,
  "data": {
    "update_available": true,
    "update_id": "upd_2026_07_001",
    "target_version_code": 101,
    "target_version_name": "1.0.1",
    "apk_url": "https://artifact.example.com/dcam/1.0.1/dcam.apk",
    "checksum": {
      "algorithm": "SHA-256",
      "value": "..."
    },
    "package_name": "com.example.dcam",
    "requires_maintenance_window": false
  }
}
### 15.2 Report Update Result

```
POST /v1/devices/{dcam_cloud_device_id}/updates/result
```

{
  "update_id": "upd_2026_07_001",
  "from_version_code": 100,
  "to_version_code": 101,
  "state": "VERIFIED",
  "reason_code": null,
  "policy_restore_state": "LOCK_TASK_RESTORED"
}
Update states:

CHECKED
DEFERRED
DOWNLOADED
VALIDATION_FAILED
INSTALL_STARTED
INSTALL_FAILED
INSTALLED
VERIFIED
POLICY_RESTORE_FAILED
## 16. Factory Production Record API

Official production record is submitted by the approved `Factory / QA workflow`, not by DSetup and not by the normal Factory Worker provisioning App.

```
POST /v1/factory/devices/{dcam_cloud_device_id}/production-record
```

Request direction:

{
  "factory_batch_id": "BATCH-2026-07-001",
  "dsetup_run_id": "DSETUP-RUN-001",
  "dsetup_scenario": "CLEAN_PROVISIONING",
  "serial_source": "SD_IDENTITY_FILE|BARCODE_SCAN|MANUAL_APPROVED_FALLBACK",
  "device_serial_number": "BC-2026-00001",
  "bodycamera_model": "BC-MODEL-A",
  "firmware_version": "FW-2026.07",
  "apk_version_code": 100,
  "apk_version_name": "1.0.0",
  "device_owner_result": "PASS",
  "serial_injection_result": "PASS",
  "serial_import_result": "PASS",
  "factory_wifi_result": "PASS",
  "firebase_provisioning_result": "PASS",
  "lock_task_result": "PASS",
  "recording_test_result": "PASS",
  "bdma_readiness_result": "PASS",
  "self_update_capability_result": "PASS",
  "factory_test_result": "READY_TO_SHIP",
  "known_limitations": []
}
Rules:

DSetup operation results may be included as input evidence.
DSetup itself does not decide or submit final PASS/QUARANTINED/READY_TO_SHIP.
Backend derives submitter identity from Factory/QA authentication context.
Factory Wi-Fi password must not appear in the record.
Allowed final values:

READY_TO_SHIP
QUARANTINED
FAILED
BLOCKED
## 17. Audit Event Model

{
  "event_type": "DEVICE_PROVISIONED",
  "actor_type": "FACTORY_WORKER",
  "actor_id": "worker_001",
  "dcam_cloud_device_id": "dcam_dev_01HXZ...",
  "serial_number": "BC-2026-00001",
  "result": "SUCCESS",
  "reason_code": null,
  "request_id": "bcece95a-5f5f-4b24-82a8-4c43bb5a1b8d",
  "created_at": "2026-07-10T08:00:00Z"
}
Recommended events:

WORKER_LOGIN_SUCCESS
WORKER_LOGIN_FAILED
WORKSPACE_OPENED
QR_SCAN_SUCCESS
QR_SCAN_REJECTED
PROVISIONING_SUBMITTED
DEVICE_RECORD_CREATED
SERIAL_LOOKUP_MAPPING_CREATED
DEVICE_PROVISIONED
DEVICE_IDENTITY_RESTORED_BY_SERIAL_NUMBER
PROVISIONING_FAILED
SUPPORT_REQUIRED
CONFIG_FETCHED
CONFIG_APPLY_RESULT_REPORTED
UPDATE_CHECKED
UPDATE_RESULT_REPORTED
FACTORY_RECORD_CREATED
DEVICE_QUARANTINED
DEVICE_READY_TO_SHIP
DSetup local operation events and official factory acceptance events remain distinct.

## 18. Standard Error Codes

Code

Meaning

Retryable

`INVALID_REQUEST`

Request schema invalid.

No

`UNAUTHORIZED`

Missing/invalid authentication.

No

`FORBIDDEN`

Caller not allowed.

No

`WORKER_INACTIVE`

Factory Worker profile inactive.

No

`QR_INVALID`

QR type/version/content invalid.

No / Rescan

`QR_EXPIRED`

QR expired.

No / Refresh and rescan

`QR_REPLAY_REJECTED`

Replay detected if policy enabled.

No

`SERIAL_LOOKUP_NOT_FOUND`

No mapping exists.

Create may be allowed in authenticated provisioning flow.

`DUPLICATE_SERIAL_CONFLICT`

Serial mapping conflict.

No / Support required

`DEVICE_DISABLED`

Device disabled.

No

`DEVICE_REVOKED`

Device revoked.

No

`DEVICE_QUARANTINED`

Device restricted.

No

`INVALID_OWNER_NAME`

Owner validation failed.

No / Correct input

`INVALID_MANUFACTURE_DATE`

Date invalid.

No / Correct input

`UNSUPPORTED_CONTRACT_VERSION`

Client contract unsupported.

No

`UPDATE_NOT_AVAILABLE`

No update.

No

`ARTIFACT_NOT_FOUND`

APK artifact missing.

Yes

`MANAGED_GOOGLE_PLAY_NOT_APPLICABLE`

Not supported in current baseline.

No

`RATE_LIMITED`

Too many requests.

Yes

`UNKNOWN_OUTCOME`

Timeout/connection loss; reconciliation required.

Conditional

`SERVER_ERROR`

Unexpected backend error.

Yes

## 19. Security Requirements

Rule

Description

API-SEC-001

HTTPS/TLS required.

API-SEC-002

Web provisioning requires verified Firebase Authentication token and active Factory Worker profile.

API-SEC-003

Backend derives worker identity/role; frontend-provided authorization fields are untrusted.

API-SEC-004

Frontend cannot direct-write Serial Lookup, Devices or Audit Events.

API-SEC-005

QR payload must not contain long-lived secret.

API-SEC-006

`serial_number` must come from QR in Web provisioning and remains read-only.

API-SEC-007

Raw Android ID and `android_id_hash` must not be sent, stored or logged.

API-SEC-008

Maintenance password, Google credential, factory Wi-Fi password, signing private key and cloud secret must never be sent through these APIs.

API-SEC-009

Factory Worker cannot override duplicate/rebind/restricted state.

API-SEC-010

Camera stream/image is not uploaded or stored.

API-SEC-011

Device APIs require device-bound auth or approved Firestore rules; exact method TBD.

API-SEC-012

Actions must be auditable with safe reason codes.

API-SEC-013

Production and non-production environments must be separated.

## 20. Retry, Idempotency and Offline Behavior

API Group

Behavior

Web provisioning submit

Use request id/idempotency key; timeout must not be assumed failed. Reconcile unknown outcome.

Serial lookup before provisioning

Android remains `PROVISIONING_REQUIRED` until network/result available.

Device record fetch

Retry later; use valid local identity/config where allowed.

Config fetch

Use last valid applied config.

Config apply result

Queue/report later if supported.

Heartbeat

Queue/drop by policy; must not block recording.

Update check/result

Retry later; non-critical versus recording/emergency.

Factory production record

Must sync before READY_TO_SHIP if Factory SOP requires backend record.

## 21. API Versioning and Compatibility

Breaking changes require new version.
Android/Web report contract version.
Backend rejects unsupported contract with stable reason_code.
Legacy admin/manual-serial Web Portal clients are not compatible with approved Factory Worker QR-only baseline.
Legacy android_id_hash/device_lookup clients are not compatible unless migration ADR exists.
## 22. Firestore Collection Direction

Logical API

Firestore Direction

Worker profile

`factory_workers/{worker_uid}` or approved equivalent

Serial lookup

`serial_lookup/{serial_number}`

Device record

`devices/{dcam_cloud_device_id}`

Effective config

`device_config/{dcam_cloud_device_id}/effective` or equivalent

Config apply result

`device_config_apply_results/{id}` or equivalent

Heartbeat/status

`device_status/{dcam_cloud_device_id}`

Update manifest/check

`device_updates/{dcam_cloud_device_id}` or channel manifest

Update result

`device_update_results/{id}`

Factory production record

`factory_production_records/{dcam_cloud_device_id}`

Audit events

`audit_events/{event_id}` or scoped subcollection

Rules:

Use Firestore collection/document model.
Do not use Realtime Database tree model.
Do not use device_lookup/{android_id_hash}.
Web frontend cannot direct-write provisioning collections.
Cloud Functions/backend enforce authorization and transactions.
High-write status/audit data requires retention/write-rate review.
## 23. Open Questions / TBD

Item

Owner / Source

Exact HTTPS function name/path mapping

Backend/Tech Lead

Exact Android device API auth method

Security + Backend + Android

Factory Worker account model: individual or shared station

Factory + Security + Backend

Factory Worker account lifecycle/password reset

Factory + Security + Backend

Exact Firestore collection/document names

Backend/Cloud Lead

Exact Firestore security rules/backend authorization

Security + Backend

QR signature/nonce/expiration/replay policy

Security + Web Portal + Android

Duplicate/rebind support process

Product + Backend + Support

Exact owner source/validation

Product + Factory + Backend

Manufacture-date minimum/future-date policy

Product + Factory

Exact update manifest/artifact URL

Self Update + Release

Exact retry/idempotency/reconciliation policy

Backend + Web Portal

Exact audit retention

Security + Backend

Exact factory production record submitter authorization/process

Factory SOP + QA + Backend

## 24. Practical Conclusion

DCAM Web Portal & Device API Contract owns API/data boundary between Android, Web Portal, Firebase Authentication, Cloud Functions and Cloud Firestore.
Web Portal user-facing account type is Factory Worker only.
Factory Worker uses Login and Workspace.
Web Portal obtains serial_number only from provisioning QR displayed by DCAM.
serial_number is read-only; no manual or direct serial-barcode input exists in Web Portal.
Firebase Authentication identifies worker; backend verifies worker profile and authorization.
Frontend does not directly write production provisioning collections.
Backend creates/restores device through serial_lookup/{serial_number}.
dcam_cloud_device_id is cloud primary device id.
DSetup does not call Web provisioning API, submit official production record or decide READY_TO_SHIP.
Factory/QA workflow owns official production-record submission.
Factory Worker cannot override duplicate/rebind/restricted device state.
Factory Wi-Fi password is not part of QR, API, Firestore business data, logs or production record.
Do not use ANDROID_ID, android_id_hash or device_lookup/{android_id_hash}.
Remote config values are requested values; Android validates/applies locally.
Self Update / APK update is primary update path.
BDMA import/user sync remains ADB-based for MVP.