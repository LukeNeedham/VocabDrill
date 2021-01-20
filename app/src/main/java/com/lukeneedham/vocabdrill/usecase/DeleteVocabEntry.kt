package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class DeleteVocabEntry(
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(vocabEntryId: Long): Completable {
        return vocabEntryRepository.deleteVocabEntry(vocabEntryId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
