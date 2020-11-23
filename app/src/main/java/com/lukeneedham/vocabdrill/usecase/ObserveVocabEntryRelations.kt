package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Observable

class ObserveVocabEntryRelations(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(vocabEntryId: Long): Observable<VocabEntryRelations> {
        return vocabEntryRepository.observeVocabEntryForId(vocabEntryId).switchMap { entry ->
            vocabGroupRepository.observeVocabGroupForId(entry.vocabGroupId).switchMap { group ->
                languageRepository.observeLanguageForId(group.languageId).map { language ->
                    VocabEntryRelations(entry, language, group)
                }
            }
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
