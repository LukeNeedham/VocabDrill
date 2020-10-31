package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import kotlin.math.sqrt

/** Algorithm from https://www.compuphase.com/cmetric.htm */
class EstimateColourDistance {
    operator fun invoke(color1: Int, color2: Int): Double {
        val red1 = Color.red(color1)
        val green1 = Color.green(color1)
        val blue1 = Color.blue(color1)

        val red2 = Color.red(color2)
        val green2 = Color.green(color2)
        val blue2 = Color.blue(color2)

        val redMean = (red1 + red2) / 2
        val redDiff = red1 - red2
        val greenDiff = green1 - green2
        val blueDiff = blue1 - blue2

        val w = (((512 + redMean) * redDiff * redDiff) shr 8)
        val x = 4 * greenDiff * greenDiff
        val y = (((767 - redMean) * blueDiff * blueDiff) shr 8)
        val z = (w + x + y).toDouble()
        return sqrt(z)
    }
}
