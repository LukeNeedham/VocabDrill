package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem

interface VocabEntryExistingCallback {
    fun onWordAChanged(item: VocabEntryItem.Existing, newWordA: String)
    fun onWordBChanged(item: VocabEntryItem.Existing, newWordB: String)
    fun onDelete(item: VocabEntryItem.Existing)
}
