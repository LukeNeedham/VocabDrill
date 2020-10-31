package com.lukeneedham.vocabdrill.util.extension

fun Int.divideRoundUp(other: Int): Int = if (other == 0) {
    throw ArithmeticException("Division by zero")
} else (this + other - 1) / other;
