package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemData

sealed class TagSuggestionItem {
    data class Existing(val vocabEntryItem: VocabEntryItemData, val data: Tag) : TagSuggestionItem()
    class Create(val vocabEntryItem: VocabEntryItemData, val name: String, val color: Int) : TagSuggestionItem()
}
