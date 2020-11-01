package com.lukeneedham.vocabdrill.presentation.feature.language.settings

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.DeleteLanguage
import com.lukeneedham.vocabdrill.usecase.ObserveLanguage
import io.reactivex.rxkotlin.plusAssign

class LanguageSettingsViewModel(
    val languageId: Long,
    private val observeLanguage: ObserveLanguage,
    private val deleteLanguage: DeleteLanguage
) : DisposingViewModel() {

    private val nameMutableLiveData = MutableLiveData<String>()
    val nameLiveData = nameMutableLiveData.toLiveData()

    private val flagCountryMutableLiveData = MutableLiveData<Country>()
    val flagCountryLiveData = flagCountryMutableLiveData.toLiveData()

    private val stateMutableLiveData = MutableLiveData<State>(State.Ready)
    val stateLiveData = stateMutableLiveData.toLiveData()

    init {
        disposables += observeLanguage(languageId).subscribe(
            {
                nameMutableLiveData.value = it.name
                flagCountryMutableLiveData.value = it.country
            },
            {
                stateMutableLiveData.value = State.Invalid
            }
        )
    }

    @SuppressLint("CheckResult")
    fun onDelete() {
        stateMutableLiveData.value = State.Working
        deleteLanguage(languageId).subscribe {
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
