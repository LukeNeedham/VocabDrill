package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.repository.VocabEntryRepository

class ObserveAllVocabEntryIdsForLanguage(
    private val vocabEntryRepository: VocabEntryRepository
) {
    operator fun invoke(languageId: Long) =
        vocabEntryRepository.observeAllVocabEntryIdsForLanguage(languageId)
}
