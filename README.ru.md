# Демо проект Kotlin Multiplatform для Desktop (JVM) и Android
[English](https://github.com/RznNike/DemoKMP#readme) | [Русский](/README.ru.md)

<img src="/readmeFiles/KMP_logo.png" alt="logo" width="300"/>

Это пример проекта с набором решений типовых задач, демонстрирующий создание кроссплатформенного приложения для ПК (JVM - Windows/Linux) и Android с использованием [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/).

## Реализованные решения
* Архитектура проекта - [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
* Архитектура UI слоя - [Compose](https://developer.android.com/develop/ui/compose/documentation) + [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel)
* Инъекция зависимостей
* Локальная база данных (SQL)
* Хранилище настроек (с хранением пользовательских классов)
* Использование HTTP API
* Использование WebSocket API
* Конфигурация Proguard (с обфускацией)
* Двухслойная навигация между экранами (потоки экранов и экраны)
* Диалоги и снекбары по всему приложению
* Загрузка изображений по сети
* Конфигурация темы Material 3
* Темная/светлая тема с автоматическим и ручным выбором
* Локализация строк
* Различные пользовательские элементы UI
* Динамический BuildConfig с информацией о платформе и типе сборки
* Графики
* Кастомный логгер с модульной структурой

#### Только для ПК
* Сборка приложения в jar файл и запуск с помощью скрипта
* Защита от запуска нескольких копий приложения одновременно
* Параметры запуска
* Горячие клавиши в контексте экрана
* Базовый просмотр PDF, диалог печати
* Встроенный логгер с собственным UI
* UI для кастомного логгера; модули базы данных, файла и кеша в памяти

#### Только для Android
* UI для просмотра сетевых логов с устройства
* Удобный запрос разрешений

## Скриншоты
<details>
    <summary>ПК - основное приложение</summary>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_1.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_2.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_3.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_4.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_5.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_6.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_7.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_8.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_9.png" alt="screenshot" width="400"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_10.png" alt="screenshot" width="400"/>
</details>

<details>
    <summary>ПК - логгер</summary>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_11.png" alt="screenshot" width="600"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_12.png" alt="screenshot" width="600"/>
    <img src="/readmeFiles/ru/screenshots/pc/screenshot_13.png" alt="screenshot" width="600"/>
</details>

<details>
    <summary>Android</summary>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_1.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_2.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_3.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_4.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_5.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_6.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_7.jpg" alt="screenshot" width="250"/>
    <img src="/readmeFiles/ru/screenshots/android/screenshot_8.jpg" alt="screenshot" width="250"/>
</details>

## Использованные библиотеки
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

#### Только для ПК
* [PDFBox](https://pdfbox.apache.org/)
* [FileKit](https://github.com/vinceglb/FileKit)

#### Только для Android
* [Chucker](https://github.com/ChuckerTeam/chucker)
* [Accompanist (permissions)](https://github.com/google/accompanist/tree/main/permissions)

## Использованные примеры API
* HTTP - [dog.ceo](https://dog.ceo/dog-api/)
* WebSocket - [websocket.org](https://websocket.org/tools/websocket-echo-server)

## Запуск проекта
Этот проект был создан и протестирован при помощи [IntelliJ IDEA](https://www.jetbrains.com/idea/). Все дальнейшие инструкции представлены для этой IDE.

#### Запуск из IDE (ПК)
Для запуска приложения на ПК в проекте есть несколько уже созданных конфигураций:
* ```run [debug]``` (с отладкой)
* ```run [debug +args]``` (с отладкой и аргументами запуска)
* ```run [staging]``` (с отладкой и proguard)
* ```run [release]``` (с proguard)

Вы также можете создавать свои конфигурации запуска по примеру уже добавленных. Для этого в IDE перейдите в меню:

```Run -> Edit Configurations -> + (Add New Configuration) -> Gradle```

#### Запуск из IDE (Android)

Для запуска приложения на Android используйте стандартную конфигурацию ```composeApp```. Тип сборки можно выбрать в меню ```Build Variants``` в боковой панели (также доступно по пути ```Build -> Select Build Variant```).

#### Запуск собранного приложения (ПК)
Данный проект настроен на сборку приложения в jar файл (для ПК). При этом на целевом компьютере требуется установленная JVM версии 17 или новее.

Для простоты запуска приложения в проекте присутствуют скрипты запуска (для Windows и Linux). Эти скрипты также производят дополнительные проверки, в частности - установлена ли JVM нужной версии.

Для запуска собранного приложения запустите скрипт ```run.vbs``` (Windows) или ```run.sh``` (Linux), находящийся в корневом каталоге приложения. Также вы можете создать ярлык приложения на рабочем столе, запустив скрипт ```create_desktop_shortcut.vbs``` / ```create_desktop_shortcut.sh```.

## Сборка

#### Для ПК
Данный проект настроен для создания сборок для Windows и Linux. Если вы хотите собрать его для macOS, обновите раздел ```nativeDistributions``` в ```build.gradle.kts``` соответствующим образом.

Чтобы создать распространяемую сборку, используйте одну из конфигураций запуска:
* ```archive [staging]``` (с отладкой и proguard)
* ```archive [release]``` (с proguard)

В результате работы будет создан архив с собранной версией приложения, готовый к распространению. Собранное приложение находится в папке ```distributableOutput```, а его архив - в папке ```distributableArchive```.

Если у вас наблюдаются проблемы со сборкой приложения, рекомендуется выбрать JDK версии ```21```. Для этого:
* Выберите JDK версии ```21``` по пути ```File -> Project Structure -> Project -> SDK```, а также в ```Language level``` под ним.
* По пути ```Settings -> Build -> Compiler -> Java compiler -> Project bytecode version``` выберите ```21```.
* По пути ```Settings -> Build -> Build tools -> Gradle -> Gradle JVM``` выберите ```Project JDK```.

*Примечание: вы можете собрать приложение только для той ОС, которую используете в данный момент. Если, например, вы используете Windows и вам необходимо создать сборку для Linux, рассмотрите возможность сделать это в виртуальной машине.*

#### Для Android
Используйте стандартные средства для сборки Android-приложения, предоставляемые IDE. Вы можете собрать подписанный APK или Bundle, перейдя по пути ```Build -> Generate Signed App Bundle / APK```.

*Примечание: не забудьте создать собственный ключ подписи и заменить им ключ-заглушку из этого проекта. Также не публикуйте ключ подписи (не выкладывайте его в репозиторий, как сделано в этом проекте для простоты), если вы планируете выкладывать исходный код своего приложения в открытый доступ. Вы можете добавить ```.jks``` файл ключа в ```gitignore```, а также перенести пароли от него из ```gradle.properties``` в ```local.properties``` (изменив блок ```signingConfigs``` в ```build.gradle.kts```).*