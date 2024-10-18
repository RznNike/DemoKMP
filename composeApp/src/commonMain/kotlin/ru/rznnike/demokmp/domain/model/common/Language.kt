package ru.rznnike.demokmp.domain.model.common

enum class Language(
    val tag: String,
    val localizedName: String
) {
    RU("ru-RU", "Русский"),
    EN("en-US", "English");

    companion object {
        val default = EN

        operator fun get(tag: String) = entries.find { it.tag == tag } ?: default
    }
}