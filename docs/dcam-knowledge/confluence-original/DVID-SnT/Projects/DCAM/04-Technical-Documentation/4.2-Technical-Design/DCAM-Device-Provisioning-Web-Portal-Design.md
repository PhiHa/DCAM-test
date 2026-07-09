# DCAM Device Provisioning Web Portal Design

**Page ID**: 49315858  
**Version**: 8  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/49315858

---


# DCAM Device Provisioning Web Portal Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design / Business Flow Design

Version

Draft 0.8

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Cloud Lead / Security Reviewer / Android Lead / Backend Lead / Web Portal Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

PM/BA, Tech Lead, Android Developers, Web Developers, Cloud/WebServer Team, QA, Factory/Admin Users

Last Updated

2026-07-09

Related Jira

Không có

Related Documents

DCAM Web Portal & Device API Contract, DCAM Factory Provisioning & Device Production SOP, 04 - Device Configuration Requirements, 09 - System Settings Requirements, 06 - Cloud Services, Update & Configuration Architecture, DCAM Android Operation Design, DCAM Android Device Owner & Kiosk Policy Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM-BDMA Data Contract, DCAM Android Development Standard

## 1. Purpose

Tài liệu này là **source of truth** cho **DCAM Device Provisioning Web Portal** business flow.

Tài liệu này định nghĩa cách một BodyCamera device mới hoặc reworked trở thành registered DCAM device thông qua Web Portal / Factory Admin flow.

Tài liệu này follows **DCAM Factory Provisioning & Device Production SOP Draft 1.0** làm chuẩn định danh thiết bị:

textTài liệu này owns:

textAPI/data schema chi tiết thuộc **DCAM Web Portal & Device API Contract**.

Important boundary:

text## 2. Current Provisioning Baseline

textProvisioning/recovery lookup ở phía backend được giữ đơn giản có chủ đích:

text dcam_cloud_device_id
devices/{dcam_cloud_device_id}]]>Chi tiết endpoint/collection/document/schema đầy đủ thuộc **DCAM Web Portal & Device API Contract**.

## 3. Scope

### 3.1 In Scope

Area

Description

New device business provisioning

Đăng ký DCAM device lần đầu khi chưa có `dcam_cloud_device_id` và serial chưa có mapping active.

Rework / factory reset recovery

Restore existing `dcam_cloud_device_id` bằng `serial_number` sau factory reset/rework.

Serial-based identity creation

Backend/WebServer tạo hoặc restore `dcam_cloud_device_id` và map `serial_number` tới cloud id trong Cloud Firestore.

Web Portal business flow

Admin mở Web Portal, nhập/verify serial/owner/manufacture date và confirm provisioning nếu UI flow yêu cầu.

Optional QR Flow

DCAM có thể hiển thị QR business provisioning chứa `serial_number` nếu UI/flow yêu cầu; QR không chứa Android system identifier.

Device information assignment

Admin nhập/scan `serial_number`, nhập/chọn `owner_name` và nhập/chọn `manufacture_date`; server lưu device information.

BodyCamera identity restore

BodyCamera/DCAM restore local `dcam_cloud_device_id` bằng serial-based provisioning result.

SD Identity File awareness

Web Portal có thể ghi nhận serial source = `SD_IDENTITY_FILE` hoặc `BARCODE_SCAN` trong factory/provisioning record.

Provisioning states

Định nghĩa state model từ `UNPROVISIONED` đến `ACTIVE`.

Error handling

Xử lý duplicate serial, invalid owner name, invalid manufacture date, server unavailable, unauthorized admin, already provisioned.

Audit

Provisioning actions và device information changes phải auditable.

### 3.2 Out of Scope

Area

Managed In

API request/response/path/schema and Firestore collection/document contract

DCAM Web Portal & Device API Contract

Firebase Realtime Database design

Not applicable for current baseline.

Android Enterprise / Device Owner enrollment

DCAM Android Device Owner & Kiosk Policy Design + Factory SOP

DSetup detailed factory operation

DCAM Factory Provisioning & Device Production SOP

SD Identity File path/schema/sync rules

DCAM Factory Provisioning & Device Production SOP

Lock Task Mode, User Restrictions, Home/Launcher policy and Maintenance Mode

DCAM Android Device Owner & Kiosk Policy Design

Device identity requirement and CSON scope

04 - Device Configuration Requirements

Cloud provider architecture and device identity model

06 - Cloud Services, Update & Configuration Architecture

Runtime startup, kiosk policy verification and local identity restore

DCAM Android Operation Design

Local DB tables and config cache

