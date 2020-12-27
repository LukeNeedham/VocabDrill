package com.lukeneedham.vocabdrill.util


object ColorExtraUtils {
    fun toHex(color: Int): String = String.format("#%06X", 0xFFFFFF and color)
}
