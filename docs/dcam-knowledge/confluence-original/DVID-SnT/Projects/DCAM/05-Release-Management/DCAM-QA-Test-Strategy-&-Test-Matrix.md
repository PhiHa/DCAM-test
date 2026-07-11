# DCAM QA Test Strategy & Test Matrix

**Page ID**: 49545345  
**Version**: 10  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/49545345

---


# DCAM QA Test Strategy & Test Matrix

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

QA Strategy / Test Matrix

Version

Approved 1.8

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / QA Lead / BDMA Lead / Security Reviewer / Cloud Lead / Android Lead / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

05 - Release Management

Target Audience

PM/BA, Tech Lead, Android Developers, QA, BDMA Team, Cloud/WebServer Team, Security Reviewer, Factory, Support, Stakeholders

Last Updated

2026-07-10

Related Jira

Không có

Related Documents

DCAM Requirements Home, DCAM Non-functional Requirements, 07 - Logging & Diagnostics Requirements, 07 - Logging, Diagnostics, Performance & Security, DCAM Logging & Diagnostics Design, DCAM Performance Budget & Resource Constraints, DCAM-BDMA Data Contract, DCAM Web Portal & Device API Contract, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM State Machine Design, DCAM Device Capability & Feature Eligibility Design, DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Device Provisioning Web Portal Implementation Design, 09 - System Settings Requirements, DCAM Self Update Design, DCAM Android Development Standard, DCAM Device POC & Hardware Validation Report, DCAM Factory Provisioning & Device Production SOP

## 1. Purpose

Tài liệu này định nghĩa QA strategy và test matrix cho **DCAM Android BodyCamera Application**.

Mục tiêu:

Xác định phạm vi test cho DCAM MVP.

Liên kết test coverage với Requirements, Architecture, Technical Design, Performance Budget, API Contract, Data Contract, Device POC và Factory SOP.

Định nghĩa test level, test environment, test device matrix, performance test group, logging/diagnostics test group và exit criteria.

Chuẩn hóa cách QA kiểm tra các luồng critical như recording, storage, user login, emergency override, BDMA import, provisioning, dedicated-device/kiosk policy, in-app operation/device/media console, controlled maintenance, update, recovery, performance, security, Operational Logging và Crash & Stability Monitoring.

Xác nhận current baseline: **không external EMM / Android Management API / Managed Google Play policy-driven update**, **Self Update / APK update là primary path**, **Play Store manual fallback là optional controlled maintenance flow**.

Current logging baseline:

Operational Logging = primary operational observability channel.
Loggly = centralized Operational Logging provider.
Firebase Crashlytics = Crash & Stability Monitoring provider.
Crashlytics does not replace Operational Logging.
Operational Logging is local-first, asynchronous and bounded.
## 2. Scope

Area

In Scope

Android Runtime

Kiểm tra startup, boot, foreground service, session restore, safe mode, policy verification, console readiness và recovery.

Logging & Diagnostics

Kiểm tra logging abstraction, local-first persistence, bounded files/queue, Backend Relay/Loggly delivery, retry, provider outage, Crashlytics fatal/non-fatal/ANR classification, sanitization và BDMA log access.

Performance Budget

Kiểm tra MVP-required performance metrics: recording latency, critical finalization, memory, storage I/O, free-space, ANR, DB busy retry và long-running stability.

Kiosk Policy

Kiểm tra DCAM-as-DPC/local Device Owner, Lock Task Mode, User Restrictions, Home/Launcher behavior, Maintenance Mode, Maintenance Password Gate và policy recovery.

No External EMM Baseline

Kiểm tra test/implementation không phụ thuộc external EMM, Android Management API hoặc Managed Google Play policy-driven update.

In-app Operation / Device / Media Console

Kiểm tra Record/Live View default screen, Setting hub, Back behavior, App Operation Settings, Device/System Settings proxy, Login Settings, Admin-only User Settings, Storage Dashboard, read-only File/Storage Manager, read-only Media Viewer và future placeholders.

Controlled Maintenance

