package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.VocabGroupRelations
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import io.reactivex.Single

class LoadVocabGroupRelations(
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(vocabGroupId: Long): Single<VocabGroupRelations> {
        val getVocabGroupAndLanguageSingle = vocabGroupRepository
            .requireVocabGroupForId(vocabGroupId)
            .flatMap {
                Single.zip(
                    Single.just(it),
                    languageRepository.requireLanguageForId(it.languageId)
                ) { vocabGroup, language ->
                    vocabGroup to language
                }
            }
        return Single.zip(
            getVocabGroupAndLanguageSingle,
            vocabEntryRepository.getAllVocabEntriesForVocabGroup(vocabGroupId)
        ) { (vocabGroup, language), entries ->
            VocabGroupRelations(vocabGroup, language, entries)
        }
    }
}
