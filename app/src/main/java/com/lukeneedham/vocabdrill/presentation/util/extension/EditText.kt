package com.lukeneedham.vocabdrill.presentation.util.extension

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

fun EditText.setOnDoneListener(listener: () -> Unit) {
    setOnEditorActionListener { _, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
            actionId == EditorInfo.IME_ACTION_DONE ||
            event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
        ) {
            listener()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.setEditable(isEditable: Boolean) {
    if (!isEditable) {
        clearFocus()
    }
    isFocusable = isEditable
    isFocusableInTouchMode = isEditable
    if (isEditable) {
        requestFocus()
    }
}

fun EditText.getSelection() = TextSelection(selectionStart, selectionEnd)

fun EditText.setSelection(selection: TextSelection) {
    setSelection(selection.start, selection.end)
}
