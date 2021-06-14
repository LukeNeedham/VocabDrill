package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.VocabEntries
import com.lukeneedham.vocabdrill.presentation.util.composable.getViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawableId
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import org.koin.core.parameter.parametersOf

class LanguageFragmentCompose : Fragment() {
    private val navArgs: LanguageFragmentComposeArgs by navArgs()
    private val languageId by lazy { navArgs.languageId }

    @ExperimentalAnimationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Root(languageId, ::openLearnPage) { popBackStackSafe() }
        }
    }

    private fun openLearnPage(learnSet: LearnSet) {
        navigateSafe(
            LanguageFragmentComposeDirections.actionLanguageFragmentToLearnFragment(
                learnSet
            )
        )
    }
}

sealed class Page {
    object Overview : Page()
    data class EntryDetail(val entry: VocabEntryAndTags) : Page()
}

data class Backstack(val pages: List<Page>)

fun Backstack.withAddPage(page: Page): Backstack = copy(pages = pages + page)
fun Backstack.withPopPage(): Backstack {
    val newPages = pages.toMutableList().apply {
        removeLast()
    }
    return copy(pages = newPages)
}

fun Backstack.isAtRoot() = pages.size == 1

@ExperimentalAnimationApi
@Composable
fun Root(
    languageId: Long,
    openLearnPage: (learnSet: LearnSet) -> Unit,
    goBackExternal: () -> Unit
) {
    var backstack by remember { mutableStateOf(Backstack(listOf(Page.Overview))) }
    val currentPage = backstack.pages.last()

    fun openEntryDetailPage(entry: VocabEntryAndTags) {
        backstack = backstack.withAddPage(Page.EntryDetail(entry))
    }

    fun onBack() {
        if (backstack.isAtRoot()) {
            goBackExternal()
        } else {
            backstack = backstack.withPopPage()
        }
    }

    BackHandler {
        onBack()
    }
    val exhaustive = when (currentPage) {
        is Page.Overview -> OverviewPage(languageId, openLearnPage, ::openEntryDetailPage, ::onBack)
        is Page.EntryDetail -> Text("TODO")
    }
}

@ExperimentalAnimationApi
@Composable
fun OverviewPage(
    languageId: Long,
    openLearnPage: (learnSet: LearnSet) -> Unit,
    openEntryDetailPage: (entry: VocabEntryAndTags) -> Unit,
    goBack: () -> Unit
) {
    val viewModel: LanguageViewModel = getViewModel { parametersOf(languageId) }

    val country by viewModel.countryLiveData.observeAsState()
    val languageName by viewModel.languageNameLiveData.observeAsState()

    val context = LocalContext.current

    fun addEntry() {

    }

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
        VocabEntries(languageId, openEntryDetailPage)
        FloatingActionButton(onClick = ::addEntry) {

        }
    }

}
