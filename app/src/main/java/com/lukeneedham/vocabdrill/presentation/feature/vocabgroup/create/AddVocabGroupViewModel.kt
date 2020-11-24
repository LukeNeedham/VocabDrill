package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.CalculateRelatedColours
import com.lukeneedham.vocabdrill.usecase.CheckValidVocabGroupName
import com.lukeneedham.vocabdrill.usecase.ExtractFlagColoursFromLanguageId
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class AddVocabGroupViewModel(
    val languageId: Long,
    private val vocabGroupRepository: VocabGroupRepository,
    private val extractFlagColoursFromLanguageId: ExtractFlagColoursFromLanguageId,
    private val calculateRelatedColours: CalculateRelatedColours,
    private val checkValidVocabGroupName: CheckValidVocabGroupName
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
        disposables += extractFlagColoursFromLanguageId(languageId)
            .subscribe { colours ->
                flagColoursMutableLiveData.value = colours
            }
    }

    fun onNameChange(name: String) {
        this.name = name
        checkValidityDisposable?.dispose()

        if (name.isBlank()) {
            isValidNameMutableLiveData.value = false
        } else {
            val disposable = checkValidVocabGroupName(languageId, name).subscribe { isValid ->
                isValidNameMutableLiveData.value = isValid
            }
            checkValidityDisposable = disposable
            disposables += disposable
        }
    }

    /** @return a new [VocabGroup], or null if [name] is invalid */
    fun createNewVocabGroup(colour: Int) {
        val name = name
        if (name == null) {
            Log.e(TAG, "Could not create Vocab Group - name is null")
            return
        }
        val group = VocabGroupProto(name, colour, languageId)
        disposables += vocabGroupRepository.addVocabGroup(group)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
            .subscribe()
    }

    fun onColourSelected(colour: Int) {
        subColoursMutableLiveData.value = calculateRelatedColours(colour)
    }
}
