<!-- Context: project-intelligence/notes | Priority: medium | Version: 1.4 | Updated: 2026-11-09 -->

# Living Notes

> Active state for DemoKMP: tech debt, open questions, gotchas, and patterns worth preserving.

## Quick Reference

- **Purpose**: current state and open items
- **Update**: when status changes

---

## Technical Debt

| Item                            | Impact                                                                                  | Priority | Mitigation                                                   |
|---------------------------------|-----------------------------------------------------------------------------------------|----------|--------------------------------------------------------------|
| Android coverage is thin        | 5 host-JVM classes; camera/MLKit/permissions/device Room untested                       | Medium   | Grow `androidUnitTest`; add instrumented tests only if device workflow appears |
| No instrumented / UI-device tests | Camera, ML Kit, permissions and on-device Room regressions caught only by manual QA     | Low      | Add `androidTest` with Compose UI tests, or accept manual QA |
| Screen-level UI unverified      | 5 component smoke classes only; screens need global Koin + window host                  | Low      | Accept as gap or build a hermetic screen harness (high cost)  |
| Kover bound is advisory         | 70 % LINE floor with `warningInsteadOfFailure = true`; no per-layer tiers               | Low      | Tighten in Phase 8 once a stable baseline exists              |
| `PowerShellWrapper` unverified  | CPU-arch mapping + RAM arithmetic have no direct test (Phase 1.1 rejected)              | Low      | Test through the `PCShell` seam if it becomes risky           |
| No CI                           | `desktopTest` / `testDebugUnitTest` are not enforced on change                          | Medium   | Add a local pre-push task or CI when the project leaves demo scope |

## Open Questions

| Question                                      | Stakeholders | Status | Next Action                                      |
|-----------------------------------------------|--------------|--------|--------------------------------------------------|
| Start Phase 4 Android hardening?              | Maintainer   | Open   | `ActivityUtils`, `PermissionHandler`, `IntentUtils`, Android utils |
| Add an instrumented source set?               | Maintainer   | Open   | Decide whether a local device/emulator workflow is acceptable |
| Tighten Kover to per-layer tiers?             | Maintainer   | Open   | Phase 8 after the baseline is re-measured        |
| Add a CI pipeline?                            | Maintainer   | Open   | Requires resolving the signing-property configuration check |

## Known Issues

| Issue                                    | Severity | Workaround                                      | Status                       |
|------------------------------------------|----------|-------------------------------------------------|------------------------------|
| Desktop builds are OS-specific           | Low      | Build on target OS / VM                         | Known (by design)            |
| Fakes drift from gateway interfaces      | Low      | Update fakes when an interface changes          | Known (no mock regeneration) |
| `BarcodeScannerViewModel` untested       | Low      | Manual QA on device                             | Known                        |
| Both test tasks in one Gradle call OOM   | Medium   | Run `desktopTest` and `testDebugUnitTest` separately with `--max-workers=1` | Known |
| Signing props required at configuration  | Medium   | Pass `-PPROJECT_KEY_PASSWORD` + `-PPROJECT_KEYSTORE_PASSWORD` to non-interactive invocations | Known |
| Filtered test run replaces results dir   | Low      | Recount only after an unfiltered run            | Known (per-task results)     |
| Robolectric Compose class costs ~9 s     | Low      | Prefer `desktopTest` unless Android runtime is required | Known (by design)    |
| Compose 1.12.2 UI-test DSL deprecations  | Low      | Non-fatal; migrate to explicit catalog coordinates later | Known               |
| Kover fails on dormant `dev` source set  | Low      | `excludedSourceSets.add("dev")` is required     | Known (0.9.11 workaround)    |

## Insights & Lessons Learned

### What Works Well
- Clean Architecture + Koin keeps features isolated and copyable
- `expect/actual` cleanly separates platform code
- `UseCaseResult` keeps error handling explicit
- Splitting tests into `commonTest` + `desktopTest` + `androidUnitTest` keeps almost all tests target-independent
- `ViewModelTestRule` hides `Dispatchers.setMain`/`resetMain` and shares one scheduler via `runTest(rule.dispatcher)`
- Koin `verify()` catches missing constructor bindings; runtime `koinApplication` tests catch lambda-resolved bindings
- Handwritten fakes make test state inspectable and avoid a mocking-framework dependency
- Robolectric unlocks real Android `SharedPreferences`/Room + Compose host tests without a device
- Kover's merged `desktopDebug` variant gives one coverage number for both targets

