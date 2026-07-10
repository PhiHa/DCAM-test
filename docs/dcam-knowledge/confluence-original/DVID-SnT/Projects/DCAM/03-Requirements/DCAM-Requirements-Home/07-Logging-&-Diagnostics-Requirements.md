# 07 - Logging & Diagnostics Requirements

**Page ID**: 47776094  
**Version**: 4  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47776094

---


# 07 - Logging & Diagnostics Requirements

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

PM/BA, Tech Lead, Android Developers, AI/ML Engineer, QA, Support

Last Updated

2026-07-07

Related Jira

None

Related Documents

DCAM Device Capability & Feature Eligibility Design, DCAM State Machine Design, DCAM Sensor & Location Monitoring Design, DCAM Realtime AI Detection Design, DCAM Recording & Capture Design, DCAM Security & Encryption Design

## 1. Purpose

Trang này ghi nhận yêu cầu chức năng liên quan đến logging và diagnostics trong DCAM.

Logs phải hỗ trợ debug/support cho recording, storage, emergency, sensor monitoring, location tracking, realtime AI, update, remote config, BDMA và device capability / feature eligibility.

## 2. Logging Scope

Area

Requirement Direction

Status

Local Logs

DCAM ghi operational logs vào `logs.txt`.

Approved

Capability Logs

Log capability detection, feature eligibility and runtime pruning.

Approved Direction

Recording Logs

Log start, stop, post-record, finalize, error, recovery.

Approved

Storage Logs

Log storage full, external removed, fallback, write failure.

Approved

Emergency Logs

Log candidate, detected, recording started, clip saved, SOS state.

Approved Direction

Sensor / Location Logs

Log monitoring/tracking lifecycle, unavailable capability, degraded state.

Approved Direction

Realtime AI Logs

Log model state, runtime state, event candidate, degraded/pruned state.

Approved Direction

Remote Config Logs

Log fetch/validate/apply/reject result without secrets.

Approved

Update Logs

Log check/download/verify/install/failure and precondition block.

Approved

BDMA Logs

Help diagnose import readiness issues.

Approved

## 3. Capability Logging Requirements

Event

Description

Status

Capability Detection Started / Completed

Device capability detection lifecycle.

Approved Direction

Hardware Capability Missing

Missing optional or core hardware.

Approved Direction

Performance Class Evaluated

CPU/memory/storage/AI performance class result.

Approved Direction

Feature Eligibility Evaluated

Supported/degraded/unsupported state per feature.

Approved Direction

Runtime Flow Pruned

Unsupported feature runtime not initialized.

Approved Direction

Degraded Mode Applied

Feature starts in reduced mode.

Approved Direction

Capability Detection Error

Detection failure and fallback profile.

Approved Direction

Example logs:

text## 4. Required Log Events

Event Group

Required Events

Recording

Start, stop, post-record, finalize, error, recovery.

Emergency

Event candidate, event created, emergency recording started, clip saved, marker created, SOS pending/sent/failed.

Sensor

Monitoring started/stopped/error/degraded, sensor unavailable, motion event candidate.

Location

Tracking started/stopped, permission missing, sample buffered, sync result.

Realtime AI

AI started/stopped, model loaded/failed, event candidate, event created, degraded/pruned.

System

App boot/startup, service killed/restarted, permission denied.

Config/Update

Config fetched/applied/rejected, update blocked/verified/failed.

BDMA

Import readiness issue, DB/write-back issue if applicable.

## 5. Sensitive Logging Rules

DCAM must not log:

textAllowed diagnostic metadata examples:

Metadata

Allowed Direction

Event type

Allowed.

State transition

Allowed.

Timestamp

Allowed.

Error code/category

Allowed.

Capability reason code

Allowed.

Runtime state

Allowed.

Detector/model version

Allowed if safe.

Confidence score

Allowed if needed for diagnostics.

## 6. Diagnostics Requirements

Requirement

Description

Status

Capability Diagnostics

Logs must explain why feature is supported, degraded, unsupported or pruned.

Approved Direction

Recording Diagnostics

Logs support investigation of recording/finalization issues.

Approved

Emergency Diagnostics

Logs support event-source-to-evidence trace.

Approved Direction

Monitoring Diagnostics

Logs support sensor/GPS issue investigation.

Approved Direction

AI Diagnostics

Logs support model/runtime/performance issue investigation.

Approved Direction

BDMA Diagnostics

Logs help diagnose import readiness and source data issues.

Approved

Security Diagnostics

Logs validation failures without exposing secrets.

Approved

## 7. Practical Conclusion

Logging must make capability-aware runtime behavior explainable.

text