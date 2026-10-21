<!-- Context: project-intelligence/decisions | Priority: medium | Version: 1.2 | Updated: 2026-10-21 -->

# Decisions Log

> Architectural decisions for DemoKMP with context. Format: Context → Decision → Rationale → Alternatives → Impact.

## Quick Reference

- **Purpose**: why key choices were made
- **Status**: Decided | Pending | Deprecated

---

## Decision: Kotlin Multiplatform for shared code

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

- **Context**: One codebase is needed for PC (Windows/Linux JVM) and Android.
- **Decision**: Use Kotlin Multiplatform (`commonMain` + `androidMain` + `desktopMain`).
- **Rationale**: Maximizes shared Kotlin while allowing platform code via `expect/actual`.
- **Alternatives**: Separate native apps — 2x code, drift. Flutter — Dart, weaker Kotlin/JVM interop.
- **Impact**: ✅ One codebase, shared domain/data · ⚠️ `expect/actual` boilerplate · ⚠️ Platform divergence risk.

---

## Decision: Compose Multiplatform for UI

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

- **Context**: UI must run on both Desktop and Android without duplicating view code.
- **Decision**: Use Compose Multiplatform with Material 3.
- **Rationale**: Single declarative UI layer; matches typical Android/Compose skills.
- **Alternatives**: Native UI per platform — 2x UI work. Compose (Android) + Swing (Desktop) — two toolkits, inconsistent effort.
- **Impact**: ✅ Shared UI · ⚠️ Some desktop-specific UI caveats.

---

## Decision: Clean Architecture layering (app/domain/data)

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

- **Context**: The demo must show a maintainable, testable structure.
- **Decision**: Three layers with dependency rule `app → domain ← data`.
- **Rationale**: Decouples business rules from frameworks; makes the pattern copyable.
- **Alternatives**: Feature-by-feature flat structure — less boilerplate, mixes concerns, less illustrative of clean layering.
- **Impact**: ✅ Clear boundaries · ⚠️ More files/interfaces.

---

## Decision: Koin for dependency injection

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

- **Context**: Need DI that works across Android and Desktop.
- **Decision**: Use Koin, one module per concern.
- **Rationale**: Multiplatform-friendly, no codegen, simple DSL.
- **Alternatives**: Manual DI — boilerplate, error-prone, doesn't scale. Kodein — featureful but extra learning curve.
- **Impact**: ✅ Simple wiring · ⚠️ Runtime (not compile-time) resolution.

---

## Decision: Room for local persistence

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

- **Context**: Need cross-platform SQL storage with a familiar API.
- **Decision**: Use Room (KSP) + bundled SQLite.
- **Rationale**: KMP support, compile-time-verified queries, Android familiarity.
- **Alternatives**: SQLDelight — KMP-native but a different DSL. Raw SQLite — no codegen, manual and unsafe.
- **Impact**: ✅ Typed queries · ⚠️ KSP codegen step required before compile.

---

## Decision: Errors-as-values via UseCaseResult

**Date**: 2026-10-15 · **Status**: Decided · **Owner**: Project author

- **Context**: Error handling must be consistent and testable across layers.
- **Decision**: UseCases return `UseCaseResult<R>` (data or error), not thrown exceptions.
- **Rationale**: Explicit; no hidden control flow across boundaries; easy to map to UI.
- **Alternatives**: Exceptions — implicit, easy to leak. Kotlin `Result` — built-in but less domain context.
- **Impact**: ✅ Explicit errors · ⚠️ Wrapper at each use site.

---

## Decision: Shared test source set with kotlin.test

**Date**: 2026-10-16 · **Status**: Decided · **Owner**: Project author *(inferred)*

- **Context**: The demo needed regression safety without adopting a heavy multiplatform test framework.
- **Decision**: `commonTest` with `kotlin("test")` + `kotlinx-coroutines-test`; platform-bound tests live in `desktopTest`.
- **Rationale**: Platform-independent tests run on every target; `kotlin.test` keeps dependencies minimal.
- **Alternatives**: Per-target duplicated test sets — drift. Kotest/MockK — heavier multiplatform setup for a demo.
- **Impact**: ✅ 86 test files, one source of truth · ⚠️ Android platform code remains untested (no `androidUnitTest`).

---

## Decision: Hand-written fakes over a mocking framework

**Date**: 2026-10-16 · **Status**: Decided · **Owner**: Project author *(inferred)*

- **Context**: Gateways are interfaces in `domain`, so test doubles must implement them somehow.
- **Decision**: Hand-written `Fake*` classes in `commonTest`, mirroring the production package. No mocking framework.
- **Rationale**: Keeps dependencies minimal, makes fake state inspectable, behaves identically on every target.
- **Alternatives**: MockK / Turbine — multiplatform setup cost and harder-to-read expectations.
- **Impact**: ✅ Explicit, inspectable test state · ⚠️ Fakes must be updated when interfaces change.

---

## Deprecated Decisions

| Decision   | Date | Replaced By | Why |
|------------|------|-------------|-----|
| (none yet) | —    | —           | —   |

## Onboarding Checklist

- [ ] Understand why KMP, Compose, Clean Arch, Koin, Room were chosen
- [ ] Understand the errors-as-values choice
- [ ] Understand the test source-set split and the fakes-over-mocks convention

## 📂 Codebase References

| Decision         | Code                                                        |
|------------------|-------------------------------------------------------------|
| Layering         | `app/`, `domain/`, `data/`                                  |
| DI               | `app/di/*.kt`                                               |
| DB               | `data/storage/`                                             |
| Network          | `data/network/`                                             |
| Errors           | `domain/common/interactor/UseCaseResult.kt`                 |
| Test source sets | `composeApp/src/commonTest/`, `composeApp/src/desktopTest/` |
| Fakes            | `composeApp/src/commonTest/.../domain/gateway/Fake*.kt`     |

## Related Files

- `technical-domain.md` · `testing-strategy.md` · `code-patterns.md` · `business-tech-bridge.md` · `living-notes.md`
