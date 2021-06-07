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

    init {
        observeAllVocabEntryAndTagsForLanguage(languageId).subscribe {
            val existingIds = it.map { EntryKey.Existing(it.entry.id) }
            val existingAndCreate = existingIds + EntryKey.Create
            _entryKeys.value = existingAndCreate
        }
    }
}

sealed class EntryKey {
    data class Existing(val id: Long) : EntryKey()
    object Create : EntryKey()
}
