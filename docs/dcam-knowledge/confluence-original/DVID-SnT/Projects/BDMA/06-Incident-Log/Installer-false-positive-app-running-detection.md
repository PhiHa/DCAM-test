# Installer false-positive app running detection

**Page ID**: 41615363  
**Version**: 4  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/41615363

---


Incident log for an installer issue where the running-process check incorrectly matched the installer itself.

# Incident Information

Field

Value

Incident ID

INC-2026-06-003

Date Reported

Reporter

 

Severity

Medium

Status

Resolved

Affected Module

Installer

Environment

Windows installer

# Summary

The installer detects a running app by searching for process name `BDMA.exe` to decide whether to show a force-close dialog. If the installer executable itself is also named `BDMA.exe`, the detection logic matches the installer process and produces a false positive.

# Impact

The installer may incorrectly report that the application is already running.

Users may be prompted to force close an app that is not actually running.

The installation flow becomes confusing and may interrupt or delay installation.

# Timeline

Time

Event

Issue identified from installer behavior review

Root cause confirmed in process-name matching logic

Fix implemented

Incident resolved

# Root Cause Analysis

Old behavior:

The installer detected a running app by finding process name `BDMA.exe`. If the installer itself was named `BDMA.exe`, it triggered a false positive.

Relevant logic:

nsiswide760The check relied only on process name, which is not unique enough in this scenario. Because the installer process could share the same executable name as the installed application, the detection logic could not distinguish between the installer and the actual installed app process.

As a result, the installer interpreted its own process as evidence that the app was running and showed the force-close dialog incorrectly.

# Resolution

Update the running-app detection logic so that a process is considered the installed application only when its process id doesn't matches the current installer PID.

This change removes the ambiguity caused by name-only matching and ensures that the installer ignores unrelated processes, including itself, even if they share the same executable name.

# Preventive Actions

Avoid using executable name alone for installer process detection.

Add regression coverage for installer scenarios where the installer executable name matches the application executable name.

Prefer path-based or PID or install-location-based validation when checking whether an application is already running.

# Lessons Learned

Process-name matching alone is fragile in installer workflows.

Installer safeguards should identify the installed application by stable context (current process PID, install dir,…).

# Approval

Role

Name

Date

Reporter

 

PM

 

Reviewer