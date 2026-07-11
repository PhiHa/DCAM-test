# DCAM Device Provisioning Web Portal App Design

**Page ID**: 50692194  
**Version**: 3  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/50692194

---


# DCAM Device Provisioning Web Portal App Design

Item

Information

Project

DCAM Android BodyCamera Application

Document Type

Web App Design

Version

Approved 1.1

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Web Portal Lead / Backend Lead / Android Lead / Security Reviewer / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Page

DCAM Device Provisioning Web Portal Design

Target Audience

Web Developers, Backend Developers, Android Developers, Factory Worker, Factory Lead, QA, Security Reviewer

Last Updated

2026-07-10

Related Documents

DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal Implementation Design, DCAM Web Portal & Device API Contract, DCAM DSetup Factory Tool Design, DCAM Factory Provisioning & Device Production SOP, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM Android Operation Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM Security & Encryption Design, DCAM SQLite Database Design, DCAM Device POC & Hardware Validation Report

## 1. Purpose

Tài liệu này mô tả **DCAM Device Provisioning Web Portal App** ở mức ứng dụng Web để dev triển khai UI, state, validation và backend integration cho factory provisioning flow.

Trang cha **DCAM Device Provisioning Web Portal Design** là source of truth cho business flow. Trang này mô tả app Web cụ thể mà `Factory Worker` sử dụng trong nhà máy.

Web Portal App là một **single-purpose factory web app** và chỉ có hai screen:

Login
Workspace
Sau khi login thành công, toàn bộ QR scan, device review, device information input, inline review, submit, result và error handling đều diễn ra trong `Workspace`.

Important baseline:

Web Portal App has only two screens: Login and Workspace.
After successful login, all actions happen inside Workspace.
Web Portal App scans the provisioning QR displayed by DCAM.
Web Portal App does not scan serial barcode directly.
Web Portal App does not manually collect serial_number.
serial_number comes from the QR payload displayed by DCAM.
serial_number is displayed as read-only inside Workspace after QR scan.
Factory Worker cannot edit or replace serial_number.
owner_name and manufacture_date are required for the approved implementation baseline.
## 2. Current App Baseline

User-facing account type = Factory Worker only.
Factory Worker login is required before provisioning.
After login success, Factory Worker lands on Workspace.
Workspace is the only working screen after login.
Factory Worker scans QR displayed on DCAM device screen inside Workspace.
QR payload contains serial_number and device context.
Workspace parses QR payload and shows read-only device information.
Factory Worker enters/selects owner_name inside Workspace.
Factory Worker uses today's manufacture_date or selects another valid date inside Workspace.
Factory Worker reviews information inline inside Workspace and submits provisioning.
Backend creates or restores devices/{dcam_cloud_device_id} by serial_number.
Workspace shows provisioning result.
Web Portal App does not set Device Owner.
Web Portal App does not inject serial_number into Android.
Web Portal App does not verify Lock Task/kiosk baseline.
Web Portal App does not mark PASS, FAIL, QUARANTINED or READY_TO_SHIP.
## 3. Scope

Area

Description

Factory Worker login

App cho phép một loại tài khoản user-facing duy nhất là `Factory Worker` login để vào `Workspace`.

Two-screen app model

App chỉ có hai screen: `Login` và `Workspace`. Không có screen riêng cho QR Scan, Device Review, Confirmation hoặc Result.

Workspace-based operation

Sau login thành công, toàn bộ thao tác provisioning được thực hiện và hiển thị trong `Workspace`.

QR scan inside Workspace

`Workspace` dùng camera/browser QR scanner để scan provisioning QR đang hiển thị trên màn hình DCAM device.

QR payload parsing

`Workspace` parse QR payload và lấy `serial_number` cùng device context từ QR.

Device information review

`Workspace` hiển thị thông tin thiết bị từ QR để worker kiểm tra trước khi submit.

Read-only serial_number

`Workspace` hiển thị `serial_number` từ QR ở dạng read-only; worker không được sửa hoặc thay thế.

Owner name input

`Workspace` yêu cầu worker nhập/chọn `owner_name` theo approved factory/customer flow.

