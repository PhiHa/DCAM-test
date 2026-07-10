# DCAM Recording & Capture Design

**Page ID**: 48529484  
**Version**: 9  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/48529484

---


# DCAM Recording & Capture Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 0.9

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / Security Reviewer

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, AI/ML Engineer, QA, BDMA Team, Support

Last Updated

2026-07-08

Related Jira

None

Related Documents

DCAM-BDMA Data Contract, 05 - User & Device Operation Requirements, DCAM State Machine Design, DCAM Device Capability & Feature Eligibility Design, DCAM Android Operation Design, DCAM Storage Design, DCAM SQLite Database Design, DCAM Sensor & Location Monitoring Design, DCAM Realtime AI Detection Design, DCAM Security & Encryption Design, 07 - Logging & Diagnostics Requirements

## 1. Purpose

**DCAM Recording & Capture Design** định nghĩa runtime behavior cho video recording, image capture, audio capture nếu enabled, pre-record/post-record nếu supported, emergency evidence behavior, recording state transitions, recording prechecks, operator authentication gate, stop/finalization pipeline và recovery của interrupted recording sessions.

Tài liệu này là authoritative cho **recording runtime**, **capture runtime**, **operator attribution for media session** và **emergency evidence flow**.

Tài liệu này không redefine media naming, folder structure, MD5 rule, BDMA cleanup baseline, user DB schema hoặc credential security. Các rule đó thuộc authoritative documents tương ứng và được reference tại đây.

## 2. Authoritative References

Topic

Authoritative Document

Local Usage

Recording/capture/emergency flow

DCAM Recording & Capture Design

Tài liệu này owns runtime flow và RecordingController behavior.

User/operator login requirement and emergency override

05 - User & Device Operation Requirements

Recording phải yêu cầu operator session, ngoại trừ emergency override.

User/session/operator snapshot DB schema

DCAM SQLite Database Design

Recording lưu operator snapshot vào media session thông qua DB repository.

Android login screen/session lifecycle

DCAM Android Operation Design

Android Operation cung cấp active operator session state.

Media naming, folders, `_IMP`, `_enc`, MD5, BDMA cleanup

DCAM-BDMA Data Contract

Tài liệu này áp dụng contract; không copy full contract tables.

Runtime transition guard and system priority

DCAM State Machine Design

Recording transitions và emergency priority tuân theo state-machine guard rules.

Capability/eligibility state names

DCAM Device Capability & Feature Eligibility Design

Recording/capture runtime consume eligibility results.

Physical file/temp/final storage mechanics

DCAM Storage Design

StorageService áp dụng final/temp file mechanics.

Security/encryption/auth behavior

DCAM Security & Encryption Design

Credential/security/encryption details được delegate.

Logging and diagnostics

07 - Logging & Diagnostics Requirements

Recording logs phải tuân theo logging policy.

## 3. Recording Authority Rule

Chỉ `RecordingController` được start, stop, pause nếu supported, resume nếu supported, mark important, finalize hoặc recover recording session.

UI, Emergency Event Manager, Sensor Monitoring và Realtime AI Detection có thể emit commands/events, nhưng không được gọi trực tiếp `CameraService`, `RecordingEngine`, `StorageService` hoặc vendor Camera SDK.

Source

Allowed

Not Allowed

UI / ViewModel

Gửi `StartRecordingCommand`, `StopRecordingCommand`, `MarkImportantCommand` qua UseCase.

Gọi trực tiếp CameraService, RecordingEngine, StorageService hoặc SDK.

EmergencyEventManager

Request emergency recording, emergency mark hoặc important marker.

Tự stop current recording.

Sensor Monitoring

Emit fall/impact/motion event candidate.

Điều khiển camera, recording hoặc storage trực tiếp.

Realtime AI Detection

Emit detection event hoặc emergency event candidate.

Điều khiển camera, recording hoặc storage trực tiếp.

Android Operation

Cung cấp operator session state, host lifecycle và foreground service.

Bypass RecordingController/state-machine/auth gate.

RecordingController

Own recording session decision, state machine interaction và finalization orchestration.

Render UI hoặc bypass storage/finalization rule.

## 4. Runtime Components

Component

Responsibility

Notes