Kiểm tra Exit Kiosk temporarily chỉ đi qua Admin/Maintenance + Maintenance Password Gate; chỉ mở approved targets; không có full unrestricted Android.

Update

Kiểm tra DCAM Self Update / APK update primary path, package validation, defer logic, policy-safe update recovery và optional manual Play Store fallback nếu được enable.

Factory Production SOP

Kiểm tra factory procedure từ raw/factory-reset BodyCamera đến quyết định READY_TO_SHIP / QUARANTINED.

User / Auth

Kiểm tra Login, no-timeout session, reboot login, Login Settings, User Settings và emergency override.

Recording / Capture

Kiểm tra normal recording, emergency recording, image/audio capture và operator attribution.

Storage

Kiểm tra internal/external storage, temp/final, `BDMA_READY`, low storage, free-space budget, read-only File/Media và recovery.

SQLite

Kiểm tra user/auth/session/media/config state, console settings, optional policy snapshot, update audit, migration, recovery, DB transaction duration và write-back.

BDMA Integration

Kiểm tra ADB discovery, media import, `.mp4` MD5 verification, user sync, logs artifact access và cleanup.

Provisioning

Kiểm tra Factory Worker QR Flow, `serial_lookup/{serial_number}`, identity restore, provisioning state và `dcam_cloud_device_id` assignment.

Remote Config

Kiểm tra fetch/cache/apply/defer/reject behavior, bao gồm kiosk requested-policy, console setting và update setting defer/reject.

Security

Kiểm tra sensitive logging, credential handling, identity protection, Maintenance Password Gate, factory Wi-Fi credential non-disclosure, Google account/manual update constraints, admin console audit và encryption direction.

Device POC Feedback

Kiểm tra POC outcomes đã được phản ánh trong test plan, release decision, Performance Budget adjustment, provider compatibility và Factory SOP.

## 3. Out of Scope

Area

Reason

Final production security certification

Cần Security Review riêng.

Full BDMA UI test

Thuộc BDMA QA scope; tài liệu này chỉ cover DCAM-facing integration.

Cloud provider production SLA

Thuộc Cloud/WebServer operation; QA chỉ verify application behavior khi provider available/unavailable.

Final hardware certification

Thuộc Device POC / Hardware Validation Report.

External EMM / Android Management API / Managed Google Play policy-driven update

Không áp dụng cho current device baseline.

Future Live Stream / PTT / AI Mode full behavior

Future requirements/designs chưa approved.

Battery/thermal numeric budget

Deferred until Device POC theo Performance Budget.

## 4. Authoritative References

Test Area

Source of Truth

Functional Requirements

DCAM Requirements Home

Non-functional Requirements

DCAM Non-functional Requirements

Logging Requirements

07 - Logging & Diagnostics Requirements

Logging Provider Ownership

07 - Logging, Diagnostics, Performance & Security

Logging Implementation

DCAM Logging & Diagnostics Design

Performance Budget

DCAM Performance Budget & Resource Constraints

Data Contract

DCAM-BDMA Data Contract

API Contract

DCAM Web Portal & Device API Contract

Android Runtime

DCAM Android Operation Design

Kiosk Policy

DCAM Android Device Owner & Kiosk Policy Design

In-app Console

DCAM In-App Operation, Device Settings & Media Console Design

Dedicated-device Decision

ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision

Recording

DCAM Recording & Capture Design

Storage

DCAM Storage Design

SQLite

DCAM SQLite Database Design

Security

DCAM Security & Encryption Design

State Guards

DCAM State Machine Design

Feature Eligibility

DCAM Device Capability & Feature Eligibility Design

Provisioning

DCAM Device Provisioning Web Portal Design + App Design + Implementation Design

Remote Config / AutoUpdate

09 - System Settings Requirements

Self Update

DCAM Self Update Design

Development Standard

DCAM Android Development Standard

Device POC Evidence

DCAM Device POC & Hardware Validation Report

Factory Acceptance / Ready-to-Ship

DCAM Factory Provisioning & Device Production SOP

