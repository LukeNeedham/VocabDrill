package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.LearnBook
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.CalculateVocabGroupColourScheme
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabEntryRelationsForVocabGroup
import com.lukeneedham.vocabdrill.usecase.ObserveVocabGroupRelations
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

class VocabGroupViewModel(
    val vocabGroupId: Long,
    private val observeVocabGroupRelations: ObserveVocabGroupRelations,
    private val observeAllVocabEntryRelationsForVocabGroup: ObserveAllVocabEntryRelationsForVocabGroup,
    private val calculateVocabGroupColourScheme: CalculateVocabGroupColourScheme
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
        val allEntryRelationsObservable = observeAllVocabEntryRelationsForVocabGroup(vocabGroupId)
        disposables += allEntryRelationsObservable.subscribe {
            entriesMutableLiveData.value = it
        }

        val groupRelationsObservable = observeVocabGroupRelations(vocabGroupId)
        disposables += groupRelationsObservable.subscribe { vocabGroupRelations ->
            languageNameMutableLiveData.value = vocabGroupRelations.language.name
            countryMutableLiveData.value = vocabGroupRelations.language.country
            vocabGroupNameMutableLiveData.value = vocabGroupRelations.vocabGroup.name

            vocabGroupRelations.entries
        }

        disposables += Observable.zip(
            allEntryRelationsObservable,
            groupRelationsObservable
        ) { entryRelations, group -> entryRelations to group }
            .subscribe { (entryRelations, groupRelations) ->
                val entries = entryRelations.map { it.vocabEntry }
                val colourScheme = calculateVocabGroupColourScheme(groupRelations.vocabGroup)
                learnEntriesMutableLiveData.value = LearnBook(entries, colourScheme)
            }
    }
}
