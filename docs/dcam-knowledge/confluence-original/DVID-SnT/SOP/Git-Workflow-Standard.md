# Git Workflow Standard

**Page ID**: 27165258  
**Version**: 2  
**Type**: page  
**URL**: undefined/spaces/DVID/pages/27165258

---


# Git Workflow Standard

## Purpose

This document defines the standard Git branching strategy and workflow for the BDMA project.

The purpose of this standard is to:

Improve development consistency

Reduce merge conflicts

Improve code review visibility

Support operational clarity

Standardize collaboration between developers

Prepare the project for scalable development and automation

# Branching Strategy

The BDMA project uses a simplified Git workflow designed for small-to-medium team collaboration.

## Main Branches

Branch

Purpose

main

Stable production-ready code

develop

Active development integration branch

# Branch Definitions

## Main Branch (`main`)

The `main` branch contains stable production-ready code.

### Rules

Direct commits are not allowed

Only reviewed and tested code may be merged

Every production release must come from `main`

Branch protection is recommended

## Development Branch (`develop`)

The `develop` branch is the primary integration branch for ongoing development.

### Rules

Feature branches are merged into `develop`

Integration testing is performed here

Temporary instability is acceptable during active development

Production deployment must not be performed directly from `develop`

# Feature Branches

Feature branches are used for individual features or development tasks.

## Naming Convention

text]]>## Examples

text### Rules

One feature per branch

Feature branches should be short-lived

Pull Request is recommended before merge

Merge target should be `develop`

# Bugfix Branches

Bugfix branches are used for non-critical issue fixes.

## Naming Convention

text]]>## Examples

text### Rules

Merge into `develop`

Require testing before merge

Pull Request is recommended

# Hotfix Branches

Hotfix branches are used for urgent production fixes.

## Naming Convention

text]]>## Examples

text## Workflow

text### Rules

Hotfix branches must start from `main`

After merge into `main`, changes must also be merged into `develop`

Hotfixes should be minimal and focused

# Release Branches (Optional)

Release branches may be used in the future if release complexity increases.

## Naming Convention

text]]>## Example

textCurrently, release branches are optional for the BDMA project.

# Pull Request Workflow

All significant code changes should use Pull Requests.

## Pull Request Template

text# Code Review Process

Code Review is required before merging important changes.

## Goals

Improve code quality

Share technical knowledge

Detect risks early

Improve consistency

## Review Checklist

Code compiles successfully

No obvious logic errors

No sensitive/debug code left behind

Naming is readable and consistent

Basic testing completed

# Merge Strategy

## Recommended Strategy

Use:

Squash Merge
or

Standard Merge Commit

Avoid excessive rebasing on shared branches.

# Branch Protection Recommendations

## Main Branch (`main`)

Recommended protections:

Require Pull Request

Require code review

Prevent force push

Prevent direct commits

## Development Branch (`develop`)

Recommended protections:

Pull Request recommended

Direct commits allowed only when necessary

# Development Workflow

## Standard Development Flow

text# Operational Principles

The BDMA Git workflow prioritizes:

Simplicity

Consistency

Readability

Maintainability

Collaboration

Operational visibility

The workflow should remain lightweight and practical while the team is still in the OSd foundation phase.