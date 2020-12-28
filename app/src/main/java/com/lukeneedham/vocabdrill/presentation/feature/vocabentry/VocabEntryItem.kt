package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations

sealed class VocabEntryItem {
    data class Existing(
        val entryId: Long,
        val languageId: Long,
        val tags: List<Tag>,
        val wordA: String,
        val wordB: String
    ) : VocabEntryItem() {
        companion object {
            fun newInstance(data: VocabEntryRelations) = Existing(
                data.vocabEntry.id,
                data.language.id,
                data.tags,
                data.vocabEntry.wordA,
                data.vocabEntry.wordB
            )
        }
    }

    data class Create(
        val languageId: Long,
        val tags: List<Tag>,
        val wordA: String?,
        val wordB: String?
    ) : VocabEntryItem() {

        companion object {
            fun newInstance(languageId: Long) =
                Create(languageId, emptyList(), null, null)
        }
    }
}
