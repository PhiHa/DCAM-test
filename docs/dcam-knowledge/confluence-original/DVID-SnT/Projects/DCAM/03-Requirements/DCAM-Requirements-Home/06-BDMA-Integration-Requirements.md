# 06 - BDMA Integration Requirements

**Page ID**: 47743376  
**Version**: 4  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/47743376

---


# 06 - BDMA Integration Requirements

Item

Information

Project

DCAM (Android BodyCamera Application)

Document Type

Functional Requirements

Version

Approved 1.2

Status

Approved

Owner

Hoàng Ngọc Quyền

Technical Reviewer

Tech Lead

Approver

Hoàng Ngọc Quyền

Parent Folder

03 - Requirements / DCAM Requirements Home

Target Audience

PM/BA, Tech Lead, Android Developers, BDMA Developers, QA

Last Updated

2026-07-07

Related Documents

DCAM-BDMA Data Contract, 08 - DCAM-BDMA Integration Boundary, DCAM BDMA Integration Technical Design, DCAM Storage Design, DCAM SQLite Database Design

## 1. Purpose

Trang này ghi nhận requirement-level boundary giữa DCAM Android và BDMA Desktop.

Trang này không định nghĩa lại folder, naming, MD5, important media, encrypted media, cleanup matrix hoặc DB/write-back contract. Các rule đó thuộc **DCAM-BDMA Data Contract**.

## 2. Authoritative References

Topic

Authoritative Document

Local Summary

Media contract, MD5 for MP4 only, folder/naming, cleanup baseline

DCAM-BDMA Data Contract

BDMA implementation phải follow Data Contract.

Integration boundary

08 - DCAM-BDMA Integration Boundary

BDMA remains ADB-based and BDMA-initiated.

BDMA implementation flow

DCAM BDMA Integration Technical Design

Technical design describes scan/import/write-back implementation notes.

DB schema/write-back details

DCAM SQLite Database Design

DB-level fields and schema compatibility are handled there.

## 3. Requirement Scope

Requirement

Direction

Status

ADB-based Import

BDMA import boundary remains ADB-based.

Approved

Data Contract Compliance

BDMA must comply with DCAM-BDMA Data Contract.

Approved

Final Media Only

BDMA imports finalized media candidates only.

Approved

Temp/Cache Handling

Temp/cache handling follows Data Contract and Storage Design.

Approved

DB Write-back

BDMA write-back must follow Data Contract and DB schema compatibility.

Approved Direction

Logs

BDMA log access follows Data Contract.

Approved

## 4. Practical Conclusion

BDMA Requirements defines requirement-level integration intent.
DCAM-BDMA Data Contract owns concrete media/file/MD5/cleanup rules.
BDMA Technical Design owns implementation flow.