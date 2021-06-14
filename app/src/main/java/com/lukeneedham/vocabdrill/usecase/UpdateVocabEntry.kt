package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class UpdateVocabEntry(
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(entry: VocabEntry): Completable =
        vocabEntryRepository.updateVocabEntry(entry)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
}
