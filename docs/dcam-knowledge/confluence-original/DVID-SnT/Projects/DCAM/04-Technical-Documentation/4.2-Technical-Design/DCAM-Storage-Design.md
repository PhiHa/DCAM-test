# DCAM Storage Design

**Page ID**: 48496699  
**Version**: 6  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48496699

---


# DCAM Storage Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 0.6

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Android Lead / BDMA Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, BDMA Team, QA, Support

Last Updated

2026-07-08

Related Documents

DCAM-BDMA Data Contract, DCAM Recording & Capture Design, DCAM SQLite Database Design, DCAM Android Operation Design, DCAM Security & Encryption Design, 07 - Logging & Diagnostics Requirements

## 1. Purpose

**DCAM Storage Design** định nghĩa Android-side storage mechanics cho DCAM, bao gồm storage root resolution, physical path validation, temp/in-progress file handling, final media movement, storage mode selection, Auto fallback behavior, free-space thresholds, BDMA readiness, storage error handling và storage recovery.

Tài liệu này implement các rule của **DCAM-BDMA Data Contract**, nhưng không redefine file naming, folder naming, MD5 rule hoặc BDMA cleanup contract.

## 2. Authoritative References

Topic

Authoritative Document

Local Usage

Folder structure, media naming, `_IMP`, `_enc`, MD5 rule and BDMA cleanup contract

DCAM-BDMA Data Contract

Storage áp dụng contract; không copy full contract tables.

Recording/capture/finalization business flow

DCAM Recording & Capture Design

Storage thực thi temp/final file mechanics được RecordingController request.

Media session DB state and recovery fields

DCAM SQLite Database Design

Storage persist state thông qua repository; schema details nằm trong DB design.

Android runtime startup/recovery hosting

DCAM Android Operation Design

Android Operation gọi storage validation và recovery scanning.

Encryption and sensitive file handling

DCAM Security & Encryption Design

Storage gọi security services theo security policy.

Logging and diagnostics

07 - Logging & Diagnostics Requirements

Storage logs phải tuân theo logging policy.

## 3. Storage Ownership Rule

textCore rules:

Rule

Description

STR-OWN-001

StorageService thực thi storage actions do RecordingController request.

STR-OWN-002

In-progress files không được expose như final media.

STR-OWN-003

Final media chỉ xuất hiện trong Data Contract media folders sau khi finalization conditions pass.

STR-OWN-004

Storage fallback nên được quyết định trước khi recording starts, trừ khi target SDK/vendor SDK chứng minh mid-session switch an toàn.

STR-OWN-005

Khi state không chắc chắn, preserve file artifacts và để recovery reconcile.

## 4. Storage Components

Component

Responsibility

`StorageRootResolver`

Resolve logical Internal/External DCAM root thành validated physical path.

`StorageHealthChecker`

Check mounted state, writable state, free space và BDMA visibility nếu cần.

`MediaPathBuilder`

Build temp/staging/final path dựa trên Data Contract naming và session metadata.

`TempFileManager`

Create và track in-progress files d��ới Temp/staging.

`FinalizationManager`

Move/rename finalized media vào approved Media folder sau khi finalization conditions pass.

`FileIntegrityService`

Generate checksum cho `.mp4` nếu enabled và policy cho phép, theo Data Contract.

`BdmaReadinessMarker`

Mark media là `BDMA_READY` chỉ sau khi file và DB readiness conditions pass.

`StorageRecoveryScanner`

Scan Temp/staging/final folders và DB cho interrupted sessions trong startup/reboot/recovery.

## 5. Physical Path Validation

Storage Target

Validation

Failure Behavior

Internal DCAM Root

Exists hoặc có thể create; writable; đủ free space; stable across reboot.

Block hoặc enter safe storage mode nếu unavailable cho DB/config/log; fallback chỉ áp dụng cho media nếu policy cho phép.

External DCAM Root

Mounted; writable; đủ space; BDMA có thể read qua agreed mechanism.

