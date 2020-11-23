package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.presentation.SettingsState
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.DeleteVocabGroup
import com.lukeneedham.vocabdrill.usecase.ObserveVocabGroupRelations
import io.reactivex.rxkotlin.plusAssign

class VocabGroupSettingsViewModel(
    val vocabGroupId: Long,
    private val observeVocabGroupRelations: ObserveVocabGroupRelations,
    private val deleteVocabGroup: DeleteVocabGroup
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val vocabGroupNameMutableLiveData = MutableLiveData<String>()
    val vocabGroupNameLiveData = vocabGroupNameMutableLiveData.toLiveData()

    private val colourMutableLiveData = MutableLiveData<Int>()
    val colourLiveData = colourMutableLiveData.toLiveData()

    private val stateMutableLiveData = MutableLiveData<SettingsState>(SettingsState.Ready)
    val stateLiveData = stateMutableLiveData.toLiveData()

    init {
        disposables += observeVocabGroupRelations(vocabGroupId).subscribe(
            { vocabGroupRelations ->
                languageNameMutableLiveData.value = vocabGroupRelations.language.name
                vocabGroupNameMutableLiveData.value = vocabGroupRelations.vocabGroup.name
                colourMutableLiveData.value = vocabGroupRelations.vocabGroup.colour
            },
            {
                stateMutableLiveData.value = SettingsState.Invalid
            }
        )
    }

    @SuppressLint("CheckResult")
    fun onDelete() {
        stateMutableLiveData.value = SettingsState.Working
        deleteVocabGroup(vocabGroupId).subscribe {
            stateMutableLiveData.value = SettingsState.Invalid
        }
    }
}
