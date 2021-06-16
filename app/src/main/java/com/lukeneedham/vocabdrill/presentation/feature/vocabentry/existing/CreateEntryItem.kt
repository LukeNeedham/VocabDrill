package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagsList
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor

@Composable
fun CreateEntryItem(
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    var wordA by remember { mutableStateOf("") }
    var wordB by remember { mutableStateOf("") }

    val wordAFocusRequester = remember { FocusRequester() }

    val backgroundColor =
        if (isSelected) ThemeColor.vocabEntryExistingBackgroundSelected else ThemeColor.vocabEntryExistingBackgroundUnselected

    var isWordAFocused by remember { mutableStateOf(false) }
    var isWordBFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isSelected) {
        if (isSelected && !isWordAFocused && !isWordBFocused) {
            wordAFocusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .clickable {
                onSelected()
            }
            .padding(top = 3.dp, bottom = 3.dp)
            .background(
                backgroundColor,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row {
            BasicTextField(
                value = wordA,
                onValueChange = { wordA = it },
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
                    .onFocusChanged {
                        isWordAFocused = it.isFocused
                        if (it.isFocused) {
                            onSelected()
                        }
                    }
                    .focusRequester(wordAFocusRequester)
            )
            BasicTextField(
                value = wordB,
                onValueChange = { wordB = it },
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, end = 10.dp, start = 10.dp)
                    .onFocusChanged {
                        isWordBFocused = it.isFocused
                        if (it.isFocused) {
                            onSelected()
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .align(Alignment.CenterVertically)
                    .clipToBounds()
            ) {
                Icon(Icons.Filled.Check, "Done")
            }
        }
    }
}
