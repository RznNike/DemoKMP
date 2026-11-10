<!-- Context: project-intelligence/decisions | Priority: medium | Version: 1.4 | Updated: 2026-11-09 -->

# Decisions Log

> Architectural decisions for DemoKMP with context. Format: Context → Decision → Rationale → Alternatives → Impact.

## Quick Reference

- **Purpose**: why key choices were made
- **Status**: Decided | Pending | Deprecated

## Decision: Kotlin Multiplatform for shared code

**Date**: 2026-10-15 · **Status**: Decided
- **Context**: One codebase for PC (Windows/Linux JVM) and Android.
- **Decision**: Kotlin Multiplatform (`commonMain` + `androidMain` + `desktopMain`).
- **Rationale**: Maximizes shared Kotlin while allowing platform code via `expect/actual`.
- **Alternatives**: Separate native apps (2x code, drift); Flutter (Dart, weaker JVM interop).
- **Impact**: ✅ One codebase · ⚠️ `expect/actual` boilerplate, platform divergence risk.

## Decision: Compose Multiplatform for UI

**Date**: 2026-10-15 · **Status**: Decided
- **Context**: UI must run on Desktop and Android without duplicate view code.
- **Decision**: Compose Multiplatform with Material 3.
- **Rationale**: Single declarative UI layer; familiar Compose skills.
- **Alternatives**: Native UI per platform (2x work); Compose + Swing (two toolkits).
- **Impact**: ✅ Shared UI · ⚠️ Some desktop-specific caveats.

## Decision: Clean Architecture layering (app/domain/data)

**Date**: 2026-10-15 · **Status**: Decided
- **Context**: The demo must show a maintainable, testable structure.
- **Decision**: Three layers with dependency rule `app → domain ← data`.
- **Rationale**: Decouples business rules from frameworks; makes the pattern copyable.
- **Alternatives**: Flat feature-by-feature structure — mixes concerns.
- **Impact**: ✅ Clear boundaries · ⚠️ More files/interfaces.

## Decision: Koin for dependency injection

**Date**: 2026-10-15 · **Status**: Decided
- **Context**: Need DI that works across Android and Desktop.
- **Decision**: Koin, one module per concern.
- **Rationale**: Multiplatform-friendly, no codegen, simple DSL.
- **Alternatives**: Manual DI (boilerplate); Kodein (extra learning curve).
- **Impact**: ✅ Simple wiring · ⚠️ Runtime (not compile-time) resolution.

## Decision: Room for local persistence

**Date**: 2026-10-15 · **Status**: Decided
- **Context**: Need cross-platform SQL storage with a familiar API.
- **Decision**: Room (KSP) + bundled SQLite.
- **Rationale**: KMP support, compile-time-verified queries, Android familiarity.
- **Alternatives**: SQLDelight (different DSL); raw SQLite (manual, unsafe).
- **Impact**: ✅ Typed queries · ⚠️ KSP codegen step before compile.

## Decision: Errors-as-values via UseCaseResult

**Date**: 2026-10-15 · **Status**: Decided
- **Context**: Error handling must be consistent and testable across layers.
- **Decision**: UseCases return `UseCaseResult<R>` (data or error), not thrown exceptions.
- **Rationale**: Explicit; no hidden control flow across boundaries; easy to map to UI.
- **Alternatives**: Exceptions (implicit, easy to leak); Kotlin `Result` (less domain context).
- **Impact**: ✅ Explicit errors · ⚠️ Wrapper at each use site.

## Decision: Shared test source set with kotlin.test

**Date**: 2026-10-16 · **Status**: Decided
- **Context**: Regression safety without a heavy multiplatform test framework.
- **Decision**: `commonTest` with `kotlin("test")` + `kotlinx-coroutines-test`; platform-bound tests in `desktopTest` / `androidUnitTest`.
- **Rationale**: Platform-independent tests run on every target; `kotlin.test` keeps dependencies minimal.
- **Alternatives**: Per-target duplicated test sets (drift); Kotest/MockK (heavier setup).
- **Impact**: ✅ 867 source-level tests over 3 source sets · ⚠️ Android platform code remains thin.

