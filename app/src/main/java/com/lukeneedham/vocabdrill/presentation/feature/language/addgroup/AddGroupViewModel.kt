package com.lukeneedham.vocabdrill.presentation.feature.language.addgroup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.VocabGroup
import com.lukeneedham.vocabdrill.domain.model.VocabGroupProto
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.CalculateRelatedColours
import com.lukeneedham.vocabdrill.usecase.ExtractFlagColours
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class AddGroupViewModel(
    val languageId: Long,
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository,
    private val extractFlagColours: ExtractFlagColours,
    private val calculateRelatedColours: CalculateRelatedColours
) : DisposingViewModel() {

    private var name: String? = null

    private var checkValidityDisposable: Disposable? = null

    private val isValidNameMutableLiveData = MutableLiveData<Boolean>(false)
    val isValidNameLiveData = isValidNameMutableLiveData.toLiveData()

    private val flagColoursMutableLiveData = MutableLiveData<List<Int>>()
    val flagColoursLiveData = flagColoursMutableLiveData.toLiveData()

    private val subColoursMutableLiveData = MutableLiveData<List<Int>>()
    val subColoursLiveData = subColoursMutableLiveData.toLiveData()

    init {
        disposables += languageRepository.requireLanguageForId(languageId).map { language ->
            extractFlagColours(language.country)
        }.subscribe { colours ->
            flagColoursMutableLiveData.value = colours
        }
    }

    fun onNameChange(name: String) {
        this.name = name
        checkValidityDisposable?.dispose()

        if (name.isBlank()) {
            isValidNameMutableLiveData.value = false
        } else {
            val disposable =
                vocabGroupRepository.getAllVocabGroupsForLanguage(languageId).subscribe { groups ->
                    val isDuplicate = groups.any { it.name == name }
                    isValidNameMutableLiveData.value = !isDuplicate
                }
            checkValidityDisposable = disposable
            disposables += disposable
        }
    }

    /** @return a new [VocabGroup], or null if [name] is invalid */
    fun createNewVocabGroup(colour: Int) {
        val name = name
        if(name == null) {
            Log.e(TAG, "Could not create Vocab Group - name is null")
            return
        }
        val group = VocabGroupProto(name, colour, languageId)
        disposables += vocabGroupRepository.addVocabGroup(group).subscribe()
    }

    fun onColourSelected(colour: Int) {
        subColoursMutableLiveData.value = calculateRelatedColours(colour)
    }
}
