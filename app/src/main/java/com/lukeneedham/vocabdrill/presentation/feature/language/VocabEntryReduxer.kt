package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagPresentItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.*
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateProps
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingProps
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground

/** Responsible for providing the [VocabEntryEditItem]s to be shown, and managing their state */
class VocabEntryReduxer(
    private val languageId: Long,
    private val chooseTextColourForBackground: ChooseTextColourForBackground,
    private val onItemsChangeListener: (items: List<VocabEntryViewProps>) -> Unit
) {
    private val existingItems: MutableList<VocabEntryEditPartialItem.Existing> = mutableListOf()
    private var createItem: VocabEntryEditPartialItem.Create = newCreateItem()

    private var selectedItem: SelectedVocabEntry = SelectedVocabEntry.None
    private var tagSuggestionsResult: TagSuggestionResult? = null

    init {
        notifyNewItems()
    }

    fun setTagSuggestionsResult(result: TagSuggestionResult) {
        tagSuggestionsResult = result
        notifyNewItems()
    }

    fun submitExistingItems(items: List<VocabEntryAndTags>) {
        val newExistingItems = items.map { createVocabEntryOrCreateFromEntry(it) }
        existingItems.clear()
        existingItems.addAll(newExistingItems)
        notifyNewItems()
    }

    fun onCreateItemWordAChanged(newWordA: String, selection: TextSelection) {
        createItem = createItem.copy(wordA = newWordA)
        val focus = FocusItem.WordA(selection)
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems()
    }

    fun onCreateItemWordBChanged(newWordB: String, selection: TextSelection) {
        createItem = createItem.copy(wordB = newWordB)
        val focus = FocusItem.WordB(selection)
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems()
    }

    fun selectCreateItem(section: InteractionSection, selection: TextSelection?) {
        val selectionOrDefault = selection ?: TextSelection.End
        val focus = when (section) {
            InteractionSection.WordAInput -> FocusItem.WordA(selectionOrDefault)
            InteractionSection.WordBInput -> FocusItem.WordB(selectionOrDefault)
            // TODO: We need a section for tag name input? Maybe?
            InteractionSection.Other -> FocusItem.None
        }
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems()
    }

    fun onCreateItemSaved() {
        createItem = newCreateItem()
        selectedItem = SelectedVocabEntry.Create(FocusItem.WordA(TextSelection.End))
        notifyNewItems()
    }

    fun focusCreateItem() {
        val focus = FocusItem.WordA(TextSelection.End)
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems()
    }

    fun focusExistingItem(entryId: Long) {
        selectedItem = SelectedVocabEntry.Existing(entryId, FocusItem.None)
        notifyNewItems()
    }

    fun onExistingItemWordAChanged(entryId: Long, newWordA: String, selection: TextSelection) {
        updateEntryId(entryId) {
            it.copy(
                entry = it.entry.copy(
                    wordA = newWordA
                )
            )
        }
        val focus = FocusItem.WordA(selection)
        selectedItem = SelectedVocabEntry.Existing(entryId, focus)
        notifyNewItems()
    }

    fun onExistingItemWordBChanged(entryId: Long, newWordB: String, selection: TextSelection) {
        updateEntryId(entryId) {
            it.copy(
                entry = it.entry.copy(
                    wordB = newWordB
                )
            )
        }
        val focus = FocusItem.WordB(selection)
        selectedItem = SelectedVocabEntry.Existing(entryId, focus)
        notifyNewItems()
    }

    fun onCreateItemTagAdded(tag: Tag) {
        val tags = createItem.tags.toMutableList()
        tags.add(tag)
        createItem = createItem.copy(tags = tags)
        notifyNewItems()
    }

    fun onCreateItemTagRemoved(tag: Tag) {
        val tags = createItem.tags.toMutableList()
        tags.remove(tag)
        createItem = createItem.copy(tags = tags)
        notifyNewItems()
    }

    /** @return all items, after fixing the viewmode for the selected item */
    fun getAllItems(): List<VocabEntryEditItem> {
        val existingEditItems = getExistingEditItems()
        val createEditItem = getCreateEditItem()
        return existingEditItems + createEditItem
    }

    fun getAllProps() = convertEditItemsToProps(getAllItems())

    fun getExistingEditItems(): List<VocabEntryEditItem.Existing> = existingItems.map {
        val entry = it.entryAndTags.entry
        val selectedItem = selectedItem

        val viewMode = if (
            selectedItem is SelectedVocabEntry.Existing &&
            selectedItem.id == entry.id
        ) {
            ViewMode.Active(selectedItem.focusItem)
        } else {
            ViewMode.Inactive
        }

        val existingTagItems = it.entryAndTags.tags.map { createExistingTagItem(it) }
        val allTagItems = if (viewMode is ViewMode.Inactive) {
            existingTagItems
        } else {
            existingTagItems + TagPresentItem.Create
        }

        VocabEntryEditItem.Existing(entry, allTagItems, viewMode)
    }

    fun getCreateEditItem(): VocabEntryEditItem.Create {
        val createItem = createItem
        val selectedItem = selectedItem
        val mode = if (selectedItem is SelectedVocabEntry.Create) {
            ViewMode.Active(selectedItem.focusItem)
        } else {
            ViewMode.Inactive
        }

        val existingTagItems = createItem.tags.map { createExistingTagItem(it) }
        val allTagItems = existingTagItems + TagPresentItem.Create

        return VocabEntryEditItem.Create(
            createItem.languageId,
            allTagItems,
            createItem.wordA,
            createItem.wordB,
            mode
        )
    }

    fun onViewModeChanged(
        item: VocabEntryEditItem,
        viewMode: ViewMode
    ) {
        when (viewMode) {
            is ViewMode.Active -> {
                val focusItem = viewMode.focusItem

                selectedItem = when (item) {
                    is VocabEntryEditItem.Create -> SelectedVocabEntry.Create(focusItem)
                    is VocabEntryEditItem.Existing -> {
                        SelectedVocabEntry.Existing(item.entry.id, focusItem)
                    }
                }
            }
            ViewMode.Inactive -> {
                // Set selected item to None, but only if [item] is indeed the current selection
                val oldSelected = selectedItem
                when (oldSelected) {
                    is SelectedVocabEntry.Create -> {
                        if (item is VocabEntryEditItem.Create) {
                            selectedItem = SelectedVocabEntry.None
                        }
                    }
                    is SelectedVocabEntry.Existing -> {
                        if (item is VocabEntryEditItem.Existing && oldSelected.id == item.entry.id) {
                            selectedItem = SelectedVocabEntry.None
                        }
                    }
                }
            }
        }
        notifyNewItems()
    }

    private fun newCreateItem() = VocabEntryEditPartialItem.Create.newInstance(languageId)

    private fun updateEntryId(id: Long, updateWork: (VocabEntryAndTags) -> VocabEntryAndTags) {
        val index = existingItems.indexOfFirst { it.entryAndTags.entry.id == id }
        val oldItem = existingItems[index].entryAndTags
        val newEntry = updateWork(oldItem)
        val newItem = createVocabEntryOrCreateFromEntry(newEntry)
        existingItems[index] = newItem
    }

    /** Fire this to kick off a recalculation of items */
    private fun notifyNewItems() {
        onItemsChangeListener(getAllProps())
    }

    private fun createVocabEntryOrCreateFromEntry(entryAndTags: VocabEntryAndTags) =
        VocabEntryEditPartialItem.Existing(entryAndTags)

    private fun createExistingTagItem(tag: Tag): TagPresentItem.Existing {
        val textColor = chooseTextColourForBackground(tag.color)
        return TagPresentItem.Existing(tag, textColor)
    }

    private fun convertEditItemsToProps(allItems: List<VocabEntryEditItem>): List<VocabEntryViewProps> {
        return allItems.map { entry ->
            val tagSuggestionsResult = tagSuggestionsResult
            val resultItem = tagSuggestionsResult?.item
            val isSameItem = resultItem != null && resultItem.isSameItem(entry)
            val suggestions = if (!isSameItem) null else tagSuggestionsResult?.suggestions

            val viewMode = if (
                selectedItem is SelectedVocabEntry.Existing &&
                selectedItem.id == entry
            ) {
                ViewMode.Active(selectedItem.focusItem)
            } else {
                ViewMode.Inactive
            }

            when (entry) {
                is VocabEntryEditItem.Existing -> VocabEntryExistingProps(entry, suggestions)
                is VocabEntryEditItem.Create -> VocabEntryCreateProps(entry, suggestions)
            }
        }
    }

    private sealed class VocabEntryEditPartialItem {
        data class Existing(val entryAndTags: VocabEntryAndTags) : VocabEntryEditPartialItem()

        data class Create(
            val languageId: Long,
            val tags: List<Tag>,
            val wordA: String?,
            val wordB: String?
        ) : VocabEntryEditPartialItem() {

            companion object {
                fun newInstance(languageId: Long) = Create(languageId, emptyList(), null, null)
            }
        }
    }
}
