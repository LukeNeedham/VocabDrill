package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabGroup
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveVocabGroup(
    private val vocabGroupRepository: VocabGroupRepository
) {
    operator fun invoke(languageId: Long): Observable<VocabGroup> {
        return vocabGroupRepository.observeVocabGroupForId(languageId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
