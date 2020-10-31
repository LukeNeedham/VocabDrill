package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import androidx.core.graphics.ColorUtils

class CalculateRelatedColours {
    /**
     * @return a list of related colours, with [colour] in the middle,
     * darker colours in the first half,
     * and lighter colours in the second half
     */
    operator fun invoke(colour: Int): List<Int> {
        val ratios = listOf(0.2f, 0.4f, 0.6f, 0.8f)

        val darkenedColours = ratios.map { ratio ->
            ColorUtils.blendARGB(Color.BLACK, colour, ratio)
        }

        val lightenedColours = ratios.map { ratio ->
            ColorUtils.blendARGB(colour, Color.WHITE, ratio)
        }

        return darkenedColours + colour + lightenedColours
    }
}
