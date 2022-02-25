package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveVocabEntryAndTagsForId(
    private val observeTagsForEntryId: ObserveTagsForEntryId,
    private val vocabEntryRepository: VocabEntryRepository
) {

    operator fun invoke(entryId: Long): Observable<VocabEntryAndTags> {

        val entryObservable = vocabEntryRepository.observeVocabEntryForId(entryId)
        val tagsObservable = observeTagsForEntryId(entryId)
        val entryAndTagsObservable =
            Observable.combineLatest(entryObservable, tagsObservable) { entry, tags ->
                VocabEntryAndTags(entry, tags)
            }

        return entryAndTagsObservable
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}

