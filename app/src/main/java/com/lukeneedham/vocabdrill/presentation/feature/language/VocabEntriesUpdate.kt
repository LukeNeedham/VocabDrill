package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryViewProps

data class VocabEntriesUpdate(val items: List<VocabEntryViewProps>, val refresh: Boolean)
