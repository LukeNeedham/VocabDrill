package com.lukeneedham.vocabdrill.presentation.feature.tag

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.LearnBook
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.CalculateColourScheme
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabEntryRelationsForLanguage
import io.reactivex.rxkotlin.plusAssign

class VocabGroupViewModel(
    val vocabGroupId: Long,
    private val observeAllVocabEntryRelationsForLanguage: ObserveAllVocabEntryRelationsForLanguage,
    private val calculateColourScheme: CalculateColourScheme
) : DisposingViewModel() {

    private val entriesMutableLiveData = MutableLiveData<List<VocabEntryRelations>>()
    val entriesLiveData = entriesMutableLiveData.toLiveData()

    private val learnEntriesMutableLiveData = MutableLiveData<LearnBook>()
    val learnEntriesLiveData = learnEntriesMutableLiveData.toLiveData()

    private val colourSchemeMutableLiveData = MutableLiveData<String>()
    val learnBookColourSchemeLiveData = colourSchemeMutableLiveData.toLiveData()

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabGroupNameMutableLiveData = MutableLiveData<String>()
    val vocabGroupNameLiveData = vocabGroupNameMutableLiveData.toLiveData()

    init {
        val allEntryRelationsObservable = observeAllVocabEntryRelationsForLanguage(vocabGroupId)
        disposables += allEntryRelationsObservable.subscribe {
            entriesMutableLiveData.value = it
        }
    }
}
