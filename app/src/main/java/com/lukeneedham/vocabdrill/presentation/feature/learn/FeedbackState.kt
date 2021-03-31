package com.lukeneedham.vocabdrill.presentation.feature.learn

sealed class FeedbackState {
    /** Empty state - this is the initial state, before ready to accept input */
    object Empty : FeedbackState()
    object AcceptingInput : FeedbackState()
    data class Correct(val correctAnswer: String) : FeedbackState()
    data class Incorrect(val inputAnswer: String, val correctAnswer: String) : FeedbackState()
}
