package com.lukeneedham.vocabdrill.presentation.feature.language.entries

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.CreateEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.ExistingEntryItem
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@Composable
fun VocabEntries(
    entryKeys: List<EntryKey>,
    selectedEntry: EntryKey?,
    listState: LazyListState,
    onEntrySelected: (key: EntryKey) -> Unit,
    onOpenEntry: (VocabEntryAndTags) -> Unit
) {

    LaunchedEffect(selectedEntry) {
        // When the selected entry changes to the create entry, scroll to the create entry
        if (selectedEntry is EntryKey.Create) {
            // Scroll first
            listState.animateScrollToItem(entryKeys.indexOf(EntryKey.Create))
            // The keyboard will now show. We have to wait some time for the screen to finish resizing
            delay(400)
            // Scroll again, for the newly resized screen
            listState.animateScrollToItem(entryKeys.indexOf(EntryKey.Create))
        }
    }

    LazyColumn(state = listState, modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        itemsIndexed(entryKeys) { index, key ->
            val isSelected = selectedEntry == key

            val exhaustive = when (key) {
                is EntryKey.Existing -> {
                    val entry = key.taggedEntry
                    ExistingEntryItem(
                        entry,
                        isSelected,
                        { onEntrySelected(key) },
                        { onOpenEntry(entry) }
                    )
                }
                EntryKey.Create -> CreateEntryItem(isSelected) { onEntrySelected(key) }
            }
        }
    }
}
