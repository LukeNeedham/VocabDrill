package com.lukeneedham.vocabdrill.usecase

import android.util.Log
import com.lukeneedham.languagecountries.LanguageProvider
import com.lukeneedham.vocabdrill.domain.UnknownCountry
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.xdrop.fuzzywuzzy.FuzzySearch

class FindCountriesForLanguage {

    private val languages = LanguageProvider.allLanguages?.languages ?: emptyList()

    private val languageNameToCountries: Map<String, List<String>> =
        languages.flatMap { language ->
            language.names.map { name -> name to language.countryCodes }
        }.toMap()

    private val languageNames = languageNameToCountries.keys.toList()

    operator fun invoke(languageName: String): Single<List<Country>> {
        return Single.fromCallable {
            search(languageName)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun search(languageName: String): List<Country> {
        if (languageName.isBlank()) {
            return emptyResult
        }

        val matches = FuzzySearch
            .extractAll(languageName, languageNames, ACCEPTED_SIMILARITY)
            .sortedDescending()
        Log.v(TAG, matches.toString())
        val countries = matches.flatMap {
            val name = languageNames[it.index]
            languageNameToCountries.getValue(name).map { Country(it) }
        }.distinct()

        return if (countries.isEmpty()) emptyResult else countries
    }

    companion object {
        private const val ACCEPTED_SIMILARITY = 90
        val emptyResult = listOf(UnknownCountry.country)
    }
}
