package ru.rznnike.demokmp.launcher

fun main() {
    try {
        runLauncher()
    } catch (error: Throwable) {
        showErrorMessage("Launch error: ${error.message ?: "unknown"}")
    }
}
