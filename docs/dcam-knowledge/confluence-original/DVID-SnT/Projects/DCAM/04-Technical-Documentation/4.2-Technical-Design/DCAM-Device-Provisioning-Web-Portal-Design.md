# DCAM Device Provisioning Web Portal Design

**Page ID**: 49315858  
**Version**: 10  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49315858

---


# DCAM Device Provisioning Web Portal Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design / Business Flow Design

Version

Approved 1.0

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Cloud Lead / Security Reviewer / Android Lead / Backend Lead / Web Portal Lead / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

PM/BA, Tech Lead, Android Developers, Web Developers, Cloud/WebServer Team, QA, Factory Worker, Factory Lead

Last Updated

2026-07-10

Related Jira

Không có

Related Documents

DCAM Device Provisioning Web Portal App Design, DCAM Device Provisioning Web Portal Implementation Design, DCAM Web Portal & Device API Contract, DCAM Factory Provisioning & Device Production SOP, DCAM DSetup Factory Tool Design, 04 - Device Configuration Requirements, 09 - System Settings Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Android Operation Design, DCAM Android Device Owner & Kiosk Policy Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM-BDMA Data Contract

## 1. Purpose

Tài liệu này là source of truth cho **DCAM Device Provisioning Web Portal business flow**.

Web Portal là ứng dụng Web chuyên dụng trong nhà máy. Ứng dụng chỉ có một loại tài khoản user-facing là `Factory Worker` và chỉ có hai screen:

Login
Workspace
Sau khi login thành công, toàn bộ QR scan, device review, nhập thông tin, submit, result và error handling được thực hiện trong `Workspace`. Các phần đó là panel/state trong cùng screen, không phải screen hoặc route riêng.

Identity baseline:

serial_number = Hardware Identity / primary recovery key
dcam_cloud_device_id = Cloud Identity / primary cloud device id
serial_lookup/{serial_number} = cloud create/restore lookup
SD Identity File = recovery cache trên external SD card
Không dùng ANDROID_ID
Không dùng android_id_hash
Không dùng device_lookup/{android_id_hash}
Tài liệu này owns:

QR-based business provisioning flow
Factory Worker behavior
Login và Workspace business behavior
Device review và device information capture
Create/restore result behavior
Provisioning states, audit direction và business error handling
Các tài liệu chi tiết:

DCAM Device Provisioning Web Portal App Design
    → owns app UI model, Workspace panels và app-side behavior

DCAM Device Provisioning Web Portal Implementation Design
    → owns frontend/backend module implementation, Firebase Hosting/Auth/Functions direction và application state

DCAM Web Portal & Device API Contract
    → owns request, response, path, schema, reason code và Firestore contract
## 2. Current Provisioning Baseline

DSetup resolves serial_number from SD Identity File or approved barcode/manual fallback.
DSetup installs approved DCAM APK.
DSetup sets or verifies Device Owner if required by the approved factory flow.
DSetup injects serial_number through the approved factory serial injection mechanism.
DSetup launches DCAM and verifies DCAM imported the expected serial_number.
DSetup stops after imported serial_number verification.
DCAM displays the provisioning QR.
Factory Worker logs in to Web Portal.
Factory Worker scans the QR displayed by DCAM inside Workspace.
Web Portal parses QR payload and displays serial_number as read-only.
Factory Worker enters/selects owner_name and manufacture_date.
Factory Worker submits provisioning from Workspace.
Backend creates or restores dcam_cloud_device_id through serial_lookup/{serial_number}.
Workspace displays the result.
Important boundary:

Web Portal does not set Android Device Owner.
Web Portal does not inject serial_number into Android.
Web Portal does not scan a serial barcode from the device label.
Web Portal does not allow manual serial_number entry.
Web Portal does not mark PASS, FAIL, QUARANTINED or READY_TO_SHIP.
## 3. Scope

### 3.1 In Scope

Area

Description

Factory Worker authentication

`Factory Worker` login trước khi truy cập `Workspace` và submit provisioning.

Two-screen application model

Business flow chỉ sử dụng `Login` và `Workspace`.

QR scan inside Workspace

Worker scan provisioning QR đang hiển thị trên DCAM device.

QR payload parsing

App parse payload type/version và lấy `serial_number` cùng device context.

Read-only device review

`serial_number`, device model, firmware/app version và QR metadata được hiển thị read-only nếu có.

Device information capture

Worker nhập/chọn `owner_name` và `manufacture_date` trong `Workspace`.

Inline review and submit

Worker review dữ liệu và submit provisioning trong cùng `Workspace`; không có Confirmation screen riêng.

New device creation

Backend tạo `dcam_cloud_device_id`, device record và serial lookup khi serial chưa được map.

Existing identity restore

Backend restore existing `dcam_cloud_device_id` khi serial đã có mapping hợp lệ.

Result and error display

