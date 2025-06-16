package ru.rznnike.demokmp.app.navigation.navtype

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
inline fun <reified T> getJsonNavType() = object : NavType<T>(
    isNullableAllowed = false
) {
    override fun put(bundle: SavedState, key: String, value: T) =
        bundle.write { putString(key, serializeAsValue(value)) }

    override fun get(bundle: SavedState, key: String): T? =
        bundle.read { parseValue(getString(key)) }

    override fun parseValue(value: String): T =
        Json.decodeFromString(String(Base64.UrlSafe.decode(value)))

    override fun serializeAsValue(value: T): String =
        Base64.UrlSafe.encode(Json.encodeToString(value).toByteArray())
}