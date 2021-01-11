package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagProto
import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class AddNewTag(
    private val tagRepository: TagRepository
) {
    operator fun invoke(proto: TagProto): Single<Tag> {
        return tagRepository.addNewTag(proto)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
