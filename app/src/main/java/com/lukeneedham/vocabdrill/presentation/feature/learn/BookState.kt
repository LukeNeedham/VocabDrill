package com.lukeneedham.vocabdrill.presentation.feature.learn

sealed class BookState {
    class Page(val contents: PageContents) : BookState()
    object Finished : BookState()
}
