package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

interface VocabEntryCreateCallback {
    fun onWordAChanged(newWordA: String, selection: TextSelection)
    fun onWordBChanged(newWordA: String, selection: TextSelection)
    fun onInteraction()
    fun save(proto: VocabEntryProto)
}