## Decision: Handwritten fakes over a mocking framework

**Date**: 2026-10-16 · **Status**: Decided
- **Context**: Gateways are interfaces in `domain`, so doubles must implement them.
- **Decision**: Handwritten `Fake*` classes in `commonTest`, mirroring the production package. No mocking framework.
- **Rationale**: Minimal dependencies, inspectable state, identical behavior on every target.
- **Alternatives**: MockK / Turbine (multiplatform setup cost, harder-to-read expectations).
- **Impact**: ✅ Explicit test state · ⚠️ Fakes must be updated when interfaces change.

## Decision: ViewModel tests via `Dispatchers.setMain` + shared rule

**Date**: 2026-10-29 · **Status**: Decided
- **Context**: `viewModelScope` hardcodes `Dispatchers.Main`; without replacement VM `init` runs on the Swing EDT or throws on the Android host JVM.
- **Decision**: Test through `ViewModelTestRule`, which calls `Dispatchers.setMain`/`resetMain` and hands out `FakeDispatcherProvider` + `FakeCoroutineScopeProvider` on one `TestDispatcher`.
- **Rationale**: No production refactor; virtual time controls the 250 ms progress debounce; one scheduler shared with `runTest`.
- **Alternatives**: Inject a scope into every ViewModel (invasive); poll wall-clock time (flaky).
- **Impact**: ✅ All ViewModels testable unchanged · ⚠️ Tests must use `runTest(rule.dispatcher)` and pair `start()`/`stop()`.

## Decision: `ErrorHandler` as an interface

**Date**: 2026-10-29 · **Status**: Decided
- **Context**: ViewModels needed a substitutable error-mapping collaborator for tests.
- **Decision**: Extract `ErrorHandler` (interface, `suspend fun proceed`) and `ErrorHandlerImpl` in `app/error`; bind the impl in Koin.
- **Rationale**: Tests inject `FakeErrorHandler`; ViewModels depend on an abstraction.
- **Alternatives**: Keep a concrete class — forces real error mapping in every VM test.
- **Impact**: ✅ Testable error paths · ⚠️ One extra binding to keep registered.

## Decision: Logger serialized through one consumer channel

**Date**: 2026-10-26 · **Status**: Decided
- **Context**: Concurrent logging could interleave sink writes and produce inconsistent timestamps.
- **Decision**: `Logger` captures one timestamp per message and enqueues the write on a single-consumer `Channel`; extensions receive `timestamp`.
- **Rationale**: Ordered, non-interleaved output without per-call semaphore locking.
- **Alternatives**: Semaphore around each extension call — replaced; allowed interleaving.
- **Impact**: ✅ Ordered logs, one timestamp · ⚠️ `Logger` must be initialized (`reset()` in tests) and assertions must wait for the queue to drain.

## Decision: `androidUnitTest` for Android-only code

**Date**: 2026-10-29 · **Status**: Decided (extended 2026-11-05)
- **Context**: Android-only ViewModels and framework code cannot run in `commonTest`/`desktopTest`.
- **Decision**: `androidUnitTest` on the host JVM with `isReturnDefaultValues = true`; reuse `ViewModelTestRule`; later added Robolectric for real framework/resource behavior.
- **Rationale**: Covers Android logic without an emulator while keeping `commonTest` platform-independent.
- **Alternatives**: Instrumented tests only (slow, device-bound); leave Android untested.
- **Impact**: ✅ 5 classes / 12 tests (2 ViewModels, Koin runtime, screen registration, Compose host) · ⚠️ No `androidTest`; camera/MLKit/permissions still untested.

## Decision: Kover for coverage measurement

