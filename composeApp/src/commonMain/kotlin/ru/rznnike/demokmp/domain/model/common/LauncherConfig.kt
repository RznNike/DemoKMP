package ru.rznnike.demokmp.domain.model.common

private const val JAVA_PATH_KEY = "java_path"
private const val SINGLE_INSTANCE_PORT_KEY = "single_instance_port"

private const val SINGLE_INSTANCE_PORT_DEFAULT = 62740

data class LauncherConfig(
    val javaPath: String = "",
    val singleInstancePort: Int = SINGLE_INSTANCE_PORT_DEFAULT
)

fun String.toLauncherConfig(): LauncherConfig {
    val configMap = split("\n").associate { line ->
        val parts = line.split("=")
        (parts.getOrNull(0) ?: "") to (parts.getOrNull(1) ?: "")
    }
    return LauncherConfig(
        javaPath = configMap[JAVA_PATH_KEY] ?: "",
        singleInstancePort = configMap[SINGLE_INSTANCE_PORT_KEY]?.toIntOrNull() ?: SINGLE_INSTANCE_PORT_DEFAULT
    )
}
