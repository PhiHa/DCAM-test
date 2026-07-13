# BDMA v1.0.141

**Page ID**: 51675152  
**Version**: 2  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/51675152

---


# Release Information

Item

Details

Product

BDMA

Version

v1.0.141

Release Date

11 Jul 2026

Release Type

Minor / Feature Release

Document version

1.0

Prepared By

[lavietanh.utc](https://ducviet.atlassian.net/wiki/people/70121:291a3abc-1d00-4cc3-84e8-af1e4491aeb1?ref=confluence)

Approved By

Pending

# 🎯 Release Overview

This release focuses on UI usability and safer default configuration behavior for BDMA.

Users get a cleaner warning notification experience, clearer processing progress states, a Settings-page reset action, and a dependent cleanup option for device folder deletion.

New installations also receive improved default Sync and Backup folder placement so Backup can be stored on a different internal drive when available.

# 🚀 New Features

Feature

Description

Notes

Default Settings Button

Added a Default Settings button on the Settings page. When clicked, the application shows a confirmation dialog; confirming restores settings on the current page to default values, while canceling leaves settings unchanged.

Tracked by [BDMA-158](https://ducviet.atlassian.net/browse/BDMA-158). This is a Settings-page usability feature.

Dependent Delete Empty Date Folders Option

Added the child option “Delete Empty Date Folders” under “Auto delete files on device after successful synced”. The child option is enabled only when the parent option is checked. If the parent is unchecked, the child option is automatically unchecked and disabled.

Tracked by [BDMA-158](https://ducviet.atlassian.net/browse/BDMA-158). Default state: parent unchecked, child unchecked and disabled.

Optimized Default Backup Location for New Installations

During fresh installation, BDMA detects available internal physical drives, keeps Sync on the operating system drive, and places Backup on a different internal drive with the largest free space when available. If no other internal drive exists, current behavior is retained.

Tracked by [BDMA-159](https://ducviet.atlassian.net/browse/BDMA-159). Applies only to fresh installations; upgrades and repairs keep existing configured locations.

# ⚡ Improvements

Area

Improvement

Impact

Warning Notification Interaction

Footer warning messages are changed from fixed text to a warning icon. Users can hover or click the icon to view warning details in a popup/popover above the icon. The popup supports one or multiple warnings and uses light fade-in/fade-out animation.

Improves footer clarity and reduces visual noise. Tracked by [BDMA-157](https://ducviet.atlassian.net/browse/BDMA-157).

Processing Progress Bar States

Processing progress bar now has clear Idle, Processing, and Completed states. Idle hides 0% and icons; Processing shows blue progress, processing icon, and percentage; Completed shows green 100% state with completion icon.

Progress updates and state transitions use smooth animation without affecting core processing behavior. Tracked by [BDMA-157](https://ducviet.atlassian.net/browse/BDMA-157).

Installer Build and R2 Publishing Reliability

Workflow now improves the installer delivery path from NSIS setup through R2 upload. NSIS installation retries up to four times when Chocolatey fails or `makensis.exe` is missing, using exponential delays of 10, 20, and 40 seconds. After the installer is built, it is copied to a branch-specific fixed name: `BDMA_dev.exe` for `develop` and `BDMA.exe` for `release`. The R2 upload validates required configuration, retries failed transfers, disables caching, and verifies the remote file using file size plus a workflow-specific upload ID.

Improves resilience against temporary install/upload failures and provides stable download URLs. Consider checking for an existing NSIS installation first, pinning the NSIS version, requiring exactly one generated installer, and adding workflow concurrency so older runs cannot overwrite newer R2 files.

Storage Location Validation and Permission Recovery

Sync and backup folders now reject removable drives such as USB, SD, MMC, and other removable bus types. The app refreshes Windows volume metadata in the background before folder selection, then validates the selected path without blocking the UI unnecessarily. Storage recovery also handles permission-related failures more gracefully by attempting ACL repair for BDMA-managed folders and showing clearer access-denied or restore-failed messages to the user.

Reduces the risk of configuring sync/backup data on unstable removable media, improves recovery when protected BDMA folders become inaccessible, and keeps restore progress and retry UI clearer during failure and completion states.

# 🐞 Bug Fixes

No bug fixes are called out in this release. [BDMA-157](https://ducviet.atlassian.net/browse/BDMA-157), [BDMA-158](https://ducviet.atlassian.net/browse/BDMA-158), and [BDMA-159](https://ducviet.atlassian.net/browse/BDMA-159) are documented as feature and usability enhancements.

# 🔐 Security Updates

No security-specific update is called out in this release.

The storage-location change in [BDMA-159](https://ducviet.atlassian.net/browse/BDMA-159) intentionally excludes removable storage devices and external drives from automatic Backup destination selection.

# 🧪 Testing Summary

Test Area

Result

Notes

Warning Notification UI

Pass

Validate footer shows warning icon only, popup appears on hover/click, multiple warnings are supported, popup appears above the icon, animation is smooth, and popup closes correctly.

Processing Progress Bar UI

Pass

Validate Idle, Processing, and Completed visual states, icons, colors, percentage visibility, and animation timing.

Settings Default Button

Pass

Validate button visibility, confirmation dialog, confirm reset behavior, and cancel behavior.

Dependent Delete Folder Option

Pass

Validate parent/child dependency, disabled state, auto-uncheck behavior, default state, and visual indentation.

Fresh Installation Storage Defaults

Pass

Validate internal-drive detection, Sync on OS drive, Backup on different internal drive when available, largest-free-space selection, and fallback behavior for single-drive machines.

Upgrade / Repair Regression

Pass

Validate existing Sync and Backup locations remain unchanged during upgrade or repair.

Theme and Language Regression

Pass

Validate warning popup and progress states in light/dark themes and English/Vietnamese UI.

# ⚠️ Known Issues / Limitations

Issue

Impact

Workaround

Backup location optimization applies only to fresh installations.

Existing installations will not automatically move Sync or Backup folders.

Keep current configured locations, or change storage paths manually if the application supports it.

Automatic Backup destination selection uses internal storage only.

Removable drives, external HDD/SSD, USB flash drives are not selected automatically.

Configure Backup manually if external or network storage is required.

# 📦 Deployment Information

Item

Details

Installer Version

v1.0.141 — BDMA-1.0.141-Setup.exe (166 MB)

Database Changes

None called out by [BDMA-157](https://ducviet.atlassian.net/browse/BDMA-157), [BDMA-158](https://ducviet.atlassian.net/browse/BDMA-158), or [BDMA-159](https://ducviet.atlassian.net/browse/BDMA-159).

Config Changes

Yes - default Settings values and new delete-empty-date-folders option behavior may affect configuration defaults

Backup Required Before Update

Recommended before any production update

Rollback Available

Yes - requires previous approved installer; preserve existing configuration backup before rollback

# 🔄 Upgrade / Migration Notes

Back up the current BDMA configuration, database, and application data before updating.

Install BDMA v1.0.141 using the approved installer.

Confirm application startup and Settings page availability.

Verify warning notification popup behavior and progress bar states.

Verify the Default Settings button and dependent delete-empty-date-folders option.

For fresh installations, verify default Sync and Backup folder locations.

For upgrades or repairs, confirm existing Sync and Backup folder locations remain unchanged.

# 📊 Operational Notes

Warning messages are no longer displayed as fixed footer text; users access warning details through the warning icon popup.

Progress bar visuals are state-based and should remain cosmetic only; they must not change the underlying processing workflow.

The delete-empty-date-folders option depends on the successful-delete-files-after-sync parent setting.

Fresh-install Backup placement prioritizes a different internal physical disk with the largest free space. If unavailable, fallback behavior keeps storage on the OS drive.

Existing installations should preserve their configured Sync and Backup locations.

# 📌 Rollback Notes

Scenario

Rollback Action

UI interaction issue after upgrade

Reinstall the previous approved stable version and validate warning footer, progress bar, and Settings page behavior.

Settings behavior issue

Restore the previous configuration backup if settings were changed, then reinstall the previous approved stable version if required.

Fresh-install storage default is unsuitable

Manually configure Sync/Backup locations if supported, or reinstall the previous approved stable version and validate storage paths.

# 🧠 PM / Engineering Notes

[BDMA-159](https://ducviet.atlassian.net/browse/BDMA-159) includes an implementation priority note: choose a different physical internal disk with the largest free space first; otherwise another partition on the OS disk with the largest free space; otherwise the OS partition itself.

# 🔗 Related Documents

[BDMA-157](https://ducviet.atlassian.net/browse/BDMA-157) - Add UI animations for warning notifications and processing progress bar

[BDMA-158](https://ducviet.atlassian.net/browse/BDMA-158) - Add default settings button and dependent delete-folder option

[BDMA-159](https://ducviet.atlassian.net/browse/BDMA-159) - Optimize default Sync and Backup storage locations for new installations

[Previous Release - BDMA v1.0.133](/wiki/spaces/DVID/pages/47710226/BDMA+v1.0.133)

[Default Settings and Delete Empty Date Folders](/wiki/spaces/DVID/pages/51052612/Default+Settings+and+Delete+Empty+Date+Folders)

[Full Changelog v1.0.134..v1.0.141](https://github.com/DucVietTech/bdma/compare/v1.0.133...v1.0.141)

[GitHub Release v1.0.141](https://github.com/DucVietTech/bdma/releases/tag/v1.0.141)