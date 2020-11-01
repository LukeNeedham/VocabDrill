package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.DeleteVocabGroup
import com.lukeneedham.vocabdrill.usecase.ObserveVocabGroup
import io.reactivex.rxkotlin.plusAssign

class VocabGroupSettingsViewModel(
    val vocabGroupId: Long,
    private val observeVocabGroup: ObserveVocabGroup,
    private val deleteVocabGroup: DeleteVocabGroup
) : DisposingViewModel() {

    private val nameMutableLiveData = MutableLiveData<String>()
    val nameLiveData = nameMutableLiveData.toLiveData()

    private val colourMutableLiveData = MutableLiveData<Int>()
    val colourLiveData = colourMutableLiveData.toLiveData()

    private val stateMutableLiveData = MutableLiveData<State>(State.Ready)
    val stateLiveData = stateMutableLiveData.toLiveData()

    init {
        disposables += observeVocabGroup(vocabGroupId).subscribe(
            {
                nameMutableLiveData.value = it.name
                colourMutableLiveData.value = it.colour
            },
            {
                stateMutableLiveData.value = State.Invalid
            }
        )
    }

    @SuppressLint("CheckResult")
    fun onDelete() {
        stateMutableLiveData.value = State.Working
        deleteVocabGroup(vocabGroupId).subscribe {
            stateMutableLiveData.value = State.Invalid
        }
    }

    sealed class State {
        /** No work is happening, ready for user input */
        object Ready : State()

        /** Working is in progress, disable user input */
        object Working : State()

        /** This language no longer exists. Quit */
        object Invalid : State()
    }
}
