package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemData

class VocabEntryItemsHandler(
    private val languageId: Long,
    private val onItemsChangeListener: (items: List<VocabEntryItemData>) -> Unit
) {
    private val existingItems: MutableList<VocabEntryItemData.Existing> = mutableListOf()
    private var createItem: VocabEntryItemData.Create = newCreateItem()

    fun submitExistingItems(items: List<VocabEntryRelations>) {
        val newExistingItems = items.map { VocabEntryItemData.Existing.newInstance(it) }
        existingItems.clear()
        existingItems.addAll(newExistingItems)
        notifyNewItems()
    }

    fun onCreateItemWordAChanged(newWordA: String) {
        createItem = createItem.copy(wordA = newWordA)
        notifyNewItems()
    }

    fun onCreateItemWordBChanged(newWordB: String) {
        createItem = createItem.copy(wordB = newWordB)
        notifyNewItems()
    }

    fun onCreateItemSaved() {
        createItem = newCreateItem()
        notifyNewItems()
    }

    fun onExistingItemWordAChanged(
        entryId: Long,
        newWordA: String
    ) {
        val index = existingItems.indexOfFirst { it.entryId == entryId }
        val oldItem = existingItems[index]
        val newItem = oldItem.copy(wordA = newWordA)
        existingItems[index] = newItem
        notifyNewItems()
    }

    fun onExistingItemWordBChanged(
        entryId: Long,
        newWordB: String
    ) {
        val index = existingItems.indexOfFirst { it.entryId == entryId }
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

    fun onCreateItemTagRemoved(tag: Tag) {
        val tags = createItem.tags.toMutableList()
        tags.remove(tag)
        createItem = createItem.copy(tags = tags)
        notifyNewItems()
    }

    fun getAllItems(): List<VocabEntryItemData> = existingItems + createItem

    fun getExistingItems(): List<VocabEntryItemData.Existing> = existingItems

    private fun newCreateItem() = VocabEntryItemData.Create.newInstance(languageId)

    private fun notifyNewItems() {
        onItemsChangeListener(getAllItems())
    }
}
