package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground

class VocabGroupItemViewModel(
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) {
    fun getTextColor(backgroundColor: Int) = chooseTextColourForBackground(backgroundColor)
}
