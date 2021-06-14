package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagsList
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor

@ExperimentalAnimationApi
@Composable
fun ExistingEntryItem(
    taggedEntry: VocabEntryAndTags,
    isSelected: Boolean,
    onSelectedChange: (isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) ThemeColor.vocabEntryExistingBackgroundSelected else ThemeColor.vocabEntryExistingBackgroundUnselected

    val sideMargin = 10.dp

    val wordTextSizeTarget = if (isSelected) 25 else 16
    val wordTextSize by animateIntAsState(targetValue = wordTextSizeTarget)
    val wordTextSizeSp = wordTextSize.sp

    Column(
        modifier = modifier
            .background(
                backgroundColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onSelectedChange(!isSelected)
            }
    ) {
        Row {
            Text(
                text = taggedEntry.entry.wordA,
                fontSize = wordTextSizeSp,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, start = sideMargin, end = 10.dp)
            )

            Text(
                text = taggedEntry.entry.wordB,
                fontSize = wordTextSizeSp,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, end = sideMargin, start = 10.dp)
            )
            Image(painter = painterResource(id = R.drawable.ic_chevron_up), contentDescription = )
            // TODO: Show disclosure to open edit view
        }
        AnimatedVisibility(
            visible = isSelected,
            enter = expandVertically(Alignment.Top),
            exit = shrinkVertically(Alignment.Top)
        ) {
            TagsList(
                taggedEntry.tags,
                Modifier.padding(start = sideMargin, end = sideMargin, bottom = 5.dp)
            )
        }
    }
}
