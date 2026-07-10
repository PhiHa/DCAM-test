# DCAM Factory Provisioning & Device Production SOP

**Page ID**: 49545629  
**Version**: 12  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/49545629

---


# DCAM Factory Provisioning & Device Production SOP

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Factory SOP / Device Production Procedure

Version

Draft 1.1

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / QA Lead / Security Reviewer / Factory Lead / Support Lead / BDMA Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

05 - Release Management

Target Audience

Factory Operator, Factory Admin, QA, Android Developers, Tech Lead, Support, Security Reviewer, Release Manager

Last Updated

2026-07-09

Related Jira

Không có

Related Documents

DCAM Project Home, DCAM Architecture Home, DCAM QA Test Strategy & Test Matrix, DCAM Device POC & Hardware Validation Report, DCAM Web Portal & Device API Contract, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Device Provisioning Web Portal Design, DCAM Self Update Design, DCAM Security & Encryption Design, DCAM Android Operation Design, DCAM-BDMA Data Contract, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

## 1. Purpose

Tài liệu SOP này định nghĩa quy trình factory/admin để chuẩn bị, update hoặc rework một thiết bị BodyCamera cho DCAM pilot hoặc production deployment.

Current baseline của SOP dùng hai định danh chính và một recovery cache:

textSOP **không dùng** `ANDROID_ID`, `android_id_hash` hoặc `device_lookup` trong production baseline để giảm phức tạp và tránh giả định sai về Device Owner/app reinstall.

Mục tiêu provisioning chuẩn là chuyển một BodyCamera raw hoặc factory-reset thành thiết bị sẵn sàng chạy DCAM:

textSOP này định nghĩa 3 kịch bản vận hành chính:

textTài liệu này là factory procedure. Tài liệu này không định nghĩa lại Android runtime design, kiosk policy, provisioning API, security implementation hoặc update package validation logic.

## 2. Current Deployment Baseline

text### 2.1 Identity Model

Identity

Field

Meaning

Stability

Source / Usage

Hardware Identity

`serial_number`

Định danh vật lý của BodyCamera.

Stable across app update, app reinstall after rework, and factory reset.

Scan bằng barcode scanner qua DSetup hoặc recover từ SD Identity File. Dùng để create/restore `dcam_cloud_device_id`.

Cloud Identity

`dcam_cloud_device_id`

Primary device id trên Firebase.

Stable cloud identity.

Firebase tạo hoặc restore theo `serial_number`.

Recovery Cache

SD Identity File

File cache trên thẻ nhớ ngoài chứa `serial_number` để giảm thao tác scan barcode sau factory reset.

Có thể tồn tại qua factory reset nếu thẻ nhớ không bị format/xóa.

DSetup đọc để recover serial. DCAM ghi/đồng bộ từ app-private serial ra SD card khi app đang hoạt động.

Rules:

text### 2.2 SD Identity File as Recovery Cache

BodyCamera sử dụng thẻ nhớ làm bộ nhớ ngoài. Dựa trên baseline này, DCAM và DSetup có thể dùng SD Identity File để giảm thao tác scan barcode sau factory reset.

Recommended location:

text/DCAM_FACTORY/device_identity.json]]>Recommended payload:

jsonSerial source priority:

textDCAM runtime synchronization rules:

textDSetup SD recovery rules:

textImportant warning:

textRecovery matrix:

Situation

Primary Source

Expected Behavior

Normal operation

DCAM app-private serial_number

DCAM chạy bình thường và sync SD Identity File nếu cần.

SD card identity file deleted

DCAM app-private serial_number

DCAM tạo lại SD Identity File.

SD card replaced while DCAM still running

DCAM app-private serial_number

DCAM tạo SD Identity File trên thẻ mới.

Factory reset, SD Identity File still exists

SD Identity File

DSetup recover serial_number, không cần scan barcode.

Factory reset + SD card missing/deleted/replaced without identity file

Barcode scanner

DSetup yêu cầu scan barcode lại.

SD Identity File conflicts with app-private serial

App-private serial wins

DCAM overwrite SD file or raise warning.

SD Identity File conflicts with manifest/Firebase

No automatic trust

DSetup stops and requires operator/support resolution.

### 2.3 DSetup Operating Scenarios

SOP chỉ giữ 3 kịch bản vận hành chính.

Scenario

When Used

DSetup Behavior

Expected Result

Provisioning from factory reset / clean device

Thiết bị mới, raw, factory-reset hoặc đã được clean management state.

DSetup tries SD Identity File &rarr; if invalid/missing, asks barcode scan &rarr; install APK &rarr; set Device Owner &rarr; verify Device Owner &rarr; inject serial &rarr; launch DCAM &rarr; Firebase create/restore by `serial_number`.

Device được provisioned thành production-ready DCAM device.

Approved DCAM APK update while DCAM remains Device Owner

DCAM vẫn là Device Owner; cần update APK/version theo approved package.

DSetup hoặc approved update path verifies current Device Owner is DCAM &rarr; installs/updates approved APK &rarr; verifies Device Owner still intact &rarr; verifies serial/cloud identity still valid &rarr; DCAM syncs SD Identity File if needed.

App được update mà không mất Device Owner, không cần factory reset, không tạo cloud device mới.

Rework / dirty state / Device Owner conflict handling

Thiết bị có dữ liệu/policy/app cũ, Device Owner khác, Device Owner conflict, hoặc state không sạch.

DSetup detects state. Nếu DCAM còn là Device Owner thì xử lý theo approved update/rework path. Nếu không có Device Owner và state sạch thì có thể set Device Owner. Nếu có Device Owner khác hoặc state không hợp lệ thì stop và yêu cầu Factory Reset Required. Sau factory reset, DSetup có thể recover serial từ SD Identity File nếu hợp lệ.

Không cố bypass Android security. Dirty/conflict state được quarantine hoặc factory reset trước khi provisioning lại.

Rules:

text### 2.4 DSetup Baseline

DSetup là factory provisioning tool dùng cho single-device production flow.

textDSetup is **single-device scan mode only** for current baseline:

textFactory Device Owner baseline:

