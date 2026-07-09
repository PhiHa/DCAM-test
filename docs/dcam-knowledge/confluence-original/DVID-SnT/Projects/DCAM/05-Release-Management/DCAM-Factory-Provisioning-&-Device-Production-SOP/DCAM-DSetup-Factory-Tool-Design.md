# DCAM DSetup Factory Tool Design

**Page ID**: 50626624  
**Version**: 1  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/50626624

---


# DCAM DSetup Factory Tool Design

Item

Information

Project

DCAM Android BodyCamera Application

Document Type

Factory Tool Design

Version

Draft 1.0

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / Factory Lead / QA Lead / Security Reviewer

Approver

Hoàng Ngọc Quyền

Parent Page

DCAM Factory Provisioning & Device Production SOP

Target Audience

Android Developers, Factory Tool Developers, Factory Operator, Factory Admin, QA, Support

Last Updated

2026-07-09

Related Documents

DCAM Factory Provisioning & Device Production SOP, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM Android Operation Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM Web Portal & Device API Contract, DCAM Security & Encryption Design, DCAM SQLite Database Design, DCAM Device POC & Hardware Validation Report

## 1. Purpose

**DCAM DSetup Factory Tool Design** mô tả tool **DSetup** chạy trên PC trong nhà máy để hỗ trợ chuẩn bị BodyCamera trong quá trình sản xuất và phát triển DCAM.

DSetup là một **simple PC factory helper tool**. Tool này không phải QA tool, không phải Web Portal, không phải production acceptance system và không quyết định thiết bị có được `READY_TO_SHIP` hay không.

Mục tiêu của DSetup là hỗ trợ Factory Operator thực hiện các bước kỹ thuật cơ bản qua `ADB` cho đến khi DCAM app đã import đúng `serial_number`.

DSetup completes when:

text## 2. Current Baseline

text## 3. Scope

Area

Description

PC factory helper

DSetup là app chạy trên PC để hỗ trợ Factory Operator thao tác với BodyCamera qua `ADB`.

Single-device operation

DSetup chỉ làm việc với đúng một Android device đang kết nối. Nếu có 0 hoặc nhiều hơn 1 device, tool phải dừng và yêu cầu operator xử lý.

Basic ADB connection check

DSetup kiểm tra device có kết nối `ADB` và có thể nhận lệnh cơ bản.

SD Identity File read

DSetup cố gắng đọc SD Identity File để lấy `serial_number` nếu file tồn tại và hợp lệ.

Barcode / manual serial input

Nếu không lấy được serial từ SD Identity File, DSetup cho phép Factory Operator scan barcode hoặc nhập `serial_number` thủ công.

APK install

DSetup install approved DCAM APK vào thiết bị.

Device Owner setup / verify

DSetup set hoặc verify `Device Owner` nếu factory flow hiện tại yêu cầu.

Serial injection

DSetup inject `serial_number` vào DCAM bằng approved factory mechanism.

Launch DCAM

DSetup launch DCAM sau khi APK install và serial injection hoàn tất.

Verify imported serial

DSetup verify DCAM đã import đúng `serial_number` mong đợi.

## 4. Out of Scope

Area

Reason

Lock Task / kiosk baseline verification

Việc verify `Lock Task`, kiosk behavior và `User Restrictions` thuộc Factory SOP, QA hoặc Device POC; không thuộc DSetup.

Recording / storage test

DSetup không chạy recording/storage acceptance test. Các test này thuộc QA Matrix hoặc Factory SOP.

Web Portal provisioning

DSetup không thay thế Web Portal hoặc Backend/Firebase business provisioning.

BDMA import / user sync

DSetup không kiểm tra BDMA import, user sync hoặc BDMA readiness.

Production record

DSetup không ghi production record chính thức và không quyết định production result.

PASS / FAIL / QUARANTINED decision

DSetup không mark `PASS`, `FAIL`, `QUARANTINED` hoặc `READY_TO_SHIP`. Các quyết định này thuộc Factory SOP / QA / factory process.

Self Update validation

DSetup không validate full Self Update path. Tool chỉ install approved APK phục vụ factory preparation.

Security review

DSetup không quyết định credential policy, encryption policy, API auth hoặc Firestore security rules.

Android runtime behavior

