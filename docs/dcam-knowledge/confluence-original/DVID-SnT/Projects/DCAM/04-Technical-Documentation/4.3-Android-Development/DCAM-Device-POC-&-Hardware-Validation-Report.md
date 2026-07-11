# DCAM Device POC & Hardware Validation Report

**Page ID**: 49545399  
**Version**: 7  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49545399

---


# DCAM Device POC & Hardware Validation Report

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Device POC / Hardware Validation Report

Version

Draft 0.5

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / QA Lead / Security Reviewer / BDMA Lead / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.3 - Android Development

Target Audience

Tech Lead, Android Developers, QA, Security Reviewer, Support, Factory/Admin Users

Last Updated

2026-07-09

Related Documents

DCAM Project Home, DCAM Architecture Home, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Android Operation Design, DCAM Self Update Design, DCAM QA Test Strategy & Test Matrix, DCAM Factory Provisioning & Device Production SOP, DCAM Security & Encryption Design, DCAM Device Capability & Feature Eligibility Design, DCAM Storage Design, DCAM Recording & Capture Design, DCAM-BDMA Data Contract

## 1. Purpose

Tài liệu này ghi nhận kết quả **POC / hardware validation** trên BodyCamera thật để chốt các quyết định phụ thuộc vào firmware, OEM behavior và Android version của thiết bị mục tiêu.

Mục tiêu của tài liệu là biến các giả định trong architecture/design thành bằng chứng kiểm chứng được trên thiết bị thật. Các kết quả trong tài liệu này sẽ quyết định implementation path cho `Device Owner`, `Lock Task`, `User Restrictions`, `Self Update`, `Recording`, `Storage`, `BDMA`, `Factory SOP` và các fallback được phép.

Current validation baseline:

Không dùng external EMM.
Không dùng Android Management API.
Không dùng Managed Google Play policy-driven update.
Tính khả thi của DCAM-as-DPC / local Device Owner phải được validate trên thiết bị thật.
Primary update path = DCAM Self Update / APK update.
Manual Google Play Store update chỉ là optional controlled fallback nếu có GMS/Play Store và có approved process/account.
Chỉ hỗ trợ Controlled Maintenance Mode; không hỗ trợ full Android unrestricted mode.
Factory SOP chỉ được approve cho một device model sau khi các POC blockers bắt buộc đã được xử lý hoặc được accept rõ ràng.
## 2. POC Scope

Area

Validation Purpose

Priority

Android version / API behavior

Xác nhận các Android APIs cần thiết có sẵn và hoạt động đúng trên target BodyCamera.

P0

DCAM-as-DPC / Device Owner setup

Xác nhận DCAM có thể trở thành Device Owner/DPC-capable mà không phụ thuộc external EMM.

P0

Lock Task Mode

Xác nhận DCAM có thể enter và recover `Lock Task`.

P0

User Restrictions

Xác nhận restriction nào được hỗ trợ, không hỗ trợ hoặc có OEM behavior đặc biệt.

P0

Home/Launcher policy

Xác nhận DCAM có thể hoạt động như Home/Launcher hoặc preferred home nếu production profile yêu cầu.

P0

Controlled Maintenance Mode

Xác nhận temporary kiosk exit chỉ mở approved targets và có thể restore policy sau khi kết thúc.

P0

Maintenance Password Gate

Xác nhận password gate chặn kiosk exit không hợp lệ và logging an toàn.

P0

No full unrestricted Android

Xác nhận user không truy cập được unrestricted launcher, app drawer hoặc Android Settings ngoài approved flow.

P0

Self Update / APK update

Xác nhận APK download, validation, install, restart và policy restore hoạt động trên device thật.

P0

Silent install feasibility

Xác nhận Device Owner/local policy có thể silent install APK hay bắt buộc phải có approved UX/session flow.

P0

Factory SOP feasibility

Xác nhận các bước SOP có thể chạy lặp lại ổn định trên target model/firmware.

P0

Production record data

Xác nhận có thể thu thập required safe production metadata mà không lưu forbidden secrets.

P0