Manufacture date input

`Workspace` yêu cầu `manufacture_date`; mặc định dùng ngày local hiện tại và cho phép chọn ngày hợp lệ khác.

Inline submit action

`Workspace` hiển thị thông tin cần review và cung cấp action để submit provisioning, không mở confirmation screen riêng.

Backend integration

`Workspace` gọi backend/API để create hoặc restore `devices/{dcam_cloud_device_id}` bằng `serial_number`.

Provisioning result display

`Workspace` hiển thị kết quả provisioning, bao gồm `dcam_cloud_device_id` khi thành công.

Safe error display

`Workspace` hiển thị lỗi an toàn, dễ hiểu cho worker và không lộ secret/internal stack trace.

## 4. Out of Scope

Area

Reason

Multi-screen wizard

Web Portal App không dùng wizard nhiều màn hình; mọi thao tác sau login nằm trong `Workspace`.

Separate QR Scan screen

QR scan là panel/state trong `Workspace`, không phải screen riêng.

Separate Device Review screen

Device review là panel/state trong `Workspace`, không phải screen riêng.

Separate Confirmation screen

Review trước submit là inline area trong `Workspace`, không phải screen riêng.

Separate Result screen

Provisioning result được hiển thị trong `Workspace`, không phải screen riêng.

Serial barcode scan

Web Portal App không scan barcode serial trực tiếp; serial được lấy từ QR do DCAM hiển thị.

Manual serial entry

Web Portal App không cho worker nhập `serial_number` thủ công.

DSetup operation

DSetup owns factory tool flow đến khi DCAM xác nhận imported `serial_number` đúng expected value.

Device Owner setup

Web Portal App không set hoặc verify `Device Owner`; việc này thuộc DSetup / Factory SOP / Kiosk Policy Design.

Android runtime identity import

Web Portal App không inject serial vào DCAM và không ghi local Android identity storage.

Lock Task / kiosk verification

Web Portal App không verify `Lock Task`, `User Restrictions`, Home/Launcher hoặc Maintenance Mode.

Recording / storage / BDMA tests

Web Portal App không chạy recording, storage finalization, ADB import hoặc BDMA readiness checks.

Production acceptance

Web Portal App không mark `PASS`, `FAIL`, `QUARANTINED` hoặc `READY_TO_SHIP`.

Role management

App không có nhiều role user-facing, permission matrix phức tạp hoặc user management screen.

Support override / rebind approval

Nếu duplicate/conflict cần override hoặc rebind, App dừng flow trong `Workspace` và hiển thị support-required message.

Firestore schema ownership

Exact collection/document/schema thuộc DCAM Web Portal & Device API Contract.

## 5. User Account Model

Account Type

Responsibility

Factory Worker

Login vào Web Portal App, làm việc trong `Workspace`, scan QR trên DCAM device, review thông tin thiết bị, nhập/chọn `owner_name`, chọn `manufacture_date`, submit provisioning và xem provisioning result.

Account model baseline:

Only one user-facing account type exists in this app: Factory Worker.
Factory Worker can perform the basic QR-based provisioning flow only.
Factory Worker cannot approve override, rebind, disable, revoke, quarantine or READY_TO_SHIP decisions.
Internal backend service roles hoặc support/admin tools nếu có không thuộc user-facing scope của Web Portal App này.

## 6. Web App Responsibilities

Responsibility

Description

Authenticate worker

`Login` yêu cầu Factory Worker xác thực trước khi vào `Workspace`.

Enter workspace

Sau login thành công, app chuyển vào `Workspace`; đây là screen làm việc duy nhất.

Start QR scan

`Workspace` mở QR scanner panel và hướng dẫn worker đưa camera vào QR đang hiển thị trên DCAM.

Parse QR payload

`Workspace` parse payload, kiểm tra payload type/version và lấy các field cần thiết.

Validate QR payload

`Workspace` reject QR nếu payload missing `serial_number`, sai type, sai version hoặc expired nếu expiration rule đã được enable.

Show device review

`Workspace` hiển thị device context lấy từ QR để worker kiểm tra.

Collect owner_name

