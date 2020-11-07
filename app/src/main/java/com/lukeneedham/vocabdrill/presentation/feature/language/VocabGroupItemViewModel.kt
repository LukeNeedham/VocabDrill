package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.VocabGroup
import com.lukeneedham.vocabdrill.domain.model.VocabGroupColourScheme
import com.lukeneedham.vocabdrill.usecase.CalculateVocabGroupColourScheme

class VocabGroupItemViewModel(
    private val calculateVocabGroupColourScheme: CalculateVocabGroupColourScheme
) {
    fun getColourScheme(vocabGroup: VocabGroup): VocabGroupColourScheme =
        calculateVocabGroupColourScheme(vocabGroup)
}
