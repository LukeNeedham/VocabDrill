package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.view.View
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem

interface TagCreateCallback {
    fun onNameChanged(
        entry: VocabEntryEditItem,
        tag: TagItem.Create,
        name: String,
        nameInputView: View
    )
}
