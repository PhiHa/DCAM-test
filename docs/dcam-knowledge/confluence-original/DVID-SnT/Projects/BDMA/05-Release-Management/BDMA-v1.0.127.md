# BDMA v1.0.127

**Page ID**: 41746454  
**Version**: 4  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/41746454

---


# Release Information

Item

Details

Product

BDMA

Version

v1.0.127

Release Date

27 Jun 2026

Release Type

Minor / Feature Release

Prepared By

[Dinh Nhan](https://ducviet.atlassian.net/wiki/people/70121:52166504-9fa8-44ba-969f-16f03f3dc4d6?ref=confluence) 

Approved By

Pending

Document Version

1.2

# ⚠️ Update Check Compatibility

**BDMA v1.0.127 and later can check for application updates after the release repository became private.**

Versions older than v1.0.127 cannot check for updates from the private repository. Users on an older version must install v1.0.127 or a later version manually before in-app update checks will work again.

# 🎯 Release Overview

This release focuses on modernizing the application interface, and making installation and maintenance workflows safer and easier for end users.

It introduces a renewed BDMA UI, improves installer behavior for install, update, and uninstall actions, and adds support for silent install scenarios.

The release also improves release distribution readiness by supporting fixed installer naming and automated publish flow alignment.

# 🚀 New Features

Feature

Description

Notes

Silent Install Support

Added silent install capability for BDMA deployment scenarios.

Useful for controlled rollout, scripted deployment, and administrator-managed installation.

Renewed Application UI

Refreshed the application interface to improve visual consistency and usability.

Based on UI refactor work in [BDMA-151](https://ducviet.atlassian.net/browse/BDMA-151).

# ⚡ Improvements

Area

Improvement

Impact

Installer Flow

Improved installer behavior so app-triggered update installs can skip unnecessary action-selection steps and proceed more directly.

Reduces friction and confusion during update execution.

App Update Flow

Improved update checking and installer launch flow to better support private distribution and reduce repeated update actions.

Makes update handling more reliable for production use.

App Uninstall Flow

Improved uninstall behavior to better close related processes and reduce visible command window flashing.

Provides a cleaner and more predictable uninstall experience.

Installer File Handling

Improved installer compatibility when the installer executable is renamed, including fixed-name distribution scenarios such as `BDMA.exe`.

Supports stable download URLs and flexible distribution naming.

UI Consistency

Updated button styling, layout behavior, spacing, checkbox rendering, and dark mode header separation.

Creates a more polished and consistent user experience across themes.

Release Distribution

Improved release pipeline readiness for publishing the latest installer to fixed URLs.

Helps simplify release delivery and reduce manual upload work.

# 🐞 Bug Fixes

Issue

Description

Status

[BDMA-154](https://ducviet.atlassian.net/browse/BDMA-154)

Fixed multiple update and uninstall flow issues, including update delivery from private repository flow, installer launch robustness, and uninstall cleanup behavior.

Fixed

[BDMA-156](https://ducviet.atlassian.net/browse/BDMA-156) Installer false-positive running app detection

Fixed installer failure when the installer executable is renamed to `BDMA.exe` the installer could detect itself as a running BDMA application.

Fixed

# 🔐 Security Updates

Item

Description

Notes

Controlled Update Access

Improved authenticated update access flow for private release distribution.

Supports more secure update retrieval in restricted repositories.

Installer Safety

Improved installer and uninstall handling around process termination and file access conditions.

Helps reduce failure cases during update and removal operations.

# 🧪 Testing Summary

Test Area

Result

Notes

Functional Testing

Pass

Validated external decrypt flow, renewed UI behavior, installer actions, and uninstall flow.

Regression Testing

Pass

Confirmed core sync, backup, restore, media view, and export workflows continue to work.

Sync Testing

Pass

No major sync regression observed during release validation.

Backup Testing

Pass

Backup-related workflows remain stable after installer and UI changes.

Performance Testing

Pass

No critical performance regression observed in updated flows.

# 📦 Deployment Information

Item

Details

Installer Version

v1.0.127

Database Changes

No confirmed database change in this release

Config Changes

No change

Backup Required Before Update

Recommended

Rollback Available

To be confirmed before deployment

# 🔄 Upgrade / Migration Notes

Recommend backing up the existing local BDMA data before applying the update.

Validate update flow behavior on representative client environments, especially where endpoint protection or restricted permissions are enabled.

For managed deployment scenarios, verify silent install parameters and post-install application launch expectations before wide rollout.

# 📊 Operational Notes

Fixed-name installer distribution is supported more safely in this release.

Automated release publishing flow to Cloudflare R2 is aligned with the goal of providing stable download URLs for production and development channels.

# 📌 Rollback Notes

Scenario

Rollback Action

Installer or update issue after upgrade

Reinstall the previous stable BDMA version and validate application startup and data access.

Deployment issue on managed environment

Stop rollout, restore the previously approved installer package, and repeat deployment using the last verified process.

# 🔗 Related Documents

[Installer false-positive app running detection](/wiki/spaces/DVID/pages/41615363/Installer+false-positive+app+running+detection)

[BDMA-151](https://ducviet.atlassian.net/browse/BDMA-151) — Refactor UI

[BDMA-154](https://ducviet.atlassian.net/browse/BDMA-154) — Improve the app update and uninstall flow

[BDMA-155](https://ducviet.atlassian.net/browse/BDMA-155) — [CI] Improve Build & Publish to Cloudflare R2

[BDMA-156](https://ducviet.atlassian.net/browse/BDMA-156) — Installation fails when installer is renamed to BDMA.exe