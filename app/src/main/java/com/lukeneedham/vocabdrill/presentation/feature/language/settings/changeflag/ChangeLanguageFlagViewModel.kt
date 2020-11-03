package com.lukeneedham.vocabdrill.presentation.feature.language.settings.changeflag

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.UnknownCountry
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.CountryMatches
import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.presentation.feature.language.create.AddLanguageViewModel
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.usecase.FindCountriesForLanguage
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class ChangeLanguageFlagViewModel(
    val languageId: Long,
    private val languageRepository: LanguageRepository,
    private val findCountriesForLanguage: FindCountriesForLanguage
) : DisposingViewModel() {

    private val loadLanguageSingle = languageRepository.requireLanguageForId(languageId)
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    private val countriesMutableLiveData = MutableLiveData<CountryMatches>(CountryMatches.Searching)
    val countriesLiveData = countriesMutableLiveData.toLiveData()

    private val languageMutableLiveData = MutableLiveData<Language>()
    val languageLiveData = languageMutableLiveData.toLiveData()

    init {
        disposables += loadLanguageSingle.subscribe { language ->
            languageMutableLiveData.value = language
            fetchCountriesForLanguageName(language.name)
        }
    }

    @SuppressLint("CheckResult")
    fun updateLanguage(country: Country) {
        fun update(language: Language) {
            val newLanguage = language.copy(country = country)
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

    private fun fetchCountriesForLanguageName(languageName: String) {
        disposables += findCountriesForLanguage(languageName)
            .subscribe { countries ->
                countriesMutableLiveData.value = CountryMatches.Found(countries)
            }
    }
}
