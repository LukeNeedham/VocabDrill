package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntry

sealed class VocabEntryEditItem {
    data class Existing(val entry: VocabEntry, val viewMode: ViewMode) : VocabEntryEditItem()

    data class Create(
        val languageId: Long,
        val tags: List<Tag>,
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
