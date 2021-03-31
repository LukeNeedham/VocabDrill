package com.lukeneedham.vocabdrill.presentation.feature.learn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.presentation.audio.SoundEffect
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.PlaySoundEffect

class LearnViewModel(
    private val learnSet: LearnSet,
    private val playSoundEffect: PlaySoundEffect
) : ViewModel() {
    private val entryList = learnSet.entries

    private val bookStateMutableLiveData = MutableLiveData<BookState>()
    val bookStateLiveData = bookStateMutableLiveData.toLiveData()

    private val feedbackStateMutableLiveData =
        MutableLiveData<FeedbackState>(FeedbackState.Empty)
    val feedbackStateLiveData = feedbackStateMutableLiveData.toLiveData()

    private var currentEntryIndex = 0

    init {
        updatePage()
    }

    fun onInput(input: String) {
        val currentEntry = entryList[currentEntryIndex]
        val correctValue = currentEntry.wordB
        val isCorrect = correctValue == input

        if (isCorrect) {
            giveCorrectFeedback(correctValue)
        } else {
            giveIncorrectFeedback(input, correctValue)
        }
    }

    /** Feedback is completed */
    fun onCurrentPageVisible() {
        feedbackStateMutableLiveData.value = FeedbackState.AcceptingInput
    }

    fun onFeedbackCompleted() {
        currentEntryIndex++
        updatePage()
    }

    private fun giveCorrectFeedback(correctValue: String) {
        feedbackStateMutableLiveData.value = FeedbackState.Correct(correctValue)
        playSoundEffect(SoundEffect.Correct)
    }

    private fun giveIncorrectFeedback(input: String, correctValue: String) {
        feedbackStateMutableLiveData.value = FeedbackState.Incorrect(input, correctValue)
        playSoundEffect(SoundEffect.Incorrect)
    }

    private fun updatePage() {
        val newEntry = entryList.getOrNull(currentEntryIndex)
        bookStateMutableLiveData.value = if (newEntry == null) {
            BookState.Finished
        } else {
            val pageNumber = (currentEntryIndex + 1).toString()
            val totalPages = entryList.size
            val progressText = "# $pageNumber / $totalPages"
            val newContents = PageContents(newEntry.wordA, progressText)
            BookState.Page(newContents)
        }
    }
}
