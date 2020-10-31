package com.lukeneedham.vocabdrill.repository.conversion

import com.lukeneedham.vocabdrill.data.persistence.model.VocabGroup as VocabGroupPersistence
import com.lukeneedham.vocabdrill.domain.model.VocabGroup as VocabGroupDomain

fun VocabGroupPersistence.toDomainModel(): VocabGroupDomain {
    return VocabGroupDomain(id, name, colour, languageId)
}
