package com.lukeneedham.vocabdrill.domain.model

data class LanguageRelations(
    val language: Language,
    val entries: List<VocabEntry>
)
