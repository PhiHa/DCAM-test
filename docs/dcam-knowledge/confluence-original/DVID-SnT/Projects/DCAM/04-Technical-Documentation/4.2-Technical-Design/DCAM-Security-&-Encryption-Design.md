# DCAM Security & Encryption Design

**Page ID**: 48496720  
**Version**: 11  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48496720

---


# DCAM Security & Encryption Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 1.1

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer / BDMA Lead / Cloud Lead / Android Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, BDMA Developers, QA, Security Reviewer, Support, Cloud/WebServer Team

Last Updated

2026-07-09

Related Jira

None

Related Documents

08 - Security & Encryption Requirements, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, 09 - System Settings Requirements, 10 - Android Device Operation Requirements, DCAM Android Device Owner & Kiosk Policy Design, DCAM In-App Operation, Device Settings & Media Console Design, DCAM Self Update Design, ADR - DCAM Android Dedicated Device / Device Owner / Lock Task Decision, DCAM-BDMA Data Contract, DCAM SQLite Database Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM State Machine Design, DCAM Web Portal & Device API Contract, DCAM QA Test Strategy & Test Matrix, DCAM Factory Provisioning & Device Production SOP, 07 - Logging & Diagnostics Requirements

## 1. Purpose

**DCAM Security & Encryption Design** định nghĩa security direction cho DCAM Android, bao gồm device identity handling, device information security, Web Portal provisioning security, kiosk policy security, in-app console security, Maintenance Password Gate, operator authentication methods, emergency override audit behavior, Self Update/package validation, optional manual Google Play Store fallback constraints, sensitive logging, encrypted media direction và BDMA/WebServer compatibility.

Current baseline:

textTài liệu này không redefine user-management requirement, DB schema, provisioning API, remote config payload, kiosk policy detail hoặc BDMA Data Contract. Tài liệu này định nghĩa security constraints mà các tài liệu đó phải tuân theo.

## 2. Authoritative References

Topic

Authoritative Document

Local Usage

Device identity, device information and CSON scope

04 - Device Configuration Requirements

Security Design ràng buộc identifier/device-info handling và logging.

Firebase/WebServer identity and Web Portal provisioning

06 - Cloud Services, Update & Configuration Architecture

Security Design ràng buộc provisioning/audit behavior.

Provisioning API and serial lookup contract

DCAM Web Portal & Device API Contract

Security Design phải tuân theo `serial_lookup/{serial_number}` và không dùng `device_lookup/{android_id_hash}`.

Factory identity/provisioning baseline

DCAM Factory Provisioning & Device Production SOP

Security Design phải tuân theo factory baseline: serial number là Hardware Identity / recovery key.

Kiosk policy

DCAM Android Device Owner & Kiosk Policy Design

Security Design ràng buộc kiosk exit, Maintenance Mode, policy removal, restriction changes and no-external-EMM baseline.

In-app console and Maintenance Password Gate UX

DCAM In-App Operation, Device Settings & Media Console Design

Security Design ràng buộc access/auth/audit behavior for console actions and controlled maintenance.

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

Security Design ràng buộc những gì được store và cách sensitive values được represented.

User/operator sync and BDMA write-back boundary

DCAM-BDMA Data Contract

Security Design ràng buộc safe sync/write-back behavior.

Android startup/session/provisioning/kiosk lifecycle

DCAM Android Operation Design

Security Design hỗ trợ identity restore, policy verification, no-timeout session và reboot-login-required policy.

Recording operator attribution and emergency override

DCAM Recording & Capture Design

Security Design bảo vệ auditability của operator attribution.

QA release validation

DCAM QA Test Strategy & Test Matrix

Security Design cung cấp security baseline để QA verify identity/provisioning/security behavior.

Sensitive logging

07 - Logging & Diagnostics Requirements

Security Design reference logging policy và thêm auth/security/policy/update examples.

## 3. Security Principles

Principle

Direction

Offline-first authentication

Operator authentication phải hoạt động without Internet/cloud dependency sau khi device đã provisioned và local data tồn tại.

Stable cloud identity

