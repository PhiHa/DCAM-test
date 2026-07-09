# 06 - Cloud Services, Update & Configuration Architecture

**Page ID**: 47120459  
**Version**: 26  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47120459

---


# 06 - Cloud Services, Update & Configuration Architecture

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Software Architecture Document / Cloud, Update & Configuration Architecture

Version

Approved 3.1

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Cloud Lead / Security Reviewer / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.1 - Software Architecture

Target Audience

PM/BA, Tech Lead, Android Developers, AI/ML Engineer, QA, BDMA Team, Cloud/WebServer Team

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, DCAM Architecture Home, 04 - Device Configuration Requirements, 09 - System Settings Requirements, DCAM Device Provisioning Web Portal Design, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM Android Operation Design, DCAM SQLite Database Design, DCAM Security & Encryption Design, DCAM Self Update Design, DCAM Device Capability & Feature Eligibility Design, DCAM State Machine Design

## 1. Purpose

Trang này mô tả kiến trúc cloud provider, Firebase Cloud Firestore storage baseline, device identity, Web Provisioning Portal boundary, remote configuration, kiosk requested-policy boundary, update provider và future WebServer/service adapters của DCAM.

Tài liệu này follows **DCAM Factory Provisioning & Device Production SOP** làm chuẩn định danh thiết bị:

textwide760Tài liệu này chỉ định nghĩa **architecture boundary**. API/data schema chi tiết thuộc **DCAM Web Portal & Device API Contract**. Luồng nghiệp vụ chi tiết của Web Portal Provisioning thuộc **DCAM Device Provisioning Web Portal Design**. Chi tiết Device Owner / Lock Task / User Restrictions thuộc **DCAM Android Device Owner & Kiosk Policy Design**. Chi tiết Self Update / APK install flow thuộc **DCAM Self Update Design**.

Current baseline:

textwide760## 2. Authoritative References

Topic

Authoritative Document

Local Summary

Factory provisioning, DSetup, serial injection and SD Identity File

DCAM Factory Provisioning & Device Production SOP

DSetup resolves serial from SD Identity File or barcode and injects serial into DCAM.

Device config file and device information requirement

04 - Device Configuration Requirements

`serial_number` là Hardware Identity/recovery key; owner name và manufacture date là device information.

API/data contract and Firestore collection/document direction

DCAM Web Portal & Device API Contract

Source of truth cho API/path/schema/error code và Firestore collection/document baseline.

Device provisioning business flow, screens, QR, states and audit

DCAM Device Provisioning Web Portal Design

Source of truth cho DCAM Web Portal business provisioning flow.

Kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

Source of truth cho DCAM-as-DPC/local Device Owner, Lock Task, User Restrictions, Home/Launcher, Maintenance Mode, no external EMM baseline and policy recovery.

In-app console / controlled maintenance

DCAM In-App Operation, Device Settings & Media Console Design

Source of truth for Setting hub, Controlled Mode, Maintenance Password Gate and optional manual Play Store fallback UX.

Remote config validation and setting apply

09 - System Settings Requirements

Remote config chỉ cung cấp requested values; DCAM validate/cache/apply theo runtime guard.

Kiosk requested-policy settings

09 - System Settings Requirements

Cloud/WebServer may publish requested kiosk policy values; Android validates and applies only when policy authority and runtime guard allow.

Device identity local persistence

DCAM SQLite Database Design

Lưu `dcam_cloud_device_id`, `serial_number`, device information, provisioning state và config cache.

Startup restore/provisioning/kiosk verification runtime

DCAM Android Operation Design

Android startup xử lý local identity, serial-based recovery, provisioning-required state and kiosk policy verification.

Identity/provisioning/security/policy security

DCAM Security & Encryption Design

Không dùng/log Android system identifier; provisioning, policy, update and maintenance actions must be auditable.

Feature eligibility and runtime pruning

DCAM Device Capability & Feature Eligibility Design

Remote config/update does not override capability; GMS/Play Store availability is capability-detected.

AutoUpdate preconditions

09 - System Settings Requirements

Cloud/update flow phải check approved preconditions from System Settings.

Self Update download/validation/install flow

DCAM Self Update Design

APK artifact provider is primary current update path.

Update state machine / priority

DCAM State Machine Design

Update has lower priority than recording, emergency, finalizing, policy recovery and unsafe runtime states.

DCAM-BDMA compatibility contract