`RecordingController`

Owns recording session lifecycle, operator gate, command handling, recording state và finalization orchestration.

Main runtime owner.

`RecordingUseCase`

Validate high-level command intent và gọi RecordingController.

Dùng bởi UI/ViewModel/API layer.

`OperatorSessionProvider`

Cung cấp active operator session hoặc emergency override decision input.

Data source từ Android Operation / DB.

`RecordingStateMachineAdapter`

Map RecordingController events sang DCAM State Machine Design.

Không định nghĩa global state list.

`CameraService` / `RecordingEngine`

Wrap CameraX/Camera2/vendor SDK recording start/stop behavior.

Không có business decision.

`ImageCaptureRuntime`

Xử lý image capture nếu feature enabled và eligible.

Dùng cùng operator gate nếu capture là evidence.

`AudioCaptureRuntime`

Xử lý audio-only capture nếu enabled và eligible.

Phụ thuộc microphone permission/capability và operator gate nếu là evidence.

`PreRecordBufferManager`

Maintain rolling buffer nếu pre-record supported và enabled.

Buffer không phải final media.

`PostRecordController`

Extend emergency recording theo post-record policy.

Chỉ chạy khi policy/state cho phép.

`StorageService`

Tạo temp/staging/final file targets và thực hiện safe move/rename.

Physical mechanics thuộc Storage Design.

`MediaSessionRepository`

Persist session state, operator snapshot, final path và BDMA readiness state vào `dcam.db`.

Schema details thuộc DB design.

`RecoveryManager`

Recover interrupted sessions/temp/final files sau crash/reboot/service kill.

Làm việc với Android Operation Design.

## 5. Recording State Machine

### 5.1 Normal Recording Flow

text### 5.2 State Meaning

State

Meaning

Allowed Commands

Failure State

`IDLE`

Không có active recording session.

StartRecording, Capture.

N/A

`PRECHECKING`

Đang check operator gate, permission, capability, storage, DB và policy.

Cancel nếu safe.

`START_FAILED`, `STORAGE_FAILED`, `PERMISSION_BLOCKED`, `OPERATOR_AUTH_REQUIRED`

`PREPARING_CAMERA`

Đang open camera hoặc prepare recording SDK.

Cancel nếu safe.

`CAMERA_FAILED`

`PREPARING_STORAGE`

Đang tạo temp/staging file hoặc session target.

Cancel nếu safe.

`STORAGE_FAILED`

`STARTING`

SDK recording start đã được requested.

Wait hoặc cancel qua safe guard.

`START_FAILED`

`RECORDING`

Recording đang active.

Stop, MarkImportant, EmergencyMark.

`CAMERA_FAILED`, `STORAGE_FAILED`

`STOPPING`

SDK stop đã được requested.

Không duplicate stop.

`STOP_FAILED`

`FINALIZING`

Đang close file, metadata, encryption, checksum, DB update.

Không update, cleanup hoặc new start.

`FINALIZE_FAILED`, `RECOVERY_REQUIRED`

`BDMA_READY`

Final file tồn tại, DB state đã updated và file safe cho BDMA scan/import.

View/list only.

N/A

`COMPLETED`

Session lifecycle completed.

Start new session.

N/A

### 5.3 Failure States

Failure State

Meaning

Required Behavior

`OPERATOR_AUTH_REQUIRED`

Normal recording/capture được request khi không có active operator session.

Reject command và show login screen.

`PERMISSION_BLOCKED`

Missing required permission.

Reject affected command và surface controlled error.

`START_FAILED`

Recording không thể start sau request.

Release resources, log failure và return to safe state.

`CAMERA_FAILED`

Camera/SDK failed hoặc disconnected.

Stop nếu có thể, preserve diagnostics và tránh corrupt final media.

`STORAGE_FAILED`

Không có safe writable temp/final target.

Stop nếu có thể, preserve temp và log reason.

`STOP_FAILED`

SDK stop failed hoặc timed out.

Enter recovery path nếu safe stop không thể confirm.

`FINALIZE_FAILED`

Finalization failed.

Preserve staging/temp/final candidate và mark recovery state.

`RECOVERY_REQUIRED`

Session state không thể trusted.

