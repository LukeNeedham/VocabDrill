package com.lukeneedham.vocabdrill.presentation.feature.learn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukeneedham.vocabdrill.domain.model.LearnBook
import com.lukeneedham.vocabdrill.presentation.audio.SoundEffect
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.PlaySoundEffect

class LearnViewModel(
    private val learnBook: LearnBook,
    private val playSoundEffect: PlaySoundEffect
) : ViewModel() {
    private val entryList = learnBook.entries

    private val bookStateMutableLiveData = MutableLiveData<BookState>()
    val bookStateLiveData = bookStateMutableLiveData.toLiveData()

    private val feedbackStateMutableLiveData = MutableLiveData<FeedbackState>(FeedbackState.Ready)
    val feedbackStateLiveData = feedbackStateMutableLiveData.toLiveData()

    val colourScheme = learnBook.colourScheme

    private var currentEntryIndex = 0

    init {
        updatePage()
    }

    fun onInput(input: String) {
        val isCorrect = isInputCorrect(input)
        if (isCorrect) {
            currentEntryIndex++
            updatePage()
            giveCorrectFeedback()
        } else {
            giveIncorrectFeedback()
        }
    }

    /** Feedback is completed */
    fun onFeedbackGiven() {
        feedbackStateMutableLiveData.value = FeedbackState.Ready
    }

    private fun giveCorrectFeedback() {
        feedbackStateMutableLiveData.value = FeedbackState.Correct
        playSoundEffect(SoundEffect.Correct)
    }

    private fun giveIncorrectFeedback() {
        feedbackStateMutableLiveData.value = FeedbackState.Incorrect
        playSoundEffect(SoundEffect.Incorrect)
    }

    private fun isInputCorrect(input: String): Boolean {
        val currentEntry = entryList.getOrNull(currentEntryIndex) ?: return false
        return currentEntry.wordB == input
    }

    private fun updatePage() {
        val newEntry = entryList.getOrNull(currentEntryIndex)
        bookStateMutableLiveData.value = if (newEntry == null) {
            BookState.Finished
        } else {
            val pageNumber = (currentEntryIndex + 1).toString()
            val totalPages = entryList.size
            val progressText = "# $pageNumber / $totalPages"
            val newContents = PageContents(progressText, newEntry.wordA)
            BookState.Page(newContents)
        }
    }
}
