package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor
import com.lukeneedham.vocabdrill.presentation.util.composable.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EntryDetailPage(
    entryId: Long,
    goBack: () -> Unit
) {
    val viewModel = getViewModel<EntryDetailPageViewModel> { parametersOf(entryId) }

    val inputTextStyle = TextStyle(fontSize = 20.sp)
    val inputTextColor = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = ThemeColor.darkGrey,
        cursorColor = ThemeColor.darkGrey
    )

    Column {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back button",
                modifier = Modifier.clickable {
                    goBack()
                }
            )
        }

        Row {
            OutlinedTextField(
                value = viewModel.wordA,
                onValueChange = viewModel::onWordAChange,
                textStyle = inputTextStyle,
                colors = inputTextColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
            )
            OutlinedTextField(
                value = viewModel.wordB,
                onValueChange = viewModel::onWordBChange,
                textStyle = inputTextStyle,
                colors = inputTextColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, end = 10.dp, start = 10.dp)
            )
        }

        TagsEditor(tags = viewModel.tags)
    }
}
