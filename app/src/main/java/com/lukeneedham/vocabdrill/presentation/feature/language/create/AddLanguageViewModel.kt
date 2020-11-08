package com.lukeneedham.vocabdrill.presentation.feature.language.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.CountryMatches
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.LiveDataZipper
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.usecase.CheckValidLanguageName
import com.lukeneedham.vocabdrill.usecase.FindCountriesForLanguage
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class AddLanguageViewModel(
    private val languageRepository: LanguageRepository,
    private val findCountriesForLanguage: FindCountriesForLanguage,
    private val checkValidLanguageName: CheckValidLanguageName
) : DisposingViewModel() {

    private var name: String? = null

    private var countriesDisposable: Disposable? = null
    private var checkValidityDisposable: Disposable? = null

    private val isValidNameMutableLiveData = MutableLiveData<Boolean>(false)

    private val countriesMutableLiveData = MutableLiveData<CountryMatches>(
        CountryMatches.Found(FindCountriesForLanguage.emptyResult)
    )
    val countriesLiveData = countriesMutableLiveData.toLiveData()

    val isConfirmEnabledLiveData: LiveData<Boolean> = LiveDataZipper.biZip(
        countriesLiveData,
        isValidNameMutableLiveData
    ) { countries, validName ->
        countries is CountryMatches.Found && validName
    }

    fun onNameChange(name: String) {
        this.name = name
        checkValidity(name)
        fetchCountriesForLanguageName(name)
    }

    fun createNewLanguage(country: Country) {
        val name = name
        if (name == null) {
            Log.e(TAG, "Cannot add language - name is null")
            return
        }
        val language = LanguageProto(name, country)
        languageRepository.addLanguage(language)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
            .subscribe()
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

    private fun fetchCountriesForLanguageName(languageName: String) {
        countriesDisposable?.dispose()
        countriesMutableLiveData.value = CountryMatches.Searching
        val disposable = findCountriesForLanguage(languageName)
            .subscribe { countries ->
                countriesMutableLiveData.value = CountryMatches.Found(countries)
            }
        countriesDisposable = disposable
        disposables += disposable
    }
}
