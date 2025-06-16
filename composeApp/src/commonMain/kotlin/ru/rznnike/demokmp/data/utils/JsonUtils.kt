package ru.rznnike.demokmp.data.utils

import kotlinx.serialization.json.Json

val defaultJson = Json {
    isLenient = true
    explicitNulls = false
    ignoreUnknownKeys = true
}