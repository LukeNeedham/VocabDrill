package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lukeneedham.vocabdrill.domain.model.TagColourScheme

class CalculateColourScheme(
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) {
    operator fun invoke(mainColour: Int): TagColourScheme {
        val borderColour = ColorUtils.blendARGB(mainColour, Color.BLACK, 0.3f)
        val textColour = chooseTextColourForBackground(mainColour)
        return TagColourScheme(mainColour, borderColour, textColour)
    }
}