Cloud/WebServer primary device id là `dcam_cloud_device_id`; không phụ thuộc owner name, manufacture date, Android system identifier hoặc `android_id_hash`.

Stable hardware recovery identity

Hardware Identity / primary recovery key là `serial_number` theo Factory SOP và API Contract baseline.

Recovery cache is not identity

SD Identity File chỉ là recovery cache; không phải authoritative Hardware Identity hoặc Cloud Identity.

Identifier minimization

Không expose hoặc log unnecessary device identifiers.

Device information minimization

Device information như `owner_name` có thể dùng cho admin/support/BDMA display nhưng không được dùng như identity key hoặc credential.

Protected credential representation

Password, pattern, QR, NFC login data, face auth material and Maintenance Password Gate credentials phải dùng approved protected representation.

Auditability

Provisioning, serial correction/rebind, config apply, kiosk policy apply/remove/failure, Maintenance Mode, maintenance password gate, update, login events, user sync, auth method changes và emergency override phải auditable.

Evidence preservation

Security failure hoặc policy failure không được silently delete hoặc modify recorded evidence.

Runtime safety

User/auth/config/policy/update changes phải được validate và apply chỉ khi runtime guard cho phép.

Dedicated-device session policy

No-timeout session được allow theo product decision, nhưng reboot phải yêu cầu login lại.

Kiosk fail closed

Required production kiosk policy failure không được làm thiết bị rơi về unrestricted field operation.

Maintenance fail closed

Failed maintenance password validation must not stop Lock Task, relax restrictions or expose Android Settings.

Update fail closed

Failed update validation/install must not leave device unrestricted or downgrade security.

No personal account dependency

Production maintenance/update must not depend on a personal Google account.

## 4. Device Identity and Device Information Security

Approved identity model:

textSecurity rules:

Rule

Description

SEC-ID-001

`serial_number` không được dùng làm Firebase/WebServer primary key; `serial_number` là Hardware Identity / primary recovery key.

SEC-ID-002

`owner_name` và `manufacture_date` không được dùng làm Firebase/WebServer primary key.

SEC-ID-003

Advertising ID không được dùng làm DCAM primary key hoặc recovery key.

SEC-ID-004

`ANDROID_ID` và Android system identifier không được dùng làm production identity, recovery lookup key hoặc server lookup input.

SEC-ID-005

Original Android system identifier không được ghi vào logs.

SEC-ID-006

`android_id_hash` không được dùng làm production identity hoặc recovery lookup trong current SOP baseline.

SEC-ID-007

`dcam_cloud_device_id` được lưu locally sau provisioning/restore và dùng cho cloud/webserver requests.

SEC-ID-008

Serial correction/rebind phải auditable và phải preserve previous serial history ở server side.

SEC-ID-009

Owner/manufacture date change phải auditable nếu update qua Web Portal, server hoặc approved admin flow.

SEC-ID-010

`manufacture_date` phải dùng format chuẩn `YYYY-MM-DD` khi stored/synced.

SEC-ID-011

`bdma_decoder_profile_id` không thuộc identity/security contract; BDMA compatibility dùng app/data/media/encoder contract metadata.

SEC-ID-012

`device_lookup/{android_id_hash}` là deprecated/removed path và không được dùng trong current production baseline.

SEC-ID-013

`serial_lookup/{serial_number}` là approved recovery lookup path cho provisioning/restore.

SEC-ID-014

SD Identity File chỉ được dùng như recovery cache, không được promote thành authoritative identity source nếu conflict với server record.

## 5. Web Portal Provisioning Security

Default DCAM business provisioning method là Web Provisioning Portal QR Flow.

Important boundary:

textSecurity rules:

Rule

Description

SEC-PROV-001

Chỉ authenticated Web Admin / Factory Admin được provision production devices.

SEC-PROV-002

Web client phải gọi backend API; production device records được tạo bởi backend.

SEC-PROV-003

Provisioning QR phải include version và challenge direction; exact signing/expiration vẫn TBD.

SEC-PROV-004

Provisioning action phải auditable với admin, time, source, `dcam_cloud_device_id`, `serial_number`, owner/manufacture info nếu changed và reason code.

SEC-PROV-005

