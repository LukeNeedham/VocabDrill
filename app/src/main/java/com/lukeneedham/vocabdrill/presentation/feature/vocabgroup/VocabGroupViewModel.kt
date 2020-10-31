package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.usecase.LoadVocabGroupRelations
import io.reactivex.rxkotlin.plusAssign

class VocabGroupViewModel(
    val vocabGroupId: Long,
    private val loadVocabGroupRelations: LoadVocabGroupRelations,
    private val vocabEntryRepository: VocabEntryRepository
) : DisposingViewModel() {

    private val entriesMutableLiveData = MutableLiveData<List<VocabEntry>>()
    val entriesLiveData: LiveData<List<VocabEntry>> = entriesMutableLiveData

    private val titleMutableLiveData = MutableLiveData<String>()
    val titleLiveData: LiveData<String> = titleMutableLiveData

    init {
        disposables += vocabEntryRepository.observeAllVocabEntriesForVocabGroup(vocabGroupId)
            .subscribe {
                entriesMutableLiveData.value = it
            }

        disposables += loadVocabGroupRelations(vocabGroupId).subscribe { vocabGroupRelations ->
            titleMutableLiveData.value =
                "${vocabGroupRelations.language.name} - ${vocabGroupRelations.vocabGroup.name}"
        }
    }

    fun addEntry(entryProto: VocabEntryProto) {
        disposables += vocabEntryRepository.addVocabEntry(entryProto).subscribe()
    }
}