## 5. Test Strategy

Test Level

Purpose

Owner

Unit Test

Kiểm tra domain logic, validators, state transitions, config validation, logging classification, sanitization, queue policy, maintenance gate validation, update validation, policy validation và eligibility rules.

Android Dev

Integration Test

Kiểm tra module boundary: DB, StorageService, RecordingController, OperatorSessionManager, KioskPolicyManager, ConsoleSettingCoordinator, SelfUpdateManager, OperationalLogger, StabilityReporter và Backend Relay client.

Android Dev / QA

Logging Integration Test

Kiểm tra local file/queue, rotation, retry, relay delivery, Loggly ingestion, Crashlytics classification, provider outage và sensitive-field filtering.

QA / Android Dev / Backend Team / Security Reviewer

Performance Test

Kiểm tra MVP-required metrics trong Performance Budget: recording latency, critical finalization, MainThread block, State Coordinator latency, DB transaction, storage I/O, free-space và stability.

QA / Android Dev

Device Test

Kiểm tra trên BodyCamera thật: camera, storage, foreground service, reboot, screen off, Lock Task, User Restrictions, in-app console, logging providers, Self Update và ADB.

QA / Android Dev

Contract Test

Kiểm tra Data Contract, API Contract, log artifact contract, file naming, MD5, DB schema, media status và BDMA read/write behavior.

QA / BDMA Team / Backend Team

Kiosk Policy Test

Kiểm tra DCAM-as-DPC/Device Owner feasibility, Lock Task, Home/Launcher, Maintenance Mode và restriction behavior.

QA / Android Dev / Security Reviewer

In-app Console Test

Kiểm tra các settings/media/storage/admin actions được phép khi Android Settings/File Manager bị hạn chế.

QA / Android Dev / Security Reviewer

Update Test

Kiểm tra Self Update/APK primary path, optional Play Store fallback nếu được enable, và không giả định Managed Google Play.

QA / Android Dev / Security Reviewer

Factory SOP Test

Kiểm tra raw/factory-reset BodyCamera có thể được chuẩn bị theo SOP và được đánh dấu đúng READY_TO_SHIP hoặc QUARANTINED.

QA / Factory / Android Dev

Recovery Test

Kiểm tra crash, reboot, service kill, DB lock/corruption, policy failure, logging provider failure, queue recovery, console setting failure và interrupted finalization/update.

QA

Security Test

Kiểm tra sensitive logging, credential handling, identity protection, Maintenance Password Gate, factory Wi-Fi password non-disclosure, Google account/manual update constraints và invalid config/package.

Security Reviewer / QA

Regression Test

Chạy trước mỗi release candidate.

QA

Acceptance Test

Kiểm tra MVP flow end-to-end, bao gồm factory-ready-to-ship path nếu release có shipment thiết bị.

PM / QA / Factory / Stakeholders

## 6. Test Environment Matrix

Environment

Purpose

Required

Android Emulator

Chỉ dùng cho early UI/domain/unit integration test.

Optional

Android Phone

Dùng cho development smoke test.

Optional

BodyCamera Device - Model A

Thiết bị target chính để test, performance baseline, logging behavior và Device POC.

Required

BodyCamera Device - Model B

Test compatibility và compare performance/provider variance.

Recommended

Non-GMS Device

Kiểm tra core app, local logging và diagnostics không phụ thuộc cứng vào GMS/Crashlytics delivery.

Required nếu target deployment bao gồm non-GMS

GMS / Play Store Device

Kiểm tra Crashlytics/provider compatibility và optional manual Play Store fallback nếu supported.

Conditional

DCAM-as-DPC / Device Owner Test Device

Kiểm tra local Device Owner/DPC/kiosk behavior without external EMM.

Required cho production kiosk profile

Factory-reset Device

Kiểm tra SOP từ raw/clean device đến ready-to-ship state.

Required trước pilot/production shipment

Windows PC with BDMA

Kiểm tra ADB import/user sync và read-only logs artifact.

Required

Web Portal Test Backend

