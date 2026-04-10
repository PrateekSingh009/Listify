package com.example.listify.presentation.screens.utils.extensions

fun String.toHeadlineCase(): String {
    val minorWords = setOf("a", "an", "the", "and", "but", "or", "for", "nor", "on", "at", "to", "from", "by", "in", "of")
    val words = this.lowercase().split(" ")

    return words.mapIndexed { index, word ->
        if (index != 0 && index != words.lastIndex && word in minorWords) {
            word // Keep minor words lowercase if they aren't first or last
        } else {
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }.joinToString(" ")
}