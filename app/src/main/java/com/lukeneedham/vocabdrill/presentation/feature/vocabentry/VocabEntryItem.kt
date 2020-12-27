package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations

sealed class VocabEntryItem {
    data class Existing(
        val data: VocabEntryRelations,
        val loadingState: LoadingState
    ) : VocabEntryItem() {
        companion object {
            fun newInstance(data: VocabEntryRelations) = Existing(data, LoadingState.Ready)
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
