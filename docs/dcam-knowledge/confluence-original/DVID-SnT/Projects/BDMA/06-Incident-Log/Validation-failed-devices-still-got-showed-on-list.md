# Validation failed devices still got showed on list

**Page ID**: 32899073  
**Version**: 1  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/32899073

---


# Incident Log

## Incident Information

Field

Value

Incident ID

INC-2026-06-001

Date Reported

2026-06-04

Reporter

[Dinh Nhan](https://ducviet.atlassian.net/wiki/people/70121:52166504-9fa8-44ba-969f-16f03f3dc4d6?ref=confluence) 

Severity

High

Status

Resolved

Affected Module

Device List 

Environment

Development

# Summary

Validation failed devices still got showed on device list of admin/user dashboard.

# Impact

The device list may display invalid devices.

Users may mistakenly think that the device has passed the authentication step.

The card lacks cameraID, so it cannot save a valid device.

The double-click flow failed silently: the UI has a card, but the device-saving logic rejects it because the validate result is invalid.

# Timeline

Time

Event

2026-06-04 09:00

Incident detected

2026-06-04 09:10

Investigation started

2026-06-04 10:00

Root cause identified

2026-06-04 12:00

Fix deployed

2026-06-04 13:30

Incident resolved

# Root Cause Analysis

PR: [https://github.com/DucVietTech/bdma/pull/55](https://github.com/DucVietTech/bdma/pull/55)
PR name: Bdma 60 restore data to backup
Source branch: BDMA-60-Restore-data-to-backup
Target branch: develop
Commit: a54388d
Commit time: 2026-05-08 16:12:04 +0700
PR size: 43 files changed, +1,468 / -519

In DashboardController.java, PR #55 adds a branch to handle DeviceEvent.EventType.UNVALIDATED and adds the handleUnvalidated() method.
The current logic only checks result == null, but does not check result.isValid(). As a result, a DeviceValidationResult that exists but has valid = false still proceeds to addTransientDevice(result).
addTransientDevice(result) creates a DeviceSummary with Status.UNVALIDATED and a cameraID taken from result.getCameraId(). When the result is invalid, cameraID may be empty/null, leading to a card with an empty cameraID appearing in the UI.

# Resolution

Edit DashboardController.handleUnvalidated(): change condition from:

if (result == null) {
    return;
}

to:

if (result == null || !result.isValid()) {
    return;
}

# Preventive Actions

Any device card shown in UI must pass DeviceValidationResult.isValid()

Add structured failure logs: Log failed verification with: ADB serial, ro.product.model, ro.product.device, ro.board.platform, fail reason

# Related Items

Type

Reference

Jira Issue

|  
Release

|  
Decision Log

|  
Technical Knowledge

|  

# Lessons Learned

Any device card shown in UI must pass DeviceValidationResult.isValid()

Should has structured failure logs: Log failed verification with: ADB serial, ro.product.model, ro.product.device, ro.board.platform, fail reason

# Approval

Role

Name

Date

Reporter

[Dinh Nhan](https://ducviet.atlassian.net/wiki/people/70121:52166504-9fa8-44ba-969f-16f03f3dc4d6?ref=confluence) 

04 Jun 2026

PM

|  |  
Reviewer

|  |