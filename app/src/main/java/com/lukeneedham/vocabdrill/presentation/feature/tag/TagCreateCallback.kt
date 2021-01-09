package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.view.View

interface TagCreateCallback {
    fun onNameChanged(tagItem: TagItem.Create, name: String, nameInputView: View)
}
