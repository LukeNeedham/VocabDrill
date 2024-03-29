package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class DeleteLanguage(
    private val languageRepository: LanguageRepository
) {
    operator fun invoke(languageId: Long): Completable {
        return languageRepository.deleteLanguage(languageId)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
