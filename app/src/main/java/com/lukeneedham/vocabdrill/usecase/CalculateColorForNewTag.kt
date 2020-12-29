package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lukeneedham.vocabdrill.repository.LanguageTagsRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class CalculateColorForNewTag(
    private val languageTagsRepository: LanguageTagsRepository,
    private val extractFlagColoursFromLanguageId: ExtractFlagColoursFromLanguageId
) {
    private val ratioListSoFar = mutableListOf<RatioStep>(RatioStep(0.5f, 0.5f))

    operator fun invoke(languageId: Long): Single<Int> {
        return Single.zip(
            extractFlagColoursFromLanguageId(languageId),
            languageTagsRepository.getAllForLanguage(languageId)
        ) { flagColours, tags ->
            val newTagIndex = tags.size
            getColourForIndex(flagColours, newTagIndex)
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }

    private fun getColourForIndex(flagColours: List<Int>, index: Int): Int {
        val numFlagColors = flagColours.size
        val hue = index % numFlagColors
        val luminanceIndex = index / numFlagColors

        /* Alternate between white and black with the same ratios - so divide by 2 */
        val luminanceRatiosIndex = luminanceIndex / 2
        val ratio = getRatio(luminanceRatiosIndex)

        val blendColor = if (luminanceIndex % 2 == 0) Color.WHITE else Color.BLACK
        return ColorUtils.blendARGB(hue, blendColor, ratio)
    }

    private fun getRatio(targetIndex: Int): Float {
        var index = ratioListSoFar.size
        while(index < targetIndex) {
            val step = ratioListSoFar[index]
            val newDiff = step.diff / 2
            val value = step.value
            val minusChild = RatioStep(value - newDiff, newDiff)
            ratioListSoFar.add(minusChild)
            val addChild = RatioStep(value + newDiff, newDiff)
            ratioListSoFar.add(addChild)
            index++
        }
        return ratioListSoFar[targetIndex].value
    }

    private data class RatioStep(val value: Float, val diff: Float)
}
