# Демо проект Kotlin Multiplatform для Desktop (JVM)
[English](https://github.com/RznNike/DemoKMP#readme) | [Русский](/README.ru.md)

Это пример проекта с набором решений типовых задач, демонстрирующий создание приложения для ПК (JVM) с использованием [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/).

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
* Параметры запуска
* Горячие клавиши

## Использованные библиотеки
* [Voyager](https://voyager.adriel.cafe/)
* [Koin](https://insert-koin.io/)
* [Room](https://developer.android.com/jetpack/androidx/releases/room)
* [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
* [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* [Ktor](https://ktor.io/)
* [Ktorfit](https://foso.github.io/Ktorfit/)
* [Coil](https://coil-kt.github.io/coil/)
* [BuildKonfig](https://github.com/yshrsmz/BuildKonfig)

## Использованные примеры API
* HTTP - [dog.ceo](https://dog.ceo/dog-api/)
* WebSocket - [websocket.org](https://websocket.org/tools/websocket-echo-server)

## Запуск проекта
Этот проект был создан и протестирован при помощи [IntelliJ IDEA](https://www.jetbrains.com/idea/). Все дальнейшие инструкции представлены для этой IDE.

### Стандартный запуск
В IDE:

```Run -> Edit Configurations -> + (Add New Configuration) -> Gradle```

Под заголовком ```Run``` добавьте эти аргументы в поле ввода, затем сохраните конфигурацию:

```desktopRun -DmainClass=ru.rznnike.demokmp.app.MainKt --quiet```

### Запуск с параметрами
Вы можете запустить Kotlin приложение с параметрами, которые будут переданы в функцию main().

#### Запуск из IDE
Создайте конфигурацию запуска с аргументами по примеру:

```desktopRun --args="'foo foo2' bar" -DmainClass=ru.rznnike.demokmp.app.MainKt --quiet```

Этот пример запустит программу с двумя аргументами: "foo foo2" и "bar".

#### Запуск .exe из PowerShell
```& ".\ru.rznnike.demokmp.exe" "foo foo2" bar```

## Сборка
Этот проект настроен для создания сборок для Windows и Linux. Если вы хотите собрать его для macOS, обновите раздел ```nativeDistributions``` в ```build.gradle.kts``` соответствующим образом.

Чтобы создать распространяемую сборку, запустите задачу Gradle ```createDistributable``` (без Proguard) или ```createReleaseDistributable``` (с Proguard).

Чтобы создать установщик, запустите ```packageXXX``` or ```packageReleaseXXX``` соответственно (где ```XXX``` - это ```Msi``` или ```Deb``` в зависимости от вашей ОС).

*Примечание: вы можете собрать приложение только для той ОС, которую используете в данный момент. Если, например, вы используете Windows и вам необходимо создать сборку для Linux, рассмотрите возможность сделать это в виртуальной машине.*

*Примечание 2: в конфигурации проекта есть фиктивный target ```android```. Он не настроен корректно (и не имеет полноценного исходного кода для работы). Этот target добавлен только для исправления бага в плагине KSP, из-за которого тот не работает, если в конфигурации проекта только один target.*