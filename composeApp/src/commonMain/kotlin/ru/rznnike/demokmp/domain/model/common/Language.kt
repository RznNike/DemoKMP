package ru.rznnike.demokmp.domain.model.common

enum class Language(
    val tag: String,
    val localizedName: String
) {
    RU("ru", "Русский"),
    EN("en", "English");

    companion object {
        val default = RU

        operator fun get(tag: String) = entries.find { it.tag == tag } ?: default
    }
}