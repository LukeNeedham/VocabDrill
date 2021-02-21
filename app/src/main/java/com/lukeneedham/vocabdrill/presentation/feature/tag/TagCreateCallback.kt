package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.view.View
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem

interface TagCreateCallback {
    fun onFocusChange(
        entry: VocabEntryEditItem,
        name: String,
        nameInputView: View,
        hasFocus: Boolean
    )

    fun onNameChanged(
        entry: VocabEntryEditItem,
        name: String,
        nameInputView: View
    )

    fun onBound(entry: VocabEntryEditItem)
}
