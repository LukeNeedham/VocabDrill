package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.usecase.ObserveVocabEntryAndTagsForId
import io.reactivex.rxkotlin.plusAssign

class EntryDetailPageViewModel(
    entryId: Long,
    observeVocabEntryAndTagsForId: ObserveVocabEntryAndTagsForId
) : DisposingViewModel() {

    var wordA by mutableStateOf("")
        private set

    var wordB by mutableStateOf("")
        private set

    var tags by mutableStateOf<List<Tag>>(emptyList())

    init {
        disposables += observeVocabEntryAndTagsForId(entryId).subscribe {
            wordA = it.entry.wordA
            wordB = it.entry.wordB
            tags = it.tags
        }
    }

    fun onWordAChange(word: String) {
        wordA = word
    }

    fun onWordBChange(word: String) {
        wordB = word
    }
}