text/
7. DSetup verifies bằng dumpsys device_policy và DCAM isDeviceOwnerApp() nếu available.
8. DSetup injects serial_number into DCAM bằng ADB factory broadcast.
9. DSetup launches DCAM.
10. DCAM imports and validates serial_number.
11. DCAM syncs serial_number to SD Identity File.
12. DCAM tự cấu hình factory Wi-Fi bằng SSID/password hardcoded trong approved APK.
13. Verify Wi-Fi connection và Internet connectivity tới Firebase.
14. Chạy DCAM Firebase business provisioning/recovery by serial_number.
15. Firebase create/restore dcam_cloud_device_id theo serial_number.
16. Apply Lock Task / User Restrictions.]]>Important boundary:

text## 3. Scope

### 3.1 In Scope

Area

Description

Device intake

Nhận và kiểm tra BodyCamera device trước provisioning.

Clean device state

Factory reset / clean account state / xác nhận không còn unmanaged account trước khi set Device Owner.

DSetup factory tool

DSetup single-device scan mode với barcode scanner để inspect state, recover serial từ SD Identity File nếu có, install/update APK, set/verify Device Owner khi hợp lệ, inject serial và launch DCAM.

SD Identity File recovery cache

DCAM sync serial_number ra thẻ nhớ; DSetup đọc file này để recover serial sau factory reset nếu hợp lệ.

APK installation/update

Chỉ cài hoặc update approved DCAM APK build thông qua DSetup/ADB hoặc approved update path.

Device Owner / DPC setup

Set DCAM hoặc approved local component thành Device Owner bằng ADB `dpm set-device-owner` khi device state hợp lệ.

Device Owner state inspection

DSetup kiểm tra DCAM Device Owner, no Device Owner, Device Owner khác, dirty state hoặc conflict.

Serial injection

DSetup inject `serial_number` vào DCAM bằng ADB factory broadcast sau khi Device Owner được verify.

Device identity capture

Recover serial từ SD Identity File hoặc scan barcode và verify `serial_number` làm Hardware Identity trước Firebase business provisioning.

Rework handling

DSetup phát hiện dirty/conflict state và yêu cầu factory reset nếu Android không cho phép set Device Owner.

Factory Wi-Fi configuration

DCAM tự cấu hình factory Wi-Fi sau khi serial được import/validate, dùng SSID/password hardcoded trong approved APK.

DCAM business provisioning/recovery

Provision/recover device qua Firebase flow, create/restore `dcam_cloud_device_id` theo `serial_number`.

Device information

Gán/verify `serial_number`, `owner_name` và `manufacture_date`.

Kiosk policy

Verify Lock Task allowlist, User Restrictions và Home/Launcher behavior nếu required.

In-app console check

Verify Record/Live default screen, Setting hub, read-only File/Media và Admin/Maintenance access.

Maintenance Password Gate

Verify controlled kiosk exit yêu cầu maintenance credential theo policy.

Recording/storage check

Verify normal recording, finalized media và storage dashboard behavior.

BDMA readiness check

Verify approved ADB/media import/user sync boundary nếu factory test yêu cầu.

Self Update capability check

Verify Self Update path/capability và safe defer behavior theo factory acceptance.

Optional Play Store fallback check

Chỉ chạy nếu device có GMS/Play Store và Product/Security approve fallback.

Factory acceptance

Chạy required checklist trước khi mark device ready to ship.

Production record

Ghi nhận safe production metadata, serial source và test/rework result.

### 3.2 Out of Scope

Area

Reason / Owner

Full BDMA UI testing

Thuộc BDMA QA scope. Factory chỉ validate DCAM-facing import readiness.

Customer field operation

Thuộc Operations & Support Runbook.

Security certification

Thuộc Security Review / Security Implementation Decisions.

Cloud production SLA

Thuộc Firebase/cloud operations.

External EMM / Managed Google Play flow

Not applicable (per ADR).

QR/NFC Android Device Owner provisioning flow

Không dùng cho SOP hiện tại; Device Owner setup đã chốt bằng ADB.

Manual Wi-Fi entry by Factory Operator

Không áp dụng cho current baseline; Wi-Fi do DCAM tự cấu hình từ approved APK.

Multi-device parallel provisioning

Không thuộc current DSetup baseline; current baseline chỉ single-device scan mode.

IMEI / Wi-Fi MAC / Bluetooth MAC as app-readable identity

Không dùng cho current baseline vì DCAM là user app Device Owner, không phải privileged/system app.

ANDROID_ID / android_id_hash production identity

Không dùng cho SOP baseline.

SD Identity File as authoritative hardware identity

Không áp dụng; file này chỉ là recovery cache.

DSetup bypass/removal of third-party Device Owner

Không thể/không được làm; nếu có Device Owner khác hoặc state conflict thì yêu cầu factory reset/rework.

Exact API contract

Thuộc DCAM Web Portal & Device API Contract / Device Provisioning Web Portal Design.

Exact APK validation implementation

Thuộc DCAM Self Update Design + Security Design.

## 4. Source-of-truth References

Topic

Source of Truth

Project baseline and documentation tree

DCAM Project Home

Architecture baseline

DCAM Architecture Home

Device Owner / Lock Task / User Restrictions / Maintenance Mode

DCAM Android Device Owner & Kiosk Policy Design

In-app console, Setting hub, Maintenance Password Gate and controlled fallback UX

DCAM In-App Operation, Device Settings & Media Console Design

API/data contract for Web Portal, device lookup, config, update and factory record

DCAM Web Portal & Device API Contract

DCAM business provisioning QR Flow

DCAM Device Provisioning Web Portal Design

Self Update / APK update

DCAM Self Update Design

Security, credential, maintenance gate and logging constraints

DCAM Security & Encryption Design

Android startup/runtime orchestration

DCAM Android Operation Design

Storage/media/BDMA contract

DCAM-BDMA Data Contract

QA acceptance and release readiness

DCAM QA Test Strategy & Test Matrix

Real-device hardware/firmware evidence

DCAM Device POC & Hardware Validation Report

## 5. Roles and Responsibilities

Role

Responsibility

Factory Operator

Factory reset/clean device state, kết nối đúng một thiết bị, scan barcode serial bằng DSetup khi SD recovery không dùng được, theo dõi pass/fail và ghi nhận production evidence.

Factory Admin

Thực hiện Firebase business provisioning/recovery thông qua approved admin flow, gán device information và approve factory account usage.

QA

