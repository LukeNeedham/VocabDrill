package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import io.reactivex.Observable

class ObserveVocabEntryForId(private val repository: VocabEntryRepository) {
    operator fun invoke(id: Long): Observable<VocabEntry> = repository.observeVocabEntryForId(id)
}
