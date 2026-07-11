# Sprint 0 POC Test Plan

**Purpose:** Verify Camera/FGS/Storage behavior on real BodyCamera hardware before implementing Sprint 1+.

**Date:** 2026-07-10  
**Owner:** Dev team  
**Blocking:** Sprint 0 completion, ADR-002/004/005

---

## Prerequisites

- [ ] Physical BodyCamera device(s) - list models below
- [ ] ADB access to device
- [ ] Test APK built with debug logging enabled
- [ ] Spreadsheet/doc for recording results

**Target devices:**
- Model: _______________
- Android version: _______________
- Manufacturer: _______________
- Firmware: _______________

---

## Test 1: Camera API Behavior

**Goal:** Verify CameraX recording survives Activity/process lifecycle events.

| Test | Steps | Expected | Actual | Pass/Fail |
|---|---|---|---|---|
| **1.1 Basic recording** | Start video → record 30s → stop | File created, playable, correct duration | | |
| **1.2 Screen off** | Start video → press power to turn screen off → wait 30s → screen on → stop | Recording continues, file complete | | |
| **1.3 Activity recreate** | Start video → rotate device (config change) → stop | Recording continues, no duplicate file | | |
| **1.4 Process kill** | Start video → `adb shell am kill <package>` → reopen app | File exists (partial or complete?), no corruption | | |
| **1.5 Multiple recordings** | Record video 1 → stop → record video 2 → stop | Both files exist, camera released properly | | |
| **1.6 Callback timing** | Log `onStart` and `onFinalize` timestamps | Finalize happens after stop, file closed | | |

**Alternative: Camera2 API**
- If CameraX fails any critical test, repeat with Camera2 API
- Document which API works better for this device

**Deliverable:** Fill table above, copy logcat output, note any crashes/ANRs.

---

## Test 2: Foreground Service Behavior

**Goal:** Verify FGS keeps recording alive when Activity dies.

| Test | Steps | Expected | Actual | Pass/Fail |
|---|---|---|---|---|
| **2.1 FGS notification** | Start recording → pull down notification shade | Persistent notification visible, tapping returns to app | | |
| **2.2 Activity finish** | Start recording → press Home → force-stop Activity (not service) | Recording continues, service alive | | |
| **2.3 Process kill** | Start recording → `adb shell am kill <package>` | Service dies, recording stops. Check restart policy. | | |
| **2.4 Service restart** | Kill service → does it restart? | Depends on `START_STICKY` vs `START_NOT_STICKY` | | |
| **2.5 Recording ownership** | Start recording in Activity → finish Activity → where is active recording state? | If Activity-owned: lost. If Service-owned: survives. | | |

**Key question:** Can FGS own CameraX recording lifecycle, or must Activity stay alive?

**Deliverable:** Document restart behavior, decide FGS ownership model.

---

## Test 3: Storage Roots and ADB Visibility

**Goal:** Find physical paths for Internal/External storage, verify BDMA can read via ADB.

### 3.1 Internal Storage Path Discovery

```bash
# On device, after recording video to "Internal" mode:
adb shell
cd /sdcard/DCIM/DCAM  # or wherever app writes
ls -la
exit

# From host:
adb pull /sdcard/DCIM/DCAM ./test-pull
```

**Record:**
- Physical path: _______________
- ADB readable? Yes/No
- Requires `adb root`? Yes/No

### 3.2 External Storage Path Discovery

Insert SD card or USB storage.

```bash
adb shell
cd /storage/XXXX-XXXX/DCIM/DCAM  # SD card path varies
ls -la
exit

adb pull /storage/XXXX-XXXX/DCIM/DCAM ./test-pull
```

**Record:**
- Physical path: _______________
- ADB readable? Yes/No
- Auto-detection: can app find external storage programmatically?

### 3.3 File Operations

| Test | Steps | Result |
|---|---|---|
| **3.3.1 Move atomicity** | Write to Temp → move to Media | Atomic or copy+delete? |
| **3.3.2 Copy+fsync** | Write to Temp → copy to Media → fsync | Any failures? |
| **3.3.3 Free space** | Check reported free space vs `df -h` | Accurate? |
| **3.3.4 Storage removed** | Start recording to SD card → eject card | App crash or safe stop? |

**Deliverable:** Document physical paths, atomicity behavior, choose finalization strategy.

---

## Test 4: Screen/Power Policy

**Goal:** Understand screen-off/dim/WakeLock behavior.

| Test | Steps | Expected | Actual |
|---|---|---|---|
| **4.1 Screen timeout** | Start recording → wait for screen dim | Screen dims, recording continues | |
| **4.2 Power button** | Start recording → press power | Screen off, recording continues | |
| **4.3 Battery optimization** | Check Settings → Apps → DCAM → Battery | Is app optimized? Does it affect recording? | |

**Deliverable:** Note any WakeLock/foreground-service exemptions needed.

---

## Test 5: Device Owner / Lock Task (if factory allows)

**Goal:** Verify kiosk mode can be enabled on this hardware.

**Prerequisites:**
- Device must support Device Owner provisioning
- Device must be factory reset or un-provisioned

```bash
# Provision as Device Owner:
adb shell dpm set-device-owner com.dvid.dcam/.DcamDeviceAdminReceiver

# Enable Lock Task:
adb shell appops set com.dvid.dcam SYSTEM_ALERT_WINDOW allow
```

| Test | Steps | Result |
|---|---|---|
| **5.1 Device Owner** | Provision as Device Owner | Success / Fail / Not supported |
| **5.2 Lock Task Mode** | Start Lock Task, try Home button | Home blocked? |
| **5.3 User Restrictions** | Set `DISALLOW_FACTORY_RESET` etc | Applied? |

**Deliverable:** Document Device Owner support, decide kiosk strategy for MVP.

---

## POC Report Template

```markdown
# DCAM Hardware POC Report

**Date:** YYYY-MM-DD
**Device:** [Model, Android version, firmware]
**Tester:** [Name]

## Summary
- CameraX: ✅ Works / ⚠️ Issues / ❌ Fails
- FGS ownership: ✅ Possible / ❌ Activity-only
- Storage ADB: ✅ Readable / ❌ Not readable
- Device Owner: ✅ Supported / ❌ Not supported

## Camera API (ADR-002)
[Results from Test 1 table]

**Decision:** Use CameraX / Camera2 / Vendor SDK because...

## FGS Ownership (ADR-004)
[Results from Test 2]

**Decision:** FGS can/cannot own recording. Restart policy: START_STICKY / START_NOT_STICKY.

## Storage (ADR-005)
- Internal physical path: `/sdcard/Android/data/com.dvid.dcam/files/DCIM/DCAM`
- External physical path: `/storage/XXXX-XXXX/DCIM/DCAM`
- ADB readable: Yes/No
- Finalization strategy: Move (atomic) / Copy+delete

**Decision:** Use Internal / External / Auto. Finalization uses...

## Blockers / Issues
[Any critical failures]

## Next Steps
- [ ] Write ADR-002 (Camera)
- [ ] Write ADR-004 (FGS)
- [ ] Write ADR-005 (Storage)
- [ ] Update delivery plan with device-specific notes
```

---

## Success Criteria

POC is complete when:
- ✅ All tests run on at least one target BodyCamera device
- ✅ Results documented in POC report
- ✅ Critical failures identified (if any)
- ✅ Team has evidence to write ADR-002, ADR-004, ADR-005
- ✅ Delivery plan updated with device-specific constraints

**Timeline:** 2-3 days (1 day setup/testing, 1 day analysis/ADR drafting, 0.5 day review).
