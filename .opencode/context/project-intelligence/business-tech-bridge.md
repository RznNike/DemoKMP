<!-- Context: project-intelligence/bridge | Priority: high | Version: 1.3 | Updated: 2026-10-30 -->

# Business ↔ Tech Bridge

> Maps DemoKMP's goal (demonstrate KMP solutions) to concrete technical implementations.

## Quick Reference

- **Purpose**: connect the demo goal to what is actually built
- **Update When**: features added or removed

## Core Mapping

| Goal                                | Technical Solution                                          | Why                                           | Value                            |
|-------------------------------------|-------------------------------------------------------------|-----------------------------------------------|----------------------------------|
| Cross-platform code sharing         | Kotlin Multiplatform `commonMain` + `expect/actual`         | Single source of truth                        | Learn once, run on PC + Android  |
| Maintainable architecture           | Clean Architecture (app/domain/data)                        | Testable, decoupled                           | Patterns learners can copy       |
| Modern declarative UI               | Compose MP + Material 3 + MVVM                              | Shared UI across targets                      | One UI skill set                 |
| Realistic backend usage             | Ktor + Ktorfit HTTP & WebSocket demos                       | Real network integration                      | Ready-made reference             |
| Persistence demos                   | Room (SQL) + Multiplatform Settings                         | Show DB + prefs patterns                      | Copy-paste persistence           |
| Platform-specific power             | COM objects (Win), PDF, camera/barcode (Android)            | Show native interop                           | Beyond common code               |
| Verified, regression-safe solutions | `commonTest` + `desktopTest` + `androidUnitTest` with fakes | Every layer/ViewModel exercised automatically | Learners can trust the reference |

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

### Feature: Automated test suite

**Business Context**:
- User need: confidence that the reference actually works
- Business goal: make the demo trustworthy and regression-safe

**Technical Implementation**:
- Solution: `commonTest` (125 files / 107 test classes) with `kotlin.test` + `kotlinx-coroutines-test` + `ktor-mock`; `desktopTest` (15) for platform-bound tests; `androidUnitTest` (2) for Android-only ViewModels — 765 `@Test` cases
- Architecture: handwritten `Fake*` gateways in `domain/gateway`; `FakeDispatcherProvider`, `FakeCoroutineScopeProvider`, `FakeErrorHandler` for collaborators; `ViewModelTestRule` for `Dispatchers.setMain`
- Coverage: all UseCases, all ViewModels except `BarcodeScannerViewModel`, all gateway impls, Koin graph (`verify()`), network interceptors, DAOs, logger sinks, navigation, domain utils/models

**Connection**: turns the demo from "code that looks right" into "code that is verified" — the strongest teaching signal the project can send.

## Trade-off Decisions

| Situation                       | Business Priority     | Technical Priority         | Decision                                                  | Rationale                                         |
|---------------------------------|-----------------------|----------------------------|-----------------------------------------------------------|---------------------------------------------------|
| Demo clarity vs completeness    | Show many features    | Keep codebase small        | Many small isolated demo screens                          | Each solution stays standalone and copyable       |
| Demo simplicity vs test tooling | Minimize deps & setup | Maximize regression safety | `kotlin.test` + fakes + `ktor-mock`, no mocking framework | Both goals met; no heavy multiplatform test setup |
| Android coverage vs demo scope  | Keep demo light       | Cover platform VMs         | Host-JVM `androidUnitTest` for 2 VMs, no emulator tests   | Real value without instrumented-test overhead     |

## Common Misalignments

| Misalignment              | Warning Signs                           | Resolution                                |
|---------------------------|-----------------------------------------|-------------------------------------------|
| Over-engineering the demo | Production-grade abstractions appear    | Keep demos minimal and illustrative       |
| Untested "done"           | A feature is merged without a test file | Add a `Fake` + test alongside the feature |

## Onboarding Checklist

- [ ] See how each demo maps to a learning goal
- [ ] Understand the key trade-off (breadth vs simplicity)
- [ ] Know how to add a test for a new feature

## 📂 Codebase References

| Context    | Code                                                                                                                                                                                   |
|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP flow  | `data/network/AppApi.kt`, `data/gateway/HTTPExampleGatewayImpl.kt`, `domain/interactor/httpexample/GetRandomImageLinksUseCase.kt`, `app/viewmodel/httpexample/HTTPExampleViewModel.kt` |
| Logger     | `domain/log/**`                                                                                                                                                                        |
| Screens    | `app/viewmodel/**`, `app/ui/**`                                                                                                                                                        |
| Test suite | `composeApp/src/commonTest/`, `desktopTest/`, `androidUnitTest/`                                                                                                                       |

## Related Files

- `business-domain.md` · `technical-domain.md` · `testing-strategy.md` · `code-patterns.md` · `decisions-log.md` · `living-notes.md`
