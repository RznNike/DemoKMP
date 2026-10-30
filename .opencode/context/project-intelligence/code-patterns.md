<!-- Context: project-intelligence/code-patterns | Priority: high | Version: 1.1 | Updated: 2026-10-30 -->

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

## 4. UI Component

```kotlin
@Composable
fun FilledButtonWithLoader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) { /* ... */ }
```

## 5. Room Entity + Mapper

Entities live in `data/storage/entity`; mapping to domain models uses extension functions.

```kotlin
@Entity
data class DBExampleDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

fun DBExampleDataEntity.toDBExampleData() = DBExampleData(id = id, name = name)
```

## 6. Logger Extension

`LoggerExtension` is the abstract base; each sink implements `addMessage`. `Logger` captures one timestamp
per message and feeds extensions through a single-consumer queue, so `timestamp` is always passed down.

```kotlin
abstract class LoggerExtension(
    protected val stopAfterOneError: Boolean = false,
    protected val isDebugEnabled: Boolean = BuildKonfig.DEBUG
) {
    protected lateinit var clock: Clock
    protected lateinit var coroutineDispatcher: CoroutineDispatcher

    open fun init(clock: Clock, coroutineDispatcher: CoroutineDispatcher)

    open suspend fun i(tag: String, message: String, timestamp: Long = clock.millis()) =
        addMessage(tag, message, LogLevel.INFO, timestamp = timestamp)

    abstract suspend fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType = LogType.DEFAULT,
        timestamp: Long = clock.millis(),
        callback: suspend (LogMessage) -> Unit = { }
    )
}
```

**Sinks**: `ConsoleLoggerExtension`, `FileLoggerExtension`, `DatabaseLoggerExtension`, `MemoryCacheLoggerExtension`.
**Init**: `Logger.init(clock, coroutineDispatcher, extensions)`; tag-scoped calls via `Logger.withTag("Navigation").i(...)`.

## 7. Event Dispatcher

Typed, tag-aware pub/sub over `AppEvent` subclasses.

```kotlin
class EventDispatcher(private val coroutineScopeProvider: CoroutineScopeProvider) {
    fun addEventListener(appEventClass: KClass<out AppEvent>, listener: EventListener)
    fun addSingleByTagEventListener(appEventClass: KClass<out AppEvent>, listener: EventListener)
    fun removeEventListener(listener: EventListener)
    fun sendEvent(appEvent: AppEvent)   // dispatches on coroutineScopeProvider.ui
}
```

## 8. Test Double (Fake)

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

## 9. Error Handling (interface + impl)

ViewModels depend on the `ErrorHandler` interface; the implementation maps throwables to user-facing text.
Tests substitute `FakeErrorHandler`.

```kotlin
// app/error/ErrorHandler.kt
interface ErrorHandler {
    suspend fun proceed(error: Throwable, callback: suspend (String) -> Unit)
}

// usage inside a ViewModel
private val errorHandler: ErrorHandler by inject()

errorHandler.proceed(throwable) { message ->
    // map message into UiState
}
```

---

## Naming Conventions

| Type                | Convention                | Example                           |
|---------------------|---------------------------|-----------------------------------|
| Files               | PascalCase, matches class | `HTTPExampleViewModel.kt`         |
| Classes/Interfaces  | PascalCase                | `AppApi`, `HTTPExampleGateway`    |
| Functions/variables | camelCase                 | `getRandomImageLinks()`           |
| Constants           | SCREAMING_SNAKE_CASE      | `IMAGES_COUNT`, `TIMEOUT_MS`      |
| Packages            | lowercase, dot-separated  | `ru.rznnike.demokmp.data.network` |
| UseCases            | Verb+Noun+`UseCase`       | `GetRandomImageLinksUseCase`      |
| Gateway impls       | interface name + `Impl`   | `HTTPExampleGatewayImpl`          |
| Room entities       | `...Entity`               | `DBExampleDataEntity`             |
| Logger sinks        | `...LoggerExtension`      | `FileLoggerExtension`             |

## 📂 Codebase References

| Pattern          | Code                                                           |
|------------------|----------------------------------------------------------------|
| API              | `data/network/AppApi.kt`                                       |
| UseCase bases    | `domain/common/interactor/*.kt`                                |
| ViewModel bases  | `app/common/viewmodel/*.kt`                                    |
| Error handler    | `app/error/ErrorHandler.kt`, `app/error/ErrorHandlerImpl.kt`   |
| Room entities    | `data/storage/entity/*.kt`                                     |
| Logger bases     | `domain/log/extension/*.kt`                                    |
| Event dispatcher | `app/dispatcher/event/EventDispatcher.kt`                      |
| Fakes            | `commonTest/kotlin/ru/rznnike/demokmp/domain/gateway/Fake*.kt` |

## Related Files

- `technical-domain.md` · `testing-strategy.md` · `decisions-log.md`
