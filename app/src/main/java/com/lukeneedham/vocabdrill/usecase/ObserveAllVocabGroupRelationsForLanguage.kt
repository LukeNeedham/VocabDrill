package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.domain.model.VocabGroup
import com.lukeneedham.vocabdrill.domain.model.VocabGroupRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.Optional
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable
import io.reactivex.rxkotlin.combineLatest

class ObserveAllVocabGroupRelationsForLanguage(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(languageId: Long): Observable<List<VocabGroupRelations>> {
        // Uses Optionals as combineLatest will not fire on empty lists
        // A null value represents empty list

        val groupAndEntriesObservable: Observable<Optional<List<Pair<VocabGroup, List<VocabEntry>>>>> =
            vocabGroupRepository.observeAllVocabGroupsForLanguage(languageId).switchMap { groups ->
                if (groups.isEmpty()) {
                    return@switchMap Observable.just(Optional(null))
                }

                groups.map { group ->
                    vocabEntryRepository.observeAllVocabEntriesForVocabGroup(group.id)
                        .map { group to it }
                }.combineLatest { it }.map { Optional(it) }
            }

        val languageObservable = languageRepository.observeLanguageForId(languageId).map {
            return@map it
        }

        return Observable.combineLatest(
            languageObservable,
            groupAndEntriesObservable
        ) { language, groupAndEntriesListOptional ->
            val groupAndEntriesList = groupAndEntriesListOptional.value
                ?: return@combineLatest Optional(null)
            val relations = groupAndEntriesList.map { (group, entries) ->
                VocabGroupRelations(group, language, entries)
            }
            Optional(relations)
        }.map { it.value ?: emptyList() }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
