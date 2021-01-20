package com.lukeneedham.vocabdrill.repository.conversion

import com.lukeneedham.vocabdrill.domain.model.VocabEntryPartial
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry as VocabEntryPersistence
import com.lukeneedham.vocabdrill.domain.model.VocabEntry as VocabEntryDomain

fun VocabEntryPersistence.toPartialModel(): VocabEntryPartial {
    return VocabEntryPartial(id, wordA, wordB, languageId)
}

fun VocabEntryPartial.withTags(tags: List<Tag>) =
    VocabEntryDomain(id, wordA, wordB, languageId, tags)

fun VocabEntryDomain.toPersistenceModel(): VocabEntryPersistence {
    val persistence = VocabEntryPersistence(languageId, wordA, wordB)
    persistence.id = id
    return persistence
}
