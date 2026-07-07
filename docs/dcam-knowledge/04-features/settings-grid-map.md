# Settings grid design map

Status: 12-category navigation and detail-list prototype. Controls and persistence come later. Files is already a read-only media explorer.

The settings home behaves like an app launcher: a three-column grid containing only an icon and category label, without recording/device status or a page heading. Opening a category shows a scrollable title, description, and list of non-sensitive setting names; system Back returns to the grid. Endpoint values, usernames, passwords, passcodes, and secret keys are deliberately absent.

| Grid item | Detail-list contents | OEM/CSON source areas for later implementation |
|---|---|---|
| Files | Browse `Video`, `IMP`, `Image`, and `Audio`; open media in an Android viewer | Contract `Media` folders only |
| Recording | Recording, segment length, pre-record, delay, boot/loop and battery warning | OEM `recording`; CSON `[video] file.*` |
| Cameras | Rear/front preview and photo sizes, rotation, IR and flashlight | OEM rear/front camera; CSON `[camera]` |
| Video streams | Default stream plus main/sub codec, resolution, FPS and quality | OEM main/sub stream; CSON `[video] main.*`, `sub.*` |
| Audio | File format, recording sound, encoding and volume | OEM preferred audio format/disable sound; CSON `[audio]`, `[video] file.audio_record_format` |
| Storage | Target, low-space warning, recycle and post-upload deletion | OEM recording storage/loop; CSON `[video] file.save_storage`, `file.recycle`, `file.low_capacity_*` |
| GPS | Enablement, GPS/AGPS mode and update/upload frequency | OEM `gps`; CSON `[location]` |
| Device | Device/operator ID, display controls, fall detection, lights and language | OEM identity/display/hardware/localization; CSON `[device]`, `[common]` |
| Security | Menu/USB protection and transmission encryption | OEM security/transmission security; CSON `[common] enter_menu.*`, `usb_storage.*`, `transport.*` |
| Network | Backend address/port and APN connection fields | OEM APN/device identity; CSON `[backend]`, `[apn]` |
| Transfer | RTSP viewing and FTP upload settings | OEM RTSP/FTP; CSON `[stream]`, `[ftp]` |
| About | Device/app version, firmware/OTA, capabilities, diagnostics | CSON `[device]`, `[backend]`, capability detection and local diagnostics |

## Later implementation rules

- Treat CSON options and OEM-exposed capabilities as device-dependent; do not assume every model supports every field.
- Load current values through application-owned configuration ports, not directly in Activity or View code.
- Mask sensitive fields and require deliberate reveal/edit flows.
- Validate values against the device-provided option lists before writing.
- Keep recording/capture available if optional configuration cannot be read.
- Log important setting changes without logging passwords, passcodes, tokens, or secret material.
- Data Contract 1.2 is approved: settings must honor Internal/External/Auto media selection, MP4-only MD5, AES-256 enablement, and cleanup rules. Store operational settings in `dcam.db`, not device-only `dcam_config.cson`; detailed UI/schema design remains pending.
