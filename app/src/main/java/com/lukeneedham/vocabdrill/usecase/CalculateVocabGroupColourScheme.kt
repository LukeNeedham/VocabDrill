package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lukeneedham.vocabdrill.domain.model.VocabGroup
import com.lukeneedham.vocabdrill.domain.model.VocabGroupColourScheme

class CalculateVocabGroupColourScheme(
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) {
    operator fun invoke(vocabGroup: VocabGroup): VocabGroupColourScheme {
        val mainColour = vocabGroup.colour
        val borderColour = ColorUtils.blendARGB(mainColour, Color.BLACK, 0.3f)
        val textColour = chooseTextColourForBackground(mainColour)
        return VocabGroupColourScheme(mainColour, borderColour, textColour)
    }
}
