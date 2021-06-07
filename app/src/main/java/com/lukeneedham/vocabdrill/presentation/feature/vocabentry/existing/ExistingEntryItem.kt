package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lukeneedham.vocabdrill.presentation.util.composable.get
import org.koin.core.parameter.parametersOf

@Composable
fun ExistingEntryItem(entryId: Long) {
    val viewModel = remember(entryId) {
        get<ExistingEntryItemViewModel> { parametersOf(entryId) }
    }

    var wordAState by remember { mutableStateOf("") }

    val wordA by viewModel.wordA.collectAsState()
    val wordB by viewModel.wordB.collectAsState()
    val mode by viewModel.mode.collectAsState()

    val canEditText = mode == EntryMode.Edit

    Column {
        Row {
            TextField(
                value = wordAState,
                onValueChange = { wordAState = it },
                enabled = true,
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = wordB,
                onValueChange = viewModel::onWordBChange,
                enabled = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
