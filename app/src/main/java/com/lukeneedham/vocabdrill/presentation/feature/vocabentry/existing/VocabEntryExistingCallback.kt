package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem

interface VocabEntryExistingCallback {
    fun onDelete(item: VocabEntryItem.Existing)
}
