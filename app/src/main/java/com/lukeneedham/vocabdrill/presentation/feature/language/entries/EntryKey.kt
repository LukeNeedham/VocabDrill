package com.lukeneedham.vocabdrill.presentation.feature.language.entries

import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags

sealed class EntryKey {
    data class Existing(val taggedEntry: VocabEntryAndTags) : EntryKey()
    object Create : EntryKey()
}
