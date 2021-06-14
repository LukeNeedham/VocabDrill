package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateEntryItemViewModel {

    private val _wordA = MutableStateFlow<String>("")
    val wordA: StateFlow<String> = _wordA

    private val _wordB = MutableStateFlow<String>("")
    val wordB: StateFlow<String> = _wordB

    private val _mode = MutableStateFlow<EntryMode>(EntryMode.View)
    val mode: StateFlow<EntryMode> = _mode

    fun onWordAChange(text: String) {
        _wordA.value = text
        // TODO: Save update
    }

    fun onWordBChange(text: String) {
        _wordB.value = text
        // TODO: Save update
    }
}