DCAM không được trở thành ACTIVE cho đến khi server identity và `serial_lookup/{serial_number}` mapping được tạo/fetched successfully.

SEC-PROV-006

Nếu serial lookup/provisioning lookup fails, DCAM vẫn ở provisioning-required/recovery state.

SEC-PROV-007

Re-provision/rebind flow yêu cầu admin approval và audit.

SEC-PROV-008

Provisioning API phải reject/ignore `bdma_decoder_profile_id` vì field này không thuộc contract.

SEC-PROV-009

Provisioning API và Android client không được dùng `device_lookup/{android_id_hash}` trong current production baseline.

## 6. Kiosk Policy Security

Detailed policy behavior thuộc **DCAM Android Device Owner & Kiosk Policy Design**. In-app Maintenance Password Gate UX thuộc **DCAM In-App Operation, Device Settings & Media Console Design**.

Rule

Description

SEC-KIOSK-001

Chỉ approved admin/support flow mới được thoát Lock Task hoặc vào Maintenance Mode.

SEC-KIOSK-002

Maintenance Mode entry/exit phải auditable với actor/source/time/result/reason code.

SEC-KIOSK-003

User Restrictions không được clear silently.

SEC-KIOSK-004

Policy failure không được mở thiết bị về unrestricted production field operation.

SEC-KIOSK-005

Kiosk exit / Maintenance credential không được hardcode, plaintext hoặc logged.

SEC-KIOSK-006

Emergency override không được cấp full interactive admin UI access, Maintenance Mode access hoặc Maintenance Password Gate bypass.

SEC-KIOSK-007

Lock Task allowlist changes phải auditable và chỉ include approved packages.

SEC-KIOSK-008

Device Owner / DPC missing state phải được logged bằng safe reason code; không expose admin token, enrollment secret hoặc raw identifier.

SEC-KIOSK-009

Maintenance Mode không được interrupt active recording, emergency hoặc finalization trừ khi có safe stop policy được approve.

SEC-KIOSK-010

Policy restore sau Maintenance Mode/update/reboot phải được verified và logged.

SEC-KIOSK-011

Enter Maintenance Mode / Exit Kiosk temporarily must require Maintenance Password Gate.

SEC-KIOSK-012

Failed Maintenance Password Gate validation must not stop Lock Task, relax restrictions or open Android Settings/system surfaces.

SEC-KIOSK-013

Repeated failed maintenance password attempts must be rate-limited, cooled down or locked according to approved policy.

SEC-KIOSK-014

Full Android unrestricted mode is not supported.

SEC-KIOSK-015

Controlled Mode may open only approved targets.

SEC-KIOSK-016

External EMM / Android Management API must not be required for current security baseline.

## 7. Maintenance Password Gate Security

Maintenance Password Gate protects Enter Maintenance Mode / Exit Kiosk temporarily.

### 7.1 Required Behavior

text### 7.2 Credential Rules

Rule

Description

SEC-MAINT-001

Maintenance password must be a dedicated maintenance credential or approved maintenance secret.

SEC-MAINT-002

Normal operator password must not be enough to exit kiosk.

SEC-MAINT-003

Admin login alone must not bypass Maintenance Password Gate unless a future ADR explicitly approves single-factor maintenance.

SEC-MAINT-004

Maintenance password must not be hardcoded in source code, resources, config files, CSON, remote config payload or logs.

SEC-MAINT-005

Maintenance password must not be stored plaintext in `dcam.db`, SharedPreferences, files, logs or crash reports.

SEC-MAINT-006

Store only approved protected representation, such as salted/slow hash or secure credential reference, according to Security Review.

SEC-MAINT-007

If Android Keystore or hardware-backed security is available, use it where appropriate for protecting secrets/keys.

SEC-MAINT-008

Maintenance password validation must use constant-time/safe comparison where applicable.

SEC-MAINT-009

Maintenance password reset/recovery must require approved Admin/Security process and audit.

SEC-MAINT-010

Maintenance password rotation policy is TBD and must be finalized by Security/Product.

SEC-MAINT-011

Maintenance Password Gate must not be available from emergency override identity.