DCAM-BDMA Data Contract

App/data/media/encoder contract version dùng để BDMA nhận dạng app; không dùng `bdma_decoder_profile_id`.

## 3. Core Decisions

Area

Decision

Status

Cloud Storage Baseline

Firebase Cloud Firestore is the selected backend storage baseline.

Approved

Realtime Database

Firebase Realtime Database is not used for current backend storage.

Not Applicable

Cloud Provider Boundary

DCAM should keep provider interfaces so implementation can use Firestore now and adapt later if required.

Approved

Device Primary Key

Firestore/WebServer primary key là `dcam_cloud_device_id`.

Approved

Hardware Identity

`serial_number` là Hardware Identity / primary recovery key.

Approved

Recovery Lookup

`serial_lookup/{serial_number}` được dùng để create/restore existing `dcam_cloud_device_id`.

Approved

SD Identity File

SD Identity File là recovery cache trên thẻ nhớ ngoài; không phải Hardware Identity.

Approved

Owner Name

`owner_name` là mutable/semi-static device information, không phải primary key.

Approved

Manufacture Date

`manufacture_date` là semi-static device information, format `YYYY-MM-DD`, không phải primary key.

Approved

Android ID

`ANDROID_ID`, `android_id_hash` và `device_lookup/{android_id_hash}` không dùng trong current production baseline.

Approved

Advertising ID

Advertising ID không được dùng làm DCAM identity key.

Approved

DCAM Business Provisioning Method

Default factory DCAM business provisioning dùng serial-number based Web Portal/Firebase flow.

Approved

Device Owner / Kiosk Setup

Device Owner/DPC setup is separate from DCAM Web Portal business provisioning and must be validated by Device POC.

Approved Direction

External EMM / Managed Google Play

Not current baseline. Do not assume Android Management API or Managed Google Play policy-driven update.

Approved Direction

Provisioning Business Flow

Detailed flow, screens, QR, states và audit thuộc DCAM Device Provisioning Web Portal Design.

Approved

API / Firestore Contract

API/path/schema/error code and Firestore collection/document direction belong to DCAM Web Portal & Device API Contract.

Approved

Remote Config

Provider publishes target config revision; DCAM fetch/validate/cache/apply safely. Initial setting groups are defined; exact field-level payload schema remains open.

Approved Baseline / Exact Fields TBD

Kiosk Requested Policy

Cloud/WebServer may publish requested kiosk policy values, but Android validates policy authority and runtime guard before apply. Initial key groups are defined; exact field-level schema remains open.

Approved Direction / Exact Fields TBD

Capability Boundary

Capability/eligibility dùng Device Capability Design, không copy state list tại đây.

Approved

App / Contract Compatibility

Firestore/WebServer may store app/version/data/media/encoder contract metadata for BDMA/support compatibility checks.

Approved Direction

BDMA Decoder Profile

`bdma_decoder_profile_id` is not used; BDMA uses built-in logic based on app + contract version.

Approved

App Update

Primary path is DCAM Self Update / APK update via approved artifact provider.

Approved Direction

Optional Manual Play Store Fallback

Optional only if GMS/Play Store exists and approved maintenance/factory account/process exists.

Conditional / POC Required

Policy-safe Update

Update must satisfy System Settings preconditions and Kiosk Policy Design constraints.

Approved Direction

Future Model Update

Model package nếu có phải được validate và check compatibility.

Future / TBD

Future WebServer

Live Streaming, PTT, SOS, JT808 và optional server analytics đi qua adapter.

Future / Approved Direction

## 4. Device Identity Model

DCAM sử dụng stable server-side device record và serial-number based recovery lookup.

Field

Role

Storage

`dcam_cloud_device_id`

Firestore/WebServer primary cloud key.

Cloud Firestore `devices/{dcam_cloud_device_id}` + `dcam.db`.

`serial_number`

Hardware Identity / primary recovery key.

App-private storage, `dcam_config.cson`, `dcam.db` mirror, Cloud Firestore, SD Identity File cache.

`serial_lookup/{serial_number}`

Recovery/create/restore lookup mapping.

Cloud Firestore `serial_lookup/{serial_number}` &rarr; `dcam_cloud_device_id`.

SD Identity File

External SD card recovery cache containing `serial_number`.

`<SD_CARD>/DCAM_FACTORY/device_identity.json` or approved equivalent.

