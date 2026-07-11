# DCAM Security & Encryption Design

**Page ID**: 48496720  
**Version**: 12  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48496720

---


# DCAM Security & Encryption Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 1.2

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer / BDMA Lead / Cloud Lead / Android Lead / Web Portal Lead / Factory Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, Web Portal Developers, Backend Developers, BDMA Developers, QA, Security Reviewer, Support, Factory, Cloud/WebServer Team

Last Updated

2026-07-10

Related Jira

None

Related Documents

08 - Security & Encryption Requirements, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, 09 - System Settings Requirements, 10 - Android Device Operation Requirements, DCAM Device Provisioning Web Portal Design, DCAM Device Provisioning Web Portal App Design, DCAM Device Provisioning Web Portal Implementation Design, DCAM Web Portal & Device API Contract, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Self Update Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, ADR - DCAM Device Identity Baseline: serial_number + dcam_cloud_device_id, DCAM-BDMA Data Contract, DCAM SQLite Database Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM State Machine Design, DCAM Logging & Diagnostics Design, DCAM QA Test Strategy & Test Matrix, DCAM Factory Provisioning & Device Production SOP, 07 - Logging & Diagnostics Requirements

## 1. Purpose

**DCAM Security & Encryption Design** định nghĩa security direction cho DCAM Android và Web Portal provisioning, bao gồm device identity handling, device information security, `Factory Worker` authentication/authorization, QR-based provisioning security, factory Wi-Fi credential decision, kiosk policy security, in-app console security, Maintenance Password Gate, operator authentication methods, emergency override audit behavior, Self Update/package validation, optional manual Google Play Store fallback constraints, sensitive logging, encrypted media direction và BDMA/WebServer compatibility.

Current baseline:

No external EMM.
No Android Management API.
No Managed Google Play policy-driven update.
Primary update path = DCAM Self Update / APK update.
Manual Google Play Store update = optional controlled maintenance fallback only if approved.
Web Portal user-facing account type = Factory Worker only.
Web Portal screens = Login and Workspace only.
Web Portal serial source = provisioning QR displayed by DCAM only.
Factory Wi-Fi SSID/password hardcoded in approved DCAM APK = approved project decision.
Tài liệu này không redefine user-management requirement, DB schema, provisioning API, remote config payload, kiosk policy detail hoặc BDMA Data Contract. Tài liệu này định nghĩa security constraints mà các tài liệu đó phải tuân theo.

## 2. Authoritative References

Topic

Authoritative Document

Local Usage

Device identity, device information and CSON scope

04 - Device Configuration Requirements

Security Design ràng buộc identifier/device-info handling và logging.

Provisioning business flow and actor model

DCAM Device Provisioning Web Portal Design

Security Design áp dụng `Factory Worker`, QR-only serial source và Login/Workspace baseline.

Web App UI/security behavior

DCAM Device Provisioning Web Portal App Design

Security Design ràng buộc read-only serial, camera privacy, no override và safe error behavior.

Web App implementation stack

DCAM Device Provisioning Web Portal Implementation Design

Security Design áp dụng Firebase Authentication, Cloud Functions backend authority và no direct production Firestore write từ frontend.

Provisioning API and serial lookup contract

DCAM Web Portal & Device API Contract

Security Design phải tuân theo `serial_lookup/{serial_number}` và không dùng `device_lookup/{android_id_hash}`.

Factory identity/provisioning and Wi-Fi baseline

DCAM Factory Provisioning & Device Production SOP

Security Design áp dụng serial baseline, DSetup boundary và approved hardcoded factory Wi-Fi decision.

Kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

Security Design ràng buộc kiosk exit, Maintenance Mode, restriction changes và no-external-EMM baseline.

In-app console and Maintenance Password Gate UX

DCAM In-App Operation, Device Settings & Media Console Design

Security Design ràng buộc access/auth/audit behavior for console actions.

Self Update / APK update

DCAM Self Update Design

Security Design ràng buộc APK source, identity, checksum, signature, install safety and failure behavior.

Remote config apply policy

09 - System Settings Requirements

Security Design ràng buộc validation/audit behavior.

User/operator requirement and login policy

05 - User & Device Operation Requirements

