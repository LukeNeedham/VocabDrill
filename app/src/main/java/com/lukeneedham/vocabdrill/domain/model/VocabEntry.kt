package com.lukeneedham.vocabdrill.domain.model

data class VocabEntry(
    val id: Long,
    val wordA: String,
    val wordB: String,
    val vocabGroupId: Long
)
