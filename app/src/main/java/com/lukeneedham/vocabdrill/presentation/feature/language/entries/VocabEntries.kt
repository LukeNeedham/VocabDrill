package com.lukeneedham.vocabdrill.presentation.feature.language.entries

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.CreateEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.ExistingEntryItem
import com.lukeneedham.vocabdrill.presentation.util.composable.get
import org.koin.core.parameter.parametersOf

@Composable
fun VocabEntries(languageId: Long) {
    val viewModel = remember(languageId) { get<VocabEntriesViewModel> { parametersOf(languageId) } }

    val entryKeys by viewModel.entryKeys.collectAsState()

    LazyColumn {
        items(entryKeys) { key ->
            when (key) {
                is EntryKey.Existing -> ExistingEntryItem(key.id)
                EntryKey.Create -> CreateEntryItem()
            }
        }
    }
}