Security Design áp dụng protection requirements cho authentication methods và sessions.

User/auth/session/identity DB tables

DCAM SQLite Database Design

Security Design ràng buộc dữ liệu được store và representation của sensitive values.

User/operator sync and BDMA write-back boundary

DCAM-BDMA Data Contract

Security Design ràng buộc safe sync/write-back behavior.

Android startup/session/provisioning/kiosk lifecycle

DCAM Android Operation Design

Security Design hỗ trợ identity restore, policy verification và reboot-login-required policy.

Recording operator attribution and emergency override

DCAM Recording & Capture Design

Security Design bảo vệ auditability của operator attribution.

Operational logging and Crashlytics

DCAM Logging & Diagnostics Design

Security Design sở hữu sensitive-field restrictions; Logging Design sở hữu routing/sanitization implementation.

QA release validation

DCAM QA Test Strategy & Test Matrix

Security Design cung cấp security baseline để QA verify.

## 3. Security Principles

Principle

Direction

Offline-first authentication

Operator authentication phải hoạt động without Internet/cloud dependency sau khi device đã provisioned và local data tồn tại.

Stable cloud identity

Cloud/WebServer primary device id là `dcam_cloud_device_id`; không phụ thuộc owner name, manufacture date hoặc Android system identifier.

Stable hardware recovery identity

Hardware Identity / primary recovery key là `serial_number`.

Recovery cache is not identity

SD Identity File chỉ là recovery cache.

Identifier minimization

Không expose hoặc log unnecessary device identifiers.

Device information minimization

`owner_name` và `manufacture_date` không được dùng như identity key hoặc credential.

Protected credential representation

Password, pattern, login QR/NFC token, face auth material và Maintenance Password Gate credential phải dùng approved protected representation.

Backend authority

Frontend không được tự quyết định authorization, duplicate/rebind hoặc ghi trực tiếp production provisioning records.

Auditability

Provisioning, serial correction/rebind, config apply, kiosk policy, Maintenance Mode, update, login, user sync và emergency override phải auditable.

Evidence preservation

Security/policy failure không được silently delete hoặc modify recorded evidence.

Runtime safety

User/auth/config/policy/update changes chỉ apply khi runtime guard cho phép.

Kiosk fail closed

Required kiosk policy failure không được làm thiết bị rơi về unrestricted field operation.

Maintenance fail closed

Failed maintenance credential validation không được relax restrictions.

Update fail closed

Failed update validation/install không được leave device unrestricted.

No personal account dependency

Production maintenance/update không phụ thuộc personal Google account.

Explicit exception control

Approved exception như hardcoded factory Wi-Fi credential phải được ghi rõ scope, risk, handling và rotation boundary; không được mở rộng thành general permission để hardcode secret khác.

## 4. Device Identity and Device Information Security

Approved identity model:

Cloud Identity / primary cloud device id = dcam_cloud_device_id
Hardware Identity / primary recovery key = serial_number
Approved recovery lookup = serial_lookup/{serial_number}
SD Identity File = recovery cache only
ANDROID_ID = not used
android_id_hash = not used
Deprecated endpoint = device_lookup/{android_id_hash}
Mutable device information = owner_name
Semi-static device information = manufacture_date

Rule

Description

SEC-ID-001

`serial_number` không được dùng làm Firebase/WebServer primary document id cho device record; primary cloud key là `dcam_cloud_device_id`.

SEC-ID-002

`owner_name` và `manufacture_date` không được dùng làm primary key.

SEC-ID-003

Advertising ID không được dùng làm primary key hoặc recovery key.

SEC-ID-004

`ANDROID_ID` và Android system identifier không được dùng làm production identity, lookup input hoặc recovery key.

SEC-ID-005

Raw Android system identifier không được ghi vào logs, Crashlytics hoặc audit.

SEC-ID-006

`android_id_hash` không được dùng trong current production baseline.

SEC-ID-007

`dcam_cloud_device_id` được lưu locally sau provisioning/restore và dùng cho cloud requests.

SEC-ID-008

Serial correction/rebind phải auditable và preserve previous serial history ở server side.

SEC-ID-009

Owner/manufacture date change phải auditable.

SEC-ID-010

