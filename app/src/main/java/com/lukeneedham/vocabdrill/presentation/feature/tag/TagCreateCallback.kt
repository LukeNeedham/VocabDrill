package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

interface TagCreateCallback {
    fun onUpdateName(
        editItem: VocabEntryEditItem,
        text: String,
        selection: TextSelection,
    )
}
