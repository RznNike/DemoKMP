<!-- Context: project-intelligence/technical | Priority: critical | Version: 1.3 | Updated: 2026-10-30 -->

# Technical Domain

> Tech stack, architecture, and development patterns for DemoKMP.

## Quick Reference

- **Purpose**: Understand how DemoKMP works technically
- **Update When**: Stack changes, new patterns, architecture decisions
- **Audience**: Developers, AI agents

---

## Primary Stack

| Layer         | Technology                           | Version        | Rationale                            |
|---------------|--------------------------------------|----------------|--------------------------------------|
| Language      | Kotlin (Multiplatform)               | 2.4.21         | Shared code across targets           |
| UI            | Compose Multiplatform                | 1.12.1         | One declarative UI for all targets   |
| Material      | Compose Material 3                   | 1.12.0-alpha03 | Material 3 across targets            |
| ViewModel     | JetBrains AndroidX Lifecycle         | 2.11.0         | MPP ViewModel + Navigation3          |
| Targets       | Android (SDK 26–37) + Desktop JVM 17 | —              | PC (Win/Linux) + Android             |
| DI            | Koin                                 | 4.2.2          | Multiplatform-friendly DI            |
| Network       | Ktor + Ktorfit                       | 3.6.0 / 2.7.5  | HTTP/WebSocket client; type-safe API |
| HTTP engine   | OkHttp                               | 5.5.0          | Ktor engine for Desktop + Android    |
| Serialization | kotlinx.serialization                | 1.11.0         | Kotlin-native JSON                   |
| Database      | Room + SQLite bundled                | 2.8.5 / 2.7.1  | Cross-platform SQL persistence       |
| Preferences   | Multiplatform Settings               | 1.3.0          | Key-value storage                    |
| Navigation    | AndroidX Navigation3                 | 1.1.2          | Type-safe navigation                 |
| Images        | Coil 3                               | 3.6.3          | Async image loading                  |
| Async         | Coroutines                           | 1.11.0         | Concurrency                          |
| Build config  | BuildKonfig                          | 0.23.0         | Generated BuildConfig                |
| Codegen       | KSP                                  | 2.3.12         | Room + Ktorfit code generation       |
| Charts        | Vico                                 | 3.3.1          | Chart rendering                      |
| Markdown      | multiplatform-markdown-renderer      | 0.46.0         | Markdown rendering                   |

**Desktop-only**: PDFBox 3.0.8 · FileKit 0.16.0 · JNA 5.19.1 · Logback 1.6.5

**Android-only**: CameraX 1.6.2 · MLKit barcode 17.3.0 · Chucker 4.3.1 · Accompanist permissions 0.37.3 · Telephoto 0.19.0 · desugaring 2.1.5

**AndroidX**: activity-compose 1.13.0 · preference-ktx 1.2.1

## Architecture

**Type**: Layered Clean Architecture — 3 layers sharing `commonMain`.

Dependency rule: `app → domain ← data` (domain never depends on data).

### Project Structure

```
composeApp/src/
├── commonMain/kotlin/ru/rznnike/demokmp/   # shared code
├── androidMain/                             # Android expect/actual
├── desktopMain/                             # Desktop expect/actual
├── commonTest/                              # platform-independent tests + fakes (125 files)
├── desktopTest/                             # platform-bound desktop tests (15 files)
└── androidUnitTest/                         # host-JVM Android tests (2 files)
```

### Package Map

| Package                                                 | Contents                                                                                                                                                                                     |
|---------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `app/viewmodel/**`                                      | Feature VMs: chartexample, customui, dbexample, global (configuration, profile, hotkeys), home, httpexample, navigation, pdfexample, settings, splash, wsexample + desktop `ui/viewmodel/**` |
| `app/ui/{view,item,dialog,theme}`                       | Compose screens, list items, dialogs, theme                                                                                                                                                  |
| `app/error`                                             | `ErrorHandler` interface + `ErrorHandlerImpl` (error → message mapping)                                                                                                                      |
| `app/di`                                                | 8 Koin modules: AppComponent, AppModule, DatabaseModule, GatewayModule, InteractorModule, NetworkModule, PreferenceModule, ViewModelModule                                                   |
| `app/dispatcher/{event,notifier}`                       | `EventDispatcher` (typed pub/sub), `Notifier` (system messages)                                                                                                                              |
| `app/common/viewmodel`                                  | `BaseViewModel`, `BaseUiViewModel`                                                                                                                                                           |
| `app/{model,utils}`                                     | Shared models, Compose/platform utils                                                                                                                                                        |
| `domain/interactor/**`                                  | UseCases per feature: app, chartexample, comobjectexample, dbexample, file, httpexample, log, pdfexample, preferences, wsexample                                                             |
| `domain/common/interactor`                              | `UseCase`, `UseCaseWithParams`, `FlowUseCase`, `FlowUseCaseWithParams`, `UseCaseResult`                                                                                                      |
| `domain/gateway`                                        | Gateway interfaces (implemented in `data`)                                                                                                                                                   |
| `domain/log` + `domain/log/extension`                   | `Logger` (single-consumer queue), `LoggerExtension` base + 4 sinks (Console/File/Database/MemoryCache)                                                                                       |
| `domain/{model,utils}`                                  | Domain models, pure utilities                                                                                                                                                                |
| `data/{gateway,network,storage,preference,shell,utils}` | Implementations; `network` has 4 interceptors + `CustomServerException`; `shell` = `ShellManager`                                                                                            |

