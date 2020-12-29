package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem

sealed class TagItem {
    data class Existing(val vocabEntryItem: VocabEntryItem, val data: Tag) : TagItem()
    class Create(val vocabEntryItem: VocabEntryItem, val name: String) : TagItem()
}