Kiểm tra provisioning và remote config.

Required

Operational Log Backend Relay Test Environment

Kiểm tra authenticated relay, accepted/rejected payload, retry, timeout và Loggly forwarding.

Required trước production logging enablement

Loggly Test Source / Environment

Kiểm tra operational event ingestion, tags, structured fields và duplicate tolerance.

Required trước production logging enablement

Firebase Crashlytics Test Project

Kiểm tra fatal, approved non-fatal, ANR/custom context và sanitization.

Required nếu Crashlytics enabled trên target profile

Artifact Provider Test Backend

Kiểm tra Self Update manifest/APK download.

Required

Offline Environment

Kiểm tra offline-first, local login, local queue, recording và BDMA readiness.

Required

Provider Failure Environment

Simulate no network, relay unavailable, Loggly unavailable và Crashlytics unavailable.

Required

Maintenance / Factory Test Environment

Kiểm tra Maintenance Mode, gate, policy relaxation và restore.

Required cho kiosk profile

## 7. Master Test Matrix

Test ID

Area

Scenario

Priority

Source Document

Status

QA-LOG-001

Operational Logging

Operational event được persist local hoặc durable queue trước/độc lập với cloud delivery.

P0

Logging Requirements + Logging Design

Draft

QA-LOG-002

Operational Logging

Local log files và upload queue có bounded rotation/retention/size behavior; không làm đầy storage.

P0

Logging Design + Storage Design

Draft

QA-LOG-003

Backend Relay / Loggly

Authenticated Backend Relay nhận valid operational event và forward tới Loggly với structured fields/correlation context.

P0

Logging Design + API Contract

Draft

QA-LOG-004

Provider Failure

Relay/Loggly unavailable không block recording, emergency, finalization hoặc core offline operation; event được retry theo policy.

P0

Logging Requirements + Logging Design

Draft

QA-LOG-005

Crashlytics

Fatal crash được Crashlytics capture với bounded safe context khi provider available.

P0

Logging Requirements + Logging Design

Draft

QA-LOG-006

Crashlytics

Approved unexpected handled exception được record non-fatal; expected timeout/validation/retry/degraded state không tạo Crashlytics spam.

P0

Logging Design

Draft

QA-LOG-007

Sensitive Logging

Password, token, maintenance credential, factory Wi-Fi password, raw Android identifier, QR/provisioning secret và sensitive payload không xuất hiện trong local logs, queue, relay, Loggly hoặc Crashlytics.

P0

Security Design + Logging Requirements

Draft

QA-LOG-008

Non-GMS / Fallback

Khi Crashlytics/GMS/provider unavailable, local Operational Logging và BDMA diagnostics artifact vẫn hoạt động.

P0

Logging Requirements + Device POC

Draft

QA-LOG-009

BDMA Logs Contract

BDMA đọc approved `logs.txt` artifact ở chế độ read-only; không modify, truncate hoặc delete; active/rotated log handling đúng Data Contract.

P1

DCAM-BDMA Data Contract + Logging Design

Draft

QA-LOG-010

Correlation / Quality

Event có timestamp, category, stable event name, level, safe reason code và correlation identifier khi applicable.

P1

Logging Requirements + Logging Design

Draft

QA-PERF-001

Performance

Recording start latency đạt `PERF-REC-001` trên warm camera path.

P0

Performance Budget

Draft

QA-PERF-002

Performance

Recording stop latency đạt `PERF-REC-002`.

P0

Performance Budget

Draft

QA-PERF-003

Performance

Critical finalization latency đạt `PERF-REC-003`; checksum không block `BDMA_READY`.

P0

Performance Budget + Concurrency Model

Draft

QA-PERF-004

Performance

MainThread block duration đạt `PERF-ANR-001`; StrictMode debug không phát hiện disk/network trên MainThread trong critical path.

P0

Performance Budget + Android Development Standard

Draft

QA-PERF-005

Performance

State Coordinator queue latency và Camera callback processing đạt `PERF-ANR-002/003`.

P0