**Date**: 2026-10-31 · **Status**: Decided
- **Context**: No coverage signal existed; test growth needed a baseline.
- **Decision**: Add Kover 0.9.11, merged `desktopDebug` variant (`desktop` + `debug`), advisory `minValue = 70` LINE, exclude generated code.
- **Rationale**: First-class Kotlin coverage; merged variant covers both targets in one report.
- **Alternatives**: JaCoCo (weaker KMP support); no measurement.
- **Impact**: ✅ Baseline 32.84 % line, HTML/XML reports · ⚠️ Advisory only; per-layer tiers deferred; `dev` source set must stay excluded.

## Decision: Robolectric for Android host tests

**Date**: 2026-11-05 · **Status**: Decided
- **Context**: Koin Android modules and Compose needed real framework behavior without a device.
- **Decision**: `robolectric:4.17` + JUnit4 + Espresso in `androidUnitTest`; `isIncludeAndroidResources = true`; test JVM on Java 21; `@Config(application = Application::class)` to avoid global Koin startup.
- **Rationale**: Real `SharedPreferences`/Room round-trips and a Compose host at host-JVM speed.
- **Alternatives**: Instrumented tests (device-bound); pure fakes only (cannot prove framework wiring).
- **Impact**: ✅ Android runtime coverage in CI-friendly tests · ⚠️ ~9 s per Compose class; Java 21 test JVM required.

## Decision: Compose UI smoke tests on the desktop host

**Date**: 2026-11-06 · **Status**: Decided
- **Context**: The UI layer had zero meaningful tests; full screen composition needs global Koin and a window host.
- **Decision**: Ship 5 component-level smoke classes in `desktopTest` using `createComposeRule()` (`junit4.v2`) and the DI-free `PreviewDesktopAppTheme`. No screen-level composition.
- **Rationale**: Cheap (~2–5 s total), hermetic, and proves the UI-test infra works without faking the harness.
- **Alternatives**: Full screen tests (would prove the harness, not the UI); Robolectric mirrors (~9 s each, no extra signal).
- **Impact**: ✅ 15 UI tests · ⚠️ Screen composition and visual styling remain accepted gaps.

## Decision: No CI pipeline and no instrumented source set (for now)

**Date**: 2026-10-30 · **Status**: Decided
- **Context**: The project is a local-only reference demo; a CI runner and device workflow add overhead.
- **Decision**: Tests run locally; no `androidTest`/`androidInstrumentedTest` source set.
- **Rationale**: Keeps the demo light; host-JVM tests already cover the valuable non-device logic.
- **Alternatives**: GitHub Actions + emulator matrix.
- **Impact**: ✅ Minimal setup · ⚠️ Regressions are not enforced on change; camera/MLKit/permissions remain untested. Revisit gate: a local device workflow.

---

## Deprecated Decisions

| Decision   | Date | Replaced By | Why |
|------------|------|-------------|-----|
| (none yet) | —    | —           | —   |

## Onboarding Checklist

- [ ] Understand why KMP, Compose, Clean Arch, Koin, Room were chosen
- [ ] Understand the errors-as-values choice and the `ErrorHandler` interface split
- [ ] Understand the test source-set split, `ViewModelTestRule`, and the fakes-over-mocks convention
- [ ] Understand the coverage stack (Kover + Robolectric + Compose UI host) and its accepted gaps

## 📂 Codebase References

| Decision         | Code                                                                     |
|------------------|--------------------------------------------------------------------------|
| Layering         | `app/`, `domain/`, `data/`                                               |
| DI               | `app/di/*.kt`                                                            |
| DB               | `data/storage/`                                                          |
| Network          | `data/network/`                                                          |
| Errors           | `domain/common/interactor/UseCaseResult.kt`, `app/error/ErrorHandler.kt` |
| Logging          | `domain/log/Logger.kt`                                                   |
| Test source sets | `commonTest/`, `desktopTest/`, `androidUnitTest/`                        |
| Fakes            | `composeApp/src/commonTest/.../domain/gateway/Fake*.kt`                  |
| Coverage         | `composeApp/build.gradle.kts` (`kover`)                                  |

## Related Files

- `technical-domain.md` · `testing-strategy.md` · `code-patterns.md` · `business-tech-bridge.md` · `living-notes.md`