Chạy factory acceptance/rework checklist, verify SD recovery behavior nếu applicable và mark pass/fail.

Android Developer / Tech Lead

Hỗ trợ lỗi DSetup, Device Owner, ADB command, serial injection, SD Identity File sync/recovery, DCAM factory Wi-Fi configuration, provisioning/rework flow, Lock Task, Self Update, firmware hoặc app-level failures.

Security Reviewer

Approve maintenance credential handling, Google account fallback policy, signing/update constraints, hardcoded factory Wi-Fi credential policy, SD Identity File validation/signature policy, DSetup behavior và identity/logging constraints.

Release Manager

Approve APK build, artifact/checksum/signature metadata, factory Wi-Fi config included in APK, DSetup-compatible factory receiver, SD Identity File support và production release package.

Support Lead

Định nghĩa handover, diagnostic, SD recovery, rework/factory reset recovery và post-shipment support readiness.

BDMA Lead

Validate BDMA import/user sync boundary nếu factory workflow bao gồm BDMA check.

## 6. Required Inputs

Input

Required

Notes

Target BodyCamera model

Có

Phải khớp device model được supported/POC-approved.

Approved firmware version

Có

Firmware phải được ghi nhận và được chấp nhận bởi Device POC/release baseline.

Approved DCAM APK

Có

Chỉ dùng release-approved APK. APK phải chứa approved factory Wi-Fi SSID/password hardcoded, DSetup-compatible factory serial receiver và SD Identity File sync support cho current baseline.

APK `versionName` / `versionCode`

Có

Phải khớp release record.

APK checksum

Có

Dùng cho factory validation.

APK signing certificate fingerprint

Có

Dùng để xác nhận trusted build.

DSetup

Có

Factory provisioning/update/rework tool chạy single-device scan mode.

Barcode scanner

Có

USB barcode scanner dùng để scan `serial_number` khi SD Identity File không có hoặc không hợp lệ.

BodyCamera SD card / external storage

Có

Dùng làm nơi lưu SD Identity File recovery cache.

Factory PC

Có

Chạy DSetup, ADB, APK install/update, Device Owner setup và BDMA checks nếu applicable.

ADB tool

Có

Required để install/update APK, run `dpm set-device-owner`, verify `dumpsys device_policy`, inject serial và launch DCAM.

Approved Device Owner component name

Có

Ví dụ format: `<dcam_package>/<DcamDeviceAdminReceiver>`. Exact value do Android Lead/Release record cung cấp.

Approved factory serial broadcast action/component

Có

Ví dụ: action/component do Android Lead cung cấp. Chỉ dùng trong factory/provisioning state.

Approved SD Identity File path/format

Có

Ví dụ `<SD_CARD>/DCAM_FACTORY/device_identity.json`. Exact path/schema do Android Lead/Security approve.

Factory Wi-Fi/network

Conditional

Required cho Firebase business provisioning/update/rework checks. SSID/password nằm trong approved DCAM APK; Factory Operator không nhập thủ công.

Serial number source

Có

SD Identity File, barcode/device label/factory record. Serial là Hardware Identity.

Firebase provisioning/recovery access

Có

Cần approved Factory Admin account/process cho business provisioning/recovery trên Firebase.

Factory/Admin account

Có

Không được dùng personal account.

Maintenance credential policy

Có

Initial setup/reset policy phải được Security/Product approve.

Owner/customer information

Conditional

Required nếu device được assign trước shipment.

Manufacture date source

Có

Factory record, vendor record hoặc approved admin input.

Approved maintenance/factory Google account

Conditional

Chỉ dùng nếu manual Play Store fallback được enable. Không dùng personal Google account.

## 7. Device Production States

State

Meaning

`RAW_DEVICE`

Device đã nhận nhưng chưa chuẩn bị.

`INSPECTED`

Đã hoàn tất kiểm tra physical/model/firmware/basic hardware.

`FACTORY_RESET_DONE`

Device sạch và không còn unmanaged personal account.

`DSETUP_RUNNING`

DSetup đang chạy single-device scan mode cho device hiện tại.

`SD_IDENTITY_RECOVERED`

DSetup đã recover `serial_number` từ SD Identity File hợp lệ.

`SD_IDENTITY_SYNCED`

DCAM đã ghi/đồng bộ `serial_number` ra SD Identity File.

`APK_INSTALLED`

Approved DCAM APK đã được install.

`APK_UPDATED`

Approved DCAM APK đã được update khi DCAM vẫn là Device Owner.

`DEVICE_OWNER_SET`

DCAM/local DPC Device Owner state đã được set bằng ADB và verify thành công.

`DEVICE_OWNER_VERIFIED`

DSetup/DCAM xác nhận DCAM là Device Owner.

`FACTORY_RESET_REQUIRED`

DSetup phát hiện dirty/conflict state hoặc Device Owner khác; cần factory reset/rework trước khi provision.

`SERIAL_INJECTED`

DSetup đã inject `serial_number` vào DCAM bằng ADB factory broadcast.

`SERIAL_VERIFIED`

DCAM đã import/validate `serial_number` và sẵn sàng dùng làm Hardware Identity.

`FACTORY_WIFI_CONFIGURED`

DCAM đã cấu hình factory Wi-Fi bằng SSID/password hardcoded trong approved APK và connectivity đã được verify.

`BUSINESS_PROVISIONED`

DCAM Firebase business provisioning hoàn tất, `dcam_cloud_device_id` available và `serial_lookup` đã được cập nhật.

`KIOSK_POLICY_APPLIED`

Lock Task/User Restrictions/Home behavior đã được verify theo policy.

`FACTORY_TEST_PASSED`

Required factory acceptance checklist đã pass.

`READY_TO_SHIP`

Device được approve để packaging/shipment.

`QUARANTINED`

Device fail required step và không được shipment.

## 8. End-to-end Factory Flow

text/
    ↓
DSetup verifies Device Owner by dumpsys device_policy and optionally DCAM isDeviceOwnerApp()
    ↓
DSetup injects serial_number by ADB factory broadcast
    ↓
DSetup launches DCAM
    ↓
DCAM imports serial_number into app-private storage and validates it as Hardware Identity
    ↓
DCAM syncs serial_number to SD Identity File
    ↓
