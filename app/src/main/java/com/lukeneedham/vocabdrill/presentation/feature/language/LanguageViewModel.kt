package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.*
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagSuggestionItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.InteractionSection
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.*
import io.reactivex.rxkotlin.plusAssign

class LanguageViewModel(
    val languageId: Long,
    private val observeAllVocabEntryAndTagsForLanguage: ObserveAllVocabEntryAndTagsForLanguage,
    private val observeLanguage: ObserveLanguage,
    private val addVocabEntry: AddVocabEntry,
    private val deleteVocabEntry: DeleteVocabEntry,
    private val updateVocabEntry: UpdateVocabEntry,
    private val addTagToVocabEntry: AddTagToVocabEntry,
    private val addNewTag: AddNewTag,
    private val calculateColorForNewTag: CalculateColorForNewTag,
    private val findTagNameMatches: FindTagNameMatches,
    private val deleteTagFromVocabEntry: DeleteTagFromVocabEntry,
    private val deleteUnusedTags: DeleteUnusedTags
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabEntriesOrCreateMutableLiveData = MutableLiveData<List<VocabEntryEditItem>>()
    val vocabEntriesOrCreateLiveData = vocabEntriesOrCreateMutableLiveData.toLiveData()

    private val tagSuggestionsMutableLiveData = MutableLiveData<List<TagSuggestionItem>>()
    val tagSuggestionsLiveData = tagSuggestionsMutableLiveData.toLiveData()

    private val itemStateHandler = VocabEntryEditItemsHandler(languageId) {
        vocabEntriesOrCreateMutableLiveData.value = it
    }

    init {
        /*
        It's possible that tags are created by create item,
        and then create item resets (for example, by killing the Fragment).
        So we need to clean up tags in init.
        */
        deleteUnusedTagsExceptCreate()

        disposables += observeLanguage(languageId).subscribe { language ->
            languageNameMutableLiveData.value = language.name
            countryMutableLiveData.value = language.country
        }

        disposables += observeAllVocabEntryAndTagsForLanguage(languageId).subscribe {
            itemStateHandler.submitExistingItems(it)
        }
    }

    fun onCreateItemWordAChanged(newWordA: String, selection: TextSelection) {
        itemStateHandler.onCreateItemWordAChanged(newWordA, selection)
    }

    fun onCreateItemWordBChanged(newWordB: String, selection: TextSelection) {
        itemStateHandler.onCreateItemWordBChanged(newWordB, selection)
    }

    fun onCreateItemInteraction(section: InteractionSection, selection: TextSelection?) {
        itemStateHandler.selectCreateItem(section, selection)
    }

    fun addEntry(proto: VocabEntryProto) {
        val ignored = addVocabEntry(proto).subscribe {
            itemStateHandler.onCreateItemSaved()
        }
    }

    fun deleteEntry(entryId: Long) {
        val ignored = deleteVocabEntry(entryId).subscribe {
            deleteUnusedTagsExceptCreate()
        }
    }

    fun onExistingItemWordAChanged(entryId: Long, newWordA: String, selection: TextSelection) {
        itemStateHandler.onExistingItemWordAChanged(entryId, newWordA, selection)
    }

    fun onExistingItemWordBChanged(entryId: Long, newWordB: String, selection: TextSelection) {
        itemStateHandler.onExistingItemWordBChanged(entryId, newWordB, selection)
    }

    fun getLearnSet(): LearnSet {
        val entriesOrCreate = vocabEntriesOrCreateMutableLiveData.value ?: emptyList()
        val entries = entriesOrCreate.filterIsInstance<VocabEntryEditItem.Existing>()
            .map { it.entry }
        return LearnSet(entries)
    }

    fun focusCreateItem() {
        itemStateHandler.focusCreateItem()
    }

    fun onViewModeChanged(item: VocabEntryEditItem, viewMode: ViewMode) {
        itemStateHandler.onViewModeChanged(item, viewMode)
    }

    fun addTagToVocabEntry(entryItem: VocabEntryEditItem, tagItem: TagSuggestionItem) {

        fun addExistingTag(tag: Tag) {
            when (entryItem) {
                is VocabEntryEditItem.Create -> {
                    itemStateHandler.onCreateItemTagAdded(tag)
                }
                is VocabEntryEditItem.Existing -> {
                    val ignored = addTagToVocabEntry(entryItem.entry.id, tag.id).subscribe()
                }
            }
        }

        when (tagItem) {
            is TagSuggestionItem.Existing -> {
                addExistingTag(tagItem.data)
            }
            is TagSuggestionItem.Create -> {
                val proto = TagProto(tagItem.name, tagItem.color, languageId)
                addNewTag(proto).subscribe { tag ->
                    addExistingTag(tag)
                }
            }
        }
    }

    fun deleteTagFromVocabEntry(entryItem: VocabEntryEditItem, tagItem: TagItem.Existing) {
        when (entryItem) {
            is VocabEntryEditItem.Create -> {
                itemStateHandler.onCreateItemTagRemoved(tagItem.data)
                deleteUnusedTagsExceptCreate()
            }
            is VocabEntryEditItem.Existing -> {
                val ignored =
                    deleteTagFromVocabEntry(entryItem.entry.id, tagItem.data.id).subscribe {
                        deleteUnusedTagsExceptCreate()
                    }
            }
        }
    }

    fun requestTagMatches(entryItem: VocabEntryEditItem, tagName: String) {
        disposables += findTagNameMatches(languageId, tagName).subscribe { tags ->
            val existingTagItems = tags.map { TagSuggestionItem.Existing(entryItem, it) }
            if (tags.any { it.name == tagName }) {
                tagSuggestionsMutableLiveData.value = existingTagItems
            } else {
                disposables += calculateColorForNewTag(languageId).subscribe { color ->
                    val newTag = TagSuggestionItem.Create(entryItem, tagName, color)
                    val allTags = listOf(newTag) + existingTagItems
                    tagSuggestionsMutableLiveData.value = allTags
                }
            }
        }
    }

    /** Perform the batched saves of changes to existing items */
    fun saveDataChanges() {
        val existingItems = itemStateHandler.getExistingEditItems()
        existingItems.forEach {
            val ignored = updateVocabEntry(it.entry).subscribe()
        }
    }

    private fun getCreateItemTagIds(): List<Long> = itemStateHandler.getCreateEditItem().tags
        .filterIsInstance<TagItem.Existing>()
        .map { it.data.id }

    /** Delete all unused tags, except the ones referenced by the create item */
    private fun deleteUnusedTagsExceptCreate() {
        val ignored = deleteUnusedTags(getCreateItemTagIds()).subscribe()
    }
}
