package ru.rznnike.demokmp.data.preference

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath
import ru.rznnike.demokmp.domain.common.DispatcherProvider

expect fun getDataStorePath() : String

const val SETTINGS_PREFERENCES = "app_data/settings.preferences_pb"

fun createDataStore(
    dispatcherProvider: DispatcherProvider
) = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    scope = CoroutineScope(dispatcherProvider.io + SupervisorJob()),
    migrations = emptyList(),
    produceFile = {
        getDataStorePath().toPath()
    }
)