## Code Standards

- Clean Architecture: `app → domain ← data`; domain exposes gateway interfaces, data implements.
- One Koin module per concern: `AppModule`, `NetworkModule`, `DatabaseModule`, `GatewayModule`, `InteractorModule`, `PreferenceModule`, `ViewModelModule`.
- UseCases extend `UseCase`/`UseCaseWithParams`/`FlowUseCase*`, return `UseCaseResult` (errors-as-values, no exceptions across layers).
- Suspend work runs on injected `DispatcherProvider.io`, never hard-coded dispatchers.
- ViewModels extend `BaseViewModel`/`BaseUiViewModel`, expose immutable `StateFlow` UI state; `progressDelayMs` is overridable for tests.
- Platform code via `expect`/`actual` (`androidMain`/`desktopMain`).
- Errors are surfaced through the `ErrorHandler` interface (`ErrorHandlerImpl` in `app/error`), so ViewModels depend on an interface.
- Room entities + DAOs in `data/storage`, mapped to domain models via extension functions.
- KSP generates Room + Ktorfit code.
- Logging via `Logger` + `LoggerExtension` sinks; `Logger` serializes messages through one consumer coroutine (ordered, one timestamp per message).
- Navigation screens log their name on resume (`NavigationScreen` + `Logger.withTag("Navigation")`).
- Cross-screen communication via `EventDispatcher` (typed `AppEvent`), system messages via `Notifier`.
- Tests use handwritten fakes, not mocks; ViewModels are tested through `ViewModelTestRule` (`Dispatchers.setMain`) — see `testing-strategy.md`.
- Android Lint strict: `abortOnError = true`, `checkAllWarnings = true`.

## Testing

| Aspect      | Detail                                                                                                    |
|-------------|-----------------------------------------------------------------------------------------------------------|
| Stack       | `kotlin("test")`, `kotlinx-coroutines-test`, `multiplatform-settings-test`, `ktor-mock`, `koin-test`      |
| Source sets | `commonTest` (125 files / 107 test classes), `desktopTest` (15), `androidUnitTest` (2)                    |
| Doubles     | Hand-written `Fake*` gateways, `FakeDispatcherProvider`, `FakeCoroutineScopeProvider`, `FakeErrorHandler` |
| Scale       | 765 `@Test` cases; every ViewModel except `BarcodeScannerViewModel`                                       |
| Gap         | No `androidTest` — camera/MLKit/permissions UI is untested                                                |

Full detail: `testing-strategy.md`.

## Security Requirements

- Signing: `demokmp.jks` keystore; passwords via Gradle properties (`PROJECT_KEYSTORE_PASSWORD`, `PROJECT_KEY_PASSWORD`).
- R8/ProGuard minification + obfuscation for `staging` and `release`.
- Network over HTTPS/WSS only (Ktor client); no cleartext endpoints.
- Bundled signing key is a stub — replace before any real release (see README).
- Network logger (Chucker) is debug/staging only; release uses no-op.

## 📂 Codebase References

| Context        | Code                                                         |
|----------------|--------------------------------------------------------------|
| Stack versions | `gradle/libs.versions.toml`, `composeApp/build.gradle.kts`   |
| DI modules     | `app/di/*.kt`                                                |
| API / network  | `data/network/AppApi.kt`, `data/network/error/*.kt`          |
| UseCase base   | `domain/common/interactor/*.kt`                              |
| ViewModels     | `app/viewmodel/**`, `app/common/viewmodel/*.kt`              |
| Room DB        | `data/storage/MainDB.kt`, `data/storage/entity/*.kt`         |
| Navigation     | `app/navigation/*.kt`                                        |
| UI components  | `app/ui/view/*.kt`, `app/ui/item/*.kt`                       |
| Errors         | `app/error/ErrorHandler.kt`, `app/error/ErrorHandlerImpl.kt` |
| Logging        | `domain/log/**`, `domain/log/extension/*.kt`                 |
| Events         | `app/dispatcher/event/EventDispatcher.kt`                    |

## Related Files

- `code-patterns.md` · `testing-strategy.md` · `business-domain.md` · `business-tech-bridge.md` · `decisions-log.md` · `living-notes.md`
