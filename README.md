# Demo Kotlin Multiplatform project for Desktop (JVM) and Android
[English](https://github.com/RznNike/DemoKMP#readme) | [Русский](/README.ru.md)

<img src="/readmeFiles/KMP_logo.png" alt="logo" width="300"/>

This is a sample project with a set of solutions to typical problems, demonstrating the creation of a cross-platform application for PC (JVM - Windows/Linux) and Android using [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/).

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
* Various custom UI elements
* Dynamic BuildConfig with platform and build type information
* Charts

#### PC only
* App building to jar file and launch using script
* Protection from launching several copies of app simultaneously
* Launch arguments
* Screen-scoped hotkeys
* Basic PDF viewer, print dialog
* Built-in logger with UI

#### Android only
* UI for viewing network logs from a device
* Convenient permissions requests

## Screenshots
<details>
    <summary>PC - main app</summary>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_1.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_2.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_3.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_4.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_5.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_6.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_7.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_8.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_9.png" alt="screenshot" width="500"/>
</details>

<details>
    <summary>PC - logger</summary>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_10.png" alt="screenshot" width="750"/>
    <img src="/readmeFiles/en/screenshots/pc/screenshot_11.png" alt="screenshot" width="750"/>
</details>

<details>
    <summary>Android</summary>
    <img src="/readmeFiles/en/screenshots/android/screenshot_1.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_2.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_3.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_4.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_5.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_6.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_7.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/en/screenshots/android/screenshot_8.jpg" alt="screenshot" width="250"/>
</details>

## Used libraries
* [Compose Navigation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation.html)
* [Koin](https://insert-koin.io/)
* [Room](https://developer.android.com/jetpack/androidx/releases/room)
* [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings)
* [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor](https://ktor.io/)
* [Ktorfit](https://foso.github.io/Ktorfit/)
* [Coil](https://coil-kt.github.io/coil/)
* [BuildKonfig](https://github.com/yshrsmz/BuildKonfig)
* [Vico](https://github.com/patrykandpatrick/vico)

#### PC only
* [PDFBox](https://pdfbox.apache.org/)
* [FileKit](https://github.com/vinceglb/FileKit)

#### Android only
* [Chucker](https://github.com/ChuckerTeam/chucker)
* [Accompanist (permissions)](https://github.com/google/accompanist/tree/main/permissions)

## Used example APIs
* HTTP - [dog.ceo](https://dog.ceo/dog-api/)
* WebSocket - [websocket.org](https://websocket.org/tools/websocket-echo-server)

## Project launch
This project was created and tested using [IntelliJ IDEA](https://www.jetbrains.com/idea/). All further instructions are provided for this IDE.

#### Launch from IDE (PC)
To run this app on a PC, the project has several already created configurations:
* ```run [debug]``` (with debug)
* ```run [debug +args]``` (with debug and launch args)
* ```run [staging]``` (with debug and proguard)
* ```run [release]``` (with proguard)

You can also create your own launch configurations based on the ones already added. To do this, in the IDE, go to the menu:

```Run -> Edit Configurations -> + (Add New Configuration) -> Gradle```

#### Launch from IDE (Android)

To run this app on Android, use the standard ```composeApp``` configuration. The build type can be selected in the ```Build Variants``` menu in the sidebar (also available under ```Build -> Select Build Variant```).

#### App build launch (PC)
This project is configured to build the application into a jar file. The target computer requires JVM version 17 or later to be installed.

To make the application easier to launch, the project includes launch scripts (for Windows and Linux). These scripts also perform additional checks, in particular - whether the required version of the JVM is installed.

To run the compiled application, run the ```run.vbs``` (Windows) or ```run.sh``` (Linux) script located in the application's root directory. You can also create a shortcut to the application on your desktop by running the script ```create_desktop_shortcut.vbs``` / ```create_desktop_shortcut.sh```.

## Building

#### For PC
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

#### For Android
Use the standard tools provided by the IDE to build an Android application. You can build a signed APK or Bundle by going to ```Build -> Generate Signed App Bundle / APK```.

*Note: don't forget to create your own signing key and replace the stub key from this project with it. Also, don't publish the signing key (don't put it in a repository, as this project does for simplicity) if you plan to release the source code of your application to the public. You can add the ```.jks``` key file to ```gitignore```, and move its passwords from ```gradle.properties``` to ```local.properties``` (by changing the ```signingConfigs``` block in ```build.gradle.kts```).*