# Demo Kotlin Multiplatform project for Desktop (JVM)

## Launch project from IDE:

New run configuration -> Gradle -> add args:

```desktopRun -DmainClass=ru.rznnike.demokmp.app.MainKt --quiet```

## Launch with args (for the main() function):

```desktopRun --args="'foo foo2' bar" -DmainClass=ru.rznnike.demokmp.app.MainKt --quiet```

## Run exe from PowerShell with args:
```& ".\ru.rznnike.demokmp.exe" "foo foo2" bar```

## Used APIs
https://dog.ceo/dog-api/

https://websocket.org/tools/websocket-echo-server