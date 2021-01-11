package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.zip
import io.reactivex.Completable

class AddVocabEntry(
    private val addTagToVocabEntry: AddTagToVocabEntry,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(entry: VocabEntryProto): Completable =
        vocabEntryRepository.addVocabEntry(entry.wordA, entry.wordB, entry.languageId)
            .flatMap { entryId ->
                val tagAdditions = entry.tags.map { tag ->
                    addTagToVocabEntry(entryId, tag.id)
                }
                tagAdditions.zip()
            }
            .ignoreElement()
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
}
