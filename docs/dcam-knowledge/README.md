# DCAM knowledge base

This folder is a local, task-oriented digest of the DCAM Confluence space. It is intended to give a developer or AI assistant enough context to reason about product intent, scope, architecture, delivery, and the current repository without rereading every source page.

Snapshot date: **2026-07-03** (Asia/Saigon). All principal Confluence documents were still marked Draft, except the documentation plan, which was marked Updated. Confluence remains the source of truth; this folder is a navigational summary.

## Read this first

1. [Product brief](00-product/product-brief.md) — what DCAM is and what the PM is optimizing for.
2. [Scope and roadmap](00-product/scope-and-roadmap.md) — MVP, phases, milestones, and delivery boundary.
3. [MVP requirement baseline](01-requirements/mvp-baseline.md) — the requirements that currently exist outside the empty Requirements folder.
4. [Open decisions](01-requirements/open-decisions.md) — what is explicitly TBD and must not be invented.
5. [System architecture](02-architecture/system-architecture.md) — architectural principles, layers, modules, and platform strategy.
6. [DCAM–BDMA and data boundary](02-architecture/data-and-bdma.md) — ownership, ADB integration, metadata, and storage direction.
7. [Development standard](03-development/android-standard.md) — the implementation rules expected by the architecture documents.
8. [Current repository state](05-current-repo/current-state.md) — what is actually implemented today and where it differs from the target.

## Topic tree

```text
dcam-knowledge/
├── 00-product/
│   ├── product-brief.md
│   └── scope-and-roadmap.md
├── 01-requirements/
│   ├── mvp-baseline.md
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
└── sources.md
```

## Ground rules for future work

- Preserve the priority order: **reliable capture → correct local data → BDMA compatibility → advanced features**.
- Core capture must work offline. Cloud is optional and may not block recording, capture, metadata, storage, or local logging.
- Treat BodyCamera hardware, firmware, Android version, GMS, GPS, storage, and network as capabilities, not assumptions.
- DCAM creates and exposes source data; BDMA initiates ADB reads and owns desktop import, indexing, management, display, backup, and export.
- Do not lock a metadata schema, storage contract, status model, encryption scheme, or streaming/PTT protocol without the missing Data Contract/design or an ADR.
- Distinguish the documented target from the current prototype. See [current-state.md](05-current-repo/current-state.md).
- Never copy local values into documentation, source control, or logs. Local build-property policy is documented in the gitignored `application-local.properties` file.

## Current documentation gap

The Product Management, planning, architecture, onboarding, and Android-standard baselines exist. The formal **Requirements**, **Technical Design**, **ADR**, **Release Management**, and **Incident Log** areas are still empty. The PM's immediate requested writing order is:

1. DCAM–BDMA Data Contract.
2. Functional Requirements.
3. Non-functional Requirements.
4. Storage Design.
5. Metadata Design.
6. Security & Encryption Design.
7. Release Plan, then advanced-feature designs and ADRs.

See [sources.md](sources.md) for the complete page/version inventory.
