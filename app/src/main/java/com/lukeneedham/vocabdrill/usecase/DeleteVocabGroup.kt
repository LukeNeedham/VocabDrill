package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.LanguageRepository
import io.reactivex.Completable

class DeleteVocabGroup(
    private val languageRepository: LanguageRepository
) {
    operator fun invoke(languageId: Long): Completable {
        return languageRepository.deleteLanguage(languageId)
    }
}
