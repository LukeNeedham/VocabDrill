package com.lukeneedham.vocabdrill.presentation.util

class OnceEvent {
    private var isConsumed = false

    fun isConsumed() = isConsumed
    fun consume() {
        isConsumed = true
    }
}