DCAM configures factory Wi-Fi using SSID/password hardcoded in approved APK
    ↓
Verify Wi-Fi connection and Firebase connectivity
    ↓
Firebase lookup/create/restore:
    serial_lookup/{serial_number} -> dcam_cloud_device_id
    devices/{dcam_cloud_device_id}
    ↓
Assign / verify owner_name and manufacture_date
    ↓
Verify dcam_cloud_device_id and serial_lookup mapping
    ↓
Apply / verify Lock Task and User Restrictions
    ↓
Verify Record / Live View default screen
    ↓
Verify Setting hub and read-only media console
    ↓
Verify Maintenance Password Gate
    ↓
Verify recording / storage / finalized media
    ↓
Verify BDMA readiness / ADB boundary if required
    ↓
Verify Self Update capability / update safety checks
    ↓
Run factory acceptance checklist
    ↓
DSetup / Factory Admin records PASS or QUARANTINED
    ↓
Mark READY_TO_SHIP or QUARANTINED]]>## 9. Detailed SOP Steps

### 9.1 Receive and Inspect Device

Step

Expected Result

Check device model.

Model khớp approved target list.

Check firmware/build version.

Firmware được ghi nhận và được chấp nhận bởi Device POC/release baseline.

Check screen, physical buttons, camera lens, USB port and casing.

Không có lỗi vật lý rõ ràng.

Check battery/charging behavior.

Device có thể charge và boot.

Check storage availability.

Storage available và writable sau app setup.

Check SD card presence if required by current batch.

SD card available nếu factory flow yêu cầu SD Identity File recovery cache.

Failure rule:

text### 9.2 Clean Device State

Step

Expected Result

Factory reset or clean device according to approved factory procedure when provisioning/rework requires it.

Device bắt đầu từ clean state phù hợp để set Device Owner bằng ADB.

Confirm no unmanaged personal Google/account remains.

Không còn personal account.

Confirm no external EMM is enrolled.

Device tuân theo no-external-EMM baseline.

Confirm no existing non-DCAM Device Owner conflicts.

Không có Device Owner cũ hoặc policy owner không thuộc DCAM.

Confirm USB/ADB mode is available only as approved for factory.

Factory có thể tiếp tục chạy ADB mà không enable unrestricted field behavior.

Confirm device is not treated as normal already-in-use device for Device Owner setup.

Device ở clean/factory setup state hoặc firmware factory state cho phép `dpm set-device-owner`.

Confirm SD card is not intentionally formatted unless approved.

SD Identity File có thể tồn tại qua factory reset nếu thẻ không bị xóa/format.

Failure rule:

text### 9.3 Run DSetup Single-device Scan Mode

DSetup là tool factory chính cho current baseline.

Step

Expected Result

Connect exactly one BodyCamera to Factory PC.

Chỉ có một ADB device được phát hiện.

Open DSetup.

DSetup vào trạng thái `WAITING_FOR_DEVICE` hoặc `ADB_DEVICE_CONNECTED`.

DSetup checks ADB device count.

Exactly one device connected.

DSetup inspects current Device Owner / policy state.

Tool phân loại scenario: clean provisioning, DCAM Device Owner update, hoặc dirty/conflict state.

If serial is required, DSetup checks SD Identity File first.

Nếu file hợp lệ, serial được recovered.

If SD recovery fails, operator scans serial barcode.

Barcode scanner nhập `serial_number` vào DSetup.

DSetup validates serial format and source.

Serial đúng format/rule của factory.

DSetup cross-checks manifest if loaded.

Nếu có CSV/manifest, serial tồn tại trong manifest và chưa used hoặc allowed for rework.

DSetup starts provisioning/update/rework run.

Tool khóa serial + device cho run hiện tại nếu serial is required.

DSetup state machine:

textRules:

text### 9.4 DSetup Device Owner State Handling

Detected State

DSetup Action

Expected Result

No Device Owner and clean/factory state

Continue provisioning and run `dpm set-device-owner`.

DCAM becomes Device Owner.

DCAM is already Device Owner

Continue approved update/rework path; do not re-run `dpm set-device-owner` unnecessarily.

Device Owner remains DCAM.

Another app is Device Owner

Stop. Report `FACTORY_RESET_REQUIRED` or `QUARANTINED`.

No attempt to bypass Android security.

Device policy state is dirty/unknown/conflicting

Stop. Report `FACTORY_RESET_REQUIRED` or escalate.

Device is not provisioned until state is clean.

ADB unauthorized/offline/multiple devices

Stop. Operator must correct connection/device state.

No accidental provisioning on wrong device.

Rules:

text### 9.5 Install or Update Approved DCAM APK

Step

Expected Result

DSetup verifies APK file name, versionName and versionCode.

Khớp release-approved record.

DSetup verifies checksum.

Khớp release-approved checksum.

DSetup verifies signing certificate fingerprint where supported.

Khớp trusted signing certificate.

DSetup confirms APK contains approved factory Wi-Fi configuration.

Release record xác nhận SSID/password hardcoded trong APK theo current baseline.

DSetup confirms APK contains DSetup-compatible factory serial receiver.

Receiver/action khớp release-approved factory provisioning contract.

DSetup confirms APK contains SD Identity File sync support if required.

APK hỗ trợ tạo/cập nhật SD Identity File từ app-private serial.

DSetup installs or updates APK using ADB/approved update method.

Install/update thành công.

DSetup verifies package and `DcamDeviceAdminReceiver` component name.

Package/component đúng với release record để dùng trong `dpm set-device-owner` nếu cần.

If update scenario, verify Device Owner remains DCAM after APK update.

Update không làm mất Device Owner/policy state.

Rules:

text### 9.6 Set Device Owner / Local DPC State

Device Owner được provision bằng ADB thông qua DSetup khi device state hợp lệ.

text
Step

Expected Result

DSetup confirms device is clean/factory-reset and ready for Device Owner setup.

Device không còn account/management state cũ gây conflict.

DSetup confirms approved DCAM APK is installed.

DCAM package tồn tại trên device.

DSetup confirms approved DeviceAdminReceiver component.

Component name khớp release/factory record.

DSetup runs ADB command if required: `adb shell dpm set-device-owner <dcam_package>/<DcamDeviceAdminReceiver>`.

Android accepts command and sets DCAM/local DPC as Device Owner.

