package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class AddVocabEntry(
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(entry: VocabEntryProto): Completable =
        vocabEntryRepository.addVocabEntry(entry)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
}
