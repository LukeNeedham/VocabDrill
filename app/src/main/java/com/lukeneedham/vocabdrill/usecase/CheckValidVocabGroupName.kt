package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class CheckValidVocabGroupName(
    private val vocabGroupRepository: VocabGroupRepository
) {
    operator fun invoke(languageId: Long, vocabGroupName: String): Single<Boolean> {
        return vocabGroupRepository.getAllVocabGroupsForLanguage(languageId).map { groups ->
            val isDuplicate = groups.any { it.name == vocabGroupName }
            !isDuplicate
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
