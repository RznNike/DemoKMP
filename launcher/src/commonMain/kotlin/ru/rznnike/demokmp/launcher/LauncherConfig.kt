package ru.rznnike.demokmp.launcher

private const val JAVA_PATH_KEY = "java_path"
private const val RENDER_API_KEY = "render_api"

data class LauncherConfig(
    val javaPath: String = "",
    val renderApi: String = ""
)

fun String.toLauncherConfig(): LauncherConfig {
    val configMap = split("\n", "\r")
        .filter { it.isNotBlank() }
        .associate { line ->
            val parts = line.split("=", limit = 2)
            (parts.getOrNull(0) ?: "") to (parts.getOrNull(1) ?: "")
        }
    return LauncherConfig(
        javaPath = configMap[JAVA_PATH_KEY] ?: "",
        renderApi = configMap[RENDER_API_KEY] ?: ""
    )
}