DCAM SQLite Database Design

Security constraints and logging policy

DCAM Security & Encryption Design

Remote config payload fields

09 - System Settings Requirements / future Remote Config design

BDMA media import and user sync

DCAM-BDMA Data Contract

## 4. Core Decisions

Decision

Status

Default factory DCAM business provisioning method is serial-number based provisioning.

Approved Direction

Firebase Cloud Firestore là selected storage baseline.

Approved

Firebase Realtime Database không dùng cho current storage baseline.

Not Applicable

`serial_number` là Hardware Identity / primary recovery key.

Approved

`dcam_cloud_device_id` là server/cloud primary device id.

Approved

`serial_lookup/{serial_number}` là lookup/create/restore path chính.

Approved

SD Identity File là recovery cache trên thẻ nhớ ngoài, không phải Hardware Identity.

Approved

DSetup có thể recover serial từ SD Identity File hoặc scan barcode.

Approved

Web Portal QR Flow không phải Android Enterprise / Device Owner enrollment.

Approved

BDMA provisioning không required cho normal factory business provisioning.

Approved Direction

`owner_name` là mutable/semi-static device information, không phải primary key.

Approved

`manufacture_date` là semi-static device information dùng ISO format `YYYY-MM-DD`, không phải primary key.

Approved

ANDROID_ID không được dùng làm DCAM device identity key.

Approved

android_id_hash không được dùng làm recovery lookup key trong current baseline.

Approved

device_lookup/{android_id_hash} không dùng trong current baseline.

Approved

Advertising ID không được dùng làm DCAM device identity key.

Approved

Provisioning action yêu cầu authenticated Web Admin / Factory Admin.

Approved Direction

BDMA decoder profile không thuộc provisioning.

Approved

App/data/media/encoder contract metadata có thể được lưu để hỗ trợ support/compatibility.

Approved Direction

QR optional nonce/signature/expiration vẫn là implementation TBD.

TBD

Exact REST backend vs direct Firestore SDK/API usage vẫn là TBD.

TBD

## 5. Actors

Actor

Responsibility

BodyCamera / DCAM App

Nhận `serial_number` từ DSetup, lưu app-private serial, sync SD Identity File, fetch/restore `dcam_cloud_device_id`, ghi local identity/config sau khi provisioning thành công.

DSetup

Resolve serial từ SD Identity File hoặc barcode scan, install/update APK, set/verify Device Owner khi required, inject serial vào DCAM.

Factory Admin / Web Admin

Login vào Web Portal, verify serial, nhập/chọn owner name, nhập/chọn manufacture date và confirm provisioning.

Web Provisioning Portal

Mobile-browser UI cho scan/entry, preview, device information entry, confirmation và result.

Firebase Cloud Firestore / WebServer Backend

Validate admin request, tạo/restore Firestore device record, tạo serial lookup mapping, lưu device information, lưu app/contract metadata nếu cần và audit log.

Android Enterprise / DPC Enrollment Flow

Deployment process riêng để đưa device vào fully managed / Device Owner. Không thuộc tài liệu này.

QA / Support

Verify provisioning flow, error cases, audit log, local restore behavior và boundary với Device Owner/kiosk policy.

## 6. High-level Business Flow

text## 7. BodyCamera-side Flow

text### 7.1 Local Write after Provisioning Success

Sau khi provisioning lookup thành công, DCAM ghi:

Local Target

Data

`dcam.db`

`dcam_cloud_device_id`, `serial_number`, `owner_name` mirror, `manufacture_date` mirror, provisioning state, remote config metadata, app/contract metadata nếu applicable.

`dcam_config.cson`

`serial_number`, `owner_name`, `manufacture_date`, device model/firmware info nếu applicable, app/contract metadata nếu cần cho BDMA/support display.

SD Identity File

Recovery cache chứa `serial_number` để hỗ trợ DSetup sau factory reset.

Logs / diagnostics

Provisioning result với safe reason codes.

`dcam_config.cson` phải chỉ chứa device-information. Operational settings và kiosk policy settings nằm trong `dcam.db`/policy boundary, không nằm trong CSON.

## 8. Web Portal Screen Flow

Screen

Purpose

Key Actions

Login

Authenticate Factory/Admin user.

Login, session check, role check.

Serial Entry / Scan

Nhập hoặc scan `serial_number`, hoặc nhận serial từ QR nếu UI flow dùng QR.

Manual input, barcode scan, or QR parsing.

Device Info Preview

Hiển thị decoded/resolved device information trước provisioning.

