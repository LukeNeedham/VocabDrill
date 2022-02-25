package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.Sizes
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor

@Composable
fun CreateEntryItem(
    wordA: String,
    onWordAChange: (String) -> Unit,
    wordB: String,
    onWordBChange: (String) -> Unit,
    isSelected: Boolean,
    onEntryAdded: () -> Unit,
    onSelected: () -> Unit,
) {
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

    val inputTextStyle = TextStyle(fontSize = 20.sp)
    val inputTextColor = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = ThemeColor.darkGrey,
        cursorColor = ThemeColor.darkGrey
    )

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
            OutlinedTextField(
                value = wordA,
                onValueChange = { onWordAChange(it) },
                textStyle = inputTextStyle,
                colors = inputTextColor,
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
            OutlinedTextField(
                value = wordB,
                onValueChange = { onWordBChange(it) },
                textStyle = inputTextStyle,
                colors = inputTextColor,
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
                    .padding(end = 5.dp)
                    .width(Sizes.RightIconWidth)
                    .background(ThemeColor.mediumGrey, CircleShape)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onEntryAdded()
                    }
            ) {
                Icon(
                    Icons.Filled.Check,
                    "Done",
                    tint = ThemeColor.white,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}
