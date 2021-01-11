package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabEntryTagRelationRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class AddTagToVocabEntry(
    private val vocabEntryTagRelationRepository: VocabEntryTagRelationRepository
) {
    operator fun invoke(vocabEntryId: Long, tagId: Long): Single<Long> {
        return vocabEntryTagRelationRepository.addTagToVocabEntry(vocabEntryId, tagId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
