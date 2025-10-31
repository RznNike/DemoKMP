package ru.rznnike.demokmp.domain.model.common

enum class UiScale(
    val value: Int
) {
    S60(value = 60),
    S80(value = 80),
    S100(value = 100),
    S120(value = 120),
    S140(value = 140);

    companion object {
        val default = S100

        operator fun get(value: Int) = entries.find { it.value == value } ?: default
    }
}