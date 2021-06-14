package com.lukeneedham.vocabdrill.presentation.feature.vocabentry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.util.composable.get
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground
import org.koin.core.parameter.parametersOf

@Composable
fun TagsList(tags: List<Tag>, modifier: Modifier = Modifier) {
    LazyRow(modifier) {
        val items = tags
        itemsIndexed(items) { index, item ->
            val endPadding = if (index == items.size) 0 else 4
            TagItem(item, Modifier.padding(end = endPadding.dp))
        }

    }
}

@Composable
fun TagItem(item: Tag, modifier: Modifier = Modifier) {
    val viewModel = remember { get<TagItemExistingViewModel> { parametersOf(item) } }

    val tag = viewModel.tag
    val color = Color(viewModel.backgroundColor)
    val textColor = Color(viewModel.textColor)

    val sideMargin = 7.dp
    val verticalMargin = 2.dp
    Text(
        text = tag.name,
        fontSize = 14.sp,
        color = textColor,
        modifier = modifier
            .background(color, RoundedCornerShape(50))
            .padding(
                start = sideMargin,
                end = sideMargin,
                top = verticalMargin,
                bottom = verticalMargin
            )
    )
}

class TagItemExistingViewModel(
    val tag: Tag,
    chooseTextColourForBackground: ChooseTextColourForBackground
) {
    val backgroundColor = tag.color
    val textColor = chooseTextColourForBackground(backgroundColor)
}

@Composable
fun TagItemCreate() {
    Text(text = "TODO")
}

sealed class TagItem {
    data class Existing(val data: Tag) : TagItem()
    object Create : TagItem()
}
