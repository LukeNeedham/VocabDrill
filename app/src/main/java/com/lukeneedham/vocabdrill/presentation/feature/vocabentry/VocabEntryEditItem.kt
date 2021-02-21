package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagPresentItem

sealed class VocabEntryEditItem {
    abstract val tagItems: List<TagPresentItem>

    data class Existing(
        val entry: VocabEntry,
        override val tagItems: List<TagPresentItem>,
        val viewMode: ViewMode
    ) : VocabEntryEditItem()

    data class Create(
        val languageId: Long,
        override val tagItems: List<TagPresentItem>,
        val wordA: String?,
        val wordB: String?,
        val viewMode: ViewMode
    ) : VocabEntryEditItem() {

        companion object {
            fun newInstance(languageId: Long) =
                Create(languageId, emptyList(), null, null, ViewMode.Inactive)
        }
    }
}
