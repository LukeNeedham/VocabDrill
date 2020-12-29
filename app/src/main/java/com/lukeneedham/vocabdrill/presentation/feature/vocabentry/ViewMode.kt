package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

sealed class ViewMode {
    object Inactive : ViewMode()
    data class Active(val focusItem: FocusItem) : ViewMode()
}
