package ru.rznnike.demokmp.app.navigation.navtype

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.json.Json
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
val LogNetworkMessageNavType = object : NavType<LogNetworkMessage>(
    isNullableAllowed = false
) {
    override fun put(bundle: SavedState, key: String, value: LogNetworkMessage) =
        bundle.write { putString(key, serializeAsValue(value)) }

    override fun get(bundle: SavedState, key: String): LogNetworkMessage? =
        bundle.read { parseValue(getString(key)) }

    override fun parseValue(value: String): LogNetworkMessage =
        Json.decodeFromString(String(Base64.UrlSafe.decode(value)))

    override fun serializeAsValue(value: LogNetworkMessage): String =
        Base64.UrlSafe.encode(Json.encodeToString(value).toByteArray())
}