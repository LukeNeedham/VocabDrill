package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem

sealed class VocabEntryEditItem {
    data class Existing(
        val entry: VocabEntry,
        val tagItems: List<TagItem>,
        val viewMode: ViewMode
    ) : VocabEntryEditItem()

    data class Create(
        val languageId: Long,
        val tags: List<TagItem>,
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