`manufacture_date` dùng format `YYYY-MM-DD`.

SEC-ID-011

`bdma_decoder_profile_id` không thuộc identity/security contract.

SEC-ID-012

`device_lookup/{android_id_hash}` không được dùng.

SEC-ID-013

`serial_lookup/{serial_number}` là approved recovery/create/restore lookup.

SEC-ID-014

SD Identity File không được promote thành authoritative identity nếu conflict với server record.

## 5. Web Portal Provisioning Security

Approved Web Portal baseline:

User-facing account type = Factory Worker only.
Screens = Login and Workspace only.
Factory Worker scans the provisioning QR displayed by DCAM.
serial_number is obtained only from the QR payload.
serial_number is read-only in Workspace.
No manual serial entry.
No direct serial barcode scan in Web Portal.
Frontend uses Firebase Authentication.
Cloud Functions/backend validates authorization and owns create/restore/audit.
Frontend does not directly write Serial Lookup, Devices or Audit Events.
Important boundary:

Web Portal QR Flow provisions DCAM business identity and device information.
It does not make DCAM Device Owner.
It does not perform production acceptance.

Rule

Description

SEC-PROV-001

Chỉ authenticated và active `Factory Worker` được mở provisioning Workspace và submit request.

SEC-PROV-002

Backend phải verify Firebase identity token, worker profile, active status và `Factory Worker` authorization; không tin role/worker id từ request body.

SEC-PROV-003

Web client phải gọi Cloud Functions/backend; frontend không direct-write production provisioning collections.

SEC-PROV-004

`serial_number` chỉ lấy từ provisioning QR displayed by DCAM và phải read-only trong UI.

SEC-PROV-005

Web Portal không có manual serial input hoặc direct serial barcode fallback.

SEC-PROV-006

QR phải include payload type/version; signature/nonce/expiration/replay policy vẫn thuộc API Contract/Security decision.

SEC-PROV-007

QR không được chứa long-lived secret, auth token, maintenance credential, factory Wi-Fi password hoặc Google credential.

SEC-PROV-008

Camera stream/image dùng để scan QR không được lưu hoặc upload.

SEC-PROV-009

Provisioning action phải auditable với worker identity, time, request id, `dcam_cloud_device_id`, `serial_number`, changed fields, result và safe reason code.

SEC-PROV-010

Nếu serial lookup/provisioning fails, DCAM vẫn ở provisioning-required/recovery state.

SEC-PROV-011

Factory Worker không được override duplicate, rebind, disabled, revoked hoặc quarantined device state.

SEC-PROV-012

Rebind/conflict resolution phải đi qua separate support/admin process và audit, ngoài user-facing Web Portal App.

SEC-PROV-013

API phải reject/ignore `bdma_decoder_profile_id`, `ANDROID_ID`, `android_id_hash` và `device_lookup/{android_id_hash}`.

SEC-PROV-014

Login/error UI không được tiết lộ account existence, raw Firebase error, backend stack trace hoặc authorization configuration.

SEC-PROV-015

Logout/session expiration phải clear current Workspace provisioning state và block submit.

## 6. Factory Wi-Fi Credential Security

Current approved project decision:

Factory Wi-Fi SSID/password is hardcoded in the approved DCAM APK.
DCAM configures factory Wi-Fi after serial_number import/validation.
Đây là một **explicit project exception** cho factory provisioning baseline. Exception này không cho phép hardcode các secret khác như Maintenance Password, cloud token, Firebase service credential, signing private key hoặc Google credential.

Rule

Description

SEC-WIFI-001

Factory Wi-Fi SSID/password chỉ được tồn tại trong release-approved DCAM APK/build configuration theo approved factory baseline.

SEC-WIFI-002

Factory Wi-Fi password không được đưa vào `dcam_config.cson`, `dcam.db`, QR payload, Web Portal request, API response hoặc production record.

SEC-WIFI-003

Factory Wi-Fi password không được ghi vào Operational Logging, Loggly, Crashlytics custom logs/keys, support logs, screenshots hoặc evidence attachment.

SEC-WIFI-004

UI không được hiển thị plaintext factory Wi-Fi password cho Factory Worker/operator.

SEC-WIFI-005

