package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.usecase.ObserveVocabEntryForId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExistingEntryItemViewModel(
    private val entryId: Long,
    observeVocabEntry: ObserveVocabEntryForId
) {
    private var vocabEntry: VocabEntry? = null

    private val _wordA = MutableStateFlow<String>("")
    val wordA: StateFlow<String> = _wordA

    private val _wordB = MutableStateFlow<String>("")
    val wordB: StateFlow<String> = _wordB

    private val _mode = MutableStateFlow<EntryMode>(EntryMode.View)
    val mode: StateFlow<EntryMode> = _mode

    init {
        observeVocabEntry(entryId).subscribe {
            onVocabEntryLoad(it)
        }
    }

    private fun onVocabEntryLoad(entry: VocabEntry) {
        vocabEntry = entry
        _wordA.value = entry.wordA
        _wordB.value = entry.wordB
    }

    fun onWordAChange(text: String) {
        _wordA.value = text
        // TODO: Save update
    }

    fun onWordBChange(text: String) {
        _wordB.value = text
        // TODO: Save update
    }
}
