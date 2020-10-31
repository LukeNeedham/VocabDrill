package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import io.reactivex.Observable
import io.reactivex.Single

class ObserveAllVocabEntryRelationsForVocabGroup(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(vocabGroupId: Long): Observable<List<VocabEntryRelations>> {

        val groupAndLanguageObservable =
            vocabGroupRepository.requireVocabGroupForId(vocabGroupId).flatMap {
                Single.zip(
                    Single.just(it),
                    languageRepository.requireLanguageForId(it.languageId)
                ) { group, language ->
                    group to language
                }
            }.repeat().toObservable()

        val entriesObservable =
            vocabEntryRepository.observeAllVocabEntriesForVocabGroup(vocabGroupId)

        return Observable.zip(
            groupAndLanguageObservable,
            entriesObservable
        ) { (group, language), entries ->
            entries.map {
                VocabEntryRelations(it, language, group)
            }
        }
    }
}
