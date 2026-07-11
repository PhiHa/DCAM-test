# Sprint 0 Documentation Index

**Created:** 2026-07-10  
**Purpose:** Navigate Sprint 0 research and planning documents

---

## Start Here

**New to Sprint 0?** Read these in order:

1. **sprint-0-summary.md** (3 min read)  
   Quick overview of what Sprint 0 is and why it matters

2. **mvp-readiness-report.md** (10 min read)  
   Full project state: architecture, docs, current code, gaps

3. **sprint-0-action-checklist.md** (5 min read)  
   Step-by-step tasks to close Sprint 0

---

## By Role

### For Project Manager / Team Lead
- **mvp-readiness-report.md** — Project status, risks, timeline impact
- **sprint-0-action-checklist.md** — What needs approval/decision

### For Developers
- **sprint-0-poc-test-plan.md** — Hardware testing procedures
- **poc-capability-assessment.md** — Which tests current code can/cannot pass
- **sprint-0-action-checklist.md** — Coding tasks (encryption, cleanup, etc.)

### For QA/Test Engineer
- **sprint-0-poc-test-plan.md** — Test procedures and report template
- **poc-capability-assessment.md** — Expected test results

---

## Document Descriptions

### 1. mvp-readiness-report.md (9.6 KB)
**Purpose:** Comprehensive project state analysis

**Contains:**
- Project overview (DCAM vs BDMA ecosystem)
- Architecture snapshot (modules, stack, current state)
- Document map (where everything lives)
- Key document summaries (product, requirements, architecture)
- Implementation state (working/in-progress/missing)
- 8 ADR decisions required
- Sprint plan overview
- Critical risks

**Read when:** You need the big picture or status report for stakeholders

---

### 2. sprint-0-summary.md (3.2 KB)
**Purpose:** Quick reference / TL;DR

**Contains:**
- What is Sprint 0?
- The two problems (startup cloud, no hardware POC)
- Sprint 0 deliverables (3 items)
- Exit criteria
- Key files created
- Next steps

**Read when:** You need a 5-minute summary for someone new

---

### 3. sprint-0-action-checklist.md (5.5 KB)
**Purpose:** Step-by-step tasks to close Sprint 0

**Contains:**
- Critical path items in order
- Hardware POC (3 days) with checklist
- Write 8 ADRs (2 days) with priority flags
- Code cleanup (1 day) with specific files
- Documentation updates (0.5 day)
- Exit criteria
- Risk if skipped

**Read when:** You're about to start Sprint 0 work and need assignments

---

### 4. sprint-0-poc-test-plan.md (7.0 KB)
**Purpose:** Hardware testing procedures

**Contains:**
- Prerequisites (device list, ADB setup)
- Test 1: Camera API (6 scenarios)
- Test 2: FGS behavior (5 scenarios)
- Test 3: Storage roots/ADB (6 scenarios)
- Test 4: Screen/power (3 scenarios)
- Test 5: Device Owner/kiosk (3 scenarios)
- POC report template
- Success criteria

**Read when:** You're running tests on physical BodyCamera device

---

### 5. poc-capability-assessment.md (8.6 KB)
**Purpose:** Can current code pass POC tests?

**Contains:**
- Executive summary (NOT READY, and why)
- Test-by-test assessment with code evidence
- Summary table (works / will fail / blocked)
- What POC will prove
- Expected POC report preview
- ADR impact analysis

**Read when:** You want to know what will happen before running POC

**Key insight:** 40% of tests will fail (Activity/FGS ownership), which is GOOD — proves gaps are real.

---

## Related Documents (Pre-Existing)

### Main Planning
- **docs/local-dev/plans/mvp-delivery-plan.md** (32 KB)  
  The master delivery playbook with capability matrix, sprint plan, failure test matrix, DDL requirements

### Current State
- **docs/local-dev/current-repo/current-state.md** (12 KB)  
  What code actually does vs approved documentation

### Confluence Digest
- **docs/dcam-knowledge/README.md**  
  Reading order for all Confluence-derived requirements/architecture

---

## FAQ

**Q: Which document do I read first?**  
A: **sprint-0-summary.md** (3 min TL;DR)

**Q: I need to run POC tests, where do I start?**  
A: **sprint-0-poc-test-plan.md** + **poc-capability-assessment.md**

**Q: What tasks do I need to complete?**  
A: **sprint-0-action-checklist.md**

**Q: Why are we doing Sprint 0?**  
A: Read section "Risk If Skipped" in **sprint-0-action-checklist.md**

**Q: How long will Sprint 0 take?**  
A: 5.5 days total: 3 days POC + 2 days ADRs + 0.5 days code cleanup

**Q: What if POC finds CameraX doesn't work?**  
A: Switch to Camera2 API. Better to know now than after Sprint 2.

---

## File Locations

All files in `D:\Projects\DCAM-test\docs\local-dev\`:

```
local-dev/
├── sprint-0-index.md                    ← You are here
├── sprint-0-summary.md                  Quick reference
├── sprint-0-action-checklist.md         Task list
├── mvp-readiness-report.md              Full analysis
├── sprint-0-poc-test-plan.md            Test procedures
├── poc-capability-assessment.md         Current code vs tests
├── current-repo/
│   └── current-state.md                 Implementation reality
├── plans/
│   ├── mvp-delivery-plan.md             Master playbook
│   ├── database-ddl-v1-design.md
│   └── database-ddl-v1.sql
└── evidence/
    └── source-structure-requirements-evidence.md
```

---

## Next Steps After Reading

1. **Today:** Review sprint-0-summary.md with team
2. **This week:** Acquire BodyCamera, run POC per test plan
3. **Next week:** Write 8 ADRs based on POC evidence
4. **Week after:** Code cleanup, Sprint 1 continues

---

**Remember:** Sprint 0 is about validating assumptions before building. Low cost (5 days), high value (prevents weeks of rework).
