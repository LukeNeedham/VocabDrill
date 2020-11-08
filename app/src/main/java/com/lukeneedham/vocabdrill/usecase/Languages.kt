package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.languagecountries.LanguageProvider

/** Pre-loads language data as a singleton */
object Languages {
    val languages = LanguageProvider.allLanguages?.languages ?: emptyList()

    val languageNameToCountries: Map<String, List<String>> =
        languages.flatMap { language ->
            language.names.map { name -> name to language.countryCodes }
        }.toMap()

    val languageNames = languageNameToCountries.keys.toList()
}
