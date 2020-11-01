package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.usecase.LoadVocabGroupRelations
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabEntryRelationsForVocabGroup
import io.reactivex.rxkotlin.plusAssign

class VocabGroupViewModel(
    val vocabGroupId: Long,
    private val loadVocabGroupRelations: LoadVocabGroupRelations,
    private val vocabEntryRepository: VocabEntryRepository,
    private val observeAllVocabEntryRelationsForVocabGroup: ObserveAllVocabEntryRelationsForVocabGroup
) : DisposingViewModel() {

    private val entriesMutableLiveData = MutableLiveData<List<VocabEntryRelations>>()
    val entriesLiveData = entriesMutableLiveData.toLiveData()

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabGroupNameMutableLiveData = MutableLiveData<String>()
    val vocabGroupNameLiveData = vocabGroupNameMutableLiveData.toLiveData()

    init {
        disposables += observeAllVocabEntryRelationsForVocabGroup(vocabGroupId).subscribe {
            entriesMutableLiveData.value = it
        }

        disposables += loadVocabGroupRelations(vocabGroupId).subscribe { vocabGroupRelations ->
            languageNameMutableLiveData.value = vocabGroupRelations.language.name
            countryMutableLiveData.value = vocabGroupRelations.language.country
            vocabGroupNameMutableLiveData.value = vocabGroupRelations.vocabGroup.name
        }
    }

    fun addEntry(entryProto: VocabEntryProto) {
        disposables += vocabEntryRepository.addVocabEntry(entryProto).subscribe()
    }
}
