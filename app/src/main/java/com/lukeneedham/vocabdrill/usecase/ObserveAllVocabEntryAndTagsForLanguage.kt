package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryTagRelationRepository
import com.lukeneedham.vocabdrill.repository.conversion.withTags
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.zip
import io.reactivex.Observable

class ObserveAllVocabEntryAndTagsForLanguage(
    private val languageRepository: LanguageRepository,
    private val vocabEntryRepository: VocabEntryRepository,
    private val vocabEntryTagRelationRepository: VocabEntryTagRelationRepository
) {
    operator fun invoke(languageId: Long): Observable<List<VocabEntryAndTags>> {
        return languageRepository.observeLanguageForId(languageId).switchMap { language ->
            vocabEntryRepository.observeAllVocabEntriesForLanguage(languageId)
                .switchMap { entries ->
                    val observablesList = entries.map { entry ->
                        vocabEntryTagRelationRepository.observeAllTagsForVocabEntry(entry.id)
                            .map { tags ->
                                entry.withTags(tags)
                            }
                    }
                    observablesList.zip()
                }
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
