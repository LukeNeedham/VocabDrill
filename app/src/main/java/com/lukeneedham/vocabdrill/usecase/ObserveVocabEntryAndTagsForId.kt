package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryTagRelationRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveVocabEntryAndTagsForId(
    private val vocabEntryRepository: VocabEntryRepository,
    private val vocabEntryTagRelationRepository: VocabEntryTagRelationRepository
) {

    operator fun invoke(entryId: Long): Observable<VocabEntryAndTags> {

        val entryObservable = vocabEntryRepository.observeVocabEntryForId(entryId)
        val tagsObservable = vocabEntryTagRelationRepository.observeAllTagsForVocabEntry(entryId)
        val entryAndTagsObservable =
            Observable.combineLatest(entryObservable, tagsObservable) { entry, tags ->
                VocabEntryAndTags(entry, tags)
            }

        return entryAndTagsObservable
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
