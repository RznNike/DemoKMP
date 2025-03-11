# Демо проект Kotlin Multiplatform для Desktop (JVM)
[English](https://github.com/RznNike/DemoKMP#readme) | [Русский](/README.ru.md)

Это пример проекта с набором решений типовых задач, демонстрирующий создание приложения для ПК (JVM) с использованием [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/).

## Реализованные решения
* Архитектура проекта - [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
* Архитектура UI слоя - [Compose](https://developer.android.com/develop/ui/compose/documentation) + [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel)
* Инъекция зависимостей
* Локальная база данных (SQL)
* Хранилище настроек (с хранением пользовательских классов)
* Сборка приложения в jar файл и запуск с помощью скрипта
* Защита от запуска нескольких копий приложения одновременно
* Использование HTTP API
* Использование WebSocket API
* Конфигурация Proguard (с обфускацией)
* Двухслойная навигация между экранами (потоки экранов и экраны)
* Диалоги и снекбары по всему приложению
* Загрузка изображений по сети
* Конфигурация темы Material 3
* Темная/светлая тема с автоматическим и ручным выбором
* Локализация строк
* Параметры запуска
* Горячие клавиши в контексте экрана
* Базовый просмотр PDF, диалог печати
* Различные пользовательские элементы UI
* Встроенный логгер с собственным UI

## Скриншоты
<details>
    <summary>Основное приложение</summary>
    <img src="/readmeFiles/ru/screenshot_1.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_2.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_3.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_4.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_5.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_6.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_7.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_8.png" alt="screenshot" width="500"/>
    <img src="/readmeFiles/ru/screenshot_9.png" alt="screenshot" width="500"/>
</details>

<details>
    <summary>Логгер</summary>
    <img src="/readmeFiles/ru/screenshot_10.png" alt="screenshot" width="750"/>
    <img src="/readmeFiles/ru/screenshot_11.png" alt="screenshot" width="750"/>
</details>

## Использованные библиотеки
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

## Использованные примеры API
* HTTP - [dog.ceo](https://dog.ceo/dog-api/)
* WebSocket - [websocket.org](https://websocket.org/tools/websocket-echo-server)

## Запуск проекта
Этот проект был создан и протестирован при помощи [IntelliJ IDEA](https://www.jetbrains.com/idea/). Все дальнейшие инструкции представлены для этой IDE.

### Запуск из IDE
Для запуска приложения в проекте есть несколько уже созданных конфигураций:
* ```run [debug]``` (с отладкой)
* ```run [debug +args]``` (с отладкой и аргументами запуска)
* ```run [staging]``` (с отладкой и proguard)
* ```run [release]``` (с proguard)

Вы также можете создавать свои конфигурации запуска по примеру уже добавленных. Для этого в IDE перейдите в меню:

```Run -> Edit Configurations -> + (Add New Configuration) -> Gradle```

### Запуск собранного приложения
Данный проект настроен на сборку приложения в jar файл. При этом на целевом компьютере требуется установленная JVM версии 17 или новее.

Для простоты запуска приложения в проекте присутствуют скрипты запуска (для Windows и Linux). Эти скрипты также производят дополнительные проверки, в частности - установлена ли JVM нужной версии.

Для запуска собранного приложения запустите скрипт ```run.vbs``` (Windows) или ```run.sh``` (Linux), находящийся в корневом каталоге приложения. Также вы можете создать ярлык приложения на рабочем столе, запустив скрипт ```create_desktop_shortcut.vbs``` / ```create_desktop_shortcut.sh```.

## Сборка
Данный проект настроен для создания сборок для Windows и Linux. Если вы хотите собрать его для macOS, обновите раздел ```nativeDistributions``` в ```build.gradle.kts``` соответствующим образом.

Чтобы создать распространяемую сборку, используйте одну из конфигураций запуска:
* ```archive [staging]``` (с отладкой и proguard)
* ```archive [release]``` (с proguard)

В результате работы будет создан архив с собранной версией приложения, готовый к распространению. Собранное приложение находится в папке ```distributableOutput```, а его архив - в папке ```distributableArchive```.

Если у вас наблюдаются проблемы со сборкой приложения, рекомендуется выбрать JDK версии ```17```. Для этого:
* Выберите JDK версии ```17``` по пути ```File -> Project Structure -> Project -> SDK```, а также в ```Language level``` под ним.
* По пути ```Settings -> Build -> Compiler -> Java compiler -> Project bytecode version``` выберите ```17```.
* По пути ```Settings -> Build -> Build tools -> Gradle -> Gradle JVM``` выберите ```Project JDK```.

*Примечание: вы можете собрать приложение только для той ОС, которую используете в данный момент. Если, например, вы используете Windows и вам необходимо создать сборку для Linux, рассмотрите возможность сделать это в виртуальной машине.*

*Примечание 2: в конфигурации проекта есть фиктивный target ```android```. Он не настроен корректно (и не имеет полноценного исходного кода для работы). Этот target добавлен только для исправления бага в плагине KSP, из-за которого тот не работает, если в конфигурации проекта только один target.*