DSetup không own Android Operation runtime behavior sau khi DCAM đã import `serial_number`.

## 5. User Roles

Role

Responsibility

Factory Operator

Mở DSetup, kết nối BodyCamera, scan hoặc nhập `serial_number` khi được yêu cầu và xử lý các lỗi thao tác cơ bản.

Factory Admin

Chuẩn bị approved APK, hướng dẫn factory flow và xử lý các trường hợp cần quyền cao hơn.

Android Developer

Implement factory broadcast/receiver hoặc approved serial import mechanism trong DCAM.

Tool Developer

Implement DSetup PC app, ADB command wrapper, UI đơn giản và validation logic.

Tech Lead

Review flow, boundary và các command có rủi ro cao như `Device Owner` setup.

Security Reviewer

Review các rule không log secret, không dùng Android ID và không bypass security baseline.

## 6. Tool Responsibilities

Responsibility

Description

Detect device

DSetup detect Android device qua `ADB` và đảm bảo chỉ có một device được xử lý.

Validate basic state

DSetup kiểm tra trạng thái kết nối cơ bản để biết device có thể nhận lệnh.

Resolve serial_number

DSetup resolve `serial_number` từ SD Identity File hoặc từ barcode/manual input.

Validate serial_number

DSetup kiểm tra `serial_number` không rỗng, đúng format tối thiểu và không chứa ký tự không hợp lệ.

Install APK

DSetup install approved DCAM APK được operator/admin chọn hoặc được cấu hình sẵn.

Set / verify Device Owner

DSetup chạy bước set hoặc verify `Device Owner` nếu factory flow yêu cầu và device đang ở trạng thái cho phép.

Inject serial_number

DSetup gửi `serial_number` vào DCAM bằng approved factory mechanism.

Launch DCAM

DSetup launch app DCAM sau khi serial injection hoàn tất.

Verify serial import

DSetup xác nhận DCAM đã nhận/import đúng `serial_number`.

Stop at serial import verification

Sau khi verify serial import thành công, DSetup dừng scope và không chạy các bước acceptance khác.

## 7. Main Flow

textCompletion condition:

text## 8. ADB Device Detection Rules

Rule

Description

DSETUP-ADB-001

DSetup phải detect Android devices bằng `ADB`.

DSETUP-ADB-002

Nếu không có device nào connected, DSetup hiển thị hướng dẫn operator kết nối thiết bị và bật đúng factory/ADB mode.

DSETUP-ADB-003

Nếu có nhiều hơn một device, DSetup phải dừng để tránh inject nhầm `serial_number`.

DSETUP-ADB-004

Nếu device ở trạng thái unauthorized, DSetup yêu cầu operator xử lý authorization theo factory instruction.

DSETUP-ADB-005

DSetup không được tự động chọn một device khi có nhiều device cùng kết nối.

DSETUP-ADB-006

DSetup không được tiếp tục APK install hoặc serial injection khi device state không rõ ràng.

Recommended device states:

ADB State

Handling

`device`

Có thể tiếp tục nếu chỉ có một device.

`unauthorized`

Dừng và yêu cầu operator authorize hoặc xử lý factory mode.

`offline`

Dừng và yêu cầu operator reconnect/restart ADB/device.

multiple devices

Dừng và yêu cầu chỉ giữ lại một device.

no device

Dừng và yêu cầu kết nối device.

## 9. SD Identity File Read

DSetup cố gắng đọc SD Identity File để recover `serial_number` trong factory reset hoặc rework flow.

Recommended path:

text/DCAM_FACTORY/device_identity.json]]>Expected minimum payload:

json
Rule

Description

DSETUP-SD-001

DSetup chỉ dùng SD Identity File để đọc `serial_number` recovery cache.

DSETUP-SD-002

SD Identity File không phải authoritative identity nếu format sai hoặc có conflict rõ ràng.

DSETUP-SD-003

Nếu file missing, malformed hoặc không đọc được, DSetup chuyển sang barcode/manual input.

DSETUP-SD-004

Nếu `serial_number` trong file rỗng hoặc sai format, DSetup không được dùng giá trị đó.

DSETUP-SD-005

DSetup không upload full SD card contents và không đọc media contents ngoài file identity cần thiết.

