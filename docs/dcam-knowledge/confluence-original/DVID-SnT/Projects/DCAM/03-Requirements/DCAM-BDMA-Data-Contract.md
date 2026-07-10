# DCAM-BDMA Data Contract

**Page ID**: 47743153  
**Version**: 8  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47743153

---


# DCAM-BDMA Data Contract

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Data Contract / Integration Contract

Version

Approved 1.7

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead / BDMA Lead / Security Reviewer / Cloud Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

03 - Requirements

Target Audience

PM/BA, Tech Lead, Android Developers, BDMA Developers, QA, Support, Cloud/WebServer Team

Last Updated

2026-07-09

Related Jira

None

Related Documents

DCAM Factory Provisioning & Device Production SOP, DCAM Web Portal & Device API Contract, DCAM MVP Scope, DCAM Architecture Home, 04 - Device Configuration Requirements, 05 - User & Device Operation Requirements, 06 - Cloud Services, Update & Configuration Architecture, 09 - System Settings Requirements, DCAM SQLite Database Design, DCAM Android Operation Design, DCAM Recording & Capture Design, DCAM Storage Design, DCAM Security & Encryption Design, 05 - Data, Storage & BDMA Architecture, 08 - DCAM-BDMA Integration Boundary, DCAM Documentation Governance

## 1. Purpose

Tài liệu này định nghĩa **Data Contract** giữa:

**DCAM Android Application** chạy trên thiết bị **BodyCamera**.

**BDMA Desktop** chạy trên máy tính **Windows/Desktop**.

**Firebase/WebServer** quản lý device identity, Web Portal provisioning và remote config metadata.

Contract này thống nhất cách DCAM tạo, lưu trữ và expose dữ liệu để BDMA có thể read, verify, import, update, sync hoặc delete theo rule đã thống nhất.

Contract này follows **DCAM Factory Provisioning & Device Production SOP Draft 1.0** cho identity baseline:

textContract này là **source of truth** cho các nhóm rule sau:

textRemote config payload fields không được định nghĩa trong tài liệu này. Nội dung đó vẫn TBD và thuộc **System Settings / Cloud Architecture**.

## 2. System Boundary

text### 2.1 DCAM Responsibilities

Area

Responsibility

Media creation

Tạo video, image và audio media.

Fixed media encoder

Tạo media theo fixed DCAM encoder behavior; BDMA không cần dynamic decoder profile.

App/contract metadata

Expose app identity, data contract version, media contract version và encoder contract version để BDMA compatibility check.

Important media marking

Tạo important files với suffix `_IMP`.

Encryption suffix

Tạo encrypted media với suffix `_enc` hoặc `_IMP_enc` khi encryption enabled.

Media storage

Lưu media vào Internal hoặc External storage theo user setting / Auto fallback.

MD5 generation

Chỉ tạo `.md5` cho video `.mp4` nếu checksum feature enabled.

Device config file

Tạo và maintain `dcam_config.cson` cho device information only.

Device identity local state

Lưu local mirror của `dcam_cloud_device_id`, `serial_number`, owner, manufacture date và provisioning state trong `dcam.db`.

SD Identity File sync

Sync app-private `serial_number` ra SD Identity File làm recovery cache nếu feature enabled.

SQLite database

Tạo và maintain `dcam.db` cho user/operator, settings, runtime state, media state, tracking, import/write-back state, identity và config cache.

User/operator runtime

Quản lý operator login session offline trên DCAM.

Logs

Ghi operational logs vào `logs.txt`.

BDMA readiness

Chỉ expose final media khi safe for BDMA scan/import.

### 2.2 BDMA Responsibilities

Area

Responsibility

Device access

Kết nối và đọc dữ liệu qua ADB.

App/contract recognition

Nhận dạng DCAM app bằng app/package/contract metadata và dùng built-in import/decode logic tương ứng.

Decoder profile

Không dùng `bdma_decoder_profile_id`; encoder phía DCAM là fixed contract và BDMA xử lý bằng compatibility table nội bộ.

Media discovery

Scan media ở cả External và Internal DCAM Media Roots.

MD5 verification

Chỉ verify `.md5` cho video `.mp4` nếu file `.md5` tồn tại.

Import

Import media vào BDMA managed storage/index.

User/operator administration

Quản lý user/operator trong BDMA database.

