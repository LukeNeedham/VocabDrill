package com.lukeneedham.vocabdrill.presentation.feature.language.entries

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.CreateEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.ExistingEntryItem
import com.lukeneedham.vocabdrill.presentation.util.composable.get
import org.koin.core.parameter.parametersOf

@ExperimentalAnimationApi
@Composable
fun VocabEntries(languageId: Long) {
    val viewModel = remember(languageId) { get<VocabEntriesViewModel> { parametersOf(languageId) } }

    val entryKeys by viewModel.entryKeys.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()

    LazyColumn(Modifier.padding(start = 10.dp, end = 10.dp)) {
        items(entryKeys) { key ->
            val isSelected = selectedEntry == key

            when (key) {
                is EntryKey.Existing -> {
                    ExistingEntryItem(
                        key.taggedEntry,
                        isSelected,
                        { viewModel.onExistingEntrySelectedChange(key, it) },
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                EntryKey.Create -> CreateEntryItem()
            }
        }
    }
}