`Workspace` yêu cầu nhập/chọn `owner_name` theo approved Product/Factory rule.

Collect manufacture_date

`Workspace` mặc định dùng ngày local hiện tại hoặc cho phép chọn ngày khác, gửi theo ISO date `YYYY-MM-DD`.

Inline review before submit

`Workspace` hiển thị serial + owner + manufacture date + device context ngay trong cùng screen trước khi submit.

Call backend

`Workspace` gọi API create/restore device identity bằng `serial_number` từ QR.

Display result

`Workspace` hiển thị success/failure và `dcam_cloud_device_id` nếu backend trả về thành công.

Prevent identity override

`Workspace` không cho worker sửa `serial_number` hoặc tự xử lý duplicate/conflict bằng UI override.

## 7. App Navigation Flow

Factory Worker opens Web Portal App
    ↓
Login screen is shown
    ↓
Factory Worker logs in
    ↓
If login success:
        Workspace screen is shown
Else:
        Login shows safe error
    ↓
Inside Workspace:
        Factory Worker scans QR displayed by DCAM device
        ↓
        Workspace parses QR payload
        ↓
        If QR payload is valid:
                Workspace shows read-only device information
        Else:
                Workspace shows QR error and allows retry
        ↓
        Factory Worker reviews read-only device information
        ↓
        Factory Worker enters/selects owner_name
        ↓
        Workspace defaults manufacture_date to today's local factory date
        ↓
        Factory Worker may select another valid date
        ↓
        Workspace shows inline review summary
        ↓
        Factory Worker submits provisioning from Workspace
        ↓
        Workspace calls backend create/restore by serial_number
        ↓
        If success:
                Workspace shows provisioning result with dcam_cloud_device_id
        Else:
                Workspace shows safe error / support-required message
App navigation rule:

Login and Workspace are the only screens.
All post-login provisioning UI states are Workspace states/panels, not separate screens/routes.
## 8. Screen List

Screen

Purpose

Key Actions

Login

Xác thực `Factory Worker` trước khi vào factory provisioning workspace.

Nhập credential, submit login, hiển thị login error nếu fail, chuyển sang `Workspace` nếu success.

Workspace

Screen làm việc duy nhất sau login; chứa QR scan, device review, information form, inline review, submit action, result display và error/retry state.

Scan QR, parse QR, review read-only `serial_number`, nhập/chọn `owner_name`, chọn `manufacture_date`, submit provisioning, xem result, retry hoặc chuyển support khi cần.

## 9. Workspace Layout

`Workspace` có thể chia thành các panel/section nội bộ. Các panel này không phải screen riêng và không yêu cầu navigation route riêng.

Workspace Area

Purpose

Behavior

Header / Session Area

Hiển thị trạng thái đăng nhập và thông tin worker/session.

Hiển thị worker name/code nếu có, environment nếu có và logout action.

QR Scanner Panel

Scan QR đang hiển thị trên DCAM device.

Open camera, scan QR, retry scan, show camera permission guidance.

Device Review Panel

Hiển thị thông tin thiết bị lấy từ QR.

Hiển thị read-only `serial_number`, model, firmware/app info và QR metadata nếu có.

Device Information Panel

Nhập/chọn thông tin nghiệp vụ cần gắn với device.

Nhập/chọn `owner_name`, dùng today/select another date cho `manufacture_date`.

Inline Review / Submit Area

Cho worker kiểm tra lần cuối và submit request.

Hiển thị serial + owner + manufacture date + device context trong cùng `Workspace`, có submit button.

Result / Error Panel

Hiển thị kết quả backend create/restore hoặc lỗi an toàn.

Show success/failure, `dcam_cloud_device_id`, safe reason code, retry action hoặc support-required message.

## 10. QR Scanner Panel

Field / Element

Behavior

Camera preview

Hiển thị camera preview để worker scan QR trên màn hình DCAM.

Scan instruction

Hướng dẫn worker mở đúng DCAM provisioning QR screen và đưa QR vào khung scan.

QR payload result

Sau khi scan thành công, `Workspace` parse payload và cập nhật Device Review Panel.

Retry scan

