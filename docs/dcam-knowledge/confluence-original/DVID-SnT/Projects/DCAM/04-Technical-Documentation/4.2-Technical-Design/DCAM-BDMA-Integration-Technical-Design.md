# DCAM BDMA Integration Technical Design

**Page ID**: 48595030  
**Version**: 5  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48595030

---


# DCAM BDMA Integration Technical Design

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Technical Design

Version

Draft 0.5

Status

Draft

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

4.2 - Technical Design

Target Audience

Tech Lead, Android Developers, BDMA Developers, QA

Last Updated

2026-07-08

Related Documents

DCAM-BDMA Data Contract, 06 - BDMA Integration Requirements, 08 - DCAM-BDMA Integration Boundary, DCAM Storage Design, DCAM SQLite Database Design

## 1. Purpose

Tài liệu này mô tả implementation direction cho BDMA import/write-back flow.

Tài liệu này không copy lại Data Contract. Concrete rules cho media naming, folder structure, `_IMP`, `_enc`, `.md5` for MP4 only, app/contract compatibility, import result baseline và cleanup baseline thuộc **DCAM-BDMA Data Contract**.

## 2. Authoritative References

Topic

Authoritative Document

Local Summary

Data/file/media/app contract

DCAM-BDMA Data Contract

Source of truth cho file/folder/naming/MD5/cleanup/app-contract behavior.

Requirement boundary

06 - BDMA Integration Requirements

Requirement-level integration intent.

Architecture boundary

08 - DCAM-BDMA Integration Boundary

ADB-based và BDMA-initiated boundary.

Storage implementation

DCAM Storage Design

Implement Data Contract storage behavior.

DB schema/write-back

DCAM SQLite Database Design

Định nghĩa schema/state compatibility cho DB write-back.

## 3. BDMA Implementation Flow

Detect Android device through ADB
        ↓
Locate DCAM storage roots according to Data Contract
        ↓
Read device config / database / logs according to Data Contract
        ↓
Identify app and contract metadata:
    app_code
    app_package_name
    app_version_code
    dcam_data_contract_version
    media_contract_version
    encoder_contract_version
        ↓
Check BDMA built-in compatibility table
        ↓
Scan finalized media locations according to Data Contract
        ↓
Build import candidate list
        ↓
Apply Data Contract verification/import rules
        ↓
Write import state if allowed by schema/contract
        ↓
Apply cleanup policy if allowed by Data Contract
## 4. App / Contract Recognition Rule

BDMA must identify what Android app and contract version it is communicating with before applying import logic.

Metadata

Purpose

`app_code`

Identify DCAM app family, e.g. `DCAM_ANDROID`.

`app_package_name`

Verify Android package identity if available.

`app_version_code` / `app_version_name`

Check app compatibility.

`dcam_data_contract_version`

Check overall DCAM-BDMA data contract compatibility.

`media_contract_version`

Check media folder/naming/checksum/import/cleanup compatibility.

`encoder_contract_version`

Check fixed DCAM encoder behavior compatibility.

BDMA must not depend on a cloud-provided decoder profile.

Not used:

bdma_decoder_profile_id
decoder_profile_id
dynamic_decoder_profile
BDMA should use an internal compatibility table such as:

Supported:
- app_code = DCAM_ANDROID
- dcam_data_contract_version >= supported_minimum
- media_contract_version >= supported_minimum
- encoder_contract_version = FIXED_V1
If unsupported, BDMA should block import or show a compatibility warning and must not modify source media.

## 5. Device Information Usage

BDMA may read device information from `dcam_config.cson` and/or `dcam.db` for display, support and import context.

Field

Usage

`serial_number`

Device display/support identifier; not primary key.

`owner_name`

Owner/customer/agency display.

`manufacture_date`

Device manufacture date; expected format `YYYY-MM-DD`.

`device_model` / `firmware_version`

Device compatibility/support display.

BDMA must not treat `serial_number`, `owner_name` or `manufacture_date` as a primary identity key. Device cloud identity remains `dcam_cloud_device_id` where available.

## 6. Local Implementation Notes

Area

Direction

Scan Order

Dùng Data Contract làm source of truth.

Verification

Dùng Data Contract làm source of truth.

Cleanup

Dùng Data Contract làm source of truth.

App Recognition

Dùng app/data/media/encoder contract metadata và BDMA built-in compatibility table.

Decoder Profile

Không dùng `bdma_decoder_profile_id`.

Device Info Display

Hiển thị serial, owner name, manufacture date nếu có.

DB Write-back

Dùng Data Contract + SQLite Database Design.

Error Handling

Map implementation errors sang Data Contract result categories.

## 7. Practical Conclusion

BDMA Technical Design owns implementation flow.
DCAM-BDMA Data Contract owns the actual data/file/import rules.
BDMA nhận dạng app bằng app/data/media/encoder contract metadata.
BDMA không dùng bdma_decoder_profile_id.
BDMA có thể hiển thị owner_name và manufacture_date như device information.
Không duplicate full contract tables tại đây.