<!-- Context: project-intelligence/code-patterns | Priority: high | Version: 1.2 | Updated: 2026-11-09 -->

# Code Patterns

> Canonical Kotlin snippets for DemoKMP's recurring implementation shapes. Copy-adapt these.

## Quick Reference

- **Purpose**: how recurring features are implemented in this codebase
- **Update When**: a pattern changes shape or a new recurring shape appears
- **Audience**: Developers, AI agents extending the demo

---

## 1. API Endpoint (Ktorfit)

Interface-only declaration; KSP generates the implementation.

```kotlin
interface AppApi {
    @GET("api/breeds/image/random/{count}")
    suspend fun getRandomImages(@Path("count") count: Int): RandomImageLinksModel
}
```

## 2. UseCase (Interactor)

Base classes live in `domain/common/interactor`. Errors are returned as values, never thrown.

```kotlin
class GetRandomImageLinksUseCase(
    private val httpExampleGateway: HTTPExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<GetRandomImageLinksUseCase.Parameters, List<String>>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) =
        httpExampleGateway.getRandomImageLinks(count = parameters.count)

    data class Parameters(val count: Int)
}
```

**Base classes**: `UseCase`, `UseCaseWithParams`, `FlowUseCase`, `FlowUseCaseWithParams`, `UseCaseResult`.

## 3. ViewModel + UiState

```kotlin
class HTTPExampleViewModel : BaseUiViewModel<HTTPExampleViewModel.UiState>() {
    private val getRandomImageLinksUseCase: GetRandomImageLinksUseCase by inject()

    override fun provideDefaultUIState() = UiState()

    @Immutable
    data class UiState(val isLoading: Boolean = false, val images: List<String> = emptyList())
}
```

## 4. Room Entity + Mapper

Entities live in `data/storage/entity`; mapping to domain models uses extension functions.

```kotlin
@Entity
data class DBExampleDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

fun DBExampleDataEntity.toDBExampleData() = DBExampleData(id = id, name = name)
```

## 5. Logger Extension

`LoggerExtension` is the abstract base; each sink implements `addMessage`. `Logger` captures one timestamp per message and feeds extensions through a single-consumer queue.

```kotlin
abstract class LoggerExtension(
    protected val stopAfterOneError: Boolean = false,
    protected val isDebugEnabled: Boolean = BuildKonfig.DEBUG
) {
    open fun init(clock: Clock, coroutineDispatcher: CoroutineDispatcher)
    open suspend fun i(tag: String, message: String, timestamp: Long = clock.millis()) =
        addMessage(tag, message, LogLevel.INFO, timestamp = timestamp)
    abstract suspend fun addMessage(
        tag: String, message: String, level: LogLevel,
        type: LogType = LogType.DEFAULT, timestamp: Long = clock.millis(),
        callback: suspend (LogMessage) -> Unit = { }
    )
}
```

**Sinks**: `ConsoleLoggerExtension`, `FileLoggerExtension`, `DatabaseLoggerExtension`, `MemoryCacheLoggerExtension`.
**Init**: `Logger.init(clock, coroutineDispatcher, extensions)`; tag-scoped calls via `Logger.withTag("Navigation").i(...)`.

## 6. Test Double (Fake)

Fakes are handwritten classes in `commonTest`, mirroring the production package.

```kotlin
class FakeHTTPExampleGateway : HTTPExampleGateway {
    var errorToThrow: Exception? = null
    val requestedCounts = mutableListOf<Int>()
    var links: List<String> = emptyList()

    override suspend fun getRandomImageLinks(count: Int): List<String> {
        errorToThrow?.let { throw it }
        requestedCounts += count
        return links
    }
}
```

## 7. Pure-Function Seam (testability)

Extract the decision into an `internal` pure function; keep the platform call site thin. This is the preferred way to make platform code testable without changing behavior.

```kotlin
// production
internal fun calculateMinimumSize(width: Dp, height: Dp, density: Density): Dimension? =
    if (width.isSpecified && height.isSpecified) Dimension(/* ... */) else null

fun Window.SetMinimumSize(width: Dp = Dp.Unspecified, height: Dp = Dp.Unspecified) {
    calculateMinimumSize(width, height, density)?.let { minimumSize = it }
}
```

Other examples: `internal resolveTheme` / `Theme.isDark`, `internal formatMacAddress`, `internal TwoSidedPrint.toSides`.

## 8. Compose UI Smoke Test (desktop)

Use `createComposeRule()` from the `v2` package and the DI-free `PreviewDesktopAppTheme`. Assert semantics-observable behavior only.

```kotlin
class DropdownSelectorTest {
    @get:Rule val rule = createComposeRule()   // androidx.compose.ui.test.junit4.v2

    @Test
    fun `emits tapped item`() {
        var selected = ""
        rule.setContent {
            PreviewDesktopAppTheme {
                DropdownSelector(items = listOf("One"), onSelect = { selected = it })
            }
        }
        rule.onNodeWithText("One").performClick()
        rule.runOnIdle { assertEquals("One", selected) }
    }
}
```

Do **not** compose screens that need `LocalWindow`, `ViewModelStoreOwner`, `getNavigator()` or global Koin — accepted gap.

## 9. Robolectric Host Test (Android)

Android framework code runs on the host JVM; no emulator.

```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [37], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)   // Compose only
class KoinModuleAndroidTest {
    @Test
    fun `resolves Android preferences`() {
        val app = koinApplication {
            androidContext(RuntimeEnvironment.getApplication())
            modules(appComponent)
        }
        // assert a real SharedPreferences / Room round-trip
    }
}
```

## Naming Conventions

| Type                | Convention                | Example                           |
|---------------------|---------------------------|-----------------------------------|
| UseCases            | Verb+Noun+`UseCase`       | `GetRandomImageLinksUseCase`      |
| Gateway impls       | interface name + `Impl`   | `HTTPExampleGatewayImpl`          |
| Room entities       | `...Entity`               | `DBExampleDataEntity`             |
| Logger sinks        | `...LoggerExtension`      | `FileLoggerExtension`             |
| Test classes        | `<ClassUnderTest>Test`    | `SettingsViewModelTest`           |
| Internal seams      | `internal` + intent verb  | `calculateMinimumSize`            |

## 📂 Codebase References

| Pattern          | Code                                                           |
|------------------|----------------------------------------------------------------|
| API              | `data/network/AppApi.kt`                                       |
| UseCase bases    | `domain/common/interactor/*.kt`                                |
| ViewModel bases  | `app/common/viewmodel/*.kt`                                    |
| Error handler    | `app/error/ErrorHandler.kt`, `app/error/ErrorHandlerImpl.kt`   |
| Room entities    | `data/storage/entity/*.kt`                                     |
| Logger bases     | `domain/log/extension/*.kt`                                    |
| Fakes            | `commonTest/kotlin/ru/rznnike/demokmp/domain/gateway/Fake*.kt` |
| UI tests         | `desktopTest/.../app/ui/**`, `androidUnitTest/.../ComposeUiHostSmokeTest.kt` |
| Test rule        | `commonTest/.../app/common/viewmodel/ViewModelTestRule.kt`     |

## Related Files

- `technical-domain.md` · `testing-strategy.md` · `decisions-log.md`
