package com.lukeneedham.vocabdrill.util.extension

val Any.TAG: String
    get() = this::class.simpleName ?: "null"
