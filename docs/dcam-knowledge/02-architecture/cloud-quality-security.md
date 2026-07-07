# Cloud, configuration, update, quality, and security

## Cloud rule

Cloud is an optional extension. Firebase can be an initial provider but must not be called from UI, ViewModel, use cases, or core business logic.

Provider-neutral interfaces may support remote config, crash reporting, device state, metrics, and HTTPS backend functions. Implementations may be Firebase SDK, REST/custom backend, BDMA/Desktop-side integration, or local-only fallback.

| Environment | Expected behavior |
|---|---|
| GMS + Internet | Configured SDK or API providers may run |
| non-GMS + Internet | REST/API/custom provider or local fallback |
| Offline | Local/default config and local logs; core capture remains functional |
| Desktop-side cloud only | Android stays local; BDMA/backend uploads imported data |
| Fully local | Capture/storage/metadata/logging/BDMA ingest still work |

Cloud work must never block or destabilize recording/capture.

## Configuration precedence

```text
runtime override
→ remote/cloud config
→ local config
→ safe built-in defaults
```

Potential settings include feature flags, timeouts, retries, camera parameters, logging level, and update policy. Important changes should be validated, versioned, and logged. No remote value may make the core recording/capture path unsafe.

## Update direction

Supported deployment directions are Play Store, self-update, manual APK, and controlled auto-update because BodyCamera environments differ. Server location, signature validation, force/silent policy, rollback, and OEM privileges remain TBD. Offline/manual deployment must remain viable.

## Logging and diagnostics

Expected local-first categories: application, recording, capture, camera, GPS, storage, update, crash, and security-relevant error events.

Minimum useful diagnostic context includes app version, device model, Android version, storage/battery, and relevant recent operation results. GMS/provider mode and GPS availability are useful additions.

Rules:

- Timestamp important events and include safe device/session/file context.
- Log start/result/error around critical hardware calls.
- Never log credentials, tokens, passwords, sensitive content, or unnecessary private metadata.
- Rotate and bound logs.
- Local crash/error logs remain available when cloud reporting is absent.
- Cloud/REST/BDMA upload is asynchronous and optional.

## Performance and reliability

- Do not block the UI thread with camera, file, database, or network work.
- Give recording priority over logging, upload, metrics, or background tasks.
- Measure startup, recording latency, capture latency, storage latency, memory, battery, crashes, and ANRs on actual BodyCamera hardware.
- Continue local operation through network/cloud timeout.
- GPS unavailable must not abort capture.
- Near-full storage, app backgrounding, crash during recording, and power interruption need explicit policies and tests.
- Long-running recording likely needs a Foreground Service; final lifecycle/recovery design is pending.

## Security baseline

- Do not hardcode or log secrets.
- Request only needed permissions and handle denial safely.
- Protect sensitive media/metadata as required.
- Authenticate and authorize cloud/provider access.
- Validate application updates before installation.
- Keep local logs free of sensitive content.

Data Contract 1.2 now decides that enabled media encryption is AES-256 and uses `_enc` or `_IMP_enc` filename suffixes. Cipher/mode details, keys, rotation, exact media/metadata scope, integrity interaction, and BDMA decryption behavior still require the dedicated Security & Encryption Design.

## Local build-property boundary

Gradle currently exposes keys loaded from `application.properties` and gitignored `application-local.properties` through `BuildConfig`. Private local-property workflow directives are kept in the local file rather than public build configuration. See [current repository state](../05-current-repo/current-state.md).
