package com.lukeneedham.vocabdrill.presentation.feature.language.entries

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.CreateEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.ExistingEntryItem
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@Composable
fun VocabEntries(
    entryKeys: List<EntryKey>,
    selectedEntry: EntryKey?,
    listState: LazyListState,
    onEntrySelected: (key: EntryKey) -> Unit,
    onOpenEntry: (VocabEntryAndTags) -> Unit,
    createWordA: String,
    onCreateWordAChange: (String) -> Unit,
    createWordB: String,
    onCreateWordBChange: (String) -> Unit,
    onEntryAdded: () -> Unit
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
        items(entryKeys) { key ->
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
                EntryKey.Create -> CreateEntryItem(
                    createWordA,
                    onCreateWordAChange,
                    createWordB,
                    onCreateWordBChange,
                    isSelected,
                    onEntryAdded
                ) { onEntrySelected(key) }
            }
        }
    }
}
