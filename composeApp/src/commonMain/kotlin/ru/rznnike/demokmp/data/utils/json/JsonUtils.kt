package ru.rznnike.demokmp.data.utils.json

import kotlinx.serialization.json.Json

val defaultJson = Json {
    isLenient = true
    explicitNulls = false
    ignoreUnknownKeys = true
}

val formatterJson = Json {
    isLenient = true
    prettyPrint = true
    prettyPrintIndent = "    "
}

fun String.prettifyJson() = try {
    formatterJson.encodeToString(formatterJson.parseToJsonElement(this))
} catch (_: Exception) {
    this
}

inline fun <reified T> String?.safeDecode(): T? = try {
    this?.let { defaultJson.decodeFromString(this) }
} catch (_: Exception) { null }