`Workspace` hiển thị success, restored, safe error hoặc support-required state.

Audit direction

Provisioning action phải gắn với authenticated worker identity, request identifier, result và safe reason code.

### 3.2 Out of Scope

Area

Managed In

Manual `serial_number` entry in Web Portal

Không hỗ trợ. Serial chỉ lấy từ QR displayed by DCAM.

Direct serial barcode scan in Web Portal

Không hỗ trợ. Barcode/label fallback thuộc DSetup/factory flow.

Separate QR Scan, Device Review, Confirmation or Result screens

Không hỗ trợ. Đây là panel/state trong `Workspace`.

Multi-role user-facing portal

Không hỗ trợ. User-facing account type duy nhất là `Factory Worker`.

User management / role administration

Ngoài phạm vi ứng dụng provisioning này.

Duplicate/rebind override by worker

Không hỗ trợ; conflict phải chuyển support/factory process.

Android Device Owner setup

DCAM DSetup Factory Tool Design + Kiosk Policy Design + Factory SOP.

DSetup detailed operation

DCAM DSetup Factory Tool Design.

Factory acceptance / ready-to-ship

DCAM Factory Provisioning & Device Production SOP + QA Test Matrix.

API request/response/path/schema

DCAM Web Portal & Device API Contract.

Runtime local identity restore

DCAM Android Operation Design.

Security implementation

DCAM Security & Encryption Design.

## 4. Core Decisions

Decision

Status

User-facing account type duy nhất là `Factory Worker`.

Approved

Application chỉ có `Login` và `Workspace`.

Approved

Mọi thao tác sau login nằm trong `Workspace`.

Approved

Provisioning QR displayed by DCAM là serial source duy nhất của Web Portal.

Approved

`serial_number` luôn read-only trong Web Portal.

Approved

Web Portal không có manual serial entry hoặc direct serial barcode scan.

Approved

Firebase Cloud Firestore là selected storage baseline.

Approved

Firebase Realtime Database không dùng cho current storage baseline.

Not Applicable

`serial_number` là Hardware Identity / primary recovery key.

Approved

`dcam_cloud_device_id` là Cloud Identity / primary cloud device id.

Approved

`serial_lookup/{serial_number}` là create/restore lookup path.

Approved

`owner_name` và `manufacture_date` là business information, không phải identity key.

Approved

Factory Worker không được override duplicate, rebind, disabled, revoked hoặc quarantined state.

Approved

QR signature, nonce, expiration và replay policy thuộc API Contract/Security.

TBD

## 5. Actors

Actor

Responsibility

BodyCamera / DCAM App

Lưu imported `serial_number`, hiển thị provisioning QR, nhận/restore `dcam_cloud_device_id` và áp dụng local identity state sau provisioning.

DSetup

Thực hiện factory tool flow đến khi DCAM xác nhận đã import đúng `serial_number`; không sở hữu Web Portal provisioning hoặc production acceptance.

Factory Worker

Login, làm việc trong `Workspace`, scan QR, review read-only device information, nhập/chọn `owner_name` và `manufacture_date`, submit provisioning và xem result.

Web Portal Frontend

Cung cấp `Login` và `Workspace`, scan/parse QR, validate UI fields, submit authenticated request và hiển thị safe result.

Backend / Cloud Functions

Xác thực worker, validate request, create/restore identity, enforce duplicate policy, ghi audit và trả stable reason code.

Firebase Cloud Firestore

Lưu worker profile, serial lookup, device record và audit data theo API Contract.

QA / Support

Verify flow, conflict handling, audit, restore và support-required cases.

## 6. High-level Business Flow

DSetup completes imported serial_number verification
    ↓
DCAM displays provisioning QR
    ↓
Factory Worker opens Web Portal
    ↓
Login screen is shown
    ↓
Factory Worker logs in
    ↓
Workspace is shown
    ↓
Factory Worker scans QR displayed by DCAM
    ↓
Workspace parses and validates QR
    ↓
Workspace displays serial_number and device context as read-only
    ↓
Factory Worker enters/selects owner_name
    ↓
Factory Worker uses today's manufacture_date or selects another approved date
    ↓
Workspace displays inline review
    ↓
Factory Worker submits provisioning
    ↓
Backend validates authenticated worker and request
    ↓
Backend checks serial_lookup/{serial_number}
    ↓
If serial is new:
        create dcam_cloud_device_id
        create serial lookup
        create device record
If serial exists and restore is allowed:
        restore existing dcam_cloud_device_id
        update allowed business information
If conflict exists:
        stop and return support-required result
    ↓
Workspace displays Created / Restored / Error result
    ↓
DCAM retrieves or receives dcam_cloud_device_id and applies local state
## 7. Screen and Workspace Model

Screen

Purpose

Login

Xác thực `Factory Worker` trước khi truy cập provisioning workspace.