DSETUP-SD-006

DSetup không tự quyết định device `READY_TO_SHIP` dựa trên SD Identity File.

## 10. Barcode / Manual Serial Input

Nếu không lấy được `serial_number` từ SD Identity File, DSetup cho phép Factory Operator scan barcode hoặc nhập thủ công.

Input Method

Description

Barcode scan

Factory Operator scan barcode trên device label hoặc production label.

Manual input

Factory Operator nhập `serial_number` thủ công nếu barcode scan không khả dụng.

Copy/paste

Có thể cho phép trong factory environment nếu có kiểm soát.

Validation direction:

Rule

Description

DSETUP-SERIAL-001

`serial_number` không được rỗng.

DSETUP-SERIAL-002

`serial_number` phải pass format rule được Product/Factory approve.

DSETUP-SERIAL-003

DSetup phải hiển thị `serial_number` để operator verify trước khi inject.

DSETUP-SERIAL-004

DSetup không được silently modify `serial_number`, ngoại trừ trim khoảng trắng đầu/cuối nếu được approve.

DSETUP-SERIAL-005

DSetup không được generate `serial_number` mới.

DSETUP-SERIAL-006

DSetup không dùng `ANDROID_ID`, `android_id_hash`, IMEI, MAC hoặc Advertising ID làm replacement cho `serial_number`.

## 11. APK Install Flow

DSetup install approved DCAM APK vào thiết bị qua `ADB` hoặc approved install method.

Step

Description

Select APK

Factory Admin hoặc tool config cung cấp approved DCAM APK.

Verify file exists

DSetup kiểm tra APK file tồn tại trên PC.

Optional checksum check

Nếu checksum được cung cấp, DSetup verify checksum trước install.

Install APK

DSetup install APK vào device bằng approved command.

Confirm package exists

DSetup verify package DCAM tồn tại sau install.

Continue to Device Owner step

Nếu install thành công, DSetup tiếp tục set/verify `Device Owner` nếu required.

Rules:

Rule

Description

DSETUP-APK-001

DSetup chỉ install approved DCAM APK.

DSETUP-APK-002

DSetup không download arbitrary APK từ nguồn không kiểm soát.

DSETUP-APK-003

Nếu APK install fail, DSetup dừng flow và hiển thị reason.

DSETUP-APK-004

DSetup không thực hiện Self Update validation đầy đủ; full Self Update thuộc DCAM Self Update Design.

DSETUP-APK-005

DSetup không lưu APK signing private key hoặc secret trong tool config.

## 12. Device Owner Setup Flow

DSetup có thể set hoặc verify `Device Owner` nếu factory setup yêu cầu. Bước này phụ thuộc trạng thái device và POC trên model/firmware thật.

Mode

Description

Set Device Owner

DSetup chạy approved command để set DCAM hoặc local DCAM DPC component làm `Device Owner` khi device đang ở trạng thái cho phép.

Verify Device Owner

DSetup kiểm tra `Device Owner` đã được set đúng hay chưa.

Skip if not required

Nếu factory flow hiện tại chưa yêu cầu hoặc chưa support, bước này có thể bị skip theo approved instruction.

Rules:

Rule

Description

DSETUP-DO-001

DSetup không được assume APK install đồng nghĩa với `Device Owner`.

DSETUP-DO-002

DSetup chỉ set `Device Owner` trên fresh/factory device hoặc trạng thái được SOP approve.

DSETUP-DO-003

Nếu device đã có non-DCAM Device Owner, DSetup phải dừng và yêu cầu factory/rework handling.

DSETUP-DO-004

Nếu set `Device Owner` fail, DSetup dừng flow và hiển thị reason.

DSETUP-DO-005

DSetup không verify full kiosk baseline, `Lock Task` behavior hoặc `User Restrictions` behavior.

DSETUP-DO-006

Full kiosk validation thuộc Factory SOP, QA Matrix hoặc Device POC.

## 13. Serial Injection Flow

DSetup inject `serial_number` vào DCAM sau khi APK install và `Device Owner` setup/verify step hoàn tất hoặc được skip theo approved flow.

Approved direction:

textPossible mechanism:

Mechanism

Description

ADB factory broadcast

DSetup gửi factory broadcast tới DCAM với `serial_number` và optional metadata.