### 7.3 Attempt, Lockout and Audit Rules

Rule

Description

SEC-MAINT-012

Failed attempts must increment non-sensitive failed-attempt metadata.

SEC-MAINT-013

Repeated failed attempts must trigger delay, cooldown, temporary lockout or escalation according to approved policy.

SEC-MAINT-014

Failed attempts must not reveal whether password length/format/partial value is correct.

SEC-MAINT-015

Audit must include actor/source/time/result/reason code but not secret values.

SEC-MAINT-016

Failed attempts must not stop Lock Task, relax restrictions or open Android Settings.

SEC-MAINT-017

Maintenance session must have timeout/inactivity policy and restore kiosk policy after exit/timeout/recovery.

SEC-MAINT-018

Maintenance password state must not be synced to BDMA or exposed through media/file viewer.

## 8. Update and Runtime Package Security

Current baseline update security:

text
Rule

Description

SEC-UPD-001

APK must match expected DCAM package identity.

SEC-UPD-002

APK must pass checksum/integrity validation before install.

SEC-UPD-003

APK signature must be trusted and compatible with update path.

SEC-UPD-004

APK source must be trusted; arbitrary user-provided APK is not allowed.

SEC-UPD-005

Update must preserve Device Owner/DPC policy, Lock Task recovery and User Restrictions baseline.

SEC-UPD-006

Update must not leave device unrestricted if install fails.

SEC-UPD-007

Update path must not run during recording, emergency, finalization, DB/storage recovery or policy recovery.

SEC-UPD-008

Update/maintenance path must not bypass Maintenance Password Gate if temporary kiosk exit is needed.

SEC-UPD-009

Managed Google Play / policy-driven update request must be rejected/ignored as not applicable for current baseline.

SEC-UPD-010

Downgrade/rollback must be explicitly approved and audited if supported.

## 9. Optional Manual Google Play Store Fallback Security

Manual Google Play Store update is not the primary update path. It is an optional controlled maintenance fallback only.

Rule

Description

SEC-PLAY-001

Play Store may be opened only through Admin / Maintenance Controlled Mode after Maintenance Password Gate.

SEC-PLAY-002

Play Store fallback is allowed only if target device has GMS/Play Store and Product/Security approve it.

SEC-PLAY-003

Only DCAM/approved apps may be updated. General app browsing/install is not allowed.

SEC-PLAY-004

Personal Google account must not be used for production maintenance update.

SEC-PLAY-005

Approved maintenance/factory Google account handling is TBD and must be auditable.

SEC-PLAY-006

Google account password/token must never be logged.

SEC-PLAY-007

Account sign-in/sign-out or account persistence must follow approved Security/Product decision.

SEC-PLAY-008

After Play Store update, DCAM must return to app, verify update result where applicable and restore kiosk policy.

SEC-PLAY-009

Play Store fallback must not become full Android unrestricted mode.

## 10. Remote Config Security

Remote config payload fields vẫn TBD. Security baseline đã approved.

Area

Security Requirement

Identity

Fetch config bằng `dcam_cloud_device_id` sau khi identity resolved.

Validation

Validate schema/version/allowed fields trước khi cache/apply.

Capability

Config không được enable unsupported hardware/runtime capability.

Kiosk policy

Kiosk settings are requested policy; actual apply must validate Device Owner/DPC authority, runtime guard and Kiosk Policy Design constraints.

Maintenance password

Remote config may define policy knobs such as timeout/failed-attempt policy, but must not carry plaintext maintenance password.

Update

Remote config may request Self Update behavior, but must not force Managed Google Play policy-driven path on current no-EMM baseline.

Runtime guard

Config apply phải deferred trong lúc recording, emergency, finalization, recovery hoặc policy unsafe states.

Local storage

Pending/applied config metadata được lưu trong `dcam.db`.

CSON scope

`dcam_config.cson` chỉ được update cho device information như serial, owner name, manufacture date, model hoặc firmware information.

Audit

Fetch/apply/reject/defer/rollback phải được log bằng safe reason codes.

Last good config

Invalid config không được replace last valid applied config.

## 11. Authentication Method Security

