# Scope, roadmap, and release intent

## MVP definition

The first MVP validates the complete local capture-to-desktop path:

```text
record or capture
→ persist media locally
→ create required metadata/status
→ expose data in an ADB-readable form
→ BDMA discovers, imports, maps, and displays it
```

Required MVP capabilities:

- Start/stop video recording and expose recording state.
- Capture and save images.
- Store media in a documented local structure.
- Generate metadata needed by BDMA, including file ID, device ID, timestamp, optional valid GPS, status, and other agreed contract fields.
- Report basic battery, storage, and GPS availability.
- Log recording, capture, storage, and error flows.
- Allow BDMA to read folders/data and map media to metadata.
- Remain stable when GPS is unavailable or storage is near full.

MVP deliverables include the Android build and source, folder and metadata specifications, installation guide, test report, and release notes.

## Nine-month roadmap

| Stage | Timing | Goal | Main output |
|---|---|---|---|
| Android training | Weeks 1–2 | Move the Java/Desktop team to Android/BodyCamera readiness | Camera training prototype |
| Phase 1: MVP Foundation | Weeks 3–12 / Months 1–3 | Recording, capture, storage, metadata, logging, device status | MVP Internal Build 0.1 |
| Phase 2: Platform Foundation & BDMA Integration | Weeks 13–22 / Months 4–5 | Data contract, BDMA E2E, recovery, GPS per file, basic encryption, Device/User foundations | Secure Platform MVP 0.2 |
| Phase 3: Advanced Communication | Weeks 23–32 / Months 6–7 | Streaming, PTT, GPS route, advanced encryption and network failure handling | Advanced Communication Beta 0.3 |
| Phase 4: Hardening & Customer Pilot | Weeks 33–40 / Months 8–9 | Regression, stability, performance, security, release preparation | Customer Pilot / Production Candidate |

The plan assumes 18 two-week sprints, though the week ranges extend to week 40. Phase 1 and Phase 2 explicitly include 20% buffer. Buffer is for delivery risk, not new scope.

## Milestones

| Milestone | Target | Success condition |
|---|---|---|
| M0 Training complete | End week 2 | Team can build, run, debug, record, and capture on BodyCamera |
| M1 Camera prototype | End week 6 | Video and image capture work on real hardware |
| M2 MVP 0.1 | End week 12 | Files, metadata, and logs are generated reliably |
| M3 DCAM–BDMA demo | End week 16 | BDMA imports and displays DCAM data |
| M4 Secure Platform MVP 0.2 | End week 22 | Integration, recovery/GPS/basic encryption, Device/User foundation |
| M5 Advanced Communication Beta 0.3 | End week 32 | Streaming/PTT beta, GPS route v1, advanced encryption scope |
| M6 Release candidate | End week 39 | Critical bugs fixed and pilot documentation prepared |
| M7 Customer pilot | End week 40 | Pilot build and release/test documents available |

## Explicit exclusions from the initial MVP

- Remote Device Management and Advanced User Management (Phase 2 foundations).
- Live Streaming and Push-to-Talk (Phase 3).
- Advanced Encryption and Full GPS Tracking Route (Phase 3; basic encryption may begin in Phase 2).
- Cloud upload, OTA update, and AI analytics (future).

The wider Phase 2 project also excludes a cloud video management platform, facial recognition, ANPR, third-party VMS integration, a public mobile application, and a separate web portal.

## Exit and approval

The MVP is complete only when recording, capture, metadata, and BDMA tests pass, there is no critical bug, and both PM and QA approve it. The final nine-month target additionally requires no critical pilot blocker and the release notes, installation guide, test report, and known-issues list.

## Planning interpretation

- The roadmap describes **what and when**.
- The development plan describes **how**, including onboarding, sprint allocation, integration, buffer, risk, and release preparation.
- Advanced capability dates are roadmap intent, not permission to weaken the capture/storage/BDMA baseline.
- The planning pages are Draft and should be checked before using dates as external commitments.