Workspace

Chứa toàn bộ QR scan, device review, information input, inline review, submit, result và error behavior.

Workspace areas:

Workspace Area

Purpose

Session Header

Hiển thị worker/session/environment và logout action nếu được enable.

QR Scanner Panel

Mở camera và scan provisioning QR displayed by DCAM.

Device Review Panel

Hiển thị `serial_number` và device context ở chế độ read-only.

Device Information Panel

Thu thập `owner_name` và `manufacture_date`.

Inline Review / Submit Area

Hiển thị dữ liệu cuối cùng và submit action trong cùng screen.

Result / Error Panel

Hiển thị Created, Restored, safe failure hoặc support-required result.

Rules:

Workspace panels are not separate screens or routes.
There is no Confirmation screen.
There is no separate Success or Failed screen.
Rescan and Provision Next Device reset the current Workspace session according to App Design.
## 8. QR Payload Direction

Exact schema thuộc **DCAM Web Portal & Device API Contract**.

Expected business fields:

payload_type = DCAM_DEVICE_PROVISIONING
payload_version
serial_number
device_model
firmware_version
app_package_name
app_version_name
app_version_code
optional serial_source
optional generated_at
optional expires_at
optional provisioning_nonce
optional signature
optional contract metadata

Rule

Description

QR-001

QR phải là provisioning QR do DCAM hiển thị.

QR-002

QR phải có supported payload type/version.

QR-003

QR phải chứa `serial_number`.

QR-004

`serial_number` được hiển thị read-only và không thể override.

QR-005

QR không được chứa `ANDROID_ID`, `android_id_hash`, credential hoặc long-lived secret.

QR-006

Camera stream không được lưu hoặc upload.

QR-007

Invalid, expired hoặc unsupported QR phải bị reject.

QR-008

QR chỉ dùng cho DCAM business provisioning; không phải Device Owner enrollment payload.

## 9. Device Information

Field

Source

Behavior

`serial_number`

QR displayed by DCAM

Read-only; không manual input; không barcode fallback trong Web Portal.

`device_model`

QR nếu available

Read-only.

`firmware_version`

QR nếu available

Read-only.

`app_version_name` / `app_version_code`

QR nếu available

Read-only.

`owner_name`

Factory Worker

Required/validation theo approved Product/Factory policy.

`manufacture_date`

Factory Worker / current factory local date

Dùng `YYYY-MM-DD`; default UX và correction policy theo App/Implementation Design.

`dcam_cloud_device_id`

Backend result

Hiển thị sau Created/Restored success.

## 10. Backend/API Direction

API schema thuộc **DCAM Web Portal & Device API Contract**.

Logical request:

authenticated Factory Worker session
request_id
serial_number from QR
owner_name
manufacture_date
approved QR/device metadata
optional nonce/signature context
Logical backend behavior:

validate authentication and Factory Worker authorization
validate request and QR-derived data
check serial_lookup/{serial_number}
create or restore dcam_cloud_device_id
create/update allowed device information
write audit event
return stable result and safe reason code
Frontend không được ghi trực tiếp vào production provisioning collections.

Request không được chứa:

ANDROID_ID
android_id_hash
device_lookup/{android_id_hash}
manual replacement serial
password or identity token as business data
maintenance credential
Device Owner credential
APK signing secret
## 11. Logical Server Data

Logical Area

Purpose

Worker Profiles

Worker identity, authorization và active status.

Serial Lookup

Mapping `serial_number` → `dcam_cloud_device_id`.

Devices

Cloud identity, hardware identity, business information, app/device metadata và device state.

Audit Events

Worker action, request identifier, result, reason code và changed fields.

Exact collection/document naming thuộc API Contract.

## 12. Provisioning State Model

State

Meaning

`PROVISIONING_REQUIRED`

DCAM đã có imported serial nhưng chưa có valid local/cloud provisioning result.

`PROVISIONING_QR_DISPLAYED`

DCAM đang hiển thị QR để Factory Worker scan.

`WORKSPACE_READY`

Worker đã login và Workspace sẵn sàng.

`QR_SCANNING`

Workspace đang scan QR.

`DEVICE_REVIEW_READY`

QR hợp lệ và device information đã hiển thị read-only.

`REVIEW_READY`

Required business information hợp lệ và có thể submit.

`PROVISIONING_PENDING`

Backend đang xử lý request.

`PROVISIONED`

Backend create/restore thành công; DCAM đang apply local state nếu cần.

`ACTIVE`

Device có valid identity và runtime/policy guards cho phép normal operation.

`SUPPORT_REQUIRED`

Duplicate/conflict/state cần xử lý ngoài Factory Worker flow.

`PROVISIONING_FAILED`

Request thất bại và cần retry hoặc support action.

`QUARANTINED` và `READY_TO_SHIP` là factory/release states, không phải decision do Web Portal App đưa ra.