Cho phép scan lại nếu QR unreadable hoặc payload invalid.

Camera permission message

Nếu browser/camera permission bị chặn, `Workspace` hiển thị hướng dẫn cấp quyền camera.

Manual serial input

Không có trong `Workspace`.

Barcode serial scan

Không có trong `Workspace`.

Rules:

Rule

Description

WEBQR-001

Web Portal App chỉ lấy `serial_number` từ provisioning QR displayed by DCAM.

WEBQR-002

App không cho worker nhập `serial_number` thủ công.

WEBQR-003

App không scan barcode serial trên tem/label thiết bị.

WEBQR-004

QR phải có payload type/version để App phân biệt với QR khác.

WEBQR-005

Nếu QR missing `serial_number`, App phải reject.

WEBQR-006

Nếu QR expired hoặc signature invalid khi rule này được enable, App phải reject.

WEBQR-007

QR scan không tự động provisioning; worker vẫn phải review inline và submit trong `Workspace`.

## 11. QR Payload Direction

Exact QR schema thuộc **DCAM Web Portal & Device API Contract**.

Expected QR payload direction:

payload_type = DCAM_DEVICE_PROVISIONING
payload_version
serial_number
device_model
firmware_version
app_package_name
app_version_name
app_version_code
optional device_time
optional serial_source
optional provisioning_nonce
optional generated_at
optional expires_at
optional signature

Field

App Behavior

`payload_type`

App verify đúng provisioning QR type trước khi tiếp tục.

`payload_version`

App verify version được support; unsupported version thì reject.

`serial_number`

App lấy từ QR, hiển thị read-only trong `Workspace` và gửi backend create/restore.

`device_model`

App hiển thị read-only nếu QR có field này.

`firmware_version`

App hiển thị read-only nếu QR có field này.

`app_package_name`

App hiển thị hoặc dùng để verify đúng DCAM app nếu cần.

`app_version_name`

App hiển thị read-only để support factory/debug.

`app_version_code`

App hiển thị read-only hoặc gửi backend nếu API yêu cầu.

`serial_source`

App hiển thị read-only nếu QR có field này.

`provisioning_nonce`

App gửi backend nếu API Contract yêu cầu nonce/correlation.

`generated_at`

App hiển thị hoặc dùng để kiểm tra freshness nếu rule được enable.

`expires_at`

App reject QR nếu QR đã hết hạn và expiration rule được enable.

`signature`

App/backend verify signature nếu Security/API Contract enable signed QR.

QR forbidden fields:

Field

Reason

`ANDROID_ID`

Không dùng Android system identifier cho production identity.

`android_id_hash`

Không dùng làm recovery lookup key trong current baseline.

`device_lookup/{android_id_hash}`

Deprecated/removed path.

Google account credential

QR không được chứa credential hoặc secret.

Maintenance password

QR không được chứa Maintenance Password Gate secret.

APK signing secret

QR không được chứa signing secret.

Factory Wi-Fi password

QR không được chứa factory Wi-Fi credential.

## 12. Device Review Panel

Field

Behavior

`serial_number`

Hiển thị read-only, không editable.

Device model

Hiển thị read-only nếu QR cung cấp.

Firmware version

Hiển thị read-only nếu QR cung cấp.

App version

Hiển thị read-only nếu QR cung cấp.

Serial source

Hiển thị read-only nếu QR cung cấp.

QR generated time

Hiển thị nếu QR cung cấp và hữu ích cho factory/debug.

Environment

Hiển thị target environment nếu backend/app config có.

Rescan action

Cho phép worker scan lại QR nếu scan nhầm thiết bị.

Rules:

Rule

Description

REVIEW-001

Worker không được edit `serial_number`.

REVIEW-002

Nếu worker scan nhầm device, worker phải rescan QR đúng thiết bị trong `Workspace`.

REVIEW-003

App không tự đổi hoặc normalize `serial_number` ngoài safe handling được API Contract approve.

REVIEW-004

Nếu QR conflict với backend policy, App không tự xử lý conflict; backend trả error/support-required.

## 13. Device Information Panel

Field

Behavior

`owner_name`

