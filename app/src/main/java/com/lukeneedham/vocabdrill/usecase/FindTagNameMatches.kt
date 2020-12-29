package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.repository.LanguageTagsRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class FindTagNameMatches(
    private val languageTagsRepository: LanguageTagsRepository
) {
    operator fun invoke(languageId: Long, tagName: String): Single<List<Tag>> {
        return languageTagsRepository.getAllForLanguage(languageId).map {
            it.filter { it.name.startsWith(tagName) }
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
