# Sprint 0 Action Checklist

**Purpose:** Close Sprint 0 gates before continuing Sprint 1 implementation.  
**Owner:** Dev team + PM  
**Due:** Before Sprint 1 completion  
**Status:** ⚠️ BLOCKING - Sprint 0 incomplete

---

## Critical Path Items (must complete in order)

### 1. Hardware POC (3 days)
- [ ] **Acquire physical BodyCamera device(s)**
  - Document: model, Android version, firmware
  - Ensure ADB access works
- [ ] **Run POC test suite** (see `sprint-0-poc-test-plan.md`)
  - Camera API: screen-off, process kill, lifecycle
  - FGS: ownership, restart, notification
  - Storage: physical paths, ADB visibility, atomicity
  - Power: WakeLock, battery optimization
  - Device Owner: kiosk provisioning (if supported)
- [ ] **Write POC report**
  - Fill template in test plan
  - Attach logcat outputs
  - Note any critical failures

**Blocking:** ADR-002, ADR-004, ADR-005

---

### 2. Write ADRs (2 days)

Based on POC evidence, write 8 ADR documents:

- [ ] **ADR-001: Architecture Confirmation**
  - Java/XML/ViewBinding/MVVM/Clean Architecture
  - Feature-first packages
  - Current `:app`/`:core` two-module shape
  - **Status:** Code already follows this; ADR formalizes it

- [ ] **ADR-002: Camera API Selection** ⚠️ BLOCKING SPRINT 2
  - CameraX vs Camera2 vs Vendor SDK
  - Decision based on POC Test 1 results
  - Document tradeoffs, lifecycle behavior
  - **Depends on:** Hardware POC

- [ ] **ADR-003: Database & Concurrency** ⚠️ BLOCKING DDL v1
  - Room vs raw SQLite
  - BDMA read/write concurrency protocol
  - Transaction boundaries
  - **Status:** Room is used; ADR confirms + documents BDMA protocol

- [ ] **ADR-004: Foreground Service Ownership** ⚠️ BLOCKING SPRINT 2
  - Can FGS own CameraX recording?
  - `START_STICKY` vs `START_NOT_STICKY`
  - Recovery after process kill
  - **Depends on:** Hardware POC

- [ ] **ADR-005: Storage & Finalization** ⚠️ BLOCKING SPRINT 2
  - Physical Internal/External/Auto roots
  - Temp → Media finalization: move vs copy
  - ADB visibility requirements
  - **Depends on:** Hardware POC

- [ ] **ADR-006: MVP Encryption Policy** ⚠️ BLOCKING SPRINT 1
  - **Decision:** Force encryption OFF for MVP
  - Remove encryption from critical path
  - Key management/BDMA decryption deferred to Phase 2
  - **Immediate action:** Add policy check that forces OFF

- [ ] **ADR-007: Authentication & Emergency** ⚠️ BLOCKING SPRINT 1
  - Password-first for MVP
  - Emergency identity: `EMERGENCY_OVERRIDE_ADMIN`
  - Emergency policy: when allowed, how enforced
  - Failed-attempt throttle/lockout
  - **Status:** Password auth exists; ADR defines emergency + lockout

- [ ] **ADR-008: BDMA Write-Back** ⚠️ BLOCKING SPRINT 3
  - Which `dcam.db` tables/fields BDMA can write
  - Locking protocol
  - Cleanup permissions (post-import delete)
  - **Depends on:** DDL v1 design

**ADR Template:** Use standard format (Context, Decision, Consequences, Alternatives Considered)

---

### 3. Code Cleanup (1 day)

- [ ] **Remove startup cloud init from AppComposition**
  - Delete `initializeCloudStateAsync()` call
  - Move device identity init to explicit runtime orchestrator (Sprint 1)
  - Keep `OperationalSettingsStore` for later use, don't write on startup

- [ ] **Force encryption OFF (ADR-006)**
  - Add policy check in `MediaEncryptionSettingsUseCaseImpl`
  - Log warning if legacy config enables encryption
  - Prevent `_enc` files from being created

- [ ] **Hide non-MVP surfaces (delivery plan §4.2)**
  - Standalone audio: default OFF in developer gates
  - Recording/Camera/Audio/Storage/GPS/Security settings: hide until real implementation
  - Transfer/Streaming/PTT/Update UI: hide
  - Keep: Login, Camera operation, Video/Image, Files (read-only), About, Device status

- [ ] **Remove RIR violations (if any)**
  - Check architecture tests pass
  - No Android imports in `feature/*/domain` or `feature/*/application/usecase`

---

### 4. Documentation Updates (0.5 day)

- [ ] **Update open-decisions.md**
  - Mark decided items (Camera API, Storage, FGS, Encryption)
  - Add any new TBDs discovered during POC

- [ ] **Update current-state.md**
  - Add POC device details
  - Note any hardware-specific constraints

- [ ] **Create ADR register page**
  - `docs/local-dev/adr/README.md` - index of all ADRs
  - `docs/local-dev/adr/ADR-001-...md` through `ADR-008-...md`

---

## Exit Criteria

Sprint 0 is complete when:

- ✅ Hardware POC executed on ≥1 target device
- ✅ POC report written with evidence
- ✅ All 8 ADRs written and reviewed
- ✅ Encryption forced OFF in code
- ✅ Startup cloud init removed
- ✅ Non-MVP surfaces hidden
- ✅ Architecture tests pass
- ✅ Team agrees on Camera/FGS/Storage choices

**After Sprint 0:** Proceed to Sprint 1 (recording authority + session enforcement).

---

## Risk If Skipped

| Risk | Impact |
|---|---|
| No hardware POC | Build on wrong assumptions; major rework when real device fails |
| No ADR-002 (Camera) | CameraX may not work; Sprint 2 blocked |
| No ADR-004 (FGS) | Recording dies on Activity loss; can't guarantee evidence preservation |
| No ADR-005 (Storage) | BDMA can't read files; integration broken |
| No ADR-006 (Encryption) | Accidental encryption breaks BDMA import; no decryption |
| Startup cloud remains | Violates offline-first; wastes battery; confuses ownership |

**Bottom line:** Sprint 0 is not bureaucracy—it prevents building on unverified assumptions.