DCAM hỗ trợ nhiều login methods theo device capability và security policy.

Method

Security Requirement

Storage Direction

Password

Offline verification; enforce lock/retry policy nếu configured.

Chỉ store approved protected representation.

Pattern

Offline verification; tránh lưu raw pattern path/sequence.

Chỉ store approved protected representation.

Face Authentication

Chỉ enable nếu device capability và security policy cho phép.

Ưu tiên platform/vendor secure biometric flow hoặc approved protected reference.

QR Code

QR phải represent credential id/token có thể validate offline.

Store approved protected reference; support revocation và revision.

NFC Tag

NFC credential phải map tới user/auth method offline.

Store approved protected reference; support revocation và revision.

Maintenance Password Gate credential

Required for Enter Maintenance Mode / Exit Kiosk temporarily; must support offline verification, lockout, audit and recovery.

Chỉ store approved protected representation; no plaintext.

Emergency Override

Chỉ dùng cho emergency recording khi chưa có operator logged in.

System identity only; không phải real admin credential.

## 12. Operator Session Security

Product decision:

textSecurity implications and controls:

Scenario

Required Security Behavior

App backgrounded

Preserve active session; unapproved background transition should not happen in normal kiosk operation.

App reopened

Reuse active session nếu same boot và DB state valid.

Process recreated without reboot

Restore session chỉ khi `device_boot_id` matches và session/auth state valid.

Device reboot

Mark previous session expired và yêu cầu login.

User disabled by BDMA sync

Không interrupt current recording; block new recording sau safe window.

Auth method revoked

Apply tại safe window và require re-login nếu session bị invalidated.

Severe auth table corruption

Block normal recording; allow diagnostics/system modules và emergency override only if safe.

Device policy missing

Block/degrade normal production field operation according to Kiosk Policy Design; operator login must not override missing required policy.

Maintenance requested

Existing operator session alone must not bypass Maintenance Password Gate.

## 13. Emergency Override Security

Emergency override không được attribute vào real Admin user.

Required system identity:

textRules:

Rule

Description

SEC-EO-001

Emergency override chỉ được allow cho emergency recording khi không có operator session.

SEC-EO-002

Emergency override phải auditable và visible trong BDMA.

SEC-EO-003

Emergency override không được cấp full interactive admin UI access.

SEC-EO-004

Emergency override identity phải system-created/protected khỏi normal user deletion.

SEC-EO-005

Media/session attribution phải preserve `EMERGENCY_OVERRIDE_ADMIN` snapshot.

SEC-EO-006

Emergency override không được dùng làm Maintenance Mode credential hoặc kiosk exit credential.

SEC-EO-007

Emergency override không được bypass Maintenance Password Gate.

## 14. BDMA User Sync Security

BDMA và DCAM synchronize user/operator data through ADB theo **DCAM-BDMA Data Contract** và **DCAM SQLite Database Design**.

Area

Security Requirement

Schema compatibility

BDMA phải check DB schema/version trước write-back.

App/contract compatibility

BDMA phải check app/data/media/encoder contract metadata; không fetch/use dynamic decoder profile.

Revision

User/auth changes phải versioned/revisioned.

Source

Changes phải record source: `DCAM`, `BDMA`, `MIGRATION`, `DEFAULT`.

Audit

Add/edit/delete/disable/auth-method changes phải auditable.

Soft delete

Ưu tiên disable/soft delete thay vì destructive delete.

Active user change

Không interrupt active recording; apply revoke/block tại safe window.

Invalid auth data

Reject invalid records và preserve last valid state.

Conflict

Apply agreed conflict rule và log conflict.

Kiosk restrictions impact

Production restrictions must be validated so approved BDMA ADB/media import/user sync boundary is not broken silently.

Maintenance password state

BDMA must not read/write/export maintenance password secret or protected credential material unless a future explicit security contract allows a safe management operation.

Google account/token

BDMA must not read/write/export Google account password/token.

## 15. Media Encryption Direction

Encrypted media naming và BDMA-facing suffix rules thuộc **DCAM-BDMA Data Contract**.

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

Dùng Android/device secure storage nếu available; exact mechanism TBD.

