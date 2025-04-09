package ru.rznnike.demokmp.domain.log

enum class LogLevel(
    val label: String
) {
    DEBUG("D"),
    INFO("I"),
    WARNING("W"),
    ERROR("E")
}