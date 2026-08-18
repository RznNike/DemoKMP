package ru.rznnike.demokmp.domain.model.common

enum class Theme(
    val id: Int
) {
    AUTO(id = 0),
    LIGHT(id = 1),
    LIGHT_CONTRAST(id = 2),
    DARK(id = 3),
    DARK_CONTRAST(id = 4);

    companion object {
        val default = AUTO

        operator fun get(id: Int) = entries.find { it.id == id } ?: default
    }
}