GMS / Play Store availability

Xác nhận thiết bị có Google Play Services và Google Play Store hay không.

P1

Manual Play Store fallback

Xác nhận optional fallback chỉ được dùng nếu available và được Product/Security approve.

P1

Google account handling

Xác nhận approved maintenance/factory account behavior nếu Play Store fallback được cho phép.

P1

Camera/recording

Xác nhận camera pipeline, resolution profiles, FPS và pre-record feasibility.

P0

Storage / BDMA

Xác nhận storage path, final media visibility, ADB import và BDMA readiness.

P0

Foreground service / boot

Xác nhận boot receiver, screen-off behavior, process kill và restart behavior.

P0

GPS/sensor/AI capability

Xác nhận optional capability và runtime pruning cho GPS, sensor và AI features.

P1

## 3. Device Baseline Information

Field

Value

BodyCamera Model

TBD

Manufacturer

TBD

Android Version

TBD

Build/Firmware Version

TBD

GMS Available

TBD

Google Play Store Available

TBD

USB/ADB Mode Behavior

TBD

Camera API / Vendor SDK

TBD

Storage Layout

TBD

Battery Optimization Behavior

TBD

Boot Auto-start Support

TBD

Device Owner Setup Support

TBD

Lock Task Support

TBD

User Restrictions Support

TBD

Silent APK Install Support

TBD

Factory SOP Repeatability

TBD

External EMM Availability

Không có trong current baseline.

## 4. Kiosk / Device Owner POC Matrix

Test ID

Scenario

Expected Evidence

Status

POC-KIOSK-001

Set DCAM hoặc local DCAM DPC component làm Device Owner trên fresh/factory device.

Device Owner state được xác nhận bằng Android/device policy inspection.

TBD

POC-KIOSK-002

Thử normal APK install mà không có Device Owner setup.

Xác nhận APK install đơn thuần không được xem là Device Owner.

TBD

POC-KIOSK-003

Verify Lock Task allowlist và enter Lock Task.

DCAM enter Lock Task và user không thể escape khỏi kiosk experience.

TBD

POC-KIOSK-004

Reboot device và verify Lock Task recovery.

DCAM verify policy và re-enter Lock Task sau boot.

TBD

POC-KIOSK-005

Process kill/crash recovery.

DCAM restore Lock Task hoặc enter policy recovery state.

TBD

POC-KIOSK-006

Home/Recents/Back behavior.

Không có đường escape khỏi approved kiosk experience.

TBD

POC-KIOSK-007

Apply User Restrictions baseline.

Các supported/unsupported restrictions được ghi nhận rõ.

TBD

POC-KIOSK-008

Attempt factory reset/safe boot/app uninstall/app control.

Bị block nếu device hỗ trợ; nếu không hỗ trợ thì ghi rõ unsupported reason.

TBD

POC-KIOSK-009

Enter Controlled Maintenance bằng password.

Chỉ approved targets được mở và có audit event.

TBD

POC-KIOSK-010

Invalid maintenance password.

Kiosk vẫn active; không restriction nào bị relax.

TBD

POC-KIOSK-011

Maintenance timeout/reboot/crash recovery.

DCAM restore User Restrictions và Lock Task.

TBD

POC-KIOSK-012

Thử unrestricted launcher/app drawer/settings.

Access unavailable hoặc bị block theo approved policy.

TBD

POC-KIOSK-013

Verify no external EMM dependency.

Device pass kiosk baseline mà không cần Android Management API/Managed Google Play.

TBD

## 5. Self Update / APK Update POC Matrix

Test ID

Scenario

Expected Evidence

Status

POC-UPD-001

DCAM load update manifest từ approved artifact provider.

Manifest được load và version được parse đúng.

TBD

POC-UPD-002

Download APK package.

APK được download với expected size/checksum.

TBD

POC-UPD-003

Validate package identity/signature/checksum/version.

Valid APK được accept; invalid APK bị reject.

TBD

POC-UPD-004

Install APK khi device idle và runtime safe.

Update thành công hoặc approved install UX được ghi nhận rõ.

