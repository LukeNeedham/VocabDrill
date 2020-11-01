package com.lukeneedham.vocabdrill.presentation.feature.language.settings.changename

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.usecase.CheckValidLanguageName
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class ChangeLanguageNameViewModel(
    val languageId: Long,
    private val languageRepository: LanguageRepository,
    private val checkValidLanguageName: CheckValidLanguageName
) : DisposingViewModel() {

    private val loadLanguageSingle = languageRepository.requireLanguageForId(languageId)
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    private var name: String? = null

    private var checkValidityDisposable: Disposable? = null

    private val isValidNameMutableLiveData = MutableLiveData<Boolean>(false)
    val isValidNameLiveData = isValidNameMutableLiveData.toLiveData()

    private val languageMutableLiveData = MutableLiveData<Language>()
    val languageLiveData = languageMutableLiveData.toLiveData()

    init {
        disposables += loadLanguageSingle.subscribe { language ->
            languageMutableLiveData.value = language
        }
    }

    fun onNameChange(name: String) {
        this.name = name
        checkValidity(name)
    }

    @SuppressLint("CheckResult")
    fun updateLanguage() {
        val name = name
        if (name == null) {
            Log.e(TAG, "Cannot update language - name is null")
            return
        }

        fun update(language: Language) {
            val newLanguage = language.copy(name = name)
            languageRepository.updateLanguage(newLanguage)
                .subscribeOn(RxSchedulers.database)
                .observeOn(RxSchedulers.main)
                .subscribe()
        }

        val loadedLanguage = languageLiveData.value
        if (loadedLanguage != null) {
            update(loadedLanguage)
        } else {
            loadLanguageSingle.subscribe { language ->
                update(language)
            }
        }
    }

    private fun checkValidity(name: String) {
        checkValidityDisposable?.dispose()

        if (name.isBlank()) {
            isValidNameMutableLiveData.value = false
        } else {
            val disposable = checkValidLanguageName(name).subscribe { isValid ->
                isValidNameMutableLiveData.value = isValid
            }
            checkValidityDisposable = disposable
            disposables += disposable
        }
    }
}
