package com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.UnknownCountry
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.usecase.FindCountriesForLanguage
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

    fun createNewLanguage(country: Country): LanguageProto? {
        val name = name ?: return null
        return LanguageProto(name, country)
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
