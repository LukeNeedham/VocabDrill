package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

interface VocabEntryExistingCallback {
    fun onWordAChanged(itemPresentationData: VocabEntryItemPresentationData.Existing, newWordA: String, selection: TextSelection)
    fun onWordBChanged(itemPresentationData: VocabEntryItemPresentationData.Existing, newWordB: String, selection: TextSelection)
    fun onViewModeChanged(itemPresentationData: VocabEntryItemPresentationData.Existing, viewMode: ViewMode)
    fun onDelete(itemPresentationData: VocabEntryItemPresentationData.Existing)
}