Performance Budget + Concurrency Model

Draft

QA-PERF-006

Performance

DB transaction duration và DB busy retry đạt `PERF-IO-004` và `PERF-ANR-007`.

P0

Performance Budget + SQLite Design

Draft

QA-PERF-007

Performance

Recording precheck enforce minimum free-space budget `PERF-STOR-001`.

P0

Performance Budget + Storage Design

Draft

QA-PERF-008

Performance

Storage full / near full không corrupt media và trigger safe stop/finalization/recovery.

P0

Performance Budget + Storage Design

Draft

QA-PERF-009

Performance

App-owned steady-state thread count đạt `PERF-STAB-005`; total process thread count được đo trong Device POC.

P1

Performance Budget + Device POC

Draft

QA-PERF-010

Performance

Continuous recording stability đạt MVP-required long-running stability metric.

P0

Performance Budget

Draft

QA-BOOT-001

Android Operation

Lần launch app đầu tiên mở DB, resolve identity và đi tới login/provisioning state.

P0

DCAM Android Operation Design

Draft

QA-KIOSK-001

Kiosk Policy

Production device boot với DCAM-as-DPC / Device Owner state bắt buộc và verify policy.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-002

Kiosk Policy

Khi Device Owner state bắt buộc bị thiếu, app vào `DEVICE_POLICY_REQUIRED` hoặc approved degraded state.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-003

Lock Task

DCAM package được allowlisted và enter Lock Task Mode sau startup.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-004

Lock Task

Home/Recents/Back behavior không cho user thoát khỏi approved kiosk experience.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-005

User Restrictions

Factory reset, app uninstall/app control và safe boot restrictions được apply hoặc unsupported reason được log.

P0

DCAM Android Device Owner & Kiosk Policy Design

Draft

QA-KIOSK-006

Maintenance Mode

Maintenance Mode được authorized có thể enter/exit, có audit và production restrictions được restore.

P0

Kiosk Policy + Security + In-App Console

Draft

QA-KIOSK-007

Policy Recovery

Khi app crash/process kill trong lúc Lock Task required, app recover và re-enter Lock Task hoặc policy recovery.

P0

Android Operation + Kiosk Policy

Draft

QA-KIOSK-008

No External EMM

Device/profile không phụ thuộc external EMM, Android Management API hoặc Managed Google Play để pass release baseline.

P0

Kiosk Policy + Self Update

Draft

QA-CONSOLE-001

In-app Console

Kiosk user không thể thoát DCAM nhưng vẫn truy cập được các in-app console functions được phép.

P0

In-App Console

Draft

QA-CONSOLE-002

Navigation

App mở `Record / Live View` mặc định; Back chuyển sang Setting và Back ở Setting quay lại Record.

P0

In-App Console

Draft

QA-CONSOLE-003

Navigation

Back từ mọi child module quay lại Setting và không bao giờ exit DCAM.

P0

In-App Console

Draft

QA-CONSOLE-004

App Operation Settings

Admin đổi video resolution khi app idle; setting được validate, persist và apply cho lần recording kế tiếp.

P0

In-App Console + System Settings + Recording Design

Draft

QA-CONSOLE-005

App Operation Settings

Admin đổi video resolution/pre-record khi đang recording; thay đổi bị defer/reject và recording hiện tại không bị gián đoạn.

P0

In-App Console + State Machine

Draft

QA-CONSOLE-006

Device/System Settings

USB/Wi-Fi/GPS control yêu cầu Admin/Maintenance authorization và không expose unrestricted Android Settings.

P0

In-App Console + Kiosk Policy Design

Draft

QA-CONSOLE-007

File/Media Manager

File Manager và Media Viewer là read-only: không delete, edit, mark important, export hoặc share.

P0

In-App Console + Security + Storage

Draft

QA-PROV-001

Provisioning

New device chưa có local identity và không restore được bằng `serial_lookup/{serial_number}` sẽ vào `PROVISIONING_REQUIRED`.

P0

Device Provisioning Web Portal Design + API Contract

Draft

