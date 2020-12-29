package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.*
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.FocusItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemData
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.*
import io.reactivex.rxkotlin.plusAssign

class LanguageViewModel(
    val languageId: Long,
    private val observeAllVocabEntryRelationsForLanguage: ObserveAllVocabEntryRelationsForLanguage,
    private val observeLanguage: ObserveLanguage,
    private val addVocabEntry: AddVocabEntry,
    private val deleteVocabEntry: DeleteVocabEntry,
    private val updateVocabEntry: UpdateVocabEntry,
    private val addTagToVocabEntry: AddTagToVocabEntry,
    private val addNewTag: AddNewTag,
    private val calculateColorForNewTag: CalculateColorForNewTag,
    private val findTagNameMatches: FindTagNameMatches
) : DisposingViewModel() {

    private val itemStateHandler = VocabEntryItemsHandler(languageId) {
        vocabEntriesMutableLiveData.value = getVocabEntryItemsFromData(it)
    }

    private var selectedItem: SelectedVocabEntry = SelectedVocabEntry.None

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabEntriesMutableLiveData = MutableLiveData<List<VocabEntryItemPresentationData>>()
    val vocabEntriesLiveData = vocabEntriesMutableLiveData.toLiveData()

    private val tagSuggestionsMutableLiveData = MutableLiveData<List<TagItem>>()
    val tagSuggestionsLiveData = tagSuggestionsMutableLiveData.toLiveData()

    init {
        refreshEntryItems()

        disposables += observeLanguage(languageId).subscribe { language ->
            languageNameMutableLiveData.value = language.name
            countryMutableLiveData.value = language.country
        }

        disposables += observeAllVocabEntryRelationsForLanguage(languageId).subscribe {
            itemStateHandler.submitExistingItems(it)
        }
    }

    fun onCreateItemWordAChanged(newWordA: String, selection: TextSelection) {
        itemStateHandler.onCreateItemWordAChanged(newWordA)
        val focus = FocusItem.WordA(selection)
        selectedItem = SelectedVocabEntry.Create(focus)
        refreshEntryItems()
    }

    fun onCreateItemWordBChanged(newWordB: String, selection: TextSelection) {
        itemStateHandler.onCreateItemWordBChanged(newWordB)
        val focus = FocusItem.WordB(selection)
        selectedItem = SelectedVocabEntry.Create(focus)
        refreshEntryItems()
    }

    fun onCreateItemInteraction() {
        selectedItem = SelectedVocabEntry.Create(FocusItem.None)
        refreshEntryItems()
    }

    fun save(proto: VocabEntryProto) {
        val ignored = addVocabEntry(proto).subscribe {
            itemStateHandler.onCreateItemSaved()
        }
    }

    fun deleteEntry(entryId: Long) {
        val ignored = deleteVocabEntry(entryId).subscribe()
    }

    fun onExistingItemWordAChanged(entryId: Long, newWordA: String, selection: TextSelection) {
        itemStateHandler.onExistingItemWordAChanged(entryId, newWordA)
        val focus = FocusItem.WordA(selection)
        selectedItem = SelectedVocabEntry.Existing(entryId, focus)
        refreshEntryItems()
    }

    fun onExistingItemWordBChanged(entryId: Long, newWordB: String, selection: TextSelection) {
        itemStateHandler.onExistingItemWordBChanged(entryId, newWordB)
        val focus = FocusItem.WordB(selection)
        selectedItem = SelectedVocabEntry.Existing(entryId, focus)
        refreshEntryItems()
    }

    private fun refreshEntryItems() {
        vocabEntriesMutableLiveData.value =
            getVocabEntryItemsFromData(itemStateHandler.getAllItems())
    }

    fun onViewModeChanged(itemPresentationData: VocabEntryItemPresentationData, viewMode: ViewMode) {
        when(viewMode) {
            is ViewMode.Active -> {
                val focusItem = viewMode.focusItem

                val data = itemPresentationData.data
                selectedItem = when (data) {
                    is VocabEntryItemData.Create -> SelectedVocabEntry.Create(focusItem)
                    is VocabEntryItemData.Existing ->
                        SelectedVocabEntry.Existing(data.entryId, focusItem)
                }
            }
            ViewMode.Inactive -> {
                // Set selected item to None, but only if [item] is indeed the current selection
                val oldSelected = selectedItem
                when(oldSelected) {
                    is SelectedVocabEntry.Create -> {
                        if(itemPresentationData.data is VocabEntryItemData.Create) {
                            selectedItem = SelectedVocabEntry.None
                        }
                    }
                    is SelectedVocabEntry.Existing -> {
                        val data = itemPresentationData.data
                        if(data is VocabEntryItemData.Existing && oldSelected.id == data.entryId) {
                            selectedItem = SelectedVocabEntry.None
                        }
                    }
                }
            }
        }
        refreshEntryItems()
    }

    fun addTagToVocabEntry(entryItem: VocabEntryItemData, tagItem: TagItem) {

        fun addExistingTag(tag: Tag) {
            when (entryItem) {
                is VocabEntryItemData.Create -> {
                    itemStateHandler.onCreateItemTagAdded(tag)
                }
                is VocabEntryItemData.Existing -> {
                    val ignored = addTagToVocabEntry(entryItem.entryId, tag.id).subscribe()
                }
            }
        }

        when (tagItem) {
            is TagItem.Existing -> {
                addExistingTag(tagItem.data)
            }
            is TagItem.Create -> {
                val tagName = tagItem.name
                calculateColorForNewTag(languageId).subscribe { color ->
                    val proto = TagProto(tagName, color, languageId)
                    addNewTag(proto).subscribe { tag ->
                        addExistingTag(tag)
                    }
                }
            }
        }
    }

    fun requestTagMatches(entryItem: VocabEntryItemData, tagName: String) {
        disposables += findTagNameMatches(languageId, tagName).subscribe { tags ->
            val existingTagItems = tags.map { TagItem.Existing(entryItem, it) }
            val allTags = if (tags.any { it.name == tagName }) {
                existingTagItems
            } else {
                val newTag = TagItem.Create(entryItem, tagName)
                listOf(newTag) + existingTagItems
            }
            tagSuggestionsMutableLiveData.value = allTags
        }
    }

    /** Perform the batched saves of changes to existing items */
    fun saveDataChanges() {
        val existingItems = itemStateHandler.getExistingItems()
        existingItems.forEach {
            val entry = VocabEntry(it.entryId, it.wordA, it.wordB, it.languageId)
            val ignored = updateVocabEntry(entry).subscribe()
        }
    }

    private fun getViewModeForVocabEntryItemData(data: VocabEntryItemData): ViewMode {
        val selectedItem = selectedItem
        return when (data) {
            is VocabEntryItemData.Create -> {
                if (selectedItem is SelectedVocabEntry.Create) {
                    ViewMode.Active(selectedItem.focusItem)
                } else {
                    ViewMode.Inactive
                }
            }
            is VocabEntryItemData.Existing -> {
                if (selectedItem is SelectedVocabEntry.Existing && selectedItem.id == data.entryId) {
                    ViewMode.Active(selectedItem.focusItem)
                } else {
                    ViewMode.Inactive
                }
            }
        }
    }

    private fun getVocabEntryItemsFromData(datas: List<VocabEntryItemData>): List<VocabEntryItemPresentationData> {
        return datas.map {
            val mode = getViewModeForVocabEntryItemData(it)
            when(it) {
                is VocabEntryItemData.Create -> VocabEntryItemPresentationData.Create(it, mode)
                is VocabEntryItemData.Existing -> VocabEntryItemPresentationData.Existing(it, mode)
            }
        }
    }
}
