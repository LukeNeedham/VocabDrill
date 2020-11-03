package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changecolour

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.VocabGroup
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

    private val flagColoursMutableLiveData = MutableLiveData<List<Int>>()
    val flagColoursLiveData = flagColoursMutableLiveData.toLiveData()

    private val subColoursMutableLiveData = MutableLiveData<List<Int>>()
    val subColoursLiveData = subColoursMutableLiveData.toLiveData()

    private val vocabGroupColorsMutableLiveData = MutableLiveData<VocabGroupColours>()
    val vocabGroupColorsLiveData = vocabGroupColorsMutableLiveData.toLiveData()

    private var loadedVocabGroup: VocabGroup? = null
    private var colourToSubColours: Map<Int, List<Int>>? = null

    init {
        disposables += loadGroupSingle.subscribe { group ->
            loadedVocabGroup = group

            disposables += extractFlagColoursFromLanguageId(group.languageId).subscribe { colours ->
                flagColoursMutableLiveData.value = colours

                val coloursToSubColours = colours.map {
                    it to calculateRelatedColours(it)
                }.toMap()
                colourToSubColours = coloursToSubColours

                val subColour = group.colour
                val primaryColour = findColourForSubColour(subColour, coloursToSubColours)
                if (primaryColour != null) {
                    vocabGroupColorsMutableLiveData.value =
                        VocabGroupColours(primaryColour, subColour)
                }
            }
        }
    }

    fun onColourSelected(colour: Int) {
        val subColours = colourToSubColours?.get(colour)
            ?: calculateRelatedColours(colour)
        subColoursMutableLiveData.value = subColours
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

    private fun findColourForSubColour(target: Int, coloursToSubColours: Map<Int, List<Int>>): Int? {
        coloursToSubColours.forEach { (colour, subcolours) ->
            if (target in subcolours) {
                return colour
            }
        }
        return null
    }
}
