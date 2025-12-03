package ru.rznnike.demokmp.data.utils.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

val defaultJson = Json {
    isLenient = true
    explicitNulls = false
    ignoreUnknownKeys = true
}

@OptIn(ExperimentalSerializationApi::class)
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