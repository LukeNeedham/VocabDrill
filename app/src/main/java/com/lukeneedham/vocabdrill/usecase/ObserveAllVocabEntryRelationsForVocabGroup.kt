package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveAllVocabEntryRelationsForVocabGroup(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(vocabGroupId: Long): Observable<List<VocabEntryRelations>> {

        val groupAndLanguageObservable =
            vocabGroupRepository.observeVocabGroupForId(vocabGroupId).switchMap {
                Observable.combineLatest(
                    Observable.just(it),
                    languageRepository.observeLanguageForId(it.languageId)
                ) { group, language ->
                    group to language
                }
            }

        val entriesObservable =
            vocabEntryRepository.observeAllVocabEntriesForVocabGroup(vocabGroupId)
                .subscribeOn(RxSchedulers.database)
                .observeOn(RxSchedulers.main)

        return Observable.combineLatest(
            groupAndLanguageObservable,
            entriesObservable
        ) { (group, language), entries ->
            entries.map {
                VocabEntryRelations(it, language, group)
            }
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
