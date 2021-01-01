package com.lukeneedham.vocabdrill.presentation.util

sealed class TextSelection {
    data class Range(val start: Int, val end: Int) : TextSelection()
    object End : TextSelection()

    companion object {
        val Start = Range(0, 0)
    }
}

