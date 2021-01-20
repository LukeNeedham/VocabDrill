package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class DeleteUnusedTags(
    private val tagRepository: TagRepository
) {
    operator fun invoke(excludeTagIds: List<Long>): Completable {
        return tagRepository.deleteUnused(excludeTagIds)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
