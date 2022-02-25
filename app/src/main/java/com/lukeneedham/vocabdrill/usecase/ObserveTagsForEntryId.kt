package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.repository.VocabEntryTagRelationRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveTagsForEntryId(
    private val vocabEntryTagRelationRepository: VocabEntryTagRelationRepository
) {
    operator fun invoke(entryId: Long): Observable<List<Tag>> {
        return vocabEntryTagRelationRepository.observeAllTagsForVocabEntry(entryId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
