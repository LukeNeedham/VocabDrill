package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class ExtractFlagColoursFromLanguageId(
    private val languageRepository: LanguageRepository,
    private val extractFlagColoursFromCountry: ExtractFlagColoursFromCountry
) {
    operator fun invoke(languageId: Long): Single<List<Int>> {
        return languageRepository.requireLanguageForId(languageId)
            .map { language ->
                extractFlagColoursFromCountry(language.country)
            }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
