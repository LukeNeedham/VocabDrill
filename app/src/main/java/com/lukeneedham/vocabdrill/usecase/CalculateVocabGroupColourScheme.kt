package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lukeneedham.vocabdrill.domain.model.TagColourScheme

class CalculateVocabGroupColourScheme(
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) {
    operator fun invoke(vocabGroup: VocabGroup): TagColourScheme {
        val mainColour = vocabGroup.colour
        val borderColour = ColorUtils.blendARGB(mainColour, Color.BLACK, 0.3f)
        val textColour = chooseTextColourForBackground(mainColour)
        return TagColourScheme(mainColour, borderColour, textColour)
    }
}
