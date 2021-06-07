package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun CreateEntryItem() {

    var wordA by remember { mutableStateOf("") }
    var wordB by remember { mutableStateOf("") }

    Column {
        Row {
            TextField(
                value = wordA,
                onValueChange = { wordA = it },
                enabled = true,
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = wordB,
                onValueChange = { wordB = it },
                enabled = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
