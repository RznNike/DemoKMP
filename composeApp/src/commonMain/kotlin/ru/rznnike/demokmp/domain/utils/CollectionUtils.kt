package ru.rznnike.demokmp.domain.utils

fun <T> List<T>.smartFilter(
    query: String,
    stringRetrievers: List<(T) -> String>
): List<T> {
    val preparedQuery = query.prepareTextForSearch()

    return filter { item ->
        stringRetrievers.any { retriever ->
            retriever(item)
                .prepareTextForSearch()
                .contains(preparedQuery)
        }
    }
}

fun String.prepareTextForSearch() = trim()
    .lowercase()
    .replace("ё", "е")

fun List<Any>?.getNewSelectionIndex(
    currentIndex: Int,
    offset: Int
): Int {
    var newIndex = currentIndex + offset
    val lastIndex = this?.lastIndex ?: 0
    when {
        newIndex < 0 -> newIndex = lastIndex
        newIndex > lastIndex -> newIndex = 0
    }
    return newIndex
}