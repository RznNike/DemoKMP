package ru.rznnike.demokmp.app.viewmodel.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.rznnike.demokmp.app.common.viewmodel.BaseViewModel

class ProfileViewModel : BaseViewModel() {
    var nameInput by mutableStateOf("")
        private set

    fun onNameInput(newValue: String) {
        nameInput = newValue
    }
}