TBD

POC-UPD-005

Install path yêu cầu temporary maintenance.

Maintenance Password Gate bắt buộc và policy được restore sau update.

TBD

POC-UPD-006

Update trong lúc recording/emergency/finalization.

Update bị defer.

TBD

POC-UPD-007

Update failure.

Current app/policy không bị rơi vào unrestricted state; recovery behavior được ghi nhận.

TBD

POC-UPD-008

Verify app version sau update/restart.

New version được detect hoặc failure reason được log.

TBD

POC-UPD-009

Confirm Managed Google Play path is not used.

Được mark là not applicable cho current baseline.

TBD

POC-UPD-010

Confirm silent install feasibility.

Silent install supported/not supported được ghi nhận bằng firmware evidence.

TBD

## 6. Optional Google Play Store Fallback POC Matrix

Chỉ chạy section này nếu device có GMS/Play Store và Product/Security cho phép fallback.

Test ID

Scenario

Expected Evidence

Status

POC-PLAY-001

Check GMS availability.

GMS present/missing được ghi nhận.

TBD

POC-PLAY-002

Check Google Play Store availability.

Play Store present/missing/disabled được ghi nhận.

TBD

POC-PLAY-003

Open Play Store từ Controlled Maintenance Mode.

Chỉ mở được sau Maintenance Password Gate.

TBD

POC-PLAY-004

Sign in approved maintenance/factory Google account nếu required.

Account process được ghi nhận; không dùng personal account.

TBD

POC-PLAY-005

Update DCAM/approved app qua Play Store.

Update result và version được verify.

TBD

POC-PLAY-006

Thử unapproved Play Store browse/install.

Bị block hoặc được ghi nhận là non-compliant; fallback phải disabled nếu không kiểm soát được.

TBD

POC-PLAY-007

Return to DCAM và restore kiosk.

Lock Task/User Restrictions được restore.

TBD

POC-PLAY-008

Account persistence/removal behavior.

Security/Product decision được ghi nhận.

TBD

Nếu không thể kiểm soát unapproved app browsing/install, manual Play Store fallback phải bị disable cho production.

## 7. In-app Console POC Matrix

Test ID

Scenario

Expected Evidence

Status

POC-CONSOLE-001

App start vào Record / Live View sau login.

Default screen được xác nhận.

TBD

POC-CONSOLE-002

Back trên Record mở Setting; Back trên Setting quay lại Record.

Navigation behavior được xác nhận.

TBD

POC-CONSOLE-003

Back từ child module quay lại Setting.

Navigation behavior được xác nhận.

TBD

POC-CONSOLE-004

Operator không thể truy cập Admin-only User Settings.

Access bị denied hoặc UI bị hidden.

TBD

POC-CONSOLE-005

Login Settings self-service chỉ hoạt động cho current user.

Password/login method behavior được xác nhận.

TBD

POC-CONSOLE-006

File Manager và Media Viewer read-only.

Không có delete/edit/mark/export/share actions.

TBD

POC-CONSOLE-007

Future modules hidden/disabled.

Server/Live/PTT/AI disabled cho đến khi design được approve.

TBD

## 8. Storage / BDMA / ADB POC Matrix

Test ID

Scenario

Expected Evidence

Status

POC-BDMA-001

Record finalized MP4 và mark BDMA readiness.

File và DB state consistent.

TBD

POC-BDMA-002

BDMA ADB import dưới production restrictions.

ADB/import path hoạt động hoặc approved maintenance path được ghi nhận.

TBD

POC-BDMA-003

User sync/write-back dưới restrictions.

Sync hoạt động hoặc restriction adjustment được ghi nhận.

TBD

POC-BDMA-004

External media / USB restrictions.

Không silently break BDMA boundary.

TBD

POC-BDMA-005

Storage recovery sau interrupted finalization.

Evidence được preserve và recovery result được ghi nhận.

TBD

## 9. Factory SOP POC Matrix

Test ID

Scenario

Expected Evidence

Status

POC-FACTORY-001

Run SOP từ raw/factory-reset device đến `READY_TO_SHIP`.

