package com.lukeneedham.vocabdrill.presentation.feature.language.entries

import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabEntryAndTagsForLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VocabEntriesViewModel(
    languageId: Long,
    observeAllVocabEntryAndTagsForLanguage: ObserveAllVocabEntryAndTagsForLanguage
) {
    private val _entryKeys = MutableStateFlow<List<EntryKey>>(emptyList())
    val entryKeys = _entryKeys.asStateFlow()

    private val _selectedEntry = MutableStateFlow<EntryKey?>(null)
    val selectedEntry = _selectedEntry.asStateFlow()

    init {
        observeAllVocabEntryAndTagsForLanguage(languageId).subscribe {
            val existingKeys = it.map { EntryKey.Existing(it) }
            val allKeys = existingKeys + EntryKey.Create
            _entryKeys.value = allKeys
        }
    }

    fun onExistingEntrySelectedChange(key: EntryKey.Existing, isSelected: Boolean) {
        if (isSelected) {
            // New selection
            _selectedEntry.value = key
        } else if (_selectedEntry.value == key) {
            // Remove all selection
            _selectedEntry.value = null
        }
    }

    fun onCreateEntrySelected() {
        _selectedEntry.value = EntryKey.Create
    }
}
