package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.EntryKey
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.VocabEntries
import com.lukeneedham.vocabdrill.presentation.theme.ThemeColor
import com.lukeneedham.vocabdrill.presentation.util.composable.getViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawableId
import com.lukeneedham.vocabdrill.presentation.util.extension.isLastItemShowing
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@ExperimentalAnimationApi
@Composable
fun LanguageOverviewPage(
    languageId: Long,
    openLearnPage: (learnSet: LearnSet) -> Unit,
    openEntryDetailPage: (entry: VocabEntryAndTags) -> Unit,
    goBack: () -> Unit
) {
    val viewModel: LanguageViewModel = getViewModel { parametersOf(languageId) }

    val country by viewModel.countryLiveData.observeAsState()
    val languageName by viewModel.languageNameLiveData.observeAsState()
    val entryKeys by viewModel.entryKeys.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()

    val scrollToBottomEvent = viewModel.scrollToBottomEvent

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isLastItemShowing = listState.isLastItemShowing()
    val showAddButton = if (isLastItemShowing == null) false else !isLastItemShowing

    val context = LocalContext.current

    fun scrollToCreateItem() {
        coroutineScope.launch {
            listState.animateScrollToItem(entryKeys.indexOf(EntryKey.Create))
        }
    }

    LaunchedEffect(scrollToBottomEvent) {
        if (scrollToBottomEvent == null || scrollToBottomEvent.isConsumed()) {
            return@LaunchedEffect
        }
        scrollToBottomEvent.consume()
        scrollToCreateItem()
    }

    Box {
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
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val country = country
                    val languageName = languageName
                    if (country != null && languageName != null) {
                        val flagId = country.getFlagDrawableId(context)

                        Image(
                            painter = painterResource(id = flagId),
                            contentDescription = "The flag for $languageName",
                        )
                        Text(text = languageName)
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_learn),
                    contentDescription = "Learn button",
                    modifier = Modifier.clickable {
                        val learnSet = viewModel.getLearnSet()
                        openLearnPage(learnSet)
                    }
                )
            }
            VocabEntries(
                entryKeys = entryKeys,
                selectedEntry = selectedEntry,
                listState = listState,
                onEntrySelected = viewModel::onEntrySelected,
                onOpenEntry = openEntryDetailPage,
                createWordA = viewModel.createWordA,
                onCreateWordAChange = viewModel::onCreateWordAChange,
                createWordB = viewModel.createWordB,
                onCreateWordBChange = viewModel::onCreateWordBChange,
                onEntryAdded = viewModel::onEntryAdded
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            AnimatedVisibility(visible = showAddButton, enter = fadeIn(), exit = fadeOut()) {
                FloatingActionButton(
                    onClick = viewModel::onCreateEntrySelected,
                    backgroundColor = ThemeColor.darkGrey,
                    modifier = Modifier
                        .padding(end = 10.dp, bottom = 10.dp)
                ) {
                    Icon(Icons.Filled.Add, "Add entry", tint = ThemeColor.white)
                }
            }
        }
    }
}