DSetup verifies with `dumpsys device_policy`.

Output thể hiện DCAM/local DPC là Device Owner.

DSetup optionally verifies in DCAM with `isDeviceOwnerApp()` if app check is available.

App runtime xác nhận Device Owner state là true.

DSetup records setup method and result.

Production record ghi rõ ADB Device Owner setup result.

Reboot and verify policy state if required.

Device Owner state còn hiệu lực sau reboot.

Rules:

text### 9.7 Inject and Verify Serial Number by DSetup

Sau khi Device Owner được verify thành công, DSetup inject `serial_number` vào DCAM bằng ADB factory broadcast. Serial có thể đến từ SD Identity File hoặc barcode scanner.

Suggested factory broadcast pattern:

text \
  -n / \
  --es serial_number ]]>
Step

Expected Result

DSetup uses resolved serial_number from current run.

Serial source là `SD_IDENTITY_FILE`, `BARCODE_SCAN`, hoặc approved fallback.

DSetup sends ADB factory broadcast to DCAM.

Broadcast được gửi tới approved factory receiver/action.

DCAM verifies Device Owner state before accepting serial.

DCAM chỉ nhận serial khi app là Device Owner.

DCAM verifies device is not business-provisioned, unless approved rework mode exists.

Không overwrite serial của device đã provisioned nếu không có approved rework mode.

DCAM validates serial format.

Serial hợp lệ theo factory rule.

DCAM stores serial in app-private storage.

Serial được import làm Hardware Identity.

DCAM returns/records import result if supported.

DSetup biết pass/fail hoặc operator verify trên UI.

DCAM prevents repeated normal-field mutation.

User thường không đổi được serial sau provisioning.

Rules:

text### 9.8 Sync SD Identity File from DCAM

Sau khi DCAM có `serial_number` hợp lệ trong app-private storage, DCAM sẽ sync serial ra SD Identity File để hỗ trợ recovery sau factory reset.

Step

Expected Result

DCAM checks app-private serial_number.

Serial tồn tại và đã pass validation.

DCAM checks SD card availability.

SD card available nếu device/batch yêu cầu recovery cache.

DCAM reads existing SD Identity File if present.

File được parse và validate nếu tồn tại.

If file missing, DCAM creates it.

SD Identity File được tạo với serial hiện tại.

If file matches app-private serial, DCAM may refresh timestamp/signature.

File vẫn hợp lệ.

If file differs from app-private serial, app-private serial wins.

DCAM overwrite file bằng app-private serial hoặc raise warning theo policy.

If SD card is replaced, DCAM creates file on new SD card.

Thẻ mới có recovery cache đúng serial của device hiện tại.

DCAM records sync result if policy allows.

Không ghi secret; chỉ ghi pass/fail/source.

Rules:

text### 9.9 Configure Factory Wi-Fi from DCAM App

Sau khi serial đã được import/validate, DCAM sẽ tự động cấu hình factory Wi-Fi bằng SSID/password được hardcoded trong approved DCAM APK nếu Wi-Fi chưa available.

Step

Expected Result

DSetup launches DCAM or operator opens DCAM.

DCAM starts factory/provisioning/rework flow.

DCAM confirms imported serial_number exists.

Hardware Identity available.

DCAM syncs SD Identity File if applicable.

Recovery cache được tạo/cập nhật nếu SD card available.

DCAM loads built-in Wi-Fi configuration.

SSID/password được đọc từ cấu hình hardcoded trong approved APK.

DCAM creates or updates Wi-Fi configuration.

Factory Wi-Fi configuration được apply thành công trên device.

DCAM enables/selects the configured network.

Device bắt đầu connect tới factory Wi-Fi.

Verify Wi-Fi connection status.

Device nhận IP và trạng thái Connected.

Verify Internet connectivity.

Device có thể truy cập Firebase để tiếp tục business provisioning/recovery.

Record Wi-Fi setup result.

Production record chỉ ghi pass/fail và optional SSID reference nếu được approve; không ghi password.

Rules:

text### 9.10 DCAM Firebase Business Provisioning and Recovery

QR trong bước này **chỉ dùng cho DCAM business provisioning trên Firebase nếu flow/UI yêu cầu**.

Step

Expected Result

Open DCAM provisioning/recovery screen if device is not business-provisioned locally.

DCAM hiển thị provisioning-required/recovery flow, local QR hoặc approved provisioning UI.

DCAM uses imported `serial_number`.

Firebase nhận Hardware Identity của BodyCamera.

Firebase checks `serial_lookup/{serial_number}`.

Nếu serial đã tồn tại, restore existing `dcam_cloud_device_id`; nếu chưa tồn tại, create new `dcam_cloud_device_id`.

Firebase updates `devices/{dcam_cloud_device_id}`.

Device record contains serial, owner/manufacture info if available, and provisioning/recovery state.

Enter/select `owner_name` if required.

Owner information được lưu nếu required.

Enter/select `manufacture_date`.

Date được lưu theo format `YYYY-MM-DD`.

Wait for DCAM to receive provisioning/recovery result.

Device receives `dcam_cloud_device_id`.

Verify `serial_lookup` mapping.

Hardware Identity mapping trỏ đúng `dcam_cloud_device_id`.

Verify device state.

Device trở thành ACTIVE nếu runtime/policy guards cho phép.

Rules:

textSuggested Firebase mapping:

text dcam_cloud_device_id
devices/{dcam_cloud_device_id}]]>Rework / factory reset recovery behavior:

text### 9.11 Apply and Verify Kiosk Policy

Step

Expected Result

Verify Lock Task package allowlist.

DCAM package được allowlisted.

Enter Lock Task Mode.

DCAM vẫn ở kiosk experience.

Verify Home/Recents/Back behavior.

User không thể thoát khỏi approved DCAM flow.

Apply/verify approved User Restrictions.

Restrictions được apply hoặc unsupported restrictions được document.

Reboot and verify Lock Task/policy recovery.

DCAM restore kiosk policy khi có thể.

Rules:

text### 9.12 In-app Console Verification

Check

Expected Result

Successful login/session flow.

App mở `Record / Live View`.

Back on Record / Live View.

Mở `Setting`.

Back on Setting.

Quay lại `Record / Live View`.

Back inside child module.

