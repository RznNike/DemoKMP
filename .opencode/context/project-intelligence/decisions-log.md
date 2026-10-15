<!-- Context: project-intelligence/decisions | Priority: medium | Version: 1.1 | Updated: 2026-10-15 -->

# Decisions Log

> Architectural decisions for DemoKMP with context. Format: Context → Decision → Rationale → Alternatives → Impact.

## Quick Reference

- **Purpose**: why key choices were made
- **Status**: Decided | Pending | Deprecated

---

## Decision: Kotlin Multiplatform for shared code

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

### Context
One codebase is needed for PC (Windows/Linux JVM) and Android.

### Decision
Use Kotlin Multiplatform (`commonMain` + `androidMain` + `desktopMain`).

### Rationale
Maximizes shared Kotlin while allowing platform code via `expect/actual`.

### Alternatives Considered
| Alternative | Pros | Cons | Why Rejected? |
|-------------|------|------|---------------|
| Separate native apps | Full platform idioms | 2x code, drift | Too much duplication |
| Flutter | Single codebase | Dart; weaker Kotlin/JVM interop | Kotlin ecosystem + JVM desktop preferred |

### Impact
- **Positive**: one codebase, shared domain/data
- **Negative**: `expect/actual` boilerplate
- **Risk**: platform divergence

---

## Decision: Compose Multiplatform for UI

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

### Context
UI must run on both Desktop and Android without duplicating view code.

### Decision
Use Compose Multiplatform with Material 3.

### Rationale
Single declarative UI layer; matches typical Android/Compose skills.

### Alternatives Considered
| Alternative | Pros | Cons | Why Rejected? |
|-------------|------|------|---------------|
| Native UI per platform | Best platform fidelity | 2x UI work | Defeats code sharing |
| Compose (Android) + Swing (Desktop) | Mature | Two toolkits | Inconsistent UI/effort |

### Impact
- **Positive**: shared UI
- **Negative**: some desktop-specific UI caveats

---

## Decision: Clean Architecture layering (app/domain/data)

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

### Context
The demo must show a maintainable, testable structure.

### Decision
Three layers with dependency rule `app → domain ← data`.

### Rationale
Decouples business rules from frameworks; makes the pattern copyable.

### Alternatives Considered
| Alternative | Pros | Cons | Why Rejected? |
|-------------|------|------|---------------|
| Feature-by-feature flat structure | Less boilerplate | Mixes concerns | Less illustrative of clean layering |

### Impact
- **Positive**: clear boundaries
- **Negative**: more files/interfaces

---

## Decision: Koin for dependency injection

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

### Context
Need DI that works across Android and Desktop.

### Decision
Use Koin (one module per concern).

### Rationale
Multiplatform-friendly, no codegen, simple DSL.

### Alternatives Considered
| Alternative | Pros | Cons | Why Rejected? |
|-------------|------|------|---------------|
| Manual DI | No dependency | Boilerplate, error-prone | Doesn't scale |
| Kodein / other | Featureful | Extra learning | Koin simpler |

### Impact
- **Positive**: simple wiring
- **Negative**: runtime (not compile-time) resolution

---

## Decision: Room for local persistence

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

### Context
Need cross-platform SQL storage with a familiar API.

### Decision
Use Room (KSP) + bundled SQLite.

### Rationale
KMP support, compile-time-verified queries, Android familiarity.

### Alternatives Considered
| Alternative | Pros | Cons | Why Rejected? |
|-------------|------|------|---------------|
| SQLDelight | KMP-native | Different DSL | Room familiarity preferred |
| Raw SQLite | No codegen | Manual, unsafe | Error-prone |

### Impact
- **Positive**: typed queries
- **Negative**: KSP codegen step required

---

## Decision: Errors-as-values via UseCaseResult

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

### Context
Error handling must be consistent and testable across layers.

### Decision
UseCases return `UseCaseResult<R>` (data or error), not thrown exceptions.

### Rationale
Explicit, no hidden control flow across boundaries; easy to map to UI.

### Alternatives Considered
| Alternative | Pros | Cons | Why Rejected? |
|-------------|------|------|---------------|
| Exceptions | Simpler happy path | Implicit, easy to leak | Less explicit |
| Kotlin `Result` | Built-in | Less domain context | Custom type preferred |

### Impact
- **Positive**: explicit errors
- **Negative**: wrapper at each use site

---

## Deprecated Decisions

| Decision | Date | Replaced By | Why |
|----------|------|-------------|-----|
| (none yet) | — | — | — |

## Onboarding Checklist

- [ ] Understand why KMP, Compose, Clean Arch, Koin, Room were chosen
- [ ] Understand the errors-as-values choice

## 📂 Codebase References

| Decision | Code |
|----------|------|
| Layering | `app/`, `domain/`, `data/` |
| DI | `app/di/*.kt` |
| DB | `data/storage/` |
| Network | `data/network/` |
| Errors | `domain/common/interactor/UseCaseResult.kt` |

## Related Files

- `technical-domain.md` · `business-tech-bridge.md` · `living-notes.md`
