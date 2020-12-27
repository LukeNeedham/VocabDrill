package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem

interface VocabEntryCreateCallback {
    fun onWordAChanged(item: VocabEntryItem.Create, newWordA: String)
    fun onWordBChanged(item: VocabEntryItem.Create, newWordA: String)
    fun save(proto: VocabEntryProto)
}