Quay lại `Setting`.

Media / Files.

Read-only; không delete/edit/mark/export/share.

Storage dashboard.

Hiển thị storage state mà không dùng unrestricted file manager.

Login Settings.

Chỉ available theo role/security/capability policy.

User Settings.

Admin-only. Operator không thể access.

Admin / Maintenance.

Yêu cầu Maintenance Password Gate trước controlled exit.

Future modules.

Server/Live/PTT/AI controls hidden/disabled cho đến khi có approved design.

### 9.13 Recording and Storage Verification

Step

Expected Result

Login as test operator.

Operator session active.

Start short test recording.

Recording start.

Stop recording.

Recording finalizes safely.

Verify finalized media appears.

Media chỉ xuất hiện sau finalization.

Verify temp/in-progress file visibility.

Temp/in-progress không hiển thị như final media.

Verify media viewer behavior.

Read-only viewer; không có mutation actions.

Verify storage dashboard.

Used/free/remaining state update hoặc refresh.

Failure rule:

text### 9.14 BDMA Readiness Verification

Step

Expected Result

Connect device to BDMA/factory PC using approved method.

Device visible theo approved ADB/USB boundary.

Verify finalized media discovery.

Finalized media có thể được discover/import bởi BDMA test flow.

Verify `.mp4` / `.md5` behavior if test media exists.

MD5 behavior tuân theo DCAM-BDMA Data Contract.

Verify user sync readiness if included in factory test.

User sync path hoạt động hoặc được document là chưa thuộc current factory check.

Verify restrictions do not silently break BDMA path.

BDMA boundary được preserve.

Failure rule:

text### 9.15 Self Update Capability Verification

Step

Expected Result

Verify update settings and Self Update capability.

Self Update path available hoặc blocked reason được document.

Verify update manifest endpoint or artifact source in test environment.

Manifest/source reachable nếu test yêu cầu online update check.

Verify invalid package rejection in test environment if available.

Invalid package bị reject.

Verify update defer during recording if test scripted.

Update bị deferred khi recording/finalizing/emergency.

Verify post-update policy restore if update test is executed.

DCAM quay lại kiosk policy sau update/restart.

Rules:

text### 9.16 Optional Manual Google Play Store Fallback Verification

Only run this section if all conditions are true:

text
Step

Expected Result

Enter Admin / Maintenance.

Maintenance Password Gate is required.

Authenticate with approved maintenance credential.

Controlled Maintenance Mode starts.

Open Google Play Store target if enabled.

Chỉ approved update target có thể reachable.

Sign in only with approved maintenance/factory account if required.

Không dùng personal Google account.

Return to DCAM.

DCAM resume controlled maintenance flow.

Restore kiosk policy.

Lock Task/User Restrictions được restore.

Failure rule:

text## 10. Factory Acceptance Checklist

Check

Expected Result

Required

Device model/firmware

Approved model và firmware được ghi nhận.

Có

SD card availability

SD card available nếu batch/release yêu cầu SD Identity File recovery cache.

Conditional

DSetup single-device mode

Exactly one ADB device detected.

Có

DSetup scenario classification

Clean provisioning, DCAM Device Owner update, or Factory Reset Required được xác định đúng.

Có

Factory reset / clean state

Device được clean trước khi set Device Owner nếu provisioning/rework yêu cầu.

Có

SD Identity File recovery

DSetup đọc/recover serial từ SD Identity File nếu file hợp lệ.

Conditional

Barcode scan

Serial được scan bằng barcode scanner nếu SD recovery không dùng được.

Conditional

Serial source recorded

Production record ghi `SD_IDENTITY_FILE`, `BARCODE_SCAN`, hoặc approved fallback.

Có

Serial number validation

Serial đúng format và khớp device label/factory record/manifest nếu applicable.

Có

APK installed/updated

Approved DCAM APK version đã được install/update bởi DSetup hoặc approved update path.

Có

APK checksum/signing

Khớp approved release record.

Có

APK contains factory Wi-Fi config

Approved APK chứa SSID/password hardcoded theo current baseline.

Có

APK contains factory serial receiver

Approved APK hỗ trợ DSetup serial injection receiver/action.

Có

APK supports SD Identity File sync

Approved APK hỗ trợ sync serial ra SD Identity File nếu feature bật.

Conditional

DeviceAdminReceiver component

Component khớp release/factory record.

Có

Device Owner setup method

ADB `dpm set-device-owner` được DSetup thực hiện khi required.

Conditional

Device Owner verification

`dumpsys device_policy` và app runtime confirm DCAM Device Owner nếu available.

Có

Serial injection

DSetup inject serial bằng ADB factory broadcast nếu provisioning/rework required.

Conditional

Serial import

DCAM import/validate serial vào app-private storage.

Conditional

SD Identity File sync

DCAM tạo/cập nhật SD Identity File từ app-private serial nếu SD available.

Conditional

Identity model

`serial_number` dùng làm Hardware Identity; `dcam_cloud_device_id` dùng làm Cloud Identity; SD Identity File là recovery cache.

Có

No ANDROID_ID dependency

SOP không phụ thuộc `ANDROID_ID`, `android_id_hash` hoặc `device_lookup`.

Có

DCAM factory Wi-Fi configuration

DCAM tự cấu hình Wi-Fi sau serial import/validation nếu cần.

Có

Wi-Fi connectivity

Device connected và có thể truy cập Firebase nếu provisioning/rework cần network.

Có

No external EMM

Device không phụ thuộc external EMM/Android Management API.

Có

Firebase provisioning/recovery

`dcam_cloud_device_id` được create/restore qua Firebase by `serial_number`.

Có

Serial lookup

`serial_lookup/{serial_number}` trỏ đúng `dcam_cloud_device_id`.

Có

QR boundary

QR chỉ dùng cho business provisioning nếu flow yêu cầu, không dùng để set Device Owner.

Có

Owner name

Được store nếu required.

Conditional

Manufacture date

Được store theo format `YYYY-MM-DD`.

Có

Lock Task

Active/recoverable.

Có

User Restrictions

Applied hoặc unsupported items được document.

Có

No unrestricted Android escape

Đã verify.

Có

Record / Live View

Default screen sau login/session restore.

Có

Setting hub

Accessible và back behavior hoạt động.

Có

File/Media

