package com.lukeneedham.vocabdrill.domain.model

/** The Vocab Entry, without tags. A stepping stone to the full domain model */
data class VocabEntryPartial(
    val id: Long,
    val wordA: String,
    val wordB: String,
    val languageId: Long
)
