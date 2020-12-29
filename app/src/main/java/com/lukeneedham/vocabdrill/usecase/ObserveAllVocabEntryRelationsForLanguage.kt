package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryTagsRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.zip
import io.reactivex.Observable

class ObserveAllVocabEntryRelationsForLanguage(
    private val languageRepository: LanguageRepository,
    private val vocabEntryRepository: VocabEntryRepository,
    private val vocabEntryTagsRepository: VocabEntryTagsRepository
) {
    operator fun invoke(languageId: Long): Observable<List<VocabEntryRelations>> {
        return languageRepository.observeLanguageForId(languageId).switchMap { language ->
            vocabEntryRepository.observeAllVocabEntriesForLanguage(languageId).switchMap { entries ->
                val observablesList = entries.map { entry ->
                    vocabEntryTagsRepository.observeAllTagsForVocabEntry(entry.id).map { tags ->
                        VocabEntryRelations(entry, language, tags)
                    }
                }
                observablesList.zip()
            }
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
