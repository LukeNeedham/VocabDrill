package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.TagColourScheme
import com.lukeneedham.vocabdrill.domain.model.VocabTag
import com.lukeneedham.vocabdrill.usecase.CalculateVocabGroupColourScheme

class VocabGroupItemViewModel(
    private val calculateVocabGroupColourScheme: CalculateVocabGroupColourScheme
) {
    fun getColourScheme(vocabGroup: VocabTag): TagColourScheme =
        calculateVocabGroupColourScheme(vocabGroup)
}
