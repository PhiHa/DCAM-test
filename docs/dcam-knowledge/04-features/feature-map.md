# Feature map by product phase

## Phase 1 — MVP Foundation

### Capture

- Video start/stop and recording state.
- Image capture and save.
- Camera framework POC and target-device validation.
- Basic handling of permission and camera errors.

### Local data

- Local media storage.
- Deterministic structure and filenames once contracted.
- Metadata generation and validation.
- Temporary/final/source status direction.
- Local logging and basic diagnostics.

### Device awareness

- Battery, storage, and GPS availability.
- Capability checks and safe fallback.

### Deliverable

MVP Internal Build 0.1 plus demo, test checklist, and known issues.

## Phase 2 — Platform Foundation & BDMA Integration

### Data and desktop integration

- DCAM–BDMA Data Contract v1.
- ADB discovery/read/import E2E demo.
- Metadata mapping for media, device, time, GPS, and status.
- File recovery/pending/corrupt handling.
- GPS per media file.

### Device and user platform

- Device ID/model/firmware/app version/storage/battery/network context.
- Basic remote status/config read-write foundation.
- User profile, operator, role, and permission foundation.

### Security

- Basic media/metadata encryption scope after agreed design and device benchmark.

### Deliverable

Secure Platform MVP Build 0.2, E2E demo, Device/User demo, and integration report.

## Phase 3 — Advanced Communication

- Live Streaming beta/basic implementation.
- Push-to-Talk trigger, capture/transmission, state/error logging.
- Full GPS route v1 by session.
- Advanced encryption.
- Timeout, reconnect, weak-network, and interruption handling.

These are beta capabilities, not a promise of full production hardening.

## Phase 4 — Hardening and Customer Pilot

- Full regression of core flows.
- Long-duration BodyCamera stability tests.
- CPU, memory, battery, and storage profiling.
- Streaming/PTT weak-network tests.
- GPS-route availability/fallback tests.
- Security review and critical/high bug resolution.
- Release notes, installation guide, test report, known issues, and pilot notes.

## Future platform

- Cloud upload/sync.
- OTA and extended operations.
- Fleet management and advanced monitoring.
- Analytics and additional downstream integrations.

## Important prototype-vs-scope note

The current repository already contains standalone audio recording, SOS action/state, CSON configuration, Loggly upload, Room-backed log outbox, and concrete file naming/folders. These are implementation/prototype facts, not automatically approved feature requirements or final Data Contract choices.