### Gotchas for Maintainers
- `expect`/`actual` pairs must be updated together across source sets
- KSP codegen (Room/Ktorfit) must run before compile; clean build if types are stale
- `BaseUiViewModel.setProgress` debounces via `viewModelScope.launch { delay(progressDelayMs) }` — virtual time
  controls it, so VM tests must call `Dispatchers.setMain(TestDispatcher)`; set `progressDelayMs = 0` or use
  `setProgress(..., immediately = true)` to skip the debounce
- `viewModelScope` hardcodes `Dispatchers.Main`: without `setMain` a VM's `init` coroutines run on
  the Swing EDT (`desktopTest`) and throw on the Android host JVM (`testDebugUnitTest`)
- `androidUnitTest` relies on `isReturnDefaultValues = true`; Robolectric Compose also needs
  `isIncludeAndroidResources = true`, `@GraphicsMode(NATIVE)` and a Java 21 test JVM
- Use `@Config(application = Application::class)` on Robolectric classes — otherwise `.app.App.onCreate()` starts global Koin
- Compose UI tests use `createComposeRule()` from `junit4.v2` and `PreviewDesktopAppTheme`; `DesktopAppTheme` needs global Koin
- `stringResource` renders `""` on the first frame — assert non-resource text or semantics
- `Logger` must be initialized (or `reset`) before use; messages go through one `Channel` consumer, so tests
  must assert after that consumer drains (see `LoggerTest`)
- Koin `verify()` only sees included modules — wrap `appComponent` in an aggregate `module { includes(...) }`
- Fakes must be updated by hand whenever a gateway/`ErrorHandler` interface changes — nothing regenerates them
- `commonTest` cannot touch JVM/desktop-only APIs; such tests belong in `desktopTest` (Android-only: `androidUnitTest`)
- Running both test tasks in one invocation forks ~115 JVMs and can OOM; run them separately
- A `--tests`-filtered run replaces `build/test-results/<task>/`; totals are only valid after an unfiltered run

## Patterns & Conventions

### Code Patterns Worth Preserving
- Gateway interface (domain) + `Impl` (data) separation
- `ErrorHandler` interface in the app layer (`ErrorHandlerImpl` in `app/error`)
- One Koin module per layer/concern
- Immutable `@Immutable` UiState data classes
- Room entity ↔ domain model extension mappers
- `internal` pure-function seams for platform logic (`calculateMinimumSize`, `resolveTheme`, `formatMacAddress`, `TwoSidedPrint.toSides`)
- Test class named `<ClassUnderTest>Test`, in the same package as the production class
- ViewModel tests through `ViewModelTestRule`, sharing its dispatcher with `runTest`
- UI smoke tests: desktop host by default, Robolectric only for Android runtime assertions

## Onboarding Checklist

- [ ] Review tech debt and its impact
- [ ] Know open questions
- [ ] Be aware of gotchas
- [ ] Know where tests live and how fakes are written
- [ ] Know the coverage stack and how to run it

## 📂 Codebase References

| Item     | Code                                                                                         |
|----------|----------------------------------------------------------------------------------------------|
| Signing  | `composeApp/build.gradle.kts` (`signingConfigs`), `demokmp.jks`                              |
| Version  | `composeApp/build.gradle.kts` (`globalVersionName`/`globalVersionCode`)                      |
| DI       | `app/di/*.kt`                                                                                |
| UI base  | `app/common/viewmodel/BaseUiViewModel.kt`                                                    |
| Errors   | `domain/common/interactor/UseCaseResult.kt`, `app/error/*.kt`                                |
| Tests    | `composeApp/src/commonTest/`, `desktopTest/`, `androidUnitTest/`                             |
| VM rule  | `composeApp/src/commonTest/.../app/common/viewmodel/ViewModelTestRule.kt`                    |
| Fakes    | `composeApp/src/commonTest/.../domain/gateway/Fake*.kt`, `.../data/shell/FakeShellManager.kt` |
| Coverage | `composeApp/build.gradle.kts` (`kover`)                                                      |

## Related Files

- `decisions-log.md` · `technical-domain.md` · `testing-strategy.md` · `business-domain.md`
