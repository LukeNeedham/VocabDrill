package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabGroupRelationsForLanguage
import com.lukeneedham.vocabdrill.usecase.ObserveLanguage
import io.reactivex.rxkotlin.plusAssign

class LanguageViewModel(
    val languageId: Long,
    private val observeAllVocabGroupRelationsForLanguage: ObserveAllVocabGroupRelationsForLanguage,
    private val observeLanguage: ObserveLanguage
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabGroupsMutableLiveData = MutableLiveData<List<VocabGroupRelations>>()
    val vocabGroupsLiveData = vocabGroupsMutableLiveData.toLiveData()

    init {
        disposables += observeLanguage(languageId).subscribe { language ->
            languageNameMutableLiveData.value = language.name
            countryMutableLiveData.value = language.country
        }

        disposables += observeAllVocabGroupRelationsForLanguage(languageId).subscribe {
            vocabGroupsMutableLiveData.value = it
        }
    }
}
