package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagPresentItem

sealed class VocabEntryEditItem {
    abstract val tagItems: List<TagPresentItem>

    /** @return whether [other] represents the same item as this */
    abstract fun isSameItem(other: VocabEntryEditItem): Boolean

    data class Existing(
        val entry: VocabEntry,
        override val tagItems: List<TagPresentItem>
    ) : VocabEntryEditItem() {

        override fun isSameItem(other: VocabEntryEditItem) =
            other is Existing && this.entry.id == other.entry.id
    }

    data class Create(
        val languageId: Long,
        override val tagItems: List<TagPresentItem>,
        val wordA: String?,
        val wordB: String?
    ) : VocabEntryEditItem() {

        override fun isSameItem(other: VocabEntryEditItem) = other is Create
    }
}
