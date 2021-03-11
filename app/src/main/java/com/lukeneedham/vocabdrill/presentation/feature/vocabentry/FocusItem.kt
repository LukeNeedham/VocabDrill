package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

sealed class FocusItem {
    data class WordA(val selection: TextSelection) : FocusItem()
    data class WordB(val selection: TextSelection) : FocusItem()
    data class AddTag(
        val selection: TextSelection,
        val text: String,
        val tagSuggestions: List<TagSuggestion>?
    ) : FocusItem()

    object None : FocusItem()
}

