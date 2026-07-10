# BDMA v1.0.133

**Page ID**: 47710226  
**Version**: 3  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/47710226

---


# Release Information

Item

Details

Product

BDMA

Version

v1.0.133

Release Date

2026-07-04

Release Type

Minor / Feature Release

Document version

1.2

Prepared By

 

Approved By

Pending

# 🎯 Release Overview

This release adds two major media-review capabilities: a Location tab with map marker support and user-specific file bookmarks.

Users can inspect GPS information for media files, view available map context, bookmark important files from list and detail views, and filter the list to bookmarked files.

The initial feature scope includes resilient online/offline map behavior, per-user bookmark persistence, synchronized bookmark state across views, and bulk bookmark actions.

# 🚀 New Features

Feature

Description

Notes

Location Tab with Map Marker

Added a Location tab to Media Viewer showing GPS coordinates, file time, device information, and a map marker for the current media file. The first implementation includes session tile caching, network-state monitoring, automatic recovery when connectivity returns, zoom request throttling, missing or invalid GPS handling, and a bundled world-map fallback.

Tracked by [BDMA-136](https://ducviet.atlassian.net/browse/BDMA-136). Map loading, recovery, and offline behavior are baseline capabilities of this newly introduced feature.

User-specific File Bookmarks

Added bookmark and unbookmark actions in file list and media detail views, bookmarked-only filtering, bookmark timestamps, real-time state synchronization, language-change refresh, visual feedback, and bulk actions including Shift+Click workflows.

Tracked by [BDMA-153](https://ducviet.atlassian.net/browse/BDMA-153). Bookmark state is stored per user and file, remains isolated between users, persists across sessions, and does not modify source media content or metadata. These are baseline capabilities of the newly introduced Bookmark feature.

# ⚡ Improvements

No standalone improvements to existing features are called out in this release. Map and bookmark behavior is documented as baseline scope under New Features.

# 🐞 Bug Fixes

No bug fixes in this release.

# 🔐 Security Updates

No security-specific update was introduced in v1.0.133. User-scoped bookmark storage and unchanged source-media content are functional requirements of the new Bookmark feature and are documented under New Features.

# 🧪 Testing Summary

Test Area

Result

Notes

Release Build / Packaging

Pass

GitHub release and BDMA-1.0.133-Setup.exe artifact were published successfully.

Location Map Functional Testing

Pass

Validated Location tab, GPS details, marker placement, missing or invalid GPS states, map fallback, and connectivity recovery against [BDMA-136 acceptance criteria](https://ducviet.atlassian.net/browse/BDMA-136).

Bookmark Functional Testing

Pass

Validated bookmark and unbookmark, per-user isolation, persistence, list/detail synchronization, bookmarked-only filtering, and bulk actions against [BDMA-153 test cases](https://ducviet.atlassian.net/browse/BDMA-153).

Regression Testing

Pass

Validated core sync, backup, restore, media view, export, pagination, themes, and language switching.

Offline / Restricted Network Testing

Pass

Validated map fallback, session tile cache, unavailable-network behavior, and reconnect recovery.

# ⚠️ Known Issues / Limitations

Issue

Impact

Workaround

Detailed map tiles require access to the OpenStreetMap tile service.

Restricted or offline clients may not display detailed street tiles.

Allow outbound HTTPS access to `tile.openstreetmap.org:443`, or accept fallback-only map mode for offline sites.

Bookmark scope is file-level and user-specific.

Shared bookmarks, timestamp bookmarks, advanced tags, and bookmark-list export are not included.

Use the per-user file bookmark and bookmarked-only filter provided in this release.

# 📦 Deployment Information

Item

Details

Installer Version

v1.0.133 — BDMA-1.0.133-Setup.exe (166.01 MB)

Database Changes

Yes — Flyway migration V6 adds the file_bookmark table and indexes.

Config Changes

None

Backup Required Before Update

Recommended because this release applies a database schema migration.

Rollback Available

Yes — requires a pre-upgrade backup and the previous installer.

# 🔄 Upgrade / Migration Notes

Back up the current BDMA database and application data.

Install BDMA v1.0.133 using the approved installer.

Start BDMA and confirm Flyway migration V6 completes; no manual SQL action is expected.

Verify application startup, user login, existing media access, per-user bookmark persistence, bookmarked-only filtering, and the Media Viewer Location tab.

For restricted networks, verify access to `tile.openstreetmap.org:443` or confirm fallback-only map behavior is acceptable.

# 📊 Operational Notes

Detailed map tiles are fetched through a loopback session cache. Cache size varies with map usage, has no configured size cap, and is cleared when the application closes.

The map monitors upstream availability and attempts to recover automatically when network access returns.

Bookmark data is stored locally by user and file. The table has indexes for user, file, and bookmark state; no fixed bookmark-count limit is configured.

Bookmark operations do not modify source media. Monitor database and temporary-disk growth in unusually high-volume sessions.

# 📌 Rollback Notes

Scenario

Rollback Action

Application or installer issue after upgrade

Stop BDMA, reinstall the previous approved stable installer, then validate startup and existing media access.

Database migration or bookmark-data issue

Stop BDMA, restore the pre-upgrade database backup, reinstall the previous stable version, and validate sync, backup, restore, and media access before resuming use.

Map behavior is unsuitable in a restricted environment

Report the issue to development team.

# 🧠 PM / Engineering Notes

- 

# 🔗 Related Documents

[BDMA-136 — Add Location Tab with Map Marker in Media Viewer](https://ducviet.atlassian.net/browse/BDMA-136)

[BDMA-153 — Add Bookmark Feature for Files](https://ducviet.atlassian.net/browse/BDMA-153)

Bookmark Feature Overview

Previous Release — BDMA v1.0.127

[GitHub Release v1.0.133](https://github.com/DucVietTech/bdma/releases/tag/v1.0.133)

[Full Changelog v1.0.127...v1.0.133](https://github.com/DucVietTech/bdma/compare/v1.0.127...v1.0.133)