Source repository/build pipeline access chứa credential phải giới hạn cho approved personnel/process.

SEC-WIFI-006

APK obfuscation có thể giảm accidental disclosure nhưng không được coi là cryptographic protection; extraction risk được project chấp nhận theo quyết định hiện tại.

SEC-WIFI-007

Credential rotation yêu cầu tạo và phát hành approved APK mới hoặc approved secure replacement mechanism trong future design.

SEC-WIFI-008

Factory Wi-Fi credential chỉ dùng trong factory environment; không được reuse cho customer/production field network.

SEC-WIFI-009

Nếu credential bị lộ hoặc network bị compromise, Security/Factory phải rotate credential và phát hành replacement build/process.

SEC-WIFI-010

Security review phải đánh giá lại exception trước production scale-up hoặc khi network topology thay đổi.

## 7. Kiosk Policy Security

Detailed policy behavior thuộc **DCAM Android Device Owner & Kiosk Policy Design**.

Rule

Description

SEC-KIOSK-001

Chỉ approved admin/support flow mới được thoát Lock Task hoặc vào Maintenance Mode.

SEC-KIOSK-002

Maintenance Mode entry/exit phải auditable.

SEC-KIOSK-003

User Restrictions không được clear silently.

SEC-KIOSK-004

Policy failure không được mở thiết bị về unrestricted production field operation.

SEC-KIOSK-005

Kiosk exit / Maintenance credential không được hardcode, plaintext hoặc logged.

SEC-KIOSK-006

Emergency override không được cấp interactive admin UI hoặc Maintenance Mode access.

SEC-KIOSK-007

Lock Task allowlist changes phải auditable và chỉ include approved packages.

SEC-KIOSK-008

Device Owner/DPC missing state phải log safe reason code.

SEC-KIOSK-009

Maintenance Mode không được interrupt active recording/emergency/finalization ngoài approved safe-stop policy.

SEC-KIOSK-010

Policy restore sau Maintenance Mode/update/reboot phải verified và logged.

SEC-KIOSK-011

Enter Maintenance Mode / Exit Kiosk temporarily phải require Maintenance Password Gate.

SEC-KIOSK-012

Failed gate validation không được stop Lock Task, relax restrictions hoặc open Android Settings.

SEC-KIOSK-013

Repeated failed attempts phải rate-limit/cooldown/lockout theo approved policy.

SEC-KIOSK-014

Full Android unrestricted mode không được hỗ trợ.

SEC-KIOSK-015

Controlled Mode chỉ mở approved targets.

SEC-KIOSK-016

External EMM / Android Management API không được required cho current baseline.

## 8. Maintenance Password Gate Security

### 8.1 Required Behavior

Admin / Maintenance action requested
    ↓
Validate caller role
    ↓
Prompt Maintenance Password Gate
    ↓
Validate protected credential
    ↓
If success and runtime guard is safe:
        allow Controlled Maintenance Mode
If failure:
        keep Lock Task and restrictions active
        audit safe reason code
### 8.2 Credential Rules

Rule

Description

SEC-MAINT-001

Maintenance password là dedicated maintenance credential hoặc approved maintenance secret.

SEC-MAINT-002

Normal operator password không đủ để exit kiosk.

SEC-MAINT-003

Admin login alone không bypass gate trừ future ADR.

SEC-MAINT-004

Maintenance password không được hardcode trong source, resources, config, CSON, remote config hoặc logs.

SEC-MAINT-005

Không lưu plaintext trong DB, preferences, files, logs hoặc crash reports.

SEC-MAINT-006

Chỉ lưu approved protected representation.

SEC-MAINT-007

Dùng Android Keystore/hardware-backed protection nếu available và phù hợp.

SEC-MAINT-008

Validation dùng safe comparison khi applicable.

SEC-MAINT-009

Reset/recovery yêu cầu approved Admin/Security process và audit.

SEC-MAINT-010

Rotation policy là TBD.

SEC-MAINT-011

Emergency override không được access gate.

### 8.3 Attempt, Lockout and Audit Rules

Rule

Description

SEC-MAINT-012

Failed attempts tăng non-sensitive failed-attempt metadata.

SEC-MAINT-013

Repeated failures trigger delay/cooldown/temporary lockout/escalation.

