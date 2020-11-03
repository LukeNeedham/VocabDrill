package com.lukeneedham.vocabdrill.presentation.feature.language

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground

class VocabGroupItemViewModel(
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) {
    fun getTextColor(backgroundColor: Int) = chooseTextColourForBackground(backgroundColor)

    fun getOutlineColour(backgroundColor: Int): Int {
        return ColorUtils.blendARGB(backgroundColor, Color.BLACK, 0.3f)
    }
}
