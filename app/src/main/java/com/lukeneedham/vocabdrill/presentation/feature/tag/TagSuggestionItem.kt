package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem

sealed class TagSuggestionItem {
    data class Existing(val vocabEntryItem: VocabEntryEditItem, val data: Tag) : TagSuggestionItem()
    class Create(val vocabEntryItem: VocabEntryEditItem, val name: String, val color: Int) : TagSuggestionItem()
}