SEC-MAINT-014

Failure không reveal partial credential correctness.

SEC-MAINT-015

Audit có actor/source/time/result/reason code, không có secret.

SEC-MAINT-016

Failed attempt không alter kiosk state.

SEC-MAINT-017

Maintenance session có timeout/inactivity policy và restore kiosk.

SEC-MAINT-018

Maintenance credential state không sync sang BDMA.

## 9. Update and Runtime Package Security

Primary update path = DCAM Self Update / APK update.
Managed Google Play / Android Management API policy-driven update = not applicable.
Manual Google Play Store update = optional controlled fallback only if approved.

Rule

Description

SEC-UPD-001

APK phải match expected package identity.

SEC-UPD-002

APK phải pass checksum/integrity validation.

SEC-UPD-003

APK signature phải trusted và compatible.

SEC-UPD-004

APK source phải trusted; arbitrary user APK không allowed.

SEC-UPD-005

Update phải preserve Device Owner/DPC, Lock Task recovery và User Restrictions baseline.

SEC-UPD-006

Install failure không được leave device unrestricted.

SEC-UPD-007

Update không chạy trong recording, emergency, finalization hoặc recovery unsafe state.

SEC-UPD-008

Update/maintenance path không bypass Maintenance Password Gate.

SEC-UPD-009

Managed Google Play policy request bị reject/ignore cho current baseline.

SEC-UPD-010

Downgrade/rollback phải explicitly approved và audited.

SEC-UPD-011

APK release process chứa hardcoded factory Wi-Fi credential phải tuân theo `Factory Wi-Fi Credential Security`.

## 10. Optional Manual Google Play Store Fallback Security

Rule

Description

SEC-PLAY-001

Play Store chỉ mở qua Controlled Maintenance sau Maintenance Password Gate.

SEC-PLAY-002

Chỉ allowed nếu device có GMS/Play Store và Product/Security approve.

SEC-PLAY-003

Chỉ DCAM/approved apps được update; general browsing/install không allowed.

SEC-PLAY-004

Không dùng personal Google account.

SEC-PLAY-005

Approved maintenance/factory account handling phải auditable.

SEC-PLAY-006

Google password/token không được logged.

SEC-PLAY-007

Account persistence theo approved decision.

SEC-PLAY-008

Sau update phải return DCAM và restore kiosk policy.

SEC-PLAY-009

Fallback không được trở thành unrestricted mode.

## 11. Remote Config Security

Area

Security Requirement

Identity

Fetch config bằng `dcam_cloud_device_id`.

Validation

Validate schema/version/allowed fields trước cache/apply.

Capability

Config không enable unsupported capability.

Kiosk policy

Requested policy phải validate authority/runtime guard.

Maintenance password

Remote config không carry plaintext credential.

Factory Wi-Fi

Remote config không được expose/return hardcoded factory Wi-Fi password trong current baseline.

Update

Không force Managed Google Play policy path.

Runtime guard

Apply deferred trong unsafe states.

Local storage

Pending/applied metadata lưu trong `dcam.db`.

CSON scope

CSON chỉ chứa approved device information.

Audit

Fetch/apply/reject/defer/rollback dùng safe reason code.

Last good config

Invalid config không replace last valid config.

## 12. Authentication Method Security

Method

Security Requirement

Storage Direction

Password

Offline verification; enforce retry/lock policy nếu configured.

Approved protected representation only.

Pattern

Không lưu raw pattern path.

Approved protected representation only.

Face Authentication

Enable theo capability/security policy.

Platform/vendor secure flow hoặc protected reference.

QR Code

Login QR/token phải validate offline và support revocation.

Protected reference.

NFC Tag

NFC credential map tới user/auth method offline.

Protected reference.

Maintenance Password Gate credential

Required cho controlled kiosk exit.

Protected representation; no plaintext.

Emergency Override

Chỉ emergency recording khi chưa có operator login.

System identity only.

Factory Worker Web credential

Firebase Authentication credential dùng cho Web Portal; frontend không lưu password trong browser storage.

Firebase Auth/session; backend validates token and worker profile.

## 13. Operator Session Security