ADB shell command wrapper

DSetup dùng wrapper command đã được Android team approve.

Local factory intent

DCAM expose factory-only import path trong build/profile được approve.

Rules:

Rule

Description

DSETUP-INJECT-001

DSetup chỉ inject `serial_number` đã được resolve và verify bởi operator/tool validation.

DSETUP-INJECT-002

DSetup không inject `android_id_hash`, `ANDROID_ID` hoặc identifier khác thay cho `serial_number`.

DSETUP-INJECT-003

DSetup không silently overwrite serial đã tồn tại nếu DCAM báo conflict; conflict handling thuộc approved rework/SOP.

DSETUP-INJECT-004

Serial injection mechanism phải chỉ available trong approved factory/development context.

DSETUP-INJECT-005

DCAM phải xác nhận import result bằng response, file/state query hoặc approved verification mechanism.

## 14. Launch DCAM

Sau khi serial injection request được gửi, DSetup launch DCAM để app hoàn tất import và expose verification result.

Rule

Description

DSETUP-LAUNCH-001

DSetup launch đúng DCAM package/activity được approved.

DSETUP-LAUNCH-002

Nếu launch fail, DSetup dừng flow và hiển thị reason.

DSETUP-LAUNCH-003

DSetup không điều khiển UI runtime sau khi DCAM đã launch, ngoại trừ verification step cần thiết.

DSETUP-LAUNCH-004

DSetup không chạy recording/storage/kiosk acceptance sau launch.

## 15. Verify DCAM Imported serial_number

Đây là completion point của DSetup.

DSetup phải verify rằng DCAM đã import đúng `serial_number` mong đợi.

Verification direction:

textPossible verification mechanisms:

Mechanism

Description

ADB query to DCAM factory endpoint

DSetup gọi approved command/query để DCAM trả về imported `serial_number`.

Broadcast/result response

DCAM trả result sau khi nhận factory injection request.

App-private exported factory check

DCAM cung cấp factory-only check mechanism nếu được Security approve.

UI confirmation fallback

Operator nhìn thấy `serial_number` trong DCAM factory/debug screen nếu automated query chưa có.

Rules:

Rule

Description

DSETUP-VERIFY-001

DSetup chỉ complete khi imported `serial_number` khớp expected `serial_number`.

DSETUP-VERIFY-002

Nếu DCAM không trả verification result, DSetup phải coi là chưa complete.

DSETUP-VERIFY-003

Nếu serial mismatch, DSetup phải stop và yêu cầu rework/support handling.

DSETUP-VERIFY-004

DSetup không được tự sửa serial trong DCAM nếu mismatch mà không có approved rework instruction.

DSETUP-VERIFY-005

Sau khi verify thành công, DSetup không tiếp tục verify `Lock Task`, recording, storage, Web Portal provisioning hoặc BDMA.

## 16. Error Handling

Error Case

Handling

No ADB device

Hiển thị hướng dẫn kết nối device và dừng flow.

Multiple ADB devices

Dừng flow và yêu cầu chỉ giữ lại một device.

Unauthorized device

Yêu cầu operator xử lý authorization/factory mode.

SD Identity File missing

Chuyển sang barcode/manual serial input.

SD Identity File invalid

Không dùng file; chuyển sang barcode/manual serial input.

Serial input invalid

Yêu cầu operator nhập/scan lại.

APK missing on PC

Dừng flow và yêu cầu chọn/cấu hình approved APK.

APK install failed

Dừng flow và hiển thị install reason.

Device Owner setup failed

Dừng flow và hiển thị reason; không chạy các bước sau.

Serial injection failed

Dừng flow và hiển thị reason.

DCAM launch failed

Dừng flow và hiển thị reason.

Serial verification timeout

Dừng flow và yêu cầu retry hoặc support handling.

Serial mismatch

Dừng flow và yêu cầu rework/support handling.

DSetup error handling chỉ phục vụ operator biết bước nào fail. Tool không tạo production acceptance result và không mark device `PASS`, `FAIL`, `QUARANTINED` hoặc `READY_TO_SHIP`.

## 17. Security Rules

Rule

Description

DSETUP-SEC-001

