package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class CheckValidLanguageName(
    private val languageRepository: LanguageRepository
) {
    operator fun invoke(languageName: String): Single<Boolean> {

        return languageRepository.getAllLanguages().map { languages ->
            val isDuplicate = languages.any { it.name == languageName }
            !isDuplicate
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