Read-only.

Có

Maintenance Password Gate

Required cho controlled kiosk exit.

Có

Recording

Short test recording thành công.

Có

Storage finalization

Finalized media chỉ visible sau finalization.

Có

BDMA readiness

Hoạt động nếu required bởi release baseline.

Conditional

Self Update capability

Verified hoặc limitation được document.

Có

Play Store fallback

Disabled hoặc verified dưới controlled process.

Conditional

Factory record

Completed và attached vào device/batch record.

Có

## 11. Failure Handling

Failure

Required Action

Unsupported model/firmware

Mark `QUARANTINED`.

Cannot factory reset / clean account or management state when required

Mark `QUARANTINED`.

DSetup detects zero or multiple ADB devices

Stop run; do not provision until exactly one device is connected.

Device Owner state cannot be determined

Stop run and escalate.

Device Owner is another app

Stop run; mark `FACTORY_RESET_REQUIRED` / `QUARANTINED`.

Device policy state is dirty/conflicting

Stop run; mark `FACTORY_RESET_REQUIRED` / `QUARANTINED`.

SD Identity File missing when expected

Fall back to barcode scan if approved; otherwise mark `QUARANTINED`.

SD Identity File malformed/invalid signature/checksum

Do not use file; fall back to barcode scan or mark `QUARANTINED`.

SD Identity File conflicts with app-private serial

App-private serial wins; overwrite SD file or raise warning according to policy.

SD Identity File conflicts with manifest/Firebase/label

Stop run and escalate; do not auto-provision.

Barcode scan missing, malformed or unreadable when required

Stop run; retry scan or use approved manual fallback.

Serial duplicate or manifest mismatch

Mark `QUARANTINED` until serial source is verified.

APK checksum/signature/version mismatch

Mark `QUARANTINED`.

APK missing approved hardcoded factory Wi-Fi configuration

Mark `QUARANTINED`.

APK missing approved DSetup factory serial receiver/action

Mark `QUARANTINED`.

APK missing SD Identity File sync support when required

Mark `QUARANTINED` or disable SD recovery for that release according to approval.

DeviceAdminReceiver component mismatch

Mark `QUARANTINED`.

ADB `dpm set-device-owner` fails when Device Owner is required

Mark `QUARANTINED` or `FACTORY_RESET_REQUIRED` trừ khi có approved fallback/ADR.

Device Owner cannot be verified by `dumpsys device_policy` or app runtime

Mark `QUARANTINED`.

APK update causes DCAM Device Owner state to be lost

Mark `QUARANTINED` and escalate.

DSetup serial injection fails

Mark `QUARANTINED` hoặc retry theo approved factory/support procedure.

DCAM rejects injected serial

Mark `QUARANTINED` cho đến khi serial/input/app state được xác minh.

DCAM cannot sync SD Identity File

Continue only if SD recovery is optional; otherwise mark `QUARANTINED` or record limitation according to release policy.

DCAM cannot apply factory Wi-Fi configuration after serial import

Mark `QUARANTINED` hoặc retry theo approved factory/support procedure.

Wi-Fi cannot connect or cannot reach Firebase when network is required

Mark `QUARANTINED` hoặc retry theo approved factory/support procedure.

Firebase serial lookup conflict

Mark `QUARANTINED` and escalate Factory Admin / Web/API Lead / Support Lead.

Firebase business provisioning/recovery fails

Mark `QUARANTINED` hoặc retry only according to support procedure.

`dcam_cloud_device_id` missing after business provisioning/recovery

Mark `QUARANTINED`.

`serial_lookup/{serial_number}` does not map to expected `dcam_cloud_device_id`

Mark `QUARANTINED`.

QR flow is used incorrectly as Device Owner setup

Stop SOP, correct process, and repeat Device Owner setup by ADB/DSetup if state allows; otherwise factory reset required.

Lock Task cannot start/recover

Mark `QUARANTINED`.

Required User Restrictions cannot be applied

Security/Product review; mark `QUARANTINED` cho đến khi được accepted.

User can escape to unrestricted Android

Mark `QUARANTINED`.

Recording/finalization fails

Mark `QUARANTINED`.

BDMA import fails when required

Mark `QUARANTINED`.

Maintenance Password Gate fails open

Mark `QUARANTINED`.

Self Update validation/install breaks kiosk policy

Mark `QUARANTINED` và escalate.

Play Store fallback cannot be controlled

Disable fallback cho production; không block Self Update baseline nếu Self Update pass.

## 12. Production Record

Mỗi produced device nên có production record chỉ chứa safe metadata.

Field

Description

`factory_batch_id`

Factory batch identifier.

`dsetup_version`

DSetup tool version used.

`dsetup_run_id`

Unique run id for one provisioning/update/rework attempt.

`dsetup_scenario`

`CLEAN_PROVISIONING`, `DCAM_DEVICE_OWNER_UPDATE`, `FACTORY_RESET_REQUIRED`, `REWORK_AFTER_FACTORY_RESET`, or approved equivalent.

`adb_device_reference`

Safe/masked ADB device reference if recorded.

`device_owner_state_before`

Safe state classification before DSetup action.

`device_owner_state_after`

Safe state classification after DSetup action.

`serial_source`

`SD_IDENTITY_FILE`, `BARCODE_SCAN`, `MANUAL_APPROVED_FALLBACK`, or approved equivalent.

`sd_identity_file_result`

Pass/fail/not available/not required/result of SD Identity File read/sync.

`sd_identity_file_path_reference`

Approved path reference only; avoid storing unnecessary full filesystem dump.

`barcode_scan_result`

Pass/fail/result of barcode scan if required.

`device_serial_number`

Assigned/verified serial number. Hardware Identity.

`serial_injection_result`

Pass/fail/result of DSetup ADB factory broadcast serial injection.

`serial_import_result`

Pass/fail/result of DCAM serial import/validation.

`dcam_cloud_device_id`

DCAM Cloud Identity / primary cloud device id.

`serial_lookup_result`

Pass/fail/result of `serial_lookup/{serial_number}` mapping.

`owner_name`

Customer/agency owner nếu được assigned.

`manufacture_date`

`YYYY-MM-DD`.

`bodycamera_model`

Device model.

`firmware_version`

Firmware/build version.

`apk_version_name`

