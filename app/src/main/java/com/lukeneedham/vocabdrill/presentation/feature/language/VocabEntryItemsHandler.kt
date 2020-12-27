package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem

class VocabEntryItemsHandler(
    private val languageId: Long,
    private val onItemsChangeListener: (items: List<VocabEntryItem>) -> Unit
) {
    private var createItem: VocabEntryItem.Create = newCreateItem()
    private var existingItems: List<VocabEntryItem.Existing> = emptyList()

    fun submitExistingItems(items: List<VocabEntryRelations>) {
        val oldExistingItems = existingItems
        val newExistingItems = items.map { item ->
            // Use the existing existing item, if it exists, to not lose state.
            // Otherwise, create a new instance
            val previousItem = oldExistingItems.firstOrNull { it.data == item }
            previousItem ?: VocabEntryItem.Existing.newInstance(item)
        }
        existingItems = newExistingItems
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

    fun onCreateItemSaving() {
    }

    fun onCreateItemSaved() {
        createItem = newCreateItem()
        notifyNewItems()
    }

    fun getItems(): List<VocabEntryItem> = existingItems + createItem

    private fun newCreateItem() = VocabEntryItem.Create.newInstance(languageId)

    private fun notifyNewItems() {
        onItemsChangeListener(getItems())
    }
}
