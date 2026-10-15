<!-- Context: project-intelligence/bridge | Priority: high | Version: 1.1 | Updated: 2026-10-15 -->

# Business ↔ Tech Bridge

> Maps DemoKMP's goal (demonstrate KMP solutions) to concrete technical implementations.

## Quick Reference

- **Purpose**: connect the demo goal to what is actually built
- **Update When**: features added or removed

## Core Mapping

| Goal | Technical Solution | Why | Value |
|------|--------------------|-----|-------|
| Cross-platform code sharing | Kotlin Multiplatform `commonMain` + `expect/actual` | Single source of truth | Learn once, run on PC + Android |
| Maintainable architecture | Clean Architecture (app/domain/data) | Testable, decoupled | Patterns learners can copy |
| Modern declarative UI | Compose MP + Material 3 + MVVM | Shared UI across targets | One UI skill set |
| Realistic backend usage | Ktor + Ktorfit HTTP & WebSocket demos | Real network integration | Ready-made reference |
| Persistence demos | Room (SQL) + Multiplatform Settings | Show DB + prefs patterns | Copy-paste persistence |
| Platform-specific power | COM objects (Win), PDF, camera/barcode (Android) | Show native interop | Beyond common code |

## Feature Mapping Examples

### Feature: HTTP example screen

**Business Context**:
- User need: a working network-call example
- Business goal: demonstrate HTTP integration

**Technical Implementation**:
- Solution: `AppApi` (Ktorfit) → `HTTPExampleGatewayImpl` → `GetRandomImageLinksUseCase` → `HTTPExampleViewModel`
- Architecture: full `app → domain ← data` flow

**Connection**: shows the complete layered flow end to end, so learners can copy it wholesale.

### Feature: Custom logger

**Business Context**:
- User need: structured logging example
- Business goal: demonstrate a modular, pluggable design

**Technical Implementation**:
- Solution: `Logger` + extensions (Console/File/DB/Memory), `LogGatewayImpl`, Room DAOs
- Architecture: abstractions in domain, implementations in data

**Connection**: showcases the modular, layered design the project teaches.

## Trade-off Decisions

| Situation | Business Priority | Technical Priority | Decision | Rationale |
|-----------|-------------------|--------------------|----------|-----------|
| Demo clarity vs completeness | Show many features | Keep codebase small | Many small isolated demo screens | Each solution stays standalone and copyable |

## Common Misalignments

| Misalignment | Warning Signs | Resolution |
|--------------|---------------|------------|
| Over-engineering the demo | Production-grade abstractions appear | Keep demos minimal and illustrative |

## Onboarding Checklist

- [ ] See how each demo maps to a learning goal
- [ ] Understand the key trade-off (breadth vs simplicity)

## 📂 Codebase References

| Context | Code |
|---------|------|
| HTTP flow | `data/network/AppApi.kt`, `data/gateway/HTTPExampleGatewayImpl.kt`, `domain/interactor/httpexample/GetRandomImageLinksUseCase.kt`, `app/viewmodel/httpexample/HTTPExampleViewModel.kt` |
| Logger | `domain/log/**` |
| Screens | `app/viewmodel/**`, `app/ui/**` |

## Related Files

- `business-domain.md` · `technical-domain.md` · `decisions-log.md` · `living-notes.md`