Installed DCAM versionName.

`apk_version_code`

Installed DCAM versionCode.

`apk_checksum`

Approved checksum used.

`apk_signing_fingerprint`

Trusted signing fingerprint nếu được ghi nhận.

`apk_factory_wifi_config_result`

Confirm approved APK includes hardcoded factory Wi-Fi config. Do not store password.

`apk_factory_serial_receiver_result`

Confirm APK supports approved DSetup factory serial receiver/action.

`apk_sd_identity_sync_result`

Confirm APK supports SD Identity File sync if required.

`device_owner_setup_method`

`DSETUP_ADB_DPM_SET_DEVICE_OWNER`, `EXISTING_DCAM_DEVICE_OWNER`, or approved equivalent.

`device_owner_component`

Approved DCAM DeviceAdminReceiver component name.

`device_owner_result`

Pass/fail/not supported/approved fallback.

`device_owner_verify_result`

Result from `dumpsys device_policy` and app runtime verification.

`factory_wifi_result`

Pass/fail/result of DCAM app Wi-Fi configuration and connectivity verification.

`factory_wifi_ssid_reference`

Optional approved SSID reference/name only if policy allows. Do not store password.

`firebase_provisioning_result`

Pass/fail/result of Firebase business provisioning/recovery by serial_number.

`lock_task_result`

Pass/fail.

`user_restrictions_result`

Pass/fail/partial with limitation.

`provisioning_admin`

Factory admin/user id, không phải secret value.

`factory_operator`

Operator thực hiện setup.

`qa_approver`

QA approver.

`factory_test_result`

Pass/fail/quarantined.

`ready_to_ship_at`

Timestamp nếu approved.

`known_limitations`

Chỉ approved limitations.

Forbidden production record fields:

text## 13. Shipment Handover

Step

Expected Result

Review factory acceptance checklist.

Tất cả required checks đã pass.

Review DSetup result.

DSetup run result là PASS, đúng scenario, đúng serial, đúng serial source, đúng APK, đúng Device Owner result.

Review production record.

Required metadata đã được ghi nhận, bao gồm DSetup run id, scenario, serial source, ADB Device Owner setup/update result, serial injection/import result, SD Identity File result nếu applicable, DCAM factory Wi-Fi result và Firebase provisioning result.

Confirm Hardware Identity.

`serial_number` đã verify và map đúng `dcam_cloud_device_id`.

Confirm Cloud Identity.

`dcam_cloud_device_id` đã create/restore đúng theo `serial_number`.

Confirm Recovery Cache.

SD Identity File đã được tạo/cập nhật nếu release/batch yêu cầu.

Confirm no ANDROID_ID dependency.

Production baseline không dựa vào `ANDROID_ID`, `android_id_hash` hoặc `device_lookup`.

Confirm Device Owner and factory Wi-Fi state.

Device Owner verified và DCAM-applied Wi-Fi connectivity đã pass.

Confirm Device Owner and kiosk policy.

Device Owner, Lock Task và User Restrictions đã verify.

Confirm QR business provisioning boundary.

QR nếu dùng chỉ được dùng cho Firebase business provisioning và mapping đã tồn tại.

Clean test media only if approved by factory/security policy.

Không delete evidence-like files nếu chưa có rule.

Confirm final runtime state.

Device đã provisioned, locked down và ready.

Confirm Self Update settings/state.

Device có thể receive/update bằng approved primary path.

Confirm Play Store fallback state.

Disabled trừ khi explicitly approved.

Power off or set shipping mode if required.

Device được chuẩn bị cho shipment.

Package accessories.

Đúng charger/cable/mount/accessories được included.

Attach label or shipment reference.

Serial/owner/batch mapping rõ ràng.

Mark `READY_TO_SHIP`.

Device có thể shipment.

## 14. Open Questions / TBD

Các mục này vẫn là TBD vì cần Device POC, Security Review, Product decision hoặc implementation detail.

Item

Owner / Source

Exact DSetup UI design and operator workflow.

Android Lead + Factory Lead + QA

Exact DSetup technology stack and release packaging.

Android Lead + Release Manager

Exact DSetup Device Owner state detection logic.

Android Lead + Factory Lead + QA

Exact ADB script/command wrapper for Device Owner setup inside DSetup.

Android Lead + Factory Lead

Exact approved DeviceAdminReceiver component name per release package.

Android Lead + Release Manager

Exact approved factory serial broadcast action/component.

Android Lead + Security

Exact DCAM factory receiver enable/disable rules.

Android Lead + Security

Exact SD Identity File path/schema/signature/checksum policy.

Android Lead + Security + Factory Lead

Exact DCAM SD Identity File sync schedule.

Android Lead + QA

Exact behavior when SD card is absent/replaced/read-only/corrupt.

Android Lead + QA + Support Lead

Exact factory reset / clean state verification commands.

Android Lead + Factory Lead + QA

Whether firmware allows ADB Device Owner setup consistently after factory reset.

Device POC + Factory Lead

Whether silent APK install/update is supported.

Device POC + Self Update Design

Exact hardcoded factory Wi-Fi SSID/password packaging and release approval process.

Android Lead + Release Manager + Security

Exact Wi-Fi connectivity verification method after DCAM configuration.

Android Lead + QA + Factory Lead

Exact serial number format and duplicate conflict rule.

Product + Factory Lead + Web/API Lead

Exact DSetup manifest/CSV support for batches.

Factory Lead + Product + Android Lead

Exact Firebase serial_lookup transaction behavior.

Web/API Lead + Android Lead

Exact rework/factory reset recovery behavior for already-provisioned serial_number.

Web/API Lead + Support Lead + QA

Exact identity conflict resolution process when serial_lookup conflicts.

Web/API Lead + Security + Support Lead

Exact production label format.

Factory Lead + Product

Exact maintenance credential initial provisioning/reset process.

Security + Product

Exact maintenance failed-attempt lockout values.

Security + Product

Whether Play Store fallback is allowed for each device model.

Security + Product + Device POC

Whether test media should be deleted or retained after factory test.

Product + Security + BDMA Lead

Exact Firebase provisioning/status/update manifest contract.

Web Portal & Device API Contract

Exact release artifact repository and checksum/signing publish process.

Release Plan + Self Update Design

## 15. Practical Conclusion

text