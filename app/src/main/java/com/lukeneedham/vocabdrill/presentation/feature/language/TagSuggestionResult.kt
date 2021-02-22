package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem

data class TagSuggestionResult(
    val item: VocabEntryEditItem,
    val suggestions: List<TagSuggestion>
    )