Hiển thị serial, model, app version, firmware version, environment, serial source nếu có.

Owner Name Input

Nhập/chọn `owner_name`.

Manual input, customer/agency selection hoặc default value theo factory/customer policy.

Manufacture Date Input

Nhập/chọn `manufacture_date`.

Date input dùng ISO date `YYYY-MM-DD`; validate date format.

Confirmation

Confirm provisioning action.

Hiển thị serial + owner + manufacture date + target environment.

Success

Provisioning completed.

Hiển thị `dcam_cloud_device_id`, serial, owner, manufacture date và status.

Failed / Retry

Error handling.

Hiển thị reason, retry hoặc tạo support action.

## 9. QR Payload Direction if QR Is Used

Exact schema thuộc **DCAM Web Portal & Device API Contract**. Business-level direction:

textRules:

Rule

Description

QR-001

QR không được chứa raw Android system identifier.

QR-002

QR không được chứa `android_id_hash`.

QR-003

QR có thể chứa `serial_number` vì serial là approved Hardware Identity.

QR-004

QR có thể chứa local nonce/correlation id nếu được API Contract/Security approve.

QR-005

QR phải có payload version để compatibility.

QR-006

Exact QR signature/expiration vẫn là TBD.

QR-007

`owner_name` và `manufacture_date` thường được nhập/chọn trong Web Portal, không required trong QR.

QR-008

QR payload này chỉ dùng cho DCAM business provisioning; không phải Android Enterprise Device Owner enrollment payload.

## 10. Backend/API Direction

API schema thuộc **DCAM Web Portal & Device API Contract**.

Current logical API flow:

textBackend/WebServer/Firestore không được yêu cầu Android tạo server-side provisioning challenge trước khi hiển thị QR trong current baseline.

Request direction cho Web Portal device creation nên include hoặc derive:

textRequest không được include:

text## 11. Server-side Data Created

Provisioning tạo hoặc cập nhật Cloud Firestore data:

textExact Firestore collection/document names có thể thay đổi trong implementation, nhưng logical ownership phải được preserve theo **DCAM Web Portal & Device API Contract**.

## 12. Provisioning State Model

State

Meaning

`UNPROVISIONED`

Chưa có local cloud identity và chưa có confirmed server mapping.

`SERIAL_REQUIRED`

DCAM/DSetup cần serial từ SD Identity File hoặc barcode scan.

`SERIAL_RESOLVED`

Serial đã được recover/scan/inject và validated.

`PROVISIONING_REQUIRED`

Device phải được register/restore thông qua Web Provisioning Portal hoặc approved backend flow.

`PROVISIONING_QR_DISPLAYED`

BodyCamera đang hiển thị local QR payload nếu UI flow dùng QR.

`PROVISIONING_PENDING`

Web Portal/admin action có thể đang xử lý hoặc Android đang chờ serial lookup result.

`PROVISIONED`

Server identity tồn tại và local restore/write đang xử lý.

`ACTIVE`

Device có valid local identity và có thể tiếp tục normal runtime/login nếu runtime/policy guards cho phép.

`DISABLED`

Device record tồn tại nhưng cloud/config operation bị disabled bởi server policy.

`REVOKED`

Device không được phép dùng production cloud/webserver identity.

`QUARANTINED`

Device fail factory/release checks và không được enter field operation.

`PROVISIONING_FAILED`

Provisioning failed; cần admin/support action.

## 13. Error and Exception Handling

Case

Expected Handling

Invalid QR payload

Portal reject và hiển thị invalid QR message.

Admin unauthorized

Portal block provisioning action.

Missing serial_number

Portal/backend reject; DSetup must recover from SD Identity File or barcode scan.

Duplicate `serial_number`

Backend restore existing device if expected; block or require admin-approved resolution if conflict exists.

Invalid `owner_name`

Portal/backend reject hoặc yêu cầu admin sửa giá trị.

Invalid `manufacture_date`

Portal/backend reject hoặc yêu cầu admin dùng valid `YYYY-MM-DD` date.

Device already provisioned

Portal hiển thị existing device; không tạo duplicate active record silently.

Server unavailable

BodyCamera vẫn ở provisioning-required/pending và admin retry sau.

Device offline after portal success

Server record tồn tại; BodyCamera hoàn tất khi serial lookup thành công sau đó.

Local DB write failed

DCAM report provisioning local apply failure và retry safe local write.

CSON write failed

DCAM giữ DB state và report CSON apply failure để retry/support.

SD Identity File sync failed

DCAM report safe warning; flow tiếp tục nếu SD recovery cache là optional.

