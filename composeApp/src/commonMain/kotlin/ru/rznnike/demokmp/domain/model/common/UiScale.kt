package ru.rznnike.demokmp.domain.model.common

enum class UiScale(
    val value: Int
) {
    S70(value = 70),
    S80(value = 80),
    S90(value = 90),
    S100(value = 100),
    S110(value = 110),
    S120(value = 120),
    S130(value = 130);

    companion object {
        val default = S100

        operator fun get(value: Int) = entries.find { it.value == value } ?: default
    }
}