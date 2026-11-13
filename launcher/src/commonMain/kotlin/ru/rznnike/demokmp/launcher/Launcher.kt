package ru.rznnike.demokmp.launcher

private const val CONFIGURATION_FILE_NAME = "launcher_configuration.ini"
private const val APPLICATION_DIRECTORY_NAME = "application"
private const val APPLICATION_JAR_NAME = "app.jar"
private const val DEFAULT_JAVA_COMMAND = "java"
private const val JAVA_VERSION_LAUNCH_ARGUMENT = "-version"
private const val JAR_LAUNCH_ARGUMENT = "-jar"
private const val RENDER_API_ARGUMENT = "renderApi"
private const val MINIMUM_JAVA_MAJOR_VERSION = 17

internal fun runLauncher() {
    val applicationDirectory = executableDirectory()

    val configString = readTextFile(joinPath(applicationDirectory, CONFIGURATION_FILE_NAME))
    if (configString == null) {
        showErrorMessage("Launch configuration not found")
        return
    }

    val config = configString.toLauncherConfig()
    val javaCommand = config.javaPath.ifBlank { DEFAULT_JAVA_COMMAND }
    val javaVersion = captureCommandOutput(javaCommand, listOf(JAVA_VERSION_LAUNCH_ARGUMENT))
        ?.let(::parseJavaMajorVersion)

    if (javaVersion == null || javaVersion < MINIMUM_JAVA_MAJOR_VERSION) {
        showErrorMessage(formatJavaVersionError(javaVersion))
        return
    }

    val jarPath = joinPath(applicationDirectory, APPLICATION_DIRECTORY_NAME, APPLICATION_JAR_NAME)
    if (!pathExists(jarPath)) {
        showErrorMessage("Executable file not found")
        return
    }

    launchDetached(
        executable = javaCommand,
        arguments = buildLaunchArguments(
            jarPath = jarPath,
            renderApi = config.renderApi
        ),
        workingDirectory = applicationDirectory
    )
}

internal fun joinPath(vararg parts: String): String =
    parts.filter { it.isNotEmpty() }.joinToString(pathSeparator.toString())

internal fun buildLaunchArguments(jarPath: String, renderApi: String): List<String> =
    listOf(JAR_LAUNCH_ARGUMENT, jarPath, "$RENDER_API_ARGUMENT=$renderApi")

internal fun formatJavaVersionError(currentVersion: Int?): String =
    "Java $MINIMUM_JAVA_MAJOR_VERSION+ required for launch, current version - ${currentVersion ?: "none"}"