# DCAM knowledge base

This folder is a local, task-oriented digest of the DCAM Confluence space. It is intended to give a developer or AI assistant enough context to reason about product intent, scope, architecture, and delivery without rereading every source page.

Snapshot date: **2026-07-08** (Asia/Saigon). The July 8 refresh updates the Data Contract, Android Device Operation Requirements, Cloud Services/Update/Configuration Architecture, System Settings Requirements, Architecture pages, Android Development Standard, and the 4.2 Technical Design folder. Confluence remains the source of truth; this folder is a navigational summary.

Repository-local implementation notes, evidence reports, and plans live outside this mirror in [docs/local-dev](../local-dev/README.md).

Technical Design pages under 4.2 are draft/target-direction material unless otherwise finalized through implementation and review. Treat them as expected design intent, not as current repository behavior.

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
10. [Technical design drafts](05-technical-design/README.md) — July 8 design intent for operation, kiosk, console/settings, recording, storage, DB, BDMA, cloud provisioning, update, security, sensors, and AI.

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
│   └── feature-map.md
├── 05-technical-design/
│   └── README.md
└── sources.md
```

## Ground rules for future work

- Preserve the priority order: **reliable capture → correct local data → BDMA compatibility → advanced features**.
- Core capture must work offline. Cloud is optional and may not block recording, capture, metadata, storage, or local logging.
- Treat BodyCamera hardware, firmware, Android version, GMS, GPS, storage, and network as capabilities, not assumptions.
- DCAM creates and exposes source data; BDMA initiates ADB reads and owns desktop import, indexing, management, display, backup, and export.
- Follow the approved Data Contract for logical roots, folders, media naming, MD5 scope, BDMA permissions, import results, and cleanup. Do not invent physical paths, embedded metadata fields, DB schema, key management, recovery, streaming, or PTT details that remain outside it.
- Distinguish the documented target from the current prototype. See [local current repository state](../local-dev/current-repo/current-state.md).
- Never copy local values into documentation, source control, or logs. Local build-property policy is documented in the gitignored `application-local.properties` file.

## Current documentation gap

The Data Contract is approved, the Requirements Home plus functional requirements now exist, Non-functional Requirements have been drafted/updated, and Technical Design 4.2 now contains the July 8 draft design set. ADR, Release Management, and Incident Log remain incomplete or not fully started. The current requested writing/expansion order should be rechecked against Confluence before use:

1. Expand Recording & Capture Requirements and BDMA Integration Requirements.
2. Create the Jira backlog from MVP, Requirements, and Data Contract.
3. Review and complete Non-functional Requirements.
4. Correct/complete Storage, SQLite, Security, Operation, and Console Technical Designs as implementation proves behavior.
5. Create Release Plan and ADRs for major decisions.

See [sources.md](sources.md) for the complete page/version inventory.
