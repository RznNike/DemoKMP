<!-- Context: project-intelligence/testing-strategy | Priority: high | Version: 2.0 | Updated: 2026-10-30 -->

# Testing Strategy

> How DemoKMP is tested: source sets, test stack, shared test rules, and the fakes-over-mocks convention.

## Quick Reference

- **Purpose**: explain the test layout so contributors can add tests correctly
- **Update When**: test stack, source-set layout, or conventions change
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

Declared in `composeApp/build.gradle.kts` → `commonTest.dependencies`.

## Source Sets

| Source Set        | Files | Test classes | Contents                                                                |
|-------------------|-------|--------------|-------------------------------------------------------------------------|
| `commonTest`      | 125   | 107          | Platform-independent tests, all fakes, shared test rules                |
| `desktopTest`     | 15    | 15           | Platform-bound desktop tests (COM, shell, DB, printing, desktop VMs)    |
| `androidUnitTest` | 2     | 2            | Android-only ViewModel tests (host JVM, `isReturnDefaultValues = true`) |
| `androidTest`     | —     | —            | **Not present** — no instrumented/UI tests                              |

**Total: 765 `@Test` cases.** **Why the split**: tests that touch JVM/desktop-only APIs (COM, shell, Room on desktop, log formatting) cannot live in `commonTest`; Android-only ViewModels cannot run on the desktop host. Everything else stays in `commonTest` so it runs on every target.

## Convention: Fakes, Not Mocks

No mocking framework. Test doubles are handwritten classes in `commonTest`, placed in the **same package as the production type** so they can implement/extend it directly.

| Location                                              | Contents                                                                                |
|-------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `domain/gateway/Fake*.kt`                             | 10 gateway fakes: `FakeHTTPExampleGateway`, `FakeLogGateway`, `FakeDBExampleGateway`, … |
| `domain/common/FakeDispatcherProvider.kt`             | Substitutes `DispatcherProvider`                                                        |
| `domain/common/FakeCoroutineScopeProvider.kt`         | Substitutes `CoroutineScopeProvider` for `Notifier` / `EventDispatcher`                 |
| `app/error/FakeErrorHandler.kt`                       | Substitutes the `ErrorHandler` interface                                                |
| `data/network/interceptor/StubResponseInterceptor.kt` | Canned HTTP responses for interceptor tests                                             |

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

## Android-Only ViewModel Tests

`androidUnitTest` hosts ViewModels that live in `androidMain` (`ActivityViewModel`, `BarcodeExampleViewModel`). They run on the host JVM, so Android framework types must not be loaded:

- `composeApp/build.gradle.kts` sets `testOptions.unitTests.isReturnDefaultValues = true` — Android methods return defaults instead of throwing.
- Same `ViewModelTestRule` applies (host JVM has no `Main` dispatcher at all).
- `BarcodeScannerViewModel` (camera/MLKit-bound) has no test yet.

## Coverage Map

| Layer                                          | Tested                                                                                                                            |
|------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| UseCases (all feature folders)                 | ✅ `domain/interactor/**`                                                                                                         |
| UseCase bases / `UseCaseResult`                | ✅ `domain/common/interactor/**`                                                                                                  |
| ViewModels                                     | ✅ every ViewModel except `BarcodeScannerViewModel` (14 `commonTest`, 5 `desktopTest`, 2 `androidUnitTest`)                       |
| DI graph                                       | ✅ `AppComponentTest` (`verify()`), `NetworkModuleTest` (`createNetworkCache`)                                                    |
| Gateways (data)                                | ✅ 7 in `commonTest` + 3 in `desktopTest`                                                                                         |
| Network                                        | ✅ `AppApi`, `AppWebSocketManagerImpl`, 4 interceptors, `CustomServerException`                                                   |
| Logger                                         | ✅ `Logger`, 4 sinks + base, `LogMessage`, `NetworkLogMessage`, not-initialized path                                              |
| Storage                                        | ✅ entities (3), DAOs (3), `MainDB`                                                                                               |
| Preferences                                    | ✅ `Preference`, `PreferencesManager`, 10 `Get/Set*UseCase`                                                                       |
| App layer                                      | ✅ `ErrorHandler`, `EventDispatcher`, `Notifier`, `FlowNavigator`, `NavigationFlowInfo`, `DateInputManager`, `ClicksFilter`       |
| Data/domain utils & models                     | ✅ `HttpUtils`, `JsonUtils`, `UUIDSerializer`, `CollectionUtils`, `DateUtils`, `StringUtils`, `OperatingSystem`, `LauncherConfig` |
| Desktop-only (COM, shell, App, DB, log format) | ✅ `desktopTest`                                                                                                                  |
| Android camera/MLKit/permissions               | ❌ beyond the 2 host-JVM ViewModel tests                                                                                          |

## Conventions

- Test name: `<ClassUnderTest>Test`, mirroring the production file name.
- Test package: identical to the production package.
- Arrange → Act → Assert, with the **Arrange** block first.
- Error branches are exercised via the fake's `errorToThrow`, never via mocking.
- Platform-bound tests go in `desktopTest`; Android-bound tests in `androidUnitTest`; everything else in `commonTest`.

## 📂 Codebase References

| Context        | Code                                                                      |
|----------------|---------------------------------------------------------------------------|
| Test deps      | `composeApp/build.gradle.kts` (`commonTest.dependencies`, `testOptions`)  |
| Shared rule    | `composeApp/src/commonTest/.../app/common/viewmodel/ViewModelTestRule.kt` |
| Fakes          | `composeApp/src/commonTest/kotlin/ru/rznnike/demokmp/domain/gateway/`     |
| Android tests  | `composeApp/src/androidUnitTest/kotlin/ru/rznnike/demokmp/`               |
| Platform tests | `composeApp/src/desktopTest/kotlin/ru/rznnike/demokmp/`                   |

## Related Files

- `code-patterns.md` · `technical-domain.md` · `living-notes.md` · `decisions-log.md`
