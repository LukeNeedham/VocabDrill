package com.lukeneedham.vocabdrill.domain.model

class VocabGroupRelations(
    val vocabGroup: VocabGroup,
    val language: Language,
    val entries: List<VocabEntry>
)
