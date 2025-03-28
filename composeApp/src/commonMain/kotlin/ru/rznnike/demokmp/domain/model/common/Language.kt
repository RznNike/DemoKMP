package ru.rznnike.demokmp.domain.model.common

enum class Language(
    val shortTag: String,
    val fullTag: String,
    val localizedName: String
) {
    RU(
        shortTag = "ru",
        fullTag = "ru-RU",
        localizedName = "Русский"
    ),
    EN(
        shortTag = "en",
        fullTag = "en-US",
        localizedName = "English"
    );

    companion object {
        val default = EN

        operator fun get(tag: String) = entries.find { it.fullTag == tag } ?: default

        fun getByShortTag(tag: String) = entries.find { it.shortTag == tag } ?: default
    }
}