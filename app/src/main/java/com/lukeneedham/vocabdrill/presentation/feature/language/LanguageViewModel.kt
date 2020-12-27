package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.AddVocabEntry
import com.lukeneedham.vocabdrill.usecase.DeleteVocabEntry
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabEntryRelationsForLanguage
import com.lukeneedham.vocabdrill.usecase.ObserveLanguage
import io.reactivex.rxkotlin.plusAssign

class LanguageViewModel(
    val languageId: Long,
    private val observeAllVocabEntryRelationsForLanguage: ObserveAllVocabEntryRelationsForLanguage,
    private val observeLanguage: ObserveLanguage,
    private val addVocabEntry: AddVocabEntry,
    private val deleteVocabEntry: DeleteVocabEntry
) : DisposingViewModel() {

    private val itemStateHandler = VocabEntryItemsHandler(languageId) {
        vocabEntriesMutableLiveData.value = it
    }

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabEntriesMutableLiveData = MutableLiveData<List<VocabEntryItem>>()
    val vocabEntriesLiveData = vocabEntriesMutableLiveData.toLiveData()

    init {
        vocabEntriesMutableLiveData.value = itemStateHandler.getItems()

        disposables += observeLanguage(languageId).subscribe { language ->
            languageNameMutableLiveData.value = language.name
            countryMutableLiveData.value = language.country
        }

        disposables += observeAllVocabEntryRelationsForLanguage(languageId).subscribe {
            itemStateHandler.submitExistingItems(it)
        }
    }

    fun onCreateItemWordAChanged(
        item: VocabEntryItem.Create,
        newWordA: String
    ) {
        itemStateHandler.onCreateItemWordAChanged(item, newWordA)
    }

    fun onCreateItemWordBChanged(
        item: VocabEntryItem.Create,
        newWordB: String
    ) {
        itemStateHandler.onCreateItemWordBChanged(item, newWordB)
    }

    fun save(proto: VocabEntryProto) {
        itemStateHandler.onCreateItemSaving()
        val ignored = addVocabEntry(proto).subscribe {
            itemStateHandler.onCreateItemSaved()
        }
    }

    fun deleteEntry(item: VocabEntryItem.Existing) {
        val ignored = deleteVocabEntry(item.data.vocabEntry.id).subscribe()
    }
}
