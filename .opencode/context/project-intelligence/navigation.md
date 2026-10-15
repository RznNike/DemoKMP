<!-- Context: project-intelligence/nav | Priority: high | Version: 1.1 | Updated: 2026-10-15 -->

# Project Intelligence

> Start here for quick project understanding. Populated for DemoKMP (Kotlin Multiplatform: Desktop JVM + Android).

## Structure

```
.opencode/context/project-intelligence/
├── navigation.md              # This file - quick overview
├── business-domain.md         # Why: reference KMP demo project
├── technical-domain.md        # How: stack, architecture, patterns
├── business-tech-bridge.md    # How goals map to implementation
├── decisions-log.md           # Key architectural decisions
└── living-notes.md            # Active issues, debt, gotchas
```

## Quick Routes

| What You Need | File | Description |
|---------------|------|-------------|
| Understand the "why" | `business-domain.md` | Problem, users, value proposition |
| Understand the "how" | `technical-domain.md` | Stack, architecture, code patterns |
| See the connection | `business-tech-bridge.md` | Goal → technical mapping |
| Know the context | `decisions-log.md` | Why decisions were made |
| Current state | `living-notes.md` | Debt, open questions, gotchas |
| All of the above | Read all files in order | Full project intelligence |

## Usage

**New team member / agent**:
1. Start with `navigation.md` (this file)
2. Read files in order for complete understanding
3. Follow each file's onboarding checklist

**Quick reference**:
- Business focus → `business-domain.md`
- Technical focus → `technical-domain.md`
- Decision context → `decisions-log.md`

## Maintenance

Trigger → Action:
- Stack change → update `technical-domain.md`
- New decision → add to `decisions-log.md`
- New debt/issue → update `living-notes.md`
- Feature change → update `business-tech-bridge.md`

## 📂 Codebase References

| Context | Code |
|---------|------|
| Stack & structure | `gradle/libs.versions.toml`, `composeApp/build.gradle.kts` |
| Source root | `composeApp/src/commonMain/kotlin/ru/rznnike/demokmp/` |

## Related Files

- Standard: `/c/Users/RznNike/.config/opencode/context/core/standards/project-intelligence.md`
- Management: `/c/Users/RznNike/.config/opencode/context/core/standards/project-intelligence-management.md`
