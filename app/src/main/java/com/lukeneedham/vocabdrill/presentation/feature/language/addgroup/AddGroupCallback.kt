package com.lukeneedham.vocabdrill.presentation.feature.language.addgroup

import com.lukeneedham.vocabdrill.domain.model.VocabGroupProto

interface AddGroupCallback {
    fun addGroup(groupProto: VocabGroupProto)
}
