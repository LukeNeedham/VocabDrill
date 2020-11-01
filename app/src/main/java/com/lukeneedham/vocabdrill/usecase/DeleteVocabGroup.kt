package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class DeleteVocabGroup(
    private val vocabGroupRepository: VocabGroupRepository
) {
    operator fun invoke(vocabGroupId: Long): Completable {
        return vocabGroupRepository.deleteVocabGroup(vocabGroupId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