DCAM operator session has no timeout.
Background/foreground does not logout operator.
Screen off/on does not logout operator.
Device reboot requires login again.

Scenario

Required Security Behavior

App backgrounded/reopened

Preserve active session nếu same boot và state valid.

Process recreated

Restore session chỉ khi `device_boot_id` và auth state valid.

Device reboot

Expire previous session và require login.

User disabled by BDMA sync

Không interrupt current recording; block new recording sau safe window.

Auth method revoked

Apply tại safe window và require re-login nếu invalidated.

Severe auth DB corruption

Block normal recording; allow diagnostics/system modules và emergency override nếu safe.

Device policy missing

Login không được override missing required policy.

Maintenance requested

Operator session không bypass Maintenance Password Gate.

## 14. Emergency Override Security

user_id = SYSTEM_EMERGENCY_OVERRIDE
operator_code = EMERGENCY_OVERRIDE_ADMIN
display_name = Emergency Override Admin
user_type = SYSTEM
role = SYSTEM_ADMIN
status = ACTIVE

Rule

Description

SEC-EO-001

Chỉ allow cho emergency recording khi không có operator session.

SEC-EO-002

Phải auditable và visible trong BDMA.

SEC-EO-003

Không cấp full interactive admin UI.

SEC-EO-004

System identity protected khỏi normal deletion.

SEC-EO-005

Media/session attribution preserve snapshot.

SEC-EO-006

Không dùng làm Maintenance credential.

SEC-EO-007

Không bypass Maintenance Password Gate.

## 15. BDMA User Sync Security

Area

Security Requirement

Schema compatibility

BDMA check DB schema/version trước write-back.

Contract compatibility

Check app/data/media/encoder metadata; không dùng dynamic decoder profile.

Revision

User/auth changes versioned/revisioned.

Source

Record source `DCAM`, `BDMA`, `MIGRATION`, `DEFAULT`.

Audit

Add/edit/delete/disable/auth-method changes auditable.

Soft delete

Ưu tiên disable/soft delete.

Active user change

Không interrupt active recording.

Invalid auth data

Reject và preserve last valid state.

Conflict

Apply agreed rule và log conflict.

Kiosk impact

Restrictions không được silently break approved ADB boundary.

Maintenance credential

BDMA không read/write/export secret/protected material.

Google credential

BDMA không read/write/export Google password/token.

Factory Wi-Fi credential

BDMA không read/write/export factory Wi-Fi password.

## 16. Media Encryption Direction

Area

Direction

Status

Encrypted media suffix

`_enc` và `_IMP_enc` theo Data Contract.

Approved Contract Direction

Encryption algorithm

Dùng approved algorithm sau security review.

TBD / Security Review

Key storage

Dùng Android/device secure storage nếu available.

TBD / POC

Key rotation

Define khi key management design finalized.

TBD

BDMA decryption compatibility

Coordinate với Data Contract/BDMA; không dùng `bdma_decoder_profile_id`.

TBD

Emergency media

Preserve evidence; encryption failure handling explicit.

Draft Direction

## 17. Sensitive Logging Rules

Allowed examples:

[IDENTITY] Device identity restored
[PROVISIONING] Factory Worker provisioning completed
[PROVISIONING] QR rejected: <reason_code>
[POLICY] Device owner state verified
[POLICY] Maintenance password gate failed: <reason_code>
[CONFIG] Config rejected: <reason_code>
[UPDATE] APK validation failed: <reason_code>
[AUTH] Login success: <safe_user_id>
[AUTH] Login failed: <reason_code>
[SECURITY] Package validation failed: <reason_code>
[FACTORY_WIFI] Configuration result: <PASS|FAILED_REASON_CODE>
Forbidden log/Crashlytics content:

maintenance password value or hash input
password hint/recovery secret
factory Wi-Fi password
Google account password/token/session token
Firebase identity token or service credential
APK signing private key
raw Android system identifier
ANDROID_ID
android_id_hash
cloud token / provisioning secret
private admin/support token
enrollment secret
full sensitive config payload
QR raw payload when it may contain restricted fields
stack traces containing credential input
## 18. Recovery and Failure Behavior

Scenario

Expected Behavior

Local DB/CSON deleted

