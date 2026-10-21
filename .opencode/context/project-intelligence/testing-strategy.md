<!-- Context: project-intelligence/testing-strategy | Priority: high | Version: 1.0 | Updated: 2026-10-21 -->

# Testing Strategy

> How DemoKMP is tested: source sets, test stack, and the fakes-over-mocks convention.

## Quick Reference

- **Purpose**: explain the test layout so contributors can add tests correctly
- **Update When**: test stack, source-set layout, or conventions change
- **Audience**: Contributors, AI agents writing tests

---

## Test Stack

| Dependency                    | Purpose                                    |
|-------------------------------|--------------------------------------------|
| `kotlin("test")`              | Assertions and test annotations (common)   |
| `kotlinx-coroutines-test`     | Coroutine test control                     |
| `multiplatform-settings-test` | In-memory `Settings` for preferences tests |

Declared in `composeApp/build.gradle.kts` → `commonTest.dependencies`.

## Source Sets

| Source Set        | Files | Contents                                            |
|-------------------|-------|-----------------------------------------------------|
| `commonTest`      | 82    | Platform-independent tests + all fakes              |
| `desktopTest`     | 4     | Platform-bound desktop tests                        |
| `androidUnitTest` | —     | **Not present** — Android platform code is untested |
| `androidTest`     | —     | **Not present** — no instrumented tests             |

**Why the split**: tests that touch JVM/desktop-only APIs (COM, shell, Room on desktop, log formatting) cannot live in `commonTest`. Everything else does, so it runs on every target.

## Convention: Fakes, Not Mocks

No mocking framework. Test doubles are hand-written classes in `commonTest`, placed in the **same package as the production interface** so they can implement it directly.

| Location                                  | Contents                                                                        |
|-------------------------------------------|---------------------------------------------------------------------------------|
| `domain/gateway/Fake*.kt`                 | 10 fakes: `FakeHTTPExampleGateway`, `FakeLogGateway`, `FakeDBExampleGateway`, … |
| `domain/common/FakeDispatcherProvider.kt` | Substitutes `DispatcherProvider`                                                |

**Fake shape** — expose the collaborators as mutable state, record calls, allow failure injection:

```kotlin
class FakeHTTPExampleGateway : HTTPExampleGateway {
    var errorToThrow: Exception? = null
    val requestedCounts = mutableListOf<Int>()
    var links: List<String> = emptyList()
    // ...
}
```

**FakeDispatcherProvider** defaults to `Dispatchers.Unconfined` — eager execution, no scheduler plumbing needed for delay-free code.

```kotlin
class FakeDispatcherProvider(
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : DispatcherProvider {
    override val ui = dispatcher
    override val default = dispatcher
    override val io = dispatcher
    override val unconfined = dispatcher
}
```

## Coverage Map

| Layer                                          | Tested                                                                                     |
|------------------------------------------------|--------------------------------------------------------------------------------------------|
| UseCases (all 10 feature folders)              | ✅ `domain/interactor/**`                                                                  |
| UseCase bases                                  | ✅ `UseCase`, `UseCaseWithParams`, `FlowUseCase`, `FlowUseCaseWithParams`, `UseCaseResult` |
| Gateways (data)                                | ✅ 7 in `commonTest` + 3 in `desktopTest`                                                  |
| Logger extensions                              | ✅ all 4 sinks + the `LoggerExtension` base                                                |
| Domain utils                                   | ✅ `CollectionUtils`, `DateUtils`, `OperatingSystem`, `StringUtils`                        |
| Domain models                                  | ✅ `LauncherConfig`, model defaults, `LogMessage`, `NetworkLogMessage`                     |
| Preferences                                    | ✅ 10 `Get/Set*UseCase` tests                                                              |
| Desktop-only (COM, shell, App, DB, log format) | ✅ `desktopTest`                                                                           |
| Android-only (camera, MLKit, permissions)      | ❌ no `androidUnitTest` source set                                                         |

## Conventions

- Test name: `<ClassUnderTest>Test`, mirroring the production file name.
- Test package: identical to the production package.
- Arrange → Act → Assert, with the **Arrange** block first.
- Error branches are exercised via the fake's `errorToThrow`, never via mocking.
- Platform-bound tests go in `desktopTest`; everything else in `commonTest`.

## 📂 Codebase References

| Context        | Code                                                                  |
|----------------|-----------------------------------------------------------------------|
| Test deps      | `composeApp/build.gradle.kts` (`commonTest.dependencies`)             |
| Fakes          | `composeApp/src/commonTest/kotlin/ru/rznnike/demokmp/domain/gateway/` |
| Platform tests | `composeApp/src/desktopTest/kotlin/ru/rznnike/demokmp/`               |
| LauncherConfig | `domain/model/common/LauncherConfig.kt`                               |

## Related Files

- `code-patterns.md` · `technical-domain.md` · `living-notes.md` · `decisions-log.md`