User/operator sync

Đồng bộ user/operator hai chiều với DCAM qua ADB.

Device config read

Read `dcam_config.cson` để lấy device information.

Database write-back

Write-back vào `dcam.db` chỉ theo approved tables/fields và schema/version rules.

Logs reading

Đọc `logs.txt` để diagnostics; không ghi, sửa hoặc xóa.

Cleanup

Xóa source media sau import theo cleanup policy.

BDMA provisioning không bắt buộc cho normal factory provisioning. **DSetup + Web Portal/Firebase serial-number based provisioning** là default factory/business provisioning baseline.

### 2.3 Firebase/WebServer Responsibilities

Area

Responsibility

Device primary record

Quản lý server-side device record theo `dcam_cloud_device_id`.

Serial lookup

Quản lý lookup mapping từ `serial_number` tới `dcam_cloud_device_id`.

Web Portal/Firebase provisioning

Tạo/restore device record và serial lookup mapping sau authorized portal/factory action.

Device information management

Lưu latest `serial_number`, `owner_name`, `manufacture_date`, device model, firmware và related device information.

Serial history

Lưu latest `serial_number` và `serial_history` nếu approved rework/admin flow cần.

App/contract metadata

Lưu app identity, app version, data contract version, media contract version và encoder contract version nếu cần cho support/compatibility.

Remote config metadata

Lưu target config revision / profile metadata.

Audit

Audit provisioning, serial/device information change, config publish/apply result.

## 3. Device Identity and Device Information Contract

DCAM device identity uses `serial_number` as Hardware Identity and `dcam_cloud_device_id` as Cloud Identity.

Field

Role

Storage

`dcam_cloud_device_id`

Firebase/WebServer primary cloud key.

Firebase/WebServer + `dcam.db`.

`serial_number`

Hardware Identity / primary recovery key.

App-private storage, `dcam_config.cson`, `dcam.db` mirror, Firebase/WebServer, SD Identity File cache.

SD Identity File

Recovery cache on external SD card; not Hardware Identity.

External SD card, approved path from Factory SOP.

`owner_name`

Mutable/semi-static device information for owner/customer/agency display.

`dcam_config.cson`, `dcam.db` mirror, Firebase/WebServer.

`manufacture_date`

Semi-static device manufacture date using ISO `YYYY-MM-DD`.

`dcam_config.cson`, `dcam.db` mirror, Firebase/WebServer.

`serial_history`

Lịch sử serial values if approved rework/admin flow changes serial.

Firebase/WebServer; optional DB mirror.

`firebase_installation_id`

App-install instance metadata only.

Firebase/WebServer + optional DB metadata. Not a device identity key.

Rules:

textLogical server mapping:

text### 3.1 App / Media / Encoder Compatibility Contract

BDMA nhận dạng app và contract version, không nhận dynamic decoder profile từ cloud.

Field

Purpose

`app_code`

Mã app nội bộ, ví dụ `DCAM_ANDROID`.

`app_package_name`

Android package name để nhận dạng app trên device.

`app_version_name` / `app_version_code`

Version app đang chạy.

`dcam_data_contract_version`

Version tổng thể contract DCAM &harr; BDMA.

`media_contract_version`

Version folder/naming/suffix/checksum/import/cleanup rule.

`encoder_contract_version`

Version fixed encoder/media encoding behavior của DCAM.

Not used:

textBDMA phải dùng built-in compatibility table để quyết định app/contract version có được support hay không.

## 4. Web Portal / Factory Provisioning Contract

Default factory/business provisioning uses **DSetup + serial-number based Web Portal/Firebase provisioning**.

textExact QR signing/expiration/API details vẫn TBD. If QR is used, it must be business provisioning only and must not contain `ANDROID_ID` or `android_id_hash`.

## 5. Storage Location Strategy

Storage Type

Purpose

Internal Storage

Lưu `dcam_config.cson`, `dcam.db`, `logs.txt` và có thể lưu media khi user chọn Internal hoặc Auto fallback.

External Storage

Ưu tiên lưu media vì thường có dung lượng lớn hơn; cũng có thể lưu SD Identity File recovery cache nếu feature enabled.

Fixed Internal Files:

textOptional External Recovery Cache:

textMedia storage rule:

User Setting

Behavior

Internal