DSetup không dùng `ANDROID_ID`, `android_id_hash`, `device_lookup/{android_id_hash}`, Advertising ID, IMEI hoặc MAC làm production identity.

DSETUP-SEC-002

DSetup chỉ dùng `serial_number` làm Hardware Identity / primary recovery key.

DSETUP-SEC-003

DSetup không lưu maintenance password, Google account password/token, cloud token, provisioning secret hoặc APK signing private key.

DSETUP-SEC-004

DSetup không upload full SD card contents hoặc media contents.

DSETUP-SEC-005

Serial injection path phải được Android/Security approve cho factory/development context.

DSETUP-SEC-006

Nếu DSetup có local runtime log để debug, log không được chứa secret values hoặc raw credential input.

DSETUP-SEC-007

DSetup không bypass Maintenance Password Gate hoặc Android runtime security policy.

DSETUP-SEC-008

DSetup không quyết định cloud identity, `dcam_cloud_device_id` hoặc Web Portal provisioning result.

## 18. UI Screens

DSetup UI nên đơn giản, tuần tự và khó thao tác nhầm.

Screen

Purpose

Welcome / Checklist

Nhắc operator kết nối đúng một BodyCamera và chuẩn bị approved APK/barcode nếu cần.

Device Detection

Hiển thị ADB device state và chỉ cho tiếp tục khi exactly one device connected.

Serial Resolution

Hiển thị kết quả đọc SD Identity File hoặc yêu cầu scan/manual input.

Serial Confirmation

Cho operator confirm `serial_number` trước khi install/inject.

APK Install

Hiển thị APK path/version/checksum nếu có và install result.

Device Owner Setup

Hiển thị set/verify `Device Owner` result nếu bước này enabled.

Serial Injection

Hiển thị injection progress/result.

Launch DCAM

Hiển thị launch progress/result.

Verify Imported Serial

Hiển thị expected và actual imported `serial_number`.

Completed

Hiển thị DSetup completed vì DCAM đã import đúng `serial_number`.

Error

Hiển thị lỗi hiện tại và bước operator cần xử lý.

## 19. Test Checklist

Test Case

Expected Result

One device connected

DSetup cho phép tiếp tục flow.

No device connected

DSetup dừng và yêu cầu kết nối device.

Multiple devices connected

DSetup dừng và yêu cầu chỉ giữ lại một device.

Unauthorized device

DSetup dừng và hiển thị unauthorized handling.

Valid SD Identity File

DSetup đọc được `serial_number` từ file.

Missing SD Identity File

DSetup yêu cầu barcode/manual serial input.

Invalid SD Identity File

DSetup không dùng file và yêu cầu barcode/manual serial input.

Valid barcode serial

DSetup accept `serial_number` sau validation.

Invalid serial input

DSetup reject và yêu cầu nhập/scan lại.

APK install success

DCAM package tồn tại trên device sau install.

APK install failure

DSetup dừng flow.

Device Owner setup success

DSetup tiếp tục serial injection.

Device Owner setup failure

DSetup dừng flow.

Serial injection success

DCAM nhận serial injection request.

DCAM imported serial matches expected

DSetup complete.

DCAM imported serial mismatch

DSetup dừng và yêu cầu rework/support handling.

Android ID inspection

DSetup không đọc/gửi/log `ANDROID_ID` hoặc `android_id_hash`.

## 20. Open Questions / TBD

Item

Status

Exact PC technology stack for DSetup

TBD

Exact supported OS for factory PC

TBD

Exact ADB packaging/distribution model

TBD

Exact approved APK selection/config mechanism

TBD

Exact `serial_number` format rule

TBD / Factory + Product

Exact SD Identity File validation rule

TBD / Android + Security

Exact ADB command for reading SD Identity File

TBD / Android + Factory Tool Dev

Exact Device Owner command/component name

TBD / Android + Device POC

Whether Device Owner setup is mandatory in all DSetup runs

TBD / Factory SOP + Device POC

Exact serial injection mechanism

TBD / Android + Security

Exact verification mechanism for imported `serial_number`

TBD / Android + Factory Tool Dev

Whether DSetup needs a factory-only build/profile flag

TBD / Security + Android

Whether local debug log is allowed and retention policy

TBD / Security + Factory

## 21. Practical Conclusion

text