Required; worker nhập/chọn theo approved factory/customer flow.

`manufacture_date`

Required; mặc định ngày local hiện tại, có thể chọn ngày hợp lệ khác; gửi theo `YYYY-MM-DD`.

`serial_number`

Chỉ hiển thị read-only; không editable.

Validation direction:

Rule

Description

FORM-001

`owner_name` không được để trống và phải pass approved length/charset rule.

FORM-002

`manufacture_date` phải là valid ISO date `YYYY-MM-DD`.

FORM-003

App không cho submit nếu required field thiếu.

FORM-004

Field không được chứa credential, token hoặc secret.

FORM-005

Exact owner validation và date policy thuộc API Contract/Product/Factory decision.

## 14. Inline Review / Submit Area

Item

Behavior

`serial_number`

Hiển thị read-only từ QR.

`owner_name`

Hiển thị giá trị worker nhập/chọn.

`manufacture_date`

Hiển thị effective date và nguồn Today/Selected date.

Device context

Hiển thị model/firmware/app version nếu có.

Target environment

Hiển thị environment nếu app/backend config cung cấp.

Submit button

Gửi provisioning request tới backend.

Edit form action

Cho phép sửa `owner_name` hoặc `manufacture_date`; không sửa serial.

Rescan action

Hủy flow hiện tại và scan lại QR nếu worker chọn nhầm device.

## 15. Backend Integration

API/path/schema chi tiết thuộc **DCAM Web Portal & Device API Contract**.

Workspace submits provisioning request:
    serial_number from QR payload
    owner_name from workspace form
    manufacture_date from workspace form
    QR metadata if required by API Contract
    worker identity/session from login

Backend:
    validates Factory Worker session
    validates QR payload / nonce / signature if enabled
    checks serial_lookup/{serial_number}
    creates or restores devices/{dcam_cloud_device_id}
    stores allowed device information
    writes audit event
    returns provisioning result
Request direction:

Field

Source

`serial_number`

QR payload displayed by DCAM.

`owner_name`

Device Information Panel trong `Workspace`.

`manufacture_date`

Device Information Panel trong `Workspace`.

`device_model`

QR payload nếu available.

`firmware_version`

QR payload nếu available.

`app_version_name`

QR payload nếu available.

`app_version_code`

QR payload nếu available.

`provisioning_nonce`

QR payload nếu API Contract yêu cầu.

`worker_session`

Firebase Authentication/session context của Factory Worker.

Response direction:

Field

App Behavior

`result`

Hiển thị Created/Restored/failure trong `Workspace`.

`dcam_cloud_device_id`

Hiển thị khi provisioning thành công.

`device_state`

Hiển thị nếu backend trả về state.

`reason_code`

Hiển thị safe reason hoặc map sang message dễ hiểu.

`support_required`

Nếu true, `Workspace` dừng flow và hướng dẫn worker chuyển support/factory process.

## 16. Result / Error Panel

Result Case

App Behavior

New device created

`Workspace` hiển thị success, `dcam_cloud_device_id`, `serial_number` và safe next-step message.

Existing device restored

`Workspace` hiển thị restored success và existing `dcam_cloud_device_id`.

Duplicate serial conflict

`Workspace` hiển thị support-required; worker không được override.

QR invalid

`Workspace` yêu cầu scan lại đúng QR.

QR expired

`Workspace` yêu cầu refresh QR trên DCAM device và scan lại.

Unauthorized worker

App quay về `Login` hoặc hiển thị login/session error an toàn.

Backend unavailable

`Workspace` hiển thị retry-safe message; không assume provisioning success.

Device disabled/revoked/quarantined

`Workspace` hiển thị state và support-required instruction; không tự đổi state.

Validation error

`Workspace` hiển thị field-level error cho `owner_name` hoặc `manufacture_date`.

## 17. Error Handling

Error Case

Handling

Login failed

`Login` hiển thị safe error và không cho vào `Workspace`.

Login expired

App yêu cầu Factory Worker login lại.

Camera permission denied

`Workspace` hiển thị hướng dẫn cấp quyền camera.

QR unreadable