Trong Auto mode fallback sang Internal trước recording; trong External mode apply policy.

Temp folder

Exists; không bị BDMA scan; writable cho active media.

Block start nếu temp/staging không thể create.

Final Media folder

Exists; tuân theo Data Contract; chỉ visible cho BDMA sau finalization.

Không expose partial file; preserve staging nếu final move fails.

Recovery / preserved area

Available cho interrupted hoặc uncertain files.

Nếu unavailable, giữ original path và mark recovery reason.

Validation sequence:

text## 6. Storage Mode and Fallback

Mode

Before Recording

During Recording

Recommended Policy

Internal

Validate internal root only.

Nếu internal fails, stop/finalize hoặc fail safely.

Không fallback silently vì user/policy đã chọn Internal.

External

Validate external root.

Nếu external fails, stop/finalize safely.

Cần clarify fallback sang Internal có được allowed không; default nên là no trừ khi policy approves.

Auto

Ưu tiên External; fallback Internal nếu External unavailable, full, not writable hoặc invalid.

Không switch mid-file trừ khi vendor SDK supports it safely.

Start new session trên fallback sau khi current session được xử lý.

## 7. Temp vs Final File Rule

BDMA chỉ được thấy finalized media.

In-progress media phải ở trong Temp/staging hoặc dùng non-final extension/name. Final media chỉ xuất hiện trong approved Media folders sau successful finalization và DB update.

File State

Location

BDMA Candidate?

Notes

`IN_PROGRESS`

Temp hoặc staging folder.

No

SDK writes tại đây trong lúc recording/capturing.

`FINALIZING`

Temp/staging.

No

Metadata/encryption/checksum/move chưa complete.

`FINALIZED`

Approved Media folder theo Data Contract.

Yes sau DB readiness.

File name tuân theo Data Contract.

`RECOVERY_REQUIRED`

Temp/recovery hoặc preserved path.

No

RecoveryManager quyết định next action.

`RECOVERY_FAILED`

Preserved diagnostic area hoặc Temp.

No trừ khi future contract định nghĩa.

Preserve cho diagnostics/support.

## 8. Free Space Thresholds

Exact values vẫn TBD, nhưng threshold names và decisions phải được định nghĩa để Android, QA và Product align behavior.

Threshold

Purpose

Action

Owner to Decide

`WARNING_FREE_SPACE`

Cảnh báo storage thấp.

Show/log warning; continue nếu safe.

Tech Lead + QA + Product

`MIN_START_FREE_SPACE`

Dung lượng tối thiểu để start recording/capture.

Block recording/capture nếu dưới threshold.

Tech Lead

`CRITICAL_ACTIVE_FREE_SPACE`

Đang record nhưng sắp hết dung lượng.

Stop safely/finalize theo policy.

Tech Lead + Product

`RESERVED_FINALIZATION_SPACE`

Dành cho metadata/checksum/DB/log/final move.

Không cho media consume reserved space.

Tech Lead

`MIN_RECOVERY_SPACE`

Dung lượng tối thiểu để run recovery/finalization.

Enter recovery warning/safe mode nếu dưới threshold.

Tech Lead + QA

## 9. Finalization and Move/Rename Direction

text
Step

Storage Responsibility

Failure Behavior

Close file

Ensure StorageService không còn write vào temp/staging file.

Giữ staging file và mark recovery nếu không thể confirm close.

Validate stability

Check file exists, size stable và file handle closed.

Giữ ở `FINALIZING` hoặc mark `RECOVERY_REQUIRED`.

Build final path

Dùng MediaPathBuilder theo Data Contract.

Fail finalization nếu không thể resolve final path an toàn.

Move/rename

Ưu tiên atomic move khi storage backend support.

Preserve staging file nếu final move fails.

DB update

Write final path/state thông qua repository transaction.

Nếu final file exists nhưng DB update fails, recovery scanner reconcile later.

Readiness mark