QA-PROV-002

API Contract

Android/Web Portal dùng `serial_number` để restore qua `serial_lookup/{serial_number}`; không dùng `ANDROID_ID`, `android_id_hash` hoặc `device_lookup/{android_id_hash}`.

P0

DCAM Web Portal & Device API Contract

Draft

QA-PROV-003

Web Portal

Factory Worker login, scan QR displayed by DCAM, serial read-only và all post-login operations in Workspace.

P0

Web Portal Business/App/Implementation Design

Draft

QA-AUTH-001

User/Auth

Normal recording không có active operator session bị reject với `OPERATOR_AUTH_REQUIRED`.

P0

User & Device Operation Requirements

Draft

QA-AUTH-002

User/Auth

Background/foreground không logout active operator session.

P0

Android Operation Design

Draft

QA-AUTH-003

User/Auth

Device reboot làm expire previous session và yêu cầu login lại.

P0

Android Operation Design

Draft

QA-REC-001

Recording

Authenticated operator có thể start và stop normal recording.

P0

Recording & Capture Design

Draft

QA-REC-002

Recording

Emergency recording không login dùng `EMERGENCY_OVERRIDE_ADMIN`.

P0

Recording & Capture Design

Draft

QA-STO-001

Storage

In-progress file không được expose như final media.

P0

Storage Design

Draft

QA-STO-002

Storage

Finalized media chỉ trở thành `BDMA_READY` sau khi file và DB state hợp lệ.

P0

Storage Design

Draft

QA-BDMA-001

BDMA

BDMA import finalized `.mp4` và verify `.md5` nếu có.

P0

Data Contract

Draft

QA-BDMA-002

BDMA

Thiếu `.md5` cho `.mp4` được import dạng Unverified/warning, không hard fail.

P0

Data Contract

Draft

QA-DB-001

SQLite

BDMA write-back bị reject nếu schema version không supported.

P0

SQLite Database Design

Draft

QA-RCFG-001

Remote Config

Invalid config bị reject và last valid applied config vẫn active.

P0

System Settings Requirements

Draft

QA-UPD-001

Self Update

Self Update là primary update path trên no-external-EMM baseline.

P0

Self Update Design

Draft

QA-UPD-002

Self Update

Update bị defer trong lúc recording.

P0

Self Update Design

Draft

QA-FACTORY-001

Factory SOP

Raw/factory-reset device có thể được xử lý theo Factory SOP và đạt expected production state.

P0

Factory SOP

Draft

QA-FACTORY-002

Factory SOP

Approved APK version/checksum/signing metadata được verify trước khi install.

P0

Factory SOP + Self Update + Security

Draft

QA-FACTORY-003

Factory SOP

Device Owner/Lock Task/User Restrictions được verify trước khi device ready to ship.

P0

Factory SOP + Kiosk Policy

Draft

QA-SEC-001

Security

Raw Android system identifier, `ANDROID_ID` và `android_id_hash` không bị log hoặc dùng làm production lookup.

P0

Security Design

Draft

QA-SEC-002

Security

Maintenance secret values/kiosk exit credential không bị log hoặc hardcode.

P0

Security Design

Draft

QA-SEC-003

Security

Factory Wi-Fi password hardcoded exception không bị expose qua logs, Crashlytics, API, QR, production record hoặc support evidence.

P0

Security Design + Factory SOP

Draft

QA-CAP-001

Device Capability

Unsupported GPS feature bị pruned và không làm app crash.

P1

Device Capability Design

Draft

## 8. Performance Test Group

Performance Test Group chạy theo **DCAM Performance Budget & Resource Constraints**.

Test Group

MVP Required

Evidence

Recording latency

Yes

`[PERF] recording_start_latency_ms`, `recording_stop_latency_ms`, capture/finalization logs.

Critical finalization

Yes

`critical_finalization_latency_ms`, `BDMA_READY` timestamp, checksum async status.

MainThread / ANR

Yes

StrictMode debug evidence, `[PERF] main_thread_block_ms` nếu instrumented.

