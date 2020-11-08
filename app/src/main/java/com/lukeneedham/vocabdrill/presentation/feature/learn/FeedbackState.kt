package com.lukeneedham.vocabdrill.presentation.feature.learn

import com.lukeneedham.vocabdrill.R

enum class FeedbackState(val inputIconRes: Int) {
    /** Ready for input - neither sound_effect_correct nor incorrect. Any previous feedback has been fully given */
    Ready(R.drawable.ic_submit),
    Correct(R.drawable.ic_correct),
    Incorrect(R.drawable.ic_incorrect)
}
