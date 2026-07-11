# BDMA v1.0.102

**Page ID**: 34471938  
**Version**: 4  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/34471938

---


## Release Information

Item

Details

Product

BDMA

Version

v1.0.102

Release Date

2026-06-08

Release Type

Major

Prepared By

[Dinh Nhan](https://ducviet.atlassian.net/wiki/people/70121:52166504-9fa8-44ba-969f-16f03f3dc4d6?ref=confluence) 

Approved By

[Hoàng Ngọc Quyền](https://ducviet.atlassian.net/wiki/people/712020:e466d315-3c2d-406d-b324-8f8eb35ccb48?ref=confluence) 

# 🎯 Release Overview

BDMA is designed to help administrators safely collect, manage, protect, and retrieve media records from body cameras while keeping local data organized, recoverable, and maintainable through controlled offline patch delivery when needed.
This first release focuses on the complete core workflow:

Manage users and approved body camera devices.

Sync media from body cameras.

Back up synced media safely.

Restore media from backup when needed.

View and export media, including encrypted camera records.

Offline Patch Delivery / Developer Mode for cases where client environments are offline, restricted, or difficult to update remotely.

# 🚀 New Features

Feature

Description

Notes

User Management

Added user management for administrator workflows.

User validation is required to view synced media.

Whitelisted Device Management

Added whitelist-based device handling and device validation for administrator workflows.

Added support for showing device lists and sync saved devices without requiring login.

Whitelist rules can be added/modified in dev mode.

Media Synchronization from Body Camera

Added media synchronization from body cameras with automatic and manual sync workflows.

Auto decrypt encrypted media from camera.

Devices must pass whitelist filtering and be approved by an administrator before they can be synced.

Media Backup and Restore

Added backup workflow for synced media.

Added restore media from backup for administrator workflows.

Backup automatically run silently in the background when no sync is active.

Sync and Backup folder are protected from normal access.

Media View and Export

Added media list and dashboard views.

Added media viewer for viewing synced files.

Added export media functionality.

Login is required to view media list and user can only see their own media.

Monitor and Maintenance

Added local logging and remote log collection through Loggly.

Added Developer mode for controlled client-side database update.

OTP from DVID ITs is required to access Developer mode.

# 🔐 Security Updates

Item

Description

Notes

Security and Data Protection

Added AES-256 local SQLite database encryption.

Added AES-256 file decryption module for encrypted camera records.

Added safeguards against data corruption using .tmp files and SHA-256 checks.

Protect sync and backup folders with restricted permission.

Added TOTP to protect developer mode.

|  

# 🧪 Testing Summary

Test Area

Result

Notes

Functional Testing

Pass

|  
Regression Testing

Pass

|  
Sync Testing

Pass

|  
Backup Testing

Pass

|  
Performance Testing

Pass

|  

# ⚠️ Known Issues

Issue

Impact

Workaround

SOS and MP3 files cannot be viewed in the current version.

Both administrator and normal user accounts.

Support for viewing these file types is planned for a future release.

When users check for updates and the application is already on the latest version, no UI confirmation is displayed. The status is only written to the log.

Users may think the update check did not work, even though the application is already up to date.

No action is required. Users can verify the current version in the application or contact an administrator. A UI confirmation message will be added in a future release.

# 📦 Deployment Information

Item

Details

Installer Version

1.0.102

Database Changes

No

Config Changes

No

Backup Required Before Update

No

Rollback Available

No

# 🔄 Upgrade / Migration Notes

Existing database from develop phase need to be deleted.

# 📊 Operational Notes

Devices must be whitelisted and saved by an administrator before they can be synced.

Media synchronization requires the body camera to be properly connected and detected by the application.

Backup and restore operations depend on the configured backup folder in admin setting.

Offline patches can be delivered through developer mode for restricted or offline client environments.

# 📌 Rollback Notes

This is the first official release of BDMA, so rollback to an earlier public version is not applicable.

# 🔗 Related Documents

[https://quyendt3k4.atlassian.net/wiki/x/hYCBAQ](https://quyendt3k4.atlassian.net/wiki/x/hYCBAQ) 

[https://quyendt3k4.atlassian.net/wiki/spaces/DVID/folder/25952272?atlOrigin=eyJpIjoiMzE2MGZjMzNhM2JhNGQ0ZDk3MjE3ZmE2NGRmYjY2NmQiLCJwIjoiYyJ9](https://quyendt3k4.atlassian.net/wiki/spaces/DVID/folder/25952272?atlOrigin=eyJpIjoiMzE2MGZjMzNhM2JhNGQ0ZDk3MjE3ZmE2NGRmYjY2NmQiLCJwIjoiYyJ9) 

[https://quyendt3k4.atlassian.net/wiki/spaces/DVID/pages/26411028](https://quyendt3k4.atlassian.net/wiki/spaces/DVID/pages/26411028)