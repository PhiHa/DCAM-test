# DCAM Sprint 0 Summary

**Created:** 2026-07-10  
**Last refreshed against Confluence:** 2026-07-13  
**Purpose:** Quick reference for Sprint 0 completion

---

## What is Sprint 0?

Validate hardware, SDK, storage, BDMA import, and offline-first assumptions before building. Prevents major rework when real device behaves differently than emulator.

Latest gap report: `docs/local-dev/evidence/confluence-refresh-gap-report-2026-07-13.md`.

---

## The Two Problems

### 1. "Startup Cloud" in AppComposition

**File:** `app/src/main/java/com/dvid/dcam/app/AppComposition.java` line ~109

**Code:**
```java
initializeCloudStateAsync(context, deviceInfo);
```

**What it does:**
- Spawns thread every app start
- Writes settings to Room DB
- Calls no-op remote config provider
- Runs even though cloud is disabled/optional

**Why wrong:**
- MVP is offline-first
- No-op provider does nothing useful
- Violates "no hidden behavior for disabled features"
- Device identity belongs to runtime, not cloud

**Fix:** Remove call, defer identity init to Sprint 1 runtime orchestrator.

---

### 2. No Hardware POC Yet

**What's missing:** Testing on physical BodyCamera device

**Critical unknowns:**
- Does CameraX work on this hardware?
- Does recording survive screen-off?
- Does recording survive process kill?
- Can FGS own recording or must Activity stay alive?
- What are physical storage paths?
- Can BDMA read via ADB?

**Why critical:** Emulator behavior â‰  real device. Samsung/Xiaomi/vendor firmware all behave differently.

**How long:** 2-3 days to test + document.

---

## Sprint 0 Deliverables

### 1. Hardware POC (docs/local-dev/plans/sprint-0-poc-test-plan.md)
Run 5 test groups on real device, document results.

### 2. Write 8 ADRs (docs/local-dev/adr/)
- ADR-001: Architecture confirmation
- ADR-002: Camera API (CameraX vs Camera2) âš ï¸
- ADR-003: Room + BDMA concurrency
- ADR-004: FGS ownership âš ï¸
- ADR-005: Storage roots + finalization âš ï¸
- ADR-006: Force encryption OFF âš ï¸
- ADR-007: Auth + emergency policy
- ADR-008: BDMA write-back protocol

âš ï¸ = Blocking next sprint

### 3. Code Cleanup
- Remove startup cloud init
- Force encryption OFF
- Hide non-MVP surfaces
- Architecture tests pass

---

## Exit Criteria

âœ… POC executed on â‰¥1 real device  
âœ… POC report written  
âœ… 8 ADRs written + reviewed  
âœ… Code cleanup complete  
âœ… Team aligned on Camera/FGS/Storage

**Then:** Proceed to Sprint 1.

---

## Key Files Created

| File | Purpose |
|---|---|
| `mvp-readiness-report.md` | Full project state analysis |
| `sprint-0-poc-test-plan.md` | Hardware testing procedures |
| `sprint-0-action-checklist.md` | Step-by-step Sprint 0 tasks |
| `sprint-0-summary.md` | This file - quick reference |

---

## Next Steps

1. **Today:** Review documents with team
2. **This week:** Acquire BodyCamera device, run POC
3. **Next week:** Write ADRs, code cleanup
4. **Week after:** Sprint 1 continues with evidence-based decisions

---

## Questions?

- **"Can we skip POC?"** No. Emulator â‰  real device. High rework risk.
- **"Can we write ADRs without POC?"** No. ADRs need evidence.
- **"What if POC finds CameraX doesn't work?"** Switch to Camera2. Better to know now.
- **"Timeline impact?"** 3 days now saves weeks of rework later.

---

**Bottom line:** Sprint 0 = validate assumptions before building. Low cost, high value.


