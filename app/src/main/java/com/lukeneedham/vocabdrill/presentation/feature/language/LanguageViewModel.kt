package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.VocabGroupProto
import com.lukeneedham.vocabdrill.domain.model.VocabGroupRelations
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabGroupRelationsForLanguage
import io.reactivex.rxkotlin.plusAssign

class LanguageViewModel(
    val languageId: Long,
    private val observeAllVocabGroupRelationsForLanguage: ObserveAllVocabGroupRelationsForLanguage,
    private val languageRepository: LanguageRepository,
    private val vocabGroupRepository: VocabGroupRepository
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val vocabGroupsMutableLiveData = MutableLiveData<List<VocabGroupRelations>>()
    val vocabGroupsLiveData = vocabGroupsMutableLiveData.toLiveData()

    init {
        disposables += languageRepository.requireLanguageForId(languageId).subscribe { language ->
            languageNameMutableLiveData.value = language.name
        }

        disposables += observeAllVocabGroupRelationsForLanguage(languageId).subscribe {
            vocabGroupsMutableLiveData.value = it
        }
    }

    fun addGroup(groupProto: VocabGroupProto) {
        disposables += vocabGroupRepository.addVocabGroup(groupProto).subscribe()
    }
}
