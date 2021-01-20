package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

interface VocabEntryExistingCallback {
    fun onWordAChanged(editItem: VocabEntryEditItem.Existing, newWordA: String, selection: TextSelection)
    fun onWordBChanged(editItem: VocabEntryEditItem.Existing, newWordB: String, selection: TextSelection)
    fun onViewModeChanged(editItem: VocabEntryEditItem.Existing, viewMode: ViewMode)
    fun onDelete(editItem: VocabEntryEditItem.Existing)
}
