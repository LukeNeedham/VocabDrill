package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class FindTagNameMatches(
    private val tagRepository: TagRepository
) {
    operator fun invoke(languageId: Long, tagName: String): Single<List<Tag>> {
        return tagRepository.getAllForLanguage(languageId).map {
            it.filter { it.name.startsWith(tagName) }
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
