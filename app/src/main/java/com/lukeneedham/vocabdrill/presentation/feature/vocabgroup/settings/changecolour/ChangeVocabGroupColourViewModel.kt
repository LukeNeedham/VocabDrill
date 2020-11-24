package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changecolour

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.CalculateRelatedColours
import com.lukeneedham.vocabdrill.usecase.ExtractFlagColoursFromLanguageId
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.rxkotlin.plusAssign

class ChangeVocabGroupColourViewModel(
    val vocabGroupId: Long,
    private val vocabGroupRepository: VocabGroupRepository,
    private val extractFlagColoursFromLanguageId: ExtractFlagColoursFromLanguageId,
    private val calculateRelatedColours: CalculateRelatedColours
) : DisposingViewModel() {

    private val loadGroupSingle = vocabGroupRepository.requireVocabGroupForId(vocabGroupId)
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    private val primaryColorsMutableLiveData = MutableLiveData<VocabGroupColours>()
    val vocabGroupColorsLiveData = primaryColorsMutableLiveData.toLiveData()

    private val subColoursMutableLiveData = MutableLiveData<VocabGroupColours>()
    val subColoursLiveData = subColoursMutableLiveData.toLiveData()

    private var loadedVocabGroup: VocabGroup? = null
    private var colourToSubColours: Map<Int, List<Int>>? = null

    init {
        disposables += loadGroupSingle.subscribe { group ->
            loadedVocabGroup = group

            disposables += extractFlagColoursFromLanguageId(group.languageId).subscribe { colours ->

                val coloursToSubColours = colours.map {
                    it to calculateRelatedColours(it)
                }.toMap()
                colourToSubColours = coloursToSubColours

                val primaryColour = findColourForSubColour(group.colour, coloursToSubColours)
                primaryColorsMutableLiveData.value = VocabGroupColours(colours, primaryColour)
            }
        }
    }

    fun onColourSelected(colour: Int) {
        val subColours = colourToSubColours?.get(colour)
            ?: calculateRelatedColours(colour)
        subColoursMutableLiveData.value = VocabGroupColours(subColours, loadedVocabGroup?.colour)
    }

    @SuppressLint("CheckResult")
    fun updateVocabGroup(colour: Int) {
        fun update(group: VocabGroup) {
            val newGroup = group.copy(colour = colour)
            vocabGroupRepository.updateVocabGroup(newGroup)
                .subscribeOn(RxSchedulers.database)
                .observeOn(RxSchedulers.main)
                .subscribe()
        }

        val loadedVocabGroup = loadedVocabGroup
        if (loadedVocabGroup != null) {
            update(loadedVocabGroup)
        } else {
            loadGroupSingle.subscribe { language ->
                update(language)
            }
        }
    }

    private fun findColourForSubColour(
        target: Int,
        coloursToSubColours: Map<Int, List<Int>>
    ): Int? {
        coloursToSubColours.forEach { (colour, subcolours) ->
            if (target in subcolours) {
                return colour
            }
        }
        return null
    }
}