RecoveryManager phải reconcile DB/files trước normal operation.

## 6. Transition Table

From

Event

Guard

To

Action

`IDLE`

`StartRecordingRequested`

Active operator session exists; feature/permission/capability preliminary OK.

`PRECHECKING`

Create session intent và log request.

`IDLE`

`StartRecordingRequested`

No active operator session.

`IDLE`

Reject `OPERATOR_AUTH_REQUIRED`; show login screen.

`IDLE`

`EmergencyRecordingRequested`

Emergency override allowed nếu không có operator session.

`PRECHECKING`

Create emergency session intent bằng active operator hoặc `EMERGENCY_OVERRIDE_ADMIN`.

`IDLE`

`CaptureRequested`

Active operator session exists nếu capture là evidence; capture feature eligible.

Capture flow

Start image/audio capture precheck.

`PRECHECKING`

`PrecheckPassed`

Operator/session/emergency override, camera, storage, DB, battery/thermal policy OK.

`PREPARING_CAMERA`

Open/prepare camera adapter.

`PRECHECKING`

`PrecheckFailed`

Required check failed.

`IDLE` or failure state

Return mapped error và log reason.

`PREPARING_CAMERA`

`CameraReady`

Camera adapter ready.

`PREPARING_STORAGE`

Resolve storage và prepare temp file.

`PREPARING_STORAGE`

`TempFileReady`

Writable temp target available.

`STARTING`

Call `CameraService.startRecording`.

`STARTING`

`SdkRecordingStarted`

SDK confirms active recording.

`RECORDING`

Persist media session active state với operator snapshot.

`RECORDING`

`StopRequested`

Stop allowed by state guard.

`STOPPING`

Call `CameraService.stopRecording`.

`RECORDING`

`MarkImportantRequested`

Mark allowed.

`RECORDING`

Mark session important và persist marker.

`RECORDING`

`EmergencyEventReceived`

Emergency evidence behavior allowed.

`RECORDING`

Mark important và/hoặc insert emergency marker.

`RECORDING`

`StartRecordingRequested`

Already recording.

`RECORDING`

Reject as `ALREADY_RECORDING`; không start duplicate session.

`STOPPING`

`SdkRecordingStopped`

SDK reports file closed or closeable.

`FINALIZING`

Run finalization pipeline.

`FINALIZING`

`FinalizeSuccess`

Final media valid và DB update successful.

`BDMA_READY`

Persist final path và readiness.

`FINALIZING`

`FinalizeFailed`

Finalization failed.

`RECOVERY_REQUIRED`

Preserve staging/temp và log recovery reason.

Any active state

`AppCrashOrProcessDeath`

Runtime interrupted.

`RECOVERY_REQUIRED` on next startup

RecoveryManager reconciles DB/files/operator snapshot.

## 7. Recording Precheck

Recording precheck phải chạy trước camera SDK start.

Check

Required?

Fail Behavior

Active operator session

Yes cho normal recording/capture evidence

Reject với `OPERATOR_AUTH_REQUIRED`.

Emergency override

Required nếu emergency recording và không có operator session

Dùng system operator `EMERGENCY_OVERRIDE_ADMIN` nếu policy cho phép.

Recording feature eligibility

Yes

Block recording với eligibility reason.

Camera capability

Yes

Block recording với capability error.

Camera permission

Yes

Block recording; request permission nếu có thể.

Microphone permission

If audio enabled

Disable audio nếu policy cho phép; nếu không thì block.

Storage writable

Yes

Fallback hoặc block tùy storage mode/policy.

Free space threshold

Yes

Block start nếu thấp hơn minimum.

DB available

Yes

Block recording hoặc enter safe mode nếu không thể persist session/operator snapshot.

Battery/thermal safe

Policy-based

Defer/block/degrade optional modules theo policy.

Location availability

No cho core recording

Continue recording; mark GPS unavailable.

Realtime AI eligibility

No cho core recording

Start recording without AI.

Update/install active

Must be safe

Block recording hoặc cancel/defer update theo priority rule.

Active finalization

Yes

Reject/defer start cho đến khi finalization completes.

Core rule:

text## 8. Command and Event Model

Type

Examples

Notes

Commands

