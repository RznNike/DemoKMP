<!-- Context: project-intelligence/testing-strategy | Priority: high | Version: 2.1 | Updated: 2026-11-09 -->

# Testing Strategy

> How DemoKMP is tested: source sets, test stack, shared rules, UI hosts, coverage, and the fakes-over-mocks convention.

## Quick Reference

- **Purpose**: explain the test layout so contributors can add tests correctly
- **Update When**: test stack, source-set layout, UI host, or coverage tooling changes
- **Audience**: Contributors, AI agents writing tests

---

## Test Stack

| Dependency                    | Purpose                                             |
|-------------------------------|-----------------------------------------------------|
| `kotlin("test")`              | Assertions and test annotations (common)            |
| `kotlinx-coroutines-test`     | Coroutine test control (`setMain`, `runTest`, time) |
| `multiplatform-settings-test` | In-memory `Settings` for preferences tests          |
| `ktor-mock`                   | `MockEngine` for HTTP/interceptor tests             |
| `koin-test`                   | DI graph validation (`verify()` — JVM-only API)     |
| Compose UI test (`junit4` v2)| `createComposeRule()` host (desktop + Android)      |
| `robolectric` 4.17            | Android framework on the host JVM                   |
| `junit` 4.13.2 + `espresso` 3.7.0 | JUnit4 runner + Android UI assertions          |
| `kover` 0.9.11                | Line/branch coverage reports                        |

Declared in `composeApp/build.gradle.kts` → `commonTest`, `desktopTest`, `androidUnitTest`.

## Source Sets

| Source Set        | Files | Classes | Tests | Contents                                                              |
|-------------------|-------|---------|-------|-----------------------------------------------------------------------|
| `commonTest`      | 130   | 111     | 679   | Platform-independent tests, all fakes, shared rules, UI-test API      |
| `desktopTest`     | 30    | 30      | 176   | Desktop-bound tests + Compose UI smoke tests                          |
| `androidUnitTest` | 5     | 5       | 12    | Host-JVM Android tests (Robolectric, `isReturnDefaultValues = true`)  |
| `androidTest`     | —     | —       | —     | **Not present** — no instrumented/UI-device tests                     |

**Source-level total: 867 `@Test` cases** (counted 2026-11-09; files containing `@Test`, excluding helpers).

**Executed totals** (unfiltered, `TEST-*.xml`): `desktopTest` = **141 suites / 855 tests / 0 failures**; `testDebugUnitTest` = **116 suites / 691 tests / 0 failures**. The difference from source-level counts is that each task also runs `commonTest`.

**Why the split**: tests touching JVM/desktop-only APIs (COM, shell, Room on desktop, log formatting) cannot live in `commonTest`; Android-only code cannot run on the desktop host. Everything else stays in `commonTest` so it runs on every target.

## Convention: Fakes, Not Mocks

No mocking framework. Test doubles are handwritten classes in `commonTest`, placed in the **same package as the production type** so they can implement/extend it directly.

| Location                                              | Contents                                                                                |
|-------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `domain/gateway/Fake*.kt`                             | 10 gateway fakes: `FakeHTTPExampleGateway`, `FakeLogGateway`, `FakeDBExampleGateway`, … |
| `data/shell/FakeShellManager.kt`                      | Substitutes the `ShellManager` interface (desktop + Android)                            |
| `domain/common/FakeDispatcherProvider.kt`             | Substitutes `DispatcherProvider`                                                        |
| `domain/common/FakeCoroutineScopeProvider.kt`         | Substitutes `CoroutineScopeProvider` for `Notifier` / `EventDispatcher`                 |
| `app/error/FakeErrorHandler.kt`                       | Substitutes the `ErrorHandler` interface                                                |
| `data/network/interceptor/StubResponseInterceptor.kt` | Canned HTTP responses for interceptor tests                                             |

Helpers: `MutableClock` (deterministic time), `NavigationTestFixtures`, `NetworkLogMessageFixture`, `ViewModelTestRule`.

**Fake shape** — expose collaborators as mutable state, record calls, allow failure injection:

```kotlin
class FakeHTTPExampleGateway : HTTPExampleGateway {
    var errorToThrow: Exception? = null
    val requestedCounts = mutableListOf<Int>()
    var links: List<String> = emptyList()
    // ...
}
```

**FakeDispatcherProvider** defaults to `Dispatchers.Unconfined`, but **do not** use that default in ViewModel tests: a real `Unconfined` leaks `withContext(...)` off the test scheduler. Pass a `TestDispatcher` (the rule does this for you).

## ViewModel Tests — `ViewModelTestRule`

