package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.Sizes
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagsList
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor

@ExperimentalAnimationApi
@Composable
fun ExistingEntryItem(
    taggedEntry: VocabEntryAndTags,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onOpened: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) ThemeColor.vocabEntryExistingBackgroundSelected else ThemeColor.vocabEntryExistingBackgroundUnselected

    val sideMargin = 10.dp

    var scaleTextAnimationFinished by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(isSelected) {
        scaleTextAnimationFinished = false
        if (isSelected) {
            focusManager.clearFocus(true)
        }
    }

    val textSizeAnimationDuration = 150
    val disclosureAnimationDuration = textSizeAnimationDuration / 3

    val wordTextSizeTarget = if (isSelected) 25 else 16
    val wordTextSize by animateIntAsState(
        targetValue = wordTextSizeTarget,
        animationSpec = tween(textSizeAnimationDuration)
    ) {
        scaleTextAnimationFinished = true
    }
    val wordTextSizeSp = wordTextSize.sp

    val showDisclosure = isSelected && scaleTextAnimationFinished

    // Workaround so disclosure can fill height,
    // as otherwise available height is not remeasured after animation
    var rowHeightPx by remember { mutableStateOf(0) }
    val disclosureHeightDp = LocalDensity.current.run {
        // Add some margin to avoid measuring jumps
        (rowHeightPx - 20).toDp()
    }

    Column(
        modifier = modifier
            .clickable {
                if (isSelected) {
                    onOpened()
                } else {
                    onSelected()
                }
            }
            .padding(top = 3.dp, bottom = 3.dp)
            .background(
                backgroundColor,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            modifier = Modifier.onSizeChanged {
                rowHeightPx = it.height
            }
        ) {
            Text(
                text = taggedEntry.entry.wordA,
                fontSize = wordTextSizeSp,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, start = sideMargin, end = 10.dp)
                    .animateContentSize()
            )
            Text(
                text = taggedEntry.entry.wordB,
                fontSize = wordTextSizeSp,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, bottom = 10.dp, end = sideMargin, start = 10.dp)
                    .animateContentSize()
            )
            Box(
                modifier = Modifier
                    .width(Sizes.RightIconWidth)
                    .align(Alignment.CenterVertically)
                    .clipToBounds()
            ) {
                // Full namespace is a workaround for an ambiguity issue
                AnimatedVisibility(
                    visible = showDisclosure,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        tween(disclosureAnimationDuration)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        tween(disclosureAnimationDuration)
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = "Disclosure",
                        Modifier
                            .fillMaxWidth()
                            .height(disclosureHeightDp)
                    )
                }
            }
        }
//        AnimatedVisibility1(
//            visible = isSelected,
//            enter = expandVertically(Alignment.Top),
//            exit = shrinkVertically(Alignment.Top)
//        ) {
//            TagsList(
//                taggedEntry.tags,
//                Modifier.padding(start = sideMargin, end = sideMargin, bottom = 5.dp)
//            )
//        }
    }
}
