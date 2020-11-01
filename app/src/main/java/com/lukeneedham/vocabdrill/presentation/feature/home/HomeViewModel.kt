package com.lukeneedham.vocabdrill.presentation.feature.home

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.rxkotlin.plusAssign

class HomeViewModel(private val languageRepository: LanguageRepository) : DisposingViewModel() {
    private val languagesMutableLiveData = MutableLiveData<List<Language>>()
    val languagesLiveData = languagesMutableLiveData.toLiveData()

    init {
        disposables += languageRepository.observeAllLanguages()
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
            .subscribe {
                languagesMutableLiveData.value = it
            }
    }

    fun addLanguage(languageProto: LanguageProto) {
        disposables += languageRepository.addLanguage(languageProto)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
            .subscribe()
    }
}
