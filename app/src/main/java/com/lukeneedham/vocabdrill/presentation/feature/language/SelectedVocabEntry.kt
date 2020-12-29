package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.FocusItem

sealed class SelectedVocabEntry {
    object None : SelectedVocabEntry()
    data class Create(val focusItem: FocusItem) : SelectedVocabEntry()
    data class Existing(val id: Long, val focusItem: FocusItem) : SelectedVocabEntry()
}
