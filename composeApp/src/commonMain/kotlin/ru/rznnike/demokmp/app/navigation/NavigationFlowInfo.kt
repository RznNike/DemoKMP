package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.serialization.Serializable
import ru.rznnike.demokmp.data.utils.json.defaultJson
import ru.rznnike.demokmp.data.utils.json.safeDecode

@Serializable
class NavigationFlowInfo(
    val type: String,
    var screenCount: Int
) {
    companion object {
        val listSaver = listSaver<SnapshotStateList<NavigationFlowInfo>, String>(
            save = { dataList ->
                dataList.map { data ->
                    defaultJson.encodeToString(data)
                }
            },
            restore = { savedList ->
                savedList.mapNotNull { saved ->
                    saved.safeDecode<NavigationFlowInfo>()
                }.toMutableStateList()
            }
        )
    }
}