`StartRecordingCommand`, `StopRecordingCommand`, `MarkImportantCommand`, `StartEmergencyRecordingCommand`, `RecoverRecordingCommand`, `CaptureImageCommand`, `CaptureAudioCommand`.

Commands là requests và có thể bị reject bởi guard/precheck.

Events

`RecordingStarted`, `RecordingStopped`, `RecordingFinalized`, `ChecksumGenerated`, `BdmaReady`, `RecordingFailed`, `CaptureCompleted`.

Events đã xảy ra và cần được log/persist khi cần.

Auth/operator events

`OperatorSessionResolved`, `OperatorAuthRequired`, `EmergencyOverrideOperatorUsed`.

Dùng cho operator attribution và audit.

Error events

`CameraError`, `StorageLow`, `StorageUnavailable`, `MetadataFailed`, `FinalizeFailed`, `PermissionBlocked`, `OperatorAuthRequired`.

Map sang `DomainResult`/UI state.

Emergency events

`ManualSosTriggered`, `FallDetected`, `WeaponDetected`, `EmergencyResolved`.

EmergencyEventManager emit event; RecordingController quyết định recording behavior.

### 8.1 Command Result Direction

Result

Meaning

`ACCEPTED`

Command accepted và transition started.

`REJECTED_BY_STATE`

Current state không cho phép command.

`REJECTED_BY_PERMISSION`

Missing required permission.

`REJECTED_BY_CAPABILITY`

Device/feature không eligible.

`REJECTED_BY_STORAGE`

Storage precheck failed.

`REJECTED_OPERATOR_AUTH_REQUIRED`

Không có active operator session cho normal recording/capture.

`ACCEPTED_EMERGENCY_OVERRIDE`

Emergency command accepted bằng `EMERGENCY_OVERRIDE_ADMIN`.

`DEFERRED`

Command valid nhưng chờ current critical state hoàn tất.

`FAILED`

Command accepted nhưng runtime failed.

## 9. Capture Runtime

Capture runtime bao gồm image capture và audio-only capture nếu các feature đó enabled và eligible.

Capture Type

Required Precheck

Runtime Direction

Image Capture

Operator session nếu evidence capture, camera capability, camera permission, storage writable, DB available.

Capture single image, finalize file và mark BDMA-ready theo Data Contract.

Audio Capture

Operator session nếu evidence capture, microphone capability, microphone permission, storage writable, DB available.

Capture audio session, finalize file và mark BDMA-ready theo Data Contract.

Emergency Capture

Active operator hoặc emergency override nếu allowed.

Dùng cùng attribution principle như emergency recording.

Capture During Recording

Policy-based.

Chỉ allow nếu camera/SDK supports it và state guard permits.

Capture During Finalizing

Not allowed by default.

Defer/reject cho đến khi finalization completes.

## 10. Pre-record / Post-record Behavior

### 10.1 Pre-record Behavior

Rule

Direction

Pre-record buffer is not final media.

BDMA không được import pre-record cache như final media.

Pre-record buffer may run before login only if policy allows.

Final emergency recording attribution dùng active operator hoặc emergency override.

Pre-record buffer must be bounded.

Duration và size phải tuân theo validated settings và storage safety.

Pre-record attach requires valid buffer.

Nếu buffer missing/corrupted, emergency recording vẫn có thể continue without it.

Pre-record must not block live recording.

Nếu buffer workload unsafe, degrade/disable pre-record trước.

### 10.2 Post-record Behavior

Rule

Direction

Post-record is policy-controlled.

Duration và enable state lấy từ validated settings.

Emergency post-record has priority over normal stop.

Nếu emergency flow active, post-record có thể continue cho đến policy end.

No new session during finalization.

Finalization phải complete hoặc enter recovery trước khi new session starts.

Post-record failure must preserve existing evidence.

Không discard already captured media chỉ vì post-record failed.

## 11. Finalization Pipeline

text minimum
    ↓
Write/embed metadata if supported and required
    ↓
Apply encryption if enabled and safe
    ↓
Apply checksum behavior according to Data Contract and settings
    ↓
Move/rename to final Media folder
    ↓
Insert/update media session in dcam.db with operator snapshot
    ↓
Mark BDMA_READY
    ↓
