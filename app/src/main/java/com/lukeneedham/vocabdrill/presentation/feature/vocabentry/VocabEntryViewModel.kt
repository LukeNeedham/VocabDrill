package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.presentation.SettingsState
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.DeleteVocabEntry
import com.lukeneedham.vocabdrill.usecase.ObserveVocabEntryRelations
import io.reactivex.rxkotlin.plusAssign

class VocabEntryViewModel(
    val vocabEntryId: Long,
    private val observeVocabEntryRelations: ObserveVocabEntryRelations,
    private val deleteVocabEntry: DeleteVocabEntry
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val vocabGroupNameMutableLiveData = MutableLiveData<String>()
    val vocabGroupNameLiveData = vocabGroupNameMutableLiveData.toLiveData()

    private val wordAMutableLiveData = MutableLiveData<String>()
    val wordALiveData = wordAMutableLiveData.toLiveData()

    private val wordBMutableLiveData = MutableLiveData<String>()
    val wordBLiveData = wordBMutableLiveData.toLiveData()

    private val stateMutableLiveData = MutableLiveData<SettingsState>(SettingsState.Ready)
    val stateLiveData = stateMutableLiveData.toLiveData()

    init {
        disposables += observeVocabEntryRelations(vocabEntryId).subscribe(
            { entryRelations ->
                val entry = entryRelations.vocabEntry
                wordAMutableLiveData.value = entry.wordA
                wordBMutableLiveData.value = entry.wordB
                languageNameMutableLiveData.value = entryRelations.language.name
                vocabGroupNameMutableLiveData.value = entryRelations.vocabGroup.name
            },
            {
                stateMutableLiveData.value = SettingsState.Invalid
            }
        )
    }

    @SuppressLint("CheckResult")
    fun onDelete() {
        stateMutableLiveData.value = SettingsState.Working
        deleteVocabEntry(vocabEntryId).subscribe {
            stateMutableLiveData.value = SettingsState.Invalid
        }
    }
}
