package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import io.reactivex.Observable

class ObserveLanguage(
    private val languageRepository: LanguageRepository
) {
    operator fun invoke(languageId: Long): Observable<Language> {
        return languageRepository.observeLanguageForId(languageId)
    }
}
