<!-- Context: project-intelligence/notes | Priority: medium | Version: 1.3 | Updated: 2026-10-30 -->

# Living Notes

> Active state for DemoKMP: tech debt, open questions, gotchas, and patterns worth preserving.

## Quick Reference

- **Purpose**: current state and open items
- **Update**: when status changes

---

## Technical Debt

| Item                            | Impact                                                                                  | Priority | Mitigation                                                   |
|---------------------------------|-----------------------------------------------------------------------------------------|----------|--------------------------------------------------------------|
| Android coverage is thin        | Only 2 Android ViewModels are tested on the host JVM; camera/MLKit/permissions untested | Medium   | Grow `androidUnitTest`; keep framework types out of VMs      |
| No instrumented / UI tests      | UI regressions caught only by manual QA                                                 | Low      | Add `androidTest` with Compose UI tests, or accept manual QA |

## Open Questions

| Question                                   | Stakeholders | Status | Next Action                                      |
|--------------------------------------------|--------------|--------|--------------------------------------------------|
| Expand `androidUnitTest` beyond 2 classes? | Maintainer   | Open   | Decide scope for a demo project                  |
| Test `BarcodeScannerViewModel`?            | Maintainer   | Open   | Extract camera/MLKit behind a gateway, then test |

## Known Issues

| Issue                               | Severity | Workaround                             | Status                       |
|-------------------------------------|----------|----------------------------------------|------------------------------|
| Desktop builds are OS-specific      | Low      | Build on target OS / VM                | Known (by design)            |
| Fakes drift from gateway interfaces | Low      | Update fakes when an interface changes | Known (no mock regeneration) |
| `BarcodeScannerViewModel` untested  | Low      | Manual QA on device                    | Known                        |

## Insights & Lessons Learned

### What Works Well
- Clean Architecture + Koin keeps features isolated and copyable
- `expect/actual` cleanly separates platform code
- `UseCaseResult` keeps error handling explicit
- Splitting tests into `commonTest` + `desktopTest` + `androidUnitTest` keeps almost all tests target-independent
- `ViewModelTestRule` hides `Dispatchers.setMain`/`resetMain` and shares one scheduler via `runTest(rule.dispatcher)`
- Koin `verify()` catches missing constructor bindings without instantiating anything
- Handwritten fakes make test state inspectable and avoid a mocking-framework dependency

### Gotchas for Maintainers
- `expect`/`actual` pairs must be updated together across source sets
- KSP codegen (Room/Ktorfit) must run before compile; clean build if types are stale
- `BaseUiViewModel.setProgress` debounces via `viewModelScope.launch { delay(progressDelayMs) }` — virtual time
  controls it, so VM tests must call `Dispatchers.setMain(TestDispatcher)`; set `progressDelayMs = 0` or use
  `setProgress(..., immediately = true)` to skip the debounce
- `viewModelScope` hardcodes `Dispatchers.Main`: without `setMain` a VM's `init` coroutines run on
  the Swing EDT (`desktopTest`) and throw on the Android host JVM (`testDebugUnitTest`)
- `androidUnitTest` relies on `isReturnDefaultValues = true`; without it any Android framework call throws
- `Logger` must be initialized (or `reset`) before use; messages go through one `Channel` consumer, so tests
  must assert after that consumer drains (see `LoggerTest`)
- Koin `verify()` only sees included modules — wrap `appComponent` in an aggregate `module { includes(...) }`
- Fakes must be updated by hand whenever a gateway/`ErrorHandler` interface changes — nothing regenerates them
- `commonTest` cannot touch JVM/desktop-only APIs; such tests belong in `desktopTest` (Android-only: `androidUnitTest`)

## Patterns & Conventions

### Code Patterns Worth Preserving
- Gateway interface (domain) + `Impl` (data) separation
- `ErrorHandler` interface in the app layer (`ErrorHandlerImpl` in `app/error`)
- One Koin module per layer/concern
- Immutable `@Immutable` UiState data classes
- Room entity ↔ domain model extension mappers
- Test class named `<ClassUnderTest>Test`, in the same package as the production class
- ViewModel tests through `ViewModelTestRule`, sharing its dispatcher with `runTest`

## Onboarding Checklist

- [ ] Review tech debt and its impact
- [ ] Know open questions
- [ ] Be aware of gotchas
- [ ] Know where tests live and how fakes are written

## 📂 Codebase References

| Item    | Code                                                                                         |
|---------|----------------------------------------------------------------------------------------------|
| Signing | `composeApp/build.gradle.kts` (`signingConfigs`), `demokmp.jks`                              |
| Version | `composeApp/build.gradle.kts` (`globalVersionName`/`globalVersionCode`)                      |
| DI      | `app/di/*.kt`                                                                                |
| UI base | `app/common/viewmodel/BaseUiViewModel.kt`                                                    |
| Errors  | `domain/common/interactor/UseCaseResult.kt`, `app/error/*.kt`                                |
| Tests   | `composeApp/src/commonTest/`, `desktopTest/`, `androidUnitTest/`                             |
| VM rule | `composeApp/src/commonTest/.../app/common/viewmodel/ViewModelTestRule.kt`                    |
| Fakes   | `composeApp/src/commonTest/.../domain/gateway/Fake*.kt`, `.../app/error/FakeErrorHandler.kt` |

## Related Files

- `decisions-log.md` · `technical-domain.md` · `testing-strategy.md` · `business-domain.md`
