# Contributing Guidelines

Hey, Mobile Ninja! We’re thrilled to have you contribute to EnergyIQ. We value collaboration, clarity, and high-quality code. Please review these guidelines to ensure a smooth contribution process.

What we’re building: EnergyIQ is a software platform that connects to a user's existing solar inverter system and transforms raw technical data into clear, actionable intelligence — energy performance, cost savings, alerts, and AI-powered insights, all in one place.

## Table of Contents

1. [How to Contribute](#1-how-to-contribute)
2. [Pull Request Process](#2-pull-request-process)
3. [Commit Message Guidelines](#3-commit-message-guidelines)
4. [Coding Standards](#4-coding-standards)
5. [Issue Reporting](#5-issue-reporting)
6. [Branching Model](#6-branching-model)
7. [Testing](#7-testing)
8. [Documentation](#8-documentation)
9. [Additional Resources](#9-additional-resources)

## 1. How to Contribute

We welcome contributions in many forms:

- Bug Reports: Submit detailed issues if you encounter a bug (e.g., attendance sync errors, timetable conflicts, fee summaries not loading).
- Feature Requests: Suggest enhancements (like new attendance modes, better analytics, parent notifications).
- Code Contributions: Fix bugs or implement new features across the app (auth, dashboards, portals, UI).
- Documentation: Improve our docs, folder structure explanations, or onboarding guides.

⚠️ For significant changes, please open an issue first to discuss your idea with the team.

## 2. Pull Request Process

- Base Branch: Always branch off `dev`. PRs must be raised against `dev`, never against `main`.
- Small, Focused Changes: Each PR should solve one issue or implement one feature (e.g., “Add timetable clash validation”).
- Clear Title & Summary: Use descriptive titles and explain your changes, rationale, and testing steps.
- Follow Feedback: Address review comments and update your PR accordingly.
- CI/CD Compliance: Ensure linting, formatting, and tests pass before submission.

⚠️ Only the project lead can merge `dev` into `staging` once stability is verified.

## 3. Commit Message Guidelines

We follow the Conventional Commits specification.

Format:

```text
<type>(<scope>): <description>
```

Types:

- `feat` — New feature (e.g., timetable clash detection)
- `fix` — Bug fix (e.g., attendance status mismatch)
- `docs` — Documentation changes
- `style` — Code style changes (formatting, UI tweaks)
- `refactor` — Code refactoring without feature/bug changes
- `test` — Adding/updating tests
- `chore` — Build process, tools, dependencies

Examples:

```text
feat(auth): implement OTP verification for onboarding
fix(api): correct streaming response for prompt messages
docs(readme): update contribution steps
```

## 4. Coding Standards

- Consistency: Follow the existing folder structure (`app/`, `components/`, `lib/`, etc.).
- Linting & Formatting: Code must pass ESLint + Prettier before committing.
- Readability: Write clean, maintainable, and well-documented code.
- Security: Prioritize secure coding practices, especially around authentication and role-based access.

## 5. Issue Reporting

When reporting an issue:

- Provide a clear description (e.g., “Teacher attendance not updating after NFC tap”).
- Include steps to reproduce.
- Add screenshots or logs where relevant.
- Suggest potential fixes if possible.

## 6. Branching Model

- Feature Branches: `feat/<short-description>`
- Bug fix Branches: `fix/<short-description>`
- Hotfix Branches: Reserved for urgent fixes directly on `main` (lead only).

Examples:

```text
feat/improve-dashboard-overview
fix/fix-inverter-api-integration
```

Regularly pull from `dev` to keep your branch updated.

## 7. Testing

- Automated Tests: Add/update tests for new features or bug fixes.
- Local Testing: Run all tests locally before PR submission.
- Manual Verification: Confirm functionality across relevant browsers/devices.

## 8. Documentation

- Update `README.md` or add documentation when adding/modifying features.
- Add inline comments in code where clarity is needed.
- For major features, update the docs in the respective domain folder.

## 9. Additional Resources

- [The Art of Pull Requests](https://github.blog/developer-skills/github/how-to-write-the-perfect-pull-request/)
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)

Thank you for contributing to EnergyIQ and helping us build a reliable platform for SMEs and solar inverter users.