Mark `BDMA_READY` chỉ sau khi toàn bộ required conditions pass.

Không BDMA-ready nếu bất kỳ required condition nào fail.

## 10. BDMA Readiness Rule

Media chỉ là `BDMA_READY` khi required readiness conditions pass.

Condition

Required?

Failure Behavior

Final file exists in approved Media folder.

Yes

Không `BDMA_READY`.

File handle is closed and size is stable.

Yes

Giữ ở `FINALIZING` hoặc `RECOVERY_REQUIRED`.

`media_session` DB record is updated.

Yes

Run reconciliation nếu file exists nhưng DB missing.

No recovery flag is pending.

Yes

Giữ not ready cho đến khi recovery completes.

Metadata step completed or skipped with explicit reason.

Depends on format/policy

Mark skipped hoặc failed reason.

Encryption completed if enabled.

If encryption enabled

Không expose final encrypted suffix trừ khi encryption successful.

Checksum behavior completed according to Data Contract/settings.

Depends on checksum policy

Nếu optional, mark unverified/warning reason; nếu required, not ready.

`BDMA_READY` nghĩa là final media safe for BDMA scan/import. Nó không có nghĩa là BDMA đã import file.

## 11. Storage Recovery

Scenario

Recovery Action

Temp file exists and DB session is `RECORDING`.

Try finalize nếu file valid; nếu không thì mark `RECOVERY_REQUIRED`.

Temp/staging file exists but SDK state is unknown.

Preserve file; run safe validation; chỉ finalize nếu validated.

Final file exists but DB missing.

Reconcile: create/recover `media_session` record với recovered flag.

DB says `BDMA_READY` but file missing.

Mark `MISSING_SOURCE_FILE` và log diagnostics.

Checksum missing for `.mp4`.

Generate nếu policy và file available; nếu không thì mark unverified/warning reason.

External removed before final move.

Preserve temp/staging nếu có thể; mark storage failure.

Move/rename interrupted.

Detect staging/final duplicates và apply deterministic recovery rule.

Final folder unavailable.

Giữ preserved/staging file và mark recovery required.

Recovery file cannot be validated.

Mark recovery failed; preserve artifact cho support nếu safe.

## 12. Runtime Persistence Direction

Detailed schema thuộc **DCAM SQLite Database Design**. Storage Design yêu cầu các information sau persistable khi cần:

text## 13. Logging and Diagnostics

Storage logs phải tuân theo **07 - Logging & Diagnostics Requirements**.

Required examples:

text
[STORAGE] Auto fallback selected: EXTERNAL -> INTERNAL
[STORAGE] Temp file created
[STORAGE] Free space warning
[STORAGE] Critical active free space reached
[STORAGE] Final move started
[STORAGE] Final move completed
[STORAGE] Final move failed
[STORAGE] BDMA readiness marked
[STORAGE] Recovery scan started
[STORAGE] Recovery candidate found
[STORAGE] Recovery completed
[STORAGE] Recovery failed: ]]>Không log sensitive data hoặc raw media content.

## 14. Open Questions / TBD

Item

Status

Actual internal root physical path

TBD

Actual external root physical path

TBD

Android scoped storage behavior by target SDK

TBD

Exact ADB visibility rule for External/Internal roots

TBD

Whether external-to-internal fallback is allowed in External mode

TBD

Vendor SDK support for mid-recording path switching

TBD

Final threshold values: warning/min-start/critical/reserved/recovery

TBD

Atomic move support by target storage backend

TBD

Temp/staging folder naming and retention policy

TBD

Recovery folder/preserved diagnostic area

TBD

Deterministic rule for interrupted move/rename duplicates

TBD

Behavior when final file exists but checksum is missing

TBD

Storage test matrix for full disk/external removed/reboot/crash

TBD

Storage cleanup policy for old Temp/recovery artifacts

TBD

Whether BDMA should see recovered media with special flag

TBD

## 15. Practical Conclusion

textKey implementation rules:

text