package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItemProps
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.*
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateProps
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingProps
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground

/** Responsible for providing the [VocabEntryEditItem]s to be shown, and managing their state */
class VocabEntryReduxer(
    private val languageId: Long,
    private val chooseTextColourForBackground: ChooseTextColourForBackground,
    private val onItemsChangeListener: (items: List<VocabEntryViewProps>, refresh: Boolean) -> Unit
) {
    private val existingItems: MutableList<VocabEntryEditPartialItem.Existing> = mutableListOf()
    private var createItem: VocabEntryEditPartialItem.Create = newCreateItem()

    private var selectedItem: SelectedVocabEntry = SelectedVocabEntry.None
        set(value) {
            field = value
            if (value.getFocusItem() !is FocusItem.AddTag) {
                // Reset tag suggestions - add tag has lost focus, suggestions are thus stale
                tagSuggestionsResult = null
            }
        }
    private var tagSuggestionsResult: TagSuggestionResult? = null

    init {
        notifyNewItems(true)
    }

    fun setTagSuggestionsResult(result: TagSuggestionResult) {
        val selectedItem = selectedItem
        val selectedFocusItem = selectedItem.getFocusItem()
        if (selectedFocusItem !is FocusItem.AddTag) {
            return
        }

        tagSuggestionsResult = result

        val newFocusItem = selectedFocusItem.copy(tagSuggestions = result.suggestions)

        when (selectedItem) {
            is SelectedVocabEntry.None -> {
            }
            is SelectedVocabEntry.Create -> {
                this.selectedItem = selectedItem.copy(focusItem = newFocusItem)
            }
            is SelectedVocabEntry.Existing -> {
                this.selectedItem = selectedItem.copy(focusItem = newFocusItem)
            }
        }

        notifyNewItems(true)
    }

    fun submitExistingItems(items: List<VocabEntryAndTags>) {
        val newExistingItems = items.map { createVocabEntryOrCreateFromEntry(it) }
        existingItems.clear()
        existingItems.addAll(newExistingItems)
        notifyNewItems(true)
    }

    fun onCreateItemWordAChanged(newWordA: String, selection: TextSelection) {
        createItem = createItem.copy(wordA = newWordA)
        val focus = FocusItem.WordA(selection)
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems(false)
    }

    fun onCreateItemWordBChanged(newWordB: String, selection: TextSelection) {
        createItem = createItem.copy(wordB = newWordB)
        val focus = FocusItem.WordB(selection)
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems(false)
    }

    fun selectCreateItem(section: InteractionSection, selection: TextSelection?) {
        val selectionOrDefault = selection ?: TextSelection.End
        val focus = when (section) {
            InteractionSection.WordAInput -> FocusItem.WordA(selectionOrDefault)
            InteractionSection.WordBInput -> FocusItem.WordB(selectionOrDefault)
            InteractionSection.Other -> FocusItem.None
        }
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems(true)
    }

    fun onCreateItemSaved() {
        createItem = newCreateItem()
        selectedItem = SelectedVocabEntry.Create(FocusItem.WordA(TextSelection.End))
        notifyNewItems(true)
    }

    /** Focus the word A input of the create item. Allows users to start typing directly */
    fun focusCreateItemWordA() {
        val focus = FocusItem.WordA(TextSelection.End)
        selectedItem = SelectedVocabEntry.Create(focus)
        notifyNewItems(true)
    }

    fun focusCreateItem() {
        selectedItem = SelectedVocabEntry.Create(FocusItem.None)
        notifyNewItems(true)
    }

    fun focusExistingItem(entryId: Long) {
        selectedItem = SelectedVocabEntry.Existing(entryId, FocusItem.None)
        notifyNewItems(true)
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
        notifyNewItems(false)
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
        notifyNewItems(false)
    }

    fun onCreateItemTagAdded(tag: Tag) {
        val tags = createItem.tags.toMutableList()
        tags.add(tag)
        createItem = createItem.copy(tags = tags)
        notifyNewItems(true)
    }

    fun onCreateItemTagRemoved(tag: Tag) {
        val tags = createItem.tags.toMutableList()
        tags.remove(tag)
        createItem = createItem.copy(tags = tags)
        notifyNewItems(true)
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

        val viewMode = getViewMode(entry)

        val existingTagItems = it.entryAndTags.tags.map { createExistingTagItem(it) }
        val allTagItems = if (viewMode is ViewMode.Inactive) {
            existingTagItems
        } else {
            val addTagFocus = (viewMode as? ViewMode.Active)?.focusItem as? FocusItem.AddTag
            val createTagText = addTagFocus?.text ?: ""
            val createTagSelection = addTagFocus?.selection
            existingTagItems + TagItemProps.Create(createTagText, createTagSelection)
        }

        VocabEntryEditItem.Existing(entry, allTagItems)
    }

    fun getCreateEditItem(): VocabEntryEditItem.Create {
        val createItem = createItem
        val selectedItem = selectedItem

        val existingTagItems = createItem.tags.map { createExistingTagItem(it) }

        val addTagFocus = if (selectedItem is SelectedVocabEntry.Create
            && selectedItem.focusItem is FocusItem.AddTag
        ) {
            selectedItem.focusItem
        } else {
            null
        }

        val focusText = addTagFocus?.text ?: ""
        val focusSelection = addTagFocus?.selection
        val allTagItems = existingTagItems + TagItemProps.Create(focusText, focusSelection)

        return VocabEntryEditItem.Create(
            createItem.languageId,
            allTagItems,
            createItem.wordA,
            createItem.wordB
        )
    }

    fun onAddTagUpdate(
        editItem: VocabEntryEditItem,
        text: String,
        selection: TextSelection
    ) {
        val tagSuggestionsResult = tagSuggestionsResult
        val resultItem = tagSuggestionsResult?.item
        val isSameItem = resultItem != null && resultItem.isSameItem(editItem)
        val tagSuggestions =
            (if (!isSameItem) null else tagSuggestionsResult?.suggestions)

        val focusItem = FocusItem.AddTag(selection, text, tagSuggestions)
        val viewMode = ViewMode.Active(focusItem)
        onViewModeChanged(editItem, viewMode)
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
        notifyNewItems(true)
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
    private fun notifyNewItems(refresh: Boolean) {
        onItemsChangeListener(getAllProps(), refresh)
    }

    private fun createVocabEntryOrCreateFromEntry(entryAndTags: VocabEntryAndTags) =
        VocabEntryEditPartialItem.Existing(entryAndTags)

    private fun createExistingTagItem(tag: Tag): TagItemProps.Existing {
        val textColor = chooseTextColourForBackground(tag.color)
        return TagItemProps.Existing(tag, textColor)
    }

    private fun convertEditItemsToProps(allItems: List<VocabEntryEditItem>): List<VocabEntryViewProps> {
        return allItems.map { editItem ->
            val viewMode = getViewMode(editItem)

            when (editItem) {
                is VocabEntryEditItem.Existing -> VocabEntryExistingProps(editItem, viewMode)
                is VocabEntryEditItem.Create -> VocabEntryCreateProps(editItem, viewMode)
            }
        }
    }

    private fun getViewMode(entry: VocabEntry): ViewMode {
        val selectedItem = selectedItem

        return if (
            selectedItem is SelectedVocabEntry.Existing &&
            selectedItem.id == entry.id
        ) {
            ViewMode.Active(selectedItem.focusItem)
        } else {
            ViewMode.Inactive
        }
    }

    private fun getViewMode(editItem: VocabEntryEditItem): ViewMode {
        val selectedItem = selectedItem

        return when {
            editItem is VocabEntryEditItem.Existing -> getViewMode(editItem.entry)

            editItem is VocabEntryEditItem.Create && selectedItem is SelectedVocabEntry.Create ->
                ViewMode.Active(selectedItem.focusItem)

            else -> ViewMode.Inactive
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
