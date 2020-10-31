package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.addentry

import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto

interface AddEntryCallback {
    fun addEntry(entryProto: VocabEntryProto)
}
