# Build 0.1 audio device tests - 2026-07-14

## Target

- ADB serial: `BODYCAMERA4HHITK`
- Model: `Android BodyCamera`
- Android: `12` / API `31`
- App: `com.dvid.dcam`, version `1.0`, version code `1`
- Removable volume: `6162-6433`
- Audio hardware command: `KEYCODE_F3` (`133`)

## Results

| Test | Result | Evidence |
|---|---|---|
| Internal normal finalization | PASS | `DCAM_KF5KW2124062200167_000000_20260714_155423.aac` staged under internal `Temp`, foreground service reported `isForeground=true`, then finalized under internal `Media/Audio` at `49,600` bytes; internal `Temp` was empty. |
| AUTO removable normal finalization | PASS | `DCAM_KF5KW2124062200167_000000_20260714_155304.aac` and `DCAM_KF5KW2124062200167_000000_20260714_155337.aac` finalized under `/storage/6162-6433/.../Media/Audio`; removable `Temp` was empty. |
| Screen-off continuation | PASS | `DCAM_KF5KW2124062200167_000000_20260714_155505.aac` grew from `31,000` to `92,225` bytes during ten seconds screen-off; `RecordingForegroundService` remained foreground. |
| Configuration rotation continuation | PASS | Same staged AAC grew from `92,225` to `156,550` bytes while rotating through `user_rotation=1` and back to `0`, then finalized at `157,325` bytes; removable `Temp` was empty. |
| Force-stop recovery | PASS | `DCAM_KF5KW2124062200167_000000_20260714_155635.aac` remained staged at `62,000` bytes after `am force-stop`, then relaunch reported `recovered=1, preserved=0, duplicates=0` and moved it to removable `Media/Audio`; `Temp` was empty. |
| Reboot recovery | PASS with vendor remount race | `DCAM_KF5KW2124062200167_000000_20260714_155707.aac` was staged at about `62,000` bytes before reboot. Boot exposed SD as `shared`, so initial recovery reported `recovered=0`. After disabling `com.bodycamera.nettysocket`, changing USB functions through `none` back to `adb`, and waiting for volume `6162-6433` to become `mounted`, same `62,000` byte AAC appeared under removable `Media/Audio`; `Temp` was empty. |
| Physical power-cut recovery | FAIL reproducibly (2/2) | Run 1: `..._160710.aac` measured `104,625` bytes while FGS was active, then mounted as `0` bytes after battery removal. Run 2: `..._162207.aac` grew from `91,450` to `141,050` bytes while FGS was active, then mounted as `0` bytes after battery removal. After repeat relaunch, recovery reported `recovered=0, preserved=2, duplicates=0`; neither file was published to `Media/Audio`. |

## Observations

- Audio capture used `Temp` during active recording and published only after normal stop or recovery.
- Screen-off and Activity recreation did not stop `MediaRecorder`; staged file size continued increasing.
- Reboot repeats firmware USB mass-storage race already recorded for video. Evaluate recovery only after removable volume and MediaProvider settle.
- No pre-existing media was deleted or modified.
- Power-cut failure occurs below recovery publication and reproduced in two consecutive runs: active AAC bytes visible before each cut were not durable on SD after sudden power loss. Recovery correctly rejects and preserves both zero-byte artifacts.

## Remaining action

Fix active AAC durability during recording, then repeat same physical battery-cut procedure. Acceptance requires exact staged AAC to remain non-zero and playable after boot, recovery to report `recovered=1`, final file to exist under `Media/Audio`, and `Temp` to be empty.
## App-private staging retest

- Candidate installed at `2026-07-14 16:42:48` moved active AAC to `/data/user/0/com.dvid.dcam/files/DurableAudioTemp` and stored a synced `.target` marker for the AUTO SD final path.
- Before battery removal, `DCAM_KF5KW2124062200167_000000_20260714_164353.aac` grew from `296,050` to `345,650` bytes with `RecordingForegroundService` foreground; no new AAC appeared in removable `Temp`.
- After physical battery removal and boot, the private staged AAC remained non-zero at `558,775` bytes. Byte durability therefore passed for this storage location.
- Tested APK entered a repeated startup crash loop: `NoSuchMethodError` for `java.nio.file.Files.readString` from `DcamStorage.validatedMarkedTarget`.
- Compatibility fix replaced `Files.readString` with `new String(Files.readAllBytes(...), UTF_8)`. `gradlew test assembleDebug` passed.
- Reinstalled fixed APK recovered exact `558,775` byte artifact into AUTO SD `Media/Audio`; private staging became empty and log reported `recovered=1, preserved=2, duplicates=0`.
- Result: durability mechanism promising, tested power-cut run not end-to-end PASS because original post-boot APK crashed. Fresh physical cut on fixed APK remains required. Product approval is also required because app-private staging hides active AAC from external `Temp`.
