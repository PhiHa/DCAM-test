# DCAM local developer docs

This folder contains repository-local documentation that is not a Confluence mirror. Use it for implementation reality, source evidence, local plans, and developer-only reference material.

Confluence-derived product, requirement, architecture, development, and feature summaries remain in [docs/dcam-knowledge](../dcam-knowledge/README.md). Confluence remains the source of truth for approved project documentation.

## ðŸš€ START HERE (Sprint 0)

**NEW:** Confluence refresh gap report added 2026-07-13.

**Quick start:**
1. Read [evidence/confluence-refresh-gap-report-2026-07-13.md](evidence/confluence-refresh-gap-report-2026-07-13.md) — latest Confluence-vs-code gaps
2. Read [sprint-0-summary.md](sprint-0-summary.md) — Sprint 0 scope
3. Follow [sprint-0-action-checklist.md](sprint-0-action-checklist.md) — tasks to close Sprint 0

**Full navigation:** See [sprint-0-index.md](sprint-0-index.md)

## Folder map

```text
local-dev/
â”œâ”€â”€ sprint-0-index.md                    Sprint 0 document navigation
â”œâ”€â”€ sprint-0-summary.md                  Quick reference (start here)
â”œâ”€â”€ sprint-0-action-checklist.md         Task list to close Sprint 0
â”œâ”€â”€ mvp-readiness-report.md              Full project state analysis
â”œâ”€â”€ sprint-0-poc-test-plan.md            Hardware testing procedures
â”œâ”€â”€ poc-capability-assessment.md         Current code vs POC tests
â”œâ”€â”€ current-repo/
â”‚   â”œâ”€â”€ README.md
â”‚   â”œâ”€â”€ current-state.md                 Implementation reality vs docs
â”‚   â”œâ”€â”€ settings-grid-map.md
â”‚   â””â”€â”€ DCAM_Huong_dan_dev_MVP_codebase.pdf
â”œâ”€â”€ evidence/
â”‚   â”œâ”€â”€ README.md
â”‚   â””â”€â”€ source-structure-requirements-evidence.md
â””â”€â”€ plans/
    â”œâ”€â”€ README.md
    â”œâ”€â”€ mvp-delivery-plan.md             Master delivery playbook
    â”œâ”€â”€ database-ddl-v1-design.md
    â””â”€â”€ database-ddl-v1.sql
```

## Boundaries

- Put current codebase status, repo-specific implementation notes, and generated codebase guides under `current-repo`.
- Put audits, traceability reports, verification notes, and source-backed evidence under `evidence`.
- Put local work plans, investigation plans, and implementation proposals under `plans`.
- Do not treat local-dev notes as approved requirements or Confluence source material.

## Sprint 0 Status

**Created:** 2026-07-10  
**Status:** âš ï¸ **BLOCKING** â€” Sprint 0 incomplete

**Critical gaps:**
- No real-device Camera/FGS/storage POC
- 8 ADRs not written (0/8 complete)
- Encryption must be forced OFF
- Startup cloud init must be removed

**Timeline:** 5-6 days to close Sprint 0 before Sprint 1 continues.

See [sprint-0-action-checklist.md](sprint-0-action-checklist.md) for details.


