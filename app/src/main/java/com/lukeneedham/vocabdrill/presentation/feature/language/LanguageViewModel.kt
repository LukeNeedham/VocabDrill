package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.*
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
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
        vocabEntriesMutableLiveData.value = it
    }

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabEntriesMutableLiveData = MutableLiveData<List<VocabEntryItem>>()
    val vocabEntriesLiveData = vocabEntriesMutableLiveData.toLiveData()

    private val tagSuggestionsMutableLiveData = MutableLiveData<List<TagItem>>()
    val tagSuggestionsLiveData = tagSuggestionsMutableLiveData.toLiveData()

    init {
        vocabEntriesMutableLiveData.value = itemStateHandler.getAllItems()

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
        val ignored = addVocabEntry(proto).subscribe {
            itemStateHandler.onCreateItemSaved()
        }
    }

    fun deleteEntry(item: VocabEntryItem.Existing) {
        val ignored = deleteVocabEntry(item.entryId).subscribe()
    }

    fun onExistingItemWordAChanged(item: VocabEntryItem.Existing, newWordA: String) {
        itemStateHandler.onExistingItemWordAChanged(item, newWordA)
    }

    fun onExistingItemWordBChanged(item: VocabEntryItem.Existing, newWordB: String) {
        itemStateHandler.onExistingItemWordBChanged(item, newWordB)
    }

    fun addTagToVocabEntry(entryItem: VocabEntryItem, tagItem: TagItem) {
        fun addExistingTag(tag: Tag) {
            when (entryItem) {
                is VocabEntryItem.Create -> {
                    itemStateHandler.onCreateItemTagAdded(tag)
                }
                is VocabEntryItem.Existing -> {
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

    fun requestTagMatches(entryItem: VocabEntryItem, tagName: String) {
        disposables += findTagNameMatches(languageId, tagName).subscribe { tags ->
            val existingTagItems = tags.map { TagItem.Existing(entryItem, it) }
            val allTags = if(tags.any { it.name == tagName }) {
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
}