TBD / POC

Key rotation

Define khi key management design finalized.

TBD

BDMA decryption compatibility

Phải coordinate với Data Contract và BDMA implementation. Không dùng `bdma_decoder_profile_id`.

TBD

Emergency media

Phải preserve evidence; encryption failure handling phải explicit.

Draft Direction

## 16. Sensitive Logging Rules

Allowed examples:

text
[POLICY] Maintenance mode entered
[POLICY] Maintenance mode exited
[CONFIG] Config fetched
[CONFIG] Config rejected: 
[UPDATE] Self update check started
[UPDATE] APK validation failed: 
[UPDATE] Managed Google Play not applicable: 
[UPDATE] Manual Play Store fallback started
[UPDATE] Manual Play Store fallback completed
[AUTH] Login success: 
[AUTH] Login failed: 
[AUTH] Emergency override used
[USER_SYNC] Auth method update rejected: 
[SECURITY] Package validation failed: ]]>Forbidden log content:

text## 17. Recovery and Failure Behavior

Scenario

Expected Behavior

Local DB/CSON deleted

Attempt identity restore bằng `serial_lookup/{serial_number}`; nếu không tìm thấy thì enter provisioning-required state.

SD Identity File exists but server mapping conflicts

Treat SD Identity File as recovery cache only; require server-side `serial_lookup/{serial_number}` validation/rebind approval before ACTIVE.

Device Owner / DPC state missing

Enter policy-required/degraded state; do not silently allow unrestricted production operation.

Lock Task start failed

Log safe reason code; enter policy recovery/degraded state.

User restriction apply failed

Log safe reason code; apply approved degraded behavior or block production if required.

Maintenance password invalid

Reject maintenance entry; keep Lock Task/restrictions active; audit safe reason code.

Maintenance password lockout/cooldown active

Reject maintenance entry until allowed by policy; audit safe reason code.

Maintenance password state missing/corrupt

Block temporary kiosk exit and require approved recovery/support process.

Maintenance Mode restore failed

Keep device in policy recovery/degraded state; do not silently return to field operation.

Managed Google Play update requested

Reject/ignore as not applicable on current baseline; use Self Update or approved fallback.

Self Update invalid package

Reject package; keep current version and kiosk policy intact.

Self Update install failure

Log reason, preserve current/recoverable state, restore policy or enter policy recovery.

Play Store fallback unavailable

Hide/disable fallback or reject with safe reason.

Personal Google account attempted

Reject production maintenance path and audit safe reason.

Invalid remote config

Reject config và giữ last valid applied config.

Auth DB corrupted

Block normal recording; allow diagnostics/system modules và emergency override only if safe.

User disabled while recording

Không interrupt current evidence; block new recording sau safe window.

Device reboot

Expire previous session và yêu cầu login; verify kiosk policy state before normal field operation.

Encryption failure during finalization

Preserve source/staging file và mark recovery/failure theo Recording/Storage design.

## 18. Open Questions / TBD

Item

Status

Exact password hash algorithm and parameters

TBD / Security Review

Pattern encoding/hashing format

TBD / Security Review

Face authentication implementation model

TBD / Device Capability + Security Review

QR credential format

TBD

NFC tag credential format

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

Maintenance Password Gate credential representation

TBD / Security Review

Maintenance password length/complexity/rotation policy

TBD / Security Review

Maintenance password reset/recovery process

TBD / Security Review

Maintenance failed-attempt lockout/cooldown policy

TBD / Security Review

Maintenance session timeout policy

TBD / Security Review

Device Owner/DPC setup secret handling if DCAM-as-DPC is selected

TBD / Security Review

Provisioning QR signature/expiration format

TBD

Web Portal provisioning API auth detail

TBD

Owner name validation/logging policy

TBD

Manufacture date correction policy

TBD

Remote config payload security schema

TBD

Kiosk policy payload security schema

TBD

Self Update artifact signing/checksum mechanism

TBD / Security Review

Manual Play Store fallback allowed or disabled

TBD / Security + Product

Maintenance/factory Google account handling

TBD / Security + Product

## 19. Practical Conclusion

text