State Coordinator / Camera callback

Yes

`[PERF] state_coordinator_queue_latency_ms`, camera callback timing.

DB transaction / busy retry

Yes

`[PERF] db_transaction_ms`, DB busy retry logs.

Storage I/O

Yes

Sustained write benchmark, move/rename latency.

Storage free-space

Yes

Free-space precheck logs and near-full test result.

Memory leak / stability

Yes

Long-running test result, memory growth, FD leak check.

App-owned thread count

Recommended in MVP; required by Device POC

Thread ownership list and total process thread count measurement.

Battery / Thermal

Deferred

Không enforce; chỉ đo nếu Device POC có dữ liệu.

## 9. Logging & Diagnostics Test Group

Test Group

MVP Required

Evidence

Local-first persistence

Yes

Local event/file/queue evidence trước hoặc độc lập với cloud delivery.

Rotation and bounded queue

Yes

File/queue size, rotation result, retention behavior và storage pressure evidence.

Backend Relay / Loggly delivery

Yes trước production logging

Relay request/result, safe request ID và corresponding Loggly event.

Provider outage and retry

Yes

Offline/unavailable simulation, queued event, retry/recovery evidence.

Crashlytics fatal

Conditional by target provider support; required when enabled

Crash report với safe bounded keys/log context.

Crashlytics non-fatal classification

Conditional by target provider support; required when enabled

Approved unexpected exception report và evidence expected failures không tạo spam.

Sensitive-data sanitization

Yes

Automated/manual scan của local logs, queue, relay payload, Loggly và Crashlytics.

Non-GMS fallback

Required nếu target includes non-GMS

Local logs và BDMA artifact hoạt động khi provider unavailable.

BDMA logs artifact

Yes

BDMA read-only access tới `logs.txt`; rotated files theo Data Contract policy.

Test rule:

Logging provider failure is a diagnostics degradation,
not a recording/storage/emergency failure.
## 10. Factory Acceptance Test Direction

Factory SOP acceptance là bắt buộc trước pilot/production shipment.

Release-approved APK
    ↓
Factory SOP execution
    ↓
Factory acceptance checklist
    ↓
QA review
    ↓
READY_TO_SHIP hoặc QUARANTINED
Rules:

Rule

Description

QA-FACTORY-RULE-001

Factory acceptance không được pass nếu APK identity/checksum/signing/version chưa được approved.

QA-FACTORY-RULE-002

Factory acceptance không được pass nếu required kiosk policy không thể verify.

QA-FACTORY-RULE-003

Factory acceptance không được pass nếu tồn tại unrestricted Android escape.

QA-FACTORY-RULE-004

Factory acceptance không được pass nếu business provisioning fail hoặc thiếu `dcam_cloud_device_id`.

QA-FACTORY-RULE-005

Factory acceptance không được pass nếu recording/finalization fail.

QA-FACTORY-RULE-006

Factory acceptance không được pass nếu required BDMA readiness test fail.

QA-FACTORY-RULE-007

Factory acceptance không ��ược pass nếu Maintenance Password Gate fails open.

QA-FACTORY-RULE-008

Factory acceptance phải mark failed devices là `QUARANTINED`.

QA-FACTORY-RULE-009

Factory acceptance phải record Device POC/performance evidence nếu build dùng cho pilot hardware baseline.

QA-FACTORY-RULE-010

Logging provider temporarily unavailable không tự động làm device fail acceptance nếu local diagnostics vẫn hoạt động và release policy không yêu cầu cloud delivery tại factory; deviation phải được ghi nhận.

## 11. Regression Test Set

Regression Group

Must Run Before Release

Startup / Login

Có

Operational Logging local-first

Có

Logging queue rotation / bounded storage

Có

Sensitive Logging sanitization

Có

Backend Relay / Loggly

Có khi production logging enabled

Crashlytics fatal/non-fatal classification

Conditional theo target/provider support

Logging provider outage / recovery

Có

BDMA logs artifact read-only

Có

Performance Budget MVP subset

Có