`Workspace` cho phép retry scan; không chuyển sang manual serial input.

Wrong QR type

`Workspace` reject và yêu cầu scan đúng DCAM provisioning QR.

Unsupported QR version

`Workspace` reject và yêu cầu update app/DCAM hoặc chuyển support.

Missing `serial_number` in QR

`Workspace` reject QR; không cho worker nhập serial thủ công.

Invalid `serial_number` format from QR

`Workspace` reject hoặc gửi backend validate; không cho edit serial.

QR expired

`Workspace` yêu cầu refresh QR trên DCAM device và scan lại.

Signature invalid

`Workspace` reject và hiển thị support/security-required message nếu signed QR được enable.

Backend timeout

`Workspace` hiển thị unknown outcome/retry option theo idempotency policy.

Duplicate serial conflict

`Workspace` dừng flow và hiển thị support-required message.

Owner name invalid

`Workspace` hiển thị field validation error.

Manufacture date invalid

`Workspace` hiển thị field validation error và yêu cầu `YYYY-MM-DD`.

## 18. Security Rules

Rule

Description

WEBAPP-SEC-001

App chỉ có một user-facing account type là `Factory Worker`.

WEBAPP-SEC-002

Factory Worker phải login trước khi vào `Workspace`.

WEBAPP-SEC-003

App chỉ có hai screens: `Login` và `Workspace`.

WEBAPP-SEC-004

App lấy `serial_number` từ QR displayed by DCAM và không cho manual serial override.

WEBAPP-SEC-005

App không đọc/gửi/lưu `ANDROID_ID`, `android_id_hash` hoặc `device_lookup/{android_id_hash}`.

WEBAPP-SEC-006

App không lưu credential, token, Maintenance Password Gate secret, Google account secret hoặc factory Wi-Fi password trong browser storage/logs.

WEBAPP-SEC-007

App không expose internal backend stack trace hoặc sensitive error details cho worker.

WEBAPP-SEC-008

Duplicate/conflict/rebind không được xử lý bằng worker UI override.

WEBAPP-SEC-009

QR signature/nonce/expiration nếu được enable phải được verify theo API Contract/Security Design.

WEBAPP-SEC-010

App không set Device Owner, không control kiosk policy và không bypass Android runtime security.

WEBAPP-SEC-011

Provisioning submit phải tạo backend audit event với worker identity/session.

WEBAPP-SEC-012

Camera stream/image dùng để scan QR không được lưu hoặc upload.

## 19. Audit Events

Event

Description

`WORKER_LOGIN_SUCCESS`

Factory Worker login thành công.

`WORKER_LOGIN_FAILED`

Login fail với safe reason.

`WORKSPACE_OPENED`

Worker vào `Workspace` sau khi login thành công.

`QR_SCAN_SUCCESS`

QR scan và parse thành công trong `Workspace`.

`QR_SCAN_REJECTED`

QR bị reject do wrong type, missing field, invalid version, expired hoặc signature invalid.

`PROVISIONING_INLINE_REVIEW_READY`

Workspace đã có đủ dữ liệu để worker review và submit.

`PROVISIONING_SUBMITTED`

Worker submit provisioning request từ `Workspace`.

`PROVISIONING_SUCCESS`

Backend create/restore device identity thành công.

`PROVISIONING_FAILED`

Backend reject hoặc failed với safe reason code.

`SUPPORT_REQUIRED`

Flow dừng vì conflict hoặc state cần xử lý ngoài app.

## 20. UI Validation Rules

Field

Validation

QR payload

Phải parse được, đúng `payload_type`, đúng supported `payload_version` và có `serial_number`.

`serial_number`

Read-only từ QR; không editable; không manual input.

`owner_name`

Required; không được để trống; phải pass approved validation.

`manufacture_date`

Required; valid ISO date `YYYY-MM-DD`; không lệch ngày do timezone conversion.

Submit provisioning

Chỉ enable trong `Workspace` khi QR valid, required fields hợp lệ và worker session active.

## 21. QA / Acceptance Checklist

Test Case

Expected Result

Factory Worker login success

Worker vào được `Workspace`.

Factory Worker login failure

