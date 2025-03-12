package ru.rznnike.demokmp.domain.model.print

enum class TwoSidedPrint(
    val id: Int
) {
    DISABLED(id = 0),
    TWO_SIDED_LONG_EDGE(id = 1),
    TWO_SIDED_SHORT_EDGE(id = 2);

    companion object {
        val default = DISABLED

        operator fun get(id: Int?) = entries.find { it.id == id } ?: default
    }
}