`owner_name`

Owner/customer/agency display information.

`dcam_config.cson`, `dcam.db` mirror, Cloud Firestore.

`manufacture_date`

Device manufacture date using ISO `YYYY-MM-DD`.

`dcam_config.cson`, `dcam.db` mirror, Cloud Firestore.

`serial_history`

Lịch sử serial values if approved rework/admin flow changes serial.

Cloud Firestore, optional DB mirror.

`firebase_installation_id`

Current app-install instance metadata.

Cloud Firestore + `dcam.db` metadata. Not a device identity key.

`app_code` / `app_package_name`

App identity metadata.

Cloud Firestore + optional local mirror.

`dcam_data_contract_version`

Overall DCAM-BDMA contract version.

Cloud Firestore + optional local mirror.

`media_contract_version`

Media folder/naming/checksum/import/cleanup version.

Cloud Firestore + optional local mirror.

`encoder_contract_version`

Fixed DCAM encoder contract version.

Cloud Firestore + optional local mirror.

Rules:

textwide760## 5. First Install / Identity Recovery Flow

textwide760Device information restored from server may include:

textwide760Factory reset recovery:

textwide760## 6. Web Provisioning Portal Boundary

Default DCAM business provisioning method cho new/reworked BodyCamera devices là **serial-number based Web Provisioning Portal / Firebase flow**.

Architecture-level summary:

textwide760Important clarification:

textwide760## 7. Remote Configuration and Kiosk Requested Policy Flow

Remote config core flow, identity boundary, cache/apply policy and initial setting groups are defined. Exact field-level payload schema and field names remain TBD for System Settings / future Remote Config design.

textwide760Kiosk requested-policy may include:

textwide760Important distinction:

textwide760## 8. Provider Abstraction

textwide760Provider abstraction giúp DCAM không để Android runtime bị coupling cứng vào một implementation cụ thể, dù current storage baseline đã chốt là Cloud Firestore.

## 9. Update Architecture Alignment

Current baseline:

textwide760Self Update architecture:

textwide760Cloud/update architecture must not bypass Lock Task / Maintenance Mode constraints.

## 10. Manual Google Play Store Fallback Boundary

Manual Google Play Store update is not the production baseline. It is a controlled fallback only.

Rule

Direction

PLAY-FB-001

Only available if target BodyCamera has GMS/Play Store and Product/Security approve fallback.

PLAY-FB-002

Must be launched only from Admin / Maintenance Controlled Mode after Maintenance Password Gate.

PLAY-FB-003

Only DCAM/approved apps may be updated.

PLAY-FB-004

Personal Google account is not allowed for production maintenance.

PLAY-FB-005

Approved maintenance/factory Google account handling is TBD and must be audited if used.

PLAY-FB-006

Must not permit unrestricted Play Store browsing/install.

PLAY-FB-007

After fallback update, return to DCAM and restore Lock Task/User Restrictions.

## 11. Open Items / TBD

Item

Status

Exact Firestore collection/document names

TBD trong API Contract

Exact Firestore security rules / backend service authorization boundary

TBD trong API Contract + Security

REST backend vs direct Firestore SDK/API usage

TBD trong API Contract + Backend

Exact SD Identity File path/schema/signature/checksum policy

TBD trong Factory SOP + Security

Exact remote config payload fields

Initial setting groups defined in System Settings; exact field-level schema TBD

Exact kiosk policy payload fields

Initial requested-policy keys defined in System Settings/Kiosk Policy Design; exact field-level schema TBD

Config profile schema and rollout algorithm

TBD

FCM/direct/topic wake-up policy

TBD

Polling interval exact value

TBD

Web Portal detailed UI wireframe

TBD trong Web Portal Provisioning Design

QR payload signature/expiration format

TBD trong Web Portal Provisioning Design

DCAM-as-DPC / Device Owner setup method

TBD trong Kiosk Policy Design / Device POC

Owner name validation rules

TBD / Admin UX

Manufacture date source and correction policy

TBD / Admin UX

Self Update artifact provider exact implementation

TBD / Self Update Design

APK validation details and silent install feasibility

TBD / Self Update + Device POC

Manual Play Store fallback availability

TBD / Device POC

Maintenance/factory Google account handling

TBD / Security + Product

Policy-safe update / maintenance window contract

TBD / Kiosk Policy + Self Update

## 12. Practical Conclusion

textwide760