# Devices without external storage can't be synced

**Page ID**: 39157761  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/39157761

---


# Incident Log

## Incident Information

Field

Value

Incident ID

INC-2026-06-002

Date Reported

2026-06-19

Reporter

 

Severity

High

Status

Resolved

Affected Module

Device Sync

Environment

Production

# Summary

Devices without external storage cannot be synced. A preflight change intended to stop sync on malfunctioned devices accidentally blocked devices that only have internal storage.

# Impact

Devices with internal storage only cannot start or complete the sync process.

# Timeline

Time

Event

2026-06-20 14:00

Incident detected

2026-06-20 14:30

Investigation started

2026-06-20  14:40

Root cause identified

2026-06-20  18:00

Fix deployed

2026-06-20  18:00

Incident resolved

# Root Cause Analysis

Cause: [Refactor folder security, sync & adb log &middot; DucVietTech/bdma@14592af](https://github.com/DucVietTech/bdma/commit/14592af6327c3dce318cc253542f77c58952c7cd)
Commit: 14592af
Commit time: 2026-05-02
Change: A preflight path that previously returned an empty list was changed to throw an exception.

The change was intended to prevent the sync process from starting on malfunctioned devices by failing fast when expected storage could not be resolved. However, devices with internal storage only do not expose external storage. The new exception path treated that valid device state as a preflight failure, so sync was blocked.

# Resolution

Update the storage preflight handling, reversed exception throw back to return empty list so a missing external-storage path does not fail devices that only support internal storage.

Allow sync to continue using internal storage when external storage is unavailable.

# Preventive Actions

Add regression coverage for devices that have internal storage only and no external storage.

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

Fail-fast preflight checks must separate malfunctioned-device states from supported device configurations.

Internal-storage-only devices should be sync-able.

# Approval

Role

Name

Date

Reporter

 

PM

 

|  
Reviewer

 

|