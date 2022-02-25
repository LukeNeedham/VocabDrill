package com.lukeneedham.vocabdrill.usecase

import android.graphics.Color

class ChooseContentColourForBackground {
    /**
     * @return either [Color.BLACK] or [Color.WHITE],
     * depending on which is most legible against [backgroundColor].
     */
    operator fun invoke(backgroundColor: Int): Int {
        val r = Color.red(backgroundColor)
        val g = Color.green(backgroundColor)
        val b = Color.blue(backgroundColor)
        return if (r * 0.299 + g * 0.587 + b * 0.114 > 186) {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }
}