Các bước có thể chạy lặp lại và thu thập đủ required evidence.

TBD

POC-FACTORY-002

Execute SOP trên device có failed Device Owner setup.

Device được mark `QUARANTINED` trừ khi có approved fallback.

TBD

POC-FACTORY-003

Execute SOP trên device có failed provisioning.

Device được mark `QUARANTINED`.

TBD

POC-FACTORY-004

Execute SOP trên device có recording/storage failure.

Device được mark `QUARANTINED`.

TBD

POC-FACTORY-005

Verify factory production record.

Chỉ chứa safe metadata; loại trừ raw Android ID, maintenance password, Google token/password và signing secrets.

TBD

POC-FACTORY-006

Verify SOP có thể chạy bởi Factory Operator/Admin với expected tools.

Required factory roles, tools, accounts và network assumptions được ghi nhận.

TBD

## 10. Decision Rules Based on POC Result

Result

Decision Direction

DCAM-as-DPC / Device Owner supported

Tiếp tục triển khai DCAM-owned kiosk policy implementation.

DCAM-as-DPC unsupported

Tạo ADR / fallback design; không được silently assume robust kiosk.

Lock Task unsupported/unreliable

Block production kiosk release cho đến khi có approved alternative.

Required restrictions unsupported

Mark policy degraded và để Product/Security quyết định acceptance.

Self Update silent install supported

Dùng silent/policy update path nếu security validation pass.

Self Update silent install unsupported

Dùng approved maintenance/update UX và document limitation.

Play Store unavailable

Disable Play Store fallback.

Play Store cannot be controlled

Disable Play Store fallback cho production.

GMS unavailable

Production baseline không được phụ thuộc Google APIs.

BDMA blocked by restrictions

Điều chỉnh restriction profile hoặc support procedure; không release cho đến khi BDMA path được validate.

Factory SOP cannot be completed repeatably

Không approve production shipment cho đến khi SOP/tooling/fallback được fix.

Factory production record cannot be captured safely

Không approve production shipment cho đến khi record format/tooling được fix.

## 11. Factory SOP Approval Gate

Factory SOP chỉ được dùng cho pilot/production shipment khi các POC gates dưới đây đã pass hoặc được explicitly accepted.

Gate

Required Result

Device model/firmware baseline

Model/firmware đã biết và được ghi nhận.

Device Owner / Kiosk behavior

Supported hoặc approved fallback đã được document.

Lock Task recovery

Đã verify sau boot/crash/update khi applicable.

No unrestricted Android escape

Đã verify không có unrestricted Android escape.

Web Portal business provisioning

Đã verify.

Recording/storage finalization

Đã verify.

BDMA boundary

Đã verify nếu release baseline yêu cầu.

Self Update capability

Đã verify hoặc limitation được accept rõ ràng.

Production record

Safe fields được confirm.

Quarantine behavior

Critical failures dẫn tới `QUARANTINED`.

## 12. Practical Conclusion

Device POC là bắt buộc trước release vì firmware/OEM behavior của BodyCamera quyết định implementation path thật.
Current baseline không dùng external EMM, Android Management API hoặc Managed Google Play policy-driven update.
Tính khả thi của DCAM-as-DPC / local Device Owner phải được validate.
Lock Task, User Restrictions, Home/Launcher và recovery phải được validate trên thiết bị thật.
Controlled Maintenance Mode không được expose full Android unrestricted mode.
Maintenance Password Gate phải bảo vệ kiosk exit.
Primary update path là DCAM Self Update / APK update.
Manual Play Store update chỉ là optional fallback nếu GMS/Play Store tồn tại và có thể kiểm soát được.
Nếu không kiểm soát được Play Store fallback thì phải disable cho production.
BDMA ADB import/user sync phải hoạt động dưới approved restriction profile.
Factory SOP chỉ được approve sau khi required POC gates đã closed hoặc được accept rõ ràng.
Tất cả TBD từ POC này phải feed back vào Technical Design, QA Matrix, Factory SOP và ADR nếu cần.