DCAM-as-DPC / Kiosk Policy

Có

Lock Task Recovery

Có

Maintenance Password Gate

Có

Controlled Maintenance / No unrestricted Android

Có

In-app Console Navigation

Có

In-app Console Access

Có

App Operation Settings Guard

Có

File/Storage Manager / Media Viewer Read-only

Có

Normal Recording

Có

Emergency Recording

Có

Storage Finalization

Có

BDMA Import

Có

SQLite Migration

Có

Web Portal / Device API Contract

Có

Factory Worker QR-only Workspace flow

Có

Serial lookup / identity restore

Có

Reboot Recovery

Có

Remote Config Apply/Reject

Có

Self Update / APK Validation

Có

Self Update Defer / Policy-safe Update

Có

No Managed Google Play Assumption

Có

Factory SOP Acceptance

Có trước pilot/production shipment

Optional Play Store Fallback

Conditional

Device Capability Pruning

Có

## 12. Exit Criteria

Criteria

Required

All P0 test cases passed

Có

Operational Logging local-first, bounded queue và provider outage tests passed

Có

Sensitive values absent from local logs, relay, Loggly and Crashlytics

Có

Crashlytics fatal/non-fatal classification passed when provider enabled

Conditional

BDMA logs artifact contract passed

Có

MVP-required Performance Budget metrics passed or documented with approved Device POC adjustment

Có

No open P0 bug

Có

P1 bugs reviewed and accepted

Có

Recording/capture regression passed on target BodyCamera

Có

Kiosk policy regression passed on target BodyCamera

Có

In-app console P0 tests passed under kiosk mode

Có

Controlled Maintenance / Maintenance Password Gate passed

Có

No unrestricted Android escape passed

Có

Web Portal / Device API Contract P0 tests passed

Có

Factory Worker QR-only Login/Workspace flow passed

Có

`serial_lookup/{serial_number}` identity restore and provisioning baseline passed

Có

Self Update primary path passed

Có

Managed Google Play / policy-driven update marked not applicable for current baseline

Có

Factory SOP acceptance passed for shipment build

Có trước pilot/production shipment

Production record format reviewed for safe metadata only

Có trước pilot/production shipment

Optional Play Store fallback either passed if enabled or documented as unavailable

Conditional

BDMA import smoke test passed under approved restriction profile

Có

Reboot/session/policy recovery test passed

Có

Release checklist completed

Có

## 13. Practical Conclusion

Tài liệu này là **QA source of truth** cho DCAM release readiness.

Requirements định nghĩa DCAM phải làm gì.
Architecture định nghĩa provider ownership và boundaries.
Technical Design định nghĩa DCAM nên hoạt động như thế nào.
Performance Budget định nghĩa metric đo nhanh/chậm/ổn định.
QA Test Strategy & Test Matrix định nghĩa cách verify DCAM behavior.
Factory SOP định nghĩa cách một thiết bị vật lý trở thành ready to ship.
Release Management quyết định build/device đã sẵn sàng release hay chưa.
P0 logging/diagnostics baseline:

Operational Logging is local-first.
Loggly is centralized Operational Logging provider.
Crashlytics is Crash & Stability Monitoring provider.
Provider outage must not block core operation.
Expected operational failures must not create Crashlytics noise.
Sensitive values must not appear in any logging channel.
BDMA can read the approved logs artifact in read-only mode.
P0 MVP coverage tiếp tục bao gồm:

startup
performance budget MVP subset
DCAM-as-DPC / Device Owner feasibility
Lock Task Mode
User Restrictions
in-app console navigation and access
read-only File/Media manager
Maintenance Password Gate
Controlled Maintenance with no unrestricted Android
Factory Worker QR-only Web Portal provisioning
serial_lookup identity restore
Web Portal & Device API Contract
login and user settings
normal recording
emergency override
storage finalization
BDMA import
SQLite recovery
remote config reject/defer
Self Update / APK update primary path
no Managed Google Play assumption
policy-safe update
factory SOP acceptance
safe production record
logging and diagnostics