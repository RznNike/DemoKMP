package ru.rznnike.demokmp.data.utils

import kotlinx.serialization.json.Json

fun defaultJson() = Json {
    isLenient = true
    explicitNulls = false
    ignoreUnknownKeys = true
}