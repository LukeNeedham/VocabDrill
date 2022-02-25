package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagItemExistingViewModel
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor
import com.lukeneedham.vocabdrill.presentation.util.composable.get
import com.lukeneedham.vocabdrill.presentation.util.composable.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TagsEditor() {
    val viewModel: TagsEditorViewModel = getViewModel()

    val searchText by viewModel.searchTextLiveData.observeAsState("")

    val tagSuggestions = viewModel.tagSuggestions
    val currentTags = viewModel.currentTags

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(ThemeColor.lightGrey, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        LazyRow {
            val items = currentTags
            itemsIndexed(items) { index, item ->
                val endPadding = if (index == items.size) 0 else 4
                EditableTagItem(item, Modifier.padding(end = endPadding.dp))
            }
        }
        OutlinedTextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange
        )
        LazyRow {
            val items = tagSuggestions
            itemsIndexed(items) { index, item ->
                val endPadding = if (index == items.size) 0 else 4
                TagItem(
                    item,
                    Modifier
                        .padding(end = endPadding.dp)
                        .clickable { viewModel.onSuggestedTagClick(item) })
            }
        }
    }
}


@Composable
fun EditableTagItem(item: Tag, modifier: Modifier = Modifier) {
    val viewModel = remember { get<TagItemExistingViewModel> { parametersOf(item) } }

    val tag = viewModel.tag
    val color = Color(viewModel.backgroundColor)
    val contentColor = Color(viewModel.contentColor)

    val sideMargin = 7.dp
    val verticalMargin = 2.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .background(color, RoundedCornerShape(20.dp))
            .padding(
                start = sideMargin,
                end = sideMargin,
                top = verticalMargin,
                bottom = verticalMargin
            )
    ) {
        Text(
            text = tag.name,
            fontSize = 13.sp,
            color = contentColor,
        )
        Icon(Icons.Filled.Close, contentDescription = "Delete", tint = contentColor)
    }
}
