package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveVocabGroupRelations(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(vocabGroupId: Long): Observable<VocabGroupRelations> {
        val getVocabGroupAndLanguageSingle = vocabGroupRepository
            .observeVocabGroupForId(vocabGroupId)
            .switchMap {
                Observable.combineLatest(
                    Observable.just(it),
                    languageRepository.observeLanguageForId(it.languageId)
                ) { vocabGroup, language ->
                    vocabGroup to language
                }
            }
        return Observable.combineLatest(
            getVocabGroupAndLanguageSingle,
            vocabEntryRepository.observeAllVocabEntriesForVocabGroup(vocabGroupId)
        ) { (vocabGroup, language), entries ->
            VocabGroupRelations(vocabGroup, language, entries)
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
