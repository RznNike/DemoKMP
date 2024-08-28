package ru.rznnike.demokmp.domain.model.common

enum class Theme(
    val id: Int
) {
    AUTO(id = 0),
    LIGHT(id = 1),
    DARK(id = 2);

    companion object {
        val default = AUTO

        operator fun get(id: Int) = entries.find { it.id == id } ?: default
    }
}