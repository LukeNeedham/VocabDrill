package com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage

import com.lukeneedham.vocabdrill.domain.model.LanguageProto

interface AddLanguageCallback {
    fun addLanguage(languageProto: LanguageProto)
}