`viewModelScope` hardcodes `Dispatchers.Main`, so it cannot be replaced by injection. Without `Dispatchers.setMain`, a ViewModel's `init` coroutines run on the Swing EDT under `desktopTest` and **throw** under `testDebugUnitTest` (no `Main` at all). The shared rule hides both:

```kotlin
// app/common/viewmodel/ViewModelTestRule.kt (commonTest)
class ViewModelTestRule(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) {
    val dispatcherProvider = FakeDispatcherProvider(dispatcher)
    val coroutineScopeProvider = FakeCoroutineScopeProvider(dispatcher)
    fun start() = Dispatchers.setMain(dispatcher)
    fun stop() = Dispatchers.resetMain()
}
```

```kotlin
private val rule = ViewModelTestRule()

@BeforeTest fun setUp() { rule.start(); startKoin { modules(module { /* … */ }) } }
@AfterTest fun tearDown() { stopKoin(); rule.stop() }

@Test
fun `state is loaded`() = runTest(rule.dispatcher) {   // ← must share the rule's scheduler
    val viewModel = MyViewModel()
    advanceUntilIdle()
    assertEquals(expected, viewModel.uiState.value)
}
```

- `UnconfinedTestDispatcher` (default) runs VM `init` eagerly — right for "state after construction".
- Use `StandardTestDispatcher` when a test must observe an *intermediate* state (e.g. `isLoading`).
- `BaseUiViewModel.progressDelayMs` is an `open var`: set it to `0` in a test to bypass the 250 ms debounce, or `setProgress(show, immediately = true)`.
- Never poll the wall clock (`Thread.sleep`, `LinkedBlockingQueue.poll`) — advance virtual time.
- Reference implementations: `SettingsViewModelTest`, `BaseUiViewModelTest`.

## Android Host Tests — Robolectric

`androidUnitTest` runs Android framework code on the host JVM. Five classes:

| Class | Scope |
|---|---|
| `KoinModuleAndroidTest` | Real `PreferenceModule.android` + `DatabaseModule.android` runtime resolution |
| `ScreenKeyListTest` | Android `screenKeyList` registration |
| `ComposeUiHostSmokeTest` | Compose render + click + recompose on the Android host |
| `ActivityViewModelTest` | Android-only ViewModel |
| `BarcodeExampleViewModelTest` | Android-only ViewModel |

- `@RunWith(RobolectricTestRunner::class)`, `@Config(sdk = [37], application = Application::class)`, `@GraphicsMode(GraphicsMode.Mode.NATIVE)` for Compose.
- `application = Application::class` avoids instantiating `.app.App`, whose `onCreate()` starts global Koin.
- `testOptions.unitTests.isIncludeAndroidResources = true` + `isReturnDefaultValues = true`.
- Test JVM runs on **Java 21** (Chucker class-file 65 + Robolectric manifest receivers + API 37); compilation stays on JVM 17.
- Cost: ~9 s per Compose class. Prefer `desktopTest` unless the assertion needs the Android runtime.

## Compose UI Tests

- **Desktop** (`desktopTest`): 5 smoke classes / 15 tests — `BottomStatusBarTest`, `AppClosingPlaceholderTest`, `CommonAlertDialogTest`, `DropdownSelectorTest`, `LogMessageItemTest`.
- Rule: `createComposeRule()` from `androidx.compose.ui.test.junit4.v2` (composition on a queued `StandardTestDispatcher`).
- Theme: `PreviewDesktopAppTheme` (DI-free). `DesktopAppTheme` / `AppTheme` need global Koin and are avoided.
- Assert only non-resource text or semantics-observable behavior; `stringResource` is empty on the first frame.
- Dialog / popup roots are reachable in the desktop host; `mainClock.autoAdvance = false` + `advanceTimeBy(...)` gives deterministic timing.
- **Accepted gap**: screen-level composition is unverified — screens need `LocalWindow`, `ViewModelStoreOwner`, `getNavigator()` and global Koin.

## Coverage — Kover

- Plugin `org.jetbrains.kotlinx.kover` **0.9.11**; merged variant `desktopDebug` = `desktop` + `debug`.
- Reports: `:composeApp:koverHtmlReportDesktopDebug`, `:composeApp:koverXmlReportDesktopDebug`.
- Verify: `:composeApp:koverVerifyDesktopDebug` — advisory (`warningInsteadOfFailure = true`), `minValue = 70` LINE.
- **Baseline (2026-10-31): 32.84 % line**.
- Excluded: Room/KSP (`*_Impl`, `*_Factory`, `*Dao_Impl*`, `*Database_Impl*`), BuildKonfig, generated Compose resources, Compose singletons/lambdas, `@Preview`.
- `dev` source set is excluded — it does not depend on `commonMain`, so BuildKonfig's `actual` has no visible `expect` (Kover would fail on `ACTUAL_WITHOUT_EXPECT`).