App giữ ở `Login` và hiển thị login error an toàn.

Screen count check

App chỉ có `Login` và `Workspace`; không có route/screen riêng cho QR Scan, Device Review, Confirmation hoặc Result.

Scan valid DCAM provisioning QR

`Workspace` parse QR và hiển thị Device Review Panel.

Scan wrong QR type

`Workspace` reject và yêu cầu scan đúng DCAM provisioning QR.

QR missing `serial_number`

`Workspace` reject; không mở manual serial input.

QR contains `serial_number`

`Workspace` hiển thị `serial_number` read-only.

Worker tries to edit serial

Không có UI hoặc action cho phép edit serial.

Owner name missing

`Workspace` block submit và hiển thị validation error.

Manufacture date default

Today được chọn mặc định và hiển thị current local date.

Manufacture date invalid

`Workspace` block submit và yêu cầu valid `YYYY-MM-DD`.

Submit provisioning success

`Workspace` hiển thị `dcam_cloud_device_id` và Created/Restored result.

Duplicate serial conflict

`Workspace` hiển thị support-required; không có override button.

Backend unavailable

`Workspace` hiển thị retry-safe message và không assume success.

Android ID fields present

Test fail nếu App đọc/gửi/log `ANDROID_ID` hoặc `android_id_hash`.

Manual serial input search

Test fail nếu UI có manual serial input.

Barcode serial scan search

Test fail nếu App có direct serial barcode scan flow.

## 22. Approved Implementation References and Remaining Inputs

Item

Status

Web technology stack

Approved in Implementation Design: Firebase Hosting + Web frontend.

Authentication mechanism

Approved in Implementation Design: Firebase Authentication.

Backend implementation

Approved in Implementation Design: Firebase Cloud Functions.

Backend storage

Approved: Firebase Cloud Firestore.

Supported browser/device for QR scan

TBD / Factory + QA + Implementation validation.

Camera permission fallback process

TBD / Factory + Implementation validation.

Worker account model: shared station or individual

TBD / Factory + Security.

Exact QR payload schema

TBD / API Contract.

QR signature/nonce/expiration/replay policy

TBD / API Contract + Security.

Exact backend endpoint/function naming

TBD / API Contract.

Exact owner source and validation

TBD / Product + Factory + API Contract.

Duplicate serial support process

TBD / Product + Support.

Printable/downloadable receipt

Not required unless Product/Factory approves later.

Offline provisioning queue

Not planned; factory network fallback remains TBD if required.

## 23. Practical Conclusion

DCAM Device Provisioning Web Portal App là single-purpose factory web app.
App chỉ có một user-facing account type: Factory Worker.
App chỉ có hai screens: Login và Workspace.
Factory Worker phải login trước khi vào Workspace.
Sau login thành công, mọi thao tác provisioning đều thực hiện và hiển thị trong Workspace.
Workspace scan QR hiển thị trên DCAM device.
QR payload chứa serial_number và device context.
Web Portal App không scan serial barcode trực tiếp.
Web Portal App không cho nhập serial_number thủ công.
serial_number là read-only sau QR scan.
owner_name và manufacture_date là required fields trong approved implementation baseline.
manufacture_date mặc định là ngày local hiện tại và có thể chọn ngày hợp lệ khác.
Workspace hiển thị inline review summary và submit action.
Backend dùng serial_number từ QR để create/restore devices/{dcam_cloud_device_id}.
Workspace hiển thị provisioning result và dcam_cloud_device_id khi success.
Web Portal App không set Device Owner.
Web Portal App không inject serial_number vào Android.
Web Portal App không verify Lock Task/kiosk baseline.
Web Portal App không chạy recording/storage/BDMA tests.
Web Portal App không mark PASS, FAIL, QUARANTINED hoặc READY_TO_SHIP.
Các phần API/schema thuộc DCAM Web Portal & Device API Contract.
Các phần implementation thuộc DCAM Device Provisioning Web Portal Implementation Design.
Các phần DSetup/serial injection thuộc DCAM DSetup Factory Tool Design.
Các phần factory acceptance thuộc Factory SOP / QA / Device POC.