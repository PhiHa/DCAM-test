# Android device operation requirements

Source status: **Approved 1.0**, Confluence page version 1, created 2026-07-07. This is a local implementation-oriented digest; the [Confluence requirement](https://ducviet.atlassian.net/wiki/spaces/DVID/pages/48496661) remains authoritative.

## Approved operating model

DCAM is a dedicated BodyCamera application. When device policy permits, Android boots into or starts DCAM, DCAM enters dedicated operation, the operator uses capture/settings/device functions, required services keep local data ready, and BDMA later imports according to the Data Contract.

| Area | Requirement |
|---|---|
| Dedicated screen | Main operation uses the primary screen for the dedicated-device experience while preserving required Android system indicators. |
| Home/launcher | DCAM may be configured as the Home/Launcher app on dedicated devices. |
| Boot startup | DCAM can start after `BOOT_COMPLETED` when device policy and permissions allow it; manual startup remains available for maintenance/fallback. |
| Exit control | Dedicated operation should prevent accidental exit; exact kiosk/lock-task mechanism depends on device policy. |
| Foreground operation | Recording, location, storage monitoring, and device work use Android foreground/background mechanisms appropriate to their lifetime. |
| Lifecycle | Handle start, pause, resume, stop, restart, boot complete, screen on/off, and device reconnect. |
| Permissions | Camera, microphone, location, storage/media, and notification denial/unavailability must not crash the app. |
| Power | Account for battery optimization, screen/wake behavior, and Android background-execution limits. |
| Recovery | Define behavior after app crash, service kill, reboot, or abnormal power loss. |

## Implementation boundary

The requirement intentionally does not prescribe the exact Activity flags, BroadcastReceiver, Foreground Service ownership, Device Owner provisioning, Lock Task/Kiosk mode, WakeLock, or battery-optimization exemptions. Those choices require Technical Design and real-device policy validation.

Current source intentionally keeps Android status and navigation bars visible so operators can see device indicators such as battery, Wi-Fi/network state, GPS/location status, notifications, and navigation controls. Runtime permission handling, lifecycle-bound CameraX, and a recording foreground notification/service exist. Boot startup, Home role, managed kiosk/exit control, screen/power policy, durable recording ownership, exact dedicated-screen behavior, and crash/reboot recovery remain open.
