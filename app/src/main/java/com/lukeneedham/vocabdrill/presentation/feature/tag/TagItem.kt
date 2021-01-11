package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemData

sealed class TagItem {
    data class Existing(val vocabEntryItem: VocabEntryItemData, val data: Tag) : TagItem()
    class Create(val vocabEntryItem: VocabEntryItemData) : TagItem()
}