## 13. Error and Exception Handling

Case

Expected Handling

Login failed

Giữ ở `Login` và hiển thị safe error.

Session expired

Chặn submit và yêu cầu login lại.

Camera permission denied

`Workspace` hiển thị hướng dẫn cấp quyền camera.

Invalid or wrong QR

Reject và cho phép scan lại.

Missing `serial_number`

Reject QR; không mở manual serial entry.

Unsupported QR version

Reject và hiển thị update/support-required message.

Duplicate serial conflict

Backend không silently remap; `Workspace` hiển thị support-required.

Existing serial restorable

Backend restore same `dcam_cloud_device_id`.

Invalid `owner_name`

Hiển thị field-level validation.

Invalid `manufacture_date`

Hiển thị field-level validation và yêu cầu valid `YYYY-MM-DD`.

Backend unavailable

Hiển thị retry-safe message; không assume success.

Request timeout / unknown outcome

Hiển thị unknown outcome và thực hiện reconciliation/idempotency theo API Contract.

Device disabled/revoked/quarantined

Hiển thị state và support-required instruction; worker không được override.

Local DCAM apply failed

DCAM giữ safe state và retry/report theo Android Operation Design.

## 14. Security and Audit

Area

Requirement

Factory Worker authentication

Chỉ authenticated và active `Factory Worker` được submit provisioning.

Backend authority

Backend phải verify authentication/authorization; không tin role hoặc worker id từ request body.

Serial handling

`serial_number` chỉ đến từ QR displayed by DCAM và luôn read-only.

Conflict handling

Factory Worker không được override duplicate, rebind hoặc restricted device state.

Firestore access

Frontend không được direct-write vào Serial Lookup, Devices hoặc Audit Events.

Sensitive data

Không log password, auth token, maintenance credential, signing secret hoặc raw restricted identifier.

Camera privacy

Không lưu hoặc upload camera stream/image trong QR scan flow.

Audit

Submit/result phải gắn worker identity, serial, cloud device id nếu có, request id, timestamp, result và safe reason code.

## 15. QA / Acceptance Checklist

Test Case

Expected Result

Screen model

Chỉ có `Login` và `Workspace`.

Valid Factory Worker login

Mở `Workspace`.

Invalid login

Giữ ở `Login` và hiển thị safe error.

Valid DCAM provisioning QR

Workspace parse QR và hiển thị device review.

Wrong QR / missing serial

Reject; không có manual serial input.

Serial display

`serial_number` read-only và không có edit control.

Direct serial barcode flow

Không tồn tại trong Web Portal.

Required owner/date validation

Invalid field chặn submit.

New serial

Backend trả Created với new `dcam_cloud_device_id`.

Existing restorable serial

Backend trả Restored với existing `dcam_cloud_device_id`.

Duplicate/conflict

Trả Support Required; không có override action.

Backend unavailable

Không assume success; hiển thị retry-safe state.

Android identity fields

Test fail nếu payload/request dùng `ANDROID_ID` hoặc `android_id_hash`.

Device Owner boundary

QR/Web Portal không được hiểu là Device Owner setup.

Production decision boundary

Web Portal không mark PASS, FAIL, QUARANTINED hoặc READY_TO_SHIP.

## 16. Open Questions / TBD

Item

Status

Exact QR payload schema

TBD / API Contract

QR nonce/signature/expiration/replay policy

TBD / API Contract + Security

Exact owner source and validation

TBD / Product + Factory

Worker account model: individual or shared station

TBD / Factory + Security

Duplicate/rebind support process

TBD / Product + Support + Backend

Request idempotency and timeout reconciliation

TBD / API Contract + Backend

Exact audit retention

TBD / Security + Backend

Supported browser/factory station profile

TBD / Implementation Design + Factory

## 17. Practical Conclusion

DCAM Device Provisioning Web Portal là single-purpose factory Web App.
User-facing account type duy nhất là Factory Worker.
Application chỉ có Login và Workspace.
Mọi thao tác sau login được thực hiện và hiển thị trong Workspace.
Factory Worker scan provisioning QR displayed by DCAM.
serial_number chỉ lấy từ QR và luôn read-only.
Web Portal không có manual serial entry hoặc direct serial barcode scan.
Factory Worker nhập/chọn owner_name và manufacture_date trong Workspace.
Backend create/restore dcam_cloud_device_id qua serial_lookup/{serial_number}.
Workspace hiển thị Created, Restored, safe error hoặc support-required result.
Web Portal không set Device Owner, không inject serial vào Android và không quyết định production acceptance.
API/path/schema/reason code thuộc DCAM Web Portal & Device API Contract.
App UI behavior thuộc DCAM Device Provisioning Web Portal App Design.
Implementation modules thuộc DCAM Device Provisioning Web Portal Implementation Design.