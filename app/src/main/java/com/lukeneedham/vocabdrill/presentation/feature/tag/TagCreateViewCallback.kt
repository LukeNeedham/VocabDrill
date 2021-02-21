package com.lukeneedham.vocabdrill.presentation.feature.tag

import android.view.View

interface TagCreateViewCallback {
    fun onFocused(name: String, nameInputView: View, hasFocus: Boolean)
    fun onNameChanged(name: String, nameInputView: View)
}
