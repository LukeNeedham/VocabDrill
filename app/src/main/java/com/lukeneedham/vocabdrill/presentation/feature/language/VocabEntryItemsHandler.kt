package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem

class VocabEntryItemsHandler(
    private val languageId: Long,
    private val onItemsChangeListener: (items: List<VocabEntryItem>) -> Unit
) {
    private val existingItems: MutableList<VocabEntryItem.Existing> = mutableListOf()
    private var createItem: VocabEntryItem.Create = newCreateItem()

    fun submitExistingItems(items: List<VocabEntryRelations>) {
        val oldExistingItems = existingItems
        val newExistingItems = items.map { item ->
            // Use the existing existing item, if it exists, to not lose state.
            // Otherwise, create a new instance
            val previousItem = oldExistingItems.firstOrNull { it.entryId == item.vocabEntry.id }
            previousItem ?: VocabEntryItem.Existing.newInstance(item)
        }
        existingItems.clear()
        existingItems.addAll(newExistingItems)
        notifyNewItems()
    }

    fun onCreateItemWordAChanged(
        item: VocabEntryItem.Create,
        newWordA: String
    ) {
        createItem = createItem.copy(wordA = newWordA)
        notifyNewItems()
    }

    fun onCreateItemWordBChanged(
        item: VocabEntryItem.Create,
        newWordB: String
    ) {
        createItem = createItem.copy(wordB = newWordB)
        notifyNewItems()
    }

    fun onCreateItemSaved() {
        createItem = newCreateItem()
        notifyNewItems()
    }

    fun onExistingItemWordAChanged(
        item: VocabEntryItem.Existing,
        newWordA: String
    ) {
        val index = existingItems.indexOfFirst { it.entryId == item.entryId }
        val oldItem = existingItems[index]
        val newItem = oldItem.copy(wordA = newWordA)
        existingItems[index] = newItem
        notifyNewItems()
    }

    fun onExistingItemWordBChanged(
        item: VocabEntryItem.Existing,
        newWordB: String
    ) {
        val index = existingItems.indexOfFirst { it.entryId == item.entryId }
        val oldItem = existingItems[index]
        val newItem = oldItem.copy(wordB = newWordB)
        existingItems[index] = newItem
        notifyNewItems()
    }

    fun onCreateItemTagAdded(tag: Tag) {
        val tags = createItem.tags.toMutableList()
        tags.add(tag)
        createItem = createItem.copy(tags = tags)
        notifyNewItems()
    }

    fun getAllItems(): List<VocabEntryItem> = existingItems + createItem

    fun getExistingItems(): List<VocabEntryItem.Existing> = existingItems

    private fun newCreateItem() = VocabEntryItem.Create.newInstance(languageId)

    private fun notifyNewItems() {
        onItemsChangeListener(getAllItems())
    }
}
