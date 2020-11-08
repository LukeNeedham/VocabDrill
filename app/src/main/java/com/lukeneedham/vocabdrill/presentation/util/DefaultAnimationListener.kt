package com.lukeneedham.vocabdrill.presentation.util

import android.view.animation.Animation

interface DefaultAnimationListener : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }
}
