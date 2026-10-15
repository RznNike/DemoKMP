<!-- Context: project-intelligence/business | Priority: high | Version: 1.1 | Updated: 2026-10-15 -->

# Business Domain

> DemoKMP is an open-source reference project: a cross-platform (PC + Android) app built with Kotlin Multiplatform to demonstrate solutions to typical problems.

## Quick Reference

- **Purpose**: Why this project exists
- **Update When**: Goals, audience, or scope change
- **Audience**: Developers, contributors, learners

## Project Identity

```
Project Name: DemoKMP
Tagline: Sample Kotlin Multiplatform project for Desktop (JVM) and Android
Problem: KMP lacks a single canonical example showing many real-world solutions working together
Solution: One runnable demo combining Clean Architecture, Compose UI, DI, DB,
          HTTP/WebSocket, charts, PDF, and platform-specific features
```

## Target Users

| Segment | Who | Need | Pain |
|---------|-----|------|------|
| Developers / learners | KMP newcomers, Android/Desktop devs | A working reference for typical tasks | Docs are fragmented; examples don't combine |
| Contributors | Open-source developers | Clear, idiomatic patterns to extend | Unclear conventions |
| Author / maintainer | RznNike | Portfolio + reusable solutions | Keeping many features current |

## Value Proposition

**For Users**:
- One runnable project solving many "typical problems" at once
- Idiomatic Clean Architecture + Compose Multiplatform reference
- Platform-specific features (COM objects, PDF, barcode, camera) demonstrated

**For Business**:
- Showcase of KMP capabilities for Desktop + Android
- Teaching/reference artifact for the community

## Scope

**In scope**: demonstrating solutions (architecture, DI, DB, HTTP/WebSocket, logging, navigation, theming, charts, markdown, PDF, custom UI).

**Out of scope**: production business logic, real backend, monetization. Uses sample APIs — dog.ceo (HTTP), echo.websocket.org (WebSocket).

## Success Metrics (qualitative)

| Metric | Definition | Target |
|--------|-----------|--------|
| Adoption | Stars / clones / forks | Growing |
| Completeness | Solutions demonstrated | Broad feature set |
| Build health | Builds on Windows/Linux + Android | Green |

## Roadmap Context

**Current Focus**: keep feature set current with dependencies

**Long-term Vision**: a canonical KMP reference project

## Business Constraints

- Demo project: relies on public sample APIs
- Desktop builds are OS-specific (build on the OS you target)

## Onboarding Checklist

- [ ] Understand it is a reference/demo, not a product
- [ ] Know the audience (KMP learners, contributors)
- [ ] Know what is in/out of scope

## 📂 Codebase References

| Context | Location |
|---------|----------|
| Project description | `README.md`, `README.ru.md` |
| Screenshots | `readmeFiles/` |
| Feature demos | `composeApp/src/commonMain/kotlin/ru/rznnike/demokmp/**` |

## Related Files

- `technical-domain.md` · `business-tech-bridge.md` · `decisions-log.md`
