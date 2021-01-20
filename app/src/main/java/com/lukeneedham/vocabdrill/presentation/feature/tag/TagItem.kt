package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem

sealed class TagItem {
    data class Existing(val vocabEntryItem: VocabEntryEditItem, val data: Tag) : TagItem()
    class Create(val vocabEntryItem: VocabEntryEditItem) : TagItem()
}
