# DCAM knowledge base

This folder is a local, task-oriented digest of the DCAM Confluence space. It is intended to give a developer or AI assistant enough context to reason about product intent, scope, architecture, delivery, and the current repository without rereading every source page.

Snapshot date: **2026-07-07** (Asia/Saigon). The prior four-page refresh is extended with the approved Android Device Operation Requirements. Confluence remains the source of truth; this folder is a navigational summary.

## Read this first

1. [Product brief](00-product/product-brief.md) — what DCAM is and what the PM is optimizing for.
2. [Scope and roadmap](00-product/scope-and-roadmap.md) — MVP, phases, milestones, and delivery boundary.
3. [DCAM-BDMA Data Contract](01-requirements/data-contract.md) — the approved storage, naming, checksum, access, import, and cleanup baseline.
4. [MVP requirement baseline](01-requirements/mvp-baseline.md) — the consolidated product baseline plus the newly created Requirements structure.
5. [Open decisions](01-requirements/open-decisions.md) — what is explicitly TBD and must not be invented.
6. [System architecture](02-architecture/system-architecture.md) — architectural principles, layers, modules, and platform strategy.
7. [DCAM–BDMA and data boundary](02-architecture/data-and-bdma.md) — ownership, ADB integration, metadata, and storage direction.
8. [Development standard](03-development/android-standard.md) — the implementation rules expected by the architecture documents.
9. [Android device operation](01-requirements/android-device-operation.md) — full-screen, boot, launcher/kiosk, lifecycle, service, permission, power, and recovery requirements.
10. [Current repository state](05-current-repo/current-state.md) — what is actually implemented today and where it differs from the target.
11. [Source structure and requirement evidence](06-research-reports/source-structure-requirements-evidence.md) — audited traceability from requirements and architecture to source, flows, tests, and gaps.

## Topic tree

```text
dcam-knowledge/
├── 00-product/
│   ├── product-brief.md
│   └── scope-and-roadmap.md
├── 01-requirements/
│   ├── mvp-baseline.md
│   ├── data-contract.md
│   ├── android-device-operation.md
│   └── open-decisions.md
├── 02-architecture/
│   ├── system-architecture.md
│   ├── data-and-bdma.md
│   └── cloud-quality-security.md
├── 03-development/
│   ├── android-standard.md
│   └── delivery-and-team.md
├── 04-features/
│   ├── feature-map.md
│   └── settings-grid-map.md
├── 05-current-repo/
│   └── current-state.md
├── 06-research-reports/
│   ├── README.md
│   └── source-structure-requirements-evidence.md
└── sources.md
```

## Ground rules for future work

- Preserve the priority order: **reliable capture → correct local data → BDMA compatibility → advanced features**.
- Core capture must work offline. Cloud is optional and may not block recording, capture, metadata, storage, or local logging.
- Treat BodyCamera hardware, firmware, Android version, GMS, GPS, storage, and network as capabilities, not assumptions.
- DCAM creates and exposes source data; BDMA initiates ADB reads and owns desktop import, indexing, management, display, backup, and export.
- Follow the approved Data Contract for logical roots, folders, media naming, MD5 scope, BDMA permissions, import results, and cleanup. Do not invent physical paths, embedded metadata fields, DB schema, key management, recovery, streaming, or PTT details that remain outside it.
- Distinguish the documented target from the current prototype. See [current-state.md](05-current-repo/current-state.md).
- Never copy local values into documentation, source control, or logs. Local build-property policy is documented in the gitignored `application-local.properties` file.

## Current documentation gap

The Data Contract is approved and the Requirements Home plus ten functional-requirement pages now exist. Technical Design, ADR, Release Management, Incident Log, and the Non-functional Requirements are still missing or not started. The current requested writing/expansion order is:

1. Expand Recording & Capture Requirements and BDMA Integration Requirements.
2. Create the Jira backlog from MVP, Requirements, and Data Contract.
3. Create Non-functional Requirements.
4. Create Storage and SQLite Database Designs.
5. Create Security & Encryption Design.
6. Create Release Plan and ADRs for major decisions.

See [sources.md](sources.md) for the complete page/version inventory.
