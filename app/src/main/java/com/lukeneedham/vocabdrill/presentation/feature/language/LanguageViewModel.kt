package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.*
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagPresentItem
import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.InteractionSection
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryViewProps
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.*
import io.reactivex.disposables.Disposable
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
    private val deleteUnusedTags: DeleteUnusedTags,
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) : DisposingViewModel() {

    private var findTagNameMatchesDisposable: Disposable? = null

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val vocabEntriesOrCreateMutableLiveData = MutableLiveData<List<VocabEntryViewProps>>()
    val vocabEntriesOrCreateLiveData = vocabEntriesOrCreateMutableLiveData.toLiveData()

    private val entryReduxer = VocabEntryReduxer(languageId, chooseTextColourForBackground) {
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
            entryReduxer.submitExistingItems(it)
        }
    }

    fun onCreateItemWordAChanged(newWordA: String, selection: TextSelection) {
        entryReduxer.onCreateItemWordAChanged(newWordA, selection)
    }

    fun onCreateItemWordBChanged(newWordB: String, selection: TextSelection) {
        entryReduxer.onCreateItemWordBChanged(newWordB, selection)
    }

    fun onCreateItemInteraction(section: InteractionSection, selection: TextSelection?) {
        entryReduxer.selectCreateItem(section, selection)
    }

    fun addEntry(proto: VocabEntryProto) {
        val ignored = addVocabEntry(proto).subscribe {
            entryReduxer.onCreateItemSaved()
        }
    }

    fun deleteEntry(entryId: Long) {
        val ignored = deleteVocabEntry(entryId).subscribe {
            deleteUnusedTagsExceptCreate()
        }
    }

    fun onExistingItemWordAChanged(entryId: Long, newWordA: String, selection: TextSelection) {
        entryReduxer.onExistingItemWordAChanged(entryId, newWordA, selection)
    }

    fun onExistingItemWordBChanged(entryId: Long, newWordB: String, selection: TextSelection) {
        entryReduxer.onExistingItemWordBChanged(entryId, newWordB, selection)
    }

    fun getLearnSet(): LearnSet {
        val entriesOrCreate = vocabEntriesOrCreateMutableLiveData.value ?: emptyList()
        val entries = entriesOrCreate.filterIsInstance<VocabEntryEditItem.Existing>()
            .map { it.entry }
        return LearnSet(entries)
    }

    fun focusCreateItem() {
        entryReduxer.focusCreateItem()
    }

    fun onViewModeChanged(item: VocabEntryEditItem, viewMode: ViewMode) {
        entryReduxer.onViewModeChanged(item, viewMode)
    }

    fun addTagToVocabEntry(entryItem: VocabEntryEditItem, tagItem: TagSuggestion) {

        fun addExistingTag(tag: Tag) {
            when (entryItem) {
                is VocabEntryEditItem.Create -> {
                    entryReduxer.onCreateItemTagAdded(tag)
                }
                is VocabEntryEditItem.Existing -> {
                    val ignored = addTagToVocabEntry(entryItem.entry.id, tag.id).subscribe()
                }
            }
        }

        val exhaustive = when (entryItem) {
            is VocabEntryEditItem.Existing -> {
                entryReduxer.focusExistingItem(entryItem.entry.id)
            }
            is VocabEntryEditItem.Create -> {
                entryReduxer.focusCreateItem()
            }
        }

        when (tagItem) {
            is TagSuggestion.Existing -> {
                addExistingTag(tagItem.data)
            }
            is TagSuggestion.Create -> {
                val proto = TagProto(tagItem.name, tagItem.color, languageId)
                addNewTag(proto).subscribe { tag ->
                    addExistingTag(tag)
                }
            }
        }
    }

    fun deleteTagFromVocabEntry(entryItem: VocabEntryEditItem, tagItem: TagPresentItem.Existing) {
        when (entryItem) {
            is VocabEntryEditItem.Create -> {
                entryReduxer.onCreateItemTagRemoved(tagItem.data)
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
        fun updateTagSuggestionResult(suggestions: List<TagSuggestion>) {
            entryReduxer.setTagSuggestionsResult(TagSuggestionResult(entryItem, suggestions))
        }

        findTagNameMatchesDisposable?.dispose()
        val tagIdsAlreadyPresent =
            entryItem.tagItems.filterIsInstance<TagPresentItem.Existing>().map { it.data.id }
        val findTagNameMatchesDisposable =
            findTagNameMatches(languageId, tagName).subscribe { tags ->
                val tagItems = tags.map {
                    TagSuggestion.Existing(it, chooseTextColourForBackground(it.color))
                }
                val nonDuplicateTagItems = tagItems.toMutableList().apply {
                    removeAll { it.data.id in tagIdsAlreadyPresent }
                }.toList()
                val showNewTag = tagName.isNotBlank() && tags.none { it.name == tagName }
                if (!showNewTag) {
                    updateTagSuggestionResult(nonDuplicateTagItems)
                } else {
                    // Current tag name doesn't currently exist - show it as a new suggestion
                    disposables += calculateColorForNewTag(languageId).subscribe { color ->
                        val newTag = TagSuggestion.Create(
                            tagName,
                            color,
                            chooseTextColourForBackground(color)
                        )
                        val allTags = listOf(newTag) + nonDuplicateTagItems
                        updateTagSuggestionResult(allTags)
                    }
                }
            }
        this.findTagNameMatchesDisposable = findTagNameMatchesDisposable
        disposables += findTagNameMatchesDisposable
    }

    /** Perform the batched saves of changes to existing items */
    fun saveDataChanges() {
        val existingItems = entryReduxer.getExistingEditItems()
        existingItems.forEach {
            val ignored = updateVocabEntry(it.entry).subscribe()
        }
    }

    private fun getCreateItemTagIds(): List<Long> = entryReduxer.getCreateEditItem().tagItems
        .filterIsInstance<TagPresentItem.Existing>()
        .map { it.data.id }

    /** Delete all unused tags, except the ones referenced by the create item */
    private fun deleteUnusedTagsExceptCreate() {
        val ignored = deleteUnusedTags(getCreateItemTagIds()).subscribe()
    }
}