Lưu media vào Internal DCAM Media Root.

External

Lưu media vào External DCAM Media Root nếu hợp lệ.

Auto

Ưu tiên External; fallback sang Internal trước khi recording nếu External unavailable/full/missing/not writable/invalid.

BDMA phải scan cả hai vị trí, ưu tiên External trước rồi đến Internal.

## 6. Logical Folder Contract

### 6.1 Internal DCAM Storage Root

text### 6.2 External DCAM Storage Root

textPhysical path thực tế là device-specific và phải được validate trên BodyCamera thật. Android-side storage mechanics thuộc **DCAM Storage Design**. SD Identity File path/schema/signature policy thuộc **DCAM Factory Provisioning & Device Production SOP** và Security Review.

## 7. Media File Naming Contract

Supported media formats:

Media Type

Supported Format

Image

`.jpg`

Video

`.mp4`

Audio

`.mp3`, `.aac`, `.wav`

Normal naming:

text]]>Important and encrypted suffix examples:

text
DCAM_XXXXXX_ZZZZZZ_YYYYMMDD_HHMMSS_enc.
DCAM_XXXXXX_ZZZZZZ_YYYYMMDD_HHMMSS_IMP_enc.]]>Encryption implementation details thuộc **DCAM Security & Encryption Design**.

## 8. MD5 Checksum Contract

File `.md5` chỉ áp dụng cho video `.mp4`.

Rule

Description

Applies to `.mp4` only

Áp dụng cho normal, important, encrypted và important encrypted video.

Same folder

`.md5` lưu cùng folder với `.mp4`.

Same base name

`.md5` dùng cùng base name với `.mp4`.

Optional for `.mp4` import

Nếu thiếu `.md5`, BDMA vẫn có thể import nhưng result là Unverified/warning.

Not applicable for image/audio

Không tạo, không tìm và không warning nếu `.jpg`, `.mp3`, `.aac`, `.wav` thiếu `.md5`.

## 9. BDMA Import and Cleanup Rules

BDMA scans:

textBDMA must not treat SD Identity File as media and must not import it as evidence.

BDMA phải ignore:

texttrừ khi một contract version trong tương lai định nghĩa rõ temp recovery/import behavior.

Case

BDMA Import

Delete Source

`.mp4` has `.md5` and verify pass

Yes

Cho phép auto delete nếu cleanup policy enabled.

`.mp4` has `.md5` and verify fail

No

No.

`.mp4` missing `.md5`

Yes, Unverified

User phải confirm theo từng file.

`.jpg`, `.mp3`, `.aac`, `.wav`

Yes nếu import succeeds.

Cho phép auto delete nếu cleanup policy enabled.

Unsupported encrypted media

No hoặc deferred.

No.

File in `Temp`

No.

No.

SD Identity File

No media import.

No media cleanup.

BDMA không được cleanup `dcam_config.cson`, `dcam.db`, `logs.txt` hoặc SD Identity File trừ khi future approved support/factory contract cho phép rõ ràng.

## 10. Device Config CSON Contract

`dcam_config.cson` is stored at:

textPurpose:

textAllowed examples:

Area

Example

Device display identity

CameraID / device display code.

Device name

BodyCamera name.

Device model

Model.

Serial number

Hardware Identity / recovery key mirror.

Owner name

Owner/customer/agency/organization display name.

Manufacture date

Device manufacture date in `YYYY-MM-DD` format.

Firmware/hardware version

Version information.

App/contract information

App version and contract version metadata if needed for BDMA compatibility display.

Example direction:

csonNot allowed in `dcam_config.cson`:

text`dcam_config.cson` có thể được restore from app-private serial/server after serial-based identity restore/provisioning if file bị mất.

## 11. SQLite Database Contract

`dcam.db` is stored at:

text`dcam.db` chứa runtime data và operational data như sau:

Data Group

Description

Device identity/provisioning

`dcam_cloud_device_id`, `serial_number`, serial source, owner name, manufacture date, app/contract metadata and provisioning state.

SD Identity File sync state

Optional local state for SD recovery cache availability/sync result.

Remote config cache

Target/pending/applied config revision và apply status.

User/operator data

User profiles, auth method references, operator session history và sync state.

Settings

Operational settings và applied setting state.

Runtime state

App/runtime/module/session state cần cho DCAM.

Media/session state