Log final result]]>`BDMA_READY` nghĩa là final media safe cho BDMA scan/import. Nó không có nghĩa là BDMA đã import file.

## 12. Emergency Runtime Behavior

Emergency events có thể đến ở nhiều recording states khác nhau. RecordingController phải quyết định safe behavior dựa trên current state.

Current State

Emergency Behavior

`IDLE` with active operator

Start emergency recording bằng active operator snapshot.

`IDLE` without active operator

Start emergency recording bằng system operator `EMERGENCY_OVERRIDE_ADMIN`.

`PRECHECKING` / `PREPARING_CAMERA` / `PREPARING_STORAGE` / `STARTING`

Upgrade session intent sang emergency nếu có thể; dùng active operator hoặc emergency override.

`RECORDING`

Mark current session important và/hoặc insert emergency marker event; giữ existing operator attribution.

`STOPPING` / `FINALIZING`

Không interrupt finalization; queue emergency event và log.

`BDMA_READY` / `COMPLETED`

Create separate emergency event record nếu event đến sau session.

`CAMERA_FAILED` / `STORAGE_FAILED`

Log emergency failed, preserve diagnostics và notify user/support nếu có thể.

`RECOVERY_REQUIRED`

Preserve current artifacts trước; chỉ queue emergency diagnostics nếu safe.

Emergency evidence rule:

text## 13. Operator Attribution Behavior

Khi recording/capture starts, RecordingController phải resolve operator attribution trước khi tạo media session.

textRequired media session snapshot:

textAudit rule:

text## 14. Recovery Behavior

Scenario

Recovery Behavior

Crash while idle

Start normally và log crash marker.

Crash while recording

Detect active session/temp file; preserve operator snapshot từ `media_session`; try finalize nếu safe.

Crash during stopping

Verify SDK/file close result nếu có thể; nếu không thì recover từ staging file.

Crash during finalizing

Resume finalization nếu safe; nếu không thì mark incomplete và preserve staging/final candidate.

Reboot with temp file

Boot recovery scans DB + Temp trước normal startup; operator login vẫn required cho new normal recording.

Final file exists but DB not updated

Reconcile DB nếu file valid; preserve operator snapshot nếu available.

DB says active but no temp/final file exists

Mark session failed với missing file reason.

Operator user deleted/disabled after media was recorded

Không rewrite old operator snapshot; future recordings yêu cầu valid session hoặc emergency override.

Storage removed during recording

Stop/fail affected session an toàn; không continue writing blindly.

Emergency crash/reboot

Preserve local evidence candidate trước; log emergency recovery event.

Recovery safety rule:

text## 15. Runtime Persistence Direction

Detailed schema thuộc **DCAM SQLite Database Design**. Recording & Capture yêu cầu các runtime information dưới đây persistable khi cần:

text## 16. Logging Direction

Required examples:

text
[RECORDING] Operator auth required
[RECORDING] Emergency override operator used
[RECORDING] Precheck passed
[RECORDING] Precheck failed: 
[RECORDING] SDK recording started
[RECORDING] Finalization started
[RECORDING] Finalization completed
[RECORDING] BDMA_READY
[RECORDING] Recovery required
[EMERGENCY] Current recording marked important
[EMERGENCY] Emergency marker created]]>Không log plaintext credentials, raw media content, sensitive media data, raw AI frames, face data, raw location history, encryption keys, credentials hoặc secrets.

## 17. Open Questions / TBD

Item

Status

Exact CameraX / Camera2 / vendor SDK implementation

TBD

Whether pause/resume is supported by target BodyCamera SDK

TBD

Image capture during active recording policy

TBD

Audio-only capture format and policy

TBD

Pre-record physical buffer format

TBD

Whether pre-record buffer may run before login for non-emergency mode

TBD

Post-record duration and emergency-specific behavior

TBD

Metadata format and whether metadata is embedded or sidecar/DB-only

TBD

Finalization timeout and retry policy

TBD

Recording recovery marker format

TBD

Exact BDMA readiness DB fields

TBD

Emergency split segment vs marker implementation

TBD

Test matrix for crash/reboot/storage removed/operator session scenarios

TBD

## 18. Practical Conclusion

text