package ru.rznnike.demokmp.app.viewmodel.global.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel

class ProfileViewModel : BaseViewModel() {
    var nameInput by mutableStateOf("John Wick")
        private set

    fun onNameInput(newValue: String) {
        nameInput = newValue
    }
}