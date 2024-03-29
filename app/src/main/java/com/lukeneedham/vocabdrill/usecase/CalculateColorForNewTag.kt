package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

// TODO: Limit to a min and max color - so not too close to black and not too close to white
class CalculateColorForNewTag(
    private val tagRepository: TagRepository,
    private val extractFlagColoursFromLanguageId: ExtractFlagColoursFromLanguageId
) {
    private val ratioListSoFar = mutableListOf(RatioStep(0.5f, 0.5f))

    operator fun invoke(languageId: Long): Single<Int> {
        val newTagIndex = tagRepository.getTagCreationCount(languageId)
        return extractFlagColoursFromLanguageId(languageId).map { flagColours ->
            getColourForIndex(flagColours, newTagIndex)
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }

    private fun getColourForIndex(flagColours: List<Int>, index: Int): Int {
        val numFlagColors = flagColours.size
        val hueIndex = index % numFlagColors
        val hue = flagColours[hueIndex]
        val luminanceIndex = index / numFlagColors

        /* Alternate between white and black with the same ratios - so divide by 2 */
        val luminanceRatiosIndex = luminanceIndex / 2
        val ratio = getRatio(luminanceRatiosIndex)

        val blendColor = if (luminanceIndex % 2 == 0) Color.WHITE else Color.BLACK
        return ColorUtils.blendARGB(hue, blendColor, ratio)
    }

    private fun getRatio(targetIndex: Int): Float {
        var index = ratioListSoFar.lastIndex
        while (index < targetIndex) {
            val step = ratioListSoFar[index]
            val newDiff = step.diff / 2
            val value = step.value
            val minusChild = RatioStep(value - newDiff, newDiff)
            ratioListSoFar.add(minusChild)
            val addChild = RatioStep(value + newDiff, newDiff)
            ratioListSoFar.add(addChild)
            index += 2
        }
        return ratioListSoFar[targetIndex].value
    }

    private data class RatioStep(val value: Float, val diff: Float)
}
