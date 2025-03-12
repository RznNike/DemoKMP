# Demo Kotlin Multiplatform project for Desktop (JVM)
[English](https://github.com/RznNike/DemoKMP#readme) | [Русский](/README.ru.md)

This is an example project with a set of solutions to typical problems, demonstrating the creation of an application for the desktop (JVM) using [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/).

## Implemented solutions
* Project architecture - [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
* UI layer architecture - [Compose](https://developer.android.com/develop/ui/compose/documentation) + [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel)
* Dependency Injection
* Local database (SQL)
* Shared preferences (with custom classes storage)
* App building to jar file and launch using script
* Protection from launching several copies of app simultaneously
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
* Basic PDF viewer, print dialog
* Various custom UI elements
* Built-in logger with UI

## Screenshots
<details>
    <summary>Main app</summary>
    <img src="/readmeFiles/en/screenshot_1.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_2.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_3.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_4.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_5.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_6.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_7.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_8.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshot_9.png" alt="screenshot" width="500"/>
</details>

<details>
    <summary>Logger</summary>
    <img src="/readmeFiles/en/screenshot_10.png" alt="screenshot" width="750"/>
    <img src="/readmeFiles/en/screenshot_11.png" alt="screenshot" width="750"/>
</details>

## Used libraries
* [Voyager](https://voyager.adriel.cafe/)
* [Koin](https://insert-koin.io/)
* [Room](https://developer.android.com/jetpack/androidx/releases/room)
* [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings)
* [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor](https://ktor.io/)
* [Ktorfit](https://foso.github.io/Ktorfit/)
* [Coil](https://coil-kt.github.io/coil/)
* [BuildKonfig](https://github.com/yshrsmz/BuildKonfig)
* [PDFBox](https://pdfbox.apache.org/)

## Used example APIs
* HTTP - [dog.ceo](https://dog.ceo/dog-api/)
* WebSocket - [websocket.org](https://websocket.org/tools/websocket-echo-server)

## Project launch
This project was created and tested using [IntelliJ IDEA](https://www.jetbrains.com/idea/). All further instructions are provided for this IDE.

### Launch from IDE
To run this app, the project has several already created configurations:
* ```run [debug]``` (with debug)
* ```run [debug +args]``` (with debug and launch args)
* ```run [staging]``` (with debug and proguard)
* ```run [release]``` (with proguard)

You can also create your own launch configurations based on the ones already added. To do this, in the IDE, go to the menu:

```Run -> Edit Configurations -> + (Add New Configuration) -> Gradle```

### App build launch
This project is configured to build the application into a jar file. The target computer requires JVM version 17 or later to be installed.

To make the application easier to launch, the project includes launch scripts (for Windows and Linux). These scripts also perform additional checks, in particular - whether the required version of the JVM is installed.

To run the compiled application, run the ```run.vbs``` (Windows) or ```run.sh``` (Linux) script located in the application's root directory. You can also create a shortcut to the application on your desktop by running the script ```create_desktop_shortcut.vbs``` / ```create_desktop_shortcut.sh```.

## Building
This project is configured to create builds for Windows and Linux. If you want to build it for macOS, update the ```nativeDistributions``` section in ```build.gradle.kts``` accordingly.

To create a distributable build, use one of the launch configurations:
* ```archive [staging]``` (with debug and proguard)
* ```archive [release]``` (with proguard)

As a result, an archive with the assembled version of the application will be created, ready for distribution. The assembled application is located in the ```distributableOutput``` folder, and its archive is in the ```distributableArchive``` folder.

If you have problems building your application, it is recommended to select JDK version ```17```. To do this:
* Select JDK version ```17``` at path ```File -> Project Structure -> Project -> SDK```, and also ```Language level``` under it.
* At path ```Settings -> Build -> Compiler -> Java compiler -> Project bytecode version``` select ```17```.
* At path ```Settings -> Build -> Build tools -> Gradle -> Gradle JVM``` select ```Project JDK```.

*Note: you can build your application only for the OS you are currently using. If, for example, you use Windows and need to create a build for Linux, consider doing it in a virtual machine.*

*Note 2: there is a dummy ```android``` target in the project configuration. It's not configured correctly (and doesn't have the proper sources to work). This target is only added to fix a bug in the KSP plugin where it will not work if there is only one target in the project configuration.*