package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabGroupRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.zip

class ObserveAllVocabGroupRelationsForLanguage(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(languageId: Long): Observable<List<VocabGroupRelations>> {
        val groupAndEntriesObservable =
            vocabGroupRepository.observeAllVocabGroupsForLanguage(languageId).flatMap { groups ->
                groups.map { group ->
                    vocabEntryRepository.observeAllVocabEntriesForVocabGroup(group.id)
                        .map { group to it }
                }.zip { it }
            }

        val languageObservable =
            languageRepository.requireLanguageForId(languageId).repeat().toObservable()

        return Observable.zip(
            languageObservable,
            groupAndEntriesObservable
        ) { language, groupAndEntriesList ->
            groupAndEntriesList.map { (group, entries) ->
                VocabGroupRelations(group, language, entries)
            }
        }
    }
}
