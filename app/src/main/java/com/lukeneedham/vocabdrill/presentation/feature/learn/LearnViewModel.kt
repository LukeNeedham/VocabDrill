package com.lukeneedham.vocabdrill.presentation.feature.learn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukeneedham.vocabdrill.domain.model.LearnBook
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData

class LearnViewModel(
    private val learnBook: LearnBook
) : ViewModel() {
    private val entryList = learnBook.entries

    private val bookStateMutableLiveData = MutableLiveData<BookState>()
    val bookStateLiveData = bookStateMutableLiveData.toLiveData()

    val colourScheme = learnBook.colourScheme

    private var currentEntryIndex = 0

    init {
        updateState()
    }

    fun onInput(input: String) {
        val isCorrect = isInputCorrect(input)
        if (isCorrect) {
            currentEntryIndex++
            updateState()
        }
    }

    private fun isInputCorrect(input: String): Boolean {
        val currentEntry = entryList.getOrNull(currentEntryIndex) ?: return false
        return currentEntry.wordB == input
    }

    private fun updateState() {
        val newEntry = entryList.getOrNull(currentEntryIndex)
        bookStateMutableLiveData.value = if (newEntry == null) {
            BookState.Finished
        } else {
            val pageNumber = (currentEntryIndex + 1).toString()
            val newContents = PageContents(pageNumber, newEntry.wordA)
            BookState.Page(newContents)
        }
    }
}
