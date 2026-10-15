<!-- Context: project-intelligence/technical | Priority: critical | Version: 1.1 | Updated: 2026-10-15 -->

# Technical Domain

> Tech stack, architecture, and development patterns for DemoKMP.

## Quick Reference

- **Purpose**: Understand how DemoKMP works technically
- **Update When**: Stack changes, new patterns, architecture decisions
- **Audience**: Developers, AI agents

## Primary Stack

| Layer | Technology | Version | Rationale |
|-------|-----------|---------|-----------|
| Language | Kotlin (Multiplatform) | 2.4.21 | Shared code across targets |
| UI | Compose Multiplatform | 1.12.1 | One declarative UI for all targets |
| Targets | Android (SDK 26–37) + Desktop JVM 17 | — | PC (Win/Linux) + Android |
| DI | Koin | 4.2.2 | Multiplatform-friendly DI |
| Network | Ktor + Ktorfit | 3.6.0 / 2.7.5 | HTTP/WebSocket client; type-safe API |
| Serialization | kotlinx.serialization | 1.11.0 | Kotlin-native JSON |
| Database | Room + SQLite bundled | 2.8.5 / 2.7.1 | Cross-platform SQL persistence |
| Preferences | Multiplatform Settings | 1.3.0 | Key-value storage |
| Navigation | AndroidX Navigation3 | 1.1.2 | Type-safe navigation |
| Images | Coil 3 | 3.6.3 | Async image loading |
| Async | Coroutines | 1.11.0 | Concurrency |
| Build config | BuildKonfig | 0.23.0 | Generated BuildConfig |
| Codegen | KSP | 2.3.12 | Room + Ktorfit code generation |
| Charts | Vico | 3.3.1 | Chart rendering |
| Markdown | multiplatform-markdown-renderer | 0.46.0 | Markdown rendering |

**Desktop-only**: PDFBox 3.0.8 · FileKit 0.16.0 · JNA 5.19.1 · Logback 1.6.5
**Android-only**: CameraX 1.6.2 · MLKit barcode 17.3.0 · Chucker 4.3.1 · Accompanist permissions 0.37.3 · Telephoto 0.19.0

## Architecture

**Type**: Layered Clean Architecture — 3 layers sharing `commonMain`.

```
app/     → presentation: viewmodels, ui (view/item/dialog/theme), navigation, di, dispatcher, error
domain/  → abstractions: gateway interfaces, interactors (UseCases), models, log
data/    → implementations: gateway impls, network, storage (Room), preference, shell
```

Dependency rule: `app → domain ← data` (domain never depends on data).

## Project Structure

```
composeApp/src/
├── commonMain/kotlin/ru/rznnike/demokmp/   # shared code
├── androidMain/                            # Android expect/actual
└── desktopMain/                            # Desktop expect/actual
```

## Code Patterns

### API endpoint (Ktorfit)

```kotlin
interface AppApi {
    @GET("api/breeds/image/random/{count}")
    suspend fun getRandomImages(@Path("count") count: Int): RandomImageLinksModel
}
```

### UseCase (interactor)

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

### ViewModel + UiState

```kotlin
class HTTPExampleViewModel : BaseUiViewModel<HTTPExampleViewModel.UiState>() {
    private val getRandomImageLinksUseCase: GetRandomImageLinksUseCase by inject()

    override fun provideDefaultUIState() = UiState()

    @Immutable
    data class UiState(val isLoading: Boolean = false, val images: List<String> = emptyList())
}
```

### UI component

```kotlin
@Composable
fun FilledButtonWithLoader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) { /* ... */ }
```

### Room entity + mappers

```kotlin
@Entity
data class DBExampleDataEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String)

fun DBExampleDataEntity.toDBExampleData() = DBExampleData(id = id, name = name)
```

## Naming Conventions

| Type | Convention | Example |
|------|-----------|---------|
| Files | PascalCase, matches class | `HTTPExampleViewModel.kt` |
| Classes/Interfaces | PascalCase | `AppApi`, `HTTPExampleGateway` |
| Functions/variables | camelCase | `getRandomImageLinks()` |
| Constants | SCREAMING_SNAKE_CASE | `IMAGES_COUNT`, `TIMEOUT_MS` |
| Packages | lowercase, dot-separated | `ru.rznnike.demokmp.data.network` |
| UseCases | Verb+Noun+`UseCase` | `GetRandomImageLinksUseCase` |
| Gateway impls | interface name + `Impl` | `HTTPExampleGatewayImpl` |
| Room entities | `...Entity` | `DBExampleDataEntity` |

## Code Standards

- Clean Architecture: `app → domain ← data`; domain exposes gateway interfaces, data implements.
- One Koin module per concern: `AppModule`, `NetworkModule`, `DatabaseModule`, `GatewayModule`, `InteractorModule`, `PreferenceModule`, `ViewModelModule`.
- UseCases extend `UseCase`/`UseCaseWithParams`, return `UseCaseResult` (errors-as-values, no exceptions across layers).
- Suspend work runs on injected `DispatcherProvider.io`, never hard-coded dispatchers.
- ViewModels extend `BaseViewModel`/`BaseUiViewModel`, expose immutable `StateFlow` UI state.
- Platform code via `expect`/`actual` (`androidMain`/`desktopMain`).
- Room entities + DAOs in `data/storage`, mapped to domain models via extension functions.
- KSP generates Room + Ktorfit code.
- Android Lint strict: `abortOnError = true`, `checkAllWarnings = true`.

## Security Requirements

- Signing: `demokmp.jks` keystore; passwords via Gradle properties (`PROJECT_KEYSTORE_PASSWORD`, `PROJECT_KEY_PASSWORD`).
- R8/ProGuard minification + obfuscation for `staging` and `release`.
- Network over HTTPS/WSS only (Ktor client); no cleartext endpoints.
- Bundled signing key is a stub — replace before any real release (see README).
- Network logger (Chucker) is debug/staging only; release uses no-op.

## 📂 Codebase References

| Context | Code |
|---------|------|
| Stack versions | `gradle/libs.versions.toml`, `composeApp/build.gradle.kts` |
| DI modules | `app/di/*.kt` |
| API definition | `data/network/AppApi.kt` |
| UseCase base | `domain/common/interactor/*.kt` |
| ViewModels | `app/viewmodel/**`, `app/common/viewmodel/*.kt` |
| Room DB | `data/storage/MainDB.kt`, `data/storage/entity/*.kt` |
| Navigation | `app/navigation/*.kt` |
| UI components | `app/ui/view/*.kt`, `app/ui/item/*.kt` |

## Related Files

- `business-domain.md` · `business-tech-bridge.md` · `decisions-log.md` · `living-notes.md`