Recording/capture session, operator snapshot, BDMA readiness và recovery state.

BDMA import/write-back state

Import status, history, external change log và sync checkpoint.

Diagnostics

Diagnostic events nếu được lưu trong DB.

DB ownership rule:

text## 12. User / Operator Sync Contract

DCAM và BDMA đều maintain user/operator management data.

textRequired emergency system identity:

textNormal recording/capture evidence yêu cầu authenticated operator session trên DCAM. Emergency recording có thể dùng system operator nếu chưa có operator logged in.

## 13. Logs File Contract

`logs.txt` is stored at:

text
Actor

Permission

DCAM

Read / Write

BDMA

Read-only

BDMA không được write, modify, truncate hoặc delete `logs.txt`.

## 14. BDMA Write-back Rules

BDMA may write-back to:

Target

Permission

`dcam_config.cson`

Read / Write / Update cho device information only nếu có approved contract path.

`dcam.db` user/profile/auth/sync tables

Theo User Sync Contract và DB ownership.

`dcam.db` approved operational/import tables

Theo SQLite Database Design và System Settings.

Source media files

Delete sau successful import theo cleanup policy.

Matching `.md5` files

Delete sau source `.mp4` video cleanup.

BDMA must not write-back to:

Target

Rule

`logs.txt`

Read-only.

Media content before import

Không được modify.

Embedded media metadata

Không được modify.

`.md5` content

Không được modify vì dùng cho video verification.

`Temp` files

Không được process trừ khi contract tương lai định nghĩa rõ.

SD Identity File

Không được modify/delete trừ khi future approved support/factory contract cho phép.

Active `operator_session` runtime state

Android runtime-owned.

Active `media_session` lifecycle fields

Android runtime-owned, trừ khi có explicit approval.

Device identity primary mapping

Thuộc Firebase/WebServer provisioning flow.

`bdma_decoder_profile_id`

Không áp dụng; field này không thuộc DCAM contract.

## 15. Error and Warning Cases

Case

Type

Expected Handling

Missing `.md5` for `.mp4`

Warning

BDMA imports video as Unverified.

Missing `.md5` for image/audio

Not applicable

Import normally; không warning.

MD5 mismatch for `.mp4`

Error

Không import; không delete source.

DB schema unsupported

Error

Không write; report compatibility error.

App/contract version unsupported

Error

BDMA blocks import or shows compatibility warning; no dynamic decoder profile is fetched.

Invalid user/auth sync data

Error

Reject record, giữ last valid state và log conflict/error.

Serial lookup not found

Provisioning

DCAM enters `PROVISIONING_REQUIRED` or approved factory/admin provisioning flow.

Server unavailable and local identity exists

Offline fallback

DCAM tiếp tục dùng last valid local identity/config cache.

Server unavailable and no local identity

Provisioning wait

DCAM chờ network/admin action.

SD Identity File missing while app-private serial exists

Recovery cache warning

DCAM recreates SD Identity File if SD card is available.

SD Identity File conflict with app-private serial

Warning / policy event

App-private serial wins; overwrite SD file or raise warning according to policy.

Logs unreadable

Warning

Tiếp tục import nếu media hợp lệ.

External storage missing

Warning

Scan Internal fallback.

Cleanup failed

Warning/Error

Import vẫn valid; cleanup issue phải được report.

## 16. Versioning

Versioned Area

Rule

Data Contract

Version của tài liệu này là contract baseline.

DB schema

`dcam.db` phải expose schema/version metadata để BDMA compatibility check.

App contract metadata

App package/version và contract metadata phải đủ để BDMA nhận dạng app.

Media contract

`media_contract_version` version hóa folder/naming/suffix/checksum/import/cleanup rule.

Encoder contract

`encoder_contract_version` version hóa fixed DCAM encoder behavior; không dùng `bdma_decoder_profile_id`.

Device identity

Server identity/provisioning contract phải được versioned.

SD Identity File

Path/schema/signature/checksum policy must be versioned if feature enabled.

Remote config

Config profile/revision schema phải được versioned.

User sync

User sync records phải dùng revision/change tokens.

Media naming

Breaking changes yêu cầu contract update và BDMA compatibility review.

Auth methods

Credential/auth sync format changes yêu cầu Security Design và contract review.

## 17. Practical Conclusion

text