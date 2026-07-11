# DCAM local developer docs

This folder contains repository-local documentation that is not a Confluence mirror. Use it for implementation reality, source evidence, local plans, and developer-only reference material.

Confluence-derived product, requirement, architecture, development, and feature summaries remain in [docs/dcam-knowledge](../dcam-knowledge/README.md). Confluence remains the source of truth for approved project documentation.

## 🚀 START HERE (Sprint 0)

**NEW:** Sprint 0 research and planning documents created 2026-07-10.

**Quick start:**
1. Read [sprint-0-summary.md](sprint-0-summary.md) (3 min) — What is Sprint 0?
2. Read [mvp-readiness-report.md](mvp-readiness-report.md) (10 min) — Full project status
3. Follow [sprint-0-action-checklist.md](sprint-0-action-checklist.md) — Tasks to close Sprint 0

**Full navigation:** See [sprint-0-index.md](sprint-0-index.md)

## Folder map

```text
local-dev/
├── sprint-0-index.md                    Sprint 0 document navigation
├── sprint-0-summary.md                  Quick reference (start here)
├── sprint-0-action-checklist.md         Task list to close Sprint 0
├── mvp-readiness-report.md              Full project state analysis
├── sprint-0-poc-test-plan.md            Hardware testing procedures
├── poc-capability-assessment.md         Current code vs POC tests
├── current-repo/
│   ├── README.md
│   ├── current-state.md                 Implementation reality vs docs
│   ├── settings-grid-map.md
│   └── DCAM_Huong_dan_dev_MVP_codebase.pdf
├── evidence/
│   ├── README.md
│   └── source-structure-requirements-evidence.md
└── plans/
    ├── README.md
    ├── mvp-delivery-plan.md             Master delivery playbook
    ├── database-ddl-v1-design.md
    └── database-ddl-v1.sql
```

## Boundaries

- Put current codebase status, repo-specific implementation notes, and generated codebase guides under `current-repo`.
- Put audits, traceability reports, verification notes, and source-backed evidence under `evidence`.
- Put local work plans, investigation plans, and implementation proposals under `plans`.
- Do not treat local-dev notes as approved requirements or Confluence source material.

## Sprint 0 Status

**Created:** 2026-07-10  
**Status:** ⚠️ **BLOCKING** — Sprint 0 incomplete

**Critical gaps:**
- No real-device Camera/FGS/storage POC
- 8 ADRs not written (0/8 complete)
- Encryption must be forced OFF
- Startup cloud init must be removed

**Timeline:** 5-6 days to close Sprint 0 before Sprint 1 continues.

See [sprint-0-action-checklist.md](sprint-0-action-checklist.md) for details.
