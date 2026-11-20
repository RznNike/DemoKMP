package ru.rznnike.demokmp.launcher

internal fun parseJavaMajorVersion(input: String): Int? {
    val versionSubstring = input
        .substringAfter("version ")
        .replace("\"", "")
        .trim()
        .lines()
        .firstOrNull()
    if (versionSubstring.isNullOrBlank()) return null

    val components = versionSubstring.split('.')
    val majorComponent = if (versionSubstring.startsWith("1.")) {
        components.getOrNull(1)
    } else {
        components.firstOrNull()
    }

    return majorComponent
        ?.takeWhile { it.isDigit() }
        ?.takeIf { it.isNotEmpty() }
        ?.toIntOrNull()
}
