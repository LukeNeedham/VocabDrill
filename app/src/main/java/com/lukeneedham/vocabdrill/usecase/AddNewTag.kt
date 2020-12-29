package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagProto
import com.lukeneedham.vocabdrill.repository.LanguageTagsRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Single

class AddNewTag(
    private val languageTagsRepository: LanguageTagsRepository
) {
    operator fun invoke(proto: TagProto): Single<Tag> {
        return languageTagsRepository.addNewTag(proto)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
