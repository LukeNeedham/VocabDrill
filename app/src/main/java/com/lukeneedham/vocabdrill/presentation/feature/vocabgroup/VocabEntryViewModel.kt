package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import android.graphics.Color
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground
import com.lukeneedham.vocabdrill.usecase.EstimateColourDistance

class VocabEntryViewModel(
    private val chooseTextColourForBackground: ChooseTextColourForBackground,
    private val estimateColourDistance: EstimateColourDistance
) {
    fun getColourScheme(tintColour: Int, backgroundColor: Int): ColourScheme {
        val sideAColour = tintColour

        val sideColourDistance = estimateColourDistance(sideAColour, DEFAULT_SIDE_B_COLOUR)
        val sideBColour = if (sideColourDistance > MIN_SIDE_COLOUR_DIFFERENCE) {
            DEFAULT_SIDE_B_COLOUR
        } else {
            FALLBACK_SIDE_B_COLOUR
        }

        val sideABorderColourDistance = estimateColourDistance(sideAColour, backgroundColor)
        val borderColour =
            if (sideABorderColourDistance > MIN_BORDER_COLOUR_DIFFERENCE) {
                sideAColour
            } else {
                val sideBBorderColourDistance = estimateColourDistance(sideBColour, backgroundColor)
                if (sideBBorderColourDistance > MIN_BORDER_COLOUR_DIFFERENCE) {
                    sideBColour
                } else {
                    FALLBACK_BORDER_COLOUR
                }
            }

        val sideATextColour = chooseTextColourForBackground(sideAColour)
        val sideBTextColor = chooseTextColourForBackground(sideBColour)

        return ColourScheme(
            tintColour,
            sideBColour,
            borderColour,
            sideATextColour,
            sideBTextColor
        )
    }

    data class ColourScheme(
        val sideAColor: Int,
        val sideBColor: Int,
        val borderColor: Int,
        val sideATextColor: Int,
        val sideBTextColor: Int,
    )

    companion object {
        const val DEFAULT_SIDE_B_COLOUR = Color.WHITE
        const val FALLBACK_SIDE_B_COLOUR = Color.BLACK
        const val FALLBACK_BORDER_COLOUR = Color.GRAY

        const val MIN_SIDE_COLOUR_DIFFERENCE = 80
        const val MIN_BORDER_COLOUR_DIFFERENCE = 60
    }
}