## Coverage Map

| Layer                                          | Tested                                                                                                                            |
|------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| UseCases (all feature folders)                 | ✅ 48 / 48 `domain/interactor/**`                                                                                                 |
| UseCase bases / `UseCaseResult`                | ✅ 5 / 5 `domain/common/interactor/**`                                                                                            |
| ViewModels                                     | ✅ every ViewModel except `BarcodeScannerViewModel` (13 common, 5 desktop, 2 Android)                                            |
| DI graph                                       | ✅ `AppComponentTest` (`verify()`), `KoinModuleTest`, `KoinModuleAndroidTest`, `NetworkModuleTest`, `DispatcherProviderTest`      |
| Gateways (data)                                | ✅ 10 impls (7 `commonTest` + 3 `desktopTest`)                                                                                    |
| Network                                        | ✅ `AppApi`, `AppWebSocketManagerImpl`, 4 interceptors, `CustomServerException`                                                   |
| Logger                                         | ✅ `Logger`, 4 sinks + base, `LogMessage`, `NetworkLogMessage`, not-initialized path                                              |
| Storage                                        | ✅ entities (3), DAOs (3), `MainDB`                                                                                               |
| Preferences                                    | ✅ `Preference`, `PreferencesManager`, 10 `Get/Set*UseCase`                                                                       |
| App layer                                      | ✅ `ErrorHandler`, `EventDispatcher`, `Notifier`, `FlowNavigator`, `NavigationFlowInfo`, `DateInputManager`, `ClicksFilter`       |
| Data/domain utils & models                     | ✅ `HttpUtils`, `JsonUtils`, `UUIDSerializer`, `CollectionUtils`, `DateUtils`, `StringUtils`, `OperatingSystem`, `LauncherConfig` |
| Desktop platform                               | ✅ shell/COM, `WindowUtils`, `Theme`, `PlatformUtils`, `FileDialogUtils`, `ClipboardUtils`, `WindowViewModel`                     |
| UI components                                  | ✅ 5 Compose smoke classes (desktop); Android host smoke                                                            |
| Android camera/MLKit/permissions               | ❌ beyond the 2 Android ViewModel tests                                                                                          |
| Screen-level composition                       | ❌ accepted gap (needs global Koin + window host)                                                                                |

## Conventions

- Test name: `<ClassUnderTest>Test`, mirroring the production file name.
- Test package: identical to the production package.
- Arrange → Act → Assert, with the **Arrange** block first.
- Error branches are exercised via the fake's `errorToThrow`, never via mocking.
- Platform-bound tests go in `desktopTest`; Android-bound tests in `androidUnitTest`; everything else in `commonTest`.
- Compose UI tests default to `desktopTest`; mirror to `androidUnitTest` only when the assertion needs the Android runtime.

## Commands & Gotchas

```bash
./gradlew :composeApp:desktopTest -PPROJECT_KEY_PASSWORD=test -PPROJECT_KEYSTORE_PASSWORD=test
./gradlew :composeApp:testDebugUnitTest -PPROJECT_KEY_PASSWORD=test -PPROJECT_KEYSTORE_PASSWORD=test
./gradlew :composeApp:koverHtmlReportDesktopDebug -PPROJECT_KEY_PASSWORD=test -PPROJECT_KEYSTORE_PASSWORD=test
```

- Run the two test tasks **separately** with `--max-workers=1`; running both in one invocation forks ~115 JVMs and can OOM (`mmap failed`).
- Signing properties are required at configuration time — non-interactive tasks fail without them.
- Results are per-task and **replaced** by a `--tests`-filtered run; recount only after an unfiltered run.
- `BUILD SUCCESSFUL` alone is not proof — check `composeApp/build/test-results/<task>/TEST-*.xml` (`tests="N" failures="0"`).

## 📂 Codebase References

| Context        | Code                                                                      |
|----------------|---------------------------------------------------------------------------|
| Test deps      | `composeApp/build.gradle.kts` (`commonTest`, `desktopTest`, `androidUnitTest`, `testOptions`) |
| Shared rule    | `composeApp/src/commonTest/.../app/common/viewmodel/ViewModelTestRule.kt` |
| Fakes          | `composeApp/src/commonTest/kotlin/ru/rznnike/demokmp/domain/gateway/`     |
| Android tests  | `composeApp/src/androidUnitTest/kotlin/ru/rznnike/demokmp/`               |
| Platform tests | `composeApp/src/desktopTest/kotlin/ru/rznnike/demokmp/`                   |

## Related Files

- `code-patterns.md` · `technical-domain.md` · `living-notes.md` · `decisions-log.md`
