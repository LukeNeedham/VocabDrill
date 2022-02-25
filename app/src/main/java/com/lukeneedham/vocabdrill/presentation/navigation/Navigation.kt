package com.lukeneedham.vocabdrill.presentation.navigation

import android.content.Intent
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.language.LanguageOverviewPage
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.detail.EntryDetailPage
import kotlinx.android.parcel.Parcelize

sealed class Page : Parcelable {
    @Parcelize
    object LanguageOverview : Page()

    @Parcelize
    data class EntryDetail(val entryId: Long) : Page()

    @Parcelize
    object NewPage : Page()
}

@Parcelize
data class Backstack(val pages: List<Page>) : Parcelable

fun Backstack.withAddPage(page: Page): Backstack = copy(pages = pages + page)
fun Backstack.withPopPage(): Backstack {
    val newPages = pages.toMutableList().apply {
        removeLast()
    }
    return copy(pages = newPages)
}

fun Backstack.isAtRoot() = pages.size == 1

// TODO: For now this is only for the language pages,
//  but this should expand as the rest of the app becomes Compose
@ExperimentalAnimationApi
@Composable
fun LanguageRoot(
    languageId: Long,
    openLearnPage: (learnSet: LearnSet) -> Unit,
    goBackExternal: () -> Unit,
    deepLink: Intent
) {
    var backstack by remember { mutableStateOf(Backstack(listOf(Page.LanguageOverview))) }
    val currentPage = backstack.pages.last()

    handleDeepLink(deepLink)

    fun handleDeepLink() {
        backstack.withAddPage(Page.NewPage)
    }

    fun openEntryDetailPage(entry: VocabEntryAndTags) {
        backstack = backstack.withAddPage(Page.EntryDetail(entry.entry.id))
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
        is Page.LanguageOverview -> LanguageOverviewPage(
            languageId,
            openLearnPage,
            ::openEntryDetailPage,
            ::onBack
        )
        is Page.EntryDetail -> EntryDetailPage(currentPage.entryId, ::onBack)
        is Page.NewPage -> Text(text = "a")
    }
}