Restore bằng `serial_lookup/{serial_number}`; nếu không found thì provisioning-required.

SD Identity File conflicts

Treat as cache only; require server validation/support.

Device Owner missing

Enter policy-required/degraded state; no unrestricted production operation.

Lock Task/restriction apply failed

Log safe reason; recover/degrade/block according to policy.

Maintenance password invalid/locked

Reject, keep kiosk active, audit.

Maintenance state missing/corrupt

Block kiosk exit và require approved recovery.

Self Update invalid/install failed

Reject/preserve current state/restore policy.

Play Store fallback unavailable

Hide/disable fallback.

Personal Google account attempted

Reject và audit.

Invalid remote config

Reject và giữ last valid config.

Factory Wi-Fi configuration failed

Không log password; record safe failure reason; retry/quarantine theo Factory SOP.

Factory Wi-Fi credential suspected compromised

Rotate network credential và release replacement approved APK/process.

Web Portal worker inactive/unauthorized

Block Workspace submit và return safe error.

QR invalid/expired/replayed

Reject và require rescan/support according to policy.

Auth DB corrupted

Block normal recording; diagnostics/emergency only nếu safe.

User disabled while recording

Không interrupt evidence; block new recording sau safe window.

Device reboot

Expire session, require login, verify kiosk policy.

Encryption failure

Preserve source/staging file và mark recovery/failure.

## 19. Open Questions / TBD

Item

Status

Exact password hash algorithm and parameters

TBD / Security Review

Pattern encoding/hashing format

TBD / Security Review

Face authentication implementation

TBD / Capability + Security

Login QR/NFC credential format

TBD

Lockout policy values

TBD

Whether DB encryption is required

TBD

Media encryption algorithm and key management

TBD

BDMA decryption process

TBD

Admin permission model on DCAM

TBD

Maintenance credential representation/rotation/reset/lockout/session timeout

TBD / Security Review

Device Owner/DPC setup secret handling

TBD / Security Review

Provisioning QR signature/expiration/replay policy

TBD / API Contract + Security

Factory Worker account model: individual or shared station

TBD / Factory + Security

Factory Worker account lifecycle and password reset process

TBD / Backend + Security + Factory

Owner name validation/logging policy

TBD

Manufacture date correction policy

TBD

Remote config and kiosk policy payload security schema

TBD

Self Update artifact signing/checksum mechanism

TBD / Security Review

Manual Play Store fallback allowed per model

TBD / Security + Product

Maintenance/factory Google account handling

TBD / Security + Product

Factory Wi-Fi credential rotation cadence and source/build access procedure

TBD / Security + Factory + Release Manager

## 20. Practical Conclusion

DCAM authentication is offline-first after provisioning.
dcam_cloud_device_id is Cloud Identity / primary cloud device id.
serial_number is Hardware Identity / primary recovery key.
serial_lookup/{serial_number} is the approved recovery lookup path.
SD Identity File is a recovery cache, not authoritative identity.
ANDROID_ID, android_id_hash and device_lookup/{android_id_hash} are not used.
Web Portal user-facing account type is Factory Worker only.
Web Portal has Login and Workspace only.
Factory Worker scans the provisioning QR displayed by DCAM.
serial_number is obtained only from QR and remains read-only.
Frontend uses Firebase Authentication and Cloud Functions/backend authority.
Factory Worker cannot override duplicate/rebind/restricted device state.
Web Portal QR Flow is business provisioning only, not Device Owner setup or production acceptance.
Factory Wi-Fi SSID/password hardcoded in approved DCAM APK is an explicit approved project exception.
Factory Wi-Fi password must never appear in logs, Crashlytics, QR, API payload, CSON, DB, UI or production record.
This exception does not permit hardcoding Maintenance Password, cloud token, signing key or other credentials.
Current baseline has no external EMM, Android Management API or Managed Google Play policy-driven update.
Primary update path is DCAM Self Update / APK update.
Enter Maintenance Mode / Exit Kiosk temporarily is protected by Maintenance Password Gate.
Full Android unrestricted mode is not supported.
Emergency override is a system operator, not a real Admin credential.
Sensitive auth/security/identity/policy/update/factory Wi-Fi values must not be logged.
Encryption/key details remain open security decisions.