package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.toLiveData
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.usecase.AddTagToVocabEntry
import com.lukeneedham.vocabdrill.usecase.FindTagNameMatches
import com.lukeneedham.vocabdrill.usecase.GetTagSuggestions
import com.lukeneedham.vocabdrill.usecase.ObserveTagsForEntryId
import io.reactivex.BackpressureStrategy
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class TagsEditorViewModel(
    private val languageId: Long,
    private val entryId: Long,
    private val getTagSuggestions: GetTagSuggestions,
    private val addTagToVocabEntry: AddTagToVocabEntry,
    private val observeTagsForEntryId: ObserveTagsForEntryId
) : DisposingViewModel() {
    private val searchTextSubject = BehaviorSubject.create<String>()
    val searchTextLiveData = searchTextSubject.toFlowable(BackpressureStrategy.LATEST).toLiveData()

    /** Debounces the search text updates */
    private val tagMatchQueries = searchTextSubject.debounce(500, TimeUnit.MILLISECONDS)

    var currentTags by mutableStateOf<List<Tag>>(emptyList())

    var tagSuggestions by mutableStateOf<List<TagSuggestion>>(emptyList())
        private set

    init {
        disposables += observeTagsForEntryId(entryId).subscribe {
            currentTags = it
        }

        disposables += tagMatchQueries.flatMap {
            getTagSuggestions(languageId, currentTags, it).toObservable()
        }.subscribe {
            tagSuggestions = it
        }
        searchTextSubject.onNext("")
    }

    fun onSearchTextChange(text: String) {
        searchTextSubject.onNext(text)
    }

    fun onSuggestedTagClick(tag: Tag) {
        addTagToVocabEntry(entryId, tag.id).subscribe()
    }
}
