package com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.UnknownCountry
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.usecase.FindCountriesForLanguage
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class AddLanguageViewModel(
    private val languageRepository: LanguageRepository,
    private val findCountriesForLanguage: FindCountriesForLanguage
) : DisposingViewModel() {

    private var name: String? = null

    private var countriesDisposable: Disposable? = null
    private var checkValidityDisposable: Disposable? = null

    private val countriesObservableMutable = MutableLiveData(listOf(UnknownCountry.country))
    val countriesObservable: LiveData<List<Country>> = countriesObservableMutable

    private val isValidNameMutableLiveData = MutableLiveData<Boolean>(false)
    val isValidNameLiveData = isValidNameMutableLiveData.toLiveData()

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
        languageRepository.addLanguage(language).subscribe()
    }

    private fun checkValidity(name: String) {
        checkValidityDisposable?.dispose()

        if (name.isBlank()) {
            isValidNameMutableLiveData.value = false
        } else {
            val disposable =
                languageRepository.getAllLanguages().subscribe { languages ->
                    val isDuplicate = languages.any { it.name == name }
                    isValidNameMutableLiveData.value = !isDuplicate
                }
            checkValidityDisposable = disposable
            disposables += disposable
        }
    }

    private fun fetchCountriesForLanguageName(languageName: String) {
        countriesDisposable?.dispose()
        val disposable = Single.fromCallable {
            findCountriesForLanguage(languageName)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { countries ->
                countriesObservableMutable.value = countries
            }
        countriesDisposable = disposable
        disposables += disposable
    }
}
