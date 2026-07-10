# BDMA v1.0.116

**Page ID**: 37847105  
**Version**: 3  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/37847105

---


## Release Information

Item

Details

Product

BDMA

Version

v1.0.116

Release Date

Release Type

Minor / Feature Release

Prepared By

Approved By

Pending

# 🎯 Release Overview

This release improves BDMA device management, media playback, external media decryption, recovery behavior, and application stability. It introduces a dedicated Device Management screen, a redesigned device list with device specification viewing, support for playable SOS and MP3 files, external encrypted media decryption, automatic missing file restore, and safer shutdown handling for active operations.

# 🚀 New Features

Feature

Description

Notes

Device Management Screen

Added a dedicated screen for managing body camera devices.

Improves administrator device control.

Reworked Device List UI

Updated the device list interface and added the ability to view device specifications.

Helps administrators review device details more easily.

External Media Decryption

Added a screen to select encrypted media files outside the BDMA application and decrypt them.

Related to PR #117.

Model Whitelist

Added a new whitelist mechanism for supported device models.

Improves device validation control.

# ⚡ Improvements

Area

Improvement

Impact

Device Save Confirmation

Hidden the device model from the device save confirmation dialog.

Reduces unnecessary detail in the confirmation flow.

Media Playback

SOS and MP3 files are now playable.

Resolves the previous limitation where these file types could not be viewed or played.

Missing File Recovery

Added automatic restore for missing files when needed.

Improves recovery and reduces manual administrator action.

Progress Display

Changed progress button behavior to use a progress bar.

Provides clearer feedback during long-running operations.

Application Close Handling

Added alerts when the application is closed while sync, export, restore, or decrypt operations are running.

Helps prevent accidental interruption of active work.

Temporary File Cleanup

Added an ensure-cleanup step to remove leftover `.tmp` files when the application closes.

Helps keep storage clean and prevents temporary files from being retained after shutdown.

# 🐞 Bug Fixes

Issue

Description

Status

External Storage Sync

Fixed sync not working when external storage is unavailable.

FixedGreen

# 🔐 Security Updates

Item

Description

Notes

Model Whitelist

Added a whitelist for allowed device models.

Supports safer device validation.

# 🧪 Testing Summary

Test Area

Result

Notes

Functional Testing

Pass

Validate device management, media playback, external decryption, and single file restore workflows.

Regression Testing

Pass

Confirm existing sync, backup, restore, export, and login workflows still work correctly.

Sync Testing

Pass

Confirm sync work on device without external storage.

Backup Testing

Pass

Confirm backup behavior after missing file restore.

Performance Testing

Pass

Validate progress bar behavior during long-running operations.

# ⚠️ Known Issues

Issue

Impact

Workaround

Power-on Device Recognition

Known issue: connecting a device while it is powered off is not automatically recognized when the device is powered on.

Planned fix in the next release.

# 📦 Deployment Information

Item

Details

Installer Version

v1.0.116

Database Changes

Yes

Config Changes

Yes

Backup Required Before Update

Recommended

Rollback Available

To be confirmed before deployment

# 🔄 Upgrade / Migration Notes

Recommend backing up existing application database in "\AppData\Local\bdma" before installing this release.

# 📊 Operational Notes

Nothing special.

# 📌 Rollback Notes

Scenario

Rollback Action

Deployment issue after upgrade

Stop the application, rerun the installer.

Database/config migration issue

Restore the pre-upgrade database, then reinstall the previous stable version.

# 🔗 Related Documents

[https://quyendt3k4.atlassian.net/wiki/spaces/DVID/folder/25952272?atlOrigin=eyJpIjoiMzE2MGZjMzNhM2JhNGQ0ZDk3MjE3ZmE2NGRmYjY2NmQiLCJwIjoiYyJ9](https://quyendt3k4.atlassian.net/wiki/spaces/DVID/folder/25952272?atlOrigin=eyJpIjoiMzE2MGZjMzNhM2JhNGQ0ZDk3MjE3ZmE2NGRmYjY2NmQiLCJwIjoiYyJ9)

[https://quyendt3k4.atlassian.net/wiki/spaces/DVID/pages/26411028](https://quyendt3k4.atlassian.net/wiki/spaces/DVID/pages/26411028)

[https://ducviet.atlassian.net/wiki/x/AYBVAg](https://ducviet.atlassian.net/wiki/x/AYBVAg)