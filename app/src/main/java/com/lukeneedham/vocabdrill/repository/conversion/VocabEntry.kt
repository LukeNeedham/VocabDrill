package com.lukeneedham.vocabdrill.repository.conversion

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.data.persistence.db.model.VocabEntry as VocabEntryPersistence
import com.lukeneedham.vocabdrill.domain.model.VocabEntry as VocabEntryDomain

fun VocabEntryPersistence.toPartialModel(): VocabEntryDomain {
    return VocabEntryDomain(id, wordA, wordB, languageId)
}

fun VocabEntryDomain.withTags(tags: List<Tag>) = VocabEntryAndTags(this, tags)

fun VocabEntryDomain.toPersistenceModel(): VocabEntryPersistence {
    val persistence = VocabEntryPersistence(languageId, wordA, wordB)
    persistence.id = id
    return persistence
}
