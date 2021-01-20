package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabEntryTagRelationRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class DeleteTagFromVocabEntry(
    private val vocabEntryTagRelationRepository: VocabEntryTagRelationRepository
) {
    operator fun invoke(vocabEntryId: Long, tagId: Long): Completable {
        return vocabEntryTagRelationRepository.deleteTagFromVocabEntry(vocabEntryId, tagId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