SD Identity File conflicts with app-private serial

App-private serial wins; overwrite file hoặc raise warning theo policy.

Device disabled/revoked/quarantined

DCAM không được enter normal ACTIVE cloud/config behavior.

Device Owner missing

Không được xử lý bởi business provisioning flow này; Android Operation/Kiosk Policy/SOP xử lý policy-required state.

## 14. Security and Audit

Security requirements được định nghĩa bởi **DCAM Security & Encryption Design**. Tài liệu này áp dụng các rule đó vào provisioning.

Area

Requirement

Admin authentication

Chỉ authenticated Web Admin / Factory Admin mới được provision.

Backend authority

Web Portal client gọi backend; backend tạo production records và serial lookup mapping.

Firestore access control

Firestore security rules hoặc backend API authorization phải ngăn unauthorized read/write.

Identifier handling

Dùng `serial_number`; không dùng raw Android system identifier hoặc `android_id_hash`.

SD Identity File

Chỉ là recovery cache; nếu ghi nhận vào backend thì chỉ ghi safe result/source metadata.

Device information

`owner_name` và `manufacture_date` là device information; thay đổi phải auditable nếu được admin/server update.

Contract metadata

App/data/media/encoder contract metadata có thể dùng cho compatibility/support; dynamic decoder profile không được dùng.

Audit

Provisioning action phải ghi nhận actor, time, source, serial source, result, changed fields và reason code.

Rebind

Re-provision/rebind yêu cầu explicit admin approval và audit.

Sensitive logging

Logs phải dùng safe ids/reason codes.

Kiosk boundary

Provisioning audit không được chứa Android/device policy enrollment secret values, Maintenance Mode credential values hoặc kiosk exit secret values.

## 15. QA / Acceptance Checklist

Test Case

Expected Result

New device with serial not mapped

Backend creates new `dcam_cloud_device_id` and `serial_lookup/{serial_number}` when authorized.

Factory reset with valid SD Identity File

DSetup recovers serial and backend restores existing `dcam_cloud_device_id`.

Factory reset without valid SD Identity File

DSetup requires barcode scan before provisioning/recovery.

Admin enters serial/owner/manufacture date

Backend stores device information and serial lookup mapping.

BodyCamera checks serial lookup after success

BodyCamera ghi local identity/device info và chỉ trở thành `ACTIVE` nếu runtime/policy guards cho phép.

Firestore baseline verified

Provisioning data được lưu trong Cloud Firestore, không phải Realtime Database.

App update after provisioning

BodyCamera keeps local identity and syncs SD Identity File if needed.

Duplicate serial

Portal/backend restores existing expected device or blocks conflict for support/admin resolution.

Invalid owner name

Portal/backend block hoặc yêu cầu sửa.

Invalid manufacture date format

Portal/backend block và yêu cầu `YYYY-MM-DD`.

Server offline

BodyCamera vẫn safe và retryable.

Local DB/CSON write failure

Failure được log và device không silently trở thành inconsistent.

Unauthorized admin

Provisioning action bị block.

Decoder profile field supplied accidentally

Backend reject/ignore field vì `bdma_decoder_profile_id` không thuộc contract.

Web Portal QR confused with Device Owner enrollment

QA verify documentation/UI labels thể hiện rõ đây là DCAM business provisioning, không phải Android Enterprise enrollment.

Android ID fields appear in payload

Test must fail; `ANDROID_ID`, `android_id_hash`, `device_lookup` are not in current baseline.

## 16. Open Questions / TBD

Item

Status

Exact QR payload optional nonce/signature/expiration mechanism

TBD / API Contract + Security

REST backend vs direct Firestore SDK/API usage

TBD / API Contract + Backend

Exact Firestore collection/document names

TBD / API Contract + Backend

Exact Firestore security rules / backend authorization boundary

TBD / API Contract + Security + Backend

Web Portal UI wireframe

TBD

Serial barcode/QR format on device label

TBD

Owner name validation length/charset

TBD

Owner source: manual input vs customer dropdown

TBD

Manufacture date source and correction policy

TBD

Duplicate serial policy

TBD

Device rebind policy

TBD

Admin roles/permissions

TBD

Audit log physical schema

TBD / API Contract + Security

Serial lookup retry interval / timeout

TBD / Android Operation

Offline factory network behavior

TBD

Exact sequencing between DSetup Device Owner setup, serial injection and Web Portal business provisioning

TBD / Factory SOP + Kiosk Policy Design + Device POC

## 17. Practical Conclusion

textDefault DCAM business provisioning baseline:

text