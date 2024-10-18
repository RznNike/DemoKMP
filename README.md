# Demo Kotlin Multiplatform project for Desktop (JVM)
[English](https://github.com/RznNike/DemoKMP#readme) | [Русский](/README.ru.md)

This is an example project with a set of solutions to typical problems, demonstrating the creation of an application for the desktop (JVM) using [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/).

## Implemented solutions
* Project architecture - [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
* UI layer architecture - [Compose](https://developer.android.com/develop/ui/compose/documentation) + [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel)
* Dependency Injection
* Local database (SQL)
* Shared preferences (with custom classes storage)
* HTTP API usage
* WebSocket API usage
* Proguard configuration (with obfuscation)
* Two-layer screen navigation (screen flows and screens)
* App-wide dialogs and snackbars
* Images loading from network
* Material 3 theme configuration
* Dark/Light theme with auto and manual selection
* Strings localization
* Launch arguments
* Screen-scoped hotkeys
* Basic PDF viewer
* Various custom UI elements

## Used libraries
* [Voyager](https://voyager.adriel.cafe/)
* [Koin](https://insert-koin.io/)
* [Room](https://developer.android.com/jetpack/androidx/releases/room)
* [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
* [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor](https://ktor.io/)
* [Ktorfit](https://foso.github.io/Ktorfit/)
* [Coil](https://coil-kt.github.io/coil/)
* [BuildKonfig](https://github.com/yshrsmz/BuildKonfig)
* [PDFBox](https://pdfbox.apache.org/)

## Used example APIs
* HTTP - [dog.ceo](https://dog.ceo/dog-api/)
* WebSocket - [websocket.org](https://websocket.org/tools/websocket-echo-server)

## Launch project
This project was created and tested using [IntelliJ IDEA](https://www.jetbrains.com/idea/). All further instructions are provided for this IDE.

### Standard launch
In IDE:

```Run -> Edit Configurations -> + (Add New Configuration) -> Gradle```

Under the ```Run``` header add these arguments in input field, then save the configuration:

```desktopRun -DmainClass=ru.rznnike.demokmp.app.MainKt --quiet```

At the line ```Environment variables``` add ```DEBUG=true``` to change debug mode flag automatically on launch.

### Launch with args
You can run a Kotlin application with arguments that will be passed to the main() function.

#### Launch from IDE
Create run configuration with arguments like that:

```desktopRun --args="'foo foo2' bar" -DmainClass=ru.rznnike.demokmp.app.MainKt --quiet```

This example will run the program with two arguments: "foo foo2" and "bar".

#### Launch .exe from PowerShell
```& ".\ru.rznnike.demokmp.exe" "foo foo2" bar```

## Build
This project is configured to create builds for Windows and Linux. If you want to build it for macOS, update the ```nativeDistributions``` section in ```build.gradle.kts``` accordingly.

To create a distributable build, run ```createDistributable``` (without Proguard) or ```createReleaseDistributable``` (with Proguard) Gradle task.

To create an installer, run ```packageDistributionForCurrentOS``` or ```packageReleaseDistributionForCurrentOS``` accordingly.

*Note: you can build your application only for the OS you are currently using. If, for example, you use Windows and need to create a build for Linux, consider doing it in a virtual machine.*

*Note 2: there is a dummy ```android``` target in the project configuration. It's not configured correctly (and doesn't have the proper sources to work). This target is only added to fix a bug in the KSP plugin where it will not work if there is only one target in the project configuration.*