package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabEntryTagsRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class AddTagToVocabEntry(
    private val vocabEntryTagsRepository: VocabEntryTagsRepository
) {
    operator fun invoke(vocabEntryId: Long, tagId: Long): Completable {
        return vocabEntryTagsRepository.addTagToVocabEntry(vocabEntryId, tagId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
