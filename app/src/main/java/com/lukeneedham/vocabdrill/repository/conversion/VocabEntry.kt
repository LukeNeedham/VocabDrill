package com.lukeneedham.vocabdrill.repository.conversion

import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry as VocabEntryPersistence
import com.lukeneedham.vocabdrill.domain.model.VocabEntry as VocabEntryDomain

fun VocabEntryPersistence.toDomainModel(): VocabEntryDomain {
    return VocabEntryDomain(id, wordA, wordB, vocabGroupId)
}
