package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

sealed class VocabEntryItemPresentationData {
    abstract val data: VocabEntryItemData

    data class Create(override val data: VocabEntryItemData.Create, val viewMode: ViewMode) :
        VocabEntryItemPresentationData()

    data class Existing(override val data: VocabEntryItemData.Existing, val viewMode: ViewMode) :
        VocabEntryItemPresentationData()
}
