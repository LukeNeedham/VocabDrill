package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagsList
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor

@ExperimentalAnimationApi
@Composable
fun ExistingEntryItemView(
    wordA: String,
    wordB: String,
    tagItems: List<TagItem>,
    isSelected: Boolean,
    onSelectedChange: (isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

}
