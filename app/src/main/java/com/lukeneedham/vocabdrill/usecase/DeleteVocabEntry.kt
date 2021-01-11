package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable

class DeleteVocabEntry(
    private val vocabEntryRepository: VocabEntryRepository,
    private val tagRepository: TagRepository
) {
    operator fun invoke(vocabEntryId: Long): Completable {
        return vocabEntryRepository.deleteVocabEntry(vocabEntryId)
            // Clean up tags which may now be unused
            // TODO: This should also be called when deleting a tag from any entry
            .andThen(tagRepository.deleteUnused())
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
